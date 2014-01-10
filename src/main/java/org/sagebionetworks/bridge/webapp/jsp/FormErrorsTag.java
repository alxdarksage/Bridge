package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.google.common.collect.Lists;

/**
 * Compared to Spring's form errors tag, this has the ability to group errors from multiple fields.
 * Sometimes based on layout this is what we want.
 */
public class FormErrorsTag extends SpringAwareTag {

	private List<FormElement> fields;
	private TagBuilder tb = new TagBuilder();
	
	public void setFields(List<FormElement> fields) {
		this.fields = fields;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		if (field == null && fields == null) {
			throw new IllegalArgumentException("FormErrorsTag requires either @field or @fields to be set");
		}
		if (this.fields != null) {
			field = fields.get(0); // so super() validation succeeds.
		}
		super.doTag();
		if (this.fields != null) {
			fieldErrors = getErrorsForAllFields();
		} else {
			fields = Lists.newArrayList(field);
		}
		// Now produce one error message for all the fields that are included.
		tb.startTag("div", "id", field.getName() + "_errors", "class", "error-text");
		for (FieldError error : fieldErrors) {
			tb.append(error.getDefaultMessage());
			tb.startTag("br").endTag("br");
		}
		tb.endTag("div");
		getJspContext().getOut().write(tb.toString());
	}
	
	private List<FieldError> getErrorsForAllFields() {
		List<FieldError> errorsForAllFields = Lists.newArrayList();
		for (FormElement element : fields) {
			String name = String.format("values['%s']", element.getName());
			Errors errors = requestContext.getErrors("dynamicForm");
			if (errors != null) {
				List<FieldError> fieldErrors = errors.getFieldErrors(name);
				if (fieldErrors != null) {
					errorsForAllFields.addAll(fieldErrors);	
				}
			}
		}
		return errorsForAllFields;
	}	
	
}
