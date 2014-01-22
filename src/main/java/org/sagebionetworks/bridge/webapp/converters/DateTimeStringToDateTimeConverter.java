package org.sagebionetworks.bridge.webapp.converters;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.convert.converter.Converter;

public class DateTimeStringToDateTimeConverter implements Converter<String,Object> {

	public static DateTimeStringToDateTimeConverter INSTANCE = new DateTimeStringToDateTimeConverter();
	
	@Override
	public Object convert(String source) {
		if (source == null) {
			return null;
		}
		return DateTime.parse(source, ISODateTimeFormat.dateTime()).toDate();
	}

}
