package org.sagebionetworks.bridge.webapp.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JournalController {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@RequestMapping(value = "/journal", method = RequestMethod.GET)
	public ModelAndView viewDashboard(BridgeRequest request, ModelAndView model) {
		model.setViewName("journal/dashboard");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms", method = RequestMethod.GET)
	public ModelAndView viewForms(BridgeRequest request, @PathVariable("participantId") String participantId, ModelAndView model) {
		model.setViewName("journal/forms");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/studies", method = RequestMethod.GET)
	public ModelAndView viewStudies(BridgeRequest request, @PathVariable String participantId, ModelAndView model) {
		model.setViewName("journal/studies");
		return model;
	}
}
