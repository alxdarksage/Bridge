package org.sagebionetworks.bridge.webapp.validators;

import java.util.Map;

import org.apache.commons.validator.routines.DoubleValidator;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.NumericFormField;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.springframework.validation.Errors;
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
			validateThisField(errors, values, field);
		}
	}

	private void validateThisField(Errors errors, Map<String, String> values, FormElement field) {
		String value = values.get(field.getName());
		if (StringUtils.isBlank(value)) {
			if (field.isRequired()) {
				errors.rejectValue("values['"+field.getName()+"']", field.getName()+".required", field.getLabel() + " is required.");
			}
			return;
		}
		if (field.getType() == ParticipantDataColumnType.DOUBLE) {
			Double converted = DoubleValidator.getInstance().validate(value);
			if (converted == null) {
				errors.rejectValue("values['"+field.getName()+"']", field.getName()+".not_a_decimal_number", field.getLabel() + " is not a decimal number.");
			} else {
				validateBoundaryRanges(field, errors, converted);
			}
		} else if (field.getType() == ParticipantDataColumnType.LONG) {
			Long converted = LongValidator.getInstance().validate(value);
			if (converted == null) {
				errors.rejectValue("values['"+field.getName()+"']", field.getName()+".not_a_number", field.getLabel() + " is not a number.");
			}
			validateBoundaryRanges(field, errors, converted.doubleValue());
		} else if (field.getType() == ParticipantDataColumnType.STRING) {
			// regular expression matching
		} else if (field.getType() == ParticipantDataColumnType.DATETIME) {
			// datetime
			// range limits
		} else if (field.getType() == ParticipantDataColumnType.BOOLEAN) {
			if (!("true".equals(value) || "false".equals(value))) {
				errors.rejectValue("values['"+field.getName()+"']", field.getName()+".not_a_boolean", field.getLabel() + " must be true or false.");
			}
		}
	}

	private void validateBoundaryRanges(FormElement field, Errors errors, Double converted) {
		NumericFormField numeric = (NumericFormField)field;
		if (numeric.getMinValue() != null && converted < numeric.getMinValue()) {
			errors.rejectValue("values['"+field.getName()+"']", field.getName()+".too_small", field.getLabel() + " is less than "+numeric.getMinValue().toString()+".");
		}
		if (numeric.getMaxValue() != null && converted > numeric.getMaxValue()) {
			errors.rejectValue("values['"+field.getName()+"']", field.getName()+".too_large", field.getLabel() + " is greater than "+numeric.getMaxValue().toString()+".");
		}
	}

}
