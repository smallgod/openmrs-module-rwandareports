package org.openmrs.module.rwandareports.reporting;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
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

import java.util.*;

/**
 * Created by josua on 10/22/18.
 */
public class SetupMentalHealthIndicatorReport {

    GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

    private Program mentalhealthProgram;

    private List<Program> mentalHealthPrograms = new ArrayList<Program>();

    private EncounterType mentalHealthEncounterType;
    private List<EncounterType> mentalHealthEncounterTypes = new ArrayList<EncounterType>();

    private List<String> onOrAfterOnOrBefore = new ArrayList<String>();

    private List<String> enrolledOnOrAfterOnOrBefore = new ArrayList<String>();

    List<Form> InitialAndRoutineEncounters;

    private Concept MHExitReasons;
    private Concept LostToFolloUpOutCome;

    private ProgramWorkflow PrimaryDiagnosisMHWorkflow;

    private ProgramWorkflowState SomatoformDisorderTroubleSomatoformMHPDWorkflowState;
    private ProgramWorkflowState EPILEPSYMHPDWorkflowState;
    private ProgramWorkflowState BipolarDisorderMHPDWorkflowState;
    private ProgramWorkflowState DepressionDueToOtherMedicalConditionMHPDWorkflowState;
    private ProgramWorkflowState DepressionWithPsychoticFeaturesMHPDWorkflowState;
    private ProgramWorkflowState DepressionUnspecifiedMHPDWorkflowState;
    private ProgramWorkflowState MajorDepressiveDisorderMHPDWorkflowState;
    private ProgramWorkflowState PSYCHOSISMHPDWorkflowState;
    private ProgramWorkflowState PsychosisDueToOtherMedicalConditionMHPDWorkflowState;
    private ProgramWorkflowState SCHIZOPHRENIAMHPDWorkflowState;


    private List<ProgramWorkflowState> SomatoformDisorderTroubleSomatoformMHWorkflowStateList =  new ArrayList<ProgramWorkflowState>() ;
    private List<ProgramWorkflowState> EPILEPSYMHWorkflowStateList =  new ArrayList<ProgramWorkflowState>() ;
    private List<ProgramWorkflowState> BipolarDisorderMHWorkflowStateList =  new ArrayList<ProgramWorkflowState>() ;
    private List<ProgramWorkflowState> DepressionList =  new ArrayList<ProgramWorkflowState>() ;
    private List<ProgramWorkflowState> PsychosisorSchizophreniaList =  new ArrayList<ProgramWorkflowState>() ;

    List<ProgramWorkflowState> BifferStateList =  new ArrayList<ProgramWorkflowState>() ;


