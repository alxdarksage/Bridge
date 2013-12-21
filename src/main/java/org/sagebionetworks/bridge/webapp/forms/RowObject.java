package org.sagebionetworks.bridge.webapp.forms;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

/**
 * A fake entity object with some enforced fields that can be used to reference/link 
 * to the individual rows in the UI.
 */
public class RowObject {
	
	private static final Logger logger = LogManager.getLogger(RowObject.class.getName());

	private Long id;
	private List<String> keySet;
	private Map<String, Object> valueMap = Maps.newLinkedHashMap();
	
	public RowObject(CompleteBloodCountSpec spec, Long id, List<String> headers, List<String> values) {
		if (spec == null) {
			throw new IllegalArgumentException("Specification cannot be null");
		}
		if (id == null) {
			throw new IllegalArgumentException("RowObject id cannot be null");
		}
		if (headers.size() != values.size()) {
			throw new IllegalArgumentException("Row names must match values 1:1");
		}
		this.id = id;
		this.keySet = headers;
		for (int i=0; i < headers.size(); i++) {
			String header = headers.get(i);
			Object value = spec.convertToObject(header, values.get(i));
			valueMap.put(header, value);
		}
	}
	
	public String getId() {
		return Long.toString(this.id);
	}
	
	// TODO: These are special, even beyond a specification.
	
	public Object getCreatedOn() {
		return getValue("createdOn");
	}
	
	public Object getModifiedOn() {
		return getValue("modifiedOn");
	}
	
	public List<String> keySet() {
		return keySet;
	}
	
	public Object getValue(String key) {
		return valueMap.get(key);
	}
	
}
