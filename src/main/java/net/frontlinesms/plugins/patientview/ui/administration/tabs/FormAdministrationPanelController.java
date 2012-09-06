package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.Collection;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm.MedicFormType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormSeries;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class FormAdministrationPanelController extends AdministrationTabPanel{
	
	/* i18n */
	private static final String FORM_PANEL_TITLE = "admin.tabs.form.panel.title";
	private static final String FIELDS_ON_FORM_PREFIX = "admin.forms.fields.on.form.prefix";
	private static final String FORM_ALREADY_RESPONDED_TO_DIALG = "admin.forms.form.already.responded.to.dialog";
	
	private static final String FORM_PANEL_XML = "/ui/plugins/patientview/administration/forms/formAdministrationPanel.xml";
	
	/* Thinlet objects */
	/**The main Thinlet container for this panel */
	private Object mainPanel;
	/**The list second from the left, with all patient view forms in it */
	private Object patientViewFormList;
	/** The list farthest to the right, with the fields of the currently selected medic form in it*/
	private Object fieldList;
	
	/** The combo box that holds the choices for the form field -> patient field mapping*/
	private Object mappingComboBox;
	private Object formTypeComboBox;

	/* Daos */
	MedicFormDao patientViewFormDao;
	MedicFormFieldDao patientViewFieldDao;
	
	public String getListItemTitle() {
		return InternationalisationUtils.getI18nString(FORM_PANEL_TITLE);
	}

	public FormAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController, appCon, FORM_PANEL_XML);
		init();
	}
	
	private void init(){
		patientViewFormList = find("patientViewFormList");
		fieldList = find("fieldList");
		mappingComboBox = find("mappingComboBox");
		formTypeComboBox= find("formTypeSelect");
		//initialize the daos
		patientViewFormDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		patientViewFieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		//initialize the lists, etc..
		populatePatientViewFormList();
	}

	/**
	 * Takes a hibernate proxy object and returns the real object
	 * @param entity
	 * @return
	 */
	public static Form initializeAndUnproxy(Form entity) {
	    if (entity == null) {
	        throw new  NullPointerException("Entity passed for initialization is null");
	    }

	    Hibernate.initialize(entity);
	    if (entity instanceof HibernateProxy) {
	        entity = (Form) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
	    }
	    return entity;
	}

	
	/**
	 * Gets all forms in the Patient View system and displays them in the
	 * proper list
	 */
	private void populatePatientViewFormList(){
		Collection<MedicForm> pvForms = patientViewFormDao.getAllMedicForms();
		removeAll(patientViewFormList);
		for(MedicForm f: pvForms){
			Object item = ui.createListItem(f.getName(), f);
			add(patientViewFormList,item);
		}
		ui.setSelectedIndex(patientViewFormList, 0);
		patientViewFormListSelectionChanged();
	}
	
	/**
	 * Called when the user makes a selection in the list of
	 * Patient View forms. Gets the form that was selected
	 * and changes the fields that are displayed in the field list
	 */
	public void patientViewFormListSelectionChanged(){
		MedicForm selectedForm = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(patientViewFormList));
		if(selectedForm != null){
			populateFieldList(selectedForm);
			populateFormTypeSelect(selectedForm);
			addFormSeriesPanel(selectedForm);
		}
	}
	
	public void formSeriesDeleted(){
		MedicForm selectedForm = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(patientViewFormList));
		addFormSeriesPanel(selectedForm);
	}
	
	private void addFormSeriesPanel(MedicForm f){
		MedicForm form = patientViewFormDao.getMedicFormForId(f.getFid());
		Object outerPanel = find("outerSeriesPanel");
		ui.removeAll(outerPanel);
		if(StringUtils.hasText(form.getSeries())){
			FormSeriesPanelController panel = new FormSeriesPanelController(ui, appCon, form,this);
			ui.add(outerPanel,panel.getMainPanel());
		}else{
			Object button = ui.createButton("Create Form Series", "createFormSeries()",null, this);
			Object panel = ui.createPanel("outerButtonPanel");
			ui.setHAlign(panel, "center");
			ui.setVAlign(panel, "center");
			ui.setWeight(panel, 1, 1);
			ui.add(panel,button);
			ui.add(outerPanel,panel);
		}
	}
	
	public void createFormSeries(){
		createFormSeries((MedicForm) ui.getAttachedObject(ui.getSelectedItem(patientViewFormList)));
	}
	
	public void createFormSeries(MedicForm form){
		form.setSeries(form.getName());
		patientViewFormDao.updateMedicForm(form);
		Object outerPanel = find("outerSeriesPanel");
		ui.removeAll(outerPanel);
		FormSeriesPanelController panel = new FormSeriesPanelController(ui, appCon,form,this);
		ui.add(outerPanel,panel.getMainPanel());
	}
	
	/**
	 * Called when the field list selection is changed.
	 */
	public void fieldListSelectionChanged(){
		MedicFormField field = (MedicFormField) ui.getAttachedObject(ui.getSelectedItem(fieldList));
		populateFieldMappingPanel(field);
	}
	
	/**
	 * Populates the field list with the fields of the form that is passed into
	 * this method
	 * @param form
	 */
	private void populateFieldList(MedicForm form){
		MedicFormField currentField = (MedicFormField) ui.getAttachedObject(ui.getSelectedItem(fieldList));
		int previousIndex = ui.getSelectedIndex(fieldList);
		if(currentField != null){
			MedicForm currentForm = currentField.getForm();
			if(currentForm != null && currentForm.getFid() != form.getFid()){
				previousIndex = -1;
			}
		}
		ui.setText(find("fieldListPanel"), getI18nString(FIELDS_ON_FORM_PREFIX)+ " \"" + form.getName()+"\"");
		removeAll(fieldList);
		for(MedicFormField mff: patientViewFieldDao.getFieldsOnForm(form)){
			Object item = ui.createListItem(mff.getLabel(), mff);
			if(mff.getMapping() != null){
				ui.setIcon(item, mff.getMapping().getIconPath());
			}
			add(fieldList,item);
		}
		if(previousIndex >0){
			ui.setSelectedIndex(fieldList, previousIndex);
		}else{
			ui.setSelectedIndex(fieldList, 0);
		}
		fieldListSelectionChanged();
	}
	
	/**
	 * Called when the field selection is changed. Populates the field mapping panel 
	 * with the combo box and selects the propper mapping choice based on the value stored
	 * @param field
	 */
	private void populateFieldMappingPanel(MedicFormField field){
		removeAll(mappingComboBox);
		ui.setAction(mappingComboBox, "mappingComboBoxSelectionChanged()", null, this);
		add(mappingComboBox,ui.createComboboxChoice(getI18nString("common.blank"),null));
		ui.setSelectedIndex(mappingComboBox,0);
		ui.setText(mappingComboBox, getI18nString("common.blank"));
		MedicForm form = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(patientViewFormList));
		for(int i = 0; i < form.getType().fields.length; i++){
			PatientFieldMapping m = form.getType().fields[i];
			Object choice = ui.createComboboxChoice(m.toString(), m);
			ui.setIcon(choice, m.getIconPath());
			add(mappingComboBox,choice);
			if(field.getMapping() == m){
				ui.setSelectedIndex(mappingComboBox, i+1);
				ui.setText(mappingComboBox, m.toString());
			}
		}
		if(field.getMapping() != null){
			ui.setIcon(mappingComboBox, field.getMapping().getIconPath());
		}else{
			ui.setIcon(mappingComboBox, "");
		}
	}
	
	public void formTypeComboBoxSelectionChanged(){
		MedicFormType mapping = (MedicFormType) ui.getAttachedObject(ui.getSelectedItem(formTypeComboBox));
		MedicForm form= (MedicForm) ui.getAttachedObject(ui.getSelectedItem(patientViewFormList));
		form.setType(mapping,true);
		ui.setAttachedObject(ui.getSelectedItem(patientViewFormList), form);
		patientViewFormDao.updateMedicForm(form);
		patientViewFormListSelectionChanged();
	}
	
	private void populateFormTypeSelect(MedicForm form){
		removeAll(formTypeComboBox);
		ui.setAction(formTypeComboBox, "formTypeComboBoxSelectionChanged()", null, this);
		for(int i = 0; i < MedicFormType.values().length; i++){
			MedicFormType m = MedicFormType.values()[i];
			Object choice = ui.createComboboxChoice(m.toString(), m);
			add(formTypeComboBox,choice);
			if(form.getType() == m){
				ui.setSelectedIndex(formTypeComboBox, i);
				ui.setText(formTypeComboBox, m.toString());
			}
		}
	}
	
	/**
	 * Called when the mapping combo box selection is changed. It saves the new mapping selection
	 */
	public void mappingComboBoxSelectionChanged(){
		PatientFieldMapping mapping = (PatientFieldMapping) ui.getAttachedObject(ui.getSelectedItem(mappingComboBox));
		MedicFormField field = (MedicFormField) ui.getAttachedObject(ui.getSelectedItem(fieldList));
		field.setMapping(mapping);
		patientViewFieldDao.updateField(field);
		Object item = ui.getSelectedItem(fieldList);
		if(field.getMapping() != null){
			ui.setIcon(item, field.getMapping().getIconPath());
		}else{
			ui.setIcon(item, "");
		}
	}
	
	public void removeButtonClicked() {
		MedicForm mf = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(patientViewFormList));
		if (mf != null) {
			if (((MedicFormResponseDao) appCon.getBean("MedicFormResponseDao")) .getFormResponsesForForm(mf).size() == 0) {
				patientViewFormDao.deleteMedicForm(mf);
				populatePatientViewFormList();
			} else {
				ui.alert(getI18nString(FORM_ALREADY_RESPONDED_TO_DIALG));
			}
		}
	}

	public String getIconPath() {
		return "/icons/big_form.png";
	}
	
	@Override
	public void willAppear() {
		subviewsWillAppear();
		populatePatientViewFormList();
	}

}
