package org.sagebionetworks.bridge.services.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.common.SynapseStatusMessage;
import org.sagebionetworks.bridge.services.SynapseClientProvider;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.versionInfo.SynapseVersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class SynapseStatusServiceController {

	private static final Logger logger = LogManager
			.getLogger(SynapseStatusServiceController.class);

	@Autowired
	private SynapseClientProvider clientProvider;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/getSynapseStatus", method = RequestMethod.GET)
	public @ResponseBody
	SynapseStatusMessage getSynapseStatus(HttpServletRequest request)
			throws Exception {
		SynapseClient synapseClient = clientProvider.getSynapseClient(request);

		SynapseVersionInfo versionInfo = synapseClient.getVersionInfo();
		SynapseStatusMessage message = new SynapseStatusMessage();
		message.setMessage(versionInfo.getVersion());

		return message;
	}
}
