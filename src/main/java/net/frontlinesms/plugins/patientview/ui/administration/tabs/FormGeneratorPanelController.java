package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.responsemapping.RandomFormGenerator;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormResponseDetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormGeneratorPanelController extends AdministrationTabPanel{

	private static final String UI_XML = "/ui/plugins/patientview/administration/formgenerator/formGenerator.xml";

	private Object formSelect;
	private Object patientSelect;
	private RandomFormGenerator formGenerator;
	
	public FormGeneratorPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon,UI_XML);
		FormDao formDao = (FormDao) this.appCon.getBean("formDao");
		PatientDao patientDao = (PatientDao) this.appCon.getBean("PatientDao");
		formSelect = super.find("formSelect");
		patientSelect = super.find("patientSelect");
		Collection<Form> forms = formDao.getAllForms();
		for(Form form : forms){
			Object item = ui.createComboboxChoice(form.getName(), form);
			ui.add(formSelect,item);
		}
		List<Patient> patients = patientDao.getAllPatients();
		for(Patient patient : patients){
			Object item = ui.createComboboxChoice(patient.getName(), patient);
			ui.add(patientSelect,item);
		}
		formGenerator = new RandomFormGenerator(appCon);
	}

	public void generate(){
		Form form = (Form) ui.getAttachedObject(ui.getSelectedItem(formSelect));
		Patient patient = (Patient) ui.getAttachedObject(ui.getSelectedItem(patientSelect));
		formGenerator.generateRandomFormResponse(form, patient);
	}
	
	@Override
	public String getIconPath() {
		return "";
	}

	@Override
	public String getListItemTitle() {
		return "Form Generator";
	}

}
