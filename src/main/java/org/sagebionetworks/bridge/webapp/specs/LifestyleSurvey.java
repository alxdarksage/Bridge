package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.Lists;

/**
 * This is just a test that we can have different forms.
 */
public class LifestyleSurvey implements Specification {

	private FormField oneField;
	
	public LifestyleSurvey() {
		oneField = new FormFieldBuilder().forField().asString().withName("feelings").withLabel("Feelings").create();
	}
	
	@Override
	public String getName() {
		return "Lifestyle";
	}

	@Override
	public String getDescription() {
		return "Lifestyle";
	}

	@Override
	public FormLayout getFormLayout() {
		return FormLayout.GRID;
	}

	@Override
	public FormElement getFormStructure() {
		return oneField;
	}

	@Override
	public List<FormElement> getAllFormElements() {
		return Lists.newArrayList((FormElement)oneField);
	}

	@Override
	public SortedMap<String, FormElement> getTableFields() {
		return null;
	}

	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {
		// noop
	}

}
