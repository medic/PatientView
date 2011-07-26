package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.HashMap;

import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.ui.UiGeneratorController;

public abstract class PersonDataImporter implements CsvDataImporter{

	protected HashMap<String,Gender> genderMap;
	
	protected UiGeneratorController ui;
	
	protected Object messageList;
	
	public PersonDataImporter(UiGeneratorController ui, Object messageList){
		this.ui = ui;
		this.messageList = messageList;
		genderMap = new HashMap<String, Gender>();
		genderMap.put(getI18nString("medic.common.male").toLowerCase(), Gender.MALE);
		genderMap.put(getI18nString("medic.common.female").toLowerCase(), Gender.FEMALE);
		genderMap.put(getI18nString("medic.common.transgender").toLowerCase(), Gender.TRANSGENDER);
	}

	public abstract Object getInformationPanel();

	public abstract String getTypeLabel();

	public abstract void importFile(String path);
	
	public Object getAdditionalOptionsPanel() {
		return ui.createPanel("");
	}
	
	protected Gender parseGender(String gender){
		return genderMap.get(gender.toLowerCase());
	}
	
	protected String parsePhoneNumber(String number){
		return number.trim().replaceAll("[^0-9]", "");
	}
}
