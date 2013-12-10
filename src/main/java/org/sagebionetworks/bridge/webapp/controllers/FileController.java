package org.sagebionetworks.bridge.webapp.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.forms.UploadForm;
import org.sagebionetworks.bridge.webapp.forms.WikiFile;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleResults;
import org.sagebionetworks.repo.model.file.PreviewFileHandle;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

@Controller
public class FileController {
	
	private static Logger logger = LogManager.getLogger(FileController.class.getName());
	
	public static final String PLACEHOLDER_URL = "/static/images/no-image.jpg";
	
	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
		
	@Resource(name = "bridgeClient")
	protected BridgeClient bridgeClient;

	public void setBridgeClient(BridgeClient bridgeClient) {
		this.bridgeClient = bridgeClient;
	}
	
	@RequestMapping(value = "/files/communities/{communityId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void imageURL(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @RequestParam("fileName") String fileName)
			throws IOException {
		
		try {
			V2WikiPage root = synapseClient.getV2RootWikiPage(communityId, ObjectType.ENTITY);
			WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, root.getId());
			URL url = synapseClient.getV2WikiAttachmentTemporaryUrl(key, fileName);
			response.sendRedirect(url.toString());
		} catch(Exception e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@RequestMapping(value = "/files/communities/{communityId}/delete", method = RequestMethod.GET)
	public String deleteImage(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @RequestParam("fileName") String fileName)
			throws IOException, JSONObjectAdapterException, SynapseException {
		
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		
		V2WikiPage root = client.getV2RootWikiPage(communityId, ObjectType.ENTITY);
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, root.getId());
		
		String selectedId = null;
		FileHandleResults results = client.getV2WikiAttachmentHandles(key);
		for (FileHandle handle : results.getList()) {
			if (handle.getFileName().equals(fileName)) {
				selectedId = handle.getId();
				break;
			}
		}
		if (selectedId != null) {
			List<String> list = root.getAttachmentFileHandleIds();
			if (list != null) {
				list.remove(selectedId);
				root.setAttachmentFileHandleIds(list);
				client.updateV2WikiPage(communityId, ObjectType.ENTITY, root);
			}
		}
		return String.format("redirect:/files/communities/%s/browse.html", communityId);
	}
	
	@RequestMapping(value = {"/files/communities/{communityId}/browse"}, method = RequestMethod.GET)
	public ModelAndView browseCommunityAttachments(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model) throws JSONObjectAdapterException, SynapseException, ClientProtocolException,
			MalformedURLException, IOException {
		
		model.setViewName("communities/browse");
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		
		V2WikiPage wiki = synapseClient.getV2RootWikiPage(community.getId(), ObjectType.ENTITY);
		
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wiki.getId());
		FileHandleResults results = synapseClient.getV2WikiAttachmentHandles(key);
		
		List<WikiFile> images = Lists.newArrayList();
		for (FileHandle handle : results.getList()) {
			// Preview files are included in the file handles, and they break things. Filter them out.
			if (!(handle instanceof PreviewFileHandle)) {
				String fileName = handle.getFileName();
				
				String previewImageLink = getPreviewImageLink(request, key, fileName);
				String permanentLink = getPermanentLink(request.getContextPath(), community, fileName);
				String deleteLink = getDeleteLink(request.getContextPath(), community, fileName);
				images.add( new WikiFile(fileName, previewImageLink, permanentLink, deleteLink) );
			}
		}
		model.addObject("images",images);
		
		return model;
	}
	
	@RequestMapping(value = {"/files/communities/{communityId}/upload"}, method = RequestMethod.POST)
	public void uploadCommunityAttachment(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @ModelAttribute("uploadForm") UploadForm uploadForm)
			throws ServletException, JSONObjectAdapterException, SynapseException, IOException {
		
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		
		List<File> files = retrieveFilesFromRequest(request, uploadForm);
		FileHandleResults results = client.createFileHandles(files);
		
		V2WikiPage wiki = synapseClient.getV2RootWikiPage(community.getId(), ObjectType.ENTITY);
		
		// The UI only allows one upload at a time.
		FileHandle handle = results.getList().get(0);
		List<String> handleIds = wiki.getAttachmentFileHandleIds();
		handleIds.add(handle.getId());
		client.updateV2WikiPage(communityId, ObjectType.ENTITY, wiki);

		// Point back to the application so we can generate temporary Synapse URLs on demand.
		String url = getPermanentLink(request.getContextPath(), community, handle.getFileName());
		String funcNum = request.getParameter("CKEditorFuncNum");
		String js = "<script>window.top.CKEDITOR.tools.callFunction("+funcNum+", '"+url.toString()+"');</script>";
		response.getWriter().print(js);
	}

	// NOTE: This is eventually consistent. If this throws an error, just ignore it and show a placeholder.
	private String getPreviewImageLink(BridgeRequest request, WikiPageKey key, String fileName) {
		String previewImageLink = null;
		try {
			previewImageLink = synapseClient.getV2WikiAttachmentPreviewTemporaryUrl(key, fileName).toExternalForm();
		} catch(Exception e) {
			previewImageLink = request.getContextPath() + PLACEHOLDER_URL;
		}
		return previewImageLink;
	}
	
	private List<File> retrieveFilesFromRequest(BridgeRequest request, UploadForm uploadForm) throws ServletException {
		List<File> files = Lists.newArrayList();
		
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			MultipartFile file = uploadForm.getFile();
			String fileName = file.getOriginalFilename();
			inputStream = file.getInputStream();
			File newFile = ClientUtils.createTempFile(request, fileName);
			FileUtils.copyInputStreamToFile(inputStream, newFile);
			logger.info("Writing file to:" + newFile.getAbsolutePath());
			files.add(newFile);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(inputStream);
		}
		return files;
	}
	
	private String getPermanentLink(String contextPath, Community community, String fileName) {
		return String.format("%s/files/communities/%s.html?fileName=%s", contextPath, community.getId(), fileName);
	}
	
	private String getDeleteLink(String contextPath, Community community, String fileName) {
		return String.format("%s/files/communities/%s/delete.html?fileName=%s", contextPath, community.getId(), fileName);
	}
	
}
