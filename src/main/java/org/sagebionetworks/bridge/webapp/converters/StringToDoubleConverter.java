package org.sagebionetworks.bridge.webapp.converters;

import org.springframework.core.convert.converter.Converter;

public class StringToDoubleConverter implements Converter<String,Object> {
	
	public static final StringToDoubleConverter INSTANCE = new StringToDoubleConverter();
	
	@Override
	public Object convert(String string) {
		if (string == null) {
			return null;
		}
		return Double.parseDouble((String)string);
	}
}
