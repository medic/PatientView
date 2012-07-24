package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.flags;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.TriggeredFlagDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class ScheduleAppointmentDialog extends ViewHandler implements FormFieldDelegate{

	public ScheduleAppointmentDialog(UiGeneratorController ui,
			ApplicationContext appCon, Patient patient, TriggeredFlag flag, FlagTab parent) {
		super(ui, appCon,UI_XML);
		this.pat = patient;
		this.flag = flag;
		this.parentController = parent;
		flagDao = (TriggeredFlagDao) appCon.getBean("TriggeredFlagDao");
		appointmentDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		Object datePanel = ui.find(mainPanel,"datePanel");
		dateField = new DateField(ui, "Appointment Date:", this,false);
		dateField.setRawResponse(new Date().getTime());
		ui.add(datePanel,dateField.getThinletPanel());		
		ui.add(mainPanel);
		ui.setVisible(mainPanel,true);
	}
	
	public void schedule(){
		ScheduledDose appt = new ScheduledDose(null, pat, dateField.getRawResponse());
		appointmentDao.saveOrUpdateScheduledDose(appt);
		flag.setAppointment(appt);
		flagDao.updateTriggeredFlag(flag);
		ui.remove(mainPanel);
		parentController.appointmentScheduled(true);
	}
	
	public void cancel(){
		ui.remove(mainPanel);
		parentController.appointmentScheduled(false);
	}
	
	private final static String UI_XML = "/ui/plugins/patientview/dashboard/tabs/flags/scheduleAppointmentDialog.xml";
	
	private TriggeredFlagDao flagDao;
	private ScheduledDoseDao appointmentDao;
	private DateField dateField;
	
	private Patient pat;
	private TriggeredFlag flag;
	
	private FlagTab parentController;
	public void formFieldChanged(ThinletFormField changedField, String newValue) {}
}
