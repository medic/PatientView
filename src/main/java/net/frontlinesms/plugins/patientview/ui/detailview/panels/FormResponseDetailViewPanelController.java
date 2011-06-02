package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class FormResponseDetailViewPanelController extends DetailViewPanelController<MedicFormResponse> {

	private MedicFormFieldResponseDao fieldResponseDao;
	private MedicFormFieldDao formFieldDao;
	
	private static final String FORM_RESPONSE_PANEL = "/ui/plugins/patientview/components/formPanel.xml";
	//i18n
	private static final String FORM = "medic.common.form";
	private static final String SUBJECT = "medic.common.labels.subject";
	private static final String SUBMITTER = "medic.common.labels.submitter";
	private static final String DATE_SUBMITTED = "medic.common.labels.date.submitted";
	
	public FormResponseDetailViewPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController,appCon,FORM_RESPONSE_PANEL);
		this.fieldResponseDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		this.formFieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
	}
	
	public Class<MedicFormResponse> getEntityClass() {
		return MedicFormResponse.class;
	}

	public void willAppear(MedicFormResponse response) {
		//set the header information
		if(response == null){
			return;
		}
		String form = getI18nString(FORM) + ": " + response.getForm().getName();
		String submitter;
		try{
			submitter = getI18nString(SUBMITTER) + ": " + response.getSubmitter().getName();
		}catch(Exception e){
			submitter = getI18nString(SUBMITTER) + ": "+ getI18nString("medic.common.labels.unknown");
		}
		String subject;
		try{
			subject = getI18nString(SUBJECT) + ": " + response.getSubjectName();
		}catch(Exception e){
			subject = getI18nString(SUBJECT) + ": " + getI18nString("medic.common.labels.unknown");
		}
		DateFormat df = InternationalisationUtils.getDateFormat();
		String date = getI18nString(DATE_SUBMITTED) + " " + df.format(response.getDateSubmitted());
		ui.setText(find("nameLabel"),  form);
		ui.setText(find("submitterLabel"),  submitter);
		ui.setText(find("dateSubmittedLabel"),  date);
		ui.setText(find("subjectLabel"), subject);
		Object fieldContainer = find("fieldPanel");
		removeAll(fieldContainer);
		//get all the responses
		ArrayList<String> responses = new ArrayList<String>();
		List<MedicFormFieldResponse> fieldResponses = fieldResponseDao.getResponsesForFormResponse(response);
		for(MedicFormFieldResponse r: fieldResponses){
			responses.add(r.getValue());
		}
		//iterate over them, displaying them in the proper fashion
		Iterator<String> responseIt = responses.iterator();
		List<MedicFormField> fields = formFieldDao.getFieldsOnForm(response.getForm());
		for(MedicFormField ff: fields){
			Object field = null;
			if(ff.getDatatype() == DataType.CHECK_BOX){
				field =ui.createCheckbox(null, ff.getLabel(), false);
				add(fieldContainer,field);
				ui.setEnabled(field, false);
				ui.setInteger(field, "weightx", 1);
				ui.setChoice(field, "halign", "fill");
				String r = responseIt.next();
				if(r.equalsIgnoreCase("true")){
					ui.setSelected(field, true);
				}
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				field = Thinlet.create("textarea");
				Object field2 = ui.createLabel(ff.getLabel());
				add(fieldContainer,field2);
				add(fieldContainer,field);
				ui.setEditable(field, false);
				ui.setInteger(field, "weightx", 1);
				ui.setChoice(field, "halign", "fill");
				ui.setChoice(field2, "halign","left");
				ui.setText(field,responseIt.next());
			}else if(ff.getDatatype() == DataType.TRUNCATED_TEXT ||
					ff.getDatatype() == DataType.WRAPPED_TEXT){
				field = ui.createLabel(ff.getLabel());
				add(fieldContainer,field);
				ui.setChoice(field, "halign", "center");
			}else{
				field = ui.createTextfield(null, "");
				Object field2 = ui.createLabel(ff.getLabel());
				add(fieldContainer,field2);
				add(fieldContainer,field);
				ui.setEditable(field, false);
				ui.setInteger(field, "weightx", 1);
				ui.setChoice(field, "halign", "fill");
				ui.setChoice(field2, "halign", "center");
				ui.setText(field, responseIt.next());
			}
		}
		subviewsWillAppear();
	}

}
