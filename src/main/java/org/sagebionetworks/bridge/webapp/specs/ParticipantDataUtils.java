package org.sagebionetworks.bridge.webapp.specs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ParticipantDataUtils {
	
	public static ParticipantDataDescriptor getDescriptor(Specification spec) {
		ParticipantDataDescriptor descriptor = new ParticipantDataDescriptor();
		descriptor.setName(spec.getName());
		descriptor.setDescription(spec.getDescription());
		descriptor.setRepeatType(spec.getRepeatType());
		descriptor.setRepeatFrequency(spec.getRepeatFrequency());
		return descriptor;
	}
	
	public static List<ParticipantDataColumnDescriptor> getColumnDescriptors(String descriptorId, Specification spec) {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList();
		for (FormElement field : spec.getAllFormElements()) {
			if (field.getType().getColumnType() != null) {
				ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
				column.setName(field.getName());
				column.setDescription(field.getLabel());
				column.setColumnType(field.getType().getColumnType()); 
				column.setParticipantDataDescriptorId(descriptorId);
				list.add(column);
			}
		}
		return list;
	}
	
	public static List<ParticipantDataRow> getRowsForCreate(Specification spec, Map<String, String> values) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForCreate() requires values map");
		}
		
		return specToParticipantDataRow(spec, values, null);
	}
	
	public static List<ParticipantDataRow> getRowsForUpdate(Specification spec, Map<String, String> values, long rowId) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForUpdate() requires values");
		}
		
		return specToParticipantDataRow(spec, values, rowId);
	}

	private static List<ParticipantDataRow> specToParticipantDataRow(Specification spec, Map<String, String> values, Long rowId) {
		Map<String, ParticipantDataValue> data = Maps.newHashMap();
		for (FormElement element : spec.getAllFormElements()) {
			ParticipantDataColumnType columnType = element.getType().getColumnType();
			if (columnType != null) {
				switch (columnType) {
				case DATETIME:
					String dateStringValue = values.get(element.getName());
					if (dateStringValue != null && dateStringValue.length() > 0) {
						try {
							Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStringValue);
							ParticipantDataDatetimeValue dateValue = new ParticipantDataDatetimeValue();
							dateValue.setValue(date.getTime());
							data.put(element.getName(), dateValue);
						} catch (ParseException e) {
							throw new RuntimeException("Could not parse date " + dateStringValue);
						}
					}
					break;
				default:
					ParticipantDataValue value = ValueTranslator.transformToValue(values, element.getName(), columnType);
					if (value != null) {
						data.put(element.getName(), value);
					}
					break;
				}
			}
		}
		ParticipantDataRow row = new ParticipantDataRow();
		row.setRowId(rowId);
		row.setData(data);
		return Collections.singletonList(row);
	}
}
