package net.frontlinesms.plugins.patientview.responsemapping;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.repository.FormResponseDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

public class RandomFormGenerator {
	
	private static DateFormat df = InternationalisationUtils.getDateFormat();

	private static Random rand = new Random();
	private static String[] firsts = { "Dieterich", "Dolores", "Freddy", "Alex",
			"Charlie", "Lindsay", "Winnie", "Terrence", "Wilson", "Jenny",
			"Meghan", "Katherine", "Poe", "Phillip", "Andrew", "Elizabeth",
			"Whitney", "Frank", "Jared", "Maximillian", "Wylie","Theodore", "Margot",
			"Forscythe","Lars","Sarah","Teddy","Fitz","Humphrey","James","Mark","Jesse", "Tierney","Brooks","Alice"};
	// list of last names
	private static String[] lasts = { "Lawson", "Threadbare", "Evermore", "Brown",
			"DeWilliams", "Taraban", "Polombo", "Benter", "Trought",
			"Finkley", "Coriander", "Groesbeck", "Trounce", "Longbottom",
			"Yip", "Fiars", "Trunch", "Whelp", "Schy", "Munificent",
			"Coyote","Brown","Black","Ames","Chavez","Richards","Phillips","Ballard"
			,"Roosevelt","Jackson","Trueblood","Wachowsky","Corlogne","O'Dea","Booker" };
	
	private PatientDao patientDao;
	private FormResponseDao formResponseDao;
	private MedicFormDao formDao;
	
	public RandomFormGenerator(ApplicationContext appCon){
		patientDao = (PatientDao) appCon.getBean("PatientDao");
		formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		formResponseDao = (FormResponseDao) appCon.getBean("formResponseDao");
	}
	
	public FormResponse generateRandomFormResponse(Form form,Patient subject){
		MedicForm medicForm = formDao.getMedicFormForForm(form);
		ArrayList<ResponseValue> responses = new ArrayList<ResponseValue>();
		for(MedicFormField field: medicForm.getFields()){
			if(field.getDatatype() != DataType.WRAPPED_TEXT && field.getDatatype() != DataType.TRUNCATED_TEXT){
				responses.add(new ResponseValue(getRandomResponse(field,subject)));
			}
		}
		FormResponse formResponse = new FormResponse(getRandomPhoneNumber(), form, responses);
		formResponseDao.saveResponse(formResponse);
		return formResponse;
	}
	
	private String getRandomResponse(MedicFormField field, Person subject){
		switch(field.getDatatype()){
			case CHECK_BOX: return new Boolean(rand.nextBoolean()).toString();				
			case TEXT_FIELD:
				if(field.getMapping() == PatientFieldMapping.NAMEFIELD){
					return getName(subject);
				}else if(field.getMapping() == PatientFieldMapping.PHONE_NUMBER){
					return getPhoneNumber(subject);
				}else if(field.getMapping() == PatientFieldMapping.IDFIELD){
					return getId((Patient) subject);
				}else if(field.getMapping() == PatientFieldMapping.GENDER){
					return getGender(subject);
				}else{
					return "response for text field";
				}
			case TEXT_AREA: return "Additional notes for a text area";
			case DATE_FIELD:
				if(field.getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
					return getRandomBirthdate(subject);
				}else{
					return df.format(getRandomDate());
				}
			case NUMERIC_TEXT_FIELD: 
				if(field.getMapping() == PatientFieldMapping.IDFIELD){
					return getId((Patient) subject);
				}else{
					return rand.nextInt(500)+"";
				}
			default: return "";
		}
	}
	
	private String getGender(Person subject){
		if(subject != null){
			return subject.getGender().toString();
		}else{
			return rand.nextBoolean()?"male":"female";
		}
	}
	
	private String getPhoneNumber(Person subject){
		if(subject != null && StringUtils.hasText(subject.getPhoneNumber())){
			return subject.getPhoneNumber();
		}else{
			return getRandomPhoneNumber();
		}
	}

	private String getRandomPhoneNumber() {
		String result = "";
		for(int i = 0; i < 10; i++){
			result += rand.nextInt(10);
		}
		return result;
	}

	private String getId(Patient subject){
		if(subject != null && StringUtils.hasText(subject.getStringID())){
			return subject.getStringID();
		}else{
			return String.valueOf(rand.nextInt(10000000));
		}
	}
	private String getName(Person subject){
		if(subject != null){
			return subject.getName();
		}else{
			return getRandomName();
		}
	}
	
	private String getRandomName(){
		return firsts[rand.nextInt(firsts.length)] + " " + lasts[rand.nextInt(lasts.length)];
	}
	
	private String getRandomBirthdate(Person subject){
		if(subject != null){
			return df.format(subject.getBirthdate());
		}else{
			return df.format(getRandomDate());
		}
	}
	
	private Date getRandomDate(){
		int day = rand.nextInt(29);
		int month = rand.nextInt(12);
		int year = rand.nextInt(112);
		GregorianCalendar calendar = new GregorianCalendar(1900 + year,month,day);
		return calendar.getTime();
	}
}
