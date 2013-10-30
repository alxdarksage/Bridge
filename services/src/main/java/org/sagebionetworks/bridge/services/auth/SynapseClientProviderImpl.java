package org.sagebionetworks.bridge.services.auth;

import javax.servlet.ServletRequest;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.services.SynapseClientProvider;
import org.sagebionetworks.bridge.services.auth.services.UserManager;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.repo.model.AuthorizationConstants;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;

public class SynapseClientProviderImpl implements SynapseClientProvider {

    private static final String PORTAL_USER_AGENT = "Bridge-Service/0.1";

    @Autowired
    private UserManager userManager;

    @Autowired
    private ServiceUrlProvider urlProvider;

    @Autowired
    private TokenProvider tokenProvider;

    /**
     * get a synapse client
     * 
     * @param request
     * @return
     */
    public SynapseClient getSynapseClient(ServletRequest request) {
        // Get the user ID
        String userId = request.getParameter(AuthorizationConstants.USER_ID_PARAM);
        if (userId == null)
            throw new UnauthorizedException("The user must be authenticated");

        SynapseClient synapseClient = new SynapseClientImpl();
        synapseClient.setSessionToken(tokenProvider.getSessionToken(request));
        synapseClient.setRepositoryEndpoint(urlProvider.getRepositoryServiceUrl());
        synapseClient.setAuthEndpoint(urlProvider.getPublicAuthBaseUrl());
        synapseClient.setFileEndpoint(StackConfiguration.getFileServiceEndpoint());
        // Append the portal's version information to the user agent.
        synapseClient.appendUserAgent(PORTAL_USER_AGENT);
        return synapseClient;
    }
}
