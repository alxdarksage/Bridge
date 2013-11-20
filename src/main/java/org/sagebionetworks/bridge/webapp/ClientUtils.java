package org.sagebionetworks.bridge.webapp;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ClientUtils {
	
	public static class ExceptionInfo {
		
		private int code;
		private String message;
		
		public ExceptionInfo(int code, String message) {
			this.code = code;
			this.message = message;
		}
		public int getCode() {
			return this.code;
		}
		public String getMessage() {
			return this.message;
		}
		
	}
	
	/**
	 * By the time exceptions get to the client across the REST interface, they are in 
	 * very sorry shape. They no longer reflect the type of the original exception, and 
	 * the message is a mess. See:
	 * 
	 * Service Error(409): FAILURE: Got HTTP status 409 for 
	 * http://localhost:8080/services-repository-develop-SNAPSHOT/auth/v1/user?originClient=bridge 
	 * Headers: User-Agent: Synpase-Java-Client/develop-SNAPSHOT Accept: application/json 
	 * Content-Type: application/json Request Content: org.apache.http.entity.StringEntity@5697454a 
	 * Response Content: {"reason":"User 'test@test.com' already exists\n"}
	 * 
	 * We can parse out the 
	 *  
	 * @param SynapseException exception
	 */
	public static ExceptionInfo parseSynapseException(SynapseException exception) {
		String message = exception.getMessage();
		if (message != null) {
			message = message.split("\"reason\":\"")[1];
			message = message.replace("\\n\"}", "");
		}
		String code = exception.getMessage();
		code = code.split(":")[0].replaceAll("\\D", "");
		return new ExceptionInfo(Integer.parseInt(code), message);
	}

	public static String parseSynapseException(SynapseException exception, int targetCode) throws SynapseException {
		ExceptionInfo info = parseSynapseException(exception);
		if (info.getCode() == targetCode) {
			return info.getMessage();
		}
		throw exception;
	}
	
	public static void formError(BindingResult result, String formName, String message) {
		result.addError(new ObjectError(formName, new String[] { message }, null, message));
	}
	
	public static void fieldError(BindingResult result, String formName, String fieldName, String message) {
		result.addError(new FieldError(formName, fieldName, null, false, new String[] { message }, null, message));
	}
	
	public static Throwable unwrapThrowable(Throwable throwable) {
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		return throwable;
	}
	
	public static String dumpACL(AccessControlList acl) {
		StringBuilder sb = new StringBuilder("ACL: ");
		if (acl != null) {
			Set<ResourceAccess> accesses = acl.getResourceAccess();
			if (accesses != null) {
				for(ResourceAccess ra: accesses) {
					for (ACCESS_TYPE type : ra.getAccessType()) {
						sb.append(type.toString()).append(", ");	
					}
				}
			}
		}
		return sb.toString();
	}
	
	public static boolean can(ACCESS_TYPE type, AccessControlList acl) {
		// This is almost certainly not correct. We have to check the ID as well, don't we?
		if (acl != null) {
			Set<ResourceAccess> accesses = acl.getResourceAccess();
			if (accesses != null) {
				for(ResourceAccess ra: accesses) {
					if (ra.getAccessType().contains(type)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static void dumpErrors(Logger logger, BindingResult result) {
		for (ObjectError error : result.getGlobalErrors()) {
			logger.info(String.format("GLOBAl ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
		for (ObjectError error : result.getFieldErrors()) {
			logger.info(String.format("FIELD ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
	}

}
