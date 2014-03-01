package org.sagebionetworks.bridge.webapp.specs.trackers;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang.NotImplementedException;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;

public class EventTracker implements Specification {
	
	public EventTracker() {
	}
	
	@Override
	public void postProcessParticipantDataRows(ModelMap map, List<ParticipantDataRow> rows) {
	}
	
	@Override
	public String getName() {
		return "Events";
	}

	@Override
	public String getDescription() {
		throw new NotImplementedException();
	}

	@Override
	public ParticipantDataRepeatType getRepeatType() {
		throw new NotImplementedException();
	}

	@Override
	public String getRepeatFrequency() {
		throw new NotImplementedException();
	}

	@Override
	public String getDatetimeStartColumnName() {
		throw new NotImplementedException();
	}

	@Override
	public FormLayout getFormLayout() {
		return null;
	}

	@Override
	public FormElement getShowStructure() {
		throw new NotImplementedException();
	}
	
	@Override
	public FormElement getEditStructure() {
		throw new NotImplementedException();
	}

	@Override
	public List<FormElement> getAllFormElements() {
		return Lists.newArrayList();
	}

	@Override
	public SortedMap<String, FormElement> getTableFields() {
		throw new NotImplementedException();
	}

	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {
	}
}
