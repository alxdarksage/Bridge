package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class LabConverter implements Converter<List<String>, ParticipantDataValue> {
	
	public static final LabConverter INSTANCE = new LabConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.size() != 5) {
			return null;
		}
		ParticipantDataLabValue pdv = new ParticipantDataLabValue();
		pdv.setEnteredValue(values.get(0));
		pdv.setUnits(values.get(1));
		pdv.setNormalizedMin(Double.parseDouble(values.get(2)));
		pdv.setNormalizedMax(Double.parseDouble(values.get(3)));
		pdv.setNormalizedValue(Double.parseDouble(values.get(4)));
		return pdv;
	}
	
}
