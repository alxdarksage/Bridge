package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.forms.WikiHeader;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

public class ClientUtils {
	
	public static class ExceptionInfo {
		
		private int code;
		private String message;
		
		public ExceptionInfo(int code, String message) {
			this.code = code;
			this.message = message;
		}
		public int getCode() {
			return this.code;
		}
		public String getMessage() {
			return this.message;
		}
		
	}
	
	public static final long LIMIT = 1000L;
	
	/**
	 * By the time exceptions get to the client across the REST interface, they are in 
	 * very sorry shape. They no longer reflect the type of the original exception, and 
	 * the message is a mess. See:
	 * 
	 * Service Error(409): FAILURE: Got HTTP status 409 for 
	 * http://localhost:8080/services-repository-develop-SNAPSHOT/auth/v1/user?originClient=bridge 
	 * Headers: User-Agent: Synpase-Java-Client/develop-SNAPSHOT Accept: application/json 
	 * Content-Type: application/json Request Content: org.apache.http.entity.StringEntity@5697454a 
	 * Response Content: {"reason":"User 'test@test.com' already exists\n"}
	 * 
	 * Here's another example:
	 * 
	 * request content: {"concreteType":"org.sagebionetworks.bridge.model.Community","description":
	 * "","name":"asdf / asdf"} exception content: Service Error(500):  FAILURE: Got HTTP 
	 * status 500 for http://localhost:8080/services-repository-develop-SNAPSHOT/bridge/v1/community
	 * 
	 * We attempt to parse out the error code and message, however, not all messages follow this 
	 * pattern. 
	 *  
	 * @param SynapseException exception
	 */
	public static ExceptionInfo parseSynapseException(SynapseException exception) {
		String message = null;
		int code = 500;
		try {
			message = exception.getMessage();
			if (message != null) {
				message = message.split("\"reason\":\"")[1];
				message = message.split("\\\\n\"}")[0];
			}
		} catch(Throwable t) {
			message = "Could not parse service message";
		}
		try {
			String codeString = exception.getMessage();
			codeString = codeString.split(":")[0].replaceAll("\\D", "");
			code = Integer.parseInt(codeString);
		} catch(Throwable t) {
		}
		return new ExceptionInfo(code, message);
	}

	public static String parseSynapseException(SynapseException exception, int targetCode) throws SynapseException {
		ExceptionInfo info = parseSynapseException(exception);
		if (info.getCode() == targetCode) {
			return info.getMessage();
		}
		throw exception;
	}
	
	public static String parseSynapseException(SynapseException e, int targetCode, String messageFragment) throws SynapseException {
		ExceptionInfo info = parseSynapseException(e);
		if (info.getCode() == targetCode && info.getMessage().contains(messageFragment)) {
			return info.getMessage();
		}
		throw e;
	}
	
	public static void formError(BindingResult result, String formName, String message) {
		result.addError(new ObjectError(formName, new String[] { message }, null, message));
	}
	
	public static void fieldError(BindingResult result, String formName, String fieldName, String message) {
		result.addError(new FieldError(formName, fieldName, null, false, new String[] { message }, null, message));
	}
	
	// NOTE: This is in Guava.
	public static Throwable unwrapThrowable(Throwable throwable) {
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		return throwable;
	}
	
	public static String dumpACL(AccessControlList acl) {
		StringBuilder sb = new StringBuilder("ACL: ");
		if (acl != null) {
			Set<ResourceAccess> accesses = acl.getResourceAccess();
			if (accesses != null) {
				for(ResourceAccess ra: accesses) {
					for (ACCESS_TYPE type : ra.getAccessType()) {
						sb.append(type.toString()).append(", ");	
					}
				}
			}
		}
		return sb.toString();
	}
	
	public static UserEntityPermissions getPermits(BridgeRequest request, String id) throws SynapseException {
		return request.getBridgeUser().getSynapseClient().getUsersEntityPermissions(id);
	}
	
	public static V2WikiPage getWikiPage(BridgeRequest request, Community community, String wikiId)
			throws JSONObjectAdapterException, SynapseException {
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		V2WikiPage page = client.getV2WikiPage( new WikiPageKey(community.getId(), ObjectType.ENTITY, wikiId) );
		if (page.getAttachmentFileHandleIds() == null) {
			page.setAttachmentFileHandleIds(new ArrayList<String>());
		}
		return page;
	}
	
	public static V2WikiPage getWikiPage(BridgeRequest request, WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		V2WikiPage page = client.getV2WikiPage( key );
		if (page.getAttachmentFileHandleIds() == null) {
			page.setAttachmentFileHandleIds(new ArrayList<String>());
		}
		return page;
	}
	
	public static void prepareCommunitySidebarData(SynapseClient synapseClient,
			Community community, ModelAndView model) throws SynapseException, JSONObjectAdapterException,
			ClientProtocolException, FileNotFoundException, IOException {
		model.addObject("community", community);
		
		List<WikiHeader> headers = ClientUtils.getWikiHeaders(synapseClient, community);
		model.addObject("wikiHeaders", headers);
		
		WikiPageKey key = new WikiPageKey(community.getId(), ObjectType.ENTITY, community.getIndexPageWikiId());
		File markdownFile = synapseClient.downloadV2WikiMarkdown(key);
		String markdown = FileUtils.readFileToString(markdownFile);
		
		// TODO: This should be removable when we fix the manager code. The manager does not create a 
		// valid starting bit of markdown for this wiki.
		if (markdown.indexOf("<ul>") == -1) {
			model.addObject("indexContent", "");
		} else {
			markdown = markdown.replace("<ul>","").replace("</ul>","");
			markdown = markdown.replace("<li>","<li class='list-group-item'>");
			model.addObject("indexContent", markdown);
		}
	}

	private static List<WikiHeader> getWikiHeaders(SynapseClient client, Community community)
			throws JSONObjectAdapterException, SynapseException {
		// Get headers for all wiki pages
		V2WikiPage root = client.getV2RootWikiPage(community.getId(), ObjectType.ENTITY);
		PaginatedResults<V2WikiHeader> results = client.getV2WikiHeaderTree(community.getId(), ObjectType.ENTITY);
		List<WikiHeader> headers = new ArrayList<>();
		for (V2WikiHeader header : results.getResults()) {
			// Don't include the root or the index wiki pages in the list.
			if (!header.getId().equals(root.getId()) && !header.getId().equals(community.getIndexPageWikiId())) {
				WikiHeader h = new WikiHeader(header, community.getId(), header.getId().equals(community.getWelcomePageWikiId()));
				headers.add(h);
			}
		}
		return headers;
	}

	public static File createTempFile(BridgeRequest request, String fileName) throws ServletException {
		File directory = (File)request.getServletContext().getAttribute(ServletContext.TEMPDIR);
		if (directory == null) {
		    throw new ServletException("Servlet container does not provide temporary directory");
		}
		return new File(directory, fileName);
	}
	
	public static void dumpErrors(Logger logger, BindingResult result) {
		if (!result.hasGlobalErrors() && !result.hasFieldErrors()) {
			logger.debug("NO ERRORS");
		}
		for (ObjectError error : result.getGlobalErrors()) {
			logger.debug(String.format("GLOBAl ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
		for (ObjectError error : result.getFieldErrors()) {
			logger.debug(String.format("FIELD ERROR: %s: %s: %s", error.getObjectName(), error.getCode(), error.getDefaultMessage()));
		}
	}

}
