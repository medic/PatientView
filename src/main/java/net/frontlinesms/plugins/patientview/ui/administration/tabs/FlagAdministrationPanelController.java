package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.repository.GroupDao;
import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagCondition;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.repository.FlagConditionDao;
import net.frontlinesms.plugins.patientview.data.repository.FlagDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.administration.EditConditionPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FlagAdministrationPanelController extends AdministrationTabPanel {

	private static final String MAIN_XML = "/ui/plugins/patientview/administration/flags/flagAdministrationPanel.xml";
	private static final String DISPLAY_FLAG_XML = "/ui/plugins/patientview/administration/flags/displayFlag.xml";
	private static final String EDIT_FLAG_XML = "/ui/plugins/patientview/administration/flags/editFlag.xml";
	private static final String CONDITION_BUTTONS_XML = "/ui/plugins/patientview/administration/flags/conditionButtons.xml";
	
	private boolean isEditing;
	
	private static final String FLAG_LIST = "flagList";
	private static final String ACTION_PANEL = "flagActionPanel";
	
	private static final String REMOVE_FLAG_BUTTON = "removeFlagButton";
	private static final String ADD_FLAG_BUTTON = "addFlagButton";
	
	private static final String FLAG_NAME_LABEL = "flagNameLabel";
	private static final String FLAG_NAME_FIELD = "flagNameField";
	private static final String CONDITION_LIST = "conditionList";
	private static final String EDIT_CONDITION_BUTTON = "editConditionButton";
	private static final String REMOVE_CONDITION_BUTTON = "removeConditionButton";
	
	private static final String CONDITIONS_TEXT_AREA = "conditionsTextArea";
	private static final String MESSAGE_TEXT_AREA = "messageTextArea";
	private static final String FORM_SELECT = "formSelect";
	private static final String ANY_OR_ALL_SELECT = "anyAllSelect";
	private static final String FORM_LABEL = "formLabel";
	private static final String CONFIRMATION_DIALOG = "confirmDialog";
	private static final String CONDITIONS_PANEL = "conditionsPanel";
//	private static final String CONDITION_BUTTONS_PANEL = 	"conditionButtonPanel";
	private static final String CONDITION_ACTIONS_PANEL = "conditionActionsPanel";
	private static final String MESSAGE_FIELD_SELECT = "messageFieldSelect";
	private static final String CONTACT_GROUP_SELECT = "contactGroupSelect";
	
	private static final String CANNOT_BE_EMPTY = getI18nString("medic.flags.fields.cannot.be.empty");
	private static final String THE_MESSAGE_TEXT = getI18nString("medic.flags.fields.message.text");
	private static final String THE_FLAG_NAME = getI18nString("medic.flags.fields.flag.name");
	private static final String ADD = getI18nString("medic.common.labels.add");
	private static final String CONDITIONS_MET = getI18nString("medic.flags.conditions.met");
	private static final String ALL = getI18nString("medic.flags.all");
	private static final String ANY = getI18nString("medic.flags.any");;
	private static final String IF = getI18nString("medic.flags.if");
	private static final String FORM_COLON = getI18nString("medic.common.form.colon");
	private static final String NO_FLAGS = getI18nString("medic.flags.none");
	private static final String FLAGS = getI18nString("medic.flags");
	
	
	private FlagDao flagDao;
	private FlagConditionDao conditionDao;
	private MedicFormDao formDao;
	private GroupDao groupDao;
	private ArrayList<FlagCondition> toRemove;
	
	public FlagAdministrationPanelController(UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, appCon, MAIN_XML);
		flagDao = (FlagDao) appCon.getBean("FlagDao");
		conditionDao = (FlagConditionDao) appCon.getBean("FlagConditionDao");
		formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		groupDao = (GroupDao) appCon.getBean("groupDao");
		isEditing = false;
		toRemove = new ArrayList<FlagCondition>();
		refreshFlagList(null);
	}

	@Override
	public String getIconPath() {
		return "/icons/flag_purple.png";
	}

	@Override
	public String getListItemTitle() {
		return FLAGS;
	}
	
	public Flag getSelectedFlag(){
		return (Flag) ui.getAttachedObject(ui.getSelectedItem(find(FLAG_LIST)));
	}
	
	public MedicForm getSelectedForm(){
		return (MedicForm) ui.getAttachedObject(ui.getSelectedItem(find(FORM_SELECT)));
	}
	
	public FlagCondition getSelectedCondition(){
		return ui.getAttachedObject(ui.getSelectedItem(find(CONDITION_LIST)),FlagCondition.class);
	}
	
	/**
	 * loads all the flags from the database 
	 * and displays them in the flag list. Maintains selection
	 * if possible.
	 */
	public void refreshFlagList(Flag toSelect){
		int beforeIndex = ui.getSelectedIndex(find(FLAG_LIST));
		if(beforeIndex < 0) beforeIndex = 0;
		removeAll(find(FLAG_LIST));
		List<Flag> flags = flagDao.getAllFlags();
		int index = 0;
		for(Flag flag: flags){
			add(find(FLAG_LIST),ui.createListItem(flag.getName(),flag));
			if(toSelect != null && toSelect.getFid() == flag.getFid()){
				ui.setSelectedIndex(find(FLAG_LIST), index);
			}
			index++;
		}
		
		if(flags.size() == 0){
			add(find(FLAG_LIST),ui.createListItem(NO_FLAGS,null));
			ui.setEnabled(find(REMOVE_FLAG_BUTTON), false);
		}else{
			ui.setEnabled(find(REMOVE_FLAG_BUTTON), true);
		}
		
		if(toSelect == null){
			if(flags.size() > beforeIndex){
				ui.setSelectedIndex(find(FLAG_LIST), beforeIndex);
			}else{
				ui.setSelectedIndex(find(FLAG_LIST), flags.size()-1);
			}
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
			ui.setText(find(FORM_LABEL), FORM_COLON+" "+f.getForm().getName());
			//create the condition text
			String conditionText = IF+" " + (f.isAny()?ANY:ALL) + " "+ CONDITIONS_MET+":\n\t";
			List<FlagCondition> conditions = conditionDao.getConditionsForFlag(f);
			for(FlagCondition c: conditions){
				conditionText += c.toString();
				conditionText +="\n\t";
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
		if(formDao.getAllMedicForms().isEmpty()){
			ui.alert("Flags require forms - you must create at least one form before you can begin creating flags.");
			return;
		}
		isEditing = false;
		removeAll(find(ACTION_PANEL));
		add(find(ACTION_PANEL),ui.loadComponentFromFile(EDIT_FLAG_XML, this));
		populateFormSelect(null);
		populateContactGroupSelect(null);
		populateMessageFieldSelect();
		ui.requestFocus(find(FLAG_NAME_FIELD));
		ui.setEnabled(find(EDIT_CONDITION_BUTTON), false);
		ui.setEnabled(find(REMOVE_CONDITION_BUTTON), false);
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
			refreshFlagList(null);
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
			populateContactGroupSelect(f.getContactGroup());
			populateConditions(f);
			ui.setText(find(MESSAGE_TEXT_AREA), f.getMessage());
			if(f.getConditions().size() == 0){
				ui.setEnabled(find(EDIT_CONDITION_BUTTON), false);
				ui.setEnabled(find(REMOVE_CONDITION_BUTTON), false);
			}else{
				ui.setSelectedIndex(find(CONDITION_LIST),0);
			}
			populateMessageFieldSelect();
		}
	}
	
	private void populateContactGroupSelect(Group contactGroup) {
		Object groupSelect = ui.find(mainPanel,CONTACT_GROUP_SELECT);
		ui.removeAll(groupSelect);
		List<Group> groups = groupDao.getAllGroups();
		int toSelect = -1;
		for(int i = 0; i < groups.size(); i++){
			Group group = groups.get(i);
			if(group.equals(contactGroup)){
				toSelect =  i;
			}
			ui.add(groupSelect,ui.createComboboxChoice(group.getName(), group));		
		}
		if(toSelect == -1 && groups.size() > 0){
			toSelect = 0;
		}
		ui.setSelectedIndex(groupSelect, toSelect);
	}

	private void populateConditions(Flag f){
		for(FlagCondition c: f.getConditions()){
			add(find(CONDITION_LIST),ui.createListItem(c.toString(), c));
		}
	}
	
	public void formSelectionChanged(){
		if(ui.getItems(find(CONDITION_LIST)).length > 0){
			ui.showConfirmationDialog("formChangeConfirmed()", this, "medic.flag.confirm.form.switch");
		}else{
			formChangeConfirmed();
		}
	}
	
	public void formChangeConfirmed(){
		ui.remove(ui.find(CONFIRMATION_DIALOG));
		ui.removeAll(find(CONDITION_LIST));
		conditionEditingCancelled();
		ui.setEnabled(find(EDIT_CONDITION_BUTTON), false);
		ui.setEnabled(find(REMOVE_CONDITION_BUTTON), false);
		populateMessageFieldSelect();
	}
	
	public void addCondition(){
		EditConditionPanel p = new EditConditionPanel(ui, appCon, getSelectedForm(), null,this);
		ui.setText(find(p.getMainPanel(),"saveEditingCondition"), ADD);
		ui.removeAll(find(CONDITION_ACTIONS_PANEL));
		add(find(CONDITION_ACTIONS_PANEL),p.getMainPanel());
	}
	
	public void editCondition(){
		EditConditionPanel p = new EditConditionPanel(ui, appCon, getSelectedForm(), getSelectedCondition(),this);
		ui.removeAll(find(CONDITION_ACTIONS_PANEL));
		add(find(CONDITION_ACTIONS_PANEL),p.getMainPanel());
	}
	
	public void removeCondition(){
		Object selected = ui.getSelectedItem(find(CONDITION_LIST));
		if(isEditing)
			toRemove.add(ui.getAttachedObject(selected,FlagCondition.class));
		int index = ui.getSelectedIndex(find(CONDITION_LIST));
		index = Math.min(index, ui.getItems(find(CONDITION_LIST)).length-2);
		ui.remove(selected);
		if(ui.getItems(find(CONDITION_LIST)).length == 0){
			ui.setEnabled(find(EDIT_CONDITION_BUTTON), false);
			ui.setEnabled(find(REMOVE_CONDITION_BUTTON), false);
		}else{
			ui.setSelectedIndex(find(CONDITION_LIST), index);
		}
	}
	
	public void conditionCreated(FlagCondition c){
		//add the condition to the panel
		boolean alreadyExisted=false;
		Object[] kids = ui.getItems(find(CONDITION_LIST));
		int index = 0;
		
		for(Object item: kids){
			if(ui.getAttachedObject(item,FlagCondition.class) == c){
				ui.setText(item, c.toString());
				ui.setSelectedIndex(find(CONDITION_LIST),index);
				alreadyExisted=true;
				break;
			}
			index++;
		}
		if(!alreadyExisted){
			Object listItem = ui.createListItem(c.toString(),c);
			add(find(CONDITION_LIST),listItem);
			ui.setSelectedItem(find(CONDITION_LIST), listItem);
		}
		conditionEditingCancelled();
		ui.setEnabled(find(EDIT_CONDITION_BUTTON), true);
		ui.setEnabled(find(REMOVE_CONDITION_BUTTON), true);
	}
	
	public void conditionEditingCancelled(){
		ui.removeAll(find(CONDITION_ACTIONS_PANEL));
		ui.add(find(CONDITION_ACTIONS_PANEL),ui.loadComponentFromFile(CONDITION_BUTTONS_XML, this));
	}
	
	public void saveFlag(){
		if(!checkField(FLAG_NAME_FIELD,THE_FLAG_NAME)) return;
		if(!checkField(MESSAGE_TEXT_AREA,THE_MESSAGE_TEXT)) return;
		String name = ui.getText(find(FLAG_NAME_FIELD));
		String message = ui.getText(find(MESSAGE_TEXT_AREA));
		Group contactGroup = (Group) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,CONTACT_GROUP_SELECT)));
		boolean any = ui.getSelectedIndex(find(ANY_OR_ALL_SELECT)) == 0;
		Flag newFlag;
		if(isEditing){
			newFlag = getSelectedFlag();
			newFlag.setName(name);
			newFlag.setMessage(message);
			newFlag.setContactGroup(contactGroup);
			newFlag.setAny(any);
			Object[] conditions = ui.getItems(find(CONDITION_LIST));
			for(Object item: conditions){
				FlagCondition fc = (FlagCondition) ui.getAttachedObject(item);
				if(fc.getCid() == -1L){
					newFlag.addCondition(fc);
				}
			}
			for(FlagCondition remove: toRemove){
				if(remove.getCid() > 0)
					newFlag.removeCondition(remove);
			}
			flagDao.updateFlag(newFlag);
			for(FlagCondition remove: toRemove){
				if(remove.getCid() > 0)
					conditionDao.deleteCondition(remove);
			}
			toRemove = new ArrayList<FlagCondition>();
		}else{
			MedicForm mf = (MedicForm) ui.getAttachedObject(ui.getSelectedItem(find(FORM_SELECT)));
			newFlag = new Flag(name, mf);
			newFlag.setMessage(message);
			newFlag.setContactGroup(contactGroup);
			newFlag.setAny(any);
			Object[] conditions = ui.getItems(find(CONDITION_LIST));
			for(Object item: conditions){
				newFlag.addCondition((FlagCondition<?>) ui.getAttachedObject(item));
			}
			flagDao.saveFlag(newFlag);
		}
		isEditing = false;
		refreshFlagList(newFlag);
	}
	
	private boolean checkField(String thinletFieldName, String fieldName){
		String contents = ui.getText(find(thinletFieldName));
		if(contents == null || contents.equals("")){
			ui.alert(fieldName+ " "+ CANNOT_BE_EMPTY);
			return false;
		}else{
			return true;
		}
	}
	
	public void editFlagCancelled(){
		isEditing = false;
		toRemove = new ArrayList<FlagCondition>();
		flagListSelectionChanged();
	}
	
	public void insertMessageText(String textToInsert){
		String messageText= ui.getText(find(MESSAGE_TEXT_AREA));
		Integer cursorIndex = ui.getCaretPosition(find(MESSAGE_TEXT_AREA));
		if(messageText == null || messageText.trim().equals("") || cursorIndex == null){
			ui.setText(find(MESSAGE_TEXT_AREA), textToInsert);
		}else{
			String insertText = messageText.substring(0, cursorIndex) + textToInsert + messageText.substring(cursorIndex);
			ui.setText(find(MESSAGE_TEXT_AREA), insertText);
		}
		ui.setFocus(find(MESSAGE_TEXT_AREA));
		ui.setCaretPosition(find(MESSAGE_TEXT_AREA), cursorIndex+textToInsert.length());
	}
	
	private void populateMessageFieldSelect(){
		ui.removeAll(find(MESSAGE_FIELD_SELECT));
		MedicForm mf = ui.getAttachedObject(ui.getSelectedItem(find(FORM_SELECT)),MedicForm.class);
		for(MedicFormField field: mf.getFields()){
			Object item = ui.createComboboxChoice(field.getLabel(), field);
			ui.add(find(MESSAGE_FIELD_SELECT),item);
		}
		ui.setSelectedIndex(find(MESSAGE_FIELD_SELECT), 0);
	}
	
	public void messageFieldSelectionChanged(){
		String label = ui.getAttachedObject(ui.getSelectedItem(find(MESSAGE_FIELD_SELECT)),MedicFormField.class).getLabel();
		insertMessageText("{"+label +"}");
	}
}