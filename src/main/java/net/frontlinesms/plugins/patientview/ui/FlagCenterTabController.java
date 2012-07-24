package net.frontlinesms.plugins.patientview.ui;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.search.impl.TriggeredFlagResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.dashboard.PatientDashboard;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagCenterTabController extends ViewHandler implements TableActionDelegate {
	private PagedAdvancedTableController table;
	private TriggeredFlagResultSet resultSet;
	
	private static final String UI_XML =  "/ui/plugins/patientview/flagTab.xml";
	
	public FlagCenterTabController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon,UI_XML);
		resultSet = new TriggeredFlagResultSet(appCon);
		table = new PagedAdvancedTableController(this,ui);
		table.setResultsSet(resultSet);
		List<HeaderColumn> columns = new ArrayList<HeaderColumn>();
		columns.add(new HeaderColumn("getFlagName", "", "Flag Type"));
		columns.add(new HeaderColumn("getPatientName", "", "Patient"));
		columns.add(new HeaderColumn("getSubmitterName", "", "Submitter"));
		columns.add(new HeaderColumn("getStringDateTriggered", "", "Date Raised"));
		columns.add(new HeaderColumn("getReason", "", "Reason"));
		columns.add(new HeaderColumn("getAppointmentString","","Appointment"));
		table.putHeader(TriggeredFlag.class, columns);
		table.setNoResultsMessage("There were no flags matching your search...");
		ui.add(ui.find(mainPanel,"tablePanel"),table.getMainPanel());
		table.refresh();
	}
	
	public void textChanged(){
		fieldSelectionChanged();
	}
	
	public void fieldSelectionChanged(){
		String choiceName = ui.getName(ui.getSelectedItem(ui.find(mainPanel,"fieldSelect")));
		if(choiceName.equals("patientNameChoice")){
			resultSet.setPatientName(ui.getText(ui.find(mainPanel,"textField")));
		}else if(choiceName.equals("flagNameChoice")){
			resultSet.setFlagName(ui.getText(ui.find(mainPanel,"textField")));
		}
		table.refresh();
	}
	
	public void activeCheckboxChanged(){
		resultSet.setActive(ui.isSelected(ui.find(mainPanel, "activeCheckbox")));
		table.refresh();
	}
	
	public void resolvedCheckboxChanged(){
		resultSet.setResolved(ui.isSelected(ui.find(mainPanel, "resolvedCheckbox")));
		table.refresh();
	}
	
	public void showPatientRecord(){
		Patient p = ((TriggeredFlag) table.getCurrentlySelectedObject()).getPatient();
			PatientDashboard dash = new PatientDashboard(ui, appCon, p);
			dash.expandDashboard();
	}

	public void doubleClickAction(Object selectedObject) {
		Patient p = ((TriggeredFlag) ui.getAttachedObject(selectedObject)).getPatient();
		PatientDashboard dash = new PatientDashboard(ui, appCon, p);
		dash.expandDashboard();
	}
	
	public void resultsChanged() {}
	
	public void selectionChanged(Object selectedObject) {
		if(selectedObject != null){
			ui.setEnabled(ui.find(mainPanel,"patientDashboardButton"),true);
		}
	}
}
