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
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.transform.impl.UndeclaredThrowableStrategy;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatus;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.versionInfo.BridgeVersionInfo;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SharedClientConnection;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.DomainType;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleResults;
import org.sagebionetworks.repo.model.file.S3FileHandle;
import org.sagebionetworks.repo.model.table.PaginatedRowSet;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public abstract class SageServicesStub implements SynapseClient, BridgeClient {
	
	private static final Logger logger = LogManager.getLogger(SageServicesStub.class.getName());

	UserSessionData currentUserData;
	String sessionToken; // when this isn't separate from UserSessionData.getProfile().getSession() there are errors.
	Set<String> agreedTOUs = Sets.newHashSet();
	Map<String,Community> communitiesById = Maps.newHashMap();
	Map<String,UserSessionData> usersById = Maps.newHashMap();
	Map<String,AccessControlList> aclsByEntityId = Maps.newHashMap(); // treating community as an entity
	Map<String,V2WikiPage> wikiPagesById = Maps.newHashMap();
	Map<String,Team> teamsById = Maps.newHashMap();
	Map<Team,Set<String>> memberships = Maps.newHashMap();
	Map<String,String> markdownsByFileHandleId = Maps.newHashMap();

	// Participant data
	Map<String,ParticipantDataDescriptor> descriptorsById = Maps.newHashMap();
	Multimap<String,ParticipantDataDescriptor> descriptorsByUserId = LinkedListMultimap.create();
	Multimap<String,ParticipantDataColumnDescriptor> columnsByDescriptorId = LinkedListMultimap.create();
	Map<String, RowSet> participantDataByDescriptorId = Maps.newHashMap();
	
	String timPowersId;
	
	// This has to be above the ID used for community. Before this can be run against
	// the real back end service, we need a way to retrieve the *actual* ID of 
	// the community that is created.
	int idCount = 2;

	public SageServicesStub() {
		System.out.println("---------------------------------------- BEING CREATED");
		
		UserSessionData data = createUser("timpowers", "timpowers@timpowers.com", true);
		timPowersId = data.getProfile().getOwnerId();
		createCommunity("1", "Fanconi Anemia", "This is a very rare but very serious disease, affecting about 1,000 people worldwide.", data);
		createUser("octaviabutler", "octaviabutler@octaviabutler.com", false);
		createUser("test", "test@test.com", true);
	}

	/**
	 * bean factory method
	 * 
	 * @return
	 */
	public static SageServicesStub createInstance() {
		// Configure CGLIB Enhancer...
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(SageServicesStub.class);
		enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));
		enhancer.setInterfaces(new Class[] { SynapseClient.class, BridgeClient.class });
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				return proxy.invokeSuper(obj, args);
			}
		});

		// Generate the proxy class and create a proxy instance.
		Object proxy = enhancer.create();

		return (SageServicesStub) proxy;
	}

	private UserSessionData createUser(String userName, String email, boolean acceptsTOU) {
		String id = newId();
		UserProfile profile = new UserProfile();
		
		profile.setDisplayName(userName);
		profile.setEmail(email);
		profile.setOwnerId(id);
		profile.setUserName(email);
		Session session = new Session();
		session.setSessionToken("session"+id);
		if (acceptsTOU) {
			agreedTOUs.add(id);
		}
		session.setAcceptsTermsOfUse(acceptsTOU);
		UserSessionData data = new UserSessionData();
		data.setIsSSO(false);
		data.setProfile(profile);
		data.setSession(session);
		usersById.put(email, data);
		usersById.put(id, data);
		return data;
	}

	// IMPORTANT: The fact that we're looking for a specific ID in the tests would prevent this from
	// running against the real back end, that needs to be fixed.
	private Community createCommunity(String id, String name, String description, UserSessionData user) {
		Community community = new Community();
		community.setId(id);
		community.setName(name);
		community.setDescription(description);
		community.setCreatedBy(user.getProfile().getEmail());
		community.setCreatedOn(new Date());
		community.setModifiedBy(user.getProfile().getEmail());
		community.setModifiedOn(new Date());
		
		// The hidden root page, under a key directly related to the community.
		V2WikiPage root = createWikiPage(user, "Root", null, "Root");
		wikiPagesById.put(community.getId(), root);
		
		V2WikiPage page = createWikiPage(user, "Welcome Page", root.getId(), "Welcome");
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
		V2WikiPage page = new V2WikiPage();
		page.setTitle(title);
		page.setCreatedBy(user.getProfile().getEmail());
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
	
	private <T> List<T> paginate(List<T> list, long limit, long offset) {
		if (list == null) {
			return Collections.emptyList();
		}
		if (list.isEmpty()) {
			return list;
		}
		int start = (int)offset;
		int end = (int)limit;
		int lastIndex = list.size();
		start = (start > lastIndex) ? lastIndex : (start < 0) ? 0 : start;
		end = (end > lastIndex) ? lastIndex : (end < 1) ? 1 : end;
		return list.subList(start, end);
	}
	
	private <T extends JSONEntity> PaginatedResults<T> toResults(List<T> list) {
		PaginatedResults<T> results = new PaginatedResults<T>();
		results.setResults(list);
		results.setTotalNumberOfResults(list.size());
		return results;
	}
	
	private <T extends JSONEntity> PaginatedResults<T> toResults(List<T> list, long limit, long offset) {
		List<T> newList = paginate(list, limit, offset);
		PaginatedResults<T> results = new PaginatedResults<T>();
		results.setResults(newList);
		results.setTotalNumberOfResults(list.size());
		return results;
	}
	
	private <T extends JSONEntity> PaginatedResults<T> toResults(Collection<T> coll, long limit, long offset) {
		if (coll == null) {
			return new PaginatedResults<T>();
		}
		return toResults(Lists.newArrayList(coll), limit, offset);
	}

	@Override
	public void appendUserAgent(String toAppend) {
		logger.debug("--> appendUserAgent: " + toAppend);
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
	public SharedClientConnection getSharedClientConnection() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getBridgeEndpoint() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setBridgeEndpoint(String repoEndpoint) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Community createCommunity(Community community) throws SynapseException {
		return createCommunity(newId(), community.getName(), community.getDescription(), currentUserData);
	}

	@Override
	public Community getCommunity(String communityId) throws SynapseException {
		Community community = communitiesById.get(communityId);
		if (community == null) {
			throw new SynapseException(new NotFoundException());
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
		Set<String> memberSet = memberships.get(team);
		if (memberSet == null) {
			memberSet = new HashSet<String>();
			memberships.put(team, memberSet);
		}
		memberSet.add(userId);
	}

	private void removeUserFromCommunityTeam(Team team, String communityId, String userId) throws SynapseException {
		// We want to simulate the error where the user is the last *admin* associated to 
		// the community.
		Set<String> memberSet = memberships.get(team);
		memberSet.remove(userId);
	}

	@Override
	public Session login(String username, String password) throws SynapseException {
		UserSessionData data = usersById.get(username);
		if (data == null) {
			throw new SynapseException();
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
		return currentUserData;
	}

	@Override
	public S3FileHandle createFileHandle(File temp, String contentType) throws SynapseException, IOException {
		S3FileHandle handle = new S3FileHandle();
		// This isn't going to be easy to stub out! We need to read in the content and save it in markdownsByFileHandleId,
		// as that's the only place we're using it right now, with a new id, and put that ID in the handle.
		String id = newId();
		String markdown = FileUtils.readFileToString(temp);
		markdownsByFileHandleId.put(id, markdown);
		handle.setId(id);
		handle.setFileName(temp.getName());
		// Create a preview item as well? Where to store it?
		/*
		id = newId();
		PreviewFileHandle pfh = new PreviewFileHandle();
		pfh.setId(id);
		pfh.setFileName("preview.png");
		*/
		return handle;
	}
	

	@Override
	public FileHandleResults createFileHandles(List<File> files) throws SynapseException {
		List<FileHandle> handles = Lists.newArrayList();
		for (File file : files) {
			try {
				String mimeType = SynapseClientImpl.guessContentTypeFromStream(file);
				FileHandle handle = createFileHandle(file, mimeType);
				handles.add(handle);
			} catch(IOException ioe) {
				throw new SynapseException(ioe);
			}
		}
		FileHandleResults results = new FileHandleResults();
		results.setList(handles);
		return results;
	}

	@Override
	public String getUserName() {
		if (currentUserData != null && currentUserData.getProfile() != null) {
			return currentUserData.getProfile().getDisplayName();
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
		logger.info("--> getUserProfile: " + ownerId);
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
			throw new SynapseException("Wiki page not found");
		}
		return wikiPagesById.get(realId);
	}

	@Override
	public V2WikiPage updateV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage page)
			throws JSONObjectAdapterException, SynapseException {
		if (!wikiPagesById.containsKey(page.getId())) {
			throw new SynapseException("Wiki page does not yet exist");
		}
		wikiPagesById.put(page.getId(), page);
		return page;
	}

	@Override
	public V2WikiPage getV2RootWikiPage(String ownerId, ObjectType ownerType) throws JSONObjectAdapterException, SynapseException {
		// The invisible root wiki page is stored under the community key.
		Community community = communitiesById.get(ownerId);
		if (community == null) {
			throw new SynapseException("Wiki page's community not found");
		}
		String communityId = community.getId();
		if (!wikiPagesById.containsKey(communityId)) {
			throw new SynapseException("Wiki page not found");
		}
		return wikiPagesById.get(communityId);
	}

	@Override
	public FileHandleResults getV2WikiAttachmentHandles(WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		if (!wikiPagesById.containsKey(key.getWikiPageId())) {
			throw new SynapseException("Wiki not found");
		}
		FileHandleResults results = new FileHandleResults();
		V2WikiPage page = wikiPagesById.get(key.getWikiPageId());
		
		// I think we need to save and restore these...
		List<FileHandle> handles = Lists.newArrayList();
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
	public String getSynapseTermsOfUse() throws SynapseException {
		return "<p>These are the Stub terms of use.</p>";
	}

	@Override
	public Team getTeam(String id) throws SynapseException {
		if (!teamsById.keySet().contains(id)) {
			throw new SynapseException("Team not found");
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
			Set<String> members = memberships.get(team);
			status.setIsMember( (members != null && members.contains(userId)) );
		}
		return status;
	}
	

	@Override
	public void createUser(NewUser user) throws SynapseException {
		throw new IllegalArgumentException("Do not use this method from bridge");
	}

	@Override
	public void createUser(NewUser user, DomainType originClient) throws SynapseException {
		if (usersById.get(user.getEmail()) != null) {
			throw new SynapseException("Service Error(409): FAILURE: Got HTTP status 409 for  Response Content: {\"reason\":\"User 'test@test.com' already exists\n\"}");
		}
		
		String USER_ID = newId();
		
		UserProfile profile = new UserProfile();
		profile.setDisplayName(user.getDisplayName());
		profile.setEmail(user.getEmail());
		profile.setFirstName(user.getFirstName());
		profile.setLastName(user.getLastName());
		profile.setOwnerId(USER_ID);
		Session session = new Session();
		session.setSessionToken(USER_ID);
		UserSessionData data = new UserSessionData();
		data.setSession(session);
		data.setProfile(profile);
		// ARGH!
		usersById.put(user.getEmail(), data);
		usersById.put(USER_ID, data);		
	}

	@Override
	public void changePassword(String sessionToken, String newPassword) throws SynapseException {
		// noop
	}

	@Override
	public void signTermsOfUse(String sessionToken, boolean acceptTerms) throws SynapseException {
		if (acceptTerms) {
			agreedTOUs.add(currentUserData.getProfile().getOwnerId());	
		}		
	}

	@Override
	public void sendPasswordResetEmail(String email) throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
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
		
		// The user will always be Tim Powers, session AAA.
		currentUserData = usersById.get(timPowersId);
		Session session = new Session();
		session.setSessionToken(timPowersId);
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
				Set<String> members = memberships.get(team);
				if (members != null && members.contains(currentUserData.getProfile().getOwnerId())) {
					memberCommunities.add(community);
				}
			}
		}
		return toResults(memberCommunities, limit, offset);
	}

	@Override
	public PaginatedResults<Community> getAllCommunities(long limit, long offset) throws SynapseException {
		return toResults(new ArrayList<Community>(communitiesById.values()));
	}

	@Override
	public PaginatedResults<UserGroupHeader> getCommunityMembers(String communityId, long limit, long offset)
			throws SynapseException {
		Community community = communitiesById.get(communityId);
		Team team = teamsById.get(community.getTeamId());
		Set<String> members = memberships.get(team);
		
		List<UserGroupHeader> headers = Lists.newArrayList();
		for (String memberId : members) {
			UserProfile profile = usersById.get(memberId).getProfile();
			
			UserGroupHeader header = new UserGroupHeader();
			header.setDisplayName(profile.getDisplayName());
			header.setEmail(profile.getEmail());
			header.setFirstName(profile.getFirstName());
			header.setIsIndividual(true);
			header.setLastName(profile.getLastName());
			header.setOwnerId(profile.getOwnerId());
			headers.add(header);
		}
		return toResults(headers);
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
			throw new SynapseException("Service Error(401): {\"reason\":\"Need at least one admin.\n\"}");
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
	public RowSet appendParticipantData(String participantIdentifier, String participantDataDescriptorId, RowSet data)
			throws SynapseException {
		return internalAppendParticipantData(participantDataDescriptorId, data);
	}
	
	@Override
	public RowSet appendParticipantData(String participantDataDescriptorId, RowSet data) throws SynapseException {
		return internalAppendParticipantData(participantDataDescriptorId, data);
	}
	
	public RowSet internalAppendParticipantData(String participantDataDescriptorId, RowSet data) throws SynapseException {
		for (Row row : data.getRows()) {
			row.setRowId(Long.parseLong(newId()));
		}
		RowSet oldData = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (oldData != null) {
			oldData.getRows().addAll(data.getRows());
		} else {
			participantDataByDescriptorId.put(participantDataDescriptorId, data);
		}
		return oldData; // or data?!?
	}

	@Override
	public RowSet updateParticipantData(String participantDataDescriptorId, RowSet data) throws SynapseException {
		
		RowSet oldData = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (oldData == null) {
			RowSet emptyData = new RowSet();
			emptyData.setHeaders(new ArrayList<String>());
			emptyData.setRows(new ArrayList<Row>());
			return emptyData;
		}
		for (Row row : data.getRows()) {
			if (row.getRowId() == null) {
				throw new SynapseException("Found previously unsaved row");
			}
			// This assumes the heading order never changes, which it could.
			Row existing = findRowById(oldData, row.getRowId());
			existing.setValues(row.getValues());
		}
		return oldData; // or data?!?
	}
	
	private Row findRowById(RowSet rowSet, Long id) {
		for (Row row : rowSet.getRows()) {
			if (row.getRowId().equals(id)) {
				return row;
			}
		}
		return null;
	}

	@Override
	public PaginatedRowSet getParticipantData(String participantDataDescriptorId, long limit, long offset)
			throws SynapseException {
		// TODO: This is not actually paged.
		RowSet data = participantDataByDescriptorId.get(participantDataDescriptorId);
		if (data == null) {
			RowSet emptyData = new RowSet();
			emptyData.setHeaders(new ArrayList<String>());
			emptyData.setRows(new ArrayList<Row>());
			PaginatedRowSet pagedRowSet = new PaginatedRowSet();
			pagedRowSet.setResults(emptyData);
			pagedRowSet.setTotalNumberOfResults(0L);
			return pagedRowSet;
		}
		PaginatedRowSet pagedRowSet = new PaginatedRowSet();
		pagedRowSet.setResults(data);
		pagedRowSet.setTotalNumberOfResults((long)data.getRows().size());
		return pagedRowSet;
	}
	
	@Override
	public ParticipantDataDescriptor createParticipantDataDescriptor(ParticipantDataDescriptor descriptor)
			throws SynapseException {
		descriptor.setId(newId());
		ParticipantDataStatus status = new ParticipantDataStatus();
		status.setLastEntryComplete(false);
		status.setParticipantDataDescriptorId(descriptor.getId());
		descriptor.setStatus(status);
		descriptorsById.put(descriptor.getId(), descriptor);
		descriptorsByUserId.put(currentUserData.getProfile().getOwnerId(), descriptor);
		return descriptor;
	}

	@Override
	public PaginatedResults<ParticipantDataDescriptor> getAllParticipantDatas(long limit, long offset)
			throws SynapseException {
		return toResults(descriptorsById.values(), limit, offset);
	}

	@Override
	public PaginatedResults<ParticipantDataDescriptor> getParticipantDatas(long limit, long offset)
			throws SynapseException {
		String ownerId = currentUserData.getProfile().getOwnerId();
		return toResults(descriptorsByUserId.get(ownerId), limit, offset);
	}

	@Override
	public ParticipantDataColumnDescriptor createParticipantDataColumnDescriptor(
			ParticipantDataColumnDescriptor column) throws SynapseException {
		if (column.getParticipantDataDescriptorId() == null) {
			throw new SynapseException("You must include a ParticipantDataDescriptor id before creating this column");
		}
		column.setId(newId());
		columnsByDescriptorId.put(column.getParticipantDataDescriptorId(), column);
		return column;
	}

	@Override
	public PaginatedResults<ParticipantDataColumnDescriptor> getParticipantDataColumnDescriptors(
			String descriptorId, long limit, long offset) throws SynapseException {
		return toResults(columnsByDescriptorId.get(descriptorId), limit, offset);
	}

	@Override
	public void sendPasswordResetEmail(String email, DomainType originClient) throws SynapseException {
		// noop
	}

	@Override
	public void setAuthEndpoint(String authEndpoint) {
		// noop
	}
	
	@Override
	public void sendParticipantDataDescriptorUpdates(ParticipantDataStatusList dataStatusList) {
		// noop? May model this when I understand it and build something against it
	}
}
