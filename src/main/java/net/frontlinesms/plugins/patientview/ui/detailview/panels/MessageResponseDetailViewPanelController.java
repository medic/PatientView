package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import java.text.DateFormat;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import thinlet.Thinlet;

public class MessageResponseDetailViewPanelController extends DetailViewPanelController<MedicMessageResponse> {

	private static final String MESSAGE_RESPONSE_PANEL = "/ui/plugins/patientview/components/formPanel.xml";
	//i18n
	private static final String SUBMITTED_BY = "detailview.labels.submitted.by";
	private static final String ON = "medic.common.on";
	
	public MessageResponseDetailViewPanelController(UiGeneratorController uiController){
		super(uiController, null, MESSAGE_RESPONSE_PANEL);
	}
	
	public Class<MedicMessageResponse> getEntityClass() {
		return MedicMessageResponse.class;
	}

	@Override
	public void willAppear(MedicMessageResponse message) {
		removeAll();
		DateFormat df = InternationalisationUtils.getDateFormat();
		Object submitterLabel = ui.createLabel(getI18nString(SUBMITTED_BY)+" "+ message.getSubmitter().getName());
		Object dateLabel = ui.createLabel(getI18nString(ON)+" " +  df.format(message.getDateSubmitted()));
		Object textarea = Thinlet.create("textarea");
		ui.setText(textarea, message.getMessageContent());
		ui.setEditable (textarea,false);
		ui.setInteger(textarea, "weightx", 1);
		ui.setInteger(submitterLabel,"weightx",1);
		ui.setInteger(dateLabel,"weightx",1);
		ui.setChoice(submitterLabel,"halign","center");
		ui.setChoice(dateLabel,"halign","center");
		add(submitterLabel);
		add(dateLabel);
		add(textarea);
	}

}
