package org.sagebionetworks.bridge.webapp;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.webapp.specs.CompleteBloodCount;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
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

import com.google.common.collect.Maps;

/**
 * This can be used to initialize a database for development, or it is run by a listener at 
 * startup when the stub version of the services is used (either for the UI tests, or for 
 * development). Same data is created through the clients, however implemented.  
 */
public class SageBootstrap {

	public interface ClientProvider {
		public SynapseAdminClient getAdminClient();
		public SynapseClient getSynapseClient();
		public BridgeClient getBridgeClient();
	}
	
	public static class IntegrationClientProvider implements ClientProvider {
		private SynapseClient synapse;
		private SynapseAdminClient admin;
		private BridgeClient bridge;
		
		public IntegrationClientProvider() {
			this.synapse = new SynapseClientImpl();
			setEndpoints(this.synapse);
			this.admin = new SynapseAdminClientImpl();
			setEndpoints(this.admin);
			this.admin.setUserName(StackConfiguration.getMigrationAdminUsername());
			this.admin.setApiKey(StackConfiguration.getMigrationAdminAPIKey());
			this.bridge = new BridgeClientImpl(this.synapse);
			setEndpoints(this.bridge);
		}
		
		@Override public SynapseAdminClient getAdminClient() { return this.admin; }
		@Override public SynapseClient getSynapseClient() { return this.synapse; }
		@Override public BridgeClient getBridgeClient() { return this.bridge; }
		
		private void setEndpoints(SynapseClient client) {
			client.setAuthEndpoint(StackConfiguration.getAuthenticationServicePrivateEndpoint());
			client.setRepositoryEndpoint(StackConfiguration.getRepositoryServiceEndpoint());
			client.setFileEndpoint(StackConfiguration.getFileServiceEndpoint());
		}		
		private void setEndpoints(BridgeClient client) {
			client.setBridgeEndpoint(StackConfiguration.getBridgeServiceEndpoint());
		}		
	}
	
	public static void main(String[] args) throws Exception {
		SageBootstrap bootstrapper = new SageBootstrap(new SageBootstrap.IntegrationClientProvider());
		bootstrapper.create();
	}
	
	private ClientProvider provider;
	
	private static final RowSet EMPTY_ROW_SET;
	static {
		EMPTY_ROW_SET = new RowSet();
		EMPTY_ROW_SET.setHeaders(Collections.<String> emptyList());
		EMPTY_ROW_SET.setRows(Collections.<Row> emptyList());
	}

	public SageBootstrap(ClientProvider provider) {
		this.provider = provider;
	}
	
