package org.sagebionetworks.bridge.webapp.forms;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BridgeUser {

	private static Logger logger = LogManager.getLogger(BridgeUser.class.getName());
	
	public static final BridgeUser PUBLIC_USER = new BridgeUser();

	@Autowired
	private BeanFactory beanFactory;
	private SynapseClient synapseClient;
	private BridgeClient bridgeClient;

	private String sessionToken;
	private String userName;
	private String ownerId;
	private String communityId;
	private String avatarUrl;
	private Boolean isBridgeAdmin;

	public Boolean isBridgeAdmin() {
		return isBridgeAdmin;
	}

	public void setBridgeAdmin(Boolean isBridgeAdmin) {
		this.isBridgeAdmin = isBridgeAdmin;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOwnerId() {
		if (ownerId == null) {
			throw new UnauthorizedException("The user must be authenticated");
		}
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getCommunityId() {
		return communityId;
	}

	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}

	public boolean isAuthenticated() {
		return sessionToken != null;
	}

	public String getAvatarUrl() {
		return (StringUtils.isNotBlank(avatarUrl)) ? avatarUrl : "images/default_avatar.png";
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public List<Community> getCommunities() throws SynapseException {
		if (!isAuthenticated()) {
			throw new UnauthorizedException("The user must be authenticated");
		}		
		try {
			BridgeClient client = getBridgeClient();
			PaginatedResults<Community> results = client.getCommunities(ClientUtils.LIMIT, 0);
			return results.getResults();
		} catch(SynapseException e) {
			logger.error(e);
		}
		return Collections.emptyList();
	}

	public SynapseClient getSynapseClient() {
		if (!isAuthenticated()) {
			throw new UnauthorizedException("The user must be authenticated");
		}
		if (this.synapseClient == null) {
			this.synapseClient = (SynapseClient) beanFactory.getBean("synapseClient");
			this.synapseClient.setSessionToken(getSessionToken());
			this.synapseClient.appendUserAgent("Bridge/0.1");
		}
		return synapseClient;
	}

	public BridgeClient getBridgeClient() {
		if (!isAuthenticated()) {
			throw new UnauthorizedException("The user must be authenticated");
		}
		if (this.bridgeClient == null) {
			this.bridgeClient = (BridgeClient) beanFactory.getBean("bridgeClient");
			this.bridgeClient.setSessionToken(getSessionToken());
			this.bridgeClient.appendUserAgent("Bridge/0.1");
		}
		return bridgeClient;
	}

	public void cleanup() {
		this.synapseClient = null;
		this.bridgeClient = null;
	}

}
