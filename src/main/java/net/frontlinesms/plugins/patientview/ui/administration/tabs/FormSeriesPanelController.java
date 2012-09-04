package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormSeriesDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;

public class FormSeriesPanelController extends ViewHandler{

	private static final String UI_XML = "/ui/plugins/patientview/administration/forms/formSeriesPanel.xml";
	
	private MedicForm form;
	
	private MedicFormSeries series;
	
	private MedicFormDao formDao;
	
	private MedicFormSeriesDao seriesDao;
	
	private static final String FORM_LIST = "formList";
	private static final String UP_BUTTON = "upButton";
	private static final String DOWN_BUTTON = "downButton";
	private static final String ADD_FORM_SELECT = "addFormToSeriesCombobox";
	
	public FormSeriesPanelController(UiGeneratorController ui, ApplicationContext appCon, MedicForm form) {
		super(ui, appCon, UI_XML);
		this.form = form;
		this.series = form.getSeries();
		this.formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		this.seriesDao = (MedicFormSeriesDao) appCon.getBean("MedicFormSeriesDao");
		populateSeriesList(0);
		populateAddFormSelect();
	}
	
	private void populateAddFormSelect(){
		List<MedicForm> forms = formDao.getFormsNotInSeries();
		Object select = ui.find(mainPanel,ADD_FORM_SELECT);
		ui.removeAll(select);
		for(MedicForm form: forms){
			Object item = ui.createComboboxChoice(form.getName(), form);
			ui.add(select,item);
		}
		ui.setSelectedIndex(select, 0);
	}
	
	public void formListSelectionChanged(){
		if(getSelectedIndex() == 0){
			ui.setEnabled(ui.find(mainPanel,UP_BUTTON), false);
		}else if(getSelectedIndex() == ui.getCount(ui.find(mainPanel,FORM_LIST))){
			ui.setEnabled(ui.find(mainPanel,DOWN_BUTTON), false);
		}else{
			ui.setEnabled(ui.find(mainPanel,DOWN_BUTTON), true);
			ui.setEnabled(ui.find(mainPanel,UP_BUTTON), true);
		}
	}
	
	private void populateSeriesList(int indexToSelect){
		if(series == null) return;
		List<MedicForm> forms = formDao.getFormsForSeries(series);
		ui.removeAll(ui.find(mainPanel,FORM_LIST));
		for(MedicForm form : forms){
			Object item = ui.createListItem(form.getName(), form);
			ui.add(ui.find(mainPanel,FORM_LIST),item);
		}
		if(indexToSelect < 0 ){
			indexToSelect = 0;
		}else if(indexToSelect >= forms.size()){
			indexToSelect = forms.size() - 1;
		}
		ui.setSelectedIndex(ui.find(mainPanel,FORM_LIST), indexToSelect);
	}
	
	private MedicForm getSelectedForm(){
		return (MedicForm) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,FORM_LIST)));
	}
	
	private int getSelectedIndex(){
		return ui.getSelectedIndex(ui.find(mainPanel,FORM_LIST));
	}
	
	public void moveFormUp(){
		moveForm(getSelectedForm(),-1);
	}
	
	public void moveFormDown(){
		moveForm(getSelectedForm(),1);
	}
	
	private void moveForm(MedicForm toMove, int dIndex){
		int index = getSelectedIndex();
		MedicForm form = getSelectedForm();
		List<MedicForm> forms = formDao.getFormsForSeries(series);
		forms.remove(form);
		forms.add(index+dIndex,form);
		for(int i = 0; i < forms.size();i++){
			forms.get(i).setSeriesPosition(i);
			formDao.updateMedicForm(forms.get(i));
		}
		series.moveForm(form, index + dIndex);
		populateSeriesList(index + dIndex);
	}

	public void removeForm(){
		MedicForm form = getSelectedForm();
		List<MedicForm> forms = formDao.getFormsForSeries(series);
		forms.remove(form);
		form.setSeries(null);
		for(int i = 0; i < forms.size();i++){
			forms.get(i).setSeriesPosition(i);
			formDao.updateMedicForm(forms.get(i));
		}
		formDao.updateMedicForm(form);
		populateSeriesList(getSelectedIndex());
		//TODO: If you just removed the selected form, close
	}
	
	public void insertForm(){
		MedicForm form = getSelectedForm();
//		List<MedicForm> forms = formDao.getFormsForSeries(series);
//		form.setSeries(series);
//		forms.add(getSelectedIndex()+1,form);
//		for(int i = 0; i < forms.size();i++){
//			forms.get(i).setSeriesPosition(i);
//			formDao.updateMedicForm(forms.get(i));
//		}
		series.insertForm(form, getSelectedIndex()+1);
		seriesDao.updateFormSeries(series);
		populateSeriesList(getSelectedIndex()+1);
		populateAddFormSelect();
	}
}
