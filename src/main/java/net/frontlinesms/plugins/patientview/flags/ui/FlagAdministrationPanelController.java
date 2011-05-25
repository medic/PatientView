package net.frontlinesms.plugins.patientview.flags.ui;

import java.awt.Color;
import java.util.List;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.events.EntityDeletedNotification;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagDefinition;
import net.frontlinesms.plugins.patientview.data.repository.FlagDefinitionDao;
import net.frontlinesms.plugins.patientview.flags.FlagConditionValidator;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.classic.ValidationFailure;
import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class FlagAdministrationPanelController extends AdministrationTabPanel implements EventObserver {

	private static final String UI_FILE = "/ui/plugins/patientview/administration/flagAdministrationPanel.xml";
	
	private FlagDefinitionDao flagDefinitionDao;
	private FlagConditionValidator flagValidator;
	
	
	//text fields
	private Object nameField;
	private Object descriptionField;
	private Object conditionArea;
	private Object conditionMessageLabel;
	
	//lists
	private Object flagList;
	
	public FlagAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController,appCon,UI_FILE);
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		this.flagDefinitionDao = (FlagDefinitionDao) appCon.getBean("FlagDefinitionDao");
		this.flagList = uiController.find(mainPanel,"flagList");
		this.nameField = uiController.find(mainPanel,"nameField");
		this.descriptionField = uiController.find(mainPanel,"descriptionField");
		this.conditionArea = uiController.find(mainPanel, "conditionArea");
		this.conditionMessageLabel = uiController.find(mainPanel,"conditionMessageLabel");
		this.flagValidator = new FlagConditionValidator(appCon);
		updateFlagList();
	}
	
	public String getIconPath() {
		return "/icons/flag_red.png";
	}

	public String getListItemTitle() {
		return InternationalisationUtils.getI18nString("medic.flags.admin.tab.title");
	}
	
	public void addFlag(){
		FlagDefinition fd = new FlagDefinition("<New Flag>", "", "");
		flagDefinitionDao.saveFlagDefinition(fd);
		ui.setFocus(nameField);
	}
	
	public void removeFlag(){
		FlagDefinition fd = (FlagDefinition) ui.getAttachedObject(ui.getSelectedItem(flagList));
		if(fd != null){
			flagDefinitionDao.deleteFlagDefinition(fd);
			clearFields();
			flagListSelectionChanged();
		}
	}
	
	private void clearFields(){
		ui.setText(nameField, "");
		ui.setText(descriptionField, "");
		ui.setText(conditionArea, "");
	}
	
	public void flagListSelectionChanged(){
		flagListSelectionChanged(ui.getSelectedIndex(flagList));
	}
	public void flagListSelectionChanged(int selectedIndex){
		FlagDefinition df = ui.getAttachedObject(ui.getItem(flagList, selectedIndex), FlagDefinition.class);
		if(df != null){
			ui.setEnabledRecursively(ui.find(mainPanel, "flagSettingsPanel"), true);
			ui.setText(nameField, df.getName());
			ui.setText(descriptionField, df.getShortDescription());
			ui.setText(conditionArea, df.getFlagCondition());
		}else{
			ui.setEnabledRecursively(ui.find(mainPanel, "flagSettingsPanel"), false);
		}
	}
	
	private void updateFlagList(){
		List<FlagDefinition> flags = flagDefinitionDao.getAllFlagDefinitions();
		ui.removeAll(flagList);
		for(FlagDefinition fd: flags){
			Object listItem = ui.createListItem(fd.getName(), fd);
			ui.setString(listItem, "tooltip", fd.getShortDescription());
			ui.setIcon(listItem, fd.getIconPath());
			ui.add(flagList,listItem);
		}
		ui.setSelectedIndex(flagList, 0);
		flagListSelectionChanged(0);
	}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntityDeletedNotification || notification instanceof EntitySavedNotification){
			if(((DatabaseEntityNotification<?>) notification).getDatabaseEntity() instanceof FlagDefinition){
				updateFlagList();
			}
		}
	}
	
	//thinlet change methods
	public void nameChanged(String text){
		getCurrentlySelectedFlagDefinition().setName(text);
		ui.setText(ui.getSelectedItem(flagList), text);
		flagDefinitionDao.updateFlagDefinition(getCurrentlySelectedFlagDefinition());
	}
	
	public void descriptionChanged(String text){
		getCurrentlySelectedFlagDefinition().setShortDescription(text);
		flagDefinitionDao.updateFlagDefinition(getCurrentlySelectedFlagDefinition());
	}
	
	public void conditionChanged(String text){
		try{
			flagValidator.validate(text);
		}catch(ValidationFailure e){
			ui.setColor(conditionMessageLabel, Thinlet.FOREGROUND, new Color(184,0,0));
			ui.setIcon(conditionMessageLabel, "/icons/cross.png");
			ui.setText(conditionMessageLabel,e.getMessage());
			return;
		}
		ui.setColor(conditionMessageLabel, Thinlet.FOREGROUND, new Color(0,168,0));
		ui.setIcon(conditionMessageLabel, "/icons/tick.png");
		ui.setText(conditionMessageLabel,"Condition is valid");
		getCurrentlySelectedFlagDefinition().setFlagCondition(text);
		flagDefinitionDao.updateFlagDefinition(getCurrentlySelectedFlagDefinition());
	}
	
	private FlagDefinition getCurrentlySelectedFlagDefinition(){
		return ui.getAttachedObject(ui.getSelectedItem(flagList),FlagDefinition.class);
	}
}
