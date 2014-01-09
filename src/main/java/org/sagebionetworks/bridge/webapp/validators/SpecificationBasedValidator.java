package org.sagebionetworks.bridge.webapp.validators;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SpecificationBasedValidator implements Validator {

	private static final Logger logger = LogManager.getLogger(SpecificationBasedValidator.class.getName());
	
	private Specification spec;
	
	public SpecificationBasedValidator(Specification spec) {
		this.spec = spec;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return DynamicForm.class.equals(clazz);
	}

	@Override
	public void validate(Object form, Errors errors) {
		DynamicForm dynamicForm = (DynamicForm)form;
		Map<String,String> values = dynamicForm.getValues();
		
		for(FormElement field : spec.getAllFormElements()) {
			if (field.getType() == null) {
				continue;
			}
			String value = values.get(field.getName());
			if (field.isRequired() && StringUtils.isBlank(value)) {
				errors.rejectValue("values['"+field.getName()+"']", field.getName()+".required", field.getLabel() + " is required.");
			}
		}
	}

}
