package org.sagebionetworks.bridge.portal.controllers;

import javax.validation.Valid;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sagebionetworks.bridge.portal.forms.SignInForm;
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
	public String get(@ModelAttribute SignInForm signInForm) throws Exception {
		logger.debug("GET: This logging statement is actually not being shown or configured correctly.");
		return "signIn";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute @Valid SignInForm signInForm, BindingResult result) {
		logger.debug("POST: This logging statement is actually not being shown or configured correctly.");
		
		if (result.hasErrors()) {
			return "signIn";	
		}
		// But we would go somewhere else, probably wherever the user came from.
		return "signIn";
	}

}
