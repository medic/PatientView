package net.frontlinesms.plugins.patientview.listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.util.StringUtils;

public class VaccineScheduleListener implements IncomingMessageListener {

	private UiGeneratorController ui;

	public VaccineScheduleListener(UiGeneratorController ui){
		this.ui = ui;
		ui.getFrontlineController().addIncomingMessageListener(this);
	}
	
	public void incomingMessageEvent(FrontlineMessage message) {
		if(shouldReply(message.getTextContent())){
			//generate the reply
			String reply = getReplyForText(message.getTextContent());
			//if there is a reply, send it
			if(StringUtils.hasText(reply)){
				ui.getFrontlineController().sendTextMessage(message.getSenderMsisdn(),reply);
			}
		}
	}

	/**
	 * If the message is at least 8 characters and is composed
	 * mostly of numbers, then we should reply
	 * @param text
	 * @return
	 */
	public boolean shouldReply(String text){
		int beforeLength = text.length();
		int afterLength = text.replaceAll("[^0-9]", "").length();
		return beforeLength >= 8 && afterLength == 8 && (beforeLength - afterLength) < 5;
	}
	
	public String getReplyForText(String message) {
		message = message.trim().replaceAll("[^0-9]", "");
		int day, month, year;
		try {
			day = Integer.parseInt(message.substring(0, 2));
			month = Integer.parseInt(message.substring(2, 4))-1;
			year = Integer.parseInt(message.substring(4));
		} catch (Exception e) {
			return null;
		}
		//don't respond to pre-2005 dates
		if(year < 2005) return "The year of birth must be 2005 or later";

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		StringBuilder response = new StringBuilder("TIKA schedule: \n");

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		// Calculate BCG, OPV, and Hep B at birth; append to string
		response = response.append(df.format(calendar.getTime()) + " BCG, OPV, HBV. \n");

		// Calculate DPT, OPV, and Hep B at 6 weeks; append to string
		calendar.add(Calendar.WEEK_OF_YEAR, 6);
		response = response.append(df.format(calendar.getTime()) + " DPT, OPV, HBV. \n");

		// Calculate DPT, OPV, and Hep B at 10 weeks; append to string
		calendar.add(Calendar.WEEK_OF_YEAR, 4);
		response = response.append(df.format(calendar.getTime()) + " DPT, OPV, HBV. \n");

		// Calculate DPT, OPV, and Hep B at 14 weeks; append to string
		calendar.add(Calendar.WEEK_OF_YEAR, 4);
		response = response.append(df.format(calendar.getTime()) + " DPT, OPV, HBV. \n");

		// Calculate MCV at 9 months; append to string
		calendar.add(Calendar.WEEK_OF_YEAR, 22);
		response = response.append(df.format(calendar.getTime()) + " MCV.");
		return response.toString();
	}
	
	public void deinit(){
		ui.getFrontlineController().removeIncomingMessageListener(this);
	}
}