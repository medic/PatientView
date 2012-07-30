package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class FormFieldGroup extends FieldGroup {
	
	private MedicForm form;
	
	public FormFieldGroup(UiGeneratorController ui, ApplicationContext appCon, MedicForm form, FormFieldDelegate delegate) {
		super(ui, appCon, delegate);
		this.form = form;
		initialize();
	}
	
	private void initialize(){
		if(form == null) return;
		for(MedicFormField mff: form.getFields()){
			// if its a label field, throw in a label
			if(mff.getDatatype() == DataType.TRUNCATED_TEXT || mff.getDatatype() == DataType.WRAPPED_TEXT){
				Object field = ui.createLabel(mff.getLabel());
				ui.add(super.getMainPanel(),field);
				ui.setChoice(field, "halign", "center");
				ui.setInteger(field, "weightx", 1);
			}else{ //otherwise, put in a normal field
				addField(mff);
			}
		}
	}
	
	public void autoFillWithPatient(Patient p){
		if(form == null) return;
		//int containing the number of non-respondable (label) fields we've passed
		int nonRespondable=0;
		for(int i = 0; i < form.getFields().size(); i++){
			MedicFormField mff = form.getFields().get(i);
			if(!mff.getDatatype().isRespondable()){
				nonRespondable++;
				continue;
			}			
			if (mff.getMapping() != null) {
				boolean greyOut = false;
				// check for the different mapping types
				switch (mff.getMapping()) {
				case NAMEFIELD:
					formFields.get(i - nonRespondable).setStringResponse(
							p.getName());
					greyOut = true;
					break;
				case BIRTHDATEFIELD:
					((DateField) formFields.get(i - nonRespondable))
							.setRawResponse(p.getBirthdate());
					greyOut = true;
					break;
				case IDFIELD:
					if(StringUtils.hasText(p.getStringID())){
						formFields.get(i - nonRespondable).setStringResponse(
								String.valueOf(p.getStringID()));
						greyOut = true;
					}
					break;
				case DATE_OF_LAST_AMENORRHEA:
					if (p.getDateOfAmenorrhea() != null) {
						((DateField) formFields.get(i - nonRespondable))
								.setRawResponse(p.getDateOfAmenorrhea());
						greyOut = true;
					}
					break;
				case ADDRESS:
					if (StringUtils.hasText(p.getAddress())) {
						formFields.get(i - nonRespondable).setStringResponse(
								p.getAddress());
						greyOut = true;
					}
					break;
				case MOTHERS_NAME:
					if (StringUtils.hasText(p.getMothersName())) {
						formFields.get(i - nonRespondable).setStringResponse(
								p.getMothersName());
						greyOut = true;
					}
					break;
				case FATHERS_NAME:
					if (StringUtils.hasText(p.getFathersName())) {
						formFields.get(i - nonRespondable).setStringResponse(
								p.getFathersName());
						greyOut = true;
					}
					break;
				case GENDER:
					formFields.get(i - nonRespondable).setStringResponse(
							p.getGender().toString());
					greyOut = true;
					break;
				case PHONE_NUMBER:
					if (StringUtils.hasText(p.getPhoneNumber())) {
						formFields.get(i - nonRespondable).setStringResponse(
								p.getPhoneNumber());
						greyOut = true;
					}
					break;
				case VISIT_DATE:
					if (p.getVisitDate() != null) {
						((DateField) formFields.get(i - nonRespondable))
								.setRawResponse(p.getVisitDate());
						greyOut = true;
					}
					break;
				default:
					break;
				}

				// if it was a mapped field, disable it after autofilling it
				if (greyOut) {
					formFields.get(i - nonRespondable).setEnabled(false);
				}
			}
		}
	}
	
}
