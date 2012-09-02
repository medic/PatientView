package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.appointment.Appointment;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.AppointmentDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AppointmentTab extends TabController implements ThinletUiEventHandler, TableActionDelegate {

	private static final String FILE_PREFIX = "/ui/plugins/patientview/dashboard/tabs/appointments/";
	private static final String XML_FILE = FILE_PREFIX + "appointmentTab.xml";
	private static final String BUTTON_PANEL_XML = FILE_PREFIX + "buttonPanel.xml";
	private static final String SCHEDULE_APPT_PANEL_XML = FILE_PREFIX + "scheduleAppointmentPanel.xml";
	private static final String RESCHEDULE_APPT_PANEL_XML = FILE_PREFIX + "rescheduleAppointmentPanel.xml";
	private static final String COMPLETE_APPT_PANEL = FILE_PREFIX + "completeAppointmentPanel.xml";
	
	private static final String APPOINTMENT_TABLE_PANEL = "appointmentTablePanel";
	private static final String BOTTOM_PANEL = "bottomPanel";
	private static final String APPT_DATE_FIELD = "appointmentDateField";
	private static final String REASON_FIELD = "reasonField";
	private static final String CONFIRMATION_DIALOG = "confirmDialog";
	private static final String RESCHEDULE_BUTTON = "rescheduleButton";
	private static final String CANCEL_BUTTON = "cancelButton";
	private static final String COMPLETE_BUTTON = "completeButton";
	private static final String PATIENT_ATTENDED_CHECKBOX = "patientAttendedCheckbox";

	
	private Patient patient;
	
	private AppointmentDao appointmentDao;
	
	private DateField dateField;
	
	TableController appointmentTable;
	
	public AppointmentTab(UiGeneratorController uiController, ApplicationContext appCon, Patient patient) {
		super(uiController, appCon);
		this.patient = patient;
		//initialize the main panel
		setTitle("Appointments");
		setIconPath("/icons/syringe.png");
		this.mainPanel = ui.loadComponentFromFile(XML_FILE,this);
		ui.removeAll(getTab());
		ui.add(getTab(),getMainPanel());
		//initialize the buttons
		Object buttonPanel = ui.loadComponentFromFile(BUTTON_PANEL_XML, this);
		ui.add(ui.find(mainPanel,BOTTOM_PANEL),buttonPanel);
		//initialize the table
		appointmentTable = new TableController(this, ui);
		List<HeaderColumn> appointmentColumns = new ArrayList<HeaderColumn>();
		appointmentColumns.add(new HeaderColumn("getReason", "/icons/syringe_small.png", "Reason"));
		appointmentColumns.add(new HeaderColumn("getScheduledDateString", "/icons/date_add.png", "Date Scheduled"));
		appointmentColumns.add(new HeaderColumn("getLocation", "/icons/tick.png", "Location"));
		appointmentColumns.add(new HeaderColumn("isAttendedString", "/icons/date_delete.png", "Attended by Patient"));
		appointmentTable.putHeader(Appointment.class, appointmentColumns);
		appointmentTable.setNoResultsMessage("No Scheduled Appointments");
		appointmentTable.setResults(new ArrayList<ScheduledDose>());
		ui.add(ui.find(mainPanel,APPOINTMENT_TABLE_PANEL),appointmentTable.getMainPanel());
		//initialize the dao
		appointmentDao = (AppointmentDao) appCon.getBean("AppointmentDao");
		refreshAppointmentTable();
	}
	
	private void refreshAppointmentTable(){
		appointmentTable.setResults(appointmentDao.getAppointmentsForPatient(patient));
		if(appointmentTable.getResultsSize() == 0){
			ui.setEnabled(ui.find(mainPanel, RESCHEDULE_BUTTON), false);
			ui.setEnabled(ui.find(mainPanel, CANCEL_BUTTON), false);
			ui.setEnabled(ui.find(mainPanel, COMPLETE_BUTTON), false);
		}else{
			ui.setEnabled(ui.find(mainPanel, RESCHEDULE_BUTTON), true);
			ui.setEnabled(ui.find(mainPanel, CANCEL_BUTTON), true);
			ui.setEnabled(ui.find(mainPanel, COMPLETE_BUTTON), true);
		}
	}
	
	public void resetBottomPanel() {
		ui.removeAll(ui.find(BOTTOM_PANEL));
		Object buttonPanel = ui.loadComponentFromFile(BUTTON_PANEL_XML, this);
		ui.add(ui.find(mainPanel,BOTTOM_PANEL),buttonPanel);
	}
	
	public void scheduleClicked(){
		ui.removeAll(ui.find(BOTTOM_PANEL));
		Object schedulePanel= ui.loadComponentFromFile(SCHEDULE_APPT_PANEL_XML,this);
		dateField = new DateField(ui, "Appointment Date", null, false);
		ui.add(ui.find(schedulePanel,APPT_DATE_FIELD),dateField.getThinletPanel());
		ui.add(ui.find(BOTTOM_PANEL),schedulePanel);
		ui.setFocus(ui.find(mainPanel,REASON_FIELD));
	}
	
	public void scheduleAppointmentConfirmed(){
		if(dateField.getRawResponse() == null){
			ui.alert("You must enter a valid date.");
			return;
		}else if(ui.getText(ui.find(mainPanel,REASON_FIELD)).equalsIgnoreCase("")){
			ui.alert("You must enter an appointment reason.");
			return;
		}
		Appointment appt = new Appointment();
		appt.setAttended(false);
		appt.setDateScheduled(dateField.getRawResponse());
		appt.setPatient(patient);
		appt.setReason(ui.getText(ui.find(mainPanel,REASON_FIELD)));
		appointmentDao.saveAppointment(appt);
		resetBottomPanel();
		refreshAppointmentTable();
	}
	
	public void rescheduleClicked(){
		ui.removeAll(ui.find(BOTTOM_PANEL));
		Object schedulePanel= ui.loadComponentFromFile(RESCHEDULE_APPT_PANEL_XML,this);
		dateField = new DateField(ui, "New Appointment Date", null, false);
		dateField.setRawResponse(getSelectedAppointment().getDateScheduled());
		ui.add(ui.find(schedulePanel,APPT_DATE_FIELD),dateField.getThinletPanel());
		ui.add(ui.find(BOTTOM_PANEL),schedulePanel);
		ui.setFocus(dateField.getThinletPanel());
	}
	
	public void rescheduleAppointmentConfirmed(){
		if(dateField.getRawResponse() == null){
			ui.alert("You must enter a valid date.");
			return;
		}
		Appointment appt = getSelectedAppointment();
		appt.setDateScheduled(dateField.getRawResponse());
		appointmentDao.updateAppointment(appt);
		resetBottomPanel();
		refreshAppointmentTable();
	}
	
	public void completeClicked(){
		ui.removeAll(ui.find(BOTTOM_PANEL));
		Object completePanel= ui.loadComponentFromFile(COMPLETE_APPT_PANEL,this);
		ui.add(ui.find(BOTTOM_PANEL),completePanel);
	}
	
	public void cancelClicked(){
		if(getSelectedAppointment() == null) return;
		
		if(getSelectedAppointment().isAttended()){
			ui.alert("You can't cancel appointments that have already happened");
		}else{
			ui.showConfirmationDialog("cancelConfirmed()", this,"stuff");
		}
	}
	
	public void finishedAppointment(){
		Appointment appt = getSelectedAppointment();
		appt.setAttended(ui.isSelected(ui.find(mainPanel,PATIENT_ATTENDED_CHECKBOX)));
		appt.setHandledBy(UserSessionManager.getUserSessionManager().getCurrentUser());
		appointmentDao.updateAppointment(appt);
		resetBottomPanel();
		refreshAppointmentTable();
	}
	
	public void cancelConfirmed(){
		ui.remove(ui.find(CONFIRMATION_DIALOG));
		appointmentDao.deleteAppointment(getSelectedAppointment());
		refreshAppointmentTable();
	}

	private Appointment getSelectedAppointment(){
		return (Appointment) appointmentTable.getCurrentlySelectedObject();
	}

	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}
	public void selectionChanged(Object selectedObject) {}
	
}
