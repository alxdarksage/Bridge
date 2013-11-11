package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;

public class NotificationTag extends SimpleTagSupport {
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();

		BridgeRequest request = (BridgeRequest) pageContext.getRequest();
		String notice = request.getNotification();

		if (StringUtils.isNotBlank(notice)) {
			out.write("<div id=\"notice\">"+notice+"</div><script>humane.log(document.getElementById('notice').textContent);</script>");
		}

	}

}
