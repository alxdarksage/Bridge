package org.sagebionetworks.bridge.webapp.integration.pages;

public class CommunitiesAdminPage extends AdminPages {
	
	public static final String TITLE = "Communities Administration";
	public static final String URL = "/admin/communities/index.html";
	
	private DataTableInPage dataTable;
	
	public CommunitiesAdminPage(WebDriverFacade facade) {
		super(facade);
	}
	
	public CommunityAdminPage getCommunityAdminPage() {
		getDataTable().clickRow("Fanconi Anemia");
		facade.waitForTitle(CommunityAdminPage.TITLE);
		return new CommunityAdminPage(facade);
	}
	
	public CommunityAdminPage clickNewCommunity() {
		facade.click("#newCommunityAct");
		facade.waitForTitle(CommunityAdminPage.TITLE);
		return new CommunityAdminPage(facade);
	}
	
	public DataTableInPage getDataTable() {
		if (dataTable == null) {
			dataTable = new DataTableInPage(facade);
		}
		return dataTable;
	}

}
