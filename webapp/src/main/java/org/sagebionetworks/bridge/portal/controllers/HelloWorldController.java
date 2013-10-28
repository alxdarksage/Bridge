package org.sagebionetworks.bridge.portal.controllers;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.common.HelloMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/hello")
public class HelloWorldController {
	
	private static final Logger logger = LogManager.getLogger(HelloWorldController.class.getName());

	private String serviceUrl;
	private RestTemplate helloMessageRestCall;
	
	@Resource(name="serviceUrl")
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	@Resource(name="helloMessageRestCall")
	public void setHelloMessageRestCall(RestTemplate helloMessageRestCall) {
		this.helloMessageRestCall = helloMessageRestCall;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String helloWorld(ModelMap model) throws Exception {
		logger.debug("Gratuitous logging message that proves log4j is working through adapter to JDK logging");
		
		HelloMessage result = helloMessageRestCall.getForObject(serviceUrl, HelloMessage.class);
		model.addAttribute("message", result.getMessage());	
		return "hello";
	}

}
