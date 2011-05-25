package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute.PersonType;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AttributeAdministrationPanelController extends AdministrationTabPanel implements ThinletUiEventHandler, TableActionDelegate {
	
	private static final String PANEL_TITLE = "admin.attributes.panel.title";
	
	public String getListItemTitle() {
		return getI18nString(PANEL_TITLE);
	}
	
	@Override
	public String getIconPath() {
		return "/icons/patient_data_card.png";
	}

	/**
	 * Boolean to signify whether we are 
	 * editing the screen for patients (true) 
	 * or chws (false) 
	 **/
	private boolean currentlyEditingPatient;
	
	/*Thinlet Objects*/
	private Object fieldSearchTable;
	private Object fieldSearchBar;
	private Object labelTextField;
	private Object currentItemTable;
	private Object dataTypeComboBox;
	
	/*Table Controllers*/
	private AdvancedTableController fieldSearchTableController;
	private AdvancedTableController currentItemTableController;
	
	
	/*DAOs*/
	private PersonAttributeDao attributeDao;
	private PersonAttributeResponseDao attributeResponseDao;
	private MedicFormFieldDao formFieldDao;
	
	/*resource files containing ui components*/
	private static final String UI_FILE_AAG_VIEW_EDITOR = "/ui/plugins/patientview/administration/attributeAdministrationPanel.xml";
	
	/*i18n*/
	private static final String LABEL_COLUMN = "medic.common.labels.label";
	private static final String PARENT_FORM_COLUMN = "medic.common.labels.parent.form";
	private static final String DATA_TYPE_COLUMN = "datatype.datatype";
	private static final String ALREADY_RESPONDED_TO_DIALOG = "admin.attributes.responses.already.present.dialog";
	private static final String ATTRIBUTE_INFO_MISSING_DIALOG = "admin.attributes.fields.not.filled.out.dialog";
	
	public AttributeAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appContext){
		super(uiController, appContext,UI_FILE_AAG_VIEW_EDITOR);
		//initialize all the uiController components
		fieldSearchTable = find("fieldSearchTable");
		fieldSearchBar = find("fieldSearchBar");
		labelTextField = find("labelTextField");
		currentItemTable = find("currentItemList");
		dataTypeComboBox = find("dataTypeComboBox");
		
		//initialize the advanced tables
		fieldSearchTableController = new AdvancedTableController(this,uiController,fieldSearchTable);
		currentItemTableController = new AdvancedTableController(this,uiController,currentItemTable);
		
		fieldSearchTableController.putHeader(MedicFormField.class, HeaderColumn.createColumnList(new String[]{getI18nString(LABEL_COLUMN), getI18nString(PARENT_FORM_COLUMN)},
																								 new String[]{"/icons/tag_purple.png", "/icons/form.png"},
																								 new String[]{"getLabel","getParentFormName" }));
		
		currentItemTableController.putHeader(Field.class, HeaderColumn.createColumnList(new String[]{getI18nString(LABEL_COLUMN), getI18nString(DATA_TYPE_COLUMN)},
														  new String[]{"/icons/tag_purple.png", "/icons/page_white_star.png"},
														  new String[]{"getLabel","getDataTypeName"}));
		currentItemTableController.setNoResultsMessage(getI18nString("admin.attributes.advancedtable.no.results.message"));
		//initialize the combo box choices
		removeAll(dataTypeComboBox);
		for(DataType d: DataType.values()){
			if(!(d.equals(DataType.CURRENCY_FIELD) || 
				d.equals(DataType.EMAIL_FIELD) || 
				d.equals(DataType.PASSWORD_FIELD) || 
				d.equals(DataType.WRAPPED_TEXT) || 
				d.equals(DataType.TRUNCATED_TEXT))){
			Object choice = uiController.createComboboxChoice(d.toString(), d);
			ui.setIcon(choice, d.getIconPath());
			add(dataTypeComboBox,choice);
			}
		}
		//initialize the DAOs
		attributeResponseDao = (PersonAttributeResponseDao) appCon.getBean("PersonAttributeResponseDao");
		attributeDao = (PersonAttributeDao) appCon.getBean("PersonAttributeDao");
		formFieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		//setting up the toggle button
		patientToggleButtonClicked();
		ui.setSelected(find("patientToggle"), true);
		ui.setSelected(find("chwToggle"), false);
		fieldSearchBarKeyPress("");
	}
	
	/**
	 * Switches from editing chw attributes to editing patient attributes
	 */
	public void patientToggleButtonClicked(){
		currentlyEditingPatient=true;
		ui.setEnabled(fieldSearchTable,true);
		ui.setEnabled(fieldSearchBar,true);
		ui.setEnabled(find("fieldSearchLabel"),true);
		updateCurrentItemTable();
	}
	
	/**
	 * Switches from editing Patient attributes to editing CHW attributes
	 */
	public void chwToggleButtonClicked(){
		currentlyEditingPatient=false;
		ui.setEnabled(fieldSearchTable,false);
		ui.setEnabled(fieldSearchBar,false);
		ui.setEnabled(find("fieldSearchLabel"),false);
		ui.setText(fieldSearchBar, "");
		updateCurrentItemTable();
	}
	
	/**
	 * Called by thinlet when the text in the field search box changes,
	 * updates the field search list
	 * @param text
	 */
	public void fieldSearchBarKeyPress(String text){
		updateFieldSearchTable(formFieldDao.findFieldsByLabel(text));
		ui.setText(labelTextField, "");
		ui.setText(dataTypeComboBox, "");
		ui.setSelectedIndex(dataTypeComboBox, -1);
	}
	
	private void updateFieldSearchTable(List<MedicFormField> fields){
		fieldSearchTableController.setResults(fields);
	}
	
	/**
	 * Called by thinlet when the add button is pressed
	 * Makes sure all the required information is available, and 
	 * then adds the attribute, or directs the user to enter more data
	 */
	public void addItemButtonPressed(){
		String label = ui.getText(labelTextField);
		if((label != "" && label !=null) && ui.getSelectedItem(dataTypeComboBox) != null){
			DataType dataType = (DataType) ui.getAttachedObject(ui.getSelectedItem(dataTypeComboBox));
			PersonType personType = (currentlyEditingPatient) ? PersonType.PATIENT : PersonType.CHW;
			PersonAttribute newAttribute = new PersonAttribute(label,dataType);
			newAttribute.setPersonType(personType);
			attributeDao.saveAttribute(newAttribute);
		}else if(fieldSearchTableController.getCurrentlySelectedObject() != null){
			MedicFormField field = (MedicFormField) fieldSearchTableController.getCurrentlySelectedObject();
			field.setIsAttributePanelField(true);
			formFieldDao.updateField(field);
		}else{
			ui.createDialog(getI18nString(ATTRIBUTE_INFO_MISSING_DIALOG));
		}
		updateCurrentItemTable();
		clearInputs();
	}
	
	private void clearInputs(){
		ui.setText(labelTextField, "");
		ui.setText(dataTypeComboBox, "");
		ui.setSelectedIndex(dataTypeComboBox, -1);
		ui.setText(fieldSearchBar, "");
		ui.setIcon(dataTypeComboBox,"");
		fieldSearchBarKeyPress("");
	}
	
	public void removeItemButtonPressed(){
		Object field = currentItemTableController.getCurrentlySelectedObject();
		if(field instanceof MedicFormField){
			((MedicFormField) field).setIsAttributePanelField(false);
			formFieldDao.updateField((MedicFormField) field);
		}else if(field instanceof PersonAttribute){
			if(attributeResponseDao.getResponsesForAttribute((PersonAttribute) field).size() == 0){
				attributeDao.deleteAttribute((PersonAttribute) field);
			}else{
				ui.alert(getI18nString(ALREADY_RESPONDED_TO_DIALOG));
			}
		}
		updateCurrentItemTable();
	}
	
	private void updateCurrentItemTable(){
		if(currentlyEditingPatient){
			List<Field> patientFields = new ArrayList<Field>();
			patientFields.addAll(formFieldDao.getAttributePanelFields());
			patientFields.addAll(attributeDao.getAttributesForPersonType(PersonType.PATIENT));
			currentItemTableController.setResults(patientFields);
		} else{
			currentItemTableController.setResults(attributeDao.getAttributesForPersonType(PersonType.CHW));
		}
	}
	
	public void fieldSearchTableSelectionChanged(){
		ui.setText(fieldSearchBar, ((MedicFormField) ui.getAttachedObject(ui.getSelectedItem(fieldSearchTable))).getLabel());
	}

	/* TableActionDelegate methods */
	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}
	public void selectionChanged(Object selectedObject) {}
	
	/* ViewHandler methods*/
	@Override
	public void viewWillAppear() {}
	@Override
	public void viewWillDisappear() {}
}
