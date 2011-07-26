package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateScheduledDoseDao extends BaseHibernateDao<ScheduledDose> implements ScheduledDoseDao {

	protected HibernateScheduledDoseDao(){
		super(ScheduledDose.class);
	}
	
	public void administerDose(ScheduledDose dose, Person administeredBy, Date dateAdministered,String placeAdministered) {
		dose.administer(administeredBy,dateAdministered, placeAdministered);
		saveOrUpdateScheduledDose(dose);
	}

	public void deleteScheduledDose(ScheduledDose dose) {
		super.delete(dose);
	}
	
	public void deleteScheduledDosesForVaccine(Vaccine vaccine, Patient patient) {
		List<ScheduledDose> toDelete = getScheduledDoses(vaccine, patient);
		for(ScheduledDose sd: toDelete){
			delete(sd);
		}
	}

	public List<ScheduledDose> getAllScheduledDoses() {
		return super.getAll();
	}

	public ScheduledDose getScheduledDose(long scheduledDoseId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("scheduledDoseId",scheduledDoseId));
		return super.getUnique(c);
	}

	public List<ScheduledDose> getScheduledDoses(Patient patient, VaccineDose dose) {
		DetachedCriteria c = super.getCriterion();
		if(patient != null) c.add(Restrictions.eq("patient",patient));
		if(dose != null) c.add(Restrictions.eq("dose",dose));
		return super.getList(c);
	}
	
	public List<ScheduledDose> getScheduledDoses(Vaccine vaccine, Patient patient) {
		DetachedCriteria c = super.getCriterion();
		c.createCriteria("dose").add(Restrictions.eq("vaccine",vaccine));
		if(patient != null){
			c.add(Restrictions.eq("patient",patient));
		}
		List<ScheduledDose> doses = super.getList(c);
		Collections.sort(doses);
		return doses;
	}
	

	public void saveOrUpdateScheduledDose(ScheduledDose dose) {
		super.getHibernateTemplate().saveOrUpdate(dose);
	}

	public void saveScheduledDoses(List<ScheduledDose> doses) {
		for(ScheduledDose dose:doses){
			saveOrUpdateScheduledDose(dose);
		}
	}

	public boolean patientHasAdministeredDosesForVaccine(Patient patient, Vaccine vaccine) {
		List<ScheduledDose> doses = getScheduledDoses(vaccine, patient);
		for(ScheduledDose dose:doses){
			if(dose.isAdministered()){
				return true;
			}
		}
		return false;
	}
}