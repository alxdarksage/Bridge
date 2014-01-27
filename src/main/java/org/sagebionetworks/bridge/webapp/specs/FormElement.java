package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

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
	 * Initial value for this field (the default value, as opposed to the defaulted value 
	 * from a prior form.
	 * @return
	 */
	public String getInitialValue();
	/**
	 * Read only value. If a field is read only with an initial value, the value is a constant, 
	 * but worth including in the data set. Note that a specification may itself change a 
	 * readonly value: this only applies to the UI for the user. 
	 * @return
	 */
	public boolean isReadonly();
	/**
	 * This value *must* be entered by the user. It cannot later be removed (only changed, 
	 * persuant to validation criteria).
	 * @return
	 */
	public boolean isRequired();
	
	public UIType getType();
	
	/**
	 * Converters to convert back and forth between a string and native representation.
	 * @return
	 */
	public Converter<List<String>,ParticipantDataValue> getParticipantDataValueConverter();
	
	public Converter<ParticipantDataValue,List<String>> getStringConverter();
	
	public List<FormElement> getChildren();
	
}
