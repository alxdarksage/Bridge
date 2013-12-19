package org.sagebionetworks.bridge.webapp.forms;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Used to (re)create the descriptors, the UI, etc. in lieu of a 
 * specification system for research instruments.
 */
public class CompleteBloodCountSpec {

	private List<Map<String,String>> rows = Lists.newArrayList();
	
	public CompleteBloodCountSpec() {
		addField("wbc", "White cells (Leukocytes)");
		addField("wbc_diff", "WBC differential (Diff)");
		addField("neutrophil", "Neutrophil");
		addField("lymphocytes", "Lymphocytes");
		addField("monocytes", "Monocytes");
		addField("eosinophil", "Eosinophil");
		addField("basophil", "Basophil");
		addField("rbc", "Red cells (Erythrocytes)");
		addField("hb", "Hemoglobin");
		addField("hct", "Hematocrit");
		addField("mcv", "Mean corpuscular volume");
		addField("mch", "Mean corpuscular hemoglobin");
		addField("mchc", "Mean corpuscular hemoglobin concentration");
		addField("rdw", "RBC distribution width");
		addField("ret", "Reticulocyte count");
		addField("plt", "Platelet count");
		addField("mpv", "Mean platelet volume");
		addField("pdw", "Platelet distribution width");
	}
	
	public ParticipantDataDescriptor getDescriptor() {
		ParticipantDataDescriptor descriptor = new ParticipantDataDescriptor();
		descriptor.setName("CBC");
		descriptor.setDescription("Complete Blood Count");
		return descriptor;
	}
	
	public List<ParticipantDataColumnDescriptor> getColumnDescriptors(ParticipantDataDescriptor pdd) {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList();
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
	
	public List<Map<String,String>> getRows() {
		return rows;
	}
	
	private void addField(String name, String description) {
		Map<String,String> row = Maps.newLinkedHashMap();
		row.put(name, description);
		row.put(name + "_units", description + ": units of measurement");
		row.put(name + "_range_low", description + ": low end of normal range");
		row.put(name + "_range_high", description + ": high end of normal range");
		rows.add(row);
	}
	
	
	
}
