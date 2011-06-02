package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.CommunityHealthWorkerDashboard;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class CommunityHealthWorkerDetailViewPanelController extends DetailViewPanelController<CommunityHealthWorker> {

	private static final String EDIT_CHW_ATTRIBUTES = "detailview.buttons.edit.attributes";
	private static final String SAVE_CHW_ATTRIBUTES = "detailview.buttons.save";
	private static final String SEE_MORE = "detailview.buttons.see.more";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/big_user_edit.png";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	private static final String EXPAND_DETAIL_VIEW_ICON = "/icons/patient_file.png";
	
	private boolean inEditingMode;
	private CommunityHealthWorker currentCHW;
	
	private CommunityHealthWorkerPanel currentCHWPanel;
	private PersonAttributePanel currentAttributePanel;
	
	public CommunityHealthWorkerDetailViewPanelController(UiGeneratorController uiController,ApplicationContext appCon){
		super(uiController,appCon);
	}
	public Class<CommunityHealthWorker> getEntityClass() {
		return CommunityHealthWorker.class;
	}

	/**
	 * Puts in a CHW person panel and the attribute panel
	 * to go with it.
	 * 
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#willAppear(java.lang.Object)
	 */
	public void willAppear(CommunityHealthWorker p) {
		currentCHW = p;
		mainPanel = Thinlet.create("panel");
		ui.setWeight(mainPanel, 1, 1);
		ui.setColumns(mainPanel, 1);
		currentCHWPanel = new CommunityHealthWorkerPanel(ui,appCon,p);
		currentAttributePanel =new PersonAttributePanel(ui,appCon,p);
		add(currentCHWPanel.getMainPanel());
		add(currentAttributePanel.getMainPanel());
		add(getBottomButtons());
		subviewsWillAppear();
	}
	
	/**
	 * @return The buttons that go at the bottom of the panel,
	 * currently an "Edit this info" button if not in editing mode
	 * and a save/cancel pair if in editing mode.
	 */
	private Object getBottomButtons(){
		Object buttonPanel = Thinlet.create("panel");
		ui.setName(buttonPanel, "buttonPanel");
		ui.setColumns(buttonPanel, 3);
		Object leftButton = ui.createButton(!inEditingMode?getI18nString(EDIT_CHW_ATTRIBUTES):getI18nString(SAVE_CHW_ATTRIBUTES));
		Object rightButton = ui.createButton(!inEditingMode?getI18nString(SEE_MORE):getI18nString(CANCEL));
		if(inEditingMode){
			ui.setAction(leftButton, "saveButtonClicked", null, this);
			ui.setIcon(leftButton, SAVE_ICON);
			ui.setAction(rightButton, "cancelButtonClicked", null, this);
			ui.setIcon(rightButton, CANCEL_ICON);
		}else{
			ui.setAction(leftButton, "editButtonClicked", null, this);
			ui.setIcon(leftButton, EDIT_ATTRIBUTE_ICON);
			if(((PersonAttributeDao) appCon.getBean("PersonAttributeDao")).getAllAttributesForPerson(currentCHW).size() == 0){
				ui.setEnabled(leftButton,false);
			}
			ui.setAction(rightButton, "showCHWDashboard", null, this);
			ui.setIcon(rightButton, EXPAND_DETAIL_VIEW_ICON);
		}
		ui.setHAlign(leftButton, Thinlet.LEFT);
		ui.setVAlign(leftButton, Thinlet.BOTTOM);
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READWRITE||
		   UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
				add(buttonPanel,leftButton);
		}
		Object spacerLabel = ui.createLabel("");
		ui.setWeight(spacerLabel, 1, 0);
		add(buttonPanel,spacerLabel);
		ui.setHAlign(rightButton, Thinlet.RIGHT);
		ui.setVAlign(rightButton, Thinlet.BOTTOM);
		add(buttonPanel, rightButton);
		ui.setWeight(buttonPanel, 1, 1);
		ui.setVAlign(buttonPanel, Thinlet.BOTTOM);
		return buttonPanel;
	}

	/**
	 * Action method for the edit button
	 */
	public void editButtonClicked(){
		inEditingMode=true;
		currentAttributePanel.switchToEditingPanel();
		ui.remove(ui.find(mainPanel,"buttonPanel"));
		add(getBottomButtons());
	}
	
	/**
	 * Action method for the save button
	 */
	public void saveButtonClicked(){
		if(currentAttributePanel.stopEditingWithSave()){
			inEditingMode=false;
			ui.remove(ui.find(mainPanel,"buttonPanel"));
			add(getBottomButtons());
		}
	}
	
	/**
	 * Action method for the cancel button
	 */
	public void cancelButtonClicked(){
		inEditingMode=false;
		currentAttributePanel.stopEditingWithoutSave();
		ui.remove(ui.find(mainPanel,"buttonPanel"));
		add(getBottomButtons());
	}

	public void showCHWDashboard(){
		CommunityHealthWorkerDashboard chwDashboard = new CommunityHealthWorkerDashboard(ui,appCon,currentCHW);
		chwDashboard.expandDashboard();
	}
}
