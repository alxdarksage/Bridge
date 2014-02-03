package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class LabToStringConverter implements Converter<ParticipantDataValue, List<String>> {
	
	public static final LabToStringConverter INSTANCE = new LabToStringConverter();

	public DoubleToStringConverter doubleConverter = new DoubleToStringConverter();
	
	@Override
	public List<String> convert(ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		ParticipantDataLabValue pdv = (ParticipantDataLabValue)source;
		List<String> values = Lists.newArrayListWithCapacity(5);
		values.add(pdv.getEnteredValue());
		values.add(pdv.getUnits());
		if (pdv.getNormalizedMin() != null) {
			values.add( doubleConverter.format(pdv.getNormalizedMin()) );	
		} else {
			values.add(null);
		}
		if (pdv.getNormalizedMax() != null) {
			values.add( doubleConverter.format(pdv.getNormalizedMax()) );
		} else {
			values.add(null);
		}
		if (pdv.getNormalizedValue() != null) {
			values.add( doubleConverter.format(pdv.getNormalizedValue()) );	
		} else {
			values.add(null);
		}
		return values;
	}
}
