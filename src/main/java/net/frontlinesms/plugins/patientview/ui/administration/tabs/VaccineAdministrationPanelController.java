package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDoseDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class VaccineAdministrationPanelController extends AdministrationTabPanel implements TableActionDelegate{

	private AdvancedTableController doseTableController;
	
	private VaccineDose currentlyEditingDose;
	
	private static final String MAIN_THINLET_XML = "/ui/plugins/patientview/administration/vaccines/vaccineAdministrationPanel.xml";
	private static final String ADD_VACCINE_XML = "/ui/plugins/patientview/administration/vaccines/addVaccinePanel.xml";
	private static final String VACCINE_BUTTONS_XML = "/ui/plugins/patientview/administration/vaccines/vaccineButtonsPanel.xml";
	private static final String EDIT_DOSE_XML = "/ui/plugins/patientview/administration/vaccines/editDosePanel.xml";
	private static final String DEFAULT_DOSE_PANEL_XML = "/ui/plugins/patientview/administration/vaccines/defaultDoseActionPanel.xml";
	
	private VaccineDao vaccineDao;
	private VaccineDoseDao vaccineDoseDao;
	private ScheduledDoseDao scheduledDoseDao;
	
	private static final String VACCINE_LIST= "vaccineList";
	private static final String VACCINE_BUTTON_PANEL = "vaccineButtonPanel";
	private static final String VACCINE_NAME_FIELD = "vaccineNameField";
	private static final String VACCINE_NAME_LABEL= "vaccineNameLabel";
	private static final String DOSE_ACTION_PANEL = "doseActionPanel";
	private static final String DOSE_TABLE_PANEL = "doseTable";
	private static final String EDIT_DOSE_PANEL = "editDosePanel";
	private static final String ENROLL_NEWBORNS_CHECKBOX = "enrollNewbornsCheckbox";
	private static final String START_DATE_MONTHS_BOX = "startDateMonths";
	private static final String START_DATE_DAYS_BOX = "startDateDays";
	private static final String END_DATE_MONTHS_BOX = "endDateMonths";
	private static final String END_DATE_DAYS_BOX = "endDateDays";
	private static final String MIN_INTERVAL_MONTHS_BOX = "minIntervalMonths";
	private static final String MIN_INTERVAL_DAYS_BOX = "minIntervalDays";
	private static final String DOSE_NAME_FIELD = "doseNameField";
	private static final String CONFIRMATION_DIALOG = "confirmDialog";
	
	
	private static final String ADD_VACCINE_BUTTON = "addVaccineButton";
	private static final String REMOVE_VACCINE_BUTTON = "removeVaccineButton";
	private static final String ADD_DOSE_BUTTON = "addDoseButton";
	private static final String DELETE_DOSE_BUTTON = "deleteDoseButton";
	private static final String EDIT_DOSE_BUTTON = "editDoseButton";
	private boolean firstShow = true;
	
	public VaccineAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon, MAIN_THINLET_XML);
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.vaccineDoseDao = (VaccineDoseDao) appCon.getBean("VaccineDoseDao");
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		doseTableController = new AdvancedTableController(this, uiController);
		doseTableController.setNoResultsMessage("No doses");
		List<HeaderColumn> columnList = new ArrayList<HeaderColumn>();
		columnList.add(new HeaderColumn("getName", "", "Dose Name"));
		columnList.add(new HeaderColumn("getStringStartDate", "", "Start Date"));
		columnList.add(new HeaderColumn("getStringEndDate", "", "End Date"));
		columnList.add(new HeaderColumn("getStringMinimumInterval", "", "Minimum Interval To Next Dose"));
		doseTableController.putHeader(VaccineDose.class, columnList);
		add(find(DOSE_TABLE_PANEL),doseTableController.getMainPanel());
	}
	
	@Override
	public String getIconPath() {
		return "/icons/syringe.png";
	}
	
	@Override
	public String getListItemTitle() {
		return "Manage Vaccines";
	}
	
	@Override
	public void willAppear(){
		refreshVaccines();
	}

	private void refreshVaccines(){
		int selectedIndex =ui.getSelectedIndex(find(VACCINE_LIST));
		removeAll(find(VACCINE_LIST));
		List<Vaccine> vaccines = vaccineDao.getAllVaccines();
		if(vaccines.size() == 0){
			add(find(VACCINE_LIST),ui.createListItem("No Vaccines",null));
		}else{
			for(Vaccine v: vaccines){
				add(find(VACCINE_LIST),ui.createListItem(v.getName(), v));
			}
		}
		if(selectedIndex < vaccines.size() && selectedIndex >=0){
			setVaccineListSelectedIndex(selectedIndex);
		}else{
			setVaccineListSelectedIndex(vaccines.size()-1);
		}
		if(vaccines.size() == 0){
			ui.setEnabled(find(REMOVE_VACCINE_BUTTON), false);
			ui.setEnabled(find(ADD_DOSE_BUTTON), false);
			ui.setEnabled(find(EDIT_DOSE_BUTTON), false);
			ui.setEnabled(find(DELETE_DOSE_BUTTON), false);
			ui.setEnabled(find(ENROLL_NEWBORNS_CHECKBOX), false);
		}else{
			ui.setEnabled(find(REMOVE_VACCINE_BUTTON), true);
			ui.setEnabled(find(ADD_DOSE_BUTTON), true);
			ui.setEnabled(find(ENROLL_NEWBORNS_CHECKBOX), true);
		}
	}
	
	public void addVaccine(){
		removeAll(find(VACCINE_BUTTON_PANEL));
		add(find(VACCINE_BUTTON_PANEL),ui.loadComponentFromFile(ADD_VACCINE_XML,this));
		ui.requestFocus(find(VACCINE_NAME_FIELD));
	}
	
	public void addVaccineConfirmed(){
		Vaccine v = new Vaccine(ui.getText(find(VACCINE_NAME_FIELD)),true);
		vaccineDao.saveOrUpdateVaccine(v);
		removeAll(find(VACCINE_BUTTON_PANEL));
		add(find(VACCINE_BUTTON_PANEL),ui.loadComponentFromFile(VACCINE_BUTTONS_XML,this));
		refreshVaccines();
	}
	
	public void addVaccineCanceled(){
		removeAll(find(VACCINE_BUTTON_PANEL));
		add(find(VACCINE_BUTTON_PANEL),ui.loadComponentFromFile(VACCINE_BUTTONS_XML,this));
	}
	
	private void setVaccineListSelectedIndex(int index){
		ui.setSelectedIndex(find(VACCINE_LIST), index);
		vaccineListSelectionChanged();
	}
	
	public void vaccineListSelectionChanged(){
		//add the standard dose panel
		removeAll(find(DOSE_ACTION_PANEL));
		add(find(DOSE_ACTION_PANEL),ui.loadComponentFromFile(DEFAULT_DOSE_PANEL_XML, this));
		//get the selected vaccine
		Vaccine v = getCurrentlySelectedVaccine();
		if(v == null){
			doseTableController.setResults(new ArrayList<VaccineDose>());
			ui.setEnabled(find(ADD_DOSE_BUTTON), false);
			ui.setEnabled(find(EDIT_DOSE_BUTTON), false);
			ui.setEnabled(find(DELETE_DOSE_BUTTON), false);
			return;
		}
		//set up the dose panel
		ui.setText(find(VACCINE_NAME_LABEL), v.getName());
		ui.setSelected(find(ENROLL_NEWBORNS_CHECKBOX), v.isAutomaticallyEnrollNewborns());
		//add the doses to the table
		int selectedIndex = doseTableController.getSelectedIndex();
		List<VaccineDose> doses = vaccineDoseDao.getDosesForVaccine(v);
		doseTableController.setResults(doses);
		if(doses.size() == 0){
			ui.setEnabled(find(EDIT_DOSE_BUTTON), false);
			ui.setEnabled(find(DELETE_DOSE_BUTTON), false);
		}else{
			ui.setEnabled(find(EDIT_DOSE_BUTTON), true);
			ui.setEnabled(find(DELETE_DOSE_BUTTON), true);
			if(selectedIndex < doses.size() && selectedIndex >=0){
				doseTableController.setSelected(selectedIndex);
			}else{
				doseTableController.setSelected(doses.size()-1);
			}
		}
	}
	
	private Vaccine getCurrentlySelectedVaccine(){
		return (Vaccine) ui.getAttachedObject(ui.getSelectedItem(find(VACCINE_LIST)));
	}
	public void editDose(){
		VaccineDose dose = (VaccineDose) doseTableController.getCurrentlySelectedObject();
		currentlyEditingDose = dose;
		removeAll(find(DOSE_ACTION_PANEL));
		add(find(DOSE_ACTION_PANEL),ui.loadComponentFromFile(EDIT_DOSE_XML, this));
		ui.setText(find(START_DATE_MONTHS_BOX),String.valueOf(dose.getStartDateMonths()));
		ui.setText(find(START_DATE_DAYS_BOX),String.valueOf(dose.getStartDateDays()));
		ui.setText(find(END_DATE_MONTHS_BOX),String.valueOf(dose.getEndDateMonths()));
		ui.setText(find(END_DATE_DAYS_BOX),String.valueOf(dose.getEndDateDays()));
		ui.setText(find(MIN_INTERVAL_MONTHS_BOX),String.valueOf(dose.getMinIntervalMonths()));
		ui.setText(find(MIN_INTERVAL_DAYS_BOX),String.valueOf(dose.getMinIntervalDays()));
		ui.setText(find(DOSE_NAME_FIELD),dose.getName());
		ui.setText(find(EDIT_DOSE_PANEL), "Editing "+ dose.getName());
		ui.requestFocus(find(DOSE_NAME_FIELD));
	}
	
	public void addDose(){
		removeAll(find(DOSE_ACTION_PANEL));
		add(find(DOSE_ACTION_PANEL),ui.loadComponentFromFile(EDIT_DOSE_XML, this));
		ui.requestFocus(find(DOSE_NAME_FIELD));
		ui.setText(find(EDIT_DOSE_PANEL), "Add a Dose");
	}
	
	
	public void removeDose(){
		if(scheduledDoseDao.getScheduledDoses(null, (VaccineDose) doseTableController.getCurrentlySelectedObject()).size() != 0){
			ui.alert("You cannot delete a dose that has appointments scheduled.");
		}else{
			ui.showConfirmationDialog("removeDoseConfirmed()", this,"Are you sure you want to delete this dose?");
		}
	}
	
	public void removeDoseConfirmed(){
		getCurrentlySelectedVaccine().removeDose((VaccineDose) doseTableController.getCurrentlySelectedObject());
		vaccineDao.saveOrUpdateVaccine(getCurrentlySelectedVaccine());
		vaccineDoseDao.deleteVaccineDose((VaccineDose) doseTableController.getCurrentlySelectedObject());
		int vaccineIndex = ui.getSelectedIndex(find(VACCINE_LIST));
		refreshVaccines();
		setVaccineListSelectedIndex(vaccineIndex);
		ui.remove(ui.find(CONFIRMATION_DIALOG));
	}
	
	public void saveDose(){
		String name = ui.getText(find(DOSE_NAME_FIELD));
		int startDateMonths = Integer.parseInt(ui.getText(find(START_DATE_MONTHS_BOX)));
		int startDateDays = Integer.parseInt(ui.getText(find(START_DATE_DAYS_BOX)));
		int endDateMonths = Integer.parseInt(ui.getText(find(END_DATE_MONTHS_BOX)));
		int endDateDays = Integer.parseInt(ui.getText(find(END_DATE_DAYS_BOX)));
		int minIntervalMonths = Integer.parseInt(ui.getText(find(MIN_INTERVAL_MONTHS_BOX)));
		int minIntervalDays = Integer.parseInt(ui.getText(find(MIN_INTERVAL_DAYS_BOX)));
		if(currentlyEditingDose == null){
			getCurrentlySelectedVaccine().addDose(new VaccineDose(name,getCurrentlySelectedVaccine(),startDateMonths,startDateDays,endDateMonths,endDateDays,minIntervalMonths,minIntervalDays));
			vaccineDao.saveOrUpdateVaccine(getCurrentlySelectedVaccine());
		}else{
			currentlyEditingDose.setStartDateDays(startDateDays);
			currentlyEditingDose.setStartDateMonths(startDateMonths);
			currentlyEditingDose.setEndDateDays(endDateDays);
			currentlyEditingDose.setEndDateMonths(endDateMonths);
			currentlyEditingDose.setMinIntervalDays(minIntervalDays);
			currentlyEditingDose.setMinIntervalMonths(minIntervalMonths);
			currentlyEditingDose.setName(name);
			vaccineDoseDao.saveOrUpdateVaccineDose(currentlyEditingDose);
		}
		cancelEditingDose();
	}
	
	public void cancelEditingDose(){
		currentlyEditingDose = null;
		vaccineListSelectionChanged();
	}
	
	public void moveDoseUp(){
		
	}
	
	public void moveDoseDown(){
		
	}
	
	
	public void removeVaccine(){
		if(scheduledDoseDao.getScheduledDosesByVaccine(getCurrentlySelectedVaccine()).size() != 0){
			ui.alert("You cannot delete a vaccine with scheduled doses.");
		}else{
			ui.showConfirmationDialog("removeVaccineConfirmed()", this,"Are you sure you want to delete this vaccine?");
		}
	}
	
	public void removeVaccineConfirmed(){
		vaccineDao.deleteVaccine(getCurrentlySelectedVaccine());
		refreshVaccines();
		ui.remove(ui.find(CONFIRMATION_DIALOG));
	}
	
	public void selectionChanged(Object selectedObject) {
		
	}
	
	public void enrollNewbornsChanged(){
		boolean autoEnrollNewborns = ui.isSelected(find(ENROLL_NEWBORNS_CHECKBOX));
		Vaccine v = getCurrentlySelectedVaccine();
		v.setAutomaticallyEnrollNewborns(autoEnrollNewborns);
		vaccineDao.saveOrUpdateVaccine(v);
	}
	
	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}
}