    public void setup() throws Exception {

        setUpProperties();

        //Monthly report set-up


        Properties properties = new Properties();
        properties.setProperty("hierarchyFields", "countyDistrict:District");


        // Quarterly Report Definition: Start

        ReportDefinition quarterlyRd = new ReportDefinition();
        quarterlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        quarterlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));

        quarterlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));

        quarterlyRd.setName("MentalHealth Indicator Report-Quarterly");

        quarterlyRd.addDataSetDefinition(createQuarterlyLocationDataSet(),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        // Quarterly Report Definition: End

        ProgramEnrollmentCohortDefinition patientEnrolledInMentalHealthProgram = new ProgramEnrollmentCohortDefinition();
        patientEnrolledInMentalHealthProgram.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
        patientEnrolledInMentalHealthProgram.setPrograms(mentalHealthPrograms);



        quarterlyRd.setBaseCohortDefinition(patientEnrolledInMentalHealthProgram,
                ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));

        Helper.saveReportDefinition(quarterlyRd);


        ReportDesign quarterlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(quarterlyRd,
                "MentalHealth_Indicator_Quarterly_Report.xls", "MentalHealth Indicator Quarterly Report (Excel)", null);
        Properties quarterlyProps = new Properties();
        quarterlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Quarterly Data Set");
        quarterlyProps.put("sortWeight","5000");
        quarterlyDesign.setProperties(quarterlyProps);
        Helper.saveReportDesign(quarterlyDesign);

    }

    public void delete() {
        ReportService rs = Context.getService(ReportService.class);
        for (ReportDesign rd : rs.getAllReportDesigns(false)) {
            if ("MentalHealth Indicator Quarterly Report (Excel)".equals(rd.getName())) {
                rs.purgeReportDesign(rd);
            }
        }
        Helper.purgeReportDefinition("MentalHealth Indicator Report-Quarterly");

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
        //  A2: Total # of patient visits to DM clinic in the last quarter
        //==================================================================
        SqlEncounterQuery patientVisitsToMentalHealthClinic = new SqlEncounterQuery();

        patientVisitsToMentalHealthClinic
                .setQuery("select encounter_id from encounter where encounter_type="+mentalHealthEncounterType.getEncounterTypeId()+" and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0 group by encounter_datetime, patient_id");
        patientVisitsToMentalHealthClinic.setName("patientVisitsToMentalHealthClinic");
        patientVisitsToMentalHealthClinic.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientVisitsToMentalHealthClinic.addParameter(new Parameter("endDate", "endDate", Date.class));

        EncounterIndicator patientVisitsToMentalHealthClinicQuarterlyIndicator = new EncounterIndicator();
        patientVisitsToMentalHealthClinicQuarterlyIndicator.setName("patientVisitsToMentalHealthClinicQuarterlyIndicator");
        patientVisitsToMentalHealthClinicQuarterlyIndicator.setEncounterQuery(new Mapped<EncounterQuery>(
                patientVisitsToMentalHealthClinic, ParameterizableUtil
                .createParameterMappings("endDate=${endDate},startDate=${startDate}")));

        dsd.addColumn(patientVisitsToMentalHealthClinicQuarterlyIndicator);

    }

    // create quarterly cohort Data set
    private CohortIndicatorDataSetDefinition createQuarterlyBaseDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Quarterly Cohort Data Set");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        createQuarterlyIndicators(dsd);
        return dsd;
    }

    private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {

        //=======================================================================
        //  A1: Total # of patients seen in the last quarter
        //==================================================================


        EncounterCohortDefinition patientSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
                onOrAfterOnOrBefore, mentalHealthEncounterTypes);

        EncounterCohortDefinition patientWithDDB = Cohorts.createEncounterBasedOnForms("patientWithDDB",
                onOrAfterOnOrBefore, InitialAndRoutineEncounters);

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

        dsd.addColumn("A1N", "Total # of patients seen in the last quarter", new Mapped(patientsSeenQuarterIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");


        //=============================================================
        // A3: Active Patient(currently enrolled with an encounter)  //
        //=============================================================


        CompositionCohortDefinition activePatientComposition = new CompositionCohortDefinition();
        activePatientComposition.setName("activePatientComposition");
        activePatientComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        activePatientComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        activePatientComposition.getSearches().put(
                "1",
                new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        activePatientComposition.getSearches().put(
                "2",
                new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        activePatientComposition.setCompositionString("1 OR 2");

        CohortIndicator activePatientQuarterIndicator = Indicators.newCountIndicator("activePatientQuarterIndicator",
                activePatientComposition,
                ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate}"));
        dsd.addColumn("ActiveAtEndDate", "Total # of active at end date", new Mapped(activePatientQuarterIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");
        dsd.addColumn("ActiveAtStartDate", "Total # of active at start date", new Mapped(activePatientQuarterIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${startDate}")), "");


//        BifferStateList.add(SomatoformDisorderTroubleSomatoformMHPDWorkflowState);
//        InStateCohortDefinition patientsInSDorTSState = Cohorts.createInCurrentState("Patient has SD or TS", BifferStateList);
//
//        CompositionCohortDefinition activePatientWithSDorTSStateComposition = new CompositionCohortDefinition();
//        activePatientWithSDorTSStateComposition.setName("activePatientWithSDorTSStateComposition");
//        activePatientWithSDorTSStateComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
//        activePatientWithSDorTSStateComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
//        activePatientWithSDorTSStateComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//        activePatientWithSDorTSStateComposition.getSearches().put(
//                "1",
//                new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
//                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
//        activePatientWithSDorTSStateComposition.getSearches().put(
//                "2",
//                new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
//                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
//        activePatientWithSDorTSStateComposition.getSearches().put(
//                "3",
//                new Mapped<CohortDefinition>(patientsInSDorTSState, ParameterizableUtil
//                        .createParameterMappings("onDate=${onDate},onDate=${onDate}")));
//        activePatientWithSDorTSStateComposition.setCompositionString("(1 OR 2) AND 3");
//
//        CohortIndicator activePatientWithSDorTSStateQuarterIndicator = Indicators.newCountIndicator("activePatientWithSDorTSStateQuarterIndicator",
//                activePatientWithSDorTSStateComposition,
//                ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate},onDate=${endDate}"));
//        dsd.addColumn("activePatientWithSDorTSState", "Total # of active patient with SD or TS", new Mapped(activePatientWithSDorTSStateQuarterIndicator,
//                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //=============================================
        // B1: Active Patient With  Depression  //
        //=============================================

        CohortIndicator activePatientWithDepressionStatesIndicator = activeAndInstateInPeriod(DepressionList,patientWithDDB,patientSeen);
        dsd.addColumn("activePatientWithDepressionStates", "Total # of active patient with Depression", new Mapped(activePatientWithDepressionStatesIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //============================================================================
        // B1: Active Patient With  Somatoform Disorder or Trouble Somatoform        //
        //============================================================================

        CohortIndicator activePatientWithSDorTSStateIndicator = activeAndInstateInPeriod(SomatoformDisorderTroubleSomatoformMHWorkflowStateList,patientWithDDB,patientSeen);
        dsd.addColumn("activePatientWithSDorTSState", "Total # of active patient with SD or TS", new Mapped(activePatientWithSDorTSStateIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //=====================================================
        // B1: Active Patient With  Psychosis/Schizophrenia  //
        //=====================================================

        CohortIndicator activePatientWithPsychosisorSchizophreniaStatesIndicator = activeAndInstateInPeriod(PsychosisorSchizophreniaList,patientWithDDB,patientSeen);
        dsd.addColumn("activePatientWithPsychosisorSchizophreniaStates", "Total # of active patient with Psychosis or Schizophrenia", new Mapped(activePatientWithPsychosisorSchizophreniaStatesIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //============================================================================
        // B1: Active Patient With  EPILEPSY //
        //============================================================================

        CohortIndicator activePatientWithEpilepsyStateIndicator = activeAndInstateInPeriod(EPILEPSYMHWorkflowStateList,patientWithDDB,patientSeen);
        dsd.addColumn("activePatientWithEpilepsyState", "Total # of active patient with epilepsy", new Mapped(activePatientWithEpilepsyStateIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //=============================================
        // B1: Active Patient With  Bipolar Disorder //
        //=============================================

        CohortIndicator activePatientWithBipolarDisorderStateIndicator = activeAndInstateInPeriod(BipolarDisorderMHWorkflowStateList,patientWithDDB,patientSeen);
        dsd.addColumn("activePatientWithBipolarDisorderState", "Total # of active patient with Bipolar Disorder", new Mapped(activePatientWithBipolarDisorderStateIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //==========================//
        // C1: # of LTFU patients   //
        //==========================//

        SqlCohortDefinition programOutcomeLTFU=Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("LTFU",MHExitReasons,LostToFolloUpOutCome);


        CompositionCohortDefinition LTFUPatientComposition = new CompositionCohortDefinition();
        LTFUPatientComposition.setName("LTFUPatientComposition");
        LTFUPatientComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        LTFUPatientComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        LTFUPatientComposition.addParameter(new Parameter("endDate", "endDate", Date.class));
        LTFUPatientComposition.addParameter(new Parameter("startDate", "startDate", Date.class));
        LTFUPatientComposition.getSearches().put(
                "1",
                new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        LTFUPatientComposition.getSearches().put(
                "2",
                new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        LTFUPatientComposition.getSearches().put(
                "3",
                new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${endDate-12m+1d},onOrAfter=${startDate-12m+1d}")));
        LTFUPatientComposition.getSearches().put(
                "4",
                new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${endDate-12m+1d},onOrAfter=${startDate-12m+1d}")));
        LTFUPatientComposition.getSearches().put(
                "5",
                new Mapped<CohortDefinition>(programOutcomeLTFU, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
        LTFUPatientComposition.setCompositionString("(NOT (1 OR 2) AND (3 OR 4)) OR 3");

        CohortIndicator LTFUPatientQuarterIndicator = Indicators.newCountIndicator("LTFUPatientQuarterIndicator",
                LTFUPatientComposition,
                ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate},startDate=${startDate},endDate=${endDate}"));


        dsd.addColumn("LTFUPatientInQuarter", "Total # of ever LFTU patients", new Mapped(LTFUPatientQuarterIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate},endDate=${endDate}")), "");


        //========================================================//
        // C2: # of patients ever encounter at that health center //
        //========================================================//

        EncounterCohortDefinition patientEverSeen = Cohorts.createEncounterParameterizedByDate("Patients seen",
                "onOrBefore", mentalHealthEncounterTypes);

        EncounterCohortDefinition patientEverWithDDB = Cohorts.createEncounterBasedOnForms("patientWithDDB",
                "onOrBefore", InitialAndRoutineEncounters);


        CompositionCohortDefinition patientEverEncounteredComposition = new CompositionCohortDefinition();
        patientEverEncounteredComposition.setName("patientEverEncounteredComposition");
        patientEverEncounteredComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        patientEverEncounteredComposition.getSearches().put(
                "1",
                new Mapped<CohortDefinition>(patientEverWithDDB, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore}")));
        patientEverEncounteredComposition.getSearches().put(
                "2",
                new Mapped<CohortDefinition>(patientEverSeen, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore}")));
        patientEverEncounteredComposition.setCompositionString("1 OR 2");

        CohortIndicator patientEverEncounteredQuarterIndicator = Indicators.newCountIndicator("patientEverEncounteredQuarterIndicator",
                patientEverEncounteredComposition,
                ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}"));
        dsd.addColumn("patientEverEncountered", "Total # of ever Encountered patients", new Mapped(patientEverEncounteredQuarterIndicator,
                ParameterizableUtil.createParameterMappings("endDate=${endDate}")), "");

        //=======================================================================
        // A3: Total # of new patients enrolled in the last month/quarter
        //==================================================================

//        ProgramEnrollmentCohortDefinition patientEnrolledInCRDP = Cohorts.createProgramEnrollmentParameterizedByStartEndDate("Enrolled In CRDP",
//                mentalhealthProgram);
//
//        ProgramEnrollmentCohortDefinition patientEnrolledInCRDPByEndDate = Cohorts.createProgramEnrollmentEverByEndDate("Enrolled In CRDP",
//                mentalhealthProgram);
//
//
//        CompositionCohortDefinition patientEnrolledInCRDPAndSeenInSameQuarter = new CompositionCohortDefinition();
//        patientEnrolledInCRDPAndSeenInSameQuarter.setName("patientEnrolledInCRDPAndSeenInSameQuarter");/*
//		patientEnrolledInCRDPAndSeenInSameQuarter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
//		patientEnrolledInCRDPAndSeenInSameQuarter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));*/
//        patientEnrolledInCRDPAndSeenInSameQuarter.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
//        patientEnrolledInCRDPAndSeenInSameQuarter.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
//        patientEnrolledInCRDPAndSeenInSameQuarter.getSearches().put(
//                "1",
//                new Mapped<CohortDefinition>(patientEnrolledInCRDP, ParameterizableUtil
//                        .createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore},enrolledOnOrAfter=${enrolledOnOrAfter}")));
//        patientEnrolledInCRDPAndSeenInSameQuarter.getSearches().put(
//                "2",
//                new Mapped<CohortDefinition>(patientEnrolledInCRDPByEndDate, ParameterizableUtil
//                        .createParameterMappings("enrolledOnOrBefore=${enrolledOnOrBefore-3m}")));
//		/*patientEnrolledInCRDPAndSeenInSameQuarter.getSearches().put(
//				"3",
//				new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
//						.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));*/
//
//        //patientEnrolledInCRDPAndSeenInSameQuarter.setCompositionString("(1 and (not 2)) and 3");
//        patientEnrolledInCRDPAndSeenInSameQuarter.setCompositionString("1 and (not 2)");
//
//
//        CohortIndicator patientEnrolledInCRDPQuarterIndicator = Indicators.newCountIndicator(
//                "patientEnrolledInCRDPQuarterIndicator", patientEnrolledInCRDPAndSeenInSameQuarter,
//                ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));
//        CohortIndicator patientEnrolledInCRDPMonthOneIndicator = Indicators.newCountIndicator(
//                "patientEnrolledInCRDPQuarterIndicator", patientEnrolledInCRDP,
//                ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${endDate-1m+1d},enrolledOnOrBefore=${endDate}"));
//        CohortIndicator patientEnrolledInCRDPMonthTwooIndicator = Indicators.newCountIndicator(
//                "patientEnrolledInCRDPQuarterIndicator", patientEnrolledInCRDP,
//                ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${endDate-2m+1d},enrolledOnOrBefore=${endDate-1m+1d}"));
//        CohortIndicator patientEnrolledInCRDPMonthThreeIndicator = Indicators.newCountIndicator(
//                "patientEnrolledInCRDPQuarterIndicator", patientEnrolledInCRDP,
//                ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${endDate-3m+1d},enrolledOnOrBefore=${endDate-2m+1d}"));
//
//        // A3 Review March 2017 (it was A3Q but now it will be E1D/A3QReview )
//
//        //InProgramCohortDefinition inAsthmaProgramByEndDate=Cohorts.createInProgramParameterizableByDate("In CRDP by EndDate",asthmaProgram);
//        SqlCohortDefinition inAsthmaProgramByEndDate = new SqlCohortDefinition();
//        inAsthmaProgramByEndDate.setName("inAsthmaProgramByEndDate");
//        inAsthmaProgramByEndDate.addParameter(new Parameter("onDate", "onDate", Date.class));
//        inAsthmaProgramByEndDate.setQuery("select patient_id from patient_program where program_id=" + mentalhealthProgram.getProgramId() + " and voided=0 and date_enrolled<= :onDate");
//
//        //Cohorts.createInProgramParameterizableByDate("In CRDP by EndDate",asthmaProgram);
//
//        //ProgramEnrollmentCohortDefinition completedInAsthamProgramByEndDate=Cohorts.createProgramCompletedByEndDate("Completed CRDP by EndDate",asthmaProgram);
//
//        SqlCohortDefinition completedInAsthamProgramByEndDate = new SqlCohortDefinition();
//        completedInAsthamProgramByEndDate.setName("completedInAsthamProgramByEndDate");
//        completedInAsthamProgramByEndDate.addParameter(new Parameter("completedOnOrBefore", "completedOnOrBefore", Date.class));
//        completedInAsthamProgramByEndDate.setQuery("select patient_id from patient_program where program_id=" + mentalhealthProgram.getProgramId() + " and voided=0 and date_completed<= :completedOnOrBefore and date_completed is not null");
//
//        //Cohorts.createProgramCompletedByEndDate("Completed CRDP by EndDate",asthmaProgram);
//
//        SqlCohortDefinition currentlyInProgramAndNotCompleted = new SqlCohortDefinition();
//        currentlyInProgramAndNotCompleted.setName("currentlyInProgramAndNotCompleted");
//        currentlyInProgramAndNotCompleted.setQuery("select patient_id from patient_program where voided=0 and program_id=" + mentalhealthProgram.getProgramId() + " and (date_completed> :endDate or date_completed is null) and date_enrolled<= :endDate");
//        currentlyInProgramAndNotCompleted.addParameter(new Parameter("endDate", "endDate", Date.class));
//
//
//        CohortIndicator currentlyInProgramAndNotCompletedIndicator = Indicators.newCountIndicator(
//                "currentlyInProgramAndNotCompletedIndicator", currentlyInProgramAndNotCompleted,
//                ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
    }

    private void setUpProperties() {

        mentalhealthProgram = gp.getProgram(GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        mentalHealthPrograms.add(mentalhealthProgram);
        mentalHealthEncounterType = gp.getEncounterType(GlobalPropertiesManagement.MENTAL_HEALTH_VISIT);
        mentalHealthEncounterTypes.add(mentalHealthEncounterType);

        onOrAfterOnOrBefore.add("onOrAfter");
        onOrAfterOnOrBefore.add("onOrBefore");

        enrolledOnOrAfterOnOrBefore.add("enrolledOnOrAfter");
        enrolledOnOrAfterOnOrBefore.add("enrolledOnOrBefore");

        InitialAndRoutineEncounters=gp.getFormList(GlobalPropertiesManagement.MENTAL_HEALTH_INITIAL_ENCOUNTER_AND_RENDERZVOUS_VISIT_FORM);

        MHExitReasons=gp.getConcept(GlobalPropertiesManagement.MENTAL_HEALTH_EXIT_REASONS_CONCEPT);
        LostToFolloUpOutCome =gp.getConcept(GlobalPropertiesManagement.LOST_TO_FOLLOWUP_OUTCOME);

        PrimaryDiagnosisMHWorkflow = gp.getProgramWorkflow(GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);

        SomatoformDisorderTroubleSomatoformMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Somatoform_Disorder_or_Trouble_Somatoform_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        EPILEPSYMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.EPILEPSY_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        BipolarDisorderMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Bipolar_Disorder_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        DepressionDueToOtherMedicalConditionMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Depression_due_to_other_medical_condition_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        DepressionWithPsychoticFeaturesMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Depression_with_Psychotic_Features_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        DepressionUnspecifiedMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Depression_unspecified_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        MajorDepressiveDisorderMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Major_Depressive_Disorder_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        PSYCHOSISMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.PSYCHOSIS_MHWorkflowPDState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        PsychosisDueToOtherMedicalConditionMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.Psychosis_due_to_other_medical_condition_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);
        SCHIZOPHRENIAMHPDWorkflowState = gp.getProgramWorkflowState(GlobalPropertiesManagement.SCHIZOPHRENIA_MHPDWorkflowState, GlobalPropertiesManagement.Primary_Diagnosis_MH_Workflow, GlobalPropertiesManagement.MENTAL_HEALTH_PROGRAM);

        SomatoformDisorderTroubleSomatoformMHWorkflowStateList.add(SomatoformDisorderTroubleSomatoformMHPDWorkflowState);
        EPILEPSYMHWorkflowStateList.add(EPILEPSYMHPDWorkflowState);
        BipolarDisorderMHWorkflowStateList.add(BipolarDisorderMHPDWorkflowState);
        DepressionList.add(DepressionDueToOtherMedicalConditionMHPDWorkflowState);
        DepressionList.add(DepressionWithPsychoticFeaturesMHPDWorkflowState);
        DepressionList.add(DepressionUnspecifiedMHPDWorkflowState);
        DepressionList.add(MajorDepressiveDisorderMHPDWorkflowState);
        PsychosisorSchizophreniaList.add(PSYCHOSISMHPDWorkflowState);
//        PsychosisorSchizophreniaList.add(PsychosisDueToOtherMedicalConditionMHPDWorkflowState);
        PsychosisorSchizophreniaList.add(SCHIZOPHRENIAMHPDWorkflowState);





    }

    private CohortIndicator activeAndInstateInPeriod(List<ProgramWorkflowState> states,EncounterCohortDefinition patientWithDDB,
                                                     EncounterCohortDefinition patientSeen ){


        InStateCohortDefinition patientsInState = Cohorts.createInCurrentState("Patient has state", states);

        CompositionCohortDefinition activePatientWithStateComposition = new CompositionCohortDefinition();
        activePatientWithStateComposition.setName("activePatientWithStateComposition");
        activePatientWithStateComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
        activePatientWithStateComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        activePatientWithStateComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        activePatientWithStateComposition.getSearches().put(
                "1",
                new Mapped<CohortDefinition>(patientWithDDB, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        activePatientWithStateComposition.getSearches().put(
                "2",
                new Mapped<CohortDefinition>(patientSeen, ParameterizableUtil
                        .createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
        activePatientWithStateComposition.getSearches().put(
                "3",
                new Mapped<CohortDefinition>(patientsInState, ParameterizableUtil
                        .createParameterMappings("onDate=${onDate},onDate=${onDate}")));
        activePatientWithStateComposition.setCompositionString("(1 OR 2) AND 3");

        CohortIndicator activePatientWithStateQuarterIndicator = Indicators.newCountIndicator("activePatientWithStateQuarterIndicator",
                activePatientWithStateComposition,
                ParameterizableUtil.createParameterMappings("onOrAfter=${endDate-12m+1d},onOrBefore=${endDate},onDate=${endDate}"));


        return activePatientWithStateQuarterIndicator;
    }
}
