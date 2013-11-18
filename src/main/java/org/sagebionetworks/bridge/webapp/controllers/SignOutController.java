package org.sagebionetworks.bridge.webapp.controllers;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/signOut")
public class SignOutController {

	private static final Logger logger = LogManager.getLogger(SignOutController.class.getName());

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public String get(BridgeRequest request, HttpSession session, SignInForm signInForm) throws SynapseException {
		BridgeUser user = request.getBridgeUser();
		
		// You're about to delete this, so copy it to the request and refer to it there.
		request.setAttribute("origin", request.getOrigin());
		
		if (user.isAuthenticated()) {
			SynapseClient client = user.getSynapseClient();
			client.logout();
			session.invalidate();
			logger.info("User #{} signed off.", user.getOwnerId());
		}
		return "signedOut";
	}

}
