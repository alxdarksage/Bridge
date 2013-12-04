package org.sagebionetworks.bridge.webapp.controllers;

import java.io.IOException;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileController {
	
	private static Logger logger = LogManager.getLogger(FileController.class.getName());
	
	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
		
	@RequestMapping(value = "/files/{communityId}/{wikiId}", method = RequestMethod.GET)
	public void imageURL(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @PathVariable("wikiId") String wikiId, 
			@RequestParam("fileName") String fileName) throws IOException {
		
		try {
			WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wikiId);
			URL url = synapseClient.getV2WikiAttachmentTemporaryUrl(key, fileName);
			response.sendRedirect(url.toString());
		} catch(Exception e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
}
