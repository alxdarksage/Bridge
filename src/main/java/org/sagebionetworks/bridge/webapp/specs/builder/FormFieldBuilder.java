package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

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
	
	public FormFieldBuilder asText() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.TEXT_INPUT);
		return this;
	}
	
	public FormFieldBuilder asText(String initialValue) {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.TEXT_INPUT);
		field.setInitialValue(initialValue);
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
		return new NumericFormFieldBuilder(UIType.DECIMAL_INPUT);
	}
	
	public FormFieldBuilder asLong() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		return new NumericFormFieldBuilder(UIType.INTEGER_INPUT);
	}

	public FormFieldBuilder asBoolean() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.CHECKBOX);
		return this;
	}
	
	public FormFieldBuilder asDate() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.DATE);
		return this;
	}
	
	public FormFieldBuilder asDateTime() {
		if (field != null) {
			throw new IllegalArgumentException(NOT_NULL_MESSAGE);
		}
		field = new FormField();
		field.setType(UIType.DATETIME);
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
