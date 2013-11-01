package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseUnauthorizedException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/signUp")
public class SignUpController {
    
    private static final Logger logger = LogManager.getLogger(SignUpController.class.getName());

    @Resource(name="synapseClient")
    private SynapseClient synapseClient;
    
    @ModelAttribute("signUpForm")
    public SignUpForm signInForm() {
        return new SignUpForm();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "signUp";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String post(@ModelAttribute @Valid SignUpForm signUpForm, BindingResult result, BridgeRequest request) throws Exception {
        if (!result.hasErrors()) {
            try {
                synapseClient.createUser(signUpForm.getNewUser());
                request.setNotification("We&#8217;ve sent you an email with instructions on completing your registration.");
            } catch (UnauthorizedException | SynapseUnauthorizedException e) {
                ClientUtils.globalFormError(result, "signUpForm", "UnauthorizedException");
            }
        }
        return "signUp";
    }

}
