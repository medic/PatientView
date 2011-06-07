package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.springframework.context.ApplicationContext;


/**
 * A paged result set for retrieving form responses
 * @author dieterichlawson
 *
 */
public class FormResponseResultSet extends PagedResultSet {

	/**
	 * Orders the results by the distance from this date
	 */
	private Date aroundDate;
	
	/**
	 * Limits the results to form responses submitted by this person
	 */
	private Person submitter;
	
	/**
	 * Limits the results to form responses about this persom
	 */
	private Person subject;
	
	/**
	 * Limits the results to form responses for this form
	 */
	private MedicForm form;
	
	/**
	 * The results
	 */
	private List<MedicFormResponse> results;
	
	private MedicFormResponseDao formResponseDao;

	public FormResponseResultSet(ApplicationContext appCon){
		this.formResponseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		super.pageSize = 28;
	}
	
	@Override
	public List<MedicFormResponse> getFreshResultsPage() {
		super.setTotalResults(formResponseDao.countFindFormResponsesWithPeople(submitter, subject, form));
		this.results = formResponseDao.findFormResponsesWithPeople(submitter, subject, form, aroundDate, getFirstResultOnPage() -1 , pageSize);
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public List<MedicFormResponse> getResults() {
		return results;
	}

	public void setAroundDate(Date aroundDate) {
		this.aroundDate = aroundDate;
	}

	public void setSubmitter(Person submitter) {
		this.submitter = submitter;
	}

	public void setSubject(Person subject) {
		this.subject = subject;
	}

	public void setForm(MedicForm form) {
		this.form = form;
	}
	
	public List getResultsPage(){
		return results;
	}

}
