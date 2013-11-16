package org.sagebionetworks.bridge.webapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.sagebionetworks.client.SharedClientConnection;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.client.exceptions.SynapseTermsOfUseException;
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
import org.sagebionetworks.repo.model.RestrictableObjectDescriptor;
import org.sagebionetworks.repo.model.ServiceConstants.AttachmentType;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.TeamMembershipStatus;
import org.sagebionetworks.repo.model.TrashedEntity;
import org.sagebionetworks.repo.model.UserGroup;
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
import org.sagebionetworks.repo.model.provenance.Activity;
import org.sagebionetworks.repo.model.query.QueryTableResults;
import org.sagebionetworks.repo.model.search.SearchResults;
import org.sagebionetworks.repo.model.search.query.SearchQuery;
import org.sagebionetworks.repo.model.status.StackStatus;
import org.sagebionetworks.repo.model.storage.StorageUsageDimension;
import org.sagebionetworks.repo.model.storage.StorageUsageSummaryList;
import org.sagebionetworks.repo.model.table.ColumnModel;
import org.sagebionetworks.repo.model.table.PaginatedColumnModels;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiHistorySnapshot;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.repo.model.versionInfo.SynapseVersionInfo;
import org.sagebionetworks.repo.model.wiki.WikiHeader;
import org.sagebionetworks.repo.model.wiki.WikiPage;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class SynapseClientStub implements SynapseClient {
	
	private static final Logger logger = LogManager.getLogger(SynapseClientStub.class.getName());

	private String sessionToken;
	private Map<String, UserSessionData> users = new HashMap<>();
	// private List<String> userIds = new LinkedList<>();
	private Set<String> agreedTOUs = new HashSet<>();
	private int idCount;
	
	public String newId() {
		return Integer.toString(++idCount);
	}
	
	public SynapseClientStub() {
		System.out.println("---------------------------------------- BEING CREATED");
		
		UserProfile profile = new UserProfile();
		agreedTOUs.add("AAA"); // Tim powers has signed the terms of use.
		profile.setDisplayName("timpowers");
		profile.setEmail("timpowers@timpowers.com");
		profile.setOwnerId("AAA");
		profile.setUserName("timpowers@timpowers.com");
		UserSessionData data = new UserSessionData();
		data.setIsSSO(false);
		data.setSessionToken("MOCK_SESSION_TOKEN");
		data.setProfile(profile);
		users.put("timpowers@timpowers.com", data);
		users.put("AAA", data);
		
		profile = new UserProfile();
		// Octavia butler has not.
		profile.setDisplayName("octaviabutler");
		profile.setEmail("octaviabutler@octaviabutler.com");
		profile.setOwnerId("BBB");
		profile.setUserName("octaviabutler@octaviabutler.com");
		data = new UserSessionData();
		data.setIsSSO(false);
		data.setSessionToken("MOCK_SESSION_TOKEN");
		data.setProfile(profile);
		users.put("octaviabutler@octaviabutler.com", data);
		users.put("BBB", data);
	}
	
	@Override
	public StackStatus getCurrentStackStatus() throws SynapseException, JSONObjectAdapterException {
		return null;
	}

	@Override
	public String getRepoEndpoint() {
		return null;
	}

	@Override
	public void appendUserAgent(String toAppend) {
	}

	@Override
	public void setRepositoryEndpoint(String repoEndpoint) {
	}

	@Override
	public void setAuthEndpoint(String authEndpoint) {
	}

	@Override
	public String getAuthEndpoint() {
		return null;
	}

	@Override
	public void setFileEndpoint(String fileEndpoint) {
	}

	@Override
	public String getFileEndpoint() {
		return null;
	}
	
	@Override
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	@Override
	public AttachmentData uploadAttachmentToSynapse(String entityId, File temp, String fileName)
			throws JSONObjectAdapterException, SynapseException, IOException {
		return null;
	}

	@Override
	public Entity getEntityById(String entityId) throws SynapseException {
		return null;
	}

	@Override
	public <T extends Entity> T putEntity(T entity) throws SynapseException {
		return null;
	}

	@Override
	public PresignedUrl waitForPreviewToBeCreated(String entityId, String tokenId, int maxTimeOut)
			throws SynapseException, JSONObjectAdapterException {
		return null;
	}

	@Override
	public PresignedUrl createAttachmentPresignedUrl(String entityId, String tokenId) throws SynapseException,
			JSONObjectAdapterException {
		return null;
	}

	@Override
	public URL getWikiAttachmentPreviewTemporaryUrl(WikiPageKey properKey, String fileName)
			throws ClientProtocolException, IOException {
		return null;
	}

	@Override
	public URL getWikiAttachmentTemporaryUrl(WikiPageKey properKey, String fileName) throws ClientProtocolException,
			IOException {
		return null;
	}

	@Override
	public UserSessionData login(String username, String password) throws SynapseException {
		UserSessionData data = users.get(username);
		if (data == null) {
			throw new SynapseException();
		}
		if (!agreedTOUs.contains(data.getProfile().getOwnerId())) {
			throw new SynapseTermsOfUseException();
		}
		return data;
	}
	
	private UserSessionData currentUserData;

	@Override
	public UserSessionData login(String username, String password, boolean explicitlyAcceptsTermsOfUse) throws SynapseException {
		UserSessionData data = users.get(username);
		if (data == null) {
			throw new SynapseException();
		}
		// Keep octavia butler forever not-accepting the terms of use, for test purposes.
		if (explicitlyAcceptsTermsOfUse && !"octaviabutler@octaviabutler.com".equals(username)) {
			agreedTOUs.add(data.getProfile().getOwnerId());
		}
		if (!agreedTOUs.contains(data.getProfile().getOwnerId())) {
			throw new SynapseTermsOfUseException();
		}
		currentUserData = data;
		return data;
	}

	@Override
	public void loginWithNoProfile(String userName, String password) throws SynapseException {
		
	}

	@Override
	public void logout() throws SynapseException {
		sessionToken = null;
	}

	@Override
	public UserSessionData getUserSessionData() throws SynapseException {
		return currentUserData;
	}

	@Override
	public boolean revalidateSession() throws SynapseException {
		return true;
	}

	@Override
	public String getCurrentSessionToken() {
		return sessionToken;
	}

	@Override
	public <T extends Entity> T createEntity(T entity) throws SynapseException {
		return null;
	}

	@Override
	public JSONObject createJSONObject(String uri, JSONObject entity) throws SynapseException {
		return null;
	}

	@Override
	public SearchResults search(SearchQuery searchQuery) throws SynapseException, UnsupportedEncodingException,
			JSONObjectAdapterException {
		return null;
	}

	@Override
	public URL getFileEntityPreviewTemporaryUrlForCurrentVersion(String entityId) throws ClientProtocolException,
			MalformedURLException, IOException {
		return null;
	}

	@Override
	public URL getFileEntityTemporaryUrlForCurrentVersion(String entityId) throws ClientProtocolException,
			MalformedURLException, IOException {
		return null;
	}

	@Override
	public URL getFileEntityPreviewTemporaryUrlForVersion(String entityId, Long versionNumber)
			throws ClientProtocolException, MalformedURLException, IOException {
		return null;
	}

	@Override
	public URL getFileEntityTemporaryUrlForVersion(String entityId, Long versionNumber) throws ClientProtocolException,
			MalformedURLException, IOException {
		return null;
	}

	@Override
	public S3FileHandle createFileHandle(File temp, String contentType) throws SynapseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WikiPage getWikiPage(WikiPageKey properKey) throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VariableContentPaginatedResults<AccessRequirement> getAccessRequirements(
			RestrictableObjectDescriptor subjectId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WikiPage updateWikiPage(String ownerId, ObjectType ownerType, WikiPage toUpdate)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRequestProfile(boolean request) {
		// TODO Auto-generated method stub
	}

	@Override
	public JSONObject getProfileData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserName() {
		return null;
	}

	@Override
	public void setUserName(String userName) {

	}

	@Override
	public String getApiKey() {
		return null;
	}

	@Override
	public void setApiKey(String apiKey) {

	}

	@Override
	public <T extends Entity> T createEntity(T entity, String activityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JSONEntity> T createJSONEntity(String uri, T entity) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityBundle createEntityBundle(EntityBundleCreate ebc) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityBundle createEntityBundle(EntityBundleCreate ebc, String activityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityBundle updateEntityBundle(String entityId, EntityBundleCreate ebc) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityBundle updateEntityBundle(String entityId, EntityBundleCreate ebc, String activityId)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getEntityByIdForVersion(String entityId, Long versionNumber) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityBundle getEntityBundle(String entityId, int partsMask) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityBundle getEntityBundle(String entityId, Long versionNumber, int partsMask) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<VersionInfo> getEntityVersions(String entityId, int offset, int limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getACL(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityHeader getEntityBenefactor(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile getMyProfile() throws SynapseException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGroupHeaderResponsePage getUserGroupHeadersByPrefix(String prefix) throws SynapseException,
			UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList updateACL(AccessControlList acl) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList updateACL(AccessControlList acl, boolean recursive) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteACL(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public AccessControlList createACL(AccessControlList acl) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<UserProfile> getUsers(int offset, int limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<UserGroup> getGroups(int offset, int limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canAccess(String entityId, ACCESS_TYPE accessType) throws SynapseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAccess(String id, ObjectType type, ACCESS_TYPE accessType) throws SynapseException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UserEntityPermissions getUsersEntityPermissions(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotations getAnnotations(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotations updateAnnotations(String entityId, Annotations updated) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends AccessRequirement> T createAccessRequirement(T ar) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ACTAccessRequirement createLockAccessRequirement(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VariableContentPaginatedResults<AccessRequirement> getUnmetAccessRequirements(
			RestrictableObjectDescriptor subjectId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends AccessApproval> T createAccessApproval(T aa) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getEntity(String uri) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JSONEntity> T getEntity(String entityId, Class<? extends T> clazz) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAccessRequirement(Long arId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Entity> T putEntity(T entity, String activityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Entity> void deleteEntity(T entity) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Entity> void deleteAndPurgeEntity(T entity) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEntityById(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAndPurgeEntityById(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Entity> void deleteEntityVersion(T entity, Long versionNumber) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEntityVersionById(String entityId, Long versionNumber) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityPath getEntityPath(Entity entity) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityPath getEntityPath(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResults<EntityHeader> getEntityTypeBatch(List<String> entityIds) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchResults<EntityHeader> getEntityHeaderBatch(List<Reference> references) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<EntityHeader> getEntityReferencedBy(Entity entity) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<EntityHeader> getEntityReferencedBy(String entityId, String targetVersion)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject query(String query) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileHandleResults createFileHandles(List<File> files) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkedFileToken createChunkedFileUploadToken(CreateChunkedFileTokenRequest ccftr) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL createChunkedPresignedUrl(ChunkRequest chunkRequest) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String putFileToURL(URL url, File file, String contentType) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkResult addChunkToFile(ChunkRequest chunkRequest) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S3FileHandle completeChunkFileUpload(CompleteChunkedFileRequest request) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UploadDaemonStatus startUploadDeamon(CompleteAllChunksRequest cacr) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UploadDaemonStatus getCompleteUploadDaemonStatus(String daemonId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalFileHandle createExternalFileHandle(ExternalFileHandle efh) throws JSONObjectAdapterException,
			SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileHandle getRawFileHandle(String fileHandleId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFileHandle(String fileHandleId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearPreview(String fileHandleId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public WikiPage createWikiPage(String ownerId, ObjectType ownerType, WikiPage toCreate)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WikiPage getRootWikiPage(String ownerId, ObjectType ownerType) throws JSONObjectAdapterException,
			SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileHandleResults getWikiAttachmenthHandles(WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadWikiAttachment(WikiPageKey key, String fileName) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadWikiAttachmentPreview(WikiPageKey key, String fileName) throws ClientProtocolException,
			FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteWikiPage(WikiPageKey key) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<WikiHeader> getWikiHeaderTree(String ownerId, ObjectType ownerType)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileHandleResults getEntityFileHandlesForCurrentVersion(String entityId) throws JSONObjectAdapterException,
			SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileHandleResults getEntityFileHandlesForVersion(String entityId, Long versionNumber)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V2WikiPage createV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage toCreate)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V2WikiPage getV2WikiPage(WikiPageKey key) throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V2WikiPage updateV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage toUpdate)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V2WikiPage restoreV2WikiPage(String ownerId, ObjectType ownerType, V2WikiPage toUpdate, Long versionToRestore)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V2WikiPage getV2RootWikiPage(String ownerId, ObjectType ownerType) throws JSONObjectAdapterException,
			SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileHandleResults getV2WikiAttachmentHandles(WikiPageKey key) throws JSONObjectAdapterException,
			SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadV2WikiAttachment(WikiPageKey key, String fileName) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadV2WikiAttachmentPreview(WikiPageKey key, String fileName) throws ClientProtocolException,
			FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getV2WikiAttachmentPreviewTemporaryUrl(WikiPageKey key, String fileName) throws ClientProtocolException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getV2WikiAttachmentTemporaryUrl(WikiPageKey key, String fileName) throws ClientProtocolException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteV2WikiPage(WikiPageKey key) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<V2WikiHeader> getV2WikiHeaderTree(String ownerId, ObjectType ownerType)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<V2WikiHistorySnapshot> getV2WikiHistory(WikiPageKey key, Long limit, Long offset)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadLocationableFromSynapse(Locationable locationable) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadLocationableFromSynapse(Locationable locationable, File destinationFile)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadFromSynapse(LocationData location, String md5, File destinationFile) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File downloadFromSynapse(String path, String md5, File destinationFile) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locationable uploadLocationableToSynapse(Locationable locationable, File dataFile) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locationable uploadLocationableToSynapse(Locationable locationable, File dataFile, String md5)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locationable updateExternalLocationableToSynapse(Locationable locationable, String externalUrl)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locationable updateExternalLocationableToSynapse(Locationable locationable, String externalUrl, String md5)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttachmentData uploadAttachmentToSynapse(String entityId, File dataFile) throws JSONObjectAdapterException,
			SynapseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttachmentData uploadUserProfileAttachmentToSynapse(String userId, File dataFile, String fileName)
			throws JSONObjectAdapterException, SynapseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttachmentData uploadAttachmentToSynapse(String id, AttachmentType attachmentType, File dataFile,
			String fileName) throws JSONObjectAdapterException, SynapseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PresignedUrl createUserProfileAttachmentPresignedUrl(String id, String tokenOrPreviewId)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PresignedUrl createAttachmentPresignedUrl(String id, AttachmentType attachmentType, String tokenOrPreviewId)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PresignedUrl waitForUserProfilePreviewToBeCreated(String userId, String tokenOrPreviewId, int timeout)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PresignedUrl waitForPreviewToBeCreated(String id, AttachmentType type, String tokenOrPreviewId, int timeout)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void downloadEntityAttachment(String entityId, AttachmentData attachmentData, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadUserProfileAttachment(String userId, AttachmentData attachmentData, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadAttachment(String id, AttachmentType type, AttachmentData attachmentData, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadEntityAttachmentPreview(String entityId, String previewId, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadUserProfileAttachmentPreview(String userId, String previewId, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadAttachmentPreview(String id, AttachmentType type, String previewId, File destFile)
			throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub

	}

	@Override
	public S3AttachmentToken createAttachmentS3Token(String id, AttachmentType attachmentType, S3AttachmentToken token)
			throws JSONObjectAdapterException, SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSynapseTermsOfUse() throws SynapseException {
		return "<p>These are the Stub terms of use.</p>";
	}

	@Override
	public Long getChildCount(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SynapseVersionInfo getVersionInfo() throws SynapseException, JSONObjectAdapterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllUserAndGroupIds() throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity getActivityForEntity(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity getActivityForEntityVersion(String entityId, Long versionNumber) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity setActivityForEntity(String entityId, String activityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteGeneratedByForEntity(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public Activity createActivity(Activity activity) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity getActivity(String activityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity putActivity(Activity activity) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteActivity(String activityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<Reference> getEntitiesGeneratedBy(String activityId, Integer limit, Integer offset)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityIdList getDescendants(String nodeId, int pageSize, String lastDescIdExcl) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityIdList getDescendants(String nodeId, int generation, int pageSize, String lastDescIdExcl)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Evaluation createEvaluation(Evaluation eval) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Evaluation getEvaluation(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Evaluation> getEvaluationByContentSource(String id, int offset, int limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Evaluation> getEvaluationsPaginated(int offset, int limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Evaluation> getAvailableEvaluationsPaginated(int offset, int limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getEvaluationCount() throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Evaluation findEvaluation(String name) throws SynapseException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Evaluation updateEvaluation(Evaluation eval) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteEvaluation(String evalId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public Participant createParticipant(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Participant getParticipant(String evalId, String principalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteParticipant(String evalId, String principalId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<Participant> getAllParticipants(String s, long offset, long limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getParticipantCount(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Submission createSubmission(Submission sub, String etag) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Submission getSubmission(String subId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubmissionStatus getSubmissionStatus(String subId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubmissionStatus updateSubmissionStatus(SubmissionStatus status) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSubmission(String subId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<Submission> getAllSubmissions(String evalId, long offset, long limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<SubmissionStatus> getAllSubmissionStatuses(String evalId, long offset, long limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<SubmissionBundle> getAllSubmissionBundles(String evalId, long offset, long limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Submission> getAllSubmissionsByStatus(String evalId, SubmissionStatusEnum status,
			long offset, long limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<SubmissionStatus> getAllSubmissionStatusesByStatus(String evalId,
			SubmissionStatusEnum status, long offset, long limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<SubmissionBundle> getAllSubmissionBundlesByStatus(String evalId,
			SubmissionStatusEnum status, long offset, long limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Submission> getMySubmissions(String evalId, long offset, long limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<SubmissionBundle> getMySubmissionBundles(String evalId, long offset, long limit)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getFileTemporaryUrlForSubmissionFileHandle(String submissionId, String fileHandleId)
			throws ClientProtocolException, MalformedURLException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSubmissionCount(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserEvaluationState getUserEvaluationState(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryTableResults queryEvaluation(String query) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveToTrash(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreFromTrash(String entityId, String newParentId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<TrashedEntity> viewTrashForUser(long offset, long limit) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void purgeTrashForUser(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void purgeTrashForUser() throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityHeader addFavorite(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFavorite(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<EntityHeader> getFavorites(Integer limit, Integer offset) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createEntityDoi(String entityId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createEntityDoi(String entityId, Long entityVersion) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public Doi getEntityDoi(String entityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Doi getEntityDoi(String s, Long entityVersion) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EntityHeader> getEntityHeaderByMd5(String md5) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String retrieveApiKey() throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidateApiKey() throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public AccessControlList updateEvaluationAcl(AccessControlList acl) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessControlList getEvaluationAcl(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserEvaluationPermissions getUserEvaluationPermissions(String evalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ColumnModel createColumnModel(ColumnModel model) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ColumnModel getColumnModel(String columnId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Team createTeam(Team team) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Team getTeam(String id) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Team> getTeams(String fragment, long limit, long offset) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<Team> getTeamsForUser(String memberId, long limit, long offset) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getTeamIcon(String teamId, Boolean redirect) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Team updateTeam(Team team) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTeam(String teamId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTeamMember(String teamId, String memberId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginatedResults<TeamMember> getTeamMembers(String teamId, String fragment, long limit, long offset)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeTeamMember(String teamId, String memberId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTeamMemberPermissions(String teamId, String memberId, boolean isAdmin) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public TeamMembershipStatus getTeamMembershipStatus(String teamId, String principalId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MembershipInvtnSubmission createMembershipInvitation(MembershipInvtnSubmission invitation)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MembershipInvtnSubmission getMembershipInvitation(String invitationId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<MembershipInvitation> getOpenMembershipInvitations(String memberId, String teamId,
			long limit, long offset) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMembershipInvitation(String invitationId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public MembershipRqstSubmission createMembershipRequest(MembershipRqstSubmission request) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MembershipRqstSubmission getMembershipRequest(String requestId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResults<MembershipRequest> getOpenMembershipRequests(String teamId, String requestorId, long limit,
			long offset) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMembershipRequest(String requestId) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTeamSearchCache() throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ColumnModel> getColumnModelsForTableEntity(String tableEntityId) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedColumnModels listColumnModels(String prefix, Long limit, Long offset) throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void createUser(NewUser user) throws SynapseException {
		throw new RuntimeException("Do not use this method from bridge");
	}

	@Override
	public void resendPasswordEmail(String email) throws SynapseException {
		System.out.println("SENDING EMAIL TO RESET PASSWORD");
	}

	@Override
	public NewUser getAuthUserInfo() throws SynapseException {
		return null;
	}

	@Override
	public void changePassword(String newPassword) throws SynapseException {
		// We don't do anything, however.
	}

	@Override
	public void changePassword(String sessionToken, String newPassword) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeEmail(String sessionToken, String newPassword) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendPasswordResetEmail() throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendPasswordResetEmail(String email) throws SynapseException {
		// TODO Auto-generated method stub

	}

	@Override
	public Session passThroughOpenIDParameters(String queryString) throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean acceptsTermsOfUse) throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean acceptsTermsOfUse,
			Boolean createUserIfNecessary) throws SynapseException {
		throw new IllegalArgumentException("Don't use this API method in Bridge");
	}

	@Override
	public SharedClientConnection getSharedClientConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StorageUsageSummaryList getStorageUsageSummary(List<StorageUsageDimension> aggregation)
			throws SynapseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createUser(NewUser user, OriginatingClient originClient) throws SynapseException {
		String USER_ID = newId();
		
		UserProfile profile = new UserProfile();
		profile.setDisplayName(user.getDisplayName());
		profile.setEmail(user.getEmail());
		profile.setFirstName(user.getFirstName());
		profile.setLastName(user.getLastName());
		profile.setOwnerId(USER_ID);
		UserSessionData data = new UserSessionData();
		data.setSessionToken(USER_ID);
		data.setProfile(profile);
		// ARGH!
		users.put(user.getEmail(), data);
		users.put(USER_ID, data);
	}

	@Override
	public void resendPasswordEmail(String email, OriginatingClient originClient) throws SynapseException {
	}

	@Override
	public void sendPasswordResetEmail(OriginatingClient originClient) throws SynapseException {
	}

	@Override
	public void sendPasswordResetEmail(String email, OriginatingClient originClient) throws SynapseException {
	}

	@Override
	public Session passThroughOpenIDParameters(String queryString, Boolean acceptsTermsOfUse,
			Boolean createUserIfNecessary, OriginatingClient originClient) throws SynapseException {
		
		// We'd like to test three scenarios here:
		// 1. Brand new user, needs to sign TOU
		// 2. Returning user who hasn't signed TOU?
		// 3. Returning user who should just be logged in to default start page
		
		System.out.println(queryString);
		// The user will always be Tim Powers, session AAA.
		currentUserData = users.get("AAA");
		Session session = new Session();
		session.setSessionToken("AAA");
		return session;
	}

}
