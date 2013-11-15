package org.sagebionetworks.bridge.webapp.forms;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

public class CommunityForm {

	@NotEmpty
	private String name;
	private String description;
	private String id;
	
	public String getFormName() {
		if (StringUtils.isBlank(id)) {
			return "NewCommunity";
		} else if (StringUtils.isBlank(name)) {
			return "Community";
		}
		return name;
	}
	public String getFormId() {
		if (StringUtils.isBlank(id)) {
			return "new";
		}
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
