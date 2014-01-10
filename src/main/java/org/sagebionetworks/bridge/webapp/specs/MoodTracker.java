package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;

import com.google.common.collect.Lists;

/**
 * This is just a test that we can have different forms.
 */
public class MoodTracker implements Specification {

	private final FormField mind;
	private final FormField body;
	
	public MoodTracker() {
		mind = new FormFieldBuilder().forField().asString().withName("Mind").withLabel("Mind").create();
		body = new FormFieldBuilder().forField().asString().withName("Body").withLabel("Body").create();
	}
	
	@Override
	public String getName() {
		return "Mood tracker";
	}

	@Override
	public String getDescription() {
		return "Mood";
	}

	@Override
	public FormLayout getFormLayout() {
		return FormLayout.NONE;
	}

	@Override
	public FormElement getFormStructure() {
		return new FormGroup("CBC", getAllFormElements());
	}

	@Override
	public List<FormElement> getAllFormElements() {
		return Lists.<FormElement> newArrayList(mind, body);
	}

	@Override
	public SortedMap<String, FormElement> getTableFields() {
		return null;
	}

	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {
		// noop
	}

	@Override
	public ParticipantDataRepeatType getRepeatType() {
		return ParticipantDataRepeatType.ALWAYS;
	}

	@Override
	public String getRepeatFrequency() {
		return null;
	}

}
