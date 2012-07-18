package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.impl.ReminderDate;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

public abstract class ReminderEvent<C> {
	
	protected Map<String,String> variables;
	
	private static final String PATIENT_NAME_TITLE= "medic.reminder.variables.patient.name";
	private static final String CHW_NAME_TITLE= "medic.reminder.variables.chw.name";
	
	public ReminderEvent(){
		variables = new HashMap<String, String>();
		String patientName = getI18nString(PATIENT_NAME_TITLE);
		String chwName = getI18nString(CHW_NAME_TITLE);

		variables.put(patientName, "{"+patientName.toLowerCase()+"}");
		variables.put(chwName, "{"+chwName.toLowerCase()+"}");
	}
	
	public abstract String getSnippet();

	public abstract List<EventTimingOption> getSupportedTimingOptions();
	
	public abstract boolean compatibileWithEvent(ReminderEvent event);
	
	public abstract boolean canBeStartEvent();
	
	public abstract boolean canBeEndEvent();
	
	public abstract List<ReminderDate<C>> getEventDatesWithContext(Patient patient);
	
	public abstract Calendar getDateForContext(Patient patient, C context);
	
	public Map<String,String> getVariables(){
		return variables;
	}
	
	public String getVariableValue(Patient patient, String key){
		if(key.equals("{"+getI18nString(PATIENT_NAME_TITLE).toLowerCase()+"}")){
			return patient.getName();
		}else if(key.equals("{"+getI18nString(CHW_NAME_TITLE).toLowerCase()+"}")){
			return patient.getCHWName();
		}else{
			return "";
		}
	}
	
	public abstract String getVariableValue(Patient patient, C context, String key);
}