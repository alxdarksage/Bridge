package org.sagebionetworks.bridge.webapp.controllers.communities;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.sagebionetworks.bridge.model.Community;
import org.sagebionetworks.bridge.webapp.ClientUtils;
import org.sagebionetworks.bridge.webapp.FormUtils;
import org.sagebionetworks.bridge.webapp.forms.WikiForm;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.dao.WikiPageKey;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.v2.wiki.V2WikiPage;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	private static final String RELATIVE_URL_BASE = "https://localhost:8888/bridge";

	private static Logger logger = LogManager.getLogger(CommunityWikiController.class.getName());
	
	@Resource(name = "synapseClient")
	protected SynapseClient synapseClient;

	public void setSynapseClient(SynapseClient synapseClient) {
		this.synapseClient = synapseClient;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/new", method = RequestMethod.GET)
	public ModelAndView newWiki(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model, @ModelAttribute WikiForm wikiForm) throws Exception {
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		model.setViewName("communities/new");
		
		ClientUtils.prepareCommunitySidebarData(synapseClient, community, model);
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/new", method = RequestMethod.POST)
	public ModelAndView saveNewWiki(BridgeRequest request, @PathVariable("communityId") String communityId,
			ModelAndView model, @ModelAttribute @Valid WikiForm wikiForm, BindingResult result) throws Exception {
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		model.setViewName("communities/new");
		
		sanitizeMarkdownField(wikiForm, result);
		if (!result.hasErrors()) {
			SynapseClient client = request.getBridgeUser().getSynapseClient();
			
			String userId = request.getBridgeUser().getOwnerId();
			File temp = ClientUtils.createTempFile(request, userId+".html");
			FileUtils.writeStringToFile(temp, wikiForm.getMarkdown());
			FileHandle handle = client.createFileHandle(temp, "text/html");
			
			V2WikiPage root = client.getV2RootWikiPage(communityId, ObjectType.ENTITY);
			V2WikiPage wiki = new V2WikiPage();
			wiki.setMarkdownFileHandleId(handle.getId());
			wiki.setTitle(wikiForm.getTitle());
			wiki.setParentWikiId(root.getId());
			V2WikiPage saved = client.createV2WikiPage(communityId, ObjectType.ENTITY, wiki);

			model.setViewName( String.format("redirect:/communities/%s/wikis/%s/edit.html", communityId, saved.getId()) );
			request.setNotification("WikiUpdated");
		} else {
			ClientUtils.prepareCommunitySidebarData(synapseClient, community, model);
		}
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, ModelAndView model, WikiForm wikiForm) throws Exception {

		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		model.setViewName("communities/edit");
		
		WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, wikiId);
		V2WikiPage wiki = ClientUtils.getWikiPage(request, key);
		File markdownFile = synapseClient.downloadV2WikiMarkdown(key);
		String markdown = FileUtils.readFileToString(markdownFile);
		
		FormUtils.valuesToWikiForm(wikiForm, wiki, markdown);
		wikiForm.setIndexWiki(wikiId.equals(community.getIndexPageWikiId()));
		
		ClientUtils.prepareCommunitySidebarData(synapseClient, community, model);
		
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/edit", method = RequestMethod.POST)
	public ModelAndView update(BridgeRequest request, @PathVariable("communityId") String communityId,
			@PathVariable("wikiId") String wikiId, @ModelAttribute @Valid WikiForm wikiForm, BindingResult result,
			ModelAndView model) throws SynapseException, JSONObjectAdapterException, IOException {
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		model.setViewName("communities/edit");
		wikiForm.setWikiId(wikiId);
		wikiForm.setIndexWiki(wikiId.equals(community.getIndexPageWikiId()));

		// There are no errors that can occur here, actually.
		sanitizeMarkdownField(wikiForm, result);
		if (wikiForm.isIndexWiki() || !result.hasErrors()) {
			V2WikiPage wiki = ClientUtils.getWikiPage(request, community, wikiId);
			
			SynapseClient client = request.getBridgeUser().getSynapseClient();
			File tempDir = (File)request.getAttribute(ServletContext.TEMPDIR);
			File temp = new File(tempDir, wiki.getId()+".html");
			FileUtils.writeStringToFile(temp, wikiForm.getMarkdown());
			FileHandle handle = client.createFileHandle(temp, "text/html");
			
			wiki.setMarkdownFileHandleId(handle.getId());
			wiki.setTitle(wikiForm.getTitle());
			client.updateV2WikiPage(community.getId(), ObjectType.ENTITY, wiki);
			
			model.setViewName( String.format("redirect:/communities/%s/wikis/%s/edit.html", communityId, wikiId) );
			request.setNotification("WikiUpdated");
		} else {
			ClientUtils.prepareCommunitySidebarData(synapseClient, community, model);
		}
		return model;
	}
	
	@RequestMapping(value = "/{communityId}/wikis/{wikiId}/delete", method = RequestMethod.GET)
	public String deleteWiki(BridgeRequest request, @RequestParam("rowSelect") String rowSelect,
			@PathVariable("communityId") String communityId, @PathVariable("wikiId") String wikiId)
			throws SynapseException {
		
		Community community = request.getBridgeUser().getBridgeClient().getCommunity(communityId);
		String finalWikiId = wikiId;
		if (rowSelect != null) {
			SynapseClient client = request.getBridgeUser().getSynapseClient();

			if (!rowSelect.equals(community.getWelcomePageWikiId()) && !rowSelect.equals(community.getIndexPageWikiId())) {
				WikiPageKey key = new WikiPageKey(communityId, ObjectType.ENTITY, rowSelect);
				client.deleteV2WikiPage(key);
			}
			if (wikiId.equals(rowSelect)) {
				finalWikiId = community.getWelcomePageWikiId();
			}
			request.setNotification("WikiDeleted");
		}
		return String.format("redirect:/communities/%s/wikis/%s/edit.html", communityId, finalWikiId);
	}

	private void sanitizeMarkdownField(WikiForm wikiForm, BindingResult result) {
		String sanitizedHTML = Jsoup.clean(wikiForm.getMarkdown(), RELATIVE_URL_BASE,
				getWhitelist(wikiForm.isIndexWiki()));
		wikiForm.setMarkdown(sanitizedHTML);
		if (StringUtils.isBlank(sanitizedHTML) && !result.hasErrors()) {
			ClientUtils.fieldError(result, "wikiForm", "markdown", "NotEmpty.wikiForm.markdown");
		}
	}
	
	private Whitelist getWhitelist(boolean isIndex) {
		if (isIndex) {
			return Whitelist.none()
				.preserveRelativeLinks(true)
				.addTags("ul", "li", "a")
				.addAttributes("a", "href", "target");
		} else {
			return Whitelist.relaxed()
				.preserveRelativeLinks(true)
				.addAttributes(":all", "class", "style", "width", "target");
		}
	}

}
