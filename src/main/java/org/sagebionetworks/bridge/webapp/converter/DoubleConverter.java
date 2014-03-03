package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

public class DoubleConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {
	
	public static final DoubleConverter INSTANCE = new DoubleConverter();

	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null || values.isEmpty() || values.get(fieldName) == null) {
			return null;
		}
		ParticipantDataDoubleValue pdv = new ParticipantDataDoubleValue();
		String value = values.get(fieldName);
		if (value == null || "".equals(value)) {
			return null;
		}
		pdv.setValue(Double.parseDouble(values.get(fieldName)));
		return pdv;
	}	

}
