package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.transform.impl.UndeclaredThrowableStrategy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataCurrentRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptorWithColumns;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataEventValue;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.timeseries.TimeSeriesColumn;
import org.sagebionetworks.bridge.model.timeseries.TimeSeriesRow;
import org.sagebionetworks.bridge.model.timeseries.TimeSeriesTable;
import org.sagebionetworks.bridge.model.versionInfo.BridgeVersionInfo;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.client.exceptions.SynapseClientException;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.AuthorizationConstants.BOOTSTRAP_TEAM;
import org.sagebionetworks.repo.model.DomainType;
import org.sagebionetworks.repo.model.IdList;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.PaginatedResultsUtil;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.NewIntegrationTestUser;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleResults;
import org.sagebionetworks.repo.model.file.S3FileHandle;
import org.sagebionetworks.repo.model.principal.AliasCheckRequest;
import org.sagebionetworks.repo.model.principal.AliasCheckResponse;
import org.sagebionetworks.repo.model.principal.AliasType;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.repo.model.versionInfo.SynapseVersionInfo;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public abstract class SageServicesStub implements SynapseClient, BridgeClient, SynapseAdminClient {
	
	private static final Logger logger = LogManager.getLogger(SageServicesStub.class.getName());

	UserSessionData currentUserData;
	String sessionToken; // when this isn't separate from UserSessionData.getProfile().getSession() there are errors.
	Set<String> agreedTOUs = Sets.newHashSet();
	Map<String,Community> communitiesById = Maps.newHashMap();
	Map<String,UserSessionData> usersById = Maps.newHashMap();
	Map<String,AccessControlList> aclsByEntityId = Maps.newHashMap(); // treating community as an entity
	Map<String,V2WikiPage> wikiPagesById = Maps.newHashMap();
	Map<String,Team> teamsById = Maps.newHashMap();
	Multimap<Team,String> teamMemberships = LinkedListMultimap.create();
	
	Map<String,String> markdownsByFileHandleId = Maps.newHashMap();
	Map<String,String> emailByUserId = Maps.newHashMap();
	Map<String,ParticipantDataStatus> statuses = Maps.newHashMap();

	// Participant data
	Map<String,ParticipantDataDescriptor> descriptorsById = Maps.newHashMap();
	Multimap<String,ParticipantDataColumnDescriptor> columnsByDescriptorById = LinkedListMultimap.create();
	Map<String, List<ParticipantDataRow>> participantDataByDescriptorId = Maps.newHashMap();
	int idCount = 2;

	public SageServicesStub() {
		logger.info("---------------------------- SageServicesStub CREATED");
		Team team = new Team();
		team.setId(BOOTSTRAP_TEAM.BRIDGE_ADMINISTRATORS.getId());
		teamsById.put(BOOTSTRAP_TEAM.BRIDGE_ADMINISTRATORS.getId(), team);
	}

	private static SageServicesStub singleStub = null;
	/**
	 * bean factory method
	 * 
	 * @return
	 */
	public static SageServicesStub createInstance() {
		if (singleStub == null) {
			// Configure CGLIB Enhancer...
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(SageServicesStub.class);
			enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));
			enhancer.setInterfaces(new Class[] { SynapseClient.class, BridgeClient.class, SynapseAdminClient.class });
			enhancer.setInterceptDuringConstruction(false);
			enhancer.setCallback(new MethodInterceptor() {
				@Override
				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
					logger.debug(method.getName());
					return proxy.invokeSuper(obj, args);
				}
			});

			// Generate the proxy class and create a proxy instance.
			Object proxy = enhancer.create();
			singleStub = (SageServicesStub) proxy;
		}
		return singleStub;
	}

	@Override
	public SynapseVersionInfo getVersionInfo() {
		SynapseVersionInfo info = new SynapseVersionInfo();
		info.setVersion("UI stub");
		return info;
	}
	
	@Override
	public long createUser(NewIntegrationTestUser user) throws SynapseException, JSONObjectAdapterException {
		String id = newId();
		UserProfile profile = new UserProfile();
		profile.setUserName(user.getUsername());
		profile.setOwnerId(id);
		Session session = new Session();
		session.setSessionToken("session"+id);
		UserSessionData data = new UserSessionData();
		data.setIsSSO(false);
		data.setProfile(profile);
		data.setSession(session);
		usersById.put(user.getUsername(), data);
		usersById.put(id, data);
		emailByUserId.put(id, user.getEmail());
		return Long.parseLong(id);
	}

	private Community createCommunity(String id, String name, String description, UserSessionData user) {
		String email = emailByUserId.get(user.getProfile().getOwnerId());
		
		Community community = new Community();
		community.setId(id);
		community.setName(name);
		community.setDescription(description);
		community.setCreatedBy(email);
		community.setCreatedOn(new Date());
		community.setModifiedBy(email);
		community.setModifiedOn(new Date());
		
		// The hidden root page, under a key directly related to the community.
		V2WikiPage root = createWikiPage(user, "Root", null, "Root");
		wikiPagesById.put(community.getId(), root);
		
		V2WikiPage page = createWikiPage(user, "Welcome to " + name, root.getId(), "Welcome");
		community.setWelcomePageWikiId(page.getId());
		
		page = createWikiPage(user, root.getId(), "Index Page", "Index");
		community.setIndexPageWikiId(page.getId());
		
		communitiesById.put(community.getId(), community);
		
		Team team = new Team();
		team.setId("synTeam" + community.getId());
		community.setTeamId(team.getId());
		teamsById.put(team.getId(), team);
		joinUserToThisCommunityTeam(team, user.getProfile().getOwnerId());
		logger.info("The team ID for " + community.getName() + " is " + team.getId());
		
		// Set user as an editor for this community
		addToAccessControlList(community.getId(), user.getProfile().getOwnerId(), ACCESS_TYPE.UPDATE, ACCESS_TYPE.CHANGE_PERMISSIONS);
		return community;
	}

	private V2WikiPage createWikiPage(UserSessionData user, String title, String parentId, String markdown) {
		String email = emailByUserId.get(user.getProfile().getOwnerId());
		
		V2WikiPage page = new V2WikiPage();
		page.setTitle(title);
		page.setCreatedBy(email);
		page.setCreatedOn(new Date());
		page.setId(newId());
		if (parentId != null) {
			page.setParentWikiId(parentId);
		}
		page.setMarkdownFileHandleId(newId());
		markdownsByFileHandleId.put(page.getMarkdownFileHandleId(), markdown);
		wikiPagesById.put(page.getId(), page);
		return page;
	}
	
	private void addToAccessControlList(String entityId, String userOwnerId, ACCESS_TYPE... types) {
		AccessControlList acl = aclsByEntityId.get(entityId);
		if (acl == null) {
			acl = new AccessControlList();
			acl.setId(entityId);
			aclsByEntityId.put(entityId, acl);
		}
		ResourceAccess selected = null;
		if (acl.getResourceAccess() == null) {
			acl.setResourceAccess(new HashSet<ResourceAccess>());
		}
		for (ResourceAccess ra : acl.getResourceAccess()) {
			if (ra.getPrincipalId() != null && userOwnerId.equals(ra.getPrincipalId().toString())) {
				selected = ra;
				break;
			}
		}
		if (selected == null) {
			selected = new ResourceAccess();
			selected.setAccessType(new HashSet<ACCESS_TYPE>());
			selected.setPrincipalId(Long.parseLong(userOwnerId));
			acl.getResourceAccess().add(selected);
		}
		selected.getAccessType().addAll(Sets.newHashSet(types));
		logger.info("Setting an ACL for: " + acl.getId());
	}

	private String newId() {
		return Integer.toString(++idCount);
	}
	
	private <T extends JSONEntity> PaginatedResults<T> toResults(List<T> list) {
		PaginatedResults<T> results = new PaginatedResults<T>();
		results.setResults(list);
		results.setTotalNumberOfResults(list.size());
		return results;
	}

	@Override
	public void appendUserAgent(String toAppend) {
		// logger.debug("--> appendUserAgent: " + toAppend);
	}

	@Override
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	@Override
	public String getCurrentSessionToken() {
		return this.sessionToken;
	}
	
	@Override
	public Community createCommunity(Community community) throws SynapseException {
		return createCommunity(newId(), community.getName(), community.getDescription(), currentUserData);
	}

	@Override
	public Community getCommunity(String communityId) throws SynapseException {
		Community community = communitiesById.get(communityId);
		if (community == null) {
			throw new SynapseClientException(new NotFoundException());
		}
		return community;
	}

	@Override
	public Community updateCommunity(Community community) throws SynapseException {
		Community cty = communitiesById.get(community.getId());
		if (cty == null) {
			throw new SynapseNotFoundException();
		}
		communitiesById.put(community.getId(), community);
		return community;
	}

	@Override
	public void deleteCommunity(String communityId) throws SynapseException {
		if (communityId == null || !communitiesById.containsKey(communityId)) {
			throw new SynapseNotFoundException(new NotFoundException("Could not find that community"));
		}
		communitiesById.remove(communityId);
	}
	
	@Override
	public void joinCommunity(String communityId) throws SynapseException {
		Community community = communitiesById.get(communityId);
		Team team = teamsById.get(community.getTeamId());
		joinUserToThisCommunityTeam(team, currentUserData.getProfile().getOwnerId());
	}
	
	@Override
	public void leaveCommunity(String communityId) throws SynapseException {
		Community community = communitiesById.get(communityId);
		Team team = teamsById.get(community.getTeamId());
		removeCommunityAdmin(communityId, currentUserData.getProfile().getOwnerId());
		removeUserFromCommunityTeam(team, communityId, currentUserData.getProfile().getOwnerId());
	}
	
	private void joinUserToThisCommunityTeam(Team team, String userId) {
		teamMemberships.put(team, userId);
	}

	private void removeUserFromCommunityTeam(Team team, String communityId, String userId) throws SynapseException {
		teamMemberships.remove(team, userId);
	}

	@Override
	public Session login(String userName, String password) throws SynapseException {
		UserSessionData data = usersById.get(userName);
		if (data == null) {
			throw new SynapseClientException();
		}
		currentUserData = data;
		Session session = data.getSession();
		session.setAcceptsTermsOfUse(true);
		if (!agreedTOUs.contains(data.getProfile().getOwnerId())) {
			session.setAcceptsTermsOfUse(false);
		}
		data.setSession(session);
		return session;		
	}

	@Override
	public void logout() throws SynapseException {
		currentUserData = null;
	}

	@Override
	public UserSessionData getUserSessionData() throws SynapseException {
		if (currentUserData == null) {
			throw new SynapseClientException();
		}
		return currentUserData;
	}

	@Override
	public S3FileHandle createFileHandle(File temp, String contentType) throws SynapseException, IOException {
		// If this was compressed, uncompress it before storing it. The actual implementation stores compressed
		// and only uncompressed when you call downloadV2WikiMarkdown(). 
		String markdown = null;
		try {
			markdown = org.sagebionetworks.downloadtools.FileUtils.readCompressedFileAsString(temp);	
		} catch(Exception e) {
			// assume it wasn't compressed.
			markdown = FileUtils.readFileToString(temp);
		}
		if (markdown == null) {
			throw new SynapseClientException(new NotFoundException());
		}
		S3FileHandle handle = new S3FileHandle();
		String id = newId();
		handle.setId(id);
		handle.setFileName(temp.getName());
		
		markdownsByFileHandleId.put(id, markdown);

		return handle;
	}
	

	@Override
	public FileHandleResults createFileHandles(List<File> files) throws SynapseException {
		List<FileHandle> handles = Lists.newArrayListWithCapacity(files.size());
		for (File file : files) {
			try {
				String mimeType = SynapseClientImpl.guessContentTypeFromStream(file);
				FileHandle handle = createFileHandle(file, mimeType);
				handles.add(handle);
			} catch(IOException ioe) {
				throw new SynapseClientException(ioe);
			}
		}
		FileHandleResults results = new FileHandleResults();
		results.setList(handles);
		return results;
	}

	@Override
	public String getUserName() {
		if (currentUserData != null && currentUserData.getProfile() != null) {
			return currentUserData.getProfile().getUserName();
		}
		return null;
	}

	@Override
	public AccessControlList getACL(String entityId) throws SynapseException {
		logger.info("Looking for an ACL for: " + entityId);
		return aclsByEntityId.get(entityId);		
	}

	@Override
	public void updateMyProfile(UserProfile userProfile) throws SynapseException {
		UserSessionData data = usersById.get(userProfile.getOwnerId());
		data.setProfile(userProfile);		
	}

	@Override
	public UserProfile getUserProfile(String ownerId) throws SynapseException {
		return usersById.get(ownerId).getProfile();
	}

	@Override
	public AccessControlList createACL(AccessControlList acl) throws SynapseException {
		aclsByEntityId.put(acl.getId(), acl);
		return acl;
	}

	@Override
	public UserEntityPermissions getUsersEntityPermissions(String entityId) throws SynapseException {
		UserEntityPermissions permits = new UserEntityPermissions();
		permits.setCanAddChild(false);
		permits.setCanChangePermissions(false);
		permits.setCanDelete(false);
		permits.setCanDownload(false);
		permits.setCanEdit(false);
		permits.setCanEnableInheritance(false);
		permits.setCanPublicRead(false);
		permits.setCanView(false);
		AccessControlList acl = aclsByEntityId.get(entityId);
		if (acl != null) {
			for (ResourceAccess ra : acl.getResourceAccess()) {
				if (ra.getPrincipalId().toString().equals(currentUserData.getProfile().getOwnerId())) {
					for (ACCESS_TYPE type : ra.getAccessType()) {
						if (type == ACCESS_TYPE.UPDATE) {
							permits.setCanEdit(true);			
						} else if (type == ACCESS_TYPE.CHANGE_PERMISSIONS) {
							permits.setCanChangePermissions(true);			
						}
					}
				}
			}
		}
		return permits;
	}

	@Override
	public V2WikiPage createV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage page)
			throws JSONObjectAdapterException, SynapseException {
		String id = newId();
		page.setId(id);
		wikiPagesById.put(page.getId(), page);
		return page;
		
	}

	@Override
	public V2WikiPage getV2WikiPage(WikiPageKey key) throws JSONObjectAdapterException, SynapseException {
		String realId = key.getWikiPageId();
		if (!wikiPagesById.containsKey(realId)) {
			throw new SynapseClientException("Wiki page not found");
		}
		return wikiPagesById.get(realId);
	}

	@Override
	public V2WikiPage updateV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage page)
			throws JSONObjectAdapterException, SynapseException {
		if (!wikiPagesById.containsKey(page.getId())) {
			throw new SynapseClientException("Wiki page does not yet exist");
		}
		wikiPagesById.put(page.getId(), page);
		return page;
	}

	@Override
	public V2WikiPage getV2RootWikiPage(String ownerId, ObjectType ownerType) throws JSONObjectAdapterException, SynapseException {
		// The invisible root wiki page is stored under the community key.
		Community community = communitiesById.get(ownerId);
		if (community == null) {
			throw new SynapseClientException("Wiki page's community not found");
		}
		String communityId = community.getId();
		if (!wikiPagesById.containsKey(communityId)) {
			throw new SynapseClientException("Wiki page not found");
		}
		return wikiPagesById.get(communityId);
	}

	@Override
	public FileHandleResults getV2WikiAttachmentHandles(WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		if (!wikiPagesById.containsKey(key.getWikiPageId())) {
			throw new SynapseClientException("Wiki not found");
		}
		FileHandleResults results = new FileHandleResults();
		V2WikiPage page = wikiPagesById.get(key.getWikiPageId());
		
		// I think we need to save and restore these...
		List<FileHandle> handles = Lists.newArrayListWithCapacity(page.getAttachmentFileHandleIds().size());
		if (page.getAttachmentFileHandleIds() != null) {
			for (String id : page.getAttachmentFileHandleIds()) {
				S3FileHandle handle = new S3FileHandle();
				handle.setId(id);
				handles.add(handle);
			}
		}
		results.setList(handles);
		return results;
	}

	@Override
	public void deleteV2WikiPage(WikiPageKey key) throws SynapseException {
		wikiPagesById.remove(key.getWikiPageId());
	}

	@Override
	public PaginatedResults<V2WikiHeader> getV2WikiHeaderTree(String ownerId, ObjectType ownerType)
			throws SynapseException, JSONObjectAdapterException {
		List<V2WikiHeader> list = Lists.newArrayList();

		// get the root, add it
		V2WikiPage root = wikiPagesById.get(ownerId);
		addToResults(list, root);
		
		// get every page under the root (we only have a shallow list, not a tree), add it
		for (V2WikiPage page : wikiPagesById.values()) {
			if (page.getParentWikiId() != null && page.getParentWikiId().equals(root.getId())) {
				addToResults(list, page);
			}
		}
		return toResults(list);
	}
	
	private void addToResults(List<V2WikiHeader> list, V2WikiPage page) {
		V2WikiHeader header = new V2WikiHeader();
		header.setId(page.getId());
		header.setParentId(page.getParentWikiId());
		header.setTitle(page.getTitle());
		list.add(header);
	}
	
	@Override
	public String getTermsOfUse(DomainType domain) throws SynapseException {
		if (domain != DomainType.BRIDGE) {
			throw new IllegalArgumentException("Should not call getTermsOfUse() with the Synapse domain");
		}
		return "<p>These are the Stub terms of use.</p>";
	}

	@Override
	public Team getTeam(String id) throws SynapseException {
		if (!teamsById.keySet().contains(id)) {
			throw new SynapseClientException("Team not found");
		}
		return teamsById.get(id);
	}

	@Override
	public TeamMembershipStatus getTeamMembershipStatus(String teamId, String userId) throws SynapseException {
		TeamMembershipStatus status = new TeamMembershipStatus();
		status.setTeamId(teamId);
		status.setUserId(userId);
		Team team = teamsById.get(teamId);
		if (team != null) {
			Collection<String> members = teamMemberships.get(team);
			status.setIsMember( (members != null && members.contains(userId)) );
		} else {
			status.setIsMember(false);
		}
		return status;
	}

	/**
	 * This returns false if either the userName or the email are a duplicate.
	 */
	@Override
	public AliasCheckResponse checkAliasAvailable(AliasCheckRequest request) throws SynapseException {
		String value = request.getAlias();
		AliasCheckResponse response = new AliasCheckResponse();
		response.setAvailable(true);
		for (UserSessionData data : usersById.values()) {
			String email = emailByUserId.get(data.getProfile().getOwnerId());
			if (email.equals(value) && request.getType() == AliasType.USER_EMAIL) {
				response.setAvailable(false);
			}
			String userName = data.getProfile().getUserName();
			if (userName.equals(value) && request.getType() == AliasType.USER_NAME) {
				response.setAvailable(false);
			}
		}
		return response;
	}
	
	@Override
	public void createUser(NewUser user) throws SynapseException {
		if (usersById.get(user.getUserName()) != null) {
			throw new SynapseClientException("Service Error(409): FAILURE: Got HTTP status 409 for  Response Content: {\"reason\":\"User '"+user.getUserName()+"' already exists\n\"}");
		}
		// Check email too
		for (UserSessionData data : usersById.values()) {
			String email = emailByUserId.get(data.getProfile().getOwnerId());
			if (user.getEmail().equals(email)) {
				throw new SynapseClientException("Service Error(409): FAILURE: Got HTTP status 409 for  Response Content: {\"reason\":\"User email '"+email+"' already exists\n\"}");
			}
		}

		String USER_ID = newId();
		emailByUserId.put(USER_ID, user.getEmail());
		
		UserProfile profile = new UserProfile();
		profile.setUserName(user.getUserName());
		profile.setFirstName(user.getFirstName());
		profile.setLastName(user.getLastName());
		profile.setOwnerId(USER_ID);
		Session session = new Session();
		session.setSessionToken(USER_ID);
		UserSessionData data = new UserSessionData();
		data.setSession(session);
		data.setProfile(profile);
		// ARGH!
		usersById.put(user.getUserName(), data);
		usersById.put(USER_ID, data);		
	}

	@Override
	public void changePassword(String sessionToken, String newPassword) throws SynapseException {
		// noop
	}

	@Override
	public void signTermsOfUse(String sessionToken, DomainType domain, boolean acceptTerms) throws SynapseException {
		if (domain != DomainType.BRIDGE) {
			throw new IllegalArgumentException("Don't call this method with any other domain than Bridge");
		}
		if (acceptTerms) {
			agreedTOUs.add(currentUserData.getProfile().getOwnerId());	
		}		
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean createUserIfNecessary)
			throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean createUserIfNecessary,
			DomainType domainType) throws SynapseException {
		// We'd like to test three scenarios here:
		// 1. Brand new user, needs to sign TOU
		// 2. Returning user who hasn't signed TOU?
		// 3. Returning user who should just be logged in to default start page
		currentUserData = usersById.values().iterator().next();
		Session session = new Session();
		session.setSessionToken(currentUserData.getProfile().getOwnerId());
		return session;		
	}

	@Override
	public BridgeVersionInfo getBridgeVersionInfo() throws SynapseException {
		BridgeVersionInfo info = new BridgeVersionInfo();
		info.setVersion("1.0");
		return info;
	}

	// Get communitiesById FOR THIS USER ALONE.
	@Override
	public PaginatedResults<Community> getCommunities(long limit, long offset) throws SynapseException {
		List<Community> memberCommunities = Lists.newArrayList();
		if (currentUserData != null && currentUserData.getProfile() != null) {
			for (Community community : communitiesById.values()) {
				Team team = teamsById.get(community.getTeamId());
				Collection<String> members = teamMemberships.get(team);
				if (members != null && members.contains(currentUserData.getProfile().getOwnerId())) {
					memberCommunities.add(community);
				}
			}
		}
		return PaginatedResultsUtil.createPaginatedResults(memberCommunities, limit, offset);
	}

	@Override
	public PaginatedResults<Community> getAllCommunities(long limit, long offset) throws SynapseException {
		return PaginatedResultsUtil.createPaginatedResults(new ArrayList<Community>(communitiesById.values()), limit, offset);
	}

	@Override
	public PaginatedResults<UserGroupHeader> getCommunityMembers(String communityId, long limit, long offset)
			throws SynapseException {
		Community community = communitiesById.get(communityId);
		Team team = teamsById.get(community.getTeamId());
		Collection<String> members = teamMemberships.get(team);
		
		List<UserGroupHeader> headers = Lists.newArrayListWithCapacity(members.size());
		for (String memberId : members) {
			UserProfile profile = usersById.get(memberId).getProfile();
			
			UserGroupHeader header = new UserGroupHeader();
			header.setUserName(profile.getUserName());
			header.setEmail(emailByUserId.get(profile.getOwnerId()));
			header.setFirstName(profile.getFirstName());
			header.setIsIndividual(true);
			header.setLastName(profile.getLastName());
			header.setOwnerId(profile.getOwnerId());
			headers.add(header);
		}
		return PaginatedResultsUtil.createPaginatedResults(headers, limit, offset);
	}
	
	@Override
	public void addCommunityAdmin(String communityId, String userId) throws SynapseException {
		// One ACL per user, which is not how this is really structured, but it's easier to stub out. 
		addToAccessControlList(communityId, userId, ACCESS_TYPE.UPDATE, ACCESS_TYPE.CHANGE_PERMISSIONS);
	}

	@Override
	public void removeCommunityAdmin(String communityId, String userId) throws SynapseException {
		// All of this only matters if you're removing yourself...
		AccessControlList acl = aclsByEntityId.get(communityId);
		
		Set<String> admins = Sets.newHashSet();
		for (ResourceAccess ra : acl.getResourceAccess()) {
			
			if (ra.getAccessType().contains(ACCESS_TYPE.UPDATE)) {
				admins.add(ra.getPrincipalId().toString());
			}
		}
		if (admins.size() < 2 && admins.contains(userId)) {
			throw new SynapseClientException("Service Error(401): {\"reason\":\"Need at least one admin.\n\"}");
		}
		for (ResourceAccess ra : acl.getResourceAccess()) {
			if (ra.getPrincipalId().toString().equals(userId)) {
				ra.getAccessType().remove(ACCESS_TYPE.CHANGE_PERMISSIONS);
				ra.getAccessType().remove(ACCESS_TYPE.UPDATE);
			}
		}
	}
	
	@Override
	public String downloadV2WikiMarkdown(WikiPageKey key) throws ClientProtocolException, FileNotFoundException,
			IOException {
		V2WikiPage page = wikiPagesById.get(key.getWikiPageId());
		if (page == null) {
			throw new FileNotFoundException("Wiki page not found");
		}
		String markdown = markdownsByFileHandleId.get(page.getMarkdownFileHandleId());
		if (markdown == null) {
			throw new FileNotFoundException("Wiki page markdown not found");
		}
		return markdown;
	}

	// For the purposes of these next two methods, we really don't care that one is on behalf of another person.
	
	@Override
	public List<ParticipantDataRow> appendParticipantData(String participantIdentifier, String participantDataDescriptorId, List<ParticipantDataRow> data)
			throws SynapseException {
		return internalAppendParticipantData(participantDataDescriptorId, data);
	}
	
	@Override
	public List<ParticipantDataRow> appendParticipantData(String participantDataDescriptorId, List<ParticipantDataRow> data) throws SynapseException {
		return internalAppendParticipantData(participantDataDescriptorId, data);
	}
	
	public List<ParticipantDataRow> internalAppendParticipantData(String participantDataDescriptorId, List<ParticipantDataRow> data)
			throws SynapseException {
		for (ParticipantDataRow row : data) {
			row.setRowId(Long.parseLong(newId()));
		}
		List<ParticipantDataRow> oldData = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (oldData != null) {
			// Internal implementation is AbstractList, work around htis.
			List<ParticipantDataRow> list = Lists.newArrayList();
			list.addAll(oldData);
			list.addAll(data);
			participantDataByDescriptorId.put(participantDataDescriptorId, list);
		} else {
			// So, when you append data, the default is that it is finished.
			String compoundKey = currentUserData.getProfile().getOwnerId() + ":" + participantDataDescriptorId;
			ParticipantDataStatus status = new ParticipantDataStatus();
			status.setLastEntryComplete(true);
			status.setLastStarted(new Date());
			status.setParticipantDataDescriptorId(participantDataDescriptorId);
			statuses.put(compoundKey, status);
			participantDataByDescriptorId.put(participantDataDescriptorId, data);
		}
		return data;
	}

	@Override
	public List<ParticipantDataRow> updateParticipantData(String participantDataDescriptorId, List<ParticipantDataRow> data)
			throws SynapseException {
		List<ParticipantDataRow> existingData = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (existingData == null) {
			throw new SynapseClientException("cannot update " + participantDataDescriptorId);
		}

		for (ParticipantDataRow dataRow : data) {
			for (ParticipantDataRow existingDataRow : existingData) {
				if (existingDataRow.getRowId().equals(dataRow.getRowId())) {
					existingDataRow.getData().putAll(dataRow.getData());
					break;
				}
			}
		}
		return existingData;
	}
	
	@Override
	public PaginatedResults<ParticipantDataRow> getRawParticipantData(String participantDataDescriptorId, long limit,
			long offset, boolean normalizeData) throws SynapseException {
		List<ParticipantDataRow> data = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (data == null) {
			data = Collections.emptyList();
		}
		return PaginatedResultsUtil.createPaginatedResults(Lists.newArrayList(data), limit, offset);
	}
	
	@Override
	public ParticipantDataDescriptor createParticipantDataDescriptor(ParticipantDataDescriptor descriptor)
			throws SynapseException {
		descriptor.setId(newId());
		descriptorsById.put(descriptor.getId(), descriptor);
		return descriptor;
	}
	
	@Override
	public PaginatedResults<ParticipantDataDescriptor> getAllParticipantDataDescriptors(long limit, long offset)
			throws SynapseException {
		for (ParticipantDataDescriptor descriptor : descriptorsById.values()) {
			descriptor.setStatus(null);
		}
		return PaginatedResultsUtil.createPaginatedResults(Lists.newArrayList(descriptorsById.values()), limit, offset);
	}

	@Override
	public PaginatedResults<ParticipantDataDescriptor> getUserParticipantDataDescriptors(long limit, long offset)
			throws SynapseException {
		String ownerId = currentUserData.getProfile().getOwnerId();
		Collection<ParticipantDataDescriptor> descriptors = descriptorsById.values();
		for (ParticipantDataDescriptor descriptor : descriptors) {
			String compoundKey = ownerId + ":" + descriptor.getId();
			ParticipantDataStatus status = statuses.get(compoundKey);
			// Basically it's assumed to be done if this call is made, however, in the 
			// real API I believe it can be null.
			if (status == null) {
				status = new ParticipantDataStatus();
				status.setLastEntryComplete(true);
				status.setParticipantDataDescriptorId(descriptor.getId());
				descriptor.setStatus(status);
			}
			descriptor.setStatus(status);
		}
		return PaginatedResultsUtil.createPaginatedResults(Lists.newArrayList(descriptors), limit, offset);
	}
	
	@Override
	public ParticipantDataRow getParticipantDataRow(String participantDataDescriptorId, Long rowId, boolean normalizeData)
			throws SynapseException {
		if (participantDataDescriptorId == null || rowId == null) {
			throw new IllegalArgumentException("participantDataDescriptorId or rowId is null");
		}
		List<ParticipantDataRow> rows = participantDataByDescriptorId.get(participantDataDescriptorId);
		for (ParticipantDataRow row : rows) {
			if (rowId.equals(row.getRowId())) {
				return row;
			}
		}
		throw new IllegalArgumentException("Row ID " + rowId.toString() + " does not exist for data set #" + participantDataDescriptorId);
	}
	
	@Override
	public ParticipantDataCurrentRow getCurrentParticipantData(String participantDataDescriptorId, boolean normalizeData)
			throws SynapseException {
		
		String compoundKey = currentUserData.getProfile().getOwnerId() + ":" + participantDataDescriptorId;
		ParticipantDataStatus status = statuses.get(compoundKey);
		ParticipantDataDescriptor descriptor = descriptorsById.get(participantDataDescriptorId);
		
		ParticipantDataRow emptyRow = new ParticipantDataRow();
		emptyRow.setData(Maps.<String,ParticipantDataValue>newHashMap()); // Collections.<String, ParticipantDataValue>emptyMap()
		
		ParticipantDataCurrentRow currentRow = new ParticipantDataCurrentRow();
		currentRow.setStatus(status);
		currentRow.setDescriptor(descriptor);
		currentRow.setPreviousData(emptyRow);
		currentRow.setCurrentData(emptyRow);
		
		List<ParticipantDataRow> rows = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (rows == null) {
			return currentRow; // this is what happens in mid-tier (nothing);
			// rows = Collections.emptyList();
		}
		// Taken directly from the server tier, same logic.
		ListIterator<ParticipantDataRow> iter = rows.listIterator(rows.size());
		if (iter.hasPrevious()) {
			ParticipantDataRow lastRow = iter.previous();
			if (BooleanUtils.isFalse(status.getLastEntryComplete())) {
				currentRow.setCurrentData(lastRow);
				if (iter.hasPrevious()) {
					currentRow.setPreviousData(iter.previous());
				}
			} else {
				currentRow.setPreviousData(lastRow);
			}
		}
		currentRow.setColumns(Lists.newArrayList(columnsByDescriptorById.get(participantDataDescriptorId)));
		return currentRow;
	}

	@Override
	public List<ParticipantDataRow> getCurrentRows(String participantDataDescriptorId, boolean normalizeData) throws SynapseException {
		List<ParticipantDataRow> result = Lists.newArrayList(getHistoryRows(participantDataDescriptorId, null, null, false));
		ParticipantDataDescriptor descriptor = descriptorsById.get(participantDataDescriptorId);
		for (Iterator<ParticipantDataRow> iterator = result.iterator(); iterator.hasNext();) {
			ParticipantDataRow row = iterator.next();
			if (((ParticipantDataEventValue) row.getData().get(descriptor.getEventColumnName())).getEnd() != null) {
				iterator.remove();
			}
		}
		return result;
	}

	@Override
	public List<ParticipantDataRow> getHistoryRows(String participantDataDescriptorId, Date begin, Date end,
			boolean normalizeData) throws SynapseException {
		List<ParticipantDataRow> rows = participantDataByDescriptorId.get(participantDataDescriptorId);
		return rows;
	}

	@Override
	public ParticipantDataColumnDescriptor createParticipantDataColumnDescriptor(
			ParticipantDataColumnDescriptor column) throws SynapseException {
		if (column.getParticipantDataDescriptorId() == null) {
			throw new SynapseClientException("You must include a ParticipantDataDescriptor id before creating this column");
		}
		column.setId(newId());
		columnsByDescriptorById.put(column.getParticipantDataDescriptorId(), column);
		return column;
	}

	@Override
	public PaginatedResults<ParticipantDataColumnDescriptor> getParticipantDataColumnDescriptors(
			String descriptorId, long limit, long offset) throws SynapseException {
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList(columnsByDescriptorById.get(descriptorId));
		return PaginatedResultsUtil.createPaginatedResults(list, limit, offset);
	}

	@Override
	public ParticipantDataDescriptorWithColumns getParticipantDataDescriptorWithColumns(String descriptorId)
			throws SynapseException {
		ParticipantDataDescriptor descriptor = descriptorsById.get(descriptorId);
		if (descriptor == null) {
			throw new SynapseNotFoundException("Could not find descriptor #" + descriptorId);
		}
		List<ParticipantDataColumnDescriptor> list = Lists.newArrayList(columnsByDescriptorById.get(descriptorId));
		if (list.isEmpty()) {
			throw new SynapseNotFoundException("Could not find columns for descriptor #" + descriptorId);
		}
		String ownerId = currentUserData.getProfile().getOwnerId();
		String compoundKey = ownerId + ":" + descriptorId;
		ParticipantDataStatus status = statuses.get(compoundKey);
		descriptor.setStatus(status);
		ParticipantDataDescriptorWithColumns descriptorWithColumns = new ParticipantDataDescriptorWithColumns();
		descriptorWithColumns.setDescriptor(descriptor);
		descriptorWithColumns.setColumns(list);
		return descriptorWithColumns;
	}


	@Override
	public void sendPasswordResetEmail(String email) throws SynapseException {
		// noop
	}

	@Override
	public void setAuthEndpoint(String authEndpoint) {
		// noop
	}
	
	@Override
	public void sendParticipantDataDescriptorUpdates(ParticipantDataStatusList dataStatusList) {
		String ownerId = currentUserData.getProfile().getOwnerId();
		for (ParticipantDataStatus status : dataStatusList.getUpdates()) {
			String descriptorId = status.getParticipantDataDescriptorId();
			String compoundKey = ownerId + ":" + descriptorId;
			statuses.put(compoundKey, status);
			logger.info("Updating status: " + compoundKey + " to status: " + status.getLastEntryComplete());
		}
	}
	
	@Override
	public void deleteParticipantDataRows(String participantDataDescriptorId, IdList rowsIds) throws SynapseException {
		List<ParticipantDataRow> rows = participantDataByDescriptorId.get(participantDataDescriptorId);
		List<ParticipantDataRow> newRows = Lists.newArrayList();
		for (Iterator<ParticipantDataRow> i = rows.iterator(); i.hasNext(); ) {
			ParticipantDataRow row = i.next();
			if (!rowsIds.getList().contains(row.getRowId())) {
				newRows.add(row);
			}
		}
		participantDataByDescriptorId.put(participantDataDescriptorId, newRows);
	}

	@Override
	public TimeSeriesTable getTimeSeries(String participantDataDescriptorId, List<String> columnNames,
			boolean normalizeData) throws SynapseException {
		TimeSeriesTable timeSeriesTable = new TimeSeriesTable();
		timeSeriesTable.setDateIndex(0L);
		TimeSeriesColumn col1 = new TimeSeriesColumn();
		col1.setName("date");
		TimeSeriesColumn col2 = new TimeSeriesColumn();
		col2.setName("nothing");
		col2.setActualMinimum(0.0);
		col2.setExpectedMinimum(0.0);
		col2.setActualMaximum(4.0);
		col2.setExpectedMaximum(4.0);
		timeSeriesTable.setColumns(Lists.newArrayList(col1, col2));
		TimeSeriesRow row1 = new TimeSeriesRow();
		row1.setValues(Lists.newArrayList("1233242343", "3.3"));
		TimeSeriesRow row2 = new TimeSeriesRow();
		row2.setValues(Lists.newArrayList("1233252343", "1.2"));
		timeSeriesTable.setRows(Lists.newArrayList(row1, row2));
		timeSeriesTable.setEvents(Lists.<ParticipantDataEventValue> newArrayList());
		timeSeriesTable.setFirstDate(new Long(new Date().getTime() - 10000L));
		return timeSeriesTable;
	}
	
	@Override
	public PaginatedResults<Team> getTeams(String fragment, long limit, long offset) throws SynapseException {
		if (fragment == null) {
			throw new IllegalArgumentException("Must have a search fragment string");
		}
		List<Team> teams = Lists.newArrayList();
		for (Team team : teamsById.values()) {
			if (team.getName() != null) {
				if (team.getName().indexOf(fragment) > -1) {
					teams.add(team);
				}
			}
		}
		return PaginatedResultsUtil.createPaginatedResults(teams, limit, offset);
	}
	
	@Override
	public Team createTeam(Team team) throws SynapseException {
		if (team.getId() == null) {
			team.setId(newId());	
		}
		teamsById.put(team.getId(), team);
		return team;
	}
	
	@Override
	public void addTeamMember(String teamId, String memberId) throws SynapseException {
		Team team = teamsById.get(teamId);
		if (team == null) {
			throw new SynapseNotFoundException("Could not find team #"+teamId);
		}
		UserSessionData member = usersById.get(memberId);
		if (member == null) {
			throw new SynapseNotFoundException("Could not find member #"+memberId);
		}
		teamMemberships.put(team, memberId);
	}
}
