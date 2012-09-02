package net.frontlinesms.plugins.patientview.responsemapping.ui;

import java.util.Date;
import java.util.List;

import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.search.impl.FormMappingResultSet;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
public class FormResponseMappingPanelController extends AdministrationTabPanel implements TableActionDelegate, EventObserver, FormFieldDelegate{

	private Object actionPanel;
	private Object comboBox;
	
	private PagedTableController tableController;
	private FormMappingResultSet resultSet;
	private MedicFormResponse currentResponse;
	
	/** The currently selected page number for mapped responses*/
	private int mappedPageNumber= 0;
	/** The currently selected page number for unmapped responses*/
	private int unmappedPageNumber= 0;
	
	private static final String UI_FILE ="/ui/plugins/patientview/administration/responsemapping/formResponseMappingAdministrationPanel.xml";
		
	public FormResponseMappingPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon,UI_FILE);
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		init();
	}

	private void init(){
		actionPanel = find("actionPanel");
		//set up the table
		tableController = new PagedTableController(this,ui, find("tablePanel"));
		tableController.putHeader(MedicFormResponse.class, HeaderColumn.createColumnList(new String[]{getI18nString("medic.common.labels.form.name"),getI18nString("medic.common.labels.date.submitted"),getI18nString("medic.common.labels.submitter")}, 
				 																		 new String[]{"/icons/form.png","/icons/date_sent.png","/icons/user_sender.png"},
				 																		 new String[]{"getFormName","getStringDateSubmitted","getSubmitterName"}));
		tableController.setNoResultsMessage(getI18nString("medic.form.response.mapping.panel.no.responses.yet"));
		tableController.enableRefreshButton(appCon);
		//set up the results set
		resultSet = new FormMappingResultSet(appCon);
		resultSet.setSearchingMapped(false);
		tableController.setResultsSet(resultSet);
		//set up the control panel
		DateField dateField = new DateField(ui,getI18nString("medic.common.labels.date.submitted"),this);
		dateField.setLabelIcon("/icons/date.png");
		add(find("controlPanel"),dateField.getThinletPanel());
		//create the form combo box
		List<MedicForm> forms = ((MedicFormDao) appCon.getBean("MedicFormDao")).getAllMedicForms();
		comboBox = Thinlet.create("combobox");
		ui.setEditable(comboBox,false);
		add(comboBox,ui.createComboboxChoice(getI18nString("medic.common.all.forms"), null));
		for(MedicForm mf: forms){
			add(comboBox,ui.createComboboxChoice(mf.getName(), mf));
		}
		ui.setAction(comboBox, "formChanged(this.selected)", null, this);
		ui.setWeight(comboBox,1,0);
		Object label = ui.createLabel(getI18nString("medic.common.form"));
		ui.setIcon(label, "/icons/form.png");
		add(find("controlPanel"),label);
		add(find("controlPanel"),comboBox);
		dateField.setRawResponse(new Date().getTime());
		ui.setText(comboBox, getI18nString("medic.common.all.forms"));
		tableController.updateTable();
	}
	
	public String getListItemTitle() {
		return getI18nString("admin.actionlist.map.form.responses");
	}

	public void selectionChanged(Object selectedObject) {
		ui.removeAll(actionPanel);
		if(selectedObject == null){
			return;
		}
		currentResponse = (MedicFormResponse) selectedObject;
		add(actionPanel,new FlexibleFormResponsePanel(ui, appCon,currentResponse).getMainPanel());
		add(actionPanel, new CandidateSearchPanel(ui,appCon,currentResponse,this).getMainPanel());
	}
	
	public void toggleChanged(Object button){
		//save the current page
		if(resultSet.isSearchingMapped()){
			mappedPageNumber = resultSet.getCurrentPage();
		}else{
			unmappedPageNumber = resultSet.getCurrentPage();
		}
		//switch the mapping type
		if(ui.getName(button).equals("mappedToggle")){
			resultSet.setSearchingMapped(true);
		}else if(ui.getName(button).equals("unmappedToggle")){
			resultSet.setSearchingMapped(false);
		}
		if(resultSet.isSearchingMapped()){
			resultSet.setCurrentPage(mappedPageNumber);
		}else{
			resultSet.setCurrentPage(unmappedPageNumber);
		}
		tableController.updateTable();
		tableController.updatePagingControls();
		tableController.setSelected(0);
		if(tableController.getResultsSet().getTotalResults() == 0){
			selectionChanged(null);
		}
	}

	public void doubleClickAction(Object selectedObject) {/*do nothing*/}
	public void resultsChanged() {/*do nothing*/}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntitySavedNotification){
			tableController.updateTable();
			tableController.setSelected(0);
		}
	}

	/**
	 * Called by the candidate search panel when a mapping event occurs
	 */
	public void currentResponseMappingChanged(){
		//if we're in the 'unmapped' panel and the 
		if(ui.isSelected(find("unmappedToggle")) && currentResponse.getSubject() != null){
			tableController.updateTable();
			tableController.setSelected(0);
			if(tableController.getResultsSet().getTotalResults() == 0){
				selectionChanged(null);
			}
		}else{
			selectionChanged(tableController.getCurrentlySelectedObject());
		}
	}
	
	public String getIconPath() {
		return "/icons/map_form.png";
	}
	
	/**
	 * Called when the date filtering field has changed
	 * @see net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate#formFieldChanged(net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField, java.lang.String)
	 */
	public void formFieldChanged(ThinletFormField changedField, String newValue) {
		resultSet.setAroundDate(new Date(((DateField) changedField).getRawResponse()));
		tableController.updateTable();
	}
	
	/**
	 * Called when the form filtering combobox has changed
	 * @param selectedIndex
	 */
	public void formChanged(int selectedIndex){
		MedicForm mf = (MedicForm) ui.getAttachedObject(ui.getItem(comboBox, selectedIndex));
		resultSet.setForm(mf);
		tableController.updateTable();
	}
}
