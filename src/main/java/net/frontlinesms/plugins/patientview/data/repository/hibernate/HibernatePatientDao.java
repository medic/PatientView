package net.frontlinesms.plugins.patientview.data.repository.hibernate;


import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernatePatientDao extends BaseHibernateDao<Patient> implements PatientDao {
	
	protected HibernatePatientDao() {
		super(Patient.class);
	}
	
	public void savePatient(Patient p) {
		super.saveWithoutDuplicateHandling(p);
	}

	public void updatePatient(Patient p) {
		super.updateWithoutDuplicateHandling(p);
	}

	public void deletePatient(Patient p, String reason) {
		p.delete(reason);
		updatePatient(p);
	}

	public List<Patient> getAllPatients() {
		DetachedCriteria c= getBaseCriterion();
		return super.getList(c);
	}
	
	public List<Patient> getPatientsForCHW(CommunityHealthWorker chw, boolean includeDeleted) {
		DetachedCriteria criteria = getBaseCriterion();
		criteria.add(Restrictions.eq("chw", chw));
		if(!includeDeleted){
			criteria.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getList(criteria);
	}

	public List<Patient> findPatientsByName(String nameFragment, int resultsLimit, boolean includeDeleted) {
		DetachedCriteria c= getBaseCriterion();
		c.add(Restrictions.like("name", nameFragment, MatchMode.ANYWHERE));
		if(!includeDeleted){
			c.add(Restrictions.eq("deleted",false));
		}
		if(resultsLimit > 0)
			return super.getList(c, 0, resultsLimit);
		else{
			return super.getList(c);
		}
	}
	
	public List<Patient> findPatientsByName(String nameFragment, boolean includeDeleted){
		return findPatientsByName(nameFragment,-1,includeDeleted);
	}
	
	public Patient getPatientById(Long id){
		DetachedCriteria c = getBaseCriterion();
		c.add(Restrictions.eq("pid", id));
		return super.getUnique(c);
	}
	
	public Patient findPatient(String name, String birthdate, String id, boolean includeDeleted){
		DetachedCriteria c = getBaseCriterion();
		//add the name restriction
		if(name !=null && !name.equals("")){
			c.add(Restrictions.eq("name", name));
		}
		//add the birthdate restriction
		if(birthdate !=null && !birthdate.equals("")){
			long bday = 0L;
			try {
				bday = InternationalisationUtils.getDateFormat().parse(birthdate).getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
			long lower = bday - 86400000;
			long upper = bday + 86400000;
			c.add(Restrictions.and(Restrictions.gt("birthdate", lower), Restrictions.lt("birthdate", upper)));
		}
		//add the id restriction
		if(id != null && !id.equals("")){
			long longId = Long.parseLong(id);
			c.add(Restrictions.eq("id", longId));
		}
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getUnique(c);
	}

	private DetachedCriteria getBaseCriterion(){
		DetachedCriteria c= DetachedCriteria.forClass(Patient.class);
		return c;
	}

	public List<Patient> getAllPatients(boolean includeDeleted) {
		DetachedCriteria criteria = getBaseCriterion();
		if(!includeDeleted){
			criteria.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getList(criteria);
	}
}
