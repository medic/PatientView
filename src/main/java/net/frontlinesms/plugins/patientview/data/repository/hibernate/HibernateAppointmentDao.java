package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.appointment.Appointment;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.AppointmentDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class HibernateAppointmentDao extends BaseHibernateDao<Appointment> implements AppointmentDao{

	protected HibernateAppointmentDao() {
		super(Appointment.class);
	}

	public void deleteAppointment(Appointment appt) {
		super.delete(appt);
	}

	public List<Appointment> getAllAppointments() {
		return super.getAll();
	}

	public List<Appointment> getAppointmentsForPatient(Patient patient) {
		DetachedCriteria c = DetachedCriteria.forClass(Appointment.class);
		c.add(Restrictions.eq("patient", patient));
		c.addOrder(Order.asc("dateScheduled"));
		return super.getList(c);
	}

	public void saveAppointment(Appointment appt) {
		super.saveWithoutDuplicateHandling(appt);
	}

	public void saveOrUpdateAppointment(Appointment appt) {
		
	}

	public void updateAppointment(Appointment appt) {
		super.updateWithoutDuplicateHandling(appt);
	}
}
