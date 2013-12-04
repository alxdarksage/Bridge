package org.sagebionetworks.bridge.webapp.forms;

import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;

public class WikiHeader {

	private final String title;
	private final String id;
	private final String communityId;
	private final boolean isLocked;

	public WikiHeader(V2WikiHeader h, String communityId, boolean isLocked) {
		this.title = h.getTitle();
		this.id = h.getId();
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
