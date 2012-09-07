package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;
import java.util.Map;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

public interface MedicFormResponseSeriesDao {

	public List<MedicFormResponse> getFormResponseSeries(Person subject, MedicFormSeries series);
	
	public Map<String,String> getFieldValuesInFormResponseSeries(Person subject, MedicFormSeries series,DataType type);
	
	public Map<String,String> getFieldValuesInFormResponseSeries(MedicFormResponse response,DataType type);

    public int getMostRecentFormResponseInSeries(String series,Person subject);
}
