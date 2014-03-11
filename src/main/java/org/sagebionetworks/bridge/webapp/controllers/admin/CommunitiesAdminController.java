package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.controllers.NonAjaxControllerBase;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

@Controller
@RequestMapping("/admin")
public class CommunitiesAdminController extends NonAjaxControllerBase {
	
	private static Logger logger = LogManager.getLogger(CommunityAdminController.class.getName());

	@ModelAttribute("communities")
	public List<Community> communities(BridgeRequest request) throws SynapseException {
	        BridgeClient client = request.getBridgeUser().getBridgeClient();
	        PaginatedResults<Community> results = client.getAllCommunities(ClientUtils.LIMIT, 0);

	        List<Community> communities = Lists.newArrayList();
	        for (Community community : results.getResults()) {
	                UserEntityPermissions permits = ClientUtils.getPermits(request, community.getId());
	                if (permits.getCanEdit()) {
	                        communities.add(community);
	                }
	        }
	        return communities;
	}	
	
	@RequestMapping(value = "/communities/index", method = RequestMethod.GET)
	public String viewCommunities(BridgeRequest request,
			@ModelAttribute("communities") List<Community> communities) throws SynapseException {
		return "admin/communities/index";
	}
	
	// It's possible through the submit button name to have another method to process another 
	// table-specific button in the same form. Very handy.
	@RequestMapping(value = "/communities/index", method = RequestMethod.POST, params = "delete=delete")
	public String batchCommunities(BridgeRequest request, @RequestParam("rowSelect") List<String> rowSelects)
			throws SynapseException {
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			for (String id : rowSelects) {
				client.deleteCommunity(id);
			}
			request.setNotification( rowSelects.size() > 1 ? "CommunitiesDeleted" : "CommunityDeleted" );
		}
		return "redirect:/admin/communities/index.html";
	}
	
}
