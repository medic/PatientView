package net.frontlinesms.plugins.patientview.search.impl;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.flag.TriggeredFlag;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.TriggeredFlagDao;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.springframework.context.ApplicationContext;

public class TriggeredFlagResultSet extends PagedResultSet {

	private List<TriggeredFlag> results;
	
	private String patientName;
	private Patient patient;
	private String flagName;
	private boolean active;
	private boolean resolved;
	
	private TriggeredFlagDao flagDao;
	
	public TriggeredFlagResultSet(ApplicationContext appCon){
		this.flagDao = (TriggeredFlagDao) appCon.getBean("TriggeredFlagDao");
		super.setPageSize(30);
		setActive(true);
		setResolved(false);
	}
	
	@Override
	public List<TriggeredFlag> getFreshResultsPage() {
		if(patient != null){
			this.results = flagDao.findTriggeredFlags(patient, resolved,active, getFirstResultOnPage() -1, pageSize);
		}else{
			this.results = flagDao.findTriggeredFlags(flagName, patientName, resolved, active, getFirstResultOnPage()-1, pageSize);
		}
		return results;
	}

	@Override
	public List<TriggeredFlag> getResultsPage() {
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setFlagName(String flagName) {
		this.flagName = flagName;
	}

	public String getFlagName() {
		return flagName;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientName() {
		return patientName;
	}

}
