package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.springframework.ui.ModelMap;

public interface Specification {

	public String getName();
	public String getDescription();
	public ParticipantDataRepeatType getRepeatType();
	public String getRepeatFrequency();

	public String getDatetimeStartColumnName();
	
	/**
	 * Sort, filter, pull out the in-progress record, whatever the view requires. Just keep
	 * it out of the controller.
	 * @param map
	 * @param rows
	 */
	public void postProcessParticipantDataRows(ModelMap map, List<ParticipantDataRow> rows);
	
	/**
	 * A strategy for displaying the tree of FormElements in the UI. There can be as many of 
	 * these as we need or want to build. NOTE: This isn't currently used. I'm not sure
	 * if it's really necessary or not.
	 * @return
	 */
	public FormLayout getFormLayout();
	/**
	 * A tree-like structure describing the form. 
	 * 
	 * @return
	 */
	public FormElement getEditStructure();
	/**
	 * A tree-like structure describing the elements that create a view of this survey. 
	 * @return
	 */
	public FormElement getShowStructure();
	
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
