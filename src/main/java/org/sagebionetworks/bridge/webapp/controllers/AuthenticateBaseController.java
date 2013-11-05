package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;

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
	
	protected String getOnErrorReturnPage(SignInForm signInForm, BridgeRequest request) {
		if (signInForm.getErrorView() == null) {
			return "redirect:"+request.getOriginURL()+"?login=error";
		}
		return signInForm.getErrorView();
	}

	protected String getOnSuccessPage(SignInForm signInForm, BridgeRequest request) {
		if (("signIn".equals(signInForm.getErrorView()) || "signedOut".equals(signInForm.getErrorView()))) {
			return request.getBridgeUser().getStartURL();
		}
		return "redirect:" + request.getOriginURL();
	}
}
