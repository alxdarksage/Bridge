package org.sagebionetworks.bridge.webapp.converter;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

public class ISODateTimeConverter implements FieldConverter<Map<String,String>, ParticipantDataValue>{

	public static final ISODateTimeConverter INSTANCE = new ISODateTimeConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null || values.isEmpty() || values.get(fieldName) == null) {
			return null;
		}
		// This changed, but hasn't updated, not sure what happened. This 
		// still isn't marked as changed.
		String value = values.get(fieldName);
		DateTimeFormatter formatter;
		if (value.indexOf('T') >= 0) {
			formatter = ISODateTimeFormat.dateTime();
		} else {
			formatter = ISODateTimeFormat.date();
		}
		Date date = DateTime.parse(values.get(fieldName), formatter).toDate();
        ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
        pdv.setValue(date.getTime());
        return pdv;
	}
	
}
