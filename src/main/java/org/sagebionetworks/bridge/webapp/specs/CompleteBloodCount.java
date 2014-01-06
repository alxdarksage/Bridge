package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompleteBloodCount implements Specification {

	private static final String CREATED_ON = "createdOn";
	private static final String MODIFIED_ON = "modifiedOn";
	private static final String TESTED_ON = "testedOn";

	private static final String UNITS_SUFFIX = "_units";
	private static final String RANGE_LOW_SUFFIX = "_range_low";
	private static final String RANGE_HIGH_SUFFIX = "_range_high";

	private final List<String> CMM = Lists.newArrayList(Ratios.CELLS_PER_CMM.getAbbrev(), Ratios.CELLS_PER_MICROLITER.getAbbrev());
	private final List<String> GMDL = Lists.newArrayList(Ratios.GRAMS_PER_DECILITER.getAbbrev(), Ratios.MILLIMOLES_PER_LITER.getAbbrev());
	private final List<String> PERC = Lists.newArrayList(Units.PERCENTAGE.getAbbrev());
	private final List<String> FL = Lists.newArrayList(Units.FEMTOLITER.getAbbrev());
	private final List<String> PG = Lists.newArrayList(Units.PICOGRAM.getAbbrev());
	private final List<String> COUNT = Lists.newArrayList("count", "c.v.", "s.d.");
	
	private final List<String> COLLECTION_METHODS = Lists.newArrayList("Finger Prick", "IV Central Line", "Central Prick");

	List<FormElement> metadata = Lists.newArrayList();
	List<FormElement> displayRows = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();

	public CompleteBloodCount() {
		FormField field1 = new FormField(CREATED_ON, "Created on date", ParticipantDataColumnType.DATETIME, true, false);
		FormField field2 = new FormField(MODIFIED_ON, "Modified on date", ParticipantDataColumnType.DATETIME, false, false);
		metadata.add( field1 );
		metadata.add( field2 );
		tableFields.put(CREATED_ON, field1);
		tableFields.put(MODIFIED_ON, field2);

		List<FormElement> rows = Lists.newArrayList();
		rows.add(new FormField(TESTED_ON, "Date of test", ParticipantDataColumnType.DATETIME, false, false));
		rows.add(new EnumeratedFormField("draw_type", "Draw type", ParticipantDataColumnType.STRING, false, true, COLLECTION_METHODS));
		displayRows.add( new FormGroup("General information", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow("rbc", "Red cells (Erythrocytes / RBC)", CMM) );
		rows.add( addRow("hb", "Hemoglobin (Hb)", GMDL) );
		rows.add( addRow("hct", "Hematocrit (HCT)", PERC) );
		rows.add( addRow("mcv", "Mean corpuscular volume (MCV)", FL) );
		rows.add( addRow("mch", "Mean corpuscular hemoglobin (MCH)", PG) );
		// uiRow.add( addField("mchc", "Mean corpuscular hemoglobin concentration (MCHC)", PERC) );
		rows.add( addRow("rdw", "RBC distribution width (RDW)", COUNT) );
		rows.add( addRow("ret", "Reticulocyte count (Ret)", COUNT) );
		displayRows.add( new FormGroup("Red blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow("wbc", "White cells (Leukocytes / WBC)", CMM) );
		rows.add( addRow("wbc_diff", "WBC differential (WBC Diff)", PERC) );
		// But these can also be counts...
		rows.add( addRow("neutrophil", "Neutrophil", PERC) );
		rows.add( addRow("neutrophil_immature", "Immature Neutrophil (band neutrophil)", PERC) );
		rows.add( addRow("lymphocytes", "Lymphocytes", PERC) );
		rows.add( addRow("monocytes", "Monocytes", PERC) );
		// uiRow.add( addField("eosinophil", "Eosinophil", CMM) );
		// uiRow.add( addField("basophil", "Basophil", CMM) );
		displayRows.add( new FormGroup("White blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow("plt", "Platelet count (Thrombocyte / PLT)", CMM) );
		rows.add( addRow("mpv", "Mean platelet volume (MPV)", FL) );
		rows.add( addRow("pdw", "Platelet distribution width (PDW)", PERC) );
		displayRows.add( new FormGroup("Platelets", rows) );
	}

	@Override
	public String getName() {
		return "CBC";
	}
	
	@Override
	public String getDescription() {
		return "Complete Blood Count";
	}
	
	@Override
	public FormLayout getFormLayout() {
		return FormLayout.GRID;
	}

	@Override
	public FormElement getFormStructure() {
		return new FormGroup("CBC", displayRows);
	}
	
	@Override
	public List<FormElement> getAllFormElements() {
		List<FormElement> elements = Lists.newArrayList();
		for (FormElement field : metadata) {
			elements.add(field);
		}
		for (FormElement displayRow : displayRows) {
			for (FormElement row: displayRow.getChildren()) {
				for (FormElement field : row.getChildren()) {
					elements.add(field);
				}
			}
		}		
		return elements;
	}
	
	@Override
	public SortedMap<String,FormElement> getTableFields() {
		return tableFields;
	}

	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {
		String datetime = ParticipantDataUtils.convertToString(new DateTime());
		if (StringUtils.isBlank(values.get(CREATED_ON))) {
			values.put(CREATED_ON, datetime);
		}
		if (StringUtils.isBlank(values.get(TESTED_ON))) {
			values.put(TESTED_ON, datetime);	
		}
		values.put(MODIFIED_ON, datetime);
	}
	
	private FormGroup addRow(String name, String description, List<String> unitEnumeration) {
		FormGroup row = new FormGroup(description);
		row.addField(new FormField(name, description, ParticipantDataColumnType.DOUBLE, false, false));
		row.addField(new EnumeratedFormField(name + UNITS_SUFFIX, "Units", ParticipantDataColumnType.STRING, false,
				true, unitEnumeration));
		row.addField(new FormField(name + RANGE_LOW_SUFFIX, "Low " + description, ParticipantDataColumnType.DOUBLE,
				false, true));
		row.addField(new FormField(name + RANGE_HIGH_SUFFIX, "High " + description, ParticipantDataColumnType.DOUBLE,
				false, true));
		return row;
	}

}
