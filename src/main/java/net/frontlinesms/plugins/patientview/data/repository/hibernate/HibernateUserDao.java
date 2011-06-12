package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * The default implementation of the User DAO
 */
public class HibernateUserDao extends BaseHibernateDao<User> implements UserDao {

	protected HibernateUserDao() {
		super(User.class);
	}

	public void deleteUser(User u, String reason) {
		u.delete(reason);
		updateUser(u);
	}

	public List<User> findUsersByUsername(String s, boolean includeDeleted) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("username", "%" + s + "%"));
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		return super.getList(c);
	}

	public Collection<User> getAllUsers() {
		return super.getAll();
	}

	public User getUserByUsername(String username, boolean includeDeleted) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("username", username));
		if(!includeDeleted){
			c.add(Restrictions.eq("deleted",false));
		}
		return super.getUnique(c);
	}

	public User getUserById(long id) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("pid", id));
		return super.getUnique(c);
	}

	public List<User> findUsersByName(String nameFragment, int limit, boolean includeDeleted) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("name", "%" + nameFragment + "%"));
		if(!includeDeleted){
			c.add(Restrictions.or(Restrictions.isNull("deleted"), Restrictions.eq("deleted",false)));
		}
		if (limit > 0)
			return super.getList(c, 0, limit);
		else {
			return super.getList(c);
		}
	}

	public void saveUser(User u) {
		super.saveWithoutDuplicateHandling(u);
	}

	public void updateUser(User u) {
		super.updateWithoutDuplicateHandling(u);
	}
	
}
