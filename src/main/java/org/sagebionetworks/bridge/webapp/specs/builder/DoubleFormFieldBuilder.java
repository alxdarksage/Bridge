package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.DoubleFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.springframework.core.convert.converter.Converter;

public class DoubleFormFieldBuilder extends FormFieldBuilder {

	public DoubleFormFieldBuilder(UIType type, ParticipantDataColumnType columnType, Converter<ParticipantDataValue, List<String>> stringConverter,
			Converter<List<String>, ParticipantDataValue> objectConverter) {
		this.field = new DoubleFormField();
		this.field.setExportable();
		this.field.getDataColumn().setColumnType(columnType);
		this.field.setType(type);
		this.field.setStringConverter(stringConverter);
		this.field.setParticipantDataValueConverter(objectConverter);
	}
	
	public DoubleFormFieldBuilder minValue(Double minValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((DoubleFormField)field).setMinValue(minValue);
		return this;
	}

	public DoubleFormFieldBuilder maxValue(Double maxValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((DoubleFormField)field).setMaxValue(maxValue);
		return this;
	}
	
}
