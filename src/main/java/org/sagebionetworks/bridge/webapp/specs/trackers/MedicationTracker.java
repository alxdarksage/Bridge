package org.sagebionetworks.bridge.webapp.specs.trackers;

import static org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType.IF_CHANGED;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.converter.DateToISODateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.DateToShortFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateConverter;
import org.sagebionetworks.bridge.webapp.forms.ParticipantDataRowAdapter;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.FormGroup;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.TabularGroup;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MedicationTracker implements Specification {

	public static final String MEDICATIONS_NAME = "Medications";

	public static final String DOSE_FIELD = "dose";
	public static final String MEDICATION_FIELD = "medication";
	public static final String DOSE_INSTRUCTIONS_FIELD = "dose_instructions";
	public static final String START_DATE_FIELD = "start_date";
	public static final String END_DATE_FIELD = "end_date";
	
	private static final String MEDICATION_LABEL = "Medication";
	private static final String INLINE_EDITOR_LABEL = "New Medication";
	private static final String END_DATE_LABEL = "End Date";
	private static final String START_DATE_LABEL = "Start Date";
	private static final String DOSE_INSTRUCTIONS_LABEL = "Dose Instructions";
	private static final String DOSE_LABEL = "Dose";
	
	private static final String DOSE_INSTRUCTIONS_PLACEHOLDER = "2x/day with water";
	private static final String DOSE_PLACEHOLDER = "10mg";
	private static final String MEDICATION_PLACEHOLDER = "Drug name";
	
	List<FormElement> fields = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();
	FormGroup inlineEditor;
	FormGroup root; 
	
	public MedicationTracker() {
		FormFieldBuilder builder = new FormFieldBuilder();
		add( builder.asText().name(MEDICATION_FIELD).label(MEDICATION_LABEL).required().create() );
		add( builder.asText().name(DOSE_FIELD).label(DOSE_LABEL).create() );
		add( builder.asText().name(DOSE_INSTRUCTIONS_FIELD).label(DOSE_INSTRUCTIONS_LABEL).create() );
		add( builder.asDate().name(START_DATE_FIELD).label(START_DATE_LABEL).create() );
		add( builder.asDate().name(END_DATE_FIELD).label(END_DATE_LABEL).create() );
		
		root = new FormGroup(UIType.LIST, getName());
		// Inline editor for a new record
		createInlineEditor(root);
		// A table, filtered to active medications
		createMedicationsTable(root, "Current Medications", "active");
		// A table, filtered to completed/past medications
		createMedicationsTable(root, "Past Medications", "finished");
	}
	
	private void add(FormElement element) {
		fields.add(element);
		tableFields.put(element.getName(), element);
	}
	
	private void createInlineEditor(FormGroup root) {
		FormFieldBuilder builder = new FormFieldBuilder();
		inlineEditor = new FormGroup(UIType.INLINE_EDITOR, INLINE_EDITOR_LABEL);
		inlineEditor.add(builder.asText().name(MEDICATION_FIELD).placeholder(MEDICATION_PLACEHOLDER).label(MEDICATION_LABEL).required().create());
		inlineEditor.add(builder.asText().name(DOSE_FIELD).placeholder(DOSE_PLACEHOLDER).label(DOSE_LABEL).required().create());
		inlineEditor.add(builder.asText().name(DOSE_INSTRUCTIONS_FIELD).placeholder(DOSE_INSTRUCTIONS_PLACEHOLDER).label(DOSE_INSTRUCTIONS_LABEL).required().create());
		inlineEditor.add(builder.asDate().name(START_DATE_FIELD).stringConverter(new DateToISODateStringConverter()).label(START_DATE_LABEL).required().create());
		inlineEditor.add(builder.asDate().name(END_DATE_FIELD).stringConverter(new DateToISODateStringConverter()).label(END_DATE_LABEL).create());
		root.add(inlineEditor);
	}

	private void createMedicationsTable(FormGroup root, String name, String modelName) {
		FormFieldBuilder builder = new FormFieldBuilder();
		FormGroup table = new TabularGroup(name, modelName);
		table.add(builder.asValue().name(MEDICATION_FIELD).label(MEDICATION_LABEL).create());
		table.add(builder.asValue().name(DOSE_FIELD).label(DOSE_LABEL).create());
		table.add(builder.asValue().name(DOSE_INSTRUCTIONS_FIELD).label(DOSE_INSTRUCTIONS_LABEL).create());
		
		FormField field = builder.asValue().name(START_DATE_FIELD).label(START_DATE_LABEL).create();
		field.setStringConverter(DateToShortFormatDateStringConverter.INSTANCE);
		table.add(field);
		
		if (modelName.equals("finished")) {
			field = builder.asValue().name(END_DATE_FIELD).label(END_DATE_LABEL)
					.stringConverter(new DateToShortFormatDateStringConverter()).create();
			table.add(field);
			root.add(table);
		} else {
			field = builder.asDate().name(END_DATE_FIELD).label(END_DATE_LABEL).create();
			table.add(field);
			root.add(table);
		}
	}
	
	@Override
	public void postProcessParticipantDataRows(ModelMap map, List<ParticipantDataRow> rows) {
		List<ParticipantDataRow> active = Lists.newArrayList();
		List<ParticipantDataRow> finished = Lists.newArrayList();
		for (ParticipantDataRow row : rows) {
			if (row.getData().get(END_DATE_FIELD) == null) {
				active.add(row);
			} else {
				finished.add(row);
			}
		}
		
		Collections.sort(active, new ParticipantDataDatetimeComparator(START_DATE_FIELD));
		Collections.sort(finished, new ParticipantDataDatetimeComparator(END_DATE_FIELD));
		
		// Create a dynamic form with the unfinished record's contents
		ParticipantDataRow inprogress = (ParticipantDataRow)map.get("inprogress");
		if (inprogress != null) {
			map.addAttribute("dynamicForm", new ParticipantDataRowAdapter(inlineEditor, inprogress));
		}
		map.addAttribute("records", null);
		map.addAttribute("active", active);
		map.addAttribute("finished", finished);
	}
	
	@Override
	public String getName() {
		return MedicationTracker.MEDICATIONS_NAME;
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
	public String getDatetimeStartColumnName() {
		return null;
	}

	@Override
	public FormLayout getFormLayout() {
		return FormLayout.ALL_RECORDS_ONE_PAGE_INLINE;
	}

	@Override
	public String getForm() {
		return "journal/trackers/medication.jsp";
	}

	@Override
	public FormElement getShowStructure() {
		return root;
	}
	
	@Override
	public FormElement getEditStructure() {
		return root;
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
		// It should also be the case that start date is never after end date...
		if (StringUtils.isNotBlank(values.get(END_DATE_FIELD)) && StringUtils.isNotBlank(values.get(START_DATE_FIELD))) {
			ParticipantDataValue pdv = ISODateConverter.INSTANCE.convert(Collections.singletonList(values.get(START_DATE_FIELD)));
			long start = ((ParticipantDataDatetimeValue)pdv).getValue();
			
			pdv = ISODateConverter.INSTANCE.convert(Collections.singletonList(values.get(END_DATE_FIELD)));
			long end = ((ParticipantDataDatetimeValue)pdv).getValue();
			
			if (start > end) {
				values.put(START_DATE_FIELD, values.get(END_DATE_FIELD));
			}
		}
	}
}
