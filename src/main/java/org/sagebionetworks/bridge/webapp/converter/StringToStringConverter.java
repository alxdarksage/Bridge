package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class StringToStringConverter implements Converter<ParticipantDataValue, List<String>> {

	public static final StringToStringConverter INSTANCE = new StringToStringConverter();
	
	@Override
	public List<String> convert(ParticipantDataValue pdv) {
		if (pdv == null) {
			return null;
		}
		return Lists.newArrayList(((ParticipantDataStringValue)pdv).getValue());
	}

}
