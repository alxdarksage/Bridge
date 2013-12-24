package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

public class EnumeratedFormField extends FormField {

	protected final List<String> enumeratedValues;
	
	public EnumeratedFormField(String name, String label, boolean defaultable, List<String > enumeratedValues) {
		super(name, label, defaultable);
		this.enumeratedValues = enumeratedValues;
	}

	public List<String> getEnumeratedValues() {
		return enumeratedValues;
	}
	
}
