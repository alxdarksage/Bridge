package org.sagebionetworks.bridge.webapp.specs.trackers;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
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
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * This is just a test that we can have different forms.
 */
public class MoodTracker implements Specification {

	private final FormField mind;
	private final FormField body;
	private final FormField date;
	
	public MoodTracker() {
		date = new FormFieldBuilder().asDate().name("date").label("date").create();
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
	public String getForm() {
		return null;
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
		return Lists.<FormElement> newArrayList(mind, body, date);
	}

	@Override
	public SortedMap<String, FormElement> getTableFields() {
		TreeMap<String,FormElement> map = Maps.newTreeMap();
		map.put("date", date);
		map.put("Mind", mind);
		map.put("Body", body);
		return map;
	}

	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {
		values.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
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
	public String getDatetimeStartColumnName() {
		return "date";
	}

	public void postProcessParticipantDataRows(ModelMap map, List<ParticipantDataRow> rows) {
		// noop
	}
}
