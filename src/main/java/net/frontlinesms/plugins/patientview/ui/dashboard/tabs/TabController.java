package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class TabController extends ViewHandler{

	private Object tab;
	
	public TabController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon);
		this.tab = ExtendedThinlet.create("tab");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setInteger(mainPanel, "top", 5);
		uiController.setInteger(mainPanel, "left", 5);
		uiController.setInteger(mainPanel, "right", 5);
		uiController.setInteger(mainPanel, "bottom", 5);
		uiController.add(tab,mainPanel);
	}

	public Object getTab() {
		return tab;
	}
	
	protected void setTitle(String title){
		ui.setText(tab, title);
	}
	
	protected void setIconPath(String iconPath){
		ui.setIcon(tab,iconPath);
	}
	
	public void willAppear(){
		System.out.println("Tab selected: " + ui.getText(tab));
	}
	
	public void willDisappear(){
		System.out.println("Tab unselected: " + ui.getText(tab));
	}
}
