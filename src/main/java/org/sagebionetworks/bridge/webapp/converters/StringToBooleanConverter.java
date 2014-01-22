package org.sagebionetworks.bridge.webapp.converters;

import org.springframework.core.convert.converter.Converter;

public class StringToBooleanConverter implements Converter<String,Object> {

	public static StringToBooleanConverter INSTANCE = new StringToBooleanConverter();
	
	@Override
	public Object convert(String source) {
		if (source == null) {
			return null;
		}
		return Boolean.parseBoolean(source);
	}

}
