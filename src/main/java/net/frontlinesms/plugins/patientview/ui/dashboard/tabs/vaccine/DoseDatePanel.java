package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.vaccine;

import java.util.Calendar;

import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class DoseDatePanel extends ViewHandler implements FormFieldDelegate{

	private static final String THINLET_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/reschedule_dialog/doseDatePanel.xml";
	
	ScheduledDose dose;
	DateField startDate;
	//DateField endDate;
	
	public DoseDatePanel(UiGeneratorController ui,ApplicationContext appCon, ScheduledDose dose) {
		super(ui,appCon,THINLET_XML);
		this.dose = dose;
		init();
	}
	
	private void init(){
		ui.setText(find("doseNameLabel"),dose.getDoseName());
		startDate = new DateField(ui, "", this,false);
		startDate.setRawResponse(dose.getWindowStartDate());
	//	endDate = new DateField(ui, "", this,false);
	//	endDate.setRawResponse(dose.getWindowEndDate());
		if(dose.isAdministered()){
			ui.setEnabledRecursively(startDate.getThinletPanel(), false);
		//	ui.setEnabledRecursively(endDate.getThinletPanel(), false);
		}
		ui.add(find("startDatePanel"),startDate.getThinletPanel());
	//	ui.add(find("endDatePanel"),endDate.getThinletPanel());
	}
	
	public void save() throws Exception{
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(startDate.getRawResponse());
//		Calendar end = Calendar.getInstance();
	//	end.setTimeInMillis(endDate.getRawResponse());
		//if(TimeUtils.compareCalendars(start, end) > -1){
		//	throw new Exception("The start date of " + dose.getDoseName()+ " is later than the end date.");
	//	}
		dose.setWindowStartDate(start.getTimeInMillis());
	//	dose.setWindowEndDate(end.getTimeInMillis());
		((ScheduledDoseDao) appCon.getBean("ScheduledDoseDao")).saveOrUpdateScheduledDose(dose);
	}

	public void formFieldChanged(ThinletFormField changedField, String newValue) {/* Do Nothing*/}
}