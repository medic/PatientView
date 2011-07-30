package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.vaccine;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class VaccineReschedulingPanel extends ViewHandler{

	private static final String THINLET_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/reschedule_dialog/rescheduleVaccinePanel.xml";
	
	private List<ScheduledDose> doses;

	private List<DoseReschedulingPanel> panels;
	
	public VaccineReschedulingPanel(UiGeneratorController ui, ApplicationContext appCon, List<ScheduledDose> doses) {
		super(ui, appCon, THINLET_XML);
		this.doses = doses;
		panels = new ArrayList<DoseReschedulingPanel>();
		init();
	}
	
	private void init(){
		ui.setText(find("vaccineNameLabel"), doses.get(0).getDose().getVaccine().getName());
		for(ScheduledDose dose: doses){
			DoseReschedulingPanel dosePanel = new DoseReschedulingPanel(ui, appCon, dose);
			panels.add(dosePanel);
			ui.add(find("doseContainerPanel"),dosePanel.getMainPanel());
		}
	}

	public void save() throws Exception{
		for(DoseReschedulingPanel panel: panels){
			panel.save();
		}
	}
}
