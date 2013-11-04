package org.sagebionetworks.bridge.webapp.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.sagebionetworks.bridge.webapp.forms.BridgeUser;

/**
 * A convenience wrapper to hide some of the ick of casting and using map keys
 * when working with request and session objects.
 * 
 * @author alxdark
 * 
 */
public class BridgeRequest extends HttpServletRequestWrapper {

	private static final String DEFAULT_ORIGIN_URL = "/portal/index.html";
	public static final String BRIDGE_USER_KEY = "BridgeUser";
	public static final String NOTICE_KEY = "notice";
	public static final String ORIGIN_KEY = "origin";

	private HttpServletRequest request;

	public BridgeRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	// Newer versions of Spring include a flash feature (a la Rails), but we
	// don't
	// have that here, so notifications does something similar.

	public void setNotification(String notice) {
		if (notice == null) {
			getSession().removeAttribute(NOTICE_KEY);
		} else {
			getSession().setAttribute(NOTICE_KEY, notice);
		}
	}

	public String getNotification() {
		String notice = (String) getSession().getAttribute(NOTICE_KEY);
		setNotification(null);
		return notice;
	}

	public void setOriginURL(String origin) {
		if (origin == null) {
			getSession().removeAttribute(ORIGIN_KEY);
		} else {
			getSession().setAttribute(ORIGIN_KEY, origin);
		}
	}

	public String getOriginURL() {
		String origin = (String) getSession().getAttribute(ORIGIN_KEY);
		if (origin == null) {
			origin = DEFAULT_ORIGIN_URL;
		}
		return origin;
	}

	/*
	 * public void setOrigin(String origin) { if (origin == null) {
	 * this.request.removeAttribute(ORIGIN_KEY); } else {
	 * this.request.setAttribute(ORIGIN_KEY, origin); } } public String
	 * getOrigin() { return (String)this.request.getAttribute(ORIGIN_KEY); }
	 */
	public void setBridgeUser(BridgeUser user) {
		if (user == null) {
			getSession().removeAttribute(BRIDGE_USER_KEY);
		} else {
			getSession().setAttribute(BRIDGE_USER_KEY, user);
		}
	}

	public BridgeUser getBridgeUser() {
		return (BridgeUser) getSession().getAttribute(BRIDGE_USER_KEY);
	}

	public boolean isUserAuthenticated() {
		BridgeUser user = (BridgeUser) getSession().getAttribute(BRIDGE_USER_KEY);
		if (user != null && user.isAuthenticated()) {
			return true;
		}
		return false;
	}

	public Integer getErrorStatusCode() {
		return (Integer) request.getAttribute("javax.servlet.error.status_code");
	}

	/**
	 * Return the root cause throwable of a request that has been routed to the
	 * global servlet error page (as defined in web.xml).
	 * 
	 * @return Throwable rootThrowable
	 */
	public Throwable getErrorThrowableCause() {
		Throwable cause = (Throwable) request.getAttribute("javax.servlet.error.exception");
		while (cause != null && cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}
}
