package org.sagebionetworks.bridge.services;

import javax.servlet.ServletRequest;

import org.sagebionetworks.client.SynapseClient;

public interface SynapseClientProvider {
	/**
	 * get a synapse client
	 * 
	 * @param request
	 * @return
	 */
	SynapseClient getSynapseClient(ServletRequest request);
}
