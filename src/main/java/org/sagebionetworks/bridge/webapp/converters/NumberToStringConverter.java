package org.sagebionetworks.bridge.webapp.converters;

import org.springframework.core.convert.converter.Converter;

public class NumberToStringConverter implements Converter<Object,String> {

	public static NumberToStringConverter INSTANCE = new NumberToStringConverter();
	
	@Override
	public String convert(Object source) {
		return source.toString();
	}

}