	public void create() throws Exception {
		createUser(provider.getAdminClient(), provider.getSynapseClient(), "octaviabutler", "octaviabutler@octaviabutler.com", false);
		createUser(provider.getAdminClient(), provider.getSynapseClient(), "timpowers", "timpowers@timpowers.com", true);
		createUser(provider.getAdminClient(), provider.getSynapseClient(), "test", "test@test.com", true);
		
		SynapseClient client = provider.getSynapseClient();
		Session session = client.login("timpowers", "password");
		session.setAcceptsTermsOfUse(true);
		client.signTermsOfUse(session.getSessionToken(), true);
		client.setSessionToken(session.getSessionToken());
		
		// This should be propagated at this point to the bridge client which uses that Synapse client
		BridgeClient bridge = provider.getBridgeClient();
		createData(bridge, "Sleep Tracker", "Daily sleep check in", ParticipantDataRepeatType.REPEATED,
				"0 0 4 * * ? *", "Sleep time", "sleep-time-slider");
		createData(bridge, "Rest Tracker", "Daily rest check in", ParticipantDataRepeatType.REPEATED,
				"0 0 4 * * ? *", "Rest time", "sleep-time-slider");
		createData(bridge, "Mood Tracker", "Mood check in", ParticipantDataRepeatType.ALWAYS, null, "Mind",
				"mood-slider", "Body", "mood-slider");
		createData(bridge, "Medication Tracker", "Medications", ParticipantDataRepeatType.IF_CHANGED, null, "xx",
				"string", "yy", "string");
		createData(bridge, "Personal Info Tracker", "Personal information", ParticipantDataRepeatType.ONCE, null, "Name",
				"string", "Address", "string");
		
		CompleteBloodCount spec = new CompleteBloodCount();
		String trackerId = createData(bridge, spec);
		createDataEntry(bridge, trackerId, spec, "collectedOn", "2013-10-23", "rbc", "3.7", "rbc_units", "M/uL", "rbc_range_low", "4", "rbc_range_high", "4.9", "hb", "12.7", "hb_units", "dL", "hb_range_low", "11", "hb_range_high", "13.3", "hct", "37", "hct_units", "%", "hct_range_low", "32", "hct_range_high", "38", "mcv", "100", "mcv_units", "fL", "mcv_range_low", "75.9", "mcv_range_high", "86.5", "mch", "34.3", "mch_units", "pg", "mch_range_low", "25.4", "mch_range_high", "29.4", "rdw", "13.2", "rdw_units", "%", "rdw_range_low", "12.7", "rdw_range_high", "14.6", "ret_units", "%", "wbc", "3.6", "wbc_units", "K/uL", "wbc_range_low", "4.5", "wbc_range_high", "10.5", "wbc_diff_units", "%", "neutrophil", "30", "neutrophil_units", "%", "neutrophil_range_low", "36", "neutrophil_range_high", "74", "neutrophil_immature", "0", "neutrophil_immature_units", "%", "neutrophil_immature_range_low", "0", "neutrophil_immature_range_high", "1", "lymphocytes", "53", "lymphocytes_units", "%", "lymphocytes_range_low", "14", "lymphocytes_range_high", "48", "monocytes", "9", "monocytes_units", "%", "monocytes_range_low", "4", "monocytes_range_high", "9", "plt", "120", "plt_units", "K/uL", "plt_range_low", "140", "plt_range_high", "440", "mpv_units", "fL", "pdw_units", "%", "createdOn", "2014-01-14T09:57:53.723-08:00", "modifiedOn", "2014-01-14T09:57:53.723-08:00", "wbc (K/mcL)", "3.6", "rbc (M/mcL)", "3.7", "plt (K/mcL)", "120");
		createDataEntry(bridge, trackerId, spec, "collectedOn", "2013-12-23",  "rbc", "4.16",  "rbc_units", "10e12/L",  "rbc_range_low", "3.8",  "rbc_range_high", "5.2",  "hb", "13.9",  "hb_units", "dL",  "hb_range_low", "11.7",  "hb_range_high", "15.7",  "hct", "40",  "hct_units", "%",  "hct_range_low", "35",  "hct_range_high", "47",  "mcv", "96",  "mcv_units", "fL",  "mcv_range_low", "76",  "mcv_range_high", "100",  "mch", "33.4",  "mch_units", "pg",  "mch_range_low", "26.5",  "mch_range_high", "33",  "rdw", "12.8",  "rdw_units", "%",  "rdw_range_low", "10",  "rdw_range_high", "15",  "ret_units", "%",  "wbc", "11.1",  "wbc_units", "10e9/L",  "wbc_range_low", "4",  "wbc_range_high", "11",  "wbc_diff_units", "%",  "neutrophil", "87.8",  "neutrophil_units", "%",  "neutrophil_immature", ".3",  "neutrophil_immature_units", "%",  "lymphocytes", "3.7",  "lymphocytes_units", "%",  "monocytes", "7",  "monocytes_units", "%",  "plt", "193",  "plt_units", "10e9/L",  "plt_range_low", "150",  "plt_range_high", "450",  "mpv_units", "fL",  "pdw_units", "%",  "createdOn", "2014-01-14T10:17:33.488-08:00",  "modifiedOn", "2014-01-14T10:17:33.488-08:00",  "wbc (K/mcL)", "11.1",  "rbc (M/mcL)", "4.16",  "plt (K/mcL)", "193.0");
		createDataEntry(bridge, trackerId, spec, "collectedOn", "2012-11-06",  "rbc", "4.11",  "rbc_units", "M/uL",  "rbc_range_low", "4.63",  "rbc_range_high", "6.08",  "hb", "13.5",  "hb_units", "dL",  "hb_range_low", "13.7",  "hb_range_high", "17.5",  "hct", "39.1",  "hct_units", "%",  "hct_range_low", "40.1",  "hct_range_high", "51",  "mcv", "96.1",  "mcv_units", "fL",  "mcv_range_low", "79",  "mcv_range_high", "92.2",  "mch", "32.8",  "mch_units", "pg",  "mch_range_low", "25.7",  "mch_range_high", "32.2",  "rdw", "13.1",  "rdw_units", "%",  "rdw_range_low", "11.6",  "rdw_range_high", "14.4",  "ret", "1.39",  "ret_units", "%",  "ret_range_low", ".51",  "ret_range_high", "1.81",  "wbc", "4.89",  "wbc_units", "K/uL",  "wbc_range_low", "4.23",  "wbc_range_high", "9.07",  "wbc_diff_units", "%",  "neutrophil", "29.6",  "neutrophil_units", "%",  "neutrophil_range_low", "34",  "neutrophil_range_high", "67.9",  "neutrophil_immature_units", "%",  "lymphocytes", "65.3",  "lymphocytes_units", "%",  "lymphocytes_range_low", "21.8",  "lymphocytes_range_high", "53.1",  "monocytes", "4.1",  "monocytes_units", "%",  "monocytes_range_low", "5.3",  "monocytes_range_high", "12.2",  "plt", "132",  "plt_units", "K/uL",  "plt_range_low", "161",  "plt_range_high", "347",  "mpv", "11",  "mpv_units", "fL",  "mpv_range_low", "9.4",  "mpv_range_high", "12.4",  "pdw_units", "%",  "createdOn", "2014-01-14T10:31:02.697-08:00",  "modifiedOn", "2014-01-14T10:31:02.697-08:00",  "wbc (K/mcL)", "4.89",  "rbc (M/mcL)", "4.11",  "plt (K/mcL)", "132");
		createDataEntry(bridge, trackerId, spec, "collectedOn", "2013-09-26",  "rbc", "4.96",  "rbc_units", "M/uL",  "rbc_range_low", "4.2",  "rbc_range_high", "5.8",  "hb", "15.6",  "hb_units", "dL",  "hb_range_low", "13.2",  "hb_range_high", "17.1",  "hct", "48.3",  "hct_units", "%",  "hct_range_low", "38.5",  "hct_range_high", "50",  "mcv", "97.4",  "mcv_units", "fL",  "mcv_range_low", "80",  "mcv_range_high", "100",  "mch", "31.5",  "mch_units", "pg",  "mch_range_low", "27",  "mch_range_high", "33",  "rdw", "15.7",  "rdw_units", "%",  "rdw_range_low", "11",  "rdw_range_high", "15",  "ret", "0",  "ret_units", "%",  "wbc", "5.6",  "wbc_units", "K/uL",  "wbc_range_low", "3.8",  "wbc_range_high", "10.8",  "wbc_diff_units", "%",  "neutrophil", "42.1",  "neutrophil_units", "%",  "neutrophil_immature_units", "%",  "lymphocytes", "40.2",  "lymphocytes_units", "%",  "monocytes", "8.7",  "monocytes_units", "%",  "plt", "203",  "plt_units", "K/uL",  "plt_range_low", "140",  "plt_range_high", "400",  "mpv_units", "fL",  "pdw_units", "%",  "createdOn", "2014-01-14T11:00:00.501-08:00",  "modifiedOn", "2014-01-14T11:00:00.501-08:00",  "wbc (K/mcL)", "5.6",  "rbc (M/mcL)", "4.96",  "plt (K/mcL)", "203");

		createCommunity(bridge, "Fanconi Anemia", "This is a very rare but very serious disease, affecting about 1,000 people worldwide.");
	}
	
