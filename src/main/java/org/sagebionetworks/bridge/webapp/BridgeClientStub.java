package org.sagebionetworks.bridge.webapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.versionInfo.BridgeVersionInfo;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SharedClientConnection;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.web.NotFoundException;

public class BridgeClientStub implements BridgeClient {

	private Map<String,Community> communities = new HashMap<>();
	private SynapseClientStub synapseClientStub;
	
	public BridgeClientStub(SynapseClientStub synapseClientStub) {
		if (synapseClientStub == null) {
			throw new IllegalArgumentException("BridgeClientStub must be initialized with a synapseClientStub");
		}
		Community community = new Community();
		community.setId(synapseClientStub.newId());
		community.setName("Fanconi Anemia");
		community.setDescription("This is a very rare but very serious disease, affecting about 1,000 people worldwide.");
		community.setCreatedBy("alxdark");
		community.setCreatedOn(new Date());
		community.setModifiedBy("alxdark");
		community.setModifiedOn(new Date());
		communities.put(community.getId(), community);
		this.synapseClientStub = synapseClientStub;
	}
	
	@Override
	public void appendUserAgent(String toAppend) {
		synapseClientStub.appendUserAgent(toAppend);
	}

	@Override
	public void setSessionToken(String sessionToken) {
		synapseClientStub.setSessionToken(sessionToken);
	}

	@Override
	public String getCurrentSessionToken() {
		return synapseClientStub.getCurrentSessionToken();
	}

	@Override
	public SharedClientConnection getSharedClientConnection() {
		return synapseClientStub.getSharedClientConnection();
	}

	@Override
	public String getBridgeEndpoint() {
		return null;
	}

	@Override
	public void setBridgeEndpoint(String repoEndpoint) {
	}

	@Override
	public BridgeVersionInfo getVersionInfo() throws SynapseException {
		BridgeVersionInfo info = new BridgeVersionInfo();
		info.setVersion("1.0");
		return info;
	}

	@Override
	public Community createCommunity(Community community) throws SynapseException {
		community.setCreatedOn(new Date());
		community.setCreatedBy(synapseClientStub.getUserName());
		community.setModifiedOn(new Date());
		community.setModifiedBy(synapseClientStub.getUserName());
		String id = synapseClientStub.newId();
		community.setId(id);
		communities.put(id, community);
		return community;
	}

	@Override
	public List<Community> getCommunities() throws SynapseException {
		return new ArrayList<Community>(communities.values());
	}

	@Override
	public Community getCommunity(String communityId) throws SynapseException {
		Community community = communities.get(communityId);
		if (community == null) {
			throw new SynapseException(new NotFoundException());
		}
		return community;
	}

	@Override
	public Community updateCommunity(Community community) throws SynapseException {
		// Actually it isn't even this: they just replace the object I believe
		/*
		Community cty = communities.get(community.getId());
		if (cty == null) {
			throw new SynapseNotFoundException();
		}
		try {
			BeanUtils.copyProperties(cty, community);	
		} catch(Exception e) {
			throw new SynapseException(e);
		}
		return cty;
		*/
		
		Community cty = communities.get(community.getId());
		if (cty == null) {
			throw new SynapseNotFoundException();
		}
		communities.put(community.getId(), community);
		return community;
	}
	
	public void deleteCommunity(String communityId) throws SynapseException {
		if (communityId == null || !communities.containsKey(communityId)) {
			throw new SynapseException(new NotFoundException("Could not find that community"));
		}
		communities.remove(communityId);
	}

}
