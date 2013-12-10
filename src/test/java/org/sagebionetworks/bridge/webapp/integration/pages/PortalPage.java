package org.sagebionetworks.bridge.webapp.integration.pages;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Maps;


public class PortalPage {

	public static final String TITLE = "Patients & Researchers in Partnership";
	public static final String URL = "/portal/index.html";
	
	private WebDriverFacade facade;
	
	public PortalPage(WebDriverFacade facade) {
		this.facade = facade;
	}
	
	/*
	public Map<String, String> getCommunityIds() {
		Map<String,String> ids = Maps.newHashMap();
		List<WebElement> elements = facade.findElements(By.cssSelector("#communities a"));
		for (WebElement element : elements) {
			ids.put(element.getText().trim(), parseIdFromUrl(element.getAttribute("href")));
		}
		return ids;
	}
	
	private String parseIdFromUrl(String url) {
		String[] parts = url.split("/");
		String last = parts[parts.length-1];
		return last.replace(".html", "");
	}
	*/
	
	public void clickAdmin() {
		facade.click("#adminAct");
	}
	public void clickSignOut() {
		facade.click("#signOutAct");
	}
	
}
