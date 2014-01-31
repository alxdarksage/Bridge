package org.sagebionetworks.bridge.webapp.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.bridge.webapp.validators.SpecificationBasedValidator;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public class JournalControllerBase {

	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;

	protected ParticipantDataRow createRow(BridgeRequest request, String participantId, String trackerId, DynamicForm dynamicForm, 
			ModelAndView model, BindingResult result, ParticipantDataStatusList statuses) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptor descriptor = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(client, specResolver, descriptor, model);
		spec.setSystemSpecifiedValues(dynamicForm.getValuesMap());

		if (result != null) {
			SpecificationBasedValidator validator = new SpecificationBasedValidator(spec);
			validator.validate(dynamicForm, result);
		}
		if (result != null && result.hasErrors()) {
			model.setViewName("journal/trackers/new");
			return null;
		} else {
			List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForCreate(spec, dynamicForm.getValuesMap());
			data = client.appendParticipantData(trackerId, data);
			client.sendParticipantDataDescriptorUpdates(statuses);
			// These are harmless to the Ajax call.
			if (Boolean.TRUE.equals(statuses.getUpdates().get(0).getLastEntryComplete())) {
				request.setNotification("Tracker created.");	
			} else {
				request.setNotification("Tracker saved.");
			}
			model.setViewName("redirect:/journal/"+participantId+"/trackers/"+trackerId+".html");
			return data.get(0);
		}
	}

	protected ParticipantDataRow updateRow(BridgeRequest request, String participantId, String trackerId,
			long rowId, DynamicForm dynamicForm, ModelAndView model, BindingResult result,
			ParticipantDataStatusList statuses) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptor descriptor = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(client, specResolver, descriptor, model);
		spec.setSystemSpecifiedValues(dynamicForm.getValuesMap());
		model.addObject("rowId", rowId);

		if (result != null) {
			SpecificationBasedValidator validator = new SpecificationBasedValidator(spec);
			validator.validate(dynamicForm, result);
		}
		if (result != null && result.hasErrors()) {
			model.setViewName("journal/trackers/edit");
			return null;
		} else {
			List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForUpdate(spec, dynamicForm.getValuesMap(), rowId);
			data = client.updateParticipantData(trackerId, data);
			if (statuses != null) {
				client.sendParticipantDataDescriptorUpdates(statuses);	
			}
			// These are harmless to the Ajax call
			request.setNotification("Tracker saved.");
			model.setViewName("redirect:/journal/" + participantId + "/trackers/" + trackerId + ".html");
			return data.get(0);
		}
	}
}
