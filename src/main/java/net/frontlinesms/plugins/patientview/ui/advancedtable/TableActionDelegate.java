package net.frontlinesms.plugins.patientview.ui.advancedtable;


public interface TableActionDelegate {
	public void selectionChanged(Object selectedObject);
	public void doubleClickAction(Object selectedObject);
	public void resultsChanged();
}
