package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.principal.AliasCheckRequest;
import org.sagebionetworks.repo.model.principal.AliasCheckResponse;
import org.sagebionetworks.repo.model.principal.AliasType;
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
		return "auth/signUp";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute @Valid SignUpForm signUpForm, BindingResult result, BridgeRequest request)
			throws Exception {
		if (!result.hasErrors()) {
			NewUser newUser = FormUtils.valuesToNewUser(new NewUser(), signUpForm);
			if (!isValid(synapseClient, AliasType.USER_NAME, newUser.getUserName())) {
				ClientUtils.fieldError(result, "signUpForm", "userName", "NotUnique.signUpForm.userName");
			}
			if (!isValid(synapseClient, AliasType.USER_EMAIL, newUser.getEmail())) {
				ClientUtils.fieldError(result, "signUpForm", "email", "NotUnique.signUpForm.email");
			}
			if (!result.hasErrors()) {
				synapseClient.createUser(newUser);
				request.setNotification("RegistrationEmailSent");
				return "redirect:"+request.getOrigin();
			}
		}
		return "auth/signUp";
	}
	
	private boolean isValid(SynapseClient client, AliasType type, String value) throws SynapseException {
		AliasCheckRequest request = new AliasCheckRequest();
		request.setAlias(value);
		request.setType(type);
		AliasCheckResponse response = client.checkAliasAvailable(request);
		return response.getAvailable();
	}
	
}
