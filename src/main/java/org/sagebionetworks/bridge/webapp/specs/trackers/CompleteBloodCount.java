package org.sagebionetworks.bridge.webapp.specs.trackers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.units.Units;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.webapp.converter.DateToShortFormatDateStringConverter;
import org.sagebionetworks.bridge.webapp.converter.ISODateConverter;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.FormGroup;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.GridGroup;
import org.sagebionetworks.bridge.webapp.specs.RangeNormBar;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationUtils;
import org.sagebionetworks.bridge.webapp.specs.UIType;
import org.sagebionetworks.bridge.webapp.specs.builder.FormFieldBuilder;
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompleteBloodCount implements Specification {

	private static final Logger logger = LogManager.getLogger(CompleteBloodCount.class.getName());

	private static final String DRAW_TYPE_FIELD = "draw_type";
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
	// private static final String MCH_FIELD = "mch";
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
	//private static final String MCH_LABEL = "Mean corpuscular hemoglobin (MCH)";
	private static final String MCV_LABEL = "Mean corpuscular volume (MCV)";
	private static final String HCT_LABEL = "Hematocrit (HCT)";
	private static final String HB_LABEL = "Hemoglobin (Hb)";
	private static final String RBC_LABEL = "Red cells (Erythrocytes / RBC)";

	private static final List<String> GRID_HEADERS = Lists.newArrayList("Value", "Units", "Normal Range");
	
	private final List<String> THOUSANDS = SpecificationUtils.getLabelsForUnits(Units.THOUSANDS_PER_MICROLITER,
			Units.BILLIONS_PER_LITER);
	private final List<String> MILLIONS = SpecificationUtils.getLabelsForUnits(Units.MILLIONS_PER_MICROLITER,
			Units.TRILLIONS_PER_LITER);
	private final List<String> DL = SpecificationUtils.getLabelsForUnits(Units.DECILITER, Units.GRAMS_PER_LITER);
	
	private final List<String> COLLECTION_METHODS = Lists.newArrayList("Finger Stick", "Central Line",
			"Peripheral Stick (arm or leg)");

	Map<String,FormElement> metadata = Maps.newHashMap();
	List<FormElement> editRows = Lists.newArrayList();
	List<FormElement> showRows = Lists.newArrayList();
	SortedMap<String,FormElement> tableFields = Maps.newTreeMap();
	List<FormElement> allFormElements;

	public CompleteBloodCount() {
		FormFieldBuilder builder = new FormFieldBuilder();
		// These two do not show up on the UI. Even if you submitted an HTTP request with them reset, 
		// they are readonly and thus would not reset.
		FormField field1 = builder.asDateTime().name(CREATED_ON_FIELD).label("Created on date").readonly().create();
		FormField field2 = builder.asDateTime().name(MODIFIED_ON_FIELD).label("Modified on date").readonly().create();
		metadata.put( field1.getName(), field1 );
		metadata.put( field2.getName(), field2 );

		List<FormElement> rows = Lists.newArrayList();
		rows.add(builder.asDate().name(COLLECTED_ON_FIELD).label(COLLECTED_ON_LABEL).required().create());
		
		FormField eff = builder.asEnum(COLLECTION_METHODS).name(DRAW_TYPE_FIELD).label(DRAW_TYPE_LABEL).defaultable().create();
		rows.add(eff);
		editRows.add( new FormGroup(GENERAL_INFORMATION_LABEL, rows) );
		
		rows = Lists.newArrayList();
		rows.add( createEditableLab(MILLIONS, RBC_FIELD, RBC_LABEL) );
		rows.add( createEditableLab(DL, HB_FIELD, HB_LABEL) ); 
		rows.add( createEditablePercLab(Units.PERCENTAGE.getLabels(), HCT_FIELD, HCT_LABEL) );
		rows.add( createEditableLab(Units.FEMTOLITER.getLabels(), MCV_FIELD, MCV_LABEL) );
		//rows.add( addEditRow(PG, MCH_FIELD, MCH_LABEL) );
		rows.add( createEditablePercLab(Units.PERCENTAGE_CV_OR_SD.getLabels(), RDW_FIELD, RDW_LABEL) );
		rows.add( createEditablePercLab(Units.PERCENTAGE.getLabels(), RET_FIELD, RET_LABEL) ); // or 10e9/L
		editRows.add( new GridGroup(RED_BLOOD_CELLS_LABEL, rows, GRID_HEADERS) );
		
		rows = Lists.newArrayList();
		rows.add( createEditableLab(THOUSANDS, WBC_FIELD, WBC_LABEL) );
		rows.add( createEditableLab(THOUSANDS, NEUTROPHIL_FIELD, NEUTROPHIL_LABEL) );
		rows.add( createEditableLab(THOUSANDS, NEUTROPHIL_IMMATURE_FIELD, NEUTROPHIL_IMMATURE_LABEL) );
		rows.add( createEditableLab(THOUSANDS, LYMPHOCYTES_FIELD, LYMPHOCYTES_LABEL) );
		rows.add( createEditableLab(THOUSANDS, MONOCYTES_FIELD, MONOCYTES_LABEL) );
		
		// I just don't see consistency in the lab reports, and think we need to have both.
        rows.add( createEditablePercLab(Units.PERCENTAGE.getLabels(), NEUTROPHIL_PERC_FIELD, NEUTROPHIL_PERC_LABEL) );
        rows.add( createEditablePercLab(Units.PERCENTAGE.getLabels(), NEUTROPHIL_IMMATURE_PERC_FIELD, IMMATURE_NEUTROPHIL_PERC_LABEL) );
        rows.add( createEditablePercLab(Units.PERCENTAGE.getLabels(), LYMPHOCYTES_PERC_FIELD, LYMPHOCYTES_PERC_LABEL) );
        rows.add( createEditablePercLab(Units.PERCENTAGE.getLabels(), MONOCYTES_PERC_FIELD, MONOCYTES_PERC_LABEL) );		
		
		editRows.add( new GridGroup(WHITE_BLOOD_CELLS_LABEL, rows, GRID_HEADERS) );
		
		rows = Lists.newArrayList();
		rows.add( createEditableLab(THOUSANDS, PLT_FIELD, PLT_LABEL) );
		rows.add( createEditableLab(Units.FEMTOLITER.getLabels(), MPV_FIELD, MPV_LABEL) );
		editRows.add( new GridGroup(PLATELETS_LABEL, rows, GRID_HEADERS) );
		
		// Show-only view
		rows = Lists.newArrayList();
		FormField collectedOn = builder
				.asValue(ISODateConverter.INSTANCE, DateToShortFormatDateStringConverter.INSTANCE)
				.name(COLLECTED_ON_FIELD).label(COLLECTED_ON_LABEL).create();
		rows.add(collectedOn);
		rows.add(builder.asValue().name(DRAW_TYPE_FIELD).label(DRAW_TYPE_LABEL).defaultable().create());
		showRows.add( new FormGroup(GENERAL_INFORMATION_LABEL, rows) );
		tableFields.put(COLLECTED_ON_FIELD, collectedOn);		

		rows = Lists.newArrayList();
		rows.add( createViewableLab(RBC_FIELD, RBC_LABEL) );
		rows.add( createViewableLab(HB_FIELD, HB_LABEL) );
		rows.add( createViewableLab(HCT_FIELD, HCT_LABEL) );
		rows.add( createViewableLab(MCV_FIELD, MCV_LABEL) );
		//rows.add( addShowRow(MCH_FIELD, MCH_LABEL) );
		rows.add( createViewableLab(RDW_FIELD, RDW_LABEL) );
		rows.add( createViewableLab(RET_FIELD, RET_LABEL) );
		showRows.add( new FormGroup(RED_BLOOD_CELLS_LABEL, rows) );
		
		rows = Lists.newArrayList();
		rows.add( createViewableLab(WBC_FIELD, WBC_LABEL) );
		rows.add( createViewableLab(NEUTROPHIL_FIELD, NEUTROPHIL_LABEL) );
		rows.add( createViewableLab(NEUTROPHIL_IMMATURE_FIELD, NEUTROPHIL_IMMATURE_LABEL) );
		rows.add( createViewableLab(LYMPHOCYTES_FIELD, LYMPHOCYTES_LABEL) );
		rows.add( createViewableLab(MONOCYTES_FIELD, MONOCYTES_LABEL) );
		showRows.add( new FormGroup(WHITE_BLOOD_CELLS_LABEL, rows) );
		
		rows = Lists.newArrayList();
		rows.add( createViewableLab(PLT_FIELD, PLT_LABEL) );
		rows.add( createViewableLab(MPV_FIELD, MPV_LABEL) );
		showRows.add( new FormGroup(PLATELETS_LABEL, rows) );
		
		allFormElements = SpecificationUtils.toList(editRows, metadata.values());
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
	public String getDatetimeStartColumnName() {
		return COLLECTED_ON_FIELD;
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
		return new FormGroup(UIType.LIST, "CBC", showRows);
	}
	
	@Override
	public List<FormElement> getAllFormElements() {
		return allFormElements;
	}
	
	@Override
	public SortedMap<String,FormElement> getTableFields() {
		return tableFields;
	}
	
	@Override
	public void postProcessParticipantDataRows(ModelMap map, List<ParticipantDataRow> rows) {
		Collections.sort(rows, new ParticipantDataDatetimeComparator(COLLECTED_ON_FIELD));
	}
	
	@Override
	public void setSystemSpecifiedValues(Map<String, String> values) {
		String datetime = ISODateTimeFormat.dateTime().print(new DateTime());
		if (StringUtils.isBlank(values.get(CREATED_ON_FIELD))) {
			values.put(CREATED_ON_FIELD, datetime);
		}
		values.put(MODIFIED_ON_FIELD, datetime);
	}
	
	private FormElement createViewableLab(String name, String description) {
		return new RangeNormBar(description, name, ValueTranslator.LABRESULT_VALUE, ValueTranslator.LABRESULT_UNITS,
				ValueTranslator.LABRESULT_MIN_NORMAL_VALUE, ValueTranslator.LABRESULT_MAX_NORMAL_VALUE);
	}
	
	private FormField createEditableLab(List<String> unitEnumeration, String name, String description) {
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField labField = builder.asLab().name(name).label(description).create();
		
		FormField field = builder.asDouble().minValue(0D).name(name + ValueTranslator.LABRESULT_VALUE)
				.label(description).compoundField().create();
		labField.getChildren().add(field);
		
		if (unitEnumeration.size() == 1) {
			FormField units = builder.asText(unitEnumeration.get(0)).name(name + ValueTranslator.LABRESULT_UNITS)
					.label("Units").readonly().compoundField().create();
			labField.getChildren().add(units);
		} else {
			FormField units = builder.asEnum(unitEnumeration).name(name + ValueTranslator.LABRESULT_UNITS)
					.label("Units").defaultable().compoundField().create();
			labField.getChildren().add(units);
		}
		
		FormField low = builder.asDouble().name(name + ValueTranslator.LABRESULT_MIN_NORMAL_VALUE)
				.label("Low " + description).defaultable().compoundField().create();

		FormField high = builder.asDouble().name(name + ValueTranslator.LABRESULT_MAX_NORMAL_VALUE)
				.label("High " + description).defaultable().compoundField().create();
		FormGroup range = new FormGroup(UIType.RANGE, "Range");
		range.addField(low);
		range.addField(high);
		labField.getChildren().add(range);
		
		return labField;
	}
	
	private FormField createEditablePercLab(List<String> unitEnumeration, String name, String description) {
		FormFieldBuilder builder = new FormFieldBuilder();
		
		FormField labField = builder.asLab().name(name).label(description).create();
		
		FormField field = builder.asDouble().minValue(0D).maxValue(100D).name(name + ValueTranslator.LABRESULT_VALUE)
				.label(description).compoundField().create();
		labField.getChildren().add(field);
		
		FormField units = builder.asText("%").name(name + ValueTranslator.LABRESULT_UNITS).label("Units").readonly().compoundField().create();
		labField.getChildren().add(units);
		
		FormField low = builder.asDouble().minValue(0D).maxValue(100D)
				.name(name + ValueTranslator.LABRESULT_MIN_NORMAL_VALUE).label("Low " + description).defaultable()
				.compoundField().create();

		FormField high = builder.asDouble().minValue(0D).maxValue(100D)
				.name(name + ValueTranslator.LABRESULT_MAX_NORMAL_VALUE).label("High " + description).defaultable()
				.compoundField().create();
		
		FormGroup range = new FormGroup(UIType.RANGE, "Range");
		range.addField(low);
		range.addField(high);
		labField.getChildren().add(range);

		return labField;
	}

}
