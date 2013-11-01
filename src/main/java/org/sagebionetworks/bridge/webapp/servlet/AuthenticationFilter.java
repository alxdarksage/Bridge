package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.sagebionetworks.bridge.webapp.forms.BridgeUser;

public class AuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException,
			IOException {

		BridgeRequest request = (BridgeRequest) req;

		if (!request.isUserAuthenticated()) {
			request.setBridgeUser(BridgeUser.PUBLIC_USER);
		}

		chain.doFilter(request, res);

		BridgeUser user = request.getBridgeUser();
		if (user != null) {
			user.cleanup();
		}

		/*
		 * HttpServletRequest request = (HttpServletRequest)req; BridgeUser user
		 * = (BridgeUser)request.getSession().getAttribute(BridgeUser.KEY);
		 * 
		 * // Currently we have no reason to redirect if the user is not signed
		 * in, // but the user is marked as a public user. if (user == null ||
		 * !user.isAuthenticated()) { // This is not injected from a Spring
		 * Context, but it's OK because // the public user has no need of any
		 * injected services. request.getSession().setAttribute(BridgeUser.KEY,
		 * BridgeUser.PUBLIC_USER); }
		 * 
		 * chain.doFilter(req, res);
		 * 
		 * // Destroy any injected services that were created during request
		 * processing. // Is this necessary? Time/memory trade-off? user =
		 * (BridgeUser)request.getSession().getAttribute(BridgeUser.KEY); if
		 * (user != null) { user.cleanup(); }
		 */
	}

	@Override
	public void destroy() {
	}

	/*
	 * If we needed to retrieve a public user, this is how we'd do it: private
	 * BridgeUser getBridgeUser() { return
	 * (BridgeUser)WebApplicationContextUtils
	 * .getRequiredWebApplicationContext(config.getServletContext())
	 * .getBean("bridgeUser"); }
	 */

}
