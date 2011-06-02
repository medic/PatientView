package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import java.util.HashMap;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;

public class BlankPanelController extends DetailViewPanelController<Object>{

	public BlankPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon);
	}

	public Class<Object> getEntityClass() {
		return null;
	}

	public HashMap<String, String> getFurtherOptions() {
		return null;
	}

	public Object getMainPanel() {

		return null;
	}
	
	@Override
	public void willAppear(Object entity) {
		// TODO Auto-generated method stub
		
	}
}
