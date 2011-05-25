package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import java.security.GeneralSecurityException;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.UserPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

/**
 * The controller for the user administration panel. The associated Thinlet
 * files are:
 *<p>
 *<code>search_action_panel.xml</code><br/>
 *<code>password_reset_notice.xml</code>
 */
public class UserAdministrationPanelController extends PersonAdministrationPanelController<User> {

	/**
	 * The path to the xml widget that appears when a user's password is reset.
	 */
	private String XML_PASSWORD_RESET_NOTICE = "/ui/plugins/patientview/administration/security/passwordResetDialog.xml";

	private static final String ADD_ICON = "/icons/doctor_add_female.png";
	private static final String EDIT_ICON = "/icons/doctor_edit_female.png";
	private static final String DELETE_ICON = "/icons/doctor_delete_female.png";
	/**
	 * The notice that appears when a uses's password is reset. This is
	 * persistant so that multiple copies do not show up if the button is
	 * clicked multiple times in a row.
	 */
	private Object resetNotice;

	/** The userDao for saving users edited in this component */
	private UserDao userDao;
	
	public UserAdministrationPanelController( UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
		this.userDao = (UserDao) appCon.getBean("UserDao");
		search("");
	}

	@Override
	public void addButtonClicked() {
		super.addButtonClicked();
		Object saveButton = find(currentPersonPanel.getMainPanel(), "savebutton");
		ui.setAction(saveButton, "saveNewUser()", null, this);
	}

	public void saveNewUser() throws GeneralSecurityException {
		currentPersonPanel.stopEditingWithSave();
		resetPassword(currentPersonPanel.getPerson());
		Object titleLabel = find(resetNotice, "titlelabel");
		ui.setText(titleLabel, getI18nString("admin.user.new"));
	}

	@Override
	protected List<User> getPeopleForString(String s) {
		return userDao.getUsersByName(s,30);
	}

	@Override
	protected String getPersonType() {
		return getI18nString("medic.common.user");
	}

	@Override
	protected void putHeader() {
		advancedTableController.putHeader(User.class, HeaderColumn.createColumnList(new String[]{getI18nString("medic.common.labels.name"), getI18nString("medic.common.labels.username"), getI18nString("medic.common.labels.role")},
				 new String[]{"/icons/user.png", "", "/icons/group_gear.png"},
				 new String[]{"getName", "getUsername", "getRoleName",}));
	}

	public String getListItemTitle() {
		return getI18nString("admin.actionlist.manage.users");
	}

	/**
	 * The callback method for the resetPasswordButton.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto library cannot be loaded
	 */
	public void resetPassword() throws GeneralSecurityException {
		// TODO: error handling
		currentPersonPanel.stopEditingWithoutSave();
		resetPassword(currentPersonPanel.getPerson());
	}

	/**
	 * A helper method that resets a users password and displays a notice of
	 * their new password.
	 * 
	 * @param user
	 *            the user to be reset
	 * @throws GeneralSecurityException
	 *             if the crypto library cannot be found
	 */
	protected void resetPassword(User user) throws GeneralSecurityException {
		String newPass = user.assignTempPassword();
		userDao.updateUser(user);
		if (resetNotice == null) {
			resetNotice = ui.loadComponentFromFile(XML_PASSWORD_RESET_NOTICE);
		}
		Object passwordLabel = find(resetNotice, "passwordlabel");
		ui.setText(passwordLabel, newPass);
		Object fieldsPanel = find(FIELDS_PANEL);
		add(fieldsPanel, resetNotice);
	}

	@Override
	protected PersonPanel<User> getPersonPanelForPerson(Person person) {
		return new UserPanel(ui, appCon, (User) person);
	}

	@Override
	public void editButtonClicked() {
		currentPersonPanel.switchToEditingPanel();
		// add the reset password button!
		Object resetPasswordButton = ui.createButton(getI18nString("password.reset"));
		ui.setAction(resetPasswordButton, "resetPassword()", null,this);
		ui.setHAlign(resetPasswordButton, "right");
		Object buttonsPanel = find(currentPersonPanel.getMainPanel(), PersonPanel.BUTTON_PANEL);
		add(buttonsPanel, resetPasswordButton);
	}

	public String getIconPath() {
		return "/icons/doctors.png";
	}

	@Override
	protected String[] getIcons() {
		String[] icons = new String[3];
		icons[ADD_INDEX] = ADD_ICON;
		icons[EDIT_INDEX] = EDIT_ICON;
		icons[REMOVE_INDEX] = DELETE_ICON;
		return icons;
	}

	@Override
	protected Class<User> getPersonClass() {
		return User.class;
	}
	
	@Override
	public void removeButtonClicked() {}
}
