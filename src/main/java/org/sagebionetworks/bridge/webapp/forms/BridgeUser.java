package org.sagebionetworks.bridge.webapp.forms;

import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BridgeUser {
    
    public static final BridgeUser PUBLIC_USER = new BridgeUser();
    
    @Autowired
    private BeanFactory beanFactory;
    private SynapseClient synapseClient;
    
    private String sessionToken;
    private String displayName;
    private String ownerId;
    private String communityId;
    
    public String getSessionToken() {
        return sessionToken;
    }
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getOwnerId() {
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
    public SynapseClient getSynapseClient() {
        if (!isAuthenticated()) {
            throw new UnauthorizedException("The user must be authenticated");
        }
        if (this.synapseClient == null) {
            this.synapseClient = (SynapseClient)beanFactory.getBean("synapseClient");
            this.synapseClient.setSessionToken(getSessionToken());
            // TODO: Not a Spring bean compatible method, so set it here.
            this.synapseClient.appendUserAgent("Bridge-Service/0.1");
        }
        return synapseClient;
    }
    public String getStartURL() {
        // TODO: This will eventually use the communityId value that'st the default for 
        // this user.
        if (this == PUBLIC_USER) {
            return "redirect:/portal/index.html";
        } else {
            return "redirect:/communities/index.html";
        }
    }
    public void cleanup() {
        this.synapseClient = null;
    }

}
