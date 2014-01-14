package org.sagebionetworks.bridge.webapp;

import java.util.List;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

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
				BridgeClient client = (BridgeClient)object;
				SpecificationResolver specResolver = (SpecificationResolver)context.getBean("specificationResolver");
				
				PaginatedResults<ParticipantDataDescriptor> descriptors = client.getAllParticipantDatas(ClientUtils.LIMIT, 0);
				for (Specification spec: specResolver.getAllSpecifications()) {
					
					if (specificationDoesNotExist(descriptors.getResults(), spec)) {
						logger.info("Creating a new ParticipantDataDescriptor with its columns: " + spec.getName());
						
						// This has to be done as a specific user. The stub makes this easier. Just call this first.
						((SynapseClient)client).login("test@test.com", "password");
						
						ParticipantDataDescriptor descriptor = ParticipantDataUtils.getDescriptor(spec);
						descriptor = client.createParticipantDataDescriptor(descriptor);
						
						List<ParticipantDataColumnDescriptor> columns = ParticipantDataUtils.getColumnDescriptors(descriptor.getId(), spec);
						for (ParticipantDataColumnDescriptor column : columns) {
							client.createParticipantDataColumnDescriptor(column);
						}
					}
				}
			} catch(Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}	
	
	private boolean specificationDoesNotExist(List<ParticipantDataDescriptor> descriptors, Specification spec) {
		for (ParticipantDataDescriptor descriptor : descriptors) {
			if (descriptor.getName().equals(spec.getName())) {
				return false;
			}
		}
		return true;
	}

}
