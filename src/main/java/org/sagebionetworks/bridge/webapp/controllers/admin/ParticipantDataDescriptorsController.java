package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.CompleteBloodCountSpec;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.servlet.OriginFilter;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class ParticipantDataDescriptorsController {
	
	private static final Logger logger = LogManager.getLogger(ParticipantDataDescriptorsController.class.getName());

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(value = "/descriptors", method = RequestMethod.GET)
	public String viewParticipantDataDescriptors() {
		return "admin/descriptors";
	}
	
	@RequestMapping(value = "/descriptors/create/cbc", method = RequestMethod.GET)
	public String createDescriptor(BridgeRequest request) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		
		CompleteBloodCountSpec spec = new CompleteBloodCountSpec();
		
		// Don't do it twice. IT tests do fill db with junk.
		PaginatedResults<ParticipantDataDescriptor> all = client.getAllParticipantDatas(ClientUtils.LIMIT, 0);
		for (ParticipantDataDescriptor d : all.getResults()) {
			if (d.getName().equals(spec.getDescriptor().getName())) {
				request.setNotification("CBC has already been created");
				return "redirect:/admin/descriptors.html";
			}
		}
		
		ParticipantDataDescriptor descriptor = spec.getDescriptor();
		descriptor = client.createParticipantDataDescriptor(spec.getDescriptor());
		List<ParticipantDataColumnDescriptor> columns = spec.getColumnDescriptors(descriptor);
		for (ParticipantDataColumnDescriptor column : columns) {
			client.createParticipantDataColumnDescriptor(column);
		}
		
		request.setNotification("Created a CBC descriptor, look for it in the journal section");
		return "redirect:/admin/descriptors.html";
	}
	
}
