package org.sagebionetworks.bridge.webapp.jsp;

import java.util.Date;

import javax.servlet.jsp.JspTagException;

@SuppressWarnings("serial")
public class FormatDateLongTag extends org.apache.taglibs.standard.tag.rt.fmt.FormatDateTag {

	public FormatDateLongTag() {
		super();
	}

	// *********************************************************************
	// Accessor methods

	// 'value' attribute
	public void setValue(Long value) throws JspTagException {
		this.value = (value == null || value == 0L) ? null : new Date(value.longValue());
	}
}
