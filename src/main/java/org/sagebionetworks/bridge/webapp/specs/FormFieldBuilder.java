package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

public class FormFieldBuilder {

	private FormField field;
	
	public FormFieldBuilder() {
	}
	
	public FormFieldBuilder forField() {
		field = new FormField();
		return this;
	}
	
	public FormFieldBuilder forEnumeratedField() {
		field = new EnumeratedFormField();
		return this;
	}
	
	public FormField create() {
		return field;
	}
	
	public FormFieldBuilder withName(String name) {
		field.name = name;
		return this;
	}

	public FormFieldBuilder withLabel(String label) {
		field.label = label;
		return this;
	}
	
	public FormFieldBuilder asString() {
		field.type = ParticipantDataColumnType.STRING;
		return this;
	}
	
	public FormFieldBuilder asDouble() {
		field.type = ParticipantDataColumnType.DOUBLE;
		return this;
	}
	
	public FormFieldBuilder asLong() {
		field.type = ParticipantDataColumnType.LONG;
		return this;
	}

	public FormFieldBuilder asBoolean() {
		field.type = ParticipantDataColumnType.BOOLEAN;
		return this;
	}
	
	public FormFieldBuilder asDatetime() {
		field.type = ParticipantDataColumnType.DATETIME;
		return this;
	}
	
	public FormFieldBuilder withInitialValue(String initialValue) {
		field.initialValue = initialValue;
		return this;
	}

	public FormFieldBuilder asReadonly() {
		field.readonly = true;
		return this;
	}

	public FormFieldBuilder asImmutable() {
		field.immutable = true;
		return this;
	}

	public FormFieldBuilder asDefaultable() {
		field.defaultable = true;
		return this;
	}	
	
	public FormFieldBuilder withEnumeration(List<String> enumeratedValues) {
		((EnumeratedFormField)field).enumeratedValues = enumeratedValues;
		return this;
	}

}
