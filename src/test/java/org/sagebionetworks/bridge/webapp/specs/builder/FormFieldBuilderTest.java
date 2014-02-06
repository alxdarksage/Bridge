package org.sagebionetworks.bridge.webapp.specs.builder;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.converter.BooleanConverter;
import org.sagebionetworks.bridge.webapp.converter.BooleanToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToISODateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToLongFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToShortFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DoubleConverter;
import org.sagebionetworks.bridge.webapp.converter.DoubleToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateTimeConverter;
import org.sagebionetworks.bridge.webapp.converter.LongConverter;
import org.sagebionetworks.bridge.webapp.converter.LongToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringToStringConverter;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.DoubleFormField;
import org.sagebionetworks.bridge.webapp.specs.LongFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;

import com.google.common.collect.Lists;

public class FormFieldBuilderTest {

	private FormFieldBuilder builder;
	
	@Before
	public void createBuilder() {
		builder = new FormFieldBuilder();
	}
	
	private FormFieldBuilder setDefaults(FormFieldBuilder builder) {
		return builder.name("Foo").label("foo");
	}
	
	@Test
	public void canCreateValue() {
		FormField field = setDefaults(builder.asValue()).create();
		assertEquals(UIType.VALUE, field.getUIType());
		assertEquals(StringToStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(StringConverter.INSTANCE, field.getParticipantDataValueConverter());
		
		field = setDefaults(builder.asValue(ISODateConverter.INSTANCE, DateToShortFormatDateStringConverter.INSTANCE)).create();
		assertEquals(DateToShortFormatDateStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(ISODateConverter.INSTANCE, field.getParticipantDataValueConverter());
	}
	
	@Test
	public void canCreateText() {
		FormField field = setDefaults(builder.asText()).create();
		assertEquals(UIType.TEXT_INPUT, field.getUIType());
		assertEquals(StringToStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(StringConverter.INSTANCE, field.getParticipantDataValueConverter());
		
		field = setDefaults(builder.asText("Default Text")).create();
		assertEquals("Default Text", field.getInitialValue());
	}
	
	@Test
	public void canCreateEnum() {
		List<String> enums = Lists.newArrayList("a", "b", "c");
		FormField field = setDefaults(builder.asEnum(enums)).create();
		
		// Could certainly be something different than this... like a set of checkboxes.
		// Just expand the as*() methods
		assertEquals(UIType.SINGLE_SELECT, field.getUIType());
		assertEquals(StringToStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(StringConverter.INSTANCE, field.getParticipantDataValueConverter());
		assertEquals(enums, ((EnumeratedFormField)field).getEnumeratedValues());
	}
	
	@Test
	public void canCreateDouble() {
		FormField field = setDefaults(builder.asDouble().minValue(2.0).maxValue(4.0)).create();
		
		assertEquals(UIType.DECIMAL_INPUT, field.getUIType());
		assertEquals(DoubleToStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(DoubleConverter.INSTANCE, field.getParticipantDataValueConverter());
		assertEquals(new Double(2.0), ((DoubleFormField)field).getMinValue());
		assertEquals(new Double(4.0), ((DoubleFormField)field).getMaxValue());
	}
	
	@Test
	public void canCreateLong() {
		FormField field = setDefaults(builder.asLong().minValue(2L).maxValue(4L)).create();
		
		assertEquals(UIType.INTEGER_INPUT, field.getUIType());
		assertEquals(LongToStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(LongConverter.INSTANCE, field.getParticipantDataValueConverter());
		assertEquals(new Long(2), ((LongFormField)field).getMinValue());
		assertEquals(new Long(4), ((LongFormField)field).getMaxValue());		
	}

	@Test
	public void canCreateBoolean() {
		FormField field = setDefaults(builder.asBoolean()).create();
		assertEquals(UIType.CHECKBOX, field.getUIType());
		assertEquals(BooleanToStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(BooleanConverter.INSTANCE, field.getParticipantDataValueConverter());
	}
	
	@Test
	public void canCreateDate() {
		FormField field = setDefaults(builder.asDate()).create();
		
		// These converters are possibly the least useful.
		assertEquals(UIType.DATE, field.getUIType());
		assertEquals(DateToISODateStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(ISODateConverter.INSTANCE, field.getParticipantDataValueConverter());
	}
	
	@Test
	public void canCreateDateTime() {
		FormField field = setDefaults(builder.asDateTime()).create();
		
		// These converters are not even correct?
		assertEquals(UIType.DATETIME, field.getUIType());
		assertEquals(DateToLongFormatDateStringConverter.INSTANCE, field.getStringConverter());
		assertEquals(ISODateTimeConverter.INSTANCE, field.getParticipantDataValueConverter());
	}
	
	@Test
	public void canSetType() {
		FormField field = setDefaults(builder.asText()).type("Foo").create();
		assertEquals("Foo", field.getDataColumn().getType());
	}
	
	@Test
	public void canSetNameAndLabel() {
		FormField field = setDefaults(builder.asText()).type("Foo").create();
		assertEquals("Foo", field.getName());
		assertEquals("foo", field.getLabel());
	}

	@Test
	public void canSetPlaceholder() {
		FormField field = setDefaults(builder.asText()).type("Foo").placeholder("Bar").create();
		assertEquals("Bar", field.getPlaceholderText());
	}
	
	@Test
	public void canSetReadonly() {
		FormField field = setDefaults(builder.asText()).create();
		assertFalse(field.isReadonly());
		
		field = setDefaults(builder.asText()).readonly().create();
		assertEquals(true, field.isReadonly());
	}
	
	@Test
	public void canSetRequired() {
		FormField field = setDefaults(builder.asText()).create();
		assertFalse(field.isRequired());
		
		field = setDefaults(builder.asText()).required().create();
		assertEquals(true, field.isRequired());
	}
	
	@Test
	public void canSetDefaultable() {
		FormField field = setDefaults(builder.asText()).create();
		assertFalse(field.isDefaultable());
		
		field = setDefaults(builder.asText()).defaultable().create();
		assertEquals(true, field.isDefaultable());
	}
}
