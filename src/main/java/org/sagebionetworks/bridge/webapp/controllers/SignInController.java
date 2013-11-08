package org.sagebionetworks.bridge.webapp.controllers;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseTermsOfUseException;
import org.sagebionetworks.repo.model.UserSessionData;
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
			return "redirect:"+request.getBridgeUser().getStartURL();
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

				// They accept the terms of use when creating their account,
				// they do not need to do it here.
				UserSessionData userSessionData = synapseClient.login(signInForm.getEmail(), signInForm.getPassword());
				BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
				request.setBridgeUser(user);
				logger.info("User #{} signed in.", user.getOwnerId());
				
			} catch (SynapseTermsOfUseException e) {
				request.saveSignInForm(signInForm);
				return "redirect:/termsOfUse.html";
			} catch (SynapseException e) {
				ClientUtils.globalFormError(result, "signInForm", "IncorrectLogin");
				return getOnErrorReturnPage(signInForm, request);
			}
		}
		return getOnSuccessPage(signInForm, request);
	}

}
