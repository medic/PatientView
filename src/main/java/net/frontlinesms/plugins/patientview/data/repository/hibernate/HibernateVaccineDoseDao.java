package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDoseDao;

public class HibernateVaccineDoseDao extends BaseHibernateDao<VaccineDose>
		implements VaccineDoseDao {

	protected HibernateVaccineDoseDao() {
		super(VaccineDose.class);
	}

	public void deleteVaccineDose(VaccineDose dose) {
		super.delete(dose);
	}

	public List<VaccineDose> findVaccineDosesByName(String nameFragment) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.ilike("name", nameFragment,MatchMode.ANYWHERE));
		return super.getList(c);
	}

	public List<VaccineDose> getAll() {
		return super.getAll();
	}

	public List<VaccineDose> getDosesForVaccine(Vaccine vaccine) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("vaccine", vaccine));
		return super.getList(c);
	}

	public VaccineDose getVaccineDose(long vaccineDoseId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("vaccineDoseId", vaccineDoseId));
		return super.getUnique(c);
	}

	public VaccineDose getVaccineDoseByName(String name) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("name", name));
		return super.getUnique(c);
	}

	public void saveOrUpdateVaccineDose(VaccineDose dose) {
		super.getHibernateTemplate().saveOrUpdate(dose);
	}
}
