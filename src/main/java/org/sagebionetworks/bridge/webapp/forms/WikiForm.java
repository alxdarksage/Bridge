package org.sagebionetworks.bridge.webapp.forms;

import org.hibernate.validator.constraints.NotEmpty;

public class WikiForm {

	@NotEmpty
	private String title;
	@NotEmpty
	private String markdown;
	
	private String wikiId;
	
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
	
}
