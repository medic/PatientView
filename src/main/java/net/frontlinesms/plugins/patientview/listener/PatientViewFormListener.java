package net.frontlinesms.plugins.patientview.listener;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm.MedicFormType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
public class PatientViewFormListener implements EventObserver{
	
	private MedicFormDao formDao;
	private UserDao userDao;
	private boolean listening = true;
	private static Logger LOG = FrontlineUtils.getLogger(PatientViewFormListener.class);
	
	private ApplicationContext appCon;
	
	public PatientViewFormListener(ApplicationContext appCon){
		this.appCon = appCon;
		this.formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		this.userDao = (UserDao) appCon.getBean("UserDao");
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
	}

	public void notify(FrontlineEventNotification notification) {
		if(!listening){
			return;
		}
		if(notification instanceof EntityUpdatedNotification<?>){
			if(((EntityUpdatedNotification<?>) notification).getDatabaseEntity() instanceof Form){
				Form f =  ((EntityUpdatedNotification<Form>) notification).getDatabaseEntity();
				if(f.isFinalised() && formDao.getMedicFormForForm(f) == null){
					LOG.trace("Attempting to create Medic Form from FrontlineSMS Form");
					try{
						MedicForm newForm = new MedicForm(f);
						newForm.setType(MedicFormType.PATIENT_DATA,true);
						formDao.saveMedicForm(newForm);
					}catch(Throwable e){
						LOG.error("Unable to create Medic Form from FrontlineSMS form",e);
					}
				}
			}
		}
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public boolean isListening() {
		return listening;
	}
	
	public void deinit(){
		((EventBus) appCon.getBean("eventBus")).unregisterObserver(this);
	}
}
