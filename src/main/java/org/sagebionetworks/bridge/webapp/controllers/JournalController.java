package org.sagebionetworks.bridge.webapp.controllers;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.CompleteBloodCount;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.sagebionetworks.repo.model.table.RowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JournalController {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());
	
	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("dynamicForm")
	public DynamicForm communityForm() {
		return new DynamicForm();
	}
	
	@ModelAttribute("descriptors")
	public List<ParticipantDataDescriptor> descriptors() throws SynapseException {
		PaginatedResults<ParticipantDataDescriptor> descriptors = bridgeClient.getAllParticipantDatas(ClientUtils.LIMIT, 0L);
		return descriptors.getResults();
	}
	
	@RequestMapping(value = "/journal", method = RequestMethod.GET)
	public ModelAndView viewAllForms(BridgeRequest request, ModelAndView model) throws SynapseException {
		model.setViewName("journal/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}", method = RequestMethod.GET)
	public ModelAndView viewForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareDescriptorAndForm(client, model, formId);
		// TODO: Don't pass conversion all the way down like this.
		ClientUtils.prepareParticipantData(client, model, spec, formId);
		model.setViewName("journal/forms/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}", method = RequestMethod.POST, params = "delete=delete")
	public String batchForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @RequestParam("rowSelect") Set<String> rowSelects)
			throws SynapseException {
		
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
		}
		request.setNotification("Not implemented");
		return "redirect:/journal/"+participantId+"/forms/"+formId+".html";
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.GET)
	public ModelAndView newSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ClientUtils.prepareDescriptorAndForm(client, model, formId);
		model.setViewName("journal/forms/new");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.POST)
	public ModelAndView createSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model) throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		
		CompleteBloodCount spec = new CompleteBloodCount();
		RowSet data = spec.getRowSetForCreate(dynamicForm.getValues());
		client.appendParticipantData(formId, data);
		
		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/"+participantId+"/forms/"+formId+".html");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.GET)
	public ModelAndView viewRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ClientUtils.prepareDescriptorAndForm(client, model, formId);
		
		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		FormUtils.valuesToDynamicForm(dynamicForm, paginatedRowSet.getResults(), rowId);
		
		model.addObject("rowId", rowId);
		
		model.setViewName("journal/forms/edit");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.POST)
	public ModelAndView updateRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		
		CompleteBloodCount spec = new CompleteBloodCount();
		RowSet data = spec.getRowSetForUpdate(dynamicForm.getValues(), paginatedRowSet.getResults(), rowId);
		client.updateParticipantData(formId, data);
		
		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/"+participantId+"/forms/"+formId+".html");
		return model;
	}
}
