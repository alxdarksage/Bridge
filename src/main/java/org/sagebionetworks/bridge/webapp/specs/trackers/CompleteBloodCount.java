package org.sagebionetworks.bridge.webapp.specs.trackers;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.webapp.converters.DateStringToDateTimeConverter;
import org.sagebionetworks.bridge.webapp.converters.DateToDateStringConverter;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.FormGrid;
import org.sagebionetworks.bridge.webapp.specs.FormGroup;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.RangeNormBar;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationUtils;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.sagebionetworks.bridge.webapp.specs.Units;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompleteBloodCount implements Specification {

	private static final Logger logger = LogManager.getLogger(CompleteBloodCount.class.getName());

	private static final String DRAW_TYPE_FIELD = "draw_type";
	private static final String PLT_CONVERTED_FIELD = "plt (K/mcL)";
	private static final String WBC_CONVERTED_FIELD = "wbc (K/mcL)";
	private static final String RBC_CONVERTED_FIELD = "rbc (M/mcL)";
	private static final String CREATED_ON_FIELD = "created_on";
	private static final String MODIFIED_ON_FIELD = "modified_on";
	private static final String COLLECTED_ON_FIELD = "collected_on";
	private static final String MPV_FIELD = "mpv";
	private static final String PLT_FIELD = "plt";
	private static final String NEUTROPHIL_FIELD = "neutrophil";
	private static final String NEUTROPHIL_IMMATURE_FIELD = "neutrophil_immature";
	private static final String LYMPHOCYTES_FIELD = "lymphocytes";
	private static final String MONOCYTES_FIELD = "monocytes";
	private static final String NEUTROPHIL_PERC_FIELD = "neutrophil_perc";
	private static final String NEUTROPHIL_IMMATURE_PERC_FIELD = "neutrophil_immature_perc";
	private static final String LYMPHOCYTES_PERC_FIELD = "lymphocytes_perc";
	private static final String MONOCYTES_PERC_FIELD = "monocytes_perc";
	private static final String WBC_FIELD = "wbc";
	private static final String RET_FIELD = "ret";
	private static final String RDW_FIELD = "rdw";
	private static final String MCH_FIELD = "mch";
	private static final String MCV_FIELD = "mcv";
	private static final String HCT_FIELD = "hct";
	private static final String HB_FIELD = "hb";
	private static final String RBC_FIELD = "rbc";
	
	private static final String PLATELETS_LABEL = "Platelets";
	private static final String WHITE_BLOOD_CELLS_LABEL = "White blood cells";
	private static final String RED_BLOOD_CELLS_LABEL = "Red blood cells";
	private static final String GENERAL_INFORMATION_LABEL = "General information";

	private static final String COLLECTED_ON_LABEL = "Collection date";
	private static final String DRAW_TYPE_LABEL = "Draw type";
	private static final String MPV_LABEL = "Mean platelet volume (MPV)";
	private static final String PLT_LABEL = "Platelet count (Thrombocyte / PLT)";
	private static final String NEUTROPHIL_LABEL = "Neutrophil count";
	private static final String NEUTROPHIL_IMMATURE_LABEL = "Immature neutrophil, segments, or granulocytes (bands) count";
	private static final String LYMPHOCYTES_LABEL = "Lymphocytes count";
	private static final String MONOCYTES_LABEL = "Monocytes count";
	private static final String NEUTROPHIL_PERC_LABEL = "Neutrophil";
	private static final String IMMATURE_NEUTROPHIL_PERC_LABEL = "Immature neutrophil, segments, or granulocytes (bands)";
	private static final String LYMPHOCYTES_PERC_LABEL = "Lymphocytes";
	private static final String MONOCYTES_PERC_LABEL = "Monocytes";
	private static final String WBC_LABEL = "White cells (Leukocytes / WBC)";
	private static final String RET_LABEL = "Reticulocyte count (Ret)";
	private static final String RDW_LABEL = "RBC distribution width (RDW)";
	private static final String MCH_LABEL = "Mean corpuscular hemoglobin (MCH)";
	private static final String MCV_LABEL = "Mean corpuscular volume (MCV)";
	private static final String HCT_LABEL = "Hematocrit (HCT)";
	private static final String HB_LABEL = "Hemoglobin (Hb)";
	private static final String RBC_LABEL = "Red cells (Erythrocytes / RBC)";
	
	private static final String UNITS_SUFFIX = "_units";
	private static final String RANGE_LOW_SUFFIX = "_range_low";
	private static final String RANGE_HIGH_SUFFIX = "_range_high";

	private static final List<String> GRID_HEADERS = Lists.newArrayList("Value", "Units", "Normal Range");

	private static long THOUSAND = 1000L;
	private static long MILLION =  1000000L;
	private static long BILLION =  1000000000L;
	private static long TRILLION = 1000000000000L;
	
	private final List<String> THOUSANDS = SpecificationUtils.getSymbolsForUnits(Units.THOUSANDS_PER_MICROLITER, Units.BILLIONS_PER_LITER);
	private final List<String> MILLIONS = SpecificationUtils.getSymbolsForUnits(Units.MILLIONS_PER_MICROLITER, Units.TRILLIONS_PER_LITER);
	private final List<String> PERC = Units.PERCENTAGE.getSymbols();
	private final List<String> DL = Units.DECILITER.getSymbols();
	private final List<String> FL = Units.FEMTOLITER.getSymbols();
	private final List<String> PG = Units.PICOGRAM.getSymbols();
	
	private final List<String> COLLECTION_METHODS = Lists.newArrayList("Finger Stick", "Central Line", "Peripheral Stick (arm or leg)");

	Map<String,FormElement> metadata = Maps.newHashMap();
	List<FormElement> editRows = Lists.newArrayList();
	List<FormElement> showRows = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();

	public CompleteBloodCount() {
		FormFieldBuilder builder = new FormFieldBuilder();
		// These two do not show up on the UI. Even if you submitted an HTTP request with them reset, 
		// they are readonly and thus would not reset.
		FormField field1 = builder.asDateTime().name(CREATED_ON_FIELD).label("Created on date").readonly().create();
		FormField field2 = builder.asDateTime().name(MODIFIED_ON_FIELD).label("Modified on date").readonly().create();
		metadata.put( field1.getName(), field1 );
		metadata.put( field2.getName(), field2 );

		FormField convertible1 = builder.asText().name(RBC_CONVERTED_FIELD).label(RBC_CONVERTED_FIELD).readonly().create();
		FormField convertible2 = builder.asText().name(WBC_CONVERTED_FIELD).label(WBC_CONVERTED_FIELD).readonly().create();
		FormField convertible3 = builder.asText().name(PLT_CONVERTED_FIELD).label(PLT_CONVERTED_FIELD).readonly().create();
		metadata.put(convertible1.getName(), convertible1);
		metadata.put(convertible2.getName(), convertible2);
		metadata.put(convertible3.getName(), convertible3);
		
		List<FormElement> rows = Lists.newArrayList();
		
		FormField collectedOn = builder.asDate().name(COLLECTED_ON_FIELD).label(COLLECTED_ON_LABEL).required().create();
		rows.add(collectedOn);
		tableFields.put(COLLECTED_ON_FIELD, collectedOn);
		
		FormField eff = builder.asEnum(COLLECTION_METHODS).name(DRAW_TYPE_FIELD).label(DRAW_TYPE_LABEL).defaultable().create();
		rows.add(eff);
		editRows.add( new FormGroup(GENERAL_INFORMATION_LABEL, rows) );
		
		rows = Lists.newArrayList();
		rows.add( addEditRow(MILLIONS, RBC_FIELD, RBC_LABEL) );
		rows.add( addEditRow(DL, HB_FIELD, HB_LABEL) ); // Canadian form was given in g/L, this will have to change
		rows.add( addEditPercRow(PERC, HCT_FIELD, HCT_LABEL) ); // Canadian form: L/L
		rows.add( addEditRow(FL, MCV_FIELD, MCV_LABEL) );
		rows.add( addEditRow(PG, MCH_FIELD, MCH_LABEL) );
		rows.add( addEditPercRow(PERC, RDW_FIELD, RDW_LABEL) );
		rows.add( addEditPercRow(PERC, RET_FIELD, RET_LABEL) ); // or 10e9/L
		editRows.add( new FormGrid(RED_BLOOD_CELLS_LABEL, rows, GRID_HEADERS) );
		
		rows = Lists.newArrayList();
		rows.add( addEditRow(THOUSANDS, WBC_FIELD, WBC_LABEL) );
		rows.add( addEditRow(THOUSANDS, NEUTROPHIL_FIELD, NEUTROPHIL_LABEL) );
		rows.add( addEditRow(THOUSANDS, NEUTROPHIL_IMMATURE_FIELD, NEUTROPHIL_IMMATURE_LABEL) );
		rows.add( addEditRow(THOUSANDS, LYMPHOCYTES_FIELD, LYMPHOCYTES_LABEL) );
		rows.add( addEditRow(THOUSANDS, MONOCYTES_FIELD, MONOCYTES_LABEL) );
		
		// I just don't see consistency in the lab reports, and think we need to have both.
        rows.add( addEditPercRow(PERC, NEUTROPHIL_PERC_FIELD, NEUTROPHIL_PERC_LABEL) );
        rows.add( addEditPercRow(PERC, NEUTROPHIL_IMMATURE_PERC_FIELD, IMMATURE_NEUTROPHIL_PERC_LABEL) );
        rows.add( addEditPercRow(PERC, LYMPHOCYTES_PERC_FIELD, LYMPHOCYTES_PERC_LABEL) );
        rows.add( addEditPercRow(PERC, MONOCYTES_PERC_FIELD, MONOCYTES_PERC_LABEL) );		
		
		editRows.add( new FormGrid(WHITE_BLOOD_CELLS_LABEL, rows, GRID_HEADERS) );
		
		rows = Lists.newArrayList();
		rows.add( addEditRow(THOUSANDS, PLT_FIELD, PLT_LABEL) );
		rows.add( addEditRow(FL, MPV_FIELD, MPV_LABEL) );
		editRows.add( new FormGrid(PLATELETS_LABEL, rows, GRID_HEADERS) );
		
		// Show-only view
		rows = Lists.newArrayList();
		rows.add(builder.asValue(new DateStringToDateTimeConverter(), new DateToDateStringConverter()).name(COLLECTED_ON_FIELD).label(COLLECTED_ON_LABEL).create());
		rows.add(builder.asValue().name(DRAW_TYPE_FIELD).label(DRAW_TYPE_LABEL).defaultable().create());
		showRows.add( new FormGroup(GENERAL_INFORMATION_LABEL, rows) );

		rows = Lists.newArrayList();
		rows.add( addShowRow(RBC_FIELD, RBC_LABEL) );
		rows.add( addShowRow(HB_FIELD, HB_LABEL) );
		rows.add( addShowRow(HCT_FIELD, HCT_LABEL) );
		rows.add( addShowRow(MCV_FIELD, MCV_LABEL) );
		rows.add( addShowRow(MCH_FIELD, MCH_LABEL) );
		rows.add( addShowRow(RDW_FIELD, RDW_LABEL) );
		rows.add( addShowRow(RET_FIELD, RET_LABEL) );
		showRows.add( new FormGroup(RED_BLOOD_CELLS_LABEL, rows) );
		
		rows = Lists.newArrayList();
		rows.add( addShowRow(WBC_FIELD, WBC_LABEL) );
		rows.add( addShowRow(NEUTROPHIL_FIELD, NEUTROPHIL_LABEL) );
		rows.add( addShowRow(NEUTROPHIL_IMMATURE_FIELD, NEUTROPHIL_IMMATURE_LABEL) );
		rows.add( addShowRow(LYMPHOCYTES_FIELD, LYMPHOCYTES_LABEL) );
		rows.add( addShowRow(MONOCYTES_FIELD, MONOCYTES_LABEL) );
		showRows.add( new FormGroup(WHITE_BLOOD_CELLS_LABEL, rows) );
		
		rows = Lists.newArrayList();
		rows.add( addShowRow(PLT_FIELD, PLT_LABEL) );
		rows.add( addShowRow(MPV_FIELD, MPV_LABEL) );
		showRows.add( new FormGroup(PLATELETS_LABEL, rows) );
	}

	@Override
	public String getName() {
		return "Complete Blood Count Tracker";
	}
	
	@Override
	public String getDescription() {
		return "Complete Blood Count Tracker";
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
	public FormElement getEditStructure() {
		return new FormGroup(UIType.LIST, "CBC", editRows);
	}
	
	@Override
	public FormElement getShowStructure() {
		return new FormGroup(UIType.LIST, "CBC",showRows);
	}
	
	@Override
	public List<FormElement> getAllFormElements() {
		List<FormElement> list = SpecificationUtils.toList(editRows);
		for (FormElement field : metadata.values()) {
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
		String datetime = ISODateTimeFormat.dateTime().print(new DateTime());
		if (StringUtils.isBlank(values.get(CREATED_ON_FIELD))) {
			values.put(CREATED_ON_FIELD, datetime);
		}
		values.put(MODIFIED_ON_FIELD, datetime);
		
		// Convert values for fields that need standard values, only the researcher sees these columns
		// (there are three)
		//
		// 1. multiply the number by the units per (e.g. billions per *)
		// 2. divide my a million (liters to microliters) - it's a millionth of a liter
		// 3. divide again by the new units per (e.g. K/* is a number in the thousands)

		Units unit = Units.unitFromString( values.get(RBC_FIELD + UNITS_SUFFIX) );
		if (unit == Units.TRILLIONS_PER_LITER) {
			double value = (Double.parseDouble(values.get(RBC_FIELD)) * TRILLION) / MILLION / MILLION;
			values.put(RBC_CONVERTED_FIELD, Double.toString(value));
		} else {
			values.put(RBC_CONVERTED_FIELD, values.get(RBC_FIELD));
		}
		
		unit = Units.unitFromString( values.get(WBC_FIELD + UNITS_SUFFIX) );
		if (unit == Units.BILLIONS_PER_LITER) {
			double value = (Double.parseDouble(values.get(WBC_FIELD)) * BILLION) / MILLION / THOUSAND;
			values.put(WBC_CONVERTED_FIELD, Double.toString(value));
		} else {
			values.put(WBC_CONVERTED_FIELD, values.get(WBC_FIELD));
		}

		unit = Units.unitFromString( values.get(PLT_FIELD + UNITS_SUFFIX) );
		if (unit == Units.BILLIONS_PER_LITER) {
			double value = (Double.parseDouble(values.get(PLT_FIELD)) * BILLION) / MILLION / THOUSAND;
			values.put(PLT_CONVERTED_FIELD, Double.toString(value));
		} else {
			values.put(PLT_CONVERTED_FIELD, values.get(PLT_FIELD));
		}
	}
	
	private FormElement addShowRow(String name, String description) {
		return new RangeNormBar(description, name, name + UNITS_SUFFIX, name + RANGE_LOW_SUFFIX, name + RANGE_HIGH_SUFFIX);
	}
	
	private FormGroup addEditRow(List<String> unitEnumeration, String name, String description) {
		FormGroup row = new FormGroup(UIType.ROW, description);
		
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField field = builder.asDouble().minValue(0D).name(name).label(description).create();
		row.addField(field);
		
		if (unitEnumeration.size() == 1) {
			FormField units = builder.asText(unitEnumeration.get(0)).name(name + UNITS_SUFFIX).label("Units").readonly().create();
			row.addField(units);
		} else {
			FormField units = builder.asEnum(unitEnumeration).name(name + UNITS_SUFFIX).label("Units").defaultable().create();
			row.addField(units);
		}
		
		FormField low = builder.asDouble().name(name + RANGE_LOW_SUFFIX).label("Low " + description).defaultable()
				.create();

		FormField high = builder.asDouble().name(name + RANGE_HIGH_SUFFIX).label("High " + description).defaultable()
				.create();
		FormGroup range = new FormGroup(UIType.RANGE, "Range");
		range.addField(low);
		range.addField(high);
		row.addField(range);
		return row;
	}
	
	private FormGroup addEditPercRow(List<String> unitEnumeration, String name, String description) {
		FormGroup row = new FormGroup(UIType.ROW, description);
		
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField field = builder.asDouble().minValue(0D).maxValue(100D).name(name).label(description).create();
		row.addField(field);
		
		FormField units = builder.asText("%").name(name + UNITS_SUFFIX).label("Units").readonly().create();
		row.addField(units);
		
		FormField low = builder.asDouble().minValue(0D).maxValue(100D).name(name + RANGE_LOW_SUFFIX)
				.label("Low " + description).defaultable().create();

		FormField high = builder.asDouble().minValue(0D).maxValue(100D).name(name + RANGE_HIGH_SUFFIX)
				.label("High " + description).defaultable().create();
		
		FormGroup range = new FormGroup(UIType.RANGE, "Range");
		range.addField(low);
		range.addField(high);
		row.addField(range);
		return row;
	}

}
