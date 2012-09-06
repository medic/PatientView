package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.patientview.data.domain.flag.Flag;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.OrderBy;

/**
 * A class that represents a form in the system.
 * MedicForm has a list of fields, a name, and a link
 * to the 'vanilla form' that it represents. This 
 *
 */
@Entity
@Table(name = "medic_forms")
public class MedicForm{

	/** Unique id for this entity. This is for hibernate usage. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long fid;

	@IndexColumn(name = "form_name_index")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parentForm")
	@OrderBy(clause = "position asc")
	private List<MedicFormField> fields;

	/**
	 * The FrontlineSMS form that this Medic form is linked to
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = {},targetEntity=Form.class)
	@JoinColumn(name = "vanilla_form_id", nullable = true)
	private Form vanillaForm;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "form")
	private List<Flag> flags;
	
	private String series;
	
	private Integer seriesPosition;
	
	public enum MedicFormType{
		PATIENT_DATA("Patient Data",new PatientFieldMapping[]{PatientFieldMapping.NAMEFIELD, 
				PatientFieldMapping.BIRTHDATEFIELD,PatientFieldMapping.IDFIELD}), 
		REGISTRATION("Patient Registration",new PatientFieldMapping[]{PatientFieldMapping.NAMEFIELD, 
				PatientFieldMapping.BIRTHDATEFIELD,PatientFieldMapping.IDFIELD,PatientFieldMapping.GENDER,
				PatientFieldMapping.DATE_OF_LAST_AMENORRHEA, PatientFieldMapping.PHONE_NUMBER, 
				PatientFieldMapping.ADDRESS, PatientFieldMapping.MOTHERS_NAME, PatientFieldMapping.FATHERS_NAME,
				PatientFieldMapping.VISIT_DATE}),
		APPOINTMENT("Appointment",new PatientFieldMapping[]{PatientFieldMapping.NAMEFIELD, 
				PatientFieldMapping.BIRTHDATEFIELD,PatientFieldMapping.IDFIELD}),
		NONE("None",new PatientFieldMapping[0]);
		
		public final String name;
		public final PatientFieldMapping[] fields;
		
		private MedicFormType(String name, PatientFieldMapping[] fields){
			this.name = name;
			this.fields = fields;
		}
		
		public String toString(){
			return name;
		}
 	}
	
	@Enumerated(EnumType.ORDINAL)
	private MedicFormType type;
	
	/**
	 * Blank Hibernate Constructor 
	 */
	public MedicForm() {}

	/**
	 * Creates a Medic form with the supplied name. Also Initializes the list of fields 
	 * @param name
	 */
	public MedicForm(String name) {
		this.name = name;
		fields = new ArrayList<MedicFormField>();
		type = MedicFormType.PATIENT_DATA;
	}
	
	/**
	 * Creates a Medic Form with the supplied name and fields.
	 * @param name
	 * @param fields
	 */
	public MedicForm(String name, List<MedicFormField> fields) {
		this.name = name;
		setFields(fields);
		type = MedicFormType.PATIENT_DATA;
	}
	
	/**
	 * creates a MedicForm from a vanilla FrontlineSMS form automatically
	 * 
	 * @param f
	 */
	public MedicForm(Form f) {
		this.vanillaForm = f;
		this.name = f.getName();
		fields = new ArrayList<MedicFormField>();
		for (FormField field : f.getFields()) {
			MedicFormField mff = new MedicFormField(this, DataType.getDataTypeForString(field.getType().name()), field.getLabel());
			fields.add(mff);
		}
		updateFieldPositions();
		type = MedicFormType.PATIENT_DATA;
	}

	/**
	 * Gets the vanilla frontline form that this medic form is linked to
	 * 
	 * @return the form
	 */
	public Form getForm() {
		return vanillaForm;
	}

	/**
	 * Sets the vanilla frontline form that this medic form is linked to
	 * 
	 * @param form
	 */
	public void setForm(Form form) {
		this.vanillaForm = form;
	}

	public void setFields(List<MedicFormField> fields) {
		this.fields = fields;
		if(this.fields == null) return;
		for(MedicFormField mff: fields){
			mff.setForm(this);
		}
		updateFieldPositions();
	}
	
	/**
	 * @return a list of the fields on the form
	 */
	public List<MedicFormField> getFields() {
		return fields;
	}
	
	/**
	 * Adds a field to the form at the end
	 * 
	 * @param field
	 */
	public void addField(MedicFormField field) {
		field.setForm(this);
		fields.add(field);
		field.setPosition(fields.size()-1);
	}

	/**
	 * removes a field from the form
	 * 
	 * @param field
	 */
	public void removeField(MedicFormField field) {
		fields.remove(field);
		updateFieldPositions();
	}
	
	private void updateFieldPositions(){
		for(int i = 0; i < fields.size();i++){
			fields.get(i).setPosition(i);
		}
	}

	/**
	 * @param name
	 *            the name of the form
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the form
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the ID of the form
	 */
	public long getFid() {
		return fid;
	}

	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

	public List<Flag> getFlags() {
		return flags;
	}

	public void setType(MedicFormType type, boolean autoMap){
		if(this.type != null && autoMap){
			Set<PatientFieldMapping> newTypes = new HashSet<PatientFieldMapping>();
			for(PatientFieldMapping mft: type.fields){
				newTypes.add(mft);
			}
			for(MedicFormField field: this.fields){
				if(newTypes.contains(field.getMapping())) continue;
				field.setMapping(null);
				for(PatientFieldMapping pfm : newTypes){
					if(pfm.toString().equalsIgnoreCase(field.getLabel()) || field.getLabel().contains(pfm.toString())){
						field.setMapping(pfm);
						break;
					}
				}
			}
		}else{
			for(MedicFormField field: this.fields){
				field.setMapping(null);
			}
		}
		this.type = type;
	}
	
	public void setType(MedicFormType type) {
		setType(type,false);
	}

	public MedicFormType getType() {
		return type;
	}

	public int getSeriesPosition() {
		return seriesPosition;
	}

	public void setSeriesPosition(int seriesPosition) {
		this.seriesPosition = seriesPosition;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}
}
