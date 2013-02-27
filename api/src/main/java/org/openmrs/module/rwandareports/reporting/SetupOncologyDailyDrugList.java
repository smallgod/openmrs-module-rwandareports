package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.DrugOrderDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.DrugOrderTotalDataSetDefinition;
import org.openmrs.module.rwandareports.definition.UpcomingChemotherapyCohortDefinition;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class SetupOncologyDailyDrugList {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Concept chemotherapy;
	
	private Concept premedication;
	
	private Concept postmedication;
	
	List<Concept> indications = new ArrayList<Concept>();
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "ChemotherapyDailyDrugList.xls",
		    "ChemotherapyDailyDrugList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:2,dataset:dataSet|sheet:1,row:5,dataset:dataSet2");
		design.setProperties(props);
		
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("ChemotherapyDailyDrugList.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Chemotherapy Daily Drug List");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Chemotherapy Daily Drug List");
				
		UpcomingChemotherapyCohortDefinition baseCohort = new UpcomingChemotherapyCohortDefinition();
		baseCohort.setChemotherapyIndication(chemotherapy);
		baseCohort.addParameter(new Parameter("asOfDate", "asOfDate", Date.class));
		baseCohort.addParameter(new Parameter("untilDate", "untilDate", Date.class));
		
		reportDefinition.setBaseCohortDefinition(baseCohort,ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate+1d}"));
		reportDefinition.addParameter(new Parameter("endDate", "Date", Date.class));
		createDataSetDefinition(reportDefinition);
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		DrugOrderDataSetDefinition dataSetDefinition = new DrugOrderDataSetDefinition();
		dataSetDefinition.setName("Chemotherapy Daily Drug List");
		dataSetDefinition.addParameter(new Parameter("asOfDate", "asOfDate", Date.class));
		dataSetDefinition.setIndication(indications);
		
		DrugOrderTotalDataSetDefinition dataSetDefinition2 = new DrugOrderTotalDataSetDefinition();
		dataSetDefinition2.setName("Chemotherapy Daily Drug List");
		dataSetDefinition2.addParameter(new Parameter("asOfDate", "asOfDate", Date.class));
		dataSetDefinition2.setIndication(indications);
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("asOfDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataSet", dataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("dataSet2", dataSetDefinition2, mappings);
	}
	
	private void setupProperties() {
		
		chemotherapy = gp.getConcept(GlobalPropertiesManagement.CHEMOTHERAPY);
		
		premedication = gp.getConcept(GlobalPropertiesManagement.PREMEDICATION);
		
		postmedication = gp.getConcept(GlobalPropertiesManagement.POSTMEDICATION);
		
		indications.add(chemotherapy);
		indications.add(premedication);
		indications.add(postmedication);
	}
}
