package net.frontlinesms.plugins.patientview.data.domain.people;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;

@Entity
@DiscriminatorValue("chw")
public class CommunityHealthWorker extends Person {

	@OneToOne(targetEntity = Contact.class, fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "contact_id")
	private Contact contactInfo;
	
	public CommunityHealthWorker() {}

	public CommunityHealthWorker(String name,String phoneNumber, Gender gender, Date birthdate) {
		super(name, gender, birthdate);
		contactInfo = new Contact(name,phoneNumber,null,null,null,true);
	}

	@Override
	public String getPersonType() {
		return "CHW";
	}
	
	public Contact getContactInfo(){
		return contactInfo;
	}
	
	public String getPhoneNumber(){
		return contactInfo.getPhoneNumber();
	}
	
	public void setPhoneNumber(String phoneNumber){
		if (contactInfo == null){
			contactInfo = new Contact(this.getName(),phoneNumber,null,null,null,true);
		}else{
			try {
				contactInfo.setPhoneNumber(phoneNumber);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (DuplicateKeyException e) {
				e.printStackTrace();
			}
		}
	}

}
