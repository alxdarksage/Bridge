package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.NumericFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.springframework.core.convert.converter.Converter;

public class NumericFormFieldBuilder extends FormFieldBuilder {

	public NumericFormFieldBuilder(UIType type, ParticipantDataColumnType columnType, Converter<ParticipantDataValue, List<String>> stringConverter,
			Converter<List<String>, ParticipantDataValue> objectConverter) {
		this.field = new NumericFormField();
		this.field.getDataColumn().setColumnType(columnType);
		this.field.setType(type);
		this.field.setStringConverter(stringConverter);
		this.field.setParticipantDataValueConverter(objectConverter);
	}
	
	public NumericFormFieldBuilder minValue(Double minValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((NumericFormField)field).setMinValue(minValue);
		return this;
	}

	public NumericFormFieldBuilder maxValue(Double maxValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((NumericFormField)field).setMaxValue(maxValue);
		return this;
	}
	
}
