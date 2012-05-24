package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getDateFormat;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;

import java.text.DateFormat;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.ui.components.DateSelectorDialog;
import net.frontlinesms.ui.ExtendedThinlet;

import org.hibernate.classic.ValidationFailure;

public class DateField extends TextBox<Long> {

	protected DateSelectorDialog ds;
	protected boolean shouldShowDateFormat;
	protected DateFormat df = getDateFormat();
	protected long date;
	Object btn;

	public DateField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate, boolean showDateFormat) {
		super(thinlet, label + (showDateFormat?" ("+getI18nString(FrontlineSMSConstants.DATEFORMAT_YMD)+")":""), delegate);
		btn = thinlet.createButton("");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setColumns(mainPanel, 3);
		ds = new DateSelectorDialog(thinlet, textBox);
	}
	
	public DateField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate) {
		super(thinlet, label + " ("+getI18nString(FrontlineSMSConstants.DATEFORMAT_YMD)+")", delegate);
		btn = thinlet.createButton("");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setColumns(mainPanel, 3);
		ds = new DateSelectorDialog(thinlet, textBox);
	}

	public void showDateSelector() {
		try {
			ds.showSelecter();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void setRawResponse(Long date){
		this.date = date;
		setStringResponse(df.format(date));
	}
	
	public Long getRawResponse(){
		try{
			this.date = df.parse(getStringResponse()).getTime();
			return date;
		}catch(Exception e){
			return null;
		}
	}
	
	public void setDateButtonEnabled(boolean value){
		thinlet.setEnabled(btn, value);
	}

	@Override
	public void validate() throws ValidationFailure{
		if(!hasResponse()){
			return;
		}
		try {
			long date = df.parse(this.getStringResponse()).getTime();
		} catch (Exception e) {
			throw new ValidationFailure("\""+ getLabel().replace(":", "")+ "\" is formatted incorrectly");
		}
	}
}
