package net.frontlinesms.plugins.patientview.data.domain.people;


import java.awt.image.BufferedImage;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.Deletable;
import net.frontlinesms.plugins.patientview.ui.imagechooser.ImageUtils;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="medic_people")
@DiscriminatorColumn(name="person_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="per")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Person extends Deletable{
	
	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	protected long pid;
	
	/**
	 * Name of this person
	 */
	@IndexColumn(name="name_index")
	protected String name;
	
	/**
	 * Birthdate of this person
	 */
	protected long birthdate;
	
	/**
	 * Phone number of this person
	 */
	protected String phoneNumber;
	
	/**
	 * Gender of this person. Right now, possibilities are m,f,t.
	 * Should figure out a better way to do this
	 */
	@Enumerated(EnumType.ORDINAL)
	protected Gender gender;
	
	@Lob
	@Basic(fetch=FetchType.LAZY)
	protected byte[] unscaledImageContent;

	@Lob
	@Basic(fetch=FetchType.LAZY)
	protected byte[] thumbnailImageContent;
	
	/**
	 * skeleton constructor for hibernate
	 */
	public Person(){}
	
	/**
	 * Protected constructor for person, used only by subclasses
	 * @param name Name of the person
	 * @param gender Gender of the Person (options are m,f,t)
	 * @param birthdate birthdate of the person
	 */
	protected Person(String name, Gender gender, Date birthdate){
		this.name = name;
		this.gender = gender;
		this.birthdate = birthdate.getTime();
	}
	
	/**
	 * Protected constructor for person, used only by subclasses
	 * @param name Name of the person
	 * @param gender Gender of the Person (options are m,f,t)
	 * @param birthdate birthdate of the person
	 * @param phoneNumber the phone number of the person
	 */
	protected Person(String name, Gender gender, Date birthdate, String phoneNumber){
		this.name = name;
		this.gender = gender;
		this.birthdate = birthdate.getTime();
		this.phoneNumber = phoneNumber;
	}
	
	public long getPid() {
		return pid;
	}
	
	/**
	 * String value of the person's ID number.
	 * This is for use in AdvancedTableController
	 * @return
	 */
	public String getStringID(){
		return ""+ pid;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthdate() {
		return new Date(birthdate);
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate.getTime();
	}
	
	public String getStringBirthdate(){
		return InternationalisationUtils.getDateFormat().format(birthdate);
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public int getAge() {
		return TimeUtils.getAge(new Date(birthdate));
	 }
	
	public String getStringAge(){
		return String.valueOf(getAge());
	}
	
	public String getStringGender(){
		return getGender().toString();
	}
	
	
	public boolean hasImage(){
		return unscaledImageContent !=null;
	}
	
	 public BufferedImage getImage() {
	     return ImageUtils.getImageFromByteArray(unscaledImageContent);
	 }
	 
	 public BufferedImage getResizedImage(){
		 return ImageUtils.getImageFromByteArray(thumbnailImageContent);
	 }

	 public void setImage(BufferedImage image, String type) {
	    unscaledImageContent = ImageUtils.getByteArrayForImage(ImageUtils.getLargeImage(image), type);
	    thumbnailImageContent = ImageUtils.getByteArrayForImage(ImageUtils.getThumbnailImage(image), type);
	 }

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
