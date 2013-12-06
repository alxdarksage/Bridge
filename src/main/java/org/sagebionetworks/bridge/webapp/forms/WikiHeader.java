package org.sagebionetworks.bridge.webapp.forms;

public class WikiHeader {

	private final String title;
	private final String id;
	private final String communityId;
	private final boolean isLocked;

	public WikiHeader(String title, String id, String communityId, boolean isLocked) {
		this.title = title;
		this.id = id;
		this.isLocked = isLocked;
		this.communityId = communityId;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}
	
	public String getCommunityId() {
		return communityId;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public String getViewURL() {
		return String.format("/communities/%s/wikis/%s.html", communityId, id);
	}
	
	public String getEditURL() {
		return String.format("/communities/%s/wikis/%s/edit.html", communityId, id);
	}
	
}
