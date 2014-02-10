package org.sagebionetworks.bridge.webapp.specs;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ParticipantDataUtils {
	
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
			if (field.getDataType() != null) {
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
	public static String getOneValue(List<String> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		return values.get(0);
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
			Converter<List<String>,ParticipantDataValue> converter = element.getParticipantDataValueConverter();
			String value = values.get(element.getName());
			if (converter != null && StringUtils.isNotBlank(value)) {
				ParticipantDataValue pdv = converter.convert(Lists.newArrayList(value));
				data.put(element.getName(), pdv);
			}
		}
		ParticipantDataRow row = new ParticipantDataRow();
		row.setRowId(rowId);
		row.setData(data);
		return Collections.singletonList(row);
	}
	
}
