package net.frontlinesms.plugins.patientview.ui;


public interface TableActionDelegate {
	public void selectionChanged(Object selectedObject);
	public void doubleClickAction(Object selectedObject);
	public Object getTable();
	public void resultsChanged();
}