package org.sagebionetworks.bridge.webapp.specs;

import org.sagebionetworks.bridge.webapp.converter.LabToStringConverter;

public class RangeNormBar extends AbstractFormElement {

	private String valueKey;
	private String unitKey;
	private String lowKey;
	private String highKey;
	
	public RangeNormBar(String label, String baseKey, String valueKey, String unitKey, String lowKey, String highKey) {
		super();
		setName(baseKey);
		setStringConverter(LabToStringConverter.INSTANCE);
		setType(UIType.RANGE_NORM_BAR);
		setLabel(label);
		this.valueKey = baseKey + valueKey;
		this.unitKey = baseKey + unitKey;
		this.lowKey = baseKey + lowKey;
		this.highKey = baseKey + highKey;
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
