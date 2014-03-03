package org.sagebionetworks.bridge.webapp.converter;

import java.text.DecimalFormat;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

public class DoubleToStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {

	public static final DoubleToStringConverter INSTANCE = new DoubleToStringConverter();

	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		Double d = ((ParticipantDataDoubleValue)source).getValue();
		if (d == null) {
			return null;
		}
		return ParticipantDataUtils.getMapForValue(fieldName, format(d));
	}

	public String format(Double d) {
		return new DecimalFormat("0.###").format(d);
	}
	
}
