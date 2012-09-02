package net.frontlinesms.plugins.patientview.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.tabs.TabController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
public abstract class PersonDashboard<P extends Person> extends Dashboard {

	protected P person;
	protected PersonAttributePanel attributePanel;
	
	protected List<TabController> tabs;
	
	protected boolean inEditingMode;
	
	protected int selectedTab = -1;
	
	private static final String GO_BACK_BUTTON = "patientrecord.buttons.go.back";
	
	private static final String EDIT_ATTRIBUTES = "detailview.buttons.edit.attributes";
	private static final String SAVE = "detailview.buttons.save";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/patient_edit_";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	
	public PersonDashboard(UiGeneratorController uiController, ApplicationContext appCon, P p) {
		super(uiController, appCon);
		this.person = p;
		tabs = new ArrayList<TabController>();
		initView();
	}
	
	protected abstract void init();

	public void initView(){
		init();
		//add all the tabs
		for(TabController tab: tabs){
			addTab(tab);
		}
		//add the attribute panel
		attributePanel = new PersonAttributePanel(ui,appCon,person);
		ui.add(leftPanel,attributePanel.getMainPanel());
		ui.add(leftPanel,getBottomButtons());
	}
	
	public void addTab(TabController tab){
		subviews.add(tab);
		add(tabbedPanel,tab.getTab());
	}
	
	@Override
	public void subviewsWillAppear(){
		if(selectedTab >= 0 && selectedTab < tabs.size()){
			tabs.get(selectedTab).willAppear();
		}
	}
	
	@Override
	public void subviewsWillDisappear(){
		if(selectedTab >= 0 && selectedTab < tabs.size()){
			tabs.get(selectedTab).willDisappear();
		}
	}
	
	public void willAppear(){
		if(selectedTab == -1){
			selectedTab = 0;
		}
		subviewsWillAppear();
	}
	
	public void tabSelectionChanged(){
		if(selectedTab >= 0 && selectedTab < tabs.size()){
			tabs.get(selectedTab).willDisappear();
		}
		selectedTab = ui.getSelectedIndex(tabbedPanel);
		if(selectedTab >= 0 && selectedTab < tabs.size()){
			tabs.get(selectedTab).willAppear();
		}
	}
	
	public void setSelectedTab(int tabNum){
		if(tabNum >= tabs.size()) return;
		ui.setInteger(tabbedPanel, "selected", tabNum);
		tabSelectionChanged();
	}
	
	protected Object getBottomButtons(){
		Object buttonPanel = ui.create("panel");
		ui.setName(buttonPanel, "buttonPanel");
		ui.setColumns(buttonPanel, 3);
		Object leftButton = ui.createButton(!inEditingMode?getI18nString(GO_BACK_BUTTON):getI18nString(SAVE));
		Object rightButton = ui.createButton(!inEditingMode?getI18nString(EDIT_ATTRIBUTES):getI18nString(CANCEL));
		if(inEditingMode){
			ui.setAction(leftButton, "saveButtonClicked", null, this);
			ui.setAction(rightButton, "cancelButtonClicked", null, this);
			ui.setIcon(leftButton, SAVE_ICON);
			ui.setIcon(rightButton, CANCEL_ICON);
			
		}else{
			ui.setAction(leftButton, "goBack()", null, this);
			ui.setIcon(leftButton, "/icons/arrow_turn_left_large.png");
			ui.setAction(rightButton, "editButtonClicked()", null, this);
			ui.setIcon(rightButton, EDIT_ATTRIBUTE_ICON + (person.getGender() == Gender.MALE?"male.png":"female.png"));
			if(((PersonAttributeDao) appCon.getBean("PersonAttributeDao")).getAllAttributesForPerson(person).size() == 0 && (person.getClass().equals(CommunityHealthWorker.class) ||(person.getClass().equals(Patient.class) && ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getAttributePanelFields().size() == 0 ))){
				ui.setEnabled(rightButton,false);
			}
		}
		ui.setHAlign(leftButton, Thinlet.LEFT);
		ui.setVAlign(leftButton, Thinlet.BOTTOM);
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READWRITE||
		   UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
			ui.add(buttonPanel,leftButton);
		}
		Object spacerLabel = ui.createLabel("");
		ui.setWeight(spacerLabel, 1, 0);
		ui.add(buttonPanel,spacerLabel);
		ui.setHAlign(rightButton, Thinlet.RIGHT);
		ui.setVAlign(rightButton, Thinlet.BOTTOM);
		ui.add(buttonPanel, rightButton);
		ui.setWeight(buttonPanel, 1, 1);
		ui.setVAlign(buttonPanel, Thinlet.BOTTOM);
		return buttonPanel;
	}
	
	public void goBack(){
		collapseDashboard();
	}
	
	public void editButtonClicked(){
		inEditingMode=true;
		attributePanel.switchToEditingPanel();
		ui.remove(ui.find(leftPanel,"buttonPanel"));
		ui.add(leftPanel,getBottomButtons());
	}
	
	public void saveButtonClicked(){
		if(attributePanel.stopEditingWithSave()){
			inEditingMode=false;
			ui.remove(ui.find(leftPanel,"buttonPanel"));
			ui.add(leftPanel,getBottomButtons());
		}
	}
	
	public void cancelButtonClicked(){
		inEditingMode=false;
		attributePanel.stopEditingWithoutSave();
		ui.remove(ui.find(leftPanel,"buttonPanel"));
		ui.add(leftPanel,getBottomButtons());
	}
}
