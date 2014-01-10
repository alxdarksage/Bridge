package org.sagebionetworks.bridge.webapp.converters;

import org.springframework.core.convert.converter.Converter;

public class BooleanToStringConverter implements Converter<Object,String> {

	public static BooleanToStringConverter INSTANCE = new BooleanToStringConverter();
	
	@Override
	public String convert(Object source) {
		// though obviously, just about anything has a toString()
		return ((Boolean)source).toString();
	}

}
