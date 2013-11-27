package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.OriginatingClient;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/signUp")
public class SignUpController {

	@Resource(name = "synapseClient")
	private SynapseClient synapseClient;

	@ModelAttribute("signUpForm")
	public SignUpForm signInForm() {
		return new SignUpForm();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(BridgeRequest request, @ModelAttribute SignUpForm signUpForm) {
		return "signUp";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute @Valid SignUpForm signUpForm, BindingResult result, BridgeRequest request)
			throws Exception {
		if (!result.hasErrors()) {
			try {
				NewUser newUser = FormUtils.valuesToNewUser(new NewUser(), signUpForm);
				// In development, you must supply a password and you get an error if you do not. In production, 
				// you cannot supply a password. In fact, that's bad, and we don't allow it through signUpForm.
				if (!StackConfiguration.isProductionStack()) {
					newUser.setPassword("password");
				}
				synapseClient.createUser(newUser, OriginatingClient.BRIDGE);
				request.setNotification("RegistrationEmailSent");
				return "redirect:"+request.getOrigin();
				
			} catch (SynapseException e) {
				String message = ClientUtils.parseSynapseException(e, 409);
				ClientUtils.fieldError(result, "signUpForm", "email", message);
			}
		}
		return "signUp";
	}
	
}
