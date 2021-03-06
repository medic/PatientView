package net.frontlinesms.plugins.patientview.data.domain.reminder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.reminder.impl.ReminderDate;

public abstract class ReminderEvent<C> {
	
	protected Map<String,String> variables;
	
	public ReminderEvent(){
		variables = new HashMap<String, String>();
		variables.put("Patient Name", "{patient name}");
		variables.put("CHW Name", "{chw name}");
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
		if(key.equals("{patient name}")){
			return patient.getName();
		}else if(key.equals("{chw name}")){
			return patient.getCHWName();
		}else{
			return "";
		}
	}
	
	public abstract String getVariableValue(Patient patient, C context, String key);
}