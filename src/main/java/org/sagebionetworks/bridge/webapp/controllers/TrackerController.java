package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.trackers.MedicationTracker;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.IdList;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

@Controller
public class TrackerController extends JournalControllerBase {

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}", method = RequestMethod.GET)
	public ModelAndView viewTrackers(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, ModelAndView model) throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);

		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		model.addObject("spec", spec);

		if (dwc.getDescriptor().getName().equals(MedicationTracker.MEDICATIONS_NAME)) {
			List<ParticipantDataRow> currentRows = client.getCurrentRows(trackerId);
			final String start = dwc.getDescriptor().getDatetimeStartColumnName();
			Collections.sort(currentRows, new Comparator<ParticipantDataRow>() {
				@Override
				public int compare(ParticipantDataRow o1, ParticipantDataRow o2) {
					return ((ParticipantDataDatetimeValue) o1.getData().get(start)).getValue().compareTo(
							((ParticipantDataDatetimeValue) o2.getData().get(start)).getValue());
				}
			});
			List<ParticipantDataRow> historyRows = client.getHistoryRows(trackerId, null, null);
			// first sort by name
			Collections.sort(historyRows, new Comparator<ParticipantDataRow>() {
				@Override
				public int compare(ParticipantDataRow o1, ParticipantDataRow o2) {
					String value1 = ValueTranslator.toString(o1.getData().get(MedicationTracker.MEDICATION_FIELD));
					String value2 = ValueTranslator.toString(o2.getData().get(MedicationTracker.MEDICATION_FIELD));
					if (value1 == null) {
						return value2 == null ? 0 : -1;
					} else if (value2 == null) {
						return 1;
					}
					return (value1.compareTo(value2));
				}
			});
			List<List<ParticipantDataRow>> historyRowsGrouped = Lists.newArrayList();
			String lastName = null;
			for (ParticipantDataRow row : historyRows) {
				String name = ValueTranslator.toString(row.getData().get(MedicationTracker.MEDICATION_FIELD));
				if (name == null || !name.equals(lastName)) {
					historyRowsGrouped.add(Lists.<ParticipantDataRow> newArrayList());
					lastName = name;
				}
				historyRowsGrouped.get(historyRowsGrouped.size() - 1).add(row);
			}
			// now sort each group by date first taken
			for (List<ParticipantDataRow> group : historyRowsGrouped) {
				Collections.sort(group, new Comparator<ParticipantDataRow>() {
					@Override
					public int compare(ParticipantDataRow o1, ParticipantDataRow o2) {
						return (((ParticipantDataDatetimeValue) o1.getData().get(start)).getValue()
								.compareTo(((ParticipantDataDatetimeValue) o2.getData().get(start)).getValue()));
					}
				});
			}
			// and sort all by date first taken
			Collections.sort(historyRowsGrouped, new Comparator<List<ParticipantDataRow>>() {
				@Override
				public int compare(List<ParticipantDataRow> o1, List<ParticipantDataRow> o2) {
					return (((ParticipantDataDatetimeValue) o1.get(0).getData().get(start)).getValue()
							.compareTo(((ParticipantDataDatetimeValue) o2.get(0).getData().get(start)).getValue()));
				}
			});
			model.addObject("current", currentRows);
			model.addObject("past", historyRowsGrouped);
			model.setViewName("journal/trackers/index");
			return model;
		}

		List<ParticipantDataRow> rows = ClientUtils.prepareParticipantDataSummary(client, model, spec, dwc.getDescriptor());
		spec.postProcessParticipantDataRows(model.getModelMap(), rows);
		model.setViewName("journal/trackers/index");
		return model;
	}

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/export", method = RequestMethod.GET)
	@ResponseBody
	public void exportTrackers(BridgeRequest request, HttpServletResponse response, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId) throws SynapseException, IOException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataRow> paginatedRowSet = client.getRawParticipantData(trackerId, ClientUtils.LIMIT, 0);
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, null);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, null);
		ClientUtils.exportParticipantData(response, spec, paginatedRowSet);
	}

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}", method = RequestMethod.POST, params = "delete=delete")
	public String batchEditTrackers(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @RequestParam("rowSelect") List<String> rowSelects) throws SynapseException {

		if (rowSelects != null) {
			// Spring silently accepts List<Long> as a rowSelect parameter type, but it does no conversion
			// without registering a lot of crazy conversion stuff.
			List<Long> newList = new ArrayList<Long>();
			for (String value : rowSelects) {
				newList.add(Long.parseLong(value));
			}
			IdList idList = new IdList();
			idList.setList(newList);

			BridgeClient client = request.getBridgeUser().getBridgeClient();
			client.deleteParticipantDataRows(trackerId, idList);
			request.setNotification(rowSelects.size() > 1 ? "RowsDeleted" : "RowDeleted");
		}
		return "redirect:/journal/" + participantId + "/trackers/" + trackerId + ".html";
	}

}
