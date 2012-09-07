package net.frontlinesms.plugins.patientview;

import net.frontlinesms.data.domain.Group;
import net.frontlinesms.resources.UserHomeFilePropertySet;

public class PVProperties extends UserHomeFilePropertySet {

	private static PVProperties instance;
	
	private static final String NOTIFICATION_GROUP = "form.series.error.notification.group";
	private static final String STRICT_SERIES_CHECKING = "enforce.series.order";

	protected PVProperties() {
		super("patientview");
	}
	
	public void setShouldEnforceSeriesOrder(boolean val){
		super.setPropertyAsBoolean(STRICT_SERIES_CHECKING, val);
	}
	
	public boolean shouldEnforceSeriesOrder(){
		return super.getPropertyAsBoolean(STRICT_SERIES_CHECKING, false);
	}
	
	public void setFormSeriesErrorNotificationGroup(Group group){
		super.setProperty(NOTIFICATION_GROUP, group.getPath());
	}
	
	public String getFormSeriesErrorNotificationGroupPath(){
		return super.getProperty(NOTIFICATION_GROUP);
	}

	public static synchronized PVProperties getInstance() {
		if(instance == null) {
			try {
				instance = new PVProperties();
			} catch (Exception ex) {
				// If we can't find the build properties, we may have serious issues later on
				throw new IllegalStateException("Could not load build properties!", ex);
			}
		}
		return instance;
	}
}
