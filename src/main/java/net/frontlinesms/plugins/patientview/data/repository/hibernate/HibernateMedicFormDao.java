package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

public class HibernateMedicFormDao extends BaseHibernateDao<MedicForm> implements MedicFormDao{
	
	protected HibernateMedicFormDao() {
		super(MedicForm.class);
	}

	public void saveMedicForm(MedicForm form) {
		super.saveWithoutDuplicateHandling(form);
	}

	public void updateMedicForm(MedicForm form) {
		super.updateWithoutDuplicateHandling(form);
	}
	
	public void deleteMedicForm(MedicForm form) {
		super.delete(form);
	}

	public List<MedicForm> getAllMedicForms() {
		return super.getAll();
	}
	
	public List<MedicForm> getAllMedicFormsInitialized() {
		Session session = this.getSessionFactory().openSession();
		Criteria c = session.createCriteria(MedicForm.class);
		c.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
		List<MedicForm> forms = c.list();
		for(MedicForm form : forms){
			if(form.getSeries() == null) continue;
			Hibernate.initialize(form.getSeries().getForms());
		}
		session.close();
		return forms;
	}

	public List<MedicForm> findMedicFormsByName(String nameFragment){
		DetachedCriteria c = DetachedCriteria.forClass(MedicForm.class);
		c.add(Restrictions.like("name", nameFragment,MatchMode.ANYWHERE));
		c.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
		return super.getList(c);
	}

	public MedicForm getMedicFormForForm(Form form) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicForm.class);
		c.add(Restrictions.eq("vanillaForm", form));
		return super.getUnique(c);
	}

	public List<MedicForm> getFormsForSeries(MedicFormSeries series) {
		Session session = this.getSessionFactory().openSession();
		Criteria c = session.createCriteria(MedicForm.class);
		c.add(Restrictions.eq("series", series));
		c.addOrder(Order.asc("seriesPosition"));
		c.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
		List<MedicForm> forms = c.list();
		for(MedicForm form : forms){
			if(form.getSeries() == null) continue;
			Hibernate.initialize(form.getSeries().getForms());
		}
		session.close();
		return forms;
	}

	public List<MedicForm> getFormsNotInSeries() {
		DetachedCriteria c = DetachedCriteria.forClass(MedicForm.class);
		c.add(Restrictions.isNull("series"));
		c.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
		return super.getList(c);
	}
}
