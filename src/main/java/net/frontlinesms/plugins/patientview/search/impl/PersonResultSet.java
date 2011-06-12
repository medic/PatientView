package net.frontlinesms.plugins.patientview.search.impl;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.PersonDao;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.springframework.context.ApplicationContext;

public class PersonResultSet<P extends Person> extends PagedResultSet {

	private List<P> results;
	
	private Class<P> personClass;
		
	private String nameString;
	
	private PersonDao personDao;
	
	private boolean includeDeleted = false;

	@Override
	public List<P> getFreshResultsPage() {
		super.setTotalResults(personDao.countFindPeople(nameString, personClass,includeDeleted));
		this.results = (List<P>) personDao.findPeople(nameString, personClass, getFirstResultOnPage() -1 , pageSize,false);
		return results;
	}

	@Override
	public List<P> getResultsPage() {
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public PersonResultSet(ApplicationContext appCon, Class<P> personClass){
		this.personDao = ((PersonDao) appCon.getBean("PersonDao"));
		super.pageSize = 28;
		this.personClass = personClass;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public String getNameString() {
		return nameString;
	}
}
