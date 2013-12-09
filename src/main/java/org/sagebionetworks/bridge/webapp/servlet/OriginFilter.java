package org.sagebionetworks.bridge.webapp.servlet;

import java.io.IOException;
import java.util.Collections;
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

public class OriginFilter implements Filter {

	private static final Logger logger = LogManager.getLogger(OriginFilter.class.getName());

	private static Set<String> excludedURLs = new HashSet<String>();
	static {
		Collections.addAll(excludedURLs, "/error.html", "/signIn.html", 
			"/signOut.html", "/signedOut.html", "/signUp.html", 
			"/requestResetPassword.html", "/resetPassword.html", "/termsOfUse.html", 
			"/termsOfUse/cancel.html", "/openId.html", "/openIdCallback.html", 
			"/profile.html");
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		
		// Let's see if any of the tests are failing because of timing issues.
		try {
			Thread.sleep(1000);	
		} catch(Throwable e) {
		}
		
		// Store the origin URL so after authentication, we can direct the user
		// to where they came from. This isn't excellent but it'll do until we
		// know the user's home community.
		HttpServletRequest request = (HttpServletRequest) req;
		BridgeRequest bridgeRequest = new BridgeRequest(request);

		String servletPath = request.getServletPath();
		
		// If this was only done for requests, not forwards or includes, it would always be *.html?
		if (!excludedURLs.contains(servletPath) && !servletPath.contains("/files/") && servletPath.endsWith(".html")) {
			logger.debug("Setting the origin URL as: " + servletPath);
			bridgeRequest.setOrigin(servletPath);
		}

		chain.doFilter(bridgeRequest, res);
		
		// Let's see if any of the tests are failing because of timing issues.
		try {
			Thread.sleep(1000);	
		} catch(Throwable e) {
		}
	}

	@Override
	public void destroy() {
	}
}
