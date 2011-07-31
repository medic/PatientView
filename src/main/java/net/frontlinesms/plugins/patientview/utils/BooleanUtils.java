package net.frontlinesms.plugins.patientview.utils;

public class BooleanUtils {

	public static boolean parseBoolean(String booleanString) throws Exception{
		if(booleanString.equalsIgnoreCase("yes") || booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("1")){
			return true;
		}else{
			return false;
		}
	}
}
