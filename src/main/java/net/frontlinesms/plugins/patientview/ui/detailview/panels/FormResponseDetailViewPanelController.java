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
	private boolean showHeader = true;
	
	private static final String FORM_RESPONSE_PANEL = "/ui/plugins/patientview/components/formPanel.xml";
	//i18n
	private static final String FORM = "medic.common.form";
	
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
		if(showHeader){
			ui.setVisible(find("nameLabel"), true);
			ui.setVisible(find("submitterLabel"), true);
			ui.setVisible(find("dateSubmittedLabel"), true);
			String form = getI18nString(FORM) + ": " + response.getForm().getName();
			String submitter;
			try{
				submitter = "Submitted by " + response.getSubmitter().getName();
			}catch(Exception e){
				submitter = "Submitted by "+ getI18nString("medic.common.labels.unknown");
			}
			DateFormat df = InternationalisationUtils.getDateFormat();
			String date = df.format(response.getDateSubmitted());
			ui.setText(find("nameLabel"),  form);
			ui.setText(find("submitterLabel"),  submitter);
			ui.setText(find("dateSubmittedLabel"),  date);
		}else{
			ui.setVisible(find("nameLabel"), false);
			ui.setVisible(find("submitterLabel"), false);
			ui.setVisible(find("dateSubmittedLabel"), false);
		}
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

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public boolean isShowHeader() {
		return showHeader;
	}

}
