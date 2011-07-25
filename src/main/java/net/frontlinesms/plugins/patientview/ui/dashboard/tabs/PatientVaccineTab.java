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
import net.frontlinesms.plugins.patientview.data.repository.VaccineDoseDao;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
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
	private VaccineDoseDao doseDao;
	private ScheduledDoseDao scheduledDoseDao;
	
	
	private DateField rescheduledStartDateField;
	private DateField rescheduledEndDateField;
	private DateField administeredDateField;
	
	private ScheduledDose toBeRescheduled;
	private ScheduledDose toBeAdministered;
	
	public PatientVaccineTab(UiGeneratorController uiController, ApplicationContext appCon, Patient patient) {
		super(uiController, appCon);
		//init the Daos
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.doseDao = (VaccineDoseDao) appCon.getBean("VaccineDoseDao");
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		this.patient = patient;
		//setup UI
		setTitle("Vaccines");
		setIconPath("/icons/syringe.png");
		this.mainPanel = ui.loadComponentFromFile(MAIN_THINLET_XML, this);
		ui.removeAll(getTab());
		ui.add(getTab(),getMainPanel());
		//setup table
		scheduledDoseController = new AdvancedTableController(this, uiController);
		List<HeaderColumn> doseColumns = new ArrayList<HeaderColumn>();
		doseColumns.add(new HeaderColumn("getDoseName", "/icons/syringe_small.png", "Dose Name"));
		doseColumns.add(new HeaderColumn("getWindowStartDateString", "/icons/date_add.png", "Date Scheduled"));
		doseColumns.add(new HeaderColumn("getAdministeredString", "/icons/tick.png", "Date Administered"));
		doseColumns.add(new HeaderColumn("getWindowEndDateString", "/icons/date_delete.png", "Window End Date"));
		scheduledDoseController.putHeader(ScheduledDose.class, doseColumns);
		scheduledDoseController.setNoResultsMessage("No Scheduled Vaccines");
		scheduledDoseController.setResults(new ArrayList<ScheduledDose>());
		ui.add(ui.find(mainPanel,SCHEDULED_DOSE_TABLE_PANEL),scheduledDoseController.getMainPanel());
		populateVaccineList();
		refreshDoseTable();
	}
	
	public void populateVaccineList(){
		ui.removeAll(ui.find(mainPanel,VACCINE_LIST));
		Set<Vaccine> scheduledVaccines = vaccineDao.getScheduledVaccinesForPatient(patient);
		for(Vaccine v: scheduledVaccines){
			ui.add(ui.find(mainPanel,VACCINE_LIST),ui.createListItem(v.getName(), v));
		}
		if(scheduledVaccines.size() == 0){
			ui.add(ui.find(mainPanel,VACCINE_LIST),ui.createListItem("No Vaccines", null));
			ui.setEnabled(ui.find(mainPanel,REMOVE_VACCINE_BUTTON), false);
		}else{
			ui.setEnabled(ui.find(mainPanel,REMOVE_VACCINE_BUTTON), true);
			ui.setSelectedIndex(ui.find(mainPanel,VACCINE_LIST),0);
			
		}
		//figure out if we should enable the schedule button
		if(vaccineDao.getUnscheduledVaccinesForPatient(patient).size() == 0){
			ui.setEnabled(ui.find(mainPanel,SCHEDULE_VACCINE_BUTTON), false);
		}else{
			ui.setEnabled(ui.find(mainPanel,SCHEDULE_VACCINE_BUTTON), true);
		}
	}
	
	public void refreshDoseTable(){
		int index = scheduledDoseController.getSelectedIndex();
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient,null);
		scheduledDoseController.setResults(doses);
		scheduledDoseController.setSelected(index);
	}
	
	private void updateDoseActionButtons(){
		ScheduledDose dose = (ScheduledDose) scheduledDoseController.getCurrentlySelectedObject();
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

	public void scheduleVaccine(){
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
		Vaccine v = (Vaccine) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,VACCINE_SELECT)));
		if(ui.isSelected(ui.find(FROM_BIRTH_RADIO))){
			scheduledDoseDao.saveScheduledDoses(VaccineScheduler.instance().scheduleVaccinesFromBirth(patient, v));
		}else if(ui.isSelected(ui.find(FROM_TODAY_RADIO))){
			scheduledDoseDao.saveScheduledDoses(VaccineScheduler.instance().scheduleVaccinesFromToday(patient, v));
		}else if(ui.isSelected(ui.find(FIRST_SHOT_TODAY_RADIO))){
			scheduledDoseDao.saveScheduledDoses(VaccineScheduler.instance().scheduleVaccinesFirstDoseToday(patient,v));
		}
		refreshDoseTable();
		scheduleVaccineCanceled();
	}
	
	public void scheduleVaccineCanceled(){
		ui.removeAll(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL));
		Object panel = ui.createPanel("");
		Object scheduleButton = ui.createButton("Schedule Vaccine", "scheduleVaccine()", null, this);
		Object removeButton = ui.createButton("Remove Vaccine", "removeVaccine()", null, this);
		ui.setName(scheduleButton, SCHEDULE_VACCINE_BUTTON);
		ui.setName(removeButton, REMOVE_VACCINE_BUTTON);
		ui.setWeight(scheduleButton, 1, 0);
		ui.setWeight(removeButton, 1, 0);
		ui.setIcon(scheduleButton, "/icons/schedule_vaccine.png");
		ui.setIcon(removeButton, "/icons/delete_vaccine_large.png");
		ui.add(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL),scheduleButton);
		ui.add(ui.find(mainPanel,VACCINE_LIST_BUTTON_PANEL),removeButton);
		populateVaccineList();
	}
	
	public void removeVaccine(){
		Vaccine v = (Vaccine) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,VACCINE_LIST)));
		if(scheduledDoseDao.patientHasAdministeredDosesForVaccine(patient, v)){
			ui.alert("You cannot remove a patient from a scheduled vaccine series once a dose has been administered");
		}else{
			ui.showConfirmationDialog("removeVaccineConfirmed()", this,"medic.vaccine.confirm.remove");
		}
	}
	
	public void removeVaccineConfirmed(){
		ui.remove(ui.find(CONFIRMATION_DIALOG));
		Vaccine v = (Vaccine) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,VACCINE_LIST)));
		scheduledDoseDao.deleteScheduledDosesForVaccine(v,patient);
		populateVaccineList();
		refreshDoseTable();
	}
	
	public void rescheduleDose(){
		ui.removeAll(ui.find(mainPanel,DOSE_BUTTON_PANEL));
		//create the basic panel
		Object reschedulePanel = ui.loadComponentFromFile(RESCHEDULE_DOSE_PANEL_XML,this);
		//get the currently selected dose
		toBeRescheduled = (ScheduledDose) scheduledDoseController.getCurrentlySelectedObject();
		//setup the date fields
		rescheduledStartDateField = new DateField(ui, "Date Scheduled", this,false);
		rescheduledStartDateField.setRawResponse(toBeRescheduled.getWindowStartDate().getTime());
		//add the date fields to the UI
		ui.add(ui.find(reschedulePanel,RESCHEDULE_DOSE_DATE_SCHEDULED_PANEL),rescheduledStartDateField.getThinletPanel());
		//add the panel to the UI
		ui.add(ui.find(mainPanel,DOSE_BUTTON_PANEL),reschedulePanel);
	}
	
	public void rescheduleDoseConfirmed(){
		//reschedule the doses
		List<ScheduledDose> toSave = VaccineScheduler.instance().rescheduleDose(toBeRescheduled, rescheduledStartDateField.getRawResponse());
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
	
	public void administerDose(){
		ui.removeAll(ui.find(mainPanel,DOSE_BUTTON_PANEL));
		//create the basic panel
		Object administerPanel = ui.loadComponentFromFile(ADMINISTER_DOSE_PANEL_XML,this);
		//get the currently selected dose
		toBeAdministered = (ScheduledDose) scheduledDoseController.getCurrentlySelectedObject();
		administeredDateField = new DateField(ui,"Administered Date",this,false);
		administeredDateField.setRawResponse(new Date());
		ui.add(ui.find(administerPanel,"administerDatePanel"),administeredDateField.getThinletPanel());
		ui.add(ui.find(mainPanel,DOSE_BUTTON_PANEL),administerPanel);
	}
	
	public void administerDoseConfirmed(){
		scheduledDoseDao.administerDose(toBeAdministered, null,administeredDateField.getRawResponse());
		List<ScheduledDose> doses = VaccineScheduler.instance().rescheduleRemainingDoses(toBeAdministered);
		scheduledDoseDao.saveScheduledDoses(doses);
		toBeAdministered = null;
		administeredDateField = null;
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
