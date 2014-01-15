package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticateBaseController {

	private static final Logger logger = LogManager.getLogger(AuthenticateBaseController.class.getName());
	
	@Autowired
	protected BeanFactory beanFactory;

	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	protected BridgeUser createBridgeUserFromUserSessionData(UserSessionData data) {
		BridgeUser user = (BridgeUser) beanFactory.getBean("bridgeUser");
		user.setUserName(data.getProfile().getUserName());
		user.setOwnerId(data.getProfile().getOwnerId());
		user.setSessionToken(data.getSession().getSessionToken());
		user.setCommunityId("index"); // hard-coded for the moment.
		return user;
	}
	
	protected String getOnSuccessPage(SignInForm signInForm, BridgeRequest request) {
		// This would be carried over from the sign out page, even as the user's 
		// session has been destroyed.
		if (StringUtils.isNotBlank(request.getParameter("origin"))) {
			return request.getParameter("origin");
		}
		return request.getOrigin();
	}
	
	protected String getOnErrorReturnPage(SignInForm signInForm, BridgeRequest request) {
		/*
		if (StringUtils.isBlank(request.getServletPath())) {
			return ""; // test only, I believe
		}
		*/
		return "auth/signIn";
	}

}
