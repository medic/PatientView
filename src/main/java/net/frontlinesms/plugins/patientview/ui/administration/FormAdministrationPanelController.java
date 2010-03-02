package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.Collection;
import java.util.Iterator;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class FormAdministrationPanelController implements
		AdministrationTabPanel, ThinletUiEventHandler {

	private static final String FORM_PANEL_TITLE = "admin.tabs.form.panel.title";
	
	private static final String FORM_PANEL_XML = "/ui/plugins/patientview/admintab/manageFormsPanel.xml";
	
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	//Thinlet objects
	/**The main Thinlet container for this panel */
	private Object mainPanel;
	/**The list farthest to the left, with all core Frontline Forms in it*/
	private Object frontlineFormList;
	/**The list second from the left, with all patient view forms in it */
	private Object patientViewFormList;
	/** The list farthest to the right, with the fields of the currently selected medic form in it*/
	private Object fieldList;
	/** The panel that displays information about the field that is selected in the field list*/
	private Object fieldInfoPanel;
	
	/** The combo box that holds the choices for the form field -> patient field mapping*/
	private Object mappingComboBox;
	
	
	//Daos
	FormDao frontlineFormDao;
	MedicFormDao patientViewFormDao;
	MedicFormFieldDao patientViewFieldDao;
	public String getListItemTitle() {
		return InternationalisationUtils.getI18NString(FORM_PANEL_TITLE);
	}
	
	public FormAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
	}
	
	private void init(){
		mainPanel = uiController.loadComponentFromFile(FORM_PANEL_XML, this);
		frontlineFormList = uiController.find(mainPanel,"frontlineFormList");
		patientViewFormList = uiController.find(mainPanel,"patientViewFormList");
		fieldList = uiController.find(mainPanel,"fieldList");
		fieldInfoPanel = uiController.find(mainPanel,"fieldInfoPanel");
		mappingComboBox = uiController.find(mainPanel,"mappingComboBox");
		//initialize the daos
		frontlineFormDao = (FormDao) appCon.getBean("formDao");
		patientViewFormDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		patientViewFieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		//initialize the lists, etc..
		populateFrontlineFormList();
		populatePatientViewFormList();
	}
	
	
	/**
	 * Gets all FrontlineSMS forms, removes the ones that already
	 * have a patient view form match, and then places them in the list
	 */
	private void populateFrontlineFormList(){
		Collection<Form> frontlineForms = frontlineFormDao.getAllForms();
		Collection<MedicForm> pvForms = patientViewFormDao.getAllMedicForms();
		for(MedicForm mf : pvForms){
			patientViewFormDao.reattach(mf);
			frontlineForms.remove(mf.getForm());
		}
		uiController.removeAll(frontlineFormList);
		for(Form f: frontlineForms){
			Object item = uiController.createListItem(f.getName(), f);
			uiController.add(frontlineFormList,item);
		}
		uiController.setSelectedIndex(frontlineFormList, 0);
	}
	
	/**
	 * Gets all forms in the Patient View system and displays them in the
	 * proper list
	 */
	private void populatePatientViewFormList(){
		Collection<MedicForm> pvForms = patientViewFormDao.getAllMedicForms();
		uiController.removeAll(patientViewFormList);
		for(MedicForm f: pvForms){
			Object item = uiController.createListItem(f.getName(), f);
			uiController.add(patientViewFormList,item);
		}
		uiController.setSelectedIndex(patientViewFormList, 0);
		patientViewFormListSelectionChanged();
	}
	
	/**
	 * Called when the user makes a selection in the list of
	 * Patient View forms. Gets the form that was selected
	 * and changes the fields that are displayed in the field list
	 */
	public void patientViewFormListSelectionChanged(){
		MedicForm selectedForm = (MedicForm) uiController.getAttachedObject(uiController.getSelectedItem(patientViewFormList));
		populateFieldList(selectedForm);
	}
	
	/**
	 * Called when the field list selection is changed.
	 */
	public void fieldListSelectionChanged(){
		MedicFormField field = (MedicFormField) uiController.getAttachedObject(uiController.getSelectedItem(fieldList));
		populateFieldMappingPanel(field);
	}
	
	/**
	 * Populates the field list with the fields of the form that is passed into
	 * this method
	 * @param form
	 */
	private void populateFieldList(MedicForm form){
		uiController.removeAll(fieldList);
		for(MedicFormField mff: patientViewFieldDao.getFieldsOnForm(form)){
			Object item = uiController.createListItem(mff.getLabel(), mff);
			uiController.add(fieldList,item);
		}
		uiController.setSelectedIndex(fieldList, 0);
		fieldListSelectionChanged();
	}
	
	/**
	 * Called when the field selection is changed. Populates the field mapping panel 
	 * with the combo box and selects the propper mapping choice based on the value stored
	 * @param field
	 */
	private void populateFieldMappingPanel(MedicFormField field){
		uiController.removeAll(mappingComboBox);
		uiController.setAction(mappingComboBox, "mappingComboBoxSelectionChanged()", null, this);
		uiController.add(mappingComboBox,uiController.createComboboxChoice("None",null));
		uiController.setSelectedIndex(mappingComboBox,0);
		uiController.setText(mappingComboBox, "None");
		for(int i = 0; i < PatientFieldMapping.values().length; i++){
			PatientFieldMapping m = PatientFieldMapping.values()[i];
			Object choice = uiController.createComboboxChoice(m.toString(), m);
			uiController.add(mappingComboBox,choice);
			if(field.getMapping() == m){
				uiController.setSelectedIndex(mappingComboBox, i+1);
				uiController.setText(mappingComboBox, m.toString());
			}
		}
	}
	
	/**
	 * Called when the mapping combo box selection is changed. It saves the new mapping selection
	 */
	public void mappingComboBoxSelectionChanged(){
		PatientFieldMapping mapping = (PatientFieldMapping) uiController.getAttachedObject(uiController.getSelectedItem(mappingComboBox));
		MedicFormField field = (MedicFormField) uiController.getAttachedObject(uiController.getSelectedItem(fieldList));
		field.setMapping(mapping);
		patientViewFieldDao.updateField(field);
	}

	public Object getPanel() {
		return mainPanel;
	}

}