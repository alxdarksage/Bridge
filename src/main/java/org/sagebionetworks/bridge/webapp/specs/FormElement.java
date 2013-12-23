package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

public interface FormElement {

	public String getName();
	public String getLabel();
	public List<FormElement> getChildren();
	
}
