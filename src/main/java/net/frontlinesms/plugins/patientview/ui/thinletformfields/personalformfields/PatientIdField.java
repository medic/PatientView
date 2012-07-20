package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class PatientIdField extends TextField implements PersonalFormField{

	public PatientIdField(String currentId, ExtendedThinlet thinlet, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18nString("medic.field.mapping.id")+":", delegate);
		setRawResponse(currentId);
	}

	public void setFieldForPerson(Person p) {
		Patient patient;
		try{
			patient = (Patient) p;
		}catch(Exception e){
			return;
		}
		patient.setExternalId(getRawResponse());
	}
}