package org.sagebionetworks.bridge.webapp.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.CheckboxItem;
import org.sagebionetworks.bridge.webapp.forms.ProfileForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	private static final Logger logger = LogManager.getLogger(ProfileController.class.getName());
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, ModelAndView model) throws Exception {
		SynapseClient synapseClient = request.getBridgeUser().getSynapseClient();
		BridgeClient bridgeClient = request.getBridgeUser().getBridgeClient();
		
		UserProfile profile = synapseClient.getUserProfile(request.getBridgeUser().getOwnerId());
		ProfileForm form = new ProfileForm();
		BeanUtils.copyProperties(profile, form);
		
		// This is very gross, but I could not get form:checkboxes to work and the documentation 
		// on it is minimal.
		List<Community> allCommunities = bridgeClient.getCommunities();
		model.addObject("communities", allCommunities);

		List<Community> memberships = bridgeClient.getCommunitiesByMember();
		List<CheckboxItem> items = new ArrayList<>();
		for (Community community : allCommunities) {
			CheckboxItem ci = new CheckboxItem(community.getName(), community.getId());
			if (memberships.contains(community)) {
				ci.setSelected(true);
			}
			items.add(ci);
		}
		model.addObject("memberships", items);
		
		model.addObject("profileForm", form);
		model.setViewName("profile");
		return model;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(BridgeRequest request, ProfileForm profileForm, BindingResult result, 
			@RequestParam(value = "memberships", required = false) List<String> memberships) throws Exception {
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		BridgeClient bridgeClient = request.getBridgeUser().getBridgeClient();

		// Doesn't want to be a member of any community.
		if (memberships == null) {
			memberships = Collections.emptyList();
		}
		List<Community> allCommunities = bridgeClient.getCommunities();
		List<String> currentMemberships = getMembershipIds(bridgeClient);
		
		for (Community community : allCommunities) {
			String id = community.getId();
			if (memberships.contains(id) && !currentMemberships.contains(id)) {
				bridgeClient.joinCommunity(id);
			} else if (!memberships.contains(id) && currentMemberships.contains(id)) {
				bridgeClient.leaveCommunity(id);
			}
		}
		
		String userId = request.getBridgeUser().getOwnerId();
		
		// Update the non-file content
		UserProfile oldProfile = client.getUserProfile(userId);
		BeanUtils.copyProperties(profileForm, oldProfile);
		client.updateMyProfile(oldProfile);
		
		request.setNotification("ProfileUpdated");
		return "redirect:"+request.getOrigin();
	}
	
	
	private List<String> getMembershipIds(BridgeClient client) throws SynapseException {
		List<String> list = new ArrayList<>();
		List<Community> memberships = client.getCommunitiesByMember();
		for (Community community : memberships) {
			list.add(community.getId());
		}
		return list;
	}
	
}
