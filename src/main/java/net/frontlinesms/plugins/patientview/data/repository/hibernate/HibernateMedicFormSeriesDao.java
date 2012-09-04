package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormSeriesDao;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormSeriesDao extends BaseHibernateDao<MedicFormSeries> implements MedicFormSeriesDao {

	protected HibernateMedicFormSeriesDao() {
		super(MedicFormSeries.class);
	}

	public void deleteFormSeries(MedicFormSeries series) {
		super.delete(series);
	}

	public void saveFormSeries(MedicFormSeries series) {
		super.saveWithoutDuplicateHandling(series);
	}

	public void updateFormSeries(MedicFormSeries series) {
		try{
			super.updateWithoutDuplicateHandling(series);
		}catch (Throwable t){
			Session session = this.getSessionFactory().openSession();
			Criteria c = session.createCriteria(MedicFormSeries.class);
			c.add(Restrictions.eq("fsid", series.getFsid()));
			MedicFormSeries fetchedSeries = (MedicFormSeries) c.uniqueResult();
			fetchedSeries.setForms(series.getForms());
			session.update(fetchedSeries);
			session.flush();
			session.close();
		}
	}

	public List<MedicFormSeries> getAllFormSeries() {
		return super.getAll();
	}
	
	public void initializeSeries(MedicFormSeries series){
		Hibernate.initialize(series.getForms());
	}
}
