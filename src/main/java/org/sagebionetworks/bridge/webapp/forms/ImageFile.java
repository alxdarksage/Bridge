package org.sagebionetworks.bridge.webapp.forms;

/**
 * The temporary link to a file as stored by Synapse (probably on S3), and an 
 * URL that points back to Bridge so the URL can be stored in a form that can 
 * be regenerated for each view (because the URL issued by Synapse is 
 * temporary).
 *
 */
public class ImageFile {

	private final String temporaryURL;
	private final String permanentURL;
	
	public ImageFile(final String temporaryURL, final String permanentURL) {
		this.temporaryURL = temporaryURL;
		this.permanentURL = permanentURL;
	}

	public String getTemporaryURL() {
		return temporaryURL;
	}

	public String getPermanentURL() {
		return permanentURL;
	}
	
}
