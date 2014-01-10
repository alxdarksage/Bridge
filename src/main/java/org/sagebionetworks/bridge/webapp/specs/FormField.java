package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import com.google.common.collect.Lists;

public class FormField implements FormElement {

	protected String name;
	protected String label;
	protected UIType type;
	protected String initialValue;
	protected boolean readonly;
	protected boolean required;
	protected boolean defaultable;
	
	public FormField() {
	}
	
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public boolean isRequired() {
		return required;
	}
	public void setRequired() {
		this.required = true;
	}
	
	@Override
	public boolean isReadonly() {
		return readonly;
	}
	public void setReadonly() {
		this.readonly = true;
	}
	
	@Override
	public UIType getType() {
		return type;
	}
	public void setType(UIType type) {
		this.type = type;
	}
	
	@Override
	public boolean isDefaultable() {
		return defaultable;
	}
	public void setDefaultable() {
		this.defaultable = true;
	}
	
	@Override
	public List<FormElement> getChildren() {
		return Lists.newArrayList();
	}
	
}
