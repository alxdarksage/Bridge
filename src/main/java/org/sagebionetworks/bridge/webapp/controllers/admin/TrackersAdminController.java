package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.SageBootstrap;
import org.sagebionetworks.bridge.webapp.SageBootstrap.ClientProvider;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class TrackersAdminController {

	private static Logger logger = LogManager.getLogger(TrackersAdminController.class.getName());
	
	@Resource(name = "bridgeClient")
	protected BridgeClient client;
	
	@ModelAttribute("descriptors")
	public List<ParticipantDataDescriptor> allDescriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		PaginatedResults<ParticipantDataDescriptor> allDescriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0L);
		Collections.sort(allDescriptors.getResults(), new Comparator<ParticipantDataDescriptor>() {
			@Override
			public int compare(ParticipantDataDescriptor pdd0, ParticipantDataDescriptor pdd1) {
				return pdd0.getName().compareTo(pdd1.getName());
			}
			
		});
		return allDescriptors.getResults();
	}
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}

	@RequestMapping(value = "/trackers/index", method = RequestMethod.GET)
	public String viewTrackers() {
		return "admin/trackers/index";
	}
	
	@RequestMapping(value = "/trackers/create", method = RequestMethod.GET)
	public String createTrackers(final BridgeRequest request) throws Exception {
		
		SageBootstrap bootstrap = new SageBootstrap(new SageBootstrap.ClientProvider() {
			@Override public SynapseAdminClient getAdminClient() { return null; }
			@Override public SynapseClient getSynapseClient() { return null; }
			@Override public BridgeClient getBridgeClient() { return request.getBridgeUser().getBridgeClient(); }
		});
		bootstrap.createTrackers();

		request.setNotification("Created any outstanding trackers.");
		return "redirect:/admin/trackers/index.html";
	}
	
	@RequestMapping(value = "/trackers/update", method = RequestMethod.GET)
	public String updateTrackers(final BridgeRequest request) throws Exception {
		// Going to walk the specifications and create any new columns that need to be created 
		// to support the spec. You can't go the other way: given some data, not clear what to do 
		// with it (unless we have a default "naked form" that just shows the fields in some
		// default way, until a form design is added).
		
		request.setNotification("Based on tracker specifications, updated the participant data object model(s).");
		return "redirect:/admin/trackers/index.html";
	}

}
