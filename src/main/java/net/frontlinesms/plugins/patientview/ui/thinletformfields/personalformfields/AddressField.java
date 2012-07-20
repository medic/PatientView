package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;

public class AddressField extends TextField implements PersonalFormField {

	public AddressField(ExtendedThinlet thinlet, FormFieldDelegate delegate, String address) {
		super(thinlet, "Address: ", delegate);
		if(address != null) setRawResponse(address);
	}

	public void setFieldForPerson(Person p) {
		Patient pat = (Patient) p;
		pat.setAddress(getRawResponse());
	}

}
