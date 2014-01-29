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
		String value = valuesMapHolder.getValuesMap().get(field.getName());
		getJspContext().getOut().write("<span class='multi'>"+value+"</span>");
	}
}
