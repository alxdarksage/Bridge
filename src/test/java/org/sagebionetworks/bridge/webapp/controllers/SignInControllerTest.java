package org.sagebionetworks.bridge.webapp.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/resources/bridge-webapp-application-context.spb.xml" })
public class SignInControllerTest {

	// Can't autowire because I'm using the same test application context XML file for 
	// the integration tests. I'm not sure how much unit testing I'm going to do in 
	// a project that's all webapp.
	private SynapseClient synapseClient;

	@Autowired
	private SignInController controller;

	private UserSessionData userSessionData;

	SignInForm form;
	BridgeRequest request;
	BindingResult binding;

	@Before
	public void setUp() {
		synapseClient = Mockito.mock(SynapseClientImpl.class);
		controller.setSynapseClient(synapseClient);
		
		userSessionData = new UserSessionData();
		userSessionData.setProfile(new UserProfile());

		form = createSignInForm();

		request = new BridgeRequest(new MockHttpServletRequest());
		request.setBridgeUser(BridgeUser.PUBLIC_USER);

		binding = new BeanPropertyBindingResult(form, "signInForm");
	}
	
	private SignInForm createSignInForm() {
		SignInForm form = new SignInForm();
		form.setEmail("tim.powers@sagebase.org");
		form.setPassword("password");
		return form;
	}

	@Test
	public void testSuccessfulLogin() throws Exception {
		when(synapseClient.login("tim.powers@sagebase.org", "password")).thenReturn(userSessionData);

		String result = controller.post(request, form, binding);
		
		assertFalse("No errors", binding.hasGlobalErrors());
		assertEquals("Redirect to origin", "redirect:/portal/index.html", result);
		assertTrue("User was stored in session", request.getBridgeUser() != null);
		assertTrue("User is not public user", request.getBridgeUser() != BridgeUser.PUBLIC_USER);
	}

	@Test
	public void testFailedLogin() throws Exception {
		when(synapseClient.login("tim.powers@sagebase.org", "password")).thenThrow(new SynapseException());

		String result = controller.post(request, form, binding);

		assertTrue("Has an error", binding.hasGlobalErrors());
		assertEquals("Redirect to origin with error", "redirect:/portal/index.html?login=error", result);
		assertTrue("Public user still in session", request.getBridgeUser() == BridgeUser.PUBLIC_USER);
	}
}
