package org.sagebionetworks.bridge.webapp.converters;

import java.text.SimpleDateFormat;

import org.springframework.core.convert.converter.Converter;

public class DateToDateTimeStringConverter implements Converter<Object,String> {
	
	public static final DateToDateTimeStringConverter INSTANCE = new DateToDateTimeStringConverter();
	
	private SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy (hh:mm a)");
	
	@Override
	public String convert(Object date) {
		return formatter.format((Object)date);
	}
}
