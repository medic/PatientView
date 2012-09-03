package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;

public interface MedicFormSeriesDao {

	public void deleteFormSeries(MedicFormSeries series);
	
	public void saveFormSeries(MedicFormSeries series);
	
	public void updateFormSeries(MedicFormSeries series);
	
	public List<MedicFormSeries> getAllFormSeries();	
}