package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;

public class HibernateVaccineDao extends BaseHibernateDao<Vaccine> implements VaccineDao {

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
}
