package org.sagebionetworks.bridge.webapp.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * "Name can only contain letters, numbers, spaces, dot (.), dash (-), underscore (_)"
 * 
 * Not sure if this all entities, all database things, etc. but there you go.
 *
 */
public class SynapseNameValidator implements ConstraintValidator<SynapseName, String> {

	@Override
	public void initialize(SynapseName name) {
	}
	
	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		return (name != null && name.matches("^[a-zA-Z0-9-_\\s\\.]{3,}"));
	}

}
