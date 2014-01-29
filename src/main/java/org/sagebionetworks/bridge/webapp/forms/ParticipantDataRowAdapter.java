package org.sagebionetworks.bridge.webapp.forms;

import java.util.Collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.SpecificationUtils;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Maps;

/**
 * This is a little more flexible than a static method to do this conversion from the controller. 
 * There was one place in the JSP where I wanted to convert individual rows, this allows it
 * via a JSP function. 
 */
public class ParticipantDataRowAdapter implements HasValuesMap {

	private static final Logger logger = LogManager.getLogger(ParticipantDataRowAdapter.class.getName());
	
	private class AdapterMap implements Map<String,String> {
		@Override public void clear() {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override public boolean containsKey(Object key) {
			return row.getData().containsKey(key);
		}
		@Override public boolean containsValue(Object value) {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override public Set<Map.Entry<String, String>> entrySet() {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override public String get(Object key) {
			ParticipantDataValue pdv = row.getData().get(key);
			Converter<ParticipantDataValue,List<String>> converter = converters.get(key);
			if (pdv != null && converter != null) {
				return ParticipantDataUtils.getOneValue(converter.convert(pdv));	
			} else if (pdv != null) {
				// This value is probably not going to be displayed as such. However, the EL in JSP 
				// can include tests that this FormElement exists by calling the valueMap. This 
				// returns something that's 1) more useful than nothing and 2) evaluates to true.
				return ValueTranslator.toString(pdv);
			}
			return "";
		}
		@Override public boolean isEmpty() {
			return row.getData().isEmpty();
		}
		@Override public Set<String> keySet() {
			return row.getData().keySet();
		}
		@Override public String put(String key, String value) {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override public void putAll(Map<? extends String, ? extends String> m) {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override public String remove(Object key) {
			throw new UnsupportedOperationException("Not implemented");
		}
		@Override public int size() {
			return row.getData().size();
		}
		@Override public Collection<String> values() {
			throw new UnsupportedOperationException("Not implemented");
		}
	}
	
	private final Map<String,Converter<ParticipantDataValue, List<String>>> converters = Maps.newHashMap();
	private final ParticipantDataRow row;
	private final AdapterMap adapterMap;
	
	public ParticipantDataRowAdapter(FormElement element, ParticipantDataRow row) {
		List<FormElement> list = SpecificationUtils.toList(element);
		for (FormElement el : list) {
			if (el.getStringConverter() != null) {
				converters.put(el.getName(), el.getStringConverter());
			}
		}
		this.row = row;
		this.adapterMap = new ParticipantDataRowAdapter.AdapterMap();
	}

	@Override
	public Map<String, String> getValuesMap() {
		return adapterMap;
	}
	
}
