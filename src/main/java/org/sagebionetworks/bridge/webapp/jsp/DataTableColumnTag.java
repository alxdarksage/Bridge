package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DataTableColumnTag extends SimpleTagSupport {

	private String label;
	private String field;
	
	@Override
	public void doTag() throws JspException, IOException {
		DataTableTag table = (DataTableTag)getParent();
		table.addColumn(this);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
}
