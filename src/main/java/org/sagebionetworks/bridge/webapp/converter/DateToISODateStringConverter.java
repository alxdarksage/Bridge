package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

/**
 * Converts date to yyyy-mm-dd format, used for date pickers, not friendly for 
 * display.
 */
public class DateToISODateStringConverter implements Converter<ParticipantDataValue, List<String>> {
	
	private static final Logger logger = LogManager.getLogger(DateToISODateStringConverter.class.getName());

	public static final DateToISODateStringConverter INSTANCE = new DateToISODateStringConverter();

	@Override
	public List<String> convert(ParticipantDataValue pdv) {
		if (pdv == null) {
			return null;
		}
		Long time = ((ParticipantDataDatetimeValue)pdv).getValue();
		if (time == null || time.longValue() == 0L) {
			return null;
		}
		return Lists.newArrayList(ISODateTimeFormat.date().print(time));
	}

}
