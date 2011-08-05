package net.frontlinesms.plugins.patientview.listener;

import java.util.Random;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.ui.UiGeneratorController;

public class StudyGroupListener implements IncomingMessageListener{

	private static Random random = new Random();
	
	private UiGeneratorController ui;

	public StudyGroupListener(UiGeneratorController ui){
		this.ui = ui;
		ui.getFrontlineController().addIncomingMessageListener(this);
	}
	
	public void incomingMessageEvent(FrontlineMessage message) {
		String text = message.getTextContent();
		if(text.length()<=10 && 
		   text.replaceAll("[^0-9]", "").trim().length()==7 && 
		   (text.charAt(0)=='e' || text.charAt(0)=='E')){
			String newMessage = "E"+ text.replaceAll("[^0-9]", "")+": Group "+ (random.nextBoolean()?"A":"B");
			ui.getFrontlineController().sendTextMessage(message.getSenderMsisdn(), newMessage);
		}
	}
	
	public void deinit(){
		ui.getFrontlineController().removeIncomingMessageListener(this);
	}
}