package org.sagebionetworks.bridge.webapp.controllers;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/error")
public class ErrorController {

	private static final Logger logger = LogManager.getLogger(ErrorController.class.getName());

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public String handleError(BridgeRequest request, HttpServletResponse response) {

		Integer statusCode = request.getErrorStatusCode();
		String title = "Error." + Integer.toString(statusCode);

		String message = "";
		Throwable throwable = request.getErrorThrowableCause();
		if (throwable != null) {
			while (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}
			message = (throwable.getMessage()).replaceAll("\"", "'");
		}
		
		// TODO: Want to catch this in its own exception handler, using Spring.
		// This controller should only deal with exceptions to exception handling.
		if (throwable instanceof UnauthorizedException) {
			return "redirect:/signIn.html";
		}

		request.setAttribute("title", title);
		request.setAttribute("message", message);

		return "error";
	}

}
