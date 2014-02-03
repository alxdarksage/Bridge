package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataBooleanValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class BooleanToStringConverter implements Converter<ParticipantDataValue, List<String>> {

	public static final BooleanToStringConverter INSTANCE = new BooleanToStringConverter();
	
	@Override
	public List<String> convert(ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		Boolean b = ((ParticipantDataBooleanValue)source).getValue();
		if (b == null) {
			return null;
		}
		return Lists.newArrayList(Boolean.toString(b));
	}

}
