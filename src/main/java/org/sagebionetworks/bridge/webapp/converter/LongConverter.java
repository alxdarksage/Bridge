package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class LongConverter implements Converter<List<String>, ParticipantDataValue> {
	
	public static final LongConverter INSTANCE = new LongConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		ParticipantDataLongValue pdv = new ParticipantDataLongValue();
		pdv.setValue(Long.parseLong(values.get(0)));
		return pdv;
	}	

}
