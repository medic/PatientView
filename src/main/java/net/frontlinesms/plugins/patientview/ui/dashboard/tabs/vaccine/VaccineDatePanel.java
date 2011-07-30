package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.vaccine;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class VaccineDatePanel extends ViewHandler{

	private static final String THINLET_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/reschedule_dialog/vaccineDatePanel.xml";
	
	private List<ScheduledDose> doses;

	private List<DoseDatePanel> panels;
	
	public VaccineDatePanel(UiGeneratorController ui, ApplicationContext appCon, List<ScheduledDose> doses) {
		super(ui, appCon, THINLET_XML);
		this.doses = doses;
		panels = new ArrayList<DoseDatePanel>();
		init();
	}
	
	private void init(){
		ui.setText(find("vaccineNameLabel"), doses.get(0).getDose().getVaccine().getName());
		for(ScheduledDose dose: doses){
			DoseDatePanel dosePanel = new DoseDatePanel(ui, appCon, dose);
			panels.add(dosePanel);
			ui.add(find("doseContainerPanel"),dosePanel.getMainPanel());
		}
	}

	public void save() throws Exception{
		for(DoseDatePanel panel: panels){
			panel.save();
		}
	}
}
