package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MostRecentObservation;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RecentEncounterType;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

public class SetupCROWNReports implements SetupReport {
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Concept reasonForExitingCare;
	
	private Program adultHiv;
	
	private Program pmtct;
	
	private Program pmtctCC;
	
	private List<Program> crownHivPrograms = new ArrayList<Program>();
	
	private List<EncounterType> hivEncounterTypes;
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition patientsTable = createPatientsReportDefinition();
		
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(patientsTable, "CROWN-PatientsTable.xls", "CROWN-PatientsTable.xls_",
		    null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:6,dataset:patientsDataSet");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		
		Helper.saveReportDesign(design);
		
		ReportDefinition rd =createRegimensReportDefinition();		
		ReportDesign designCSV = Helper.createCsvReportDesign(rd,"CROWN-Regimens Table.csv_");
		Helper.saveReportDesign(designCSV);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("CROWNReports.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("CROWN-Patients Table");
		Helper.purgeReportDefinition("CROWN-Regimens Table");
	}
	
	private ReportDefinition createPatientsReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("CROWN-Patients Table");		
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		createPatientsDataSet(reportDefinition);		
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private ReportDefinition createRegimensReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("CROWN-Regimens Table");	
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));				
		createRegimensDataSet(reportDefinition);
		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createPatientsDataSet(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition patientsDataset = new RowPerPatientDataSetDefinition();
		patientsDataset.setName(reportDefinition.getName() + " Data Set");
		patientsDataset.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		InProgramCohortDefinition inCrownHIVProgram = Cohorts.createInProgramParameterizableByDate(
		    "inCROWNhiv: In CROWN Programs", crownHivPrograms, "onDate");
		//Add Filters		
		patientsDataset.addFilter(inCrownHIVProgram, ParameterizableUtil.createParameterMappings("onDate=${endDate}"));
		
		//Add Columns
		patientsDataset.addColumn(RowPerPatientColumns.getTracnetId("TRACNET_ID"), new HashMap<String, Object>());
		patientsDataset.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		patientsDataset.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		patientsDataset.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		RecentEncounterType lastEncounterType = RowPerPatientColumns.getRecentEncounterType("LastVisit",hivEncounterTypes, "dd-MMM-yyyy", null);
		patientsDataset.addColumn(lastEncounterType, new HashMap<String, Object>());
		
		MostRecentObservation exitingCareReason = RowPerPatientColumns.getMostRecent("Reason for exiting care", reasonForExitingCare, "dd/MM/yyyy");
		patientsDataset.addColumn(exitingCareReason, new HashMap<String, Object>());	
		
		patientsDataset.addColumn(RowPerPatientColumns.getAccompRelationship("AccompName"), new HashMap<String, Object>());
		patientsDataset.addColumn(RowPerPatientColumns.getPatientAddress("Address", true, true, true, true),
		    new HashMap<String, Object>());
		patientsDataset.addColumn(RowPerPatientColumns.getDateOfBirth("Date of Birth", null, null),
		    new HashMap<String, Object>());
		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("patientsDataSet", patientsDataset, mappings);
	}
	
	private void createRegimensDataSet(ReportDefinition reportDefinition) {
		
		SqlDataSetDefinition sqldsd=new SqlDataSetDefinition();
		sqldsd.setSqlQuery("select o.patient_id,d.name,dro.dose,d.units,o.start_date,o.discontinued_date,o.auto_expire_date,d.route from orders o " +
				"inner join drug_order dro on o.order_id=dro.order_id " +
				"left join drug d on dro.drug_inventory_id=d.drug_id" +
				" where o.start_date<=:endDate");		
		sqldsd.addParameter(new Parameter("endDate", "End Date:", Date.class));		
		
		
		reportDefinition.addDataSetDefinition("regimensDataSet",Mapped.mapStraightThrough(sqldsd));
		
		
	}
	
	private void setupProperties() {
		reasonForExitingCare = Context.getConceptService().getConceptByUuid("3cde5ef4-26fe-102b-80cb-0017a47871b2");
		adultHiv = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pmtct = gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		pmtctCC = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
		
		crownHivPrograms.add(adultHiv);
		crownHivPrograms.add(pmtct);
		crownHivPrograms.add(pmtctCC);
		
		hivEncounterTypes = gp.getEncounterTypeList(GlobalPropertiesManagement.HIV_ENCOUNTER_TYPES,":");
	}
}