	private void createUser(SynapseAdminClient admin, SynapseClient synapse, String userName, String email, boolean acceptsTermsOfUse)
			throws SynapseException, JSONObjectAdapterException {
		try {
			NewIntegrationTestUser newUser = new NewIntegrationTestUser();
			newUser.setUsername(userName);
			newUser.setEmail(email);
			newUser.setPassword("password");
			admin.createUser(newUser);
			if (acceptsTermsOfUse) {
				Session session = synapse.login(userName, "password");
				synapse.signTermsOfUse(session.getSessionToken(), true);
			}
		} catch(Exception e) {
			System.out.println("User '" + userName + "' already exists");
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
	
	private String createData(BridgeClient bridge, Specification spec) throws Exception {
		ParticipantDataDescriptor desc = ParticipantDataUtils.getDescriptor(spec);
		desc = bridge.createParticipantDataDescriptor(desc);
		List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(desc.getId(), spec);
		for (ParticipantDataColumnDescriptor column : columns) {
			bridge.createParticipantDataColumnDescriptor(column);
		}
		return desc.getId();
	}
	
	private void createDataEntry(BridgeClient client, String trackerId, Specification spec, String... values) throws Exception {
		Map<String, String> map = Maps.newHashMap();
		for (int i=0; i < values.length; i+=2) {
			map.put(values[i], values[i+1]);
		}
		RowSet data = ParticipantDataUtils.getRowSetForCreate(spec, map);
		client.appendParticipantData(trackerId, data);
	}
	
	private void createCommunity(BridgeClient client, String name, String description) throws SynapseException {
		Community community = new Community();
		community.setName(name);
		community.setDescription(description);
		client.createCommunity(community);
	}
}