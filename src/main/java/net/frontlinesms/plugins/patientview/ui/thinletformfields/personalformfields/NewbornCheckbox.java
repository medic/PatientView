package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.CheckBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

public class NewbornCheckbox extends CheckBox implements PersonalFormField {

	private VaccineDao vaccineDao;
	private ScheduledDoseDao doseDao;
	
	public NewbornCheckbox(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate,ApplicationContext appCon) {
		super(thinlet, label, delegate);
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.doseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
	}

	public void setFieldForPerson(Person p) {
		//do nothing
	}
}
