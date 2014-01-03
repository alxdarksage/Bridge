package org.sagebionetworks.bridge.webapp.forms;

import java.util.Map;

import com.google.common.collect.Maps;

public class DynamicForm {
	
	private Map<String, String> values = Maps.newHashMap();

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

}
