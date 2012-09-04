package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.ArrayList;
import java.util.List;

public class MedicFormSeries {

	private List<MedicForm> forms;
	private String name;
	
	public MedicFormSeries(MedicForm first, String name){
		forms = new ArrayList<MedicForm>();
		this.setName(name);
		addForm(first);
	}
	
	public MedicFormSeries(List<MedicForm> forms){
		this.forms = forms;
		if(forms.size() > 0)
			this.name = forms.get(0).getName();
	}
	
	public void setForms(List<MedicForm> forms){
		this.forms = forms;
		updatePositions();
	}
	
	public boolean addForm(MedicForm form){
		boolean val = forms.add(form);
		form.setSeries(getName());
		updatePositions();
		return val;
	}
	
	public boolean removeForm(MedicForm form){
		boolean val = forms.remove(form);
		form.setSeries(null);
		updatePositions();
		return val;
	}
	
	public void moveForm(int oldIndex, int newIndex){
		MedicForm form = forms.remove(oldIndex);
		forms.add(newIndex,form);
		updatePositions();
	}
	
	public void insertForm(MedicForm form, int index){
		forms.add(index, form);
		form.setSeries(getName());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
