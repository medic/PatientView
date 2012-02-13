package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagCondition;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.repository.FlagConditionDao;
import net.frontlinesms.plugins.patientview.data.repository.FlagDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagAdministrationPanelController extends AdministrationTabPanel {

	private static final String MAIN_XML = "/ui/plugins/patientview/administration/flags/flagAdministrationPanel.xml";
	private static final String DISPLAY_FLAG_XML = "/ui/plugins/patientview/administration/flags/displayFlag.xml";
	private static final String EDIT_FLAG_XML = "/ui/plugins/patientview/administration/flags/editFlag.xml";
	
	private boolean isEditing;
	
	private static final String FLAG_LIST = "flagList";
	private static final String ACTION_PANEL = "flagActionPanel";
	
	private static final String REMOVE_FLAG_BUTTON = "removeFlagButton";
	private static final String ADD_FLAG_BUTTON = "addFlagButton";
	
	private static final String FLAG_NAME_LABEL = "flagNameLabel";
	private static final String FLAG_NAME_FIELD = "flagNameField";
	private static final String CONDITION_LIST = "conditionList";

	private static final String CONDITIONS_TEXT_AREA = "conditionsTextArea";
	private static final String MESSAGE_TEXT_AREA = "messageTextArea";
	private static final String FORM_SELECT = "formSelect";
	private static final String ANY_OR_ALL_SELECT = "anyAllSelect";
	private static final String FORM_LABEL = "formLabel";
	private static final String CONFIRMATION_DIALOG = "confirmDialog";

	private FlagDao flagDao;
	private FlagConditionDao conditionDao;
	private MedicFormDao formDao;
	
	public FlagAdministrationPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon, MAIN_XML);
		flagDao = (FlagDao) appCon.getBean("FlagDao");
		conditionDao = (FlagConditionDao) appCon.getBean("FlagConditionDao");
		formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
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
	
	/**
	 * loads all the flags
	 * from the database and displays them
	 * in the flag list. Maintains selection
	 * if possible.
	 */
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
		if(flags.size() > beforeIndex){
			ui.setSelectedIndex(find(FLAG_LIST), beforeIndex);
		}else{
			ui.setSelectedIndex(find(FLAG_LIST), flags.size()-1);
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
			ui.setText(find(FORM_LABEL), "Form: "+f.getForm().getName());
			//create the condition text
			String conditionText = "If " + (f.isAny()?"any":"all") + " of these conditions are met:\n";
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
	
	/**
	 * Displays the panels for adding
	 * a flag.
	 */
	public void addFlag(){
		isEditing = false;
		removeAll(find(ACTION_PANEL));
		add(find(ACTION_PANEL),ui.loadComponentFromFile(EDIT_FLAG_XML, this));
		populateFormSelect(null);
		ui.requestFocus(find(FLAG_NAME_FIELD));
	}
	
	private void populateFormSelect(MedicForm toSelect){
		List<MedicForm> forms = formDao.getAllMedicForms();
		removeAll(find(FORM_SELECT));
		int index = 0,i=0;
		for(MedicForm f:forms){
			add(find(FORM_SELECT),ui.createComboboxChoice(f.getName(),f));
			if(toSelect != null && f.getFid() == toSelect.getFid())
				index = i;
			i++;
		}
		ui.setSelectedIndex(find(FORM_SELECT), index);
	}
	
	public void removeFlag(int firstTime){
		if(firstTime == 1){
			ui.showConfirmationDialog("removeFlag(0)", this,"medic.flag.confirm.delete");
		}else{
			isEditing = false;
			// delete the reminder
			flagDao.deleteFlag(getSelectedFlag());
			// refresh the list
			refreshFlagList();
			// remove the confirmation dialog
			ui.remove(ui.find(CONFIRMATION_DIALOG));
		}
	}
	
	public void editFlag(){
		isEditing = true;
		removeAll(find(ACTION_PANEL));
		Flag f = getSelectedFlag();
		if(f != null){
			add(find(ACTION_PANEL),ui.loadComponentFromFile(EDIT_FLAG_XML, this));
			ui.setEnabled(find(FORM_SELECT),false);
			ui.setText(find(FLAG_NAME_FIELD), f.getName());
			ui.setSelectedIndex(find(ANY_OR_ALL_SELECT), f.isAny()?0:1);
			populateFormSelect(f.getForm());
			populateConditions(f);
			ui.setText(find(MESSAGE_TEXT_AREA), f.getMessage());
		}
	}
	
	private void populateConditions(Flag f){
		List<FlagCondition> conditions = conditionDao.getConditionsForFlag(f);
		for(FlagCondition c: conditions){
			add(find(CONDITION_LIST),ui.createListItem(c.toString(), c));
		}
	}
	
	public void formSelectionChanged(){
		
	}
	
	public void addCondition(){
		
	}
	
	public void editCondition(){
		
	}
	
	public void removeCondition(){
		
	}
	
	public void saveFlag(){
		if(!checkField(FLAG_NAME_FIELD,"The flag name")) return;
		if(!checkField(MESSAGE_TEXT_AREA,"The message text")) return;
		String name = ui.getText(find(FLAG_NAME_FIELD));
		String message = ui.getText(find(MESSAGE_TEXT_AREA));
		boolean any = ui.getSelectedIndex(find(ANY_OR_ALL_SELECT)) == 0;
		if(isEditing){
			Flag f = getSelectedFlag();
			f.setName(name);
			f.setMessage(message);
			f.setAny(any);
			//update conditions?
			flagDao.updateFlag(f);
		}else{
			MedicForm mf = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(find(FORM_SELECT)));
			Flag f = new Flag(name, mf);
			f.setMessage(message);
			f.setAny(any);
			//update conditions?
			flagDao.saveFlag(f);
		}
		isEditing = false;
		refreshFlagList();
	}
	
	private boolean checkField(String thinletFieldName, String fieldName){
		String contents = ui.getText(find(thinletFieldName));
		if(contents == null || contents.equals("")){
			ui.alert(fieldName+ " cannot be empty.");
			return false;
		}else{
			return true;
		}
	}
	
	public void editFlagCancelled(){
		isEditing = false;
		flagListSelectionChanged();
	}
	
	
}