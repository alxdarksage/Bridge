package org.sagebionetworks.bridge.webapp.specs;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

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
	RANGE_NORM_BAR,
	// Just the key to the column and its type, so the value can be output as a formatted string.
	VALUE,
	DATE(ParticipantDataColumnType.DATETIME),
	DATETIME(ParticipantDataColumnType.DATETIME),
	TEXT_INPUT(ParticipantDataColumnType.STRING),
	INTEGER_INPUT(ParticipantDataColumnType.LONG),
	DECIMAL_INPUT(ParticipantDataColumnType.DOUBLE),
	SINGLE_SELECT(ParticipantDataColumnType.STRING),
	CHECKBOX(ParticipantDataColumnType.BOOLEAN);
	

	private ParticipantDataColumnType columnType;
	
	private UIType() {
	}
	
	private UIType(ParticipantDataColumnType columnType) {
		this.columnType = columnType;
	}
	
	public ParticipantDataColumnType getColumnType() {
		return this.columnType;
	}
	
	public String getLowerName() {
		return this.name().toLowerCase();
	}
}
