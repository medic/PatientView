package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;
import net.frontlinesms.plugins.patientview.search.OrderBySQL;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicMessageResponseDao extends BaseHibernateDao<MedicMessageResponse> implements MedicMessageResponseDao{

	protected HibernateMedicMessageResponseDao() {
		super(MedicMessageResponse.class);
	}

	public Collection<MedicMessageResponse> getAllMedicMessageResponse() {
		return super.getAll();
	}

	public void saveMedicMessageResponse(MedicMessageResponse message) {
		super.saveWithoutDuplicateHandling(message);
	}

	public void updateMedicMessageResponse(MedicMessageResponse message) {
		super.updateWithoutDuplicateHandling(message);
	}

	public MedicMessageResponse getMessageForVanillaMessage(FrontlineMessage vanillaMessage) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicMessageResponse.class);
		c.add(Restrictions.eq("message",vanillaMessage));
		return super.getUnique(c);
	}

	public int countFindMessages(String messageFragment, String senderNumber, boolean searchingFrom, boolean searchingTo) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicMessageResponse.class);
		if(messageFragment != null && !messageFragment.trim().equals("")) c.add(Restrictions.ilike("messageContent",messageFragment,MatchMode.ANYWHERE));
		
		DetachedCriteria messageCrit = c.createCriteria("message");
		
		if(searchingFrom && !searchingTo){
			messageCrit.add(Restrictions.eq("senderMsisdn",senderNumber));
		}else if(searchingTo && !searchingFrom){
			messageCrit.add(Restrictions.eq("recipientMsisdn",senderNumber));
		}else if(searchingTo && searchingFrom){
			messageCrit.add(Restrictions.or(Restrictions.eq("senderMsisdn",senderNumber),Restrictions.eq("recipientMsisdn",senderNumber)));
		}
		return super.getCount(c);
	}

	public List<MedicMessageResponse> findMessages(String messageFragment, String senderNumber, boolean searchingFrom, boolean searchingTo, Date aroundDate, int firstResult, int maxResults) {
		//if we aren't searching anything, return nothing
		if(!searchingFrom && !searchingTo) return new ArrayList<MedicMessageResponse>();
		//create the criteria
		DetachedCriteria c = DetachedCriteria.forClass(MedicMessageResponse.class);
		if(messageFragment != null && !messageFragment.trim().equals("")) c.add(Restrictions.ilike("messageContent",messageFragment,MatchMode.ANYWHERE));
		
		DetachedCriteria messageCrit = c.createCriteria("message");
		
		if(searchingFrom && !searchingTo){
			messageCrit.add(Restrictions.eq("senderMsisdn",senderNumber));
		}else if(searchingTo && !searchingFrom){
			messageCrit.add(Restrictions.eq("recipientMsisdn",senderNumber));
		}else if(searchingTo && searchingFrom){
			messageCrit.add(Restrictions.or(Restrictions.eq("senderMsisdn",senderNumber),Restrictions.eq("recipientMsisdn",senderNumber)));
		}
		
		if(aroundDate != null){
			c.addOrder(OrderBySQL.sqlFormula("abs(dateSubmitted - " + aroundDate.getTime() + ") asc"));
		}
		return super.getHibernateTemplate().findByCriteria(c, firstResult, maxResults);
	}
}
