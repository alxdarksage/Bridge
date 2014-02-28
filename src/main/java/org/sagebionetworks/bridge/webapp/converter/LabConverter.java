package org.sagebionetworks.bridge.webapp.converter;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;

public class LabConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {
	
	private static final Logger logger = LogManager.getLogger(LabConverter.class.getName());

	public static final LabConverter INSTANCE = new LabConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null) {
			return null;
		}
		ParticipantDataLabValue pdv = new ParticipantDataLabValue();
		pdv.setEnteredValue(values.get(fieldName+ValueTranslator.LABRESULT_ENTERED));
		pdv.setUnits(values.get(fieldName+ValueTranslator.LABRESULT_UNITS));
		String v = values.get(fieldName+ValueTranslator.LABRESULT_NORMALIZED_MIN);
		if (StringUtils.isNotEmpty(v)) {
			pdv.setNormalizedMin(Double.parseDouble(v));	
		}
		v = values.get(fieldName+ValueTranslator.LABRESULT_NORMALIZED_MAX);
		if (StringUtils.isNotEmpty(v)) {
			pdv.setNormalizedMax(Double.parseDouble(v));
		}
		v = values.get(fieldName+ValueTranslator.LABRESULT_NORMALIZED_VALUE);
		if (StringUtils.isNotEmpty(v)) {
			pdv.setNormalizedValue(Double.parseDouble(v));	
		}
		return pdv;
	}
	
	
}
