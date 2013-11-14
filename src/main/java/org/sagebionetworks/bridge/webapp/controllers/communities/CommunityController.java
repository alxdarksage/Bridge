package org.sagebionetworks.bridge.webapp.controllers.communities;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.springframework.stereotype.Controller;
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

	@RequestMapping(value = "/{communityId}", method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, @PathVariable("communityId") String communityId, ModelAndView model) throws Exception {
		//Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		//model.addObject("community", community);
		
		Community c1 = new Community();
		c1.setId("syn1");
		c1.setName("Fanconia Anemia");
		c1.setDescription("This is very rare genetic disorder");
		
		Community c2 = new Community();
		c2.setId("syn2");
		c2.setName("Diabetes (Type II)");
		c2.setDescription("This disease can be caused by lifestyle factors");
		
		List<Community> communities = new ArrayList<>();
		communities.add(c1);
		communities.add(c2);
		model.addObject("communities", communities);
		
		model.setViewName("communities/index");
		return model;
	}

}
