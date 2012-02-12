package net.frontlinesms.plugins.patientview.data.domain.flag;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

public class StringFlagCondition extends FlagCondition<String> {

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

}
