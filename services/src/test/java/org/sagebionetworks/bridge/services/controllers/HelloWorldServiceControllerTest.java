package org.sagebionetworks.bridge.services.controllers;

import static org.mockito.Mockito.when;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.bridge.common.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
	"file:src/main/resources/bridge-services-application-context.spb.xml",
	"file:src/test/resources/test-bridge-services-application-context.spb.xml"
})
public class HelloWorldServiceControllerTest {
	
	@Autowired
	private HelloWorldServiceController controller;
	
	@Test
	public void testWithoutSpringMvcTest() throws Exception {
    	HelloMessage result = controller.getHelloMessage();
    	
    	assertEquals("View is 'help'", result.getMessage(), "Hello all you Bridge people!");
	}
}
