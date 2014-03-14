package org.sagebionetworks.bridge.webapp;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.StackConfiguration;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnType;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataRepeatType;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.ParticipantDataStatusList;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.model.data.value.ValueFactory;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.trackers.CompleteBloodCount;
import org.sagebionetworks.bridge.webapp.specs.trackers.MoodTracker;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.BridgeClientImpl;
import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseAdminClientImpl;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.SynapseClientImpl;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.manager.team.TeamConstants;
import org.sagebionetworks.repo.model.DomainType;
import org.sagebionetworks.repo.model.UserSessionData;
import org.sagebionetworks.repo.model.auth.NewIntegrationTestUser;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.common.collect.Maps;

/**
 * This can be used to initialize a database for development, or it is run by a listener at 
 * startup when the stub version of the services is used (either for the UI tests, or for 
 * development). Same data is created through the clients, however implemented.  
 */
public class SageBootstrap {
	
	private static final Logger logger = LogManager.getLogger(SageBootstrap.class.getName());

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
			this.synapse = new SynapseClientImpl(DomainType.BRIDGE);
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
	
	public SageBootstrap(ClientProvider provider) {
		this.provider = provider;
	}
	
	public void create() throws Exception {
		createUser(false, provider.getAdminClient(), provider.getSynapseClient(), "octaviabutler", "octaviabutler@octaviabutler.com", false);
		createUser(true, provider.getAdminClient(), provider.getSynapseClient(), "timpowers", "timpowers@timpowers.com", true);
		createUser(true, provider.getAdminClient(), provider.getSynapseClient(), "test", "test@test.com", true);
		createUser(false, provider.getAdminClient(), provider.getSynapseClient(), "notanadmin", "notanadmin@test.com", true);
		createTrackers();
		
		BridgeClient bridge = provider.getBridgeClient();
		createCommunity(bridge, "Fanconi Anemia", "This is a very rare but very serious disease, affecting about 1,000 people worldwide.");
	}

	private void signInAsTimPowers() throws SynapseException {
		SynapseClient client = provider.getSynapseClient();
		Session session = client.login("timpowers", "password");
		client.signTermsOfUse(session.getSessionToken(), DomainType.BRIDGE, true);
		client.setSessionToken(session.getSessionToken());
		provider.getBridgeClient().setSessionToken(session.getSessionToken());
	}

	private static class Column {
		String name;
		String type;
		ParticipantDataColumnType columnType;

		public Column(String name, String type, ParticipantDataColumnType columnType) {
			this.name = name;
			this.type = type;
			this.columnType = columnType;
		}
	}

