package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;

import com.google.common.collect.Lists;

public class CompleteBloodCount implements Specification {
	
	private static final String UNITS_SUFFIX = "_units";
	private static final String RANGE_LOW_SUFFIX = "_range_low";
	private static final String RANGE_HIGH_SUFFIX = "_range_high";

	private final List<String> CMM = Lists.newArrayList(Ratios.CELLS_PER_CMM.getAbbrev(), Ratios.CELLS_PER_MICROLITER.getAbbrev());
	private final List<String> GMDL = Lists.newArrayList(Ratios.GRAMS_PER_DECILITER.getAbbrev(), Ratios.MILLIMOLES_PER_LITER.getAbbrev());
	private final List<String> PERC = Lists.newArrayList(Units.PERCENTAGE.getAbbrev());
	private final List<String> FL = Lists.newArrayList(Units.FEMTOLITER.getAbbrev());
	private final List<String> PG = Lists.newArrayList(Units.PICOGRAM.getAbbrev());
	private final List<String> COUNT = Lists.newArrayList("count", "c.v.", "s.d.");	

	List<FormElement> metadata = Lists.newArrayList();
	List<FormElement> displayRows = Lists.newArrayList();

	public CompleteBloodCount() {
		metadata.add( new FormField(CREATED_ON, "", false) );
		metadata.add( new FormField(MODIFIED_ON, "", false) );

		List<FormElement> rows = Lists.newArrayList();
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

	private FormGroup addRow(String name, String description, List<String> unitEnumeration) {
		FormGroup row = new FormGroup(description);
		row.addField(new FormField(name, description, false));
		row.addField(new EnumeratedFormField(name + UNITS_SUFFIX, description + ": units of measurement", true, unitEnumeration));
		row.addField(new FormField(name + RANGE_LOW_SUFFIX, description + ": low end of normal range", true));
		row.addField(new FormField(name + RANGE_HIGH_SUFFIX, description + ": high end of normal range", true));
		return row;
	}

}
