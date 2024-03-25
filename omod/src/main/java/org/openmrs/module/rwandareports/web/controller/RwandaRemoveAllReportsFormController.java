package org.openmrs.module.rwandareports.web.controller;

import org.openmrs.module.rwandareports.util.ReportSetup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RwandaRemoveAllReportsFormController {
	
	@RequestMapping("/module/rwandareports/remove_all.form")
	public ModelAndView removeAllReports() throws Exception{
		ReportSetup.cleanTables();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
}
