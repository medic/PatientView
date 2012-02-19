package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

@Entity
@DiscriminatorValue(value = "num")
public class NumericFlagCondition extends FlagCondition<Integer> {

	private int numOperand;
	
	@Override
	public boolean evaluate(MedicFormFieldResponse fieldResponse) {
		Integer i = Integer.valueOf(fieldResponse.getValue());
		if(getOperation() == FlagConditionOperation.EQUAL){
			return i == numOperand;
		}else if(getOperation() == FlagConditionOperation.NOT_EQUAL){
			return i != numOperand;
		}else if(getOperation() == FlagConditionOperation.LESS_THAN){
			return i < numOperand;
		}else if(getOperation() == FlagConditionOperation.LESS_THAN_EQUAL_TO){
			return i <= numOperand;
		}else if(getOperation() == FlagConditionOperation.GREATER_THAN){
			return i > numOperand;
		}else{
			return i >= numOperand;
		}
	}

	@Override
	public Integer getOperand() {
		return numOperand;
	}

	@Override
	public void setOperand(Integer value) {
		this.numOperand = value;
	}

	@Override
	public String toString() {
		return "\"" + field.getLabel() + "\" " + getOperation().label + " " + numOperand;
	}

	@Override
	public boolean isValidOperand(String operand) {
		try{
			int i = Integer.valueOf(operand);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	@Override
	public void setOperand(String value) {
		try{
			numOperand = Integer.valueOf(value);
		}catch(Exception e){
			numOperand = -1;
		}
	}
}
