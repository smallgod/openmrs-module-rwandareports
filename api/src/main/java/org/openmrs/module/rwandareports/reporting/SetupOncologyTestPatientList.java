package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupOncologyTestPatientList {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Program oncologyProgram;
	
	private Form pathResult;
	
	private Form pathSubmission;
	
	private EncounterType outpatientOncology;
	
	private Concept biopsyResult;
	
	private Concept resultsVisit;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	private Concept accession;
	
	private Concept biopsyScheduled;
	
	private Concept primaryDoctorConstruct;
	
	private Concept primaryDoctorDetails; 
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "BiopsyResultsContactList.xls",
		    "BiopsyResultsContactList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:8,dataset:dataset|sheet:2,row:8,dataset:dataset2|sheet:3,row:8,dataset:dataset3|sheet:4,row:8,dataset:dataset4");
		design.setProperties(props);
		
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("BiopsyResultsContactList.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Biopsy Results/Tracking Contact List");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Biopsy Results/Tracking Contact List");
				
		reportDefinition.addParameter(new Parameter("endDate", "Date:", Date.class));	
		reportDefinition.setBaseCohortDefinition(Cohorts.createInProgramParameterizableByDate("Oncology", oncologyProgram), ParameterizableUtil.createParameterMappings("onDate=${endDate}"));
		createDataSetDefinition(reportDefinition);
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("PathBack");
		RowPerPatientDataSetDefinition dataSetDefinition2 = new RowPerPatientDataSetDefinition();
		dataSetDefinition2.setName("PathNotBackButExpected");
		RowPerPatientDataSetDefinition dataSetDefinition3 = new RowPerPatientDataSetDefinition();
		dataSetDefinition3.setName("PathNotBack");
		RowPerPatientDataSetDefinition dataSetDefinition4 = new RowPerPatientDataSetDefinition();
		dataSetDefinition4.setName("PathNotTake");
		
		dataSetDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
		dataSetDefinition2.addParameter(new Parameter("endDate", "endDate", Date.class));
		dataSetDefinition3.addParameter(new Parameter("endDate", "endDate", Date.class));
		dataSetDefinition4.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("biopsyResultSort Date", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		SortCriteria sortCriteria2 = new SortCriteria();
		sortCriteria2.addSortElement("nextRDV", SortDirection.ASC);
		dataSetDefinition2.setSortCriteria(sortCriteria2);
		
		SortCriteria sortCriteria3 = new SortCriteria();
		sortCriteria3.addSortElement("accessionSort Date", SortDirection.ASC);
		dataSetDefinition3.setSortCriteria(sortCriteria3);
		
		SortCriteria sortCriteria4 = new SortCriteria();
		sortCriteria4.addSortElement("biopsyScheduledSort", SortDirection.ASC);
		dataSetDefinition4.setSortCriteria(sortCriteria4);
		
		//Add filters
		dataSetDefinition.addFilter(Cohorts.createPatientsWhereMostRecentEncounterIsForm(pathResult, outpatientOncology), ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		dataSetDefinition2.addFilter(Cohorts.createPatientsDueForVisit(resultsVisit, pathResult), ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		dataSetDefinition3.addFilter(Cohorts.createPatientsOverdueForVisit(pathSubmission, pathResult), ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		dataSetDefinition4.addFilter(Cohorts.createPatientsLateForVisit(biopsyScheduled, pathSubmission), ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", resultsVisit, "dd/MMM/yyyy"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", resultsVisit, "dd/MMM/yyyy"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", resultsVisit, "dd/MMM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		 
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("biopsyResult", biopsyResult, "dd/MMM/yyyy"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("accession", accession, "dd/MMM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("biopsyResultSort", biopsyResult, "yyyy/MM/dd"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("accessionSort", accession, "yyyy/MM/dd"), new HashMap<String, Object>());
		
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("biopsyScheduledSort", biopsyScheduled, "yyyy/MM/dd"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("biopsyScheduled", biopsyScheduled, "dd/MMM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentObsgroup("primaryDoctor", primaryDoctorConstruct, primaryDoctorDetails, "dd/MMM/yyyy"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecentObsgroup("primaryDoctor", primaryDoctorConstruct, primaryDoctorDetails, "dd/MMM/yyyy"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecentObsgroup("primaryDoctor", primaryDoctorConstruct, primaryDoctorDetails, "dd/MMM/yyyy"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecentObsgroup("primaryDoctor", primaryDoctorConstruct, primaryDoctorDetails, "dd/MMM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null), new HashMap<String, Object>());
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset", dataSetDefinition, mappings);
		reportDefinition.addDataSetDefinition("dataset2", dataSetDefinition2, mappings);
		reportDefinition.addDataSetDefinition("dataset3", dataSetDefinition3, mappings);
		reportDefinition.addDataSetDefinition("dataset4", dataSetDefinition4, mappings);
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		pathResult = gp.getForm(GlobalPropertiesManagement.PATH_RESULTS_FORM);
		
		outpatientOncology = gp.getEncounterType(GlobalPropertiesManagement.OUTPATIENT_ONCOLOGY_ENCOUNTER);
		
		resultsVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_PATHOLOGY_RESULT_VISIT);
		
		biopsyResult = gp.getConcept(GlobalPropertiesManagement.BIOPSY_URL);
		
		accession = gp.getConcept(GlobalPropertiesManagement.PATHOLOGY_ACCESSION_NUMBER);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
		
		primaryDoctorConstruct = gp.getConcept(GlobalPropertiesManagement.PRIMARY_DOCTOR_CONSTRUCT);
		
		primaryDoctorDetails = gp.getConcept(GlobalPropertiesManagement.PRIMARY_DOCTOR_NAME);
		
		biopsyScheduled = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_TEST_VISIT);
		
		pathSubmission = gp.getForm(GlobalPropertiesManagement.PATH_SUBMISSION_FORM);
	}
}
