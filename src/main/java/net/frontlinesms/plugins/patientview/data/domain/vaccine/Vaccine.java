package net.frontlinesms.plugins.patientview.data.domain.vaccine;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OrderBy;

@Entity
@Table(name = "medic_vaccines")
public class Vaccine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long vaccineId;
	
	private String name;
	
	@OneToMany(fetch=FetchType.EAGER,mappedBy="vaccine",cascade=CascadeType.ALL)
	@OrderBy(clause = "position asc")
	private List<VaccineDose> doses;
	
	private boolean automaticallyEnrollNewborns;

	public Vaccine(){}
	
	public Vaccine(String name, boolean automaticallyEnrollNewborns) {
		super();
		this.name = name;
		this.automaticallyEnrollNewborns = automaticallyEnrollNewborns;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private void updateDosePositions(){
		for(int i = 0; i < doses.size();i++){
			doses.get(i).setPosition(i);
		}
	}
	
	public void setDoses(List<VaccineDose> doses) {
		this.doses= doses;
		if(this.doses== null) return;
		for(VaccineDose dose: doses){
			dose.setVaccine(this);
		}
		updateDosePositions();
	}
	
	public void addDose(VaccineDose dose){
		dose.setVaccine(this);
		doses.add(dose);
		dose.setPosition(doses.size()-1);
	}
	
	public void insertDose(int position, VaccineDose dose){
		doses.add(position, dose);
		updateDosePositions();
	}

	public void removeDose(VaccineDose dose){
		doses.remove(dose);
		updateDosePositions();
	}
	
	public List<VaccineDose> getDoses() {
		return doses;
	}

	public void setAutomaticallyEnrollNewborns(boolean automaticallyEnrollNewborns) {
		this.automaticallyEnrollNewborns = automaticallyEnrollNewborns;
	}

	public boolean isAutomaticallyEnrollNewborns() {
		return automaticallyEnrollNewborns;
	}

	public long getVaccineId() {
		return vaccineId;
	}
}