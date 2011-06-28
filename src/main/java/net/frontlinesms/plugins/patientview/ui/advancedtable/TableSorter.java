package net.frontlinesms.plugins.patientview.ui.advancedtable;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class TableSorter implements Comparator<Object>{
	
	private Method compareMethod;
	
	private boolean isAscending;
	
	private Class<?> compareClass;
	
	public TableSorter(Method compareMethod, boolean isAscending,Class<?> compareClass){
		this.compareMethod = compareMethod;
		this.isAscending = isAscending;
		this.compareClass = compareClass;
	}
	
	public int compare(Object o1, Object o2) {
		//handle the null cases
		if(o1 == null && o2 != null){
			return -1;
		}else if(o1 !=null && o2 == null){
			return 1;
		}else if(o1 == null && o2==null){
			return 0;
		}
		//get the results
		String result1;
		//the first result
		try {
			result1 = (String) compareMethod.invoke(o1,null);
		} catch (Exception e) {
			result1 = "";
		}
		//the second result
		String result2;
		try {
			result2 = (String) compareMethod.invoke(o2,null);
		} catch (Exception e) {
			result2 = "";
		}
		//suggest a compare class if there isn't one already
		if(compareClass == null){
			suggestCompareClass();
		}
		//compare the results
		int result;
		if(compareClass.equals(Date.class)){
			result = compareDates(result1, result2);
		}else if(compareClass.equals(Integer.class)){
			result = compareInts(result1, result2);
		}else{
			result = compareStrings(result1, result2);
		}
		//add ordering
		if(isAscending){
			return result* -1;
		}else{	
			return result;
		}
	}
	
	private void suggestCompareClass(){
		if(compareMethod.getName().toLowerCase().contains("date")){
			compareClass = Date.class;
		}else if(compareMethod.getName().toLowerCase().contains("id")){
			compareClass = Integer.class;
		}else{
			compareClass = String.class;
		}
	}
	
	private int compareStrings(String s1, String s2){
		return s1.compareTo(s2);
	}
	
	private int compareDates(String s1, String s2){
		DateFormat df = InternationalisationUtils.getDateFormat();
		if(df == null){
			df = DateFormat.getDateInstance();
		}
		Date d1, d2;
		try {
			d1 = df.parse(s1);
		} catch (ParseException e) {
			d1 = null;
		}
		try {
			d2 = df.parse(s2);
		} catch (ParseException e) {
			d2 = null;
		}
		return d1.compareTo(d2);
	}
	
	private int compareInts(String s1, String s2){
		Integer i1,i2;
		try{
			i1 = Integer.parseInt(s1);
		}catch(Exception e){i1 =null;}
		try{
			i2 = Integer.parseInt(s2);
		}catch(Exception e2){i2 = null;}
		return i1.compareTo(i2);
	}
}
