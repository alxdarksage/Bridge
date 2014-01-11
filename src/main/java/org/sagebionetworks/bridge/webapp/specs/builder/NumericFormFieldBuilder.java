package org.sagebionetworks.bridge.webapp.specs.builder;

import org.sagebionetworks.bridge.webapp.specs.NumericFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.springframework.core.convert.converter.Converter;

public class NumericFormFieldBuilder extends FormFieldBuilder {

	public NumericFormFieldBuilder(UIType type, Converter<Object,String> stringConverter,
			Converter<String,Object> objectConverter) {
		this.field = new NumericFormField();
		this.field.setType(type);
		this.field.setStringConverter(stringConverter);
		this.field.setObjectConverter(objectConverter);
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
