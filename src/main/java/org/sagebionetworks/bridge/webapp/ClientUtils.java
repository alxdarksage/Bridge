package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.RowObject;
import org.sagebionetworks.bridge.webapp.forms.WikiHeader;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.CompleteBloodCount;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormField;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ClientUtils {
	
	private static final Logger logger = LogManager.getLogger(ClientUtils.class.getName());

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
	
	public static final long LIMIT = 10000L;
	
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
	
	
	public static Specification prepareSpecificationAndDescriptor(BridgeClient client, SpecificationResolver resolver,
			ModelAndView model, String descriptorId) throws SynapseException {
		
		ParticipantDataDescriptor descriptor = null;
		PaginatedResults<ParticipantDataDescriptor> records = client.getAllParticipantDatas(ClientUtils.LIMIT, 0);
		for (ParticipantDataDescriptor d : records.getResults()) {
			if (d.getId().equals(descriptorId)) {
				descriptor = d;
				break;
			}
		}
		if (descriptor == null) {
			throw new SynapseNotFoundException("Could not find ParticipantDataDescriptor with ID of " + descriptorId);
		}
		Specification specification = resolver.getSpecification(descriptor.getName());
		if (specification == null) {
			throw new SynapseNotFoundException("Could not find Specification for a Descriptor with the name " + descriptor.getName());
		}
		model.addObject("descriptor", descriptor);
		model.addObject("form", specification);
		return specification;
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
		String markdown = synapseClient.downloadV2WikiMarkdown(key);
		
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

	public static void prepareParticipantData(BridgeClient client, ModelAndView model, Specification spec, String formId) throws SynapseException {
		// Block error (which isn't an error here, and also contains a whole Tomcat page in the message field).
		try {
			List<RowObject> records = Lists.newArrayList();
			
			PaginatedRowSet paginatedRowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0);
			RowSet rowSet = paginatedRowSet.getResults();
			List<String> headers = rowSet.getHeaders();
			SortedMap<String,FormElement> tabs = spec.getTableFields();
			
			// TODO: Inefficient. 
			for (Row row : rowSet.getRows()) {
				List<Object> values = Lists.newArrayList();
				for (Map.Entry<String, FormElement> entry : tabs.entrySet()) {
					String fieldName = entry.getKey();
					FormElement field = entry.getValue();
					String serValue = row.getValues().get( headers.indexOf(fieldName) );
					
					values.add( ParticipantDataUtils.convertToObject(field.getType(), serValue) );
				}
				records.add( new RowObject(row.getRowId(), new ArrayList<String>(tabs.keySet()), values) );
			}
			model.addObject("records", records);
		} catch(SynapseException e) {
			logger.error(e);
			model.addObject("records", Lists.newArrayList());
		}
	}
	
	public static Row getRowById(RowSet rowSet, long rowId) {
		for (Row row : rowSet.getRows()) {
			if (row.getRowId().equals(rowId)) {
				return row;
			}
		}
		throw new IllegalArgumentException(Long.toString(rowId) + " is not a valid row");
	}	
	
	public static String getValueInRow(Row row, List<String> headers, String header) {
		for (int i=0; i < headers.size(); i++) {
			if (header.equals(headers.get(i))) {
				return row.getValues().get(i);
			}
		}
		throw new IllegalArgumentException(header + " is not a valid header");
	}	

	private static List<WikiHeader> getWikiHeaders(SynapseClient client, Community community)
			throws JSONObjectAdapterException, SynapseException {
		// Get headers for all wiki pages
		V2WikiPage root = client.getV2RootWikiPage(community.getId(), ObjectType.ENTITY);
		PaginatedResults<V2WikiHeader> results = client.getV2WikiHeaderTree(community.getId(), ObjectType.ENTITY);
		List<WikiHeader> headers = Lists.newArrayList();
		for (V2WikiHeader header : results.getResults()) {
			// Don't include the root or the index wiki pages in the list.
			if (!header.getId().equals(root.getId()) && !header.getId().equals(community.getIndexPageWikiId())) {
				String title = HtmlUtils.htmlEscape(header.getTitle());
				String id = header.getId();
				WikiHeader h = new WikiHeader(title, id, community.getId(), header.getId().equals(community.getWelcomePageWikiId()));
				headers.add(h);
			}
		}
		return headers;
	}
	
	public static boolean defaultValuesFromPriorForm(BridgeClient client, Specification spec, DynamicForm dynamicForm,
			String formId) {
		boolean anyDefaulted = false;
		try {
			// Right now these are sorted first to last entered, so we'd default from the last in the list.
			// I would like this to change to reverse the order, then this'll need to change as well.
			PaginatedRowSet rowSet = client.getParticipantData(formId, ClientUtils.LIMIT, 0L);
			if (rowSet.getTotalNumberOfResults() > 0L) {
				long len = rowSet.getTotalNumberOfResults();
				List<String> headers = rowSet.getResults().getHeaders();
				Row finalRow = rowSet.getResults().getRows().get((int)len-1);
				List<String> values = finalRow.getValues();
				Set<String> defaults = defaultTheseFields(spec);

				for (int i=0; i < headers.size(); i++) {
					String header = headers.get(i);
					if (defaults.contains(header)) {
						dynamicForm.getValues().put(header, values.get(i));
						anyDefaulted = true;
					}
				}
			}
		} catch(SynapseException se) {
			// An exception shouldn't be thrown when there's no records.
		}
		return anyDefaulted;
	}
	
	private static Set<String> defaultTheseFields(Specification spec) {
		Set<String> matches = Sets.newHashSet();
		for (FormElement element : spec.getAllFormElements()) {
			if (element.isDefaultable()) {
				matches.add(element.getName());
			}
		}
		return matches;
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
