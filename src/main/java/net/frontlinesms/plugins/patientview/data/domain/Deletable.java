package net.frontlinesms.plugins.patientview.data.domain;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;

@MappedSuperclass
public abstract class Deletable {
	
	protected boolean deleted = false;
	
	protected long dateDeleted;
	
	protected String deleteReason;
	
	@OneToOne(fetch=FetchType.LAZY,cascade={},optional=true)
	protected Person deleter;

	public boolean isDeleted() {
		return deleted;
	}

	public String getRemoveReason() {
		return deleteReason;
	}
	
	public Person getDeleter(){
		return deleter;
	}
	
	public Date getDateDeleted(){
		return new Date(dateDeleted);
	}
	
	public void delete(String reason){
		deleted = true;
		this.deleteReason = reason;
		this.deleter = UserSessionManager.getUserSessionManager().getCurrentUser();
		this.dateDeleted = new Date().getTime();
	}
}