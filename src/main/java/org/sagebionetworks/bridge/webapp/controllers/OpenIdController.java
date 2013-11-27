package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openid4java.OpenIDException;
import org.sagebionetworks.authutil.OpenIDConsumerUtils;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseForbiddenException;
import org.sagebionetworks.client.exceptions.SynapseTermsOfUseException;
import org.sagebionetworks.client.exceptions.SynapseUnauthorizedException;
import org.sagebionetworks.repo.model.OriginatingClient;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// TODO Get all the direct use of response out of here. Get rid of hard-coding of /webapp. 

@Controller
public class OpenIdController extends AuthenticateBaseController {

	private static final Logger logger = LogManager.getLogger(OpenIdController.class.getName());

	public static final String ACCEPTS_TERMS_OF_USE_COOKIE_NAME = "sagebionetworks.acceptsTermsOfUse";
	public static final String ACCEPTS_TERMS_OF_USE_PARAM = "acceptsTermsOfUse";
	public static final String ACCEPTS_TERMS_OF_USE_REQUIRED_TOKEN = "TermsOfUseAcceptanceRequired";
	public static final String OPEN_ID_PROVIDER_PARAM = "OPEN_ID_PROVIDER";
	public static final String RETURN_TO_URL_PARAM = "RETURN_TO_URL";
	public static final String OPEN_ID_URI = "/openId.html"; 
	public static final String OPEN_ID_CALLBACK_URI = "/openIdCallback.html";
	public static final String RETURN_TO_URL_COOKIE = "sagebionetworks.returnToUrl";
	public static final String REDIRECT_MODE_COOKIE = "sagebionetworks.redirectMode";
	public static final String OPEN_ID_ERROR_TOKEN = "OpenIDError";
	public static final int COOKIE_MAX_AGE_SECONDS = 60;

	@Resource(name = "privateSynapseClient")
	private SynapseClient privateSynapseClient;

	@RequestMapping(value = "/openId", method = RequestMethod.POST)
	public String openId(BridgeRequest request, HttpServletResponse response) throws Exception {

		// I'm sure there's a better way to do this, this is so wacky
		String openIdUri = request.getContextPath() + OPEN_ID_URI;
		String thisUrl = request.getRequestURL().toString();
		int i = thisUrl.indexOf(openIdUri);
		if (i < 0) {
			throw new Exception("Request URL is missing suffix " + openIdUri);
		}
		String redirectEndpoint = thisUrl.substring(0, i);
		
		String providerName = request.getParameter(OPEN_ID_PROVIDER_PARAM);
		if (providerName == null) {
			throw new Exception("Missing parameter " + OPEN_ID_PROVIDER_PARAM);
		}
		String returnToURL = request.getParameter(RETURN_TO_URL_PARAM);
		if (returnToURL == null) {
			throw new Exception("Missing parameter " + RETURN_TO_URL_PARAM);
		}
		boolean acceptsTOU = "true".equals(request.getParameter(ACCEPTS_TERMS_OF_USE_PARAM));

		return openID(providerName, acceptsTOU, returnToURL, request, response, redirectEndpoint);
	}

	@RequestMapping(value = "/openIdCallback")
	public String openIdCallback(BridgeRequest request, HttpServletResponse response) throws Exception {
		if ("cancel".equals(request.getParameter("openid.mode"))) {
			// This is a funny one. This is if the user hits the cancel button on the Google auth form. 
			// I'm not sure if this value can be passed through or not.
			request.setNotification("SignInCancelled");
			return "redirect:" + request.getOrigin();
		}
		return openIDCallback(request, response);
	}

