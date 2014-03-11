package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.controllers.NonAjaxControllerBase;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

@Controller
@RequestMapping("/admin")
public class TrackersAdminController extends NonAjaxControllerBase {

	private static Logger logger = LogManager.getLogger(TrackersAdminController.class.getName());
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;
	
	@Resource(name = "bridgeClient")
	protected BridgeClient client;
	
	@RequestMapping(value = "/trackers/index", method = RequestMethod.GET)
	public String viewTrackers() {
		return "admin/trackers/index";
	}

	@RequestMapping(value = "/trackers/update", method = RequestMethod.GET)
	public ModelAndView updateTrackers(final BridgeRequest request,
			@ModelAttribute("descriptors") List<ParticipantDataDescriptor> descriptors, ModelAndView model)
			throws Exception {
		Map<String, ParticipantDataDescriptor> descriptorMap = getDescriptorMap(descriptors);
		
		StringBuilder sb = new StringBuilder();
		updateOrCreateTrackersIfMissing(sb, descriptorMap);
		//createColumnsIfMissing(sb, descriptorMap);
		
		for (ParticipantDataDescriptor descriptor : descriptorMap.values()) {
			Specification spec = specResolver.getSpecification(descriptor.getName());
			if (spec == null || spec.getAllFormElements() == null) {
				sb.append("<p>Skipped " + descriptor.getName() + ", it has no UI specified.</p>");
			}
		}
		
		model.addObject("messages", sb.toString());

		model.setViewName("admin/trackers/index");
		return model;
	}

	private void updateOrCreateTrackersIfMissing(StringBuilder sb, Map<String, ParticipantDataDescriptor> descriptorMap)
			throws SynapseException, IllegalAccessException, InvocationTargetException {
		
		for(Specification spec : specResolver.getAllSpecifications()) {
			ParticipantDataDescriptor descriptor = descriptorMap.get(spec.getName());
			if (descriptor == null) {
				sb.append("<p>Created tracker " + spec.getName() + ". </p>");
				
				descriptor = client.createParticipantDataDescriptor(ParticipantDataUtils.getDescriptor(spec));
				List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(
						descriptor.getId(), spec);
				for (ParticipantDataColumnDescriptor column : columns) {
					client.createParticipantDataColumnDescriptor(column);
				}
			} else {
				sb.append("<p>Tracker " + spec.getName() + " updated if there were changes.");
				ParticipantDataDescriptor newDesc = ParticipantDataUtils.getDescriptor(spec);
				descriptor.setName(newDesc.getName());
				descriptor.setDescription(newDesc.getDescription());
				descriptor.setRepeatFrequency(newDesc.getRepeatFrequency());
				descriptor.setRepeatType(newDesc.getRepeatType());
				client.updateParticipantDataDescriptor(descriptor);
				updateColumns(client, sb, spec, descriptor);
			}
		}
	}

	private void updateColumns(BridgeClient client, StringBuilder sb, Specification spec,
			ParticipantDataDescriptor descriptor) throws SynapseException {
		Map<String,ParticipantDataColumnDescriptor> columnsByName = getColumnsMap(client, descriptor.getId()); 
		
		List<ParticipantDataColumnDescriptor> specColumns = ParticipantDataUtils.getColumnDescriptors(descriptor.getId(), spec);
		for (ParticipantDataColumnDescriptor specColumn : specColumns) {
			if (!columnsByName.containsKey(specColumn.getName())) {
				sb.append("<br><b>&bull; created a column for '" + specColumn.getName() + "'.</b> ");
				specColumn.setParticipantDataDescriptorId(descriptor.getId());
				client.createParticipantDataColumnDescriptor(specColumn);
			} else {
				sb.append("<br>&bull; skipping column '" + specColumn.getName() + "', it already exists. ");
				compareColumns(sb, columnsByName.get(specColumn.getName()), specColumn);
			}
		}
		sb.append("</p>");
	}
	
	private Map<String, ParticipantDataDescriptor> getDescriptorMap(List<ParticipantDataDescriptor> descriptors) {
		Map<String,ParticipantDataDescriptor> map = Maps.newHashMap();
		for (ParticipantDataDescriptor descriptor : descriptors) {
			map.put(descriptor.getName(), descriptor);
		}
		return map;
	}
	
	private Map<String, ParticipantDataColumnDescriptor> getColumnsMap(BridgeClient client, String descriptorId)
			throws SynapseException {
		PaginatedResults<ParticipantDataColumnDescriptor> columns = client.getParticipantDataColumnDescriptors(
				descriptorId, ClientUtils.LIMIT, 0L);

		Map<String,ParticipantDataColumnDescriptor> map = Maps.newHashMap();
		for (ParticipantDataColumnDescriptor column : columns.getResults()) {
			map.put(column.getName(), column);
		}
		return map;
	}
	
	private void compareColumns(StringBuilder sb, ParticipantDataColumnDescriptor existingColumn,
			ParticipantDataColumnDescriptor specColumn) {
		String message = "<br>&bull; <b>But, the '%s' field changed and this column is immutable (create a new column by renaming this column)</b>";
		
		if (existingColumn.getExportable() != existingColumn.getExportable()) {
			sb.append(String.format(message, "exportable"));
		}
		if (existingColumn.getReadonly() != existingColumn.getReadonly()) {
			sb.append(String.format(message, "readonly"));
		}
		if (existingColumn.getRequired() != existingColumn.getRequired()) {
			sb.append(String.format(message, "required"));
		}
		if (!areEqual(existingColumn.getColumnType(), specColumn.getColumnType())) {
			sb.append(String.format(message, "columnType"));
		}
		if (!areEqual(existingColumn.getDefaultValue(), specColumn.getDefaultValue())) {
			sb.append(String.format(message, "defaultValue"));
		}
		if (!areEqual(existingColumn.getDescription(), specColumn.getDescription())) {
			sb.append(String.format(message, "description"));
		}
		if (!areEqual(existingColumn.getType(), specColumn.getType())) {
			sb.append(String.format(message, "type"));
		}
	}
	
	private boolean areEqual(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		return obj1.equals(obj2);
	}

}
