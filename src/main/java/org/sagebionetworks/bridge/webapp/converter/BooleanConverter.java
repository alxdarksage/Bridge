package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataBooleanValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class BooleanConverter implements Converter<List<String>, ParticipantDataValue> {
	
	public static final BooleanConverter INSTANCE = new BooleanConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		ParticipantDataBooleanValue pdv = new ParticipantDataBooleanValue();
		pdv.setValue(Boolean.parseBoolean(values.get(0)));
		return pdv;
	}
	
}
