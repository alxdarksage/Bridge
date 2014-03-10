package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
		
		String unitString = values.get(fieldName+ValueTranslator.LABRESULT_UNITS);
		pdv.setUnits(unitString);
		
		String value = values.get(fieldName + ValueTranslator.LABRESULT_VALUE);
		if (StringUtils.isNotBlank(value)) {
			pdv.setValue( Double.parseDouble(value) );
		}
		
		String minValue = values.get(fieldName + ValueTranslator.LABRESULT_MIN_NORMAL_VALUE);
		if (StringUtils.isNotBlank(minValue)) {
			pdv.setMinNormal( Double.parseDouble(minValue) );
		}
		
		String maxValue = values.get(fieldName + ValueTranslator.LABRESULT_MAX_NORMAL_VALUE);
		if (StringUtils.isNotBlank(maxValue)) {
			pdv.setMaxNormal( Double.parseDouble(maxValue) );
		}
		
		return pdv;
	}
	
}