	/**
	 * This is called from the admin section as there's no way to create these on staging, otherwise.
	 * @throws SynapseException
	 * @throws Exception
	 */
	public void createTrackers() throws SynapseException, Exception {
		signInAsTimPowers();

		BridgeClient bridge = provider.getBridgeClient();

		// @formatter:off
		createData(bridge, "Sleep Tracker", "Daily sleep check in", null, ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *", null, null,
				new Column("Sleep time", "sleep-time-slider", ParticipantDataColumnType.DOUBLE));
		createData(bridge, "Rest Tracker", "Daily rest check in", null, ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *", null, null,
				new Column("Rest time", "sleep-time-slider", ParticipantDataColumnType.DOUBLE));
		createData(bridge, "Personal Info Tracker", "Personal information", null, ParticipantDataRepeatType.ONCE, null, null, null,
				new Column("Name", "string", ParticipantDataColumnType.STRING),
				new Column("Address", "string", ParticipantDataColumnType.STRING));
		String medsId = createData(bridge, "Medications", "Medication and supplement tracker", "medication",
				ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *",
				null, "medication",
				new Column( "medication", "The medication or supplement event", ParticipantDataColumnType.EVENT),
				new Column( "dose", "The dosage", ParticipantDataColumnType.STRING),
				new Column( "dose_instructions", "The dosage instructions", ParticipantDataColumnType.STRING));
		createDataEntry(bridge, medsId, new String[] {
				"medication", "dose"
			}, new Object[] {
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Dec 15, 2012"), DateFormat.getDateInstance().parse("Jan 3, 2013"),
						"Anadrol", "Anadrol"), "10mg",
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Jan 3, 2013"), DateFormat.getDateInstance().parse("Feb 21, 2013"),
						"Anadrol", "Anadrol"), "25mg",
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Feb 21, 2013"), null,
						"Anadrol", "Anadrol"), "50mg",
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Feb 21, 2012"), null,
						"Vitamin D", "Vitamin D"), "occasionally",
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Mar 17, 2013"), DateFormat.getDateInstance().parse("Apr 18, 2013"),
						"Amicar", "Amicar"), "10mg 2xday",
		});

		String eventsId = createData(bridge, "Events", "Events tracker", "event",
				ParticipantDataRepeatType.REPEATED, "0 0 3 * * ? *",
				null, "event",
				new Column( "event", "The event", ParticipantDataColumnType.EVENT),
				new Column( "details", "The optional event details", ParticipantDataColumnType.STRING));
		createDataEntry(bridge, eventsId, new String[] {
				"event", "details"
			}, new Object[] {
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Jan 1, 2013"), DateFormat.getDateInstance().parse("May 20, 2013"),
						"marathon", null), "training for the marathon",
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Jun 1, 2013"), null,
						"waiting for transfusion", null), null,
				ValueFactory.createEventValue(DateFormat.getDateInstance().parse("Mar 17, 2013"), DateFormat.getDateInstance().parse("Apr 18, 2013"),
						"Hospitalization", null), "acute",
		});

		createData(bridge, "Smoking Tracker", "Smoking check in", "question_smoking", ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *", null, null,
				new Column("collected_on", null, ParticipantDataColumnType.DATETIME),
				new Column("ever_smoked", null, ParticipantDataColumnType.BOOLEAN),
				new Column("what", null, ParticipantDataColumnType.STRING),
				new Column("count_per_week", null, ParticipantDataColumnType.STRING),
				new Column("how_long", null, ParticipantDataColumnType.STRING),
				new Column("when_stopped", null, ParticipantDataColumnType.DATETIME));
		createData(bridge, "Drinking Tracker", "Drinking check in", "question_drinking", ParticipantDataRepeatType.REPEATED, "0 0 4 * * ? *", null, null,
				new Column("collected_on", null, ParticipantDataColumnType.DATETIME),
				new Column("drinker", null, ParticipantDataColumnType.STRING),
				new Column("count_per_drinking_day", null, ParticipantDataColumnType.STRING),
				new Column("frequency", null, ParticipantDataColumnType.STRING),
				new Column("kind", null, ParticipantDataColumnType.STRING),
				new Column("how_long_in_months", null, ParticipantDataColumnType.LONG),
				new Column("when_stopped", null, ParticipantDataColumnType.DATETIME),
				new Column("other", null, ParticipantDataColumnType.STRING));

		// Need to create it this way or it says it's different (although only trivially) when we update trackers
		Specification spec = new MoodTracker();
		String trackerId = createData(bridge, spec);

		createDataEntry(bridge, trackerId, new String[] {
				"date", "Mind", "Body"
			}, new Object[] {
				DateFormat.getDateInstance().parse("Nov 1, 2012"), .2, .2,
				DateFormat.getDateInstance().parse("Jan 1, 2013"), .5, .2,
				DateFormat.getDateInstance().parse("Feb 1, 2013"), .6, .5,
				DateFormat.getDateInstance().parse("Apr 1, 2013"), .5, .3,
				DateFormat.getDateInstance().parse("May 15, 2013"), .5, .8,
				DateFormat.getDateInstance().parse("Jun 15, 2013"), .7, .9
		});

		spec = new CompleteBloodCount();
		trackerId = createData(bridge, spec);
		/*
		createDataEntry(bridge, trackerId, spec, "collected_on", "2013-10-23", "rbc-entered", "3.7", "rbc-units", "M/uL", "rbc-normalizedMin", "4", "rbc-normalizedMax", "4.9", "hb-entered", "12.7", "hb-units", "dL", "hb-normalizedMin", "11", "hb-normalizedMax", "13.3", "hct-entered", "37", "hct-units", "%", "hct-normalizedMin", "32", "hct-normalizedMax", "38", "mcv-entered", "100", "mcv-units", "fL", "mcv-normalizedMin", "75.9", "mcv-normalizedMax", "86.5", "mch-entered", "34.3", "mch-units", "pg", "mch-normalizedMin", "25.4", "mch-normalizedMax", "29.4", "rdw-entered", "13.2", "rdw-units", "%", "rdw-normalizedMin", "12.7", "rdw-normalizedMax", "14.6", "ret-units", "%", "wbc-entered", "3.6", "wbc-units", "K/uL", "wbc-normalizedMin", "4.5", "wbc-normalizedMax", "10.5", "wbc_diff-units", "%", "neutrophil-entered", "30", "neutrophil-units", "%", "neutrophil-normalizedMin", "36", "neutrophil-normalizedMax", "74", "neutrophil_immature-entered", "0", "neutrophil_immature-units", "%", "neutrophil_immature-normalizedMin", "0", "neutrophil_immature-normalizedMax", "1", "lymphocytes-entered", "53", "lymphocytes-units", "%", "lymphocytes-normalizedMin", "14", "lymphocytes-normalizedMax", "48", "monocytes-entered", "9", "monocytes-units", "%", "monocytes-normalizedMin", "4", "monocytes-normalizedMax", "9", "plt-entered", "120", "plt-units", "K/uL", "plt-normalizedMin", "140", "plt-normalizedMax", "440", "mpv-units", "fL", "pdw-units", "%", "created_on", "2014-01-14T09:57:53.723-08:00", "modified_on", "2014-01-14T09:57:53.723-08:00", "wbc (K/mcL)", "3.6", "rbc (M/mcL)", "3.7", "plt (K/mcL)", "120");
		createDataEntry(bridge, trackerId, spec, "collected_on", "2013-12-23",  "rbc-entered", "4.16",  "rbc-units", "10e12/L",  "rbc-normalizedMin", "3.8",  "rbc-normalizedMax", "5.2",  "hb-entered", "13.9",  "hb-units", "dL",  "hb-normalizedMin", "11.7",  "hb-normalizedMax", "15.7",  "hct-entered", "40",  "hct-units", "%",  "hct-normalizedMin", "35",  "hct-normalizedMax", "47",  "mcv-entered", "96",  "mcv-units", "fL",  "mcv-normalizedMin", "76",  "mcv-normalizedMax", "100",  "mch-entered", "33.4",  "mch-units", "pg",  "mch-normalizedMin", "26.5",  "mch-normalizedMax", "33",  "rdw-entered", "12.8",  "rdw-units", "%",  "rdw-normalizedMin", "10",  "rdw-normalizedMax", "15",  "ret-units", "%",  "wbc-entered", "11.1",  "wbc-units", "10e9/L",  "wbc-normalizedMin", "4",  "wbc-normalizedMax", "11",  "wbc_diff-units", "%",  "neutrophil-entered", "87.8",  "neutrophil-units", "%",  "neutrophil_immature-entered", ".3",  "neutrophil_immature-units", "%",  "lymphocytes-entered", "3.7",  "lymphocytes-units", "%",  "monocytes-entered", "7",  "monocytes-units", "%",  "plt-entered", "193",  "plt-units", "10e9/L",  "plt-normalizedMin", "150",  "plt-normalizedMax", "450",  "mpv-units", "fL",  "pdw-units", "%",  "created_on", "2014-01-14T10:17:33.488-08:00",  "modified_on", "2014-01-14T10:17:33.488-08:00",  "wbc (K/mcL)", "11.1",  "rbc (M/mcL)", "4.16",  "plt (K/mcL)", "193.0");
		createDataEntry(bridge, trackerId, spec, "collected_on", "2012-11-06",  "rbc-entered", "4.11",  "rbc-units", "M/uL",  "rbc-normalizedMin", "4.63",  "rbc-normalizedMax", "6.08",  "hb-entered", "13.5",  "hb-units", "dL",  "hb-normalizedMin", "13.7",  "hb-normalizedMax", "17.5",  "hct-entered", "39.1",  "hct-units", "%",  "hct-normalizedMin", "40.1",  "hct-normalizedMax", "51",  "mcv-entered", "96.1",  "mcv-units", "fL",  "mcv-normalizedMin", "79",  "mcv-normalizedMax", "92.2",  "mch-entered", "32.8",  "mch-units", "pg",  "mch-normalizedMin", "25.7",  "mch-normalizedMax", "32.2",  "rdw-entered", "13.1",  "rdw-units", "%",  "rdw-normalizedMin", "11.6",  "rdw-normalizedMax", "14.4",  "ret", "1.39",  "ret-units", "%",  "ret-normalizedMin", ".51",  "ret-normalizedMax", "1.81",  "wbc-entered", "4.89",  "wbc-units", "K/uL",  "wbc-normalizedMin", "4.23",  "wbc-normalizedMax", "9.07",  "wbc_diff-units", "%",  "neutrophil-entered", "29.6",  "neutrophil-units", "%",  "neutrophil-normalizedMin", "34",  "neutrophil-normalizedMax", "67.9",  "neutrophil_immature-units", "%",  "lymphocytes-entered", "65.3",  "lymphocytes-units", "%",  "lymphocytes-normalizedMin", "21.8",  "lymphocytes-normalizedMax", "53.1",  "monocytes-entered", "4.1",  "monocytes-units", "%",  "monocytes-normalizedMin", "5.3",  "monocytes-normalizedMax", "12.2",  "plt-entered", "132",  "plt-units", "K/uL",  "plt-normalizedMin", "161",  "plt-normalizedMax", "347",  "mpv", "11",  "mpv-units", "fL",  "mpv-normalizedMin", "9.4",  "mpv-normalizedMax", "12.4",  "pdw-units", "%",  "created_on", "2014-01-14T10:31:02.697-08:00",  "modified_on", "2014-01-14T10:31:02.697-08:00",  "wbc (K/mcL)", "4.89",  "rbc (M/mcL)", "4.11",  "plt (K/mcL)", "132");
		createDataEntry(bridge, trackerId, spec, "collected_on", "2013-09-26",  "rbc-entered", "4.96",  "rbc-units", "M/uL",  "rbc-normalizedMin", "4.2",  "rbc-normalizedMax", "5.8",  "hb-entered", "15.6",  "hb-units", "dL",  "hb-normalizedMin", "13.2",  "hb-normalizedMax", "17.1",  "hct-entered", "48.3",  "hct-units", "%",  "hct-normalizedMin", "38.5",  "hct-normalizedMax", "50",  "mcv-entered", "97.4",  "mcv-units", "fL",  "mcv-normalizedMin", "80",  "mcv-normalizedMax", "100",  "mch-entered", "31.5",  "mch-units", "pg",  "mch-normalizedMin", "27",  "mch-normalizedMax", "33",  "rdw-entered", "15.7",  "rdw-units", "%",  "rdw-normalizedMin", "11",  "rdw-normalizedMax", "15",  "ret", "0",  "ret-units", "%",  "wbc-entered", "5.6",  "wbc-units", "K/uL",  "wbc-normalizedMin", "3.8",  "wbc-normalizedMax", "10.8",  "wbc_diff-units", "%",  "neutrophil-entered", "42.1",  "neutrophil-units", "%",  "neutrophil_immature-units", "%",  "lymphocytes-entered", "40.2",  "lymphocytes-units", "%",  "monocytes-entered", "8.7",  "monocytes-units", "%",  "plt-entered", "203",  "plt-units", "K/uL",  "plt-normalizedMin", "140",  "plt-normalizedMax", "400",  "mpv-units", "fL",  "pdw-units", "%",  "created_on", "2014-01-14T11:00:00.501-08:00",  "modified_on", "2014-01-14T11:00:00.501-08:00",  "wbc (K/mcL)", "5.6",  "rbc (M/mcL)", "4.96",  "plt (K/mcL)", "203");
		createDataEntry(bridge, trackerId, spec, "collected_on", "2014-01-13",  "rbc-entered", "3.83",  "rbc-units", "M/uL",  "rbc-normalizedMin", "4",  "rbc-normalizedMax", "5.3",  "hb-entered", "13.1",  "hb-units", "dL",  "hb-normalizedMin", "12",  "hb-normalizedMax", "16",  "hct-entered", "38.3",  "hct-units", "%",  "hct-normalizedMin", "35",  "hct-normalizedMax", "47",  "mcv-entered", "99.9",  "mcv-units", "fL",  "mcv-normalizedMin", "76",  "mcv-normalizedMax", "90",  "mch-entered", "34.3",  "mch-units", "pg",  "mch-normalizedMin", "25",  "mch-normalizedMax", "31",  "rdw-entered", "11.8",  "rdw-units", "%",  "rdw-normalizedMin", "11.5",  "rdw-normalizedMax", "14.5",  "ret-units", "%",  "wbc-entered", "3.8",  "wbc-units", "K/uL",  "wbc-normalizedMin", "4",  "wbc-normalizedMax", "12",  "wbc_diff-units", "%",  "neutrophil-units", "%",  "neutrophil_immature-units", "%",  "lymphocytes-units", "%",  "monocytes-units", "%",  "plt-entered", "205",  "plt-units", "K/uL",  "plt-normalizedMin", "150",  "plt-normalizedMax", "450",  "mpv-units", "fL",  "pdw-units", "%",  "created_on", "2014-01-20T16:08:57.294-08:00",  "modified_on", "2014-01-20T16:08:57.294-08:00",  "wbc (K/mcL)", "3.8",  "rbc (M/mcL)", "3.83",  "plt (K/mcL)", "205");
		createDataEntry(bridge, trackerId, spec, "collected_on", "2013-12-11",  "rbc-entered", "4.42",  "rbc-units", "M/uL",  "rbc-normalizedMin", "3.8",  "rbc-normalizedMax", "5.1",  "hb-entered", "13.4",  "hb-units", "dL",  "hb-normalizedMin", "12",  "hb-normalizedMax", "16",  "hct-entered", "40.1",  "hct-units", "%",  "hct-normalizedMin", "37",  "hct-normalizedMax", "47",  "mcv-entered", "90.8",  "mcv-units", "fL",  "mcv-normalizedMin", "81",  "mcv-normalizedMax", "99",  "mch-entered", "30.3",  "mch-units", "pg",  "mch-normalizedMin", "26",  "mch-normalizedMax", "34",  "rdw-entered", "12.7",  "rdw-units", "%",  "rdw-normalizedMin", "11.5",  "rdw-normalizedMax", "15",  "ret-units", "%",  "wbc-entered", "5.1",  "wbc-units", "K/uL",  "wbc-normalizedMin", "3.8",  "wbc-normalizedMax", "10.8",  "wbc_diff-units", "%",  "neutrophil-units", "%",  "neutrophil_immature-units", "%",  "lymphocytes-entered", "32.2",  "lymphocytes-units", "%",  "lymphocytes-normalizedMin", "15",  "lymphocytes-normalizedMax", "40",  "monocytes-entered", "8.4",  "monocytes-units", "%",  "monocytes-normalizedMin", "0",  "monocytes-normalizedMax", "10",  "plt-entered", "375",  "plt-units", "K/uL",  "plt-normalizedMin", "140",  "plt-normalizedMax", "400",  "mpv", "8.4",  "mpv-units", "fL",  "mpv-normalizedMin", "7.4",  "mpv-normalizedMax", "10.4",  "pdw-units", "%",  "created_on", "2014-01-20T16:14:52.767-08:00",  "modified_on", "2014-01-20T16:14:52.767-08:00",  "wbc (K/mcL)", "5.1",  "rbc (M/mcL)", "4.42",  "plt (K/mcL)", "375");
		*/
		// @formatter:on
	}
	
	private void createUser(boolean isAdmin, SynapseAdminClient admin, SynapseClient synapse, String userName, String email, boolean acceptsTermsOfUse)
			throws SynapseException, JSONObjectAdapterException {
		try {
			NewIntegrationTestUser newUser = new NewIntegrationTestUser();
			newUser.setUsername(userName);
			newUser.setEmail(email);
			newUser.setPassword("password");
			try {
				admin.createUser(newUser);
			} catch (SynapseException e) {
				if (!e.getMessage().contains("already exists")) {
					throw e;
				}
			}
			
			Session session = synapse.login(userName, "password");
			if (acceptsTermsOfUse) {
				synapse.signTermsOfUse(session.getSessionToken(), DomainType.BRIDGE, true);
			}
			if (isAdmin) {
				UserSessionData data = synapse.getUserSessionData();
				admin.addTeamMember(TeamConstants.BRIDGE_ADMINISTRATORS.toString(), data.getProfile().getOwnerId());
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("User '" + userName + "' already exists");
		}
	}

	private String createData(BridgeClient bridge, String name, String description, String type, ParticipantDataRepeatType repeatType,
			String repeatFrequency, String datetimeStartColumnName, String eventColumnName, Column... cols)
			throws SynapseException {
		ParticipantDataDescriptor desc = new ParticipantDataDescriptor();
		desc.setDescription(description);
		desc.setName(name);
		desc.setType(type);
		desc.setRepeatType(repeatType);
		desc.setRepeatFrequency(repeatFrequency);
		desc.setDatetimeStartColumnName(datetimeStartColumnName);
		desc.setEventColumnName(eventColumnName);
		desc = bridge.createParticipantDataDescriptor(desc);
		for (Column column : cols) {
			ParticipantDataColumnDescriptor col = new ParticipantDataColumnDescriptor();
			col.setParticipantDataDescriptorId(desc.getId());
			col.setName(column.name);
			col.setDescription("");
			col.setType(column.type);
			col.setColumnType(column.columnType);
			bridge.createParticipantDataColumnDescriptor(col);
		}
		bridge.appendParticipantData(desc.getId(), Collections.<ParticipantDataRow> emptyList());
		return desc.getId();
	}

	private String createData(BridgeClient bridge, Specification spec) throws Exception {
		ParticipantDataDescriptor desc = ParticipantDataUtils.getDescriptor(spec);
		logger.info("Creating " + desc.getName());
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
		List<ParticipantDataRow> data = ParticipantDataUtils.getRowsForCreate(spec, map);
		client.appendParticipantData(trackerId, data);
		ParticipantDataStatusList statuses = ParticipantDataUtils.getFinishedStatus(trackerId);
		client.sendParticipantDataDescriptorUpdates(statuses);
	}

	private void createDataEntry(BridgeClient bridge, String id, String[] strings, Object[] objects) throws Exception {
		for (int i = 0; i < objects.length; i += strings.length) {
			Map<String, ParticipantDataValue> values = Maps.newHashMap();
			for (int j = 0; j < strings.length; j++) {
				Object val = objects[i + j];
				if (val != null) {
					ParticipantDataValue dataVal;
					if (val instanceof String) {
						dataVal = ValueFactory.createStringValue((String) val);
					} else if (val instanceof Double) {
						dataVal = ValueFactory.createDoubleValue((Double) val);
					} else if (val instanceof Long) {
						dataVal = ValueFactory.createLongValue((Long) val);
					} else if (val instanceof Date) {
						dataVal = ValueFactory.createDatetimeValue((Date) val);
					} else if (val instanceof ParticipantDataValue) {
						dataVal = (ParticipantDataValue) val;
					} else {
						throw new RuntimeException("Type " + val.getClass() + " not handled");
					}
					values.put(strings[j], dataVal);
				}
			}
			ParticipantDataRow row = new ParticipantDataRow();
			row.setData(values);
			bridge.appendParticipantData(id, Collections.<ParticipantDataRow> singletonList(row));
		}
	}

	private void createCommunity(BridgeClient client, String name, String description) throws SynapseException {
		Community community = new Community();
		community.setName(name);
		community.setDescription(description);
		client.createCommunity(community);
	}

}
