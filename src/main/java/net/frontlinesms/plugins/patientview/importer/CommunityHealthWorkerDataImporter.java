package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.importer.validation.CommunityHealthWorkerCsvValidator;
import net.frontlinesms.plugins.patientview.importer.validation.CsvColumns;
import net.frontlinesms.plugins.patientview.importer.validation.CsvValidationException;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

public class CommunityHealthWorkerDataImporter extends PersonDataImporter
implements ThinletUiEventHandler {

	private CommunityHealthWorkerDao chwDao;

	private CommunityHealthWorkerCsvValidator validator;

	public CommunityHealthWorkerDataImporter(Object messageList, UiGeneratorController ui, ApplicationContext appCon) {
		super(ui, messageList);
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		validator = new CommunityHealthWorkerCsvValidator();
	}

	public Object getInformationPanel() {
		Object panel = ui.createPanel("");
		String chw = getI18nString("medic.common.chw");
		ui.setColumns(panel, 1);
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 1: " + chw + " " + getI18nString("simplesearch.fields.name")));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 2: " + chw + " "+ getI18nString("simplesearch.fields.birthdate") + " ("+ getI18nString(FrontlineSMSConstants.DATEFORMAT_YMD)+ ")"));
		ui.add(panel, ui.createLabel(getI18nString("medic.importer.labels.column")+ " 3: " + chw + " "+ getI18nString("simplesearch.fields.gender") + " ("+ getI18nString("medic.common.male") + ", "+ getI18nString("medic.common.female") + ", or "+ getI18nString("medic.common.transgender") + ")"));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column")+ " 4: "+ chw+ " "+ getI18nString("medic.importer.formatting.info.phone.number")));
		return panel;
	}

	public void importFile(String path, boolean ignoreHeader) {
		// begin import
		addMessageToList(getI18nString("medic.importer.beginning.message") + ": " + path);
		// validate the CSV file
		List<CsvValidationException> exceptions = validator.validateFile(path, ignoreHeader);
		if (exceptions.size() != 0) {// if there were errors, show them
			for (CsvValidationException e : exceptions) {
				addMessageToList(e.toString());
			}
		} else {// otherwise, import the file
			List<CommunityHealthWorker> chws = new ArrayList<CommunityHealthWorker>();
			try {
				chws = parseChws(path,ignoreHeader);
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
			}
			for(CommunityHealthWorker chw: chws){
				chwDao.saveCommunityHealthWorker(chw);
			}
			addMessageToList("===== CHW data import complete =====");
			addMessageToList("===== " + chws.size() + " CHWs created successfully =====");
		}
	}

	private List<CommunityHealthWorker> parseChws(String path, boolean ignoreHeader) throws FileNotFoundException, IOException, ParseException {
		List<CommunityHealthWorker> chws = new ArrayList<CommunityHealthWorker>();
		// set up
		CSVReader reader = new CSVReader(new FileReader(path));
		String[] currLine;
		// iterate over the lines of the file
		while ((currLine = reader.readNext()) != null) {
			if(ignoreHeader){
				ignoreHeader=false;
				continue;
			}
			if(currLine.length <3) continue;
			// get the values
			String name = currLine[CsvColumns.NAME_INDEX];
			String phoneNumber = null;
			if(hasColumn(currLine, CsvColumns.PHONE_NUMBER_INDEX)){
				phoneNumber = parsePhoneNumber(currLine[CsvColumns.PHONE_NUMBER_INDEX]);
			}
			Gender gender = parseGender(currLine[CsvColumns.GENDER_INDEX]);
			Date birthdate = InternationalisationUtils.getDateFormat().parse(currLine[CsvColumns.BDAY_INDEX]);
			//create the new CHW
			CommunityHealthWorker chw = new CommunityHealthWorker(name,phoneNumber, gender,birthdate.getTime());
			//save the CHW
			chws.add(chw);
		}
		return chws;
	}

	public String getTypeLabel() {
		return getI18nString("medic.common.chws");
	}
}