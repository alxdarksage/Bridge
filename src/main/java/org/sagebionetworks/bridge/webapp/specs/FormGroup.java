package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class FormGroup implements FormElement {

	private final String label;
	private final List<FormElement> children = Lists.newArrayList();
	private final UIType type;
	protected Converter<String, Object> objectConverter;
	protected Converter<Object, String> stringConverter;
	
	public FormGroup(final String label) {
		this(UIType.GROUP, label);
	}
	
	public FormGroup(final String label, final List<FormElement> children) {
		this(UIType.GROUP, label, children);
	}
	
	public FormGroup(UIType type, final String label) {
		this.type = type;
		this.label = label;
	}
	
	public FormGroup(UIType type, final String label, final List<FormElement> children) {
		this.type = type;
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
	public UIType getType() {
		return type;
	}

	@Override
	public boolean isDefaultable() {
		return false;
	}
	
	@Override
	public List<FormElement> getChildren() {
		return children;
	}

	@Override
	public String getInitialValue() {
		return null;
	}

	@Override
	public boolean isRequired() {
		return false;
	}
	
	@Override
	public boolean isReadonly() {
		return false;
	}

	@Override
	public Converter<String, Object> getObjectConverter() {
		return objectConverter;
	}

	@Override
	public Converter<Object, String> getStringConverter() {
		return stringConverter;
	}
	
	public void addField(FormElement element) {
		if (element == null) {
			throw new IllegalArgumentException("Form element cannot be null");
		}
		children.add(element);
	}

}
