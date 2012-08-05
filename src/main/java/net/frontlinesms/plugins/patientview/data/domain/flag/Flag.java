package net.frontlinesms.plugins.patientview.data.domain.flag;

import java.util.HashSet;
import java.util.List;
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

import net.frontlinesms.data.domain.Group;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
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
		
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	private Group contactGroup;
	
	/**
	 * true if 'any' conditions can be matched
	 * false if 'all' conditions must be matched
	 */
	private boolean any;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="flag",fetch=FetchType.EAGER,targetEntity=FlagCondition.class)
	private Set<FlagCondition<?>> conditions;
	
	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false)
	protected MedicForm form;

	public Flag(){}
	
	public Flag(String name, MedicForm form){
		this.name = name;
		this.form = form;
		setConditions(new HashSet<FlagCondition<?>>());
	}
	
	public boolean evaluate(MedicFormResponse mfr, ApplicationContext context){
		MedicFormFieldResponseDao responseDao = (MedicFormFieldResponseDao) context.getBean("MedicFormFieldResponseDao");
		for(FlagCondition<?> c: getConditions()){
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
	
	public String generateMessage(MedicFormResponse response, ApplicationContext appCon){
		String result = new String(message);
		String name = response.getSubject() != null? response.getSubjectName():"Unknown Patient";
		result = result.replaceAll("\\{patient name\\}", name);
		String vhwName = "Unknown VHW";
		if(response.getSubject() != null && ((Patient)response.getSubject()).getChw() != null){
			vhwName = ((Patient) response.getSubject()).getCHWName();
		}
		result = result.replaceAll("\\{vhw name\\}", vhwName);
		
		MedicFormFieldResponseDao fieldResponseDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		List<MedicFormFieldResponse> responses = fieldResponseDao.getResponsesForFormResponse(response);
		for(MedicFormFieldResponse fieldResponse: responses){
			result = result.replaceAll("\\{" + fieldResponse.getFieldLabel() + "\\}", fieldResponse.getValue());
		}
		return result;
	}
	
	public MedicForm getForm(){
		return form;
	}
	
	public void setForm(MedicForm form){
		this.form = form;
	}
	
	public void addCondition(FlagCondition<?> c){
		c.setFlag(this);
		getConditions().add(c);
	}
	
	public void removeCondition(FlagCondition<?> c){
		getConditions().remove(c);
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

	public void setConditions(Set<FlagCondition<?>> conditions) {
		this.conditions = conditions;
	}

	public Set<FlagCondition<?>> getConditions() {
		return conditions;
	}

	public Group getContactGroup() {
		return contactGroup;
	}

	public void setContactGroup(Group contactGroup) {
		this.contactGroup = contactGroup;
	}
}
