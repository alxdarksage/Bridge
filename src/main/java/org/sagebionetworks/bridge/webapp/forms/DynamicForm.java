package org.sagebionetworks.bridge.webapp.forms;

import java.util.Map;

import com.google.common.collect.Maps;

public class DynamicForm implements HasValuesMap {
	
	private Map<String, String> values = Maps.newHashMap();

	@Override
	public Map<String, String> getValuesMap() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

}
