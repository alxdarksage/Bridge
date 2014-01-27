package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;


import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class LongToStringConverter implements Converter<ParticipantDataValue, List<String>> {
	
	public static final LabToStringConverter INSTANCE = new LabToStringConverter();
	
	@Override
	public List<String> convert(ParticipantDataValue source) {
		Long l = ((ParticipantDataLongValue)source).getValue();
		return Lists.newArrayList(l.toString());
	}

}
