package org.sagebionetworks.bridge.webapp.controllers;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.ResetPasswordForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.web.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/resetPassword")
public class ResetPasswordController {
    
    @Resource(name="synapseClient")
    private SynapseClient synapseClient;
    
    @ModelAttribute("resetPasswordForm")
    public ResetPasswordForm resetPasswordForm() {
        return new ResetPasswordForm();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "resetPassword";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String post(@ModelAttribute @Valid ResetPasswordForm resetPasswordForm, BindingResult result, BridgeRequest request) {
        if (!result.hasErrors()) {
            try {
                synapseClient.sendPasswordResetEmail(resetPasswordForm.getEmail());
                request.setNotification("We&#8217;ve sent you an email. Follow the link in your email to reset your password.");
                return "redirect:"+ request.getOriginURL();
            } catch(SynapseNotFoundException e) {
                ClientUtils.globalFormError(result, "resetPasswordForm", "UserNotFoundException");
            } catch(SynapseException e) {
                ClientUtils.formError(result, "signUpForm", e.getMessage());
            }
        }
        return "resetPassword";
    }
}
