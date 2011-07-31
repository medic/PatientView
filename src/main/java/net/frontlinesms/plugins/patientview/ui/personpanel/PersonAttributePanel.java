package net.frontlinesms.plugins.patientview.ui.personpanel;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.FieldGroup;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

/**
 * This class handles the display of person attributes in the detail view.
 */
public class PersonAttributePanel {
	
	
	private Object mainPanel;
	
	private boolean inEditingMode;
	
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private Person person;
	
	private FieldGroup fieldGroup;
	
	//Daos
	private PersonAttributeDao attributeDao;
	private PersonAttributeResponseDao attributeResponseDao;
	private MedicFormFieldDao fieldDao;
	private MedicFormFieldResponseDao fieldResponseDao;
	
	
	public PersonAttributePanel(UiGeneratorController uiController, ApplicationContext appCon, Person person){
		this.uiController = uiController;
		this.appCon = appCon;
		this.person = person;
		this.fieldGroup = new FieldGroup(uiController, appCon, null);
		// create the main ui objects
		mainPanel = Thinlet.create("panel");
		uiController.setInteger(mainPanel,"weightx",1);
		//uiController.setInteger(mainPanel,"weighty",1);
		uiController.setInteger(mainPanel, "columns", 1);
		uiController.setInteger(mainPanel, "left", 5);
		uiController.setGap(mainPanel, 10);
		//fetch the DAOs
		attributeDao = (PersonAttributeDao) appCon.getBean("PersonAttributeDao");
		fieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		attributeResponseDao = (PersonAttributeResponseDao) appCon.getBean("PersonAttributeResponseDao");
		fieldResponseDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		//do the main initialization
		initPanel();
	}
	
	/**
	 * Initializes the panel, placing in it all attribute panel fields that the person has
	 * answers for. If they are a Patient, then that includes Form Fields that have been
	 * marked as attribute panel fields
	 */
	private void initPanel(){
		inEditingMode = false;
		uiController.removeAll(mainPanel);
		for(PersonAttribute attribute: attributeDao.getAnsweredAttributesForPerson(person)){
			PersonAttributeResponse response = attributeResponseDao.getMostRecentAttributeResponse(attribute, person);
			if(response.getValue()!= null && !response.getValue().equals("")){
				DataType type = attribute.getDatatype();
				if(type.isBoolean()){
					String value = response.getValue().equalsIgnoreCase(InternationalisationUtils.getI18nString("datatype.true"))? type.getTrueLabel() :type.getFalseLabel();
					uiController.add(mainPanel,getFieldDisplayPanel(value,attribute.getLabel()));
				}else if(type == DataType.TEXT_AREA){
					uiController.add(mainPanel,getTextAreaDisplayPanel(response.getValue(), attribute.getLabel()));
				}else{
					uiController.add(mainPanel,getFieldDisplayPanel(response.getValue(),attribute.getLabel()));
				}
			}
		}
		if(person instanceof Patient){
			for(MedicFormField field: fieldDao.getAnsweredAttributePanelFieldsForPerson(person)){
				MedicFormFieldResponse response = fieldResponseDao.getMostRecentFieldResponse(field, person);
					if(response.getValue()!= null && !response.getValue().equals("")){
					DataType type = field.getDatatype();
					if(type.isBoolean()){
						String value = response.getValue().equalsIgnoreCase(InternationalisationUtils.getI18nString("datatype.true"))? type.getTrueLabel() :type.getFalseLabel();
						uiController.add(mainPanel,getFieldDisplayPanel(value,field.getLabel()));
					}else if(type == DataType.TEXT_AREA){
						uiController.add(mainPanel,getTextAreaDisplayPanel(response.getValue(), field.getLabel()));
					}else{
						uiController.add(mainPanel,getFieldDisplayPanel(response.getValue(),field.getLabel()));
					}
				}
			}
		}
	}
	
	/**
	 * Returns a thinlet panel that displays the response
	 * to a text area field
	 */
	private Object getTextAreaDisplayPanel(String value, String label){
		Object textArea = Thinlet.create("textarea");
		uiController.setInteger(textArea, "weightx",1);
		Object panel = Thinlet.create("panel");
		uiController.setInteger(panel,"columns",1);
		uiController.setWeight(panel, 1, 0);
		uiController.setGap(panel, 4);
		uiController.setEditable(textArea,false);
		uiController.setText(textArea, value);
		uiController.setColspan(panel, 1);
		Object fLabel = uiController.createLabel(label);
		uiController.add(panel,fLabel);
		uiController.add(panel,textArea);
		return panel;
	}
	
	/**
	 * Returns a thinlet panel that displays the response
	 * to any field except a text area field
	 */
	private Object getFieldDisplayPanel(String value, String label){
		Object panel = uiController.createLabel(label +": "+ value);
		uiController.setColspan(panel, 1);
		return panel;
	}
	/**
	 * Removes whatever was previously in the panel and 
	 * adds editable fields for every attribute that the
	 * person could have.
	 */
	public void switchToEditingPanel(){
		uiController.removeAll(mainPanel);
		fieldGroup.removeAll();
		ArrayList<Field> attributes = new ArrayList<Field>();
		attributes.addAll(attributeDao.getAllAttributesForPerson(person));
		if(person instanceof Patient){
			attributes.addAll(fieldDao.getAttributePanelFields());
		}
		for(Field f: attributes){
			ThinletFormField tff = ThinletFormField.getThinletFormFieldForDataType(f.getDatatype(), uiController, appCon, f.getLabel(),null);
			tff.setField(f);
			if(f instanceof PersonAttribute){
				PersonAttributeResponse response= attributeResponseDao.getMostRecentAttributeResponse((PersonAttribute) f, person);
				if(response !=null){
					tff.setStringResponse(response.getValue());
				}
			}else{
				MedicFormFieldResponse mffr =fieldResponseDao.getMostRecentFieldResponse((MedicFormField) f, person); 
				if(mffr !=null){
					tff.setStringResponse(mffr.getValue());
				}
			}
			fieldGroup.addField(tff);	
			uiController.add(mainPanel,fieldGroup.getMainPanel());
		}
		inEditingMode=true;
	}
	
	/**
	 * Checks to see if all the thinlet form fields are valid, have responses, and have changed,
	 * and then creates new response objects and saves them to the database
	 */
	public boolean validateAndSaveResponses(){
		if(!inEditingMode)
			return false;
		
		if(fieldGroup.validate(true)){
			for(ThinletFormField tff: fieldGroup.getFormFields()){
				if(tff.getField() instanceof PersonAttribute){
					PersonAttributeResponse response = new PersonAttributeResponse(tff.getStringResponse(), (PersonAttribute) tff.getField(), person, UserSessionManager.getUserSessionManager().getCurrentUser());
					attributeResponseDao.saveAttributeResponse(response);
				}else if(tff.getField() instanceof MedicFormField){
					MedicFormFieldResponse mffr = new MedicFormFieldResponse(tff.getStringResponse(),(MedicFormField) tff.getField(), person,UserSessionManager.getUserSessionManager().getCurrentUser());
					fieldResponseDao.saveFieldResponse(mffr);
				}
			}
			return true;
		}
		return false;
	}
	
	public void stopEditingWithoutSave(){
		initPanel();
	}
	
	public boolean stopEditingWithSave(){
		if(validateAndSaveResponses()){
			initPanel();
			return true;
		}
		return false;
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	
}
