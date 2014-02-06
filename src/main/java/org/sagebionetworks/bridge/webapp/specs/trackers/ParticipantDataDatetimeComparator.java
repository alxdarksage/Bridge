package org.sagebionetworks.bridge.webapp.specs.trackers;

import java.util.Comparator;

import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;

public class ParticipantDataDatetimeComparator implements Comparator<ParticipantDataRow> {

	private String fieldName;
	
	public ParticipantDataDatetimeComparator(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public int compare(ParticipantDataRow row0, ParticipantDataRow row1) {
		Long cd0 = ((ParticipantDataDatetimeValue)row0.getData().get(fieldName)).getValue();
		Long cd1 = ((ParticipantDataDatetimeValue)row1.getData().get(fieldName)).getValue();
		return cd1.compareTo(cd0);
	}
}
