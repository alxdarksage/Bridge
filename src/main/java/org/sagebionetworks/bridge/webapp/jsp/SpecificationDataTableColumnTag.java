package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class SpecificationDataTableColumnTag extends SimpleTagSupport {

	private String link;
	private String icon;
	private String stat;
	private String className;
	
	@Override
	public void doTag() throws JspException, IOException {
		SpecificationDataTableTag table = (SpecificationDataTableTag)getParent();
		table.setSpecificationColumn(this);
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
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
}
