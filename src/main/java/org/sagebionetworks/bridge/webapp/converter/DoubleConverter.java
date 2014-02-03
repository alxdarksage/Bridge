package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class DoubleConverter implements Converter<List<String>, ParticipantDataValue> {
	
	public static final DoubleConverter INSTANCE = new DoubleConverter();

	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty() || values.get(0) == null) {
			return null;
		}
		ParticipantDataDoubleValue pdv = new ParticipantDataDoubleValue();
		pdv.setValue(Double.parseDouble(values.get(0)));
		return pdv;
	}	

}
