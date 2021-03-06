package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernateCommunityHealthWorkerDao extends BaseHibernateDao<CommunityHealthWorker> implements CommunityHealthWorkerDao {
	
	private PatientDao patientDao;
	public void setPatientDao(PatientDao patientDao){this.patientDao = patientDao;}
	public PatientDao getPatientDao(){return patientDao;}
	
	protected HibernateCommunityHealthWorkerDao() {
		super(CommunityHealthWorker.class);
	}

	public void saveCommunityHealthWorker(CommunityHealthWorker chw) {
		super.saveWithoutDuplicateHandling(chw);
	}

	public void updateCommunityHealthWorker(CommunityHealthWorker chw) {
		super.updateWithoutDuplicateHandling(chw);
	}
	
	public void deleteCommunityHealthWorker(CommunityHealthWorker chw, String reason, CommunityHealthWorker newChw) {
		chw.delete(reason);
		updateCommunityHealthWorker(chw);
		List<Patient> patients = patientDao.getPatientsForCHW(chw, false);
		for(Patient p: patients){
			p.setChw(newChw);
			patientDao.updatePatient(p);
		}
	}

	public List<CommunityHealthWorker> getAllCommunityHealthWorkers() {
		return super.getAll();
	}
	
	public List<CommunityHealthWorker> getAllCommunityHealthWorkers( boolean includeDeleted) {
		DetachedCriteria c = super.getCriterion();
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getList(c);
	}

	public List<CommunityHealthWorker> findCommunityHealthWorkerByName(String nameFragment, int limit, boolean includeDeleted){
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", nameFragment,MatchMode.ANYWHERE));
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		if(limit > 0) return super.getList(c, 0, limit);
		else return super.getList(c);
	}

	public List<CommunityHealthWorker> findCommunityHealthWorkerByName(String nameFragment, boolean includeDeleted){
		return findCommunityHealthWorkerByName(nameFragment,-1, includeDeleted);
	}
	
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(String phoneNumber){
		DetachedCriteria c= super.getCriterion();
		c.createCriteria("contactInfo").add(Restrictions.eq("phoneNumber",phoneNumber));
		return super.getUnique(c);
	}

	public CommunityHealthWorker getCommunityHealthWorkerById(long id) {
		DetachedCriteria c = DetachedCriteria.forClass(CommunityHealthWorker.class);
		c.add(Restrictions.eq("pid", id));
		return super.getUnique(c);
	}
}
