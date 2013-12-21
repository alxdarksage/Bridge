package org.sagebionetworks.bridge.webapp.controllers;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.CompleteBloodCountSpec;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.sagebionetworks.repo.model.table.Row;
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
		ClientUtils.prepareDescriptor(client, model, formId);
		// TODO: Once this is an interface, we can make a map between the descriptor name and the object instance.
		// We won't have to pass it in like this.
		ClientUtils.prepareParticipantData(client, model, new CompleteBloodCountSpec(), formId);
		model.setViewName("journal/forms/index");
		return model;
	}
	
	// It's possible through the submit button name to have another method to process another 
	// table-specific button in the same form. Very handy.
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}", method = RequestMethod.POST, params = "delete=delete")
	public String batchForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @RequestParam("rowSelect") Set<String> rowSelects)
			throws SynapseException {
		
		/*
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			
			RowSet rowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0).getResults();
			
			List<Row> oldRows = rowSet.getRows();
			List<Row> newRows = Lists.newArrayList();
			int count = 0;
			for (int i=0; i < oldRows.size(); i++) {
				if (rowSelects.contains(Integer.toString(i))) {
					count++; // removed
				} else {
					newRows.add( oldRows.get(i) );
				}
			}
			rowSet.setRows(newRows);
			client.updateParticipantData(formId, rowSet);
			
			if (count == 1) {
				request.setNotification("FormDeleted");
			} else if (count > 1) {
				request.setNotification("FormsDeleted");
			}
		}*/
		request.setNotification("Not implemented");
		return "redirect:/journal/"+participantId+"/forms/"+formId+".html";
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.GET)
	public ModelAndView newSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ClientUtils.prepareDescriptor(client, model, formId);
		
		CompleteBloodCountSpec spec = new CompleteBloodCountSpec();
		model.addObject("form", spec);
		
		model.setViewName("journal/forms/new");

		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.POST)
	public ModelAndView createSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model) throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		
		CompleteBloodCountSpec spec = new CompleteBloodCountSpec();
		dynamicForm.setSpec(spec);
		RowSet data = dynamicForm.getNewRowSet();
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
		CompleteBloodCountSpec spec = new CompleteBloodCountSpec();
		ClientUtils.prepareDescriptor(client, model, formId);
		
		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		Row row = ClientUtils.getRowById(paginatedRowSet, rowId);
		dynamicForm.setSpec(spec);
		
		FormUtils.valuesToDynamicForm(dynamicForm, paginatedRowSet.getResults().getHeaders(), row);
		
		model.addObject("form", spec);
		model.addObject("rowId", row.getRowId());
		
		model.setViewName("journal/forms/edit");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.POST)
	public ModelAndView updateRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();

		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		Row row = ClientUtils.getRowById(paginatedRowSet, rowId);

		dynamicForm.setSpec(new CompleteBloodCountSpec());
		// It's annoying you have to pass the headers in, but we need them in the server's order here.
		RowSet data = dynamicForm.getUpdatedRowSet(paginatedRowSet.getResults().getHeaders(), row);
		client.updateParticipantData(formId, data);
		
		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/"+participantId+"/forms/"+formId+".html");
		return model;
	}
}
