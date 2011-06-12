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
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false,cascade={})
	@JoinColumn(name="vaccine")
	private Vaccine vaccine;
	
	@OneToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy="dose")
	private Set<ScheduledDose> scheduledDoses;
	
	private int position;
	
	private int windowStartDate;
	
	private int windowEndDate;
	
	private int minimumInterval;
	
	public VaccineDose(){}
	
	public VaccineDose(String name, int windowStartDate, int windowEndDate, int minimumInterval) {
		super();
		this.name = name;
		this.windowStartDate = windowStartDate;
		this.windowEndDate = windowEndDate;
		this.minimumInterval = minimumInterval;
	}

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

	public int getWindowStartDate() {
		return windowStartDate;
	}

	public void setWindowStartDate(int windowStartDate) {
		this.windowStartDate = windowStartDate;
	}

	public int getWindowEndDate() {
		return windowEndDate;
	}

	public void setWindowEndDate(int windowEndDate) {
		this.windowEndDate = windowEndDate;
	}

	public int getMinimumInterval() {
		return minimumInterval;
	}

	public void setMinimumInterval(int minimumInterval) {
		this.minimumInterval = minimumInterval;
	}

	public long getDoseId() {
		return doseId;
	}
}
