package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagCondition;
import net.frontlinesms.plugins.patientview.data.repository.FlagConditionDao;
import net.frontlinesms.plugins.patientview.data.repository.FlagDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagAdministrationPanelController extends AdministrationTabPanel {

	private static final String MAIN_XML = "/ui/plugins/patientview/administration/flags/flagAdministrationPanel.xml";
	private static final String DISPLAY_FLAG_XML = "/ui/plugins/patientview/administration/flags/displayFlag.xml";
	private static final String EDIT_FLAG_XML = "/ui/plugins/patientview/administration/flags/editFlag.xml";
	
	private boolean isEditing;
	
	private static final String FLAG_LIST = "flagList";
	private static final String ACTION_PANEL = "actionPanel";
	
	private static final String REMOVE_FLAG_BUTTON = "removeFlagButton";
	private static final String ADD_FLAG_BUTTON = "addFlagButton";
	
	private static final String FLAG_NAME_LABEL = "flagNameLabel";
	private static final String CONDITIONS_TEXT_AREA = "conditionsTextArea";
	private static final String MESSAGE_TEXT_AREA = "messageTextArea";

	private FlagDao flagDao;
	private FlagConditionDao conditionDao;
	
	public FlagAdministrationPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon, MAIN_XML);
		flagDao = (FlagDao) appCon.getBean("FlagDao");
		conditionDao = (FlagConditionDao) appCon.getBean("FlagConditionDao");
		isEditing = false;
		refreshFlagList();
	}

	@Override
	public String getIconPath() {
		return "/icons/flag_red.png";
	}

	@Override
	public String getListItemTitle() {
		return "Flags";
	}
	
	public Flag getSelectedFlag(){
		return (Flag) ui.getAttachedObject(ui.getSelectedItem(find(FLAG_LIST)));
	}
	
	public void refreshFlagList(){
		int beforeIndex = ui.getSelectedIndex(find(FLAG_LIST));
		if(beforeIndex < 0) beforeIndex = 0;
		removeAll(find(FLAG_LIST));
		List<Flag> flags = flagDao.getAllFlags();
		for(Flag flag: flags){
			add(find(FLAG_LIST),ui.createListItem(flag.getName(),flag));
		}
		if(flags.size() == 0){
			add(find(FLAG_LIST),ui.createListItem("No Flags",null));
			ui.setEnabled(find(REMOVE_FLAG_BUTTON), false);
		}else{
			ui.setEnabled(find(REMOVE_FLAG_BUTTON), true);
		}
		if(flags.size() >= beforeIndex){
			ui.setSelectedIndex(find(FLAG_LIST), beforeIndex);
		}else{
			ui.setSelectedIndex(find(FLAG_LIST), flags.size());
		}
		flagListSelectionChanged();
	}
	
	/**
	 * Called when a flag in the flag list is selected
	 * Loads the 'display flag' panel and makes it visible
	 * to the user.
	 */
	public void flagListSelectionChanged(){
		isEditing=false;
		removeAll(find(ACTION_PANEL));
		Flag f = getSelectedFlag();
		if(f != null){
			add(find(ACTION_PANEL),ui.loadComponentFromFile(DISPLAY_FLAG_XML, this));
			//set the flag name
			ui.setText(find(FLAG_NAME_LABEL), f.getName());
			//create the condition text
			String conditionText = "If " + (f.isAny()?"any":"all") + " these conditions are met:\n";
			List<FlagCondition> conditions = conditionDao.getConditionsForFlag(f);
			for(FlagCondition c: conditions){
				conditionText += c.toString();
				conditionText +="\n";
			}
			//set the condition text
			ui.setText(find(CONDITIONS_TEXT_AREA), conditionText);
			//set the message text
			ui.setText(find(MESSAGE_TEXT_AREA), f.getMessage());	
		}
	}
	
	public void addFlag(){
		
	}
	
	public void removeFlag(){
		
	}
	
	public void editFlag(){
		
	}
	
	public void formSelectionChanged(){
		
	}
	
	public void anyAllChoiceChanged(){
		
	}
	
	public void addCondition(){
		
	}
	
	public void editCondition(){
		
	}
	
	public void removeCondition(){
		
	}
	
	public void saveFlag(){
		
	}
	
	public void editFlagCancelled(){
		
	}
	
	
}