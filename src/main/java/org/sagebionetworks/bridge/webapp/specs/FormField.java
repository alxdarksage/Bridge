package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

import com.google.common.collect.Lists;

public class FormField implements FormElement {

	protected final String name;
	protected final String label;
	protected final ParticipantDataColumnType type;
	protected final boolean immutable;
	protected final boolean defaultable;
	
	public FormField(final String name, final String label, final ParticipantDataColumnType type, final boolean immutable, final boolean defaultable) {
		this.name = name;
		this.label = label;
		this.type = type;
		this.immutable = immutable;
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
