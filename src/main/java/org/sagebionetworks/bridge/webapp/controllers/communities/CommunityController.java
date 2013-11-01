package org.sagebionetworks.bridge.webapp.controllers.communities;

import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/communities")
public class CommunityController {

    @ModelAttribute("signInForm")
    public SignInForm signInForm() {
        return new SignInForm();
    }
    
	@RequestMapping(value = "/{communityId}", method = RequestMethod.GET)
	public String get(BridgeRequest request, @PathVariable("communityId") String communityId, ModelMap map) {
		return "communities/index";
	}
	
}
