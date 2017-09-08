package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.util.RwandaReportsUtil;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupDiabetesQuarterlyAndMonthReport {
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	// properties
	private Program DMProgram;
	
	private List<Program> DMPrograms = new ArrayList<Program>();
	
	private int DMEncounterTypeId;
	
	private EncounterType DMEncounterType;
	
	private Form DDBform;
	
	private Form DiabetesFlowVisit;
	
	private List<Form> DDBforms = new ArrayList<Form>();
	
	private List<EncounterType> patientsSeenEncounterTypes = new ArrayList<EncounterType>();
	
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	
	private List<String> onOrBefOnOrAf = new ArrayList<String>();
	
	private Concept glucose;
	
	private Concept weight;
	
	private Concept height;
	
	private Concept diastolicBP;
	
	private Concept systolicBP;
	
	private Concept hbA1c;
	
	private Concept sensationInRightFoot;
	
	private Concept sensationInLeftFoot;
	
	private Concept creatinine;
	
	private Concept insulin7030;
	
	private Concept insulinLente;
	
	private Concept insulinRapide;
	
	private Concept lisinopril;
	
	private Concept captopril;
	
	private List<Concept> lisinoprilCaptopril = new ArrayList<Concept>();
	
	private List<Concept> diabetesDrugConcepts = new ArrayList<Concept>();
	
	private Concept metformin;
	
	private Concept glibenclimide;
	
	private ProgramWorkflowState diedState;
	
	private Concept admitToHospital;
	
	private Concept locOfHosp;
	
	private List<Drug> onAceInhibitorsDrugs = new ArrayList<Drug>();
	
	List<Concept> neuropathyConcepts = new ArrayList<Concept>();
	
	List<Concept> insulinConcepts = new ArrayList<Concept>();
	
	List<Concept> metforminAndGlibenclimideConcepts = new ArrayList<Concept>();

	private Concept NCDSpecificOutcomes;
	private Concept NCDRelatedDeathOutcomes;

	private Concept exitReasonFromCare;
	private Concept patientDiedConcept;

	List<Concept> DeathOutcomeResons=new  ArrayList<Concept>();

	private Concept unknownCauseDeathOutcomes;
	private Concept otherCauseOfDeathOutcomes;

	private Concept urinaryAlbumin;



	public void setup() throws Exception {
		
		setUpProperties();
		
		//Monthly report set-up
		ReportDefinition monthlyRd = new ReportDefinition();
		monthlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		monthlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));

		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		monthlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		monthlyRd.setName("NCD-Diabetes Indicator Report-Monthly");
		
		monthlyRd.addDataSetDefinition(createMonthlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		// Quarterly Report Definition: Start
		
		ReportDefinition quarterlyRd = new ReportDefinition();
		quarterlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		quarterlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));


		quarterlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		quarterlyRd.setName("NCD-Diabetes Indicator Report-Quarterly");
		
		quarterlyRd.addDataSetDefinition(createQuarterlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		// Quarterly Report Definition: End
		
		ProgramEnrollmentCohortDefinition patientEnrolledInDM = new ProgramEnrollmentCohortDefinition();
		patientEnrolledInDM.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInDM.setPrograms(DMPrograms);
		
		monthlyRd.setBaseCohortDefinition(patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));
		quarterlyRd.setBaseCohortDefinition(patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));
		
		Helper.saveReportDefinition(monthlyRd);
		Helper.saveReportDefinition(quarterlyRd);
		
		ReportDesign monthlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRd,
		    "DM_Monthly_Indicator_Report.xls", "Diabetes Monthly Indicator Report (Excel)", null);
		Properties monthlyProps = new Properties();
		monthlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Monthly Data Set");
		monthlyProps.put("sortWeight","5000");
		monthlyDesign.setProperties(monthlyProps);
		Helper.saveReportDesign(monthlyDesign);
		
		ReportDesign quarterlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(quarterlyRd,
		    "DM_Quarterly_Indicator_Report.xls", "Diabetes Quarterly Indicator Report (Excel)", null);
		Properties quarterlyProps = new Properties();
		quarterlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Quarterly Data Set");
		quarterlyProps.put("sortWeight","5000");
		quarterlyDesign.setProperties(quarterlyProps);
		Helper.saveReportDesign(quarterlyDesign);
		
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Diabetes Monthly Indicator Report (Excel)".equals(rd.getName())
			        || "Diabetes Quarterly Indicator Report (Excel)".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("NCD-Diabetes Indicator Report-Quarterly");
		Helper.purgeReportDefinition("NCD-Diabetes Indicator Report-Monthly");
		
	}
	
	// Create Monthly Location Data set
	public LocationHierachyIndicatorDataSetDefinition createMonthlyLocationDataSet() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createMonthlyEncounterBaseDataSet());
		ldsd.addBaseDefinition(createMonthlyBaseDataSet());
		ldsd.setName("Encounter Monthly Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		return ldsd;
	}
	
	private EncounterIndicatorDataSetDefinition createMonthlyEncounterBaseDataSet() {
		
		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
		
		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		createMonthlyIndicators(eidsd);
		return eidsd;
	}
	
	private void createMonthlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
		
		SqlEncounterQuery patientVisitsToDMClinic = new SqlEncounterQuery();
		
		patientVisitsToDMClinic
		        .setQuery("select encounter_id from encounter where encounter_type="
		                + DMEncounterType.getEncounterTypeId()
		                + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0 group by encounter_datetime, patient_id");
		patientVisitsToDMClinic.setName("patientVisitsToDMClinic");
		patientVisitsToDMClinic.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientVisitsToDMClinic.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		EncounterIndicator patientVisitsToDMClinicMonthIndicator = new EncounterIndicator();
		patientVisitsToDMClinicMonthIndicator.setName("patientVisitsToDMClinicMonthIndicator");
		patientVisitsToDMClinicMonthIndicator.setEncounterQuery(new Mapped<EncounterQuery>(patientVisitsToDMClinic,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
		
		dsd.addColumn(patientVisitsToDMClinicMonthIndicator);
		
	}
	
	//Create Quarterly Encounter Data set
	
	public LocationHierachyIndicatorDataSetDefinition createQuarterlyLocationDataSet() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createEncounterQuarterlyBaseDataSet());
		ldsd.addBaseDefinition(createQuarterlyBaseDataSet());
		ldsd.setName("Encounter Quarterly Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		return ldsd;
	}
	
	private EncounterIndicatorDataSetDefinition createEncounterQuarterlyBaseDataSet() {
		
		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createQuarterlyIndicators(eidsd);
		return eidsd;
	}
	
	private void createQuarterlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
		//=======================================================================
		//  A1: Total # of patient visits to DM clinic in the last quarter
		//==================================================================
		SqlEncounterQuery patientVisitsToDMClinic = new SqlEncounterQuery();
		
		patientVisitsToDMClinic.setQuery("select e.encounter_id from encounter e where e.encounter_type="
		        + DMEncounterTypeId +" and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 group by encounter_datetime, patient_id");
		patientVisitsToDMClinic.setName("patientVisitsToDMClinic");
		patientVisitsToDMClinic.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientVisitsToDMClinic.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		EncounterIndicator patientVisitsToDMClinicQuarterIndicator = new EncounterIndicator();
		patientVisitsToDMClinicQuarterIndicator.setName("patientVisitsToDMClinicQuarterIndicator");
		patientVisitsToDMClinicQuarterIndicator.setEncounterQuery(new Mapped<EncounterQuery>(patientVisitsToDMClinic,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
		
		dsd.addColumn(patientVisitsToDMClinicQuarterIndicator);
		
	}
	
	// create monthly cohort Data set
	
	private CohortIndicatorDataSetDefinition createMonthlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		
		createMonthlyIndicators(dsd);
		return dsd;
	}
	
	// create quarterly cohort Data set
	private CohortIndicatorDataSetDefinition createQuarterlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Quarterly Cohort Data Set");
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		createQuarterlyIndicators(dsd);
		return dsd;
	}
	
	private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {
		
		//=======================================================================
		//  A2: Total # of patients seen in the last month/quarter
		//==================================================================
		
		EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
		    onOrAfterOnOrBefore, patientsSeenEncounterTypes);
		
		EncounterCohortDefinition patientWithDDB = Cohorts.createEncounterBasedOnForms("patientWithDDB",
		    onOrAfterOnOrBefore, DDBforms);
		
		CompositionCohortDefinition patientsSeenComposition = new CompositionCohortDefinition();
		patientsSeenComposition.setName("patientsSeenComposition");
		patientsSeenComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenComposition.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenComposition.setCompositionString("1 OR 2");
		
		CohortIndicator patientsSeenQuarterIndicator = Indicators.newCountIndicator("patientsSeenMonthThreeIndicator",
		    patientsSeenComposition,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		CohortIndicator patientsSeenMonthOneIndicator = Indicators.newCountIndicator("patientsSeenMonthOneIndicator",
		    patientsSeenComposition,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-1m+1d},onOrBefore=${endDate}"));
		CohortIndicator patientsSeenMonthTwoIndicator = Indicators.newCountIndicator("patientsSeenMonthTwoIndicator",
		    patientsSeenComposition,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m+1d}"));
		CohortIndicator patientsSeenMonthThreeIndicator = Indicators.newCountIndicator("patientsSeenMonthThreeIndicator",
		    patientsSeenComposition,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate-2m+1d}"));






		//========================================================================================
		//  Active patients with no exit in Reporting periode
		//=======================================================================================

		SqlCohortDefinition programOutcomeNCDRelatedDeath=Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("NCDRelatedDeath",NCDSpecificOutcomes,NCDRelatedDeathOutcomes);

		SqlCohortDefinition obsNCDRelatedDeath=Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("obsNCDRelatedDeath",NCDSpecificOutcomes,NCDRelatedDeathOutcomes);



		CompositionCohortDefinition NCDRelatedDeath= new CompositionCohortDefinition();
		NCDRelatedDeath.setName("NCDRelatedDeath");
		NCDRelatedDeath.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		NCDRelatedDeath.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		NCDRelatedDeath.getSearches().put("1",new Mapped<CohortDefinition>(programOutcomeNCDRelatedDeath, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		NCDRelatedDeath.getSearches().put("2",new Mapped<CohortDefinition>(obsNCDRelatedDeath, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		NCDRelatedDeath.setCompositionString("1 OR 2");

		SqlCohortDefinition patientsInPatientDiedState = Cohorts
				.createPatientsInStateNotPredatingProgramEnrolment(diedState);

		SqlCohortDefinition patientWithAnyReasonForExitingFromCare=Cohorts.getPatientsWithObsGreaterThanNtimesByStartDateEndDate("patientWithAnyReasonForExitingFromCare",exitReasonFromCare,1);
		SqlCohortDefinition obsPatientDiedReasonForExitingFromCare=Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("obsPatientDiedReasonForExitingFromCare",exitReasonFromCare,patientDiedConcept);

		CompositionCohortDefinition activePatientWithNoExitBeforeQuarterStart = new CompositionCohortDefinition();
		activePatientWithNoExitBeforeQuarterStart.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithNoExitBeforeQuarterStart.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithNoExitBeforeQuarterStart.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithNoExitBeforeQuarterStart.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithNoExitBeforeQuarterStart.getSearches().put("1",new Mapped<CohortDefinition>(patientsInPatientDiedState, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore-3m},onOrAfter=${onOrAfter-9m}")));
		activePatientWithNoExitBeforeQuarterStart.getSearches().put("2",new Mapped<CohortDefinition>(NCDRelatedDeath, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore-3m},onOrAfter=${onOrAfter-9m}")));
		activePatientWithNoExitBeforeQuarterStart.getSearches().put("3",new Mapped<CohortDefinition>(obsPatientDiedReasonForExitingFromCare, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore-3m},onOrAfter=${onOrAfter-9m}")));
		activePatientWithNoExitBeforeQuarterStart.getSearches().put("4",new Mapped<CohortDefinition>(patientWithAnyReasonForExitingFromCare, ParameterizableUtil.createParameterMappings("startDate=${startDate-9m},endDate=${endDate-3m}")));
		activePatientWithNoExitBeforeQuarterStart.getSearches().put("5",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-9m}")));
		activePatientWithNoExitBeforeQuarterStart.setCompositionString("5 AND (NOT (1 OR 2 OR 3 OR 4))");


		CohortIndicator activePatientIndicator = Indicators.newCountIndicator("activePatientIndicator",
				activePatientWithNoExitBeforeQuarterStart,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("Active", "Total # of Active patients : seen in the last 12 months", new Mapped(
				activePatientIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



		ProgramEnrollmentCohortDefinition patientEnrolledInDMProgram = Cohorts.createProgramEnrollmentParameterizedByStartEndDate(
				"Enrolled In DM", DMProgram);

		ProgramEnrollmentCohortDefinition patientEnrolledInDMProgramByEndDate = Cohorts.createProgramEnrollmentEverByEndDate("Enrolled Ever In DM", DMProgram);

		CompositionCohortDefinition patientCurrentEnrolledInDMAndSeenInSameQuarter=new CompositionCohortDefinition();
		patientCurrentEnrolledInDMAndSeenInSameQuarter.setName("patientCurrentEnrolledInDMAndSeenInSameQuarter");/*
		patientCurrentEnrolledInDMAndSeenInSameQuarter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientCurrentEnrolledInDMAndSeenInSameQuarter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));*/
		patientCurrentEnrolledInDMAndSeenInSameQuarter.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientCurrentEnrolledInDMAndSeenInSameQuarter.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientCurrentEnrolledInDMAndSeenInSameQuarter.getSearches().put(
				"1",new Mapped<CohortDefinition>(patientEnrolledInDMProgram, ParameterizableUtil
						.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore},enrolledOnOrAfter=${enrolledOnOrAfter}")));
		patientCurrentEnrolledInDMAndSeenInSameQuarter.getSearches().put(
				"2",new Mapped<CohortDefinition>(patientEnrolledInDMProgramByEndDate, ParameterizableUtil
						.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore-3m}")));
		patientCurrentEnrolledInDMAndSeenInSameQuarter.getSearches().put(
				"3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));

		//patientCurrentEnrolledInDMAndSeenInSameQuarter.setCompositionString("(1 and (not 2)) and 3");
		patientCurrentEnrolledInDMAndSeenInSameQuarter.setCompositionString("1 and (not 2)");




		CohortIndicator patientCurrentEnrolledInDMAndSeenInSameQuarterIndicator = Indicators.newCountIndicator(
				"patientCurrentEnrolledInDMAndSeenInSameQuarterIndicator", patientCurrentEnrolledInDMAndSeenInSameQuarter,
				ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));

		dsd.addColumn(
				"NewInQ",
				"Total # of new patients enrolled in the last quarter",
				new Mapped(patientCurrentEnrolledInDMAndSeenInSameQuarterIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//=============================================
// A3: Total # of patients ever enrolled
//=============================================
		CohortIndicator patientEverEnrolledInDMIndicator = Indicators.newCountIndicator(
				"patientEverEnrolledInDMIndicator", patientEnrolledInDMProgramByEndDate,
				ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate+1d}"));

		dsd.addColumn(
				"EverEnrolled",
				"Total # of patients evere enrolled in the last quarter",
				new Mapped(patientEverEnrolledInDMIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//============================================
// A4: Percentage of active patients
//============================================
		InProgramCohortDefinition inDMProgramByEndDate=Cohorts.createInProgramParameterizableByDate("In DM by EndDate",DMProgram);
		ProgramEnrollmentCohortDefinition completedInDMProgramByEndDate=Cohorts.createProgramCompletedByEndDate("Completed DM by EndDate",DMProgram);


		CompositionCohortDefinition currentlyInProgramAndNotCompleted= new CompositionCohortDefinition();
		currentlyInProgramAndNotCompleted.setName("currentlyInProgramAndNotCompleted");
		currentlyInProgramAndNotCompleted.addParameter(new Parameter("onDate", "onDate", Date.class));
		currentlyInProgramAndNotCompleted.addParameter(new Parameter("completedOnOrBefore", "completedOnOrBefore", Date.class));
		currentlyInProgramAndNotCompleted.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(inDMProgramByEndDate, ParameterizableUtil
						.createParameterMappings("onDate=${onDate}")));
		currentlyInProgramAndNotCompleted.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(completedInDMProgramByEndDate, ParameterizableUtil
						.createParameterMappings("completedOnOrBefore=${completedOnOrBefore}")));
		currentlyInProgramAndNotCompleted.setCompositionString("1 and (not 2)");

		CohortIndicator currentlyInProgramAndNotCompletedIndicator=Indicators.newCountIndicator(
				"currentlyInProgramAndNotCompletedIndicator", currentlyInProgramAndNotCompleted,
				ParameterizableUtil.createParameterMappings("onDate=${endDate},completedOnOrBefore=${endDate}"));

		dsd.addColumn(
				"CurEnrol",
				"Percentage of active patients)",
				new Mapped(currentlyInProgramAndNotCompletedIndicator, ParameterizableUtil
						.createParameterMappings("endDate=${endDate}")), "");

//==============================================
// A5: Proportion of all active patients with age<26
//===============================================


		AgeCohortDefinition under26=Cohorts.createUnderAgeCohort("under26",26);


		CompositionCohortDefinition activePatientUnder26 = new CompositionCohortDefinition();
		activePatientUnder26.setName("activePatientUnder26");
		activePatientUnder26.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientUnder26.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientUnder26.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientUnder26.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientUnder26.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		activePatientUnder26.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientUnder26.getSearches().put("2",new Mapped<CohortDefinition>(under26, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		activePatientUnder26.setCompositionString("1 and 2");


		CohortIndicator activePatientUnder26Indicator = Indicators.newCountIndicator("activePatientUnder26Indicator",
				activePatientUnder26,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));


		dsd.addColumn("ActiveUnder26", "Total # of Active patients : seen in the last 12 months Under 26", new Mapped(
				activePatientUnder26Indicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");




//==============================================
// A6: Proportion of all active patients with age<=15
//===============================================


		AgeCohortDefinition under16=Cohorts.createUnderAgeCohort("under16",16);

		CompositionCohortDefinition activePatientUnder16 = new CompositionCohortDefinition();
		activePatientUnder16.setName("activePatientUnder16");
		activePatientUnder16.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientUnder16.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientUnder16.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientUnder16.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientUnder16.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		activePatientUnder16.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientUnder16.getSearches().put("2",new Mapped<CohortDefinition>(under16, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		activePatientUnder16.setCompositionString("1 and 2");


		CohortIndicator activePatientUnder16Indicator = Indicators.newCountIndicator("activePatientUnder16Indicator",
				activePatientUnder16,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));


		dsd.addColumn("ActiveUnder16", "Total # of Active patients : seen in the last 12 months Under 16", new Mapped(
				activePatientUnder16Indicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


//==============================================
// A7: Of all active patients, % male
//===============================================

		GenderCohortDefinition malePatients = Cohorts.createMaleCohortDefinition("Male patients");



		CompositionCohortDefinition activeMalePatients = new CompositionCohortDefinition();
		activeMalePatients.setName("activeMalePatients");
		activeMalePatients.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeMalePatients.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		activeMalePatients.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activeMalePatients.getSearches().put("2",new Mapped<CohortDefinition>(malePatients, null));
		activeMalePatients.setCompositionString("1 and 2");


		CohortIndicator activeMalePatientsIndicator = Indicators.newCountIndicator("activeMalePatientsIndicator",
				activeMalePatients,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("ActiveMale", "Total # of Active patients : seen in the last 12 months who are Male", new Mapped(
				activeMalePatientsIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



//=======================================
//  A8: Total active patients disaggregated by BMI category
//=======================================

		SqlCohortDefinition patientsWithBMILessThan18=Cohorts.getPatientsWithBMI("patientsWithBMILessThan18","< 18",weight,height);

		SqlCohortDefinition patientsWithBMIBetween18And25=Cohorts.getPatientsWithBMI("patientsWithBMIBetween18And25"," between 18.0 and 25.0",weight,height);

		SqlCohortDefinition patientsWithBMIAbove25AndLessThan30=Cohorts.getPatientsWithBMI("patientsWithBMIAbove25AndLessThan30"," between 25.1 and 29.9",weight,height);

		SqlCohortDefinition patientsWithBMIGreaterThanOrEqual30=Cohorts.getPatientsWithBMI("patientsWithBMILessThan18",">= 30.0",weight,height);


		CompositionCohortDefinition activeUnderweight = new CompositionCohortDefinition();
		activeUnderweight.setName("activeUnderweight");
		activeUnderweight.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeUnderweight.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeUnderweight.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeUnderweight.addParameter(new Parameter("endDate", "endDate", Date.class));
		activeUnderweight.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activeUnderweight.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithBMILessThan18, null));
		activeUnderweight.setCompositionString("1 and 2");

		CohortIndicator activeUnderweightIndicator = Indicators.newCountIndicator("activeUnderweightIndicator",
				activeUnderweight, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("Underweight", "Total # of Active patients : Underweight", new Mapped(
				activeUnderweightIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



		CompositionCohortDefinition activeNormal = new CompositionCohortDefinition();
		activeNormal.setName("activeNormal");
		activeNormal.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeNormal.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeNormal.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeNormal.addParameter(new Parameter("endDate", "endDate", Date.class));
		activeNormal.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activeNormal.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithBMIBetween18And25, null));
		activeNormal.setCompositionString("1 and 2");

		CohortIndicator activeNormalIndicator = Indicators.newCountIndicator("activeNormalIndicator",
				activeNormal, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("Normal", "Total # of Active patients : Normal", new Mapped(
				activeNormalIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


		CompositionCohortDefinition activeOverweight = new CompositionCohortDefinition();
		activeOverweight.setName("activeOverweight");
		activeOverweight.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeOverweight.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeOverweight.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeOverweight.addParameter(new Parameter("endDate", "endDate", Date.class));
		activeOverweight.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activeOverweight.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithBMIAbove25AndLessThan30, null));
		activeOverweight.setCompositionString("1 and 2");

		CohortIndicator activeOverweightIndicator = Indicators.newCountIndicator("activeOverweightIndicator",
				activeOverweight, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("Overweight", "Total # of Active patients : Overweight", new Mapped(
				activeOverweightIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

		CompositionCohortDefinition activeObese = new CompositionCohortDefinition();
		activeObese.setName("activeObese");
		activeObese.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeObese.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeObese.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeObese.addParameter(new Parameter("endDate", "endDate", Date.class));
		activeObese.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activeObese.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithBMIGreaterThanOrEqual30, null));
		activeObese.setCompositionString("1 and 2");

		CohortIndicator activeObeseIndicator = Indicators.newCountIndicator("activeObeseIndicator",
				activeObese, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("Obese", "Total # of Active patients : Obese", new Mapped(
				activeObeseIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");




		//=================================================================================
		//  A10: % of deaths which are disease related
		//=================================================================================
//Numerator

		CohortIndicator NCDRelatedDeathIndicator = Indicators.newCountIndicator(
				"NCDRelatedDeathIndicator", NCDRelatedDeath,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		dsd.addColumn("A10N", "% of deaths which are disease related", new Mapped(
				NCDRelatedDeathIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//Denominator

		SqlCohortDefinition patientDiedOfNCDRelatedDeath= Cohorts.getPatientsWithOutcomeprogramEndReasons("patientDiedOfNCDRelatedDeath",NCDSpecificOutcomes,DeathOutcomeResons);


		CompositionCohortDefinition activePatientsInPatientDiedStateOrNCDRelatedDeath = new CompositionCohortDefinition();
		activePatientsInPatientDiedStateOrNCDRelatedDeath.setName("activePatientsInPatientDiedState");
		activePatientsInPatientDiedStateOrNCDRelatedDeath.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-9m+1d}")));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(patientsInPatientDiedState, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.getSearches().put(
				"3",
				new Mapped<CohortDefinition>(patientDiedOfNCDRelatedDeath, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));

		activePatientsInPatientDiedStateOrNCDRelatedDeath.getSearches().put(
				"4",
				new Mapped<CohortDefinition>(obsPatientDiedReasonForExitingFromCare, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		activePatientsInPatientDiedStateOrNCDRelatedDeath.getSearches().put(
				"5",
				new Mapped<CohortDefinition>(NCDRelatedDeath, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));

		activePatientsInPatientDiedStateOrNCDRelatedDeath.setCompositionString("1 AND (2 OR 3 OR 4 OR 5)");

		CohortIndicator activePatientsInPatientDiedStateInQuarterIndicator = Indicators
				.newCountIndicator(
						"activePatientsInPatientDiedStateInQuarterIndicator",
						activePatientsInPatientDiedStateOrNCDRelatedDeath,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		//========================================================
		//        Adding columns to data set definition         //
		//========================================================


		dsd.addColumn(
				"DiedQ",
				"Total active patients, number who Died in quarter",
				new Mapped(activePatientsInPatientDiedStateInQuarterIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");





//===================================
// B1: Of total active patients seen in the last quarter, % who had Urinary Albumin tested at least within 12 months of the end of the reporting period.
//======================================
		SqlCohortDefinition patientWithurinaryAlbumin=Cohorts.getPatientsWithObservationsByStartDateAndEndDate("patientWithurinaryAlbumin", urinaryAlbumin);

		CompositionCohortDefinition activePatientWithurinaryAlbumin = new CompositionCohortDefinition();
		activePatientWithurinaryAlbumin.setName("activePatientWithurinaryAlbumin");
		activePatientWithurinaryAlbumin.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithurinaryAlbumin.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithurinaryAlbumin.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithurinaryAlbumin.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithurinaryAlbumin.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientWithurinaryAlbumin.getSearches().put("2",new Mapped<CohortDefinition>(patientWithurinaryAlbumin, ParameterizableUtil.createParameterMappings("startDate=${endDate-12m},endDate=${endDate}")));
		activePatientWithurinaryAlbumin.setCompositionString("1 and 2");

		CohortIndicator activePatientWithurinaryAlbuminIndicator = Indicators.newCountIndicator("activePatientWithurinaryAlbuminIndicator",
				activePatientWithurinaryAlbumin, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("B1", "Total # of Active patients : Albumin documented in 12 months", new Mapped(
				activePatientWithurinaryAlbuminIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



//======================================================
// B2:  Of total active patients seen in the last quarter, % who had Creatinine tested at least within 12 months of the reporting period
//======================================================

		SqlCohortDefinition patientWithCreatinine=Cohorts.getPatientsWithObservationsByStartDateAndEndDate("patientWithCreatinine", creatinine);

		CompositionCohortDefinition activePatientWithCreatinine = new CompositionCohortDefinition();
		activePatientWithCreatinine.setName("activePatientWithCreatinine");
		activePatientWithCreatinine.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithCreatinine.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithCreatinine.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithCreatinine.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithCreatinine.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientWithCreatinine.getSearches().put("2",new Mapped<CohortDefinition>(patientWithCreatinine, ParameterizableUtil.createParameterMappings("startDate=${endDate-12m},endDate=${endDate}")));
		activePatientWithCreatinine.setCompositionString("1 and 2");

		CohortIndicator activePatientWithCreatinineIndicator = Indicators.newCountIndicator("activePatientWithCreatinineIndicator",
				activePatientWithCreatinine, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("B2", "Total # of Active patients : Creatinine documented in 12 months", new Mapped(
				activePatientWithCreatinineIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


//======================================================
// B3: Total active Diabetes patients who enrolled at least 3 months before the end of the reporting period, % of who had HbA1C documented  in the Q
// ======================================================

		SqlCohortDefinition patientWithHBA1C=Cohorts.getPatientsWithObservationsByStartDateAndEndDate("patientWithCreatinine", hbA1c);

		CompositionCohortDefinition activePatientWithHBA1C = new CompositionCohortDefinition();
		activePatientWithHBA1C.setName("activePatientWithHBA1C");
		activePatientWithHBA1C.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithHBA1C.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithHBA1C.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithHBA1C.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithHBA1C.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		activePatientWithHBA1C.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientWithHBA1C.getSearches().put("2",new Mapped<CohortDefinition>(patientWithHBA1C, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		activePatientWithHBA1C.getSearches().put("3",new Mapped<CohortDefinition>(patientEnrolledInDMProgramByEndDate, ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore-3m}")));
		activePatientWithHBA1C.setCompositionString("1 and 2 and 3");

		CohortIndicator activePatientWithHBA1CIndicator = Indicators.newCountIndicator("activePatientWithHBA1CIndicator",
				activePatientWithHBA1C, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},enrolledOnOrBefore=${endDate}"));


		dsd.addColumn("B3N", "Total # of Active patients Enrolled before the Q : HbA1C documented  in the Q", new Mapped(
				activePatientWithHBA1CIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



		CompositionCohortDefinition activePatientEnrolledBeforeTheQ = new CompositionCohortDefinition();
		activePatientEnrolledBeforeTheQ.setName("activePatientEnrolledBeforeTheQ");
		activePatientEnrolledBeforeTheQ.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientEnrolledBeforeTheQ.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientEnrolledBeforeTheQ.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientEnrolledBeforeTheQ.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientEnrolledBeforeTheQ.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		activePatientEnrolledBeforeTheQ.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientEnrolledBeforeTheQ.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInDMProgramByEndDate, ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore-3m}")));
		activePatientEnrolledBeforeTheQ.setCompositionString("1 and 2");

		CohortIndicator activePatientEnrolledBeforeTheQIndicator = Indicators.newCountIndicator("activePatientEnrolledBeforeTheQIndicator",
				activePatientEnrolledBeforeTheQ, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},enrolledOnOrBefore=${endDate}"));


		dsd.addColumn("B3D", "Total # of Active patients Enrolled before the Q ", new Mapped(
				activePatientEnrolledBeforeTheQIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


//===============================================
// B4: % of active patients on oral therapy only
//===============================================

		SqlCohortDefinition patientOnInsulin = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate(
				"patientOnInsulinAtLastVist", insulinConcepts);

		SqlCohortDefinition onMetforminOrGlibenclimide = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate(
				"onMetforminOrGlibenclimide", metforminAndGlibenclimideConcepts);

		CompositionCohortDefinition activePatientOnOralTherapyOnly = new CompositionCohortDefinition();
		activePatientOnOralTherapyOnly.setName("activePatientOnOralTherapyOnly");
		activePatientOnOralTherapyOnly.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientOnOralTherapyOnly.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientOnOralTherapyOnly.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientOnOralTherapyOnly.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientOnOralTherapyOnly.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientOnOralTherapyOnly.getSearches().put("2",new Mapped<CohortDefinition>(onMetforminOrGlibenclimide, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		activePatientOnOralTherapyOnly.getSearches().put("3",new Mapped<CohortDefinition>(patientOnInsulin, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		activePatientOnOralTherapyOnly.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator activePatientOnOralTherapyOnlyIndicator = Indicators.newCountIndicator("activePatientOnOralTherapyOnlyIndicator",
				activePatientOnOralTherapyOnly, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("B4N", "Total # of active patients on oral therapy only ", new Mapped(
				activePatientOnOralTherapyOnlyIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");





		//=======================================================================
		// C1: Of total DM active patients, % with a visit in last 12 months but no visit within last 6 months.
		//==================================================================

		CompositionCohortDefinition activeAndNotSeenIn6MonthsPatients = new CompositionCohortDefinition();
		activeAndNotSeenIn6MonthsPatients.setName("activeAndNotSeenIn6MonthsPatients");
		activeAndNotSeenIn6MonthsPatients.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeAndNotSeenIn6MonthsPatients.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeAndNotSeenIn6MonthsPatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeAndNotSeenIn6MonthsPatients.addParameter(new Parameter("endDate", "endDate", Date.class));

		activeAndNotSeenIn6MonthsPatients.getSearches().put("1",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-9m}")));
		activeAndNotSeenIn6MonthsPatients.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activeAndNotSeenIn6MonthsPatients.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-3m}")));
		activeAndNotSeenIn6MonthsPatients.setCompositionString("1 AND 2 AND (NOT 3)");

		CohortIndicator activeAndNotSeenIn6MonthsPatientsIndicator = Indicators.newCountIndicator(
				"activeAndNotSeenIn6MonthsPatientsIndicator",
				activeAndNotSeenIn6MonthsPatients,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));

		dsd.addColumn(
				"C1",
				"Of total DM active patients, % with a visit in last 12 months but no visit within last 6 months",
				new Mapped(activeAndNotSeenIn6MonthsPatientsIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//=======================================================
// Of all active patients with HbA1c tested in the last 6 months, % with last HbA1c <=8 ///of all active for the Q, % hbA1C <=8%
//=======================================================

		CompositionCohortDefinition activePatientWithHBA1CIn6Months = new CompositionCohortDefinition();
		activePatientWithHBA1CIn6Months.setName("activePatientWithHBA1CIn6Months");
		activePatientWithHBA1CIn6Months.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithHBA1CIn6Months.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithHBA1CIn6Months.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithHBA1CIn6Months.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithHBA1CIn6Months.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		activePatientWithHBA1CIn6Months.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientWithHBA1CIn6Months.getSearches().put("2",new Mapped<CohortDefinition>(patientWithHBA1C, ParameterizableUtil.createParameterMappings("startDate=${endDate-6m},endDate=${endDate}")));
		activePatientWithHBA1CIn6Months.setCompositionString("1 and 2");

		CohortIndicator activePatientWithHBA1CIn6MonthsIndicator = Indicators.newCountIndicator("activePatientWithHBA1CIn6MonthsIndicator",
				activePatientWithHBA1CIn6Months, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},enrolledOnOrBefore=${endDate}"));


		dsd.addColumn("C2D", "Total # Of all active patients with HbA1c tested in the last 6 months", new Mapped(
				activePatientWithHBA1CIn6MonthsIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");

		NumericObsCohortDefinition patientsWithHbA1cLessThanOrEqualTo8=Cohorts.createNumericObsCohortDefinition("patientsWithHbA1cLessThanOrEqualTo8",onOrAfterOnOrBefore,hbA1c,8.0,RangeComparator.LESS_EQUAL,TimeModifier.ANY);

		CompositionCohortDefinition activePatientWithHBA1CIn6MonthsLessEqual8 = new CompositionCohortDefinition();
		activePatientWithHBA1CIn6MonthsLessEqual8.setName("activePatientWithHBA1CIn6MonthsLessEqual8");
		activePatientWithHBA1CIn6MonthsLessEqual8.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithHBA1CIn6MonthsLessEqual8.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithHBA1CIn6MonthsLessEqual8.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithHBA1CIn6MonthsLessEqual8.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithHBA1CIn6MonthsLessEqual8.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		activePatientWithHBA1CIn6MonthsLessEqual8.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}")));
		activePatientWithHBA1CIn6MonthsLessEqual8.getSearches().put("2",new Mapped<CohortDefinition>(patientWithHBA1C, ParameterizableUtil.createParameterMappings("startDate=${endDate-6m},endDate=${endDate}")));
		activePatientWithHBA1CIn6MonthsLessEqual8.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithHbA1cLessThanOrEqualTo8, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${endDate}")));
		activePatientWithHBA1CIn6MonthsLessEqual8.setCompositionString("1 and 2 and 3");

		CohortIndicator activePatientWithHBA1CIn6MonthsLessEqual8Indicator = Indicators.newCountIndicator("activePatientWithHBA1CIn6MonthsLessEqual8Indicator",
				activePatientWithHBA1CIn6MonthsLessEqual8, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},enrolledOnOrBefore=${endDate}"));


		dsd.addColumn("C2N", "Total # Of all active patients with HbA1c tested in the last 6 months, % with last HbA1c <=8", new Mapped(
				activePatientWithHBA1CIn6MonthsLessEqual8Indicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");




		//====================================================
		// C3: of active patients with creatinine check in past 12 months, % with latest Cr > 200
		//=====================================================


		NumericObsCohortDefinition patientsWithCreatinineGreaterThan200 = Cohorts.createNumericObsCohortDefinition(
				"patientsWithSRGreaterThan200", onOrAfterOnOrBefore, creatinine, 200, RangeComparator.GREATER_THAN,
				TimeModifier.LAST);

		NumericObsCohortDefinition patientWithCreatinineChecked = Cohorts.createNumericObsCohortDefinition("patientWithCreatinineChecked", onOrAfterOnOrBefore,creatinine, 0.0, RangeComparator.GREATER_EQUAL, TimeModifier.ANY);

		CompositionCohortDefinition patientActiveWithCreatinineCheckedIn12Months = new CompositionCohortDefinition();
		patientActiveWithCreatinineCheckedIn12Months.setName("patientActiveWithCheckedIn12Months");
		patientActiveWithCreatinineCheckedIn12Months.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithCreatinineCheckedIn12Months.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithCreatinineCheckedIn12Months.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithCreatinineCheckedIn12Months.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithCreatinineCheckedIn12Months.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithCreatinineCheckedIn12Months.getSearches().put("2", new Mapped<CohortDefinition>(patientWithCreatinineChecked, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-9m}")));
		patientActiveWithCreatinineCheckedIn12Months.setCompositionString("1 AND 2");


		CohortIndicator patientActiveWithCreatinineCheckedIn12MonthsIndicator = Indicators.newCountIndicator(
				"patientActiveWithCheckedIn12MonthsIndicator", patientActiveWithCreatinineCheckedIn12Months,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C3D",
				"of active patients with creatinine check in past 12 months",
				new Mapped(patientActiveWithCreatinineCheckedIn12MonthsIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200 = new CompositionCohortDefinition();
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.setName("patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200");
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.getSearches().put("2", new Mapped<CohortDefinition>(patientWithCreatinineChecked, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-9m}")));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.getSearches().put("3", new Mapped<CohortDefinition>(patientsWithCreatinineGreaterThan200, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-9m}")));
		patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200.setCompositionString("1 AND 2 AND 3");


		CohortIndicator patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200Indicator = Indicators.newCountIndicator(
				"patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200Indicator", patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C3N",
				"of active patients with creatinine check in past 12 months, % with latest Cr > 200",
				new Mapped(patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200Indicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		//====================================================
		// C4: Of total DM active patients with blood pressure checked at last visit date, % with BP<=130/80 mmHg
		//=====================================================


		NumericObsCohortDefinition patientsWithSystolicBPLessEqual130 = Cohorts.createNumericObsCohortDefinition(
				"patientsWithSystolicLessEqual", systolicBP, 130.0, RangeComparator.LESS_EQUAL,
				TimeModifier.LAST);
		NumericObsCohortDefinition patientsWithDiastolicBPLessEqual80 = Cohorts.createNumericObsCohortDefinition(
				"patientsWithDiastolicBPLessEqual", diastolicBP, 80.0, RangeComparator.LESS_EQUAL,
				TimeModifier.LAST);

		NumericObsCohortDefinition patientWithSystolicBPChecked = Cohorts.createNumericObsCohortDefinition("patientWithSystolicBPChecked",systolicBP, 0, RangeComparator.GREATER_EQUAL, TimeModifier.LAST);
		NumericObsCohortDefinition patientWithDiastolicBPChecked = Cohorts.createNumericObsCohortDefinition("patientWithDiastolicBPChecked",diastolicBP, 0, RangeComparator.GREATER_EQUAL, TimeModifier.LAST);

		CompositionCohortDefinition patientActiveWithBPCheckedIn12Months = new CompositionCohortDefinition();
		patientActiveWithBPCheckedIn12Months.setName("patientActiveWithBPCheckedIn12Months");
		patientActiveWithBPCheckedIn12Months.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithBPCheckedIn12Months.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithBPCheckedIn12Months.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithBPCheckedIn12Months.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithBPCheckedIn12Months.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithBPCheckedIn12Months.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSystolicBPChecked, null));
		patientActiveWithBPCheckedIn12Months.getSearches().put("3", new Mapped<CohortDefinition>(patientWithDiastolicBPChecked, null));
		patientActiveWithBPCheckedIn12Months.setCompositionString("1 AND 2 and 3");


		CohortIndicator patientActiveWithBPCheckedIn12MonthsIndicator = Indicators.newCountIndicator(
				"patientActiveWithBPCheckedIn12MonthsIndicator", patientActiveWithBPCheckedIn12Months,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C4D",
				"of active patients with creatinine check in past 12 months",
				new Mapped(patientActiveWithBPCheckedIn12MonthsIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition patientActiveWithBPLessEqualThan130Over80 = new CompositionCohortDefinition();
		patientActiveWithBPLessEqualThan130Over80.setName("patientActiveWithBPLessEqualThan130Over80");
		patientActiveWithBPLessEqualThan130Over80.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithBPLessEqualThan130Over80.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithBPLessEqualThan130Over80.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithBPLessEqualThan130Over80.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithBPLessEqualThan130Over80.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithBPLessEqualThan130Over80.getSearches().put("2", new Mapped<CohortDefinition>(patientsWithSystolicBPLessEqual130, null));
		patientActiveWithBPLessEqualThan130Over80.getSearches().put("3", new Mapped<CohortDefinition>(patientsWithDiastolicBPLessEqual80, null));
		patientActiveWithBPLessEqualThan130Over80.setCompositionString("1 AND 2 and 3");



		CohortIndicator patientActiveWithBPLessEqualThan130Over80Indicator = Indicators.newCountIndicator(
				"patientActiveWithBPLessEqualThan130Over80Indicator", patientActiveWithBPLessEqualThan130Over80,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C4N",
				"of active patients with creatinine check in past 12 months, % with latest Cr > 200",
				new Mapped(patientActiveWithBPLessEqualThan130Over80Indicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		//==============================================================
		// D3: Of patients currently enrolled, % Lost to follow up
		//==============================================================

		CompositionCohortDefinition currentlyInProgramButLost= new CompositionCohortDefinition();
		currentlyInProgramButLost.setName("currentlyInProgramButLost");
		currentlyInProgramButLost.addParameter(new Parameter("onDate", "onDate", Date.class));
		currentlyInProgramButLost.addParameter(new Parameter("completedOnOrBefore", "completedOnOrBefore", Date.class));
		currentlyInProgramButLost.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		currentlyInProgramButLost.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		currentlyInProgramButLost.addParameter(new Parameter("startDate", "startDate", Date.class));
		currentlyInProgramButLost.addParameter(new Parameter("endDate", "endDate", Date.class));
		currentlyInProgramButLost.getSearches().put("1",new Mapped<CohortDefinition>(currentlyInProgramAndNotCompleted, ParameterizableUtil.createParameterMappings("completedOnOrBefore=${completedOnOrBefore},onDate=${onDate}")));
		currentlyInProgramButLost.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		currentlyInProgramButLost.setCompositionString("1 and (not 2)");

		CohortIndicator currentlyInProgramButLostIndicator=Indicators.newCountIndicator(
				"currentlyInProgramButLostIndicator", currentlyInProgramButLost,
				ParameterizableUtil.createParameterMappings("onDate=${endDate},completedOnOrBefore=${endDate},startDate=${startDate},endDate=${endDate},onOrBefore=${endDate},onOrAfter=${startDate}"));

		dsd.addColumn(
				"Lost",
				"Percentage of active HF patients in the last Quarter(active/ currently enrolled)",
				new Mapped(currentlyInProgramButLostIndicator, ParameterizableUtil
						.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");




/*



		//=======================================================================
		// A3: Total # of new patients enrolled in the last month/quarter
		//==================================================================
		
		CompositionCohortDefinition patientEnrolledInDM = Cohorts.createEnrolledInProgramDuringPeriod("Enrolled In DM",
		    DMProgram);
		
		CohortIndicator patientEnrolledInDMQuarterIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInDMQuarterIndicator", patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m+1d},endDate=${endDate}"));
		CohortIndicator patientEnrolledInDMMonthOneIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInDMQuarterIndicator", patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-1m+1d},endDate=${endDate}"));
		CohortIndicator patientEnrolledInDMMonthTwooIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInDMQuarterIndicator", patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-2m+1d},endDate=${endDate-1m+1d}"));
		CohortIndicator patientEnrolledInDMMonthThreeIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInDMQuarterIndicator", patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m+1d},endDate=${endDate-2m+1d}"));
		
		//=======================================================================
		// A4: Total # of new patients with RDV in the last month/quarter
		//==================================================================
		
		CohortIndicator patientRDVQuarterIndicator = Indicators.newCountIndicator("patientRDVQuarterIndicator", patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		CohortIndicator patientRDVMonthOneIndicator = Indicators.newCountIndicator("patientRDVMonthOneIndicator",
		    patientSeen, ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-1m+1d},onOrBefore=${endDate}"));
		CohortIndicator patientRDVMonthTwooIndicator = Indicators.newCountIndicator("patientRDVMonthTwooIndicator",
		    patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m+1d}"));
		CohortIndicator patientRDVMonthThreeIndicator = Indicators.newCountIndicator("patientRDVMonthThreeIndicator",
		    patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate-2m+1d}"));
		
		//=======================================================================
		//B1: Pediatric:  Of the new patients enrolled in the last quarter, % ≤15 years old at intake
		//==================================================================
		
		SqlCohortDefinition patientsUnderFifteenAtEnrollementDate = Cohorts.createUnder15AtEnrollmentCohort(
		    "patientsUnder15AtEnrollment", DMProgram);
		
		CompositionCohortDefinition patientsUnderFifteenComposition = new CompositionCohortDefinition();
		patientsUnderFifteenComposition.setName("patientsUnderFifteenComposition");
		patientsUnderFifteenComposition.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientsUnderFifteenComposition.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientsUnderFifteenComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientEnrolledInDM, ParameterizableUtil
		            .createParameterMappings("startDate=${enrolledOnOrAfter},endDate=${enrolledOnOrBefore}")));
		patientsUnderFifteenComposition.getSearches().put("2",
		    new Mapped<CohortDefinition>(patientsUnderFifteenAtEnrollementDate, null));
		patientsUnderFifteenComposition.setCompositionString("1 AND 2");
		
		CohortIndicator patientsUnderFifteenCountIndicator = Indicators.newCountIndicator(
		    "patientsUnderFifteenCountIndicator", patientsUnderFifteenComposition,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${endDate-3m+1d},enrolledOnOrBefore=${endDate}"));
		
		//=======================================================================
		//B2: Gender: Of the new patients enrolled in the last quarter, % male
		//==================================================================
		

		CompositionCohortDefinition malePatientsEnrolledInDM = new CompositionCohortDefinition();
		malePatientsEnrolledInDM.setName("malePatientsEnrolledIn");
		malePatientsEnrolledInDM.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		malePatientsEnrolledInDM.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		malePatientsEnrolledInDM.addParameter(new Parameter("startDate", "startDate", Date.class));
		malePatientsEnrolledInDM.addParameter(new Parameter("endDate", "endDate", Date.class));
		malePatientsEnrolledInDM.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientEnrolledInDM, ParameterizableUtil
		            .createParameterMappings("startDate=${enrolledOnOrAfter},endDate=${enrolledOnOrBefore}")));
		malePatientsEnrolledInDM.getSearches().put("2", new Mapped<CohortDefinition>(malePatients, null));
		malePatientsEnrolledInDM.setCompositionString("1 AND 2");
		
		CohortIndicator malePatientsEnrolledInDMCountIndicator = Indicators.newCountIndicator(
		    "malePatientsEnrolledInDMCountIndicator", malePatientsEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${endDate-3m+1d},enrolledOnOrBefore=${endDate}"));
		
		//=======================================================================
		//B3: Of the new patients enrolled in the last month/quarter, % with HbA1c done at intake
		//==================================================================
		
		//// collection for HbA1c not provided at intake, modification id needed on DDB form.
		
		//		CompositionCohortDefinition patientsEnrolledAndHaveHbAc1AtIntake = Cohorts
		//		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveHbAc1AtIntake",
		//		            DMProgram, DDBform, hbA1c);
		//		
		//		CohortIndicator patientsEnrolledAndHaveHbAc1AtIntakeCountIndicator = Indicators.newCountIndicator(
		//		    "patientsEnrolledAndHaveHbAc1AtIntakeCountIndicator", patientsEnrolledAndHaveHbAc1AtIntake,
		//		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m+1d},endDate=${endDate}"));
		
		//=======================================================================
		//B4: Of the new patients enrolled in the last month/quarter, % with Glucose done at intake
		//==================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveglucoseAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveglucoseAtIntake",
		            DMProgram, DDBform, glucose);
		
		CohortIndicator patientsEnrolledAndHaveglucoseAtIntakeCountIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndHaveglucoseAtIntakeCountIndicator", patientsEnrolledAndHaveglucoseAtIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m+1d},endDate=${endDate}"));
		
		//=======================================================================
		//B5: Of the new patients enrolled in the last month/quarter, % with Height Weight recorded at intake
		//==================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveWeightAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveWeightAtIntake",
		            DMProgram, DDBform, weight);
		CompositionCohortDefinition patientsEnrolledAndHaveHeightAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveHeightAtIntake",
		            DMProgram, DDBform, height);
		
		CompositionCohortDefinition heightWeightIntake = new CompositionCohortDefinition();
		heightWeightIntake.setName("patientsEnrolledAndHeightWeightRecordedAtIntake");
		heightWeightIntake.addParameter(new Parameter("startDate", "start", Date.class));
		heightWeightIntake.addParameter(new Parameter("endDate", "end", Date.class));
		heightWeightIntake.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveWeightAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		heightWeightIntake.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveHeightAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		heightWeightIntake.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndHeightWeightRecordedAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndHeightWeightRecordedAtIntakeCountIndicator", heightWeightIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m+1d},endDate=${endDate}"));
		
		//=======================================================================
		//B6: Of the new patients enrolled in the last month/quarter, % with BP recorded at intake
		//==================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveDiastolicAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveDiastolicAtIntake",
		            DMProgram, DDBform, diastolicBP);
		CompositionCohortDefinition patientsEnrolledAndHaveSystolicAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveSystolicAtIntake",
		            DMProgram, DDBform, systolicBP);
		
		CompositionCohortDefinition patientsEnrolledAndBPRecordedAtIntake = new CompositionCohortDefinition();
		patientsEnrolledAndBPRecordedAtIntake.setName("patientsEnrolledAndBPRecordedAtIntake");
		patientsEnrolledAndBPRecordedAtIntake.addParameter(new Parameter("startDate", "start", Date.class));
		patientsEnrolledAndBPRecordedAtIntake.addParameter(new Parameter("endDate", "end", Date.class));
		patientsEnrolledAndBPRecordedAtIntake.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveDiastolicAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndBPRecordedAtIntake.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveSystolicAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndBPRecordedAtIntake.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndBPRecordedAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndBPRecordedAtIntakeCountIndicator", patientsEnrolledAndBPRecordedAtIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m},endDate=${endDate}"));
		
		//=======================================================================
		//B7: Of the new patients enrolled in the last month/quarter, % with Neuropathy status recorded at intake
		//==================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveSensationLeftFootAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveDiastolicAtIntake",
		            DMProgram, DDBform, sensationInLeftFoot);
		CompositionCohortDefinition patientsEnrolledAndHaveSensationRightFootAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveSystolicAtIntake",
		            DMProgram, DDBform, sensationInRightFoot);
		
		CompositionCohortDefinition patientsEnrolledAndNeuropathyCheckedAtIntake = new CompositionCohortDefinition();
		patientsEnrolledAndNeuropathyCheckedAtIntake.setName("patientsEnrolledAndNeuropathyCheckedAtIntake");
		patientsEnrolledAndNeuropathyCheckedAtIntake.addParameter(new Parameter("startDate", "start", Date.class));
		patientsEnrolledAndNeuropathyCheckedAtIntake.addParameter(new Parameter("endDate", "end", Date.class));
		patientsEnrolledAndNeuropathyCheckedAtIntake.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveSensationLeftFootAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndNeuropathyCheckedAtIntake.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveSensationRightFootAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndNeuropathyCheckedAtIntake.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndNeuropathyCheckedAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndNeuropathyCheckedAtIntakeCountIndicator", patientsEnrolledAndNeuropathyCheckedAtIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-3m+1d},endDate=${endDate}"));
		
		//=======================================================================
		//C1: Of total patients seen in the last quarter and are on ace inhibitors, % who had Creatinine tested in the last 6 months
		//==================================================================
		
		SqlCohortDefinition onAceInhibitor = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("onAceInhibitor",
		    lisinoprilCaptopril);
		
		NumericObsCohortDefinition testedForCreatinine = Cohorts.createNumericObsCohortDefinition(
		    "patientsTestedForCreatinine", onOrAfterOnOrBefore, creatinine, 0, null, TimeModifier.LAST);
		
		CompositionCohortDefinition patientsSeenOnAceInhibitorsAndTestedForCreatinine = new CompositionCohortDefinition();
		patientsSeenOnAceInhibitorsAndTestedForCreatinine.setName("patientsSeenOnAceInhibitorsAndTestedForCreatinine");
		patientsSeenOnAceInhibitorsAndTestedForCreatinine.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenOnAceInhibitorsAndTestedForCreatinine
		        .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenOnAceInhibitorsAndTestedForCreatinine.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onAceInhibitor, ParameterizableUtil
		            .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenOnAceInhibitorsAndTestedForCreatinine.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(testedForCreatinine, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-3m}")));
		patientsSeenOnAceInhibitorsAndTestedForCreatinine.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenOnAceInhibitorsAndTestedForCreatinine.setCompositionString("1 AND 2 AND 3");
		
		CohortIndicator patientsSeenOnAceInhibitorsAndTestedForCreatinineCountIndicator = Indicators.newCountIndicator(
		    "patientsSeenOnAceInhibitorsAndTestedForCreatinineCountIndicator",
		    patientsSeenOnAceInhibitorsAndTestedForCreatinine,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		CompositionCohortDefinition patientsSeenOnAceInhibitors = new CompositionCohortDefinition();
		patientsSeenOnAceInhibitors.setName("patientsSeenOnAceInhibitors");
		patientsSeenOnAceInhibitors.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenOnAceInhibitors.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenOnAceInhibitors.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onAceInhibitor, ParameterizableUtil
		            .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenOnAceInhibitors.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenOnAceInhibitors.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenOnAceInhibitorsCountIndicator = Indicators.newCountIndicator(
		    "patientsSeenOnAceInhibitorsCountIndicator", patientsSeenOnAceInhibitors,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		//=======================================================================
		//C2: Of total patients seen in the last quarter, % who had HbA1c tested in the last 6 months
		//==================================================================
		
		NumericObsCohortDefinition patientsTestedForHbA1c = Cohorts.createNumericObsCohortDefinition(
		    "patientsTestedForHbA1c", onOrAfterOnOrBefore, hbA1c, 0, null, TimeModifier.LAST);
		
		CompositionCohortDefinition patientsSeenAndTestedForHbA1c = new CompositionCohortDefinition();
		patientsSeenAndTestedForHbA1c.setName("patientsSeenAndTestedForHbA1c");
		patientsSeenAndTestedForHbA1c.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndTestedForHbA1c.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndTestedForHbA1c.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsTestedForHbA1c, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter-3m}")));
		patientsSeenAndTestedForHbA1c.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndTestedForHbA1c.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndTestedForHbA1cCountIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndTestedForHbA1cCountIndicator", patientsSeenAndTestedForHbA1c,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		//=======================================================================
		//C3: Of all the patients ever registered (with Diabetes DDB), % ever tested for HbA1c
		//==================================================================
		
		EncounterCohortDefinition everRegisteredWithDDB = Cohorts.createEncounterBasedOnForms("EverRegistered with DDB",
		    "onOrBefore", DDBforms);
		
		NumericObsCohortDefinition everTestedForHbA1c = Cohorts.createNumericObsCohortDefinition("everTestedForHbA1c",
		    hbA1c, 0, null, TimeModifier.ANY);
		
		CompositionCohortDefinition everRegisteredWithDDBAndTestForHbA1c = new CompositionCohortDefinition();
		everRegisteredWithDDBAndTestForHbA1c.setName("everRegisteredWithDDBAndTestForHbA1c");
		everRegisteredWithDDBAndTestForHbA1c.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		everRegisteredWithDDBAndTestForHbA1c.getSearches().put("1", new Mapped<CohortDefinition>(everTestedForHbA1c, null));
		everRegisteredWithDDBAndTestForHbA1c.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(everRegisteredWithDDB, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore}")));
		everRegisteredWithDDBAndTestForHbA1c.setCompositionString("1 AND 2");
		
		CohortIndicator everRegisteredWithDDBAndTestForHbA1cIndicatorNumerator = Indicators.newCountIndicator(
		    "everRegisteredWithDDBAndTestForHbA1cIndicatorNumerator", everRegisteredWithDDBAndTestForHbA1c,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}"));
		
		CohortIndicator everRegisteredWithDDBAndTestForHbA1cIndicatorDenominator = Indicators.newCountIndicator(
		    "everRegisteredWithDDBAndTestForHbA1cIndicatorDenominator", everRegisteredWithDDB,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}"));
		
		//=======================================================================
		//C4: Of total patients seen in the last quarter, % with height weight recorded at last visit
		//==================================================================
		
		SqlCohortDefinition heightAtLastVist = Cohorts.getPatientsWithObservationAtLastVisit("heightAtLastVist", height,
		    DMEncounterType);
		SqlCohortDefinition weightAtLastVist = Cohorts.getPatientsWithObservationAtLastVisit("weightAtLastVist", weight,
		    DMEncounterType);
		
		CompositionCohortDefinition patientsSeenAndBMIAtLastVist = new CompositionCohortDefinition();
		patientsSeenAndBMIAtLastVist.setName("patientsSeenAndBMIAtLastVist");
		patientsSeenAndBMIAtLastVist.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndBMIAtLastVist.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndBMIAtLastVist.getSearches().put("1", new Mapped<CohortDefinition>(heightAtLastVist, null));
		patientsSeenAndBMIAtLastVist.getSearches().put("2", new Mapped<CohortDefinition>(weightAtLastVist, null));
		
		patientsSeenAndBMIAtLastVist.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndBMIAtLastVist.setCompositionString("1 AND 2 AND 3");
		
		CohortIndicator patientsSeenAndBMIAtLastVistCountIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndBMIAtLastVistCountIndicator", patientsSeenAndBMIAtLastVist,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		//=======================================================================
		//C5: Of total patients seen in the last quarter, % with BP recorded at last visit
		//==================================================================
		
		SqlCohortDefinition systolicBPAtLastVist = Cohorts.getPatientsWithObservationAtLastVisit("systolicBPAtLastVist",
		    systolicBP, DMEncounterType);
		SqlCohortDefinition diastolicBPAtLastVist = Cohorts.getPatientsWithObservationAtLastVisit("systolicBPAtLastVist",
		    diastolicBP, DMEncounterType);
		
		CompositionCohortDefinition patientsSeenAndBPAtLastVist = new CompositionCohortDefinition();
		patientsSeenAndBPAtLastVist.setName("patientsSeenAndBPAtLastVist");
		patientsSeenAndBPAtLastVist.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndBPAtLastVist.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndBPAtLastVist.getSearches().put("1", new Mapped<CohortDefinition>(systolicBPAtLastVist, null));
		patientsSeenAndBPAtLastVist.getSearches().put("2", new Mapped<CohortDefinition>(diastolicBPAtLastVist, null));
		
		patientsSeenAndBPAtLastVist.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndBPAtLastVist.setCompositionString("(1 OR 2) AND 3");
		
		CohortIndicator patientsSeenAndBPAtLastVistCountIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndBPAtLastVistCountIndicator", patientsSeenAndBPAtLastVist,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		//=======================================================================
		//C6: Of total patients seen in the last year, % with Neuropathy checked in the last year
		//==================================================================
		
		SqlCohortDefinition neuropathyChecked = Cohorts.getPatientsWithObservationsBetweenStartDateAndEndDate(
		    "neuropathyChecked", neuropathyConcepts);
		
		CompositionCohortDefinition patientsSeenAndNeuropathyChecked = new CompositionCohortDefinition();
		patientsSeenAndNeuropathyChecked.setName("patientsSeenAndNeuropathyChecked");
		patientsSeenAndNeuropathyChecked.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndNeuropathyChecked.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndNeuropathyChecked.addParameter(new Parameter("start", "start", Date.class));
		patientsSeenAndNeuropathyChecked.addParameter(new Parameter("end", "end", Date.class));
		patientsSeenAndNeuropathyChecked.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndNeuropathyChecked.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(neuropathyChecked, ParameterizableUtil
		            .createParameterMappings("start=${start},end=${end}")));
		patientsSeenAndNeuropathyChecked.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndNeuropathyCheckedCountIndicator = Indicators
		        .newCountIndicator(
		            "patientsSeenAndNeuropathyCheckedCountIndicator",
		            patientsSeenAndNeuropathyChecked,
		            ParameterizableUtil
		                    .createParameterMappings("start=${endDate-12m+1d},end=${endDate},onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		CohortIndicator patientsSeenInOneYearCountIndicator = Indicators.newCountIndicator(
		    "patientsSeenInOneYearCountIndicator", patientsSeenComposition,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		//=======================================================================
		//D1: Of total patients seen in the last month/quarter, % with no regimen documented
		//==================================================================
		
		SqlCohortDefinition patientOnRegimen = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("patientsOnRegime",
		    diabetesDrugConcepts);
		
		CompositionCohortDefinition patientsSeenAndNotOnAnyDMRegimen = new CompositionCohortDefinition();
		patientsSeenAndNotOnAnyDMRegimen.setName("patientsSeenAndNotOnAnyDMRegimen");
		patientsSeenAndNotOnAnyDMRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndNotOnAnyDMRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndNotOnAnyDMRegimen.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndNotOnAnyDMRegimen.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientOnRegimen, ParameterizableUtil
		            .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenAndNotOnAnyDMRegimen.setCompositionString("1 AND (NOT 2)");
		
		CohortIndicator patientsSeenAndNotOnAnyDMRegimenCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndNotOnAnyDMRegimenCountMonthIndicator", patientsSeenAndNotOnAnyDMRegimen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		//=======================================================================
		//D2: Of total patients seen in the last quarter, % on any type of insulin at last visit
		//==================================================================
		

		
		CompositionCohortDefinition patientsSeenAndOnInsulin = new CompositionCohortDefinition();
		patientsSeenAndOnInsulin.setName("patientsSeenAndOnInsulin");
		patientsSeenAndOnInsulin.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndOnInsulin.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndOnInsulin.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndOnInsulin.getSearches().put("2", new Mapped<CohortDefinition>(patientOnInsulin, ParameterizableUtil
				.createParameterMappings("endDate=${endDate}")));
		patientsSeenAndOnInsulin.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndOnInsulinCountQuarterIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndOnInsulinCountQuarterIndicator", patientsSeenAndOnInsulin,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//D3: Of total patients seen in the last quarter and on any type of insulin at last visit, % who are on metformin
		//==================================================================
		
		SqlCohortDefinition onMetformin = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("onMetformin", metformin);
		
		CompositionCohortDefinition patientsSeenAndOnInsulinAndOnMetformin = new CompositionCohortDefinition();
		patientsSeenAndOnInsulinAndOnMetformin.setName("patientsSeenAndOnInsulinAndOnMetformin");
		patientsSeenAndOnInsulinAndOnMetformin.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndOnInsulinAndOnMetformin.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndOnInsulinAndOnMetformin.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsSeenAndOnInsulinAndOnMetformin.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenAndOnInsulin, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndOnInsulinAndOnMetformin.getSearches().put("2",
		    new Mapped<CohortDefinition>(onMetformin, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		patientsSeenAndOnInsulinAndOnMetformin.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndOnInsulinAndOnMetforminCountQuarterIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndOnInsulinAndOnMetforminCountQuarterIndicator", patientsSeenAndOnInsulinAndOnMetformin,
		    ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//D4: Of total patients seen in the last quarter and on any type of insulin at last visit, % who are on mixed insulin
		//==================================================================
		
		SqlCohortDefinition onInsulinMixte = Cohorts
		        .getPatientsOnCurrentRegimenBasedOnEndDate("onInsulinMixte", insulin7030);
		
		CompositionCohortDefinition patientsSeenAndOnInsulinAndOnInsulinMixte = new CompositionCohortDefinition();
		patientsSeenAndOnInsulinAndOnInsulinMixte.setName("patientsSeenAndOnInsulinAndOnMetformin");
		patientsSeenAndOnInsulinAndOnInsulinMixte.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndOnInsulinAndOnInsulinMixte.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndOnInsulinAndOnInsulinMixte.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsSeenAndOnInsulinAndOnInsulinMixte.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenAndOnInsulin, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndOnInsulinAndOnInsulinMixte.getSearches().put("2",
		    new Mapped<CohortDefinition>(onInsulinMixte, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		patientsSeenAndOnInsulinAndOnInsulinMixte.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndOnInsulinAndOnInsulinMixteCountQuarterIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndOnInsulinAndOnInsulinMixteCountQuarterIndicator", patientsSeenAndOnInsulinAndOnInsulinMixte,
		    ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//D5: Of total patients seen in the last quarter and on any type of insulin at last visit, % who have accompanateurs
		//==================================================================		
		
		SqlCohortDefinition patientsWhitAccompagnateur = Cohorts.createPatientsWithAccompagnateur(
		    "allPatientsWhitAccompagnateur", "endDate");
		
		CompositionCohortDefinition patientsSeenAndOnInsulinAndWhitAccompagnateur = new CompositionCohortDefinition();
		patientsSeenAndOnInsulinAndWhitAccompagnateur.setName("patientsSeenAndOnInsulinAndWhitAccompagnateur");
		patientsSeenAndOnInsulinAndWhitAccompagnateur.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndOnInsulinAndWhitAccompagnateur.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndOnInsulinAndWhitAccompagnateur.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsSeenAndOnInsulinAndWhitAccompagnateur.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenAndOnInsulin, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndOnInsulinAndWhitAccompagnateur.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsWhitAccompagnateur, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		patientsSeenAndOnInsulinAndWhitAccompagnateur.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndOnInsulinAndWhitAccompagnateurCountQuarterIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndOnInsulinAndWhitAccompagnateurCountQuarterIndicator",
		    patientsSeenAndOnInsulinAndWhitAccompagnateur, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${endDate-3m+1d},onOrBefore=${endDate},endDate=${endDate}"));
		
		//=======================================================================
		//D6: Of total patients seen in the last quarter, % on oral medications only (metformin and/or glibenclimide)
		//==================================================================		
		

		
		CompositionCohortDefinition patientsSeenAndOnMetforminOrGlibenclimide = new CompositionCohortDefinition();
		patientsSeenAndOnMetforminOrGlibenclimide.setName("patientsSeenAndOnMetforminOrGlibenclimide");
		patientsSeenAndOnMetforminOrGlibenclimide.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndOnMetforminOrGlibenclimide.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndOnMetforminOrGlibenclimide.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsSeenAndOnMetforminOrGlibenclimide.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndOnMetforminOrGlibenclimide.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(onMetforminOrGlibenclimide, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")));
		patientsSeenAndOnMetforminOrGlibenclimide.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndOnMetforminOrGlibenclimideCountQuarterIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndOnMetforminOrGlibenclimideCountQuarterIndicator", patientsSeenAndOnMetforminOrGlibenclimide,
		    ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},onOrAfter=${endDate-3m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//E1: Of total active patients, % who died
		//==================================================================		
		
		PatientStateCohortDefinition patientDied = Cohorts.createPatientStateCohortDefinition("Died patient", diedState);
		
		CompositionCohortDefinition activeAndDiedPatients = new CompositionCohortDefinition();
		activeAndDiedPatients.setName("activeAndDiedPatients");
		activeAndDiedPatients.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeAndDiedPatients.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeAndDiedPatients.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		activeAndDiedPatients.getSearches().put("2", new Mapped<CohortDefinition>(patientDied, null));
		activeAndDiedPatients.setCompositionString("1 AND 2");
		
		CohortIndicator activeAndDiedPatientsCountQuarterIndicator = Indicators.newCountIndicator(
		    "activeAndDiedPatientsCountQuarterIndicator", activeAndDiedPatients,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//E2: Of total active patients, % with documented hospitalization (in flowsheet) in the last quarter (exclude hospitalization on DDB)
		//==================================================================
		
		SqlCohortDefinition patientHospitalized = Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate(
		    "patientHospitalized", DMEncounterType, locOfHosp);
		
		CompositionCohortDefinition activeAndHospitalizedPatients = new CompositionCohortDefinition();
		activeAndHospitalizedPatients.setName("activeAndHospitalizedPatients");
		activeAndHospitalizedPatients.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeAndHospitalizedPatients.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activeAndHospitalizedPatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		activeAndHospitalizedPatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		activeAndHospitalizedPatients.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		activeAndHospitalizedPatients.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientHospitalized, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate},startDate=${startDate}")));
		activeAndHospitalizedPatients.setCompositionString("1 AND 2");
		
		CohortIndicator activeAndHospitalizedPatientsCountQuarterIndicator = Indicators
		        .newCountIndicator(
		            "activeAndHospitalizedPatientsCountQuarterIndicator",
		            activeAndHospitalizedPatients,
		            ParameterizableUtil
		                    .createParameterMappings("endDate=${endDate},startDate=${endDate-3m+1d},onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//E3: Of total active patients, % with no visit 14 weeks or more past last visit date
		//==================================================================		
		
		EncounterCohortDefinition withDiabetesVisit = Cohorts.createEncounterParameterizedByDate("withDiabetesVisit",
		    onOrAfterOnOrBefore, DMEncounterType);
		
		CompositionCohortDefinition activeAndNotwithDiabetesVisitInFourteenWeeksPatients = new CompositionCohortDefinition();
		activeAndNotwithDiabetesVisitInFourteenWeeksPatients.setName("activeAndNotwithDiabetesVisitInFourteenWeeksPatients");
		activeAndNotwithDiabetesVisitInFourteenWeeksPatients
		        .addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activeAndNotwithDiabetesVisitInFourteenWeeksPatients.addParameter(new Parameter("onOrBefore", "onOrBefore",
		        Date.class));
		activeAndNotwithDiabetesVisitInFourteenWeeksPatients.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		activeAndNotwithDiabetesVisitInFourteenWeeksPatients.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(withDiabetesVisit, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter+12m-14w}")));
		activeAndNotwithDiabetesVisitInFourteenWeeksPatients.setCompositionString("1 AND (NOT 2)");
		
		CohortIndicator activeAndNotwithDiabetesVisitInFourteenWeeksPatientsCountQuarterIndicator = Indicators
		        .newCountIndicator("activeAndNotwithDiabetesVisitInFourteenWeeksPatientsNumeratorCountQuarterIndicator",
		            activeAndNotwithDiabetesVisitInFourteenWeeksPatients,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//E4: Of total active patients on insulin at last visit, % with no visit 14 weeks or more past last visit date		
		//==================================================================
		
		CompositionCohortDefinition activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients = new CompositionCohortDefinition();
		activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients
		        .setName("activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients");
		activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients.addParameter(new Parameter("onOrAfter",
		        "onOrAfter", Date.class));
		activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients.addParameter(new Parameter("onOrBefore",
		        "onOrBefore", Date.class));
		activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenAndOnInsulin, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(withDiabetesVisit, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter+12m-14w}")));
		activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients.setCompositionString("1 AND (NOT 2)");
		
		CohortIndicator activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatientsNumeratorCountQuarterIndicatorrs = Indicators
		        .newCountIndicator(
		            "activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatientsNumeratorCountQuarterIndicator",
		            activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatients,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		CohortIndicator activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatientsDenominatoCountQuarterIndicatorrs = Indicators
		        .newCountIndicator(
		            "activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatientsDenominatorCountQuarterIndicator",
		            patientsSeenAndOnInsulin,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		//=======================================================================
		//E5: Of patients who have had HbA1c tested in the last quarter, % with last HbA1c <8 
		//==================================================================
		
		NumericObsCohortDefinition patientsWithLastHbA1cLessThanEight = Cohorts.createNumericObsCohortDefinition(
		    "patientsWithLastHbA1cLessThanEight", hbA1c, 8.0, RangeComparator.LESS_THAN, TimeModifier.LAST);
		
		CompositionCohortDefinition patientsTestedForHbA1cWithLastHbA1cLessThanEight = new CompositionCohortDefinition();
		patientsTestedForHbA1cWithLastHbA1cLessThanEight.setName("patientsTestedForHbA1cWithLastHbA1cLessThanEight");
		patientsTestedForHbA1cWithLastHbA1cLessThanEight.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsTestedForHbA1cWithLastHbA1cLessThanEight.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsTestedForHbA1cWithLastHbA1cLessThanEight.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsTestedForHbA1c, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsTestedForHbA1cWithLastHbA1cLessThanEight.getSearches().put("2",
		    new Mapped<CohortDefinition>(patientsWithLastHbA1cLessThanEight, null));
		patientsTestedForHbA1cWithLastHbA1cLessThanEight.setCompositionString("1 AND 2");
		
		CohortIndicator patientsTestedForHbA1cWithLastHbA1cLessThanEightNumeratorCountQuarterIndicatorrs = Indicators
		        .newCountIndicator("patientsTestedForHbA1cWithLastHbA1cLessThanEightNumeratorCountQuarterIndicator",
		            patientsTestedForHbA1cWithLastHbA1cLessThanEight,
		            ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		CohortIndicator patientsTestedForHbA1cWithLastHbA1cLessThanEightDenominatoCountQuarterIndicatorrs = Indicators
		        .newCountIndicator("patientsTestedForHbA1cWithLastHbA1cLessThanEightDenominatorCountQuarterIndicator",
		            patientsTestedForHbA1c,
		            ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${endDate-3m+1d}"));
		
		//=================================================
		//     Adding columns to data set definition     //
		//=================================================
		
		dsd.addColumn("A2Q", "Total # of patients seen in the last quarter", new Mapped(patientsSeenQuarterIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A2QM1", "Total # of patients seen in the last month one", new Mapped(patientsSeenMonthOneIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A2QM2", "Total # of patients seen in the last month two", new Mapped(patientsSeenMonthTwoIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A2QM3", "Total # of patients seen in the last month three", new Mapped(
		        patientsSeenMonthThreeIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A3Q", "Total # of new patients enrolled in the last quarter", new Mapped(
		        patientEnrolledInDMQuarterIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A3QM1", "Total # of new patients enrolled in the month one", new Mapped(
		        patientEnrolledInDMMonthOneIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A3QM2", "Total # of new patients enrolled in the month two", new Mapped(
		        patientEnrolledInDMMonthTwooIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")),
		    "");
		dsd.addColumn("A3QM3", "Total # of new patients enrolled in the month three", new Mapped(
		        patientEnrolledInDMMonthThreeIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")),
		    "");
		
		dsd.addColumn("A4Q", "Total # of new patients with RDV in the last quarter", new Mapped(patientRDVQuarterIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A4QM1", "Total # of new patients with RDV in the month one", new Mapped(patientRDVMonthOneIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A4QM2", "Total # of new patients with RDV in the month two", new Mapped(patientRDVMonthTwooIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A4QM3", "Total # of new patients with RDV in the month three", new Mapped(
		        patientRDVMonthThreeIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn("B1N",
		    "Pediatric: Of the new patients enrolled in the last quarter, number ≤15 years old at intake", new Mapped(
		            patientsUnderFifteenCountIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")),
		    "");
		
		dsd.addColumn("B2N", "Gender: Of the new patients enrolled in the last quarter, number male", new Mapped(
		        malePatientsEnrolledInDMCountIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")),
		    "");
		//		dsd.addColumn("B3NQ", "New patients enrolled in the last quarter, Number with HbA1c done at intake",
		//		new Mapped(patientsEnrolledAndHaveHbAc1AtIntakeCountIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B4NQ",
		    "New patients enrolled in the last quarter, Number with Glucose done at intake",
		    new Mapped(patientsEnrolledAndHaveglucoseAtIntakeCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B5NQ",
		    "New patients enrolled in the last quarter, Number with Height Weight Recorded recorded at intake",
		    new Mapped(patientsEnrolledAndHeightWeightRecordedAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B6NQ",
		    "New patients enrolled in the last quarter, With BP recorded at intake",
		    new Mapped(patientsEnrolledAndBPRecordedAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B7NQ",
		    "new patients enrolled in the last quarter, Number with Neuropathy checked at intake",
		    new Mapped(patientsEnrolledAndNeuropathyCheckedAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C1NQ",
		    "total patients seen in the last quarter and are on ace inhibitors, Numerator who had Creatinine tested in the last 6 months",
		    new Mapped(patientsSeenOnAceInhibitorsAndTestedForCreatinineCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C1DQ",
		    "total patients seen in the last quarter and are on ace inhibitors",
		    new Mapped(patientsSeenOnAceInhibitorsCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C2NQ",
		    "total patients seen in the last quarter, Numeric who had HbA1c tested in the last 6 months",
		    new Mapped(patientsSeenAndTestedForHbA1cCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C3NQ",
		    "all the patients ever registered (with Diabetes DDB), Number Numerator ever tested for HbA1c",
		    new Mapped(everRegisteredWithDDBAndTestForHbA1cIndicatorNumerator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "C3DQ",
		    "all the patients ever registered (with Diabetes DDB), Number Denominator ever tested for HbA1c",
		    new Mapped(everRegisteredWithDDBAndTestForHbA1cIndicatorDenominator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C4NQ",
		    "total patients seen in the last quarter, number with BMI recorded at last visit",
		    new Mapped(patientsSeenAndBMIAtLastVistCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C5NQ",
		    "total patients seen in the last quarter, number with BP recorded at last visit",
		    new Mapped(patientsSeenAndBPAtLastVistCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "C6NY",
		    "Of total patients seen in the last year, number with Neuropathy checked in the last year",
		    new Mapped(patientsSeenAndNeuropathyCheckedCountIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("C6DY", "total patients seen in the last year", new Mapped(patientsSeenInOneYearCountIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "D1NQ",
		    "Total patients seen in the last month/quarter,number with no regimen documented",
		    new Mapped(patientsSeenAndNotOnAnyDMRegimenCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "D2NQ",
		    "Total patients seen in the last quarter, Number on any type of insulin at last visit",
		    new Mapped(patientsSeenAndOnInsulinCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "D3NQ",
		    "Total patients seen in the last quarter and on any type of insulin at last visit, Number who are on metformin",
		    new Mapped(patientsSeenAndOnInsulinAndOnMetforminCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "D4NQ",
		    "Total patients seen in the last quarter and on any type of insulin at last visit, Number who are on mixed insulin",
		    new Mapped(patientsSeenAndOnInsulinAndOnInsulinMixteCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "D5NQ",
		    "Total patients seen in the last quarter and on any type of insulin at last visit, number who have accompanateurs",
		    new Mapped(patientsSeenAndOnInsulinAndWhitAccompagnateurCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "D6NQ",
		    "Total patients seen in the last quarter, number on oral medications only (metformin and/or glibenclimide)",
		    new Mapped(patientsSeenAndOnMetforminOrGlibenclimideCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "E1NQ",
		    "Total active patients, number who died",
		    new Mapped(activeAndDiedPatientsCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "E2NQ",
		    "Total active patients, number with documented hospitalization (in flowsheet) in the last quarter (exclude hospitalization on DDB)",
		    new Mapped(activeAndHospitalizedPatientsCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "E3NQ",
		    "Total active patients, number with no visit 14 weeks or more past last visit date",
		    new Mapped(activeAndNotwithDiabetesVisitInFourteenWeeksPatientsCountQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn("E4NQ",
		    "Total active patients on insulin at last visit, Numerator with no visit 14 weeks or more past last visit date",
		    new Mapped(activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatientsNumeratorCountQuarterIndicatorrs,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "E4DQ",
		    "Total active patients on insulin at last visit, Denominator with no visit 14 weeks or more past last visit date",
		    new Mapped(activeAndOnInsulinAndNotwithDiabetesVisitInFourteenWeeksPatientsDenominatoCountQuarterIndicatorrs,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "E5NQ",
		    "Patients who have had HbA1c tested in the last quarter, Numerator with last HbA1c <8 ",
		    new Mapped(patientsTestedForHbA1cWithLastHbA1cLessThanEightNumeratorCountQuarterIndicatorrs, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("E5DQ", "Patients who have had HbA1c tested in the last quarter, Denominator with last HbA1c <8 ",
		    new Mapped(patientsTestedForHbA1cWithLastHbA1cLessThanEightDenominatoCountQuarterIndicatorrs,
		            ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
*/
	}
	
	private void createMonthlyIndicators(CohortIndicatorDataSetDefinition dsd) {
		//=======================================================
		// A2: Total # of patients seen in the last month/quarter
		//=======================================================
		EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
		    onOrAfterOnOrBefore, patientsSeenEncounterTypes);
		
		EncounterCohortDefinition patientWithDDB = Cohorts.createEncounterBasedOnForms("patientWithDDB",
		    onOrAfterOnOrBefore, DDBforms);
		
		CompositionCohortDefinition patientsSeenComposition = new CompositionCohortDefinition();
		patientsSeenComposition.setName("patientsSeenComposition");
		patientsSeenComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenComposition.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenComposition.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenComposition.setCompositionString("1 OR 2");
		
		CohortIndicator patientsSeenMonthOneIndicator = Indicators.newCountIndicator("patientsSeenMonthOneIndicator",
		    patientsSeenComposition,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-1m+1d},onOrBefore=${endDate}"));
		
		//=================================================================
		// A3: Total # of new patients enrolled in the last month/quarter
		//=================================================================
		
		CompositionCohortDefinition patientEnrolledInDM = Cohorts.createEnrolledInProgramDuringPeriod("Enrolled In DM",
		    DMProgram);
		
		CohortIndicator patientEnrolledInDMMonthOneIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInDMQuarterIndicator", patientEnrolledInDM,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-1m+1d},endDate=${endDate}"));
		
		//==============================================================
		// A4: Total # of patients with a return visit in the last month
		//==============================================================
		
		CohortIndicator patientsRDVMonthIndicator = Indicators.newCountIndicator("patientsRDVIndicator", patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-1m+1d},onOrBefore=${endDate}"));
		
		//B3: Of the new patients enrolled in the last month/quarter, % with HbA1c done at intake
		
		// collection for HbA1c not provided at intake, modification id needed on DDB form.		
		//		CompositionCohortDefinition patientsEnrolledAndHaveHbAc1AtIntake = Cohorts.getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveHbAc1AtIntake", DMProgram, DDBform, hbA1c);
		
		//		CohortIndicator patientsEnrolledAndHaveHbAc1AtIntakeCountMonthIndicator = Indicators
		//        .newCountIndicator(
		//            "patientsEnrolledAndHaveHbAc1AtIntakeCountMonthIndicator",
		//            patientsEnrolledAndHaveHbAc1AtIntake,
		//            ParameterizableUtil
		//                    .createParameterMappings("startDate=${endDate-1m+1d},endDate=${endDate}"));
		
		//B4: Of the new patients enrolled in the last month/quarter, % with Glucose done at intake
		
		CompositionCohortDefinition patientsEnrolledAndHaveglucoseAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveglucoseAtIntake",
		            DMProgram, DDBform, glucose);
		
		CohortIndicator patientsEnrolledAndHaveglucoseAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndHaveglucoseAtIntakeCountIndicator", patientsEnrolledAndHaveglucoseAtIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-1m+1d},endDate=${endDate}"));
		
		//=======================================================================================================
		//B5: Of the new patients enrolled in the last month/quarter, % with height and weight recorded at intake
		//=======================================================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveWeightAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveWeightAtIntake",
		            DMProgram, DDBform, weight);
		CompositionCohortDefinition patientsEnrolledAndHaveHeightAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveHeightAtIntake",
		            DMProgram, DDBform, height);
		
		CompositionCohortDefinition heightWeightIntake = new CompositionCohortDefinition();
		heightWeightIntake.setName("patientsEnrolledAndHeightWeightRecordedAtIntake");
		heightWeightIntake.addParameter(new Parameter("startDate", "start", Date.class));
		heightWeightIntake.addParameter(new Parameter("endDate", "endDate", Date.class));
		heightWeightIntake.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveWeightAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		heightWeightIntake.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveHeightAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		heightWeightIntake.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndHeightWeightRecordedAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndHeightWeightRecordedAtIntakeCountIndicator", heightWeightIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-1m+1d},endDate=${endDate}"));
		
		//==========================================================================================
		//B6: Of the new patients enrolled in the last month/quarter, % with BP recorded at intake
		//==========================================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveDiastolicAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveDiastolicAtIntake",
		            DMProgram, DDBform, diastolicBP);
		CompositionCohortDefinition patientsEnrolledAndHaveSystolicAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveSystolicAtIntake",
		            DMProgram, DDBform, systolicBP);
		
		CompositionCohortDefinition patientsEnrolledAndBPRecordedAtIntake = new CompositionCohortDefinition();
		patientsEnrolledAndBPRecordedAtIntake.setName("patientsEnrolledAndBPRecordedAtIntake");
		patientsEnrolledAndBPRecordedAtIntake.addParameter(new Parameter("startDate", "start", Date.class));
		patientsEnrolledAndBPRecordedAtIntake.addParameter(new Parameter("endDate", "end", Date.class));
		patientsEnrolledAndBPRecordedAtIntake.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveDiastolicAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndBPRecordedAtIntake.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveSystolicAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndBPRecordedAtIntake.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndBPRecordedAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndBPRecordedAtIntakeCountIndicator", patientsEnrolledAndBPRecordedAtIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-1m},endDate=${endDate}"));
		
		//==========================================================================================================
		//B7: Of the new patients enrolled in the last month/quarter, % with Neuropathy status recorded at intake
		//==========================================================================================================
		
		CompositionCohortDefinition patientsEnrolledAndHaveSensationLeftFootAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveDiastolicAtIntake",
		            DMProgram, DDBform, sensationInLeftFoot);
		CompositionCohortDefinition patientsEnrolledAndHaveSensationRightFootAtIntake = Cohorts
		        .getNewPatientsWithObservationAtIntakeBetweenStartAndEndDate("patientsEnrolledAndHaveSystolicAtIntake",
		            DMProgram, DDBform, sensationInRightFoot);
		
		CompositionCohortDefinition patientsEnrolledAndNeuropathyCheckedAtIntake = new CompositionCohortDefinition();
		patientsEnrolledAndNeuropathyCheckedAtIntake.setName("patientsEnrolledAndNeuropathyCheckedAtIntake");
		patientsEnrolledAndNeuropathyCheckedAtIntake.addParameter(new Parameter("startDate", "start", Date.class));
		patientsEnrolledAndNeuropathyCheckedAtIntake.addParameter(new Parameter("endDate", "end", Date.class));
		patientsEnrolledAndNeuropathyCheckedAtIntake.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveSensationLeftFootAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndNeuropathyCheckedAtIntake.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsEnrolledAndHaveSensationRightFootAtIntake, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndNeuropathyCheckedAtIntake.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndNeuropathyCheckedAtIntakeCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsEnrolledAndNeuropathyCheckedAtIntakeCountIndicator", patientsEnrolledAndNeuropathyCheckedAtIntake,
		    ParameterizableUtil.createParameterMappings("startDate=${endDate-1m+1d},endDate=${endDate}"));
		
		//==================================================================================
		//D1: Of total patients seen in the last month/quarter, % with no regimen documented
		//==================================================================================
		
		SqlCohortDefinition patientOnRegimen = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("patientsOnRegime",
		    diabetesDrugConcepts);
		
		CompositionCohortDefinition patientsSeenAndNotOnAnyDMRegimen = new CompositionCohortDefinition();
		patientsSeenAndNotOnAnyDMRegimen.setName("patientsSeenAndNotOnAnyDMRegimen");
		patientsSeenAndNotOnAnyDMRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndNotOnAnyDMRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndNotOnAnyDMRegimen.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsSeenComposition, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenAndNotOnAnyDMRegimen.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientOnRegimen, ParameterizableUtil
		            .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenAndNotOnAnyDMRegimen.setCompositionString("1 AND (NOT 2)");
		
		CohortIndicator patientsSeenAndNotOnAnyDMRegimenCountMonthIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndNotOnAnyDMRegimenCountMonthIndicator", patientsSeenAndNotOnAnyDMRegimen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-1m+1d},onOrBefore=${endDate}"));
		
		//========================================================
		//        Adding columns to data set definition         //
		//========================================================
		
		dsd.addColumn("A2QM1", "Total # of patients seen in the last month one", new Mapped(patientsSeenMonthOneIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A3QM1", "Total # of new patients enrolled in the month one", new Mapped(
		        patientEnrolledInDMMonthOneIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn("A4QM1", "Total # of patients with return visit in the last month", new Mapped(
		        patientsRDVMonthIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "B4NM",
		    "New patients enrolled in the last month, Number with Glucose done at intake",
		    new Mapped(patientsEnrolledAndHaveglucoseAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "B5NM",
		    "Of the new patients enrolled in the last month, Number with HeightWeight recorded at intake",
		    new Mapped(patientsEnrolledAndHeightWeightRecordedAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "B6NM",
		    "new patients enrolled in the last month, Number with BP recorded at intake",
		    new Mapped(patientsEnrolledAndBPRecordedAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "B7NM",
		    "new patients enrolled in the last month, Number with Neuropathy checked at intake",
		    new Mapped(patientsEnrolledAndNeuropathyCheckedAtIntakeCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		dsd.addColumn(
		    "D1NM",
		    "Total patients seen in the last month, number with no regimen documented",
		    new Mapped(patientsSeenAndNotOnAnyDMRegimenCountMonthIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
	}
	
	private void setUpProperties() {
		DMProgram = gp.getProgram(GlobalPropertiesManagement.DM_PROGRAM);
		DMPrograms.add(DMProgram);
		DMEncounterTypeId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
		    GlobalPropertiesManagement.DIABETES_VISIT));
		DMEncounterType = gp.getEncounterType(GlobalPropertiesManagement.DIABETES_VISIT);
		DDBform = gp.getForm(GlobalPropertiesManagement.DIABETES_DDB_FORM);
		DiabetesFlowVisit = gp.getForm(GlobalPropertiesManagement.DIABETES_FLOW_VISIT);
		DDBforms.add(DDBform);
		DDBforms.add(DiabetesFlowVisit);
		
		patientsSeenEncounterTypes.add(DMEncounterType);
		
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		glucose = gp.getConcept(GlobalPropertiesManagement.GLUCOSE);
		weight = gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
		height = gp.getConcept(GlobalPropertiesManagement.HEIGHT_CONCEPT);
		diastolicBP = gp.getConcept(GlobalPropertiesManagement.DIASTOLIC_BLOOD_PRESSURE);
		systolicBP = gp.getConcept(GlobalPropertiesManagement.SYSTOLIC_BLOOD_PRESSURE);
		hbA1c = gp.getConcept(GlobalPropertiesManagement.HBA1C);
		onOrBefOnOrAf.add("onOrBef");
		onOrBefOnOrAf.add("onOrAf");
		sensationInRightFoot = gp.getConcept(GlobalPropertiesManagement.SENSATION_IN_RIGHT_FOOT);
		sensationInLeftFoot = gp.getConcept(GlobalPropertiesManagement.SENSATION_IN_LEFT_FOOT);
		
		neuropathyConcepts.add(sensationInLeftFoot);
		neuropathyConcepts.add(sensationInRightFoot);
		
		lisinopril = gp.getConcept(GlobalPropertiesManagement.LISINOPRIL);
		captopril = gp.getConcept(GlobalPropertiesManagement.CAPTOPRIL);
		
		lisinoprilCaptopril.add(lisinopril);
		lisinoprilCaptopril.add(captopril);
		
		onAceInhibitorsDrugs.addAll(RwandaReportsUtil.getDrugs(lisinopril));
		onAceInhibitorsDrugs.addAll(RwandaReportsUtil.getDrugs(captopril));
		
		creatinine = gp.getConcept(GlobalPropertiesManagement.SERUM_CREATININE);
		insulin7030 = gp.getConcept(GlobalPropertiesManagement.INSULIN_70_30);
		insulinLente = gp.getConcept(GlobalPropertiesManagement.INSULIN_LENTE);
		insulinRapide = gp.getConcept(GlobalPropertiesManagement.INSULIN_RAPIDE);
		
		insulinConcepts.add(insulin7030);
		insulinConcepts.add(insulinLente);
		insulinConcepts.add(insulinRapide);
		
		diabetesDrugConcepts = gp.getConceptsByConceptSet(GlobalPropertiesManagement.DIABETES_TREATMENT_DRUG_SET);
		
		metformin = gp.getConcept(GlobalPropertiesManagement.METFORMIN_DRUG);
		glibenclimide = gp.getConcept(GlobalPropertiesManagement.GLIBENCLAMIDE_DRUG);
		
		metforminAndGlibenclimideConcepts.add(metformin);
		metforminAndGlibenclimideConcepts.add(glibenclimide);
		
		//	diedState = DMProgram.getWorkflow(28).getState("PATIENT DIED");
		
		diedState = DMProgram.getWorkflowByName(
		    Context.getAdministrationService().getGlobalProperty(GlobalPropertiesManagement.DIABETE_TREATMENT_WORKFLOW))
		        .getState(
		            Context.getAdministrationService().getGlobalProperty(GlobalPropertiesManagement.PATIENT_DIED_STATE));
		
		admitToHospital = gp.getConcept(GlobalPropertiesManagement.HOSPITAL_ADMITTANCE);
		locOfHosp = gp.getConcept(GlobalPropertiesManagement.LOCATION_OF_HOSPITALIZATION);


		NCDSpecificOutcomes=gp.getConcept(GlobalPropertiesManagement.NCD_SPECIFIC_OUTCOMES);
		NCDRelatedDeathOutcomes= gp.getConcept(GlobalPropertiesManagement.NCD_RELATED_DEATH_OUTCOMES);

		exitReasonFromCare=gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
		patientDiedConcept=gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);

		unknownCauseDeathOutcomes =gp.getConcept(GlobalPropertiesManagement.UNKNOWN_CAUSE_OF_DEATH_OUTCOMES);
		otherCauseOfDeathOutcomes =gp.getConcept(GlobalPropertiesManagement.OTHER_CAUSE_OF_DEATH_OUTCOMES);


		DeathOutcomeResons.add(NCDRelatedDeathOutcomes);
		DeathOutcomeResons.add(unknownCauseDeathOutcomes);
		DeathOutcomeResons.add(otherCauseOfDeathOutcomes);

		urinaryAlbumin=gp.getConcept(GlobalPropertiesManagement.URINARY_ALBUMIN);

	}
}
