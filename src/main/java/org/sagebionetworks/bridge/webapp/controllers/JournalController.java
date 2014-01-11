package org.sagebionetworks.bridge.webapp.controllers;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.bridge.webapp.validators.SpecificationBasedValidator;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.sagebionetworks.repo.model.table.RowSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class JournalController {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@ModelAttribute("dynamicForm")
	public DynamicForm communityForm() {
		return new DynamicForm();
	}

	@ModelAttribute("descriptors")
	public List<ParticipantDataDescriptor> allDescriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataDescriptor> allDescriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0L);
		Collections.sort(allDescriptors.getResults(), new Comparator<ParticipantDataDescriptor>() {
			@Override
			public int compare(ParticipantDataDescriptor arg0, ParticipantDataDescriptor arg1) {
				return arg0.getDescription().compareTo(arg1.getDescription());
			}
			
		});
		return allDescriptors.getResults();
	}

	@ModelAttribute
	public void descriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataDescriptor> descriptors = client.getParticipantDatas(ClientUtils.LIMIT, 0L);
		ClientUtils.prepareDescriptorsByStatus(model, client, descriptors);
	}

	@RequestMapping(value = "/journal", method = RequestMethod.GET)
	public ModelAndView viewAllForms(BridgeRequest request, ModelAndView model) throws SynapseException {
		// Force redirect to sign in
		request.getBridgeUser().getBridgeClient();
		model.setViewName("journal/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}", method = RequestMethod.GET)
	public ModelAndView viewForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		model.addObject("spec", spec);
		ClientUtils.prepareParticipantData(client, model, spec, formId);
		model.setViewName("journal/forms/index");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}", method = RequestMethod.POST, params = "delete=delete")
	public String batchForms(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @RequestParam("rowSelect") Set<String> rowSelects)
			throws SynapseException {
		
		if (rowSelects != null) {
			BridgeClient client = request.getBridgeUser().getBridgeClient();
		}
		request.setNotification("Not implemented");
		return "redirect:/journal/"+participantId+"/forms/"+formId+".html";
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.GET)
	public ModelAndView newSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model)
			throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		Set<String> defaultedFields = ClientUtils.defaultValuesFromPriorForm(client, spec, dynamicForm, formId);
		model.addObject("defaultedFields", defaultedFields);
		
		model.setViewName("journal/forms/new");
		return model;
	}

	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/new", method = RequestMethod.POST)
	public ModelAndView createSurvey(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @ModelAttribute DynamicForm dynamicForm, BindingResult result,
			ModelAndView model) throws SynapseException {

		createRow(request, formId, dynamicForm, result, model);
		if (result.hasErrors()) {
			model.setViewName("journal/forms/new");
			return model;
		}
		
		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/"+participantId+"/forms/"+formId+".html");
		return model;
	}

	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.POST)
	public ModelAndView updateRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId, @ModelAttribute DynamicForm dynamicForm,
			BindingResult result, ModelAndView model) throws SynapseException {

		updateRow(request, formId, dynamicForm, result, model, rowId);
		model.addObject("rowId", rowId);
		if (result.hasErrors()) {
			model.setViewName("journal/forms/edit");
			return model;
		}

		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/" + participantId + "/forms/" + formId + ".html");
		return model;
	}

	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/ajax/new", method = RequestMethod.POST)
	@ResponseBody
	public Object appendValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {

		RowSet data = createRow(request, formId, dynamicForm, null, model);

		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", data.getRows().get(0).getRowId());
		return result;
	}

	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/ajax/row/{rowId}", method = RequestMethod.POST)
	@ResponseBody
	public Object setValuesAjax(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model) throws SynapseException {

		RowSet data = updateRow(request, formId, dynamicForm, null, model, rowId);

		Map<String, Object> result = Maps.newHashMap();
		result.put("rowId", data.getRows().get(0).getRowId());
		return result;
	}

	private RowSet createRow(BridgeRequest request, String formId, DynamicForm dynamicForm, BindingResult result, ModelAndView model)
			throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		spec.setSystemSpecifiedValues(dynamicForm.getValues());
		
		if (result != null) {
			SpecificationBasedValidator validator = new SpecificationBasedValidator(spec);
			validator.validate(dynamicForm, result);
			if (result.hasErrors()) {
				return null;
			}
		}
		RowSet data = ParticipantDataUtils.getRowSetForCreate(spec, dynamicForm.getValues());
		return client.appendParticipantData(formId, data);
	}

	private RowSet updateRow(BridgeRequest request, String formId, DynamicForm dynamicForm, BindingResult result, ModelAndView model,
			long rowId) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		spec.setSystemSpecifiedValues(dynamicForm.getValues());

		if (result != null) {
			SpecificationBasedValidator validator = new SpecificationBasedValidator(spec);
			validator.validate(dynamicForm, result);
			if (result.hasErrors()) {
				return null;
			}
		}
		RowSet data = ParticipantDataUtils.getRowSetForUpdate(spec, dynamicForm.getValues(), rowId);
		return client.updateParticipantData(formId, data);
	}

	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.GET)
	public ModelAndView viewRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		model.addObject("rowId", rowId);
		
		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		FormUtils.valuesToDynamicForm(dynamicForm, paginatedRowSet.getResults(), rowId);
		
		model.setViewName("journal/forms/edit");
		return model;
	}

}
