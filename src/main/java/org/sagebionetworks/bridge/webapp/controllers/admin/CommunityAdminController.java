package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.CheckboxItem;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	
	@RequestMapping(value = "/communities/new", method = RequestMethod.GET)
	public String newCommunity(@ModelAttribute("communityForm") CommunityForm communityForm) {
		return "admin/community";
	}
	
	@RequestMapping(value = "/communities/new", method = RequestMethod.POST)
	public ModelAndView createCommunity(BridgeRequest request, @ModelAttribute @Valid CommunityForm communityForm,
			BindingResult result, ModelAndView model) throws SynapseException {
		model.setViewName("admin/community");
		if (!result.hasErrors()) {
			try {
				BridgeClient client = request.getBridgeUser().getBridgeClient();
				Community community = FormUtils.valuesToCommunity(new Community(), communityForm);
				client.createCommunity(community);
				model.setViewName("redirect:/admin/communities.html");
				request.setNotification("CommunityCreated");
			} catch(SynapseException e) {
				String message = ClientUtils.parseSynapseException(e, 500, "Invalid Entity name");
				ClientUtils.fieldError(result, "communityForm", "name", message);
			}
		}
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.GET)
	public ModelAndView viewCommunity(BridgeRequest request, @PathVariable String communityId,
			CommunityForm communityForm, ModelAndView map) throws SynapseException {
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		FormUtils.valuesToCommunityForm(communityForm, community);
		communityForm.setId(community.getId());

		map.addObject("administrators", getAdministrators(request, communityId));
		map.setViewName("admin/community");
		
		return map;
	}
	
	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.POST)
	public ModelAndView updateCommunity(BridgeRequest request, @PathVariable String communityId,
			@ModelAttribute @Valid CommunityForm communityForm, BindingResult result, ModelAndView map,
			@RequestParam(value = "administrators", required = false) List<String> administrators) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		map.setViewName("admin/community");

		if (!result.hasErrors()) {
			if (administrators == null) {
				administrators = Collections.emptyList();
			}
			List<String> members = getMemberIds(request, communityId);
			List<String> currentAdministrators = getAdministratorIds(request, communityId);
			try {
				// Add, then remove, or you can get strange error conditions based on intermediate 
				// state of having no one assigned.
				for (String memberId : members) {
					if (administrators.contains(memberId) & !currentAdministrators.contains(memberId)) {
						client.addCommunityAdmin(communityId, memberId);
					}
				}
				for (String memberId : members) {
					if (!administrators.contains(memberId) & currentAdministrators.contains(memberId)) {
						client.removeCommunityAdmin(communityId, memberId);
					}
				}
			} catch(SynapseException e) {
				String message = ClientUtils.parseSynapseException(e, 401);
				ClientUtils.fieldError(result, "communityForm", "administrators", message);
				map.addObject("administrators", getAdministrators(request, communityId));
				return map;
			}			

			Community community = client.getCommunity(communityId);
			FormUtils.valuesToCommunity(community, communityForm);
			client.updateCommunity(community);
			map.setViewName("redirect:/admin/communities.html");
		}
		return map;
	}
	
	private List<CheckboxItem> getAdministrators(BridgeRequest request, String communityId) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		SynapseClient synapseClient = request.getBridgeUser().getSynapseClient();
		
		List<UserGroupHeader> members = client.getCommunityMembers(communityId, ClientUtils.LIMIT, 0).getResults();
		AccessControlList acl = synapseClient.getACL(communityId);
		
		List<CheckboxItem> items = new ArrayList<>();
		for (UserGroupHeader member : members) {
			logger.info("Community has this member: " + member.getDisplayName());
			CheckboxItem ci = new CheckboxItem(member.getDisplayName(), member.getOwnerId());
			if (isUserAdmin(acl, member.getOwnerId())) {
				ci.setSelected(true);
			}
			items.add(ci);
		}
		return items;
	}
	
	private List<String> getAdministratorIds(BridgeRequest request, String communityId) throws SynapseException {
		List<CheckboxItem> items = getAdministrators(request, communityId);
		List<String> list = new ArrayList<>();
		for (CheckboxItem item : items) {
			if (item.isSelected()) {
				list.add(item.getId());
			}
		}
		return list;
	}
	
	private List<String> getMemberIds(BridgeRequest request, String communityId) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		List<String> list = new ArrayList<>();
		List<UserGroupHeader> members = client.getCommunityMembers(communityId, ClientUtils.LIMIT, 0).getResults();
		for (UserGroupHeader member : members) {
			list.add(member.getOwnerId());
		}
		return list;
	}
	
	private boolean isUserAdmin(AccessControlList acl, String userId) {
		for (ResourceAccess ra : acl.getResourceAccess()) {
			if (ra.getPrincipalId().toString().equals(userId) &&
				ra.getAccessType().contains(ACCESS_TYPE.UPDATE)) { // or permissions change?
				return true;
			}
		}
		return false;
	}

}
