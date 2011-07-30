package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.vaccine;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.PatientVaccineTab;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class RescheduleVaccinesDialog extends ViewHandler{

	private ScheduledDoseDao doseDao;
	
	private Patient patient;
	
	private List<VaccineReschedulingPanel> panels;
	
	private PatientVaccineTab parentController;
	
	private static final String THINLET_XML = "/ui/plugins/patientview/dashboard/tabs/vaccines/reschedule_dialog/masterRescheduleDialog.xml";
	
	public RescheduleVaccinesDialog(UiGeneratorController ui, ApplicationContext appCon, Patient patient, PatientVaccineTab parentController) {
		super(ui, appCon,THINLET_XML);
		this.doseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		this.patient = patient;
		this.parentController = parentController;
		panels = new ArrayList<VaccineReschedulingPanel>();
		init();
	}
	
	private void init(){
		ui.add(mainPanel);
		List<Vaccine> vaccines = ((VaccineDao) appCon.getBean("VaccineDao")).getAllVaccines();
		for(Vaccine v: vaccines){
			List<ScheduledDose> doses = doseDao.getScheduledDoses(v, patient);
			if(doses.size() >0){
				VaccineReschedulingPanel vaccinePanel = new VaccineReschedulingPanel(ui, appCon, doses);
				panels.add(vaccinePanel);
				ui.add(find("vaccineContainerPanel"),vaccinePanel.getMainPanel());
			}
		}
		ui.setVisible(mainPanel, true);
	}
	
	public void closeDialog(){
		ui.remove(mainPanel);
	}
	
	public void save(){
		for(VaccineReschedulingPanel panel:panels){
			try{
				panel.save();
			}catch(Exception e){
				ui.alert(e.getMessage());
				return;
			}
		}
		closeDialog();
		parentController.refreshDoseTable();
	}
}