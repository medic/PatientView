package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.CHWComboBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.ConceptionDateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.MotherCheckbox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.NewbornCheckbox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PatientIdField;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class PatientFieldGroup extends PersonFieldGroup<Patient> {

	private PatientDao patientDao;
	private VaccineDao vaccineDao;
	private ScheduledDoseDao doseDao;
	
	private NewbornCheckbox newbornCheckBox;
	private MotherCheckbox motherCheckBox;
	private ConceptionDateField cDateField;
	
	private Long dateOfConception;
	private Boolean isNewborn;
	
	public PatientFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, Patient person) {
		super(ui, appCon, delegate, person);
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.doseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
	}
	
	public PatientFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, String name, Gender gender, long birthdate, long dateOfConception, boolean isNewborn) {
		super(ui, appCon, delegate, name,gender,birthdate);
		this.dateOfConception = dateOfConception;
		System.out.println("DoCIC: " + dateOfConception);
		System.out.println("DoCIC2: " + this.dateOfConception);
		this.isNewborn = isNewborn;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.doseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		addAdditionalFields();
	}

	@Override
	protected void addAdditionalFields() {
		CHWComboBox chwCombo = new CHWComboBox(ui, appCon, getPerson() == null?null:getPerson().getChw(),null);
		super.addField(chwCombo);
		PatientIdField idField = new PatientIdField(getPerson()!=null?getPerson().getExternalId():"", ui, this);
		super.insertField(idField, 1);
		System.out.println("DoC: " + dateOfConception);
		if((dateOfConception != null && dateOfConception > 0L) ||
				(getPerson()!= null && getPerson().getDateOfConception() !=null) || (isNewPersonGroup && dateOfConception == null)){
			cDateField = new ConceptionDateField(ui, this,getPerson()!=null?getPerson().getDateOfConception():dateOfConception);
			super.insertField(cDateField, 4);
		}
		if(isNewPersonGroup && dateOfConception == null){
			newbornCheckBox = new NewbornCheckbox(ui, "Enroll in vaccines for newborns?", null , appCon);
			if(isNewborn != null && isNewborn) newbornCheckBox.setRawResponse(true);
			motherCheckBox = new MotherCheckbox(ui, "Enroll in antenatal care?", null , appCon);
			if(isNewborn != null && !isNewborn) motherCheckBox.setRawResponse(true);
			super.addField(newbornCheckBox);
			super.addField(motherCheckBox);
		}
	}
	
	@Override
	protected void saveOrUpdatePerson() {
		if(isNewPersonGroup){
			patientDao.savePatient(getPerson());
		}else{
			patientDao.updatePatient(getPerson());
		}
		//if they are a newborn, enroll them in the proper vaccines and notify the chw
		if(isNewborn != null){
			if(isNewborn || (newbornCheckBox != null && newbornCheckBox.getRawResponse())){
				System.out.println("NEWBORN DETECTED");
				List<Vaccine> vaccines = vaccineDao.getNewbornVaccines();
				for(Vaccine v: vaccines){
					List<ScheduledDose> scheduledDoses = VaccineScheduler.instance().scheduleVaccinesFromBirth(getPerson(), v);
					doseDao.saveScheduledDoses(scheduledDoses);
				}
				if(getPerson().getChw()!=null && StringUtils.hasText(getPerson().getChw().getPhoneNumber())){
					String message = "Newborn registered: "+ getPerson().getName() + ", DOB: "+ InternationalisationUtils.getDateFormat().print(getPerson().getBirthdate()) + ", ID: "+ getPerson().getStringID();
					ui.getFrontlineController().sendTextMessage(getPerson().getChw().getPhoneNumber(), message);
				}
			// if they're a new mother, enroll them in the proper vaccines and notify the CHW
			}else if(!isNewborn || (motherCheckBox != null && motherCheckBox.getRawResponse() && getPerson().getDateOfConception() != null && getPerson().getDateOfConception() > 0)){
				System.out.println("MOTHER DETECTED");
				List<Vaccine> antenatalVaccines = vaccineDao.getAntenatalVaccines();
				for(Vaccine v: antenatalVaccines){
					List<ScheduledDose> scheduledDoses = VaccineScheduler.instance().scheduleVaccinesFromDateOfConception(getPerson(), v);
					doseDao.saveScheduledDoses(scheduledDoses);
				}
				if(getPerson().getChw()!=null && StringUtils.hasText(getPerson().getChw().getPhoneNumber())){
					String message = "Mother registered: "+ getPerson().getName() + ", ID: "+ getPerson().getStringID();
					ui.getFrontlineController().sendTextMessage(getPerson().getChw().getPhoneNumber(), message);
				}
			}
		}
	}

	@Override
	protected Patient createNewPerson() {
		return new Patient();
	}
}