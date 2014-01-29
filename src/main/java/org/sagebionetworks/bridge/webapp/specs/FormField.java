package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

public class FormField implements FormElement {

	private ParticipantDataColumnDescriptor dataColumn;
	protected String label;
	protected UIType type;
	protected boolean readonly;
	protected boolean required;
	protected boolean defaultable;
	protected Converter<List<String>, ParticipantDataValue> toParticipantDataValueConverter;
	protected Converter<ParticipantDataValue, List<String>> toStringConverter;
	
	public FormField() {
		this.dataColumn = new ParticipantDataColumnDescriptor();
	}
	
	@Override
	public ParticipantDataColumnDescriptor getDataColumn() {
		return dataColumn;
	}
	
	public void setDataColumn(ParticipantDataColumnDescriptor column) {
		this.dataColumn = column;
	}
	
	@Override
	public String getName() {
		return dataColumn.getName();
	}
	public void setName(String name) {
		dataColumn.setName(name);
	}

	@Override
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getInitialValue() {
		return dataColumn.getDefaultValue();
	}
	public void setInitialValue(String initialValue) {
		dataColumn.setDefaultValue(initialValue);
	}

	@Override
	public boolean isRequired() {
		return required;
	}
	public void setRequired() {
		this.required = true;
	}
	
	@Override
	public boolean isReadonly() {
		return readonly;
	}
	public void setReadonly() {
		this.readonly = true;
	}
	
	@Override
	public UIType getType() {
		return type;
	}
	public void setType(UIType type) {
		this.type = type;
	}
	
	@Override
	public boolean isDefaultable() {
		return defaultable;
	}
	public void setDefaultable() {
		this.defaultable = true;
	}
	
	@Override
	public List<FormElement> getChildren() {
		return Lists.newArrayList();
	}

	@Override
	public Converter<List<String>, ParticipantDataValue> getParticipantDataValueConverter() {
		return toParticipantDataValueConverter;
	}
	
	public void setParticipantDataValueConverter(Converter<List<String>, ParticipantDataValue> converter) {
		this.toParticipantDataValueConverter = converter ;
	}

	@Override
	public Converter<ParticipantDataValue, List<String>> getStringConverter() {
		return toStringConverter;
	}
	
	public void setStringConverter(Converter<ParticipantDataValue, List<String>> converter) {
		this.toStringConverter = converter;
	}
	
}
