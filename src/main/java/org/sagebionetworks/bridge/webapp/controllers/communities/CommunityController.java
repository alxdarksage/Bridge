package org.sagebionetworks.bridge.webapp.controllers.communities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.controllers.NonAjaxControllerBase;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CommunityController extends NonAjaxControllerBase {
	
	private static Logger logger = LogManager.getLogger(CommunityController.class.getName());

	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	public void setBridgeClient(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}

	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
	
	@ModelAttribute("communityForm")
	public CommunityForm communityForm() {
		return new CommunityForm();
	}
	
	/**
	 * Not currently in the UI, but it allows us to easily retrieve a community in tests.
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/community", method = RequestMethod.GET)
	public ModelAndView getUsersDefaultCommunity(BridgeRequest request, ModelAndView model) throws Exception {
		List<Community> communities = request.getBridgeUser().getCommunities();
		if (communities != null && !communities.isEmpty()) {
			String communityId = communities.get(0).getId();
			prepareIndexPage(request, model, communityId, null);
		} else {
			// Not logged in, just show the first one returned.
			communities = bridgeClient.getAllCommunities(ClientUtils.LIMIT, 0L).getResults();
			if (communities != null && !communities.isEmpty()) {
				String communityId = communities.get(0).getId();
				prepareIndexPage(request, model, communityId, null);
			}
		}
		return model;
	}

	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, @PathVariable("communityId") String communityId, ModelAndView model)
			throws Exception {
		
		prepareIndexPage(request, model, communityId, null);
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}", method = RequestMethod.GET)
	public ModelAndView viewWiki(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, ModelAndView model) throws Exception {
		
		prepareIndexPage(request, model, communityId, wikiId);
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/join", method = RequestMethod.GET)
	public String join(BridgeRequest request, @PathVariable("communityId") String communityId) throws Exception {
		// We could verify that the user hasn't already joined, but why.
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		client.joinCommunity(communityId);
		
		request.setNotification("JoinedCommunity");
		return "redirect:/communities/"+communityId+".html";
	}
	
	private void prepareIndexPage(BridgeRequest request, ModelAndView model, String communityId, String wikiId)
			throws SynapseException, ClientProtocolException, FileNotFoundException, IOException,
			JSONObjectAdapterException {
		
		Community community = bridgeClient.getCommunity(communityId);
		ClientUtils.prepareCommunitySidebarData(synapseClient, community, model);
		
		model.setViewName("communities/show");
		
		if (wikiId == null) {
			wikiId = community.getWelcomePageWikiId();
		}
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wikiId);
		String markdown = synapseClient.downloadV2WikiMarkdown(key);
		model.addObject("wikiContent", markdown);
		model.addObject("wikiId", wikiId);

		if (request.isUserAuthenticated()) {
			UserEntityPermissions permits = ClientUtils.getPermits(request, community.getId());
			model.addObject("editable", permits.getCanEdit());

			SynapseClient synapseClient = request.getBridgeUser().getSynapseClient();
			Team team = synapseClient.getTeam(community.getTeamId());
			TeamMembershipStatus status = synapseClient.getTeamMembershipStatus(team.getId(), request.getBridgeUser().getOwnerId());
			model.addObject("joinable", !status.getIsMember());
		} else {
			model.addObject("editable", false);
			model.addObject("joinable", false);
		}
	}
}
