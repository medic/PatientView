package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.repository.GroupDao;
import net.frontlinesms.plugins.patientview.data.domain.reminder.EventTimingOption;
import net.frontlinesms.plugins.patientview.data.domain.reminder.RecurringReminderFrequency;
import net.frontlinesms.plugins.patientview.data.domain.reminder.Reminder;
import net.frontlinesms.plugins.patientview.data.domain.reminder.ReminderEvent;
import net.frontlinesms.plugins.patientview.data.domain.reminder.event.ReminderEventDirectory;
import net.frontlinesms.plugins.patientview.data.domain.reminder.impl.OneTimeReminder;
import net.frontlinesms.plugins.patientview.data.domain.reminder.impl.RecurringReminder;
import net.frontlinesms.plugins.patientview.data.repository.ReminderDao;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class ReminderAdministrationPanelController extends AdministrationTabPanel{

	private static String THINLET_XML = "/ui/plugins/patientview/administration/reminders/reminderAdministrationPanel.xml";
	private static String DISPLAY_REMINDER_XML = "/ui/plugins/patientview/administration/reminders/displayReminderPanel.xml";
	private static String EDIT_REMINDER_XML = "/ui/plugins/patientview/administration/reminders/editReminderPanel.xml";
	private static String RECURRING_TIMING_PANEL_XML = "/ui/plugins/patientview/administration/reminders/recurringTimingPanel.xml";
	private static String ONE_TIME_TIMING_PANEL_XML = "/ui/plugins/patientview/administration/reminders/oneTimeTimingPanel.xml";
	
	private static final String REMINDER_LIST = "reminderList";
	private static final String ACTION_PANEL = "actionPanel";
	private static final String REMINDER_NAME_LABEL = "reminderNameLabel";
	private static final String TIMING_TEXT_AREA = "timingTextArea";
	private static final String MESSAGE_TEXT_AREA = "messageTextArea";
	private static final String MESSAGE_PANEL = "messagePanel";
	private static final String MESSAGE_VARIABLE_PANEL = "variableButtonPanel";
	private static final String RECIPIENT_SELECT = "recipientSelect";
	private static final String TIMING_PANEL = "timingPanel";
	private static final String REMINDER_TYPE_SELECT = "typeSelect";
	private static final String FROM_EVENT_SELECT = "fromEventSelect";
	private static final String TO_EVENT_SELECT = "toEventSelect";
	private static final String FROM_DATE_OPTION_SELECT = "fromDateOptionSelect";
	private static final String TO_DATE_OPTION_SELECT = "toDateOptionSelect";
	private static final String RECCURANCE_OPTION_SELECT = "recurringOptionSelect";
	private static final String EXTENDED_FROM_PANEL = "extendedFromPanel";
	private static final String EXTENDED_TO_PANEL = "extendedToPanel";
	
	private static final String REMINDER_NAME_FIELD = "reminderNameField";
	private static final String FROM_MONTHS_FIELD = "fromMonthsField";
	private static final String FROM_DAYS_FIELD = "fromDaysField";
	private static final String TO_MONTHS_FIELD = "toMonthsField";
	private static final String TO_DAYS_FIELD = "toDaysField";
	private static final String TIME_OF_DAY_FIELD = "timeOfDayField";
	
	private static final String CONFIRMATION_DIALOG = "confirmDialog";
	private static final String REMOVE_REMINDER_BUTTON = "removeReminderButton";
	private static final String EDIT_REMINDER_BUTTON = "removeReminderButton";
	
	private static final String CANNOT_BE_EMPTY = getI18nString("medic.reminder.field.cannot.be.empty");
	private static final String THE_TO_DAYS_FIELD = getI18nString("medic.reminder.field.to.days");
	private static final String THE_TO_MONTHS_FIELD = getI18nString("medic.reminder.field.to.months");
	private static final String MESSAGE_FIELD = getI18nString("medic.reminder.field.message");
	private static final String THE_FROM_DAYS_FIELD = getI18nString("medic.reminder.field.from.days");
	private static final String THE_FROM_MONTHS_FIELD = getI18nString("medic.reminder.field.from.months");
	private static final String NAME_FIELD = getI18nString("medic.reminder.field.name");
	private static final String MANAGE_REMINDERS = getI18nString("medic.reminders.manage");
	private static final String TO_THE = getI18nString("medic.reminder.to.the");
	private static final String PATIENT = getI18nString("medic.common.patient");
	private static final String PATIENTS_CHW = getI18nString("medic.reminder.destination.patients.chw");
	private static final String NO_REMINDERS = getI18nString("medic.reminders.none");
	
	private ReminderDao reminderDao;
	private GroupDao groupDao;
	
	private boolean isEditing;
	
	public ReminderAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon,THINLET_XML);
		reminderDao = (ReminderDao) appCon.getBean("ReminderDao");
		groupDao = (GroupDao) appCon.getBean("groupDao");
		refreshReminderList(null);
	}
	
	public void refreshReminderList(Long toSelect){
		int beforeIndex = ui.getSelectedIndex(find(REMINDER_LIST));
		if(beforeIndex < 0) beforeIndex = 0;
		removeAll(find(REMINDER_LIST));
		List<Reminder> reminders = reminderDao.getAllReminders();
		int index = 0;
		for(Reminder reminder: reminders){
			add(find(REMINDER_LIST),ui.createListItem(reminder.getName(),reminder));
			if(toSelect != null && toSelect == reminder.getReminderId()){
				ui.setSelectedIndex(find(REMINDER_LIST), index);
			}
			index++;
		}
		if(reminders.size() == 0){
			add(find(REMINDER_LIST),ui.createListItem(NO_REMINDERS,null));
			ui.setEnabled(find(REMOVE_REMINDER_BUTTON), false);
			ui.setEnabled(find(EDIT_REMINDER_BUTTON), false);
		}else{
			ui.setEnabled(find(REMOVE_REMINDER_BUTTON), true);
			ui.setEnabled(find(EDIT_REMINDER_BUTTON), true);
		}
		if(toSelect == null){
			if(reminders.size() > beforeIndex){
				ui.setSelectedIndex(find(REMINDER_LIST), beforeIndex);
			}else{
				ui.setSelectedIndex(find(REMINDER_LIST), reminders.size()-1);
			}
		}
		showReminderDisplayView();
	}
	
	public void reminderSelectionChanged(){
		showReminderDisplayView();
	}

	private Reminder getSelectedReminder(){
		return (Reminder) ui.getAttachedObject(ui.getSelectedItem(find(REMINDER_LIST)));
	}
	
	private void showReminderDisplayView() {
		isEditing = false;
		removeAll(find(ACTION_PANEL));
		if(getSelectedReminder() != null){
			add(find(ACTION_PANEL),ui.loadComponentFromFile(DISPLAY_REMINDER_XML, this));
			ui.setText(find(REMINDER_NAME_LABEL), getSelectedReminder().getName());
			ui.setText(find(TIMING_TEXT_AREA), getSelectedReminder().getTimingString());
			ui.setText(find(MESSAGE_TEXT_AREA), TO_THE+" contacts in the group \""+ 
				getSelectedReminder().getContactGroup().getName()+"\":\n\n" +
				getSelectedReminder().getMessageFormat());
		}
	}

	@Override
	public String getIconPath() {
		return "/icons/reminder.png";
	}

	@Override
	public String getListItemTitle() {
		return MANAGE_REMINDERS;
	}
	
	public void addReminder(){
		isEditing = false;
		removeAll(find(ACTION_PANEL));
		add(find(ACTION_PANEL),ui.loadComponentFromFile(EDIT_REMINDER_XML, this));
		populateReminderTypeSelect(null,true);
		populateNewReminderTimingPanel();
		ui.requestFocus(find(REMINDER_NAME_FIELD));
	}
	
	private void populateReminderTypeSelect(Class<?> toSelect , boolean enabled){
		removeAll(find(REMINDER_TYPE_SELECT));
		boolean shouldSelect = (toSelect == null);
		int count = 0;
		for(Class<?> reminder: getReminderTypes()){
			Reminder r = null;
			try {
				r = (Reminder) reminder.newInstance();
			} catch (Exception e) {e.printStackTrace(); } 
			add(find(REMINDER_TYPE_SELECT),ui.createComboboxChoice(r.getTypeName(), reminder));
			if(shouldSelect || (toSelect != null && toSelect.equals(reminder))){
				ui.setSelectedIndex(find(REMINDER_TYPE_SELECT), count);
				shouldSelect = false;
			}
			count++;
		}
		ui.setEnabled(find(REMINDER_TYPE_SELECT), enabled);
	}
	
	public void reminderTypeSelectionChanged(){
		isEditing = false;
		populateNewReminderTimingPanel();
	}
	
	private void populateNewReminderTimingPanel(){
		removeAll(find(TIMING_PANEL));
		if(getSelectedReminderType().equals(RecurringReminder.class)){
			add(find(TIMING_PANEL),ui.loadComponentFromFile(RECURRING_TIMING_PANEL_XML, this));
			populateEventOptionSelect(true,null);
			populateEventOptionSelect(false,null);
			populateDateOptionSelect(true,null);
			populateDateOptionSelect(false,null);
			populateRecurrenceOptionSelect(null);
		}else if(getSelectedReminderType().equals(OneTimeReminder.class)){
			add(find(TIMING_PANEL),ui.loadComponentFromFile(ONE_TIME_TIMING_PANEL_XML, this));
			populateEventOptionSelect(true,null);
			populateDateOptionSelect(true,null);
		}
		populateRecipientSelect(null);
	}
	
	private void populateRecipientSelect(Group contactGroup) {
		Object groupSelect = ui.find(mainPanel,RECIPIENT_SELECT);
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

	private void populateEventOptionSelect(boolean fromEvent, Class<?> toSelect){
		String eventSelect = fromEvent?FROM_EVENT_SELECT:TO_EVENT_SELECT;
		//get the currently selected reminder type
		Reminder r=getSelectedReminderInstance();
		if(r == null) return;
		//clear the selects
		removeAll(find(eventSelect));
		//add the options
		boolean shouldSelect =(toSelect == null);
		int count = 0;
		boolean selected = false;
		for(ReminderEvent event: fromEvent?ReminderEventDirectory.getStartEvents():ReminderEventDirectory.getEndEvents()){
			if(r.supportsEvent(event)){
				//if the event  is supported, add it
				add(find(eventSelect),ui.createComboboxChoice(event.getSnippet(), event));
				//if this is the first iteration, select the option
				if(shouldSelect || (toSelect != null && toSelect.equals(event.getClass()))){
					ui.setSelectedIndex(find(eventSelect), count);
					shouldSelect = false;
					selected = true;
				}
				count++;
			}
		}	
		if(!selected){
			ui.setSelectedIndex(find(eventSelect), 0);
		}
		updateMessageVariables();
	}
	
	private void updateMessageVariables(){
		removeAll(find(MESSAGE_VARIABLE_PANEL));
		ReminderEvent startEvent = (ReminderEvent) ui.getAttachedObject(ui.getSelectedItem(find(FROM_EVENT_SELECT)));
		Map<String,String> variables = startEvent.getVariables();
		Object panel = find(MESSAGE_VARIABLE_PANEL);
		for(Entry<String,String> tuple: variables.entrySet()){
			Object button = ui.createButton(tuple.getKey(), "insertMessageText('"+tuple.getValue()+"')", null, this);
			ui.add(panel,button);
		}
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
	
	public void fromEventOptionChanged(){
		EventTimingOption currentOption = (EventTimingOption) ui.getAttachedObject(ui.getSelectedItem(find(FROM_DATE_OPTION_SELECT)));
		populateDateOptionSelect(true,currentOption);
		updateMessageVariables();
	}
	
	public void toEventOptionChanged(){
		EventTimingOption currentOption = (EventTimingOption) ui.getAttachedObject(ui.getSelectedItem(find(TO_DATE_OPTION_SELECT)));
		populateDateOptionSelect(false,currentOption);
		updateMessageVariables();
	}

	private void populateDateOptionSelect(boolean fromOption,EventTimingOption toSelect) {
		String eventSelect = fromOption ? FROM_EVENT_SELECT : TO_EVENT_SELECT;
		String dateOption = fromOption ? FROM_DATE_OPTION_SELECT: TO_DATE_OPTION_SELECT;
		//take out all the current options
		removeAll(find(dateOption));
		//get the reminder event
		ReminderEvent<?> rEvent = (ReminderEvent<?>) ui.getAttachedObject(ui.getSelectedItem(find(eventSelect)));
		rEvent = (ReminderEvent<?>) ui.getAttachedObject(ui.getSelectedItem(find(eventSelect)));
		if (rEvent == null) return;
		//add all the supported timing options
		boolean shouldSelect = (toSelect == null);
		int count = 0;
		boolean selected = false;
		for (EventTimingOption option : rEvent.getSupportedTimingOptions()) {
			add(find(dateOption), ui.createComboboxChoice(option.name, option));
			//if this is the first one, select it
			if (shouldSelect || (toSelect != null && toSelect.equals(option))) {
				ui.setSelectedIndex(find(dateOption), count);
				shouldSelect = false;
				selected = true;
			}
			count++;
		}
		if(!selected){
			ui.setSelectedIndex(find(dateOption), 0);
		}
		if(fromOption){
			fromDateOptionChanged();
		}else{
			toDateOptionChanged();
		}
	}
	
	public void fromDateOptionChanged(){
		EventTimingOption option = (EventTimingOption) ui.getAttachedObject(ui.getSelectedItem(find(FROM_DATE_OPTION_SELECT)));
		if(option.equals(EventTimingOption.DAY_OF)){
			ui.setVisible(find(EXTENDED_FROM_PANEL), false);
		}else{
			ui.setVisible(find(EXTENDED_FROM_PANEL), true);
		}
	}
	
	public void toDateOptionChanged(){
		EventTimingOption option = (EventTimingOption) ui.getAttachedObject(ui.getSelectedItem(find(TO_DATE_OPTION_SELECT)));
		if(option.equals(EventTimingOption.DAY_OF)){
			ui.setVisible(find(EXTENDED_TO_PANEL), false);
		}else{
			ui.setVisible(find(EXTENDED_TO_PANEL), true);
		}
	}
	
	private void populateRecurrenceOptionSelect(RecurringReminderFrequency toSelect){
		if(getSelectedReminderInstance() != null && getSelectedReminderInstance() instanceof RecurringReminder){
			removeAll(find(RECCURANCE_OPTION_SELECT));
			boolean shouldSelect = (toSelect == null);
			int count = 0;
			for(RecurringReminderFrequency frequency:RecurringReminderFrequency.values()){
				add(find(RECCURANCE_OPTION_SELECT),ui.createComboboxChoice(frequency.getName(), frequency));
				if(shouldSelect || (toSelect != null && toSelect.equals(frequency))){
					ui.setSelectedIndex(find(RECCURANCE_OPTION_SELECT), count);
					shouldSelect=false;
				}
				count++;
			}
		}
	}
	
	public void removeReminder(){
		ui.showConfirmationDialog("removeReminderConfirmed()", this,"medic.reminder.confirm.delete");
	}
	
	public void saveReminder(){
		String name=null,fromMonthsString=null,fromDaysString=null,message = null;
		Group contactGroup = null;
		Reminder r = getSelectedReminderInstance();
		//create and check all the field data
		if(checkField(REMINDER_NAME_FIELD, NAME_FIELD)){
			name = ui.getText(find(REMINDER_NAME_FIELD));
		}else return;
		if(checkField(FROM_MONTHS_FIELD, THE_FROM_MONTHS_FIELD)){
			fromMonthsString = ui.getText(find(FROM_MONTHS_FIELD));
		}else return;
		if(checkField(FROM_DAYS_FIELD, THE_FROM_DAYS_FIELD)){
			fromDaysString = ui.getText(find(FROM_DAYS_FIELD));
		}else return;
		if(checkField(MESSAGE_TEXT_AREA, MESSAGE_FIELD)){
			message = ui.getText(find(MESSAGE_TEXT_AREA));
		}else return;
		if(ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,RECIPIENT_SELECT))) == null){
			ui.alert("You must select a contact group to send this reminder to.");
			return;
		}
		contactGroup = (Group) ui.getAttachedObject(ui.getSelectedItem(ui.find(mainPanel,RECIPIENT_SELECT)));
		//get the from event
		Class<?> fromEvent = ui.getAttachedObject(ui.getSelectedItem(find(FROM_EVENT_SELECT))).getClass();
		//correctly create the time of day, start days, and start months
		int multiplier = ((EventTimingOption) ui.getAttachedObject(ui.getSelectedItem(find(FROM_DATE_OPTION_SELECT)))).multiplier;
		int timeOfDay, fromDays, fromMonths;
		timeOfDay = ui.getSelectedIndex(find(TIME_OF_DAY_FIELD));
		fromDays = Integer.parseInt(fromDaysString)* multiplier;
		fromMonths = Integer.parseInt(fromMonthsString)* multiplier;
		//if its a onetimer, just save it
		Long reminderId = null;
		if(!(r instanceof RecurringReminder)){
			OneTimeReminder oneTime;
			if(!isEditing){
				oneTime = new OneTimeReminder();
			}else{
				oneTime = (OneTimeReminder) getSelectedReminder();
			}
			oneTime.setMessageFormat(message);
			oneTime.setName(name);
			oneTime.setTimeOfDay(timeOfDay);
			oneTime.setStartCriteria(fromEvent, fromDays, fromMonths);
			oneTime.setContactGroup(contactGroup);
			reminderDao.saveOrUpdateReminder(oneTime);
			reminderId = oneTime.getReminderId();
		}else if(r instanceof RecurringReminder){
			String toMonthsString=null,toDaysString=null;
			if(checkField(TO_MONTHS_FIELD, THE_TO_MONTHS_FIELD)){
				toMonthsString = ui.getText(find(TO_MONTHS_FIELD));
			}else return;
			if(checkField(TO_DAYS_FIELD, THE_TO_DAYS_FIELD)){
				toDaysString = ui.getText(find(TO_DAYS_FIELD));
			}else return;
			//properly calculate the to days and months
			multiplier = ((EventTimingOption) ui.getAttachedObject(ui.getSelectedItem(find(TO_DATE_OPTION_SELECT)))).multiplier;
			int toMonths = Integer.parseInt(toMonthsString) * multiplier;
			int toDays = Integer.parseInt(toDaysString) * multiplier;
			//get the to event
			Class<?> toEvent = ui.getAttachedObject(ui.getSelectedItem(find(TO_EVENT_SELECT))).getClass();
			//get the frequency option
			RecurringReminderFrequency frequency = (RecurringReminderFrequency) ui.getAttachedObject(ui.getSelectedItem(find(RECCURANCE_OPTION_SELECT)));
			//create the reminder!
			RecurringReminder recur;
			if(!isEditing){
				recur = new RecurringReminder();
			}else{
				recur = (RecurringReminder) getSelectedReminder();
			}
			recur.setMessageFormat(message);
			recur.setName(name);
			recur.setTimeOfDay(timeOfDay);
			recur.setStartCriteria(fromEvent, fromDays, fromMonths);
			recur.setEndCriteria(toEvent, toDays, toMonths);
			recur.setFrequency(frequency);
			recur.setContactGroup(contactGroup);
			reminderDao.saveOrUpdateReminder(recur);
			reminderId = recur.getReminderId();
		}
		isEditing = false;
		refreshReminderList(reminderId);
	}
	
	private boolean checkField(String thinletFieldName, String fieldName){
		String contents = ui.getText(find(thinletFieldName));
		if(contents == null || contents.equals("")){
			ui.alert(fieldName+ " " +CANNOT_BE_EMPTY);
			return false;
		}else{
			return true;
		}
	}
	
	public void editReminder(){
		isEditing = true;
		removeAll(find(ACTION_PANEL));
		add(find(ACTION_PANEL),ui.loadComponentFromFile(EDIT_REMINDER_XML, this));
		Reminder r = getSelectedReminder();
		populateReminderTypeSelect(r.getClass(),false);
		populateRecipientSelect(getSelectedReminder().getContactGroup());
		removeAll(find(TIMING_PANEL));
		if(r instanceof RecurringReminder){
			RecurringReminder recur = (RecurringReminder) r;
			//add the proper panel
			add(find(TIMING_PANEL),ui.loadComponentFromFile(RECURRING_TIMING_PANEL_XML, this));
			//set the name
			ui.setText(find(REMINDER_NAME_FIELD), recur.getName());
			//set the event option
			populateEventOptionSelect(true,recur.getStartEvent().getClass());
			populateEventOptionSelect(false,recur.getEndEvent().getClass());
			//populate the date options (before, after, day of)
			int startTot = recur.getStartDays() +recur.getStartMonths();
			EventTimingOption startOption = startTot > 0?EventTimingOption.AFTER:(startTot<0?EventTimingOption.BEFORE:EventTimingOption.DAY_OF);
			populateDateOptionSelect(true,startOption);
			//populate the ending date option
			int endTot = recur.getEndDays() +recur.getEndMonths();
			EventTimingOption endOption = endTot > 0?EventTimingOption.AFTER:(endTot<0?EventTimingOption.BEFORE:EventTimingOption.DAY_OF);
			populateDateOptionSelect(false,endOption);
			//set the recurrance option
			populateRecurrenceOptionSelect(recur.getFrequency());
			//set the month and day options
			if(startTot != 0){
				ui.setText(find(FROM_DAYS_FIELD), String.valueOf(Math.abs(recur.getStartDays())));
				ui.setText(find(FROM_MONTHS_FIELD), String.valueOf(Math.abs(recur.getStartMonths())));
			}
			if(endTot != 0){
				ui.setText(find(TO_DAYS_FIELD), String.valueOf(Math.abs(recur.getEndDays())));
				ui.setText(find(TO_MONTHS_FIELD), String.valueOf(Math.abs(recur.getEndMonths())));
			}
			//set the time of day
			ui.setSelectedIndex(find(TIME_OF_DAY_FIELD),recur.getTimeOfDay());
			//set the message
			ui.setText(find(MESSAGE_TEXT_AREA),recur.getMessageFormat());
			ui.requestFocus(find(REMINDER_NAME_FIELD));
		}else if(getSelectedReminderType().equals(OneTimeReminder.class)){
			OneTimeReminder oneTime = (OneTimeReminder) r;
			//add the proper panel
			add(find(TIMING_PANEL),ui.loadComponentFromFile(ONE_TIME_TIMING_PANEL_XML, this));
			//set the name
			ui.setText(find(REMINDER_NAME_FIELD), oneTime.getName());
			//set the event option
			populateEventOptionSelect(true,oneTime.getStartEvent().getClass());
			//populate the date options (before, after, day of)
			int startTot = oneTime.getStartDays() +oneTime.getStartMonths();
			EventTimingOption startOption = startTot > 0?EventTimingOption.AFTER:(startTot<0?EventTimingOption.BEFORE:EventTimingOption.DAY_OF);
			populateDateOptionSelect(true,startOption);
			//set the month and day options
			if(startTot != 0){
				ui.setText(find(FROM_DAYS_FIELD), String.valueOf(Math.abs(oneTime.getStartDays())));
				ui.setText(find(FROM_MONTHS_FIELD), String.valueOf(Math.abs(oneTime.getStartMonths())));
			}
			//set the time of day
			ui.setSelectedIndex(find(TIME_OF_DAY_FIELD),oneTime.getTimeOfDay());
			//set the message
			ui.setText(find(MESSAGE_TEXT_AREA),oneTime.getMessageFormat());
			ui.requestFocus(find(REMINDER_NAME_FIELD));
		}
	}
	
	public void editReminderCancelled(){
		isEditing = false;
		showReminderDisplayView();
	}

	/**
	 * Called when the user confirms that they wish to remove a reminder
	 */
	public void removeReminderConfirmed() {
		isEditing = false;
		// delete the reminder
		reminderDao.deleteReminder(getSelectedReminder());
		// refresh the list
		refreshReminderList(null);
		// remove the confirmation dialog
		ui.remove(ui.find(CONFIRMATION_DIALOG));
	}
	
	private Class<?> getSelectedReminderType(){
		try{
			Class<?> reminderClass = (Class<?>) ui.getAttachedObject(ui.getSelectedItem(find(REMINDER_TYPE_SELECT)));
			return reminderClass;
		}catch(Exception e){ return null;}
	}
	
	private Reminder getSelectedReminderInstance(){
		Reminder r=null;
		try {
			r = (Reminder) getSelectedReminderType().newInstance();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Class<?>> getReminderTypes(){
		List<Class<?>> reminderTypes = new ArrayList<Class<?>>();
		reminderTypes.add(RecurringReminder.class);
		reminderTypes.add(OneTimeReminder.class);
		return reminderTypes;
	}
}