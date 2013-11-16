package org.sagebionetworks.bridge.webapp.controllers.portal;

import java.util.List;

import javax.annotation.Resource;

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
	
	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	public void setBridgeClient(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}
	
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public ModelAndView index(BridgeRequest request, ModelAndView model) throws SynapseException {
		List<Community> communities = bridgeClient.getCommunities();
		model.addObject("communities", communities);
		model.setViewName("portal/index");
		return model;
	}

}
