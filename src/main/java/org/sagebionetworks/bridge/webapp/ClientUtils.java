package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataCurrentRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataStringValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.DynamicForm;
import org.sagebionetworks.bridge.webapp.forms.RowObject;
import org.sagebionetworks.bridge.webapp.forms.WikiHeader;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ibm.icu.text.SimpleDateFormat;

public class ClientUtils {
	
	private static final Logger logger = LogManager.getLogger(ClientUtils.class.getName());

	private static final String SYNAPSE_SESSION_COOKIE_NAME = "synapse";
	
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
		if (model != null) {
			model.addObject("descriptor", descriptor);
			model.addObject("form", specification);
		}
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

	public static void prepareParticipantData(BridgeClient client, ModelAndView model, Specification spec, String trackerId) throws SynapseException {
		List<RowObject> records = Lists.newArrayList();
		
		PaginatedResults<ParticipantDataRow> paginatedRowSet = client.getRawParticipantData(trackerId, ClientUtils.LIMIT, 0);
		SortedMap<String,FormElement> tabs = spec.getTableFields();
		
		// TODO: Inefficient.
		for (ParticipantDataRow row : paginatedRowSet.getResults()) {
			List<String> values = Lists.newArrayList();
			for (Map.Entry<String, FormElement> entry : tabs.entrySet()) {
				String fieldName = entry.getKey();
				FormElement field = entry.getValue();
				String serValue = getValueAsString(row.getData().get(fieldName));
				
				Converter<String,Object> converter = field.getObjectConverter();
				if (converter != null) {
					Object object = converter.convert(serValue);
					Converter<Object,String> converter2 = field.getStringConverter();
					if (converter2 != null) {
						serValue = converter2.convert(object);
					}
				}
				values.add(serValue);
			}
			records.add( new RowObject(row.getRowId(), new ArrayList<String>(tabs.keySet()), values) );
		}
		model.addObject("records", records);
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
	
	public static Set<String> defaultValuesFromPriorTracker(BridgeClient client, Specification spec,
			DynamicForm dynamicForm, String trackerId) throws SynapseException {
		Set<String> defaultedFields = Sets.newHashSet();
		// Right now these are sorted first to last entered, so we'd default from the last in the list.
		// I would like this to change to reverse the order, then this'll need to change as well.
		ParticipantDataCurrentRow currentRow = client.getCurrentParticipantData(trackerId);
		// This absolutely throws an NPE if there are no records at all.
		if (currentRow.getPreviousData() != null) {
			Set<String> defaults = defaultTheseFields(spec);
			for (Entry<String, ParticipantDataValue> entry : currentRow.getPreviousData().getData().entrySet()) {
				if (defaults.contains(entry.getKey())) {
					dynamicForm.getValuesMap().put(entry.getKey(), getValueAsString(entry.getValue()));
					defaultedFields.add(entry.getKey());
				}
			}
		}
		return defaultedFields;
	}
	
	public static String getValueAsString(ParticipantDataValue value) {
		if (value instanceof ParticipantDataDoubleValue) {
			// This intelligently trims the number, but if the user likes entering 2.0 or 0.2, then
			// it's going to look different coming back. We're not preserving the value as entered.
			// That'll require Lab or String.
			Double d = ((ParticipantDataDoubleValue)value).getValue();
			return new DecimalFormat("0.###").format(d);
		} else if (value instanceof ParticipantDataDatetimeValue) {
			return new SimpleDateFormat("yyyy-MM-dd").format(((ParticipantDataDatetimeValue) value).getValue());
		} else {
			return ValueTranslator.toString(value);
		}
	}

	public static ParticipantDataValue getStringAsValue(String str) {
		ParticipantDataStringValue value = new ParticipantDataStringValue();
		value.setValue(str);
		return value;
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
	
	public static void prepareDescriptorsByStatus(Model model, final BridgeClient client,
			PaginatedResults<ParticipantDataDescriptor> descriptors) throws ParseException, SynapseException {
		List<ParticipantDataDescriptor> descriptorsAlways = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsDue = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsIfNew = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsIfChanged = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsNoPrompt = Lists.newArrayListWithExpectedSize(20);
		
		Date now = new Date();
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.roll(Calendar.MONTH, false);
		Calendar tenMinutesAgo = Calendar.getInstance();
		tenMinutesAgo.add(Calendar.MINUTE, -10);
		List<ParticipantDataStatus> statusUpdateList = Lists.newArrayListWithExpectedSize(20);
		for (ParticipantDataDescriptor descriptor : descriptors.getResults()) {
			ParticipantDataStatus status = descriptor.getStatus();
			boolean needsUpdate = false;
			Date lastStarted = descriptor.getStatus().getLastStarted();
			Date lastPrompted = descriptor.getStatus().getLastPrompted();
			boolean lastEntryComplete = BooleanUtils.isTrue(descriptor.getStatus().getLastEntryComplete());

			// we want to prompt when:
			// - something new is due and we haven't prompted yet (lastPrompted < lastCron && lastStarted < lastCron)
			// - something new or conditional is due and we haven't prompted for a month (?? frequency ??)

			// did we ever prompt?
			boolean firstPrompt = lastPrompted == null;

			// is it a month ago, or less than 10 minutes ago (we want to make sure the user sees)
			boolean repeatPrompt = lastPrompted != null && lastPrompted.after(lastMonth.getTime());

			// is it a month ago, or less than 10 minutes ago (we want to make sure the user sees)
			boolean repeatPromptWasRecent = lastPrompted != null && lastPrompted.after(tenMinutesAgo.getTime());

			boolean shouldPrompt = firstPrompt || repeatPrompt || repeatPromptWasRecent;

			boolean repeatDue = false;
			if (!StringUtils.isEmpty(descriptor.getRepeatFrequency())) {
				if (lastStarted != null) {
					CronExpression cronExpression = new CronExpression(descriptor.getRepeatFrequency());
					if (cronExpression.getNextValidTimeAfter(lastStarted).before(now)) {
						repeatDue = true;
					}
				}
			}

			List<ParticipantDataDescriptor> promptList = null;
			switch (descriptor.getRepeatType()) {
			case ALWAYS:
				promptList = descriptorsAlways;
				if (!repeatPromptWasRecent) {
					if (!BooleanUtils.isTrue(status.getLastEntryComplete())) {
						status.setLastEntryComplete(true);
					}
					needsUpdate = true;
				}
				break;
			case IF_NEW:
				if (shouldPrompt || repeatDue) {
					promptList = descriptorsIfNew;
				}
				break;
			case IF_CHANGED:
				if (shouldPrompt || repeatDue) {
					promptList = descriptorsIfChanged;
				}
				break;
			case ONCE:
				if (!lastEntryComplete && shouldPrompt) {
					promptList = descriptorsDue;
				}
				break;
			case REPEATED:
				if (repeatDue) {
					status.setLastEntryComplete(true);
					needsUpdate = true;
				}
				if (repeatDue || (!lastEntryComplete && shouldPrompt)) {
					promptList = descriptorsDue;
				}
				break;
			}
			if (promptList != null) {
				promptList.add(descriptor);
				if (!repeatPromptWasRecent) {
					status.setLastPrompted(now);
					needsUpdate = true;
				}
			} else {
				descriptorsNoPrompt.add(descriptor);
			}

			if (needsUpdate) {
				if (!repeatPromptWasRecent) {
					status.setLastPrompted(now);
				}
				statusUpdateList.add(status);
			}
		}

		if (statusUpdateList.size() > 0) {
			ParticipantDataStatusList dataStatusList = new ParticipantDataStatusList();
			dataStatusList.setUpdates(statusUpdateList);
			client.sendParticipantDataDescriptorUpdates(dataStatusList);
		}

		List<ParticipantDataCurrentRow> descriptorsWithDataAlways = Lists.transform(descriptorsAlways,
				new Function<ParticipantDataDescriptor, ParticipantDataCurrentRow>() {
					@Override
					public ParticipantDataCurrentRow apply(ParticipantDataDescriptor descriptor) {
						try {
							return client.getCurrentParticipantData(descriptor.getId());
						} catch (SynapseException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					}
				});

		model.addAttribute("descriptorsAlways", descriptorsWithDataAlways);
		model.addAttribute("descriptorsDue", descriptorsDue);
		model.addAttribute("descriptorsIfNew", descriptorsIfNew);
		model.addAttribute("descriptorsIfChanged", descriptorsIfChanged);
		model.addAttribute("descriptorsNoPrompt", descriptorsNoPrompt);
	}

	public static String getSynapseSessionCookie(BridgeRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i=0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(SYNAPSE_SESSION_COOKIE_NAME)) {
					return cookies[i].getValue();
				}
			}
		}
		return null;
	}
	
	public static void setSynapseSessionCookie(BridgeRequest request, HttpServletResponse response, int expiry) {
		BridgeUser user = request.getBridgeUser();
		String value = "";
		if (expiry != 0 && user != null && user.isAuthenticated()) {
			value = user.getBridgeClient().getCurrentSessionToken();
		}
		Cookie cookie = new Cookie(SYNAPSE_SESSION_COOKIE_NAME, value);
		cookie.setPath("/");
		cookie.setMaxAge(expiry); // thirty minutes in seconds
		response.addCookie(cookie);
	}	

}
