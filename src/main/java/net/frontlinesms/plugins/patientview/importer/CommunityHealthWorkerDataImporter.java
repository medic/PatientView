package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.importer.validation.CommunityHealthWorkerCsvValidator;
import net.frontlinesms.plugins.patientview.importer.validation.CsvValidationException;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

public class CommunityHealthWorkerDataImporter extends PersonDataImporter implements ThinletUiEventHandler{
	
	private CommunityHealthWorkerDao chwDao;
	
	private CommunityHealthWorkerCsvValidator validator;
	
	public CommunityHealthWorkerDataImporter(Object messageList, UiGeneratorController ui,ApplicationContext appCon){
		super(ui,messageList);
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		validator = new CommunityHealthWorkerCsvValidator();
	}

	public Object getInformationPanel() {
		Object panel = ui.createPanel("");
		String chw = getI18nString("medic.common.chw");
		ui.setColumns(panel, 1);
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 1: "+chw + " " + getI18nString("simplesearch.fields.name")));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 2: "+chw+ " "+ getI18nString("simplesearch.fields.birthdate")+" ("+getI18nString(FrontlineSMSConstants.DATEFORMAT_YMD)+")"));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 3: "+chw + " "+ getI18nString("simplesearch.fields.gender")+ " (" +getI18nString("medic.common.male")+", " +getI18nString("medic.common.female")+", or " +getI18nString("medic.common.transgender") +")"));
		ui.add(panel,ui.createLabel(getI18nString("medic.importer.labels.column") + " 4: "+chw +  " "+getI18nString("medic.importer.formatting.info.phone.number")));
		return panel;
	}

	public void importFile(String path) {
		addMessageToList(getI18nString("medic.importer.beginning.message")+ ": " + path);
		try {
			List<CsvValidationException> exceptions = validator.validateFile(path);
			if(exceptions.size() != 0){
				for(CsvValidationException e : exceptions){
					addMessageToList(e.toString());
				}
			}else{
				CSVReader reader = new CSVReader(new FileReader(path));
				String[] currLine;
				List<CommunityHealthWorker> chws = new ArrayList<CommunityHealthWorker>();
				int lineNumber = 0;
				try {
					while((currLine = reader.readNext()) != null){
						CommunityHealthWorker chw  = new CommunityHealthWorker(currLine[0], parsePhoneNumber(currLine[3]), parseGender(currLine[2]), InternationalisationUtils.getDateFormat().parse(currLine[1]));
						chwDao.saveCommunityHealthWorker(chw);
						lineNumber ++;
					}
				}catch (Exception e){
					addMessageToList(getI18nString("medic.importer.file.parsing.error"));
					addMessageToList(e.toString());
				}
				if(exceptions.size() == 0){
					addMessageToList("====== "+getI18nString("medic.common.chw")+" " +getI18nString("medic.importer.creation.complete")+" ======");
					addMessageToList(lineNumber + " " + getI18nString("medic.common.chws")+ " " +getI18nString("medic.importer.success.message"));
				}
			}
		} catch (FileNotFoundException e) {
			addMessageToList(getI18nString("medic.importer.file.not.found"));
			addMessageToList(e.toString());
		}
	}
	
	private void addMessageToList(String message){
		String text = ui.getText(messageList);
		String newLine = "["+getI18nString("medic.common.chw")+ " "+getI18nString("medic.data.importer") +"] "+InternationalisationUtils.getDatetimeFormat().format(new Date()) + " - " + message;
		text += "\n"+newLine;
		ui.setText(messageList, text);
	}

	public String getTypeLabel() {
		return getI18nString("medic.common.chws");
	}
}
