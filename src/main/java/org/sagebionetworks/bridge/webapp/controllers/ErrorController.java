package org.sagebionetworks.bridge.webapp.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.web.ForbiddenException;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.ServiceUnavailableException;
import org.sagebionetworks.repo.web.TemporarilyUnavailableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

@Controller
@RequestMapping("/error")
public class ErrorController {

	private static final String IS_DEVELOP = "isDevelop";
	private static final String EXCEPTION = "exception";
	private static final String MESSAGE = "message";
	private static final String ERROR_CODE = "errorCode";

	private static final Logger logger = LogManager.getLogger(ErrorController.class.getName());
	
	// In fact, I don't believe many of these get thrown, because they are not wrapped by 
	// SynapseException, SE is a replacement for these domain-specific exceptions.
	private static Map<Class<? extends Throwable>,String> errorCodes = Maps.newHashMap();
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

		map.addObject(IS_DEVELOP, Boolean.valueOf(StackConfiguration.isDevelopStack()));
		
		Throwable throwable = request.getErrorThrowableCause();
		if (throwable != null) {
			map.addObject(EXCEPTION, throwable);
			processThrowableDetails(request, throwable, map);
		} else {
			Integer statusCode = request.getErrorStatusCode();
			map.addObject(ERROR_CODE, "Error." + Integer.toString(statusCode));	
		}
		return map;
	}
	
	private void processThrowableDetails(BridgeRequest request, Throwable throwable, ModelAndView map) {
		if (throwable instanceof UnauthorizedException) {
			map.clear();
			map.setViewName("redirect:/signIn.html");
		} else if (throwable instanceof SynapseNotFoundException) {
			map.addObject(ERROR_CODE, "Error.404");
			map.addObject(MESSAGE, throwable.getMessage());
		} else if (throwable instanceof SynapseException) {
			ClientUtils.ExceptionInfo info = ClientUtils.parseSynapseException((SynapseException)throwable);
			map.addObject(ERROR_CODE, "Error." + Integer.toString(info.getCode()));
			map.addObject(MESSAGE, info.getMessage());
		} else {
			String exceptionCode = errorCodes.get(throwable.getClass());
			if (exceptionCode != null) {
				map.addObject(ERROR_CODE, exceptionCode);
			}
			map.addObject(MESSAGE, throwable.getMessage());
		}
	}

}
