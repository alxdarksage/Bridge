package org.sagebionetworks.bridge.webapp.controllers;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/signIn")
public class SignInController {
	
	private static final Logger logger = LogManager.getLogger(SignInController.class.getName());

	@RequestMapping(method = RequestMethod.GET)
	public String get(@ModelAttribute SignInForm signInForm) {
		return "signIn";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute @Valid SignInForm signInForm, BindingResult result) {
		if (result.hasErrors()) {
			return "signIn";	
		}
		// But we would go somewhere else, probably wherever the user came from.
		return "signIn";
	}

}
