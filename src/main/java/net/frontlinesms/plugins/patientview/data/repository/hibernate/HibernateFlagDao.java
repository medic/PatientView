package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.repository.FlagDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateFlagDao extends BaseHibernateDao<Flag> implements FlagDao {

	protected HibernateFlagDao() {
		super(Flag.class);
	}

	public void deleteFlag(Flag flag) {
		super.delete(flag);
	}

	public List<Flag> getAllFlags() {
		return super.getAll();
	}

	public List<Flag> getAllFlags(boolean includeDeleted) {
		return null;
	}

	public Flag getFlagById(long id) {
		DetachedCriteria c = DetachedCriteria.forClass(Flag.class);
		c.add(Restrictions.eq("fid",id));
		return super.getUnique(c);
	}

	public void saveFlag(Flag flag) {
		super.saveWithoutDuplicateHandling(flag);
	}

	public void updateFlag(Flag flag) {
		super.updateWithoutDuplicateHandling(flag);
	}
}
