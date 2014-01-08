package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
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

	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(value = "/descriptors/index", method = RequestMethod.GET)
	public String viewParticipantDataDescriptors() {
		return "admin/descriptors/index";
	}
	
	@RequestMapping(value = "/descriptors/create/cbc", method = RequestMethod.GET)
	public String createDescriptor(BridgeRequest request) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		
		PaginatedResults<ParticipantDataDescriptor> descriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0);
		for (Specification spec: specResolver.getAllSpecifications()) {
			
			if (specificationDoesNotExist(descriptors.getResults(), spec)) {
				ParticipantDataDescriptor descriptor = ParticipantDataUtils.getDescriptor(spec);
				descriptor = client.createParticipantDataDescriptor(descriptor);
				
				List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(descriptor.getId(), spec);
				for (ParticipantDataColumnDescriptor column : columns) {
					client.createParticipantDataColumnDescriptor(column);
				}
			}
		}
		request.setNotification("Created any missing descriptors. Look for them in the journal section");
		return "redirect:/admin/descriptors/index.html";
	}
	
	private boolean specificationDoesNotExist(List<ParticipantDataDescriptor> descriptors, Specification spec) {
		for (ParticipantDataDescriptor descriptor : descriptors) {
			if (descriptor.getName().equals(spec.getName())) {
				return false;
			}
		}
		return true;
	}
	
}
