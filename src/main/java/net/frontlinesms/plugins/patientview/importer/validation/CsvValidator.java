package net.frontlinesms.plugins.patientview.importer.validation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

public abstract class CsvValidator {

	public List<CsvValidationException> validateFile(String filename, boolean ignoreHeader){
		CSVReader reader=null;
		try {
			reader = new CSVReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			List<CsvValidationException> exceptions = new ArrayList<CsvValidationException>();
			CsvValidationException e2 = new CsvValidationException(0, "File not found: "+ filename);
			exceptions.add(e2);
			return exceptions;
		}
		return validate(reader,ignoreHeader);
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

	public abstract List<CsvValidationException> validate(CSVReader reader ,boolean ignoreHeader);
}
