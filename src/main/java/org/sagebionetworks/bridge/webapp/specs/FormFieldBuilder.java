package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

public class FormFieldBuilder {

	private static final String NULL_MESSAGE = "Must first call a for* type method on the builder";
	private static final String NOT_NULL_MESSAGE = "Called a for* method in the middle of constructing an instance";
	
	private FormField field;
	
	public FormFieldBuilder() {
	}
	
	public FormFieldBuilder forField() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		return this;
	}
	
	public FormFieldBuilder forEnumeratedField() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new EnumeratedFormField();
		return this;
	}
	
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
		FormField temp = field;
		field = null;
		return temp;
	}
	
	public FormFieldBuilder withName(String name) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.name = name;
		return this;
	}

	public FormFieldBuilder withLabel(String label) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.label = label;
		return this;
	}
	
	public FormFieldBuilder asString() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.type = ParticipantDataColumnType.STRING;
		return this;
	}
	
	public FormFieldBuilder asDouble() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.type = ParticipantDataColumnType.DOUBLE;
		return this;
	}
	
	public FormFieldBuilder asLong() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.type = ParticipantDataColumnType.LONG;
		return this;
	}

	public FormFieldBuilder asBoolean() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.type = ParticipantDataColumnType.BOOLEAN;
		return this;
	}
	
	public FormFieldBuilder asDatetime() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.type = ParticipantDataColumnType.DATETIME;
		return this;
	}
	
	public FormFieldBuilder withInitialValue(String initialValue) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.initialValue = initialValue;
		return this;
	}

	public FormFieldBuilder asReadonly() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.readonly = true;
		return this;
	}

	public FormFieldBuilder asRequired() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.required = true;
		return this;
	}
	
	public FormFieldBuilder asDefaultable() {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		field.defaultable = true;
		return this;
	}	
	
	public FormFieldBuilder withEnumeration(List<String> enumeratedValues) {
		if (field == null) {
			throw new IllegalArgumentException(NULL_MESSAGE);
		}
		((EnumeratedFormField)field).enumeratedValues = enumeratedValues;
		return this;
	}

}
