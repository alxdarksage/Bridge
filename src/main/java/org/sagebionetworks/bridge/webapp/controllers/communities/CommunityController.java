package org.sagebionetworks.bridge.webapp.controllers.communities;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.controllers.admin.CommunityAdminController;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
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
		
		// Hmm.
		AccessControlList acl = synapseClient.getACL(community.getId());
		model.addObject("editable", ClientUtils.can(ACCESS_TYPE.UPDATE, acl));
		
		return model;
	}

	@RequestMapping(value = "/{communityId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model, CommunityForm communityForm) throws Exception {
		Community community = bridgeClient.getCommunity(communityId);
		model.addObject("community", community);
		model.setViewName("communities/edit");
		
		// Maybe this is a static method somewhere. ClientUtils?
		CommunityAdminController admin = new CommunityAdminController();
		admin.valuesToCommunityForm(communityForm, community);
		
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/edit", method = RequestMethod.POST)
	public ModelAndView save(BridgeRequest request, @PathVariable("communityId") String communityId,
			@ModelAttribute CommunityForm communityForm, BindingResult result, ModelAndView map)
			throws SynapseException {
		
		Community community = bridgeClient.getCommunity(communityId);
		map.setViewName("communities/edit");
		map.addObject("community", community);
		
		ClientUtils.dumpErrors(logger, result);
		
		// There are no errors that can occur here, actually.
		if (!result.hasErrors()) {
			community.setDescription(communityForm.getDescription());
			bridgeClient.updateCommunity(community);
			
			map.setViewName("redirect:/communities/" + community.getId() + ".html");
			request.setNotification("CommunityUpdated");
		}
		return map;
	}

}
