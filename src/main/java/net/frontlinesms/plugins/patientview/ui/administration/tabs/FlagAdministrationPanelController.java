package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;

public class FlagAdministrationPanelController extends AdministrationTabPanel {

	private static final String XML_PATH = "/ui/plugins/patientview/administration/flags/flagAdministrationPanel.xml";
	
	public FlagAdministrationPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon, XML_PATH);

	}

	@Override
	public String getIconPath() {
		return "/icons/flag_red.png";
	}

	@Override
	public String getListItemTitle() {
		return "Flags";
	}
	
	public void flagListSelectionChanged(){
		
	}
	
	public void addFlag(){
		
	}
	
	public void removeFlag(){
		
	}
}