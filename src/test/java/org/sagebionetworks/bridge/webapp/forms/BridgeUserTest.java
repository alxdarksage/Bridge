package org.sagebionetworks.bridge.webapp.forms;

import static org.junit.Assert.*;

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
        user.setDisplayName("B");
        user.setOwnerId("C");
        
        assertEquals("Session token getter is correct", "A", user.getSessionToken());
        assertEquals("Display name getter is correct", "B", user.getDisplayName());
        assertEquals("Owner ID is getter correct", "C", user.getOwnerId());
    }

}
