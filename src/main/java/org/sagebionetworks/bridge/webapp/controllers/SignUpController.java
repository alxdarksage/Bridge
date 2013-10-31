package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
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
    
    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String getSignUp() {
        return "signUp";
    }
    
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public String postSignUp(@ModelAttribute @Valid SignUpForm signUpForm, BindingResult result,
            HttpServletRequest request) throws Exception {
        // Here's an excellent chance to work on your error strategy.
        if (!result.hasErrors()) {
            try {
                synapseClient.createUser(signUpForm.getNewUser());
                request.setAttribute("notice",
                        "We&#8217;ve sent you an email with instructions on completing your registration.");
            } catch (Exception e) {
                result.addError(new ObjectError("signUpForm", e.getMessage()));
                return "signUp";
            }

        }
        return "signUp";
    }
    
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public String getResetPassword() {
        return "resetPassword";
    }
    
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String postResetPassword(@ModelAttribute @Valid SignUpForm signUpForm, BindingResult result, 
            HttpServletRequest request) throws Exception {
        try {
            synapseClient.sendPasswordResetEmail(signUpForm.getEmail());
            request.setAttribute("notice", "We&#8217;ve sent you an email. Follow the link in your email to reset your password.");
        } catch(Exception e) {
            result.addError(new ObjectError("signUpForm", e.getMessage()));
            return "resetPassword";
        }
        return "resetPassword";
    }

}
