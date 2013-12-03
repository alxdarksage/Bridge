package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.versionInfo.BridgeVersionInfo;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SharedClientConnection;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseNotFoundException;
import org.sagebionetworks.evaluation.model.Evaluation;
import org.sagebionetworks.evaluation.model.Participant;
import org.sagebionetworks.evaluation.model.Submission;
import org.sagebionetworks.evaluation.model.SubmissionBundle;
import org.sagebionetworks.evaluation.model.SubmissionStatus;
import org.sagebionetworks.evaluation.model.SubmissionStatusEnum;
import org.sagebionetworks.evaluation.model.UserEvaluationPermissions;
import org.sagebionetworks.evaluation.model.UserEvaluationState;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.ACTAccessRequirement;
import org.sagebionetworks.repo.model.AccessApproval;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.AccessRequirement;
import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.BatchResults;
import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.EntityBundleCreate;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.EntityIdList;
import org.sagebionetworks.repo.model.EntityPath;
import org.sagebionetworks.repo.model.LocationData;
import org.sagebionetworks.repo.model.Locationable;
import org.sagebionetworks.repo.model.MembershipInvitation;
import org.sagebionetworks.repo.model.MembershipInvtnSubmission;
import org.sagebionetworks.repo.model.MembershipRequest;
import org.sagebionetworks.repo.model.MembershipRqstSubmission;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.OriginatingClient;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.RestrictableObjectDescriptor;
import org.sagebionetworks.repo.model.ServiceConstants.AttachmentType;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.TrashedEntity;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.repo.model.UserGroupHeaderResponsePage;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.VariableContentPaginatedResults;
import org.sagebionetworks.repo.model.VersionInfo;
import org.sagebionetworks.repo.model.attachment.AttachmentData;
import org.sagebionetworks.repo.model.attachment.PresignedUrl;
import org.sagebionetworks.repo.model.attachment.S3AttachmentToken;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.model.auth.UserEntityPermissions;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.doi.Doi;
import org.sagebionetworks.repo.model.file.ChunkRequest;
import org.sagebionetworks.repo.model.file.ChunkResult;
import org.sagebionetworks.repo.model.file.ChunkedFileToken;
import org.sagebionetworks.repo.model.file.CompleteAllChunksRequest;
import org.sagebionetworks.repo.model.file.CompleteChunkedFileRequest;
import org.sagebionetworks.repo.model.file.CreateChunkedFileTokenRequest;
import org.sagebionetworks.repo.model.file.ExternalFileHandle;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleResults;
import org.sagebionetworks.repo.model.file.S3FileHandle;
import org.sagebionetworks.repo.model.file.UploadDaemonStatus;
import org.sagebionetworks.repo.model.message.MessageBundle;
import org.sagebionetworks.repo.model.message.MessageRecipientSet;
import org.sagebionetworks.repo.model.message.MessageSortBy;
import org.sagebionetworks.repo.model.message.MessageStatus;
import org.sagebionetworks.repo.model.message.MessageStatusType;
import org.sagebionetworks.repo.model.message.MessageToUser;
import org.sagebionetworks.repo.model.provenance.Activity;
import org.sagebionetworks.repo.model.query.QueryTableResults;
import org.sagebionetworks.repo.model.search.SearchResults;
import org.sagebionetworks.repo.model.search.query.SearchQuery;
import org.sagebionetworks.repo.model.status.StackStatus;
import org.sagebionetworks.repo.model.storage.StorageUsageDimension;
import org.sagebionetworks.repo.model.storage.StorageUsageSummaryList;
import org.sagebionetworks.repo.model.table.ColumnModel;
import org.sagebionetworks.repo.model.table.PaginatedColumnModels;
import org.sagebionetworks.repo.model.table.RowReferenceSet;
import org.sagebionetworks.repo.model.table.RowSet;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHistorySnapshot;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.repo.model.versionInfo.SynapseVersionInfo;
import org.sagebionetworks.repo.model.wiki.WikiHeader;
import org.sagebionetworks.repo.model.wiki.WikiPage;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.common.collect.Sets;

