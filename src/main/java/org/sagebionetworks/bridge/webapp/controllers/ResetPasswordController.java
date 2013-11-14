package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.RequestResetPasswordForm;
import org.sagebionetworks.bridge.webapp.forms.ResetPasswordForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.OriginatingClient;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ResetPasswordController {
	
	private static final Logger logger = LogManager.getLogger(ResetPasswordController.class.getName());

	@Resource(name = "synapseClient")
	private SynapseClient synapseClient;

	@RequestMapping(value="/requestResetPassword", method = RequestMethod.GET)
	public String get(@ModelAttribute("requestResetPasswordForm") RequestResetPasswordForm requestResetPasswordForm) {
		return "requestResetPassword";
	}

	@RequestMapping(value="/requestResetPassword", method = RequestMethod.POST)
	public String post(
			@ModelAttribute("requestResetPasswordForm") @Valid RequestResetPasswordForm requestResetPasswordForm,
			BindingResult result, BridgeRequest request) {
		if (!result.hasErrors()) {
			try {
				synapseClient.sendPasswordResetEmail(requestResetPasswordForm.getEmail(), OriginatingClient.BRIDGE);
				request.setNotification("ResetEmailSent");
				return "redirect:" + request.getOriginURL();
			} catch (SynapseNotFoundException e) {
				ClientUtils.globalFormError(result, "resetPasswordForm", "UserNotFoundException");
			} catch (SynapseException e) {
				ClientUtils.formError(result, "signUpForm", e.getMessage());
			}
		}
		return "requestResetPassword";
	}
	
	@RequestMapping(value="/resetPassword", method = RequestMethod.GET)
	public String get(@ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) {
		return "resetPassword";
	}

	@RequestMapping(value="/resetPassword", method = RequestMethod.POST)
	public String post(@ModelAttribute("resetPasswordForm") @Valid ResetPasswordForm resetPasswordForm,
			BindingResult result, BridgeRequest request) throws Exception {

		if (!result.hasErrors()) {
			try {
				synapseClient.changePassword(resetPasswordForm.getToken(), resetPasswordForm.getPassword());
				request.setNotification("PasswordChanged");
				return "redirect:" + request.getOriginURL();
			} catch(Exception e) {
				ClientUtils.globalFormError(result, "resetPasswordForm", "RestFailed");
			}
		}
		return "resetPassword";
	}
}
