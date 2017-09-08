/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports.reporting;

import java.util.*;

import org.openmrs.*;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
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
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

/**
 *
 */
public class SetupHeartFailureQuarterlyAndMonthlyReport {
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	// properties
	private Program heartFailureProgram;
	
	private List<Program> HFPrograms = new ArrayList<Program>();
	
	private EncounterType heartFailureEncounterType;

	private List<EncounterType> heartFailureEncounterTypes=new ArrayList<EncounterType>();;

	private EncounterType postOpVisit;

	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();

	List<Concept> DeathOutcomeResons=new  ArrayList<Concept>();
	
	private Form heartFailureDDBform;
	
	private Form postoperatoireCardiaqueRDV;
	
	private Form insuffisanceCardiaqueRDV;
	
	private List<Form> postoperatoireAndinsuffisanceRDV = new ArrayList<Form>();
	
	private Form postoperatoireCardiaqueHospitalisations;
	
	private Form insuffisanceCardiaqueHospitalisations;
	
	private List<Form> postoperatoireAndinsuffisanceHospitalisations = new ArrayList<Form>();
	
	private List<Form> postoperatoire = new ArrayList<Form>();
	
	private List<Form> echoForms = new ArrayList<Form>();
	
	private List<Form> cardConsultForm = new ArrayList<Form>();
	
	private Concept NYHACLASS;
	
	private Concept NYHACLASS4;
	
	private Concept serumCreatinine;
	
	private Concept systolicBP;
	
	private Concept carvedilol;
	
	private Concept atenolol;
	
	private Concept lisinopril;
	
	private Concept captopril;
	
	private Concept furosemide;
	
	private Concept aldactone;
	
	private Concept warfarin;
	
	private Concept DDBEchoResult;
	
	private Concept ejectionFraction;
	
	private Concept echoComment;
	
	private Concept notDone;
	
	private Concept internationalNormalizedRatio;
	
	private List<Concept> carvedilolAndAtenolol = new ArrayList<Concept>();
	
	private List<Concept> echoConcepts = new ArrayList<Concept>();
	
	private List<Concept> lisinoprilAndCaptopril = new ArrayList<Concept>();
	
	private ProgramWorkflowState postOperative;
	
	private ProgramWorkflowState patientDied;
	
	private Concept heartFailureDiagnosis;
	private Concept cardiomyopathy;
	private Concept miralStenosis;
	private Concept rhuematicHeartDisease;
	private Concept hypertensiveDisease;
	private Concept congenitalHeartDisease;



	private Concept NCDSpecificOutcomes;
	private Concept NCDRelatedDeathOutcomes;
	private Concept unknownCauseDeathOutcomes;
	private Concept otherCauseOfDeathOutcomes;

	private Concept exitReasonFromCare;
	private Concept patientDiedConcept;

	private Concept HIVStatus;
	
	
	public void setup() throws Exception {
		
		setUpProperties();
		
		// Monthly report set-up
		ReportDefinition monthlyRd = new ReportDefinition();
		monthlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		monthlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		monthlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		monthlyRd.setName("NCD-Heart Failure Indicator Report-Monthly");
		
		monthlyRd.addDataSetDefinition(createMonthlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
        // Quarterly Report Definition: Start
		
		ReportDefinition quarterlyRd = new ReportDefinition();
		quarterlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		quarterlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		quarterlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		quarterlyRd.setName("NCD-Heart Failure Indicator Report-Quarterly");
		
		quarterlyRd.addDataSetDefinition(createQuarterlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		// Quarterly Report Definition: End
		
		ProgramEnrollmentCohortDefinition patientEnrolledInHF = new ProgramEnrollmentCohortDefinition();
		patientEnrolledInHF.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInHF.setPrograms(HFPrograms);
		
		monthlyRd.setBaseCohortDefinition(patientEnrolledInHF,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));
		
		quarterlyRd.setBaseCohortDefinition(patientEnrolledInHF,
			    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));
		
		Helper.saveReportDefinition(monthlyRd);
		Helper.saveReportDefinition(quarterlyRd);
		
		ReportDesign monthlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRd,
		    "HF_Monthly_Indicator_Report.xls", "Heart Failure Indicator Monthly Report (Excel)", null);
		Properties monthlyProps = new Properties();
		monthlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Monthly Data Set");
		monthlyProps.put("sortWeight","5000");
		monthlyDesign.setProperties(monthlyProps);
		Helper.saveReportDesign(monthlyDesign);
		
