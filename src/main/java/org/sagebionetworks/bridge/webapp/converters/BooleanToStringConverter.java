package org.sagebionetworks.bridge.webapp.converters;

import org.springframework.core.convert.converter.Converter;

public class BooleanToStringConverter implements Converter<Object,String> {

	public static BooleanToStringConverter INSTANCE = new BooleanToStringConverter();
	
	@Override
	public String convert(Object source) {
		if (source == null) {
			return null;
		}
		return ((Boolean)source).toString();
	}

}
