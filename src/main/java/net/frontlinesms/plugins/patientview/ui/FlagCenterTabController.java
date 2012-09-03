package net.frontlinesms.plugins.patientview.ui;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.search.impl.TriggeredFlagResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.StaticSelectionTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.dashboard.PatientDashboard;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagCenterTabController extends ViewHandler implements TableActionDelegate {
	private StaticSelectionTableController table;
	private TriggeredFlagResultSet resultSet;
	
	private static final String UI_XML =  "/ui/plugins/patientview/flagTab.xml";
	
	public FlagCenterTabController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon,UI_XML);
		resultSet = new TriggeredFlagResultSet(appCon);
		table = new StaticSelectionTableController(this,ui);
		table.setResultsSet(resultSet);
		table.enableRefreshButton(appCon);
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
		if(!(resolvedSelected() || activeSelected())){
			ui.setSelected(ui.find(mainPanel,"activeCheckbox"), true);
		}
		resultSet.setActive(activeSelected());
		table.refresh();
	}
	
	public void resolvedCheckboxChanged(){
		if(!(resolvedSelected() || activeSelected())){
			ui.setSelected(ui.find(mainPanel,"resolvedCheckbox"), true);
		}
		resultSet.setResolved(resolvedSelected());
		table.refresh();
	}
	
	private boolean activeSelected(){
		return ui.isSelected(ui.find(mainPanel, "activeCheckbox"));
	}
	
	private boolean resolvedSelected(){
		return ui.isSelected(ui.find(mainPanel, "resolvedCheckbox"));
	}
		
	public void showPatientRecord(){
		Patient p = ((TriggeredFlag) table.getSelectedObject()).getPatient();
		PatientDashboard dash = new PatientDashboard(ui, appCon, p);
		dash.setSelectedTab(1);
		dash.expandDashboard("flagTabMainPanel","flagTab");
	}

	public void doubleClickAction(Object selectedObject) {
		showPatientRecord();
	}
	
	public void resultsChanged() {
		updatePatientDashboardButton();
	}
	
	public void selectionChanged(Object selectedObject) {
		if(selectedObject != null){
			ui.setEnabled(ui.find(mainPanel,"patientDashboardButton"),true);
		}else{
			ui.setEnabled(ui.find(mainPanel,"patientDashboardButton"),false);
		}
	}
	
	private void updatePatientDashboardButton(){
		Object selectedObject = table.getSelectedObject();
		if(selectedObject != null){
			ui.setEnabled(ui.find(mainPanel,"patientDashboardButton"),true);
		}else{
			ui.setEnabled(ui.find(mainPanel,"patientDashboardButton"),false);
		}
	}
}
