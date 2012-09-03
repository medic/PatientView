package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormSeriesDao;

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
		super.updateWithoutDuplicateHandling(series);
	}

	public List<MedicFormSeries> getAllFormSeries() {
		return super.getAll();
	}
}
