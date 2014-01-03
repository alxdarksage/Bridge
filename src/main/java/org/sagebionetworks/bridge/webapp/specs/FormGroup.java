package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import com.google.common.collect.Lists;

public class FormGroup implements FormElement {

	private final String label;
	private final List<FormElement> children = Lists.newArrayList();
	
	public FormGroup(final String label) {
		this.label = label;
	}
	
	public FormGroup(final String label, final List<FormElement> children) {
		this.label = label;
		this.children.addAll(children);
	}
	
	@Override
	public String getName() {
		return label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean getDefaultable() {
		return false;
	}

	@Override
	public List<FormElement> getChildren() {
		return children;
	}
	
	public void addField(FormField field) {
		if (field == null) {
			throw new IllegalArgumentException("Field cannot be null");
		}
		children.add(field);
	}

}
