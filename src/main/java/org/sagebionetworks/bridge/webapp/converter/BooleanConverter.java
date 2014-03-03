package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataBooleanValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

public class BooleanConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {
	
	public static final BooleanConverter INSTANCE = new BooleanConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null || values.isEmpty() || values.get(fieldName) == null) {
			return null;
		}
		ParticipantDataBooleanValue pdv = new ParticipantDataBooleanValue();
		pdv.setValue(Boolean.parseBoolean(values.get(fieldName)));
		return pdv;
	}
	
}
