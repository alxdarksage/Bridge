package org.sagebionetworks.bridge.webapp.controllers;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.forms.TermsOfUseForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TermsOfUseController extends AuthenticateBaseController {
	
	private static final Logger logger = LogManager.getLogger(TermsOfUseController.class.getName());

	@RequestMapping(value = "/tou", method = RequestMethod.GET)
	public String getWithoutCheckbox(BridgeRequest request, @ModelAttribute TermsOfUseForm termsOfUseForm) throws Exception {
		// Throws exception because I can't imagine what we'd do that was better 
		// than the error page if this failed
		termsOfUseForm.setTermsOfUse(synapseClient.getSynapseTermsOfUse());	
		return "tou";
	}
	
	@RequestMapping(value = "/termsOfUse", method = RequestMethod.GET)
	public String get(BridgeRequest request, @ModelAttribute TermsOfUseForm termsOfUseForm) throws Exception {
		// Throws exception because I can't imagine what we'd do that was better 
		// than the error page if this failed
		termsOfUseForm.setTermsOfUse(synapseClient.getSynapseTermsOfUse());	
		return "termsOfUse";
	}

	@RequestMapping(value = "/termsOfUse", method = RequestMethod.POST)
	public String post(BridgeRequest request, @ModelAttribute @Valid TermsOfUseForm termsOfUseForm, BindingResult result) throws Exception {
		if (!result.hasErrors()) {
			SignInForm signInForm = request.restoreSignInForm();
			
			try {
				// They accept the terms of use when creating their account,
				// they do not need to do it here.
				UserSessionData userSessionData = synapseClient.login(signInForm.getEmail(), signInForm.getPassword(), true);
				BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
				request.setBridgeUser(user);
				logger.info("User #{} signed in.", user.getOwnerId());
				
			} catch (SynapseException e) {
				ClientUtils.globalFormError(result, "termsOfUse", "IncorrectLogin");
				return getOnErrorReturnPage(signInForm, request);
			}
			// Log them in now, using the original credentials... yikes.
			// redirect to login with terms of use set?!?
			return "redirect:" + request.getOriginURL();
		}
		termsOfUseForm.setTermsOfUse(synapseClient.getSynapseTermsOfUse());
		return "termsOfUse";
	}

}
