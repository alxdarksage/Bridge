package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
		String value = values.get(fieldName);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		pdv.setValue(Long.parseLong(values.get(fieldName)));
		return pdv;
	}	

}
