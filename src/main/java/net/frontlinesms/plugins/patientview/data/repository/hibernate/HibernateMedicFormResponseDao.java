package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Date;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.search.OrderBySQL;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormResponseDao extends BaseHibernateDao<MedicFormResponse> implements MedicFormResponseDao{

	protected HibernateMedicFormResponseDao() {
		super(MedicFormResponse.class);
	}

	public List<MedicFormResponse> getAllFormResponses() {
		return super.getAll();
	}

	public void saveMedicFormResponse(MedicFormResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateMedicFormResponse(MedicFormResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}
	
	public List<MedicFormResponse> getFormResponsesForSubject(Person subject){
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.eq("subject", subject));
		return super.getList(c);
	}
	
	public List<MedicFormResponse> getFormResponsesForSubmitter(Person submitter){
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.eq("submitter", submitter));
		return super.getList(c);
	}

	public List<MedicFormResponse> getFormResponsesForForm(MedicForm form) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.eq("form", form));
		return super.getList(c);
	}

	public List<MedicFormResponse> getMappedResponses() {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.isNotNull("subject"));
		return super.getList(c);
	}


	public List<MedicFormResponse> getUnmappedResponses() {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.isNull("subject"));
		return super.getList(c);
	}

	public int countFindFormResponses(boolean searchingMappedForms, MedicForm form) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.setFetchMode("form.fields", FetchMode.LAZY);
		if(searchingMappedForms){
			c.add(Restrictions.isNotNull("subject"));
			c.createCriteria("submitter").add(Restrictions.sqlRestriction("{alias}.person_type='chw'"));
		}else{
			c.add(Restrictions.isNull("subject"));
		}
		if(form != null){
			c.add(Restrictions.eq("form", form));
		}
		return super.getCount(c);
	}

	public List<MedicFormResponse> findFormResponses(boolean searchingMappedForms, MedicForm form, Date aroundDate, int startIndex, int maxResults) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.setFetchMode("form.fields", FetchMode.LAZY);
		//are we searching mapped forms?
		if(searchingMappedForms){
			c.add(Restrictions.isNotNull("subject"));
			c.createCriteria("submitter").add(Restrictions.sqlRestriction("{alias}.person_type='chw'"));
		}else{
			c.add(Restrictions.isNull("subject"));
		}
		//set the form
		if(form != null){
			c.add(Restrictions.eq("form", form));
		}
		//set the date
		if(aroundDate != null){
			c.addOrder(OrderBySQL.sqlFormula("abs(dateSubmitted - " + aroundDate.getTime() + ") asc"));
		}else{
			c.addOrder(OrderBySQL.sqlFormula("dateSubmitted desc"));
		}
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return  super.getHibernateTemplate().findByCriteria(c, startIndex, maxResults);
	}

	public int countFindFormResponsesWithPeople(Person submitter, Person subject, MedicForm form) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		if(submitter != null){
			c.add(Restrictions.eq("submitter", submitter));
		}
		if(subject != null){
			c.add(Restrictions.eq("subject", subject));
		}
		if(form != null){
			c.add(Restrictions.eq("form", form));
		}
		return super.getCount(c);
	}

	public List<MedicFormResponse> findFormResponsesWithPeople(Person submitter, Person subject, MedicForm form, Date aroundDate, int startIndex, int maxResults) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		if(submitter != null){
			c.add(Restrictions.eq("submitter", submitter));
		}
		if(subject != null){
			c.add(Restrictions.eq("subject", subject));
		}
		if(form != null){
			c.add(Restrictions.eq("form", form));
		}
		
		if(aroundDate != null){
			c.addOrder(OrderBySQL.sqlFormula("abs(dateSubmitted - " + aroundDate.getTime() + ") asc"));
		}
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return super.getHibernateTemplate().findByCriteria(c, startIndex, maxResults);
	}
}
