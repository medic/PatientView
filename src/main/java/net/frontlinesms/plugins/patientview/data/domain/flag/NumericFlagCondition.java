package net.frontlinesms.plugins.patientview.data.domain.flag;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseSeriesDao;
import net.frontlinesms.plugins.patientview.utils.ExpressionUtils;

import org.springframework.context.ApplicationContext;

@Entity
@DiscriminatorValue(value = "num")
public class NumericFlagCondition extends FlagCondition<String> {

	private String expression;

	@Override
	public boolean evaluate(MedicFormFieldResponse fieldResponse, MedicFormResponse formResponse, ApplicationContext appCon) {
		double i = Double.valueOf(fieldResponse.getValue());
		MedicFormResponseSeriesDao seriesDao = (MedicFormResponseSeriesDao) appCon.getBean("MedicFormResponseSeriesDao");
		Map<String,String> values = seriesDao.getFieldValuesInFormResponseSeries(formResponse,DataType.NUMERIC_TEXT_FIELD);
		double numOperand = evaluate(expression, values);
		if (getOperation() == FlagConditionOperation.EQUAL) {
			return i == numOperand;
		} else if (getOperation() == FlagConditionOperation.NOT_EQUAL) {
			return i != numOperand;
		} else if (getOperation() == FlagConditionOperation.LESS_THAN) {
			return i < numOperand;
		} else if (getOperation() == FlagConditionOperation.LESS_THAN_EQUAL_TO) {
			return i <= numOperand;
		} else if (getOperation() == FlagConditionOperation.GREATER_THAN) {
			return i > numOperand;
		} else {
			return i >= numOperand;
		}
	}

	public double evaluate(String expression, Map<String,String> values) {
		for(String key : values.keySet()){
			expression = expression.replace("{"+key+"}", ""+values.get(key));
		}
		return ExpressionUtils.evaluate(expression);
	}

	@Override
	public String getOperand() {
		return expression;
	}

	@Override
	public void setOperand(String value) {
		this.expression = value;
	}

	@Override
	public String toString() {
		return "\"" + field.getLabel() + "\" " + getOperation().label + " " + expression;
	}

	@Override
	public boolean isValidOperand(String operand) {
		operand = operand.replaceAll("\\{[^}]*\\}", "0");
		return ExpressionUtils.isValidExpression(operand);
	}
}
