package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagCondition;
import net.frontlinesms.plugins.patientview.data.repository.FlagConditionDao;

public class HibernateFlagConditionDao extends BaseHibernateDao<FlagCondition> implements FlagConditionDao {

	protected HibernateFlagConditionDao() {
		super(FlagCondition.class);
	}

	public List<FlagCondition> getConditionsForFlag(Flag f) {
		DetachedCriteria c = DetachedCriteria.forClass(FlagCondition.class);
		c.add(Restrictions.eq("flag", f));
		return super.getList(c);
	}

	public void deleteCondition(FlagCondition fc) {
		super.delete(fc);
	}
}
