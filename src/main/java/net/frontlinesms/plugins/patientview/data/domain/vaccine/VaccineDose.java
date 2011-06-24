package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "medic_vaccine_doses")
public class VaccineDose {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long doseId;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false,cascade={CascadeType.PERSIST,CascadeType.REFRESH})
	@JoinColumn(name="vaccine")
	private Vaccine vaccine;
	
	@OneToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy="dose")
	private Set<ScheduledDose> scheduledDoses;
	
	private int position;
	
	private int startDateMonths;
	
	private int startDateDays;

	private int endDateMonths;
	
	private int endDateDays;
	
	private int minIntervalMonths;
	
	private int minIntervalDays;
	
	public VaccineDose(String name, Vaccine vaccine, int startDateMonths, int startDateDays, int endDateMonths, int endDateDays, int minIntervalMonths, int minIntervalDays) {
		super();
		this.name = name;
		this.vaccine = vaccine;
		this.startDateMonths = startDateMonths;
		this.startDateDays = startDateDays;
		this.endDateMonths = endDateMonths;
		this.endDateDays = endDateDays;
		this.minIntervalMonths = minIntervalMonths;
		this.minIntervalDays = minIntervalDays;
	}

	public String getStringStartDate(){
		return startDateMonths + " months " + startDateDays + " days";
	}
	
	public String getStringEndDate(){
		return endDateMonths + " months " + endDateDays + " days";
	}
	
	public String getStringMinimumInterval(){
		return minIntervalMonths+ " months " +minIntervalDays+ " days";
	}
	
	public int getStartDateMonths() {
		return startDateMonths;
	}

	public void setStartDateMonths(int startDateMonths) {
		this.startDateMonths = startDateMonths;
	}

	public int getStartDateDays() {
		return startDateDays;
	}

	public void setStartDateDays(int startDateDays) {
		this.startDateDays = startDateDays;
	}

	public int getEndDateMonths() {
		return endDateMonths;
	}

	public void setEndDateMonths(int endDateMonths) {
		this.endDateMonths = endDateMonths;
	}

	public int getEndDateDays() {
		return endDateDays;
	}

	public void setEndDateDays(int endDateDays) {
		this.endDateDays = endDateDays;
	}

	public int getMinIntervalMonths() {
		return minIntervalMonths;
	}

	public void setMinIntervalMonths(int minIntervalMonths) {
		this.minIntervalMonths = minIntervalMonths;
	}

	public int getMinIntervalDays() {
		return minIntervalDays;
	}

	public void setMinIntervalDays(int minIntervalDays) {
		this.minIntervalDays = minIntervalDays;
	}

	public VaccineDose(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vaccine getVaccine() {
		return vaccine;
	}

	public void setVaccine(Vaccine vaccine) {
		this.vaccine = vaccine;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getDoseId() {
		return doseId;
	}
}
