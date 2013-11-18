package org.sagebionetworks.bridge.webapp.controllers.communities;

import javax.annotation.Resource;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/communities")
public class CommunityController {

	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	public void setBridgeClient(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(value = "/{communityId}", method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, @PathVariable("communityId") String communityId, ModelAndView model) throws Exception {
		Community community = bridgeClient.getCommunity(communityId);
		model.addObject("community", community);
		model.setViewName("communities/index");
		return model;
	}

}
