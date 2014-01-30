package org.sagebionetworks.bridge.webapp.specs.builder;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.webapp.converter.StringConverter;
import org.sagebionetworks.bridge.webapp.converter.StringToStringConverter;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;

public class EnumeratedFormFieldBuilder extends FormFieldBuilder {

	public EnumeratedFormFieldBuilder(List<String> enumeratedValues) {
		this.field = new EnumeratedFormField();
		this.field.setExportable();
		((EnumeratedFormField)field).setEnumeratedValues(enumeratedValues);
		this.field.getDataColumn().setColumnType(ParticipantDataColumnType.STRING);
		this.field.setType(UIType.SINGLE_SELECT);
		this.field.setParticipantDataValueConverter(StringConverter.INSTANCE);
		this.field.setStringConverter(StringToStringConverter.INSTANCE);
	}
	
}
