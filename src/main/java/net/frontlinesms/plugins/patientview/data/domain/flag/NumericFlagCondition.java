package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

@Entity
@DiscriminatorValue(value = "num")
public class NumericFlagCondition extends FlagCondition<Integer> {

	private int operand;
	
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

	@Override
	public Integer getOperand() {
		return operand;
	}

	@Override
	public void setOperand(Integer value) {
		this.operand = value;
	}

	@Override
	public String toString() {
		return "\"" + field.getLabel() + "\" " + operation.label + " " + operand;
	}
}
