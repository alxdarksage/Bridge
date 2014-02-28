package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.FieldConverter;

import com.google.common.collect.Lists;

abstract public class AbstractFormElement implements FormElement {

	protected ParticipantDataColumnDescriptor column;
	protected String placeholderText;
	protected boolean defaultable;
	protected boolean isCompoundField;
	protected UIType type;
	protected FieldConverter<Map<String,String>, ParticipantDataValue> participantDataValueConverter;
	protected FieldConverter<ParticipantDataValue, Map<String,String>> stringConverter;
	protected List<FormElement> children = Lists.newArrayList();
	
	protected AbstractFormElement() {
		this.column = new ParticipantDataColumnDescriptor();
	}	
	
	@Override
	public ParticipantDataColumnDescriptor getDataColumn() {
		return column;
	}

	@Override
	public void setDataColumn(ParticipantDataColumnDescriptor column) {
		this.column = column;
	}
	
	@Override
	public ParticipantDataColumnType getDataType() {
		return column.getColumnType();
	}
	
	@Override
	public boolean isCompoundField() {
		return isCompoundField;
	}

	@Override
	public void setIsCompoundField(boolean isCompoundField) {
		this.isCompoundField = isCompoundField;
	}
	
	@Override
	public String getName() {
		return column.getName();
	}

	@Override
	public String getLabel() {
		return column.getDescription();
	}
	
	@Override
	public String getPlaceholderText() {
		return placeholderText;
	}

	@Override
	public boolean isDefaultable() {
		return defaultable;
	}

	@Override
	public String getInitialValue() {
		return column.getDefaultValue();
	}

	@Override
	public boolean isReadonly() {
		return nullSafeBoolean(column.getReadonly());
	}
	
	@Override
	public boolean isRequired() {
		return nullSafeBoolean(column.getRequired());
	}
	
	@Override
	public boolean isExportable() {
		return nullSafeBoolean(column.getExportable());
	}

	@Override
	public UIType getUIType() {
		return type;
	}

	@Override
	public FieldConverter<Map<String,String>, ParticipantDataValue> getParticipantDataValueConverter() {
		return participantDataValueConverter;
	}

	@Override
	public FieldConverter<ParticipantDataValue, Map<String,String>> getStringConverter() {
		return stringConverter;
	}

	@Override
	public List<FormElement> getChildren() {
		return children;
	}
	
	public void setName(String name) {
		column.setName(name);
	}
	
	public void setLabel(String label) {
		column.setDescription(label);
	}
	
	public void setPlaceholderText(String placeholderText) {
		this.placeholderText = placeholderText;
	}
	
	public void setInitialValue(String initialValue) {
		column.setDefaultValue(initialValue);
	}
	
	public void setRequired() {
		column.setRequired(true);
	}
	
	public void setReadonly() {
		column.setReadonly(true);
	}
	
	public void setExportable() {
		column.setExportable(true);
	}
	
	public void setPrivate() {
		column.setExportable(false);
	}
	
	public void setType(UIType type) {
		this.type = type;
	}
	
	public void setDefaultable() {
		this.defaultable = true;
	}
	
	public void setParticipantDataValueConverter(FieldConverter<Map<String,String>, ParticipantDataValue> converter) {
		this.participantDataValueConverter = converter;
	}
	
	public void setStringConverter(FieldConverter<ParticipantDataValue, Map<String,String>> converter) {
		this.stringConverter = converter;
	}

	private boolean nullSafeBoolean(Boolean value) {
		return (value == null) ? false : value.booleanValue();
	}
	
}
