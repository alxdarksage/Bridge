package org.sagebionetworks.bridge.webapp.controllers.communities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.ImageFile;
import org.sagebionetworks.bridge.webapp.forms.UploadForm;
import org.sagebionetworks.bridge.webapp.forms.WikiForm;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


/**
 * /communities/#/wikis/#/edit (GET, POST to update)
 * /communities/#/wikis/#/browse (attachments)
 * /communities/#/wikis/#/upload (files as attachments)
 * 
 */
@Controller
public class CommunityWikiController {
	
	private static Logger logger = LogManager.getLogger(CommunityWikiController.class.getName());
	
	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, ModelAndView model, WikiForm wikiForm) throws Exception {
		
		V2WikiPage wiki = prepareAndRetrieveModels(request, communityId, wikiId, model);
		FormUtils.valuesToWikiForm(wikiForm, wiki);
		
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wiki.getId());
		File markdownFile = synapseClient.downloadV2WikiMarkdown(key);
		String markdown = FileUtils.readFileToString(markdownFile);
		wikiForm.setMarkdown(markdown);
		
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.POST)
	public ModelAndView update(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, @ModelAttribute @Valid WikiForm wikiForm, BindingResult result,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException, IOException {
		
		V2WikiPage wiki = prepareAndRetrieveModels(request, communityId, wikiId, model);
		
		// There are no errors that can occur here, actually.
		if (!result.hasErrors()) {
			SynapseClient client = request.getBridgeUser().getSynapseClient();
			String sanitizedHTML = Jsoup.clean(wikiForm.getMarkdown(), "https://localhost:8888/webapp", getCustomWhitelist());
			
			logger.debug("Sanitized HTML: " + sanitizedHTML);
			
			// You need to create a file temporarily to do this: bad.
			File tempDir = (File)request.getAttribute(ServletContext.TEMPDIR);
			File temp = new File(tempDir, wiki.getId()+".html");
			FileUtils.writeStringToFile(temp, sanitizedHTML);
			FileHandle handle = client.createFileHandle(temp, "text/html");
			
			wiki.setMarkdownFileHandleId(handle.getId());
			wiki.setTitle(wikiForm.getTitle());
			client.updateV2WikiPage(communityId, ObjectType.ENTITY, wiki);
			
			model.setViewName("redirect:/communities/" + communityId + ".html");
			request.setNotification("CommunityUpdated");
		}
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/browse", method = RequestMethod.GET)
	public ModelAndView browseAttachments(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, ModelAndView model) throws JSONObjectAdapterException,
			SynapseException, ClientProtocolException, MalformedURLException, IOException {
		
		model.setViewName("communities/browse");
		
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wikiId);
		FileHandleResults results = synapseClient.getV2WikiAttachmentHandles(key);
		List<ImageFile> images = new ArrayList<>();
		for (FileHandle handle : results.getList()) {
			// Preview files are included in the file handles, and they break things. Filter them out.
			if (!(handle instanceof PreviewFileHandle)) {
				String fileName = handle.getFileName();
				URL tempURL = synapseClient.getV2WikiAttachmentPreviewTemporaryUrl(key, fileName);
				ImageFile file = new ImageFile(tempURL.toExternalForm(), getLinkback(request.getContextPath(),
						communityId, wikiId, fileName));
				images.add(file);
			}
		}
		model.addObject("images",images);
		
		return model;
	}	
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/upload", method = RequestMethod.POST)
	public void uploadAttachment(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @PathVariable("wikiId") String wikiId,
			@ModelAttribute("uploadForm") UploadForm uploadForm, BindingResult result) throws ServletException,
			JSONObjectAdapterException, SynapseException, IOException {
		
		List<File> files = retrieveFiles(request, uploadForm);
		
		SynapseClient client = request.getBridgeUser().getSynapseClient();
		FileHandleResults results = client.createFileHandles(files);
		
		V2WikiPage page = ClientUtils.getWikiPage(request, communityId, wikiId);
		if (page.getAttachmentFileHandleIds() == null) {
			page.setAttachmentFileHandleIds(new ArrayList<String>());
		}
		List<String> handleIds = page.getAttachmentFileHandleIds();

		// The UI only allows one upload at a time.
		FileHandle handle = results.getList().get(0);
		handleIds.add(handle.getId());
		client.updateV2WikiPage(communityId, ObjectType.ENTITY, page);

		// Point back to the application so we can generate temporary Synapse URLs on demand.
		
		String url = getLinkback(request.getContextPath(), communityId, wikiId, handle.getFileName());
		String funcNum = request.getParameter("CKEditorFuncNum");
		String js = "<script>window.top.CKEDITOR.tools.callFunction("+funcNum+", '"+url.toString()+"');</script>";
		response.getWriter().print(js);
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
	
	// TODO: Ick
	private V2WikiPage prepareAndRetrieveModels(BridgeRequest request, String communityId, String wikiId,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Community community = client.getCommunity(communityId);
		
		V2WikiPage wiki = ClientUtils.getWikiPage(request, communityId, wikiId);
		
		model.addObject("community", community);
		model.setViewName("communities/edit");
		return wiki;
	}

	private List<File> retrieveFiles(BridgeRequest request, UploadForm uploadForm) throws ServletException {
		List<File> files = new ArrayList<>();
		
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
	
	private String getLinkback(String contextPath, String communityId, String wikiId, String fileName) {
		return String.format("%s/files/%s/%s.html?fileName=%s", contextPath, communityId, wikiId, fileName);
	}
	
	private Whitelist getCustomWhitelist() {
		return Whitelist.relaxed()
			.preserveRelativeLinks(true)
			.addAttributes(":all", "class", "style", "width");
	}
}
