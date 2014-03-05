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
		if (pdv.getEnteredValue() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_ENTERED, pdv.getEnteredValue());	
		}
		if (pdv.getUnits() != null) {
			map.put(fieldName+ValueTranslator.LABRESULT_UNITS, pdv.getUnits());	
		}
		Units unit = Units.unitFromString(pdv.getUnits());
		if (pdv.getNormalizedMin() != null) {
			addToMap(map, fieldName + ValueTranslator.LABRESULT_NORMALIZED_MIN, pdv.getNormalizedMin(), unit);
		}
		if (pdv.getNormalizedMax() != null) {
			addToMap(map, fieldName + ValueTranslator.LABRESULT_NORMALIZED_MAX, pdv.getNormalizedMax(), unit);
		}
		if (pdv.getNormalizedValue() != null) {
			map.put(fieldName + ValueTranslator.LABRESULT_NORMALIZED_VALUE,
					DoubleToStringConverter.INSTANCE.format(pdv.getNormalizedValue()));
		}
		return map;
	}
	
	private void addToMap(Map<String,String> map, String fieldName, double amount, Units unit) {
		if (unit != null) {
			map.put(fieldName, DoubleToStringConverter.INSTANCE.format(denormalize(amount, unit)));
		} else {
			map.put(fieldName, DoubleToStringConverter.INSTANCE.format(amount));
		}
	}
	
	private double denormalize(double amount, Units unit) {
		return unit.convertFromNormalized(amount).getAmount();
	}
}
