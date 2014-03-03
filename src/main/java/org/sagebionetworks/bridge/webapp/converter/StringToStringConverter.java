package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

public class StringToStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {

	public static final StringToStringConverter INSTANCE = new StringToStringConverter();
	
	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue pdv) {
		if (pdv == null) {
			return null;
		}
		String value = ((ParticipantDataStringValue)pdv).getValue();
		if (value == null) {
			return null;
		}
		return ParticipantDataUtils.getMapForValue(fieldName, value);
	}

}
