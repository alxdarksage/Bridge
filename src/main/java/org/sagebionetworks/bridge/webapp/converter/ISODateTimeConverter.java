package org.sagebionetworks.bridge.webapp.converter;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class ISODateTimeConverter implements Converter<List<String>, ParticipantDataValue>{

	public static final ISODateTimeConverter INSTANCE = new ISODateTimeConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty() || values.get(0) == null) {
			return null;
		}
		// This changed, but hasn't updated, not sure what happened. This 
		// still isn't marked as changed.
		String value = values.get(0);
		DateTimeFormatter formatter;
		if (value.indexOf('T') >= 0) {
			formatter = ISODateTimeFormat.dateTime();
		} else {
			formatter = ISODateTimeFormat.date();
		}
		Date date = DateTime.parse(values.get(0), formatter).toDate();
        ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
        pdv.setValue(date.getTime());
        return pdv;
	}
	
}
