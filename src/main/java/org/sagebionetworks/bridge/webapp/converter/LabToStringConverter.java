package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.units.Units;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;

import com.google.common.collect.Maps;

public class LabToStringConverter implements FieldConverter<ParticipantDataValue, Map<String,String>> {
	
	public static final LabToStringConverter INSTANCE = new LabToStringConverter();

	@Override
	public Map<String,String> convert(String fieldName, ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		ParticipantDataLabValue pdv = (ParticipantDataLabValue)source;
		Map<String,String> map = Maps.newHashMap();
		if (pdv.getValue() != null) {
			map.put(fieldName + ValueTranslator.LABRESULT_VALUE,
					DoubleToStringConverter.INSTANCE.format(pdv.getValue()));
		}
		if (pdv.getUnits() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_UNITS, pdv.getUnits());	
		}
		if (pdv.getMinNormal() != null) {
			addToMap(map, fieldName + ValueTranslator.LABRESULT_MIN_NORMAL_VALUE, pdv.getMinNormal());
		}
		if (pdv.getMaxNormal() != null) {
			addToMap(map, fieldName + ValueTranslator.LABRESULT_MAX_NORMAL_VALUE, pdv.getMaxNormal());
		}
		return map;
	}
	
	private void addToMap(Map<String,String> map, String fieldName, double amount) {
		map.put(fieldName, DoubleToStringConverter.INSTANCE.format(amount));
	}
	
}
