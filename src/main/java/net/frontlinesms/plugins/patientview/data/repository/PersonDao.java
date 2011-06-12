package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;

public interface PersonDao {

	public List<Person> findPeople(String nameFragment, Class<? extends Person> personClass, int startIndex, int maxResults, boolean includeDeleted);
	
	public int countFindPeople(String nameFragment, Class<? extends Person> personClass, boolean includeDeleted);
}
