package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.FormField;

/**
 * This carries a lot of information, it's easiest to render as a tag.
 *
 */
public class FieldTag extends SimpleTagSupport {

	private static final Logger logger = LogManager.getLogger(FieldTag.class.getName());

	private TagBuilder tb = new TagBuilder();
	
	private FormField field;
	private DynamicForm dynamicForm;
	private Set<String> defaultedFields;
	
	public void setField(FormField field) {
		this.field = field;
	}
	public void setDynamicForm(DynamicForm form) {
		this.dynamicForm = form;
	}
	public void setDefaultedFields(Set<String> defaultedFields) {
		this.defaultedFields = defaultedFields;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		if (field == null) {
			throw new IllegalArgumentException("FieldTag requires @field to be set");
		}
		if (dynamicForm == null) {
			throw new IllegalArgumentException("FieldTag requires @dynamiceForm to be set");
		}
		if (field instanceof EnumeratedFormField) {
			renderEnumeration();
		} else {
			renderInput();
		}
		getJspContext().getOut().write(tb.toString());
	}

	private void renderInput() {
		String currentValue = dynamicForm.getValues().get(field.getName());
		// This is exceptional in that it doesn't look like a field. It's filled out and can't be changed.
		if (field.getInitialValue() != null && field.isReadonly()) {
			tb.startTag("p", "class", "form-control-static");
			tb.append(field.getInitialValue());
			tb.endTag("p");
			
			tb.startTag("input", "type", "hidden");
			addDefaultAttributes(currentValue);
			tb.addAttribute("value", field.getInitialValue());
			tb.endTag("input");
		} else {
			tb.startTag("input", "type", "text");
			addDefaultAttributes(currentValue);
			if (currentValue == null && field.getInitialValue() != null) {
				tb.addAttribute("value", field.getInitialValue());
			}
			if (currentValue != null) {
				tb.addAttribute("value", currentValue);
			}
			tb.endTag("input");
		}
		
	}
	
	private void renderEnumeration() {
		String currentValue = dynamicForm.getValues().get(field.getName());
		tb.startTag("select");
		addDefaultAttributes(currentValue);
		for (String value : ((EnumeratedFormField)field).getEnumeratedValues()) {
			tb.startTag("option", "value", value);
			if (value.equals(currentValue)) {
				tb.addAttribute("selected", "selected");
			}
			tb.append(value);
			tb.endTag("option");
		}
		tb.endTag("select");
	}
	

	private void addDefaultAttributes(String currentValue) {
		tb.addAttribute("id", field.getName());
		tb.addAttribute("name", String.format("values['%s']", field.getName()));
		tb.addAttribute("data-type", field.getType().name().toLowerCase());
		if (StringUtils.isNotBlank(currentValue) && defaultedFields != null && defaultedFields.contains(field.getName())) {
			tb.addAttribute("class", "form-control input-sm defaulted");
		} else {
			tb.addAttribute("class", "form-control input-sm");
		}
		if (field.isReadonly()) {
			tb.addAttribute("readonly", "readonly");
		}
	}
	
}
