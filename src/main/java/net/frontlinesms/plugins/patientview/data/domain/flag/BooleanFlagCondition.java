package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

@Entity
@DiscriminatorValue(value = "bool")
public class BooleanFlagCondition extends FlagCondition<Boolean> {
	
	private boolean operand;
	
	@Override
	public boolean evaluate(MedicFormFieldResponse response) {
		boolean v = response.getValue().equalsIgnoreCase("true");
		return v == operand;
	}

	@Override
	public Boolean getOperand() {
		return operand;
	}

	@Override
	public void setOperand(Boolean value) {
		this.operand = value;
	}
}