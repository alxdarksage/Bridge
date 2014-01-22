package org.sagebionetworks.bridge.webapp;

import java.util.Map;

import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.forms.CommunityForm;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.ProfileForm;
import org.sagebionetworks.bridge.webapp.forms.SignUpForm;
import org.sagebionetworks.bridge.webapp.forms.WikiForm;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;

import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

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
		newUser.setUserName(signUpForm.getUserName());
		return newUser;
	}
	
	public static WikiForm valuesToWikiForm(WikiForm wikiForm, V2WikiPage wiki, String markdown) {
		wikiForm.setTitle(wiki.getTitle());
		wikiForm.setWikiId(wiki.getId());
		wikiForm.setMarkdown(markdown);
		return wikiForm;
	}
	
	public static ProfileForm valuesToProfileForm(ProfileForm form, UserProfile profile) {
		form.setUserName(profile.getUserName());
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
	
	public static DynamicForm valuesToDynamicForm(DynamicForm dynamicForm, ParticipantDataRow row) {
		Map<String, String> values = Maps.transformEntries(row.getData(), new EntryTransformer<String, ParticipantDataValue, String>() {
			@Override
			public String transformEntry(String key, ParticipantDataValue value) {
				return ClientUtils.getValueAsString(value);
			}
		});
		dynamicForm.setValues(values);
		return dynamicForm;
	}
	
}
