package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.CHWComboBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.NewbornCheckbox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PatientIdField;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientFieldGroup extends PersonFieldGroup<Patient> implements ThinletUiEventHandler {

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
		if(isNewPersonGroup){
			checkBox = new NewbornCheckbox(ui, "Enroll in vaccines for newborns?", null , appCon);
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
				List<ScheduledDose> scheduledDoses = VaccineScheduler.instance().scheduleVaccinesFromBirth(getPerson(), v);
				doseDao.saveScheduledDoses(scheduledDoses);
			}
		}
		if(!isNewPersonGroup && bday.hasChanged() && vaccineDao.getScheduledVaccinesForPatient(getPerson()).size() >0){
			ui.showConfirmationDialog("rescheduleVaccines()", this, "medic.vaccine.reschedule.confirm");
		}
	}
	
	public void rescheduleVaccines(){
		VaccineScheduler.instance().rescheduleUnadministeredDosesFromBirth(getPerson());
		ui.remove(ui.find("confirmDialog"));
	}

	@Override
	protected Patient createNewPerson() {
		return new Patient();
	}
}