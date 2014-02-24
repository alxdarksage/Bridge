package org.sagebionetworks.bridge.webapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseAdminClient;
import org.sagebionetworks.client.SynapseClient;
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
	
	private static boolean initialized = false;

	public class StubClientProvider implements SageBootstrap.ClientProvider {
		private SageServicesStub stub;
		public StubClientProvider(SageServicesStub stub) {
			this.stub = stub;
		}
		@Override public SynapseAdminClient getAdminClient() { return stub; }
		@Override public SynapseClient getSynapseClient() { return stub; }
		@Override public BridgeClient getBridgeClient() { return stub; }
		
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!initialized) {
			ApplicationContext context = event.getApplicationContext();
			Object object = context.getBean("bridgeClient");
			if (object instanceof SageServicesStub) {
				try {
					SageBootstrap bootstrap = new SageBootstrap(new StubClientProvider((SageServicesStub) object));
					bootstrap.create();
					initialized = true;
				} catch (Throwable throwable) {
					logger.error(throwable.getMessage(), throwable);
				}
			}
		}
	}	
	
}
