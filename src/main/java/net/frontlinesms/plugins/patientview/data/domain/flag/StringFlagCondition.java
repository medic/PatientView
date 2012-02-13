package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

@Entity
@DiscriminatorValue(value = "str")
public class StringFlagCondition extends FlagCondition<String> {

	private String operand;
	
	@Override
	public boolean evaluate(MedicFormFieldResponse mfr) {
		if(operation == FlagConditionOperation.EQUAL){
			return operand == mfr.getValue();
		}else if(operation == FlagConditionOperation.NOT_EQUAL){
			return operand != mfr.getValue();
		}else{//operation must be 'contains'
			return mfr.getValue().contains(operand);
		}
	}

	@Override
	public String getOperand() {
		return operand;
	}

	@Override
	public void setOperand(String value) {
		this.operand = value;
	}

}
