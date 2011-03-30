package net.frontlinesms.plugins.patientview.importer.validation;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.List;
public class CommunityHealthWorkerCsvValidator extends PersonCsvValidator{

	protected static final int PHONE_NUMBER_INDEX = 3;
	@Override
	public void doAdditionalValidation(int lineNumber, String[] line, List<CsvValidationException> exceptions) {
		if(line[PHONE_NUMBER_INDEX].replaceAll("[^0-9]", "").length() < 11){
			exceptions.add(new CsvValidationException(lineNumber, getI18nString("medic.common.labels.phone.number")+ " \""+line[PHONE_NUMBER_INDEX]+"\" "+ getI18nString("medic.importer.invalid.phone.number")));
		}
	}

}
