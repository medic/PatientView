package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class AmenorrheaDateField extends DateField implements PersonalFormField {

	public AmenorrheaDateField(ExtendedThinlet thinlet, FormFieldDelegate delegate, Long dateOfConception) {
		super(thinlet, InternationalisationUtils.getI18nString("thinletformfields.menstrualperiod")+":", delegate);
		if(dateOfConception != null){
			this.setRawResponse(dateOfConception);
		}
	}

	public void setFieldForPerson(Person p) {
		if(p instanceof Patient && getRawResponse() != null){
			((Patient) p).setDateOfAmenorrhea(getRawResponse());
		}
	}
}