package net.frontlinesms.plugins.patientview.data.domain.flag;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;

import org.springframework.context.ApplicationContext;


@Entity
@Table(name="medic_flags")
public class Flag {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;
	
	//The name of this flag
	private String name;
	
	//The templated message of this flag
	private String message;
	
	/**
	 * true if 'any' conditions can be matched
	 * false if 'all' conditions must be matched
	 */
	private boolean any;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="flag",fetch=FetchType.LAZY,targetEntity=FlagCondition.class)
	protected Set<FlagCondition<?>> conditions;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	protected MedicForm form;

	public Flag(){}
	
	public Flag(String name, MedicForm form){
		this.name = name;
		this.form = form;
		conditions = new HashSet<FlagCondition<?>>();
	}
	
	public boolean evaluate(MedicFormResponse mfr, ApplicationContext context){
		MedicFormFieldResponseDao responseDao = (MedicFormFieldResponseDao) context.getBean("MedicFormFieldResponseDao");
		for(FlagCondition<?> c: conditions){
			MedicFormFieldResponse response = responseDao.getResponseForFormResponseAndField(mfr, c.getField());
			boolean condResult = c.evaluate(response);
			if(isAny() && condResult){
				return true;
			}else if(!isAny() && !condResult){
				return false;
			}
		}
		if(isAny()){
			return false;
		}else{
			return true;
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public MedicForm getForm(){
		return form;
	}
	
	public void setForm(MedicForm form){
		this.form = form;
	}
	
	public void addCondition(FlagCondition<?> c){
		c.setFlag(this);
		conditions.add(c);
	}
	
	public void removeCondition(FlagCondition<?> c){
		conditions.remove(c);
	}

	public void setAny(boolean any) {
		this.any = any;
	}

	public boolean isAny() {
		return any;
	}

	public long getFid() {
		return fid;
	}
}
