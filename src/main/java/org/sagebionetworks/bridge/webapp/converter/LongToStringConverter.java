package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

public class LongToStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {
	
	public static final LabToStringConverter INSTANCE = new LabToStringConverter();
	
	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		Long l = ((ParticipantDataLongValue)source).getValue();
		if (l == null) {
			return null;
		}
		return ParticipantDataUtils.getMapForValue(fieldName, l.toString());
	}

}
