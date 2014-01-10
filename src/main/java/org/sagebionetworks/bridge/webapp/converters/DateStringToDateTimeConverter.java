package org.sagebionetworks.bridge.webapp.converters;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.convert.converter.Converter;

public class DateStringToDateTimeConverter implements Converter<String,Object> {
	
	public static DateStringToDateTimeConverter INSTANCE = new DateStringToDateTimeConverter();

	@Override
	public Object convert(String source) {
		return DateTime.parse(source, ISODateTimeFormat.date()).toDate();
	}

}
