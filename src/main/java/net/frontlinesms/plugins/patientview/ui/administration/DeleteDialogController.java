package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

/**
 * A controller class for showing 'delete' options to the user
 *
 */
public class DeleteDialogController extends ViewHandler{
	
	private final static String UI_XML = "/ui/plugins/patientview/administration/deleteDialog.xml";
	
	/**
	 * The parent controller that will be notified when this dialog closes
	 */
	private DeleteDialogDelegate parentController;
	
	/** The name of the entity, e.g. Patient, CHW, etc..*/
	private String entityName;
	
	private CommunityHealthWorkerDao chwDao;
	
	
	private static final String CHW_OPTION_PANEL = "chwOptionPanel";
	private static final String ORPHAN_RADIO_BUTTON = "orphanRadio";
	private static final String TRANSFER_RADIO_BUTTON = "transferRadio";
	private static final String TRANSFER_CHW_SELECT = "transferChwSelect";
	private static final String REASON_TEXT_AREA = "reasonArea";
	
	public DeleteDialogController(UiGeneratorController uiController, DeleteDialogDelegate parentController, String entityName){
		super(uiController,null,UI_XML);
		this.parentController = parentController;
		this.entityName = entityName;
		init(null);
	}
	
	public DeleteDialogController(UiGeneratorController uiController, DeleteDialogDelegate parentController, String entityName, ApplicationContext appCon, CommunityHealthWorker toDelete){
		super(uiController,null,UI_XML);
		this.parentController = parentController;
		this.entityName = entityName;
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		init(toDelete);
	}
	
	private void init(CommunityHealthWorker toDelete){
		ui.setText(mainPanel, "Delete " + entityName);
		ui.add(mainPanel);
		if(chwDao != null){
			ui.setVisible(find(CHW_OPTION_PANEL),true);
			ui.setAction(find(ORPHAN_RADIO_BUTTON), "radioSelectionChanged()", null, this);
			ui.setAction(find(TRANSFER_RADIO_BUTTON), "radioSelectionChanged()", null, this);
			List<CommunityHealthWorker> chws = chwDao.getAllCommunityHealthWorkers(false);
			for(CommunityHealthWorker chw: chws){
				if(chw.getPid() != toDelete.getPid()){
					Object choice = ui.createComboboxChoice(chw.getName(), chw);
					ui.add(find(TRANSFER_CHW_SELECT),choice);
				}
			}
			ui.setSelectedIndex(find(TRANSFER_CHW_SELECT),0);
		}
		ui.setVisible(mainPanel, true);
	}
	
	public void radioSelectionChanged(){
		ui.setEnabled(find(TRANSFER_CHW_SELECT),ui.isSelected(find(TRANSFER_RADIO_BUTTON)));
	}
	
	public void deleteClicked(){
		closeDialog();
		if(chwDao == null || !ui.isSelected(find(TRANSFER_RADIO_BUTTON))){
			parentController.dialogReturned(true, ui.getText(find(REASON_TEXT_AREA)),null);
		}else{
			CommunityHealthWorker newChw = (CommunityHealthWorker) ui.getAttachedObject(ui.getSelectedItem(find(TRANSFER_CHW_SELECT)));
			parentController.dialogReturned(true, ui.getText(find(REASON_TEXT_AREA)),newChw);
		}
	}
	
	public void cancelClicked(){
		closeDialog();
		parentController.dialogReturned(false,null,null);
	}

	private void closeDialog(){
		ui.remove(mainPanel);
	}
}