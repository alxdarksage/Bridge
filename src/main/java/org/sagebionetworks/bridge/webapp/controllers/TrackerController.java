package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataEventValue;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.Specification;
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

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Controller
public class TrackerController extends JournalControllerBase {

	private static abstract class EventComparator implements Comparator<ParticipantDataRow> {
		private final String eventColumnName;

		public EventComparator(String eventColumnName) {
			this.eventColumnName = eventColumnName;
		}

		@Override
		public int compare(ParticipantDataRow o1, ParticipantDataRow o2) {
			ParticipantDataEventValue eventValue1 = (ParticipantDataEventValue) o1.getData().get(eventColumnName);
			ParticipantDataEventValue eventValue2 = (ParticipantDataEventValue) o2.getData().get(eventColumnName);
			return compare(eventValue1, eventValue2);
		}

		abstract public int compare(ParticipantDataEventValue e1, ParticipantDataEventValue e2);
	}

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}", method = RequestMethod.GET)
	public ModelAndView viewTrackers(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, ModelAndView model) throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);

		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		model.addObject("spec", spec);

		if ("medication".equals(dwc.getDescriptor().getType())) {
			final String eventColumn = dwc.getDescriptor().getEventColumnName();

			List<ParticipantDataRow> currentRows = client.getCurrentRows(trackerId, false);
			// sort by name?
			Collections.sort(currentRows, new EventComparator(eventColumn) {
				@Override
				public int compare(ParticipantDataEventValue e1, ParticipantDataEventValue e2) {
					return e1.getName().compareTo(e2.getName());
				}
			});

			List<ParticipantDataRow> historyRows = client.getHistoryRows(trackerId, null, null, false);
			Multimap<String, ParticipantDataRow> indexedHistoryRows = Multimaps.index(historyRows,
					new Function<ParticipantDataRow, String>() {
				@Override
				public String apply(ParticipantDataRow input) {
					return ((ParticipantDataEventValue)input.getData().get(eventColumn)).getGrouping();
				}
			});

			model.addObject("current", currentRows);
			model.addObject("past", indexedHistoryRows.asMap().values());
			model.setViewName("journal/trackers/index");
			return model;
		}

		if ("event".equals(dwc.getDescriptor().getType())) {
			List<ParticipantDataRow> currentRows = client.getCurrentRows(trackerId, false);

			List<ParticipantDataRow> historyRows = client.getHistoryRows(trackerId, null, null, false);

			model.addObject("current", currentRows);
			model.addObject("past", historyRows);
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
		PaginatedResults<ParticipantDataRow> paginatedRowSet = client.getRawParticipantData(trackerId, ClientUtils.LIMIT, 0, false);
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, null);
		ClientUtils.exportParticipantData(response, dwc.getDescriptor(), dwc.getColumns(), paginatedRowSet);
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
