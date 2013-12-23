package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompleteBloodCount implements Specification {

	
	private static final Logger logger = LogManager.getLogger(CompleteBloodCount.class.getName());
	
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	private static final String UNITS_SUFFIX = "_units";
	private static final String RANGE_LOW_SUFFIX = "_range_low";
	private static final String RANGE_HIGH_SUFFIX = "_range_high";
	
	public class CBCRow {
		private final String label;
		private  final String valueField;
		private final String unitField;
		private final String lowRangeField;
		private final String highRangeField;
		private final List<String> unitEnumeration;
		
		// TODO: Why this *and* the RowObject?
		public CBCRow(String label, String valueField, List<String> unitEnumeration) {
			this.label = label;
			this.valueField = valueField;
			this.unitField = valueField + UNITS_SUFFIX;
			this.unitEnumeration = unitEnumeration;
			this.lowRangeField = valueField + RANGE_LOW_SUFFIX; 
			this.highRangeField = valueField + RANGE_HIGH_SUFFIX;
		}

		public String getLabel() { return label; }
		public String getValueField() { return valueField; }
		public String getUnitField() { return unitField; }
		public String getLowRangeField() { return lowRangeField; }
		public String getHighRangeField() { return highRangeField; }
		public List<String> getUnitEnumeration() { return unitEnumeration; }
	}

	// TODO: Actually do these measure by measure because they seem to vary a lot. This is sort of setting
	// us up to use consistent abbreviations to figure out the measures for conversion purposes.
	private final List<String> CMM = Lists.newArrayList(Ratios.CELLS_PER_CMM.getAbbrev(), Ratios.CELLS_PER_MICROLITER.getAbbrev());
	private final List<String> GMDL = Lists.newArrayList(Ratios.GRAMS_PER_DECILITER.getAbbrev(), Ratios.MILLIMOLES_PER_LITER.getAbbrev());
	private final List<String> PERC = Lists.newArrayList(Units.PERCENTAGE.getAbbrev());
	private final List<String> FL = Lists.newArrayList(Units.FEMTOLITER.getAbbrev());
	private final List<String> PG = Lists.newArrayList(Units.PICOGRAM.getAbbrev());
	private final List<String> COUNT = Lists.newArrayList("count", "c.v.", "s.d.");

	private List<String> metadata = Lists.newArrayList(CREATED_ON, MODIFIED_ON);
	private List<Map<String,String>> rows = Lists.newArrayList();
	private Map<String, List<CBCRow>> displayRows = Maps.newLinkedHashMap(); // key order is important
	
	public CompleteBloodCount() {
		List<CBCRow> uiRow = Lists.newArrayList();
		uiRow.add( addField("rbc", "Red cells (Erythrocytes)", CMM) );
		uiRow.add( addField("hb", "Hemoglobin", GMDL) );
		uiRow.add( addField("hct", "Hematocrit", PERC) );
		uiRow.add( addField("mcv", "Mean corpuscular volume", FL) );
		uiRow.add( addField("mch", "Mean corpuscular hemoglobin", PG) );
		// uiRow.add( addField("mchc", "Mean corpuscular hemoglobin concentration", PERC) );
		uiRow.add( addField("rdw", "RBC distribution width", COUNT) );
		uiRow.add( addField("ret", "Reticulocyte count", COUNT) );
		displayRows.put("Red blood cells", uiRow);
		
		uiRow = Lists.newArrayList();
		uiRow.add( addField("wbc", "White cells (Leukocytes)", CMM) );
		uiRow.add( addField("wbc_diff", "WBC differential (Diff)", PERC) );
		// But these can also be counts...
		uiRow.add( addField("neutrophil", "Neutrophil", PERC) );
		uiRow.add( addField("neutrophil_immature", "Immature Neutrophil (band neutrophil)", PERC) );
		uiRow.add( addField("lymphocytes", "Lymphocytes", PERC) );
		uiRow.add( addField("monocytes", "Monocytes", PERC) );
		// uiRow.add( addField("eosinophil", "Eosinophil", CMM) );
		// uiRow.add( addField("basophil", "Basophil", CMM) );
		displayRows.put("White blood cells", uiRow);
		
		uiRow = Lists.newArrayList();
		uiRow.add( addField("plt", "Platelet count (Thrombocyte)", CMM) );
		uiRow.add( addField("mpv", "Mean platelet volume (MPV)", FL) );
		uiRow.add( addField("pdw", "Platelet distribution width", PERC) );
		displayRows.put("Platelets", uiRow);
	}
	
	@Override
	public FormLayout getFormLayout() {
		return FormLayout.GRID;
	}

	@Override
	public List<FormElement> getFormElements() {
		return null;
	}
	
	public Map<String, List<CBCRow>> getDisplayRows() {
		return displayRows;
	}

	@Override
	public ParticipantDataDescriptor getDescriptor() {
		ParticipantDataDescriptor descriptor = new ParticipantDataDescriptor();
		descriptor.setName("CBC");
		descriptor.setDescription("Complete Blood Count");
		return descriptor;
	}

	@Override
	public List<ParticipantDataColumnDescriptor> getColumnDescriptors(ParticipantDataDescriptor descriptor) {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList();
		for (String meta : metadata) {
			ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
			column.setName(meta);
			column.setColumnType(ParticipantDataColumnType.STRING); // datetime stamp
			column.setParticipantDataDescriptorId(descriptor.getId());
			list.add(column);
		}
		for (Map<String,String> row : rows) {
			for (String name : row.keySet()) {
				String description = row.get(name);
				ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
				column.setName(name);
				column.setDescription(description);
				column.setColumnType(ParticipantDataColumnType.DOUBLE); // All of them?!?
				column.setParticipantDataDescriptorId(descriptor.getId());
				list.add(column);
			}
		}
		return list;
	}

	@Override
	public RowSet getRowSetForCreate(Map<String, String> values) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForCreate() requires values map");
		}
		Row row = new Row();
		List<String> newValues = Lists.newArrayList();
		for (String header : getFieldNames()) {
			if (header.equals(CREATED_ON) || header.equals(MODIFIED_ON)) {
				newValues.add( convertToString(header, StringUtils.EMPTY	) ); // anything but null
			} else {
				newValues.add( convertToString(header, values.get(header)) );	
			}
		}
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(getFieldNames());
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
	/**
	 * Does this need to be public?
	 */
	@Override
	public List<String> getFieldNames() {
		List<String> rowNames = Lists.newArrayList(metadata);
		for (Map<String,String> map : rows) {
			rowNames.addAll(map.keySet());
		}
		return rowNames;
	}

	@Override
	public RowSet getRowSetForUpdate(Map<String, String> values, RowSet rowSet, long rowId) {
		if (values == null) {
			throw new IllegalArgumentException("getRowSetForUpdate() requires values");
		}
		Row row = ClientUtils.getRowById(rowSet, rowId);
		List<String> newValues = Lists.newArrayList();
		for (String header : getFieldNames()) {
			if (header.equals(CREATED_ON)) {
				newValues.add( getValueInRow(row, rowSet.getHeaders(), CREATED_ON) ); // passthrough value, immutable
			} else if (header.equals(MODIFIED_ON)) {
				newValues.add( convertToString(header, StringUtils.EMPTY) ); // anything but null
			} else {
				newValues.add( convertToString(header, values.get(header)) );	
			}
		}
		row.setValues(newValues);
		RowSet data = new RowSet();
		data.setHeaders(getFieldNames());
		data.setRows(Lists.newArrayList(row));
		return data;
	}
	
	@Override
	public Object convertToObject(String header, String value) {
		if (StringUtils.isNotBlank(value) && !"null".equals(value)) {
			if (CREATED_ON.equals(header) || MODIFIED_ON.equals(header)) {
				return DateTime.parse(value, ISODateTimeFormat.dateTime()).toDate();
			}
		}
		return value;
	}
	
	@Override
	public String convertToString(String header, Object object) {
		if (object != null) {
			if (header.equals(CREATED_ON) || header.equals(MODIFIED_ON)) {
				DateTime date = new DateTime();
				return date.toString(ISODateTimeFormat.dateTime());
			} else {
				return (String)object;
			}
		}
		return "";	
	}	
	
	private CBCRow addField(String name, String description, List<String> unitEnumeration) {
		Map<String,String> row = Maps.newLinkedHashMap(); // order is important
		row.put(name, description);
		row.put(name + UNITS_SUFFIX, description + ": units of measurement");
		row.put(name + RANGE_LOW_SUFFIX, description + ": low end of normal range");
		row.put(name + RANGE_HIGH_SUFFIX, description + ": high end of normal range");
		rows.add(row);
		return new CBCRow(description, name, unitEnumeration);
	}
	
	private String getValueInRow(Row row, List<String> headers, String header) {
		for (int i=0; i < headers.size(); i++) {
			if (header.equals(headers.get(i))) {
				return row.getValues().get(i);
			}
		}
		throw new IllegalArgumentException(header + " is not a valid header");
	}
	
}
