package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;
import thinlet.Thinlet;

public class FormDetailViewPanelController extends DetailViewPanelController<MedicForm> {
	
	private static final String FORM_PANEL = "/ui/plugins/patientview/components/formPanel.xml";
	
	public FormDetailViewPanelController(UiGeneratorController uiController){
		super(uiController,null,FORM_PANEL);
		remove(find("submitterLabel"));
		remove(find("subjectLabel"));
		remove(find("dateSubmittedLabel"));
	}
	/**
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#getEntityClass()
	 */
	@Override
	public Class<MedicForm> getEntityClass() {
		return MedicForm.class;
	}
	
	/**
	 * Populates the main panel with a picture of the form. 
	 * @see net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController#willAppear(java.lang.Object)
	 */
	@Override
	public void willAppear(MedicForm form) {
		ui.setText(find("nameLabel"), form.getName());
		Object fieldContainer = find("fieldPanel");
		removeAll(fieldContainer);
		for(MedicFormField ff: form.getFields()){
			Object field = null;
			if(ff.getDatatype() == DataType.CHECK_BOX){
				field =ui.createCheckbox(null, ff.getLabel(), false);
				ui.add(fieldContainer,field);
				ui.setEnabled(field,false);
				ui.setInteger(field, "weightx", 1);
				ui.setChoice(field, "halign", "fill");
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				field = Thinlet.create("textarea");
				Object field2 = ui.createLabel(ff.getLabel());
				ui.add(fieldContainer,field2);
				ui.add(fieldContainer,field);
				ui.setEditable(field,false);
				ui.setInteger(field, "weightx", 1);
				ui.setChoice(field, "halign", "fill");
				ui.setChoice(field2, "halign","left");
			}else if(ff.getDatatype() == DataType.TRUNCATED_TEXT ||
					ff.getDatatype() == DataType.WRAPPED_TEXT){
				field = ui.createLabel(ff.getLabel());
				ui.add(fieldContainer,field);
				ui.setChoice(field, "halign", "center");
			}else{
				field = ui.createTextfield(null, "");
				Object field2 = ui.createLabel(ff.getLabel());
				ui.add(fieldContainer,field2);
				ui.add(fieldContainer,field);
				ui.setEditable(field,false);
				ui.setInteger(field, "weightx", 1);
				ui.setChoice(field, "halign", "fill");
				ui.setChoice(field2, "halign", "center");
			}
		}
		subviewsWillAppear();
	}
}
