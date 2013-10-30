package org.sagebionetworks.bridge.services.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.services.SynapseClientProvider;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.web.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AuthServiceController {

	private static final Logger logger = LogManager
			.getLogger(AuthServiceController.class);

	@Autowired
	private SynapseClientProvider clientProvider;

	@ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody
	Session authenticate(HttpServletRequest request,
			@RequestBody NewUser credentials) throws NotFoundException,
			SynapseException {
		SynapseClient synapseClient = clientProvider.getSynapseClient(request);
		UserSessionData userSessionData = synapseClient.login(
				credentials.getEmail(), credentials.getPassword());
		Session session = new Session();
		session.setSessionToken(userSessionData.getSessionToken());
		return session;
	}
}
