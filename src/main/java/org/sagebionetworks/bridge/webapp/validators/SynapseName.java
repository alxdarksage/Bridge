package org.sagebionetworks.bridge.webapp.validators;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = SynapseNameValidator.class)
@Documented
public @interface SynapseName {

	//"Name can only contain letters, numbers, spaces, dot (.), dash (-), underscore (_) and must be at least 3 characters long.";
    String message() default "org.sagebionetworks.bridge.webapp.validators.SynapseName.message";
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};    

}