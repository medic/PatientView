package net.frontlinesms.plugins.patientview.ui.dashboard;

import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.utils.PVConstants;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class Dashboard extends ViewHandler{

	private static final String UI_FILE = "/ui/plugins/patientview/dashboard/dashboard.xml";

	//cached thinlet objects
	protected Object leftPanel;
	protected Object tabbedPanel;
	
	protected Object stashedPanel;
	
	protected String stashedTabName;
	protected String stashedPanelName;
	
	//controllers
	
	public Dashboard(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController,appCon,UI_FILE);
		leftPanel = uiController.find(mainPanel,"leftPanel");
		tabbedPanel = uiController.find(mainPanel,"tabPanel");
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	public void tabSelectionChanged(){
		System.out.println("Selection changed");
	}
	
	
	public void expandDashboard(String panelName, String tabName){
		stashedTabName = tabName;
		stashedPanelName = panelName;
		stashedPanel = ui.find(panelName);
		ui.removeAll(ui.find(tabName));
		ui.add(ui.find(tabName),getMainPanel());
	}
	
	public void expandDashboard(){
		willAppear();
		expandDashboard(PVConstants.MAIN_PANEL_NAME,PVConstants.TAB_NAME);	
	}

	public void collapseDashboard(){
		subviewsWillDisappear();
		ui.removeAll(ui.find(stashedTabName));
		ui.add(ui.find(stashedTabName),stashedPanel);
	}
}

