package org.sagebionetworks.bridge.webapp.controllers;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.CheckboxItem;
import org.sagebionetworks.bridge.webapp.forms.ProfileForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.UserProfile;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	private static final Logger logger = LogManager.getLogger(ProfileController.class.getName());
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("communities")
	public List<Community> allCommunities(BridgeRequest request) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<Community> allResults = client.getAllCommunities(ClientUtils.LIMIT, 0);
		return allResults.getResults();
	}
	
	@ModelAttribute("memberships")
	public List<CheckboxItem> memberships(BridgeRequest request,
			@ModelAttribute("communities") List<Community> communities) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<Community> memberships = client.getCommunities(ClientUtils.LIMIT, 0);
		
		List<CheckboxItem> items = Lists.newArrayList();
		for (Community community : communities) {
			CheckboxItem ci = new CheckboxItem(HtmlUtils.htmlEscape(community.getName()), community.getId());
			if (memberships.getResults().contains(community)) {
				ci.setSelected(true);
			}
			items.add(ci);
		}
		return items;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, ModelAndView model, @ModelAttribute ProfileForm profileForm,
			@ModelAttribute("communities") List<Community> communities) throws Exception {
		
		SynapseClient synapseClient = request.getBridgeUser().getSynapseClient();
		UserProfile profile = synapseClient.getUserProfile(request.getBridgeUser().getOwnerId());
		FormUtils.valuesToProfileForm(profileForm, profile);
		
		model.setViewName("profile");
		return model;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(BridgeRequest request, @ModelAttribute @Valid ProfileForm profileForm,
			BindingResult result, @RequestParam(value = "memberships", required = false) List<String> memberships,
			ModelAndView model, @ModelAttribute("communities") List<Community> communities,
			@ModelAttribute("memberships") List<CheckboxItem> existingMemberships) throws SynapseException {
		
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		BridgeClient bridgeClient = request.getBridgeUser().getBridgeClient();
		model.setViewName("profile");
		
		if (!result.hasErrors()) {
			try {
				// Doesn't want to be a member of any community, empty checkboxes.
				if (memberships == null) {
					memberships = Collections.emptyList();
				}
				List<String> currentMemberships = getMembershipIds(existingMemberships);
				
				for (Community community : communities) {
					String id = community.getId();
					if (memberships.contains(id) && !currentMemberships.contains(id)) {
						bridgeClient.joinCommunity(id);
					} else if (!memberships.contains(id) && currentMemberships.contains(id)) {
						bridgeClient.leaveCommunity(id);	
					}
				}
				
				// Update the non-community membership
				String userId = request.getBridgeUser().getOwnerId();
				UserProfile oldProfile = client.getUserProfile(userId);
				FormUtils.valuesToUserProfile(oldProfile, profileForm);
				client.updateMyProfile(oldProfile);
				
				request.setNotification("ProfileUpdated");
				model.setViewName("redirect:"+request.getOrigin());			
			} catch (SynapseException e) {
				String message = ClientUtils.parseSynapseException(e, 401, "Need at least one admin");
				ClientUtils.fieldError(result, "profileForm", "memberships", message);
			}
		}
		return model;
	}
	
	private List<String> getMembershipIds(List<CheckboxItem> existingMemberships) throws SynapseException {
		List<String> list = Lists.newArrayList();
		for (CheckboxItem community : existingMemberships) {
			if (community.isSelected()) {
				list.add(community.getId());	
			}
		}
		return list;
	}
	
}
