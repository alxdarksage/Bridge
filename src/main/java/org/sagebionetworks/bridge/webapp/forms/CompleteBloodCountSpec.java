package org.sagebionetworks.bridge.webapp.forms;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Used to (re)create the descriptorsById, the UI, etc. in lieu of a 
 * specification system for research instruments.
 */
public class CompleteBloodCountSpec {
	
	private static final Logger logger = LogManager.getLogger(CompleteBloodCountSpec.class.getName());
	
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

	// Synapse is not utf-8 compliant, you can't use anything reliably other than ascii?
	// private final List<String> CMM = Lists.newArrayList("cells/cmm", "cells/mcL", "cells/µL"); 
	private final List<String> CMM = Lists.newArrayList("cells/cmm", "cells/mcL", "cells/uL");
	private final List<String> GMDL = Lists.newArrayList("gm/dL", "g/DL", "mmol/L");
	private final List<String> PERC = Lists.newArrayList("%");
	private final List<String> FL = Lists.newArrayList("fL");
	private final List<String> PG = Lists.newArrayList("pg");
	private final List<String> COUNT = Lists.newArrayList("count", "c.v.", "s.d.");
	
	private List<String> metadata = Lists.newArrayList(CREATED_ON, MODIFIED_ON);
	private List<Map<String,String>> rows = Lists.newArrayList();
	private Map<String, List<CBCRow>> displayRows = Maps.newLinkedHashMap(); // key order is important

	public CompleteBloodCountSpec() {
		List<CBCRow> uiRow = Lists.newArrayList();
		uiRow.add( addField("rbc", "Red cells (Erythrocytes)", CMM) );
		uiRow.add( addField("hb", "Hemoglobin", GMDL) );
		uiRow.add( addField("hct", "Hematocrit", PERC) );
		uiRow.add( addField("mcv", "Mean corpuscular volume", FL) );
		uiRow.add( addField("mch", "Mean corpuscular hemoglobin", PG) );
		uiRow.add( addField("mchc", "Mean corpuscular hemoglobin concentration", PERC) );
		uiRow.add( addField("rdw", "RBC distribution width", COUNT) );
		uiRow.add( addField("ret", "Reticulocyte count", COUNT) );
		displayRows.put("Red blood cells", uiRow);
		
		uiRow = Lists.newArrayList();
		uiRow.add( addField("wbc", "White cells (Leukocytes)", CMM) );
		uiRow.add( addField("wbc_diff", "WBC differential (Diff)", PERC) );
		uiRow.add( addField("neutrophil", "Neutrophil", CMM) );
		uiRow.add( addField("lymphocytes", "Lymphocytes", CMM) );
		uiRow.add( addField("monocytes", "Monocytes", CMM) );
		uiRow.add( addField("eosinophil", "Eosinophil", CMM) );
		uiRow.add( addField("basophil", "Basophil", CMM) );
		displayRows.put("White blood cells", uiRow);
		
		uiRow = Lists.newArrayList();
		uiRow.add( addField("plt", "Platelet count", CMM) );
		uiRow.add( addField("mpv", "Mean platelet volume", FL) );
		uiRow.add( addField("pdw", "Platelet distribution width", PERC) );
		displayRows.put("Platelets", uiRow);
	}
	
	public Map<String, List<CBCRow>> getDisplayRows() {
		return displayRows;
	}
	
	/**
	 * The only way to reliably retrieve values out of a RowSet, so far as I can see, 
	 * is to record the order in which the fields were written.
	 */
	public List<String> getRowNames() {
		List<String> rowNames = Lists.newArrayList(metadata);
		for (Map<String,String> map : rows) {
			rowNames.addAll(map.keySet());
		}
		return rowNames;
	}
	
	public Object convertToObject(String header, String value) {
		if (StringUtils.isNotBlank(value) && !"null".equals(value)) {
			if (CREATED_ON.equals(header) || MODIFIED_ON.equals(header)) {
				return DateTime.parse(value, ISODateTimeFormat.dateTime()).toDate();
			}
		}
		return value;
	}
	
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
	
	public ParticipantDataDescriptor getDescriptor() {
		ParticipantDataDescriptor descriptor = new ParticipantDataDescriptor();
		descriptor.setName("CBC");
		descriptor.setDescription("Complete Blood Count");
		return descriptor;
	}
	
	public List<ParticipantDataColumnDescriptor> getColumnDescriptors(ParticipantDataDescriptor pdd) {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList();
		for (String meta : metadata) {
			ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
			column.setName(meta);
			column.setColumnType(ParticipantDataColumnType.STRING); // datetime stamp
			column.setParticipantDataDescriptorId(pdd.getId());
			list.add(column);
		}
		for (Map<String,String> row : rows) {
			for (String name : row.keySet()) {
				String description = row.get(name);
				ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
				column.setName(name);
				column.setDescription(description);
				column.setColumnType(ParticipantDataColumnType.DOUBLE); // All of them?!?
				column.setParticipantDataDescriptorId(pdd.getId());
				list.add(column);
			}
		}
		return list;
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
	
}
