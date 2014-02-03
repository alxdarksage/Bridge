package org.sagebionetworks.bridge.webapp.converter;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class ISODateTimeConverter implements Converter<List<String>, ParticipantDataValue>{

	public static final ISODateTimeConverter INSTANCE = new ISODateTimeConverter();
	
	@Override
	public ParticipantDataValue convert(List<String> values) {
		if (values == null || values.isEmpty() || values.get(0) == null) {
			return null;
		}
		// This changed, but hasn't updated, not sure what happened. This 
		// still isn't marked as changed.
        Date date = DateTime.parse(values.get(0), ISODateTimeFormat.dateTime()).toDate(); 
        ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
        pdv.setValue(date.getTime());
        return pdv;
	}
	
}
