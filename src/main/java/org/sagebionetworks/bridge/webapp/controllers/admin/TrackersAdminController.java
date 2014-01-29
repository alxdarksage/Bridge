package org.sagebionetworks.bridge.webapp.controllers.admin;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.SageBootstrap;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
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
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/admin")
public class TrackersAdminController {

	private static Logger logger = LogManager.getLogger(TrackersAdminController.class.getName());
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;
	
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
	public String createTestTrackers(final BridgeRequest request) throws Exception {

		
		request.setNotification("Created any missing trackers.");
		return "redirect:/admin/trackers/index.html";
	}
	
	@RequestMapping(value = "/trackers/update", method = RequestMethod.GET)
	public ModelAndView updateTrackers(final BridgeRequest request, ModelAndView model) throws Exception {
		List<ParticipantDataDescriptor> descriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0L).getResults();
		
		StringBuilder sb = new StringBuilder();

		createTrackersIfMissing(sb, descriptors);
		createColumnsIfMissing(sb, descriptors);
		
		model.addObject("messages", sb.toString());
		model.setViewName("admin/trackers/index");
		return model;
	}

	private void createColumnsIfMissing(StringBuilder sb, List<ParticipantDataDescriptor> descriptors)
			throws SynapseException {
		for (ParticipantDataDescriptor descriptor : descriptors) {
			Specification spec = specResolver.getSpecification(descriptor.getName());
			if (spec == null) {
				sb.append("<p>Skipped " + descriptor.getName() + ", it has no UI specified.</p>");
				continue;
			}
			sb.append("<p>Examined "+descriptor.getName()+" for changes. ");
			
			PaginatedResults<ParticipantDataColumnDescriptor> columns = client.getParticipantDataColumnDescriptors(
					descriptor.getId(), ClientUtils.LIMIT, 0L);

			Map<String,ParticipantDataColumnDescriptor> columnsByName = Maps.newHashMap();
			for (ParticipantDataColumnDescriptor column : columns.getResults()) {
				columnsByName.put(column.getName(), column);
			}
			if (spec.getAllFormElements() != null) {
				for (FormElement element : spec.getAllFormElements()) {
					if (element.getDataType() != null) {
						if (columnsByName.get(element.getName()) == null) {
							ParticipantDataColumnDescriptor column = element.getDataColumn();
							column.setParticipantDataDescriptorId(descriptor.getId());
							client.createParticipantDataColumnDescriptor(column);
							sb.append("<br>&bull; created a column for '" + element.getName() + "'. ");
						} else {
							sb.append("<br>&bull; skipping column '" + element.getName() + "', it already exists. ");
						}
					}
				}
			}
			sb.append("</p>");
		}
	}

	private void createTrackersIfMissing(StringBuilder sb, List<ParticipantDataDescriptor> descriptors)
			throws SynapseException {
		// Create any trackers that aren't currently in the system.
		for(Specification spec : specResolver.getAllSpecifications()) {
			ParticipantDataDescriptor descriptor = null;
			for (ParticipantDataDescriptor d : descriptors) {
				if (d.getName().equals(spec.getName())) {
					descriptor = d;
				}
			}
			if (descriptor == null) {
				descriptor = ParticipantDataUtils.getDescriptor(spec);
				descriptor = client.createParticipantDataDescriptor(descriptor);
				
				List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(
						descriptor.getId(), spec);
				
				for (ParticipantDataColumnDescriptor column : columns) {
					client.createParticipantDataColumnDescriptor(column);
				}
				sb.append("<p>Created tracker " + spec.getName() + ". </p>");
			}
		}
	}
	

}
