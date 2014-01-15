package org.sagebionetworks.bridge.webapp.integration;

import java.util.Collections;
import java.util.List;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.webapp.specs.CompleteBloodCount;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.client.BaseClient;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.BridgeClientImpl;
import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseAdminClientImpl;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.auth.NewIntegrationTestUser;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.RowSet;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class SageUserBoostrap {

	private static final RowSet EMPTY_ROW_SET;

	static {
		EMPTY_ROW_SET = new RowSet();
		EMPTY_ROW_SET.setHeaders(Collections.<String> emptyList());
		EMPTY_ROW_SET.setRows(Collections.<Row> emptyList());
	}

	public static void main(String[] args) {
		SageUserBoostrap bootstrapper = new SageUserBoostrap();
		bootstrapper.createContent();
	}

	public void createContent() {
		try {
			SynapseAdminClient adminSynapse = new SynapseAdminClientImpl();
			setEndpoints(adminSynapse);
			adminSynapse.setUserName(StackConfiguration.getMigrationAdminUsername());
			adminSynapse.setApiKey(StackConfiguration.getMigrationAdminAPIKey());

			createUser(adminSynapse, "octaviabutler", "octaviabutler@octaviabutler.com", false);
			createUser(adminSynapse, "timpowers", "timpowers@timpowers.com", true);
			createUser(adminSynapse, "test", "test@test.com", true);

			SynapseClient synapse = new SynapseClientImpl();
			setEndpoints(synapse);
			
			Session session = synapse.login("test", "password");
			session.setAcceptsTermsOfUse(true);
			synapse.signTermsOfUse(session.getSessionToken(), true);
			synapse.setSessionToken(session.getSessionToken());
			
			BridgeClient bridge = new BridgeClientImpl(synapse);
			setEndpoints(bridge);
			
			createData(bridge, "Sleep Tracker", "Daily sleep check in", ParticipantDataRepeatType.REPEATED,
					"0 0 4 * * ? *", "Sleep time", "sleep-time-slider");
			createData(bridge, "Rest Tracker", "Daily rest check in", ParticipantDataRepeatType.REPEATED,
					"0 0 4 * * ? *", "Rest time", "sleep-time-slider");
			createData(bridge, "Mood Tracker", "Mood check in", ParticipantDataRepeatType.ALWAYS, null, "Mind",
					"mood-slider", "Body", "mood-slider");
			createData(bridge, "Medications", "Medications", ParticipantDataRepeatType.IF_CHANGED, null, "xx",
					"string", "yy", "string");
			createData(bridge, "Personal Info", "Personal information", ParticipantDataRepeatType.ONCE, null, "Name",
					"string", "Address", "string");

			CompleteBloodCount cbc = new CompleteBloodCount();
			ParticipantDataDescriptor desc = ParticipantDataUtils.getDescriptor(cbc);
			desc = bridge.createParticipantDataDescriptor(desc);
			List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(desc.getId(), cbc);
			for (ParticipantDataColumnDescriptor column : columns) {
				bridge.createParticipantDataColumnDescriptor(column);
			}
			bridge.appendParticipantData(desc.getId(), EMPTY_ROW_SET);
			
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private void createData(BridgeClient bridge, String name, String description, ParticipantDataRepeatType repeatType,
			String repeatFrequency, String... cols) throws SynapseException {
		ParticipantDataDescriptor desc = new ParticipantDataDescriptor();
		desc.setDescription(description);
		desc.setName(name);
		desc.setRepeatType(repeatType);
		desc.setRepeatFrequency(repeatFrequency);
		desc = bridge.createParticipantDataDescriptor(desc);
		int index = 0;
		while (index < cols.length) {
			ParticipantDataColumnDescriptor col = new ParticipantDataColumnDescriptor();
			col.setParticipantDataDescriptorId(desc.getId());
			col.setName(cols[index++]);
			col.setDescription("");
			col.setType(cols[index++]);
			bridge.createParticipantDataColumnDescriptor(col);
		}
		bridge.appendParticipantData(desc.getId(), EMPTY_ROW_SET);
	}

	private void createUser(SynapseAdminClient client, String userName, String email, boolean acceptsTermsOfUse)
			throws SynapseException, JSONObjectAdapterException {

		try {
			NewIntegrationTestUser newUser = new NewIntegrationTestUser();
			newUser.setUsername(userName);
			newUser.setEmail(email);
			newUser.setPassword("password");
			client.createUser(newUser);
		} catch(Exception e) {
			System.out.println("User '" + userName + "' already exists");
		}
	}

	private void setEndpoints(BaseClient client) {
		if (client instanceof SynapseClient) {
			((SynapseClient) client).setAuthEndpoint(StackConfiguration.getAuthenticationServicePrivateEndpoint());
			((SynapseClient) client).setRepositoryEndpoint(StackConfiguration.getRepositoryServiceEndpoint());
			((SynapseClient) client).setFileEndpoint(StackConfiguration.getFileServiceEndpoint());
		}
		if (client instanceof BridgeClient) {
			((BridgeClient) client).setBridgeEndpoint(StackConfiguration.getBridgeServiceEndpoint());
		}
	}
}
