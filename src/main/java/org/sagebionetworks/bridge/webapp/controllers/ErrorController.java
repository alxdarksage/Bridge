package org.sagebionetworks.bridge.webapp.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.web.ForbiddenException;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.ServiceUnavailableException;
import org.sagebionetworks.repo.web.TemporarilyUnavailableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController {

	private static final Logger logger = LogManager.getLogger(ErrorController.class.getName());
	
	// In fact, I don't believe many of these get thrown, because they are not wrapped by 
	// SynapseException, SE is a replacement for these domain-specific exceptions.
	private static Map<Class<? extends Throwable>,String> errorCodes = new HashMap<>();
	static {
		errorCodes.put(UnauthorizedException.class, "Error.401");
		errorCodes.put(ForbiddenException.class, "Error.403");
		errorCodes.put(NotFoundException.class, "Error.404");
		errorCodes.put(ServiceUnavailableException.class, "Error.503");
		errorCodes.put(TemporarilyUnavailableException.class, "Error.503");
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView handleError(BridgeRequest request, HttpServletResponse response, ModelAndView map) {
		map.setViewName("error");

		Integer statusCode = request.getErrorStatusCode();
		map.addObject("errorCode", "Error." + Integer.toString(statusCode));

		Throwable throwable = request.getErrorThrowableCause();
		if (throwable != null) {
			processThrowable(request, throwable, map);
		}

		return map;
	}
	
	private void processThrowable(BridgeRequest request, Throwable throwable, ModelAndView map) {
		throwable = ClientUtils.unwrapThrowable(throwable);
		logger.error("MESSAGE STRING: " + throwable.getMessage());
		
		if (throwable instanceof UnauthorizedException) {
			map.setViewName("redirect:/signIn.html");
		} else if (throwable instanceof SynapseException) {
			ClientUtils.ExceptionInfo info = ClientUtils.parseSynapseException((SynapseException)throwable);
			map.addObject("errorCode", "Error." + Integer.toString(info.getCode()));
			map.addObject("message", info.getMessage());
		} else {
			String exceptionCode = errorCodes.get(throwable.getClass());
			if (exceptionCode != null) {
				map.addObject("errorCode", exceptionCode);
			}
			map.addObject("message", throwable.getMessage());
		}
	}

}
