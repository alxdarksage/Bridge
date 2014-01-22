package org.sagebionetworks.bridge.webapp.integration;

import java.util.Collections;
import java.util.List;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.trackers.CompleteBloodCount;
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
			
			createData(bridge, "Sleep Tracker", "Daily sleep check in", ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *", "Sleep time",
					"sleep-time-slider", ParticipantDataColumnType.DOUBLE);
			createData(bridge, "Rest Tracker", "Daily rest check in", ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *", "Rest time",
					"sleep-time-slider", ParticipantDataColumnType.DOUBLE);
			createData(bridge, "Mood Tracker", "Mood check in", ParticipantDataRepeatType.ALWAYS, null, "Mind", "mood-slider",
					ParticipantDataColumnType.DOUBLE, "Body", "mood-slider", ParticipantDataColumnType.DOUBLE);
			createData(bridge, "Medication Tracker", "Medications", ParticipantDataRepeatType.IF_CHANGED, null, "xx", "string",
					ParticipantDataColumnType.STRING, "yy", "string", ParticipantDataColumnType.STRING);
			createData(bridge, "Personal Info Tracker", "Personal information", ParticipantDataRepeatType.ONCE, null, "Name", "string",
					ParticipantDataColumnType.STRING, "Address", "string", ParticipantDataColumnType.STRING);

			CompleteBloodCount cbc = new CompleteBloodCount();
			ParticipantDataDescriptor desc = ParticipantDataUtils.getDescriptor(cbc);
			desc = bridge.createParticipantDataDescriptor(desc);
			List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(desc.getId(), cbc);
			for (ParticipantDataColumnDescriptor column : columns) {
				bridge.createParticipantDataColumnDescriptor(column);
			}
			bridge.appendParticipantData(desc.getId(), Collections.<ParticipantDataRow> emptyList());
			
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private void createData(BridgeClient bridge, String name, String description, ParticipantDataRepeatType repeatType,
			String repeatFrequency, Object... cols) throws SynapseException {
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
			col.setName((String) cols[index++]);
			col.setDescription("");
			col.setType((String) cols[index++]);
			col.setColumnType((ParticipantDataColumnType) cols[index++]);
			bridge.createParticipantDataColumnDescriptor(col);
		}
		bridge.appendParticipantData(desc.getId(), Collections.<ParticipantDataRow> emptyList());
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
