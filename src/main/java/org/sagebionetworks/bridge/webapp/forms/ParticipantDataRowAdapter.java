package org.sagebionetworks.bridge.webapp.forms;


import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.SpecificationUtils;

import com.google.common.collect.Maps;

public class ParticipantDataRowAdapter implements HasValuesMap {
	
	private final Map<String,String> map;
	
	public ParticipantDataRowAdapter(FormElement element, ParticipantDataRow row) {
		List<FormElement> list = SpecificationUtils.toList(element);
		this.map = Maps.newHashMap();
		for (FormElement el : list) {
			if (el.getStringConverter() != null) {
				Map<String,String> values = el.getStringConverter().convert(el.getName(), row.getData().get(el.getName()));
				if (values != null) {
					map.putAll(values);	
				}
			}
		}
	}
	
	@Override
	public Map<String, String> getValuesMap() {
		return map;
	}
	
}
