package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.importer.validation.CsvColumns;
import net.frontlinesms.plugins.patientview.importer.validation.CsvValidationException;
import net.frontlinesms.plugins.patientview.importer.validation.PatientCsvValidator;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

public class PatientDataImporter extends PersonDataImporter{
	
	private PatientDao patientDao;
	
	private CommunityHealthWorkerDao chwDao;
	
	private PatientCsvValidator validator;
	
	public PatientDataImporter(Object messageList, UiGeneratorController uiController,ApplicationContext appCon){
		super(uiController,messageList);
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		validator = new PatientCsvValidator(appCon);
	}

	public Object getInformationPanel() {
		Object panel = ui.createPanel("");
		ui.setColumns(panel, 1);
		String patient = getI18nString("medic.common.patient");
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 1: "+patient+ " " + getI18nString("simplesearch.fields.name")));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 2: "+patient+ " " + getI18nString("simplesearch.fields.birthdate")+" ("+getI18nString(FrontlineSMSConstants.DATEFORMAT_YMD)+")"));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 3: "+patient+ " " + getI18nString("simplesearch.fields.gender")+" (" +getI18nString("medic.common.male")+", " +getI18nString("medic.common.female")+", or " +getI18nString("medic.common.transgender") +")"));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 4: "+patient+ " "+getI18nString("medic.importer.formatting.info.phone.number")));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 5: "+getI18nString("medic.importer.patient.chw.info")));
		return panel;
	}

	public void importFile(String path) {
		addMessageToList(getI18nString("medic.importer.beginning.message")+": " + path);
		try {
			List<CsvValidationException> exceptions = validator.validateFile(path);
			if(exceptions.size() != 0){
				for(CsvValidationException e : exceptions){
					addMessageToList(e.toString());
				}
			}else{
				CSVReader reader = new CSVReader(new FileReader(path));
				String[] currLine;
				List<Patient> patients = new ArrayList<Patient>();
				int lineNumber = 0;
				try {
					while((currLine = reader.readNext()) != null){
						List<CommunityHealthWorker> chw = chwDao.findCommunityHealthWorkerByName(currLine[CsvColumns.CHW_INDEX],-1, true);
						if(chw.size() == 1 && !currLine[CsvColumns.CHW_INDEX].trim().equals("")){
							Patient patient  = new Patient(chw.get(0),currLine[CsvColumns.NAME_INDEX],parseGender(currLine[CsvColumns.GENDER_INDEX]),InternationalisationUtils.getDateFormat().parse(currLine[CsvColumns.BDAY_INDEX]));
							patient.setPhoneNumber(parsePhoneNumber(currLine[CsvColumns.PHONE_NUMBER_INDEX]));
							patientDao.savePatient(patient);
						}else if(currLine[CsvColumns.CHW_INDEX].trim().equals("")){
							Patient patient  = new Patient(null,currLine[CsvColumns.NAME_INDEX],parseGender(currLine[CsvColumns.GENDER_INDEX]),InternationalisationUtils.getDateFormat().parse(currLine[CsvColumns.BDAY_INDEX]));
							patient.setPhoneNumber(parsePhoneNumber(currLine[CsvColumns.PHONE_NUMBER_INDEX]));
							patientDao.savePatient(patient);
						}else{
							addMessageToList(getI18nString("medic.importer.line")+" " + lineNumber+ ": "+ getI18nString("medic.importer.patient.chw.parsing.error"));
						}
						lineNumber ++;
					}
				}catch(Exception e){
					addMessageToList(getI18nString("medic.importer.file.parsing.error"));
					addMessageToList(e.toString());
				}
				if(exceptions.size() == 0){
					addMessageToList("====== "+getI18nString("medic.common.patient")+" " +getI18nString("medic.importer.creation.complete")+" ======");
					addMessageToList(lineNumber + " " + getI18nString("medic.common.patients")+ " " +getI18nString("medic.importer.success.message"));
				}
			}
		} catch (FileNotFoundException e) {
			addMessageToList(getI18nString("medic.importer.file.not.found"));
			addMessageToList(e.toString());
		}
	}
	
	private void addMessageToList(String message){
		String text = ui.getText(messageList);
		String newLine = "["+getI18nString("medic.common.patient")+ " "+getI18nString("medic.data.importer") +"] "+InternationalisationUtils.getDatetimeFormat().format(new Date()) + " - " + message;
		text += "\n"+newLine;
		ui.setText(messageList, text);
	}

	public String getTypeLabel() {
		return "Patients";
	}
}
