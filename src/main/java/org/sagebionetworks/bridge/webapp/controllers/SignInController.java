package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/signIn")
public class SignInController {
	
	private static final Logger logger = LogManager.getLogger(SignInController.class.getName());

	@Autowired
	private BeanFactory beanFactory;
	
	@Resource(name="synapseClient")
	private SynapseClient synapseClient;
	
	public void setSynapseClient(SynapseClient synapseClient) {
	    this.synapseClient = synapseClient;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(@ModelAttribute SignInForm signInForm) {
		return "signIn";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute @Valid SignInForm signInForm, BindingResult result, HttpSession session) throws SynapseException {
	    if (userIsAuthenticated(session)) {
	        return "redirect:"+signInForm.getOrigin();
	    }
		if (!result.hasErrors()) {
	        try {
	            logger.info("About to sign in user.");
	            UserSessionData userSessionData = synapseClient.login(signInForm.getUserName(), signInForm.getPassword());
	            BridgeUser user = createBridgeUserFromUserSessionData(userSessionData);
	            session.setAttribute(BridgeUser.KEY, user);
	            logger.info("User #{} signed in.", user.getOwnerId());
	            
	            // TODO: If the user signed in from the signIn page, they cannot go to their origin
	            // page. In this case we need their community as a default, but until then:
	            
	            // It's the same case with the signOut page, we need to redirect.
	            if ("/signIn.html".equals(signInForm.getOrigin())) {
	                return "redirect:/communities/index.html";
	            }
	            return "redirect:"+signInForm.getOrigin();
	        } catch(Throwable e) {
                logger.error("User could not be signed in: {}", e.getMessage());
                result.addError(new ObjectError("signInForm", e.getMessage()));
	        }
		}
		// Flash attributes are not available in Spring 3.0.x. We'll have to wing it.
		return "redirect:"+signInForm.getOrigin() + "?login=error";
	}
	
	private BridgeUser createBridgeUserFromUserSessionData(UserSessionData data) {
	    BridgeUser user = (BridgeUser)beanFactory.getBean("bridgeUser");
	    user.setDisplayName(data.getProfile().getDisplayName());
	    user.setOwnerId(data.getProfile().getOwnerId());
	    user.setSessionToken(data.getSessionToken());
	    return user;
	}
	
	private boolean userIsAuthenticated(HttpSession session) {
	    BridgeUser user = (BridgeUser)session.getAttribute(BridgeUser.KEY);
	    if (user != null && user.isAuthenticated()) {
	        return true;
	    }
	    return false;
	}

}
