package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.springframework.context.ApplicationContext;

public class FormMappingResultSet extends PagedResultSet {

	private List<MedicFormResponse> results;
	
	private MedicFormResponseDao formResponseDao;
	
	private boolean searchingMapped;
	
	private boolean searchingRegForms = false;
	
	private Date aroundDate;
	
	private MedicForm form;
	
	public FormMappingResultSet(ApplicationContext appCon){
		this.formResponseDao = ((MedicFormResponseDao) appCon.getBean("MedicFormResponseDao"));
		super.pageSize=30;
		setSearchingMapped(false);
	}
	
	@Override
	public List<MedicFormResponse> getFreshResultsPage() {
		super.setTotalResults(formResponseDao.countFindFormResponses(isSearchingMapped(),searchingRegForms, form));
		this.results = formResponseDao.findFormResponses(isSearchingMapped(),searchingRegForms, form, aroundDate, getFirstResultOnPage() -1, pageSize);
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public List<MedicFormResponse> getResultsPage(){
		return results;
	}
	
	public void setSearchingMapped(boolean searchingMapped) {
		this.searchingMapped = searchingMapped;
	}
	public boolean isSearchingMapped() {
		return searchingMapped;
	}

	public void setAroundDate(Date aroundDate) {
		this.aroundDate = aroundDate;
	}

	public void setForm(MedicForm form) {
		this.form = form;
	}

	public void setSearchingRegForms(boolean searchingRegForms) {
		this.searchingRegForms = searchingRegForms;
	}
}
