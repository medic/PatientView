package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	
	/**
	 * The vaccine dose that is currently being edited, if any 
	 */
	private VaccineDose currentlyEditingDose;
	
	//Thinlet XML files
	private static final String MAIN_THINLET_XML = "/ui/plugins/patientview/administration/vaccines/vaccineAdministrationPanel.xml";
	private static final String ADD_VACCINE_XML = "/ui/plugins/patientview/administration/vaccines/addVaccinePanel.xml";
	private static final String VACCINE_BUTTONS_XML = "/ui/plugins/patientview/administration/vaccines/vaccineButtonsPanel.xml";
	private static final String EDIT_DOSE_XML = "/ui/plugins/patientview/administration/vaccines/editDosePanel.xml";
	private static final String DEFAULT_DOSE_PANEL_XML = "/ui/plugins/patientview/administration/vaccines/defaultDoseActionPanel.xml";
	
	//DAOS
	private VaccineDao vaccineDao;
	private VaccineDoseDao vaccineDoseDao;
	private ScheduledDoseDao scheduledDoseDao;
	
	//Thinlet UI element names
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
	//Thinlet button names
	private static final String REMOVE_VACCINE_BUTTON = "removeVaccineButton";
	private static final String ADD_DOSE_BUTTON = "addDoseButton";
	private static final String DELETE_DOSE_BUTTON = "deleteDoseButton";
	private static final String EDIT_DOSE_BUTTON = "editDoseButton";
	
	public VaccineAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon, MAIN_THINLET_XML);
		//init the daos
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.vaccineDoseDao = (VaccineDoseDao) appCon.getBean("VaccineDoseDao");
		this.scheduledDoseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		//create the dose table
		doseTableController = new AdvancedTableController(this, uiController);
		doseTableController.setNoResultsMessage("No doses");
		List<HeaderColumn> columnList = new ArrayList<HeaderColumn>();
		columnList.add(new HeaderColumn("getName", "/icons/syringe_small.png", "Dose Name"));
		columnList.add(new HeaderColumn("getStringStartDate", "/icons/date_add.png", "Start Date"));
		columnList.add(new HeaderColumn("getStringEndDate", "/icons/date_delete.png", "End Date"));
		columnList.add(new HeaderColumn("getStringMinimumInterval", "/icons/time.png", "Minimum Interval To Next Dose"));
		doseTableController.putHeader(VaccineDose.class, columnList);
		//add the dose table to the view
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

	/**
	 * Refreshes the list of vaccines and enables/disables
	 * the related buttons as needed.
	 */
	private void refreshVaccines(){
		//save the current selection index
		int selectedIndex =ui.getSelectedIndex(find(VACCINE_LIST));
		if(selectedIndex < 0) selectedIndex = 0;
		//refresh the view
		removeAll(find(VACCINE_LIST));
		List<Vaccine> vaccines = vaccineDao.getAllVaccines();
		if(vaccines.size() == 0){
			add(find(VACCINE_LIST),ui.createListItem("No Vaccines",null));
		}else{
			for(Vaccine v: vaccines){
				add(find(VACCINE_LIST),ui.createListItem(v.getName(), v));
			}
		}
		//if the old selected index is still within the table size, use it
		if(selectedIndex < vaccines.size()){
			setVaccineListSelectedIndex(selectedIndex);
		}else{// otherwise, set it to the last element in the table
			setVaccineListSelectedIndex(vaccines.size()-1);
		}
		//if there are no vaccines, disable some buttons
		if(vaccines.size() == 0){
			ui.setEnabled(find(REMOVE_VACCINE_BUTTON), false);
		}else{//otherwise, enable these buttons
			ui.setEnabled(find(REMOVE_VACCINE_BUTTON), true);
			ui.setEnabled(find(ADD_DOSE_BUTTON), true);
			ui.setEnabled(find(ENROLL_NEWBORNS_CHECKBOX), true);
		}
	}
	
	private void setVaccineListSelectedIndex(int index){
		ui.setSelectedIndex(find(VACCINE_LIST), index);
		vaccineListSelectionChanged();
	}
	
	/**
	 * Called when the selection in the vaccine list changes.
	 * This method handles the updating of the dose table, the enabling
	 * and the disabling of the appropriate buttons. 
	 */
	public void vaccineListSelectionChanged(){
		//add the standard dose panel
		removeAll(find(DOSE_ACTION_PANEL));
		//get the selected vaccine
		Vaccine v = getCurrentlySelectedVaccine();
		//if the vaccine is null, disable some buttons and return
		if(v == null) return;
		add(find(DOSE_ACTION_PANEL),ui.loadComponentFromFile(DEFAULT_DOSE_PANEL_XML, this));
		//set up the dose panel
		ui.setText(find(VACCINE_NAME_LABEL), v.getName());
		ui.setSelected(find(ENROLL_NEWBORNS_CHECKBOX), v.isAutomaticallyEnrollNewborns());
		//add the doses to the table
		int selectedIndex = doseTableController.getSelectedIndex();
		List<VaccineDose> doses = vaccineDoseDao.getDosesForVaccine(v);
		doseTableController.setResults(doses);
		//enable/disable buttons
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
	
	/**
	 * @return the currently selected vaccine in the vaccine list
	 */
	private Vaccine getCurrentlySelectedVaccine(){
		return (Vaccine) ui.getAttachedObject(ui.getSelectedItem(find(VACCINE_LIST)));
	}
	
	//Button event handler methods

	/**
	 * Called when the "Add Vaccine" button is clicked 
	 */
	public void addVaccine(){
		removeAll(find(VACCINE_BUTTON_PANEL));
		add(find(VACCINE_BUTTON_PANEL),ui.loadComponentFromFile(ADD_VACCINE_XML,this));
		ui.requestFocus(find(VACCINE_NAME_FIELD));
	}
	
	/**
	 * Called after a name has been given and the add vaccine button
	 * has been clicked for a second time 
	 */
	public void addVaccineConfirmed(){
		String name = ui.getText(find(VACCINE_NAME_FIELD));
		if(name == null || name.trim().equals("")){
			ui.setText(find(VACCINE_NAME_FIELD), "");
			ui.alert("You cannot create a vaccine without a name");
			return;
		}
		Vaccine v = new Vaccine(ui.getText(find(VACCINE_NAME_FIELD)),true);
		vaccineDao.saveOrUpdateVaccine(v);
		removeAll(find(VACCINE_BUTTON_PANEL));
		add(find(VACCINE_BUTTON_PANEL),ui.loadComponentFromFile(VACCINE_BUTTONS_XML,this));
		refreshVaccines();
	}
	
	/**
	 * Called if the add vaccine process is cancelled
	 * partway through (during naming). 
	 */
	public void addVaccineCanceled(){
		removeAll(find(VACCINE_BUTTON_PANEL));
		add(find(VACCINE_BUTTON_PANEL),ui.loadComponentFromFile(VACCINE_BUTTONS_XML,this));
	}
	

	/**
	 * Called when the "Remove Vaccine" button is clicked
	 */
	public void removeVaccine(){
		if(scheduledDoseDao.getScheduledDoses(getCurrentlySelectedVaccine(),null).size() != 0){
			ui.alert("You cannot delete a vaccine with scheduled doses.");
		}else{
			ui.showConfirmationDialog("removeVaccineConfirmed()", this,"medic.vaccine.confirm.delete");
		}
	}
	
	/**
	 * Called when the user confirms that they
	 * wish to remove the vaccine in question
	 */
	public void removeVaccineConfirmed(){
		//deleted the vaccine
		vaccineDao.deleteVaccine(getCurrentlySelectedVaccine());
		//refresh the list
		refreshVaccines();
		//remove the confirmation dialog
		ui.remove(ui.find(CONFIRMATION_DIALOG));
	}
	
	/**
	 * Called when the "Add Dose" button is clicked"
	 */
	public void addDose(){
		removeAll(find(DOSE_ACTION_PANEL));
		add(find(DOSE_ACTION_PANEL),ui.loadComponentFromFile(EDIT_DOSE_XML, this));
		ui.requestFocus(find(DOSE_NAME_FIELD));
		ui.setText(find(EDIT_DOSE_PANEL), "Add a Dose");
	}

	/**
	 * Called when the "Edit Dose" button is clicked
	 */
	public void editDose(){
		VaccineDose dose = (VaccineDose) doseTableController.getCurrentlySelectedObject();
		currentlyEditingDose = dose;
		//create the panel
		removeAll(find(DOSE_ACTION_PANEL));
		add(find(DOSE_ACTION_PANEL),ui.loadComponentFromFile(EDIT_DOSE_XML, this));
		//populate the panel
		ui.setText(find(START_DATE_MONTHS_BOX),String.valueOf(dose.getStartDateMonths()));
		ui.setText(find(START_DATE_DAYS_BOX),String.valueOf(dose.getStartDateDays()));
		ui.setText(find(END_DATE_MONTHS_BOX),String.valueOf(dose.getEndDateMonths()));
		ui.setText(find(END_DATE_DAYS_BOX),String.valueOf(dose.getEndDateDays()));
		ui.setText(find(MIN_INTERVAL_MONTHS_BOX),String.valueOf(dose.getMinIntervalMonths()));
		ui.setText(find(MIN_INTERVAL_DAYS_BOX),String.valueOf(dose.getMinIntervalDays()));
		ui.setText(find(DOSE_NAME_FIELD),dose.getName());
		ui.setText(find(EDIT_DOSE_PANEL), "Editing "+ dose.getName());
		//focus on the first field
		ui.requestFocus(find(DOSE_NAME_FIELD));
	}
	
	/**
	 * Called when editing is cancelled
	 */
	public void cancelEditingDose(){
		currentlyEditingDose = null;
		refreshVaccines();
	}
	
	/**
	 * Called when the "Remove Dose" button is clicked.
	 * This method checks to see if the Dose can be removed
	 * by seeing if there are any SchedledDoses attached to it,
	 * and then asking the user whether they really want to delete
	 * the dose.
	 */
	public void removeDose(){
		if(scheduledDoseDao.getScheduledDoses(null, (VaccineDose) doseTableController.getCurrentlySelectedObject()).size() != 0){
			ui.alert("You cannot delete a dose that has appointments scheduled.");
		}else{
			ui.showConfirmationDialog("removeDoseConfirmed()", this,"medic.vaccine.dose.confirm.delete");
		}
	}
	
	/**
	 * Called when the user confirms that they
	 * would like to delete a VaccineDose.
	 */
	public void removeDoseConfirmed(){
		getCurrentlySelectedVaccine().removeDose((VaccineDose) doseTableController.getCurrentlySelectedObject());
		vaccineDao.saveOrUpdateVaccine(getCurrentlySelectedVaccine());
		vaccineDoseDao.deleteVaccineDose((VaccineDose) doseTableController.getCurrentlySelectedObject());
		int vaccineIndex = ui.getSelectedIndex(find(VACCINE_LIST));
		refreshVaccines();
		setVaccineListSelectedIndex(vaccineIndex);
		ui.remove(ui.find(CONFIRMATION_DIALOG));
	}
	
	/**
	 * Called when the addDose or saveDose processes are done
	 */
	public void saveDose(){
		//prepare all the data
		String name = ui.getText(find(DOSE_NAME_FIELD));
		if(name == null || name.trim().equals("")){
			ui.alert("You can't create a dose without a name.");
			return;
		}
		int startDateMonths = Integer.parseInt(ui.getText(find(START_DATE_MONTHS_BOX)));
		int startDateDays = Integer.parseInt(ui.getText(find(START_DATE_DAYS_BOX)));
		int endDateMonths = Integer.parseInt(ui.getText(find(END_DATE_MONTHS_BOX)));
		int endDateDays = Integer.parseInt(ui.getText(find(END_DATE_DAYS_BOX)));
		int minIntervalMonths = Integer.parseInt(ui.getText(find(MIN_INTERVAL_MONTHS_BOX)));
		int minIntervalDays = Integer.parseInt(ui.getText(find(MIN_INTERVAL_DAYS_BOX)));
		//if we're not currently editing a dose, create a new one
		if(currentlyEditingDose == null){
			getCurrentlySelectedVaccine().addDose(new VaccineDose(name,getCurrentlySelectedVaccine(),startDateMonths,startDateDays,endDateMonths,endDateDays,minIntervalMonths,minIntervalDays));
			vaccineDao.saveOrUpdateVaccine(getCurrentlySelectedVaccine());
		}else{ // otherwise, modify the existing dose
			currentlyEditingDose.setStartDateDays(startDateDays);
			currentlyEditingDose.setStartDateMonths(startDateMonths);
			currentlyEditingDose.setEndDateDays(endDateDays);
			currentlyEditingDose.setEndDateMonths(endDateMonths);
			currentlyEditingDose.setMinIntervalDays(minIntervalDays);
			currentlyEditingDose.setMinIntervalMonths(minIntervalMonths);
			currentlyEditingDose.setName(name);
	//		vaccineDao.saveOrUpdateVaccine(currentlyEditingDose.getVaccine());
			vaccineDoseDao.saveOrUpdateVaccineDose(currentlyEditingDose);
		}
		//reset the screen
		cancelEditingDose();
	}
	
	/**
	 * Called when the value of the 'enroll newborns' checkbox
	 * changes. This method saves the value to the database.
	 */
	public void enrollNewbornsChanged(){
		boolean autoEnrollNewborns = ui.isSelected(find(ENROLL_NEWBORNS_CHECKBOX));
		Vaccine v = getCurrentlySelectedVaccine();
		v.setAutomaticallyEnrollNewborns(autoEnrollNewborns);
		vaccineDao.saveOrUpdateVaccine(v);
	}
	
	// Unused AdvancedTableDeletegate methods
	public void selectionChanged(Object selectedObject) {}
	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}
}