package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateCommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.classic.ValidationFailure;
import org.springframework.context.ApplicationContext;

public class CHWComboBox extends ThinletFormField<CommunityHealthWorker> implements PersonalFormField{
	
	private CommunityHealthWorker response;
	private Object comboBox;
	private HibernateCommunityHealthWorkerDao chwDao;
	private boolean hasChanged = false;
	
	public CHWComboBox(ExtendedThinlet thinlet, ApplicationContext appCon, CommunityHealthWorker chw, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18nString("medic.common.chw")+":", delegate);
		comboBox =ExtendedThinlet.create("combobox");
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		thinlet.setEditable(comboBox, false);
		chwDao = (HibernateCommunityHealthWorkerDao) appCon.getBean("CHWDao");
		fillComboBox(chw);
		thinlet.add(mainPanel,comboBox);
		thinlet.setWeight(comboBox, 5, 0);
		thinlet.setColspan(mainPanel, 2);
	}

	public void fillComboBox(CommunityHealthWorker toSelect){
		thinlet.removeAll(comboBox);
		List<CommunityHealthWorker> chws = chwDao.getAllCommunityHealthWorkers(false);
		Object nullChoice = thinlet.createComboboxChoice("No CHW", null);
		thinlet.add(comboBox,nullChoice);
		int indexToSelect = 0;
		for(int i = 0; i < chws.size(); i++){
			CommunityHealthWorker chw = chws.get(i);
			Object choice = thinlet.createComboboxChoice(chw.getName(), chw);
			thinlet.add(comboBox,choice);
			if(toSelect != null && chw.getPid() == toSelect.getPid()){
				indexToSelect = i+1;
			}
		}
		thinlet.setSelectedIndex(comboBox, indexToSelect);
		response = (CommunityHealthWorker) thinlet.getAttachedObject(thinlet.getSelectedItem(comboBox));
	}
	
	public void selectionChanged(int index){
		hasChanged =true;
		if(index >=0){
			response = (CommunityHealthWorker) thinlet.getAttachedObject(thinlet.getItem(comboBox, index));
		}else{
			response = null;
		}
		super.responseChanged();
	}

	@Override
	public void validate() throws ValidationFailure{
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void setRawResponse(CommunityHealthWorker chw) {
		response = chw;
		fillComboBox(chw);
	}
	
	@Override
	/** DOES NOTHING**/
	public void setStringResponse(String chw) {
	}
	
	@Override
	public CommunityHealthWorker getRawResponse(){
		return response;
	}
	
	public boolean hasResponse(){
		return true;
	}
	
	@Override
	public String getStringResponse(){
		if(response!=null){
			return response.getName();
		}else{
			return "";
		}
	}

	public void setFieldForPerson(Person p) {
		Patient pat;
		try{
			pat = (Patient) p;
		}catch(Throwable t){return;}
		pat.setChw(getRawResponse());
	}
	
}
