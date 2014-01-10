package org.sagebionetworks.bridge.webapp.converters;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateToDateStringConverter implements Converter<Date,String> {

	private SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
	
	@Override
	public String convert(Date date) {
		return formatter.format(date);
	}

}
