package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseSeriesDao;

public class HibernateMedicFormResponseSeriesDao extends BaseHibernateDao<MedicFormResponse> implements MedicFormResponseSeriesDao {

	protected HibernateMedicFormResponseSeriesDao() {
		super(MedicFormResponse.class);
	}

	private MedicFormResponseDao responseDao;
	public void setResponseDao (MedicFormResponseDao responseDao ){this.responseDao = responseDao;}
	public MedicFormResponseDao getResponseDao (){return responseDao;}

	private MedicFormFieldResponseDao fieldResponseDao;
	public void setFieldResponseDao (MedicFormFieldResponseDao fieldResponseDao ){this.fieldResponseDao = fieldResponseDao;}
	public MedicFormFieldResponseDao getFieldResponseDao (){return fieldResponseDao;}

	private MedicFormDao formDao;
	public void setFormDao (MedicFormDao formDao ){this.formDao = formDao;}
	public MedicFormDao getFormDao (){return formDao;}
	
	public List<MedicFormResponse> getFormResponseSeries(Person subject, MedicFormSeries series) {
		return getFormResponseSeries(subject, series.getName());
	}
	
	public List<MedicFormResponse> getFormResponseSeries(Person subject, String series) {
		List<MedicFormResponse> responses = new ArrayList<MedicFormResponse>();
		List<MedicForm> forms = formDao.getFormsForSeries(series);
		for(MedicForm form : forms){
			MedicFormResponse formResponse = responseDao.findLatestFormResponseForSubject(subject, form);
			if(formResponse != null){
				responses.add(formResponse);
			}
		}
		return responses;
	}

	public Map<String, String> getFieldValuesInFormResponseSeries(Person subject, MedicFormSeries series, DataType type) {
		return getFieldValuesInFormResponseSeries(subject, series.getName(), type);	
	}
	
	public Map<String, String> getFieldValuesInFormResponseSeries(Person subject, String series, DataType type) {
		List<MedicFormResponse> responses = getFormResponseSeries(subject,series);
		Map<String,String> values = new HashMap<String,String>();
		for(MedicFormResponse response: responses){
			for(MedicFormFieldResponse fieldResponse: fieldResponseDao.getResponsesForFormResponse(response)){
				if(fieldResponse.getField().getDatatype() != type) continue;
				values.put(response.getFormName()+":"+fieldResponse.getFieldLabel(), fieldResponse.getValue());
			}
		}
		return values;
	}
	
	public Map<String, String> getFieldValuesInFormResponseSeries(MedicFormResponse response, DataType type) {
		Map<String, String> values = getFieldValuesInFormResponseSeries(response.getSubject(), response.getForm().getSeries(), type);
		for(MedicFormFieldResponse fieldResponse: fieldResponseDao.getResponsesForFormResponse(response)){
			if(fieldResponse.getField().getDatatype() != type) continue;
			values.put(response.getFormName()+":"+fieldResponse.getFieldLabel(), fieldResponse.getValue());
		}
		return values;
	}
}