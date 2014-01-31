package org.sagebionetworks.bridge.webapp.specs.trackers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.FormGroup;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This is just a test that we can have different forms.
 */
public class MoodTracker implements Specification {

	private final FormField mind;
	private final FormField body;
	
	public MoodTracker() {
		mind = new FormFieldBuilder().asDouble().name("Mind").label("Mind").type("mood-slider").create();
		body = new FormFieldBuilder().asDouble().name("Body").label("Body").type("mood-slider").create();
	}
	
	@Override
	public String getName() {
		return "Mood Tracker";
	}

	@Override
	public String getDescription() {
		return "Mood check in";
	}

	@Override
	public FormLayout getFormLayout() {
		return FormLayout.NONE;
	}

	@Override
	public FormElement getEditStructure() {
		return new FormGroup("Mood Tracker", getAllFormElements());
	}

	@Override
	public FormElement getShowStructure() {
		return new FormGroup("Mood Tracker", getAllFormElements());
	}
	
	@Override
	public List<FormElement> getAllFormElements() {
		return Lists.<FormElement> newArrayList(mind, body);
	}

	@Override
	public SortedMap<String, FormElement> getTableFields() {
		TreeMap<String,FormElement> map = Maps.newTreeMap();
		map.put("Mind", mind);
		map.put("Body", body);
		return map;
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

	@Override
	public Comparator<ParticipantDataRow> getSortComparator() {
		return null;
	}
}
