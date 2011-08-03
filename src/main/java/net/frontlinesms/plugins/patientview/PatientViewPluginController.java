package net.frontlinesms.plugins.patientview;

import java.util.Calendar;
import java.util.Timer;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderDispatcher;
import net.frontlinesms.plugins.patientview.listener.PatientViewFormListener;
import net.frontlinesms.plugins.patientview.listener.PatientViewMessageListener;
import net.frontlinesms.plugins.patientview.listener.VaccineScheduleListener;
import net.frontlinesms.plugins.patientview.responsemapping.IncomingFormMatcher;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.PatientViewThinletTabController;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

@PluginControllerProperties(name="PatientView", iconPath="/icons/big_medic.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/patientview/patientview-spring-hibernate.xml",
		hibernateConfigPath ="classpath:net/frontlinesms/plugins/patientview/patientview.hibernate.cfg.xml",i18nKey="PatientView")
public class PatientViewPluginController extends BasePluginController{

	/** the {@link FrontlineSMS} instance that this plugin is attached to */
	private FrontlineSMS frontlineController;
	
	private UiGeneratorController ui;
	
	/** The application context used for fetching daos and other spring beans**/
	private ApplicationContext applicationContext;
	
	private static IncomingFormMatcher incomingFormMatcher;
	private PatientViewMessageListener messageListener;
	private PatientViewFormListener formListener; 
	private PatientViewThinletTabController tabController;
	private ReminderDispatcher reminderDispatch;
	private VaccineScheduleListener vaccineListener;
	/** 
	 * @see net.frontlinesms.plugins.BasePluginController#initThinletTab(net.frontlinesms.ui.UiGeneratorController)
	 */
	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		this.ui = uiController;
		//init the thinlet tab
		tabController = new PatientViewThinletTabController(this,ui);
		//start the vaccine listener
		vaccineListener = new VaccineScheduleListener(ui);
		//start the reminder dispatcher
		reminderDispatch = new ReminderDispatcher(ui, applicationContext);
		Timer t = new Timer();
		int minutes = Calendar.getInstance().get(Calendar.MINUTE);
		int firstRunWait = (61 - minutes) % 60;
		System.out.println("Dispatching reminders in " + firstRunWait + " minutes");
		t.scheduleAtFixedRate(reminderDispatch, firstRunWait * 60 * 1000 , ReminderDispatcher.INTERVAL_MINUTES * 60 * 1000);
//		t.scheduleAtFixedRate(reminderDispatch, 1000 , 15 * 1000);
		return tabController.getTab();
	}

	/**
	 * @see net.frontlinesms.plugins.PluginController#deinit()
	 */
	public void deinit() {
		reminderDispatch.cancel();
		vaccineListener.deinit();
		incomingFormMatcher.deinit();
		messageListener.deinit();
		formListener.deinit();
	}

	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static IncomingFormMatcher getFormMatcher(){
		return incomingFormMatcher;
	}
	/** 
	 * @see net.frontlinesms.plugins.PluginController#init(net.frontlinesms.FrontlineSMS, org.springframework.context.ApplicationContext)
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		this.applicationContext = applicationContext;
		//start some services
		UserSessionManager.getUserSessionManager().init(applicationContext);
		incomingFormMatcher = new IncomingFormMatcher(applicationContext);
		messageListener = new PatientViewMessageListener(applicationContext);
		formListener = new PatientViewFormListener(applicationContext);
		VaccineScheduler.instance().init(applicationContext);
	}
	
	public void stopListening(){
		messageListener.setListening(false);
		formListener.setListening(false);
	}
	
	public void startListening(){
		messageListener.setListening(true);
		formListener.setListening(true);
	}

	public void setTabController(PatientViewThinletTabController tabController) {
		this.tabController = tabController;
	}

	public PatientViewThinletTabController getTabController() {
		return tabController;
	}
}
