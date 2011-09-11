package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.CheckBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.ExtendedThinlet;

public class MotherCheckbox extends CheckBox implements PersonalFormField {

	public MotherCheckbox(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate,ApplicationContext appCon) {
		super(thinlet, label, delegate);
	}

	public void setFieldForPerson(Person p) {
		//do nothing
	}

}
