package org.sagebionetworks.bridge.webapp.jsp;

import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DataTableButtonTag extends SimpleTagSupport {
	
	private String id;
	private String type = "default"; 
	private String icon; // Icon from Bootstrap library to show in the button
	private String label;
	private String action;
	private String confirm;
	
	@Override
	public void doTag() {
		DataTableTag table = (DataTableTag) findAncestorWithClass(this, DataTableTag.class);
		if (table != null) {
			table.addButton(this);
		}
		SpecificationDataTableTag stable = (SpecificationDataTableTag) findAncestorWithClass(this,
				SpecificationDataTableTag.class);
		if (stable != null) {
			stable.addButton(this);
		}
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
}
