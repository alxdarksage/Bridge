package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/signIn")
public class SignInController {

	private static final Logger logger = LogManager.getLogger(SignInController.class.getName());

	@Autowired
	private BeanFactory beanFactory;

	@Resource(name = "synapseClient")
	private SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(@ModelAttribute SignInForm signInForm, BridgeRequest request) {
		if (request.isUserAuthenticated()) {
			return request.getBridgeUser().getStartURL();
		}
		return "signIn";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute @Valid SignInForm signInForm, BindingResult result, BridgeRequest request)
			throws SynapseException {
		if (result.hasErrors()) {
			return getOnErrorReturnPage(signInForm, request);
		}
		// Shouldn't happen that user is authenticated, but.
		if (!request.isUserAuthenticated()) {
			try {

				// They accept the terms of use when creating their account,
				// they do not need to do it here.
				UserSessionData userSessionData = synapseClient.login(signInForm.getEmail(),
						signInForm.getPassword(), true);
				BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
				request.setBridgeUser(user);
				logger.info("User #{} signed in.", user.getOwnerId());
				
			} catch (SynapseException e) {
				ClientUtils.globalFormError(result, "signInForm", "IncorrectLogin");
				return getOnErrorReturnPage(signInForm, request);
			}
		}
		return getOnSuccessPage(signInForm, request);
	}
	
	private String getOnErrorReturnPage(SignInForm signInForm, BridgeRequest request) {
		if (signInForm.getErrorView() == null) {
			return "redirect:"+request.getOriginURL()+"?login=error";
		}
		return signInForm.getErrorView();
	}

	private String getOnSuccessPage(SignInForm signInForm, BridgeRequest request) {
		if (("signIn".equals(signInForm.getErrorView()) || "signedOut".equals(signInForm.getErrorView()))) {
			return request.getBridgeUser().getStartURL();
		}
		return "redirect:" + request.getOriginURL();
	}
	
	private BridgeUser createBridgeUserFromUserSessionData(UserSessionData data) {
		BridgeUser user = (BridgeUser) beanFactory.getBean("bridgeUser");
		user.setDisplayName(data.getProfile().getDisplayName());
		user.setOwnerId(data.getProfile().getOwnerId());
		user.setSessionToken(data.getSessionToken());
		user.setCommunityId("index"); // hard-coded for the moment.
		return user;
	}

}
