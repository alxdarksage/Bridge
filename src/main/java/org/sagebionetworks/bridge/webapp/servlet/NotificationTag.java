package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class NotificationTag extends SimpleTagSupport {

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();

		BridgeRequest request = (BridgeRequest) pageContext.getRequest();
		String notice = request.getNotification();

		// TODO: Commons util for this.
		if (notice != null && !"".equals(notice)) {
			out.write("<script>humane.log(\"" + notice + "\");</script>");
		}

	}

}
