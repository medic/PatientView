package net.frontlinesms.plugins.patientview.importer.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.frontlinesms.plugins.patientview.importer.CommunityHealthWorkerDataImporter;
import net.frontlinesms.plugins.patientview.importer.CsvDataImporter;
import net.frontlinesms.plugins.patientview.importer.FormResponseDataImporter;
import net.frontlinesms.plugins.patientview.importer.PatientDataImporter;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.CheckBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;


public class CsvImporterPanelController extends AdministrationTabPanel implements FormFieldDelegate {

	private Object messageList;
	private Object dataTypeComboBox;
	private Object fileTextField;
	private Object additionalOptionsPanel;
	private CheckBox ignoreHeadersBox;
	
	private List<CsvDataImporter> importers;

	private Object infoPanel;
	
	private static final String UI_FILE_XML = "/ui/plugins/patientview/administration/dataimport/dataImportAdministrationPanel.xml";
	
	
	public CsvImporterPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController,appCon,UI_FILE_XML);
		init();
	}
	
	private void init(){
		messageList = find("messageArea");
		fileTextField = find("pathField");
		additionalOptionsPanel= find("additionalOptionsPanel");
		infoPanel = find("infoPanel");
		dataTypeComboBox = find("dataTypeComboBox");

		//initialize the importers
		importers = new ArrayList<CsvDataImporter>();
		importers.add(new CommunityHealthWorkerDataImporter(messageList, ui, appCon));
		importers.add(new PatientDataImporter(messageList,ui,appCon));
		importers.add(new FormResponseDataImporter(messageList, ui, appCon));
		for(CsvDataImporter di: importers){
			add(dataTypeComboBox,ui.createComboboxChoice(di.getTypeLabel(), di));
		}
		ui.setSelectedIndex(dataTypeComboBox, 0);
		ui.setText(dataTypeComboBox, importers.get(0).getTypeLabel());
		datatypeChanged();
	}
	
	public String getListItemTitle() {
		return getI18nString("medic.importer.tab.title");
	}

	public void datatypeChanged(){
		removeAll(additionalOptionsPanel);
		removeAll(infoPanel);
		CsvDataImporter importer = ui.getAttachedObject(ui.getSelectedItem(dataTypeComboBox),CsvDataImporter.class);
		add(additionalOptionsPanel,importer.getAdditionalOptionsPanel());
		ignoreHeadersBox = new CheckBox(ui, "Ignore the first line of the CSV file", this);
		
		add(additionalOptionsPanel,ignoreHeadersBox.getThinletPanel());
		add(infoPanel,importer.getInformationPanel());
	}
	
	public void browseButtonClicked(){
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showDialog(null, getI18nString("medic.common.label.open"));
		if(returnVal == JFileChooser.APPROVE_OPTION){
			ui.setText(fileTextField, fc.getSelectedFile().getAbsolutePath());
		}

	}
	
	public void importButtonClicked(){
		CsvDataImporter importer = ui.getAttachedObject(ui.getSelectedItem(dataTypeComboBox), CsvDataImporter.class);
		importer.importFile(ui.getText(fileTextField),ignoreHeadersBox.getRawResponse());
	}
	
	public void clearLog(){
		ui.setText(messageList, "");
	}

	public String getIconPath() {
		return "/icons/import_data.png";
	}

	public void formFieldChanged(ThinletFormField changedField, String newValue) {/* do nothing*/}
}