	private String openID(String openIdProviderName, boolean acceptsTermsOfUse, String returnToURL,
			BridgeRequest request, HttpServletResponse response, String redirectEndpoint) throws IOException,
			ServletException, OpenIDException, URISyntaxException {

		// Stash info that the portal needs in cookies
		Cookie cookie = new Cookie(RETURN_TO_URL_COOKIE, returnToURL);
		cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
		response.addCookie(cookie);

		cookie = new Cookie(ACCEPTS_TERMS_OF_USE_COOKIE_NAME, "" + acceptsTermsOfUse);
		cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
		response.addCookie(cookie);

		// Get the redirect
		String openIDCallbackURL = redirectEndpoint + request.getContextPath() + OPEN_ID_CALLBACK_URI;
		String redirectURL = OpenIDConsumerUtils.authRequest(openIdProviderName, openIDCallbackURL);

		// Send the user off to the next part of the handshake
		request.setOauthRedirect(redirectURL);
		return "redirect:"+redirectURL;
	}

	private String openIDCallback(BridgeRequest request, HttpServletResponse response) throws Exception {
		String returnToURL = null;
		Boolean acceptsTermsOfUse = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (RETURN_TO_URL_COOKIE.equals(c.getName())) {
				returnToURL = c.getValue();
			} else if (ACCEPTS_TERMS_OF_USE_COOKIE_NAME.equals(c.getName())) {
				acceptsTermsOfUse = Boolean.parseBoolean(c.getValue());
			}
		}

		if (returnToURL == null) {
			throw new RuntimeException("Missing required return-to URL.");
		}
		String queryString = URLDecoder.decode(request.getQueryString(), "UTF-8");

		// Send all the Open ID info to the repository services
		Session session = privateSynapseClient.passThroughOpenIDParameters(queryString, true, OriginatingClient.BRIDGE);
		
		if (session.getAcceptsTermsOfUse()) {
			acceptsTermsOfUse = true;
		}
		
		privateSynapseClient.setSessionToken(session.getSessionToken());
		
		String location = createRedirectForAuth(request, response, returnToURL, session.getSessionToken(), acceptsTermsOfUse);
		
		privateSynapseClient.signTermsOfUse(session.getSessionToken(), acceptsTermsOfUse);
		if (acceptsTermsOfUse) {
			// Here we'd like to get the information necessary to create the user...
			UserSessionData userSessionData = privateSynapseClient.getUserSessionData();
			BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
			request.setBridgeUser(user);
		} else {
			return "redirect:"+"/termsOfUse.html?oauth=true";
		}
		/*
		try {
			// Here we'd like to get the information necessary to create the user...
			UserSessionData userSessionData = privateSynapseClient.getUserSessionData();
			BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
			request.setBridgeUser(user);
		} catch (SynapseForbiddenException | SynapseUnauthorizedException | SynapseTermsOfUseException e) {
			return "redirect:"+"/termsOfUse.html?oauth=true";
		}
		*/
		return "redirect:"+location;
	}

	public static String createErrorRedirectURL(String returnToURL) throws URISyntaxException {
		return OpenIDConsumerUtils.addRequestParameter(returnToURL, "status=" + OPEN_ID_ERROR_TOKEN);
	}
	
	protected String createRedirectForAuth(BridgeRequest request, HttpServletResponse response, String returnToURL,
			String sessionToken, boolean acceptsTermsOfUse) throws URISyntaxException {
		String redirectUrl = createRedirectURL(request, returnToURL, sessionToken, acceptsTermsOfUse);
		return response.encodeRedirectURL(redirectUrl);
	}

	private String createRedirectURL(HttpServletRequest request, String returnToURL, String sessionToken,
			boolean crowdAcceptsTermsOfUse) throws URISyntaxException {
		String redirectUrl = null;
		if (crowdAcceptsTermsOfUse) {
			redirectUrl = OpenIDConsumerUtils
					.addRequestParameter(returnToURL, "status=OK&sessionToken=" + sessionToken);
		} else {
			redirectUrl = OpenIDConsumerUtils.addRequestParameter(returnToURL, "status="
					+ ACCEPTS_TERMS_OF_USE_REQUIRED_TOKEN);
		}
		return redirectUrl;
	}

}
