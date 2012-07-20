package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.AddressField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.AmenorrheaDateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.CHWComboBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.NewbornCheckbox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.ParentNameField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PatientIdField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.VisitDateField;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientFieldGroup extends PersonFieldGroup<Patient> {

	private PatientDao patientDao;
	private VaccineDao vaccineDao;
	private ScheduledDoseDao doseDao;
	
	private NewbornCheckbox checkBox;
	
	
	public PatientFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, Patient person) {
		super(ui, appCon, delegate, person);
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.doseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
	}

	@Override
	protected void addAdditionalFields() {
		CHWComboBox chwCombo = new CHWComboBox(ui, appCon, getPerson() == null?null:getPerson().getChw(),null);
		super.addField(chwCombo);
		PatientIdField idField = new PatientIdField(getPerson()!=null?getPerson().getExternalId():"", ui, this);
		super.insertField(idField, 1);
		super.addField(new AddressField(ui, this,getPerson()!=null?getPerson().getAddress():""));
		super.addField(new VisitDateField(ui, this, false, getPerson()!=null?getPerson().getVisitDate():null));
		super.addField(new ParentNameField(ui, this, true, getPerson()!=null?getPerson().getMothersName():""));
		super.addField(new ParentNameField(ui, this, false, getPerson()!=null?getPerson().getFathersName():""));
		if(isNewPersonGroup){
			AmenorrheaDateField df = new AmenorrheaDateField(ui, this, getPerson() == null?null:getPerson().getDateOfAmenorrhea());
			super.addField(df);
			checkBox = new NewbornCheckbox(ui, "Enroll in ANC appointments?", null , appCon);
			super.addField(checkBox);
		}
		
	}
	
	@Override
	protected void saveOrUpdatePerson() {
		if(isNewPersonGroup){
			patientDao.savePatient(getPerson());
		}else{
			patientDao.updatePatient(getPerson());
		}
		if(checkBox != null && checkBox.getRawResponse()){
			List<Vaccine> vaccines = vaccineDao.getNewbornVaccines();
			for(Vaccine v: vaccines){
				List<ScheduledDose> scheduledDoses = VaccineScheduler.instance().scheduleVaccinesFromDateOfAmenorrhea(getPerson(), v);
				doseDao.saveScheduledDoses(scheduledDoses);
			}
		}
	}

	@Override
	protected Patient createNewPerson() {
		return new Patient();
	}
}