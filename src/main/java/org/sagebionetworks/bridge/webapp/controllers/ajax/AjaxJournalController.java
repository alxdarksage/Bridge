package org.sagebionetworks.bridge.webapp.controllers.ajax;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.controllers.JournalControllerBase;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

@Controller
public class AjaxJournalController extends JournalControllerBase {

	private static final Logger logger = LogManager.getLogger(AjaxJournalController.class.getName());

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/ajax/new", method = RequestMethod.POST)
	@ResponseBody
	public Object appendValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model)
			throws SynapseException {

		ParticipantDataRow data = createRow(request, participantId, trackerId, dynamicForm, model, null,
				ParticipantDataUtils.getInProcessStatus(trackerId));
		model.setViewName(null);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", data.getRowId());
		return result;
	}

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/ajax/row/{rowId}", method = RequestMethod.POST)
	@ResponseBody
	public Object setValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {

		ParticipantDataRow data = ajaxUpdateRow(request, participantId, trackerId, rowId, dynamicForm,
				ParticipantDataUtils.getInProcessStatus(trackerId));
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", data.getRowId());
		return result;
	}
	
	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/ajax/row/{rowId}/nostatuschange", method = RequestMethod.POST)
	@ResponseBody
	public Object finishValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @PathVariable("rowId") Long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {

		ParticipantDataRow data = ajaxUpdateRow(request, participantId, trackerId, rowId, dynamicForm, null);

		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", data.getRowId());
		return result;
	}
}
