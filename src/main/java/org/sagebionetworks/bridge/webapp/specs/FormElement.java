package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

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
	public boolean isDefaultable();
	/**
	 * Can this value be changed after it is created?
	 * 
	 * @return
	 */
	public boolean isImmutable();
	/**
	 * Initial value for this field (the default value, as opposed to the defaulted value 
	 * from a prior form.
	 * @return
	 */
	public String getInitialValue();
	/**
	 * Read only value. If a field is read only with an initial value, the value is permanently
	 * set but worth including in the data set.
	 * @return
	 */
	public boolean isReadonly();
	
	public ParticipantDataColumnType getType();
	
	public List<FormElement> getChildren();
	
}
