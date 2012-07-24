package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.flags;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.search.impl.TriggeredFlagResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.TabController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagTab  extends TabController implements ThinletUiEventHandler, TableActionDelegate  {
	private static final String UI_XML = "/ui/plugins/patientview/dashboard/tabs/flags/flagTab.xml";

	private Patient patient;
	private PagedAdvancedTableController table;
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
		table = new PagedAdvancedTableController(this,uiController);
		table.setResultsSet(resultSet);
		List<HeaderColumn> columns = new ArrayList<HeaderColumn>();
		columns.add(new HeaderColumn("getFlagName", "", "Type"));
		columns.add(new HeaderColumn("getStringDateTriggered", "", "Date Raised"));
		columns.add(new HeaderColumn("getReason", "", "Reason"));
		columns.add(new HeaderColumn("getAppointmentString","","Appointment"));
		table.putHeader(TriggeredFlag.class, columns);
		table.setNoResultsMessage("There were no flags matching your search...");
		uiController.add(uiController.find(mainPanel,"tablePanel"),table.getMainPanel());
		table.refresh();
	}

	public void activeCheckboxChanged(){
		boolean active = ui.isSelected(ui.find(mainPanel,"activeCheckbox"));
		resultSet.setActive(active);
		resultSet.setResolved(!active);
		setHeaders(active);
		table.refresh();
	}
	
	public void resolvedCheckboxChanged(){
		boolean resolved = ui.isSelected(ui.find(mainPanel,"resolvedCheckbox"));
		resultSet.setResolved(resolved);
		resultSet.setActive(!resolved);
		setHeaders(!resolved);
		table.refresh();
	}
	
	public void setHeaders(boolean active){
		List<HeaderColumn> columns = new ArrayList<HeaderColumn>();
		columns.add(new HeaderColumn("getFlagName", "", "Type"));
		columns.add(new HeaderColumn("getStringDateTriggered", "", "Date Raised"));
		columns.add(new HeaderColumn("getAppointmentString","","Appointment"));
		if(active){
			columns.add(new HeaderColumn("getReason", "", "Reason"));
		}else{
			columns.add(new HeaderColumn("getComments", "", "Comments"));
			columns.add(new HeaderColumn("getDateResolvedString", "", "Date Resolved"));
			columns.add(new HeaderColumn("getResolverName", "", "Resolved By"));
		}
		table.putHeader(TriggeredFlag.class, columns);
	}
	
	public void scheduleAppointment(){
		ScheduleAppointmentDialog dialog = new ScheduleAppointmentDialog(ui, appCon, patient, (TriggeredFlag) table.getCurrentlySelectedObject(), this);
	}
	
	public void resolveFlag(){
		ResolveFlagDialog dialog = new ResolveFlagDialog(ui, appCon, (TriggeredFlag) table.getCurrentlySelectedObject(), this);
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
			ui.setEnabled(ui.find(mainPanel,"scheduleAppointmentButton"), true);
			ui.setEnabled(ui.find(mainPanel,"resolveFlagButton"), true);
		}else{
			ui.setEnabled(ui.find(mainPanel,"scheduleAppointmentButton"), false);
			ui.setEnabled(ui.find(mainPanel,"resolveFlagButton"), false);
		}
	}

	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}
}
