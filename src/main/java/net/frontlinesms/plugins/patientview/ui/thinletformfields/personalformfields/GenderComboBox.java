package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import org.hibernate.classic.ValidationFailure;

import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class GenderComboBox extends ThinletFormField<Gender> implements PersonalFormField{

	protected Object comboBox;
	protected boolean hasChanged = false;
	
	public GenderComboBox(ExtendedThinlet thinlet, Gender gender, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18nString("medic.common.labels.gender")+":", delegate);
		comboBox = thinlet.create("combobox");
		for(Gender g: Gender.values()){
			thinlet.add(comboBox,thinlet.createComboboxChoice(g.toString(), g));
		}
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		thinlet.setEditable(comboBox, false);
		thinlet.add(mainPanel,comboBox);
		thinlet.setWeight(comboBox, 5, 0);
		//initialize the comboBox
		if(gender !=null){
			setRawResponse(gender);
		}else{
			setRawResponse(Gender.FEMALE);
		}
	}

	public void selectionChanged(int index){
		if(index >=0){
			hasChanged = true;
			super.responseChanged();
		}
	}

	@Override
	public void validate() throws ValidationFailure{
		if(!hasResponse()){
			throw new ValidationFailure("\""+ getLabel().replace(":", "")+ "\" does not have a gender selected.");
		}
	}
	
	public boolean hasChanged(){		
		return hasChanged;
	}
	
	@Override
	public Gender getRawResponse() {
		return (Gender) thinlet.getAttachedObject(thinlet.getSelectedItem(comboBox));
	}
	
	@Override
	public void setRawResponse(Gender s) {
		thinlet.setText(comboBox, s.toString());
		for(int i =0; i < Gender.values().length;i++){
			if(Gender.values()[i].equals(s)){
				thinlet.setSelectedIndex(comboBox,i);
			}
		}
	}
	
	@Override
	public String getStringResponse() {
		if(getRawResponse()!=null){
			return getRawResponse().toString();
		}else{
			return null;
		}
	}

	@Override
	public void setStringResponse(String response) {
		if(Gender.getGenderForName(response) != null)
			setRawResponse(Gender.getGenderForName(response));
	}

	public void setFieldForPerson(Person p) {
		p.setGender(getRawResponse());
	}
}
