package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.BirthdateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.GenderComboBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.NameField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PersonalFormField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PhoneNumberField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class PersonFieldGroup<P extends Person> extends FieldGroup {

	private P person;
	
	protected boolean isNewPersonGroup;
	
	public PersonFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, P person) {
		super(ui, appCon, delegate);
		this.setPerson(person);
		this.isNewPersonGroup = (person == null);
		initialize();
	}
	
	private void initialize(){
		NameField name = new NameField(ui, isNewPersonGroup ? "" : getPerson().getName(),null);
		GenderComboBox gender = new GenderComboBox(ui,isNewPersonGroup? null : getPerson().getGender(),null);
		BirthdateField bday = new BirthdateField(ui, isNewPersonGroup? new Date() : getPerson().getBirthdate(),null);
		PhoneNumberField phoneNumber = new PhoneNumberField(ui,isNewPersonGroup? "":getPerson().getPhoneNumber(),null, appCon);
		super.addField(name);
		super.addField(gender);
		super.addField(bday);
		super.addField(phoneNumber);
		addAdditionalFields();
	}
	
	public boolean saveIfValid(boolean alert){
		if(validate(alert)){
			if(isNewPersonGroup){
				setPerson(createNewPerson());
			}
			setFields(isNewPersonGroup);
			saveOrUpdatePerson();
			return  true;
		}
		return false;
	}
	
	private void setFields(boolean ifChanged){
		for(ThinletFormField<?> field: getFormFields()){
			if(ifChanged || field.hasChanged()){
				((PersonalFormField) field).setFieldForPerson(getPerson());
			}
		}
	}
	
	protected abstract void addAdditionalFields();
	
	protected abstract void saveOrUpdatePerson();
	
	protected abstract P createNewPerson();

	public void setPerson(P person) {
		this.person = person;
	}

	public P getPerson() {
		return person;
	}
}
