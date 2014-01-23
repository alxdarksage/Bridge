package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
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

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
		PaginatedResults<ParticipantDataDescriptor> allDescriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0L);
		Collections.sort(allDescriptors.getResults(), new Comparator<ParticipantDataDescriptor>() {
			@Override
			public int compare(ParticipantDataDescriptor pdd0, ParticipantDataDescriptor pdd1) {
				return pdd0.getName().compareTo(pdd1.getName());
			}
			
		});
		return allDescriptors.getResults();
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
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}", method = RequestMethod.POST, params = "delete=delete")
	public String batchTrackers(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @RequestParam("rowSelect") Set<String> rowSelects)
			throws SynapseException {
		
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			request.setNotification( rowSelects.size() > 1 ? "TrackersDeleted" : "TrackerDeleted" );
		}
		request.setNotification("Not implemented");
		return "redirect:/journal/"+participantId+"/trackers/"+trackerId+".html";
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/export", method = RequestMethod.GET)
	@ResponseBody
	public void exportTrackers(BridgeRequest request, HttpServletResponse response,
			@PathVariable("participantId") String participantId, @PathVariable("trackerId") String trackerId)
			throws SynapseException, IOException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataRow> paginatedRowSet = client.getRawParticipantData(trackerId, ClientUtils.LIMIT, 0);
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, null, trackerId);
		
		// There's a Spring way to do this, but until we do another CSV export, it's really not worth it 
		response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename="+spec.getName()+".csv");
        Set<String> headers = Sets.newTreeSet();
        for (ParticipantDataRow row : paginatedRowSet.getResults()) {
			headers.addAll(row.getData().keySet());
		}
        CSVWriter writer = new CSVWriter(response.getWriter());
		writer.writeNext(headers.toArray(new String[] {}));
        for (ParticipantDataRow row : paginatedRowSet.getResults()) {
			List<String> values = Lists.newArrayListWithCapacity(headers.size());
			for (String header : headers) {
				values.add(ValueTranslator.toString(row.getData().get(header)));
			}
			writer.writeNext( values.toArray(new String[] {}));
		}
		writer.flush();
		writer.close();
		response.flushBuffer();
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/new", method = RequestMethod.GET)
	public ModelAndView newTracker(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model)
			throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		Set<String> defaultedFields = ClientUtils.defaultValuesFromPriorTracker(client, spec, dynamicForm, trackerId);
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
		ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		model.addObject("rowId", rowId);
		
		ParticipantDataRow row = client.getParticipantDataRow(trackerId, rowId);
		FormUtils.valuesToDynamicForm(dynamicForm, row);
		
		model.setViewName("journal/trackers/show");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/row/{rowId}/edit", method = RequestMethod.GET)
	public ModelAndView editRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, trackerId);
		model.addObject("rowId", rowId);
		
		ParticipantDataRow row = client.getParticipantDataRow(trackerId, rowId);
		FormUtils.valuesToDynamicForm(dynamicForm, row);
		
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

}
