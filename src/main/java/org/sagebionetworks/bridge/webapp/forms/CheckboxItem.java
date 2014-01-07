package org.sagebionetworks.bridge.webapp.forms;

public class CheckboxItem {

	private boolean selected;
	private String displayName;
	private String id;
	
	public CheckboxItem(String displayName, String id) {
		this.displayName = displayName;
		this.id = id;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}
	
}
