package org.sagebionetworks.bridge.webapp.controllers;

import static org.mockito.Mockito.when;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.bridge.common.HelloMessage;
import org.sagebionetworks.bridge.portal.controllers.HelloWorldController;
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
	"file:src/main/resources/portal-dispatcher-servlet.xml",
	"file:src/test/resources/test-portal-dispatcher-servlet.xml"
})
public class HelloWorldControllerTest {
	
	@Autowired
	private String serviceUrl;
	
	@Autowired
	private RestTemplate helloMessageRestCall;
	
	@Autowired
	private HelloWorldController controller;
	
	private HelloMessage message;
	
	@Before
	public void setUp() {
    	message = new HelloMessage();
    	message.setMessage("HelloMessage message test");
	}
	
	@Test
	public void testWithoutSpringMvcTest() throws Exception {
    	when(helloMessageRestCall.getForObject(serviceUrl, HelloMessage.class)).thenReturn(message);
    	
    	ModelMap model = new ModelMap();
    	String result = controller.helloWorld(model);
    	
    	assertEquals("View is 'help'", result, "hello");
    	assertTrue("Has a message key", model.containsKey("message"));
    	assertEquals("Message is correct", model.get("message"), "HelloMessage message test");
	}
	
// Can't use this because Synapse is on Spring 3.0.5.RELEASE. Eventually this means we will have trouble
// unit testing controllers for security and other things that are accomplished with filters... But this 
// will do for now.
//	@Autowired
//    private WebApplicationContext wac;
//	
//	@Autowired
//	private String serviceUrl;
//	
//	@Autowired
//	private RestTemplate helloMessageRestCall;
//
//    private MockMvc mockMvc;
//
//    @Before
//    public void setup() {
//    	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//    }
//    
//	@Test
//	public void testHelloWorldController() throws Exception {
//		// Mock out the service response
//    	HelloMessage message = new HelloMessage();
//    	message.setMessage("HelloMessage message test");
//    	when(helloMessageRestCall.getForObject(serviceUrl, HelloMessage.class)).thenReturn(message);
//    	
//    	// Test /hello
//		this.mockMvc.perform(get("/hello"))
//			.andExpect(status().isOk())
//			.andExpect(view().name("hello"))
//			.andExpect(model().attributeExists("message"))
//			.andExpect(model().attribute("message", "HelloMessage message test"));
//	}

}
