package org.sagebionetworks.bridge.webapp.converter;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class LabToStringConverter implements Converter<ParticipantDataValue, List<String>> {
	
	public static final LabToStringConverter INSTANCE = new LabToStringConverter();
	
	@Override
	public List<String> convert(ParticipantDataValue source) {
		ParticipantDataLabValue pdv = (ParticipantDataLabValue)source;
		List<String> values = Lists.newArrayListWithCapacity(5);
		values.add(pdv.getEnteredValue());
		values.add(pdv.getUnits());
		values.add(pdv.getNormalizedMin().toString());
		values.add(pdv.getNormalizedMax().toString());
		values.add(pdv.getNormalizedValue().toString());
		return values;
	}
}
