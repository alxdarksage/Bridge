package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

public interface FormElement {

	/**
	 * The form name, and the name of the field in the table where the information is saved.
	 * @return
	 */
	public String getName();
	/**
	 * A short, human-readable label for this value.
	 * @return
	 */
	public String getLabel();
	/**
	 * Can this value be pre-set from the value of a prior form already saved by the user?
	 * @return
	 */
	public boolean getDefaultable();
	
	public List<FormElement> getChildren();
	
}
