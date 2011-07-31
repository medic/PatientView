package net.frontlinesms.plugins.patientview.data.repository.hibernate;

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
	String hqlQuery = "select distinct v from Vaccine v order by v.name asc";
	
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
		return super.getList(hqlQuery, null);
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
		Set<Vaccine> toRemove = new HashSet<Vaccine>();
		for(Vaccine schedV: scheduledVaccines){
			for(Vaccine vacc: allVaccines){
				//if the vaccine has already been scheduled or the vaccine has no doses, remove it
				if(schedV.getVaccineId() == vacc.getVaccineId()){
					toRemove.add(vacc);
				}
			}
		}
		for(Vaccine vaccine: allVaccines){
			if(vaccine.getDoses().size() == 0){
				toRemove.add(vaccine);
			}
		}
		allVaccines.removeAll(toRemove);
		return allVaccines;
	}
	public List<Vaccine> getNewbornVaccines() {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("automaticallyEnrollNewborns", true));
		return super.getList(c);
	}
}