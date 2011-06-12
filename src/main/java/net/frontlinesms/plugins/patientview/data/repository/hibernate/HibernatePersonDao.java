package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.PersonDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernatePersonDao extends BaseHibernateDao<Person> implements PersonDao {

	protected HibernatePersonDao() {
		super(Person.class);
	}

	public int countFindPeople(String nameFragment, Class<? extends Person> personClass, boolean includeDeleted) {
		DetachedCriteria c = DetachedCriteria.forClass(personClass);
		if(nameFragment != null && !nameFragment.trim().equals("")){
			c.add(Restrictions.ilike("name",nameFragment,MatchMode.ANYWHERE));
		}
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getCount(c);
	}

	public List<Person> findPeople(String nameFragment, Class<? extends Person> personClass, int startIndex, int maxResults, boolean includeDeleted) {
		DetachedCriteria c = DetachedCriteria.forClass(personClass);
		if(nameFragment != null && !nameFragment.trim().equals("")){
			c.add(Restrictions.ilike("name",nameFragment,MatchMode.ANYWHERE));
		}
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getHibernateTemplate().findByCriteria(c, startIndex, maxResults);
	}
}