package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public interface Specification {

	public String getName();
	public String getDescription();
	
	/**
	 * A strategy for displaying the tree of FormElements in the UI. There can be as many of 
	 * these as we need or want to build. 
	 * @return
	 */
	public FormLayout getFormLayout();
	/**
	 * A tree-like structure describing the form in a pattern that goes with the given FormLayout 
	 * (that is, if this structure doesn't match what the form layout code is expecting, then 
	 * stuff will break).
	 * @return
	 */
	public FormElement getFormStructure();
	/**
	 * All the form elements in a flat list, whether they are in the form UI presented to 
	 * the user or not. These should be in the same order as the ParticipantDataColumnDescriptors 
	 * for the ParticipantData records.
	 * @return
	 */
	public List<FormElement> getAllFormElements();
	/**
	 * Get a map of the column names and their form element descriptions, in the order they 
	 * should be shown left-to-right in a table view for this form.
	 * @return
	 */
	public SortedMap<String,FormElement> getTableFields();

	/**
	 * After creation/update by a user, this method may set values on the map to be persisted, 
	 * reformat values, compare values. It should eventually be able to do custom validation
	 * as well.  
	 * @param values
	 */
	public void setSystemSpecifiedValues(Map<String, String> values);
}
