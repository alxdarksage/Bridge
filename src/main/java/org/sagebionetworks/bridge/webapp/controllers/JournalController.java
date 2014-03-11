package org.sagebionetworks.bridge.webapp.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JournalController extends JournalControllerBase {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());

	@RequestMapping(value = "/journal", method = RequestMethod.GET)
	public ModelAndView viewJournal(BridgeRequest request, ModelAndView model) throws SynapseException {
		// Force redirect to sign in
		request.getBridgeUser().getBridgeClient();
		model.setViewName("journal/index");
		return model;
	}

	@RequestMapping(value = "/journal2", method = RequestMethod.GET)
	public ModelAndView viewJournal2(BridgeRequest request, ModelAndView model) throws SynapseException {
		// Force redirect to sign in
		request.getBridgeUser().getBridgeClient();
		model.setViewName("journal/index2");
		return model;
	}
}
