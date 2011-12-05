package net.frontlinesms.plugins.patientview.importer.validation;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.ui.i18n.InternationalisationUtils;
import au.com.bytecode.opencsv.CSVReader;

public abstract class PersonCsvValidator extends CsvValidator{

	
	protected static String[] genderPossibilities = new String[]{getI18nString("medic.common.male"),
																getI18nString("medic.common.female"),
																getI18nString("medic.common.transgender")};
	
	@Override
	public List<CsvValidationException> validate(CSVReader reader, boolean ignoreHeader) {
		String[] currLine;
		List<CsvValidationException> exceptions = new ArrayList<CsvValidationException>();
		int lineNumber = 0;
		try {
			while((currLine = reader.readNext()) != null){
				if(ignoreHeader){
					ignoreHeader=false;
					continue;
				}
				lineNumber++;
				if(currLine.length <=1){
					//this is probably a junk line
					continue;
				}else if(currLine.length < 3){
					exceptions.add(new CsvValidationException(lineNumber, "Not enough information."));
					continue;
				}
				//check the name
				if(!hasColumn(currLine, CsvColumns.NAME_INDEX)){
					exceptions.add(new CsvValidationException(lineNumber, getI18nString("medic.importer.blank.chw.name")));
				}
				//check the birthdate
				try{
					InternationalisationUtils.getDateFormat().parse(currLine[CsvColumns.BDAY_INDEX].trim()).getTime();
				}catch(Exception e){
					exceptions.add(new CsvValidationException(lineNumber, getI18nString("medic.importer.date.format.error")+ ": \""+currLine[CsvColumns.BDAY_INDEX]+"\""));
				}
				//check gender
				boolean validGender = false;
				for(String gender: genderPossibilities){
					if(currLine[CsvColumns.GENDER_INDEX].trim().equalsIgnoreCase(gender)){
						validGender = true;
					}
				}
				if(!validGender){
					exceptions.add(new CsvValidationException(lineNumber, getI18nString("medic.importer.gender.format.error")+": \""+currLine[CsvColumns.GENDER_INDEX]+"\""));
				}
				//check phone number
				if(hasColumn(currLine, CsvColumns.PHONE_NUMBER_INDEX)){
					String address = currLine[CsvColumns.PHONE_NUMBER_INDEX].replaceAll("[^0-9]", "");
					if(!address.trim().equals("") && address.length() < 10){
						exceptions.add(new CsvValidationException(lineNumber, "Phone number formatted incorrectly: \""+currLine[CsvColumns.PHONE_NUMBER_INDEX]+"\""));
					}
				}
				doAdditionalValidation(lineNumber,currLine,exceptions);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exceptions;
	}
	
	public abstract void doAdditionalValidation(int lineNumber, String[] line,List<CsvValidationException> exceptions);

}
