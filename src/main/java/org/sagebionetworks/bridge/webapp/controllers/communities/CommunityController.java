package org.sagebionetworks.bridge.webapp.controllers.communities;

import java.util.List;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.ProfileForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/communities")
public class CommunityController {

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, ModelAndView model) throws Exception {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		List<Community> communities = client.getCommunities();
		model.addObject("communities", communities);
		model.setViewName("profile");
		return model;
	}

	@RequestMapping(value = "/{communityId}", method = RequestMethod.GET)
	public String get(BridgeRequest request, @PathVariable("communityId") String communityId, ModelMap map) throws Exception {
		return "communities/index";
	}

}
