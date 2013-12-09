package org.sagebionetworks.bridge.webapp;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.ProfileForm;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.bridge.webapp.forms.WikiForm;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;

/**
 * Manual copying of data from object to object. Not always wise to 
 * use reflection utilities for security reasons (e.g. allows copying 
 * of IDs and other information).
 * 
 */
public class FormUtils {
	
	public static CommunityForm valuesToCommunityForm(CommunityForm communityForm, Community community) {
		communityForm.setName(community.getName());
		communityForm.setDescription(community.getDescription());
		return communityForm;
	}
	
	public static Community valuesToCommunity(Community community, CommunityForm communityForm) {
		community.setName(communityForm.getName());
		community.setDescription(communityForm.getDescription());
		return community;
	}
	
	public static NewUser valuesToNewUser(NewUser newUser, SignUpForm signUpForm) {
		newUser.setEmail(signUpForm.getEmail());
		newUser.setDisplayName(signUpForm.getDisplayName());
		return newUser;
	}
	
	public static WikiForm valuesToWikiForm(WikiForm wikiForm, V2WikiPage wiki, String markdown) {
		wikiForm.setTitle(wiki.getTitle());
		// This has become a good deal more complicated and is not finished.
		// wikiForm.setMarkdown(wiki.getMarkdown());
		wikiForm.setWikiId(wiki.getId());
		wikiForm.setMarkdown(markdown);
		return wikiForm;
	}
	
	public static ProfileForm valuesToProfileForm(ProfileForm form, UserProfile profile) {
		form.setDisplayName(profile.getDisplayName());
		form.setFirstName(profile.getFirstName());
		form.setLastName(profile.getLastName());
		form.setSummary(profile.getSummary());
		return form;
	}
	
	public static UserProfile valuesToUserProfile(UserProfile profile, ProfileForm form) {
		profile.setFirstName(form.getFirstName());
		profile.setLastName(form.getLastName());
		profile.setSummary(form.getSummary());
		return profile;
	}
	
	
}
