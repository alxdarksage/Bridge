package org.sagebionetworks.bridge.webapp.controllers;

import java.text.ParseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JournalControllerBase {

	private static final Logger logger = LogManager.getLogger(JournalControllerBase.class.getName());
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("dynamicForm")
	public DynamicForm communityForm() {
		return new DynamicForm();
	}

	@ModelAttribute("descriptors")
	public List<ParticipantDataDescriptor> allDescriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		List<ParticipantDataDescriptor> allDescriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0L)
				.getResults();
		Collections.sort(allDescriptors, new Comparator<ParticipantDataDescriptor>() {
			@Override
			public int compare(ParticipantDataDescriptor pdd0, ParticipantDataDescriptor pdd1) {
				return pdd0.getName().compareTo(pdd1.getName());
			}
			
		});
		return allDescriptors;
	}

	@ModelAttribute
	public void descriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataDescriptor> descriptors = client.getParticipantDatas(ClientUtils.LIMIT, 0L);
		ClientUtils.prepareDescriptorsByStatus(model, client, descriptors);
	}

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
		
		// Not passing the result object into the method is the equivalent of saying there's no 
		// UI on the request, it's an Ajax request and the client will take care of the results.
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptor descriptor = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(client, specResolver, descriptor, model);
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
	
	protected ParticipantDataRow ajaxUpdateRow(BridgeRequest request, String participantId, String trackerId,
			long rowId, DynamicForm dynamicForm, ParticipantDataStatusList statuses)
			throws SynapseException {
		
		// Not passing the result object into the method is the equivalent of saying there's no 
		// UI on the request, it's an Ajax request and the client will take care of the results.
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptor descriptor = ClientUtils.prepareDescriptor(client, trackerId, null);
		Specification spec = ClientUtils.prepareSpecification(client, specResolver, descriptor, null);
		spec.setSystemSpecifiedValues(dynamicForm.getValuesMap());

		List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForUpdate(spec, dynamicForm.getValuesMap(), rowId);
		data = client.updateParticipantData(trackerId, data);
		client.sendParticipantDataDescriptorUpdates(statuses);	
		return data.get(0);
	}
	
}
