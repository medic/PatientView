package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.search.impl.FormResponseResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormResponseDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
public class FormResponseTab<P extends Person> extends TabController implements TableActionDelegate, FormFieldDelegate {

	protected PagedAdvancedTableController formResponseTable;
	protected FormResponseDetailViewPanelController formResponsePanel;
	protected FormResponseResultSet resultSet;
	protected Object comboBox;
	protected P person;
	

	//private static final String FORM_RESPONSES_LABEL = "patientrecord.labels.form.responses";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FORM_SENDER_COLUMN = "medic.common.labels.form.sender";
	private static final String FORM_SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";
	private static final String TAB_TITLE = "medic.common.form.responses";
	private static final String FORM_COMBOBOX_LABEL = "medic.common.form";
	private static final String ALL_FORMS = "medic.common.all.forms";
	
	private static final String UI_FILE ="/ui/plugins/patientview/dashboard/tabs/formResponseTab.xml";

	public FormResponseTab(UiGeneratorController uiController, ApplicationContext appCon, P person) {
		super(uiController, appCon);
		this.person = person;
		init();
	}

	protected void init() {
		super.setTitle(getI18nString(TAB_TITLE));
		super.setIconPath("/icons/big_form.png");
		//set up skeleton
		ui.add(super.getMainPanel(),ui.loadComponentFromFile(UI_FILE));
		//set up right panel, the form response panel
		formResponsePanel = new FormResponseDetailViewPanelController(ui, appCon);
		ui.add(ui.find(super.getMainPanel(),"formPanel"),formResponsePanel.getMainPanel());
		//set up the table, starting with the result set
		resultSet = new FormResponseResultSet(appCon);
		if(isCHW()){
			resultSet.setSubmitter(person);
		}else{
			resultSet.setSubject(person);
		}
		resultSet.setAroundDate(new Date());
		// add the form response table
		formResponseTable = new PagedAdvancedTableController(this, ui,ui.find(getMainPanel(),"tablePanel"));
		if(isCHW()){
			formResponseTable.putHeader(MedicFormResponse.class, HeaderColumn.createColumnList(new String[] { getI18nString(FORM_NAME_COLUMN), getI18nString(FORM_SUBJECT_COLUMN), getI18nString(DATE_SUBMITTED_COLUMN) },new String[]{"/icons/form.png","", "/icons/date_sent.png"}, new String[] { "getFormName", "getSubjectName", "getStringDateSubmitted" }));
		}else{
			formResponseTable.putHeader(MedicFormResponse.class, HeaderColumn.createColumnList(new String[] { getI18nString(FORM_NAME_COLUMN), getI18nString(FORM_SENDER_COLUMN), getI18nString(DATE_SUBMITTED_COLUMN) },new String[]{"/icons/form.png","/icons/user_sender.png", "/icons/date_sent.png"}, new String[] { "getFormName", "getSubmitterName", "getStringDateSubmitted" }));
		}
		formResponseTable.setResultsSet(resultSet);
		formResponseTable.updateTable();
		formResponseTable.setNoResultsMessage(getI18nString("medic.form.responses.tab.no.search.results"));
		formResponseTable.enableRefreshButton(appCon);
		//set up controls
		//create the date controls
		DateField dateField = new DateField(ui,getI18nString(DATE_SUBMITTED_COLUMN),this);
		dateField.setLabelIcon("/icons/date.png");
		ui.add(ui.find(getMainPanel(),"controlPanel"),dateField.getThinletPanel());
		ui.add(ui.find(getMainPanel(),"controlPanel"),ui.createLabel("   "));
		//create the form combo box
		List<MedicForm> forms = ((MedicFormDao) appCon.getBean("MedicFormDao")).getAllMedicForms();
		comboBox = ui.create("combobox");
		ui.setEditable(comboBox, false);
		ui.add(comboBox,ui.createComboboxChoice(getI18nString(ALL_FORMS), null));
		ui.setSelectedIndex(comboBox, 0);
		for(MedicForm mf: forms){
			ui.add(comboBox,ui.createComboboxChoice(mf.getName(), mf));
		}
		ui.setAction(comboBox, "formChanged(this.selected)", null, this);
		ui.setWeight(comboBox,1,0);
		Object label = ui.createLabel(getI18nString(FORM_COMBOBOX_LABEL));
		ui.setIcon(label, "/icons/form.png");
		ui.add(ui.find(getMainPanel(),"controlPanel"),label);
		ui.add(ui.find(getMainPanel(),"controlPanel"),comboBox);
		//add the spacer
		Object spacer = ui.createLabel("");
		ui.setWeight(spacer, 1, 0);
		ui.add(ui.find(getMainPanel(),"controlPanel"),spacer);
		formResponseTable.setSelected(0);
	}
	

	
	protected boolean isCHW(){
		return (person instanceof CommunityHealthWorker);
	}


	public void selectionChanged(Object selectedObject) {
		formResponsePanel.willAppear((MedicFormResponse) selectedObject);
	}

	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}

	public void formFieldChanged(ThinletFormField changedField, String newValue) {
		resultSet.setAroundDate(((DateField) changedField).getRawResponse());
		formResponseTable.updateTable();
	}
	
	public void formChanged(int selectedIndex){
		MedicForm mf = (MedicForm) ui.getAttachedObject(ui.getItem(comboBox, selectedIndex));
		resultSet.setForm(mf);
		formResponseTable.updateTable();
	}
}
