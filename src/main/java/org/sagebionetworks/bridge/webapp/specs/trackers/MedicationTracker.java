package org.sagebionetworks.bridge.webapp.specs.trackers;

import static org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType.IF_CHANGED;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.converter.DateToShortFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.FormGroup;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MedicationTracker implements Specification {

	List<FormElement> fields = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();
	
	public MedicationTracker() {
		FormFieldBuilder builder = new FormFieldBuilder();
		add( builder.asText().name("medication").label("Medication").required().create() );
		add( builder.asText().name("dosage").label("Dosage").required().create() );
		add( builder.asDate().name("start_date").label("Start Date").required().create() );
		add( builder.asDate().name("end_date").label("End Date").create() );
	}
	
	private void add(FormElement element) {
		fields.add(element);
		tableFields.put(element.getName(), element);
	}
	
	
	@Override
	public String getName() {
		return "Medication Tracker";
	}

	@Override
	public String getDescription() {
		return "Medication Tracker";
	}

	@Override
	public ParticipantDataRepeatType getRepeatType() {
		return IF_CHANGED;
	}

	@Override
	public String getRepeatFrequency() {
		return null;
	}

	@Override
	public FormLayout getFormLayout() {
		return FormLayout.ALL_RECORDS_ONE_PAGE_INLINE;
	}

	@Override
	public FormElement getEditStructure() {
		FormGroup root = new FormGroup(UIType.LIST, getName());
		// Inline editor for a new record
		createInlineEditor(root);
		// A table, filtered to active medications
		createMedicationsTable(root, "Current Medications");
		// A table, filtered to completed/past medications
		createMedicationsTable(root, "Past Medications");
		return root;
	}
	
	@Override
	public Comparator<ParticipantDataRow> getSortComparator() {
		return null;
	}
	
	private void createInlineEditor(FormGroup root) {
		FormFieldBuilder builder = new FormFieldBuilder();
		FormGroup inlineEditor = new FormGroup(UIType.INLINE_EDITOR, "New Medication");
		inlineEditor.add(builder.asText().name("medication").label("Medication").required().create());
		inlineEditor.add(builder.asText().name("dosage").label("Dosage").required().create());
		inlineEditor.add(builder.asDate().name("start_date").label("Start Date").required().create());
		inlineEditor.add(builder.asDate().name("end_date").label("End Date").create());
		root.add(inlineEditor);
	}

	private void createMedicationsTable(FormGroup root, String name) {
		FormFieldBuilder builder = new FormFieldBuilder();
		FormGroup table = new FormGroup(UIType.TABULAR, name);
		table.add(builder.asText().name("medication").label("Medication").readonly().create());
		table.add(builder.asText().name("dosage").label("Dosage").readonly().create());
		
		FormField field = builder.asValue().name("start_date").label("Start Date").create();
		field.setStringConverter(DateToShortFormatDateStringConverter.INSTANCE);
		table.add(field);
		
		field = builder.asDate().name("end_date").label("End Date").create();
		table.add(field);
		root.add(table);
	}
	
	@Override
	public FormElement getShowStructure() {
		return getEditStructure(); // No different in this presentation
	}

	@Override
	public List<FormElement> getAllFormElements() {
		return fields;
	}

	@Override
	public SortedMap<String, FormElement> getTableFields() {
		return tableFields;
	}

	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {

	}

}
