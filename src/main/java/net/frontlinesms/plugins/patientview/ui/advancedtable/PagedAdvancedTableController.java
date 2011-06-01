package net.frontlinesms.plugins.patientview.ui.advancedtable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PagedAdvancedTableController extends AdvancedTableController implements EventObserver{

	protected PagedResultSet resultSet;
	
	protected Object pagingControls;
	
	private boolean refreshOnAppear = false;
	
	private final static String PAGING_CONTROLS_XML = "/ui/plugins/patientview/components/pagingControls.xml";
		
	protected Map<Class<?>,Boolean> refreshButtonStates = Collections.synchronizedMap(new HashMap<Class<?>,Boolean>());
	
	public PagedAdvancedTableController(TableActionDelegate delegate,UiGeneratorController uiController) {
		super(delegate, uiController);
		ui.setWeight(mainPanel, 1, 1);
		ui.setColumns(mainPanel,1);
		ui.setGap(mainPanel, 6);
		pagingControls = ui.loadComponentFromFile(PAGING_CONTROLS_XML, this);
		add(pagingControls);			
}
	
	public PagedAdvancedTableController(TableActionDelegate delegate,UiGeneratorController uiController, Object panel) {
			super(delegate, uiController);
			setMainPanel(panel);
			ui.setWeight(mainPanel, 1, 1);
			ui.setColumns(mainPanel,1);
			ui.setGap(mainPanel, 6);
			pagingControls = ui.loadComponentFromFile(PAGING_CONTROLS_XML, this);
			add(pagingControls);			
	}
	
	public void updateTable(){
		setResults(resultSet.getFreshResultsPage());
	}

	/**
	 * action method for left page button
	 */
	public void pageLeft(){
		this.resultSet.previousPage();
		this.setResults(resultSet.getFreshResultsPage());
		updatePagingControls();
	}
	
	/**
	 * action method for right page button
	 */
	public void pageRight(){
		this.resultSet.nextPage();
		this.setResults(resultSet.getFreshResultsPage());
		updatePagingControls();
	}
	
	@Override
	public void putHeader(Class<?> headerClass, List<HeaderColumn> columns){
		super.putHeader(headerClass,columns);
		refreshButtonStates.put(headerClass, false);
	}
			
	//TODO: this should be private
	public void updatePagingControls(){
		if(resultSet == null || resultSet.getTotalResults() == 0){
			ui.setEnabled(find(pagingControls, "rightPageButton"),false);
			ui.setEnabled(find(pagingControls, "leftPageButton"),false);
			ui.setText(find(pagingControls, "resultsLabel"),getI18nString("pagingcontrols.no.results"));
			return;
		}
		//set the paging buttons
		ui.setEnabled(find(pagingControls, "refreshButton"),refreshButtonStates.get(currentClass));
		ui.setEnabled(find(pagingControls, "leftPageButton"),resultSet.hasPreviousPage());
		ui.setEnabled(find(pagingControls, "rightPageButton"),resultSet.hasNextPage());
		String pagingLabel = getI18nString("pagingcontrols.results")+" " + getResultsSet().getFirstResultOnPage() + " "+getI18nString("pagingcontrols.to")+" " +
					resultSet.getLastResultOnPage() + " "+getI18nString("pagingcontrols.of")+" " + resultSet.getTotalResults();
		ui.setText(find(pagingControls, "resultsLabel"),pagingLabel);
	}

	@Override
	public void setResults(List<?> results){
		super.setResults(results);
		updatePagingControls();
	}

	public void setResultsSet(PagedResultSet resultsManager) {
		this.resultSet = resultsManager;
	}

	public PagedResultSet getResultsSet() {
		return resultSet;
	}

	public void setPagingControlBorder(boolean hasBorder){
		Object panel = find(pagingControls,"bottomButtonPanel");
		ui.setBorder(panel, hasBorder);
		if(hasBorder){
			ui.setInteger(panel, "top", 5);
			ui.setInteger(panel, "left", 5);
			ui.setInteger(panel, "right", 5);
			ui.setInteger(panel, "bottom", 5);
		}else{
			ui.setInteger(panel, "top", 0);
			ui.setInteger(panel, "left", 0);
			ui.setInteger(panel, "right", 0);
			ui.setInteger(panel, "bottom", 0);

		}
	}
	
	public void enableRefreshButton(ApplicationContext appCon){
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		ui.setVisible(find("refreshButton"), true);
		if(resultSet != null){
			updatePagingControls();
		}else{
			ui.setEnabled(find("refreshButton"), false);
		}
	}
	
	public void refresh(){
		refreshButtonStates.put(currentClass, false);
		updateTable();
	}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof DatabaseEntityNotification){
			DatabaseEntityNotification<?> dbNotification = (DatabaseEntityNotification<?>) notification;
			for(Class<?> c: refreshButtonStates.keySet()){
				if(dbNotification.getDatabaseEntity().getClass().equals(c)){
					refreshButtonStates.put(c, true);
				}
			}
		}
		updatePagingControls();
	}

	public void setRefreshOnAppear(boolean refreshOnAppear) {
		this.refreshOnAppear = refreshOnAppear;
	}
	
	@Override
	public void willAppear(){
		if(refreshOnAppear) refresh();
		subviewsWillAppear();
	}
}
