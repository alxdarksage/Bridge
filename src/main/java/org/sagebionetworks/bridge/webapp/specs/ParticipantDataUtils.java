package org.sagebionetworks.bridge.webapp.specs;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;

import com.google.common.collect.Lists;

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
	
	public static RowSet getRowSetForCreate(Specification spec, Map<String, String> values) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForCreate() requires values map");
		}
		
		List<String> newValues = Lists.newArrayList();
		List<String> headers = Lists.newArrayList();
		for (FormElement element : spec.getAllFormElements()) {
			if (element.getType().getColumnType() != null) {
				headers.add(element.getName());
				String value = values.get(element.getName());
				newValues.add(StringUtils.isEmpty(value) ? null : value);
			}
		}
		return createSingleRowRowSet(null, newValues, headers);
	}
	
	public static RowSet getRowSetForUpdate(Specification spec, Map<String, String> values, long rowId) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForUpdate() requires values");
		}
		
		List<String> newValues = Lists.newArrayList();
		List<String> headers = Lists.newArrayList();
		for (FormElement element : spec.getAllFormElements()) {
			if (element.getType().getColumnType() != null) {
				if (!element.isReadonly()) {
					headers.add(element.getName());
					String value = values.get(element.getName());
					newValues.add(StringUtils.isEmpty(value) ? null : value);
				}
			}
		}

		return createSingleRowRowSet(rowId, newValues, headers);
	}

	private static RowSet createSingleRowRowSet(Long rowId, List<String> newValues, List<String> headers) {
		Row row = new Row();
		row.setRowId(rowId);
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(headers);
		data.setRows(Collections.singletonList(row));
		return data;
	}
	
}
