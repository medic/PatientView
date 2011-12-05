package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.RegistrationFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.search.impl.FormMappingResultSet;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormResponseDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanelDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.PatientFieldGroup;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

public class PatientRegistrationPanelController extends AdministrationTabPanel implements ThinletUiEventHandler, TableActionDelegate, PersonPanelDelegate{

	private FormMappingResultSet resultSet;
	private PagedAdvancedTableController table;
	private FormResponseDetailViewPanelController formResponsePanel;
	private MedicFormResponseDao formDao;
	private MedicFormFieldResponseDao fieldDao;
	private static final String UI_XML = "/ui/plugins/patientview/administration/patientRegistrationPanel.xml";
	
	public PatientRegistrationPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon,UI_XML);
		fieldDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		formDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		formResponsePanel = new FormResponseDetailViewPanelController(ui, appCon);
		formResponsePanel.setShowHeader(false);
		add(find("regPanel"),formResponsePanel.getMainPanel());
		resultSet = new FormMappingResultSet(appCon);
		resultSet.setSearchingRegForms(true);
		table = new PagedAdvancedTableController(this, ui);
		List<HeaderColumn> columnList = new ArrayList<HeaderColumn>();
		columnList.add(new HeaderColumn("getFormName","/icons/form.png","Form"));
		columnList.add(new HeaderColumn("getSubmitterName","/icons/user.png","Submitter"));
		columnList.add(new HeaderColumn("getStringDateSubmitted","/icons/date.png","Date Submitted"));
		table.putHeader(MedicFormResponse.class,columnList);
		table.setResultsSet(resultSet);
		table.refresh();
		add(find("tablePanel"),table.getMainPanel());
	}

	@Override
	public String getIconPath() {
		return "/icons/patient_add_female.png";
	}

	@Override
	public String getListItemTitle() {
		return "Register Patients";
	}

	public void willAppear(){
		int selectedIndex = table.getSelectedIndex();
		table.refresh();
		table.setSelected(selectedIndex);
		super.willAppear();
	}
	
	public void doubleClickAction(Object selectedObject) {
		//do nothing
	}

	public void resultsChanged() {
		//do nothing
	}

	public void selectionChanged(Object selectedObject) {
		removeAll(find("regPanel"));
		if(selectedObject == null) return;
		PatientFieldGroup pfg = formToFields((MedicFormResponse) selectedObject);
		PatientPanel p = new PatientPanel(ui,appCon,pfg,this);
		add(find("regPanel"),p.getMainPanel());
		add(find("regPanel"),formResponsePanel.getMainPanel());
		formResponsePanel.willAppear((MedicFormResponse) selectedObject);
	}

	private PatientFieldGroup formToFields(MedicFormResponse form){
		PatientFieldGroup result;
		String firstname="", lastname="";
		Gender gender = null;
		long birthdate = 0L;
		int age=-1;
		long amenDateOfConception = 0L;
		long lmpDateOfConception = 0L;
		boolean isNewborn = false;
		if(form.getForm().isChildRegistrationForm()){
			isNewborn = true;
			List<MedicFormFieldResponse> fields = fieldDao.getResponsesForFormResponse(form);
			for(MedicFormFieldResponse mfr: fields){
				if(mfr.getField() == null){
					continue;
				}
				if(mfr.getField().getRegMapping() == RegistrationFieldMapping.FIRSTNAME){
					firstname = mfr.getValue();
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.LASTNAME){
					lastname = mfr.getValue();
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.GENDER){
					gender = Gender.stringToGender(mfr.getValue());
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.BIRTHDATE){
					try{
						birthdate = InternationalisationUtils.getDateFormat().parseMillis(mfr.getValue());
					}catch(Throwable t){
						birthdate = 0L;
					}
				}
			}
		}else if(form.getForm().isMotherRegistrationForm()){
			isNewborn = false;
			gender = Gender.FEMALE;
			List<MedicFormFieldResponse> fields = fieldDao.getResponsesForFormResponse(form);
			for(MedicFormFieldResponse mfr: fields){
				
				if(mfr.getField() == null){
					continue;
				}
				if(mfr.getField().getRegMapping() == RegistrationFieldMapping.FIRSTNAME){
					firstname = mfr.getValue();
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.LASTNAME){
					lastname = mfr.getValue();
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.GENDER){
					gender = Gender.stringToGender(mfr.getValue());
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.BIRTHDATE){
					try{
						birthdate = InternationalisationUtils.getDateFormat().parseMillis(mfr.getValue());
					}catch(Throwable t){
						birthdate = 0L;
					}
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.AGE){
					try{
						age = Integer.parseInt(mfr.getValue());
					}catch(Throwable t){}
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.MONTHOFAMENORIA){
					int months;
					try{
						months = Integer.parseInt(mfr.getValue());
					}catch(Throwable t){ continue;}
					double weeksRemaining = 40.0 - ((30.0 * months)/7.0);
					DateTime current = DateTime.now(InternationalisationUtils.ethiopicChronology);
					amenDateOfConception = current.plusWeeks(TimeUtils.safeLongToInt(Math.round(weeksRemaining))).getMillis();
				}else if(mfr.getField().getRegMapping() == RegistrationFieldMapping.LASTMENSTRALCYCLE){
					long lastMens;
					try{
						lastMens = InternationalisationUtils.getDateFormat().parseMillis(mfr.getValue());
					}catch(Throwable t){ continue;}
					DateTime done = new DateTime(lastMens,InternationalisationUtils.ethiopicChronology);
					lmpDateOfConception = done.plusWeeks(40).getMillis();
				}
			}
		}
		
		if(lmpDateOfConception == 0L && amenDateOfConception > 0L){
			lmpDateOfConception = amenDateOfConception;
		}
		if(birthdate == 0L && age > 0){
			DateTime cur = DateTime.now(InternationalisationUtils.ethiopicChronology);
			birthdate = cur.minusYears(age).getMillis();
		}
		result = new PatientFieldGroup(ui, appCon, null, firstname + " " + lastname, gender, birthdate, lmpDateOfConception, isNewborn);
		return result;
	}

	public void didCreatePerson() {
		MedicFormResponse mfr = (MedicFormResponse) table.getCurrentlySelectedObject();
		mfr.setRegistered(true);
		formDao.updateMedicFormResponse(mfr);
		int index = table.getSelectedIndex();
		table.refresh();
		table.setSelected(index);
	}
}
