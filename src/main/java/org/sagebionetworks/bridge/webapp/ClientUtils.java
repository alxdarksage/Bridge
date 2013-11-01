package org.sagebionetworks.bridge.webapp;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class ClientUtils {

	public static void globalFormError(BindingResult result, String formName, String key) {
		result.addError(new ObjectError(formName, new String[] { key }, null, key));
	}

	public static void formError(BindingResult result, String formName, String message) {
		result.addError(new ObjectError(formName, message));
	}

}
