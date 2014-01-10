package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.NumericFormField;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * This carries a lot of information, it's easiest to render as a tag.
 *
 */
public class FieldTag extends SpringAwareTag {

	private static final Logger logger = LogManager.getLogger(FieldTag.class.getName());

	private TagBuilder tb = new TagBuilder();

	private String fieldName;
	private DynamicForm dynamicForm;
	private Set<String> defaultedFields;
	
	public void setDynamicForm(DynamicForm form) {
		this.dynamicForm = form;
	}
	public void setDefaultedFields(Set<String> defaultedFields) {
		this.defaultedFields = defaultedFields;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		if (dynamicForm == null) {
			throw new IllegalArgumentException("FieldTag requires @dynamiceForm to be set");
		}
		fieldName = String.format("values['%s']", field.getName());
		if (field.getInitialValue() != null && field.isReadonly()) {
			renderReadonly();
		} else if (field.getType() == ParticipantDataColumnType.DATETIME) {
			renderDatetime();
		} else if (field instanceof EnumeratedFormField) {
			renderEnumeration();
		} else {
			renderInput();
		}
		// This needs to be grouped at the layout level, not always right under the field itself.
		/*
		for (FieldError error : fieldErrors) {
			tb.startTag("span", "id", field.getName() + "_errors", "class", "error-text");
			tb.append(error.getDefaultMessage());
			tb.endTag("span");
		}
		*/
		getJspContext().getOut().write(tb.toString());
	}

	// This is exceptional in that it doesn't look like a field. It's filled out and can't be changed.
	private void renderReadonly() {
		tb.startTag("p", "class", "form-control-static");
		tb.append(field.getInitialValue());
		tb.endTag("p");
		
		tb.startTag("input", "type", "hidden");
		tb.addAttribute("id", field.getName());
		tb.addAttribute("name", fieldName);
		tb.addAttribute("value", field.getInitialValue());
		tb.endTag("input");
	}
	
	private void renderDatetime() {
		String currentValue = dynamicForm.getValues().get(field.getName());
		tb.startTag("input");
		addDefaultAttributes(currentValue);
		tb.addAttribute("type", "date"); // not datetime, which might be an issue
		if (currentValue == null && field.getInitialValue() != null) {
			tb.addAttribute("value", field.getInitialValue());
		}
		if (currentValue != null) {
			tb.addAttribute("value", currentValue);
		}
		tb.endTag("input");
	}
	
	private void renderInput() {
		String currentValue = dynamicForm.getValues().get(field.getName());
		tb.startTag("input");
		addDefaultAttributes(currentValue);
		if (field instanceof NumericFormField) {
			addNumericAttributes((NumericFormField)field);
		} else {
			tb.addAttribute("type", "text");
		}
		if (currentValue == null && field.getInitialValue() != null) {
			tb.addAttribute("value", field.getInitialValue());
		}
		if (currentValue != null) {
			tb.addAttribute("value", currentValue);
		}
		tb.endTag("input");
	}
	
	private void renderEnumeration() {
		String currentValue = dynamicForm.getValues().get(field.getName());
		tb.startTag("select");
		addDefaultAttributes(currentValue);
		tb.startTag("option", "value", "");
		tb.append("Select value");
		tb.endTag("option");
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
		Set<String> classes = Sets.newHashSet("form-control", "input-sm");
		
		if(!fieldErrors.isEmpty()) {
			classes.add("error-control");
		}
		if (StringUtils.isNotBlank(currentValue) && defaultedFields != null && defaultedFields.contains(field.getName())) {
			classes.add("defaulted");
		}
		tb.addAttribute("id", field.getName());
		tb.addAttribute("name", fieldName);
		tb.addAttribute("data-type", field.getType().name().toLowerCase());
		if (field.isReadonly()) {
			tb.addAttribute("readonly", "readonly");
		}
		tb.addAttribute("class", Joiner.on(" ").join(classes));
	}
	
	private void addNumericAttributes(NumericFormField numeric) {
		tb.addAttribute("type", "number");
		if (numeric.getMinValue() != null) {
			tb.addAttribute("min", numeric.getMinValue().toString());
		}
		if (numeric.getMaxValue() != null) {
			tb.addAttribute("max", numeric.getMaxValue().toString());
		}
	}
	
}
