package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompleteBloodCount implements Specification {
	
	private static final Logger logger = LogManager.getLogger(CompleteBloodCount.class.getName());

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
		FormFieldBuilder builder = new FormFieldBuilder();
		// These two do not show up on the UI. Even if you submitted an HTTP request with them reset, 
		// they are readonly and thus would not reset.
		FormField field1 = builder.forField().withName(CREATED_ON).withLabel("Created on date").asReadonly().asDatetime().create();
		FormField field2 = builder.forField().withName(MODIFIED_ON).withLabel("Modified on date").asReadonly().asDatetime().create();
		metadata.add( field1 );
		metadata.add( field2 );
		tableFields.put(CREATED_ON, field1);
		tableFields.put(MODIFIED_ON, field2);

		List<FormElement> rows = Lists.newArrayList();
		
		FormField dot = builder.forField().withName(TESTED_ON).withLabel("Date of test").asDatetime().asRequired().create();
		rows.add(dot);
		
		FormField eff = builder.forEnumeratedField().withName("draw_type").withLabel("Draw type").asString()
				.asDefaultable().withEnumeration(COLLECTION_METHODS).create();
		rows.add(eff);
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
		List<FormElement> list = SpecificationUtils.toList(displayRows);
		for (FormElement field : metadata) {
			list.add(field);
		}
		return list;
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
		
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField field = builder.forField().withName(name).withLabel(description).asDouble().create();
		row.addField(field);
		
		if (unitEnumeration == PERC) {
			FormField units = builder.forField().withName(name + UNITS_SUFFIX).withLabel("Units").asString()
					.withInitialValue("%").asReadonly().create();
			row.addField(units);
			
			FormField low = builder.forField().withName(name + RANGE_LOW_SUFFIX).withLabel("Low " + description).asDouble()
					.withInitialValue("0").asReadonly().asDefaultable().create();
			row.addField(low);

			FormField high = builder.forField().withName(name + RANGE_HIGH_SUFFIX).withLabel("High " + description)
					.withInitialValue("100").asReadonly().asDouble().asDefaultable().create();
			row.addField(high);
		} else {
			FormField units = builder.forEnumeratedField().withName(name + UNITS_SUFFIX).withLabel("Units").asString()
					.asDefaultable().withEnumeration(unitEnumeration).create();
			row.addField(units);
			
			FormField low = builder.forField().withName(name + RANGE_LOW_SUFFIX).withLabel("Low " + description).asDouble()
					.asDefaultable().create();
			row.addField(low);

			FormField high = builder.forField().withName(name + RANGE_HIGH_SUFFIX).withLabel("High " + description)
					.asDouble().asDefaultable().create();
			row.addField(high);
		}
		return row;
	}

}
