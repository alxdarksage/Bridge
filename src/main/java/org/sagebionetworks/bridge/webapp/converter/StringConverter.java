package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class StringConverter implements Converter<List<String>, ParticipantDataValue> {
	
	public static final StringConverter INSTANCE = new StringConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty() || values.get(0) == null) {
			return null;
		}
		ParticipantDataStringValue pdv = new ParticipantDataStringValue();
		pdv.setValue(values.get(0));
		return pdv;
	}
	
}
