package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagCondition;

public interface FlagConditionDao {
	
	/**
	 * Returns a list of all conditions associated
	 * with the supplied flag
	 * @param f
	 * @return
	 */
	public List<FlagCondition> getConditionsForFlag(Flag f);
}
