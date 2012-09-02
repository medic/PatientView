package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.flags;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.search.impl.TriggeredFlagResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.StaticSelectionTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.TabController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagTab  extends TabController implements ThinletUiEventHandler, TableActionDelegate  {
	private static final String UI_XML = "/ui/plugins/patientview/dashboard/tabs/flags/flagTab.xml";

	private Patient patient;
	private StaticSelectionTableController table;
	private TriggeredFlagResultSet resultSet;
	
	private boolean searchingForPatient = true;
	
	public FlagTab(UiGeneratorController uiController, ApplicationContext appCon,Patient patient) {
		super(uiController, appCon);
		uiController.add(mainPanel,uiController.loadComponentFromFile(UI_XML, this));
		super.setIconPath("/icons/flag_purple.png");
		super.setTitle("Flags");
		this.patient = patient;
		resultSet = new TriggeredFlagResultSet(appCon);
		resultSet.setPatient(patient);
		table = new StaticSelectionTableController(this,uiController);
		table.setResultsSet(resultSet);
		List<HeaderColumn> columns = new ArrayList<HeaderColumn>();
		columns.add(new HeaderColumn("getFlagName", "", "Flag"));
		columns.add(new HeaderColumn("getStringDateTriggered", "", "Date Raised"));
		columns.add(new HeaderColumn("getReason", "", "Reason"));
		columns.add(new HeaderColumn("getAppointmentString","","Appointment"));
		table.putHeader(TriggeredFlag.class, columns);
		table.setNoResultsMessage("There were no flags matching your search...");
		uiController.add(uiController.find(mainPanel,"tablePanel"),table.getMainPanel());
		table.refresh();
	}

	public void willAppear(){
		table.refresh();
	}
	
	public void checkboxChanged(String name){
		resultSet.setActive(activeChecked());
		resultSet.setResolved(resolvedChecked());
		setHeaders(activeChecked(),resolvedChecked());
		if(!(activeChecked() || resolvedChecked())){
			ui.setSelected(ui.find(mainPanel, name),true);
			return;
		}
		table.refresh();
	}
	
	private boolean activeChecked(){
		return ui.isSelected(ui.find(mainPanel, "activeCheckbox"));
	}
	
	private boolean resolvedChecked(){
		return ui.isSelected(ui.find(mainPanel, "resolvedCheckbox"));
	}

	public void setHeaders(boolean active, boolean resolved){
		List<HeaderColumn> columns = new ArrayList<HeaderColumn>();
		columns.add(new HeaderColumn("getFlagName", "", "Type"));
		columns.add(new HeaderColumn("getStringDateTriggered", "", "Date Raised"));
		columns.add(new HeaderColumn("getAppointmentString","","Appointment"));
		if(active){
			columns.add(new HeaderColumn("getReason", "", "Reason"));
		}
		if(resolved){
			columns.add(new HeaderColumn("getComments", "", "Comments"));
			columns.add(new HeaderColumn("getDateResolvedString", "", "Date Resolved"));
			columns.add(new HeaderColumn("getResolverName", "", "Resolved By"));
		}
		table.putHeader(TriggeredFlag.class, columns);
	}
	
	public void scheduleAppointment(){
		ScheduleAppointmentDialog dialog = new ScheduleAppointmentDialog(ui, appCon, patient, (TriggeredFlag) table.getSelectedObject(), this);
	}
	
	public void resolveFlag(){
		ResolveFlagDialog dialog = new ResolveFlagDialog(ui, appCon, (TriggeredFlag) table.getSelectedObject(), this);
	}
	
	public void flagResolved(boolean success){
		if(success){
			table.refresh();
		}
	}
	
	public void appointmentScheduled(boolean success){
		if(success){
			table.refresh();
		}
	}
	
	public void selectionChanged(Object selectedObject) {
		if(selectedObject != null){
			TriggeredFlag flag = (TriggeredFlag) selectedObject;
			if(flag.getAppointment() == null){
				ui.setEnabled(ui.find(mainPanel,"scheduleAppointmentButton"), true);
			}
			if(!flag.isResolved()){
				ui.setEnabled(ui.find(mainPanel,"resolveFlagButton"), true);
			}
		}else{
			ui.setEnabled(ui.find(mainPanel,"scheduleAppointmentButton"), false);
			ui.setEnabled(ui.find(mainPanel,"resolveFlagButton"), false);
		}
	}

	public void doubleClickAction(Object selectedObject) {}
	
	public void resultsChanged() {
		selectionChanged(table.getSelectedObject()); 
	}
}
