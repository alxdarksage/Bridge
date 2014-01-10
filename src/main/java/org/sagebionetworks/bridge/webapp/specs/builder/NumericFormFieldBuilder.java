package org.sagebionetworks.bridge.webapp.specs.builder;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.webapp.specs.NumericFormField;

public class NumericFormFieldBuilder extends FormFieldBuilder {

	public NumericFormFieldBuilder(ParticipantDataColumnType type) {
		this.field = new NumericFormField();
		this.field.setType(type);
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
