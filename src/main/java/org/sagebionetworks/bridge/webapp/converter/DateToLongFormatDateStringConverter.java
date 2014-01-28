package org.sagebionetworks.bridge.webapp.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class DateToLongFormatDateStringConverter implements Converter<ParticipantDataValue, List<String>> {
	
	public static final DateToLongFormatDateStringConverter INSTANCE = new DateToLongFormatDateStringConverter();
	
	@Override
	public List<String> convert(ParticipantDataValue pdv) {
		// These are not thread-safe.
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy (hh:mm a)");
		long time = ((ParticipantDataDatetimeValue)pdv).getValue();
		return Lists.newArrayList(formatter.format(new Date(time)));
	}
	
}
