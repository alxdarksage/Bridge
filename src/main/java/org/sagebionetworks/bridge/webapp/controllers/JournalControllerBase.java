package org.sagebionetworks.bridge.webapp.controllers;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.bridge.webapp.validators.SpecificationBasedValidator;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

public class JournalControllerBase extends NonAjaxControllerBase {

	private static final Logger logger = LogManager.getLogger(JournalControllerBase.class.getName());
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;
	
	@ModelAttribute("dynamicForm")
	public DynamicForm communityForm() {
		return new DynamicForm();
	}

	@ModelAttribute
	public void descriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataDescriptor> descriptors = client.getUserParticipantDataDescriptors(ClientUtils.LIMIT, 0L);
		ClientUtils.prepareDescriptorsByStatus(model, client, descriptors);
	}

	protected ParticipantDataRow createRow(BridgeRequest request, String participantId, String trackerId, DynamicForm dynamicForm, 
			ModelAndView model, BindingResult result, ParticipantDataStatusList statuses) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
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
			
			for (ParticipantDataRow row : data) {
				for (Map.Entry<String, ParticipantDataValue> entry : row.getData().entrySet()) {
					logger.info(entry.getKey() + ", " + ValueTranslator.toString(entry.getValue()));
				}
			}
			
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
		
		// Not passing the result object into the method is the equivalent of saying there's no 
		// UI on the request, it's an Ajax request and the client will take care of the results.

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		spec.setSystemSpecifiedValues(dynamicForm.getValuesMap());
		model.addObject("rowId", rowId);

		SpecificationBasedValidator validator = new SpecificationBasedValidator(spec);
		validator.validate(dynamicForm, result);

		if (result.hasErrors()) {
			model.setViewName("journal/trackers/edit");
			return null;
		}
		List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForUpdate(spec, dynamicForm.getValuesMap(), rowId);
		data = client.updateParticipantData(trackerId, data);
		if (statuses != null) {
			logger.info("Updating the status of this record, has it been finished?" + statuses.getUpdates().get(0).getLastEntryComplete().toString());
			client.sendParticipantDataDescriptorUpdates(statuses);	
		}
		request.setNotification("Tracker saved.");
		model.setViewName("redirect:/journal/" + participantId + "/trackers/" + trackerId + ".html");
		return data.get(0);
	}
	
}