public class SageServicesStub implements SynapseClient, BridgeClient {
	
	private static final Logger logger = LogManager.getLogger(SageServicesStub.class.getName());

	private Map<String,Community> communities = new HashMap<>();
	UserSessionData currentUserData;
	String sessionToken; // when this isn't separate from UserSessionData.getProfile().getSession() there are errors.
	Map<String, UserSessionData> users = new HashMap<>();
	Map<String,AccessControlList> acls = new HashMap<>();
	Map<String, V2WikiPage> wikiPages = new HashMap<>();
	Map<String, Team> teams = new HashMap<>();
	Map<Team,Set<String>> memberships = new HashMap<>();
	Set<String> agreedTOUs = new HashSet<>();
	
	String timPowersId;
	
	int idCount;

	public SageServicesStub() {
		System.out.println("---------------------------------------- BEING CREATED");
		
		UserSessionData data = createUser("timpowers", "timpowers@timpowers.com", true);
		timPowersId = data.getProfile().getOwnerId();
		createCommunity("1", "Fanconi Anemia", "This is a very rare but very serious disease, affecting about 1,000 people worldwide.", data);
		createUser("octaviabutler", "octaviabutler@octaviabutler.com", false);
		createUser("test", "test@test.com", true);
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
		users.put(email, data);
		users.put(id, data);
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
		
		V2WikiPage page = createWikiPage(user);
		community.setWelcomePageWikiId(page.getId());
		
		page = createWikiPage(user);
		community.setIndexPageWikiId(page.getId());
		
		communities.put(community.getId(), community);
		
		Team team = new Team();
		team.setId("synTeam" + community.getId());
		community.setTeamId(team.getId());
		teams.put(team.getId(), team);
		joinUserToThisCommunityTeam(team, user.getProfile().getOwnerId());
		logger.info("The team ID for " + community.getName() + " is " + team.getId());
		
		// Set user as an editor for this community
		makeAccessControlList(user.getProfile().getOwnerId(), community.getId(), ACCESS_TYPE.UPDATE, ACCESS_TYPE.CHANGE_PERMISSIONS);
		return community;
	}

	private V2WikiPage createWikiPage(UserSessionData user) {
		V2WikiPage page = new V2WikiPage();
		page.setCreatedBy(user.getProfile().getEmail());
		page.setCreatedOn(new Date());
		page.setId(newId());
		wikiPages.put(page.getId(), page);
		return page;
	}
	
	private void makeAccessControlList(String userOwnerId, String entityId, ACCESS_TYPE... types) {
		AccessControlList acl = new AccessControlList();
		acl.setId(entityId);
		ResourceAccess ra = new ResourceAccess();
		if (types != null) {
			ra.setAccessType( Sets.newHashSet(types) );
		}
		// Cannot set long id, so setting this directly in the stub implementation in a way where 
		// we correctly retrieve the acls for a user/object combo
		// ra.setPrincipalId();
		acl.setResourceAccess( Sets.newHashSet(ra) );
		logger.info("Setting an ACL for: " + acl.getId()+":"+userOwnerId);
		acls.put(acl.getId()+":"+userOwnerId, acl);
	}

	private String newId() {
		return Integer.toString(++idCount);
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
		Community community = communities.get(communityId);
		if (community == null) {
			throw new SynapseException(new NotFoundException());
		}
		return community;
	}

	@Override
	public Community updateCommunity(Community community) throws SynapseException {
		Community cty = communities.get(community.getId());
		if (cty == null) {
			throw new SynapseNotFoundException();
		}
		communities.put(community.getId(), community);
		return community;
	}

	@Override
	public void deleteCommunity(String communityId) throws SynapseException {
		if (communityId == null || !communities.containsKey(communityId)) {
			throw new SynapseException(new NotFoundException("Could not find that community"));
		}
		communities.remove(communityId);
	}
	
