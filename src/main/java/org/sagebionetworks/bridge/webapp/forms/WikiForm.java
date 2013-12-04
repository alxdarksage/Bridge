package org.sagebionetworks.bridge.webapp.forms;

import org.hibernate.validator.constraints.NotEmpty;

public class WikiForm {

	@NotEmpty
	private String title;
	@NotEmpty
	private String markdown;
	
	private String wikiId;
	private String communityId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMarkdown() {
		return markdown;
	}
	public void setMarkdown(String markdown) {
		this.markdown = markdown;
	}
	public String getWikiId() {
		return wikiId;
	}
	public void setWikiId(String wikiId) {
		this.wikiId = wikiId;
	}
	public String getCommunityId() {
		return communityId;
	}
	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}
	
	public String getEditURL() {
		if (wikiId != null) {
			return String.format("/communities/%s/wikis/%s/edit.html", communityId, wikiId);	
		} else {
			return String.format("/communities/%s/wikis/new.html", communityId, wikiId);
		}
	}
	
}
