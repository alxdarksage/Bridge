package org.sagebionetworks.bridge.webapp.converters;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateToDateStringConverter implements Converter<Object,String> {

	public static final DateToDateStringConverter INSTANCE = new DateToDateStringConverter();
	
	private SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
	
	@Override
	public String convert(Object date) {
		if (date == null) {
			return null;
		}
		return formatter.format((Date)date);
	}

}
