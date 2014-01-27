package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.sagebionetworks.bridge.webapp.forms.HasValuesMap;

public class ValueTag extends SpringAwareTag {

	private HasValuesMap valuesMapHolder;
	
	public void setValuesMapHolder(HasValuesMap valuesMapHolder) {
		this.valuesMapHolder = valuesMapHolder;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		if (valuesMapHolder == null) {
			throw new IllegalArgumentException("ValueTag requires @valuesMapHolder to be set");
		}
		String currentValue = valuesMapHolder.getValuesMap().get(field.getName());
		if (currentValue != null) {
			
			/*
			if (field.getParticipantDataValueConverter() != null && field.getStringConverter() != null) {
				Object parsed = field.getParticipantDataValueConverter().convert(currentValue);
				currentValue = field.getStringConverter().convert(parsed);
			}
			*/
			getJspContext().getOut().write("<span class='multi'>"+currentValue+"</span>");
		}
	}
}
