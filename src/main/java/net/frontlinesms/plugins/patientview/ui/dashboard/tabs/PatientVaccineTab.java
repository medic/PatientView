package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.vaccine.RescheduleVaccinesDialog;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientVaccineTab extends TabController implements ThinletUiEventHandler, TableActionDelegate, FormFieldDelegate {

	private static final String MAIN_THINLET_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/patientVaccineTab.xml";
	private static final String SCHEDULE_VACCINE_PANEL_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/scheduleVaccinePanel.xml";
	private static final String RESCHEDULE_DOSE_PANEL_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/rescheduleDosePanel.xml";
	private static final String DOSE_BUTTON_PANEL_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/doseButtonPanel.xml";
	private static final String ADMINISTER_DOSE_PANEL_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/administerDosePanel.xml";
	
	private static final String VACCINE_LIST = "vaccineList";
	private static final String VACCINE_LIST_BUTTON_PANEL = "vaccineButtonPanel";
	private static final String DOSE_BUTTON_PANEL = "doseButtonPanel";
	
	private static final String RESCHEDULE_DOSE_DATE_SCHEDULED_PANEL = "dateScheduledPanel";
	private static final String RESCHEDULE_DOSE_END_DATE_PANEL = "endDatePanel";
	private static final String RESCHEDULE_DOSE_BUTTON = "rescheduleDoseButton";
	private static final String ADMINISTER_DOSE_BUTTON = "adminsterDoseButton";
	private static final String ADMINISTRATION_LOCATION_FIELD = "administrationLocationField";
	private static final String RESCHEDULE_ALL_DOSES_BUTTON = "rescheduleAllButton";
	
	private static final String VACCINE_SELECT = "vaccineSelect";
	private static final String SCHEDULE_VACCINE_BUTTON = "scheduleVaccineButton";
	private static final String REMOVE_VACCINE_BUTTON = "removeVaccineButton";
	private static final String SCHEDULED_DOSE_TABLE_PANEL ="scheduledDoseTablePanel";
	private static final String SCHEDULED_DOSE_PANEL ="scheduledDosePanel";
	
	private static final String FROM_BIRTH_RADIO = "fromBirthRadio";
	private static final String FROM_TODAY_RADIO = "fromTodayRadio";
	private static final String FIRST_SHOT_TODAY_RADIO = "firstShotTodayRadio";
	
	private static final String CONFIRMATION_DIALOG = "confirmDialog";
	
	private AdvancedTableController scheduledDoseController;
	
	private Patient patient;
	private Vaccine currentVaccine;

	private VaccineDao vaccineDao;
	private ScheduledDoseDao scheduledDoseDao;
	
	
	private DateField rescheduledStartDateField;
	private DateField rescheduledEndDateField;
	private DateField administeredDateField;
	private TextField placeAdministeredField;
	
	private ScheduledDose toBeRescheduled;
	private ScheduledDose toBeAdministered;
	
	public PatientVaccineTab(UiGeneratorController uiController, ApplicationContext appCon, Patient patient) {
		super(uiController, appCon);
		//init the Daos
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		this.patient = patient;
		//setup UI
		setTitle("Appointments");
		setIconPath("/icons/syringe.png");
		this.mainPanel = ui.loadComponentFromFile(MAIN_THINLET_XML, this);
		ui.removeAll(getTab());
		ui.add(getTab(),getMainPanel());
		//setup table
		scheduledDoseController = new AdvancedTableController(this, uiController);
		List<HeaderColumn> doseColumns = new ArrayList<HeaderColumn>();
		doseColumns.add(new HeaderColumn("getDoseName", "/icons/syringe_small.png", "Appointment Name"));
		doseColumns.add(new HeaderColumn("getWindowStartDateString", "/icons/date_add.png", "Date Scheduled"));
		doseColumns.add(new HeaderColumn("getAdministeredString", "/icons/tick.png", "Status"));
		doseColumns.add(new HeaderColumn("getWindowEndDateString", "/icons/date_delete.png", "Window End Date"));
		scheduledDoseController.putHeader(ScheduledDose.class, doseColumns);
		scheduledDoseController.setNoResultsMessage("No Appointments");
		scheduledDoseController.setResults(new ArrayList<ScheduledDose>());
		ui.add(ui.find(mainPanel,SCHEDULED_DOSE_TABLE_PANEL),scheduledDoseController.getMainPanel());
		populateVaccineList(null);
		refreshDoseTable();
	}
	
	public void populateVaccineList(Vaccine toSelect){
		ui.removeAll(ui.find(mainPanel,VACCINE_LIST));
		Set<Vaccine> scheduledVaccines = vaccineDao.getScheduledVaccinesForPatient(patient);
		
		//if there are 2 or more vaccines, add an 'all vaccines' option
		if(scheduledVaccines.size()>=2){
			ui.add(ui.find(mainPanel,VACCINE_LIST),ui.createListItem("All Appointment Schedules", null));
		}
		int selectIndex = 0;
		boolean foundVaccine = false;
		//add all the vaccines to the list
		for(Vaccine v: scheduledVaccines){
			ui.add(ui.find(mainPanel,VACCINE_LIST),ui.createListItem(v.getName(), v));
			if(!foundVaccine){
				selectIndex++;
				 if(toSelect != null && v.getVaccineId() == toSelect.getVaccineId()){
					 foundVaccine = true;
				 }
			}
		}
		// if there are no vaccines, add a "No Vaccines" option
		if(scheduledVaccines.size() == 0){
			ui.add(ui.find(mainPanel,VACCINE_LIST),ui.createListItem("No Appointment Schedules", null));
		}else{// if there are vaccines, select index 1
			if(toSelect == null){
				ui.setSelectedIndex(ui.find(mainPanel,VACCINE_LIST),0);
			}else{
				if(scheduledVaccines.size()>=2){
					ui.setSelectedIndex(ui.find(mainPanel,VACCINE_LIST),selectIndex);
				}else{
					ui.setSelectedIndex(ui.find(mainPanel,VACCINE_LIST),selectIndex-1);
				}
			}
		}
		updateVaccineButtons(scheduledVaccines);
	}
	
	public Vaccine getSelectedVaccine(){
		Object selected = ui.getSelectedItem(ui.find(mainPanel,VACCINE_LIST));
		if(selected != null){
			return (Vaccine) ui.getAttachedObject(selected);
		}else{
			return null;
		}
	}
	
	public void updateVaccineButtons(Set<Vaccine> scheduledVaccines){
		if(scheduledVaccines.size() == 0){
			ui.setEnabled(ui.find(mainPanel,REMOVE_VACCINE_BUTTON), false);
		}else if(getSelectedVaccine() != null){
			ui.setEnabled(ui.find(mainPanel,REMOVE_VACCINE_BUTTON), true);			
		}else{
			ui.setEnabled(ui.find(mainPanel,REMOVE_VACCINE_BUTTON), false);
		}
		//figure out if we should enable the schedule button
		if(vaccineDao.getUnscheduledVaccinesForPatient(patient).size() == 0){
			ui.setEnabled(ui.find(mainPanel,SCHEDULE_VACCINE_BUTTON), false);
		}else{
			ui.setEnabled(ui.find(mainPanel,SCHEDULE_VACCINE_BUTTON), true);
		}
	}
	
	public void vaccineListSelectionChanged(){
		Set<Vaccine> scheduledVaccines = vaccineDao.getScheduledVaccinesForPatient(patient);
		updateVaccineButtons(scheduledVaccines);
		refreshDoseTable();
	}
	
	public void refreshDoseTable(){
		int index = scheduledDoseController.getSelectedIndex();
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(getSelectedVaccine(),patient);
		scheduledDoseController.setResults(doses);
		if(index >= doses.size()){
			index = doses.size()-1;
		}
		scheduledDoseController.setSelected(index);
	}
	
	private void updateDoseActionButtons(){
		ScheduledDose dose = (ScheduledDose) scheduledDoseController.getCurrentlySelectedObject();
		ui.setEnabled(ui.find(mainPanel,RESCHEDULE_ALL_DOSES_BUTTON),scheduledDoseController.getResultsSize() > 0);
		if(ui.find(mainPanel,RESCHEDULE_DOSE_BUTTON) == null){
			rescheduleDoseCancelled();
		}
		if(dose == null){
			ui.setEnabled(ui.find(mainPanel,RESCHEDULE_DOSE_BUTTON), false);
			ui.setEnabled(ui.find(mainPanel,ADMINISTER_DOSE_BUTTON), false);
			return;
		}
		if(!dose.isAdministered()){
			ui.setEnabled(ui.find(mainPanel,RESCHEDULE_DOSE_BUTTON), true);
			boolean cannotAdminister = false;
			for(ScheduledDose d:scheduledDoseDao.getScheduledDoses(dose.getDose().getVaccine(),patient)){
				if(d.getDose().getPosition() < dose.getDose().getPosition() && !d.isAdministered()){
					cannotAdminister=true;
				}
			}
			if(cannotAdminister){
				ui.setEnabled(ui.find(mainPanel,ADMINISTER_DOSE_BUTTON), false);
			}else{
				ui.setEnabled(ui.find(mainPanel,ADMINISTER_DOSE_BUTTON), true);
			}
		}else{
			ui.setEnabled(ui.find(mainPanel,RESCHEDULE_DOSE_BUTTON), false);
			ui.setEnabled(ui.find(mainPanel,ADMINISTER_DOSE_BUTTON), false);
		}
	}
	
	/*Schedule Vaccine action methods */

	public void scheduleVaccineClicked(){
		ui.removeAll(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL));
		ui.add(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL),ui.loadComponentFromFile(SCHEDULE_VACCINE_PANEL_XML, this));
		List<Vaccine> vaccines = vaccineDao.getUnscheduledVaccinesForPatient(patient);
		boolean shouldSelect = true;
		for(Vaccine v: vaccines){
			Object choice = ui.createComboboxChoice(v.getName(),v);
			ui.add(ui.find(mainPanel,VACCINE_SELECT),choice);
			if(shouldSelect){
				ui.setSelectedIndex(ui.find(mainPanel,VACCINE_SELECT), 0);
				ui.setText(ui.find(mainPanel,VACCINE_SELECT), v.getName());
				shouldSelect = false;
			}
		}
	}
	
	public void scheduleVaccineConfirmed(){
		if(ui.find(mainPanel,"confirmScheduleButton") == null) return;
		Vaccine v = (Vaccine) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,VACCINE_SELECT)));
		if(ui.isSelected(ui.find(FROM_BIRTH_RADIO))){
			scheduledDoseDao.saveScheduledDoses(VaccineScheduler.instance().scheduleVaccinesFromBirth(patient, v));
		}else if(ui.isSelected(ui.find(FROM_TODAY_RADIO))){
			scheduledDoseDao.saveScheduledDoses(VaccineScheduler.instance().scheduleVaccinesFromToday(patient, v));
		}else if(ui.isSelected(ui.find(FIRST_SHOT_TODAY_RADIO))){
			if(patient.getDateOfConception() != null && patient.getDateOfConception() > 0){
				scheduledDoseDao.saveScheduledDoses(VaccineScheduler.instance().scheduleVaccinesFromDateOfConception(patient,v));
			}else{
				ui.alert("This patient's date of conception has not been entered into the system yet.");
				return;
			}
		}
		scheduleVaccineCanceled();
		populateVaccineList(v);
		refreshDoseTable();
	}
	
	public void scheduleVaccineCanceled(){
		ui.removeAll(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL));
		Object panel = ui.createPanel("");
		Object scheduleButton = ui.createButton("Add Appointment Schedule", "scheduleVaccineClicked()", null, this);
		Object removeButton = ui.createButton("Remove Appointment Schedule", "removeVaccineClicked()", null, this);
		ui.setName(scheduleButton, SCHEDULE_VACCINE_BUTTON);
		ui.setName(removeButton, REMOVE_VACCINE_BUTTON);
		ui.setWeight(scheduleButton, 1, 0);
		ui.setWeight(removeButton, 1, 0);
		ui.setIcon(scheduleButton, "/icons/schedule_vaccine.png");
		ui.setIcon(removeButton, "/icons/delete_vaccine_large.png");
		ui.add(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL),scheduleButton);
		ui.add(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL),removeButton);
		updateVaccineButtons(vaccineDao.getScheduledVaccinesForPatient(patient));
	}
	
	/* Remove vaccine action methods */
	
	public void removeVaccineClicked(){
		Vaccine v = (Vaccine) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,VACCINE_LIST)));
		if(scheduledDoseDao.patientHasAdministeredDosesForVaccine(patient, v)){
			ui.alert("You cannot remove a patient from an appointment schedule once one appointment has been completed.");
		}else{
			ui.showConfirmationDialog("removeVaccineConfirmed()", this,"medic.vaccine.confirm.remove");
		}
	}
	
	public void removeVaccineConfirmed(){
		ui.remove(ui.find(CONFIRMATION_DIALOG));
		Vaccine v = (Vaccine) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,VACCINE_LIST)));
		scheduledDoseDao.deleteScheduledDosesForVaccine(v,patient);
		populateVaccineList(null);
		refreshDoseTable();
	}
	
	/* Reschedule dose action methods */
	
	public void rescheduleDoseClicked(){
		ui.removeAll(ui.find(mainPanel,DOSE_BUTTON_PANEL));
		//create the basic panel
		Object reschedulePanel = ui.loadComponentFromFile(RESCHEDULE_DOSE_PANEL_XML,this);
		//get the currently selected dose
		toBeRescheduled = (ScheduledDose) scheduledDoseController.getCurrentlySelectedObject();
		//setup the date fields
		rescheduledStartDateField = new DateField(ui, "New Appointment Date", this,false);
		rescheduledStartDateField.setRawResponse(toBeRescheduled.getWindowStartDate());
		//add the date fields to the UI
		ui.add(ui.find(reschedulePanel,RESCHEDULE_DOSE_DATE_SCHEDULED_PANEL),rescheduledStartDateField.getThinletPanel());
		//add the panel to the UI
		ui.add(ui.find(mainPanel,DOSE_BUTTON_PANEL),reschedulePanel);
	}
	
	public void rescheduleDoseConfirmed(){
		if(VaccineScheduler.instance().doseWillViolatePreviousWindow(toBeRescheduled, rescheduledStartDateField.getRawResponse())){
			Object dialog = ui.showConfirmationDialog("windowViolationAcknowledged()", this, "medic.vaccine.reschedule.will.violate.window");
		}else{
			rescheduleDose();
		}
	}
	
	public void windowViolationAcknowledged(){
		ui.remove(ui.find(CONFIRMATION_DIALOG));
		rescheduleDose();
	}
	
	private void rescheduleDose(){
		//reschedule the doses
		List<ScheduledDose> toSave = VaccineScheduler.instance().rescheduleDose(toBeRescheduled, new Date(rescheduledStartDateField.getRawResponse()));
		//save the doses
		scheduledDoseDao.saveOrUpdateScheduledDose(toBeRescheduled);
		scheduledDoseDao.saveScheduledDoses(toSave);
		//clean up
		toBeRescheduled = null;
		rescheduledStartDateField = null;
		rescheduleDoseCancelled();
		refreshDoseTable();
	}
	
	public void rescheduleDoseCancelled(){
		ui.remove(ui.find(mainPanel,DOSE_BUTTON_PANEL));
		ui.add(ui.find(mainPanel,SCHEDULED_DOSE_PANEL),ui.loadComponentFromFile(DOSE_BUTTON_PANEL_XML, this));
	}
	
	/* Reschedule all doses action method */
	
	public void rescheduleAllDosesClicked(){
		RescheduleVaccinesDialog dialog = new RescheduleVaccinesDialog(ui, appCon, patient,this);
	}
	
	/* Administer dose action methods */
	
	public void administerDoseClicked(){
		ui.removeAll(ui.find(mainPanel,DOSE_BUTTON_PANEL));
		//create the basic panel
		Object administerPanel = ui.loadComponentFromFile(ADMINISTER_DOSE_PANEL_XML,this);
		//get the currently selected dose
		toBeAdministered = (ScheduledDose) scheduledDoseController.getCurrentlySelectedObject();
		administeredDateField = new DateField(ui,"Date",this,false);
		administeredDateField.setRawResponse(new Date().getTime());
		ui.add(ui.find(administerPanel,"administerDatePanel"),administeredDateField.getThinletPanel());
		placeAdministeredField = new TextField(ui,"Place",this);
		ui.add(ui.find(administerPanel,"administerDatePanel"),placeAdministeredField.getThinletPanel());
		ui.add(ui.find(mainPanel,DOSE_BUTTON_PANEL),administerPanel);
	}
	
	public void administerDoseConfirmed(){
		if(ui.find(mainPanel,"confirmAdministerDoseButton") == null) return;
		scheduledDoseDao.administerDose(toBeAdministered, null,administeredDateField.getRawResponse(),placeAdministeredField.getRawResponse());
		List<ScheduledDose> doses = VaccineScheduler.instance().rescheduleRemainingDoses(toBeAdministered);
		scheduledDoseDao.saveScheduledDoses(doses);
		toBeAdministered = null;
		administeredDateField = null;
		placeAdministeredField = null;
		administerDoseCancelled();
		refreshDoseTable();
	}
	
	public void administerDoseCancelled(){
		ui.remove(ui.find(mainPanel,DOSE_BUTTON_PANEL));
		ui.add(ui.find(mainPanel,SCHEDULED_DOSE_PANEL),ui.loadComponentFromFile(DOSE_BUTTON_PANEL_XML, this));
	}
	
	public void selectionChanged(Object selectedObject) {
		updateDoseActionButtons();
	}

	public void formFieldChanged(ThinletFormField changedField, String newValue) {}
	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}
}
