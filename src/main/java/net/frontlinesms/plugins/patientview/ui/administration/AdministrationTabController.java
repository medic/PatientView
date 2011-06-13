package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.importer.ui.CsvImporterPanelController;
import net.frontlinesms.plugins.patientview.responsemapping.ui.FormResponseMappingPanelController;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.AttributeAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.CommunityHealthWorkerAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.FormAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.PatientAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.UserAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.VaccineAdministrationPanelController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AdministrationTabController extends ViewHandler{

	/** the list of actions that can be performed*/
	private Object actionList;
	/** the panel containing the list to the left and the juicy stuff to the right
	 * you add the options to this panel to make them appear*/
	private Object splitPanel;
	
	/**the Thinlet XML files used for this tab **/
	private static final String UI_FILE_MAINTAB =  "/ui/plugins/patientview/administration/administrationTab.xml";
	private static final String TASK_LIST = "tasklist";
	private static final String SPLIT_PANEL = "splitpanel";
	private static final String ACTION_PANEL = "actionPanel";
	
	private ArrayList<AdministrationTabPanel> panels;
	
	private AdministrationTabPanel currentPanel;
	
	public AdministrationTabController(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController,appCon,UI_FILE_MAINTAB);
		init();
	}
	
	public void init(){
		//init the main, static components
		actionList = find(TASK_LIST);
		splitPanel = find(SPLIT_PANEL);
		//init the different choices for the action list
		panels = new ArrayList<AdministrationTabPanel>();
		panels.add(new PatientAdministrationPanelController(ui,appCon));
		panels.add(new CommunityHealthWorkerAdministrationPanelController(ui,appCon));
		panels.add(new UserAdministrationPanelController(ui,appCon));
		panels.add(new FormAdministrationPanelController(ui,appCon));
		//panels.add(new SecurityPanelController(uiController));
		panels.add(new AttributeAdministrationPanelController(ui,appCon));
		panels.add(new FormResponseMappingPanelController(ui,appCon));
		panels.add(new CsvImporterPanelController(ui,appCon));
		panels.add(new VaccineAdministrationPanelController(ui,appCon));
		//panels.add(new FlagAdministrationPanelController(ui, appCon));
		//create all the list items
		for(AdministrationTabPanel panel: panels){
			Object listItem = ui.createListItem(panel.getListItemTitle(), panel);
			ui.setIcon(listItem, panel.getIconPath());
			add(actionList,listItem);
		}
		ui.setSelectedIndex(actionList, 0);
		listSelectionChanged();
	}

	
	private Object getActionPanel(){
		return find(splitPanel, ACTION_PANEL);
	}
	
	public void listSelectionChanged(){
		AdministrationTabPanel panel = (AdministrationTabPanel) ui.getAttachedObject(ui.getSelectedItem(actionList));
		if(currentPanel != null){
			removeSubview(currentPanel);
		}else{
			removeAll(getActionPanel());
		}
		addSubview(getActionPanel(), panel);
		currentPanel = panel;
	}
}
