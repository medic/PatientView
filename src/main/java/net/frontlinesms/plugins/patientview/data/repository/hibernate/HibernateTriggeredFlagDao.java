package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.TriggeredFlagDao;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

public class HibernateTriggeredFlagDao extends BaseHibernateDao<TriggeredFlag> implements
		TriggeredFlagDao {

	protected HibernateTriggeredFlagDao() {
		super(TriggeredFlag.class);
	}
	
	public void saveTriggeredFlag(TriggeredFlag flag) {
		super.saveWithoutDuplicateHandling(flag);
	}

	public void updateTriggeredFlag(TriggeredFlag flag) {
		super.updateWithoutDuplicateHandling(flag);
	}
	
	public void deleteTriggeredFlag(TriggeredFlag flag) {
		super.delete(flag);
	}

	public List<TriggeredFlag> getTriggeredFlags(boolean resolved,boolean unresolved) {
		if(resolved ^ unresolved){
			DetachedCriteria c= DetachedCriteria.forClass(TriggeredFlag.class);
			if(resolved){
				c.add(Restrictions.eq("resolved",true));
			}else{
				c.add(Restrictions.eq("resolved",false));
			}
			c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return super.getList(c);
		}else if(resolved && unresolved){
			return getAllTriggeredFlags();
		}else{
			return new ArrayList<TriggeredFlag>();
		}
	}

	public List<TriggeredFlag> getAllTriggeredFlags() {
		return super.getAll();
	}

	public List<TriggeredFlag> getResolvedTriggeredFlags() {
		return getTriggeredFlags(true,false);
	}

	public TriggeredFlag getTriggeredFlagById(long id) {
		DetachedCriteria c= DetachedCriteria.forClass(TriggeredFlag.class);
		c.add(Restrictions.eq("id",id));
		return super.getUnique(c);
	}

	public List<TriggeredFlag> getUnresolvedTriggeredFlags() {
		return getTriggeredFlags(false,true);
	}

	public int countTriggeredFlags(String flagName, String patientName,
			boolean resolved, boolean unresolved) {
		return super.getCount(getCriteriaForFlagSearch(flagName, patientName, resolved, unresolved));
	}
	
	private DetachedCriteria getCriteriaForFlagSearch(String flagName, String patientName, boolean resolved, boolean unresolved){
		DetachedCriteria c = DetachedCriteria.forClass(TriggeredFlag.class);
		if(StringUtils.hasText(flagName)){
			c.createCriteria("flag").add(Restrictions.like("name", flagName,MatchMode.ANYWHERE));
		}
		if(StringUtils.hasText(patientName)){
			c.createCriteria("patient").add(Restrictions.like("name", patientName,MatchMode.ANYWHERE));
		}
		if(resolved ^ unresolved){
			if(resolved){
				c.add(Restrictions.eq("resolved", true));
			}else{
				c.add(Restrictions.eq("resolved", false));
			}
		}
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c;
	}
	
	private DetachedCriteria getCriteriaForFlagSearch(Patient patient, boolean resolved, boolean unresolved){
		DetachedCriteria c = DetachedCriteria.forClass(TriggeredFlag.class);
		if(patient != null){
			c.add(Restrictions.eq("patient", patient));
		}
		if(resolved ^ unresolved){
			if(resolved){
				c.add(Restrictions.eq("resolved", true));
			}else{
				c.add(Restrictions.eq("resolved", false));
			}
		}
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c;
	}

	public List<TriggeredFlag> findTriggeredFlags(String flagName,
			String patientName, boolean resolved, boolean unresolved,
			int startIndex, int maxResults) {
		return super.getList(getCriteriaForFlagSearch(flagName, patientName, resolved, unresolved), startIndex, maxResults);
	}

	public int countTriggeredFlags(Patient patient, boolean resolved, boolean active) {
		
		return super.getCount(getCriteriaForFlagSearch(patient,resolved,active));
	}

	public List<TriggeredFlag> findTriggeredFlags(Patient patient,
			boolean resolved, boolean active, int startIndex, int maxResults) {
		return super.getList(getCriteriaForFlagSearch(patient, resolved, active), startIndex, maxResults);
	}
}
