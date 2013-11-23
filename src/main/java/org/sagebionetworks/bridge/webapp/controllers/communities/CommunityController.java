package org.sagebionetworks.bridge.webapp.controllers.communities;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/communities")
public class CommunityController {
	
	private static Logger logger = LogManager.getLogger(CommunityController.class.getName());

	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;
	
	public void setBridgeClient(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("communityForm")
	public CommunityForm communityForm() {
		return new CommunityForm();
	}

	@RequestMapping(value = "/{communityId}", method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, @PathVariable("communityId") String communityId, ModelAndView model) throws Exception {
		Community community = bridgeClient.getCommunity(communityId);
		model.addObject("community", community);
		model.setViewName("communities/index");
		
		if (request.isUserAuthenticated()) {
			UserEntityPermissions permits = ClientUtils.getPermits(request, community.getId());
			model.addObject("editable", permits.getCanEdit());

			Team team = synapseClient.getTeam(community.getTeamId());
			TeamMembershipStatus status = synapseClient.getTeamMembershipStatus(team.getId(), request.getBridgeUser().getOwnerId());
			model.addObject("joinable", !status.getIsMember());
		} else {
			model.addObject("editable", false);
			model.addObject("joinable", false);
		}
		return model;
	}

	@RequestMapping(value = "/{communityId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model, CommunityForm communityForm) throws Exception {
		Community community = bridgeClient.getCommunity(communityId);
		model.addObject("community", community);
		model.setViewName("communities/edit");
		FormUtils.valuesToCommunityForm(communityForm, community);
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/edit", method = RequestMethod.POST)
	public ModelAndView update(BridgeRequest request, @PathVariable("communityId") String communityId,
			@ModelAttribute CommunityForm communityForm, BindingResult result, ModelAndView map)
			throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Community community = client.getCommunity(communityId);
		map.setViewName("communities/edit");
		map.addObject("community", community);

		// There are no errors that can occur here, actually.
		if (!result.hasErrors()) {
			String sanitizedHTML = Jsoup.clean(communityForm.getDescription(), getCustomWhitelist());
			community.setDescription(sanitizedHTML);
			client.updateCommunity(community);

			map.setViewName("redirect:/communities/" + community.getId() + ".html");
			request.setNotification("CommunityUpdated");
		}
		return map;
	}
	
	@RequestMapping(value = "/{communityId}/join", method = RequestMethod.GET)
	public String join(BridgeRequest request, @PathVariable("communityId") String communityId) throws Exception {
		// We could verify that the user hasn't already joined, but why.
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		client.joinCommunity(communityId);
		
		request.setNotification("JoinedCommunity");
		return "redirect:/communities/"+communityId+".html";
	}
	
	private Whitelist getCustomWhitelist() {
		return Whitelist.relaxed()
			.addAttributes(":all", "class", "style", "width");
	}

}
