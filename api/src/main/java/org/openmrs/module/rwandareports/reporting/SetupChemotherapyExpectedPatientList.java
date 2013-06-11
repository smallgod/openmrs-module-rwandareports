package org.openmrs.module.rwandareports.reporting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.ConsecutiveCombinedDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.WeekViewDataSetDefinition;
import org.openmrs.module.rwandareports.definition.UpcomingChemotherapyCohortDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupChemotherapyExpectedPatientList {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Program oncologyProgram;

	private ProgramWorkflow diagnosis;
	
	private Concept chemotherapy;
	
	private Concept telephone;
	
	private Concept telephone2;
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "ChemotherapyExpectedPatientList.xls",
		    "ChemotherapyPatientList.xls_", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:4,dataset:dataset2|sheet:2,row:7,dataset:dataset");
		props.put("sortWeight","5000");
		design.setProperties(props);
		
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("ChemotherapyPatientList.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Chemotherapy Expected Patient List");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Chemotherapy Expected Patient List");
					
		reportDefinition.setBaseCohortDefinition(Cohorts.createInProgramParameterizableByDate("Oncology", oncologyProgram), ParameterizableUtil.createParameterMappings("onDate=${endDate}"));
	
		reportDefinition.addParameter(new Parameter("endDate", "Week of (select Monday)", Date.class));
		createDataSetDefinition(reportDefinition);
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Chemotherapy Patient List");
		
		RowPerPatientDataSetDefinition baseSetDefinition = new RowPerPatientDataSetDefinition();
		baseSetDefinition.setName("Chemotherapy Base Patient List");
		
		UpcomingChemotherapyCohortDefinition baseCohort = new UpcomingChemotherapyCohortDefinition();
		baseCohort.setChemotherapyIndication(chemotherapy);
		baseCohort.addParameter(new Parameter("asOfDate", "asOfDate", Date.class));
		baseCohort.addParameter(new Parameter("untilDate", "untilDate", Date.class));
		
		dataSetDefinition.addFilter(baseCohort,ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate}"));
		baseSetDefinition.addFilter(baseCohort,ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate}"));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("familyName", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		dataSetDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));
		
		SortCriteria baseSortCriteria = new SortCriteria();
		baseSortCriteria.addSortElement("familyName", SortDirection.ASC);
		baseSetDefinition.setSortCriteria(baseSortCriteria);
		baseSetDefinition.addParameter(new Parameter("endDate", "Monday", Date.class));
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		baseSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		baseSetDefinition.addColumn(RowPerPatientColumns.getMiddleNameColumn("middleName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		baseSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		baseSetDefinition.addColumn(RowPerPatientColumns.getIMBId("id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate}"));
		baseSetDefinition.addColumn(RowPerPatientColumns.getDrugRegimenInformationParameterized("regimen", false, false), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getRegimenDateInformationParameterized("regimenDate", "dd/MMM/yyyy"), ParameterizableUtil.createParameterMappings("asOfDate=${endDate},untilDate=${endDate}"));
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getStateOfPatient("diagnosis", oncologyProgram, diagnosis, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone", telephone, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecent("telephone2", telephone2, null), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("address", true, true, true, true),
		    new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getAccompRelationship("accompagnateur"), new HashMap<String, Object>());
		
		ConsecutiveCombinedDataSetDefinition consecutiveDataSetDefinition = new ConsecutiveCombinedDataSetDefinition();
		consecutiveDataSetDefinition.setName("consecutiveDataSetDefinition");
		consecutiveDataSetDefinition.setBaseDefinition(dataSetDefinition);
		consecutiveDataSetDefinition.setNumberOfIterations(7);
		consecutiveDataSetDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("startDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset", consecutiveDataSetDefinition, mappings);
		
		WeekViewDataSetDefinition weekDataSetDefinition = new WeekViewDataSetDefinition();
		weekDataSetDefinition.setName("weekDataSetDefinition");
		weekDataSetDefinition.setBaseDefinition(baseSetDefinition);
		weekDataSetDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		
		Map<String, Object> baseMappings = new HashMap<String, Object>();
		baseMappings.put("startDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset2", weekDataSetDefinition, baseMappings);
		
	}
	
	private void setupProperties() {
		
		oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		diagnosis = gp.getProgramWorkflow(GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW, GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		
		chemotherapy = gp.getConcept(GlobalPropertiesManagement.CHEMOTHERAPY);
		
		telephone = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
		
		telephone2 = gp.getConcept(GlobalPropertiesManagement.SECONDARY_TELEPHONE_NUMBER_CONCEPT);
	}
}
