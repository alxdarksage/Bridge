package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// TODO: So this is getting to be a pretty fat controller. It seems silly to create an 
// application facade over the client over the rest service over the service object 
// over the manager (over the DAO), so I'm leaving it for now.

@Controller
@RequestMapping("/admin")
public class CommunityAdminController {
	
	private static Logger logger = LogManager.getLogger(CommunityAdminController.class.getName());

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("communityForm")
	public CommunityForm communityForm() {
		return new CommunityForm();
	}
	
	@RequestMapping(value = "/communities", method = RequestMethod.GET)
	public ModelAndView viewCommunities(BridgeRequest request) throws SynapseException {
		ModelAndView map = new ModelAndView("admin/communities");
		
		List<Community> communities = new ArrayList<>();
		PaginatedResults<Community> results = request.getBridgeUser().getBridgeClient().getAllCommunities(ClientUtils.LIMIT, 0);
		for (Community community : results.getResults()) {
			UserEntityPermissions permits = ClientUtils.getPermits(request, community.getId());
			if (permits.getCanEdit()) {
				communities.add(community);
			}
		}
		map.addObject("communities", communities);
		return map;
	}
	
	// It's possible through the submit button name to have another method to process another 
	// table-specific button in the same form. Very handy.
	@RequestMapping(value = "/communities", method = RequestMethod.POST, params = "delete=delete")
	public String batchCommunities(BridgeRequest request, @RequestParam("rowSelect") List<String> rowSelects)
			throws SynapseException {
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			
			int count = 0;
			for (String id : rowSelects) {
				client.deleteCommunity(id);
				count++;
			}
			if (count == 1) {
				request.setNotification("CommunityDeleted");
			} else if (count > 1) {
				request.setNotification("CommunitiesDeleted");
			}
		}
		return "redirect:/admin/communities.html";
	}
	
	@RequestMapping(value = "/communities/new", method = RequestMethod.GET)
	public ModelAndView newCommunity() {
		ModelAndView map = new ModelAndView("admin/community");
		map.addObject("formId","new");
		return map;
	}
	
	@RequestMapping(value = "/communities/new", method = RequestMethod.POST)
	public ModelAndView createCommunity(BridgeRequest request, @ModelAttribute @Valid CommunityForm communityForm,
			BindingResult result, ModelAndView map) throws SynapseException {
		map.setViewName("admin/community");
		if (!result.hasErrors()) {
			try {
				BridgeClient client = request.getBridgeUser().getBridgeClient();
				Community community = FormUtils.valuesToCommunity(new Community(), communityForm);
				client.createCommunity(community);
				map.setViewName("redirect:/admin/communities.html");
				request.setNotification("CommunityCreated");
			} catch(SynapseException e) {
				String message = ClientUtils.parseSynapseException(e, 500, "Invalid Entity name");
				ClientUtils.fieldError(result, "communityForm", "name", message);
			}
		}
		return map;
	}
	
	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.GET)
	public ModelAndView viewCommunity(BridgeRequest request, @PathVariable String communityId,
			CommunityForm communityForm, ModelAndView map) throws SynapseException {
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		FormUtils.valuesToCommunityForm(communityForm, community);
		communityForm.setId(community.getId());
		/*
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		AccessControlList acl = client.getACL(community.getId());
		PaginatedResults<TeamMember> members = client.getTeamMembers(community.getTeamId(), null, 10000, 0);
		
		List<CheckboxItem> items = new ArrayList<>();
		for (TeamMember member : members.getResults()) {
			UserGroupHeader user = member.getMember();
			CheckboxItem ci = new CheckboxItem(user.getDisplayName(), user.getOwnerId());
			if (isUserAdmin(acl, user.getOwnerId())) {
				ci.setSelected(true);
			}
			items.add(ci);
		}
		map.addObject("editors", items);
		*/
		map.setViewName("admin/community");
		
		return map;
	}
	
	// TODO This should be in the CommunityManager
	/*
	private boolean isUserAdmin(AccessControlList acl, String userId) {
		for (ResourceAccess ra : acl.getResourceAccess()) {
			if (ra.getPrincipalId().toString().equals(userId) &&
				ra.getAccessType().contains(ACCESS_TYPE.UPDATE)) {
				return true;
			}
		}
		return false;
	}
	*/
	
	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.POST)
	public ModelAndView updateCommunity(BridgeRequest request, @PathVariable String communityId,
			@ModelAttribute @Valid CommunityForm communityForm, BindingResult result, ModelAndView map,
			@RequestParam(value = "editors", required = false) List<String> editors) throws SynapseException {
		
		map.setViewName("admin/community");
		if (!result.hasErrors()) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			
			Community community = client.getCommunity(communityId);
			FormUtils.valuesToCommunity(community, communityForm);
			client.updateCommunity(community);
			map.setViewName("redirect:/admin/communities.html");
		}
		return map;
	}
	
}
