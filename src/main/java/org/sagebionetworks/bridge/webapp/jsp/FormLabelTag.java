package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormLabelTag extends SpringAwareTag {
	
	private static final Logger logger = LogManager.getLogger(FormLabelTag.class.getName());

	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		if (!field.isRequired() && fieldErrors.isEmpty()) {
			getJspContext().getOut().write(field.getLabel());
		} else {
			TagBuilder tb = new TagBuilder();
			if (!fieldErrors.isEmpty()) {
				tb.startTag("span", "class", "error-text");
			}
			tb.append(field.getLabel());
			if (field.isRequired()) {
				tb.append(" ");
				tb.startTag("span");
				if (fieldErrors.isEmpty()) {
					tb.addAttribute("class", "error-text");
				}
				tb.append("*");
				tb.endTag("span");
			}
			if (!fieldErrors.isEmpty()) {
				tb.startTag("span");
			}
			getJspContext().getOut().write(tb.toString());
		}
	}

}
