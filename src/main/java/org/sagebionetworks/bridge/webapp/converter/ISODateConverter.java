package org.sagebionetworks.bridge.webapp.converter;

import java.util.Date;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

public class ISODateConverter implements FieldConverter<Map<String,String>, ParticipantDataValue> {

	public static final ISODateConverter INSTANCE = new ISODateConverter();
	
	@Override
	public ParticipantDataValue convert(String fieldName, Map<String,String> values) {
		if (values == null || values.isEmpty() || values.get(fieldName) == null) {
			return null;
		}
		Date date = DateTime.parse(values.get(fieldName), ISODateTimeFormat.date()).toDate();
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		pdv.setValue(date.getTime());
		return pdv;
	}

}
