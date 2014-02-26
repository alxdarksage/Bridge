package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.controllers.AuthenticateBaseController;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.DomainType;
import org.sagebionetworks.repo.model.UserSessionData;

public class AuthenticationFilter extends AuthenticateBaseController implements Filter {
	
	private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class.getName());
	
	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException,
			IOException {

		BridgeRequest request = new  BridgeRequest((HttpServletRequest)req);
		HttpServletResponse response = (HttpServletResponse) res;
		
		if (!request.isUserAuthenticated()) {
			tryToReestablishSession(request, response);	
		}
		if (request.isUserAuthenticated()) {
			ClientUtils.setSynapseSessionCookie(request, response, 30 * 60); // 30 minutes
		} else {
			request.setBridgeUser(BridgeUser.PUBLIC_USER);
			ClientUtils.setSynapseSessionCookie(request, response, 0);
		}
		
		// Spring Security is overkill
        if (request.getServletPath().startsWith("/admin/") && !request.isUserInRole("admin")) {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/signIn.html"));
		} else {
			chain.doFilter(request, res);
		}
		BridgeUser user = request.getBridgeUser();
		if (user != null) {
			user.cleanup();
		} 
	}
	
	private void tryToReestablishSession(BridgeRequest request, HttpServletResponse response) {
		try {
			String sessionToken = ClientUtils.getSynapseSessionCookie(request);
			if (sessionToken != null) {
				// We need a new one each request, we cannot re-use the injected one since
				// the filter will be reused.
				SynapseClient client = (SynapseClient) beanFactory.getBean("synapseClient");
				client.setSessionToken(sessionToken);
				UserSessionData data = client.getUserSessionData(DomainType.BRIDGE);
				BridgeUser user = createBridgeUserFromUserSessionData(data);
				request.setBridgeUser(user);
				logger.info("User re-established Synapse session");				
			}
		} catch (SynapseException e) {
			ClientUtils.setSynapseSessionCookie(request, response, 0);
			logger.info("Cannot re-establish session for user: " + e.getMessage());
		}
	}
	
	@Override
	public void destroy() {
	}

}
