package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.FormField;

/**
 * This carries a lot of information, it's easiest to render as a tag.
 *
 */
public class FieldTag extends SimpleTagSupport {

	private static final Logger logger = LogManager.getLogger(DataTableTag.class.getName());

	private TagBuilder tb = new TagBuilder();
	
	private FormField field;
	private String currentValue;
	private Set<String> defaultedFields;
	
	public void setField(FormField field) {
		this.field = field;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public void setDefaultedFields(Set<String> defaultedFields) {
		this.defaultedFields = defaultedFields;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		if (field instanceof EnumeratedFormField) {
			renderEnumeration();
		} else {
			renderInput();
		}
		getJspContext().getOut().write(tb.toString());
	}

	private void renderInput() {
		// This is exceptional in that it doesn't look like a field. It's filled out and can't be changed.
		if (field.getInitialValue() != null && field.isReadonly()) {
			tb.startTag("p", "class", "form-control-static");
			tb.append(field.getInitialValue());
			tb.endTag("p");
			
			tb.startTag("input", "type", "hidden");
			addDefaultAttributes();
			tb.addAttribute("value", field.getInitialValue());
			tb.endTag("input");
		} else {
			tb.startTag("input", "type", "text");
			addDefaultAttributes();
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
		tb.startTag("select");
		addDefaultAttributes();
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
	

	private void addDefaultAttributes() {
		tb.addAttribute("id", field.getName());
		tb.addAttribute("name", String.format("values['%s']", field.getName()));
		if (defaultedFields.contains(field.getName())) {
			tb.addAttribute("class", "form-control input-sm defaulted");
		} else {
			tb.addAttribute("class", "form-control input-sm");
		}
		if (field.isReadonly()) {
			tb.addAttribute("readonly", "readonly");
		}
	}
	
}
