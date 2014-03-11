package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MedicationController {

	private static final Logger logger = LogManager.getLogger(MedicationController.class.getName());

	@RequestMapping(value = "/medication/{trackerId}/export", method = RequestMethod.GET)
	@ResponseBody
	public void exportMedication(BridgeRequest request, HttpServletResponse response, @PathVariable("trackerId") String trackerId)
			throws SynapseException, IOException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		List<ParticipantDataRow> historyRows = client.getHistoryRows(trackerId, null, null, false);
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, null);
		ClientUtils.exportRows(historyRows, response, dwc);
	}
}
