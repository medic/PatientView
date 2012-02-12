package net.frontlinesms.plugins.patientview.data.domain.flag;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

public class BooleanFlagCondition extends FlagCondition<Boolean> {

	@Override
	public boolean evaluate(MedicFormFieldResponse response) {
		boolean v = response.getValue().equalsIgnoreCase("true");
		return v == operand;
	}
}