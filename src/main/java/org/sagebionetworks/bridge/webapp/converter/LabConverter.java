package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.sagebionetworks.bridge.model.data.units.Measure;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;

public class LabConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {
	
	public static final LabConverter INSTANCE = new LabConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null) {
			return null;
		}
		ParticipantDataLabValue pdv = new ParticipantDataLabValue();
		
		// If there's no unit string, then a lot of these conversions will just fail.
		// In fact we don't know the starting units and can't convert. In that case, 
		// right now, we're just persisting the value as entered so it isn't lost.
		
		String unitString = values.get(fieldName+ValueTranslator.LABRESULT_UNITS);
		pdv.setUnits(unitString);
		
		String valueString = values.get(fieldName + ValueTranslator.LABRESULT_ENTERED);
		pdv.setEnteredValue(valueString);
		Measure value = Measure.measureFromStrings(valueString, unitString);
		if (value != null) {
			pdv.setNormalizedValue(value.convertToNormalized().getAmount());
		} else if (StringUtils.isNotBlank(valueString)) {
			pdv.setNormalizedValue( Double.parseDouble(valueString) );
		}
		
		String minValue = values.get(fieldName + ValueTranslator.LABRESULT_NORMALIZED_MIN);
		Measure min = Measure.measureFromStrings(minValue, unitString);
		if (min != null) {
			pdv.setNormalizedMin(min.convertToNormalized().getAmount());
		} else if (StringUtils.isNotBlank(minValue)) {
			pdv.setNormalizedMin( Double.parseDouble(minValue) );
		}
		
		String maxValue = values.get(fieldName + ValueTranslator.LABRESULT_NORMALIZED_MAX);
		Measure max = Measure.measureFromStrings(maxValue, unitString);
		if (max != null) {
			pdv.setNormalizedMax(max.convertToNormalized().getAmount());
		} else if (StringUtils.isNotBlank(maxValue)) {
			pdv.setNormalizedMax( Double.parseDouble(maxValue) );
		}
		
		return pdv;
	}
	
}
