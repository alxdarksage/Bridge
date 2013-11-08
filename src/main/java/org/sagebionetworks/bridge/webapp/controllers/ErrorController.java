package org.sagebionetworks.bridge.webapp.controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/error")
public class ErrorController {

	private static final Logger logger = LogManager.getLogger(ErrorController.class.getName());

	private static Map<Integer, String> errorCodes = new HashMap<Integer, String>();
	static {
		errorCodes.put(400, "Bad Request");
		errorCodes.put(401, "Unauthorized");
		errorCodes.put(402, "Payment Required");
		errorCodes.put(403, "Forbidden");
		errorCodes.put(404, "Not Found");
		errorCodes.put(405, "Method Not Allowed");
		errorCodes.put(406, "Not Acceptable");
		errorCodes.put(407, "Proxy Authentication Required");
		errorCodes.put(408, "Request Timeout");
		errorCodes.put(409, "Conflict");
		errorCodes.put(410, "Gone");
		errorCodes.put(411, "Length Required");
		errorCodes.put(412, "Precondition Failed");
		errorCodes.put(413, "Request Entity Too Large");
		errorCodes.put(414, "Request-URI Too Long");
		errorCodes.put(415, "Unsupported Media Type");
		errorCodes.put(416, "Requested Range Not Satisfiable");
		errorCodes.put(417, "Expectation Failed");
		errorCodes.put(500, "Internal Server Error");
		errorCodes.put(501, "Not Implemented");
		errorCodes.put(502, "Bad Gateway");
		errorCodes.put(503, "Service Unavailable");
		errorCodes.put(504, "Gateway Timeout");
		errorCodes.put(505, "HTTP Version Not Supported");
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public void handleError(BridgeRequest request) {

		// TODO: Yet more of this can be bug in BridgeRequest
		Integer statusCode = request.getErrorStatusCode();
		String title = errorCodes.get(statusCode);
		if (title == null) {
			title = errorCodes.get(500);
		}

		String message = "";
		Throwable throwable = request.getErrorThrowableCause();
		if (throwable != null) {
			message = (throwable.getMessage()).replaceAll("\"", "'");
			logger.error(throwable);
		}
		
		if (throwable instanceof UnauthorizedException) {
			title = errorCodes.get(401);
		}

		request.setAttribute("title", title);
		request.setAttribute("message", message);
	}

}
