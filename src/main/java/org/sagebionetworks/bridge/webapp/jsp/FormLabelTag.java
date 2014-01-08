package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.specs.FormElement;

public class FormLabelTag extends SimpleTagSupport {
	
	private static final Logger logger = LogManager.getLogger(FormLabelTag.class.getName());

	private FormElement element;
	
	public void setElement(FormElement element) {
		this.element = element;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		if (element == null) {
			throw new IllegalArgumentException("FormLabelTag requires @element to be set");
		}
		if (!element.isRequired()) {
			getJspContext().getOut().write(element.getLabel());
		} else {
			TagBuilder tb = new TagBuilder();
			tb.append(element.getLabel());
			tb.append(" ");
			tb.startTag("span", "style","color:#B22222");
			tb.append("*");
			tb.endTag("span");
			getJspContext().getOut().write(tb.toString());
		}
	}

}
