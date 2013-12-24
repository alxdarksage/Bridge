package org.sagebionetworks.bridge.webapp.specs;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;

import com.google.common.collect.Lists;

public class CompleteBloodCount implements Specification {

	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
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
		rows.add( addRow("rbc", "Red cells (Erythrocytes)", CMM) );
		rows.add( addRow("hb", "Hemoglobin", GMDL) );
		rows.add( addRow("hct", "Hematocrit", PERC) );
		rows.add( addRow("mcv", "Mean corpuscular volume", FL) );
		rows.add( addRow("mch", "Mean corpuscular hemoglobin", PG) );
		// uiRow.add( addField("mchc", "Mean corpuscular hemoglobin concentration", PERC) );
		rows.add( addRow("rdw", "RBC distribution width", COUNT) );
		rows.add( addRow("ret", "Reticulocyte count", COUNT) );
		displayRows.add( new FormGroup("Red blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow("wbc", "White cells (Leukocytes)", CMM) );
		rows.add( addRow("wbc_diff", "WBC differential (Diff)", PERC) );
		// But these can also be counts...
		rows.add( addRow("neutrophil", "Neutrophil", PERC) );
		rows.add( addRow("neutrophil_immature", "Immature Neutrophil (band neutrophil)", PERC) );
		rows.add( addRow("lymphocytes", "Lymphocytes", PERC) );
		rows.add( addRow("monocytes", "Monocytes", PERC) );
		// uiRow.add( addField("eosinophil", "Eosinophil", CMM) );
		// uiRow.add( addField("basophil", "Basophil", CMM) );
		displayRows.add( new FormGroup("White blood cells", rows) );
		
		rows = Lists.newArrayList();
		rows.add( addRow("plt", "Platelet count (Thrombocyte)", CMM) );
		rows.add( addRow("mpv", "Mean platelet volume (MPV)", FL) );
		rows.add( addRow("pdw", "Platelet distribution width", PERC) );
		displayRows.add( new FormGroup("Platelets", rows) );
	}
	
	@Override
	public FormLayout getFormLayout() {
		return FormLayout.GRID;
	}

	@Override
	public List<FormElement> getFormElements() {
		return displayRows;
	}

	@Override
	public List<String> getFieldNames() {
		List<String> fieldNames = Lists.newArrayList();
		for (FormElement field : metadata) {
			fieldNames.add(field.getName());
		}
		for (FormElement displayRow : displayRows) {
			for (FormElement row: displayRow.getChildren()) {
				for (FormElement field : row.getChildren()) {
					fieldNames.add(field.getName());
				}
			}
		}		
		return fieldNames;
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
		for (FormElement meta : metadata) {
			ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
			column.setName(meta.getName());
			column.setColumnType(ParticipantDataColumnType.STRING); // datetime stamp
			column.setParticipantDataDescriptorId(descriptor.getId());
			list.add(column);
		}
		for (FormElement displayRow : displayRows) {
			for (FormElement row: displayRow.getChildren()) {
				for (FormElement field : row.getChildren()) {
					ParticipantDataColumnDescriptor column = new ParticipantDataColumnDescriptor();
					column.setName(field.getName());
					column.setDescription(field.getLabel());
					column.setColumnType(ParticipantDataColumnType.DOUBLE); // All of them?!?
					column.setParticipantDataDescriptorId(descriptor.getId());
					list.add(column);
				}
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
	
	private FormGroup addRow(String name, String description, List<String> unitEnumeration) {
		FormGroup row = new FormGroup(description);
		row.addColumn(new FormField(name, description, false));
		row.addColumn(new EnumeratedFormField(name + UNITS_SUFFIX, description + ": units of measurement", true, unitEnumeration));
		row.addColumn(new FormField(name + RANGE_LOW_SUFFIX, description + ": low end of normal range", true));
		row.addColumn(new FormField(name + RANGE_HIGH_SUFFIX, description + ": high end of normal range", true));
		return row;
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
