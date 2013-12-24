package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import com.google.common.collect.Lists;

public class FormField implements FormElement {

	protected String name;
	protected String label;
	protected boolean defaultable;
	
	public FormField(String name, String label, boolean defaultable) {
		this.name = name;
		this.label = label;
		this.defaultable = defaultable;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public boolean getDefaultable() {
		return defaultable;
	}

	@Override
	public List<FormElement> getChildren() {
		return Lists.newArrayList();
	}

}
