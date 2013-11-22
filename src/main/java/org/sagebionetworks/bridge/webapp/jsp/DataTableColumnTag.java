package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DataTableColumnTag extends SimpleTagSupport {

	private String label;
	private String field;
	private String link;
	private String icon;
	private String stat;
	
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getStatic() {
		return stat;
	}

	public void setStatic(String stat) {
		this.stat = stat;
	}
	
}
