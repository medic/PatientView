package net.frontlinesms.plugins.patientview.ui.advancedtable;

import java.util.List;

import net.frontlinesms.ui.UiGeneratorController;

public class StaticSelectionTableController extends PagedTableController {

	public StaticSelectionTableController(TableActionDelegate delegate, UiGeneratorController uiController) {
		super(delegate, uiController);
	}
	
	public StaticSelectionTableController(TableActionDelegate delegate,UiGeneratorController uiController, Object panel) {
		super(delegate,uiController,panel);
	}
	
	public void setResults(List<?> results){
		Object oldObj = super.getSelectedObject();
		int oldIndex = super.getSelectedIndex();
		Class<?> oldClass = super.currentClass;
		super.setResults(results);
		if(super.currentClass != oldClass || results.size() == 0){
			return;
		}
		for(int i = 0; i < results.size(); i++){
			if(results.get(i).equals(oldObj)){
				super.setSelected(i);
				return;
			}
		}
		super.setSelected(oldIndex);
	}
}
