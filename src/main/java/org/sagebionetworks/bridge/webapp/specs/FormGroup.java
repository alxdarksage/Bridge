package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import com.google.common.collect.Lists;

public class FormGroup implements FormElement {

	private final String label;
	private final List<FormElement> columns = Lists.newArrayList();
	
	public FormGroup(final String label) {
		this.label = label;
	}
	
	public FormGroup(final String label, final List<FormElement> rows) {
		this.label = label;
		this.columns.addAll(rows);
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
		return columns;
	}
	
	public void addColumn(FormField column) {
		if (column == null) {
			throw new IllegalArgumentException("Column cannot be null");
		}
		columns.add(column);
	}

}
