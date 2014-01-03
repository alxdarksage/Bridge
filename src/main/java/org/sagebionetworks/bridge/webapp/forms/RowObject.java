package org.sagebionetworks.bridge.webapp.forms;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;

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
	
	public RowObject(Long id, List<String> headers, List<String> values) {
		if (id == null) {
			throw new IllegalArgumentException("RowObject id cannot be null");
		}
		if (headers.size() != values.size()) {
			throw new IllegalArgumentException("FormGroup names must match values 1:1");
		}
		this.id = id;
		this.keySet = headers;
		for (int i=0; i < headers.size(); i++) {
			String header = headers.get(i);
			Object value = ParticipantDataUtils.convertToObject(header, values.get(i));
			valueMap.put(header, value);
		}
	}
	
	public String getId() {
		return Long.toString(this.id);
	}
	
	public Object getCreatedOn() {
		return getValue(Specification.CREATED_ON);
	}
	
	public Object getModifiedOn() {
		return getValue(Specification.MODIFIED_ON);
	}
	
	public List<String> keySet() {
		return keySet;
	}
	
	public Object getValue(String key) {
		return valueMap.get(key);
	}
	
}
