package org.sagebionetworks.bridge.webapp.specs;

import java.util.Collections;
import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

public class RangeNormBar implements FormElement {

	private String label;
	private String valueKey;
	private String unitKey;
	private String lowKey;
	private String highKey;
	
	public RangeNormBar(String label, String valueKey, String unitKey, String lowKey, String highKey) {
		this.label = label;
		this.valueKey = valueKey;
		this.unitKey = unitKey;
		this.lowKey = lowKey;
		this.highKey = highKey;
	}
	
	@Override
	public ParticipantDataColumnDescriptor getDataColumn() {
		return null;
	}
	
	@Override
	public String getName() {
		return "RangeNormBar";
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isDefaultable() {
		return false;
	}

	@Override
	public String getInitialValue() {
		return null;
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public UIType getType() {
		return UIType.RANGE_NORM_BAR;
	}

	@Override
	public Converter<List<String>, ParticipantDataValue> getParticipantDataValueConverter() {
		return null;
	}

	@Override
	public Converter<ParticipantDataValue, List<String>> getStringConverter() {
		return null;
	}

	@Override
	public List<FormElement> getChildren() {
		return Collections.emptyList();
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
