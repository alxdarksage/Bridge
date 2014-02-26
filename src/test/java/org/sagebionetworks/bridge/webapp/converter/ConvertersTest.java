package org.sagebionetworks.bridge.webapp.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataBooleanValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;

import com.google.common.collect.Lists;

public class ConvertersTest {
	
	private static final long DATE_IN_MILLIS = 1391447941117L;
	
	private ParticipantDataValue makePDV(String value) {
		ParticipantDataStringValue pdv = new ParticipantDataStringValue();
		pdv.setValue(value);
		return pdv;
	}
	private ParticipantDataValue makePDV(Long value) {
		ParticipantDataLongValue pdv = new ParticipantDataLongValue();
		pdv.setValue(value);
		return pdv;
	}
	private ParticipantDataValue makePDV(Double value) {
		ParticipantDataDoubleValue pdv = new ParticipantDataDoubleValue();
		pdv.setValue(value);
		return pdv;
	}
	private ParticipantDataValue makePDV(Boolean value) {
		ParticipantDataBooleanValue pdv = new ParticipantDataBooleanValue();
		pdv.setValue(value);
		return pdv;
	}
	private ParticipantDataValue makePDV(Date value) {
		ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
		if (value != null) {
			pdv.setValue(value.getTime());	
		}
		return pdv;
	}
	private ParticipantDataValue makePDV(String enteredValue, String units, Double min, Double max, Double normValue) {
		ParticipantDataLabValue pdv = new ParticipantDataLabValue();
		pdv.setEnteredValue(enteredValue);
		pdv.setUnits(units);
		pdv.setNormalizedMin(min);
		pdv.setNormalizedMax(max);
		pdv.setNormalizedValue(normValue);
		return pdv;
	}

	@Test
	public void booleanConverter() {
		BooleanConverter converter = new BooleanConverter();
		
		ParticipantDataValue pdv = converter.convert(Lists.newArrayList("true"));
		assertEquals(Boolean.TRUE, ((ParticipantDataBooleanValue)pdv).getValue());
		
		pdv = converter.convert((List<String>)null);
		assertNull(pdv);
		
		pdv = converter.convert(Lists.<String>newArrayList());
		assertNull(pdv);
		
		pdv = converter.convert(Lists.newArrayList((String)null));
		assertNull(pdv);		
	}

	@Test
	public void booleanToStringConverter() {
		BooleanToStringConverter converter = new BooleanToStringConverter();
		
		List<String> result = converter.convert(makePDV(Boolean.FALSE));
		assertEquals("false", result.get(0));
		
		result = converter.convert(makePDV(Boolean.TRUE));
		assertEquals("true", result.get(0));
		
		result = converter.convert(null);
		assertNull(result);
		
		result = converter.convert(makePDV((Boolean)null));
		assertNull(result);		
	}
	
	@Test
	public void dateToISODateStringConverter() {
		DateToISODateStringConverter converter = new DateToISODateStringConverter();
		
		Date date = new Date(DATE_IN_MILLIS);
		List<String> result = converter.convert(makePDV(date));
		assertEquals("2014-02-03", result.get(0));
		
		result = converter.convert(null);
		assertNull(result);
		
		result = converter.convert(makePDV((Date)null));
		assertNull(result);
		
		result = converter.convert(makePDV(new Date(0L)));
		assertNull(result);
	}
	
	@Test
	public void dateToLongFormatDateStringConverter() {
		DateToLongFormatDateStringConverter converter = new DateToLongFormatDateStringConverter();
		
		Date date = new Date(1391447941117L);
		List<String> result = converter.convert(makePDV(date));
		assertEquals("February 03, 2014 (09:19 AM)", result.get(0));
		
		result = converter.convert(null);
		assertNull(result);
		
		result = converter.convert(makePDV((Date)null));
		assertNull(result);
		
		result = converter.convert(makePDV(new Date(0L)));
		assertNull(result);
	}
	
	@Test
	public void dateToShortFormatDateStringConverter() {
		DateToShortFormatDateStringConverter converter = new DateToShortFormatDateStringConverter();
		
		Date date = new Date(1391447941117L);
		List<String> result = converter.convert(makePDV(date));
		assertEquals("February 03, 2014", result.get(0));
		
		result = converter.convert(null);
		assertNull(result);
		
		result = converter.convert(makePDV((Date)null));
		assertNull(result);
		
		result = converter.convert(makePDV(new Date(0L)));
		assertNull(result);
	}
	
	@Test
	public void isoDateConverter() {
		List<String> values = Lists.newArrayList("2014-02-03");
		
		ISODateConverter converter = new ISODateConverter();
		ParticipantDataValue pdv = converter.convert(values);
		
		// Is truncated because time of day isn't represented
		DateToShortFormatDateStringConverter converter2 = new DateToShortFormatDateStringConverter();
		Date newDate = new Date(((ParticipantDataDatetimeValue)pdv).getValue().longValue());
		List<String> result = converter2.convert(makePDV(newDate));
		assertEquals("February 03, 2014", result.get(0));
		
		pdv = converter.convert((List<String>)null);
		assertNull(pdv);
		
		pdv = converter.convert(Lists.<String>newArrayList());
		assertNull(pdv);
		
		pdv = converter.convert(Lists.newArrayList((String)null));
		assertNull(pdv);
	}
	
