package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

/**
 * Converts date to yyyy-mm-dd format, used for date pickers, not friendly for 
 * display.
 */
public class DateToISODateStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {
	
	private static final Logger logger = LogManager.getLogger(DateToISODateStringConverter.class.getName());

	public static final DateToISODateStringConverter INSTANCE = new DateToISODateStringConverter();

	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue pdv) {
		if (pdv == null) {
			return null;
		}
		Long time = ((ParticipantDataDatetimeValue)pdv).getValue();
		if (time == null || time.longValue() == 0L) {
			return null;
		}
		return ParticipantDataUtils.getMapForValue(fieldName, ISODateTimeFormat.date().print(time));
	}

}
