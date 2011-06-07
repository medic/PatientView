package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.PatientDashboard;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class PatientDetailViewPanelController extends DetailViewPanelController<Patient> {

	private static final String EDIT_PATIENT_ATTRIBUTES = "detailview.buttons.edit.attributes";
	private static final String SAVE_PATIENT_ATTRIBUTES = "detailview.buttons.save";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String SEE_MORE = "detailview.buttons.see.more";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/patient_edit_";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	private static final String EXPAND_DETAIL_VIEW_ICON = "/icons/patient_file.png";
	
	private Patient currentPatient;
	private boolean inEditingMode;
	
	private PatientPanel currentPatientPanel;
	private PersonAttributePanel currentAttributePanel;
	
	public PatientDetailViewPanelController(UiGeneratorController uiController,ApplicationContext appCon){
		super(uiController,appCon);
		inEditingMode=false;
	}
	public Class<Patient> getEntityClass() {
		return Patient.class;
	}

	public void willAppear(Patient p) {
		inEditingMode=false;
		currentPatient= p;
		mainPanel = Thinlet.create("panel");
		ui.setWeight(mainPanel, 1, 1);
		ui.setColumns(mainPanel, 1);
		currentPatientPanel = new PatientPanel(ui,appCon,p);
		currentAttributePanel = new PersonAttributePanel(ui,appCon,p);
		add(currentPatientPanel.getMainPanel());
		add(currentAttributePanel.getMainPanel());
		add(getBottomButtons());
	}
	
	private Object getBottomButtons(){
		Object buttonPanel = Thinlet.create("panel");
		ui.setName(buttonPanel, "buttonPanel");
		ui.setColumns(buttonPanel, 3);
		Object leftButton = ui.createButton(!inEditingMode?getI18nString(EDIT_PATIENT_ATTRIBUTES):getI18nString(SAVE_PATIENT_ATTRIBUTES));
		Object rightButton = ui.createButton(!inEditingMode?getI18nString(SEE_MORE):getI18nString(CANCEL));
		if(inEditingMode){
			ui.setAction(leftButton, "saveButtonClicked", null, this);
			ui.setAction(rightButton, "cancelButtonClicked", null, this);
			ui.setIcon(leftButton, SAVE_ICON);
			ui.setIcon(rightButton, CANCEL_ICON);
			
		}else{
			ui.setAction(leftButton, "editButtonClicked", null, this);
			ui.setAction(rightButton, "showPatientDashboard", null, this);
			ui.setIcon(leftButton, EDIT_ATTRIBUTE_ICON + (currentPatient.getGender() == Gender.MALE?"male.png":"female.png"));
			if(((PersonAttributeDao) appCon.getBean("PersonAttributeDao")).getAllAttributesForPerson(currentPatient).size() == 0 && ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getAttributePanelFields().size() == 0 ){
				ui.setEnabled(leftButton,false);
			}
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

	public void editButtonClicked(){
		inEditingMode=true;
		currentAttributePanel.switchToEditingPanel();
		remove(find("buttonPanel"));
		add(getBottomButtons());
	}
	
	public void saveButtonClicked(){
		if(currentAttributePanel.stopEditingWithSave()){
			inEditingMode=false;
			remove(find("buttonPanel"));
			add(getBottomButtons());
		}
	}
	
	public void cancelButtonClicked(){
		inEditingMode=false;
		currentAttributePanel.stopEditingWithoutSave();
		remove(find("buttonPanel"));
		add(getBottomButtons());
	}
	
	public void showPatientDashboard(){
		PatientDashboard patientDashboard = new PatientDashboard(ui,appCon,currentPatient);
		patientDashboard.expandDashboard();
	}
}
