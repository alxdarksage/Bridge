package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class LabConverter implements Converter<List<String>, ParticipantDataValue> {
	
	public static final LabConverter INSTANCE = new LabConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.size() != 4) {
			return null;
		}
		ParticipantDataLabValue pdv = new ParticipantDataLabValue();
		pdv.setEnteredValue(values.get(0));
		pdv.setUnits(values.get(1));
		pdv.setNormalizedMin(Double.parseDouble(values.get(2)));
		pdv.setNormalizedMax(Double.parseDouble(values.get(3)));
		// This has to be the value for now, conversions will be built into 
		// this converter, it's a converter, after all. Will get any information 
		// on this from the column data.
		pdv.setNormalizedValue(Double.parseDouble(values.get(0)));
		return pdv;
	}
	
}
