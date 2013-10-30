package org.sagebionetworks.bridge.services.auth;

import javax.servlet.ServletRequest;

import org.sagebionetworks.repo.model.AuthorizationConstants;

public class TokenProviderImpl implements TokenProvider {

    @Override
    public String getSessionToken(ServletRequest request) {
        return request.getParameter(AuthorizationConstants.SESSION_TOKEN_PARAM);
    }
}
