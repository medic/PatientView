package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.springframework.context.ApplicationContext;

public class SmsMessageResultSet extends PagedResultSet{

	private String contentSearchString = "";
	private Date aroundDate;
	
	private String senderNumber;
	private boolean searchingFrom=true;
	private boolean searchingTo=true;
	
	private List<MedicMessageResponse> results;
	private MedicMessageResponseDao messageDao;

	public SmsMessageResultSet(ApplicationContext appCon){
		this.messageDao = (MedicMessageResponseDao) appCon.getBean("MedicMessageResponseDao");
		super.pageSize = 17;
	}
	
	@Override
	public List<MedicMessageResponse> getFreshResultsPage() {
		super.setTotalResults(messageDao.countFindMessages(contentSearchString, senderNumber, searchingFrom, searchingTo)); 
		this.results = messageDao.findMessages(contentSearchString, senderNumber, searchingFrom, searchingTo, aroundDate, getFirstResultOnPage() -1, pageSize);
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public void setContentSearchString(String contentSearchString) {
		this.contentSearchString = contentSearchString;
	}
	
	public void setAroundDate(Date aroundDate) {
		this.aroundDate = aroundDate;
	}

	public void setSenderNumber(String senderNumber) {
		this.senderNumber = senderNumber;
	}

	public String getSenderNumber() {
		return senderNumber;
	}

	public void setSearchingFrom(boolean searchingFrom) {
		this.searchingFrom = searchingFrom;
	}
	
	public void setSearchingTo(boolean searchingTo) {
		this.searchingTo = searchingTo;
	}
	
	public List<MedicMessageResponse> getResultsPage(){
		return results;
	}
}
