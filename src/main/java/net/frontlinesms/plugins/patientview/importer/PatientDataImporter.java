package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.importer.validation.CsvColumns;
import net.frontlinesms.plugins.patientview.importer.validation.CsvValidationException;
import net.frontlinesms.plugins.patientview.importer.validation.PatientCsvValidator;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

public class PatientDataImporter extends PersonDataImporter {

	private PatientDao patientDao;

	private CommunityHealthWorkerDao chwDao;

	private PatientCsvValidator validator;

	private VaccineDao vaccineDao;
	private ScheduledDoseDao doseDao;

	public PatientDataImporter(Object messageList, UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, messageList);
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		this.vaccineDao = (VaccineDao) appCon.getBean("VaccineDao");
		this.doseDao = (ScheduledDoseDao) appCon.getBean("ScheduledDoseDao");
		validator = new PatientCsvValidator(appCon);
	}

	public Object getInformationPanel() {
		Object panel = ui.createPanel("");
		ui.setColumns(panel, 1);
		String patient = getI18nString("medic.common.patient");
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 1: " + patient + " " + getI18nString("simplesearch.fields.name")));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 2: " + patient + " " + getI18nString("simplesearch.fields.birthdate") + " ("+ getI18nString(FrontlineSMSConstants.DATEFORMAT_YMD)+ ")"));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 3: " + patient + " " + getI18nString("simplesearch.fields.gender") + " ("+ getI18nString("medic.common.male") + ", "+ getI18nString("medic.common.female") + ", or "+ getI18nString("medic.common.transgender") + ")"));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 4: " + patient + " " + getI18nString("medic.importer.formatting.info.phone.number")));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 5: " + getI18nString("medic.importer.patient.chw.info")));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 6: " + "Enroll the patient in vaccines for newborns? (yes/no, optional, default no)"));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 7: " + "Patient ID number (optional, defaults to database ID)"));
		return panel;
	}

	public void importFile(String path, boolean ignoreHeader) {
		// begin import
		addMessageToList(getI18nString("medic.importer.beginning.message") + ": " + path);
		// validate the file
		List<CsvValidationException> exceptions = validator.validateFile(path,ignoreHeader);
		if (exceptions.size() != 0) {//if there were errors, print them to the log
			for (CsvValidationException e : exceptions) {
				addMessageToList(e.toString());
			}
		} else {//if there weren't any errors, attempt to import the file
			Map<Patient,Boolean> patients = new HashMap<Patient, Boolean>();
			try {
				patients = parsePatients(path,ignoreHeader);
			} catch (FileNotFoundException e) {
				addMessageToList("Could not find the file: "+ path);
				return;
			} catch (IOException e) {
				addMessageToList("Error reading file: "+ path);
				addMessageToList(e.toString());
				return;
			} catch (ParseException e) {
				addMessageToList("Error parsing file");
				addMessageToList(e.toString());
				return;
			} catch (Exception e) {
				addMessageToList("Error parsing file");
				addMessageToList(e.toString());
				return;
			}
			//now save the patients
			for(Entry<Patient, Boolean> patientEntry: patients.entrySet()){
				patientDao.savePatient(patientEntry.getKey());
				if(patientEntry.getValue()){
					scheduleVaccines(patientEntry.getKey());
				}
			}
			addMessageToList("===== Patient data import complete =====");
			addMessageToList("===== " + patients.entrySet().size() + " patients created successfully =====");
		}
	}

	private Map<Patient,Boolean> parsePatients(String path, boolean ignoreHeader) throws FileNotFoundException, IOException, ParseException{
		Map<Patient,Boolean> results = new HashMap<Patient, Boolean>();
		// prepare for patient creation
		CSVReader reader = new CSVReader(new FileReader(path));
		String[] currLine;
		int lineNumber = 0;
		while ((currLine = reader.readNext()) != null) {
			if(ignoreHeader){
				ignoreHeader=false;
				continue;
			}
			if(currLine.length < 3) continue;
			// get the name, birthdate, and gender
			String name = currLine[CsvColumns.NAME_INDEX].trim();
			Gender gender = parseGender(currLine[CsvColumns.GENDER_INDEX]);
			long birthdate = 0L;
			try{
				birthdate = InternationalisationUtils.getDateFormat().parse(currLine[CsvColumns.BDAY_INDEX]).getTime();
			}catch(Exception e){}
			// get the secondary ID number
			String secondaryId = null;
			if (hasColumn(currLine, CsvColumns.SECONDARY_ID_INDEX)) {
				secondaryId = currLine[CsvColumns.SECONDARY_ID_INDEX];
			}
			// get the CHW
			CommunityHealthWorker chw = null;
			if (hasColumn(currLine, CsvColumns.CHW_INDEX)) {
				List<CommunityHealthWorker> chwList = chwDao.findCommunityHealthWorkerByName(currLine[CsvColumns.CHW_INDEX], -1, true);
				if (chwList.size() == 1) {
					chw = chwList.get(0);
				}
			}
			// get the phone number
			String phoneNumber = null;
			if (hasColumn(currLine, CsvColumns.PHONE_NUMBER_INDEX)) {
				phoneNumber = parsePhoneNumber(currLine[CsvColumns.PHONE_NUMBER_INDEX]);
			}
			// decide whether or not this patient should be enrolled in
			// newborn vaccines
			boolean enrollNewborn = hasColumn(currLine, CsvColumns.NEWBORN_INDEX) && Boolean.parseBoolean(currLine[CsvColumns.NEWBORN_INDEX]);
			// create the patient
			Patient p = new Patient(chw, name, gender, birthdate);
			p.setExternalId(secondaryId);
			p.setPhoneNumber(phoneNumber);
			results.put(p, enrollNewborn);
			lineNumber++;
		}
		return results;
	}

	private void scheduleVaccines(Patient p) {
		List<Vaccine> vaccines = vaccineDao.getNewbornVaccines();
		for (Vaccine v : vaccines) {
			List<ScheduledDose> scheduledDoses = VaccineScheduler.instance()
			.scheduleVaccinesFromBirth(p, v);
			doseDao.saveScheduledDoses(scheduledDoses);
		}
	}

	public String getTypeLabel() {
		return "Patients";
	}
}
