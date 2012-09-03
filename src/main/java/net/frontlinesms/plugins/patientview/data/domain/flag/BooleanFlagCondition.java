package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

@Entity
@DiscriminatorValue(value = "bool")
public class BooleanFlagCondition extends FlagCondition<Boolean> {
	
	private boolean boolOperand;
	
	@Override
	public boolean evaluate(MedicFormFieldResponse response, MedicFormResponse formResponse, ApplicationContext appCon) {
		boolean v = response.getValue().equalsIgnoreCase("true");
		return v == boolOperand;
	}

	@Override
	public Boolean getOperand() {
		return boolOperand;
	}

	@Override
	public void setOperand(Boolean value) {
		this.boolOperand = value;
	}

	@Override
	public String toString() {
		String result = "\"" + field.getLabel()+"\" is " + String.valueOf(boolOperand);
		return result;
	}

	@Override
	public boolean isValidOperand(String operand) {
		return operand.equalsIgnoreCase("true") || operand.equalsIgnoreCase("false");
	}

	@Override
	public void setOperand(String value) {
		boolOperand = value.equalsIgnoreCase("true");
	}
	
}