package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

public class LongConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {
	
	public static final LongConverter INSTANCE = new LongConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null || values.isEmpty() || values.get(fieldName) == null) {
			return null;
		}
		ParticipantDataLongValue pdv = new ParticipantDataLongValue();
		try {
			pdv.setValue(Long.parseLong(values.get(fieldName)));
		} catch(NumberFormatException e) {
		}
		return pdv;
	}	

}
