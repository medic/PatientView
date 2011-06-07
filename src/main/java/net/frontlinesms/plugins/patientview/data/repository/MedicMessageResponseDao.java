package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;

public interface MedicMessageResponseDao {

	/**
	 * @return all MedicMessageResponses
	 */
	public Collection<MedicMessageResponse> getAllMedicMessageResponse();

	/**
	 * Saves a message response
	 * @param message
	 */
	public void saveMedicMessageResponse(MedicMessageResponse message);

	/**
	 * Updates a message response
	 * @param message
	 */
	public void updateMedicMessageResponse(MedicMessageResponse message);
	
	/**
	 * Returns the medic message that corresponds with the given FrontlineSMS message
	 * @param vanillaMessage
	 * @return
	 */
	public MedicMessageResponse getMessageForVanillaMessage(FrontlineMessage vanillaMessage);
	
	public List<MedicMessageResponse> findMessages(String messageFragment, String senderNumber, boolean searchingFrom, boolean searchingTo, Date aroundDate, int startIndex, int maxResults);
	
	public int countFindMessages(String messageFragment, String senderNumber, boolean searchingFrom, boolean searchingTo);

}
