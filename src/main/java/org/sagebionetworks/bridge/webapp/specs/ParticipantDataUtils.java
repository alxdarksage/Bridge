package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
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
		return descriptor;
	}
	
	public static List<ParticipantDataColumnDescriptor> getColumnDescriptors(String descriptorId, Specification spec) {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList();
		for (FormElement field : spec.getAllFormElements()) {
			ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
			column.setName(field.getName());
			column.setDescription(field.getLabel());
			column.setColumnType(field.getType()); 
			column.setParticipantDataDescriptorId(descriptorId);
			list.add(column);			
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

	// All data is stored in ParticipantData as a string type. And most of it comes from the UI
	// in string form, so not all types may need to be converted, but you can overload this method.
	public static String convertToString(DateTime datetime) {
		return datetime.toString(ISODateTimeFormat.dateTime());
	}
	
	public static Object convertToObject(ParticipantDataColumnType type, String value) {
		if (StringUtils.isNotBlank(value) && !"null".equals(value)) {
			switch(type) {
			case FILEHANDLEID:
			case STRING:
				return value;
			case DATETIME:
				return DateTime.parse(value, ISODateTimeFormat.dateTime()).toDate();
			case BOOLEAN:
				return Boolean.valueOf(value);
			case LONG:
				return Long.parseLong(value);
			case DOUBLE:
				return Double.parseDouble(value);
			}
		}
		return value;
	}
	
}
