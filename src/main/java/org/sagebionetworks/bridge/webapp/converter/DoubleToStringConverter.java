package org.sagebionetworks.bridge.webapp.converter;

import java.text.DecimalFormat;
import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class DoubleToStringConverter implements Converter<ParticipantDataValue, List<String>> {

	public static final DoubleToStringConverter INSTANCE = new DoubleToStringConverter();

	@Override
	public List<String> convert(ParticipantDataValue source) {
		if (source == null) {
			return null;
		}
		Double d = ((ParticipantDataDoubleValue)source).getValue();
		return Lists.newArrayList(new DecimalFormat("0.###").format(d));
	}

}
