package net.frontlinesms.plugins.patientview.ui.components;

/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Color;
import java.io.IOException;
import java.util.Calendar;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import thinlet.Thinlet;

/**
 * @author kadu
 *
 */
public class DateSelectorDialog {
	
//> STATIC CONSTANTS
	private static final String COMPONENT_LB_MONTH = "lbMonth";
	private static final String COMPONENT_BT_NEXT = "btNext";
	private static final String COMPONENT_BT_PREVIOUS = "btPrevious";
	private static final String UI_FILE_DATE_SELECTER_FORM = "/ui/core/util/dgDate.xml";

//> INSTANCE PROPERTIES
	/** Logger for this class */
	private final Logger log = FrontlineUtils.getLogger(getClass());
	/** A calendar object with a poorly-defined role */
	private DateTime current;
	/** A month value with a poorly-defined role */
	private int curMonth;
	/** A year value with a poorly-defined role */
	private int curYear;
	/** The {@link Thinlet} ui controller */
	private ExtendedThinlet ui;
	/** The textfield that the date will be inserted into when date selection has completed. */
	private Object textField;
	private int dayToHighlight;
	private int monthToHighlight;
	private int yearToHighlight;
	
	private final static String[] ETHIOPIC_MONTHS = new String[]{
		"M\u00E4sk\u00E4r\u00E4m","\u1E6C\u0259q\u0259mt", "\u1E2A\u0259dar",
		"Ta\u1E2B\u015Ba\u015B", "\u1E6C\u0259rr","Y\u00E4katit",
		"M\u00E4gabit","Miyazya","G\u0259nbot","S\u00E4ne",
		"\u1E24amle","N\u00E4hase","\u1E56ag\u02B7\u0259men"
	};
	
//> CONSTRUCTORS
	/**
	 * Constructs a new date selecter which will update a particular textfield.
	 * @param ui The thinlet ui controller
	 * @param textField The textfield that the selected date will be entered into
	 */
	public DateSelectorDialog(ExtendedThinlet ui, Object textField) {
		this.ui = ui;
		this.textField = textField;
	}
	
	/**
	 * Shows the date selecter dialog, showing the previous date or today.
	 * @throws IOException
	 */
	public void showSelecter() throws IOException {
		Object dialog = ui.parse(UI_FILE_DATE_SELECTER_FORM);
		init(dialog);
		showMonth(dialog);
		ui.add(dialog);
	}
	
	/**
	 * Initialises the dialog buttons, set their action method and gets the dte to be shown.
	 * 
	 * @param dialog
	 */
	private void init(Object dialog) {
		log.trace("ENTER");
		Object prev = ui.find(dialog, COMPONENT_BT_PREVIOUS);
		ui.setCloseAction(dialog, "closeDialog(this)", dialog, this);
		ui.setAction(prev, "previousMonth(dateSelecter)", dialog, this);
		Object next = ui.find(dialog, COMPONENT_BT_NEXT);
		ui.setAction(next, "nextMonth(dateSelecter)", dialog, this);
		current = DateTime.now(InternationalisationUtils.ethiopicChronology);
		setDayToHighlight();
		current = new DateTime(current.getYear(),current.getMonthOfYear(),1,0,0,InternationalisationUtils.ethiopicChronology);
		if (!ui.getText(textField).equals("")) {
			log.debug("Previous date is [" + ui.getText(textField) + "]");
			try {
				current = InternationalisationUtils.getDateFormat().parseDateTime(ui.getText(textField));
				setDayToHighlight();
			} catch (Exception e) {}
		}
		log.debug("Current date is [" + current.toString() + "]");
		curMonth = current.getMonthOfYear();
		curYear = current.getYear();
		log.trace("EXIT");
	}

//> UI EVENT METHODS
	/**
	 * @param dialog
	 */
	public void closeDialog(Object dialog) {
		ui.remove(dialog);
	}

	/**
	 * Method called when the user clicks the next month button on the dialog.
	 * 
	 * @param dialog
	 */
	public void nextMonth(Object dialog) {
		current = current.plusMonths(1);
		curMonth = current.getMonthOfYear();
		curYear = current.getYear();
		showMonth(dialog);
	}
	
	/**
	 * Method called when the user clicks the previous month button on the dialog.
	 * 
	 * @param dialog
	 */
	public void previousMonth(Object dialog) {
		current = current.minusMonths(1);
		curMonth = current.getMonthOfYear();
		curYear = current.getYear();
		showMonth(dialog);
	}
	
	/**
	 * Fill all the dates in the dialog for the current month.
	 * 
	 * @param dialog
	 */
	private void showMonth(Object dialog) {
		log.trace("ENTER");
		current = new DateTime(current.getYear(),current.getMonthOfYear(),1,2,2,InternationalisationUtils.ethiopicChronology);
		String curMonth = ETHIOPIC_MONTHS[current.getMonthOfYear() -1] + " " + curYear;
		Object lbMonth = ui.find(dialog, COMPONENT_LB_MONTH);
		log.debug("Current month [" + curMonth + "]");
		ui.setText(lbMonth, curMonth);
		for (int i = 1; i <= 6; i++) {
			fillRow(dialog, "pn" + i);
		}
		if(current.getMonthOfYear() != this.curMonth) current = current.minusMonths(1);
		log.trace("EXIT");
	}

	public void selectionMade(Object dialog, String day) {
		DateTime dt = new DateTime(this.curYear,this.curMonth,Integer.parseInt(day),0,0,InternationalisationUtils.ethiopicChronology);
		String date = InternationalisationUtils.getDateFormat().print(dt);
		ui.setText(textField, date);
		ui.remove(dialog);
		ui.invokeAction(this.textField);
	}
	
//> UI HELPER METHODS
	/** Sets the day to be highlighted */
	private void setDayToHighlight() {
		dayToHighlight = current.getDayOfMonth();
		monthToHighlight = current.getMonthOfYear();
		yearToHighlight = current.getYear();
	}
	/**
	 * Sets the button texts for the supplied week.
	 * 
	 * @param dialog
	 * @param pnName
	 */
	private void fillRow(Object dialog, String pnName) {
		Object panel = ui.find(dialog, pnName);
		Object buttons[] = ui.getItems(panel);
		cleanButtons(buttons);
		
		int dayOfWeek;
		while (current.getMonthOfYear() == this.curMonth) {
			dayOfWeek = current.getDayOfWeek();
			Object button = buttons[dayOfWeek-1];
			ui.setEnabled(button, true);
			ui.setText(button, String.valueOf(current.getDayOfMonth()));
			ui.setAction(button, "selectionMade(dateSelecter, this.text)", dialog, this);
			if (isDayToHighlight()) {
				ui.setColor(button, Thinlet.FOREGROUND, Color.RED);
			}
			current = current.plusDays(1);
			if (dayOfWeek == Calendar.SATURDAY) {
				//we've reached the end of the week, so we break;
				break;
			}
		}
	}
	
	private void cleanButtons(Object[] buttons) {
		for (Object but : buttons) {
			ui.setText(but, "");
			ui.setEnabled(but, false);
			ui.setColor(but, Thinlet.FOREGROUND, Color.BLUE);
		}
	}
	private boolean isDayToHighlight() {
		return (current.getDayOfMonth() == dayToHighlight 
				&& current.getMonthOfYear() == monthToHighlight
				&& current.getYear() == yearToHighlight);
	}
}
