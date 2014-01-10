package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
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
		
		Row row = new Row();
		List<String> newValues = Lists.newArrayList();
		List<String> headers = Lists.newArrayList();
		for (FormElement element : spec.getAllFormElements()) {
			if (element.getType() != null) {
				headers.add(element.getName());
				newValues.add( values.get(element.getName()) );
			}
		}
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(headers);
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
	public static RowSet getRowSetForUpdate(Specification spec, Map<String, String> values, RowSet rowSet, long rowId) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForUpdate() requires values");
		}
		
		Row row = ClientUtils.getRowById(rowSet, rowId);
		List<String> newValues = Lists.newArrayList();
		List<String> headers = Lists.newArrayList();
		for (FormElement element : spec.getAllFormElements()) {
			if (element.getType() != null) {
				headers.add(element.getName());
				if (element.isReadonly()) {
					newValues.add( ClientUtils.getValueInRow(row, rowSet.getHeaders(), element.getName()) );
				} else {
					newValues.add( values.get(element.getName()) );
				}
			}
		}
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(headers);
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
}
