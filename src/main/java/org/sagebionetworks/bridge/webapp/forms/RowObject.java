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
public class RowObject implements HasValuesMap {
	
	private static final Logger logger = LogManager.getLogger(RowObject.class.getName());

	private Long id;
	private Map<String, String> valuesByHeader = Maps.newHashMap();
	
	public RowObject(Long id, List<String> headers, List<String> values) {
		if (id == null) {
			throw new IllegalArgumentException("RowObject id cannot be null");
		}
		if (headers.size() != values.size()) {
			throw new IllegalArgumentException("RowObject names must match values 1:1");
		}
		this.id = id;
		for (int i=0; i < headers.size(); i++) {
			valuesByHeader.put(headers.get(i), values.get(i));
		}
	}
	
	public String getId() {
		return this.id.toString();
	}

	@Override
	public Map<String, String> getValuesMap() {
		return valuesByHeader;
	}
	
}
