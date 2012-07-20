package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import org.springframework.util.StringUtils;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;

public class ParentNameField extends TextField implements PersonalFormField {
	
	private boolean mother;
	
	public ParentNameField(ExtendedThinlet thinlet,
			FormFieldDelegate delegate, boolean mother, String name) {
		super(thinlet, mother?"Mother's Name:":"Father's Name:", delegate);
		this.mother = mother;
		if(StringUtils.hasText(name)){
			setRawResponse(name);
		}
	}
	
	public void setFieldForPerson(Person p) {
		Patient pat = (Patient) p;
		if(mother){
			pat.setMothersName(getRawResponse());
		}else{
			pat.setFathersName(getRawResponse());
		}
	}

}
