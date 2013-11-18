package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.sagebionetworks.bridge.webapp.forms.BridgeUser;

public class AuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException,
			IOException {

		BridgeRequest request = (BridgeRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!request.isUserAuthenticated()) {
			request.setBridgeUser(BridgeUser.PUBLIC_USER);
		}
		
		// This is pretty silly as a form of security, but Spring Security is overkill
		if (request.getServletPath().startsWith("/admin/") && !request.isUserInRole("admin")) {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/signIn.html"));
		}
		
		chain.doFilter(request, res);

		BridgeUser user = request.getBridgeUser();
		if (user != null) {
			user.cleanup();
		}
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
