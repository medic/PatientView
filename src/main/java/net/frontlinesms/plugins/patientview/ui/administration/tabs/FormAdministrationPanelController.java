package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.Collection;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.ApplicationContext;

public class FormAdministrationPanelController extends AdministrationTabPanel{
	
	/* i18n */
	private static final String FORM_PANEL_TITLE = "admin.tabs.form.panel.title";
	private static final String FIELDS_ON_FORM_PREFIX = "admin.forms.fields.on.form.prefix";
	private static final String FORM_ALREADY_RESPONDED_TO_DIALG = "admin.forms.form.already.responded.to.dialog";
	
	private static final String FORM_PANEL_XML = "/ui/plugins/patientview/administration/formAdministrationPanel.xml";
	
	/* Thinlet objects */
	/**The main Thinlet container for this panel */
	private Object mainPanel;
	/**The list second from the left, with all patient view forms in it */
	private Object patientViewFormList;
	/** The list farthest to the right, with the fields of the currently selected medic form in it*/
	private Object fieldList;
	
	/** The combo box that holds the choices for the form field -> patient field mapping*/
	private Object mappingComboBox;
	
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
		if(selectedForm != null)
			populateFieldList(selectedForm);
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
		ui.setText(find("fieldListTitle"), getI18nString(FIELDS_ON_FORM_PREFIX)+ " \"" + form.getName()+"\"");
		removeAll(fieldList);
		for(MedicFormField mff: patientViewFieldDao.getFieldsOnForm(form)){
			Object item = ui.createListItem(mff.getLabel(), mff);
			if(mff.getMapping() != null){
				ui.setIcon(item, mff.getMapping().getIconPath());
			}
			add(fieldList,item);
		}
		ui.setSelectedIndex(fieldList, 0);
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
		for(int i = 0; i < PatientFieldMapping.values().length; i++){
			PatientFieldMapping m = PatientFieldMapping.values()[i];
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
