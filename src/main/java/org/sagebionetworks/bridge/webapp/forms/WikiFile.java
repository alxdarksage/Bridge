package org.sagebionetworks.bridge.webapp.forms;

/**
 * The temporary preview link to a file as stored by Synapse (probably on S3), 
 * and an URL that points back to Bridge so the URL can be stored in a form 
 * that can be regenerated for each view (because the URL issued by Synapse is 
 * temporary). Finally, the URL to delete the image from the list of files 
 * held for the set of wiki pages (all the attachments are associated to the root 
 * wiki object).
 */
public class WikiFile {

	private final String previewURL;
	private final String permanentURL;
	private final String deleteURL;
	
	public WikiFile(final String previewURL, final String permanentURL, final String deleteURL) {
		this.previewURL = previewURL;
		this.permanentURL = permanentURL;
		this.deleteURL = deleteURL;
	}

	public String getPreviewURL() {
		return previewURL;
	}

	public String getPermanentURL() {
		return permanentURL;
	}

	public String getDeleteURL() {
		return deleteURL;
	}
	
}
