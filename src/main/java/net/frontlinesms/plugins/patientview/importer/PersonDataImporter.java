package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.Date;
import java.util.HashMap;

import org.springframework.util.StringUtils;

import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public abstract class PersonDataImporter implements CsvDataImporter{

	protected HashMap<String,Gender> genderMap;
	
	protected UiGeneratorController ui;
	
	protected Object messageList;
	
	public PersonDataImporter(UiGeneratorController ui, Object messageList){
		this.ui = ui;
		this.messageList = messageList;
		//prep the gender parsing map
		genderMap = new HashMap<String, Gender>();
		genderMap.put(getI18nString("medic.common.male").toLowerCase(), Gender.MALE);
		genderMap.put(getI18nString("medic.common.female").toLowerCase(), Gender.FEMALE);
		genderMap.put(getI18nString("medic.common.transgender").toLowerCase(), Gender.TRANSGENDER);
	}

	public abstract Object getInformationPanel();

	public abstract String getTypeLabel();

	public abstract void importFile(String path, boolean ignoreHeader);
	
	public Object getAdditionalOptionsPanel() {
		return ui.createPanel("");
	}
	
	/**
	 * Tests to see if the array contains a information at the supplied columnInde
	 * @param currLine
	 * @param columnIndex
	 * @return
	 */
	protected boolean hasColumn(String[] currLine, int columnIndex) {
		return currLine.length >= columnIndex + 1 && StringUtils.hasText(currLine[columnIndex]);
	}
	
	/**
	 * Returns a gender for a string, defaults to female
	 * @param gender
	 * @return
	 */
	protected Gender parseGender(String gender){
		if(genderMap.get(gender.toLowerCase()) != null){
			return genderMap.get(gender.toLowerCase());
		}else{
			return Gender.FEMALE;
		}
	}
	
	/**
	 * Parses a phone number by stripping all non-numeric characters.
	 * Returns an empty string if the parameter is null.
	 * @param number
	 * @return
	 */
	protected String parsePhoneNumber(String number){
		if(number!= null){
			return number.trim().replaceAll("[^0-9]", "");
		}else{
			return "";
		}
	}
	
	/**
	 * Adds a message to the log
	 * @param message
	 */
	protected void addMessageToList(String message) {
		String text = ui.getText(messageList);
		String newLine = "[ "+ InternationalisationUtils.getDatetimeFormat().format(new Date()) + " ]  " + message;
		text += "\n" + newLine;
		ui.setText(messageList, text);
	}
}
