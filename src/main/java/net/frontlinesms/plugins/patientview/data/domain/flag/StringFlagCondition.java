package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

import org.springframework.context.ApplicationContext;

@Entity
@DiscriminatorValue(value = "str")
public class StringFlagCondition extends FlagCondition<String> {

	private String strOperand;
	
	@Override
	public boolean evaluate(MedicFormFieldResponse mfr, MedicFormResponse formResponse, ApplicationContext appCon) {
		if(getOperation() == FlagConditionOperation.EQUAL){
			return strOperand == mfr.getValue();
		}else if(getOperation() == FlagConditionOperation.NOT_EQUAL){
			return strOperand != mfr.getValue();
		}else{//operation must be 'contains'
			return mfr.getValue().contains(strOperand);
		}
	}

	@Override
	public String getOperand() {
		return strOperand;
	}

	@Override
	public void setOperand(String value) {
		this.strOperand = value;
	}
	public String toString(){
		return "\"" + field.getLabel() + "\" " + getOperation().label + " " + strOperand;
	}

	@Override
	public boolean isValidOperand(String operand) {
		return true;
	}
	
}
