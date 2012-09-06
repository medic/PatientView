package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormSeriesPanelController extends ViewHandler{

	private static final String UI_XML = "/ui/plugins/patientview/administration/forms/formSeriesPanel.xml";
	
	private MedicForm form;
	
	private MedicFormSeries series;
	
	private MedicFormDao formDao;
	
	private FormAdministrationPanelController parent;
		
	private static final String FORM_LIST = "formList";
	private static final String UP_BUTTON = "upButton";
	private static final String DOWN_BUTTON = "downButton";
	private static final String ADD_FORM_SELECT = "addFormToSeriesCombobox";
	
	public FormSeriesPanelController(UiGeneratorController ui, ApplicationContext appCon, MedicForm form,FormAdministrationPanelController parent) {
		super(ui, appCon, UI_XML);
		this.form = form;
		this.formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		this.series = new MedicFormSeries(formDao.getFormsForSeries(form.getSeries()));
		this.parent = parent;
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
		}else{
			ui.setEnabled(ui.find(mainPanel,UP_BUTTON), true);
		}
		if(getSelectedIndex() == series.getForms().size() -1){
			ui.setEnabled(ui.find(mainPanel,DOWN_BUTTON), false);
		}else{
			ui.setEnabled(ui.find(mainPanel,DOWN_BUTTON), true);
		}
	}
	
	private void populateSeriesList(int indexToSelect){
		if(series == null) return;
		ui.removeAll(ui.find(mainPanel,FORM_LIST));
		for(MedicForm form : series.getForms()){
			Object item = ui.createListItem(form.getName(), form);
			ui.add(ui.find(mainPanel,FORM_LIST),item);
		}
		if(indexToSelect < 0 ){
			indexToSelect = 0;
		}else if(indexToSelect >= series.getForms().size()){
			indexToSelect = series.getForms().size() - 1;
		}
		ui.setSelectedIndex(ui.find(mainPanel,FORM_LIST), indexToSelect);
		formListSelectionChanged();
	}
	
	private MedicForm getSelectedForm(){
		return series.getForm(getSelectedIndex());
	}
	
	private int getSelectedIndex(){
		return ui.getSelectedIndex(ui.find(mainPanel,FORM_LIST));
	}
	
	public void moveFormUp(){
		moveForm(getSelectedIndex(),-1);
	}
	
	public void moveFormDown(){
		moveForm(getSelectedIndex(),1);
	}
	
	private void moveForm(int indexToMove, int dIndex){
		series.moveForm(indexToMove, getSelectedIndex() + dIndex);
		for(MedicForm sForm : series.getForms()){
			formDao.updateMedicForm(sForm);
		}
		populateSeriesList(getSelectedIndex() + dIndex);
	}

	public void removeForm(){
		MedicForm form = getSelectedForm();
		series.removeForm(form);
		for(MedicForm sForm : series.getForms()){
			formDao.updateMedicForm(sForm);
		}
		formDao.updateMedicForm(form);
		populateSeriesList(getSelectedIndex());
		populateAddFormSelect();
		if(form.getFid() == this.form.getFid()){
			parent.formSeriesDeleted();
		}
	}
	
	public void insertForm(){
		MedicForm form = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,ADD_FORM_SELECT)));
		series.insertForm(form, getSelectedIndex()+1);
		for(MedicForm sForm : series.getForms()){
			formDao.updateMedicForm(sForm);
		}
		populateSeriesList(getSelectedIndex()+1);
		populateAddFormSelect();
	}
}
