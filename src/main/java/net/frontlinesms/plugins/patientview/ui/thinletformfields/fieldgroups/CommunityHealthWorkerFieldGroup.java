package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class CommunityHealthWorkerFieldGroup extends PersonFieldGroup<CommunityHealthWorker> {

	private CommunityHealthWorkerDao chwDao;

	public CommunityHealthWorkerFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, CommunityHealthWorker person) {
		super(ui, appCon, delegate, person);
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
	}

	@Override
	protected void addAdditionalFields() {}

	@Override
	protected void saveOrUpdatePerson() {
		if(isNewPersonGroup){
			chwDao.saveCommunityHealthWorker(getPerson());
		}else{
			chwDao.updateCommunityHealthWorker(getPerson());
		}
	}

	@Override
	protected CommunityHealthWorker createNewPerson() {
		return new CommunityHealthWorker();
	}

}
