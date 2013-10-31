package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/signUp")
public class SignUpController {
    
    private static final Logger logger = LogManager.getLogger(SignUpController.class.getName());

    @Resource(name="synapseClient")
    private SynapseClient synapseClient;
    
    public void setSynapseClient(SynapseClient synapseClient) {
        this.synapseClient = synapseClient;
    }

    @ModelAttribute("signUpForm")
    public SignUpForm signInForm() {
        return new SignUpForm();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "signUp";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String post(@ModelAttribute @Valid SignUpForm signUpForm, BindingResult result) throws Exception {
        // Here's an excellent chance to work on your error strategy.
        if (!result.hasErrors()) {
            throw new RuntimeException("Dang, we couldn't add you.");
            //synapseClient.createUser(signUpForm.getNewUser());
        }
        return "signUp";
    }
    

}
