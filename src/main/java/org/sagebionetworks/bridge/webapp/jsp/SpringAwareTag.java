package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.support.JspAwareRequestContext;
import org.springframework.web.servlet.support.RequestContext;

public class SpringAwareTag extends SimpleTagSupport {
	
	protected static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE =
			"org.springframework.web.servlet.tags.REQUEST_CONTEXT";
	
	protected FormElement field;
	protected RequestContext requestContext;
	protected List<FieldError> fieldErrors;
	
	public void setField(FormElement field) {
		this.field = field;
	}

	@Override
	public void doTag() throws JspException, IOException {
		if (field == null) {
			throw new IllegalArgumentException("FieldTag requires @field to be set");
		}
		
		// From BindingErrorsTag
		getJspContext().findAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
		requestContext = (RequestContext) getJspContext().getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
		if (requestContext == null) {
			requestContext = new JspAwareRequestContext((PageContext)getJspContext());
			getJspContext().setAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE, this.requestContext);
		}
		fieldErrors = getErrors();
	}
	
	private List<FieldError> getErrors() {
		String name = String.format("values['%s']", field.getName());
		return requestContext.getErrors("dynamicForm").getFieldErrors(name);
	}	
}
