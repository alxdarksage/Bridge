package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.HasValuesMap;
import org.sagebionetworks.bridge.webapp.specs.DoubleFormField;
import org.sagebionetworks.bridge.webapp.specs.EnumeratedFormField;
import org.sagebionetworks.bridge.webapp.specs.UIType;

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
	private HasValuesMap valuesMapHolder;
	private Set<String> defaultedFields;
	
	public void setValuesMapHolder(HasValuesMap valuesMapHolder) {
		this.valuesMapHolder = valuesMapHolder;
	}
	public void setDefaultedFields(Set<String> defaultedFields) {
		this.defaultedFields = defaultedFields;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		fieldName = String.format("valuesMap['%s']", field.getName());
		
		if (field.isReadonly()) {
			renderReadonly();
		} else if (field.getUIType() == UIType.DATE || field.getUIType() == UIType.DATETIME) {
			renderDate(field.getUIType().name().toLowerCase());
		} else if (field instanceof EnumeratedFormField) {
			renderEnumeration();
		} else {
			renderInput();
		}
		getJspContext().getOut().write(tb.toString());
	}

	// This is exceptional in that it doesn't look like a field. It's filled out and can't be changed.
	private void renderReadonly() {
		String value = getValue();
		tb.startTag("p", "class", "form-control-static");
		tb.append(value);
		tb.endTag("p");
		
		tb.startTag("input", "type", "hidden");
		tb.addAttribute("id", field.getName());
		tb.addAttribute("name", fieldName);
		tb.addAttribute("value", value);
		tb.endTag("input");
	}
	
	private void renderDate(String subType) {
		String value = getValue();
		tb.startTag("input");
		addDefaultAttributes(value);
		tb.addAttribute("type", subType); // not datetime, which might be an issue
		tb.addAttribute("value", value);
		tb.endTag("input");
	}

	private void renderInput() {
		String value = getValue();
		tb.startTag("input");
		addDefaultAttributes(value);
		if (field instanceof DoubleFormField) {
			addNumericAttributes((DoubleFormField)field);
		} else {
			tb.addAttribute("type", "text");
		}
		tb.addAttribute("value", value);
		tb.endTag("input");
	}
	
	private void renderEnumeration() {
		String currentValue = getValue();
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
	
	private String getValue() {
		String currentValue = valuesMapHolder.getValuesMap().get(field.getName());
		if (currentValue == null && field.getInitialValue() != null) {
			return field.getInitialValue();
		}
		return currentValue;
	}
	
	private void addDefaultAttributes(String currentValue) {
		Set<String> classes = Sets.newHashSet("form-control", "input-sm");
		
		if(!fieldErrors.isEmpty()) {
			classes.add("error-control");
		}
		if (field.isDefaultable() && StringUtils.isNotBlank(currentValue) && defaultedFields != null && defaultedFields.contains(field.getName())) {
			classes.add("defaulted");
		}
		tb.addAttribute("id", field.getName());
		tb.addAttribute("name", fieldName);
		if (field.getDataType() != null) {
			tb.addAttribute("data-type", field.getDataType().name().toLowerCase());	
		}
		if (field.getPlaceholderText() != null) {
			tb.addAttribute("placeholder", field.getPlaceholderText());	
		}
		if (field.isReadonly()) {
			tb.addAttribute("readonly", "readonly");
		}
		if (field.isRequired()) {
			tb.addAttribute("required", "required");
		}
		tb.addAttribute("class", Joiner.on(" ").join(classes));
	}
	
	private void addNumericAttributes(DoubleFormField numeric) {
		tb.addAttribute("type", "number");  
		if (numeric.getMinValue() != null) {
			tb.addAttribute("min", numeric.getMinValue().toString());
		}
		if (numeric.getMaxValue() != null) {
			tb.addAttribute("max", numeric.getMaxValue().toString());
		}
		// It's weird but if you don't do this, you can't enter decimal values.
		if (numeric.getUIType() == UIType.DECIMAL_INPUT) {
			tb.addAttribute("step", "any");
		}
	}
	
}
