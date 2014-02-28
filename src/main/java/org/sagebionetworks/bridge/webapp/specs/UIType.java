package org.sagebionetworks.bridge.webapp.specs;

public enum UIType {
	
	NULL,
	GROUP,
	LIST,
	// Lays out a set of children in columns, with the grid as a whole providing the label/errors
	GRID,
	// One set of fields in a grid (a row)
	ROW,
	// Two fields grouped together as a range of values.
	RANGE,
	// A bar showing lab results with a range of expected values for the lab
	RANGE_NORM_BAR,
	// Just the key to the column and its type, so the value can be output as a formatted string.
	VALUE,
	// Shows a horizontal editor for a record as a whole
	INLINE_EDITOR,
	// Component that looks like data tables elsewhere in the application, but it contains FormElement components, 
	// and so can edit one or more ParticipantData records
	TABULAR,
	LAB_ROW,
	DATE,
	DATETIME,
	TEXT_INPUT,
	INTEGER_INPUT,
	DECIMAL_INPUT,
	SINGLE_SELECT,
	CHECKBOX;
	
	public String getLowerName() {
		return this.name().toLowerCase();
	}
}
