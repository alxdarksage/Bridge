package org.sagebionetworks.bridge.webapp.controllers;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.sagebionetworks.repo.model.table.RowSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

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
	
	// @ModelAttribute("descriptors")
	public List<ParticipantDataDescriptor> descriptors(BridgeRequest request) throws SynapseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataDescriptor> descriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0L);
		Collections.sort(descriptors.getResults(), new Comparator<ParticipantDataDescriptor>() {
			@Override
			public int compare(ParticipantDataDescriptor arg0, ParticipantDataDescriptor arg1) {
				return arg0.getDescription().compareTo(arg1.getDescription());
			}
			
		});
		return descriptors.getResults();
	}
	

	@ModelAttribute
	public void descriptors(BridgeRequest request, Model model) throws SynapseException, ParseException {
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedResults<ParticipantDataDescriptor> descriptors = client.getParticipantDatas(ClientUtils.LIMIT, 0L);

		List<ParticipantDataDescriptor> descriptorsAlways = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsDue = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsIfNew = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsIfChanged = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsNoPrompt = Lists.newArrayListWithExpectedSize(20);
		Date now = new Date();
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.roll(Calendar.MONTH, false);
		Calendar tenMinutesAgo = Calendar.getInstance();
		tenMinutesAgo.add(Calendar.MINUTE, -10);
		List<ParticipantDataStatus> statusUpdateList = Lists.newArrayListWithExpectedSize(20);
		for (ParticipantDataDescriptor descriptor : descriptors.getResults()) {
			ParticipantDataStatus status = descriptor.getStatus();
			boolean needsUpdate = false;
			Date lastStarted = descriptor.getStatus().getLastStarted();
			Date lastPrompted = descriptor.getStatus().getLastPrompted();
			boolean lastEntryComplete = BooleanUtils.isTrue(descriptor.getStatus().getLastEntryComplete());

			// we want to prompt when:
			// - something new is due and we haven't prompted yet (lastPrompted < lastCron && lastStarted < lastCron)
			// - something new or conditional is due and we haven't prompted for a month (?? frequency ??)

			// did we ever prompt?
			boolean firstPrompt = lastPrompted == null;

			// is it a month ago, or less than 10 minutes ago (we want to make sure the user sees)
			boolean repeatPrompt = lastPrompted != null && lastPrompted.after(lastMonth.getTime());

			// is it a month ago, or less than 10 minutes ago (we want to make sure the user sees)
			boolean repeatPromptWasRecent = lastPrompted != null && lastPrompted.after(tenMinutesAgo.getTime());

			boolean shouldPrompt = firstPrompt || repeatPrompt || repeatPromptWasRecent;

			boolean repeatDue = false;
			if (!StringUtils.isEmpty(descriptor.getRepeatFrequency())) {
				if (lastStarted != null) {
					CronExpression cronExpression = new CronExpression(descriptor.getRepeatFrequency());
					if (cronExpression.getNextValidTimeAfter(lastStarted).before(now)) {
						repeatDue = true;
					}
				}
			}

			List<ParticipantDataDescriptor> promptList = null;
			switch (descriptor.getRepeatType()) {
			case ALWAYS:
				promptList = descriptorsAlways;
				break;
			case IF_NEW:
				if (shouldPrompt || repeatDue) {
					promptList = descriptorsIfNew;
				}
				break;
			case IF_CHANGED:
				if (shouldPrompt || repeatDue) {
					promptList = descriptorsIfChanged;
				}
				break;
			case ONCE:
				if (!lastEntryComplete && shouldPrompt) {
					promptList = descriptorsDue;
				}
				break;
			case REPEATED:
				if (repeatDue) {
					status.setLastEntryComplete(true);
					needsUpdate = true;
				}
				if (repeatDue || (!lastEntryComplete && shouldPrompt)) {
					promptList = descriptorsDue;
				}
				break;
			}
			if (promptList != null) {
				promptList.add(descriptor);
				if (!repeatPromptWasRecent) {
					status.setLastPrompted(now);
					needsUpdate = true;
				}
			} else {
				descriptorsNoPrompt.add(descriptor);
			}

			if (needsUpdate) {
				if (!repeatPromptWasRecent) {
					status.setLastPrompted(now);
				}
				statusUpdateList.add(status);
			}
		}

		if (statusUpdateList.size() > 0) {
			ParticipantDataStatusList dataStatusList = new ParticipantDataStatusList();
			dataStatusList.setUpdates(statusUpdateList);
			client.sendParticipantDataDescriptorUpdates(dataStatusList);
		}

		model.addAttribute("descriptorsAlways", descriptorsAlways);
		model.addAttribute("descriptorsDue", descriptorsDue);
		model.addAttribute("descriptorsIfNew", descriptorsIfNew);
		model.addAttribute("descriptorsIfChanged", descriptorsIfChanged);
		model.addAttribute("descriptorsNoPrompt", descriptorsNoPrompt);
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
			@PathVariable("formId") String formId, @ModelAttribute DynamicForm dynamicForm,
			ModelAndView model) throws SynapseException {

		BridgeClient client = request.getBridgeUser().getBridgeClient();
		
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		spec.setSystemSpecifiedValues(dynamicForm.getValues());
		RowSet data = ParticipantDataUtils.getRowSetForCreate(spec, dynamicForm.getValues());
		client.appendParticipantData(formId, data);
		
		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/"+participantId+"/forms/"+formId+".html");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.GET)
	public ModelAndView viewRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId, ModelAndView model,
			@ModelAttribute DynamicForm dynamicForm) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		
		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		FormUtils.valuesToDynamicForm(dynamicForm, paginatedRowSet.getResults(), rowId);
		
		model.addObject("rowId", rowId);
		
		model.setViewName("journal/forms/edit");
		return model;
	}
	
	@RequestMapping(value = "/journal/{participantId}/forms/{formId}/row/{rowId}", method = RequestMethod.POST)
	public ModelAndView updateRow(BridgeRequest request, @PathVariable("participantId") String participantId,
			@PathVariable("formId") String formId, @PathVariable("rowId") long rowId,
			@ModelAttribute DynamicForm dynamicForm, ModelAndView model) throws SynapseException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
		
		Specification spec = ClientUtils.prepareSpecificationAndDescriptor(client, specResolver, model, formId);
		spec.setSystemSpecifiedValues(dynamicForm.getValues());
		RowSet data = ParticipantDataUtils.getRowSetForUpdate(spec, dynamicForm.getValues(),
				paginatedRowSet.getResults(), rowId);
		client.updateParticipantData(formId, data);
		
		request.setNotification("Survey updated.");
		model.setViewName("redirect:/journal/"+participantId+"/forms/"+formId+".html");
		return model;
	}
}
