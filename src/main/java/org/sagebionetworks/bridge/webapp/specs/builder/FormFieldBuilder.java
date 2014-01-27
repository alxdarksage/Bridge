package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.BooleanConverter;
import org.sagebionetworks.bridge.webapp.converter.BooleanToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToLongFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DoubleConverter;
import org.sagebionetworks.bridge.webapp.converter.DoubleToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToISODateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateTimeConverter;
import org.sagebionetworks.bridge.webapp.converter.LongConverter;
import org.sagebionetworks.bridge.webapp.converter.LongToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringToStringConverter;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.springframework.core.convert.converter.Converter;

public class FormFieldBuilder {

	protected static final String NULL_MESSAGE = "Must first call a creation method (asType) before building an instance";
	protected static final String NOT_NULL_MESSAGE = "Called a creation method before finishing an instance";
	
	protected FormField field;
	
	public FormField create() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		if (field.getType() == null) {
			throw new IllegalArgumentException("Must set a field type before calling create()");
		}
		if (field.getName() == null) {
			throw new IllegalArgumentException("Must set a field name before calling create()");
		}
		if (field.getLabel() == null) {
			throw new IllegalArgumentException("Must set a field label before calling create()");
		}
		if (field.getParticipantDataValueConverter() == null) {
			throw new IllegalArgumentException("Must set a participant data value converter");
		}
		if (field.getStringConverter() == null) {
			throw new IllegalArgumentException("Must set a PDV to string converter");
		}
		FormField temp = field;
		field = null;
		return temp;
	}
	
	public FormFieldBuilder asValue() {
		return asValue(StringConverter.INSTANCE, StringToStringConverter.INSTANCE);
	}
	
	public FormFieldBuilder asValue(Converter<List<String>, ParticipantDataValue> pdvConverter,
			Converter<ParticipantDataValue, List<String>> stringConverter) {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.VALUE);
		field.setStringConverter(stringConverter);
		field.setParticipantDataValueConverter(pdvConverter);
		return this;
	}
	
	public FormFieldBuilder asText() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.TEXT_INPUT);
		field.setParticipantDataValueConverter(StringConverter.INSTANCE);
		field.setStringConverter(StringToStringConverter.INSTANCE);
		return this;
	}
	
	public FormFieldBuilder asText(String initialValue) {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.TEXT_INPUT);
		field.setInitialValue(initialValue);
		field.setParticipantDataValueConverter(StringConverter.INSTANCE);
		field.setStringConverter(StringToStringConverter.INSTANCE);
		return this;
	}
	
	public EnumeratedFormFieldBuilder asEnum(List<String> enumeratedValues) {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		return new EnumeratedFormFieldBuilder(enumeratedValues);
	}
	
	public NumericFormFieldBuilder asDouble() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		return new NumericFormFieldBuilder(UIType.DECIMAL_INPUT, DoubleToStringConverter.INSTANCE, DoubleConverter.INSTANCE);
	}
	
	public FormFieldBuilder asLong() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		return new NumericFormFieldBuilder(UIType.DECIMAL_INPUT, LongToStringConverter.INSTANCE, LongConverter.INSTANCE);
	}

	public FormFieldBuilder asBoolean() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.CHECKBOX);
		field.setStringConverter(BooleanToStringConverter.INSTANCE);
		field.setParticipantDataValueConverter(BooleanConverter.INSTANCE);
		return this;
	}
	
	public FormFieldBuilder asDate() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.DATE);
		field.setStringConverter(DateToISODateStringConverter.INSTANCE);
		field.setParticipantDataValueConverter(ISODateConverter.INSTANCE);
		return this;
	}
	
	public FormFieldBuilder asDateTime() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.DATETIME);
		field.setStringConverter(DateToLongFormatDateStringConverter.INSTANCE);
		field.setParticipantDataValueConverter(ISODateTimeConverter.INSTANCE);
		return this;
	}
	
	public FormFieldBuilder name(String name) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setName(name);
		return this;
	}

	public FormFieldBuilder label(String label) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setLabel(label);
		return this;
	}

	public FormFieldBuilder readonly() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setReadonly();
		return this;
	}

	public FormFieldBuilder required() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setRequired();
		return this;
	}
	
	public FormFieldBuilder defaultable() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setDefaultable();
		return this;
	}	
	
}