	@Test
	public void isoDateTimeConverter() {
		// This needs to be marked as changed
		List<String> values = Lists.newArrayList("2014-02-03T09:19:00.000-06:00");
		
		ISODateTimeConverter converter = new ISODateTimeConverter();
		ParticipantDataValue pdv = converter.convert(values);
		
		// Is truncated because time of day isn't represented
		DateToLongFormatDateStringConverter converter2 = new DateToLongFormatDateStringConverter();
		Date newDate = new Date(((ParticipantDataDatetimeValue)pdv).getValue().longValue());
		List<String> result = converter2.convert(makePDV(newDate));
		assertEquals("February 03, 2014 (07:19 AM)", result.get(0));
		
		pdv = converter.convert((List<String>)null);
		assertNull(pdv);
		
		pdv = converter.convert(Lists.<String>newArrayList());
		assertNull(pdv);
		
		pdv = converter.convert(Lists.newArrayList((String)null));
		assertNull(pdv);		
	}
	
	@Test
	public void doubleConverter() {
		DoubleConverter converter = new DoubleConverter();
		
		ParticipantDataValue pdv = converter.convert(Lists.newArrayList(".3"));
		assertEquals(new Double(.3), ((ParticipantDataDoubleValue)pdv).getValue());
		
		pdv = converter.convert((List<String>)null);
		assertNull(pdv);
		
		pdv = converter.convert(Lists.<String>newArrayList());
		assertNull(pdv);
		
		pdv = converter.convert(Lists.newArrayList((String)null));
		assertNull(pdv);		
	}
	
	@Test
	public void doubleToStringConverter() {
		DoubleToStringConverter converter = new DoubleToStringConverter();
		
		List<String> result = converter.convert(makePDV(0.3));
		assertEquals("0.3", result.get(0));
		
		result = converter.convert(makePDV(3.0));
		assertEquals("3", result.get(0));
		
		result = converter.convert(null);
		assertNull(result);
		
		result = converter.convert(makePDV((Double)null));
		assertNull(result);
	}
	
	@Test
	public void labConverter() {
		LabConverter converter = new LabConverter();
		
		List<String> values = Lists.newArrayList("2.3", "K/ul", "0", "100.0", "2.3");
		ParticipantDataLabValue pdv = (ParticipantDataLabValue)converter.convert(values);
		
		assertEquals("2.3", pdv.getEnteredValue());
		assertEquals("K/ul", pdv.getUnits());
		assertEquals(new Double(0.0), pdv.getNormalizedMin());
		assertEquals(new Double(100.0), pdv.getNormalizedMax());
		assertEquals(new Double(2.3), pdv.getNormalizedValue());
		
		ParticipantDataValue pdv2 = converter.convert((List<String>)null);
		assertNull(pdv2);
		
		pdv2 = converter.convert(Lists.<String>newArrayList());
		assertNull(pdv2);
		
		pdv2 = converter.convert(Lists.newArrayList((String)null));
		assertNull(pdv2);		
	}
	
	@Test
	public void labToStringConverter() {
		LabToStringConverter converter = new LabToStringConverter();
		
		List<String> result = converter.convert(makePDV("2.3", "K/ul", 0.0, 100.0, 2.3));
		assertEquals("2.3", result.get(0));
		assertEquals("K/ul", result.get(1));
		assertEquals("0", result.get(2));
		assertEquals("100", result.get(3));
		assertEquals("2.3", result.get(4));
		
		// They can all be nulls, that's okay.
		result = converter.convert(makePDV(null, null, null, null, null));
		assertNull(result.get(0));
		assertNull(result.get(1));
		assertNull(result.get(2));
		assertNull(result.get(3));
		assertNull(result.get(4));		
		
		result = converter.convert(null);
		assertNull(result);
	}
	
	@Test
	public void longConverter() {
		LongConverter converter = new LongConverter();
		
		ParticipantDataValue pdv = converter.convert(Lists.newArrayList("3"));
		assertEquals(new Long(3), ((ParticipantDataLongValue)pdv).getValue());
		
		pdv = converter.convert((List<String>)null);
		assertNull(pdv);
		
		pdv = converter.convert(Lists.<String>newArrayList());
		assertNull(pdv);
		
		pdv = converter.convert(Lists.newArrayList((String)null));
		assertNull(pdv);
	}
	
	@Test
	public void longToStringConverter() {
		LongToStringConverter converter = new LongToStringConverter();
		
		List<String> result = converter.convert(makePDV(3L));
		assertEquals("3", result.get(0));
		
		result = converter.convert(null);
		assertNull(result);
		
		result = converter.convert(makePDV((Long)null));
		assertNull(result);
	}

	@Test
	public void stringConverter() {
		// This is an identity converter.
		StringConverter converter = new StringConverter();
		ParticipantDataValue pdv = converter.convert(Collections.singletonList("Ramada"));
		assertEquals("Ramada", ((ParticipantDataStringValue)pdv).getValue());
		
		pdv = converter.convert(new ArrayList<String>());
		assertNull(pdv);
		
		pdv = converter.convert(null);
		assertNull(pdv);
		
		pdv = converter.convert(Collections.singletonList((String)null));
		assertNull(pdv);
	}
	
	@Test
	public void stringToStringConverter() {
		// This is an identity converter.
		StringToStringConverter converter = new StringToStringConverter();
		List<String> result = converter.convert(makePDV("Ramada"));
		assertEquals("Ramada", result.get(0));
		
		result = converter.convert(makePDV((String)null));
		assertNull(result);
	}
	
}
