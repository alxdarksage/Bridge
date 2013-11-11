package org.sagebionetworks.bridge.webapp.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.forms.ProfileForm;
import org.sagebionetworks.bridge.webapp.forms.SignInForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.UserProfile;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	private static final Logger logger = LogManager.getLogger(ProfileController.class.getName());
	
	@ModelAttribute("signInForm")
	public SignInForm signInForm() {
		return new SignInForm();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView get(BridgeRequest request, ModelAndView model) throws Exception {
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		UserProfile profile = client.getUserProfile(request.getBridgeUser().getOwnerId());
		ProfileForm form = new ProfileForm();
		BeanUtils.copyProperties(profile, form);
		model.addObject("profileForm", form);
		model.setViewName("profile");
		return model;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(BridgeRequest request, ProfileForm profileForm, BindingResult result) throws Exception {
		//ServletContext context = request.getServletContext();
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		
		String userId = request.getBridgeUser().getOwnerId();
		
		// Update the non-file content
		UserProfile oldProfile = client.getUserProfile(userId);
		/* This has apparently changed and has no update to the new file stuff. Return to this after 
		 * more basic stuff is working.
		if (profileForm.getPhotoFile() != null && !profileForm.getPhotoFile().isEmpty()) {
			File workFile = null;
			try {
				File tempDir = (File)context.getAttribute("javax.servlet.context.tempdir");
				if (tempDir == null) {
					throw new Exception("Container did not provide a temporary working directory");
				}
				workFile = new File(tempDir, userId);
				FileUtils.copyInputStreamToFile(profileForm.getPhotoFile().getInputStream(), workFile);
				
				// These are deprecated and don't appear to work.
				// AttachmentData data = client.uploadAttachmentToSynapse(userId, AttachmentType.USER_PROFILE, workFile, "Profile Photo");
				//AttachmentData data = client.uploadUserProfileAttachmentToSynapse(userId, workFile, "Profile Photo");
				oldProfile.setPic(data);
			} finally {
				workFile.delete();
			}
		}
		*/
		BeanUtils.copyProperties(profileForm, oldProfile);
		client.updateMyProfile(oldProfile);
		
		request.setNotification("ProfileUpdated");
		return "redirect:"+request.getBridgeUser().getStartURL();
	}
	
}
