package org.sagebionetworks.bridge.webapp.converter;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class ISODateConverter implements Converter<List<String>, ParticipantDataValue> {

	public static final ISODateConverter INSTANCE = new ISODateConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		Date date = DateTime.parse(values.get(0), ISODateTimeFormat.date()).toDate();
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		pdv.setValue(date.getTime());
		return pdv;
	}

}
