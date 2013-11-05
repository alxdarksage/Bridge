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
import org.sagebionetworks.client.exceptions.SynapseTermsOfUseException;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.Session;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OpenIdController extends AuthenticateBaseController {
	
	/*
	sessionToken: ac0036ca-8815-4fd2-8b73-9e1ee928a426
	User-Agent: Synpase-Java-Client/develop-SNAPSHOT
	Accept: application/json
	Content-Type: application/json
Request Content: org.apache.http.entity.StringEntity@38156964
Response Content: {"reason":"org.openid4java.message.MessageException: 0x100: Required parameter missing: openid.return_to\n"}
	at org.sagebionetworks.client.SynapseClientImpl.dispatchSynapseRequest(SynapseClientImpl.java:3602)
	at org.sagebionetworks.client.SynapseClientImpl.signAndDispatchSynapseRequest(SynapseClientImpl.java:3501)
	at org.sagebionetworks.client.SynapseClientImpl.createJSONObjectEntity(SynapseClientImpl.java:3268)
	at org.sagebionetworks.client.SynapseClientImpl.createAuthEntity(SynapseClientImpl.java:3228)
	at org.sagebionetworks.client.SynapseClientImpl.passThroughOpenIDParameters(SynapseClientImpl.java:5161)
	at org.sagebionetworks.bridge.webapp.controllers.OpenIdController.openIDCallback(OpenIdController.java:123)
	at org.sagebionetworks.bridge.webapp.controllers.OpenIdController.openIdCallback(OpenIdController.java:81)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.springframework.web.bind.annotation.support.HandlerMethodInvoker.invokeHandlerMethod(HandlerMethodInvoker.java:176)
	 */
	
	private static final Logger logger = LogManager.getLogger(OpenIdController.class.getName());

	public static final String ACCEPTS_TERMS_OF_USE_COOKIE_NAME = "sagebionetworks.acceptsTermsOfUse";
	public static final String ACCEPTS_TERMS_OF_USE_PARAM = "acceptsTermsOfUse";
	public static final String ACCEPTS_TERMS_OF_USE_REQUIRED_TOKEN = "TermsOfUseAcceptanceRequired";
	// TODO: Hardcoded, change this.
	public static final String OPEN_ID_URI = "/webapp/openId.html"; 
	public static final String OPEN_ID_PROVIDER = "OPEN_ID_PROVIDER";
	public static final String OPEN_ID_CALLBACK_URI = "/webapp/openIdCallback.html";
	public static final String RETURN_TO_URL_PARAM = "RETURN_TO_URL";
	public static final String RETURN_TO_URL_COOKIE_NAME = "sagebionetworks.returnToUrl";
	public static final String REDIRECT_MODE_COOKIE_NAME = "sagebionetworks.redirectMode";
	public static final String OPEN_ID_ERROR_TOKEN = "OpenIDError";
	public static final int COOKIE_MAX_AGE_SECONDS = 60;

	@Resource(name = "privateSynapseClient")
	private SynapseClient privateSynapseClient;

	@RequestMapping(value = "/openId", method = RequestMethod.POST)
	public void openId(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String thisUrl = request.getRequestURL().toString();
		int i = thisUrl.indexOf(OPEN_ID_URI);
		if (i < 0) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getWriter().println("Request URL is missing suffix " + OPEN_ID_URI);
			return;
		}
		String redirectEndpoint = thisUrl.substring(0, i);
		String openIdProviderName = request.getParameter(OPEN_ID_PROVIDER);
		if (openIdProviderName == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getWriter().println("Missing parameter " + OPEN_ID_PROVIDER);
			return;
		}
		String explicitlyAcceptsTermsOfUseString = request.getParameter(ACCEPTS_TERMS_OF_USE_PARAM);
		Boolean explicitlyAcceptsTermsOfUse = explicitlyAcceptsTermsOfUseString == null ? false : new Boolean(
				explicitlyAcceptsTermsOfUseString);
		String returnToURL = request.getParameter(RETURN_TO_URL_PARAM);
		if (returnToURL == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.getWriter().println("Missing parameter " + RETURN_TO_URL_PARAM);
			return;
		}

		openID(openIdProviderName, explicitlyAcceptsTermsOfUse, returnToURL, request, response, redirectEndpoint);
	}

	@RequestMapping(value = "/openIdCallback")
	public void openIdCallback(BridgeRequest request, HttpServletResponse response) throws Exception {
		if ("cancel".equals(request.getParameter("openid.mode"))) {
			// This is a funny one. This is if the user hits the cancel button on the Google auth form. 
			// I'm not sure if this value can be passed through or not.
			request.setNotification("Sign in cancelled.");
			response.sendRedirect(request.getContextPath() + request.getOriginURL());
			return;
		}
		openIDCallback(request, response);
	}

	private void openID(String openIdProviderName, Boolean acceptsTermsOfUse, String returnToURL,
			HttpServletRequest request, HttpServletResponse response, String redirectEndpoint) throws IOException,
			ServletException, OpenIDException, URISyntaxException {

		// Stash info that the portal needs in cookies
		Cookie cookie = new Cookie(RETURN_TO_URL_COOKIE_NAME, returnToURL);
		cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
		response.addCookie(cookie);

		cookie = new Cookie(ACCEPTS_TERMS_OF_USE_COOKIE_NAME, "" + acceptsTermsOfUse);
		cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
		response.addCookie(cookie);

		// Get the redirect
		String openIDCallbackURL = redirectEndpoint + OPEN_ID_CALLBACK_URI;
		String redirectURL = OpenIDConsumerUtils.authRequest(openIdProviderName, openIDCallbackURL);

		// Send the user off to the next part of the handshake
		response.sendRedirect(redirectURL);
	}

	private void openIDCallback(BridgeRequest request, HttpServletResponse response) throws Exception {
		String returnToURL = null;
		Boolean acceptsTermsOfUse = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			if (RETURN_TO_URL_COOKIE_NAME.equals(c.getName())) {
				returnToURL = c.getValue();
			} else if (ACCEPTS_TERMS_OF_USE_COOKIE_NAME.equals(c.getName())) {
				acceptsTermsOfUse = Boolean.parseBoolean(c.getValue());
			}
		}

		if (returnToURL == null) {
			throw new RuntimeException("Missing required return-to URL.");
		}

		// Send all the Open ID info to the repository services
		logger.info(URLDecoder.decode(request.getQueryString(), "UTF-8"));
		Session session = privateSynapseClient.passThroughOpenIDParameters(URLDecoder.decode(request.getQueryString(), "UTF-8"),
				acceptsTermsOfUse);

		// Check to see if the user has accepted the terms of use
		privateSynapseClient.setSessionToken(session.getSessionToken());
		try {

			// Here we'd like to get the information necessary to creat the user...
			UserSessionData userSessionData = privateSynapseClient.getUserSessionData();
			BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
			request.setBridgeUser(user);
			
		} catch (SynapseTermsOfUseException e) {
			response.sendRedirect(request.getContextPath() + "/termsOfUse.html");
		}

		// Redirect the user appropriately
		String redirectUrl = createRedirectURL(request, returnToURL, session.getSessionToken(), acceptsTermsOfUse);
		String location = response.encodeRedirectURL(redirectUrl);
		response.sendRedirect(location);
	}

	private String createRedirectURL(HttpServletRequest request, String returnToURL, String sessionToken, boolean crowdAcceptsTermsOfUse)
			throws URISyntaxException {
		String redirectUrl = null;
		if (crowdAcceptsTermsOfUse) {
			redirectUrl = 
				OpenIDConsumerUtils.addRequestParameter(returnToURL, "status=OK&sessionToken=" + sessionToken);
		} else {
			redirectUrl = OpenIDConsumerUtils.addRequestParameter(returnToURL, 
				"status="+ ACCEPTS_TERMS_OF_USE_REQUIRED_TOKEN);
		}
		return request.getContextPath() + redirectUrl;
	}

	public static String createErrorRedirectURL(String returnToURL) throws URISyntaxException {
		return OpenIDConsumerUtils.addRequestParameter(returnToURL, "status=" + OPEN_ID_ERROR_TOKEN);
	}

}
