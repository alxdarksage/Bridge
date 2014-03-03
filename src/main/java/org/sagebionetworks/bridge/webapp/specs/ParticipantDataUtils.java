package org.sagebionetworks.bridge.webapp.specs;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.FieldConverter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ParticipantDataUtils {
	
	private static final Logger logger = LogManager.getLogger(ParticipantDataUtils.class.getName());
	
	public static ParticipantDataDescriptor getDescriptor(Specification spec) {
		ParticipantDataDescriptor descriptor = new ParticipantDataDescriptor();
		descriptor.setName(spec.getName());
		descriptor.setDescription(spec.getDescription());
		descriptor.setRepeatType(spec.getRepeatType());
		descriptor.setRepeatFrequency(spec.getRepeatFrequency());
		descriptor.setDatetimeStartColumnName(spec.getDatetimeStartColumnName());
		return descriptor;
	}
	
	public static List<ParticipantDataColumnDescriptor> getColumnDescriptors(String descriptorId, Specification spec) {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList();
		for (FormElement field : spec.getAllFormElements()) {
			if (field.getDataColumn().getColumnType() != null && !field.isCompoundField()) {
				field.getDataColumn().setParticipantDataDescriptorId(descriptorId);
				list.add(field.getDataColumn());
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
	
	// Due to labs and other compound fields, we have to check for this in many cases.
	
	public static Map<String,String> getMapForValue(String fieldName, String value) {
		Map<String,String> map = Maps.newHashMap();
		map.put(fieldName, value);
		return map;
	}

	public static ParticipantDataStatusList getFinishedStatus(String id) {
		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(id);
		status.setLastEntryComplete(true);
		status.setLastStarted(new Date());
		statuses.setUpdates(Collections.singletonList(status));
		return statuses;
	}
	
	public static ParticipantDataStatusList getInProcessStatus(String id) {
		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(id);
		status.setLastEntryComplete(false);
		status.setLastStarted(new Date());
		statuses.setUpdates(Collections.singletonList(status));
		return statuses;
	}
	
	private static List<ParticipantDataRow> specToParticipantDataRow(Specification spec, Map<String, String> values, Long rowId) {
		Map<String, ParticipantDataValue> data = Maps.newHashMap();
		for (FormElement element : spec.getAllFormElements()) {
			FieldConverter<Map<String,String>, ParticipantDataValue> converter = element.getParticipantDataValueConverter();
			if (converter != null && !element.isCompoundField()) {
				ParticipantDataValue pdv = converter.convert(element.getName(), values);
				if (pdv != null) {
					data.put(element.getName(), pdv);	
				}
			}
		}
		ParticipantDataRow row = new ParticipantDataRow();
		row.setRowId(rowId);
		row.setData(data);
		return Collections.singletonList(row);
	}
	
}
