package net.frontlinesms.plugins.patientview.listener;

import java.util.List;

import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.FlagDao;
import net.frontlinesms.plugins.patientview.data.repository.TriggeredFlagDao;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagListener implements EventObserver{

	private FlagDao flagDao;
	private TriggeredFlagDao triggeredFlagDao;
	private ApplicationContext appCon;
	private UiGeneratorController ui;
	
	public FlagListener(ApplicationContext appCon, UiGeneratorController ui){
		this.appCon = appCon;
		this.ui = ui;
		this.flagDao = (FlagDao) appCon.getBean("FlagDao");
		this.triggeredFlagDao = (TriggeredFlagDao) appCon.getBean("TriggeredFlagDao");
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
	}
	
	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntitySavedNotification<?>){
			EntitySavedNotification<?> sNotification = (EntitySavedNotification<?>) notification;
			if(sNotification.getDatabaseEntity() instanceof MedicFormResponse){
				handleForm((MedicFormResponse) sNotification.getDatabaseEntity());
			}
		}
	}
	
	private void handleForm(MedicFormResponse mfr){
		List<Flag> flags = flagDao.getAllFlags();
		for(Flag flag: flags){
			if(mfr.getForm().getFid() == flag.getForm().getFid() && flag.evaluate(mfr, appCon)){
				String message= flag.generateMessage(mfr, appCon);
				ui.getFrontlineController().sendTextMessage(flag.getDestinationPhoneNumber(), message);
				ui.alert(flag.generateMessage(mfr, appCon));
				TriggeredFlag tf = new TriggeredFlag(flag,mfr,message);
				triggeredFlagDao.saveTriggeredFlag(tf);
			}
		}
	}
}
