package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.controllers.SignInController;

public class OriginFilter implements Filter {

	private static final Logger logger = LogManager.getLogger(OriginFilter.class.getName());

	private static Set<String> excludedURLs = new HashSet<String>();
	static {
		excludedURLs.add("/error.html");
		excludedURLs.add("/signIn.html");
		excludedURLs.add("/signOut.html");
		excludedURLs.add("/signedOut.html");
		excludedURLs.add("/signUp.html");
		excludedURLs.add("/resetPassword.html");
		excludedURLs.add("/termsOfUse.html");
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		// Store the origin URL so after authentication, we can direct the user
		// to where they came from. This isn't excellent but it'll do until we
		// know the user's home community.
		HttpServletRequest request = (HttpServletRequest) req;
		BridgeRequest bridgeRequest = new BridgeRequest(request);

		String servletPath = request.getServletPath();
		
		if (!excludedURLs.contains(servletPath) && servletPath.endsWith(".html")) {
			logger.debug("Setting the origin URL as: " + servletPath);
			bridgeRequest.setOriginURL(servletPath);
		}

		chain.doFilter(bridgeRequest, res);
	}

	@Override
	public void destroy() {
	}
}
