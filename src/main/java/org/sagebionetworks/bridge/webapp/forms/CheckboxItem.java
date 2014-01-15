package org.sagebionetworks.bridge.webapp.forms;

public class CheckboxItem {

	private boolean selected;
	private String label;
	private String id;
	
	public CheckboxItem(String label, String id) {
		this.label = label;
		this.id = id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}
	
}
