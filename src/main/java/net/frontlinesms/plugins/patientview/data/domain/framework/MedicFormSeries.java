package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "medic_form_series")
public class MedicFormSeries {

	/** Unique id for this entity. This is for hibernate usage. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long fsid;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "series")
	@Cascade({CascadeType.SAVE_UPDATE})
	@OrderBy(value="seriesPosition")
	private List<MedicForm> forms;
	
	public MedicFormSeries(){}
	
	public long getFsid() {
		return fsid;
	}
	
	public boolean addForm(MedicForm form){
		boolean val = forms.add(form);
		updatePositions();
		return val;
	}
	
	public boolean removeForm(MedicForm form){
		boolean val = forms.remove(form);
		updatePositions();
		return val;
	}
	
	public void insertForm(MedicForm form, int index){
		forms.add(index, form);
		updatePositions();
	}
	
	public MedicForm getForm(int index){
		return forms.get(index);
	}
	
	private void updatePositions(){
		for(int i = 0; i < forms.size();i++){
			forms.get(i).setSeriesPosition(i);
		}
	}
	
	public List<MedicForm> getForms(){
		return forms;
	}
}
