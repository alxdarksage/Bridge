package org.sagebionetworks.bridge.webapp.controllers.admin;

import org.sagebionetworks.bridge.webapp.controllers.NonAjaxControllerBase;
import org.sagebionetworks.bridge.webapp.servlet.BridgeRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController extends NonAjaxControllerBase {
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String get(BridgeRequest request, ModelAndView model) throws Exception {
		return "admin/index";
	}
	
	
}
