package org.sagebionetworks.bridge.webapp.controllers.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/portal/")
public class PortalController {

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index() {
		return "portal/index";
	}
	
}
