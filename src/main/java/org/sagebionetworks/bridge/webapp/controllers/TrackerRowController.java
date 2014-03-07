package org.sagebionetworks.bridge.webapp.controllers;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.ParticipantDataRowAdapter;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TrackerRowController extends JournalControllerBase {
	
	private static final Logger logger = LogManager.getLogger(TrackerRowController.class.getName());
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/new", method = RequestMethod.GET)
	public ModelAndView newRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model)
			throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		Set<String> defaultedFields = FormUtils.defaultsToDynamicForm(dynamicForm, client, spec, trackerId);
		model.addObject("defaultedFields", defaultedFields);
		
		model.setViewName("journal/trackers/new");
		return model;
	}

	// This is the finish action, which doesn't have to be marked as such in the HTML. In some forms there's just
	// a submit button to save (where resuming a form makes no sense).
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/new", method = RequestMethod.POST)
	public ModelAndView finishNewRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, BindingResult result,
			ModelAndView model) throws SynapseException {
		
		createRow(request, participantId, trackerId, dynamicForm, model, result,
				ParticipantDataUtils.getFinishedStatus(trackerId));
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/new", method = RequestMethod.POST, params="save=save")
	public ModelAndView saveNewRowForLater(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, BindingResult result,
			ModelAndView model) throws SynapseException {

		createRow(request, participantId, trackerId, dynamicForm, model, result,
				ParticipantDataUtils.getInProcessStatus(trackerId));
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}", method = RequestMethod.GET)
	public ModelAndView viewRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		model.addObject("rowId", rowId);
		
		ParticipantDataRow row = client.getParticipantDataRow(trackerId, rowId, false);
		ClientUtils.logSensitive(logger, row.getData());
		model.addObject("dynamicForm", new ParticipantDataRowAdapter(spec.getShowStructure(), row));
		
		model.setViewName("journal/trackers/show");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}/edit", method = RequestMethod.GET)
	public ModelAndView editRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		model.addObject("rowId", rowId);
		
		ParticipantDataRow row = client.getParticipantDataRow(trackerId, rowId, false);
		model.addObject("dynamicForm", new ParticipantDataRowAdapter(spec.getEditStructure(), row));
		
		model.setViewName("journal/trackers/edit");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}", method = RequestMethod.POST)
	public ModelAndView finishRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, @ModelAttribute DynamicForm dynamicForm,
			BindingResult result, ModelAndView model) throws SynapseException {

		updateRow(request, participantId, trackerId, rowId, dynamicForm, model, result,
				ParticipantDataUtils.getFinishedStatus(trackerId));
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/resume", method = RequestMethod.GET)
	public ModelAndView resumeInProcessRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		
		ParticipantDataRow row = client.getCurrentParticipantData(trackerId, false).getCurrentData();
		model.addObject("rowId", row.getRowId());
		model.addObject("dynamicForm", new ParticipantDataRowAdapter(spec.getEditStructure(), row));
		
		model.setViewName("journal/trackers/resume");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}/resume", method = RequestMethod.POST)
	public ModelAndView finishInProcessRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			BindingResult result, @ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		updateRow(request, participantId, trackerId, rowId, dynamicForm, model, result,
				ParticipantDataUtils.getFinishedStatus(trackerId));
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}/resume", method = RequestMethod.POST, params = "save=save")
	public ModelAndView saveInProcessRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			BindingResult result, @ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		updateRow(request, participantId, trackerId, rowId, dynamicForm, model, result,
				ParticipantDataUtils.getInProcessStatus(trackerId));
		return model;
	}

}
