package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * /admin/communities.html GET OR POST TO DELETE
 * /admin/communities/1.html GET OR POST TO UPDATE
 * /admin/communities/new.html GET OR POST TO CREATE
 * 
 * @author alxdark
 *
 */

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
		List<Community> communities = request.getBridgeUser().getBridgeClient().getCommunities();
		map.addObject("communities", communities);
		return map;
	}
	
	// It's possible through the submit button name to have another method to process another 
	// table-specific button in the same form. Very handy.
	@RequestMapping(value = "/communities", method = RequestMethod.POST, params = "delete=delete")
	public String batchCommunities(BridgeRequest request, @RequestParam("rowSelect") List<String> rowSelects) {
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			int count = 0;
			for (String id : rowSelects) {
				// .... and you can't delete a community.
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
			BindingResult result, ModelAndView map) throws Exception {
		if (result.hasErrors()) {
			map.setViewName("admin/community");
		} else {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			Community community = createCommunity(communityForm, request.getBridgeUser().getDisplayName());
			client.createCommunity(community);
			map.setViewName("redirect:/admin/communities.html");
		}
		return map;
	}
	
	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.GET)
	public String viewCommunity(BridgeRequest request, @PathVariable String communityId, CommunityForm communityForm) throws SynapseException {
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		editCommunity(community, communityForm);
		return "admin/community";
	}
	
	@RequestMapping(value = "/communities/{communityId}", method = RequestMethod.POST)
	public ModelAndView updateCommunity(BridgeRequest request, @PathVariable String communityId,
			@ModelAttribute @Valid CommunityForm communityForm, BindingResult result, ModelAndView map) throws SynapseException {
		
		if (result.hasErrors()) {
			map.setViewName("admin/community");
		} else {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
			Community community = client.getCommunity(communityId);
			modifyCommunity(community, communityForm, request.getBridgeUser().getDisplayName());
			client.updateCommunity(community);
			map.setViewName("redirect:/admin/communities.html");
		}
		return map;
	}
	
	private Community createCommunity(CommunityForm communityForm, String displayName) throws IllegalAccessException, InvocationTargetException {
		Community community = new Community();
		community.setCreatedOn(new Date());
		community.setCreatedBy(displayName);
		modifyCommunity(community, communityForm, displayName);
		return community;
	}
	
	private void modifyCommunity(Community community, CommunityForm communityForm, String displayName) {
		// There is no common interface for this that I can see.
		community.setName(communityForm.getName());
		community.setDescription(communityForm.getDescription());
		community.setModifiedBy(displayName);
		community.setModifiedOn(new Date());
	}
	
	private void editCommunity(Community community, CommunityForm communityForm) {
		if (community != null) {
			try {
				BeanUtils.copyProperties(communityForm, community);	
			} catch(Exception e) {
				logger.error(e);
			}
		}
	}
		
	
}
