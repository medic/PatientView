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


	
	public VaccineDose(String name, Vaccine vaccine, int startDateMonths, int startDateDays) {
		super();
		this.name = name;
		this.vaccine = vaccine;
		this.startDateMonths = startDateMonths;
		this.startDateDays = startDateDays;

	}

	public String getStringStartDate(){
		return startDateMonths + " months " + startDateDays + " days";
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
