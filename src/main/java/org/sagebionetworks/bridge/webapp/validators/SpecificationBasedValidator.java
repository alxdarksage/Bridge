package org.sagebionetworks.bridge.webapp.validators;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataLongValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.DoubleFormField;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.collect.Lists;

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
		Map<String,String> values = dynamicForm.getValuesMap();
		
		for(FormElement field : spec.getAllFormElements()) {
			if (field.getDataColumn() != null) {
				validateThisField(errors, values, field);
			}
		}
		/*for (FieldError error : errors.getFieldErrors()) {
			logger.info(error.getCode());
		}
		for (ObjectError error : errors.getGlobalErrors()) {
			logger.info(error.getCode());
		}*/
	}

	private void validateThisField(Errors errors, Map<String, String> values, FormElement field) {
		String value = values.get(field.getName());
		if (StringUtils.isBlank(value)) {
			if (field.isRequired()) {
				errors.rejectValue("valuesMap['"+field.getName()+"']", field.getName()+".required", field.getLabel() + " is required.");
			}
			return;
		}
		ParticipantDataColumnType dataType = field.getDataColumn().getColumnType();
		Converter<List<String>,ParticipantDataValue> converter = field.getParticipantDataValueConverter();
		if (converter != null) {
			try {
				ParticipantDataValue captured = converter.convert(Lists.newArrayList(value));
				
				if (dataType == ParticipantDataColumnType.DOUBLE) {
					Double converted = ((ParticipantDataDoubleValue)captured).getValue();
					validateBoundaryRanges(field, errors, converted);
				} else if (dataType == ParticipantDataColumnType.LONG) {
					Long converted = ((ParticipantDataLongValue)captured).getValue();
					validateBoundaryRanges(field, errors, converted.doubleValue());
				}
			} catch(Throwable e) {
				String message = field.getLabel() + " is not a "+dataType.name().toLowerCase()+".";
				String key = field.getName() + ".invalid_" + dataType.name().toLowerCase();
				errors.rejectValue("valuesMap['"+field.getName()+"']", key, message);
			}
		}
	}

	private void validateBoundaryRanges(FormElement field, Errors errors, Double converted) {
		DoubleFormField numeric = (DoubleFormField)field;
		if (numeric.getMinValue() != null && converted < numeric.getMinValue()) {
			errors.rejectValue("valuesMap['"+field.getName()+"']", field.getName()+".too_small", field.getLabel() + " is less than "+numeric.getMinValue().toString()+".");
		}
		if (numeric.getMaxValue() != null && converted > numeric.getMaxValue()) {
			errors.rejectValue("valuesMap['"+field.getName()+"']", field.getName()+".too_large", field.getLabel() + " is greater than "+numeric.getMaxValue().toString()+".");
		}
	}

}
