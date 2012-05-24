package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.appointment.Appointment;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface AppointmentDao {

	public void deleteAppointment(Appointment appt);
	
	public void saveAppointment(Appointment appt);
	
	public void updateAppointment(Appointment appt);
	
	public void saveOrUpdateAppointment(Appointment appt);
	
	public List<Appointment> getAllAppointments();
	
	public List<Appointment> getAppointmentsForPatient(Patient patient);
}