package org.sagebionetworks.bridge.webapp.specs;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;

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

	private final List<String> THOUSANDS = SpecificationUtils.getSymbolsForUnits(Units.THOUSANDS_PER_MICROLITER, Units.BILLIONS_PER_LITER);
	private final List<String> MILLIONS = SpecificationUtils.getSymbolsForUnits(Units.MILLIONS_PER_MICROLITER, Units.TRILLIONS_PER_LITER);
	private final List<String> PERC = Units.PERCENTAGE.getSymbols();
	private final List<String> DL = Units.DECILITER.getSymbols();
	private final List<String> FL = Units.FEMTOLITER.getSymbols();
	private final List<String> PG = Units.PICOGRAM.getSymbols();
	
	private final List<String> COLLECTION_METHODS = Lists.newArrayList("Finger Prick", "IV Central Line", "Central Prick");

	List<FormElement> metadata = Lists.newArrayList();
	List<FormElement> displayRows = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();

	public CompleteBloodCount() {
		FormFieldBuilder builder = new FormFieldBuilder();
		// These two do not show up on the UI. Even if you submitted an HTTP request with them reset, 
		// they are readonly and thus would not reset.
		FormField field1 = builder.asDatetime().name(CREATED_ON).label("Created on date").readonly().create();
		FormField field2 = builder.asDatetime().name(MODIFIED_ON).label("Modified on date").readonly().create();
		metadata.add( field1 );
		metadata.add( field2 );
		tableFields.put(TESTED_ON, field1);
		// tableFields.put(MODIFIED_ON, field2);

		List<FormElement> rows = Lists.newArrayList();
		
		FormField dot = builder.asDatetime().name(TESTED_ON).label("Date of test").required().create();
		rows.add(dot);
		
		FormField eff = builder.asEnum(COLLECTION_METHODS).name("draw_type").label("Draw type").defaultable()
				.create();
		rows.add(eff);
		displayRows.add( new FormGroup("General information", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow(MILLIONS, "rbc", "Red cells (Erythrocytes / RBC)") );
		rows.add( addRow(DL, "hb", "Hemoglobin (Hb)") );
		rows.add( addPercentageRow(PERC, "hct", "Hematocrit (HCT)") );
		rows.add( addRow(FL, "mcv", "Mean corpuscular volume (MCV)") );
		rows.add( addRow(PG, "mch", "Mean corpuscular hemoglobin (MCH)") );
		rows.add( addPercentageRow(PERC, "rdw", "RBC distribution width (RDW)") );
		rows.add( addPercentageRow(PERC, "ret", "Reticulocyte count (Ret)") );
		displayRows.add( new FormGroup("Red blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow(THOUSANDS, "wbc", "White cells (Leukocytes / WBC)") );
		rows.add( addPercentageRow(PERC, "wbc_diff", "WBC differential (WBC Diff)") );
		// But these can also be counts...
		rows.add( addPercentageRow(PERC, "neutrophil", "Neutrophil") );
		rows.add( addPercentageRow(PERC, "neutrophil_immature", "Immature Neutrophil (band neutrophil)") );
		rows.add( addPercentageRow(PERC, "lymphocytes", "Lymphocytes") );
		rows.add( addPercentageRow(PERC, "monocytes", "Monocytes") );
		displayRows.add( new FormGroup("White blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow(THOUSANDS, "plt", "Platelet count (Thrombocyte / PLT)") );
		rows.add( addRow(FL, "mpv", "Mean platelet volume (MPV)") );
		rows.add( addPercentageRow(PERC, "pdw", "Platelet distribution width (PDW)") );
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
	public ParticipantDataRepeatType getRepeatType() {
		return ParticipantDataRepeatType.IF_NEW;
	}
	
	@Override
	public String getRepeatFrequency() {
		return null;
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
		/* This shouldn't need conversion as it appears HTML 5 date element
		 * uses ISO Date format
		String testDate = values.get(TESTED_ON);
		if (testDate != null) {
			DateTime testedOn = DateTime.parse(testDate, ISODateTimeFormat.date());
			values.put(TESTED_ON, ParticipantDataUtils.convertToString(testedOn));
		}
		 */
		String datetime = ParticipantDataUtils.convertToString(new DateTime());
		if (StringUtils.isBlank(values.get(CREATED_ON))) {
			values.put(CREATED_ON, datetime);
		}
		values.put(MODIFIED_ON, datetime);
	}
	
	private FormGroup addRow(List<String> unitEnumeration, String name, String description) {
		FormGroup row = new FormGroup(description);
		
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField field = builder.asDouble().name(name).label(description).create();
		row.addField(field);
		
		if (unitEnumeration.size() == 1) {
			FormField units = builder.asString(unitEnumeration.get(0)).name(name + UNITS_SUFFIX).label("Units").readonly().create();
			row.addField(units);
		} else {
			FormField units = builder.asEnum(unitEnumeration).name(name + UNITS_SUFFIX).label("Units").defaultable().create();
			row.addField(units);
		}
		
		FormField low = builder.asDouble().name(name + RANGE_LOW_SUFFIX).label("Low " + description).defaultable()
				.create();
		row.addField(low);

		FormField high = builder.asDouble().name(name + RANGE_HIGH_SUFFIX).label("High " + description).defaultable()
				.create();
		row.addField(high);
		return row;
	}
	
	private FormGroup addPercentageRow(List<String> unitEnumeration, String name, String description) {
		FormGroup row = new FormGroup(description);
		
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField field = builder.asDouble().minValue(0D).maxValue(100D).name(name).label(description).create();
		row.addField(field);
		
		FormField units = builder.asString("%").name(name + UNITS_SUFFIX).label("Units").readonly().create();
		row.addField(units);
		
		FormField low = builder.asDouble().minValue(0D).maxValue(100D).name(name + RANGE_LOW_SUFFIX)
				.label("Low " + description).defaultable().create();
		row.addField(low);

		FormField high = builder.asDouble().minValue(0D).maxValue(100D).name(name + RANGE_HIGH_SUFFIX)
				.label("High " + description).defaultable().create();
		row.addField(high);
		return row;
	}

}
