package org.sagebionetworks.bridge.webapp.controllers.communities;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
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
	public String get(HttpSession session, @PathVariable("communityId") String communityId, ModelMap map) {
	    BridgeUser user = (BridgeUser)session.getAttribute(BridgeUser.KEY);
	    try {
	        
	        // So, this works, but it returns JSON, which is like: why did I go to all the trouble to include
	        // the client if it's just going to return JSONObject? 
	        JSONObject response = user.getSynapseClient().query("select * from project where createdByPrincipalId == " + user.getOwnerId());
	        JSONArray array = response.getJSONArray("results");
	        map.addAttribute("projects", array);
	        
	    } catch(Throwable t) {
	        // Need to think through an error-handling strategy, maybe humane.js with a set key.
	    }
		
		return "communities/index";
	}
	
}
