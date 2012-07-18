package net.frontlinesms.plugins.patientview.data.domain.reminder;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;
public enum EventTimingOption{

	BEFORE("medic.reminder.timing.before", -1),
	AFTER("medic.reminder.timing.after", 1),
	DAY_OF("medic.reminder.timing.dayof", 0);
	
	public final String name;
	
	public final int multiplier;
	
	private EventTimingOption(String name, int multiplier){
		this.name = getI18nString(name);
		this.multiplier = multiplier;
	}
}