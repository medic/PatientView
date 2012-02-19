package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

@Entity
@Table(name="medic_flag_conditions")
@DiscriminatorColumn(name="cond_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="cond")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class FlagCondition<E> {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	protected long cid = -1L;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="flag_id", nullable=true)
	protected Flag flag;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="field_id", nullable=true)
	protected MedicFormField field;
	
	@Enumerated(EnumType.ORDINAL)
	private FlagConditionOperation operation;
		
	public abstract boolean evaluate(MedicFormFieldResponse fieldResponse);

	public FlagCondition(){}
	
	public FlagCondition(MedicFormField field, FlagConditionOperation operation, E operand){
		this.setOperand(operand);
		this.setOperation(operation);
		this.field = field;
	}
	
	public long getCid() {
		return cid;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setField(MedicFormField field) {
		this.field = field;
	}

	public MedicFormField getField() {
		return field;
	}

	public abstract void setOperand(E value); 
	
	public abstract void setOperand(String value); 

	public abstract E getOperand();
	
	public abstract String toString();
	
	public abstract boolean isValidOperand(String operand);

	public void setOperation(FlagConditionOperation operation) {
		this.operation = operation;
	}

	public FlagConditionOperation getOperation() {
		return operation;
	}
	
	public static FlagCondition getFlagConditionForDataType(DataType type){
		if(type == DataType.NUMERIC_TEXT_FIELD){
			return new NumericFlagCondition();
		}else if(type == DataType.CHECK_BOX || 
				 type == DataType.POSITIVENEGATIVE || 
				 type == DataType.TRUEFALSE || 
				 type == DataType.YESNO){
			return new BooleanFlagCondition();	
		}else if(type == DataType.TEXT_AREA ||
				 type == DataType.TEXT_FIELD){
			return new StringFlagCondition();
		}
		return null;
	}
}