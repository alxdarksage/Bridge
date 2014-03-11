package org.sagebionetworks.bridge.webapp.controllers.portal;

import javax.annotation.Resource;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.controllers.NonAjaxControllerBase;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PortalController extends NonAjaxControllerBase {
	
	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	@RequestMapping(value = {"/index", "/portal/index"}, method = RequestMethod.GET)
	public ModelAndView index(ModelAndView model) throws SynapseException {
		PaginatedResults<Community> results = bridgeClient.getAllCommunities(ClientUtils.LIMIT, 0);
		model.addObject("communities", results.getResults());
		model.setViewName("portal/index");
		return model;
	}

}
