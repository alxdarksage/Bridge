package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

public interface Specification {

	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
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

}
