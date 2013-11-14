package org.sagebionetworks.bridge.webapp;

import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class ClientUtils {

	public static void globalFormError(BindingResult result, String formName, String key) {
		result.addError(new ObjectError(formName, new String[] { key }, null, key));
	}

	public static void formError(BindingResult result, String formName, String message) {
		result.addError(new ObjectError(formName, message));
	}
	
	public static void dumpErrors(Logger logger, BindingResult result) {
		for (ObjectError error : result.getAllErrors()) {
			logger.info(String.format("ALL ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
		for (ObjectError error : result.getGlobalErrors()) {
			logger.info(String.format("GLOBAl ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
		for (ObjectError error : result.getFieldErrors()) {
			logger.info(String.format("FIELD ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
	}

}
