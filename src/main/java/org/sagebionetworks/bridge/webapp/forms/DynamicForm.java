package org.sagebionetworks.bridge.webapp.forms;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DynamicForm {
	
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	private CompleteBloodCountSpec spec;
	private Map<String, String> values = Maps.newHashMap();

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}
	
	public void setSpec(CompleteBloodCountSpec spec) {
		this.spec = spec;
	}
	
	public RowSet getNewRowSet() {
		if (spec == null) {
			throw new IllegalArgumentException("DynamicForm requires a form specification");
		}
		Row row = new Row();
		List<String> values = Lists.newArrayList();
		for (String header : spec.getRowNames()) {
			if (header.equals(CREATED_ON) || header.equals(MODIFIED_ON)) {
				values.add( spec.convertToString(header, StringUtils.EMPTY	) ); // anything but null
			} else {
				values.add( spec.convertToString(header, getValues().get(header)) );	
			}
		}
		row.setValues(values);
		RowSet data = new RowSet();
		data.setHeaders(spec.getRowNames());
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
	// TODO: Broken, pretty sure this is because we're using spec headers, not the headers
	// in the order that they come back from server, and that doesn't work for getValueInRow().
	public RowSet getUpdatedRowSet(List<String> headers, Row row) {
		if (spec == null) {
			throw new IllegalArgumentException("DynamicForm requires a form specification");
		}
		List<String> values = Lists.newArrayList();
		for (String header : spec.getRowNames()) {
			if (header.equals(CREATED_ON)) {
				values.add( getValueInRow(row, headers, CREATED_ON) ); // passthrough value, immutable
			} else if (header.equals(MODIFIED_ON)) {
				values.add( spec.convertToString(header, StringUtils.EMPTY) ); // anything but null
			} else {
				values.add( spec.convertToString(header, getValues().get(header)) );	
			}
		}
		row.setValues(values);
		RowSet data = new RowSet();
		data.setHeaders(spec.getRowNames());
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
	private String getValueInRow(Row row, List<String> headers, String header) {
		for (int i=0; i < headers.size(); i++) {
			if (header.equals(headers.get(i))) {
				return row.getValues().get(i);
			}
		}
		throw new IllegalArgumentException(header + " is not a valid header");
	}
}
