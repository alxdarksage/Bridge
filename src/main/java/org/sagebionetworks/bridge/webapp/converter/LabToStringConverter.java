package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;

import com.google.common.collect.Maps;

public class LabToStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {
	
	public static final LabToStringConverter INSTANCE = new LabToStringConverter();

	public DoubleToStringConverter doubleConverter = new DoubleToStringConverter();
	
	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		ParticipantDataLabValue pdv = (ParticipantDataLabValue)source;
		
		Map<String,String> map = Maps.newHashMap();
		if (pdv.getEnteredValue() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_ENTERED, pdv.getEnteredValue());	
		}
		if (pdv.getUnits() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_UNITS, pdv.getUnits());	
		}
		if (pdv.getNormalizedMin() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_NORMALIZED_MIN, doubleConverter.format(pdv.getNormalizedMin()));	
		}
		if (pdv.getNormalizedMax() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_NORMALIZED_MAX, doubleConverter.format(pdv.getNormalizedMax()));
		}
		if (pdv.getNormalizedValue() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_NORMALIZED_VALUE, doubleConverter.format(pdv.getNormalizedValue()));	
		}
		return map;
	}
}
