package net.frontlinesms.plugins.patientview.data.domain.flag;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

@Entity
@Table(name="medic_flag_conditions")
@DiscriminatorColumn(name="cond_type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="cond")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class FlagCondition {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long cid;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="flag_id", nullable=true)
	private Flag flag;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name="field_id", nullable=true)
	private MedicFormField field;
	
	public abstract boolean evaluate(MedicFormResponse mfr);

	public FlagCondition(){}
	
	public FlagCondition(Flag flag){
		this.flag = flag;
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
}