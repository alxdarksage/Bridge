package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

import com.google.common.collect.Lists;

public class FormField implements FormElement {

	protected String name;
	protected String label;
	protected ParticipantDataColumnType type;
	protected String initialValue;
	protected boolean readonly;
	protected boolean immutable;
	protected boolean defaultable;
	
	public FormField() {
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
	public String getInitialValue() {
		return initialValue;
	}

	@Override
	public boolean isReadonly() {
		return readonly;
	}
	
	@Override
	public ParticipantDataColumnType getType() {
		return type;
	}
	
	@Override
	public boolean isDefaultable() {
		return defaultable;
	}
	
	@Override
	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public List<FormElement> getChildren() {
		return Lists.newArrayList();
	}
	
}
