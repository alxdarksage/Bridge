package org.sagebionetworks.bridge.webapp.forms;

import java.util.List;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

/**
 * A fake entity object that can be used to reference/link to the individual rows in 
 * a ParticipantData record.
 */
public class RowObject {
	
	private static final Logger logger = LogManager.getLogger(RowObject.class.getName());

	private Long id;
	private List<String> headers;
	private Map<String, Object> valuesByHeader = Maps.newHashMap();
	
	public RowObject(Long id, List<String> headers, List<Object> values) {
		if (id == null) {
			throw new IllegalArgumentException("RowObject id cannot be null");
		}
		if (headers.size() != values.size()) {
			throw new IllegalArgumentException("RowObject names must match values 1:1");
		}
		this.id = id;
		this.headers = headers;
		for (int i=0; i < headers.size(); i++) {
			valuesByHeader.put(headers.get(i), values.get(i));
		}
	}
	
	public String getId() {
		return this.id.toString();
	}
	
	public List<String> getHeaders() {
		return headers;
	}
	
	public Object getValueForHeader(String key) {
		return valuesByHeader.get(key);
	}
	
}
