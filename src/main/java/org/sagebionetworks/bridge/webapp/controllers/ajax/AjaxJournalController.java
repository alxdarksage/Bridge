package org.sagebionetworks.bridge.webapp.controllers.ajax;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

@Controller
public class AjaxJournalController {

	private static final Logger logger = LogManager.getLogger(AjaxJournalController.class.getName());
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;

	@RequestMapping(value = "/journal/{participantId}/trackers/{trackerId}/ajax/new", method = RequestMethod.POST)
	@ResponseBody
	public Object appendValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("trackerId") String trackerId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model)
			throws SynapseException {

		ParticipantDataRow data = ajaxCreateRow(request, participantId, trackerId, dynamicForm, model, null,
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
	
	private ParticipantDataRow ajaxCreateRow(BridgeRequest request, String participantId, String trackerId, DynamicForm dynamicForm, 
			ModelAndView model, BindingResult result, ParticipantDataStatusList statuses) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, model);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, model);
		List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForCreate(spec, dynamicForm.getValuesMap());
		data = client.appendParticipantData(trackerId, data);
		client.sendParticipantDataDescriptorUpdates(statuses);
		return data.get(0);
	}	
	
	private ParticipantDataRow ajaxUpdateRow(BridgeRequest request, String participantId, String trackerId,
			long rowId, DynamicForm dynamicForm, ParticipantDataStatusList statuses)
			throws SynapseException {
		
		// Not passing the result object into the method is the equivalent of saying there's no 
		// UI on the request, it's an Ajax request and the client will take care of the results.
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ParticipantDataDescriptorWithColumns dwc = ClientUtils.prepareDescriptor(client, trackerId, null);
		Specification spec = ClientUtils.prepareSpecification(specResolver, dwc, null);
		spec.setSystemSpecifiedValues(dynamicForm.getValuesMap());

		List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForUpdate(spec, dynamicForm.getValuesMap(), rowId);
		data = client.updateParticipantData(trackerId, data);
		if (statuses != null) {
			client.sendParticipantDataDescriptorUpdates(statuses);	
		}
		return data.get(0);
	}

	@RequestMapping(value = "/journal/{participantId}/series/{series}/ajax/timeseries", method = RequestMethod.GET)
	@ResponseBody
	public Object setValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("series") String series, @RequestParam(required = false) List<String> columns) throws SynapseException,
			UnsupportedEncodingException {

		return ClientUtils.getTimeSeries(request.getBridgeUser().getBridgeClient(), series, columns);
	}
}
