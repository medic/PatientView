package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.BooleanFlagCondition;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagCondition;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagConditionOperation;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.ui.ViewHandler;
import net.frontlinesms.plugins.patientview.ui.administration.tabs.FlagAdministrationPanelController;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class EditConditionPanel extends ViewHandler {
	
	private static final String UI_XML = "/ui/plugins/patientview/administration/flags/editConditionPanel.xml"; 

	private static final String FIELD_SELECT = "fieldSelect";
	private static final String OPERATOR_SELECT = "operatorSelect";
	private static final String OPERAND_FIELD = "operandField";
	
	private FlagCondition<?> condition;
	private MedicForm form;
	
	private MedicFormFieldDao fieldDao;
	
	private FlagAdministrationPanelController parent;
	
	public EditConditionPanel(UiGeneratorController ui, ApplicationContext appCon, MedicForm form, FlagCondition condition, FlagAdministrationPanelController parent) {
		super(ui, appCon,UI_XML);
		this.condition = condition;
		this.form = form;
		this.parent = parent;
		fieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		populateFieldSelect();
		if(condition != null){
			ui.setText(find(OPERAND_FIELD),condition.getOperand().toString());
			ui.setEnabled(find(FIELD_SELECT), false);
		}
	}
	
	private MedicFormField getSelectedField(){
		return (MedicFormField) ui.getAttachedObject(ui.getSelectedItem(find(FIELD_SELECT)));
	}
	
	private FlagConditionOperation getSelectedOperation(){
		return (FlagConditionOperation) ui.getAttachedObject(ui.getSelectedItem(find(OPERATOR_SELECT)));
	}
	
	private void populateFieldSelect(){
		List<MedicFormField> fields = fieldDao.getFieldsOnForm(form);
		int index = 0;
		for(MedicFormField field: fields){
			if(FlagConditionOperation.isOperable(field.getDatatype())){
				add(find(FIELD_SELECT),ui.createComboboxChoice(field.getLabel(),field));
				if(condition != null && field.getFid() == condition.getField().getFid()){
					ui.setSelectedIndex(find(FIELD_SELECT), index);
				}
				index++;
			}
		}
		if(condition == null){
			System.out.println("Setting selection");
			ui.setSelectedIndex(find(FIELD_SELECT), 0);
		}
		fieldSelectionChanged();
	}

	public void fieldSelectionChanged(){
		MedicFormField mff = getSelectedField();
		ui.removeAll(find(OPERATOR_SELECT));
		if(mff == null) System.out.println("BAD");
		List<FlagConditionOperation> operations = FlagConditionOperation.getOperationsForDataType(mff.getDatatype());
		if(operations.size() == 0){
			add(find(OPERATOR_SELECT),ui.createComboboxChoice("No available operations", null));
		}else{
			int index = 0;
			for(FlagConditionOperation op: operations){
				add(find(OPERATOR_SELECT),ui.createComboboxChoice(op.label, op));
				if(condition != null && op == condition.getOperation()){
					ui.setSelectedIndex(find(OPERATOR_SELECT), index);
				}
				index++;
			}
		}
		if(condition == null || operations.size() == 0){
			ui.setSelectedIndex(find(OPERATOR_SELECT), 0);
		}
		operationSelectionChanged();
	}
	
	public void operationSelectionChanged(){
		if(getSelectedOperation().isUnary){
			ui.setVisible(find(OPERAND_FIELD), false);
		}else{
			ui.setVisible(find(OPERAND_FIELD), true);
		}
	}

	public void saveCondition(){
		if(condition == null){
			condition = FlagCondition.getFlagConditionForDataType(getSelectedField().getDatatype());
		}
		condition.setOperation(getSelectedOperation());
		condition.setField(getSelectedField());
		String operand = ui.getText(find(OPERAND_FIELD));

		//manually set the operand if its a boolean flag condition
		if(condition instanceof BooleanFlagCondition){
			if(getSelectedOperation() == FlagConditionOperation.TRUE){
				operand = "true";
			}else{
				operand = "false";
			}
		}
		//check the operand
		if(!condition.isValidOperand(operand)){
			String alertMessage= "Cannot compare fields of type \"" + 
								getSelectedField().getDataTypeName() + 
								"\" with the value \""+operand + "\".";
			ui.alert(alertMessage);
			ui.requestFocus(find(OPERAND_FIELD));
			return;
		}
		condition.setOperand(operand);
		parent.conditionCreated(condition);
	}
	
	public void cancel(){
		parent.conditionEditingCancelled();
	}
}
