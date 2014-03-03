package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataBooleanValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

public class BooleanToStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {

	public static final BooleanToStringConverter INSTANCE = new BooleanToStringConverter();
	
	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		Boolean b = ((ParticipantDataBooleanValue)source).getValue();
		if (b == null) {
			return null;
		}
		return ParticipantDataUtils.getMapForValue(fieldName, Boolean.toString(b));
	}

}
