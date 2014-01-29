package org.sagebionetworks.bridge.webapp.specs;

public class RangeNormBar extends AbstractFormElement {

	private String valueKey;
	private String unitKey;
	private String lowKey;
	private String highKey;
	
	public RangeNormBar(String label, String valueKey, String unitKey, String lowKey, String highKey) {
		super();
		setType(UIType.RANGE_NORM_BAR);
		setLabel(label);
		this.valueKey = valueKey;
		this.unitKey = unitKey;
		this.lowKey = lowKey;
		this.highKey = highKey;
	}

	public String getValueKey() {
		return valueKey;
	}

	public String getUnitKey() {
		return unitKey;
	}

	public String getLowKey() {
		return lowKey;
	}

	public String getHighKey() {
		return highKey;
	}

}
