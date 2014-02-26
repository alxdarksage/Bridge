package org.sagebionetworks.bridge.webapp.validators;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.specs.trackers.CompleteBloodCount;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SpecificationBasedValidatorTest {

	private Errors errors;
	private SpecificationBasedValidator validator;
	private DynamicForm dynamicForm;
	
	@Before
	public void beforeTest() {
		validator = new SpecificationBasedValidator(new CompleteBloodCount()); 
		dynamicForm = new DynamicForm();
		dynamicForm.setValues(Maps.<String,String>newHashMap());
		errors = new BindException(dynamicForm, "dynamicForm");
	}
	
	@Test
	public void test() {
		Set<String> messages = Sets.newHashSet();
		
		// Invalid data type
		dynamicForm.getValuesMap().put("modified_on", "test bad value");
		messages.add("modified_on.invalid_datetime");
		
		dynamicForm.getValuesMap().put("lymphocytes", "test bad value");
		messages.add("lymphocytes.invalid_double");
		
		// Value out of range
		dynamicForm.getValuesMap().put("monocytes", "-20");
		messages.add("monocytes.too_small");
		
		dynamicForm.getValuesMap().put("hct", "220");
		messages.add("hct.too_large");
		
		// test required: not including the collected_on value.
		messages.add("collected_on.required");
		
		validator.validate(dynamicForm, errors);
		
		Set<String> actualMessages = Sets.newHashSet();
		for (FieldError error : errors.getFieldErrors()) {
			actualMessages.add(error.getCode());
		}
		assertEquals(messages, actualMessages);
	}

}
