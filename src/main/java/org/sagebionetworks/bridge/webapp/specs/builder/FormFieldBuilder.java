package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.BooleanConverter;
import org.sagebionetworks.bridge.webapp.converter.BooleanToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToISODateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToLongFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DoubleConverter;
import org.sagebionetworks.bridge.webapp.converter.DoubleToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.FieldConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateTimeConverter;
import org.sagebionetworks.bridge.webapp.converter.LabConverter;
import org.sagebionetworks.bridge.webapp.converter.LabToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.LongConverter;
import org.sagebionetworks.bridge.webapp.converter.LongToStringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringToStringConverter;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;

public class FormFieldBuilder {

	protected static final String NULL_MESSAGE = "Must first call a creation method (asType) before building an instance";
	protected static final String NOT_NULL_MESSAGE = "Called a creation method before finishing an instance";
	
	protected FormField field;
	
	public FormField create() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		if (field.getUIType() == null) {
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
	
	public FormFieldBuilder asValue(FieldConverter<Map<String,String>, ParticipantDataValue> pdvConverter,
			FieldConverter<ParticipantDataValue, Map<String,String>> stringConverter) {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setExportable();
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
		field.setExportable();
		field.getDataColumn().setColumnType(ParticipantDataColumnType.STRING);
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
		field.setExportable();
		field.getDataColumn().setColumnType(ParticipantDataColumnType.STRING);
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
	
	public DoubleFormFieldBuilder asDouble() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		return new DoubleFormFieldBuilder(UIType.DECIMAL_INPUT, ParticipantDataColumnType.DOUBLE,
				DoubleToStringConverter.INSTANCE, DoubleConverter.INSTANCE);
	}
	
	public LongFormFieldBuilder asLong() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		return new LongFormFieldBuilder(UIType.INTEGER_INPUT, ParticipantDataColumnType.LONG,
				LongToStringConverter.INSTANCE, LongConverter.INSTANCE);
	}

	public FormFieldBuilder asBoolean() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setExportable();
		field.getDataColumn().setColumnType(ParticipantDataColumnType.BOOLEAN);
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
		field.setExportable();
		field.getDataColumn().setColumnType(ParticipantDataColumnType.DATETIME);
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
		field.setExportable();
		field.getDataColumn().setColumnType(ParticipantDataColumnType.DATETIME);
		field.setType(UIType.DATETIME);
		field.setStringConverter(DateToLongFormatDateStringConverter.INSTANCE);
		field.setParticipantDataValueConverter(ISODateTimeConverter.INSTANCE);
		return this;
	}
	
	public FormFieldBuilder asLab() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setExportable();
		field.getDataColumn().setColumnType(ParticipantDataColumnType.LAB);
		field.setType(UIType.LAB_ROW);
		field.setStringConverter(LabToStringConverter.INSTANCE);
		field.setParticipantDataValueConverter(LabConverter.INSTANCE);
		return this;
	}
	
	public FormFieldBuilder compoundField() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setIsCompoundField(true);
		return this;
	}
	
	public FormFieldBuilder type(String type) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.getDataColumn().setType(type);
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

	public FormFieldBuilder placeholder(String placeholderText) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setPlaceholderText(placeholderText);
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
	
	public FormFieldBuilder stringConverter(FieldConverter<ParticipantDataValue, Map<String,String>> converter) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setStringConverter(converter);
		return this;
	}
	
	public FormFieldBuilder dataConverter(FieldConverter<Map<String,String>, ParticipantDataValue> converter) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.setParticipantDataValueConverter(converter);
		return this;
	}
	
}
