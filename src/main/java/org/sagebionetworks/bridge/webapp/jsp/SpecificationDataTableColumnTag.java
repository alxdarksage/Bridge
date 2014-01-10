package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.springframework.core.convert.converter.Converter;

public class SpecificationDataTableColumnTag extends SimpleTagSupport {

	private Specification specification;
	private String link;
	private String icon;
	private String stat;
	private String className;
	private Converter<Object,String> converter;
	
	@Override
	public void doTag() throws JspException, IOException {
		DataTableTag table = (DataTableTag)getParent();
		table.setSpecificationColumns(this);
	}
	
	public Specification getSpecification() {
		return specification;
	}
	public void setSpecification(Specification specification) {
		this.specification = specification;
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
	public Converter getConverter() {
		return converter;
	}
	public void setConverter(Converter converter) {
		this.converter = converter;
	}	
}
