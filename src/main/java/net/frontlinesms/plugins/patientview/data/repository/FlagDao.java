package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;

public interface FlagDao {

	/**
	 * Returns the flag with fid 'id'
	 * or null otherwise.
	 * 
	 * @param id
	 * @return
	 */
	public Flag getFlagById(long id);
	
	/**
	 * Returns all flags (including deleted flags)
	 * @return
	 */
	public List<Flag> getAllFlags();
	
	/**
	 * Returns all flags. 'includeDeleted' allows
	 * you to specify whether or not you want
	 * deleted flags as well as currently active flags
	 * 
	 * @param includeDeleted
	 * @return
	 */
	public List<Flag> getAllFlags(boolean includeDeleted);
	
	/**
	 * Save a flag to the data source. This
	 * operation also saves all of 'flag's 
	 * FlagCondition objects.
	 * 
	 * @param flag
	 */
	public void saveFlag(Flag flag);
	
	/**
	 * Update a flag and all its FlagCondition
	 * objects
	 */
	public void updateFlag(Flag flag);
	
	/**
	 * Soft delete a flag and all its FlagCondition
	 * objects.
	 *  
	 * @param flag
	 */
	public void deleteFlag(Flag flag);
}
