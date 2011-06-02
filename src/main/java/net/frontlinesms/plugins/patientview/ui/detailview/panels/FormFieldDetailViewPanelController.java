package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormFieldDetailViewPanelController extends DetailViewPanelController<FormField> {

	public FormFieldDetailViewPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon);
	}

	public Class getEntityClass() {
		return null;
	}

	public Object getMainPanel() {
		return null;
	}

	public void willAppear(FormField field) {

	}
}
