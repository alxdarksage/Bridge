package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.RequestResetPasswordForm;
import org.sagebionetworks.bridge.webapp.forms.ResetPasswordForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
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
		return "auth/requestResetPassword";
	}

	@RequestMapping(value="/requestResetPassword", method = RequestMethod.POST)
	public String post(BridgeRequest request,
			@ModelAttribute("requestResetPasswordForm") @Valid RequestResetPasswordForm requestResetPasswordForm,
			BindingResult result) throws SynapseException {
		if (!result.hasErrors()) {
			synapseClient.sendPasswordResetEmail(requestResetPasswordForm.getEmail());
			request.setNotification("ResetEmailSent");
			return "redirect:" + request.getOrigin();
		}
		return "auth/requestResetPassword";
	}
	
	@RequestMapping(value="/resetPassword", method = RequestMethod.GET)
	public String get(@ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm) {
		return "auth/resetPassword";
	}

	@RequestMapping(value="/resetPassword", method = RequestMethod.POST)
	public String post(@ModelAttribute("resetPasswordForm") @Valid ResetPasswordForm resetPasswordForm,
			BindingResult result, BridgeRequest request) throws SynapseException {
		if (!result.hasErrors()) {
			synapseClient.changePassword(resetPasswordForm.getToken(), resetPasswordForm.getPassword());
			request.setNotification("PasswordChanged");
			return "redirect:/signOut.html";
		}
		return "auth/resetPassword";
	}
}
