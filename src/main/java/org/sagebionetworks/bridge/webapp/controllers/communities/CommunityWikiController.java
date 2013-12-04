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
import org.sagebionetworks.bridge.webapp.forms.WikiHeader;
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
 * /communities/#
 * 		/wikis/new (GET, POST to create)
 * 		/wikis/#/edit (GET, POST to update)
 * 		/wikis/#/browse (attachments)
 * 		/wikis/#/upload (files as attachments)
 *      /wikis/#/all (all other wiki pages for cross navigation)
 */
@Controller
@RequestMapping(value = "/communities")
public class CommunityWikiController {
	
	private static Logger logger = LogManager.getLogger(CommunityWikiController.class.getName());
	
	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/new", method = RequestMethod.GET)
	public ModelAndView newWiki(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model, WikiForm wikiForm) throws Exception {
		
		storeCommunityAndGetWikiPage(request, communityId, null, model);
		model.setViewName("communities/new");
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/new", method = RequestMethod.POST)
	public ModelAndView saveNewWiki(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model, @ModelAttribute @Valid WikiForm wikiForm, BindingResult result) throws Exception {
		
		storeCommunityAndGetWikiPage(request, communityId, null, model);
		model.setViewName("communities/new");
		if (!result.hasErrors()) {
			SynapseClient client = request.getBridgeUser().getSynapseClient();
			// NOTE: It doesn't use the relative URI, it just wants to know it's there... ?
			String sanitizedHTML = Jsoup.clean(wikiForm.getMarkdown(), "https://localhost:8888/webapp", getCustomWhitelist());
			
			String userId = request.getBridgeUser().getOwnerId();
			File tempDir = (File)request.getAttribute(ServletContext.TEMPDIR);
			File temp = new File(tempDir, userId+".html");
			FileUtils.writeStringToFile(temp, sanitizedHTML);
			FileHandle handle = client.createFileHandle(temp, "text/html");
			
			V2WikiPage root = client.getV2RootWikiPage(communityId, ObjectType.ENTITY);
			V2WikiPage wiki = new V2WikiPage();
			wiki.setMarkdownFileHandleId(handle.getId());
			wiki.setTitle(wikiForm.getTitle());
			wiki.setParentWikiId(root.getId());
			V2WikiPage saved = client.createV2WikiPage(communityId, ObjectType.ENTITY, wiki);

			model.setViewName( String.format("redirect:/communities/%s/wikis/%s.html", communityId, saved.getId()) );
			request.setNotification("CommunityUpdated");
		}
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, ModelAndView model, WikiForm wikiForm) throws Exception {

		V2WikiPage wiki = storeCommunityAndGetWikiPage(request, communityId, wikiId, model);
		model.setViewName("communities/edit");
		FormUtils.valuesToWikiForm(wikiForm, wiki);
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		List<WikiHeader> headers = ClientUtils.getWikiHeadersFor(synapseClient, community);
		model.addObject("wikiHeaders", headers);
		
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wiki.getId());
		File markdownFile = synapseClient.downloadV2WikiMarkdown(key);
		String markdown = FileUtils.readFileToString(markdownFile);
		wikiForm.setMarkdown(markdown);
		
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.POST)
	public ModelAndView update(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, @ModelAttribute @Valid WikiForm wikiForm, BindingResult result,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException, IOException {
		
		V2WikiPage wiki = storeCommunityAndGetWikiPage(request, communityId, wikiId, model);
		model.setViewName("communities/edit");
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
			
			model.setViewName( String.format("redirect:/communities/%s/wikis/%s.html", communityId, wikiId) );
			request.setNotification("CommunityUpdated");
		}
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/browse", method = RequestMethod.GET)
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
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/upload", method = RequestMethod.POST)
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

	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/all", method = RequestMethod.GET)
	public ModelAndView allPages(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, ModelAndView model) throws ServletException, JSONObjectAdapterException, SynapseException, IOException {
		
		// Get headers for all wiki pages. Removes root, but would like to mark the other two as well.
		// Going to move this.
		
		V2WikiPage wiki = storeCommunityAndGetWikiPage(request, communityId, wikiId, model);
		model.addObject("wiki", wiki);

		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		List<WikiHeader> headers = ClientUtils.getWikiHeadersFor(synapseClient, community);
		model.addObject("wikiHeaders", headers);
		
		model.setViewName("communities/all");
		return model;
	}
	
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/all", method = RequestMethod.POST, params = "delete=delete")
	public String batchWikis(BridgeRequest request, @RequestParam("rowSelect") List<String> rowSelects,
			@PathVariable("communityId") String communityId, @PathVariable("wikiId") String wikiId)
			throws SynapseException {
		
		if (rowSelects != null) {
			Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
			SynapseClient client = request.getBridgeUser().getSynapseClient();
			
			int count = 0;
			for (String id : rowSelects) {
				if (!id.equals(community.getWelcomePageWikiId()) && !id.equals(community.getIndexPageWikiId())) {
					WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, id);
					client.deleteV2WikiPage(key);
					count++;
				}
			}
			if (count == 1) {
				request.setNotification("CommunityDeleted");
			} else if (count > 1) {
				request.setNotification("CommunitiesDeleted");
			}
		}
		return String.format("redirect:/communities/%s/wikis/%s/all.html", communityId, wikiId);
	}
	
	
	private V2WikiPage storeCommunityAndGetWikiPage(BridgeRequest request, String communityId, String wikiId,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Community community = client.getCommunity(communityId);
		model.addObject("community", community);
		if (wikiId != null) {
			return ClientUtils.getWikiPage(request, communityId, wikiId);	
		}
		return null;
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
