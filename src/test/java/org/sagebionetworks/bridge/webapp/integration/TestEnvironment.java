package org.sagebionetworks.bridge.webapp.integration;

public class TestEnvironment {

	public static boolean isUI() {
		return "ui".equals(System.getProperty("org.sagebionetworks.bridge.profile"));
	}
	
}
