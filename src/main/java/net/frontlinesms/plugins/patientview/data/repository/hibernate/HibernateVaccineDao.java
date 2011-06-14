package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernateVaccineDao extends BaseHibernateDao<Vaccine> implements VaccineDao {

	private ScheduledDoseDao scheduledDoseDao;
	public void setScheduledDoseDao(ScheduledDoseDao scheduledDoseDao) {this.scheduledDoseDao = scheduledDoseDao;}
	public ScheduledDoseDao getScheduledDoseDao() {return scheduledDoseDao;}
	
	protected HibernateVaccineDao() {
		super(Vaccine.class);
	}

	public void deleteVaccine(Vaccine vaccine) {
		super.delete(vaccine);
	}

	public List<Vaccine> findVaccineByName(String nameFragment) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.ilike("name", nameFragment,MatchMode.ANYWHERE));
		return super.getList(c);
	}

	public List<Vaccine> getAllVaccines() {
		return super.getAll();
	}

	public Vaccine getVaccine(long vaccineId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("vaccineId",vaccineId));
		return super.getUnique(c);
	}

	public Vaccine getVaccineByName(String name) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("name",name));
		return super.getUnique(c);
	}

	public void saveOrUpdateVaccine(Vaccine vaccine) {
		super.getHibernateTemplate().saveOrUpdate(vaccine);
	}
	

	public Set<Vaccine> getScheduledVaccinesForPatient(Patient patient) {
		List<ScheduledDose> doses = scheduledDoseDao.getScheduledDoses(patient,null);
		Set<Vaccine> vaccineSet = new HashSet<Vaccine>();
		for(ScheduledDose dose: doses){
			vaccineSet.add(dose.getDose().getVaccine());
		}
		return vaccineSet;
	}

	public List<Vaccine> getUnscheduledVaccinesForPatient(Patient patient) {
		Set<Vaccine> scheduledVaccines = getScheduledVaccinesForPatient(patient);
		List<Vaccine> allVaccines = getAll();
		List<Vaccine> toRemove = new ArrayList<Vaccine>();
		for(Vaccine schedV: scheduledVaccines){
			for(Vaccine vacc: allVaccines){
				if(schedV.getVaccineId() == vacc.getVaccineId()){
					toRemove.add(vacc);
				}
			}
		}
		allVaccines.removeAll(toRemove);
		return allVaccines;
	}
}