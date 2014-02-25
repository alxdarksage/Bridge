package org.sagebionetworks.bridge.webapp.servlet;


import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.versionInfo.SynapseVersionInfo;
import org.springframework.web.context.ServletContextAware;

public class VersionListener implements ServletContextAware {

	private static final Logger logger = LogManager.getLogger(VersionListener.class.getName());
	
	private static final String BRIDGE_VERSION = "bridgeVersion";
	private static final String REPO_VERSION = "repoVersion";
	
	@Resource(name = "synapseClient")
	private SynapseClient synapseClient;
	
	private String bridgeBuildVersion;
	
	public void setBridgeBuildVersion(String bridgeBuildVersion) {
		this.bridgeBuildVersion = bridgeBuildVersion;
	}

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}

	@Override
	public void setServletContext(ServletContext context) {
		try {
			
			SynapseVersionInfo info = synapseClient.getVersionInfo();	
			context.setAttribute(REPO_VERSION, info.getVersion());
			context.setAttribute(BRIDGE_VERSION, bridgeBuildVersion);
			
		} catch(Throwable throwable) {
			// We can continue without version numbers
			logger.error(throwable);
			context.setAttribute(REPO_VERSION, "--");
			context.setAttribute(BRIDGE_VERSION, "--");
		}
	}

}