		ReportDesign quarterlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(quarterlyRd,
			    "HF_Quarterly_Indicator_Report.xls", "Heart Failure Quarterly Indicator Report (Excel)", null);
			Properties quarterlyProps = new Properties();
			quarterlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Quarterly Data Set");
			quarterlyProps.put("sortWeight","5000");
			quarterlyDesign.setProperties(quarterlyProps);
			Helper.saveReportDesign(quarterlyDesign);
		
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Heart Failure Indicator Monthly Report (Excel)".equals(rd.getName()) || "Heart Failure Quarterly Indicator Report (Excel)".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("NCD-Heart Failure Indicator Report-Monthly");
		Helper.purgeReportDefinition("NCD-Heart Failure Indicator Report-Quarterly");
		
	}
	
	private void createMonthlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
		
		SqlEncounterQuery patientVisitsToHFClinic = new SqlEncounterQuery();
		
		patientVisitsToHFClinic.setQuery("select e.encounter_id from encounter e where e.encounter_type="
		        + heartFailureEncounterType.getEncounterTypeId()
		        + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0");
		patientVisitsToHFClinic.setName("patientVisitsToHFClinic");
		patientVisitsToHFClinic.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientVisitsToHFClinic.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		EncounterIndicator patientVisitsToHFClinicMonthIndicator = new EncounterIndicator();
		patientVisitsToHFClinicMonthIndicator.setName("patientVisitsToHFClinicMonthIndicator");
		patientVisitsToHFClinicMonthIndicator.setEncounterQuery(new Mapped<EncounterQuery>(patientVisitsToHFClinic,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${endDate-1m+1d}")));
		
		dsd.addColumn(patientVisitsToHFClinicMonthIndicator);
		
		//==============================================================
		// C2: % of Patient visits in the last quarter with documented BP
		//==============================================================
		SqlEncounterQuery patientVisitsWithDocumentedBP = new SqlEncounterQuery();
		
		patientVisitsWithDocumentedBP
		        .setQuery("select e.encounter_id from encounter e,obs o where o.encounter_id=e.encounter_id and e.encounter_type="
		                + heartFailureEncounterType.getEncounterTypeId()
		                + " and o.concept_id="
		                + systolicBP.getConceptId()
		                + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 ");
		patientVisitsWithDocumentedBP.setName("patientVisitsToHypertensionClinic");
		patientVisitsWithDocumentedBP.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientVisitsWithDocumentedBP.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		EncounterIndicator patientVisitsWithDocumentedBPIndicator = new EncounterIndicator();
		patientVisitsWithDocumentedBPIndicator.setName("patientVisitsWithDocumentedBPIndicator");
		patientVisitsWithDocumentedBPIndicator.setEncounterQuery(new Mapped<EncounterQuery>(patientVisitsWithDocumentedBP,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		
		dsd.addColumn(patientVisitsWithDocumentedBPIndicator);
		
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
  
//create quarterly cohort Data set
	private CohortIndicatorDataSetDefinition createQuarterlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Quarterly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createQuarterlyIndicators(dsd);
		return dsd;
	}
	
	private void createQuarterlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
		//=======================================================================
		//  A1: Total # of patient visits to Heart failure clinic in the last quarter
		//==================================================================
				
		
        SqlEncounterQuery patientVisitsToHFClinic = new SqlEncounterQuery();
		
		patientVisitsToHFClinic.setQuery("select e.encounter_id from encounter e where e.encounter_type="
		        + heartFailureEncounterType.getEncounterTypeId()
		        + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 group by e.encounter_datetime, e.patient_id");
		patientVisitsToHFClinic.setName("patientVisitsToHFClinic");
		patientVisitsToHFClinic.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientVisitsToHFClinic.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		EncounterIndicator patientVisitsToHFClinicQuarterlyIndicator = new EncounterIndicator();
		patientVisitsToHFClinicQuarterlyIndicator.setName("patientVisitsToHFClinicQuarterlyIndicator");
		patientVisitsToHFClinicQuarterlyIndicator.setEncounterQuery(new Mapped<EncounterQuery>(patientVisitsToHFClinic,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));
		
		dsd.addColumn(patientVisitsToHFClinicQuarterlyIndicator);
		
	}
	
	private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {

		EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
				onOrAfterOnOrBefore, heartFailureEncounterTypes);


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
				.createPatientsInStateNotPredatingProgramEnrolment(patientDied);

		//========================================================================================
		//  Active patients with no exit in Reporting periode
		//=======================================================================================

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



		// =======================================================
		// A2: Total # of patients seen in the last quarter
		// =======================================================

		
		CohortIndicator patientsSeenIndicator = Indicators.newCountIndicator("patientsSeenIndicator", patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn("A2Q", "Total # of patients seen in the last quarter", new Mapped(patientsSeenIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================================
		// A3: Total # of new patients enrolled in the last month/quarter
		//==================================================================
		
		ProgramEnrollmentCohortDefinition patientEnrolledInHFProgram = Cohorts.createProgramEnrollmentParameterizedByStartEndDate(
		    "Enrolled In Heart Failure Program", heartFailureProgram);





		ProgramEnrollmentCohortDefinition patientEnrolledInHFProgramByEndDate = Cohorts.createProgramEnrollmentEverByEndDate("Enrolled In HFP",
				heartFailureProgram);



		CompositionCohortDefinition patientEnrolledInHFPAndSeenInSameQuarter=new CompositionCohortDefinition();
		patientEnrolledInHFPAndSeenInSameQuarter.setName("patientEnrolledInCRDPAndSeenInSameQuarter");/*
		patientEnrolledInHFPAndSeenInSameQuarter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientEnrolledInHFPAndSeenInSameQuarter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));*/
		patientEnrolledInHFPAndSeenInSameQuarter.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInHFPAndSeenInSameQuarter.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInHFPAndSeenInSameQuarter.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
						.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore},enrolledOnOrAfter=${enrolledOnOrAfter}")));
		patientEnrolledInHFPAndSeenInSameQuarter.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(patientEnrolledInHFProgramByEndDate, ParameterizableUtil
						.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore-3m}")));
		/*patientEnrolledInHFPAndSeenInSameQuarter.getSearches().put(
				"3",
				new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));*/

		//patientEnrolledInHFPAndSeenInSameQuarter.setCompositionString("(1 and (not 2)) and 3");
		patientEnrolledInHFPAndSeenInSameQuarter.setCompositionString("1 and (not 2)");



		
		CohortIndicator patientEnrolledInHFProgramQuarterIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFProgramQuarterIndicator", patientEnrolledInHFPAndSeenInSameQuarter,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "A3Q",
		    "Total # of new patients enrolled in the last quarter",
		    new Mapped(patientEnrolledInHFProgramQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		// A3Qreview: Proportion of active asthma patients in currently enrolled patients

		InProgramCohortDefinition inHFProgramByEndDate=Cohorts.createInProgramParameterizableByDate("In HF by EndDate",heartFailureProgram);
		ProgramEnrollmentCohortDefinition completedInHFProgramByEndDate=Cohorts.createProgramCompletedByEndDate("Completed HF by EndDate",heartFailureProgram);

		CompositionCohortDefinition currentlyInProgramAndNotCompleted= new CompositionCohortDefinition();
		currentlyInProgramAndNotCompleted.setName("currentlyInProgramAndNotCompleted");
		currentlyInProgramAndNotCompleted.addParameter(new Parameter("onDate", "onDate", Date.class));
		currentlyInProgramAndNotCompleted.addParameter(new Parameter("completedOnOrBefore", "completedOnOrBefore", Date.class));
		currentlyInProgramAndNotCompleted.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(inHFProgramByEndDate, ParameterizableUtil
						.createParameterMappings("onDate=${onDate}")));
		currentlyInProgramAndNotCompleted.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(completedInHFProgramByEndDate, ParameterizableUtil
						.createParameterMappings("completedOnOrBefore=${completedOnOrBefore}")));
		currentlyInProgramAndNotCompleted.setCompositionString("1 and (not 2)");

		CohortIndicator currentlyInProgramAndNotCompletedIndicator=Indicators.newCountIndicator(
				"currentlyInProgramAndNotCompletedIndicator", currentlyInProgramAndNotCompleted,
				ParameterizableUtil.createParameterMappings("onDate=${endDate},completedOnOrBefore=${endDate}"));

		dsd.addColumn(
				"A3QReview",
				"Percentage of active HF patients in the last Quarter(active/ currently enrolled)",
				new Mapped(currentlyInProgramAndNotCompletedIndicator, ParameterizableUtil
						.createParameterMappings("endDate=${endDate}")), "");


		
		//======================================
		// A4: Total # of patients ever enrolled
		//A7 Review: # Ever enrolled
		//======================================
		
		ProgramEnrollmentCohortDefinition patientEverEnrolledInHFProgram = Cohorts.createProgramEnrollmentEverByEndDate(
				"Ever Enrolled In Heart Failure Program", heartFailureProgram);

		CohortIndicator patientEverEnrolledInHFProgramIndicator = Indicators.newCountIndicator(
		    "patientEverEnrolledInHFProgramIndicator", patientEverEnrolledInHFProgram,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));

		dsd.addColumn(
		    "A7Q",
		    "Total # of patients ever enrolled",
		    new Mapped(patientEverEnrolledInHFProgramIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");


		//=======================================================================
		//A4: Total # of patients ever enrolled
		//  Review: % of all active patients that are age < 16 years old
		//==================================================================

		SqlCohortDefinition patientsUnderFifteenAtEnrollementDate = Cohorts.createUnder16AtEnrollmentCohort(
				"patientsUnder15AtEnrollment", heartFailureProgram);

		CompositionCohortDefinition patientsUnderFifteenComposition = new CompositionCohortDefinition();
		patientsUnderFifteenComposition.setName("patientsUnderFifteenComposition");
		patientsUnderFifteenComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsUnderFifteenComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));

		patientsUnderFifteenComposition.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsUnderFifteenComposition.getSearches().put("2",
				new Mapped<CohortDefinition>(patientsUnderFifteenAtEnrollementDate, null));
		patientsUnderFifteenComposition.setCompositionString("1 AND 2");

		CohortIndicator patientsUnderFifteenCountIndicator = Indicators.newCountIndicator(
				"patientsUnderFifteenCountIndicator", patientsUnderFifteenComposition,
				ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));

		//=================================================
		//     Adding columns to data set definition     //
		//=================================================

		dsd.addColumn(
				"A4Q",
				"Pediatric: Of the new patients enrolled in the last quarter, number < 16 years old at intake",
				new Mapped(patientsUnderFifteenCountIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate}")),
				"");







		//======================================
		// A5: Percentage of eligible new Heart Failure  patients with at least one return visit in the quarter
		// A5 Review: Active Male patient
		//======================================
		GenderCohortDefinition malePatient=Cohorts.createMaleCohortDefinition("malePatient");

		CompositionCohortDefinition patientActiveMale = new CompositionCohortDefinition();
		patientActiveMale.setName("patientActiveMale");
		patientActiveMale.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveMale.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveMale.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveMale.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveMale.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}")));
		patientActiveMale.getSearches().put("2",new Mapped<CohortDefinition>(malePatient, null));

		patientActiveMale.setCompositionString("1 AND 2");



		CohortIndicator patientActiveMaleIndicator = Indicators.newCountIndicator("patientActiveMaleIndicator",
				patientActiveMale,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("ActiveMale", "Total # of Active Male patients", new Mapped(
				patientActiveMaleIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");







		//=================================================================================
		//  A8: % of deaths which are disease related
		//=================================================================================
//Numerator

		CohortIndicator NCDRelatedDeathIndicator = Indicators.newCountIndicator(
				"NCDRelatedDeathIndicator", NCDRelatedDeath,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		dsd.addColumn("A8QN", "% of deaths which are disease related", new Mapped(
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
		//activePatientsInPatientDiedState.setCompositionString("2 OR 3 OR 4");

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



		//======================================
		// A6: Of all active patients in the last quarter, % HIV status documented
		//======================================

		SqlCohortDefinition patientsWithHIVStatusDocumented= Cohorts.getPatientsWithObsEver(
				"patientsWithHIVStatusDocumented", HIVStatus);

		CompositionCohortDefinition patientActiveHIV = new CompositionCohortDefinition();
		patientActiveHIV.setName("patientActiveHIV");
		patientActiveHIV.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveHIV.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveHIV.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveHIV.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveHIV.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}")));
		patientActiveHIV.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithHIVStatusDocumented, null));

		patientActiveHIV.setCompositionString("1 AND 2");



		CohortIndicator patientActiveHIVIndicator = Indicators.newCountIndicator("patientActiveHIVIndicator",
				patientActiveHIV,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn("ActiveHIVDoc", "Total # of Active patients with HIV documented", new Mapped(
				patientActiveHIVIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


		//======================================
		// B1: Of the patients newly enrolled in the previous quarter,  # and % with echocardiography documented
		//======================================
		
		
		CodedObsCohortDefinition echoResultNotDone=Cohorts.createCodedObsCohortDefinition("echoResultNotDone",onOrAfterOnOrBefore, DDBEchoResult, notDone, SetComparator.IN, TimeModifier.LAST);


		SqlCohortDefinition echoDocumanted=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("echoDocumanted", echoForms, echoConcepts);

		/*SqlCohortDefinition echoDocumanted=new SqlCohortDefinition("select distinct o.person_id from encounter e, obs o where e.encounter_id=o.encounter_id and e.form_id in("+heartFailureDDBform.getFormId()+") and o.concept_id in ("+DDBEchoResult.getConceptId()+") and o.voided=0 and e.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and (o.value_numeric is NOT NULL or o.value_coded is NOT NULL or o.value_datetime is NOT NULL or o.value_boolean is NOT NULL)");
		echoDocumanted.addParameter(new Parameter("startDate", "startDate", Date.class));
		echoDocumanted.addParameter(new Parameter("endDate", "endDate", Date.class));
*/

		CompositionCohortDefinition patientsWithEchoDocumanted = new CompositionCohortDefinition();
		patientsWithEchoDocumanted.setName("patientsWithEchoDocumanted");
		patientsWithEchoDocumanted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWithEchoDocumanted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsWithEchoDocumanted.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsWithEchoDocumanted.addParameter(new Parameter("endDate", "endDate", Date.class));
				patientsWithEchoDocumanted.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientsWithEchoDocumanted.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientsWithEchoDocumanted.getSearches().put("1",new Mapped<CohortDefinition>(echoResultNotDone, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsWithEchoDocumanted.getSearches().put("2",new Mapped<CohortDefinition>(echoDocumanted, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsWithEchoDocumanted.getSearches().put("3",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientsWithEchoDocumanted.setCompositionString("2 AND 3 AND (NOT 1)");
		//patientsWithEchoDocumanted.setCompositionString("2 AND (NOT 1)");

		CohortIndicator patientsWithEchoDocumantedIndicator = Indicators.newCountIndicator(
		    "patientsWithEchoDocumantedIndicator", patientsWithEchoDocumanted,ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));

		dsd.addColumn(
		    "B1",
		    "Total number of patients newly enrolled in the previous quarter with echocardiography documented",
		    new Mapped(patientsWithEchoDocumantedIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//======================================
		// B2: Of the patients newly enrolled in the previous two quarters,  # and % with cardiology consultation with 6 months of progam enrollment date
		//======================================
		
		
		
		EncounterCohortDefinition patientsWithCardioConsultForm=Cohorts.createEncounterBasedOnForms("patientsWithCardioConsultForm",onOrAfterOnOrBefore,cardConsultForm);
		
		CompositionCohortDefinition patientsWithCardConsult = new CompositionCohortDefinition();
		patientsWithCardConsult.setName("patientsWithCardConsult");
		patientsWithCardConsult.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWithCardConsult.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsWithCardConsult.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientsWithCardConsult.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientsWithCardConsult.getSearches().put("1",new Mapped<CohortDefinition>(patientsWithCardioConsultForm, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));		
		patientsWithCardConsult.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFPAndSeenInSameQuarter, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientsWithCardConsult.setCompositionString("1 AND 2");
		
		
		/*CohortIndicator patientEnrolledInHFProgramIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFProgramIndicator", patientEnrolledInHFProgram,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${endDate-6m},enrolledOnOrBefore=${endDate}"));
*/
		CohortIndicator patientEnrolledInHFProgramInLastTwoQuarterIndicator = Indicators.newCountIndicator(
				"patientEnrolledInHFProgramQuarterIndicator", patientEnrolledInHFPAndSeenInSameQuarter,
				ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${endDate-6m},enrolledOnOrBefore=${endDate}"));



		CohortIndicator patientsWithCardConsultIndicator = Indicators.newCountIndicator(
		    "patientsWithCardConsultIndicator", patientsWithCardConsult,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${endDate-6m},enrolledOnOrBefore=${endDate},onOrAfter=${endDate-6m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B2D",
		    "Total number of patients newly enrolled in the previous two quarters",
		    new Mapped(patientEnrolledInHFProgramInLastTwoQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B2N",
		    "Total number of patients newly enrolled in the previous two quarters, with cardiology consultation with 6 months ",
		    new Mapped(patientsWithCardConsultIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		

		//=========================================================
		// B3: Of total active patients, % ever seen by a cardiologist
		//=========================================================

		EncounterCohortDefinition patientsWithCardioConsultFormEver=Cohorts.createEncounterBasedOnForms("patientsWithCardioConsultFormEver",cardConsultForm);

		CompositionCohortDefinition activePatientWithCardioConsultFormEver=new CompositionCohortDefinition();
		activePatientWithCardioConsultFormEver.setName("activePatientWithCardioConsultFormEver");
		activePatientWithCardioConsultFormEver.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientWithCardioConsultFormEver.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientWithCardioConsultFormEver.addParameter(new Parameter("startDate", "startDate", Date.class));
		activePatientWithCardioConsultFormEver.addParameter(new Parameter("endDate", "endDate", Date.class));
		activePatientWithCardioConsultFormEver.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		activePatientWithCardioConsultFormEver.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithCardioConsultFormEver,null));
		activePatientWithCardioConsultFormEver.setCompositionString("1 AND 2");


		CohortIndicator activePatientWithCardioConsultFormEverIndicator = Indicators.newCountIndicator(
		    "activePatientWithCardioConsultFormEverIndicator", activePatientWithCardioConsultFormEver,ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));

		dsd.addColumn(
				"B3N",
				"Of total active patients, % ever seen by a cardiologist ",
				new Mapped(activePatientWithCardioConsultFormEverIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		///======================================
		// B3: Of the patients newly enrolled in the previous quarter, distribution of type of heart failure diagnosis within 60 days of enrollment date 
		//======================================
		
		
		//======================================
		// B3A: cardiomyopathy
		//======================================		
		
		SqlCohortDefinition patientWithCardiomyopathyDiagnosis=Cohorts.getPatientsWithCodedObsEver("patientWithCardiomyopathyDiagnosis", heartFailureDiagnosis, cardiomyopathy);
		
		CompositionCohortDefinition patientActiveWithCardiomyopathyDiagnosis = new CompositionCohortDefinition();
		patientActiveWithCardiomyopathyDiagnosis.setName("patientActiveWithCardiomyopathyDiagnosis");
		patientActiveWithCardiomyopathyDiagnosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithCardiomyopathyDiagnosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithCardiomyopathyDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithCardiomyopathyDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithCardiomyopathyDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCardiomyopathyDiagnosis, null));
		patientActiveWithCardiomyopathyDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithCardiomyopathyDiagnosis.setCompositionString("1 AND 2");
		
		

		CohortIndicator patientActiveWithCardiomyopathyDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientActiveWithCardiomyopathyDiagnosisIndicator", patientActiveWithCardiomyopathyDiagnosis,ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B3AN",
		    "Of all active patients in the last Q, disaggregated by heart failure diagnosis: cardiomyopathy",
		    new Mapped(patientActiveWithCardiomyopathyDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//======================================
		// B3B: rheumatic heart disease
		//======================================		
		
		SqlCohortDefinition patientWithRheumaticDiagnosis=Cohorts.getPatientsWithCodedObsEver("patientWithRheumaticDiagnosis", heartFailureDiagnosis, rhuematicHeartDisease);

		CompositionCohortDefinition patientActiveWithRheumaticDiagnosis = new CompositionCohortDefinition();
		patientActiveWithRheumaticDiagnosis.setName("patientActiveWithRheumaticDiagnosis");
		patientActiveWithRheumaticDiagnosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithRheumaticDiagnosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithRheumaticDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithRheumaticDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithRheumaticDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithRheumaticDiagnosis,null));
		patientActiveWithRheumaticDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));

		patientActiveWithRheumaticDiagnosis.setCompositionString("1 AND 2");

		CohortIndicator patientActiveWithRheumaticDiagnosisIndicator = Indicators.newCountIndicator(
				"patientActiveWithRheumaticDiagnosisIndicator", patientActiveWithRheumaticDiagnosis,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));

		dsd.addColumn(
		    "B3BN",
		    "Of all active patients in the last Q, disaggregated by heart failure diagnosis: Rheumatic",
		    new Mapped(patientActiveWithRheumaticDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// B3C: mitral stenosis
		//======================================		
		
		SqlCohortDefinition patientWithMitralStenosisDiagnosis=Cohorts.getPatientsWithCodedObsEver("patientWithMitralStenosisDiagnosis", heartFailureDiagnosis, miralStenosis);

		CompositionCohortDefinition patientActiveWithMitralStenosisDiagnosis = new CompositionCohortDefinition();
		patientActiveWithMitralStenosisDiagnosis.setName("patientActiveWithMitralStenosisDiagnosis");
		patientActiveWithMitralStenosisDiagnosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithMitralStenosisDiagnosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithMitralStenosisDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithMitralStenosisDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithMitralStenosisDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithMitralStenosisDiagnosis, null));
		patientActiveWithMitralStenosisDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));

		patientActiveWithMitralStenosisDiagnosis.setCompositionString("1 AND 2");



		CohortIndicator patientActiveWithMitralStenosisDiagnosisIndicator = Indicators.newCountIndicator(
				"patientActiveWithMitralStenosisDiagnosisIndicator", patientActiveWithMitralStenosisDiagnosis,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));

		dsd.addColumn(
		    "B3CN",
		    "Of all active patients in the last Q, disaggregated by heart failure diagnosis: Mitral stenosis",
		    new Mapped(patientActiveWithMitralStenosisDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		//======================================
		// B3D: hypertensive heart disease
		//======================================		
		
		SqlCohortDefinition patientWithHypertensiveDiagnosis=Cohorts.getPatientsWithCodedObsEver("patientWithhypertensiveDiagnosis", heartFailureDiagnosis, hypertensiveDisease);

		CompositionCohortDefinition patientActiveWithHypertensiveDiagnosis = new CompositionCohortDefinition();
		patientActiveWithHypertensiveDiagnosis.setName("patientActiveWithHypertensiveDiagnosis");
		patientActiveWithHypertensiveDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithHypertensiveDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithHypertensiveDiagnosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithHypertensiveDiagnosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithHypertensiveDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithHypertensiveDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithHypertensiveDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithHypertensiveDiagnosis, null));
		patientActiveWithHypertensiveDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));

		patientActiveWithHypertensiveDiagnosis.setCompositionString("1 AND 2");



		CohortIndicator patientActiveWithHypertensiveDiagnosisIndicator = Indicators.newCountIndicator(
				"patientActiveWithHypertensiveDiagnosisIndicator", patientActiveWithHypertensiveDiagnosis,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
		    "B3DN",
		    "Of all active patients in the last Q, disaggregated by heart failure diagnosis: Hypertensive heart disease",
		    new Mapped(patientActiveWithHypertensiveDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// B3E: patient congenital Diagnosis
		//======================================		
		
		
		SqlCohortDefinition patientWithcongenitalDiagnosis=Cohorts.getPatientsWithCodedObsEver("patientWithOtherDiagnosis", heartFailureDiagnosis, congenitalHeartDisease);
		
		CompositionCohortDefinition patientActiveWithcongenitalDiagnosis = new CompositionCohortDefinition();
		patientActiveWithcongenitalDiagnosis.setName("patientActiveWithcongenitalDiagnosis");
		patientActiveWithcongenitalDiagnosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithcongenitalDiagnosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithcongenitalDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithcongenitalDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithcongenitalDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithcongenitalDiagnosis, null));
		patientActiveWithcongenitalDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));

		patientActiveWithcongenitalDiagnosis.setCompositionString("1 AND 2");
		
		

		CohortIndicator patientActiveWithcongenitalDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientActiveWithcongenitalDiagnosisIndicator", patientActiveWithcongenitalDiagnosis,ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));
		
		dsd.addColumn(
		    "B3EN",
		    "Of all active patients in the last Q, disaggregated by heart failure diagnosis: Congenital Diagnosis",
		    new Mapped(patientActiveWithcongenitalDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// B3F: No diagnosis
		//======================================		
/*
		
		CompositionCohortDefinition patientEnrolledWithNoDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithNoDiagnosis.setName("patientEnrolledWithNoDiagnosis");
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithNoDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCardiomyopathyDiagnosis, null));
		patientEnrolledWithNoDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientWithRheumaticDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithNoDiagnosis.getSearches().put("3",new Mapped<CohortDefinition>(patientWithMitralStenosisDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithNoDiagnosis.getSearches().put("4",new Mapped<CohortDefinition>(patientWithHypertensiveDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithNoDiagnosis.getSearches().put("5",new Mapped<CohortDefinition>(patientWithOtherDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		
		patientEnrolledWithNoDiagnosis.getSearches().put("6",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithNoDiagnosis.setCompositionString("6 AND (NOT (1 OR 2 OR 3 OR 4 OR 5))");
		
		

		CohortIndicator patientEnrolledWithNoDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithNoDiagnosisIndicator", patientEnrolledWithNoDiagnosis,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B3FN",
		    "Total number of patients newly enrolled in the previous quarter with no diagnosis",
		    new Mapped(patientEnrolledWithNoDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		*/
		
		

		//======================================
		// B5: Of total active, % without NYHA class documented at last visit
		//======================================



		SqlCohortDefinition patientWithNYHAClass=Cohorts.getPatientsWithObsinLastFormSubmitted("patientWithNYHAClass", NYHACLASS, heartFailureEncounterType);

		CompositionCohortDefinition patientActiveWithoutNYHAClass = new CompositionCohortDefinition();
		patientActiveWithoutNYHAClass.setName("patientActiveWithcongenitalDiagnosis");
		patientActiveWithoutNYHAClass.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithoutNYHAClass.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithoutNYHAClass.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithoutNYHAClass.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithoutNYHAClass.getSearches().put("1",new Mapped<CohortDefinition>(patientWithNYHAClass, null));
		patientActiveWithoutNYHAClass.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));

		patientActiveWithoutNYHAClass.setCompositionString("2 AND (NOT 1)");
		//patientActiveWithoutNYHAClass.setCompositionString("1 AND (NOT 2)");

		CohortIndicator patientActiveWithoutNYHAClassIndicator = Indicators.newCountIndicator(
				"patientActiveWithoutNYHAClassIndicator", patientActiveWithoutNYHAClass,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));

		dsd.addColumn(
				"B5Act",
				"Of total active, % without NYHA class documented at last visit",
				new Mapped(patientActiveWithoutNYHAClassIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//==========================================
//New patients enrolled in the last quarter, disaggregated by NYHA class
//==========================================

		Collection<ConceptAnswer> concAns=Context.getConceptService().getConcept(NYHACLASS.getConceptId()).getAnswers();

		for (ConceptAnswer ca:concAns){
			String conName=ca.getAnswerConcept().getName().toString().replaceAll("\\s", "");

			SqlCohortDefinition patientWithCurrentNYHAClass=Cohorts.getPatientsWithLastCodedObsEver("patientWithOtherDiagnosis", NYHACLASS, ca.getAnswerConcept());

			CompositionCohortDefinition patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass=new CompositionCohortDefinition();
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.setName("patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass");/*
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));*/
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCurrentNYHAClass,null));
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFPAndSeenInSameQuarter, ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore},enrolledOnOrBefore=${enrolledOnOrBefore}")));
			patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass.setCompositionString("1 AND 2");



			CohortIndicator patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClassIndicator = Indicators.newCountIndicator(
					"patientEnrolledInHFProgramQuarterIndicator", patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClass,
					ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));

			dsd.addColumn(
					conName,
					"Total # of new patients enrolled in the last quarter with "+conName,
					new Mapped(patientEnrolledInHFPAndSeenInSameQuarterWithCurrentNYHAClassIndicator, ParameterizableUtil
							.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		}



		/*CodedObsCohortDefinition patientWithNYHAClassIV=Cohorts.createCodedObsCohortDefinition("patientWithNYHAClassIV", NYHACLASS, NYHACLASS4, SetComparator.IN, TimeModifier.LAST);
		
		SqlCohortDefinition patientWithNYHA=new SqlCohortDefinition();
		patientWithNYHA.setName("patientWithNYHA");
		patientWithNYHA.setQuery("select distinct person_id from obs where concept_id="+NYHACLASS.getConceptId()+" and voided=0 and value_coded is not null");
		
		
		CompositionCohortDefinition patientEnrolledInHFWithNYHA = new CompositionCohortDefinition();
		patientEnrolledInHFWithNYHA.setName("patientEnrolledInHFWithNYHA");
		patientEnrolledInHFWithNYHA.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInHFWithNYHA.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
			
		patientEnrolledInHFWithNYHA.getSearches().put("1",new Mapped<CohortDefinition>(patientWithNYHA, null));
		
		patientEnrolledInHFWithNYHA.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledInHFWithNYHA.setCompositionString("1 AND 2");	
		
		
				
		CompositionCohortDefinition patientEnrolledInHFWithNYHAClassIV = new CompositionCohortDefinition();
		patientEnrolledInHFWithNYHAClassIV.setName("patientEnrolledInHFWithNYHAClassIV");
		patientEnrolledInHFWithNYHAClassIV.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInHFWithNYHAClassIV.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
			
		patientEnrolledInHFWithNYHAClassIV.getSearches().put("1",new Mapped<CohortDefinition>(patientWithNYHAClassIV, null));
		
		patientEnrolledInHFWithNYHAClassIV.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledInHFWithNYHAClassIV.setCompositionString("1 AND 2");	
		
		
		CohortIndicator patientEnrolledInHFWithNYHAIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFWithNYHAIndicator", patientEnrolledInHFWithNYHA,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
		
		CohortIndicator patientEnrolledInHFWithNYHAClassIVIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFWithNYHAClassIVIndicator", patientEnrolledInHFWithNYHAClassIV,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "B4D",
		    "Total # of patients enrolled In HF With NYHA ",
		    new Mapped(patientEnrolledInHFWithNYHAIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		dsd.addColumn(
		    "B4N",
		    "Total # of patients enrolled In HF With NYHA Class IV ",
		    new Mapped(patientEnrolledInHFWithNYHAClassIVIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
*/
		//======================================
		// B5: Of the patients newly enrolled in quarter with documented creatinine check in the last quarter, # and % with first creatinine >200
		//======================================
		
		
		SqlCohortDefinition patientsWithFirstCreatinineAfterDateEnrolled=new SqlCohortDefinition();
		patientsWithFirstCreatinineAfterDateEnrolled.setName("patientsWithFirstCreatinineAfterDateEnrolled");
		patientsWithFirstCreatinineAfterDateEnrolled.setQuery("select pp.patient_id from patient_program pp, (select * from (select person_id,obs_datetime,value_numeric from obs o where o.concept_id="+serumCreatinine.getConceptId()+" and o.voided=0 order by o.obs_datetime) as firstcrea group by firstcrea.person_id) as fcr where pp.program_id="+heartFailureProgram.getProgramId()+" and pp.patient_id=fcr.person_id and date_enrolled <= fcr.obs_datetime and fcr.obs_datetime <= :endDate and pp.voided=0");
		patientsWithFirstCreatinineAfterDateEnrolled.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		
		
		SqlCohortDefinition patientsWithFirstCreatinineGreaterThan200AfterDateEnrolled=new SqlCohortDefinition();
		patientsWithFirstCreatinineGreaterThan200AfterDateEnrolled.setName("patientsWithFirstCreatinineGreaterThan200AfterDateEnrolled");
		patientsWithFirstCreatinineGreaterThan200AfterDateEnrolled.setQuery("select pp.patient_id from patient_program pp, (select * from (select person_id,obs_datetime,value_numeric from obs o where o.concept_id="+serumCreatinine.getConceptId()+" and o.voided=0 order by o.obs_datetime) as firstcrea group by firstcrea.person_id) as fcr where pp.program_id="+heartFailureProgram.getProgramId()+" and pp.patient_id=fcr.person_id and date_enrolled <= fcr.obs_datetime and fcr.obs_datetime <= :endDate and fcr.value_numeric > 200 and pp.voided=0");
		patientsWithFirstCreatinineGreaterThan200AfterDateEnrolled.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		
		CompositionCohortDefinition patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled = new CompositionCohortDefinition();
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.setName("patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled");
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithFirstCreatinineAfterDateEnrolled,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.getSearches().put("3",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled.setCompositionString("2 AND 3");
		
		
		CompositionCohortDefinition patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled = new CompositionCohortDefinition();
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.setName("patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled");
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.getSearches().put("1",new Mapped<CohortDefinition>(patientsWithFirstCreatinineGreaterThan200AfterDateEnrolled,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithFirstCreatinineAfterDateEnrolled,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.getSearches().put("3",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled.setCompositionString("1 AND 2 AND 3");
		
		
		CohortIndicator patientEnrolledInHFWithFirstCreatinineAfterDateEnrolledIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFWithFirstCreatinineAfterDateEnrolledIndicator", patientEnrolledInHFWithFirstCreatinineAfterDateEnrolled,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
		
		CohortIndicator patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolledIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolledIndicator", patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolled,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "B5N",
		    "Total # of patients enrolled In HF With Creatinine > 200 ",
		    new Mapped(patientEnrolledInHFWithFirstCreatinineAfterGreaterThan200AfterDateEnrolledIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B5D",
		    "Total # of patients enrolled In HF With Creatinine",
		    new Mapped(patientEnrolledInHFWithFirstCreatinineAfterDateEnrolledIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		//=====================================
		// B6: Of the new patients enrolled in the month, # and % who are post-cardiac surgery at time of intake
		//=====================================
		
		
		SqlCohortDefinition patientWithPostOpStartDateEqualsToProgramEnrollementDate=Cohorts.createPatientsWithStartDateOfStateEqualsToProgramEnrolmentDate(postOperative);
		
		
		CompositionCohortDefinition patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate = new CompositionCohortDefinition();
		patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate.setName("patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate");
		patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate.getSearches().put("1",new Mapped<CohortDefinition>(patientWithPostOpStartDateEqualsToProgramEnrollementDate,null));
		patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate.setCompositionString("1 AND 2");
		
		
		CohortIndicator patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDateIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDateIndicator", patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDate,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${endDate-1m},enrolledOnOrBefore=${endDate}"));
		
		CohortIndicator patientEnrolledLastMonthInHFProgramIndicator = Indicators.newCountIndicator(
		    "patientEnrolledLastMonthInHFProgramIndicator", patientEnrolledInHFProgram,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${endDate-1m},enrolledOnOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "B6N",
		    "new patients enrolled in the month,who are post-cardiac surgery at time of intake",
		    new Mapped(patientEnrolledWithPostOpStartDateEqualsToProgramEnrollementDateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B6D",
		    "Total # of patients enrolled In HF With Creatinine",
		    new Mapped(patientEnrolledLastMonthInHFProgramIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		//========================================================
		// C1:  Of active patients, # and % who had Cr checked at a visit within the past 6 months
		//========================================================
		
		
		
		SqlCohortDefinition patientsWithCreatinineInHFEncounter = Cohorts.getPatientsWithObsGreaterThanNtimesByStartDateEndDate("patientsWithCreatinineInHFEncounter", serumCreatinine, 0);
		
		CompositionCohortDefinition patientSeenWithCreatinineInHFEncounter = new CompositionCohortDefinition();
		patientSeenWithCreatinineInHFEncounter.setName("patientSeenWithCreatinineInHFEncounter");
		patientSeenWithCreatinineInHFEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenWithCreatinineInHFEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenWithCreatinineInHFEncounter.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientSeenWithCreatinineInHFEncounter.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientSeenWithCreatinineInHFEncounter.getSearches().put("1",new Mapped<CohortDefinition>(patientsWithCreatinineInHFEncounter, ParameterizableUtil
	            .createParameterMappings("startDate=${endDate-12m},endDate=${endDate}")));
		patientSeenWithCreatinineInHFEncounter.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil
	            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientSeenWithCreatinineInHFEncounter.setCompositionString("1 AND 2");



		CohortIndicator patientSeenWithCreatinineInHFEncounterIndicator = Indicators.newCountIndicator(
		    "patientSeenWithCreatinineInHFEncounterIndicator", patientSeenWithCreatinineInHFEncounter,ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));
		

		dsd.addColumn(
		    "C1N",
		    "Total # of patients enrolled In HF With checked Creatinine in visit",
		    new Mapped(patientSeenWithCreatinineInHFEncounterIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
//==========================================
// Total number patients eligible for cardiac surgery/catheterization
//========================================

		/*InStateCohortDefinition patientWithPostOpState=Cohorts.createInProgramStateParameterizableByDate("patientWithPostOpStartDate", postOperative);
		EncounterCohortDefinition patientWithPostOpVisit=Cohorts.createEncounterParameterizedByDate("patientWithPostOpVisit","onOrBefore",postOpVisit);
*/
		CompositionCohortDefinition eligibleForCardiacSurgeryOrCatheterization = new CompositionCohortDefinition();
		eligibleForCardiacSurgeryOrCatheterization.setName("eligibleForCardiacSurgeryOrCatheterization");
		eligibleForCardiacSurgeryOrCatheterization.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		eligibleForCardiacSurgeryOrCatheterization.addParameter(new Parameter("onDate", "onDate", Date.class));
		eligibleForCardiacSurgeryOrCatheterization.getSearches().put("1",new Mapped<CohortDefinition>(patientWithcongenitalDiagnosis, null));
		eligibleForCardiacSurgeryOrCatheterization.getSearches().put("2",new Mapped<CohortDefinition>(patientWithRheumaticDiagnosis, null));
		/*eligibleForCardiacSurgeryOrCatheterization.getSearches().put("3", new Mapped<CohortDefinition>(patientWithPostOpState, ParameterizableUtil.createParameterMappings("onDate=${onDate}")));
		eligibleForCardiacSurgeryOrCatheterization.getSearches().put("4", new Mapped<CohortDefinition>(patientWithPostOpVisit, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		eligibleForCardiacSurgeryOrCatheterization.setCompositionString("1 AND 2 AND (NOT (3 OR 4))");
*/
		eligibleForCardiacSurgeryOrCatheterization.setCompositionString("1 AND 2");


		CohortIndicator eligibleForCardiacSurgeryOrCatheterizationIndicator = Indicators.newCountIndicator(
				"eligibleForCardiacSurgeryOrCatheterizationIndicator", eligibleForCardiacSurgeryOrCatheterization,ParameterizableUtil
						.createParameterMappings("onOrBefore=${endDate},onDate=${endDate}"));


		dsd.addColumn(
				"C2",
				"Total number patients eligible for cardiac surgery/catheterization",
				new Mapped(eligibleForCardiacSurgeryOrCatheterizationIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		//===============================================================
		// C3:  Of total active patients eligible cardiac surgery/catheterization, % of patients who received cardiac surgery in the last quarter
		//===============================================================


		CompositionCohortDefinition patientActiveWithcongenitalAndRheumaticDiagnosis = new CompositionCohortDefinition();
		patientActiveWithcongenitalAndRheumaticDiagnosis.setName("patientActiveWithcongenitalAndRheumaticDiagnosis");
		patientActiveWithcongenitalAndRheumaticDiagnosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithcongenitalDiagnosis, null));
		patientActiveWithcongenitalAndRheumaticDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientWithRheumaticDiagnosis, null));
		patientActiveWithcongenitalAndRheumaticDiagnosis.getSearches().put("3",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));

		patientActiveWithcongenitalAndRheumaticDiagnosis.setCompositionString("1 AND 2 AND 3");



		CohortIndicator patientActiveWithcongenitalAndRheumaticDiagnosisIndicator = Indicators.newCountIndicator(
				"patientActiveWithcongenitalAndRheumaticDiagnosisIndicator", patientActiveWithcongenitalAndRheumaticDiagnosis,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));

		dsd.addColumn(
				"C3D",
				"Of total active patients eligible cardiac surgery/catheterization",
				new Mapped(patientActiveWithcongenitalAndRheumaticDiagnosisIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");






		InStateCohortDefinition patientWithPostOpState=Cohorts.createInProgramStateParameterizableByDate("patientWithPostOpStartDate", postOperative,"onOrBefore");
		EncounterCohortDefinition patientWithPostOpVisit=Cohorts.createEncounterParameterizedByDate("patientWithPostOpVisit","onOrBefore",postOpVisit);


		CompositionCohortDefinition patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery = new CompositionCohortDefinition();
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.setName("patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery");
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.getSearches().put("1",new Mapped<CohortDefinition>(patientActiveWithcongenitalAndRheumaticDiagnosis, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.getSearches().put("2", new Mapped<CohortDefinition>(patientWithPostOpState, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.getSearches().put("3", new Mapped<CohortDefinition>(patientWithPostOpState, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore-3m}")));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.getSearches().put("4", new Mapped<CohortDefinition>(patientWithPostOpVisit, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.getSearches().put("5", new Mapped<CohortDefinition>(patientWithPostOpVisit, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore-3m}")));
		patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery.setCompositionString("1 AND (2 OR 4) AND (NOT (3 OR 5))");



		CohortIndicator eligibleForCardiacSurgeryOrCatheterizationReceivedCardiacSurgeryIndicator = Indicators.newCountIndicator(
				"eligibleForCardiacSurgeryOrCatheterizationReceivedCardiacSurgeryIndicator", patientActiveWithcongenitalAndRheumaticDiagnosisReceivedCardiacSurgery,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C3N",
				"Of total active patients eligible cardiac surgery/catheterization, % of patients who received cardiac surgery in the last quarter",
				new Mapped(eligibleForCardiacSurgeryOrCatheterizationReceivedCardiacSurgeryIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		//=========================================================
		// C4: Of total ever-enrolled HF patients in the last Q,  % ever received post cardiac surgery
		//=========================================================


		CompositionCohortDefinition patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery = new CompositionCohortDefinition();
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.setName("patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery");
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.getSearches().put("1",new Mapped<CohortDefinition>(patientEverEnrolledInHFProgram, ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.getSearches().put("2", new Mapped<CohortDefinition>(patientWithPostOpState, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.getSearches().put("3", new Mapped<CohortDefinition>(patientWithPostOpVisit, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery.setCompositionString("1 AND (2 OR 3)");



		CohortIndicator patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgeryIndicator = Indicators.newCountIndicator(
				"patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgeryIndicator", patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgery,ParameterizableUtil
						.createParameterMappings("onOrBefore=${endDate},enrolledOnOrBefore=${endDate}"));


		dsd.addColumn(
				"C4N",
				"Of total ever-enrolled HF patients in the last Q,  % ever received post cardiac surgery",
				new Mapped(patientEverEnrolledWithcongenitalAndRheumaticDiagnosisEverReceivedCardiacSurgeryIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		//=========================================================
		// C5: Of total ever-enrolled HF patients in the last Q,  % ever received post cardiac surgery
		//=========================================================


		CompositionCohortDefinition patientActiveEverReceivedCardiacSurgery = new CompositionCohortDefinition();
		patientActiveEverReceivedCardiacSurgery.setName("patientActiveEverReceivedCardiacSurgery");
		patientActiveEverReceivedCardiacSurgery.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveEverReceivedCardiacSurgery.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveEverReceivedCardiacSurgery.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveEverReceivedCardiacSurgery.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveEverReceivedCardiacSurgery.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveEverReceivedCardiacSurgery.getSearches().put("2", new Mapped<CohortDefinition>(patientWithPostOpState, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientActiveEverReceivedCardiacSurgery.getSearches().put("3", new Mapped<CohortDefinition>(patientWithPostOpVisit, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientActiveEverReceivedCardiacSurgery.setCompositionString("1 AND (2 OR 3)");



		CohortIndicator patientActiveEverReceivedCardiacSurgeryIndicator = Indicators.newCountIndicator(
				"patientActiveEverReceivedCardiacSurgeryIndicator", patientActiveEverReceivedCardiacSurgery,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C5D",
				"Of total active patients who are post cardiac surgery",
				new Mapped(patientActiveEverReceivedCardiacSurgeryIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		SqlCohortDefinition patientOnWarfarinOnEndDate=Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("patientOnWarfarin", warfarin);


		CompositionCohortDefinition patientActiveEverReceivedCardiacSurgeryOnwarfarin = new CompositionCohortDefinition();
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.setName("patientActiveEverReceivedCardiacSurgery");
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.getSearches().put("2", new Mapped<CohortDefinition>(patientWithPostOpState, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.getSearches().put("3", new Mapped<CohortDefinition>(patientWithPostOpVisit, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.getSearches().put("4",new Mapped<CohortDefinition>(patientOnWarfarinOnEndDate,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarin.setCompositionString("1 AND (2 OR 3) AND 4");



		CohortIndicator patientActiveEverReceivedCardiacSurgeryOnwarfarinIndicator = Indicators.newCountIndicator(
				"patientActiveEverReceivedCardiacSurgeryOnwarfarinIndicator", patientActiveEverReceivedCardiacSurgeryOnwarfarin,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C5N",
				"Of total active patients who are post cardiac surgery,  % on warfarin",
				new Mapped(patientActiveEverReceivedCardiacSurgeryOnwarfarinIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		//=====================================================================
		// C6: Of total active post cardiac surgery and on warfarin, % with subtherapeutic INR last visit < 1.5
		//=====================================================================

		SqlCohortDefinition patientWithInternationalNormalizedRatio = Cohorts.getPatientsWithObsEver("patientWithInternationalNormalizedRatio", internationalNormalizedRatio);
		NumericObsCohortDefinition patientWithInternationalNormalizedRatioLessThanOneAndHalf = Cohorts.createNumericObsCohortDefinition("patientWithInternationalNormalizedRatioLessThanOneAndHalf", internationalNormalizedRatio, 1.5, RangeComparator.LESS_THAN, TimeModifier.LAST);


		CompositionCohortDefinition patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested = new CompositionCohortDefinition();
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.setName("patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested");
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.getSearches().put("1",new Mapped<CohortDefinition>(patientActiveEverReceivedCardiacSurgeryOnwarfarin, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.getSearches().put("2", new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatio, null));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested.setCompositionString("1 AND 2");



		CohortIndicator patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTestedIndicator = Indicators.newCountIndicator(
				"patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTestedIndicator", patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTested,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C6D",
				"Of total active patients who are post cardiac surgery on warfarin",
				new Mapped(patientActiveEverReceivedCardiacSurgeryOnwarfarinINRTestedIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested = new CompositionCohortDefinition();
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.setName("patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested");
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.getSearches().put("1",new Mapped<CohortDefinition>(patientActiveEverReceivedCardiacSurgeryOnwarfarin, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.getSearches().put("2", new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatio, null));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.getSearches().put("3", new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatioLessThanOneAndHalf, null));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested.setCompositionString("1 AND 2 AND 3");



		CohortIndicator patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTestedIndicator = Indicators.newCountIndicator(
				"patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTestedIndicator", patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTested,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C6N",
				"Of total active post cardiac surgery and on warfarin, % with subtherapeutic INR last visit < 1.5",
				new Mapped(patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINROneAndHalfTestedIndicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
	//===================================================
	//	C7: Of total active patients who are post cardiac surgery and on warfarin % with supra therapeutic INR >5 in the last quarter,
	//===================================================
		NumericObsCohortDefinition patientWithInternationalNormalizedRatioGreaterThan5 = Cohorts.createNumericObsCohortDefinition("patientWithInternationalNormalizedRatioGreaterThan5",onOrAfterOnOrBefore, internationalNormalizedRatio, 5, RangeComparator.GREATER_THAN, TimeModifier.LAST);



		CompositionCohortDefinition patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5 = new CompositionCohortDefinition();
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.setName("patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5");
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.getSearches().put("1",new Mapped<CohortDefinition>(patientActiveEverReceivedCardiacSurgeryOnwarfarin, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.getSearches().put("2", new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatio, null));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.getSearches().put("3", new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatioGreaterThan5, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5.setCompositionString("1 AND 2 AND 3");


		CohortIndicator patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5Indicator = Indicators.newCountIndicator(
				"patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5Indicator", patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5,ParameterizableUtil
						.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


		dsd.addColumn(
				"C7N",
				"Of total active patients who are post cardiac surgery and on warfarin % with supra therapeutic INR >5 in the last quarter,",
				new Mapped(patientActiveEverReceivedCardiacSurgeryOnwarfarinWithLastINRGreaterThan5Indicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		//====================================================
		// C8: of active patients with creatinine check in past 12 months, % with latest Cr > 200
		//=====================================================


		NumericObsCohortDefinition patientsWithCreatinineGreaterThan200 = Cohorts.createNumericObsCohortDefinition(
				"patientsWithSRGreaterThan200", onOrAfterOnOrBefore, serumCreatinine, 200, RangeComparator.GREATER_THAN,
				TimeModifier.LAST);

		NumericObsCohortDefinition patientWithCreatinineChecked = Cohorts.createNumericObsCohortDefinition("patientWithCreatinineChecked", onOrAfterOnOrBefore,serumCreatinine, 0, null, TimeModifier.ANY);

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
				"C8D",
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
				"C8N",
				"of active patients with creatinine check in past 12 months, % with latest Cr > 200",
				new Mapped(patientActiveWithCreatinineCheckedIn12MonthsAndGreaterThan200Indicator, ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		//=====================================================
		// D1 Of total active patients, disaggregated by NYHA class
		//====================================================





		for (ConceptAnswer ca:concAns){
			String conName=ca.getAnswerConcept().getName().toString().replaceAll("\\s", "");

			SqlCohortDefinition patientWithCurrentNYHAClass=Cohorts.getPatientsWithLastCodedObsEver("patientWithOtherDiagnosis", NYHACLASS, ca.getAnswerConcept());

			CompositionCohortDefinition patientActiveWithCurrentNYHAClass=new CompositionCohortDefinition();
			patientActiveWithCurrentNYHAClass.setName("patientActiveWithCurrentNYHAClass");
			patientActiveWithCurrentNYHAClass.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
			patientActiveWithCurrentNYHAClass.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			patientActiveWithCurrentNYHAClass.addParameter(new Parameter("startDate", "startDate", Date.class));
			patientActiveWithCurrentNYHAClass.addParameter(new Parameter("endDate", "endDate", Date.class));
			patientActiveWithCurrentNYHAClass.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCurrentNYHAClass,null));
			patientActiveWithCurrentNYHAClass.getSearches().put("2",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
			patientActiveWithCurrentNYHAClass.setCompositionString("1 AND 2");



			CohortIndicator patientActiveWithCurrentNYHAClassIndicator = Indicators.newCountIndicator(
					"patientActiveWithCurrentNYHAClassIndicator", patientActiveWithCurrentNYHAClass,
					ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrBefore=${endDate},onOrAfter=${startDate}"));

			dsd.addColumn(
					"Act_"+conName,
					"Of total active patients, disaggregated by NYHA class: "+conName,
					new Mapped(patientActiveWithCurrentNYHAClassIndicator, ParameterizableUtil
							.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		}


		//============================================
		// Of total active patients, % with no visit 6 months & <12 months past last visit date
		//===========================================


		CompositionCohortDefinition patientActiveWithNoVisitInLastTwoQuarters=new CompositionCohortDefinition();
		patientActiveWithNoVisitInLastTwoQuarters.setName("patientActiveWithNoVisitInLastTwoQuarters");
		patientActiveWithNoVisitInLastTwoQuarters.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWithNoVisitInLastTwoQuarters.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientActiveWithNoVisitInLastTwoQuarters.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientActiveWithNoVisitInLastTwoQuarters.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientActiveWithNoVisitInLastTwoQuarters.getSearches().put("1",new Mapped<CohortDefinition>(activePatientWithNoExitBeforeQuarterStart, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}")));
		patientActiveWithNoVisitInLastTwoQuarters.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrBefore-6m}")));
		patientActiveWithNoVisitInLastTwoQuarters.setCompositionString("1 AND (NOT 2)");


		CohortIndicator patientActiveWithNoVisitInLastTwoQuartersIndicator = Indicators.newCountIndicator(
				"patientActiveWithNoVisitInLastTwoQuartersIndicator", patientActiveWithNoVisitInLastTwoQuarters,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrBefore=${endDate},onOrAfter=${startDate}"));

		dsd.addColumn(
				"D2N",
				"Of total active patients, % with no visit 6 months & <12 months past last visit date",
				new Mapped(patientActiveWithNoVisitInLastTwoQuartersIndicator, ParameterizableUtil
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






		//========================================================
		// C2:  Of active patients, # and % with BP recorded at last visit
		//========================================================
		
		
		SqlCohortDefinition patientsWithBPInHFLastEncounter=Cohorts.getPatientsWithObservationAtLastVisit("patientsWithBPInHFLastEncounter", systolicBP, heartFailureEncounterType);
		
		
		
		CompositionCohortDefinition patientSeenWithBPInHFLastEncounter = new CompositionCohortDefinition();
		patientSeenWithBPInHFLastEncounter.setName("patientSeenWithBPInHFLastEncounter");
		patientSeenWithBPInHFLastEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenWithBPInHFLastEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientSeenWithBPInHFLastEncounter.getSearches().put("1",new Mapped<CohortDefinition>(patientsWithBPInHFLastEncounter,null));
		
		patientSeenWithBPInHFLastEncounter.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenWithBPInHFLastEncounter.setCompositionString("1 AND 2");	
		
		
		CohortIndicator patientSeenWithBPInHFLastEncounterIndicator = Indicators.newCountIndicator("patientSeenWithBPInHFLastEncounterIndicator", patientSeenWithBPInHFLastEncounter,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m},onOrBefore=${endDate}"));
		
		dsd.addColumn("C2N", "Total # of patients seen in the 12 months with BP at last visit", new Mapped(patientSeenWithBPInHFLastEncounterIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
			
		//========================================================
		// C3:  Of total active patients who are post cardiac surgery and on warfarin with INR check in the last quarter, # and % with INR < 2 at last check
		//========================================================
		
		
		/*
		SqlCohortDefinition patientsInPostOperativeState = Cohorts.createPatientsInStateNotPredatingProgramEnrolment(postOperative);
		



		SqlCohortDefinition patientOnWarfarin=Cohorts.getPatientsOnCurrentRegimenBasedOnStartDateEndDate("patientOnWarfarin", warfarin);


		CompositionCohortDefinition patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio = new CompositionCohortDefinition();
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.setName("patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio");
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.getSearches().put("1",new Mapped<CohortDefinition>(patientsInPostOperativeState,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.getSearches().put("2",new Mapped<CohortDefinition>(patientOnWarfarin,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.getSearches().put("3",new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatio,ParameterizableUtil.createParameterMappings("start=${startDate},end=${endDate}")));
		
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.getSearches().put("4",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrBefore-12m}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio.setCompositionString("1 AND 2 AND 3 AND 4");	
		
		
		
		CompositionCohortDefinition patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2 = new CompositionCohortDefinition();
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.setName("patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2");
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.getSearches().put("1",new Mapped<CohortDefinition>(patientsInPostOperativeState,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.getSearches().put("2",new Mapped<CohortDefinition>(patientOnWarfarin,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.getSearches().put("3",new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatioLessThan2,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.getSearches().put("4",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrBefore-12m}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2.setCompositionString("1 AND 2 AND 3 AND 4");	
		
		
		
		CohortIndicator patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2Indicator = Indicators.newCountIndicator("patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2Indicator", patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		CohortIndicator patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioIndicator = Indicators.newCountIndicator("patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioIndicator", patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatio,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		
		dsd.addColumn("C3D_OLD", "Total active patients who are post cardiac surgery and on warfarin with INR check in the last quarter", new Mapped(patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn("C3N_OLD", "Total active patients who are post cardiac surgery and on warfarin with INR less than 2 check in the last quarter", new Mapped(patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2Indicator,
	        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
	*/
		
		//========================================================
		// C4:  Of total active patients who are post cardiac surgery and on warfarin with INR check in the last quarter, # and % with INR > 4 at last check
		//========================================================
		
		/*
		NumericObsCohortDefinition patientWithInternationalNormalizedRatioGreaterThan4 = Cohorts.createNumericObsCohortDefinition("patientWithInternationalNormalizedRatioGreaterThan4", onOrAfterOnOrBefore, internationalNormalizedRatio, 4.0, RangeComparator.GREATER_THAN, TimeModifier.LAST);
		
		
		CompositionCohortDefinition patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4 = new CompositionCohortDefinition();
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.setName("patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4");
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.getSearches().put("1",new Mapped<CohortDefinition>(patientsInPostOperativeState,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.getSearches().put("2",new Mapped<CohortDefinition>(patientOnWarfarin,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.getSearches().put("3",new Mapped<CohortDefinition>(patientWithInternationalNormalizedRatioGreaterThan4,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.getSearches().put("4",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrBefore-12m}")));
		patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4.setCompositionString("1 AND 2 AND 3 AND 4");	
		
		
		
		CohortIndicator patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4Indicator = Indicators.newCountIndicator("patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4Indicator", patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn("C4N_Old", "Total active patients who are post cardiac surgery and on warfarin with INR Greater than 4 check in the last quarter", new Mapped(patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4Indicator,
	        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
	
		*/
		//========================================================
		// D1:  Of total patients seen in the last quarter, % on carvedilol or atenolol as of last visit
		//========================================================
		
		
		SqlCohortDefinition patientsOnCarvedilolOrAtenololAtLastVisit=Cohorts.getPatientsOnRegimenAtLastVisit("patientsOnCarvedilolOrAtenololAtLastVisit", carvedilolAndAtenolol, heartFailureEncounterType);
		
		
		CompositionCohortDefinition patientSeenOnCarvedilolOrAtenololAtLastVisit = new CompositionCohortDefinition();
		patientSeenOnCarvedilolOrAtenololAtLastVisit.setName("patientSeenOnCarvedilolOrAtenololAtLastVisit");
		patientSeenOnCarvedilolOrAtenololAtLastVisit.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenOnCarvedilolOrAtenololAtLastVisit.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientSeenOnCarvedilolOrAtenololAtLastVisit.getSearches().put("1",new Mapped<CohortDefinition>(patientsOnCarvedilolOrAtenololAtLastVisit, null));
		
		patientSeenOnCarvedilolOrAtenololAtLastVisit.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenOnCarvedilolOrAtenololAtLastVisit.setCompositionString("1 AND 2");	
		
		CohortIndicator patientSeenOnCarvedilolOrAtenololAtLastVisitIndicator = Indicators.newCountIndicator("patientSeenOnCarvedilolOrAtenololAtLastVisitIndicator", patientSeenOnCarvedilolOrAtenololAtLastVisit, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		

		dsd.addColumn("D1N", "Total # of patients seen and on carvedilol or atenolol as of last visit", new Mapped(patientSeenOnCarvedilolOrAtenololAtLastVisitIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//========================================================
		// D2:  Of total patients seen in the last quarter, % on lisinopril or captopril as of last visit
		//========================================================
		
		SqlCohortDefinition patientsOnLisinoprilOrCaptoprilAtLastVisit=Cohorts.getPatientsOnRegimenAtLastVisit("patientsOnLisinoprilOrCaptoprilAtLastVisit", lisinoprilAndCaptopril, heartFailureEncounterType);
		
		CompositionCohortDefinition patientSeenOnLisinoprilOrCaptoprilAtLastVisit = new CompositionCohortDefinition();
		patientSeenOnLisinoprilOrCaptoprilAtLastVisit.setName("patientSeenOnLisinoprilOrCaptoprilAtLastVisit");
		patientSeenOnLisinoprilOrCaptoprilAtLastVisit.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenOnLisinoprilOrCaptoprilAtLastVisit.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientSeenOnLisinoprilOrCaptoprilAtLastVisit.getSearches().put("1",new Mapped<CohortDefinition>(patientsOnLisinoprilOrCaptoprilAtLastVisit, null));
		
		patientSeenOnLisinoprilOrCaptoprilAtLastVisit.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenOnLisinoprilOrCaptoprilAtLastVisit.setCompositionString("1 AND 2");
		
		
		CohortIndicator patientSeenOnLisinoprilOrCaptoprilAtLastVisitIndicator = Indicators.newCountIndicator("patientSeenOnLisinoprilOrCaptoprilAtLastVisitIndicator", patientSeenOnLisinoprilOrCaptoprilAtLastVisit,ParameterizableUtil
	            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		

		dsd.addColumn("D2N_old", "Total # of patients seen and on lisinopril or captopril as of last visit", new Mapped(patientSeenOnLisinoprilOrCaptoprilAtLastVisitIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//========================================================
		// D3:  Of total patients seen in the last quarter, % on furosemide as of last visit
		//========================================================
		
		SqlCohortDefinition patientsOnFurosemideAtLastVisit=Cohorts.getPatientsOnRegimenAtLastVisit("patientsOnFurosemideAtLastVisit", furosemide, heartFailureEncounterType);
		
		CompositionCohortDefinition patientSeenOnFurosemideAtLastVisit = new CompositionCohortDefinition();
		patientSeenOnFurosemideAtLastVisit.setName("patientSeenOnFurosemideAtLastVisit");
		patientSeenOnFurosemideAtLastVisit.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenOnFurosemideAtLastVisit.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientSeenOnFurosemideAtLastVisit.getSearches().put("1",new Mapped<CohortDefinition>(patientsOnFurosemideAtLastVisit, null));
		
		patientSeenOnFurosemideAtLastVisit.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenOnFurosemideAtLastVisit.setCompositionString("1 AND 2");
		
		
		CohortIndicator patientSeenOnFurosemideAtLastVisitIndicator = Indicators.newCountIndicator("patientSeenOnFurosemideAtLastVisitIndicator", patientSeenOnFurosemideAtLastVisit,ParameterizableUtil
	            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		

		dsd.addColumn("D3N", "Total # of patients seen and on furosemide as of last visit", new Mapped(patientSeenOnFurosemideAtLastVisitIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//========================================================
		// D4:  Of total patients seen in the last quarter, % on aldactone as of last visit
		//========================================================
		
		SqlCohortDefinition patientsOnAldactoneAtLastVisit=Cohorts.getPatientsOnRegimenAtLastVisit("patientsOnAldactoneAtLastVisit", aldactone, heartFailureEncounterType);
		
		CompositionCohortDefinition patientSeenOnAldactoneAtLastVisit = new CompositionCohortDefinition();
		patientSeenOnAldactoneAtLastVisit.setName("patientSeenOnAldactoneAtLastVisit");
		patientSeenOnAldactoneAtLastVisit.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenOnAldactoneAtLastVisit.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientSeenOnAldactoneAtLastVisit.getSearches().put("1",new Mapped<CohortDefinition>(patientsOnAldactoneAtLastVisit, null));
		
		patientSeenOnAldactoneAtLastVisit.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenOnAldactoneAtLastVisit.setCompositionString("1 AND 2");
		
		
		CohortIndicator patientsOnAldactoneAtLastVisitIndicator = Indicators.newCountIndicator("patientsOnAldactoneAtLastVisitIndicator", patientSeenOnAldactoneAtLastVisit,ParameterizableUtil
	            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		

		dsd.addColumn("D4N", "Total # of patients seen and on aldactone as of last visit", new Mapped(patientsOnAldactoneAtLastVisitIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		//=======================================================
		// D5: Of total active patients, # of patients who received cardiac surgery in the last quarter
		//=======================================================
		
		
		/*
		CompositionCohortDefinition patientsSeenAndInPostOperativeState = new CompositionCohortDefinition();
		patientsSeenAndInPostOperativeState.setName("patientsSeenAndInPostOperativeState");
		patientsSeenAndInPostOperativeState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndInPostOperativeState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndInPostOperativeState.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsInPostOperativeState, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsSeenAndInPostOperativeState.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore}")));
		patientsSeenAndInPostOperativeState.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndInPostOperativeStateIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndInPostOperativeStateIndicator", patientsSeenAndInPostOperativeState,
		    ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "D5N",
		    "Total active patients, # of patients who received cardiac surgery in the last quarter",
		    new Mapped(patientsSeenAndInPostOperativeStateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		*///=======================================================
		// E1: Number of active patients who have an accompanateur documented
		//=======================================================
		
		
		SqlCohortDefinition patientsWihtAccompagnateur = Cohorts.createPatientsWithAccompagnateur(
		    "allPatientsWihtAccompagnateur", "endDate");
		
		CompositionCohortDefinition patientActiveWihtAccompagnateur = new CompositionCohortDefinition();
		patientActiveWihtAccompagnateur.setName("patientActiveWihtAccompagnateur");
		patientActiveWihtAccompagnateur.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWihtAccompagnateur.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientActiveWihtAccompagnateur.getSearches().put("1",new Mapped<CohortDefinition>(patientsWihtAccompagnateur, ParameterizableUtil
	            .createParameterMappings("endDate=${endDate}")));
		
		patientActiveWihtAccompagnateur.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientActiveWihtAccompagnateur.setCompositionString("1 AND 2");
		
		
		CohortIndicator patientActiveWihtAccompagnateurIndicator = Indicators.newCountIndicator(
		    "patientActiveWihtAccompagnateurIndicator", patientActiveWihtAccompagnateur,
		    ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-12m},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "E1",
		    "Total active patients, who have an accompanateur documented",
		    new Mapped(patientActiveWihtAccompagnateurIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//=======================================================
		// E2: Of the active patients, # and % with a visit in the last 3 months
		//=======================================================
		
		
		CohortIndicator patientActiveInLast3MothsIndicator = Indicators.newCountIndicator(
		    "patientActiveInLast3MothsIndicator", patientSeen,
		    ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-3m},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "E2N",
		    "Total active patients,with a visit in the last 3 months",
		    new Mapped(patientActiveInLast3MothsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//=======================================================
		// E3: Of total active patients, # and % with no visit in last 6 months
		//=======================================================
		
		
		
		CompositionCohortDefinition patientActiveWihtNoVisitsInLast6Months = new CompositionCohortDefinition();
		patientActiveWihtNoVisitsInLast6Months.setName("patientActiveWihtNoVisitsInLast6Months");
		patientActiveWihtNoVisitsInLast6Months.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientActiveWihtNoVisitsInLast6Months.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientActiveWihtNoVisitsInLast6Months.getSearches().put("1",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientActiveWihtNoVisitsInLast6Months.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore}")));
		patientActiveWihtNoVisitsInLast6Months.setCompositionString("(NOT 1) AND 2");
		
		CohortIndicator patientActiveWihtNoVisitsInLast6MonthsIndicator = Indicators.newCountIndicator(
		    "patientActiveWihtNoVisitsInLast6MonthsIndicator", patientActiveWihtNoVisitsInLast6Months,ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-6m},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "E3N",
		    "Total active patients,with no visit in last 6 months",
		    new Mapped(patientActiveWihtNoVisitsInLast6MonthsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//=======================================================
		// F1: Number of patients who died in review Quarter
		//=======================================================
		InProgramCohortDefinition enrolledInHFProgramAsOfTheStartDate = Cohorts
        .createInProgramParameterizableByDate("enrolledInHFProgram", heartFailureProgram);
		

		
		CompositionCohortDefinition patientsEnrolledAndInPatientDiedState = new CompositionCohortDefinition();
		patientsEnrolledAndInPatientDiedState.setName("patientsEnrolledAndInPatientDiedState");
		patientsEnrolledAndInPatientDiedState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsEnrolledAndInPatientDiedState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsEnrolledAndInPatientDiedState.addParameter(new Parameter("onDate", "onDate", Date.class));
		patientsEnrolledAndInPatientDiedState.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(enrolledInHFProgramAsOfTheStartDate, ParameterizableUtil
		            .createParameterMappings("onDate=${onDate}")));
		patientsEnrolledAndInPatientDiedState.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsInPatientDiedState, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsEnrolledAndInPatientDiedState.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndInPatientDiedStateIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndInPostOperativeStateIndicator", patientsEnrolledAndInPatientDiedState,
		    ParameterizableUtil
            .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},onDate=${endDate-1d}"));
		
		dsd.addColumn(
		    "F1",
		    "Number of patients who died in review quarter",
		    new Mapped(patientsEnrolledAndInPatientDiedStateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		

		//=======================================================
		// F2: Of total active patients, # and  % with at least one hospitalization in the last quarter
		//=======================================================
		EncounterCohortDefinition patientWithPostoperatoireAndinsuffisanceHospitalisations = Cohorts
		        .createEncounterBasedOnForms("patientWithPostoperatoireAndinsuffisanceHospitalisations",
		            onOrAfterOnOrBefore, postoperatoireAndinsuffisanceHospitalisations);
		
		/*EncounterCohortDefinition patientWithPostoperatoireHospitalisations = Cohorts.createEncounterBasedOnForms(
		    "patientWithPostoperatoireAndinsuffisanceHospitalisations", onOrAfterOnOrBefore, postoperatoire);
		*/
		CompositionCohortDefinition patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations = new CompositionCohortDefinition();
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations
		        .setName("patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations");
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.addParameter(new Parameter("onOrAfter", "onOrAfter",
		        Date.class));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.addParameter(new Parameter("onOrBefore", "onOrBefore",
		        Date.class));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientWithPostoperatoireAndinsuffisanceHospitalisations, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrBefore-12m},onOrBefore=${onOrBefore}")));
		/*patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientWithPostoperatoireHospitalisations, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));*/
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenWithPostoperatoireAndinsuffisanceHospitalisationsIndicator = Indicators
		        .newCountIndicator("patientsSeenWithPostoperatoireAndinsuffisanceHospitalisationsIndicator",
		            patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations,
		            ParameterizableUtil
		            .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}"));
		
		dsd.addColumn(
		    "F2N",
		    "Total active patients with at least one hospitalization in the last quarter",
		    new Mapped(patientsSeenWithPostoperatoireAndinsuffisanceHospitalisationsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// F3: Of total active patients, # and % with NYHA class IV at last visit (not including DDB)
		//=======================================================
		SqlCohortDefinition patientsWithPostoperatoireOrInsuffisanceRDV = Cohorts
		        .getPatientsWithObservationInFormBetweenStartAndEndDate("patientsWithPostoperatoireOrInsuffisanceRDV",
		            postoperatoireAndinsuffisanceRDV, NYHACLASS, NYHACLASS4);
		
		CompositionCohortDefinition activePatientsWithPostoperatoireOrInsuffisanceRDV = new CompositionCohortDefinition();
		activePatientsWithPostoperatoireOrInsuffisanceRDV.setName("activePatientsWithSC");
		activePatientsWithPostoperatoireOrInsuffisanceRDV.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientsWithPostoperatoireOrInsuffisanceRDV
		        .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientsWithPostoperatoireOrInsuffisanceRDV.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithPostoperatoireOrInsuffisanceRDV, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		activePatientsWithPostoperatoireOrInsuffisanceRDV.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		activePatientsWithPostoperatoireOrInsuffisanceRDV.setCompositionString("1 AND 2");
		
		CohortIndicator activePatientsWithPostoperatoireOrInsuffisanceRDVIndicator = Indicators.newCountIndicator(
		    "activePatientsWithSRBetweenDatesIndicator", activePatientsWithPostoperatoireOrInsuffisanceRDV,
		    ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-12m},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "F3N",
		    "Total active patients, # and % with NYHA class IV at last visit (not including DDB)",
		    new Mapped(activePatientsWithPostoperatoireOrInsuffisanceRDVIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");








	}
	
	private EncounterIndicatorDataSetDefinition createMonthlyEncounterBaseDataSet() {
		
		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
		
		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		createMonthlyIndicators(eidsd);
		return eidsd;
	}
	
	// create monthly cohort Data set
	
	private CohortIndicatorDataSetDefinition createMonthlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		createMonthlyIndicators(dsd);
		return dsd;
	}
	
	private void createMonthlyIndicators(CohortIndicatorDataSetDefinition dsd) {
		// =======================================================
		// A2: Total # of patients seen in the last month
		// =======================================================
		EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
		    onOrAfterOnOrBefore, heartFailureEncounterType);
		
		CohortIndicator patientsSeenIndicator = Indicators.newCountIndicator("patientsSeenIndicator", patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn("A2M", "Total # of patients seen in the last month", new Mapped(patientsSeenIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// A3: Total # of new patients enrolled in the last month
		//=======================================================
		ProgramEnrollmentCohortDefinition enrolledInHFProgram = Cohorts.createProgramEnrollmentParameterizedByStartEndDate(
		    "enrolledInHFProgram", heartFailureProgram);
		
		CohortIndicator enrolledInHFProgramIndicator = Indicators.newCountIndicator("enrolledInHFProgramIndicator",
		    enrolledInHFProgram,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
		
		dsd.addColumn("A3M", "Total # of new patients enrolled in the last month", new Mapped(enrolledInHFProgramIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// B1: Of the new patients enrolled in the last month, % with echocardiography documented
		//=======================================================
		
		SqlCohortDefinition patientsWithEchocardiographyDocumented = Cohorts
		        .getPatientsWithEchocardiographyDocumented("patientsWithEchocardiographyDocumented");
		
		CompositionCohortDefinition patientsEnrolledAndWithEchocardiographyDocumented = new CompositionCohortDefinition();
		patientsEnrolledAndWithEchocardiographyDocumented.setName("patientsEnrolledAndWithNYHAClassIV");
		patientsEnrolledAndWithEchocardiographyDocumented.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsEnrolledAndWithEchocardiographyDocumented
		        .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		patientsEnrolledAndWithEchocardiographyDocumented.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsEnrolledAndWithEchocardiographyDocumented.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsEnrolledAndWithEchocardiographyDocumented.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithEchocardiographyDocumented, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndWithEchocardiographyDocumented.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(enrolledInHFProgram, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}")));
		patientsEnrolledAndWithEchocardiographyDocumented.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndWithEchocardiographyDocumentedIndicator = Indicators
		        .newCountIndicator(
		            "patientsEnrolledAndWithEchocardiographyDocumentedIndicator",
		            patientsEnrolledAndWithEchocardiographyDocumented,
		            ParameterizableUtil
		                    .createParameterMappings("endDate=${endDate},startDate=${startDate},onOrBefore=${endDate},onOrAfter=${startDate}"));
		dsd.addColumn(
		    "B1M",
		    "New patients enrolled in the last month with echocardiography documented ",
		    new Mapped(patientsEnrolledAndWithEchocardiographyDocumentedIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		//=======================================================
		// B4: Of the new patients enrolled in the last month, % with NYHA class IV 
		//=======================================================
		
		SqlCohortDefinition patientsWithNYHAClassIV = Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate(
		    "patientsWithpatientsWithNYHAClassIV", heartFailureDDBform, NYHACLASS, NYHACLASS4);
		
		CompositionCohortDefinition patientsEnrolledAndWithNYHAClassIV = new CompositionCohortDefinition();
		patientsEnrolledAndWithNYHAClassIV.setName("patientsEnrolledAndWithNYHAClassIV");
		patientsEnrolledAndWithNYHAClassIV.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsEnrolledAndWithNYHAClassIV.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		patientsEnrolledAndWithNYHAClassIV.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsEnrolledAndWithNYHAClassIV.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsEnrolledAndWithNYHAClassIV.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithNYHAClassIV, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		patientsEnrolledAndWithNYHAClassIV.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(enrolledInHFProgram, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}")));
		patientsEnrolledAndWithNYHAClassIV.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndWithNYHAClassIVIndicator = Indicators
		        .newCountIndicator(
		            "patientsEnrolledAndWithNYHAClassIVIndicator",
		            patientsEnrolledAndWithNYHAClassIV,
		            ParameterizableUtil
		                    .createParameterMappings("endDate=${endDate},startDate=${startDate},onOrBefore=${endDate},onOrAfter=${startDate}"));
		dsd.addColumn(
		    "B4M",
		    "New patients enrolled in the last month with NYHA class IV ",
		    new Mapped(patientsEnrolledAndWithNYHAClassIVIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// B5: Of the patients newly enrolled in month with documented creatinine check in the last month, # and % with first creatinine >200
		
		//=======================================================
		NumericObsCohortDefinition patientsWithSRGreaterThan200 = Cohorts.createNumericObsCohortDefinition(
		    "patientsWithSRGreaterThan200", onOrAfterOnOrBefore, serumCreatinine, 200, RangeComparator.GREATER_THAN,
		    TimeModifier.FIRST);
		
		NumericObsCohortDefinition patientsWithSRChecked = Cohorts.createNumericObsCohortDefinition("patientsWithSRChecked",
		    onOrAfterOnOrBefore, serumCreatinine, 0, null, TimeModifier.ANY);
		
		CompositionCohortDefinition enrolledWithWithCRChecked = new CompositionCohortDefinition();
		enrolledWithWithCRChecked.setName("enrolledWithWithCRChecked");
		enrolledWithWithCRChecked.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		enrolledWithWithCRChecked.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		enrolledWithWithCRChecked.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithSRChecked, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		enrolledWithWithCRChecked.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(enrolledInHFProgram, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}")));
		enrolledWithWithCRChecked.setCompositionString("1 AND 2");
		
		CohortIndicator enrolledWithCRCheckedIndicator = Indicators.newCountIndicator("enrolledWithCRCheckedIndicator",
		    enrolledWithWithCRChecked,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "B5DM",
		    "Newly enrolled in month with documented creatinine check in the last month",
		    new Mapped(enrolledWithCRCheckedIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		CompositionCohortDefinition enrolledWithWithCRGreaterThan200 = new CompositionCohortDefinition();
		enrolledWithWithCRGreaterThan200.setName("enrolledWithWithCRGreaterThan200");
		enrolledWithWithCRGreaterThan200.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		enrolledWithWithCRGreaterThan200.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		enrolledWithWithCRGreaterThan200.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithSRGreaterThan200, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		enrolledWithWithCRGreaterThan200.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(enrolledInHFProgram, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${onOrAfter},enrolledOnOrBefore=${onOrBefore}")));
		enrolledWithWithCRGreaterThan200.setCompositionString("1 AND 2");
		
		CohortIndicator enrolledWithCRGreaterThan200Indicator = Indicators.newCountIndicator(
		    "enrolledWithWithCRGreaterThan200Indicator", enrolledWithWithCRGreaterThan200,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "B5NM",
		    "Newly enrolled in month with documented creatinine check in the last month, with first creatinine >200",
		    new Mapped(enrolledWithCRGreaterThan200Indicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// C1: Of active patients, # and % who had Cr checked at a visit within the past 6 months 
		//=======================================================
		
		CohortIndicator activePatientsIndicator = Indicators.newCountIndicator("activePatientsIndicator", patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
		
		dsd.addColumn("C1DM", "Total # of patients seen in the last 12 months", new Mapped(activePatientsIndicator,
		        ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
		
		NumericObsCohortDefinition patientsWithSRBetweenDates = Cohorts.createNumericObsCohortDefinition(
		    "patientsWithSRBetweenDates", onOrAfterOnOrBefore, serumCreatinine, 0, null, TimeModifier.ANY);
		
		CompositionCohortDefinition activePatientsWithSC = new CompositionCohortDefinition();
		activePatientsWithSC.setName("activePatientsWithSC");
		activePatientsWithSC.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientsWithSC.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientsWithSC.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithSRBetweenDates, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		activePatientsWithSC.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrBefore-6m},onOrBefore=${onOrBefore}")));
		activePatientsWithSC.setCompositionString("1 AND 2");
		
		CohortIndicator activePatientsWithSRBetweenDatesIndicator = Indicators.newCountIndicator(
		    "activePatientsWithSRBetweenDatesIndicator", activePatientsWithSC,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-6m+1d},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "C1NM",
		    "Active patients who had Cr checked at a visit within the past 6 months ",
		    new Mapped(activePatientsWithSRBetweenDatesIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// D1: Of total patients seen in the last month, % on carvedilol or atenolol as of last visit
		//=======================================================
		
		SqlCohortDefinition onCarvedilolOratenolol = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate(
		    "onCarvedilolOratenolol", carvedilolAndAtenolol);
		
		CompositionCohortDefinition patientsSeenOnCarvedilolOratenolol = new CompositionCohortDefinition();
		patientsSeenOnCarvedilolOratenolol.setName("patientsSeenOnCarvedilolOratenolol");
		patientsSeenOnCarvedilolOratenolol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenOnCarvedilolOratenolol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenOnCarvedilolOratenolol.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onCarvedilolOratenolol, ParameterizableUtil
		            .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenOnCarvedilolOratenolol.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenOnCarvedilolOratenolol.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenOnCarvedilolOratenololIndicator = Indicators.newCountIndicator(
		    "patientsSeenOnCarvedilolOratenolol", patientsSeenOnCarvedilolOratenolol,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "D1M",
		    "patients seen in the last month and are on carvedilol or atenolol",
		    new Mapped(patientsSeenOnCarvedilolOratenololIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// D2: Of total patients seen in the last month, % on lisinopril or captopril as of last visit
		//=======================================================
		
		SqlCohortDefinition onLisinoprilOrCaptopril = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate(
		    "onLisinoprilOrCaptopril", lisinoprilAndCaptopril);
		
		CompositionCohortDefinition patientsSeenOnLisinoprilOrCaptopril = new CompositionCohortDefinition();
		patientsSeenOnLisinoprilOrCaptopril.setName("patientsSeenOnLisinoprilOrCaptopril");
		patientsSeenOnLisinoprilOrCaptopril.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenOnLisinoprilOrCaptopril.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenOnLisinoprilOrCaptopril.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(onLisinoprilOrCaptopril, ParameterizableUtil
		            .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenOnLisinoprilOrCaptopril.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenOnLisinoprilOrCaptopril.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenOnLisinoprilOrCaptoprilIndicator = Indicators.newCountIndicator(
		    "patientsSeenOnLisinoprilOrCaptoprilIndicator", patientsSeenOnLisinoprilOrCaptopril,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "D2M",
		    "patients seen in the last month and are on lisinopril or captopril",
		    new Mapped(patientsSeenOnLisinoprilOrCaptoprilIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// D3: Of total patients seen in the last month, % on furosemide as of last visit
		//=======================================================
		
		SqlCohortDefinition onFurosemide = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("onFurosemide", furosemide);
		
		CompositionCohortDefinition patientsSeenOnFurosemide = new CompositionCohortDefinition();
		patientsSeenOnFurosemide.setName("patientsSeenOnFurosemide");
		patientsSeenOnFurosemide.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenOnFurosemide.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenOnFurosemide.getSearches()
		        .put(
		            "1",
		            new Mapped<CohortDefinition>(onFurosemide, ParameterizableUtil
		                    .createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenOnFurosemide.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenOnFurosemide.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenOnFurosemideIndicator = Indicators.newCountIndicator(
		    "patientsSeenOnFurosemideIndicator", patientsSeenOnFurosemide,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "D3M",
		    "patients seen in the last month and are on furosemide",
		    new Mapped(patientsSeenOnFurosemideIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// D4: Of total patients seen in the last month, % on aldactone as of last visit
		//=======================================================
		
		SqlCohortDefinition onAldactone = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("onAldactone", aldactone);
		
		CompositionCohortDefinition patientsSeenOnAldactone = new CompositionCohortDefinition();
		patientsSeenOnAldactone.setName("patientsSeenOnAldactone");
		patientsSeenOnAldactone.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenOnAldactone.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenOnAldactone.getSearches().put("1",
		    new Mapped<CohortDefinition>(onAldactone, ParameterizableUtil.createParameterMappings("endDate=${onOrBefore}")));
		patientsSeenOnAldactone.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenOnAldactone.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenOnAldactoneIndicator = Indicators.newCountIndicator("patientsSeenOnAldactoneIndicator",
		    patientsSeenOnAldactone,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "D4M",
		    "patients seen in the last month and are on aldactone",
		    new Mapped(patientsSeenOnAldactoneIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// D5: Of total active patients, # of patients who received cardiac surgery in the last month
		//=======================================================
		
		SqlCohortDefinition patientsInPostOperativeState = Cohorts
		        .createPatientsInStateNotPredatingProgramEnrolment(postOperative);
		
		CompositionCohortDefinition patientsSeenAndInPostOperativeState = new CompositionCohortDefinition();
		patientsSeenAndInPostOperativeState.setName("patientsSeenAndInPostOperativeState");
		patientsSeenAndInPostOperativeState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsSeenAndInPostOperativeState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsSeenAndInPostOperativeState.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsInPostOperativeState, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsSeenAndInPostOperativeState.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrBefore-12m+1d},onOrBefore=${onOrBefore}")));
		patientsSeenAndInPostOperativeState.setCompositionString("1 AND 2");
		
		CohortIndicator patientsSeenAndInPostOperativeStateIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndInPostOperativeStateIndicator", patientsSeenAndInPostOperativeState,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "D5NM",
		    "Total active patients patients who received cardiac surgery in the last month",
		    new Mapped(patientsSeenAndInPostOperativeStateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// F1: Number of patients who died in review month
		//=======================================================
		ProgramEnrollmentCohortDefinition enrolledInHFProgramAsOfTheSartDate = Cohorts
		        .createProgramEnrollmentParameterizedByStartEndDate("enrolledInHFProgram", heartFailureProgram);
		
		SqlCohortDefinition patientsInPatientDiedState = Cohorts
		        .createPatientsInStateNotPredatingProgramEnrolment(patientDied);
		
		CompositionCohortDefinition patientsEnrolledAndInPatientDiedState = new CompositionCohortDefinition();
		patientsEnrolledAndInPatientDiedState.setName("patientsEnrolledAndInPatientDiedState");
		patientsEnrolledAndInPatientDiedState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsEnrolledAndInPatientDiedState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsEnrolledAndInPatientDiedState.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(enrolledInHFProgramAsOfTheSartDate, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${onOrAfter-1d},enrolledOnOrBefore=${onOrBefore}")));
		patientsEnrolledAndInPatientDiedState.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientsInPatientDiedState, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrBefore},onOrBefore=${onOrBefore}")));
		patientsEnrolledAndInPatientDiedState.setCompositionString("1 AND 2");
		
		CohortIndicator patientsEnrolledAndInPatientDiedStateIndicator = Indicators.newCountIndicator(
		    "patientsSeenAndInPostOperativeStateIndicator", patientsEnrolledAndInPatientDiedState,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "F1M",
		    "Number of patients who died in review month",
		    new Mapped(patientsEnrolledAndInPatientDiedStateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// F2: Of total active patients, # and  % with at least one hospitalization in the last month
		//=======================================================
		EncounterCohortDefinition patientWithPostoperatoireAndinsuffisanceHospitalisations = Cohorts
		        .createEncounterBasedOnForms("patientWithPostoperatoireAndinsuffisanceHospitalisations",
		            onOrAfterOnOrBefore, postoperatoireAndinsuffisanceHospitalisations);
		
		EncounterCohortDefinition patientWithPostoperatoireHospitalisations = Cohorts.createEncounterBasedOnForms(
		    "patientWithPostoperatoireAndinsuffisanceHospitalisations", onOrAfterOnOrBefore, postoperatoire);
		
		CompositionCohortDefinition patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations = new CompositionCohortDefinition();
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations
		        .setName("patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations");
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.addParameter(new Parameter("onOrAfter", "onOrAfter",
		        Date.class));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.addParameter(new Parameter("onOrBefore", "onOrBefore",
		        Date.class));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientWithPostoperatoireAndinsuffisanceHospitalisations, ParameterizableUtil
		            .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrBefore-12m+1d},onOrBefore=${onOrBefore}")));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.getSearches().put(
		    "3",
		    new Mapped<CohortDefinition>(patientWithPostoperatoireHospitalisations, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations.setCompositionString("(1 AND 2) OR 3");
		
		CohortIndicator patientsSeenWithPostoperatoireAndinsuffisanceHospitalisationsIndicator = Indicators
		        .newCountIndicator("patientsSeenWithPostoperatoireAndinsuffisanceHospitalisationsIndicator",
		            patientsSeenWithPostoperatoireAndinsuffisanceHospitalisations,
		            ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "F2NM",
		    "Total active patients with at least one hospitalization in the last month",
		    new Mapped(patientsSeenWithPostoperatoireAndinsuffisanceHospitalisationsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================
		// F3: Of total active patients, # and % with NYHA class IV at last visit (not including DDB)
		//=======================================================
		SqlCohortDefinition patientsWithPostoperatoireOrInsuffisanceRDV = Cohorts
		        .getPatientsWithObservationInFormBetweenStartAndEndDate("patientsWithPostoperatoireOrInsuffisanceRDV",
		            postoperatoireAndinsuffisanceRDV, NYHACLASS, NYHACLASS4);
		
		CompositionCohortDefinition activePatientsWithPostoperatoireOrInsuffisanceRDV = new CompositionCohortDefinition();
		activePatientsWithPostoperatoireOrInsuffisanceRDV.setName("activePatientsWithSC");
		activePatientsWithPostoperatoireOrInsuffisanceRDV.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		activePatientsWithPostoperatoireOrInsuffisanceRDV
		        .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		activePatientsWithPostoperatoireOrInsuffisanceRDV.getSearches().put(
		    "1",
		    new Mapped<CohortDefinition>(patientsWithPostoperatoireOrInsuffisanceRDV, ParameterizableUtil
		            .createParameterMappings("startDate=${onOrAfter},endDate=${onOrBefore}")));
		activePatientsWithPostoperatoireOrInsuffisanceRDV.getSearches().put(
		    "2",
		    new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
		            .createParameterMappings("onOrAfter=${onOrBefore-12m+1d},onOrBefore=${onOrBefore}")));
		activePatientsWithPostoperatoireOrInsuffisanceRDV.setCompositionString("1 AND 2");
		
		CohortIndicator activePatientsWithPostoperatoireOrInsuffisanceRDVIndicator = Indicators.newCountIndicator(
		    "activePatientsWithSRBetweenDatesIndicator", activePatientsWithPostoperatoireOrInsuffisanceRDV,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn(
		    "F3NM",
		    "Active patients with NYHA class IV at last visit (not including DDB)",
		    new Mapped(activePatientsWithPostoperatoireOrInsuffisanceRDVIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
	}
	
	private void setUpProperties() {
		heartFailureProgram = gp.getProgram(GlobalPropertiesManagement.HEART_FAILURE_PROGRAM_NAME);
		HFPrograms.add(heartFailureProgram);
		
		heartFailureEncounterType = gp.getEncounterType(GlobalPropertiesManagement.HEART_FAILURE_ENCOUNTER);

		heartFailureEncounterTypes.add(heartFailureEncounterType);
		
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		heartFailureDDBform = gp.getForm(GlobalPropertiesManagement.HEART_FAILURE_DDB);
		NYHACLASS = gp.getConcept(GlobalPropertiesManagement.NYHA_CLASS);
		NYHACLASS4 = gp.getConcept(GlobalPropertiesManagement.NYHA_CLASS_4);
		serumCreatinine = gp.getConcept(GlobalPropertiesManagement.SERUM_CREATININE);
		
		systolicBP = gp.getConcept(GlobalPropertiesManagement.SYSTOLIC_BLOOD_PRESSURE);
		
		carvedilol = gp.getConcept(GlobalPropertiesManagement.CARVEDILOL);
		atenolol = gp.getConcept(GlobalPropertiesManagement.ATENOLOL);
		carvedilolAndAtenolol.add(carvedilol);
		carvedilolAndAtenolol.add(atenolol);
		lisinopril = gp.getConcept(GlobalPropertiesManagement.LISINOPRIL);
		captopril = gp.getConcept(GlobalPropertiesManagement.CAPTOPRIL);
		lisinoprilAndCaptopril.add(lisinopril);
		lisinoprilAndCaptopril.add(captopril);
		
		furosemide = gp.getConcept(GlobalPropertiesManagement.FUROSEMIDE);
		aldactone = gp.getConcept(GlobalPropertiesManagement.ALDACTONE);
		
		warfarin=gp.getConcept(GlobalPropertiesManagement.WARFARIN);
		
		postoperatoireCardiaqueRDV = gp.getForm(GlobalPropertiesManagement.POSTOPERATOIRE_CARDIAQUERDV);
		insuffisanceCardiaqueRDV = gp.getForm(GlobalPropertiesManagement.INSUFFISANCE_CARDIAQUE_RDV);
		
		postoperatoireAndinsuffisanceRDV.add(postoperatoireCardiaqueRDV);
		postoperatoireAndinsuffisanceRDV.add(insuffisanceCardiaqueRDV);
		
		postoperatoireCardiaqueHospitalisations = gp
		        .getForm(GlobalPropertiesManagement.POSTOPERATOIRE_CARDIAQUE_HOSPITALISATIONS);
		insuffisanceCardiaqueHospitalisations = gp
		        .getForm(GlobalPropertiesManagement.INSUFFISANCE_CARDIAQUE_HOSPITALISATIONS);
		
		postoperatoire.add(postoperatoireCardiaqueHospitalisations);
		
		postoperatoireAndinsuffisanceHospitalisations.add(postoperatoireCardiaqueHospitalisations);
		postoperatoireAndinsuffisanceHospitalisations.add(insuffisanceCardiaqueHospitalisations);
		postOperative = gp.getProgramWorkflowState(GlobalPropertiesManagement.POST_OPERATIVE_STATE,
		    GlobalPropertiesManagement.SURGERY_STATUS_WORKFLOW, GlobalPropertiesManagement.HEART_FAILURE_PROGRAM_NAME);
		patientDied = gp.getProgramWorkflowState(GlobalPropertiesManagement.HEART_FAILURE_PATIENT_DIED_STATE,
		    GlobalPropertiesManagement.HEART_FAILURE_TREATMENT_STATUS_WORKFLOW,
		    GlobalPropertiesManagement.HEART_FAILURE_PROGRAM_NAME);
		
		notDone=gp.getConcept(GlobalPropertiesManagement.NOT_DONE);
		
		ejectionFraction=gp.getConcept(GlobalPropertiesManagement.EJECTION_FRACTION);
		
		DDBEchoResult=gp.getConcept(GlobalPropertiesManagement.DDB_ECHOCARDIOGRAPH_RESULT);
		
		echoComment=gp.getConcept(GlobalPropertiesManagement.DDB_ECHOCARDIOGRAPH_COMMENT);
		
		echoConcepts.add(DDBEchoResult);
		echoConcepts.add(ejectionFraction);
		echoConcepts.add(echoComment);
		
		echoForms.add(heartFailureDDBform);
		echoForms.add(gp.getForm(GlobalPropertiesManagement.CARDIOLOGY_CONSULT_FORM));
		echoForms.add(gp.getForm(GlobalPropertiesManagement.HYPERTENSION_FLOW_ECHOCARDIOGRAPHIE_FORM));
		
		cardConsultForm.add(gp.getForm(GlobalPropertiesManagement.CARDIOLOGY_CONSULT_FORM));
		
		
		heartFailureDiagnosis = gp.getConcept(GlobalPropertiesManagement.HEART_FAILURE_DIAGNOSIS);
		cardiomyopathy = gp.getConcept(GlobalPropertiesManagement.CARDIOMYOPATHY);
		miralStenosis = gp.getConcept(GlobalPropertiesManagement.MITRAL_STENOSIS);
		rhuematicHeartDisease = gp.getConcept(GlobalPropertiesManagement.RHUEMATIC_HEART_DISEASE);
		hypertensiveDisease = gp.getConcept(GlobalPropertiesManagement.HYPERTENSIVE_HEART_DISEASE);
		congenitalHeartDisease=gp.getConcept(GlobalPropertiesManagement.CONGENITAL_HEART_FAILURE);
		internationalNormalizedRatio=gp.getConcept(GlobalPropertiesManagement.INTERNATIONAL_NORMALIZED_RATIO);



		NCDSpecificOutcomes=gp.getConcept(GlobalPropertiesManagement.NCD_SPECIFIC_OUTCOMES);
		NCDRelatedDeathOutcomes= gp.getConcept(GlobalPropertiesManagement.NCD_RELATED_DEATH_OUTCOMES);
		unknownCauseDeathOutcomes =gp.getConcept(GlobalPropertiesManagement.UNKNOWN_CAUSE_OF_DEATH_OUTCOMES);
		otherCauseOfDeathOutcomes =gp.getConcept(GlobalPropertiesManagement.OTHER_CAUSE_OF_DEATH_OUTCOMES);

		DeathOutcomeResons.add(NCDRelatedDeathOutcomes);
		DeathOutcomeResons.add(unknownCauseDeathOutcomes);
		DeathOutcomeResons.add(otherCauseOfDeathOutcomes);

		exitReasonFromCare=gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
		patientDiedConcept=gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
		HIVStatus=gp.getConcept(GlobalPropertiesManagement.HIV_STATUS);

		postOpVisit=gp.getEncounterType(GlobalPropertiesManagement.POST_CARDIAC_SURGERY_VISIT);

		heartFailureEncounterTypes.add(postOpVisit);
	}
	
}
