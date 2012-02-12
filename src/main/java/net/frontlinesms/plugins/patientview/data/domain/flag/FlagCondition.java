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
	protected long cid;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="flag_id", nullable=true)
	protected Flag flag;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="field_id", nullable=true)
	protected MedicFormField field;
	
	@Enumerated(EnumType.ORDINAL)
	protected FlagConditionOperation operation;
	
	protected E operand;
	
	public abstract boolean evaluate(MedicFormFieldResponse fieldResponse);

	public FlagCondition(){}
	
	public FlagCondition(MedicFormField field, FlagConditionOperation operation, E operand){
		this.setOperand(operand);
		this.operation = operation;
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

	public void setOperand(E value) {
		this.operand = value;
	}

	public E getOperand() {
		return operand;
	}
}