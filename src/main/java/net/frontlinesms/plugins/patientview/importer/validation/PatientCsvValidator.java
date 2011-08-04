package net.frontlinesms.plugins.patientview.importer.validation;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;

import org.springframework.context.ApplicationContext;

public class PatientCsvValidator extends PersonCsvValidator{

	protected CommunityHealthWorkerDao chwDao;

	public PatientCsvValidator(ApplicationContext appCon) {
		super();
		chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
	}

	@Override
	public void doAdditionalValidation(int lineNumber, String[] line, List<CsvValidationException> exceptions) {
		//validate the CHW, if present
		if(hasColumn(line,CsvColumns.CHW_INDEX)){
			List<CommunityHealthWorker> chws = chwDao.findCommunityHealthWorkerByName(line[CsvColumns.CHW_INDEX], -1, true);
			if(chws.size() == 0){
				exceptions.add(new CsvValidationException(lineNumber, getI18nString("medic.importer.no.chw.association.error")+" \"" + line[CsvColumns.CHW_INDEX]+"\""));
			}else if(chws.size() >1){
				exceptions.add(new CsvValidationException(lineNumber, getI18nString("medic.importer.multiple.chw.association.error")+" \"" + line[CsvColumns.CHW_INDEX]+"\""));
			}
		}
	}
}