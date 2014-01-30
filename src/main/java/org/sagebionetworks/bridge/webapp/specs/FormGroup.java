package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

public class FormGroup extends AbstractFormElement {
	
	public FormGroup(final String label) {
		this(UIType.GROUP, label);
	}
	
	public FormGroup(final String label, final List<FormElement> children) {
		this(UIType.GROUP, label, children);
	}
	
	public FormGroup(UIType type, final String label) {
		setType(type);
		setLabel(label);
	}
	
	public FormGroup(UIType type, final String label, final List<FormElement> children) {
		setType(type);
		setLabel(label);
		getChildren().addAll(children);
	}
	
	public void add(FormElement element) {
		if (element == null) {
			throw new IllegalArgumentException("Child element in a FormGroup cannot be null");
		}
		getChildren().add(element);
	}
	
	public void addField(FormElement element) {
		if (element == null) {
			throw new IllegalArgumentException("Form element cannot be null");
		}
		getChildren().add(element);
	}

}
