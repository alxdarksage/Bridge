package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;

public class EnumeratedFormFieldBuilder extends FormFieldBuilder {

	public EnumeratedFormFieldBuilder(List<String> enumeratedValues) {
		this.field = new EnumeratedFormField();
		((EnumeratedFormField)field).setEnumeratedValues(enumeratedValues);
		this.field.setType(UIType.SINGLE_SELECT);
	}
	
}
