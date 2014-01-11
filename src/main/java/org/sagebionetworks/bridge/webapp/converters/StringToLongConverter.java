package org.sagebionetworks.bridge.webapp.converters;

import org.springframework.core.convert.converter.Converter;

public class StringToLongConverter implements Converter<String,Object> {
		
	public static final StringToLongConverter INSTANCE = new StringToLongConverter();
	
	@Override
	public Object convert(String string) {
		return Long.parseLong((String)string);
	}

}
