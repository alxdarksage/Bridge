package org.sagebionetworks.bridge.webapp.specs;

public class NumericFormField extends FormField {
	
	protected Double minValue;
	protected Double maxValue;
	
	public NumericFormField() {
		super();
	}

	public Double getMinValue() {
		return minValue;
	}
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

}
