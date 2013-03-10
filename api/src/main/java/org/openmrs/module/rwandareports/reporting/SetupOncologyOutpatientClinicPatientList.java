package org.openmrs.module.rwandareports.reporting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.RangeComparator;
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

public class SetupOncologyOutpatientClinicPatientList {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Program oncologyProgram;

	private ProgramWorkflow diagnosis;
	
	private Concept scheduledVisit;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "OncologyOutpatientClinicConsult.xls",
		    "OncologyOutpatientClinicConsult.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:7,dataset:dataSet");
		design.setProperties(props);
		
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("OncologyOutpatientClinicConsult.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Oncology Outpatient Clinic Patient List");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Oncology Outpatient Clinic Patient List");
				
		reportDefinition.addParameter(new Parameter("endDate", "Week of (select Monday)", Date.class));	
		reportDefinition.setBaseCohortDefinition(Cohorts.createInProgramParameterizableByDate("Oncology", oncologyProgram), ParameterizableUtil.createParameterMappings("onDate=${endDate}"));
		createDataSetDefinition(reportDefinition);
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Chemotherapy Missed List");
		
		dataSetDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDV", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		//Add filters
		dataSetDefinition.addFilter(Cohorts.createDateObsCohortDefinition(scheduledVisit, RangeComparator.GREATER_EQUAL, RangeComparator.LESS_EQUAL, TimeModifier.LAST), ParameterizableUtil.createParameterMappings("value2=${endDate+7d},value1=${endDate}"));
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("nextRDV", scheduledVisit, "dd/MMM/yyyy"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		 
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"), new HashMap<String, Object>());
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataSet", dataSetDefinition, mappings);
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		scheduledVisit = gp.getConcept(GlobalPropertiesManagement.ONCOLOGY_SCHEDULED_OUTPATIENT_VISIT);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
	}
}
