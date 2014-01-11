package org.sagebionetworks.bridge.webapp.specs;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

public enum UIType {
	
	GROUP,
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
}
