package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.forms.HasValuesMap;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;

public class ValueTag extends SpringAwareTag {

	private ParticipantDataRow row;
	private HasValuesMap valuesMapHolder;
	
	public void setValuesMapHolder(HasValuesMap valuesMapHolder) {
		this.valuesMapHolder = valuesMapHolder;
	}
	
	public void setParticipantDataRow(ParticipantDataRow row) {
		this.row = row;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		if (row != null) {
			ParticipantDataValue value = row.getData().get(field.getName());
			if (value != null) {
				if (field.getParticipantDataValueConverter() != null) {
					String string = ParticipantDataUtils.getOneValue(field.getStringConverter().convert(value));
					getJspContext().getOut().write("<span class='multi'>"+string+"</span>");
				}
			}
		} else if (valuesMapHolder != null) {
			String value = valuesMapHolder.getValuesMap().get(field.getName());
			getJspContext().getOut().write("<span class='multi'>"+value+"</span>");
		} else {
			getJspContext().getOut().write("<span class='multi'>ERROR</span>");
		}
	}
}
