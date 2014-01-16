package org.sagebionetworks.bridge.webapp.specs;

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
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompleteBloodCount implements Specification {

	private static final Logger logger = LogManager.getLogger(CompleteBloodCount.class.getName());

	private static long THOUSAND = 1000L;
	private static long MILLION =  1000000L;
	private static long BILLION =  1000000000L;
	private static long TRILLION = 1000000000000L;
	
	private static final String PLT_CONVERTED = "plt (K/mcL)";
	private static final String WBC_CONVERTED = "wbc (K/mcL)";
	private static final String RBC_CONVERTED = "rbc (M/mcL)";

	private static final String CREATED_ON = "createdOn";
	private static final String MODIFIED_ON = "modifiedOn";
	private static final String COLLECTED_ON = "collectedOn";

	private static final String UNITS_SUFFIX = "_units";
	private static final String RANGE_LOW_SUFFIX = "_range_low";
	private static final String RANGE_HIGH_SUFFIX = "_range_high";

	private static final List<String> GRID_HEADERS = Lists.newArrayList("Value", "Units", "Normal Range");
	
	private final List<String> THOUSANDS = SpecificationUtils.getSymbolsForUnits(Units.THOUSANDS_PER_MICROLITER, Units.BILLIONS_PER_LITER);
	private final List<String> MILLIONS = SpecificationUtils.getSymbolsForUnits(Units.MILLIONS_PER_MICROLITER, Units.TRILLIONS_PER_LITER);
	private final List<String> PERC = Units.PERCENTAGE.getSymbols();
	private final List<String> DL = Units.DECILITER.getSymbols();
	private final List<String> FL = Units.FEMTOLITER.getSymbols();
	private final List<String> PG = Units.PICOGRAM.getSymbols();
	
	private final List<String> COLLECTION_METHODS = Lists.newArrayList("Finger Prick", "IV Central Line", "Central Prick");

	Map<String,FormElement> metadata = Maps.newHashMap();
	List<FormElement> editRows = Lists.newArrayList();
	List<FormElement> showRows = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();

	public CompleteBloodCount() {
		FormFieldBuilder builder = new FormFieldBuilder();
		// These two do not show up on the UI. Even if you submitted an HTTP request with them reset, 
		// they are readonly and thus would not reset.
		FormField field1 = builder.asDateTime().name(CREATED_ON).label("Created on date").readonly().create();
		FormField field2 = builder.asDateTime().name(MODIFIED_ON).label("Modified on date").readonly().create();
		metadata.put( field1.getName(), field1 );
		metadata.put( field2.getName(), field2 );

		FormField convertible1 = builder.asText().name(RBC_CONVERTED).label(RBC_CONVERTED).readonly().create();
		FormField convertible2 = builder.asText().name(WBC_CONVERTED).label(WBC_CONVERTED).readonly().create();
		FormField convertible3 = builder.asText().name(PLT_CONVERTED).label(PLT_CONVERTED).readonly().create();
		metadata.put(convertible1.getName(), convertible1);
		metadata.put(convertible2.getName(), convertible2);
		metadata.put(convertible3.getName(), convertible3);
		
		List<FormElement> rows = Lists.newArrayList();
		
		FormField collectedOn = builder.asDate().name(COLLECTED_ON).label("Collection date").required().create();
		rows.add(collectedOn);
		tableFields.put(COLLECTED_ON, collectedOn);
		
		FormField eff = builder.asEnum(COLLECTION_METHODS).name("draw_type").label("Draw type").defaultable().create();
		rows.add(eff);
		editRows.add( new FormGroup("General information", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addEditRow(MILLIONS, "rbc", "Red cells (Erythrocytes / RBC)") );
		rows.add( addEditRow(DL, "hb", "Hemoglobin (Hb)") );
		rows.add( addEditPercRow(PERC, "hct", "Hematocrit (HCT)") );
		rows.add( addEditRow(FL, "mcv", "Mean corpuscular volume (MCV)") );
		rows.add( addEditRow(PG, "mch", "Mean corpuscular hemoglobin (MCH)") );
		rows.add( addEditPercRow(PERC, "rdw", "RBC distribution width (RDW)") );
		rows.add( addEditPercRow(PERC, "ret", "Reticulocyte count (Ret)") );
		editRows.add( new FormGrid("Red blood cells", rows, GRID_HEADERS) );
		
		rows = Lists.newArrayList();
		rows.add( addEditRow(THOUSANDS, "wbc", "White cells (Leukocytes / WBC)") );
		rows.add( addEditPercRow(PERC, "wbc_diff", "WBC differential (WBC Diff)") );
		rows.add( addEditPercRow(PERC, "neutrophil", "Neutrophil") );
		rows.add( addEditPercRow(PERC, "neutrophil_immature", "Immature Neutrophil (band neutrophil)") );
		rows.add( addEditPercRow(PERC, "lymphocytes", "Lymphocytes") );
		rows.add( addEditPercRow(PERC, "monocytes", "Monocytes") );
		editRows.add( new FormGrid("White blood cells", rows, GRID_HEADERS) );
		
		rows = Lists.newArrayList();
		rows.add( addEditRow(THOUSANDS, "plt", "Platelet count (Thrombocyte / PLT)") );
		rows.add( addEditRow(FL, "mpv", "Mean platelet volume (MPV)") );
		rows.add( addEditPercRow(PERC, "pdw", "Platelet distribution width (PDW)") );
		editRows.add( new FormGrid("Platelets", rows, GRID_HEADERS) );
		
		// EEEK
		rows = Lists.newArrayList();
		rows.add(builder.asValue(new DateStringToDateTimeConverter(), new DateToDateStringConverter()).name(COLLECTED_ON).label("Collection date").create());
		rows.add(builder.asValue().name("draw_type").label("Draw type").defaultable().create());
		showRows.add( new FormGroup("General information", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addShowRow("rbc", "Red cells (Erythrocytes / RBC)") );
		rows.add( addShowRow("hb", "Hemoglobin (Hb)") );
		rows.add( addShowRow("hct", "Hematocrit (HCT)") );
		rows.add( addShowRow("mcv", "Mean corpuscular volume (MCV)") );
		rows.add( addShowRow("mch", "Mean corpuscular hemoglobin (MCH)") );
		rows.add( addShowRow("rdw", "RBC distribution width (RDW)") );
		rows.add( addShowRow("ret", "Reticulocyte count (Ret)") );
		showRows.add( new FormGroup("Red blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addShowRow("wbc", "White cells (Leukocytes / WBC)") );
		rows.add( addShowRow("wbc_diff", "WBC differential (WBC Diff)") );
		rows.add( addShowRow("neutrophil", "Neutrophil") );
		rows.add( addShowRow("neutrophil_immature", "Immature Neutrophil (band neutrophil)") );
		rows.add( addShowRow("lymphocytes", "Lymphocytes") );
		rows.add( addShowRow("monocytes", "Monocytes") );
		showRows.add( new FormGroup("White blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addShowRow("plt", "Platelet count (Thrombocyte / PLT)") );
		rows.add( addShowRow("mpv", "Mean platelet volume (MPV)") );
		rows.add( addShowRow("pdw", "Platelet distribution width (PDW)") );
		showRows.add( new FormGroup("Platelets", rows) );
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
	
	// Currently, not used anywhere.
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
		if (StringUtils.isBlank(values.get(CREATED_ON))) {
			values.put(CREATED_ON, datetime);
		}
		values.put(MODIFIED_ON, datetime);
		
		// Convert values for fields that need standard values, only the researcher sees these columns
		// (there are three)
		//
		// 1. multiply the number by the units per (e.g. billions per *)
		// 2. divide my a million (liters to microliters) - it's a millionth of a liter
		// 3. divide again by the new units per (e.g. K/* is a number in the thousands)

		Units unit = Units.unitFromString( values.get("rbc" + UNITS_SUFFIX) );
		if (unit == Units.TRILLIONS_PER_LITER) {
			double value = (Double.parseDouble(values.get("rbc")) * TRILLION) / MILLION / MILLION;
			values.put(RBC_CONVERTED, Double.toString(value));
		} else {
			values.put(RBC_CONVERTED, values.get("rbc"));
		}
		
		unit = Units.unitFromString( values.get("wbc" + UNITS_SUFFIX) );
		if (unit == Units.BILLIONS_PER_LITER) {
			double value = (Double.parseDouble(values.get("wbc")) * BILLION) / MILLION / THOUSAND;
			values.put(WBC_CONVERTED, Double.toString(value));
		} else {
			values.put(WBC_CONVERTED, values.get("wbc"));
		}

		unit = Units.unitFromString( values.get("plt" + UNITS_SUFFIX) );
		if (unit == Units.BILLIONS_PER_LITER) {
			double value = (Double.parseDouble(values.get("plt")) * BILLION) / MILLION / THOUSAND;
			values.put(PLT_CONVERTED, Double.toString(value));
		} else {
			values.put(PLT_CONVERTED, values.get("plt"));
		}
	}
	
	private FormElement addShowRow(String name, String description) {
		return new RangeNormBar(description, name, name + UNITS_SUFFIX, name + RANGE_LOW_SUFFIX, name + RANGE_HIGH_SUFFIX);
	}
	
	private FormGroup addEditRow(List<String> unitEnumeration, String name, String description) {
		FormGroup row = new FormGroup(UIType.ROW, description);
		
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField field = builder.asDouble().name(name).label(description).create();
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
