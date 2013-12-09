package org.sagebionetworks.bridge.webapp.forms;

import org.springframework.web.multipart.MultipartFile;

public class ProfileForm {

	private String displayName;
	private String firstName;
	private String lastName;
	private String summary;
	private String memberships; // just here to hold an error...
	
	private MultipartFile photoFile;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public MultipartFile getPhotoFile() {
		return photoFile;
	}
	public void setPhotoFile(MultipartFile photoFile) {
		this.photoFile = photoFile;
	}
	public String getMemberships() {
		return memberships;
	}
	public void setMemberships(String memberships) {
		this.memberships = memberships;
	}
}
