package org.sagebionetworks.bridge.webapp.forms;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.sagebionetworks.bridge.webapp.validators.SynapseName;

public class CommunityForm {

	@NotEmpty
	@SynapseName
	private String name;
	private String description;
	private String id;
	private String administrators; // just here to hold an error...
	
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
	public String getAdministrators() {
		return administrators;
	}
	public void setAdministrators(String administrators) {
		this.administrators = administrators;
	}
}
