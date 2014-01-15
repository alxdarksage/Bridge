package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.sagebionetworks.bridge.webapp.forms.DynamicForm;

public class ValueTag extends SpringAwareTag {

	private DynamicForm dynamicForm;
	
	public void setDynamicForm(DynamicForm form) {
		this.dynamicForm = form;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		if (dynamicForm == null) {
			throw new IllegalArgumentException("ValueTag requires @dynamiceForm to be set");
		}
		String currentValue = dynamicForm.getValues().get(field.getName());
		if (field.getObjectConverter() != null && field.getStringConverter() != null) {
			Object parsed = field.getObjectConverter().convert(currentValue);
			currentValue = field.getStringConverter().convert(parsed);
		}
		getJspContext().getOut().write(currentValue);
	}
}
