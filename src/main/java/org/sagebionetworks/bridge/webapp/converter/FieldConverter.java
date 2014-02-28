package org.sagebionetworks.bridge.webapp.converter;

public interface FieldConverter<S, T> {
	T convert(String fieldName, S source);
}
