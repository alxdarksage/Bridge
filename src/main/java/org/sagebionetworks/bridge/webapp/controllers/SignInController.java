package org.sagebionetworks.bridge.webapp.controllers;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.Session;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/signIn")
public class SignInController extends AuthenticateBaseController {

	private static final Logger logger = LogManager.getLogger(SignInController.class.getName());

	@RequestMapping(method = RequestMethod.GET)
	public String get(@ModelAttribute SignInForm signInForm, BridgeRequest request) {
		if (request.isUserAuthenticated()) {
			return "redirect:" + request.getOrigin();
		}
		return "signIn";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(BridgeRequest request, @ModelAttribute @Valid SignInForm signInForm, BindingResult result)
			throws SynapseException {
		if (result.hasErrors()) {
			return getOnErrorReturnPage(signInForm, request);
		}
		
		// Shouldn't happen that user is authenticated, but.
		if (!request.isUserAuthenticated()) {
			try {
				Session session = synapseClient.login(signInForm.getEmail(), signInForm.getPassword());
				if (!session.getAcceptsTermsOfUse()) {
					request.saveSignInForm(signInForm);
					return "redirect:/termsOfUse.html";
				}
				UserSessionData userSessionData = synapseClient.getUserSessionData();
				BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
				request.setBridgeUser(user);
				logger.info("User #{} signed in.", user.getOwnerId());
				
			} catch (SynapseException e) {
				ClientUtils.formError(result, "signInForm", "IncorrectLogin");
				return getOnErrorReturnPage(signInForm, request);
			}
		}
		return "redirect:"+getOnSuccessPage(signInForm, request);
	}

}
