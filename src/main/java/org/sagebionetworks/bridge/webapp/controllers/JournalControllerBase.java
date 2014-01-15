package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;

import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.bridge.webapp.validators.SpecificationBasedValidator;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.table.RowSet;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public class JournalControllerBase {
	
	@Resource(name = "specificationResolver")
	protected SpecificationResolver specResolver;

	protected RowSet createRow(BridgeRequest request, String formId, DynamicForm dynamicForm, BindingResult result, ModelAndView model)
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
	
	protected RowSet updateRow(BridgeRequest request, String formId, DynamicForm dynamicForm, BindingResult result, ModelAndView model,
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

}
