package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getDateFormat;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18nString;
import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.ui.components.DateSelectorDialog;
import net.frontlinesms.ui.ExtendedThinlet;

import org.hibernate.classic.ValidationFailure;
import org.joda.time.format.DateTimeFormatter;

public class DateField extends TextBox<Long> {

	protected DateSelectorDialog ds;
	protected boolean shouldShowDateFormat;
	protected DateTimeFormatter df = getDateFormat();
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
		setStringResponse(df.print(date));
	}
	
	public Long getRawResponse(){
		try{
			this.date = df.parseMillis(getStringResponse());
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
			long date = df.parseMillis(this.getStringResponse());
		} catch (Exception e) {
			throw new ValidationFailure("\""+ getLabel().replace(":", "")+ "\" is formatted incorrectly");
		}
	}
}
