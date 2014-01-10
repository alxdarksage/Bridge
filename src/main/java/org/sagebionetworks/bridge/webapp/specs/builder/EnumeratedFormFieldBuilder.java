package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;

public class EnumeratedFormFieldBuilder extends FormFieldBuilder {

	public EnumeratedFormFieldBuilder(List<String> enumeratedValues) {
		this.field = new EnumeratedFormField();
		((EnumeratedFormField)field).setEnumeratedValues(enumeratedValues);
		this.field.setType(ParticipantDataColumnType.STRING);
	}
	
}
