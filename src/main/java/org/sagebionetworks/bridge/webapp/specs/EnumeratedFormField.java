package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

public class EnumeratedFormField extends FormField {

	protected List<String> enumeratedValues;
	
	public EnumeratedFormField() {
		super();
	}

	public List<String> getEnumeratedValues() {
		return enumeratedValues;
	}
}