	@Override
	public void joinCommunity(String communityId) throws SynapseException {
		Community community = communities.get(communityId);
		Team team = teams.get(community.getTeamId());
		joinUserToThisCommunityTeam(team, currentUserData.getProfile().getOwnerId());
	}
	
	@Override
	public void leaveCommunity(String communityId) throws SynapseException {
		Community community = communities.get(communityId);
		Team team = teams.get(community.getTeamId());
		removeUserFromCommunityTeam(team, currentUserData.getProfile().getOwnerId());
	}
	
	private void joinUserToThisCommunityTeam(Team team, String userId) {
		Set<String> memberSet = memberships.get(team);
		if (memberSet == null) {
			memberSet = new HashSet<>();
			memberships.put(team, memberSet);
		}
		memberSet.add(userId);
	}

	private void removeUserFromCommunityTeam(Team team, String userId) {
		Set<String> memberSet = memberships.get(team);
		memberSet.remove(userId);
	}
	@Override
	public StackStatus getCurrentStackStatus() throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getRepoEndpoint() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setRepositoryEndpoint(String repoEndpoint) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setAuthEndpoint(String authEndpoint) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getAuthEndpoint() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setFileEndpoint(String fileEndpoint) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getFileEndpoint() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AttachmentData uploadAttachmentToSynapse(String entityId, File temp, String fileName)
			throws JSONObjectAdapterException, SynapseException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Entity getEntityById(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends Entity> T putEntity(T entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PresignedUrl waitForPreviewToBeCreated(String entityId, String tokenId, int maxTimeOut)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PresignedUrl createAttachmentPresignedUrl(String entityId, String tokenId) throws SynapseException,
			JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getWikiAttachmentPreviewTemporaryUrl(WikiPageKey properKey, String fileName)
			throws ClientProtocolException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getWikiAttachmentTemporaryUrl(WikiPageKey properKey, String fileName) throws ClientProtocolException,
			IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Session login(String username, String password) throws SynapseException {
		UserSessionData data = users.get(username);
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
	public boolean revalidateSession() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public <T extends Entity> T createEntity(T entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public JSONObject createJSONObject(String uri, JSONObject entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public SearchResults search(SearchQuery searchQuery) throws SynapseException, UnsupportedEncodingException,
			JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getFileEntityPreviewTemporaryUrlForCurrentVersion(String entityId) throws ClientProtocolException,
			MalformedURLException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getFileEntityTemporaryUrlForCurrentVersion(String entityId) throws ClientProtocolException,
			MalformedURLException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getFileEntityPreviewTemporaryUrlForVersion(String entityId, Long versionNumber)
			throws ClientProtocolException, MalformedURLException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getFileEntityTemporaryUrlForVersion(String entityId, Long versionNumber) throws ClientProtocolException,
			MalformedURLException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public S3FileHandle createFileHandle(File temp, String contentType) throws SynapseException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public WikiPage getWikiPage(WikiPageKey properKey) throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public VariableContentPaginatedResults<AccessRequirement> getAccessRequirements(
			RestrictableObjectDescriptor subjectId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public WikiPage updateWikiPage(String ownerId, ObjectType ownerType, WikiPage toUpdate)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setRequestProfile(boolean request) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public JSONObject getProfileData() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getUserName() {
		if (currentUserData != null && currentUserData.getProfile() != null) {
			return currentUserData.getProfile().getDisplayName();
		}
		return null;
	}

	@Override
	public void setUserName(String userName) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getApiKey() {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setApiKey(String apiKey) {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends Entity> T createEntity(T entity, String activityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends JSONEntity> T createJSONEntity(String uri, T entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityBundle createEntityBundle(EntityBundleCreate ebc) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityBundle createEntityBundle(EntityBundleCreate ebc, String activityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityBundle updateEntityBundle(String entityId, EntityBundleCreate ebc) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityBundle updateEntityBundle(String entityId, EntityBundleCreate ebc, String activityId)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Entity getEntityByIdForVersion(String entityId, Long versionNumber) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityBundle getEntityBundle(String entityId, int partsMask) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityBundle getEntityBundle(String entityId, Long versionNumber, int partsMask) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<VersionInfo> getEntityVersions(String entityId, int offset, int limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AccessControlList getACL(String entityId) throws SynapseException {
		if (currentUserData == null || currentUserData.getProfile() == null) {
			logger.error("No user, no ACLs will be returned");
			// Wonder if this is how it really works
			return new AccessControlList();
		}
		logger.info("Looking for an ACL for: " + entityId+":"+currentUserData.getProfile().getOwnerId());
		return acls.get(entityId+":"+currentUserData.getProfile().getOwnerId());		
	}

	@Override
	public EntityHeader getEntityBenefactor(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public UserProfile getMyProfile() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void updateMyProfile(UserProfile userProfile) throws SynapseException {
		UserSessionData data = users.get(userProfile.getOwnerId());
		data.setProfile(userProfile);		
	}

	@Override
	public UserProfile getUserProfile(String ownerId) throws SynapseException {
		logger.info("--> getUserProfile: " + ownerId);
		return users.get(ownerId).getProfile();
	}

	@Override
	public UserGroupHeaderResponsePage getUserGroupHeadersByIds(List<String> ids) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public UserGroupHeaderResponsePage getUserGroupHeadersByPrefix(String prefix) throws SynapseException,
			UnsupportedEncodingException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AccessControlList updateACL(AccessControlList acl) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AccessControlList updateACL(AccessControlList acl, boolean recursive) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteACL(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AccessControlList createACL(AccessControlList acl) throws SynapseException {
		acls.put(acl.getId()+":"+currentUserData.getProfile().getOwnerId(), acl);
		return acl;		
	}

	@Override
	public PaginatedResults<UserProfile> getUsers(int offset, int limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<UserGroup> getGroups(int offset, int limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public boolean canAccess(String entityId, ACCESS_TYPE accessType) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean canAccess(String id, ObjectType type, ACCESS_TYPE accessType) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public UserEntityPermissions getUsersEntityPermissions(String entityId) throws SynapseException {
		// For now, the owners of the community have edit permissions, and that's the creator.
		UserEntityPermissions permits = new UserEntityPermissions();
		Community community = communities.get(entityId);
		if (community != null && community.getCreatedBy().equals(currentUserData.getProfile().getEmail())) {
			permits.setCanEdit(true);
			permits.setCanChangePermissions(true);
		} else {
			permits.setCanEdit(false);
			permits.setCanChangePermissions(false);
		}
		return permits;
	}

	@Override
	public Annotations getAnnotations(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Annotations updateAnnotations(String entityId, Annotations updated) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends AccessRequirement> T createAccessRequirement(T ar) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public ACTAccessRequirement createLockAccessRequirement(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public VariableContentPaginatedResults<AccessRequirement> getUnmetAccessRequirements(
			RestrictableObjectDescriptor subjectId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends AccessApproval> T createAccessApproval(T aa) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public JSONObject getEntity(String uri) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends JSONEntity> T getEntity(String entityId, Class<? extends T> clazz) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteAccessRequirement(Long arId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends Entity> T putEntity(T entity, String activityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends Entity> void deleteEntity(T entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends Entity> void deleteAndPurgeEntity(T entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteEntityById(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteAndPurgeEntityById(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public <T extends Entity> void deleteEntityVersion(T entity, Long versionNumber) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteEntityVersionById(String entityId, Long versionNumber) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityPath getEntityPath(Entity entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityPath getEntityPath(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public BatchResults<EntityHeader> getEntityTypeBatch(List<String> entityIds) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public BatchResults<EntityHeader> getEntityHeaderBatch(List<Reference> references) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<EntityHeader> getEntityReferencedBy(Entity entity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<EntityHeader> getEntityReferencedBy(String entityId, String targetVersion)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public JSONObject query(String query) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public FileHandleResults createFileHandles(List<File> files) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public ChunkedFileToken createChunkedFileUploadToken(CreateChunkedFileTokenRequest ccftr) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL createChunkedPresignedUrl(ChunkRequest chunkRequest) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String putFileToURL(URL url, File file, String contentType) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public ChunkResult addChunkToFile(ChunkRequest chunkRequest) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public S3FileHandle completeChunkFileUpload(CompleteChunkedFileRequest request) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public UploadDaemonStatus startUploadDeamon(CompleteAllChunksRequest cacr) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public UploadDaemonStatus getCompleteUploadDaemonStatus(String daemonId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public ExternalFileHandle createExternalFileHandle(ExternalFileHandle efh) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public FileHandle getRawFileHandle(String fileHandleId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteFileHandle(String fileHandleId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void clearPreview(String fileHandleId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public WikiPage createWikiPage(String ownerId, ObjectType ownerType, WikiPage toCreate)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public WikiPage getRootWikiPage(String ownerId, ObjectType ownerType) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public FileHandleResults getWikiAttachmenthHandles(WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadWikiAttachment(WikiPageKey key, String fileName) throws ClientProtocolException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadWikiAttachmentPreview(WikiPageKey key, String fileName) throws ClientProtocolException,
			FileNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteWikiPage(WikiPageKey key) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<WikiHeader> getWikiHeaderTree(String ownerId, ObjectType ownerType)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public FileHandleResults getEntityFileHandlesForCurrentVersion(String entityId) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public FileHandleResults getEntityFileHandlesForVersion(String entityId, Long versionNumber)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public V2WikiPage createV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage toCreate)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public V2WikiPage getV2WikiPage(WikiPageKey key) throws JSONObjectAdapterException, SynapseException {
		String realId = key.getWikiPageId();
		if (!wikiPages.containsKey(realId)) {
			throw new SynapseException("Wiki page not found");
		}
		return wikiPages.get(realId);
	}

	@Override
	public V2WikiPage updateV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage toUpdate)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public V2WikiPage restoreV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage toUpdate, Long versionToRestore)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public V2WikiPage getV2RootWikiPage(String ownerId, ObjectType ownerType) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public FileHandleResults getV2WikiAttachmentHandles(WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadV2WikiAttachment(WikiPageKey key, String fileName) throws ClientProtocolException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadV2WikiAttachmentPreview(WikiPageKey key, String fileName) throws ClientProtocolException,
			FileNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getV2WikiAttachmentPreviewTemporaryUrl(WikiPageKey key, String fileName) throws ClientProtocolException,
			IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getV2WikiAttachmentTemporaryUrl(WikiPageKey key, String fileName) throws ClientProtocolException,
			IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteV2WikiPage(WikiPageKey key) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<V2WikiHeader> getV2WikiHeaderTree(String ownerId, ObjectType ownerType)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<V2WikiHistorySnapshot> getV2WikiHistory(WikiPageKey key, Long limit, Long offset)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadLocationableFromSynapse(Locationable locationable) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadLocationableFromSynapse(Locationable locationable, File destinationFile)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadFromSynapse(LocationData location, String md5, File destinationFile) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public File downloadFromSynapse(String path, String md5, File destinationFile) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Locationable uploadLocationableToSynapse(Locationable locationable, File dataFile) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Locationable uploadLocationableToSynapse(Locationable locationable, File dataFile, String md5)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Locationable updateExternalLocationableToSynapse(Locationable locationable, String externalUrl)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Locationable updateExternalLocationableToSynapse(Locationable locationable, String externalUrl, String md5)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AttachmentData uploadAttachmentToSynapse(String entityId, File dataFile) throws JSONObjectAdapterException,
			SynapseException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AttachmentData uploadUserProfileAttachmentToSynapse(String userId, File dataFile, String fileName)
			throws JSONObjectAdapterException, SynapseException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AttachmentData uploadAttachmentToSynapse(String id, AttachmentType attachmentType, File dataFile,
			String fileName) throws JSONObjectAdapterException, SynapseException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PresignedUrl createUserProfileAttachmentPresignedUrl(String id, String tokenOrPreviewId)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PresignedUrl createAttachmentPresignedUrl(String id, AttachmentType attachmentType, String tokenOrPreviewId)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PresignedUrl waitForUserProfilePreviewToBeCreated(String userId, String tokenOrPreviewId, int timeout)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PresignedUrl waitForPreviewToBeCreated(String id, AttachmentType type, String tokenOrPreviewId, int timeout)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void downloadEntityAttachment(String entityId, AttachmentData attachmentData, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void downloadUserProfileAttachment(String userId, AttachmentData attachmentData, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void downloadAttachment(String id, AttachmentType type, AttachmentData attachmentData, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void downloadEntityAttachmentPreview(String entityId, String previewId, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void downloadUserProfileAttachmentPreview(String userId, String previewId, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void downloadAttachmentPreview(String id, AttachmentType type, String previewId, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public S3AttachmentToken createAttachmentS3Token(String id, AttachmentType attachmentType, S3AttachmentToken token)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String getSynapseTermsOfUse() throws SynapseException {
		return "<p>These are the Stub terms of use.</p>";
	}

	@Override
	public MessageToUser sendMessage(MessageToUser message) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<MessageBundle> getInbox(List<MessageStatusType> inboxFilter, MessageSortBy orderBy,
			Boolean descending, long limit, long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<MessageToUser> getOutbox(MessageSortBy orderBy, Boolean descending, long limit, long offset)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public MessageToUser getMessage(String messageId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public MessageToUser forwardMessage(String messageId, MessageRecipientSet recipients) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<MessageToUser> getConversation(String associatedMessageId, MessageSortBy orderBy,
			Boolean descending, long limit, long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void updateMessageStatus(MessageStatus status) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteMessage(String messageId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Long getChildCount(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public SynapseVersionInfo getVersionInfo() throws SynapseException, JSONObjectAdapterException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Set<String> getAllUserAndGroupIds() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Activity getActivityForEntity(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Activity getActivityForEntityVersion(String entityId, Long versionNumber) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Activity setActivityForEntity(String entityId, String activityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteGeneratedByForEntity(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Activity createActivity(Activity activity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Activity getActivity(String activityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Activity putActivity(Activity activity) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteActivity(String activityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Reference> getEntitiesGeneratedBy(String activityId, Integer limit, Integer offset)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityIdList getDescendants(String nodeId, int pageSize, String lastDescIdExcl) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityIdList getDescendants(String nodeId, int generation, int pageSize, String lastDescIdExcl)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Evaluation createEvaluation(Evaluation eval) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Evaluation getEvaluation(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Evaluation> getEvaluationByContentSource(String id, int offset, int limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Evaluation> getEvaluationsPaginated(int offset, int limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Evaluation> getAvailableEvaluationsPaginated(int offset, int limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Long getEvaluationCount() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Evaluation findEvaluation(String name) throws SynapseException, UnsupportedEncodingException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Evaluation updateEvaluation(Evaluation eval) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteEvaluation(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Participant createParticipant(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Participant getParticipant(String evalId, String principalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteParticipant(String evalId, String principalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Participant> getAllParticipants(String s, long offset, long limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Long getParticipantCount(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Submission createSubmission(Submission sub, String etag) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Submission getSubmission(String subId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public SubmissionStatus getSubmissionStatus(String subId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public SubmissionStatus updateSubmissionStatus(SubmissionStatus status) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteSubmission(String subId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Submission> getAllSubmissions(String evalId, long offset, long limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<SubmissionStatus> getAllSubmissionStatuses(String evalId, long offset, long limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<SubmissionBundle> getAllSubmissionBundles(String evalId, long offset, long limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Submission> getAllSubmissionsByStatus(String evalId, SubmissionStatusEnum status,
			long offset, long limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<SubmissionStatus> getAllSubmissionStatusesByStatus(String evalId,
			SubmissionStatusEnum status, long offset, long limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<SubmissionBundle> getAllSubmissionBundlesByStatus(String evalId,
			SubmissionStatusEnum status, long offset, long limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Submission> getMySubmissions(String evalId, long offset, long limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<SubmissionBundle> getMySubmissionBundles(String evalId, long offset, long limit)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getFileTemporaryUrlForSubmissionFileHandle(String submissionId, String fileHandleId)
			throws ClientProtocolException, MalformedURLException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Long getSubmissionCount(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public UserEvaluationState getUserEvaluationState(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public QueryTableResults queryEvaluation(String query) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public StorageUsageSummaryList getStorageUsageSummary(List<StorageUsageDimension> aggregation)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void moveToTrash(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void restoreFromTrash(String entityId, String newParentId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<TrashedEntity> viewTrashForUser(long offset, long limit) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void purgeTrashForUser(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void purgeTrashForUser() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public EntityHeader addFavorite(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void removeFavorite(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<EntityHeader> getFavorites(Integer limit, Integer offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void createEntityDoi(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void createEntityDoi(String entityId, Long entityVersion) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Doi getEntityDoi(String entityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Doi getEntityDoi(String s, Long entityVersion) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public List<EntityHeader> getEntityHeaderByMd5(String md5) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public String retrieveApiKey() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void invalidateApiKey() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AccessControlList updateEvaluationAcl(AccessControlList acl) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public AccessControlList getEvaluationAcl(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public UserEvaluationPermissions getUserEvaluationPermissions(String evalId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public RowReferenceSet appendRowsToTable(RowSet toAppend) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public ColumnModel createColumnModel(ColumnModel model) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public ColumnModel getColumnModel(String columnId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Team createTeam(Team team) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Team getTeam(String id) throws SynapseException {
		if (!teams.keySet().contains(id)) {
			throw new SynapseException("Team not found");
		}
		return teams.get(id);
	}

	@Override
	public PaginatedResults<Team> getTeams(String fragment, long limit, long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<Team> getTeamsForUser(String memberId, long limit, long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public URL getTeamIcon(String teamId, Boolean redirect) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public Team updateTeam(Team team) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteTeam(String teamId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void addTeamMember(String teamId, String memberId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<TeamMember> getTeamMembers(String teamId, String fragment, long limit, long offset)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void removeTeamMember(String teamId, String memberId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void setTeamMemberPermissions(String teamId, String memberId, boolean isAdmin) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public TeamMembershipStatus getTeamMembershipStatus(String teamId, String userId) throws SynapseException {
		TeamMembershipStatus status = new TeamMembershipStatus();
		status.setTeamId(teamId);
		status.setUserId(userId);
		Team team = teams.get(teamId);
		if (team != null) {
			Set<String> members = memberships.get(team);
			status.setIsMember( (members != null && members.contains(userId)) );
		}
		return status;
	}
	
	@Override
	public MembershipInvtnSubmission createMembershipInvitation(MembershipInvtnSubmission invitation)
			throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public MembershipInvtnSubmission getMembershipInvitation(String invitationId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<MembershipInvitation> getOpenMembershipInvitations(String memberId, String teamId,
			long limit, long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteMembershipInvitation(String invitationId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public MembershipRqstSubmission createMembershipRequest(MembershipRqstSubmission request) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public MembershipRqstSubmission getMembershipRequest(String requestId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedResults<MembershipRequest> getOpenMembershipRequests(String teamId, String requestorId, long limit,
			long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void deleteMembershipRequest(String requestId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void updateTeamSearchCache() throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public List<ColumnModel> getColumnModelsForTableEntity(String tableEntityId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public PaginatedColumnModels listColumnModels(String prefix, Long limit, Long offset) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
		
	}

	@Override
	public void createUser(NewUser user) throws SynapseException {
		throw new IllegalArgumentException("Do not use this method from bridge");
	}

	@Override
	public void createUser(NewUser user, OriginatingClient originClient) throws SynapseException {
		if (users.get(user.getEmail()) != null) {
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
		users.put(user.getEmail(), data);
		users.put(USER_ID, data);		
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
	public void sendPasswordResetEmail(String email, OriginatingClient originClient) throws SynapseException {
		// noop
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString) throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean createUserIfNecessary)
			throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean createUserIfNecessary,
			OriginatingClient originClient) throws SynapseException {
		// We'd like to test three scenarios here:
		// 1. Brand new user, needs to sign TOU
		// 2. Returning user who hasn't signed TOU?
		// 3. Returning user who should just be logged in to default start page
		
		// The user will always be Tim Powers, session AAA.
		currentUserData = users.get(timPowersId);
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

	// Get communities FOR THIS USER ALONE.
	@Override
	public PaginatedResults<Community> getCommunities(long limit, long offset) throws SynapseException {
		List<Community> memberCommunities = new ArrayList<>();
		if (currentUserData != null && currentUserData.getProfile() != null) {
			for (Community community : communities.values()) {
				Team team = teams.get(community.getId());
				Set<String> members = memberships.get(team);
				if (members != null && members.contains(currentUserData.getProfile().getOwnerId())) {
					memberCommunities.add(community);
				}
			}
		}
		PaginatedResults<Community> results = new PaginatedResults<>();
		results.setResults(memberCommunities);
		results.setTotalNumberOfResults(results.getResults().size());
		return results;
	}

	@Override
	public PaginatedResults<Community> getAllCommunities(long limit, long offset) throws SynapseException {
		PaginatedResults<Community> results = new PaginatedResults<>();
		results.setResults(paginate(new ArrayList<Community>(communities.values()), limit, offset));
		results.setTotalNumberOfResults(results.getResults().size());
		return results;
	}

	@Override
	public PaginatedResults<UserGroupHeader> getCommunityMembers(String communityId, long limit, long offset)
			throws SynapseException {
		Community community = communities.get(communityId);
		Team team = teams.get(community.getId());
		Set<String> members = memberships.get(team);
		
		List<UserGroupHeader> headers = new ArrayList<>();
		for (String memberId : members) {
			UserProfile profile = users.get(memberId).getProfile();
			
			UserGroupHeader header = new UserGroupHeader();
			header.setDisplayName(profile.getDisplayName());
			header.setEmail(profile.getEmail());
			header.setFirstName(profile.getFirstName());
			header.setIsIndividual(true);
			header.setLastName(profile.getLastName());
			header.setOwnerId(profile.getOwnerId());
			headers.add(header);
		}
		PaginatedResults<UserGroupHeader> results = new PaginatedResults<>();
		results.setResults(paginate(headers, limit, offset));
		results.setTotalNumberOfResults(results.getResults().size());
		return results;
	}
	
	@Override
	public void addCommunityAdmin(String communityId, String memberName) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void removeCommunityAdmin(String communityId, String memberName) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	private <T> List<T> paginate(List<T> list, long limit, long offset) {
		int start = (int)offset;
		int end = (int)limit;
		int lastIndex = list.size()-1;
		start = (start > lastIndex) ? lastIndex : (start < 0) ? 0 : start;
		end = (end > lastIndex) ? lastIndex : (end < 0) ? 0 : end;
		logger.info("Retrieving from " + start + " to " + (end+1) + " of " + list.size() + " items.");
		return list.subList(start, end+1);
	}

	@Override
	public V2WikiPage getVersionOfV2WikiPage(WikiPageKey key, Long version) throws JSONObjectAdapterException,
			SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public FileHandleResults getVersionOfV2WikiAttachmentHandles(WikiPageKey key, Long version)
			throws JSONObjectAdapterException, SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public File downloadV2WikiMarkdown(WikiPageKey key) throws ClientProtocolException, FileNotFoundException,
			IOException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public File downloadVersionOfV2WikiMarkdown(WikiPageKey key, Long version) throws ClientProtocolException,
			FileNotFoundException, IOException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public TeamMember getTeamMember(String teamId, String memberId) throws SynapseException {
		throw new UnsupportedOperationException("Not implemented.");
	}

}
