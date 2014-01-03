package org.sagebionetworks.bridge.webapp.integration;

import java.util.UUID;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseAdminClientImpl;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class SageUserBoostrap {

	public static void main(String[] args) {
		SageUserBoostrap bootstrapper = new SageUserBoostrap();
		bootstrapper.createUsers();
	}
	
	public void createUsers() {
		try {
			SynapseAdminClient adminSynapse = new SynapseAdminClientImpl();
			setEndpoints(adminSynapse);
			adminSynapse.setUserName(StackConfiguration.getMigrationAdminUsername());
			adminSynapse.setApiKey(StackConfiguration.getMigrationAdminAPIKey());

			SynapseClient synapse = new SynapseClientImpl();
			createUser(adminSynapse, synapse, "test", "test@test.com", true);
			createUser(adminSynapse, synapse, "timpowers", "timpowers@timpowers.com", true);
			createUser(adminSynapse, synapse, "octaviabutler", "octaviabutler@octaviabutler.com", false);

		} catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private Long createUser(SynapseAdminClient client, SynapseClient newUserClient, String userName, String email,
			boolean acceptsTermsOfUse) throws SynapseException, JSONObjectAdapterException {

		setEndpoints(newUserClient);

		Session session = new Session();
		session.setAcceptsTermsOfUse(acceptsTermsOfUse);
		session.setSessionToken(UUID.randomUUID().toString());
		newUserClient.setSessionToken(session.getSessionToken());
		
		UserProfile profile = new UserProfile();
		profile.setDisplayName(userName);
		profile.setEmail(email);
		profile.setUserName(userName);

		return client.createUser(email, "password", profile, session);
	}
	
	private void setEndpoints(SynapseClient client) {
		client.setAuthEndpoint(StackConfiguration.getAuthenticationServicePrivateEndpoint());
		client.setRepositoryEndpoint(StackConfiguration.getRepositoryServiceEndpoint());
		client.setFileEndpoint(StackConfiguration.getFileServiceEndpoint());
	}
	

}
