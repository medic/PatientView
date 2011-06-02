package net.frontlinesms.plugins.patientview.ui.detailview;

import java.util.HashMap;

import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.CommunityHealthWorkerDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormResponseDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.PatientDetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class DetailViewController extends ViewHandler{
	
	private HashMap<Class,DetailViewPanelController> controllers;

	private DetailViewPanelController<?> currentViewController;
	
	public DetailViewController(Object panel, UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController,appCon);
		this.mainPanel = panel;
		CommunityHealthWorkerDetailViewPanelController chwPanel = new CommunityHealthWorkerDetailViewPanelController(uiController, appCon);
		FormDetailViewPanelController formPanel = new FormDetailViewPanelController(uiController);
	//	FormFieldDetailViewPanelController fieldPanel = new FormFieldDetailViewPanelController(uiController);
		FormResponseDetailViewPanelController formResponsePanel = new FormResponseDetailViewPanelController(uiController,appCon);
		PatientDetailViewPanelController patientPanel = new PatientDetailViewPanelController(uiController, appCon);
		
		controllers = new HashMap<Class,DetailViewPanelController>();
		controllers.put(chwPanel.getEntityClass(), chwPanel);
		controllers.put(formPanel.getEntityClass(), formPanel);
		//controllers.put(fieldPanel.getClass(), fieldPanel);
		controllers.put(formResponsePanel.getEntityClass(), formResponsePanel);
		controllers.put(patientPanel.getEntityClass(),patientPanel);
		//controllers.put(null, blankPanel);
		//selectionChanged(null);
	}
	
	/**
	 * Called when the selection changes in the main search table.
	 * Switches out the view controllers, after giving them fair warning
	 * @param entity The entity that was selected in the table
	 */
	public void selectionChanged(Object entity){
		
		if(entity == null)
			return;
		if(currentViewController !=null){
			removeSubview(currentViewController);
		}else{
			removeAll();
		}
		if(controllers.get(entity.getClass()) !=null){
			controllers.get(entity.getClass()).willAppear(entity);
			addSubview(controllers.get(entity.getClass()));
			currentViewController = controllers.get(entity.getClass());
		}
	}
	
}
