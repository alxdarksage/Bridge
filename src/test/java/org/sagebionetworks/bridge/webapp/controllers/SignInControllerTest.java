package org.sagebionetworks.bridge.webapp.controllers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.google.inject.Binder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
    "file:src/main/webapp/WEB-INF/applicationContext.xml",
    "file:src/test/resources/test-bridge-webapp-application-context.spb.xml"
})
public class SignInControllerTest {
    
    @Autowired
    private SynapseClient synapseClient;
    
    @Autowired
    private SignInController controller;
    
    private UserSessionData userSessionData;
    
    SignInForm form;
    MockHttpSession session;
    BindingResult binding;    
    
    @Before
    public void setUp() {
        reset(synapseClient);
        
        userSessionData = new UserSessionData();
        userSessionData.setProfile(new UserProfile());
        
        form = createSignInForm();
        session = new MockHttpSession();
        session.setAttribute(BridgeUser.KEY, BridgeUser.PUBLIC_USER);
        binding = new BeanPropertyBindingResult(form, "signInForm");        
    }
    
    private SignInForm createSignInForm() {
        SignInForm form = new SignInForm();
        form.setUserName("tim.powers@sagebase.org");
        form.setPassword("password");
        form.setOrigin("communities/index");
        return form;
    }
    
    @Test
    public void testSuccessfulLogin() throws Exception {
        when(synapseClient.login("tim.powers@sagebase.org", "password")).thenReturn(userSessionData);
        
        String result = controller.post(form, binding, session);
        
        assertFalse("No errors", binding.hasGlobalErrors());
        assertEquals("Redirect to origin", "redirect:communities/index", result);
        assertTrue("User was stored in session", session.getAttribute(BridgeUser.KEY) != null);
        assertTrue("User is not public user", session.getAttribute(BridgeUser.KEY) != BridgeUser.PUBLIC_USER);
    }

    @Test
    public void testFailedLogin() throws Exception {
        when(synapseClient.login("tim.powers@sagebase.org", "password")).thenThrow(new SynapseException());
        
        String result = controller.post(form, binding, session);
        
        assertTrue("Has an error", binding.hasGlobalErrors());
        assertEquals("Redirect to origin with error", "redirect:communities/index?login=error", result);
        assertTrue("Public user still in session", session.getAttribute(BridgeUser.KEY) == BridgeUser.PUBLIC_USER);
    }
    

    // So, what doesn't happen is that it doesn't get validated... so then you get a crap redirect.
    // This is why it would be better if we were using Spring 3.4.x. I can'f find guidance on how to 
    // get this to work in Spring 3.0. Will have to use integration tests, which will require a mock
    // version of synapseClient, then perhaps I can run these tests against that client as well.
    /*
    @Test 
    public void testEmptyLogin() throws Exception {
        when(synapseClient.login(null, null)).thenThrow(new SynapseException());
        form = new SignInForm();
        
        String result = controller.post(form, binding, session);

        assertTrue("Has an error", binding.hasGlobalErrors());
        assertEquals("Redirect to origin with error", "redirect:communities/index?login=error", result);
        assertTrue("Public user still in session", session.getAttribute(BridgeUser.KEY) == BridgeUser.PUBLIC_USER);
    }
    */
}
