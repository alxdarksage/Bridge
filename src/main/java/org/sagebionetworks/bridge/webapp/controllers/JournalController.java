package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.IdList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JournalController extends JournalControllerBase {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());
	
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

	@RequestMapping(value = "/journal", method = RequestMethod.GET)
	public ModelAndView viewJournal(BridgeRequest request, ModelAndView model) throws SynapseException {
		// Force redirect to sign in
		request.getBridgeUser().getBridgeClient();
		model.setViewName("journal/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}", method = RequestMethod.GET)
	public ModelAndView viewForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		model.addObject("spec", spec);
		ClientUtils.prepareParticipantData(client, model, spec, trackerId);
		model.setViewName("journal/trackers/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/export", method = RequestMethod.GET)
	@ResponseBody
	public void exportTrackers(BridgeRequest request, HttpServletResponse response,
			@PathVariable("participantId") String participantId, @PathVariable("trackerId") String trackerId)
			throws SynapseException, IOException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataRow> paginatedRowSet = client.getRawParticipantData(trackerId, ClientUtils.LIMIT, 0);
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, null, trackerId);
		ClientUtils.exportParticipantData(response, spec, paginatedRowSet);
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/new", method = RequestMethod.GET)
	public ModelAndView newTracker(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model)
			throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		Set<String> defaultedFields = FormUtils.defaultsToDynamicForm(dynamicForm, client, spec, trackerId);
		model.addObject("defaultedFields", defaultedFields);
		
		model.setViewName("journal/trackers/new");
		return model;
	}

	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/new", method = RequestMethod.POST)
	public ModelAndView createTracker(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, BindingResult result,
			ModelAndView model) throws SynapseException {

		createRow(request, trackerId, dynamicForm, result, model);
		if (result.hasErrors()) {
			model.setViewName("journal/trackers/new");
			return model;
		}
		
		request.setNotification("Tracker updated.");
		model.setViewName("redirect:/journal/"+participantId+"/trackers/"+trackerId+".html");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}", method = RequestMethod.GET)
	public ModelAndView viewRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		model.addObject("rowId", rowId);
		
		ParticipantDataRow row = client.getParticipantDataRow(trackerId, rowId);
		
		ClientUtils.logSensitive(logger, row.getData());
		
		FormUtils.valuesToDynamicForm(spec, dynamicForm, row);
		
		model.setViewName("journal/trackers/show");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}/edit", method = RequestMethod.GET)
	public ModelAndView editRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		model.addObject("rowId", rowId);
		
		ParticipantDataRow row = client.getParticipantDataRow(trackerId, rowId);
		FormUtils.valuesToDynamicForm(spec, dynamicForm, row);
		
		model.setViewName("journal/trackers/edit");
		return model;
	}

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}", method = RequestMethod.POST)
	public ModelAndView updateRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, @ModelAttribute DynamicForm dynamicForm,
			BindingResult result, ModelAndView model) throws SynapseException {

		updateRow(request, trackerId, dynamicForm, result, model, rowId);
		model.addObject("rowId", rowId);
		if (result.hasErrors()) {
			model.setViewName("journal/trackers/edit");
			return model;
		}

		request.setNotification("Tracker updated.");
		model.setViewName("redirect:/journal/" + participantId + "/trackers/" + trackerId + ".html");
		return model;
	}
	
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}", method = RequestMethod.POST, params = "delete=delete")
	public String batchTrackers(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @RequestParam("rowSelect") List<String> rowSelects)
			throws SynapseException {
		
		if (rowSelects != null) {
			// Spring silently accepts List<Long> as a rowSelect parameter type, but it does no conversion
			// without registering a lot of crazy conversion stuff.
			List<Long> newList = new ArrayList<Long>();
			for (String value : rowSelects) {
				newList.add(Long.parseLong(value));
			}
			IdList idList = new IdList();
			idList.setList(newList);
			
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			client.deleteParticipantDataRows(trackerId, idList);
			request.setNotification( rowSelects.size() > 1 ? "RowsDeleted" : "RowDeleted" );
		}
		return "redirect:/journal/"+participantId+"/trackers/"+trackerId+".html";
	}

}
