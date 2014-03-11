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
public class AjaxQuestionController {
	private static final Logger logger = LogManager.getLogger(AjaxQuestionController.class.getName());

	@RequestMapping(value = "/question/{trackerId}/ajax/new", method = RequestMethod.POST)
	@ResponseBody
	public Object newQuestion(BridgeRequest request, @PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();

		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		ParticipantDataRow row = ClientUtils.createRowFromForm(dwc, dynamicForm.getValuesMap());

		List<ParticipantDataRow> rows = client.appendParticipantData(trackerId, Collections.<ParticipantDataRow> singletonList(row));

		String step = dynamicForm.getValuesMap().get("step");
		boolean done = false;
		if ("done".equals(step)) {
			step = null;
			done = true;
		}

		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(trackerId);
		status.setLastStarted(new Date());
		status.setLastPrompted(new Date());
		status.setLastEntryComplete(done);
		status.setCurrentStep(step);
		statuses.setUpdates(Collections.singletonList(status));

		client.sendParticipantDataDescriptorUpdates(statuses);

		model.setViewName(null);

		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", rows.get(0).getRowId());
		result.put("done", done);
		result.put("step", step);
		return result;
	}

	@RequestMapping(value = "/question/{trackerId}/row/{rowId}/ajax/update", method = RequestMethod.POST)
	@ResponseBody
	public Object updateQuestion(BridgeRequest request, @PathVariable("trackerId") String trackerId, @PathVariable("rowId") Long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();

		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		ParticipantDataRow row = ClientUtils.createRowFromForm(dwc, dynamicForm.getValuesMap());
		row.setRowId(rowId);

		List<ParticipantDataRow> rows = client.updateParticipantData(trackerId, Collections.<ParticipantDataRow> singletonList(row));

		String step = dynamicForm.getValuesMap().get("step");
		boolean done = false;
		if ("done".equals(step)) {
			step = null;
			done = true;
		}

		ParticipantDataStatusList statuses = new ParticipantDataStatusList();
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setParticipantDataDescriptorId(trackerId);
		status.setLastStarted(new Date());
		status.setLastPrompted(new Date());
		status.setLastEntryComplete(done);
		status.setCurrentStep(step);
		statuses.setUpdates(Collections.singletonList(status));

		client.sendParticipantDataDescriptorUpdates(statuses);

		model.setViewName(null);

		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", rows.get(0).getRowId());
		result.put("done", done);
		result.put("step", step);
		return result;
	}

	@RequestMapping(value = "/question/{trackerId}/ajax/skip", method = RequestMethod.POST)
	@ResponseBody
	public void skipQuestion(BridgeRequest request, @PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model) throws SynapseException {
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
