package net.frontlinesms.plugins.patientview.importer.validation;



import java.util.List;
public class CommunityHealthWorkerCsvValidator extends PersonCsvValidator{
	
	@Override
	public void doAdditionalValidation(int lineNumber, String[] line, List<CsvValidationException> exceptions) {}

}
