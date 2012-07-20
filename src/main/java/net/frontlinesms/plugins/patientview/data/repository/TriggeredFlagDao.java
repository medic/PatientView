package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;

public interface TriggeredFlagDao {

	public void saveTriggeredFlag(TriggeredFlag flag);
	public void updateTriggeredFlag(TriggeredFlag flag);
	public void deleteTriggeredFlag(TriggeredFlag flag);
	
	public List<TriggeredFlag> findTriggeredFlags(String flagName, String patientName, boolean resolved, boolean unresolved,int startindex, int maxResults);
	public int countTriggeredFlags(String flagName, String patientName, boolean resolved, boolean unresolved);

	public List<TriggeredFlag> getTriggeredFlags(boolean resolved, boolean unresolved);
	public List<TriggeredFlag> getResolvedTriggeredFlags();
	public List<TriggeredFlag> getUnresolvedTriggeredFlags();
	public List<TriggeredFlag> getAllTriggeredFlags();
	
	public TriggeredFlag getTriggeredFlagById(long id);
}
