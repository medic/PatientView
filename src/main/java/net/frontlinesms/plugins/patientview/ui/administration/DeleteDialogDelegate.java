package net.frontlinesms.plugins.patientview.ui.administration;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;

public interface DeleteDialogDelegate {

	public void dialogReturned(Boolean delete, String reason, CommunityHealthWorker newCHW);
}
