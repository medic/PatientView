package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.util.List;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.search.impl.PersonResultSet;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.plugins.patientview.ui.administration.DeleteDialogController;
import net.frontlinesms.plugins.patientview.ui.administration.DeleteDialogDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class PersonAdministrationPanelController<E extends Person> extends AdministrationTabPanel implements TableActionDelegate, EventObserver, DeleteDialogDelegate{

	/**
	 * The main panel of the person administration screen
	 */
	protected PagedTableController advancedTableController;
	protected PersonResultSet<E> personResultSet;
	private Object advancedTable;
	protected PersonPanel<E> currentPersonPanel;
	
	/* Thinlet object names*/
	private static final String RESULTS_TABLE = "resultstable";
	private static final String ADD_BUTTON = "addbutton";
	private static final String REMOVE_BUTTON = "removebutton";
	private static final String EDIT_BUTTON = "editbutton";
	protected static final String FIELDS_PANEL = "fieldspanel";
	private static final String SEARCH_FIELD = "searchbox";
	
	/* i18n */
	private static final String MANAGE= "medic.common.labels.manage";
	private static final String ADD = "medic.common.labels.add";
	private static final String REMOVE = "medic.common.labels.remove";
	private static final String EDIT = "medic.common.labels.edit";
	
	protected static final int ADD_INDEX= 0;
	protected static final int EDIT_INDEX= 1;
	protected static final int REMOVE_INDEX= 2;
	
	/** Thinlet XML file **/
	private static String UI_FILE_MANAGE_PERSON_PANEL = "/ui/plugins/patientview/administration/personAdministrationPanel.xml";
	
	
	public PersonAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		super(uiController, appCon, UI_FILE_MANAGE_PERSON_PANEL);
		init();
	}

	private void init(){
		advancedTable = find(RESULTS_TABLE);
		advancedTableController = new PagedTableController(this,ui,advancedTable);
		putHeader();
		personResultSet = new PersonResultSet<E>(appCon, getPersonClass());
		advancedTableController.setResultsSet(personResultSet);
		ui.setText(find("titleLabel"), getI18nString(MANAGE)+ " "+ getPersonType() + "s");
		ui.setText(find(ADD_BUTTON), getI18nString(ADD)+ " " + getPersonType());
		ui.setText(find(REMOVE_BUTTON), getI18nString(REMOVE)+ " " + getPersonType());
		ui.setText(find(EDIT_BUTTON), getI18nString(EDIT)+ " " + getPersonType());
		ui.setIcon(find(ADD_BUTTON), getIcons()[ADD_INDEX]);
		ui.setIcon(find(EDIT_BUTTON), getIcons()[EDIT_INDEX]);
		ui.setIcon(find(REMOVE_BUTTON), getIcons()[REMOVE_INDEX]);
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		advancedTableController.setSelected(0);
		selectionChanged(advancedTableController.getCurrentlySelectedObject());
	}
	
	public void editButtonClicked(){
		if(currentPersonPanel != null){
			currentPersonPanel.switchToEditingPanel();
		}
	}
	
	public void addButtonClicked(){
		currentPersonPanel = getPersonPanelForPerson(null);
		removeAll(find(FIELDS_PANEL));
		add(find(FIELDS_PANEL), currentPersonPanel.getMainPanel());
	}
	
	public void removeButtonClicked(){
		DeleteDialogController deleteDialog = new DeleteDialogController(ui,this,"Person");
	}
	
	protected abstract String[] getIcons();
	
	/**
	 * should set the header of the advanced table as is required
	 * for the class of person being displayed
	 */
	protected abstract void putHeader();
	
	/**
	 * @return a name for the type of person that is being displayed
	 */
	protected abstract String getPersonType();
	
	protected abstract Class<E> getPersonClass();
	
	/**
	 * @return an arraylist of people for the search string s
	 */
	protected abstract List<E> getPeopleForString(String s);
	
	/**
	 * @param person
	 * @return A person panel of the proper type for the parameter
	 */
	protected abstract PersonPanel<E> getPersonPanelForPerson(Person person);

	/** 
	 * Called when the selection of the results table is changed
	 * @see net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate#selectionChanged(java.lang.Object)
	 */
	public void selectionChanged(Object selectedObject) {
		if(selectedObject == null){
			return;
		}
		currentPersonPanel = getPersonPanelForPerson((Person) selectedObject);
		ui.removeAll(find(FIELDS_PANEL));
		ui.add(find(FIELDS_PANEL), currentPersonPanel.getMainPanel());
		
	}

	/**
	 * Called when text in the search box changes. Should
	 * initiate the placement of the proper results in the results table
	 * @param text the text in the search box
	 */
	public void search(String text){
		personResultSet.setNameString(text);
		advancedTableController.updateTable();
		advancedTableController.setSelected(0);
		selectionChanged(advancedTableController.getCurrentlySelectedObject());
	}
	
	/**
	 * @see net.frontlinesms.plugins.patientview.ui.AdvancedTableDataSource#refreshResults()
	 */
	public void notify(FrontlineEventNotification event){
		if(event instanceof DatabaseEntityNotification){
			DatabaseEntityNotification den = (DatabaseEntityNotification) event;
			if(den.getDatabaseEntity().getClass().equals(getPersonClass())){
				advancedTableController.updateTable();
			}
		}
	}
	
	/** @see net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate#doubleClickAction(java.lang.Object)*/
	public void doubleClickAction(Object selectedObject) {}
	
	/** @see net.frontlinesms.plugins.patientview.ui.advancedtable.TableActionDelegate#resultsChanged() */
	public void resultsChanged() {}
}
