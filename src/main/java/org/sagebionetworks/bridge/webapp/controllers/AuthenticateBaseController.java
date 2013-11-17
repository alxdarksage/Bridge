package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticateBaseController {
	
	@Autowired
	protected BeanFactory beanFactory;

	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
	
	protected BridgeUser createBridgeUserFromUserSessionData(UserSessionData data) {
		BridgeUser user = (BridgeUser) beanFactory.getBean("bridgeUser");
		user.setDisplayName(data.getProfile().getDisplayName());
		user.setOwnerId(data.getProfile().getOwnerId());
		user.setSessionToken(data.getSessionToken());
		user.setCommunityId("index"); // hard-coded for the moment.
		return user;
	}
	
	protected String getOnSuccessPage(SignInForm signInForm, BridgeRequest request) {
		return request.getOrigin();
	}
	
	protected String getOnErrorReturnPage(SignInForm signInForm, BridgeRequest request) {
		if (StringUtils.isBlank(request.getServletPath())) {
			return ""; // test only, I believe
		}
		return request.getServletPath().substring(1).replace(".html", "");
	}

}
