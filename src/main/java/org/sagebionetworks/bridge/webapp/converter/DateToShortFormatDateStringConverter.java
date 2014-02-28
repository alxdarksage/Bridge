package org.sagebionetworks.bridge.webapp.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

public class DateToShortFormatDateStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {
	
	public static final DateToShortFormatDateStringConverter INSTANCE = new DateToShortFormatDateStringConverter();
	
	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue pdv) {
		if (pdv == null) {
			return null;
		}
		// These are not thread-safe.
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
		Long time = ((ParticipantDataDatetimeValue)pdv).getValue();
		if (time == null || time.longValue() == 0L) {
			return null;
		}
		return ParticipantDataUtils.getMapForValue(fieldName, formatter.format(new Date(time)));
	}
}
