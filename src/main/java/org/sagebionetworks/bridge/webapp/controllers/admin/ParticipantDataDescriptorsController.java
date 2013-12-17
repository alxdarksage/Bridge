package org.sagebionetworks.bridge.webapp.controllers.admin;

import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class ParticipantDataDescriptorsController {


	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(value = "/descriptors", method = RequestMethod.GET)
	public String viewParticipantDataDescriptors() {
		return "admin/descriptors";
	}
	
}
