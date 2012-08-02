package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class VisitDateField extends DateField implements PersonalFormField {

	public VisitDateField(ExtendedThinlet thinlet,
			FormFieldDelegate delegate, boolean showDateFormat, Long visitDate) {
		super(thinlet, InternationalisationUtils.getI18nString("thinletformfields.visitdate")+":", delegate, showDateFormat);
		if(visitDate != null && visitDate > 0L){
			setRawResponse(visitDate);
		}
	}

	public void setFieldForPerson(Person p) {
		Patient pat = (Patient) p;
		pat.setVisitDate(getRawResponse());
	}

}