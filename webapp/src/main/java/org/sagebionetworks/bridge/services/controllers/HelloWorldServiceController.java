package org.sagebionetworks.bridge.services.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.common.HelloMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class HelloWorldServiceController {
	
	// private static Logger logger = Logger.getLogger(HelloWorldServiceController.class.getName());
	private static Logger logger = LogManager.getLogger(HelloWorldServiceController.class.getName());

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/getHelloMessage", method = RequestMethod.GET, produces = "application/json")	
	public @ResponseBody HelloMessage getHelloMessage() throws Exception {
		logger.info("Calling HelloWorldServiceController.getHelloMessage()");
		
		return new HelloMessage();
	}
	
}
