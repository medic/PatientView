package net.frontlinesms.plugins.patientview.importer;

public interface CsvDataImporter {
	
	public Object getAdditionalOptionsPanel();
	
	public Object getInformationPanel();
	
	public void importFile(String path, boolean ignoreHeader);
	
	public String getTypeLabel();
}
