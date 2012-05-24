package net.frontlinesms.plugins.patientview.vaccine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.utils.TimeUtils;

public class VaccineSchedulerTest extends BaseTestCase{

	private Calendar c;
	private Patient p;
	private Vaccine v;
	
	private void setup(int year, int month, int day, int hour){
		c = Calendar.getInstance();
		c.set(year,month, day,hour,0);
		p = new Patient(null, "", Gender.MALE, c.getTimeInMillis());
		v = new Vaccine("OPV", true);
		//OPV at 1 months w/1 month window
		VaccineDose vd1 = new VaccineDose("OPV 1", v, 1, 0);
		//OPV at 3 months w/1 month window
		VaccineDose vd2 = new VaccineDose("OPV 2", v, 3, 0);
		//OPV at 5 months w/1 month window
		VaccineDose vd3 = new VaccineDose("OPV 3", v, 5, 0);
		List<VaccineDose> doses = new ArrayList<VaccineDose>();
		doses.add(vd1);
		doses.add(vd2);
		doses.add(vd3);
		v.setDoses(doses);
	}
	
/*	public void test_scheduleVaccinesFromBirth_justMonths_scheduledProperly(){
		setup(1980,Calendar.MAY,15,3);
		List<ScheduledDose> schedDoses = VaccineScheduler.instance().scheduleVaccinesFromBirth(p, v);
		//check the size
		assertEquals(3, schedDoses.size());
		//check the first one
		Calendar first = TimeUtils.getCalendar(schedDoses.get(0).getWindowStartDate());
		assertEquals(Calendar.JUNE,first.get(Calendar.MONTH));
		assertEquals(15,first.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,first.get(Calendar.YEAR));
		//check the second one
		Calendar second = TimeUtils.getCalendar(schedDoses.get(1).getWindowStartDate());
		//assert
		assertEquals(Calendar.AUGUST,second.get(Calendar.MONTH));
		assertEquals(15,second.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,second.get(Calendar.YEAR));
		//check the third one
		Calendar third = TimeUtils.getCalendar(schedDoses.get(2).getWindowStartDate());
		//assert
		assertEquals(Calendar.OCTOBER,third.get(Calendar.MONTH));
		assertEquals(15,third.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,third.get(Calendar.YEAR));
	}*/
/*
	public void test_scheduleVaccinesFromBirth_wrapsMonths_scheduledProperly(){
		c = Calendar.getInstance();
		c.set(1980,Calendar.MAY, 28,3,0);
		p = new Patient(null, "", Gender.MALE, c.getTimeInMillis());
		v = new Vaccine("OPV", true);
		//OPV at 1 months w/1 month window
		VaccineDose vd1 = new VaccineDose("OPV 1", v, 1, 20, 2, 0, 0, 1);
		//OPV at 3 months w/1 month window
		VaccineDose vd2 = new VaccineDose("OPV 2", v, 3, 18, 4, 0, 0, 1);
		//OPV at 5 months w/1 month window
		VaccineDose vd3 = new VaccineDose("OPV 3", v, 5, 9, 6, 0, 0, 1);
		List<VaccineDose> doses = new ArrayList<VaccineDose>();
		doses.add(vd1);
		doses.add(vd2);
		doses.add(vd3);
		v.setDoses(doses);
		List<ScheduledDose> schedDoses = VaccineScheduler.instance().scheduleVaccinesFromBirth(p, v);
		//check the size
		assertEquals(3, schedDoses.size());
		//check the first one
		Calendar first = TimeUtils.getCalendar(schedDoses.get(0).getWindowStartDate());
		assertEquals(Calendar.JULY,first.get(Calendar.MONTH));
		assertEquals(18,first.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,first.get(Calendar.YEAR));
		//check the second one
		Calendar second = TimeUtils.getCalendar(schedDoses.get(1).getWindowStartDate());
		//second date should be Aug 21st 1980
		assertEquals(Calendar.SEPTEMBER,second.get(Calendar.MONTH));
		assertEquals(15,second.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,second.get(Calendar.YEAR));
		//check the third one
		Calendar third = TimeUtils.getCalendar(schedDoses.get(2).getWindowStartDate());
		//third date should be October 16th 1980
		assertEquals(Calendar.NOVEMBER,third.get(Calendar.MONTH));
		assertEquals(6,third.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,third.get(Calendar.YEAR));
	}*/
}