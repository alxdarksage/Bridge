package org.sagebionetworks.bridge.webapp.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class BridgeUserTest {

	@Test
	public void testPublicUserIsNotAuthenticated() {
		BridgeUser user = BridgeUser.PUBLIC_USER;
		assertFalse(user.isAuthenticated());
	}

	@Test
	public void testIdentifiersAreCorrect() {
		BridgeUser user = new BridgeUser();
		user.setSessionToken("A");
		user.setUserName("B");
		user.setOwnerId("C");

		assertEquals("Session token getter is correct", "A", user.getSessionToken());
		assertEquals("Display name getter is correct", "B", user.getUserName());
		assertEquals("Owner ID is getter correct", "C", user.getOwnerId());
	}

}
