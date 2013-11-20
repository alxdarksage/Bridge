package org.sagebionetworks.bridge.webapp.controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.forms.TermsOfUseForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
	public String post(BridgeRequest request, HttpServletResponse response, @ModelAttribute @Valid TermsOfUseForm termsOfUseForm, BindingResult result)
			throws Exception {
		
		if (!result.hasErrors()) {
			SignInForm signInForm = request.restoreSignInForm();
			if (termsOfUseForm.isOauthRedirect()) {
				
				Cookie cookie = new Cookie(OpenIdController.ACCEPTS_TERMS_OF_USE_COOKIE_NAME, "true");
				cookie.setMaxAge(OpenIdController.COOKIE_MAX_AGE_SECONDS);
				response.addCookie(cookie);
				// They've accepted the TOU, do the OAuth thing again with that permission set. 
				return "redirect:"+request.getOauthRedirect();
				
			} else if (signInForm != null) {
				
				UserSessionData userSessionData = synapseClient.login(signInForm.getEmail(), signInForm.getPassword(), true);
				BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
				request.setBridgeUser(user);
				logger.info("User #{} signed in.", user.getOwnerId());
				
			} else {
				request.setNotification("SessionError");
			}
			return "redirect:" + request.getOrigin();
		}
		termsOfUseForm.setTermsOfUse(synapseClient.getSynapseTermsOfUse());
		return "termsOfUse";
	}

	@RequestMapping(value = "/termsOfUse/cancel", method = RequestMethod.GET)
	public String cancelTermsOfUse(BridgeRequest request) {
		return "redirect:"+request.getOrigin();
	}
}
