package org.sagebionetworks.bridge.webapp.jsp;

import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.forms.HasValuesMap;
import org.sagebionetworks.bridge.webapp.forms.ParticipantDataRowAdapter;
import org.sagebionetworks.bridge.webapp.specs.FormElement;

public class JspFunctions {

	public static HasValuesMap valuesMapHolder(FormElement element, ParticipantDataRow row) {
		return new ParticipantDataRowAdapter(element, row);
	}
	
}
