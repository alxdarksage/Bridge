package org.sagebionetworks.bridge.webapp.controllers;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * For any controller handling GET or POST requests that would require reference 
 * data for the site-wide mobile navigation, or a sign in form for the normal 
 * pages, etc. Ajax-based controllers should not subclass from this. 
 */
public class NonAjaxControllerBase {
	
	private Comparator<ParticipantDataDescriptor> pddComparator = new Comparator<ParticipantDataDescriptor>() {
		@Override public int compare(ParticipantDataDescriptor pdd0, ParticipantDataDescriptor pdd1) {
			return pdd0.getName().compareTo(pdd1.getName());
		}
	};
	
	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	public void setBridgeClient(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}

	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("allCommunities")
	public List<Community> communities(BridgeRequest request) throws SynapseException {
		return bridgeClient.getAllCommunities(ClientUtils.LIMIT, 0).getResults();
	}

	@ModelAttribute("trackers")
	public List<ParticipantDataDescriptor> trackers(BridgeRequest request, Model model) throws SynapseException, ParseException {
		if (request.getBridgeUser().isAuthenticated()) {
			BridgeClient bridge = request.getBridgeUser().getBridgeClient();
			List<ParticipantDataDescriptor> trackers = bridge.getAllParticipantDataDescriptors(ClientUtils.LIMIT, 0L)
					.getResults();
			Collections.sort(trackers, pddComparator);
			return trackers; 
		}
		return Collections.emptyList();
	}

}
