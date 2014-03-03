package org.sagebionetworks.bridge.webapp.controllers.ajax;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataEventValue;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

@Controller
public class AjaxMedicationController {
	private static final Logger logger = LogManager.getLogger(AjaxMedicationController.class.getName());

	@RequestMapping(value = "/medication/{trackerId}/ajax/new", method = RequestMethod.POST)
	@ResponseBody
	public Object newMedication(BridgeRequest request, @PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model)
			throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();

		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		ParticipantDataRow row = ClientUtils.createRowFromForm(dwc, dynamicForm.getValuesMap());
		ParticipantDataEventValue event = (ParticipantDataEventValue) row.getData().get(dwc.getDescriptor().getEventColumnName());
		event.setGrouping(event.getName());

		List<ParticipantDataRow> rows = client.appendParticipantData(trackerId, Collections.<ParticipantDataRow> singletonList(row));

		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(trackerId);
		status.setLastStarted(new Date());
		status.setLastPrompted(new Date());
		statuses.setUpdates(Collections.singletonList(status));

		client.sendParticipantDataDescriptorUpdates(statuses);

		model.setViewName(null);

		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", rows.get(0).getRowId());
		return result;
	}

	@RequestMapping(value = "/medication/{trackerId}/row/{rowId}/ajax/changeDosage", method = RequestMethod.POST)
	@ResponseBody
	public Object changeDosageMedication(BridgeRequest request, @PathVariable("trackerId") String trackerId,
			@PathVariable("rowId") Long rowId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model, BindingResult result)
			throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();

		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		ParticipantDataRow newRow = ClientUtils.createRowFromForm(dwc, dynamicForm.getValuesMap());
		ParticipantDataEventValue newEvent = (ParticipantDataEventValue) newRow.getData().get(dwc.getDescriptor().getEventColumnName());
		newEvent.setGrouping(newEvent.getName());

		// first close old row and then create new one
		ParticipantDataRow oldRow = client.getParticipantDataRow(trackerId, rowId);
		ParticipantDataEventValue oldEvent = (ParticipantDataEventValue) oldRow.getData().get(dwc.getDescriptor().getEventColumnName());
		oldEvent.setEnd(newEvent.getStart());
		client.updateParticipantData(trackerId, Collections.<ParticipantDataRow> singletonList(oldRow));

		List<ParticipantDataRow> rows = client.appendParticipantData(trackerId, Collections.<ParticipantDataRow> singletonList(newRow));

		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(trackerId);
		Date now = new Date();
		status.setLastAnswered(now);
		status.setLastStarted(now);
		status.setLastPrompted(now);
		statuses.setUpdates(Collections.singletonList(status));

		client.sendParticipantDataDescriptorUpdates(statuses);

		model.setViewName(null);

		return Collections.<String, Object> singletonMap("rowId", rows.get(0).getRowId());
	}

	@RequestMapping(value = "/medication/{trackerId}/row/{rowId}/ajax/close", method = RequestMethod.POST)
	@ResponseBody
	public Object closeMedication(BridgeRequest request, @PathVariable("trackerId") String trackerId, @PathVariable("rowId") Long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();

		// ParticipantDataValue end = ISODateTimeConverter.INSTANCE.convert(MedicationTracker.END_DATE_FIELD, dynamicForm.getValuesMap());
		String value = dynamicForm.getValuesMap().get("medication-end");
		DateTimeFormatter formatter;
		if (value.indexOf('T') >= 0) {
			formatter = ISODateTimeFormat.dateTime();
		} else {
			formatter = ISODateTimeFormat.date();
		}
		Date endDate = DateTime.parse(value, formatter).toDate();

		// close old row
		ParticipantDataRow oldRow = client.getParticipantDataRow(trackerId, rowId);
		ParticipantDataEventValue oldEvent = (ParticipantDataEventValue) oldRow.getData().get("medication");
		oldEvent.setEnd(endDate.getTime());
		client.updateParticipantData(trackerId, Collections.<ParticipantDataRow> singletonList(oldRow));

		Map<String, Object> result = Maps.newHashMap();
		result.put("result", "ok");
		return result;
	}

	@RequestMapping(value = "/medication/{trackerId}/ajax/nochange", method = RequestMethod.GET)
	@ResponseBody
	public void nochangeToMedications(BridgeRequest request, @PathVariable("trackerId") String trackerId) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(trackerId);
		Date now = new Date();
		status.setLastPrompted(now);
		status.setLastAnswered(now);
		statuses.setUpdates(Collections.singletonList(status));

		client.sendParticipantDataDescriptorUpdates(statuses);
	}

}
