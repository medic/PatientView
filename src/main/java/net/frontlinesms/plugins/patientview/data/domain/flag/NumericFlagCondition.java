package net.frontlinesms.plugins.patientview.data.domain.flag;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

public class NumericFlagCondition extends FlagCondition<Integer> {

	@Override
	public boolean evaluate(MedicFormFieldResponse fieldResponse) {
		Integer i = Integer.valueOf(fieldResponse.getValue());
		if(operation == FlagConditionOperation.EQUAL){
			return i == operand;
		}else if(operation == FlagConditionOperation.NOT_EQUAL){
			return i != operand;
		}else if(operation == FlagConditionOperation.LESS_THAN){
			return i < operand;
		}else if(operation == FlagConditionOperation.LESS_THAN_EQUAL_TO){
			return i <= operand;
		}else if(operation == FlagConditionOperation.GREATER_THAN){
			return i > operand;
		}else{
			return i >= operand;
		}
	}
}
