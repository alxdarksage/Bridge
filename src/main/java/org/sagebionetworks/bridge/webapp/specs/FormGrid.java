package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

public class FormGrid extends FormGroup {

	List<String> headers;
	
	public FormGrid(String label, final List<String> headers) {
		super(UIType.GRID, label);
		this.headers = headers;
	}

	public FormGrid(final String label, final List<FormElement> children, final List<String> headers) {
		super(UIType.GRID, label, children);
		this.headers = headers;
	}
	
	public List<String> getHeaders() {
		return headers;
	}

}
