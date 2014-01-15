package org.sagebionetworks.bridge.webapp;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataColumnDescriptor;
import org.sagebionetworks.bridge.model.data.ParticipantDataDescriptor;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;
import org.sagebionetworks.bridge.webapp.specs.SpecificationResolver;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.table.RowSet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.google.common.collect.Maps;

/**
 * When using the stub, this listener creates some dummy in-memory objects. It 
 * otherwise does nothing, is part of test infrastructure, and can be ignored.
 * 
 */
public class SageStubApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LogManager.getLogger(SageStubApplicationListener.class.getName());
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Object object = context.getBean("bridgeClient");
		if (object instanceof SageServicesStub) {
			try {
				String formId = null;
				BridgeClient client = (BridgeClient)object;
				SpecificationResolver specResolver = (SpecificationResolver)context.getBean("specificationResolver");
				
				PaginatedResults<ParticipantDataDescriptor> descriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0);
				for (Specification spec: specResolver.getAllSpecifications()) {
					
					logger.info("Creating a new ParticipantDataDescriptor with its columns: " + spec.getName());
					
					// This has to be done as a specific user. The stub makes this easier. Just call this first.
					((SynapseClient)client).login("test", "password");
					
					ParticipantDataDescriptor descriptor = ParticipantDataUtils.getDescriptor(spec);
					descriptor = client.createParticipantDataDescriptor(descriptor);
					if (spec.getName().equals("CBC")) {
						formId = descriptor.getId();
					}
					List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(descriptor.getId(), spec);
					for (ParticipantDataColumnDescriptor column : columns) {
						client.createParticipantDataColumnDescriptor(column);
					}
				}
				Specification spec = specResolver.getSpecification("CBC");
				createCBCRecords(client, formId, spec, "collectedOn", "2013-10-23", "rbc", "3.7", "rbc_units", "M/uL", "rbc_range_low", "4", "rbc_range_high", "4.9", "hb", "12.7", "hb_units", "dL", "hb_range_low", "11", "hb_range_high", "13.3", "hct", "37", "hct_units", "%", "hct_range_low", "32", "hct_range_high", "38", "mcv", "100", "mcv_units", "fL", "mcv_range_low", "75.9", "mcv_range_high", "86.5", "mch", "34.3", "mch_units", "pg", "mch_range_low", "25.4", "mch_range_high", "29.4", "rdw", "13.2", "rdw_units", "%", "rdw_range_low", "12.7", "rdw_range_high", "14.6", "ret_units", "%", "wbc", "3.6", "wbc_units", "K/uL", "wbc_range_low", "4.5", "wbc_range_high", "10.5", "wbc_diff_units", "%", "neutrophil", "30", "neutrophil_units", "%", "neutrophil_range_low", "36", "neutrophil_range_high", "74", "neutrophil_immature", "0", "neutrophil_immature_units", "%", "neutrophil_immature_range_low", "0", "neutrophil_immature_range_high", "1", "lymphocytes", "53", "lymphocytes_units", "%", "lymphocytes_range_low", "14", "lymphocytes_range_high", "48", "monocytes", "9", "monocytes_units", "%", "monocytes_range_low", "4", "monocytes_range_high", "9", "plt", "120", "plt_units", "K/uL", "plt_range_low", "140", "plt_range_high", "440", "mpv_units", "fL", "pdw_units", "%", "createdOn", "2014-01-14T09:57:53.723-08:00", "modifiedOn", "2014-01-14T09:57:53.723-08:00", "wbc (K/mcL)", "3.6", "rbc (M/mcL)", "3.7", "plt (K/mcL)", "120");
				createCBCRecords(client, formId, spec, "collectedOn", "2013-12-23",  "rbc", "4.16",  "rbc_units", "10e12/L",  "rbc_range_low", "3.8",  "rbc_range_high", "5.2",  "hb", "13.9",  "hb_units", "dL",  "hb_range_low", "11.7",  "hb_range_high", "15.7",  "hct", "40",  "hct_units", "%",  "hct_range_low", "35",  "hct_range_high", "47",  "mcv", "96",  "mcv_units", "fL",  "mcv_range_low", "76",  "mcv_range_high", "100",  "mch", "33.4",  "mch_units", "pg",  "mch_range_low", "26.5",  "mch_range_high", "33",  "rdw", "12.8",  "rdw_units", "%",  "rdw_range_low", "10",  "rdw_range_high", "15",  "ret_units", "%",  "wbc", "11.1",  "wbc_units", "10e9/L",  "wbc_range_low", "4",  "wbc_range_high", "11",  "wbc_diff_units", "%",  "neutrophil", "87.8",  "neutrophil_units", "%",  "neutrophil_immature", ".3",  "neutrophil_immature_units", "%",  "lymphocytes", "3.7",  "lymphocytes_units", "%",  "monocytes", "7",  "monocytes_units", "%",  "plt", "193",  "plt_units", "10e9/L",  "plt_range_low", "150",  "plt_range_high", "450",  "mpv_units", "fL",  "pdw_units", "%",  "createdOn", "2014-01-14T10:17:33.488-08:00",  "modifiedOn", "2014-01-14T10:17:33.488-08:00",  "wbc (K/mcL)", "11.1",  "rbc (M/mcL)", "4.16",  "plt (K/mcL)", "193.0");
				createCBCRecords(client, formId, spec, "collectedOn", "2012-11-06",  "rbc", "4.11",  "rbc_units", "M/uL",  "rbc_range_low", "4.63",  "rbc_range_high", "6.08",  "hb", "13.5",  "hb_units", "dL",  "hb_range_low", "13.7",  "hb_range_high", "17.5",  "hct", "39.1",  "hct_units", "%",  "hct_range_low", "40.1",  "hct_range_high", "51",  "mcv", "96.1",  "mcv_units", "fL",  "mcv_range_low", "79",  "mcv_range_high", "92.2",  "mch", "32.8",  "mch_units", "pg",  "mch_range_low", "25.7",  "mch_range_high", "32.2",  "rdw", "13.1",  "rdw_units", "%",  "rdw_range_low", "11.6",  "rdw_range_high", "14.4",  "ret", "1.39",  "ret_units", "%",  "ret_range_low", ".51",  "ret_range_high", "1.81",  "wbc", "4.89",  "wbc_units", "K/uL",  "wbc_range_low", "4.23",  "wbc_range_high", "9.07",  "wbc_diff_units", "%",  "neutrophil", "29.6",  "neutrophil_units", "%",  "neutrophil_range_low", "34",  "neutrophil_range_high", "67.9",  "neutrophil_immature_units", "%",  "lymphocytes", "65.3",  "lymphocytes_units", "%",  "lymphocytes_range_low", "21.8",  "lymphocytes_range_high", "53.1",  "monocytes", "4.1",  "monocytes_units", "%",  "monocytes_range_low", "5.3",  "monocytes_range_high", "12.2",  "plt", "132",  "plt_units", "K/uL",  "plt_range_low", "161",  "plt_range_high", "347",  "mpv", "11",  "mpv_units", "fL",  "mpv_range_low", "9.4",  "mpv_range_high", "12.4",  "pdw_units", "%",  "createdOn", "2014-01-14T10:31:02.697-08:00",  "modifiedOn", "2014-01-14T10:31:02.697-08:00",  "wbc (K/mcL)", "4.89",  "rbc (M/mcL)", "4.11",  "plt (K/mcL)", "132");
				createCBCRecords(client, formId, spec, "collectedOn", "2013-09-26",  "rbc", "4.96",  "rbc_units", "M/uL",  "rbc_range_low", "4.2",  "rbc_range_high", "5.8",  "hb", "15.6",  "hb_units", "dL",  "hb_range_low", "13.2",  "hb_range_high", "17.1",  "hct", "48.3",  "hct_units", "%",  "hct_range_low", "38.5",  "hct_range_high", "50",  "mcv", "97.4",  "mcv_units", "fL",  "mcv_range_low", "80",  "mcv_range_high", "100",  "mch", "31.5",  "mch_units", "pg",  "mch_range_low", "27",  "mch_range_high", "33",  "rdw", "15.7",  "rdw_units", "%",  "rdw_range_low", "11",  "rdw_range_high", "15",  "ret", "0",  "ret_units", "%",  "wbc", "5.6",  "wbc_units", "K/uL",  "wbc_range_low", "3.8",  "wbc_range_high", "10.8",  "wbc_diff_units", "%",  "neutrophil", "42.1",  "neutrophil_units", "%",  "neutrophil_immature_units", "%",  "lymphocytes", "40.2",  "lymphocytes_units", "%",  "monocytes", "8.7",  "monocytes_units", "%",  "plt", "203",  "plt_units", "K/uL",  "plt_range_low", "140",  "plt_range_high", "400",  "mpv_units", "fL",  "pdw_units", "%",  "createdOn", "2014-01-14T11:00:00.501-08:00",  "modifiedOn", "2014-01-14T11:00:00.501-08:00",  "wbc (K/mcL)", "5.6",  "rbc (M/mcL)", "4.96",  "plt (K/mcL)", "203");
			} catch(Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}	
	
	private void createCBCRecords(BridgeClient client, String formId, Specification spec, String... values) {
		try {
			Map<String, String> map = Maps.newHashMap();
			for (int i=0; i < values.length; i+=2) {
				map.put(values[i], values[i+1]);
			}
			RowSet data = ParticipantDataUtils.getRowSetForCreate(spec, map);
			client.appendParticipantData(formId, data);
		} catch(Throwable throwable) {
			logger.error(throwable);
		}
	}

}
