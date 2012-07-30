package net.frontlinesms.plugins.patientview.ui.dashboard;

import net.frontlinesms.plugins.patientview.utils.PVConstants;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class Dashboard implements ThinletUiEventHandler{

	private static final String UI_FILE = "/ui/plugins/patientview/dashboard/dashboard.xml";

	//cached thinlet objects
	protected Object mainPanel;
	protected Object leftPanel;
	protected Object tabbedPanel;
	
	protected Object stashedPanel;
	
	protected String stashedTabName;
	protected String stashedPanelName;
	
	//controllers
	protected UiGeneratorController uiController;
	protected ApplicationContext appCon;
	
	public Dashboard(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		mainPanel = uiController.loadComponentFromFile(UI_FILE, this); 
		leftPanel = uiController.find(mainPanel,"leftPanel");
		tabbedPanel = uiController.find(mainPanel,"tabPanel");
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	
	public void expandDashboard(String panelName, String tabName){
		stashedTabName = tabName;
		stashedPanelName = panelName;
		stashedPanel = uiController.find(panelName);
		uiController.removeAll(uiController.find(tabName));
		uiController.add(uiController.find(tabName),getMainPanel());
	}
	
	public void expandDashboard(){
		expandDashboard(PVConstants.MAIN_PANEL_NAME,PVConstants.TAB_NAME);	
	}

	public void collapseDashboard(){
		System.out.println("Collapsing " + stashedPanelName + " " + stashedTabName);
		uiController.removeAll(uiController.find(stashedTabName));
		uiController.add(uiController.find(stashedTabName),stashedPanel);
	}
}

