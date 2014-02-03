package org.sagebionetworks.bridge.webapp.specs;

public class LongFormField extends FormField {
	
	protected Long minValue;
	protected Long maxValue;
	
	public LongFormField() {
		super();
	}

	public Long getMinValue() {
		return minValue;
	}
	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}

	public Long getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}
	
}
