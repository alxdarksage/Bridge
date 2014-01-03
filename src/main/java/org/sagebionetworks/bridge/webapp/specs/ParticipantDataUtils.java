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
			// TODO: FormElement has to declare more types than this eventually.
			column.setColumnType(ParticipantDataColumnType.DOUBLE); 
			column.setParticipantDataDescriptorId(descriptorId);
			list.add(column);			
		}
		return list;
	}
	
	public static RowSet getRowSetForCreate(Specification spec, Map<String, String> values) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForCreate() requires values map");
		}
		List<String> names = getFieldNames(spec);
		Row row = new Row();
		List<String> newValues = Lists.newArrayList();
		for (String header : names) {
			if (header.equals(Specification.CREATED_ON) || header.equals(Specification.MODIFIED_ON)) {
				newValues.add( convertToString(header, StringUtils.EMPTY	) ); // anything but null
			} else {
				newValues.add( convertToString(header, values.get(header)) );	
			}
		}
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(names);
		data.setRows(Lists.newArrayList(row));
		return data;
	}

	public static RowSet getRowSetForUpdate(Specification spec, Map<String, String> values, RowSet rowSet, long rowId) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForUpdate() requires values");
		}
		List<String> names = getFieldNames(spec);
		Row row = ClientUtils.getRowById(rowSet, rowId);
		List<String> newValues = Lists.newArrayList();
		for (String header : names) {
			if (header.equals(Specification.CREATED_ON)) {
				newValues.add( getValueInRow(row, rowSet.getHeaders(), Specification.CREATED_ON) ); // passthrough value, immutable
			} else if (header.equals(Specification.MODIFIED_ON)) {
				newValues.add( convertToString(header, StringUtils.EMPTY) ); // anything but null
			} else {
				newValues.add( convertToString(header, values.get(header)) );	
			}
		}
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(names);
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
	public static Object convertToObject(String header, String value) {
		if (StringUtils.isNotBlank(value) && !"null".equals(value)) {
			if (Specification.CREATED_ON.equals(header) || Specification.MODIFIED_ON.equals(header)) {
				return DateTime.parse(value, ISODateTimeFormat.dateTime()).toDate();
			}
		}
		return value;
	}
	
	private static String getValueInRow(Row row, List<String> headers, String header) {
		for (int i=0; i < headers.size(); i++) {
			if (header.equals(headers.get(i))) {
				return row.getValues().get(i);
			}
		}
		throw new IllegalArgumentException(header + " is not a valid header");
	}
	
	private static String convertToString(String header, Object object) {
		if (object != null) {
			if (header.equals(Specification.CREATED_ON) || header.equals(Specification.MODIFIED_ON)) {
				DateTime date = new DateTime();
				return date.toString(ISODateTimeFormat.dateTime());
			} else {
				return (String)object;
			}
		}
		return "";	
	}
	
	private static List<String> getFieldNames(Specification spec) {
		List<String> names = Lists.newArrayList();
		for (FormElement field : spec.getAllFormElements()) {
			names.add(field.getName());
		}
		return names;
	}
}
