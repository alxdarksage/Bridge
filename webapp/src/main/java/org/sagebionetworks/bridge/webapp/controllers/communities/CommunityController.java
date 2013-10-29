package org.sagebionetworks.bridge.webapp.controllers.communities;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/communities")
public class CommunityController {

	@RequestMapping(value = "/{communityId}", method = RequestMethod.GET)
	public String get(@PathVariable("communityId") String communityId) {
			
		// We would use the ID which is presumably a Synapse ID, and therefore a string.
		
		return "communities/index";
	}
	
}
