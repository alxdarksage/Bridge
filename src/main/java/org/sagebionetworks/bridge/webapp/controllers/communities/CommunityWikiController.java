package org.sagebionetworks.bridge.webapp.controllers.communities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.sagebionetworks.bridge.webapp.forms.UploadForm;
import org.sagebionetworks.bridge.webapp.forms.WikiForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.BridgeClient;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.attachment.AttachmentData;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleResults;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.repo.model.wiki.WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


/**
 * /communities/#/wikis/#/edit (GET, POST to update)
 * /communities/#/wikis/#/browse (attachments)
 * /communities/#/wikis/#/upload (files as attachments)
 * 
 * @author alxdark
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
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.POST)
	public ModelAndView update(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, @ModelAttribute @Valid WikiForm wikiForm, BindingResult result,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException {
		
		V2WikiPage wiki = prepareAndRetrieveModels(request, communityId, wikiId, model);
		
		// There are no errors that can occur here, actually.
		if (!result.hasErrors()) {
			String sanitizedHTML = Jsoup.clean(wikiForm.getMarkdown(), getCustomWhitelist());
			// wiki.setMarkdown(sanitizedHTML);
			logger.info("Content that will be saved: " + sanitizedHTML);
			wiki.setTitle(wikiForm.getTitle());
			request.getBridgeUser().getSynapseClient().updateV2WikiPage(communityId, ObjectType.ENTITY, wiki);
			
			model.setViewName("redirect:/communities/" + communityId + ".html");
			request.setNotification("CommunityUpdated");
		}
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/browse", method = RequestMethod.GET)
	public ModelAndView browseAttachments(ModelAndView model) {
		model.setViewName("communities/browse");
		return model;
	}
	
	@RequestMapping(value = "/communities/{communityId}/wikis/{wikiId}/upload", method = RequestMethod.POST)
	public void uploadAttachment(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @PathVariable("wikiId") String wikiId,
			@ModelAttribute("uploadForm") UploadForm uploadForm, BindingResult result) throws ServletException,
			JSONObjectAdapterException, SynapseException {
		
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			MultipartFile file = uploadForm.getFile();
			// This seems very suspect to me. This will have to change.
			String fileName = file.getOriginalFilename();
			
			inputStream = file.getInputStream();

			File directory = (File)request.getServletContext().getAttribute(ServletContext.TEMPDIR);
			
			if (directory == null) {
			    throw new ServletException("Servlet container does not provide temporary directory");
			}
			File newFile = new File(directory.getAbsolutePath() + "/" + fileName);
			logger.info("Writing file to:" + newFile.getAbsolutePath());
			
			if (!newFile.exists()) {
				newFile.createNewFile();
			}
			FileUtils.copyInputStreamToFile(inputStream, newFile);

			SynapseClient client = request.getBridgeUser().getSynapseClient();
			List<File> files = new ArrayList<>();
			files.add(newFile);
			
			FileHandleResults results = client.createFileHandles(files);
			
			V2WikiPage page = ClientUtils.getWikiPage(request, communityId, wikiId);
			if (page.getAttachmentFileHandleIds() == null) {
				page.setAttachmentFileHandleIds(new ArrayList<String>());
			}
			List<String> handleIds = page.getAttachmentFileHandleIds();
			
			FileHandle handle = results.getList().get(0);
			handleIds.add(handle.getId());
			
			client.updateV2WikiPage(communityId, ObjectType.ENTITY, page);

			String url = request.getContextPath() + "/page/" +communityId+ "/" +wikiId+ "/" + handle.getFileName();
			
			String funcNum = request.getParameter("CKEditorFuncNum");
			String js = "<script>window.top.CKEDITOR.tools.callFunction("+funcNum+", '"+url.toString()+"');</script>";
			response.getWriter().print(js);
			
		} catch (IOException e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	@RequestMapping(value = "/page/{communityId}/{wikiId}/{fileName}", method = RequestMethod.GET)
	public void imageURL(BridgeRequest request, HttpServletResponse response,
			@PathVariable("communityId") String communityId, @PathVariable("wikiId") String wikiId, 
			@PathVariable("fileName") String fileName) throws IOException {
		
		try {
			WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wikiId);
			URL url = synapseClient.getV2WikiAttachmentTemporaryUrl(key, fileName);
			response.getWriter().print(url.toString());
		} catch(Exception e) {
			response.getWriter().print("");
		}
	}
	
	private V2WikiPage prepareAndRetrieveModels(BridgeRequest request, String communityId, String wikiId,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException {
		
		BridgeClient client = request.getBridgeUser().getBridgeClient();
		Community community = client.getCommunity(communityId);
		
		V2WikiPage wiki = ClientUtils.getWikiPage(request, communityId, wikiId);
		
		model.addObject("community", community);
		model.setViewName("communities/edit");
		return wiki;
	}
	
	private Whitelist getCustomWhitelist() {
		return Whitelist.relaxed()
			.addAttributes(":all", "class", "style", "width");
	}
}
