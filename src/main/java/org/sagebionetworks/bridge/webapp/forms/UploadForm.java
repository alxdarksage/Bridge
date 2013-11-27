package org.sagebionetworks.bridge.webapp.forms;

import org.springframework.web.multipart.MultipartFile;

public class UploadForm {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	public void getUpload(MultipartFile file) {
		this.file = file;
	}
	
	public void setUpload(MultipartFile file) {
		this.file = file;
	}
	
}
