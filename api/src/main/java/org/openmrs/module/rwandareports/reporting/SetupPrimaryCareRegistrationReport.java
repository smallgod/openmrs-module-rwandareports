package org.openmrs.module.rwandareports.reporting;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.PrimaryCareDataSetDefinition;

public class SetupPrimaryCareRegistrationReport extends SingleSetupReport {
	
	protected final static Log log = LogFactory.getLog(SetupPrimaryCareRegistrationReport.class);

	@Override
	public String getReportName() {
		return "Research-Primary Care Registration Data";
	}

	public void setup() throws Exception {
		log.info("Setting up report: " + getReportName());
		ReportDefinition rd = createReportDefinition();
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "PrimaryCareRegistrationData.xls", "XlsPrimaryCareRegistrationData",
		    null);
		
		createDataSetDefinition(rd);
		
		Helper.saveReportDefinition(rd);
		
		Properties props = new Properties();
		props.put(
		    "repeatingSections",
		    "sheet:1,row:2,dataset:dataSet");
		props.put("sortWeight","5000");
		design.setProperties(props);
		Helper.saveReportDesign(design);
		
	}
	
	private ReportDefinition createReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName(getReportName());
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition rd) {
		
		PrimaryCareDataSetDefinition dataSet = new PrimaryCareDataSetDefinition();
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		
		rd.addDataSetDefinition("dataSet", dataSet, mappings);
	}
}
