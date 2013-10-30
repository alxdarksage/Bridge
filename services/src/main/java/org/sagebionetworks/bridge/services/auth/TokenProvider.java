package org.sagebionetworks.bridge.services.auth;

import javax.servlet.ServletRequest;

public interface TokenProvider {

    /**
     * Get the user's Synapse session token.
     * 
     * @param request
     * @return
     */
    public String getSessionToken(ServletRequest request);

}
