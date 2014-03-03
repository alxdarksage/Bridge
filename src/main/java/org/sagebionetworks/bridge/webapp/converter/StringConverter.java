package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

public class StringConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {
	
	public static final StringConverter INSTANCE = new StringConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null || values.isEmpty() || values.get(fieldName) == null) {
			return null;
		}
		ParticipantDataStringValue pdv = new ParticipantDataStringValue();
		pdv.setValue(values.get(fieldName));
		return pdv;
	}
	
}
