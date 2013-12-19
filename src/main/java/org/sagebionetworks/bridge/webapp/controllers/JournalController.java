package org.sagebionetworks.bridge.webapp.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.CompleteBloodCountSpec;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

@Controller
public class JournalController {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());
	
	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("descriptors")
	public List<ParticipantDataDescriptor> descriptors() throws SynapseException {
		PaginatedResults<ParticipantDataDescriptor> descriptors = bridgeClient.getAllParticipantDatas(ClientUtils.LIMIT, 0L);
		return descriptors.getResults();
	}
	
	@RequestMapping(value = "/journal", method = RequestMethod.GET)
	public ModelAndView viewDashboard(BridgeRequest request, ModelAndView model) {
		model.setViewName("journal/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}", method = RequestMethod.GET)
	public ModelAndView viewForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		
		ClientUtils.addParticipantDataDescriptor(bridgeClient, model, formId);
		
		try {
			PaginatedRowSet records = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
			model.addObject("records", records);
		} catch(Exception e) {
			// this throws a gibberish exception when there are no records. It's not a 404, it's a 500 with 
			// a Tomcat web page as the message of the exception.
			model.addObject("records", Lists.newArrayList());
		}
		
		// Show all the records for this form, with the ability to create, delete.
		model.setViewName("journal/forms/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.GET)
	public ModelAndView createSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, ModelAndView model) throws SynapseException {
		
		ClientUtils.addParticipantDataDescriptor(bridgeClient, model, formId);
		// There's currently only one... so the form would have to map to it. 
		model.addObject("form", new CompleteBloodCountSpec());
		model.setViewName("journal/forms/new");

		return model;
	}
	
}
