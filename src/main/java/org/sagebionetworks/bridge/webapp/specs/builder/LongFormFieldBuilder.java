package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.FieldConverter;
import org.sagebionetworks.bridge.webapp.specs.LongFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;

public class LongFormFieldBuilder extends FormFieldBuilder {

	public LongFormFieldBuilder(UIType type, ParticipantDataColumnType columnType,
			FieldConverter<ParticipantDataValue, Map<String,String>> stringConverter,
			FieldConverter<Map<String,String>, ParticipantDataValue> objectConverter) {
		this.field = new LongFormField();
		this.field.setExportable();
		this.field.getDataColumn().setColumnType(columnType);
		this.field.setType(type);
		this.field.setStringConverter(stringConverter);
		this.field.setParticipantDataValueConverter(objectConverter);
	}
	
	public LongFormFieldBuilder minValue(Long minValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((LongFormField)field).setMinValue(minValue);
		return this;
	}

	public LongFormFieldBuilder maxValue(Long maxValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((LongFormField)field).setMaxValue(maxValue);
		return this;
	}
}
