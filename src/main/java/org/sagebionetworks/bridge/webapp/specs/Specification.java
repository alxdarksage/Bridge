package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.repo.model.table.RowSet;

public interface Specification {
	
	public FormLayout getFormLayout();
	public List<FormElement> getFormElements();
	public List<String> getFieldNames();
	
	public Object convertToObject(String header, String value);
	public String convertToString(String header, Object object);
	
	public ParticipantDataDescriptor getDescriptor();
	public List<ParticipantDataColumnDescriptor> getColumnDescriptors(ParticipantDataDescriptor descriptor);

	public RowSet getRowSetForCreate(Map<String, String> values);
	public RowSet getRowSetForUpdate(Map<String, String> values, RowSet rowSet, long rowId);

}
