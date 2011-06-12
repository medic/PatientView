package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateScheduledDoseDao extends BaseHibernateDao<ScheduledDose> implements ScheduledDoseDao {

	protected HibernateScheduledDoseDao(){
		super(ScheduledDose.class);
	}

	public void administerDose(ScheduledDose dose, Person administeredBy) {
		dose.administer(administeredBy);
		saveOrUpdateScheduledDose(dose);
	}

	public void deleteScheduledDose(ScheduledDose dose) {
		super.delete(dose);
	}

	public List<ScheduledDose> getAllScheduledDoses() {
		return super.getAll();
	}

	public ScheduledDose getSchedledDose(long scheduledDoseId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("scheduledDoseId",scheduledDoseId));
		return super.getUnique(c);
	}

	public List<ScheduledDose> getScheduledDose(Patient patient, VaccineDose dose) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("patient",patient));
		c.add(Restrictions.eq("dose",dose));
		return super.getList(c);
	}

	public void saveOrUpdateScheduledDose(ScheduledDose dose) {
		super.getHibernateTemplate().saveOrUpdate(dose);
	}

}
