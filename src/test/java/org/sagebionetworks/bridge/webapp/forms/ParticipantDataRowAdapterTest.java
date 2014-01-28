package org.sagebionetworks.bridge.webapp.forms;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.DateToShortFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;

import com.google.common.collect.Maps;

public class ParticipantDataRowAdapterTest {

	@Test
	public void correctlyFormatsValue() {
		FormField field = new FormFieldBuilder().asDateTime().name("collected_on").label("").create();
		field.setStringConverter(DateToShortFormatDateStringConverter.INSTANCE);
		
		Date date = DateTime.parse("2013-07-02", ISODateTimeFormat.date()).toDate();
		
		Map<String, ParticipantDataValue> values = Maps.newHashMap();
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		pdv.setValue(date.getTime());
		values.put("collected_on", pdv);
		ParticipantDataRow row = new ParticipantDataRow();
		row.setData(values);
		ParticipantDataRowAdapter adapter = new ParticipantDataRowAdapter(field, row);
		
		String formattedValue = adapter.getValuesMap().get("collected_on");
		assertEquals("July 02, 2013", formattedValue);
	}

}
