package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

public class EnumeratedFormField extends FormField {

	protected final List<String> enumeratedValues;
	
	public EnumeratedFormField(String name, String label, ParticipantDataColumnType type, boolean immutable,
			boolean defaultable, List<String> enumeratedValues) {
		super(name, label, type, immutable, defaultable);
		this.enumeratedValues = enumeratedValues;
	}

	public List<String> getEnumeratedValues() {
		return enumeratedValues;
	}
	
}
