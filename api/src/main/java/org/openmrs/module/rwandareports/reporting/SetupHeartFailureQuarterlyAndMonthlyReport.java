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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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
	
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	
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
		        + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0");
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
		// =======================================================
		// A2: Total # of patients seen in the last quarter
		// =======================================================
		EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
		    onOrAfterOnOrBefore, heartFailureEncounterType);
		
		CohortIndicator patientsSeenIndicator = Indicators.newCountIndicator("patientsSeenIndicator", patientSeen,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn("A2Q", "Total # of patients seen in the last quarter", new Mapped(patientsSeenIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//=======================================================================
		// A3: Total # of new patients enrolled in the last month/quarter
		//==================================================================
		
		ProgramEnrollmentCohortDefinition patientEnrolledInHFProgram = Cohorts.createProgramEnrollmentParameterizedByStartEndDate(
		    "Enrolled In Heart Failure Program", heartFailureProgram);
		
		CohortIndicator patientEnrolledInHFProgramQuarterIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFProgramQuarterIndicator", patientEnrolledInHFProgram,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));		
		
		dsd.addColumn(
		    "A3Q",
		    "Total # of new patients enrolled in the last quarter",
		    new Mapped(patientEnrolledInHFProgramQuarterIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// A4: Total # of patients ever enrolled 
		//======================================
		
		ProgramEnrollmentCohortDefinition patientEverEnrolledInHFProgram = Cohorts.createProgramEnrollmentParameterizedByStartEndDate(
		    "Ever Enrolled In Heart Failure Program", heartFailureProgram);
		
		CohortIndicator patientEverEnrolledInHFProgramIndicator = Indicators.newCountIndicator(
		    "patientEverEnrolledInHFProgramIndicator", patientEverEnrolledInHFProgram,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));		
		
		dsd.addColumn(
		    "A4Q",
		    "Total # of patients ever enrolled",
		    new Mapped(patientEverEnrolledInHFProgramIndicator, ParameterizableUtil
		            .createParameterMappings("endDate=${endDate}")), "");
		

		//======================================
		// A5: Percentage of eligible new Heart Failure  patients with at least one return visit in the quarter
		//======================================
		
		CohortIndicator patientEnrolledInHFProgramOneMonthBeforeStartDateOneMonthBeforeEndDateIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFProgramOneMonthBeforeStartDateOneMonthBeforeEndDateIndicator", patientEnrolledInHFProgram,
		    ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate-1m},enrolledOnOrBefore=${endDate-1m}"));	
		
		EncounterCohortDefinition patientWithPostOpAndHFRDVFormInPeriod = Cohorts.createEncounterBasedOnForms("patientWithPostOpAndHFRDVFormInPeriod",
		    onOrAfterOnOrBefore, postoperatoireAndinsuffisanceRDV);
		
		
		CompositionCohortDefinition patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate = new CompositionCohortDefinition();
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.setName("patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate");
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
		            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		
		patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.getSearches().put("2",new Mapped<CohortDefinition>(patientWithPostOpAndHFRDVFormInPeriod, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
	patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate.setCompositionString("1 AND 2");
		
	CohortIndicator patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDateIndicator = Indicators.newCountIndicator(
	    "patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDateIndicator", patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDate,ParameterizableUtil
        .createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate-1m},enrolledOnOrBefore=${endDate-1m}"));	
	
		
		dsd.addColumn(
		    "A5QD",
		    "Total # of new patients enrolled in startdate minus 1m and enddate minus 1m ",
		    new Mapped(patientEnrolledInHFProgramOneMonthBeforeStartDateOneMonthBeforeEndDateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "A5QN",
		    "Total # of new patients enrolled in startdate minus 1m and enddate minus 1m with RDV forms ",
		    new Mapped(patientWithPostOpAndHFRDVFormInPeriodAndEnrolledBeforeBothStartDadeEndDateIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// A6A: Age distribution of active patients   <= 15
		//======================================
		
		AgeCohortDefinition patientWithUnder16Years=Cohorts.createUnderAgeCohort("patientWithUnder15Years",15);
		
		CompositionCohortDefinition patientSeenUnder16Years = new CompositionCohortDefinition();
		patientSeenUnder16Years.setName("patientSeenUnder16Years");
		patientSeenUnder16Years.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenUnder16Years.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenUnder16Years.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		
		patientSeenUnder16Years.getSearches().put("1",new Mapped<CohortDefinition>(patientWithUnder16Years, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder16Years.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenUnder16Years.setCompositionString("1 AND 2");		
		
		CohortIndicator patientSeenUnder16YearsIndicator = Indicators.newCountIndicator(
		    "patientSeenUnder16YearsIndicator", patientSeenUnder16Years,ParameterizableUtil
            .createParameterMappings("effectiveDate=${endDate},onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A6A",
		    "Total # of patients seen under 16 years",
		    new Mapped(patientSeenUnder16YearsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// A6B: Age distribution of active patients   16 to 30
		//======================================
		
		AgeCohortDefinition patientWithUnder31Years=Cohorts.createUnderAgeCohort("patientWithUnder31Years",30);
		
		
		CompositionCohortDefinition patientSeenUnder31Years = new CompositionCohortDefinition();
		patientSeenUnder31Years.setName("patientSeenUnder31Years");
		patientSeenUnder31Years.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenUnder31Years.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenUnder31Years.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		
		patientSeenUnder31Years.getSearches().put("1",new Mapped<CohortDefinition>(patientWithUnder16Years, ParameterizableUtil
	            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder31Years.getSearches().put("2",new Mapped<CohortDefinition>(patientWithUnder31Years, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder31Years.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenUnder31Years.setCompositionString("NOT 1 AND (2 AND 3)");		
		
		CohortIndicator patientSeenUnder31YearsIndicator = Indicators.newCountIndicator(
		    "patientSeenUnder31YearsIndicator", patientSeenUnder31Years,ParameterizableUtil
            .createParameterMappings("effectiveDate=${endDate},onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A6B",
		    "Total # of patients seen under 31 years",
		    new Mapped(patientSeenUnder31YearsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		
		//======================================
		// A6C: Age distribution of active patients   31 to 45
		//======================================
		
		AgeCohortDefinition patientWithUnder46Years=Cohorts.createUnderAgeCohort("patientWithUnder45Years",45);
		
		
		CompositionCohortDefinition patientSeenUnder46Years = new CompositionCohortDefinition();
		patientSeenUnder46Years.setName("patientSeenUnder46Years");
		patientSeenUnder46Years.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenUnder46Years.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenUnder46Years.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		
		patientSeenUnder46Years.getSearches().put("1",new Mapped<CohortDefinition>(patientWithUnder31Years, ParameterizableUtil
	            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder46Years.getSearches().put("2",new Mapped<CohortDefinition>(patientWithUnder46Years, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder46Years.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenUnder46Years.setCompositionString("NOT 1 AND (2 AND 3)");		
		
		CohortIndicator patientSeenUnder46YearsIndicator = Indicators.newCountIndicator(
		    "patientSeenUnder46YearsIndicator", patientSeenUnder46Years,ParameterizableUtil
            .createParameterMappings("effectiveDate=${endDate},onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A6C",
		    "Total # of patients seen under 46 years",
		    new Mapped(patientSeenUnder46YearsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		

		
		//======================================
		// A6D: Age distribution of active patients   46 to 60
		//======================================
		
		AgeCohortDefinition patientWithUnder61Years=Cohorts.createUnderAgeCohort("patientWithUnder61Years",60);
		
		
		CompositionCohortDefinition patientSeenUnder61Years = new CompositionCohortDefinition();
		patientSeenUnder61Years.setName("patientSeenUnder61Years");
		patientSeenUnder61Years.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenUnder61Years.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenUnder61Years.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		
		patientSeenUnder61Years.getSearches().put("1",new Mapped<CohortDefinition>(patientWithUnder46Years, ParameterizableUtil
	            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder61Years.getSearches().put("2",new Mapped<CohortDefinition>(patientWithUnder61Years, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder61Years.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenUnder61Years.setCompositionString("NOT 1 AND (2 AND 3)");		
		
		CohortIndicator patientSeenUnder61YearsIndicator = Indicators.newCountIndicator(
		    "patientSeenUnder61YearsIndicator", patientSeenUnder61Years,ParameterizableUtil
            .createParameterMappings("effectiveDate=${endDate},onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A6D",
		    "Total # of patients seen under 61 years",
		    new Mapped(patientSeenUnder61YearsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		

		//======================================
		// A6E: Age distribution of active patients   61 to 75
		//======================================
		
		AgeCohortDefinition patientWithUnder76Years=Cohorts.createUnderAgeCohort("patientWithUnder61Years",75);
		
		
		CompositionCohortDefinition patientSeenUnder76Years = new CompositionCohortDefinition();
		patientSeenUnder76Years.setName("patientSeenUnder76Years");
		patientSeenUnder76Years.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenUnder76Years.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenUnder76Years.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		
		patientSeenUnder76Years.getSearches().put("1",new Mapped<CohortDefinition>(patientWithUnder61Years, ParameterizableUtil
	            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder76Years.getSearches().put("2",new Mapped<CohortDefinition>(patientWithUnder76Years, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenUnder76Years.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenUnder76Years.setCompositionString("NOT 1 AND (2 AND 3)");		
		
		CohortIndicator patientSeenUnder76YearsIndicator = Indicators.newCountIndicator(
		    "patientSeenUnder76YearsIndicator", patientSeenUnder76Years,ParameterizableUtil
            .createParameterMappings("effectiveDate=${endDate},onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A6E",
		    "Total # of patients seen under 76 years",
		    new Mapped(patientSeenUnder76YearsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//======================================
		// A6F: Age distribution of active patients   over 75
		//======================================
		
		CompositionCohortDefinition patientSeenOver75Years = new CompositionCohortDefinition();
		patientSeenOver75Years.setName("patientSeenOver75Years");
		patientSeenOver75Years.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenOver75Years.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientSeenOver75Years.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		
		patientSeenOver75Years.getSearches().put("2",new Mapped<CohortDefinition>(patientWithUnder76Years, ParameterizableUtil
		            .createParameterMappings("effectiveDate=${effectiveDate}")));
		
		patientSeenOver75Years.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenOver75Years.setCompositionString("NOT 2 AND 3");		
		
		CohortIndicator patientSeenOver75YearsIndicator = Indicators.newCountIndicator(
		    "patientSeenOver75YearsIndicator", patientSeenOver75Years,ParameterizableUtil
            .createParameterMappings("effectiveDate=${endDate},onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A6F",
		    "Total # of patients seen over 75 years",
		    new Mapped(patientSeenOver75YearsIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		

		//======================================
		// A7: Age distribution of active patients   over 75
		//======================================
		
		
		GenderCohortDefinition malePatient=Cohorts.createMaleCohortDefinition("malePatient");
		
		CompositionCohortDefinition malePatientSeen = new CompositionCohortDefinition();
		malePatientSeen.setName("malePatientSeen");
		malePatientSeen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malePatientSeen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		malePatientSeen.getSearches().put("2",new Mapped<CohortDefinition>(malePatient, null));
		
		malePatientSeen.getSearches().put("3",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		malePatientSeen.setCompositionString("2 AND 3");		
		
		
		CohortIndicator malePatientSeenIndicator = Indicators.newCountIndicator(
		    "malePatientSeenIndicator", malePatientSeen,ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-12m},onOrBefore=${endDate}"));
		
		
		CohortIndicator patientSeenIndicator = Indicators.newCountIndicator(
		    "patientSeenIndicator", patientSeen,ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-12m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "A7N",
		    "Total # of male patients seen",
		    new Mapped(malePatientSeenIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		dsd.addColumn(
		    "A7D",
		    "Total # of patients seen",
		    new Mapped(patientSeenIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		

		//======================================
		// B1: Of the patients newly enrolled in the previous quarter,  # and % with echocardiography documented
		//======================================
		
		
		CodedObsCohortDefinition echoResultNotDone=Cohorts.createCodedObsCohortDefinition("echoResultNotDone",onOrAfterOnOrBefore, DDBEchoResult, notDone, SetComparator.IN, TimeModifier.LAST);
		
		SqlCohortDefinition echoDocumanted=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("echoDocumanted", echoForms, echoConcepts);
		
		
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
		patientsWithCardConsult.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientsWithCardConsult.setCompositionString("1 AND 2");
		
		
		CohortIndicator patientEnrolledInHFProgramIndicator = Indicators.newCountIndicator(
		    "patientEnrolledInHFProgramIndicator", patientEnrolledInHFProgram,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${endDate-6m},enrolledOnOrBefore=${endDate}"));
		
		
		CohortIndicator patientsWithCardConsultIndicator = Indicators.newCountIndicator(
		    "patientsWithCardConsultIndicator", patientsWithCardConsult,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${endDate-6m},enrolledOnOrBefore=${endDate},onOrAfter=${endDate-6m},onOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B2D",
		    "Total number of patients newly enrolled in the previous two quarters",
		    new Mapped(patientEnrolledInHFProgramIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn(
		    "B2N",
		    "Total number of patients newly enrolled in the previous two quarters, with cardiology consultation with 6 months ",
		    new Mapped(patientsWithCardConsultIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		///======================================
		// B3: Of the patients newly enrolled in the previous quarter, distribution of type of heart failure diagnosis within 60 days of enrollment date 
		//======================================
		
		
		//======================================
		// B3A: cardiomyopathy
		//======================================		
		
		SqlCohortDefinition patientWithCardiomyopathyDiagnosis=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithCardiomyopathyDiagnosis",heartFailureDDBform,heartFailureDiagnosis,cardiomyopathy);
		
		CompositionCohortDefinition patientEnrolledWithCardiomyopathyDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithCardiomyopathyDiagnosis.setName("patientEnrolledWithCardiomyopathyDiagnosis");
		patientEnrolledWithCardiomyopathyDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithCardiomyopathyDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithCardiomyopathyDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithCardiomyopathyDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithCardiomyopathyDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCardiomyopathyDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithCardiomyopathyDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithCardiomyopathyDiagnosis.setCompositionString("1 AND 2");
		
		

		CohortIndicator patientEnrolledWithCardiomyopathyDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithCardiomyopathyDiagnosisIndicator", patientEnrolledWithCardiomyopathyDiagnosis,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B3AN",
		    "Total number of patients newly enrolled in the previous quarter with cardiomyopathy",
		    new Mapped(patientEnrolledWithCardiomyopathyDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		//======================================
		// B3B: rheumatic heart disease
		//======================================		
		
		SqlCohortDefinition patientWithRheumaticDiagnosis=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithRheumaticDiagnosis",heartFailureDDBform,heartFailureDiagnosis,rhuematicHeartDisease);
		
		CompositionCohortDefinition patientEnrolledWithRheumaticDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithRheumaticDiagnosis.setName("patientEnrolledWithRheumaticDiagnosis");
		patientEnrolledWithRheumaticDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithRheumaticDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithRheumaticDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithRheumaticDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithRheumaticDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithRheumaticDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithRheumaticDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithRheumaticDiagnosis.setCompositionString("1 AND 2");
		
		

		CohortIndicator patientEnrolledWithRheumaticDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithRheumaticDiagnosisIndicator", patientEnrolledWithRheumaticDiagnosis,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B3BN",
		    "Total number of patients newly enrolled in the previous quarter with Rheumatic",
		    new Mapped(patientEnrolledWithRheumaticDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// B3C: mitral stenosis
		//======================================		
		
		SqlCohortDefinition patientWithMitralStenosisDiagnosis=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithMitralStenosisDiagnosis",heartFailureDDBform,heartFailureDiagnosis,miralStenosis);
		
		CompositionCohortDefinition patientEnrolledWithMitralStenosisDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithMitralStenosisDiagnosis.setName("patientEnrolledWithMitralStenosisDiagnosis");
		patientEnrolledWithMitralStenosisDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithMitralStenosisDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithMitralStenosisDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithMitralStenosisDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithMitralStenosisDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithMitralStenosisDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithMitralStenosisDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithMitralStenosisDiagnosis.setCompositionString("1 AND 2");
		
		

		CohortIndicator patientEnrolledWithMitralStenosisDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithRheumaticDiagnosisIndicator", patientEnrolledWithMitralStenosisDiagnosis,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B3CN",
		    "Total number of patients newly enrolled in the previous quarter with Mitral stenosis",
		    new Mapped(patientEnrolledWithMitralStenosisDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
		//======================================
		// B3D: hypertensive heart disease
		//======================================		
		
		SqlCohortDefinition patientWithHypertensiveDiagnosis=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithhypertensiveDiagnosis",heartFailureDDBform,heartFailureDiagnosis,hypertensiveDisease);
		
		CompositionCohortDefinition patientEnrolledWithHypertensiveDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithHypertensiveDiagnosis.setName("patientEnrolledWithHypertensiveDiagnosis");
		patientEnrolledWithHypertensiveDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithHypertensiveDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithHypertensiveDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithHypertensiveDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithHypertensiveDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithHypertensiveDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithHypertensiveDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithHypertensiveDiagnosis.setCompositionString("1 AND 2");
		
		

		CohortIndicator patientEnrolledWithHypertensiveDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithhypertensiveDiagnosisIndicator", patientEnrolledWithHypertensiveDiagnosis,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B3DN",
		    "Total number of patients newly enrolled in the previous quarter with Hypertensive heart disease",
		    new Mapped(patientEnrolledWithHypertensiveDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// B3E: other heart failure diagnosis
		//======================================		
		
		
		SqlCohortDefinition patientWithOtherDiagnosis=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientWithOtherDiagnosis",heartFailureDDBform,heartFailureDiagnosis);
		
		CompositionCohortDefinition patientEnrolledWithOtherDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithOtherDiagnosis.setName("patientEnrolledWithOtherDiagnosis");
		patientEnrolledWithOtherDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithOtherDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithOtherDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithOtherDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithOtherDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCardiomyopathyDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithOtherDiagnosis.getSearches().put("2",new Mapped<CohortDefinition>(patientWithRheumaticDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithOtherDiagnosis.getSearches().put("3",new Mapped<CohortDefinition>(patientWithMitralStenosisDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithOtherDiagnosis.getSearches().put("4",new Mapped<CohortDefinition>(patientWithHypertensiveDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		patientEnrolledWithOtherDiagnosis.getSearches().put("5",new Mapped<CohortDefinition>(patientWithOtherDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
		
		patientEnrolledWithOtherDiagnosis.getSearches().put("6",new Mapped<CohortDefinition>(patientEnrolledInHFProgram, ParameterizableUtil
	            .createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
		patientEnrolledWithOtherDiagnosis.setCompositionString("6 AND 5 AND (NOT (1 OR 2 OR 3 OR 4))");
		
		

		CohortIndicator patientEnrolledWithOtherDiagnosisIndicator = Indicators.newCountIndicator(
		    "patientEnrolledWithOtherDiagnosisIndicator", patientEnrolledWithOtherDiagnosis,ParameterizableUtil
            .createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));	
		
		dsd.addColumn(
		    "B3EN",
		    "Total number of patients newly enrolled in the previous quarter with other diagnosis",
		    new Mapped(patientEnrolledWithOtherDiagnosisIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		//======================================
		// B3F: No diagnosis
		//======================================		
		
		
		CompositionCohortDefinition patientEnrolledWithNoDiagnosis = new CompositionCohortDefinition();
		patientEnrolledWithNoDiagnosis.setName("patientEnrolledWithNoDiagnosis");
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledWithNoDiagnosis.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledWithNoDiagnosis.getSearches().put("1",new Mapped<CohortDefinition>(patientWithCardiomyopathyDiagnosis, ParameterizableUtil
	            .createParameterMappings("startDate=${startDate},endDate=${startDate+60d}")));		
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
		
		
		

		//======================================
		// B4: Of the new patients enrolled in the last quarter, % with NYHA class IV
		//======================================
		
		CodedObsCohortDefinition patientWithNYHAClassIV=Cohorts.createCodedObsCohortDefinition("patientWithNYHAClassIV", NYHACLASS, NYHACLASS4, SetComparator.IN, TimeModifier.LAST);
		
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
		
		
		
		SqlCohortDefinition patientsWithCreatinineInHFEncounter = Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("patientsWithCreatinineInHFEncounter",heartFailureEncounterType,serumCreatinine);
		
		CompositionCohortDefinition patientSeenWithCreatinineInHFEncounter = new CompositionCohortDefinition();
		patientSeenWithCreatinineInHFEncounter.setName("patientSeenWithCreatinineInHFEncounter");
		patientSeenWithCreatinineInHFEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientSeenWithCreatinineInHFEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			
		patientSeenWithCreatinineInHFEncounter.getSearches().put("1",new Mapped<CohortDefinition>(patientsWithCreatinineInHFEncounter, ParameterizableUtil
	            .createParameterMappings("startDate=${endDate-6m},endDate=${endDate}")));
		
		patientSeenWithCreatinineInHFEncounter.getSearches().put("2",new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
	            .createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientSeenWithCreatinineInHFEncounter.setCompositionString("1 AND 2");	
		
		CohortIndicator patientSeenWithCreatinineInHFEncounterIndicator = Indicators.newCountIndicator(
		    "patientSeenWithCreatinineInHFEncounterIndicator", patientSeenWithCreatinineInHFEncounter,ParameterizableUtil
            .createParameterMappings("onOrAfter=${endDate-12m},onOrBefore=${endDate}"));
		

		dsd.addColumn(
		    "C1N",
		    "Total # of patients enrolled In HF With checked Creatinine in visit",
		    new Mapped(patientSeenWithCreatinineInHFEncounterIndicator, ParameterizableUtil
		            .createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		
		
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
		
		
		
		SqlCohortDefinition patientsInPostOperativeState = Cohorts.createPatientsInStateNotPredatingProgramEnrolment(postOperative);
		
		SqlCohortDefinition patientOnWarfarin=Cohorts.getPatientsOnCurrentRegimenBasedOnStartDateEndDate("patientOnWarfarin",warfarin);
		
		SqlCohortDefinition patientWithInternationalNormalizedRatio = Cohorts.getPatientsWithObservationsBetweenStartDateAndEndDate("patientWithInternationalNormalizedRatio", internationalNormalizedRatio);
		
		NumericObsCohortDefinition patientWithInternationalNormalizedRatioLessThan2 = Cohorts.createNumericObsCohortDefinition("patientWithInternationalNormalizedRatioLessThan2", onOrAfterOnOrBefore, internationalNormalizedRatio, 2.0, RangeComparator.LESS_THAN, TimeModifier.LAST);
		

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
		
		
		dsd.addColumn("C3D", "Total active patients who are post cardiac surgery and on warfarin with INR check in the last quarter", new Mapped(patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioIndicator,
		        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		dsd.addColumn("C3N", "Total active patients who are post cardiac surgery and on warfarin with INR less than 2 check in the last quarter", new Mapped(patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioLessThan2Indicator,
	        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
	
		
		//========================================================
		// C4:  Of total active patients who are post cardiac surgery and on warfarin with INR check in the last quarter, # and % with INR > 4 at last check
		//========================================================
		
		
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
		
		dsd.addColumn("C4N", "Total active patients who are post cardiac surgery and on warfarin with INR Greater than 4 check in the last quarter", new Mapped(patientSeenWithPostOperativeStateOnWarfarinWithInternationalNormalizedRatioGreaterThan4Indicator,
	        ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
	
		
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
		

		dsd.addColumn("D2N", "Total # of patients seen and on lisinopril or captopril as of last visit", new Mapped(patientSeenOnLisinoprilOrCaptoprilAtLastVisitIndicator,
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
		
		
		
		//=======================================================
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
		
		SqlCohortDefinition patientsInPatientDiedState = Cohorts
		        .createPatientsInStateNotPredatingProgramEnrolment(patientDied);
		
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
		internationalNormalizedRatio=gp.getConcept(GlobalPropertiesManagement.INTERNATIONAL_NORMALIZED_RATIO);
		
	}
	
}
