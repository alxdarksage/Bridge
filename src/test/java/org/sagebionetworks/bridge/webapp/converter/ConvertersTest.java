package org.sagebionetworks.bridge.webapp.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataBooleanValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLabValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;

import com.google.common.collect.Maps;

public class ConvertersTest {
	
	private static final String TEST = "test";
	private static final long DATE_IN_MILLIS = 1391447941117L;
	
	private Map<String,String> getMap(String fieldName, String value) {
		Map<String,String> map = Maps.newHashMap();
		map.put(fieldName, value);
		return map;
	}
	
	private Map<String,String> getMap(String fieldName) {
		return Maps.newHashMap();
	}
	
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
	private ParticipantDataValue makePDV(Double value, String units, Double min, Double max, Double normValue) {
		ParticipantDataLabValue pdv = new ParticipantDataLabValue();
		pdv.setValue(value);
		pdv.setUnits(units);
		pdv.setMinNormal(min);
		pdv.setMaxNormal(max);
		return pdv;
	}

	@Test
	public void booleanConverter() {
		BooleanConverter converter = new BooleanConverter();
		
		ParticipantDataValue pdv = converter.convert(TEST, getMap(TEST, "true"));
		assertEquals(Boolean.TRUE, ((ParticipantDataBooleanValue)pdv).getValue());
		
		pdv = converter.convert(TEST, null);
		assertNull(pdv);
		
		pdv = converter.convert(TEST, new HashMap<String,String>());
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST, null));
		assertNull(pdv);		
	}

	@Test
	public void booleanToStringConverter() {
		BooleanToStringConverter converter = new BooleanToStringConverter();
		
		Map<String,String> result = converter.convert(TEST, makePDV(Boolean.FALSE));
		assertEquals("false", result.get(TEST));
		
		result = converter.convert(TEST, makePDV(Boolean.TRUE));
		assertEquals("true", result.get(TEST));
		
		result = converter.convert(TEST, null);
		assertNull(result);
		
		result = converter.convert(TEST, makePDV((Boolean)null));
		assertNull(result);		
	}
	
	@Test
	public void dateToISODateStringConverter() {
		DateToISODateStringConverter converter = new DateToISODateStringConverter();
		
		Date date = new Date(DATE_IN_MILLIS);
		Map<String,String> result = converter.convert(TEST, makePDV(date));
		assertEquals("2014-02-03", result.get(TEST));
		
		result = converter.convert(TEST, null);
		assertNull(result);
		
		result = converter.convert(TEST, makePDV((Date)null));
		assertNull(result);
		
		result = converter.convert(TEST, makePDV(new Date(0L)));
		assertNull(result);
	}
	
	@Test
	public void dateToLongFormatDateStringConverter() {
		DateToLongFormatDateStringConverter converter = new DateToLongFormatDateStringConverter();
		
		Date date = new Date(1391447941117L);
		Map<String,String> result = converter.convert(TEST, makePDV(date));
		assertEquals("February 03, 2014 (09:19 AM)", result.get(TEST));
		
		result = converter.convert(TEST, null);
		assertNull(result);
		
		result = converter.convert(TEST, makePDV((Date)null));
		assertNull(result);
		
		result = converter.convert(TEST, makePDV(new Date(0L)));
		assertNull(result);
	}
	
	@Test
	public void dateToShortFormatDateStringConverter() {
		DateToShortFormatDateStringConverter converter = new DateToShortFormatDateStringConverter();
		
		Date date = new Date(1391447941117L);
		Map<String,String> result = converter.convert(TEST, makePDV(date));
		assertEquals("February 03, 2014", result.get(TEST));
		
		result = converter.convert(TEST, null);
		assertNull(result);
		
		result = converter.convert(TEST, makePDV((Date)null));
		assertNull(result);
		
		result = converter.convert(TEST, makePDV(new Date(0L)));
		assertNull(result);
	}
	
	@Test
	public void isoDateConverter() {
		
		Map<String,String> values = getMap(TEST, "2014-02-03");
		
		ISODateConverter converter = new ISODateConverter();
		ParticipantDataValue pdv = converter.convert(TEST, values);
		
		// Is truncated because time of day isn't represented
		DateToShortFormatDateStringConverter converter2 = new DateToShortFormatDateStringConverter();
		Date newDate = new Date(((ParticipantDataDatetimeValue)pdv).getValue().longValue());
		Map<String,String> result = converter2.convert(TEST, makePDV(newDate));
		assertEquals("February 03, 2014", result.get(TEST));
		
		pdv = converter.convert(TEST, getMap(TEST, null));
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST));
		assertNull(pdv);
		
		pdv = converter.convert(TEST, null);
		assertNull(pdv);
	}
	
	@Test
	public void isoDateTimeConverter() {
		// This needs to be marked as changed
		Map<String,String> values = getMap(TEST, "2014-02-03T09:19:00.000-06:00");
		
		ISODateTimeConverter converter = new ISODateTimeConverter();
		ParticipantDataValue pdv = converter.convert(TEST, values);
		
		// Is truncated because time of day isn't represented
		DateToLongFormatDateStringConverter converter2 = new DateToLongFormatDateStringConverter();
		Date newDate = new Date(((ParticipantDataDatetimeValue)pdv).getValue().longValue());
		Map<String,String> result = converter2.convert(TEST, makePDV(newDate));
		assertEquals("February 03, 2014 (07:19 AM)", result.get(TEST));
		
		pdv = converter.convert(TEST, null);
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST));
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST, null));
		assertNull(pdv);		
	}
	
	@Test
	public void doubleConverter() {
		DoubleConverter converter = new DoubleConverter();
		
		ParticipantDataValue pdv = converter.convert(TEST, getMap(TEST, ".3"));
		assertEquals(new Double(.3), ((ParticipantDataDoubleValue)pdv).getValue());
		
		pdv = converter.convert(TEST, null);
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST));
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST, null));
		assertNull(pdv);		
	}
	
	@Test
	public void doubleToStringConverter() {
		DoubleToStringConverter converter = new DoubleToStringConverter();
		
		Map<String,String> result = converter.convert(TEST, makePDV(0.3));
		assertEquals("0.3", result.get(TEST));
		
		result = converter.convert(TEST, makePDV(3.0));
		assertEquals("3", result.get(TEST));
		
		result = converter.convert(TEST, null);
		assertNull(result);
		
		result = converter.convert(TEST, makePDV((Double)null));
		assertNull(result);
	}
	
	@Test
	public void labConverter() {
		LabConverter converter = new LabConverter();
		
		Map<String,String> map = Maps.newHashMap();
		map.put(TEST+ValueTranslator.LABRESULT_VALUE, "2.3");
		map.put(TEST+ValueTranslator.LABRESULT_UNITS, "K/ul");
		map.put(TEST+ValueTranslator.LABRESULT_MIN_NORMAL_VALUE, "0");
		map.put(TEST+ValueTranslator.LABRESULT_MAX_NORMAL_VALUE, "100.0");
		
		ParticipantDataLabValue pdv = (ParticipantDataLabValue)converter.convert(TEST, map);
		
		assertEquals(2.3, pdv.getValue(), 0.0);
		assertEquals("K/ul", pdv.getUnits());
		assertEquals(new Double(0.0), pdv.getMinNormal());
		assertEquals(new Double(100.0), pdv.getMaxNormal());
	}
	
	@Test
	public void labToStringConverter() {
		LabToStringConverter converter = new LabToStringConverter();

		Map<String,String> result = converter.convert(TEST, makePDV(2.3, "K/ul", 0.0, 100.0, 2.3));
		assertEquals("2.3", result.get(TEST+ValueTranslator.LABRESULT_VALUE));
		assertEquals("K/ul", result.get(TEST+ValueTranslator.LABRESULT_UNITS));
		assertEquals("0", result.get(TEST+ValueTranslator.LABRESULT_MIN_NORMAL_VALUE));
		assertEquals("100", result.get(TEST+ValueTranslator.LABRESULT_MAX_NORMAL_VALUE));
		
		// They can all be nulls, that's okay.
		result = converter.convert(TEST, makePDV(null, null, null, null, null));
		assertNull(result.get(TEST+ValueTranslator.LABRESULT_VALUE));
		assertNull(result.get(TEST+ValueTranslator.LABRESULT_UNITS));
		assertNull(result.get(TEST+ValueTranslator.LABRESULT_MIN_NORMAL_VALUE));
		assertNull(result.get(TEST+ValueTranslator.LABRESULT_MAX_NORMAL_VALUE));
		
		result = converter.convert(TEST, null);
		assertNull(result);
	}
	
	@Test
	public void longConverter() {
		LongConverter converter = new LongConverter();
		
		ParticipantDataValue pdv = converter.convert(TEST, getMap(TEST, "3"));
		assertEquals(new Long(3), ((ParticipantDataLongValue)pdv).getValue());
		
		pdv = converter.convert(TEST, null);
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST));
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST, null));
		assertNull(pdv);
	}
	
	@Test
	public void longToStringConverter() {
		LongToStringConverter converter = new LongToStringConverter();
		
		Map<String,String> result = converter.convert(TEST, makePDV(3L));
		assertEquals("3", result.get(TEST));
		
		result = converter.convert(TEST, null);
		assertNull(result);
		
		result = converter.convert(TEST, makePDV((Long)null));
		assertNull(result);
	}

	@Test
	public void stringConverter() {
		// This is an identity converter.
		StringConverter converter = new StringConverter();
		ParticipantDataValue pdv = converter.convert(TEST, getMap(TEST, "Ramada"));
		assertEquals("Ramada", ((ParticipantDataStringValue)pdv).getValue());
		
		pdv = converter.convert(TEST, null);
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST));
		assertNull(pdv);
		
		pdv = converter.convert(TEST, getMap(TEST, null));
		assertNull(pdv);
	}
	
	@Test
	public void stringToStringConverter() {
		// This is an identity converter.
		StringToStringConverter converter = new StringToStringConverter();
		Map<String,String> result = converter.convert(TEST, makePDV("Ramada"));
		assertEquals("Ramada", result.get(TEST));
		
		result = converter.convert(TEST, makePDV((String)null));
		assertNull(result);
	}
	
}
