package net.frontlinesms.plugins.patientview.ui.administration;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

public abstract class AdministrationTabPanel extends ViewHandler{

	public AdministrationTabPanel(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon);
	}
	
	public AdministrationTabPanel(UiGeneratorController ui, ApplicationContext appCon,String xmlPath) {
		super(ui, appCon, xmlPath);
	}

	/**
	 * @return The desired title for the panel's list item in the action list
	 */
	public abstract String getListItemTitle();
	
	public abstract String getIconPath();
}
