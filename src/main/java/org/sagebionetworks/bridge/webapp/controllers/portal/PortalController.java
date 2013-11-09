package org.sagebionetworks.bridge.webapp.controllers.portal;

import java.util.List;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/portal/")
public class PortalController {

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public ModelAndView index(BridgeRequest request, ModelAndView model) {
		/*
		try {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			List<Community> communities = client.getCommunities();
			model.addObject("communities", communities);
		} catch(SynapseException e) {
			// This just can'te be let through on the home screen...
		}
		*/
		model.setViewName("portal/index");
		return model;
	}

}
