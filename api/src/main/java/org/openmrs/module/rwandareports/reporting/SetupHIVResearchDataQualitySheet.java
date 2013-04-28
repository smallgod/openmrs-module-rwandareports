package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculationBasedOnMultiplePatientDataDefinitions;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfProgramCompletion;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfProgramEnrolment;
import org.openmrs.module.rowperpatientreports.patientdata.definition.DateOfWorkflowStateChange;
import org.openmrs.module.rowperpatientreports.patientdata.definition.FirstDrugOrderStartedRestrictedByConceptSet;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MultiplePatientDataDefinitions;
import org.openmrs.module.rwandareports.customcalculator.DateDiffBetweenTwoDateResults;
import org.openmrs.module.rwandareports.customcalculator.DiffBetweenStatusAndRegimenManipulation;
import org.openmrs.module.rwandareports.customcalculator.DrugOrderDateManipulation;
import org.openmrs.module.rwandareports.customcalculator.ValueDateManipulation;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupHIVResearchDataQualitySheet {
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	//properties retrieved from global variables
	private Concept cd4;
	
	private Concept weight;
	
	private Concept height;
	
	private Concept who;
	
	private ProgramWorkflowState onArt;
	
	private ProgramWorkflowState onArtPedi;
	
	private ProgramWorkflowState onArtPMTCTPreg;
	
	private List<ProgramWorkflowState> pws = new ArrayList<ProgramWorkflowState>();
	
	private Concept art;
	
	private Concept artSet;
	
	private Program pmtct;
	
	private Program adultHiv;
	
	private Program pediHiv;
	
	private Program pmtctCC;
	
	private Program externalHiv;
	
	private List<Program> hivPrograms;
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "HIVResearchDataQuality.xls",
		    "HIVResearchDataQuality", null);
		
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,dataset:dataSet,sheet:1,row:6,dataset:BaselineCD4AtProgramPatientDataSet|sheet:1,row:11,dataset:BaselineCD4AtARTPatientDataSet|sheet:1,row:16,dataset:BaselineWeightAtProgramPatientDataSet|sheet:1,row:21,dataset:BaselineWeightAtARTPatientDataSet|sheet:1,row:26,dataset:NoHeightPatientDataSet|sheet:1,row:31,dataset:NoWHOPatientDataSet|sheet:1,row:36,dataset:RegimenStateDiffPatientDataSet|sheet:1,row:41,dataset:ExternalHIVPatientDataSet");
																														
		design.setProperties(props);
		
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("HIVResearchDataQuality".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("DQ-HIV Research Data Quality");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("DQ-HIV Research Data Quality");
		
		createDataSetDefinition(reportDefinition);
		
		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		reportDefinition.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		h.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("BaselineCD4AtProgramPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition2 = new RowPerPatientDataSetDefinition();
		dataSetDefinition2.setName("BaselineCD4AtARTPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition3 = new RowPerPatientDataSetDefinition();
		dataSetDefinition3.setName("BaselineWeightAtProgramPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition4 = new RowPerPatientDataSetDefinition();
		dataSetDefinition4.setName("BaselineWeightAtARTPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition5 = new RowPerPatientDataSetDefinition();
		dataSetDefinition5.setName("NoHeightPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition6 = new RowPerPatientDataSetDefinition();
		dataSetDefinition6.setName("NoWHOPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition7 = new RowPerPatientDataSetDefinition();
		dataSetDefinition7.setName("RegimenStateDiffPatientDataSet");
		
		RowPerPatientDataSetDefinition dataSetDefinition8 = new RowPerPatientDataSetDefinition();
		dataSetDefinition8.setName("ExternalHIVPatientDataSet");
		
		//Cohorts
		InProgramCohortDefinition hivProgramCohort = Cohorts.createInProgram("hivPrograms", hivPrograms);
		InProgramCohortDefinition externalCohort = Cohorts.createInProgram("externalHIV", externalHiv);
		
		CompositionCohortDefinition hivExcludeTransfer = new CompositionCohortDefinition();
		hivExcludeTransfer.setName("hivExcludeTransfer");
		hivExcludeTransfer.getSearches().put("hiv",  new Mapped<CohortDefinition>(hivProgramCohort, new HashMap<String, Object>()));
		hivExcludeTransfer.getSearches().put("transfer",  new Mapped<CohortDefinition>(externalCohort, new HashMap<String, Object>()));
		hivExcludeTransfer.setCompositionString("hiv not transfer");
		
		CompositionCohortDefinition onAllArt = new CompositionCohortDefinition();
		onAllArt.setName("onArt");
		onAllArt.getSearches().put(
		    "adult",
		    new Mapped<CohortDefinition>(Cohorts.createPatientStateCohortDefinition("onArt", onArt), new HashMap<String, Object>()));
		onAllArt.getSearches().put(
		    "pedi",
		    new Mapped<CohortDefinition>(Cohorts.createPatientStateCohortDefinition("onArtPedi", onArtPedi), new HashMap<String, Object>()));
		onAllArt.getSearches().put(
		    "pmtctPreg",
		    new Mapped<CohortDefinition>(Cohorts.createPatientStateCohortDefinition("onArtPMTCTPreg", onArtPMTCTPreg), new HashMap<String, Object>()));
		onAllArt.setCompositionString("adult or pedi or pmtctPreg");
	
		//Adding filters to all the datasets
		
		//Baseline CD4 at Program Enrollment
		dataSetDefinition.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition.addFilter(Cohorts.createPatientsWithoutBaseLineObservationProgramEnrollment(cd4, hivPrograms, 60, 30), new HashMap<String, Object>());
		
		//Baseline CD4 at ART Enrollment
		dataSetDefinition2.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition2.addFilter(onAllArt, new HashMap<String, Object>());
		dataSetDefinition2.addFilter(Cohorts.createPatientsWithoutBaseLineObservation(cd4, pws, 60, 30), new HashMap<String, Object>());
		
		//Baseline Weight at Program Enrollment
		dataSetDefinition3.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition3.addFilter(Cohorts.createPatientsWithoutBaseLineObservationProgramEnrollment(weight, hivPrograms, 60, 30), new HashMap<String, Object>());
		
		//Baseline Weight at ART Enrollment
		dataSetDefinition4.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition4.addFilter(onAllArt, new HashMap<String, Object>());
		dataSetDefinition4.addFilter(Cohorts.createPatientsWithoutBaseLineObservation(weight, pws, 60, 30), new HashMap<String, Object>());
		
		//No Height
		dataSetDefinition5.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition5.addFilter(Cohorts.createNoObservationDefintion(height), new HashMap<String, Object>());
		
		//No WHO
		dataSetDefinition6.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition6.addFilter(Cohorts.createNoObservationDefintion(who), new HashMap<String, Object>());
		
		//ART Status and Regimen don't match
		dataSetDefinition7.addFilter(hivExcludeTransfer, new HashMap<String, Object>());
		dataSetDefinition7.addFilter(Cohorts.createPatientsWhereDrugRegimenDoesNotMatchState(artSet, pws), new HashMap<String, Object>());
		
		//External HIV Programs
		dataSetDefinition8.addFilter(externalCohort, new HashMap<String, Object>());
		
		//Add Columns
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition6.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition7.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		dataSetDefinition8.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition6.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition7.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		dataSetDefinition8.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition6.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition7.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		dataSetDefinition8.addColumn(RowPerPatientColumns.getIMBId("Id"), new HashMap<String, Object>());
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition2.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition3.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition4.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition5.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition6.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition7.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		dataSetDefinition8.addColumn(RowPerPatientColumns.getTreatmentGroupOfAllHIVPatientIncludingCompleted("Group", null), new HashMap<String, Object>());
		
		MultiplePatientDataDefinitions hivEnroll = RowPerPatientColumns.getDateOfAllHIVEnrolment("hivEnrolment", "dd-MMM-yyyy");
		
		dataSetDefinition.addColumn(hivEnroll, new HashMap<String, Object>());
		dataSetDefinition3.addColumn(hivEnroll, new HashMap<String, Object>());
		dataSetDefinition7.addColumn(hivEnroll, new HashMap<String, Object>());

		
		CustomCalculationBasedOnMultiplePatientDataDefinitions minProgramDate = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		minProgramDate.addPatientDataToBeEvaluated(hivEnroll, new HashMap<String, Object>());
		minProgramDate.setName("MinProgramDate");
		minProgramDate.setCalculator(new ValueDateManipulation("hivEnrolment", -60, Calendar.DAY_OF_YEAR, "dd-MMM-yyyy"));
		dataSetDefinition.addColumn(minProgramDate, new HashMap<String, Object>());
		dataSetDefinition3.addColumn(minProgramDate, new HashMap<String, Object>());
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions maxProgramDate = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		maxProgramDate.addPatientDataToBeEvaluated(hivEnroll, new HashMap<String, Object>());
		maxProgramDate.setName("MaxProgramDate");
		maxProgramDate.setCalculator(new ValueDateManipulation("hivEnrolment", 30, Calendar.DAY_OF_YEAR, "dd-MMM-yyyy"));
		dataSetDefinition.addColumn(maxProgramDate, new HashMap<String, Object>());
		dataSetDefinition3.addColumn(maxProgramDate, new HashMap<String, Object>());
		
		DateOfWorkflowStateChange artStart = RowPerPatientColumns.getDateOfWorkflowStateChange("artWorkflowStart", art, "dd-MMM-yyyy");
		
		dataSetDefinition2.addColumn(artStart, new HashMap<String, Object>());
		dataSetDefinition4.addColumn(artStart, new HashMap<String, Object>());
		dataSetDefinition7.addColumn(artStart, new HashMap<String, Object>());
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions minARTDate = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		minARTDate.addPatientDataToBeEvaluated(artStart, new HashMap<String, Object>());
		minARTDate.setName("MinARTDate");
		minARTDate.setCalculator(new ValueDateManipulation("artWorkflowStart", -60, Calendar.DAY_OF_YEAR, "dd-MMM-yyyy"));
		dataSetDefinition2.addColumn(minARTDate, new HashMap<String, Object>());
		dataSetDefinition4.addColumn(minARTDate, new HashMap<String, Object>());
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions maxARTDate = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		maxARTDate.addPatientDataToBeEvaluated(artStart, new HashMap<String, Object>());
		maxARTDate.setName("MaxARTDate");
		maxARTDate.setCalculator(new ValueDateManipulation("artWorkflowStart", 30, Calendar.DAY_OF_YEAR, "dd-MMM-yyyy"));
		dataSetDefinition2.addColumn(maxARTDate, new HashMap<String, Object>());
		dataSetDefinition4.addColumn(maxARTDate, new HashMap<String, Object>());
		
		FirstDrugOrderStartedRestrictedByConceptSet startArt = RowPerPatientColumns.getDrugOrderForStartOfART("StartART", "dd-MMM-yyyy");
		dataSetDefinition7.addColumn(startArt,
		    new HashMap<String, Object>());
		
		dataSetDefinition7.addColumn(RowPerPatientColumns.getDateOfEarliestProgramEnrolment("pmtctEnrolment", pmtct, "dd-MMM-yyyy"), new HashMap<String, Object>());
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions dateDiff = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		dateDiff.addPatientDataToBeEvaluated(startArt, new HashMap<String, Object>());
		dateDiff.addPatientDataToBeEvaluated(artStart, new HashMap<String, Object>());
		dateDiff.setName("dateDiff");
		dateDiff.setCalculator(new DiffBetweenStatusAndRegimenManipulation());
		dataSetDefinition7.addColumn(dateDiff, new HashMap<String, Object>());
		
		DateOfProgramEnrolment externalEnrollment = RowPerPatientColumns.getDateOfProgramEnrolment("externalEnrollment", externalHiv, "dd-MMM-yyyy");
		DateOfProgramCompletion externalCompletion = RowPerPatientColumns.getDateOfProgramCompletion("externalComplete", externalHiv, "dd-MMM-yyyy");
		dataSetDefinition8.addColumn(externalEnrollment, new HashMap<String, Object>());
		dataSetDefinition8.addColumn(externalCompletion, new HashMap<String, Object>());
		
		CustomCalculationBasedOnMultiplePatientDataDefinitions dateDiffExternal = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		dateDiffExternal.addPatientDataToBeEvaluated(externalEnrollment, new HashMap<String, Object>());
		dateDiffExternal.addPatientDataToBeEvaluated(externalCompletion, new HashMap<String, Object>());
		dateDiffExternal.setName("dateDiffExternal");
		dateDiffExternal.setCalculator(new DateDiffBetweenTwoDateResults("externalEnrollment", "externalComplete"));
		dataSetDefinition8.addColumn(dateDiffExternal, new HashMap<String, Object>());
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(dataSetDefinition);
		ldsd.addBaseDefinition(dataSetDefinition2);
		ldsd.addBaseDefinition(dataSetDefinition3);
		ldsd.addBaseDefinition(dataSetDefinition4);
		ldsd.addBaseDefinition(dataSetDefinition5);
		ldsd.addBaseDefinition(dataSetDefinition6);
		ldsd.addBaseDefinition(dataSetDefinition7);
		ldsd.addBaseDefinition(dataSetDefinition8);
		ldsd.setName("Location Data Set");
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		reportDefinition.addDataSetDefinition("dataSet", ldsd, ParameterizableUtil.createParameterMappings("location=${location}"));
	}
	
	private void setupProperties() {
		
		cd4 = gp.getConcept(GlobalPropertiesManagement.CD4_TEST);
		
		weight = gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
		
		height = gp.getConcept(GlobalPropertiesManagement.HEIGHT_CONCEPT);
		
		who = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE);
		
		onArt = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE, GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
	
		onArtPedi = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE, GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		
		onArtPMTCTPreg = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE, GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		
		pws.add(onArt);
		pws.add(onArtPedi);
		pws.add(onArtPMTCTPreg);
		
		art = gp.getConcept(GlobalPropertiesManagement.ON_ART_TREATMENT_STATUS_CONCEPT);
		
		pmtct = gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
		
		adultHiv = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		
		pediHiv = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		
		pmtctCC = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
		
		externalHiv = gp.getProgram(GlobalPropertiesManagement.EXTERNAL_HIV_PROGRAM);
		
		hivPrograms = new ArrayList<Program>();
		hivPrograms.add(pmtct);
		hivPrograms.add(adultHiv);
		hivPrograms.add(pediHiv);
		hivPrograms.add(pmtctCC);
		
		artSet = gp.getConcept(GlobalPropertiesManagement.ART_DRUGS_SET);
	}
}
