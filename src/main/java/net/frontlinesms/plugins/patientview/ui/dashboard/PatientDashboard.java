package net.frontlinesms.plugins.patientview.ui.dashboard;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.FormResponseTab;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.PatientVaccineTab;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.SubmitFormTab;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.flags.FlagTab;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientDashboard extends PersonDashboard<Patient> {
		
	public PatientDashboard(UiGeneratorController uiController, ApplicationContext appCon, Patient p) {
		super(uiController, appCon, p);
	}

	@Override
	protected void init() {
		ui.add(leftPanel,new PatientPanel(ui,appCon,person).getMainPanel());
		tabs.add(new FormResponseTab(ui,appCon,person));
		tabs.add(new FlagTab(ui,appCon,person));
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READWRITE||
		   UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
				tabs.add(new SubmitFormTab(ui,appCon,person));
				tabs.add(new PatientVaccineTab(ui,appCon,person));
		}
	}

}
