package net.frontlinesms.plugins.patientview.ui.dashboard.tabs.flags;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.TriggeredFlagDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class ResolveFlagDialog extends ViewHandler {

	public ResolveFlagDialog(UiGeneratorController ui,
			ApplicationContext appCon, TriggeredFlag flag, FlagTab parent) {
		super(ui, appCon,UI_XML);
		this.flag = flag;
		this.parentController = parent;
		flagDao = (TriggeredFlagDao) appCon.getBean("TriggeredFlagDao");
		appointmentDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		ui.add(mainPanel);
		ui.setVisible(mainPanel,true);
	}
	
	public void resolve(){
		flag.setResolved(true);
		flag.setDateResolved(new Date().getTime());
		flag.setComments(ui.getText(ui.find(mainPanel,"commentsArea")));
		flag.setResolvedBy(UserSessionManager.getUserSessionManager().getCurrentUser());
		flagDao.updateTriggeredFlag(flag);
		ui.remove(mainPanel);
		parentController.flagResolved(true);
	}
	
	public void cancel(){
		ui.remove(mainPanel);
		parentController.flagResolved(false);
	}
	
	private final static String UI_XML = "/ui/plugins/patientview/dashboard/tabs/flags/resolveFlagDialog.xml";
	
	private TriggeredFlagDao flagDao;
	private ScheduledDoseDao appointmentDao;
	private DateField dateField;
	
	private TriggeredFlag flag;
	
	private FlagTab parentController;
	public void formFieldChanged(ThinletFormField changedField, String newValue) {}
}
