package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataCurrentRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataEventValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueTranslator;
import org.sagebionetworks.bridge.model.timeseries.TimeSeriesRow;
import org.sagebionetworks.bridge.model.timeseries.TimeSeriesTable;
import org.sagebionetworks.bridge.webapp.forms.BridgeUser;
import org.sagebionetworks.bridge.webapp.forms.WikiHeader;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.FormLayout;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.bridge.webapp.specs.trackers.MedicationTracker;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
	
	public static void log(Logger loggr, Object... objects) {
		if (StackConfiguration.isDevelopStack()) {
			for (int i=0; i < objects.length; i++) {
				if (objects[i] instanceof String) {
					loggr.info(objects[i]);
				} else {
					loggr.info(i + ": -------------------------------------------------------");
					loggr.info(ToStringBuilder.reflectionToString(objects[i]));	
				}
			}
		}
	}
	
	public static UserEntityPermissions getPermits(BridgeRequest request, String id) throws SynapseException {
		return request.getBridgeUser().getSynapseClient().getUsersEntityPermissions(id);
	}
	
	public static ParticipantDataDescriptorWithColumns prepareDescriptor(BridgeClient client, String descriptorId,
			ModelAndView model) throws SynapseException {
		
		ParticipantDataDescriptorWithColumns dwc = client.getParticipantDataDescriptorWithColumns(descriptorId);
		if (dwc.getDescriptor().getStatus() == null) {
			// a dummy "all is finished" status
			dwc.getDescriptor().setStatus(ParticipantDataUtils.getFinishedStatus(null).getUpdates().get(0));
		}
		if (model != null) {
			model.addObject("descriptor", dwc.getDescriptor());
		}
		return dwc;
	}
	
	public static Specification prepareSpecification(SpecificationResolver resolver,
			ParticipantDataDescriptorWithColumns dwc, ModelAndView model) throws SynapseException {
		
		ParticipantDataDescriptor descriptor = dwc.getDescriptor();
		Specification specification = resolver.getSpecification(descriptor.getName());
		if (specification == null) {
			throw new SynapseNotFoundException("Could not find Specification for a Descriptor with the name "
					+ descriptor.getName());
		}
		// update specification with columns
		Map<String,ParticipantDataColumnDescriptor> mapping = Maps.newHashMap();
		for (ParticipantDataColumnDescriptor column : dwc.getColumns()) {
			mapping.put(column.getName(), column);
		}
		for (FormElement element : specification.getAllFormElements()) {
			if (mapping.containsKey(element.getName())) {
				element.setDataColumn(mapping.get(element.getName()));
			}
		}		
		if (model != null) {
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
	
	/**
	 *	if user has never completed a record:
	 *		- currentRow will have empty current/previous
	 *	elseif user has completed the last record:
	 *	    - current = that last record
	 *	    - previous = the immediately prior record
	 *	else
	 *	    - current is null
	 *	    - previous is the last, unfinished record
	 * @param client
	 * @param model
	 * @param spec
	 * @param descriptor
	 * @throws SynapseException
	 */
	public static List<ParticipantDataRow> prepareParticipantDataSummary(BridgeClient client, ModelAndView model, Specification spec,
			ParticipantDataDescriptor descriptor) throws SynapseException {
		
		boolean mustHaveStarterRecord = (spec.getFormLayout() == FormLayout.ALL_RECORDS_ONE_PAGE_INLINE);
		
		List<ParticipantDataRow> rows = client.getRawParticipantData(descriptor.getId(), ClientUtils.LIMIT, 0, false).getResults();
		ParticipantDataCurrentRow currentRow = client.getCurrentParticipantData(descriptor.getId(), false);
		
		if (currentRow.getCurrentData() == null || currentRow.getPreviousData() == null) { // it's or, not and...
			if (mustHaveStarterRecord) {
				logger.info("User has never completed a record, creating inprogress record based on the layout");
				currentRow = createAnInProgressRecord(client, descriptor);
				model.addObject("inprogress", currentRow.getCurrentData());
			}
		} else if (isInProgress(descriptor)) {
			logger.info("User has not completed the last record, removing and exposing as inprogress record");
			ParticipantDataRow inProgressRow = removeInProgressRow(currentRow.getCurrentData(), rows);
			model.addObject("inprogress", inProgressRow);
		} else {
			if (mustHaveStarterRecord) {
				logger.info("User completed the last record, creating a new one based on the layout");
				currentRow = createAnInProgressRecord(client, descriptor);
				model.addObject("inprogress", currentRow.getCurrentData());
			}
		}
		model.addObject("records", rows);
		return rows;
	}
	
	private static ParticipantDataCurrentRow createAnInProgressRecord(BridgeClient client, ParticipantDataDescriptor descriptor) throws SynapseException {
		ParticipantDataRow row = new ParticipantDataRow();
		row.setData(Maps.<String, ParticipantDataValue> newHashMap());
		client.appendParticipantData(descriptor.getId(), Lists.newArrayList(row));
		client.sendParticipantDataDescriptorUpdates(ParticipantDataUtils.getInProcessStatus(descriptor.getId()));

		return client.getCurrentParticipantData(descriptor.getId(), false);
	}
	
	private static ParticipantDataRow removeInProgressRow(ParticipantDataRow rowToRemove, List<ParticipantDataRow> allRows) {
		ParticipantDataRow found = null;
		for (Iterator<ParticipantDataRow> i = allRows.iterator(); i.hasNext();) {
			ParticipantDataRow row = i.next();
			if (row.getRowId().equals(rowToRemove.getRowId())) {
				found = row;
				i.remove();
			}
		}
		return found;
	}

	private static boolean isInProgress(ParticipantDataDescriptor descriptor) {
		if (descriptor.getStatus() == null) {
			return false;
		}
		return (Boolean.FALSE.equals(descriptor.getStatus().getLastEntryComplete()));
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
		List<ParticipantDataDescriptor> descriptorsTimelines = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsQuestions = Lists.newArrayListWithExpectedSize(20);
		List<ParticipantDataDescriptor> descriptorsQuestionsToAsk = Lists.newArrayListWithExpectedSize(20);
		ParticipantDataDescriptor medicationsIfChanged = null;
		List<ParticipantDataRow> medications = null;
		ParticipantDataDescriptor events = null;
		ParticipantDataDescriptor cbc = null;
		
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
			Date lastAnswered = descriptor.getStatus().getLastAnswered();
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

			// unless we explicitely set it as seen/answered
			boolean wasUpdatedRecently = lastAnswered != null && lastAnswered.after(lastMonth.getTime());

			boolean shouldPrompt = !wasUpdatedRecently && (firstPrompt || repeatPrompt || repeatPromptWasRecent);

			boolean repeatDue = false;
			if (!StringUtils.isEmpty(descriptor.getRepeatFrequency())) {
				if (lastStarted != null) {
					CronExpression cronExpression = new CronExpression(descriptor.getRepeatFrequency());
					if (cronExpression.getNextValidTimeAfter(lastStarted).before(now)) {
						repeatDue = true;
					}
				}
			}

			// Medications will show as long as yes or no is not clicked
			if ("medication".equals(descriptor.getType())) {
				if (shouldPrompt || repeatDue) {
					medicationsIfChanged = descriptor;
					medications = client.getCurrentRows(descriptor.getId(), false);
				} else {
					descriptorsNoPrompt.add(descriptor);
				}
				descriptorsTimelines.add(descriptor);
				continue;
			}

			if ("event".equals(descriptor.getType())) {
				events = descriptor;
			}

			if ("cbc".equals(descriptor.getType())) {
				cbc = descriptor;
			}

			if (descriptor.getType() != null && descriptor.getType().startsWith("question")) {
				descriptorsQuestions.add(descriptor);
				if (shouldPrompt || repeatDue) {
					descriptorsQuestionsToAsk.add(descriptor);
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

			if (descriptor.getDatetimeStartColumnName() != null) {
				descriptorsTimelines.add(descriptor);
			}
			if (descriptor.getEventColumnName() != null) {
				descriptorsTimelines.add(descriptor);
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
							return client.getCurrentParticipantData(descriptor.getId(), false);
						} catch (SynapseException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					}
				});
		List<ParticipantDataCurrentRow> questionsWithData = Lists.transform(descriptorsQuestionsToAsk,
				new Function<ParticipantDataDescriptor, ParticipantDataCurrentRow>() {
					@Override
					public ParticipantDataCurrentRow apply(ParticipantDataDescriptor descriptor) {
						try {
							return client.getCurrentParticipantData(descriptor.getId(), false);
						} catch (SynapseException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					}
				});
		List<ParticipantDataCurrentRow> descriptorsQuestionsWithData = Lists.transform(descriptorsQuestions,
				new Function<ParticipantDataDescriptor, ParticipantDataCurrentRow>() {
					@Override
					public ParticipantDataCurrentRow apply(ParticipantDataDescriptor descriptor) {
						try {
							return client.getCurrentParticipantData(descriptor.getId(), false);
						} catch (SynapseException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					}
				});

		long timelineStart = new Date().getTime();
		for (ParticipantDataDescriptor descriptor : descriptorsTimelines) {
			TimeSeriesTable timeSeries = client.getTimeSeries(descriptor.getId(), null, false);
			timelineStart = Math.min(timelineStart, timeSeries.getFirstDate());
		}

		model.addAttribute("descriptorsAlways", descriptorsWithDataAlways);
		model.addAttribute("descriptorsDue", descriptorsDue);
		model.addAttribute("descriptorsIfNew", descriptorsIfNew);
		model.addAttribute("descriptorsIfChanged", descriptorsIfChanged);
		model.addAttribute("descriptorsNoPrompt", descriptorsNoPrompt);
		model.addAttribute("descriptorsTimelines", descriptorsTimelines);
		model.addAttribute("timelineStart", timelineStart);
		model.addAttribute("medicationsIfChanged", medicationsIfChanged);
		model.addAttribute("medications", medications);
		model.addAttribute("events", events);
		model.addAttribute("cbc", cbc);
		model.addAttribute("descriptorsQuestionsToAsk", questionsWithData);
		model.addAttribute("descriptorsQuestionsWithData", descriptorsQuestionsWithData);
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
	
	public static void logSensitive(Logger logr, Map<String,ParticipantDataValue> values) {
		if (StackConfiguration.isDevelopStack()) {
			logr.info(" ---- value map ---- ");
			for (Map.Entry<String, ParticipantDataValue> entry : values.entrySet()) {
				ParticipantDataValue value = entry.getValue();
				logr.info( String.format("%s: %s", entry.getKey(), value.toString()) );
			}
		}
	}
	
	public static void exportParticipantData(HttpServletResponse response, ParticipantDataDescriptor descriptor,
			List<ParticipantDataColumnDescriptor> columns, PaginatedResults<ParticipantDataRow> paginatedRowSet)
			throws IOException {
		
		response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename="+descriptor.getName()+".csv");
        CSVWriter writer = new CSVWriter(response.getWriter());

        // This will now export every column whether in the UI or not
        boolean writeTitles = true;
        Map<String,String> values = Maps.newHashMap();
        SortedSet<String> columnNames = Sets.newTreeSet();
        
		for (ParticipantDataRow row : paginatedRowSet.getResults()) {
			for (ParticipantDataColumnDescriptor column : columns) {
				ParticipantDataValue input = row.getData().get(column.getName());
				ValueTranslator.transformToStrings(input, values, column, columnNames);
				if (writeTitles) {
					writer.writeNext(columnNames.toArray(new String[] {}));
					writeTitles = false;
				}
				List<String> rowValues = Lists.newArrayList();
				for (String colName : columnNames) {
					rowValues.add(values.get(colName));
				}
				writer.writeNext(rowValues.toArray(new String[] {}));
			}
		}
		writer.flush();
		writer.close();
		response.flushBuffer();		
	}

	public static ParticipantDataRow createRowFromForm(ParticipantDataDescriptorWithColumns dwc, Map<String, String> valuesMap) {
		Map<String, ParticipantDataValue> data = Maps.newHashMap();
		for (ParticipantDataColumnDescriptor column : dwc.getColumns()) {
			ParticipantDataValue value = ValueTranslator.transformToValue(valuesMap, column);
			if (value != null) {
				data.put(column.getName(), value);
			}
		}
		ParticipantDataRow row = new ParticipantDataRow();
		row.setData(data);
		return row;
	}

	public static void exportRows(List<ParticipantDataRow> rows, HttpServletResponse response, ParticipantDataDescriptorWithColumns dwc)
			throws IOException {
		// There's a Spring way to do this, but until we do another CSV export, it's really not worth it
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=" + dwc.getDescriptor().getName() + ".csv");

		CSVWriter writer = new CSVWriter(response.getWriter());
		List<String> headers = Lists.newArrayListWithCapacity(dwc.getColumns().size());
		for (ParticipantDataColumnDescriptor column : dwc.getColumns()) {
			headers.add(column.getName());
		}
		writer.writeNext(headers.toArray(new String[dwc.getColumns().size()]));

		for (ParticipantDataRow row : rows) {
			List<String> values = Lists.newArrayListWithCapacity(headers.size());
			for (ParticipantDataColumnDescriptor column : dwc.getColumns()) {
				String value = ValueTranslator.toString(row.getData().get(column.getName()));
				values.add(value);
			}
			writer.writeNext(values.toArray(new String[dwc.getColumns().size()]));
		}
		writer.flush();
		writer.close();
		response.flushBuffer();
	}

	public static class Column {
		private final String name;
		private final String type;

		public Column(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}
	}

	public static class Event {
		private final String name;
		private final Long start;
		private final Long end;

		public Event(String name, Long start, Long end) {
			this.name = name;
			this.start = start;
			this.end = end;
		}

		public String getName() {
			return name;
		}

		public Long getStart() {
			return start;
		}

		public Long getEnd() {
			return end;
		}
	}

	public static class Data {
		private final Column[] cols;
		private final Object[][] rows;
		private final ParticipantDataEventValue[] events;

		public Data(Column[] cols, Object[][] rows, ParticipantDataEventValue[] events) {
			this.cols = cols;
			this.rows = rows;
			this.events = events;
		}

		public Column[] getCols() {
			return cols;
		}

		public Object[][] getRows() {
			return rows;
		}

		public ParticipantDataEventValue[] getEvents() {
			return events;
		}
	}

	public static Data getTimeSeries(BridgeClient client, String series, List<String> columns) throws SynapseException {
		TimeSeriesTable timeSeriesList = client.getTimeSeries(series, columns, false);

		int columnCount = timeSeriesList.getColumns().size();
		int rowCount = timeSeriesList.getRows().size();
		int dateIndex = timeSeriesList.getDateIndex().intValue();
		if (dateIndex != 0) {
			// optimized for dateIndex being the first column
			swap(timeSeriesList.getColumns(), 0, dateIndex);
			for (TimeSeriesRow row : timeSeriesList.getRows()) {
				swap(row.getValues(), 0, dateIndex);
			}
		}

		// turn the timeSeries into a row column, where column 0 is date and other columns values
		Column[] cols = new Column[columnCount];
		cols[0] = new Column(timeSeriesList.getColumns().get(dateIndex).getName(), "datetime");
		for (int colIndex = 1; colIndex < columnCount; colIndex++) {
			cols[colIndex] = new Column(timeSeriesList.getColumns().get(colIndex).getName(), "number");
		}

		Object[][] rows = new Object[rowCount][];
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			TimeSeriesRow row = timeSeriesList.getRows().get(rowIndex);
			rows[rowIndex] = new Object[columnCount];
			for (int colIndex = 0; colIndex < columnCount; colIndex++) {
				rows[rowIndex][colIndex] = row.getValues().get(colIndex);
			}
		}

		ParticipantDataEventValue[] events = timeSeriesList.getEvents().toArray(
				new ParticipantDataEventValue[timeSeriesList.getEvents().size()]);

		Data result = new Data(cols, rows, events);
		return result;
	}

	private static <T> void swap(List<T> list, int index1, int index2) {
		T value1 = list.get(index1);
		list.set(index1, list.get(index2));
		list.set(0, value1);
	}
}
