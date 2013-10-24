package org.sagebionetworks.bridge.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class HelloMessageTest {

	@Test
	public void test() {
		HelloMessage message = new HelloMessage();
		
		assertEquals("Message is correct", "Hello all you Bridge people!", message.getMessage());
	}

}
