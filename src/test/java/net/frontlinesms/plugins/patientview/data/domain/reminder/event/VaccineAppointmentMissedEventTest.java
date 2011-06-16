package net.frontlinesms.plugins.patientview.data.domain.reminder.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.frontlinesms.junit.HibernateTestCase;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.ScheduledDose;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.Vaccine;
import net.frontlinesms.plugins.patientview.data.domain.vaccine.VaccineDose;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.ScheduledDoseDao;
import net.frontlinesms.plugins.patientview.data.repository.VaccineDao;
import net.frontlinesms.plugins.patientview.vaccine.VaccineScheduler;

import org.springframework.beans.factory.annotation.Required;

public class VaccineAppointmentMissedEventTest extends HibernateTestCase {

	private CommunityHealthWorkerDao communityHealthWorkerDao;
	private PatientDao patientDao;
	
	private ScheduledDoseDao scheduledDoseDao;
	private VaccineDao vaccineDao;
	
	private Patient patient;
	
	public void test_getDatesOcurred_threeMissedAppointments_threeDates(){
		setup(1980,Calendar.MAY,15,3);
		VaccineAppointmentMissedEvent vame = new VaccineAppointmentMissedEvent();
		vame.setScheduledDoseDao(scheduledDoseDao);
		assertEquals(3, vame.getEventDates(patient).size());
	}
	
	public void test_getDatesOcurred_threeMissedAppointments_datesCorrect(){
		setup(1980,Calendar.MAY,15,3);
		VaccineAppointmentMissedEvent vame = new VaccineAppointmentMissedEvent();
		vame.setScheduledDoseDao(scheduledDoseDao);
		Calendar first = vame.getEventDates(patient).get(0);
		Calendar second = vame.getEventDates(patient).get(1);
		Calendar third = vame.getEventDates(patient).get(2);
		assertEquals(1980,first.get(Calendar.YEAR));
		assertEquals(5,first.get(Calendar.MONTH));
		assertEquals(15,first.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,second.get(Calendar.YEAR));
		assertEquals(7,second.get(Calendar.MONTH));
		assertEquals(15,second.get(Calendar.DAY_OF_MONTH));
		assertEquals(1980,third.get(Calendar.YEAR));
		assertEquals(9,third.get(Calendar.MONTH));
		assertEquals(15,third.get(Calendar.DAY_OF_MONTH));
	}
	
	public void test_getDatesOcurred_twoMissedAppointments_twoDates(){
		Calendar c = Calendar.getInstance();
		setup(c.get(Calendar.YEAR),c.get(Calendar.MONTH) - 3 ,c.get(Calendar.DAY_OF_MONTH) - 15,3);
		VaccineAppointmentMissedEvent vame = new VaccineAppointmentMissedEvent();
		vame.setScheduledDoseDao(scheduledDoseDao);
		assertEquals(2, vame.getEventDates(patient).size());
	}
	
	public void test_getDatesOcurred_oneMissedAppointments_oneDate(){
		Calendar c = Calendar.getInstance();
		setup(c.get(Calendar.YEAR),c.get(Calendar.MONTH) - 1 ,c.get(Calendar.DAY_OF_MONTH) - 15,3);
		VaccineAppointmentMissedEvent vame = new VaccineAppointmentMissedEvent();
		vame.setScheduledDoseDao(scheduledDoseDao);
		assertEquals(1, vame.getEventDates(patient).size());
	}
	
	public void setup(int year, int month, int day,int hour){
		CommunityHealthWorker chw = new CommunityHealthWorker("Random Name", "+103932929392", Gender.FEMALE, new Date());
		Calendar c = Calendar.getInstance();
		c.set(year, month, day,hour,0);
		patient = new Patient(chw, "Random Johnson", Gender.MALE, new Date(c.getTimeInMillis()));
		communityHealthWorkerDao.saveCommunityHealthWorker(chw);
		patientDao.savePatient(patient);
		Vaccine v = new Vaccine("OPV", true);
		VaccineDose vd1 = new VaccineDose("OPV 1", v, 1, 0, 2, 0, 0, 1);
		VaccineDose vd2 = new VaccineDose("OPV 2", v, 3, 0, 4, 0, 0, 1);
		VaccineDose vd3 = new VaccineDose("OPV 3", v, 5, 0, 6, 0, 0, 1);
		List<VaccineDose> doses = new ArrayList<VaccineDose>();
		doses.add(vd1);
		doses.add(vd2);
		doses.add(vd3);
		v.setDoses(doses);
		vaccineDao.saveOrUpdateVaccine(v);
		List<ScheduledDose> schedDoses = VaccineScheduler.scheduleVaccinesFromBirth(patient, v);
		scheduledDoseDao.saveScheduledDoses(schedDoses);
		assertTrue(true);
	}
	
	@Required
	public void setCommunityHealthWorkerDao(CommunityHealthWorkerDao chwDao) {
		this.communityHealthWorkerDao = chwDao;
	}
	
	@Required
	public void setPatientDao(PatientDao patientDao) {
		this.patientDao = patientDao;
	}
	
	@Required
	public void setScheduledDoseDao(ScheduledDoseDao scheduledDoseDao) {
		this.scheduledDoseDao = scheduledDoseDao;
	}
	
	@Required
	public void setVaccineDao(VaccineDao vaccineDao) {
		this.vaccineDao = vaccineDao;
	}
}
