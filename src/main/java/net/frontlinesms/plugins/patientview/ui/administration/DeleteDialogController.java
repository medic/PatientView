package net.frontlinesms.plugins.patientview.ui.administration;

import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * A controller class for showing 'delete' options to the user
 * @author dieterichlawson
 *
 */
public class DeleteDialogController extends ViewHandler{
	
	private final static String UI_XML = "/ui/plugins/patientview/administration/deleteDialog.xml";
	
	/**
	 * The parent controller that will be notified when this dialog closes
	 */
	private DeleteDialogDelegate parentController;
	
	/** Top level thinlet object*/
	private Object reasonTextArea;
	
	/** The name of the entity, e.g. Patient, CHW, etc..*/
	private String entityName;
	
	public DeleteDialogController(UiGeneratorController uiController, DeleteDialogDelegate parentController, String entityName){
		super(uiController,null,UI_XML);
		this.parentController = parentController;
		this.entityName = entityName;
		init();
	}
	
	private void init(){
		reasonTextArea  = find("reasonArea");
		ui.setText(mainPanel, "Delete " + entityName);
		ui.add(mainPanel);
		ui.setVisible(mainPanel, true);
	}
	
	public void deleteClicked(){
		closeDialog();
		parentController.dialogReturned(true, ui.getText(reasonTextArea));
	}
	
	public void cancelClicked(){
		closeDialog();
		parentController.dialogReturned(false,null);
	}

	private void closeDialog(){
		ui.setVisible(mainPanel,false);
		ui.remove(mainPanel);
	}
}
