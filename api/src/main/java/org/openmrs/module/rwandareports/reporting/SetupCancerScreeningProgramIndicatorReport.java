package org.openmrs.module.rwandareports.reporting;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SetupCancerScreeningProgramIndicatorReport {

    GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

    // properties

    private Form oncologyBreastScreeningExamination;

    private Form oncologyCervicalScreeningExamination;

    private Form mUzimaBreastScreening;

    private Form mUzimaCervicalScreening;


    private Form oncologyScreeningLabResultsForm;
    private Form muzimaOncologyScreeningLabResults;

    private  List<Form> oncologyScreeningLabResultsForms =new ArrayList<Form>();

    private  List<Form> screeningExaminationForms =new ArrayList<Form>();

    private  List<String> parameterNames=new ArrayList<String>();

    private Concept screeningType;

    private Concept HPV;

    private Concept testResult;
    private Concept HPVpositive;
    private Concept HPVNegative;

    private  List<Concept> testResults =new ArrayList<Concept>();

    private  List<Concept> positiveTestResults =new ArrayList<Concept>();

    private Form mUzimaCervicalCancerScreeningFollowup;

    private Form OncologyCervicalScreeningFollowUp;

    private  List<Form> cervicalCancerScreeningFollowupAndExaminationForms=new ArrayList<Form>();;

    private Concept typeOfVIAPerformed;
    private Concept VIATriage;
    private  List<Concept> VIATriageInList=new ArrayList<Concept>();

    private Concept VIAResults;
    private Concept VIAAndEligibleForThermalAblation ;
    private Concept VIAAndEligibleForLEEP;

    private  List<Concept> VIAAndEligibleResults=new ArrayList<Concept>();

    private Form mUzimaBreastCancerScreening;
    private List<Form> breastCancerScreeningForms = new ArrayList<Form>();

    private Concept reasonForBreastExam;
    private Concept breastSymptoms;
    private Concept screening;
    private Concept breastExamination;
    private Concept ABNORMAL;

    private Concept nextStep;
    private Concept referredTo;
    private Concept furtherManagement;

    private  List<Concept> furtherManagementInList=new ArrayList<Concept>();

    private Concept medicalImaging;
    private Concept BIOPSY;
    private Concept OTHERNONCODED;

    private Concept breastUltrasound;
    private Concept YES;
    private Concept solidMass;
    private Concept intermediate;
    private Concept highSuspiciousForMalignancy;
    private Concept proceduresDone;
    private Concept PAPANICOLAOUSMEAR;
    private Concept NORMAL;

    private Form muzimaOncologyScreeningDiagnosis;
    private Form oncologyScreeningDiagnosis;
    private List<Form> diagnosisScreeningForms = new ArrayList<Form>();

    private Concept CONFIRMEDDIAGNOSIS;
    private Concept confirmedCancerDiagnosis;
    private Concept cervicalCancer;
    private Concept breastCancer;

    private Form oncologyBreastCancerScreeningTransferIn;
    private List<Form> oncologyBreastCancerScreeningTransferInList = new ArrayList<Form>();


    private Concept VIANegative;
    private  List<Concept> VIANegativeInList=new ArrayList<Concept>();;

    private Concept VIAScreen;
    private  List<Concept> VIAScreenInList=new ArrayList<Concept>();

    private Concept typeOfTreatmentPerformed;
    private Concept thermalAblation;
    private Concept cryotherapy;
    private  List<Concept> thermalAblationAndCryotherapyList =new ArrayList<Concept>();

    private Concept reasonsForReferral;
    private Concept loopElectrosurgicalExcisionProcedure;
    private  List<Concept> loopElectrosurgicalExcisionProcedureInList=new ArrayList<Concept>();

    private  List<Concept> LEEPAndColposcopy=new ArrayList<Concept>();



    private Concept suspectedCancer;
    private  List<Concept> suspectedCancerInList=new ArrayList<Concept>();

    private Concept biopsyperformed;
    private Concept yes;
    private  List<Concept> yesInList=new ArrayList<Concept>();

    private Form oncologyCervicalCancerScreeningTransferIn;
    private  List<Form> oncologyCervicalCancerScreeningTransferInList=new ArrayList<Form>();

    private Concept hivStatus;

    private Concept positive;

    private Concept reasonsForReferralIn;

    private Concept entryMode;
    private Concept transferIn;

    public void setup() throws Exception {

        setUpProperties();

        Properties properties = new Properties();
        properties.setProperty("hierarchyFields", "countyDistrict:District");

        // Quarterly Report Definition: Start

        ReportDefinition monthlyRd = new ReportDefinition();
        monthlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        monthlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));


        monthlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));

        monthlyRd.setName("ONC - Cancer Screening Program Indicator Report");

        monthlyRd.addDataSetDefinition(createLocationDataSet(),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        // Monthly Report Definition: End

        EncounterCohortDefinition ScreeningExaminationEncounter=Cohorts.createEncounterBasedOnForms("ScreeningExaminationEncounter",parameterNames, screeningExaminationForms);

        monthlyRd.setBaseCohortDefinition(ScreeningExaminationEncounter,
                ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}"));

        Helper.saveReportDefinition(monthlyRd);

        ReportDesign mothlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRd,
                "ONC_Cancer_Screening_Program_Indicators.xls", "Cancer Screening Program Indicators Report (Excel)", null);
        Properties monthlyProps = new Properties();
        monthlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Cancer Screening Data Set");
        monthlyProps.put("sortWeight","5000");
        mothlyDesign.setProperties(monthlyProps);
        Helper.saveReportDesign(mothlyDesign);

    }

    public void delete() {
        ReportService rs = Context.getService(ReportService.class);
        for (ReportDesign rd : rs.getAllReportDesigns(false)) {
            if ("Cancer Screening Program Indicator Report (Excel)".equals(rd.getName())) {
                rs.purgeReportDesign(rd);
            }
        }
        Helper.purgeReportDefinition("ONC - Cancer Screening Program Indicator Report");

    }



    //Create Monthly Encounter Data set

    public LocationHierachyIndicatorDataSetDefinition createLocationDataSet() {

        LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
                createEncounterBaseDataSet());
        ldsd.addBaseDefinition(createBaseDataSet());
        ldsd.setName("Encounter Cancer Screening Data Set");
        ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));

        return ldsd;
    }

    private EncounterIndicatorDataSetDefinition createEncounterBaseDataSet() {

        EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
        eidsd.setName("eidsd");
        eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        createIndicators(eidsd);
        return eidsd;
    }

    private void createIndicators(EncounterIndicatorDataSetDefinition dsd) {

    }

    // create monthly cohort Data set

    private CohortIndicatorDataSetDefinition createBaseDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Monthly Cohort Data Set");
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        createIndicators(dsd);
        return dsd;
    }

    private void createIndicators(CohortIndicatorDataSetDefinition dsd) {
       // Cervical PROGRAM INDICATORS


        GenderCohortDefinition female=Cohorts.createFemaleCohortDefinition("female");


        // Percentage of screen-positive women referred for suspected cancer who attended the referral

        SqlCohortDefinition screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral",cervicalCancerScreeningFollowupAndExaminationForms,reasonsForReferral, suspectedCancerInList);


        SqlCohortDefinition screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn",screeningExaminationForms,reasonsForReferralIn, suspectedCancerInList);


        CompositionCohortDefinition screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral = new CompositionCohortDefinition();
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral.setName("screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral");
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter("startDate", "startDate", Date.class));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral.addParameter(new Parameter("endDate", "endDate", Date.class));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("1",new Mapped<CohortDefinition>(female,null));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral.getSearches().put("2",new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferral, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral.setCompositionString("1 and 2");

        CohortIndicator screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator = Indicators.newCountIndicator("screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator",
                screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));


        dsd.addColumn("CP1D", "Reffering HF : # of suspected cancer reffered out, scheduled appointment date.", new Mapped(
                screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



        CompositionCohortDefinition screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital=new CompositionCohortDefinition();
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.setName("screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital");
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter("startDate", "startDate", Date.class));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter("endDate", "endDate", Date.class));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("1",new Mapped<CohortDefinition>(screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferral, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.getSearches().put("2",new Mapped<CohortDefinition>(screenedForCervicalCancerWithSuspectedCancerAsReasonsForReferralInTransferIn, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital.setCompositionString("1 and 2");

        CohortIndicator screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator = Indicators.newCountIndicator("screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator",
                screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospital, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));


        dsd.addColumn("CP1N", "Receiving HF: # reffered and attended , date of arrival (Inclusive 30 days between scheduled appointment  date from reffering  HF and actual arrival at receiving HF", new Mapped(
                screenedWomenForCervicalCancerWithSuspectedCancerAsReasonsForReferralRecevedAtHospitalIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



        // Percentage of VIA positive women referred for further management who attended the referral

        SqlCohortDefinition screenedForCervicalCancerWithFurtherManagementAsReasonsForReferral=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("screenedForCervicalCancerWithFurtherManagementAsReasonsForReferral",cervicalCancerScreeningFollowupAndExaminationForms,reasonsForReferral, furtherManagementInList);


        SqlCohortDefinition screenedForCervicalCancerWithFurtherManagementAsReasonsForReferralInTransferIn=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("screenedForCervicalCancerWithFurtherManagementAsReasonsForReferralInTransferIn",screeningExaminationForms,reasonsForReferralIn, furtherManagementInList);

        SqlCohortDefinition screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult",cervicalCancerScreeningFollowupAndExaminationForms,VIAResults,VIAAndEligibleResults);



        CompositionCohortDefinition screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral = new CompositionCohortDefinition();
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.setName("screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral");
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.addParameter(new Parameter("startDate", "startDate", Date.class));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.addParameter(new Parameter("endDate", "endDate", Date.class));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.getSearches().put("1",new Mapped<CohortDefinition>(female,null));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.getSearches().put("2",new Mapped<CohortDefinition>(screenedForCervicalCancerWithFurtherManagementAsReasonsForReferral, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.getSearches().put("3",new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral.setCompositionString("1 and 2 and 3");

        CohortIndicator screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralIndicator = Indicators.newCountIndicator("screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralIndicator",
                screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));


        dsd.addColumn("CP2D", "1.Reffering HF : # of thermoablation, Cryotherapy, LEEP reffered out, scheduled appointment date.", new Mapped(
                screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");



        CompositionCohortDefinition screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital=new CompositionCohortDefinition();
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.setName("screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital");
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter("startDate", "startDate", Date.class));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.addParameter(new Parameter("endDate", "endDate", Date.class));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.getSearches().put("1",new Mapped<CohortDefinition>(screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferral, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.getSearches().put("2",new Mapped<CohortDefinition>(screenedForCervicalCancerWithFurtherManagementAsReasonsForReferralInTransferIn, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.getSearches().put("3",new Mapped<CohortDefinition>(screenedForCervicalCancerWithVIATriageAndVIAAndElligibleResult, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital.setCompositionString("1 and 2 and 3");

        CohortIndicator screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospitalIndicator = Indicators.newCountIndicator("screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospitalIndicator",
                screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospital, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));


        dsd.addColumn("CP2N", "Receiving HF: # reffered and attended , date of arrival (Inclusive 30 days between scheduled appointment  date from reffering  HF and actual arrival at receiving HF", new Mapped(
                screenedWomenForCervicalCancerWithFurtherManagementAsReasonsForReferralRecevedAtHospitalIndicator, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");


        // Percentage of screen-positive women referred for suspected cancer who were diagnosed with cancer

        SqlCohortDefinition screenedForCervicalCancerWithEntryModeIsarTransferIn=Cohorts.getPatientsWithObservationInFormBetweenStartAndEndDate("screenedForCervicalCancerWithEntryModeIsarTransferIn",cervicalCancerScreeningFollowupAndExaminationForms,entryMode,transferIn);



        // Percentage of screen-positive women with lesions eligible for Thermal ablation/ cryotherapy who received those proceedures
        //Percentage of screen-positive women with lesions eligible for Thermal ablation/ cryotherapy who received those proceedures during the same visit
       // Percentage of screen-positive women referred for large lesions who received LEEP




        // Breast PROGRAM INDICATORS
        //Percentage of breast abnormal findings people referred from HC who are seen at DH
        //Percentage of people receiving CBE who originally had breast symptoms and are diagnosed with breast cancer
        //% of women receiving CBE who originally had no breast symptoms but are diagnosed with breast cancer
        //% of women receiving CBE who have no breast symptoms but are found to have abnormal CBE

    }

    private void setUpProperties() {
        oncologyBreastScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_BREAST_SCREENING_EXAMINATION);
        oncologyCervicalScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_CERVICAL_SCREENING_EXAMINATION);

        mUzimaBreastScreening=Context.getFormService().getForm("mUzima Breast cancer screening");
        mUzimaCervicalScreening=Context.getFormService().getForm("mUzima Cervical cancer screening");

        parameterNames.add("onOrBefore");
        parameterNames.add("onOrAfter");

        screeningExaminationForms.add(oncologyBreastScreeningExamination);
        screeningExaminationForms.add(oncologyCervicalScreeningExamination);
        screeningExaminationForms.add(mUzimaBreastScreening);
        screeningExaminationForms.add(mUzimaCervicalScreening);
        screeningType=Context.getConceptService().getConceptByUuid("7e4e6554-d6c5-4ca3-b371-49806a754992");
        HPV=Context.getConceptService().getConceptByUuid("f7c2d59d-2043-42ce-b04d-08564d54b0c7");


        oncologyScreeningLabResultsForm=Context.getFormService().getFormByUuid("d7e4f3e6-2462-427d-83df-97d8488a53aa");
        muzimaOncologyScreeningLabResults=Context.getFormService().getFormByUuid("3a0e1a09-c88a-4412-99c6-cdbd7add50fd");

        oncologyScreeningLabResultsForms.add(oncologyScreeningLabResultsForm);
        oncologyScreeningLabResultsForms.add(muzimaOncologyScreeningLabResults);

        testResult=Context.getConceptService().getConceptByUuid("bfb3eb1e-db98-4846-9915-0168511c6298");
        HPVpositive=Context.getConceptService().getConceptByUuid("1b4a5f67-6106-4a4d-a389-2f430be543e4");
        HPVNegative =Context.getConceptService().getConceptByUuid("64c23192-54e4-4750-9155-2ed0b736a0db");
        testResults.add(HPVpositive);
        testResults.add(HPVNegative);
        positiveTestResults.add(HPVpositive);

        mUzimaCervicalCancerScreeningFollowup=Context.getFormService().getFormByUuid("94470633-8a84-4430-9910-10dcd628a0a2");
        OncologyCervicalScreeningFollowUp=Context.getFormService().getFormByUuid("9de98350-bc86-4012-a559-fcce13fc10c5");

        cervicalCancerScreeningFollowupAndExaminationForms.add(mUzimaCervicalCancerScreeningFollowup);
        cervicalCancerScreeningFollowupAndExaminationForms.add(OncologyCervicalScreeningFollowUp);
        cervicalCancerScreeningFollowupAndExaminationForms.add(oncologyCervicalScreeningExamination);
        cervicalCancerScreeningFollowupAndExaminationForms.add(mUzimaCervicalScreening);

        typeOfVIAPerformed=Context.getConceptService().getConceptByUuid("820b0e37-5d3e-46c6-9462-a8e7adaff954");
        VIATriage=Context.getConceptService().getConceptByUuid("69a0ca97-2fee-4c4a-9d84-4f2c25f70c93");
        VIATriageInList.add(VIATriage);

        VIAResults = Context.getConceptService().getConceptByUuid("a37a937a-a2a6-4c22-975f-986fb3599ea3");
        VIAAndEligibleForThermalAblation = Context.getConceptService().getConceptByUuid("3fe69559-cc82-48cb-926e-5d925aca088b");
        VIAAndEligibleForLEEP = Context.getConceptService().getConceptByUuid("402f3951-420e-4c09-9a9a-955bc0cff140");

        VIAAndEligibleResults.add(VIAAndEligibleForThermalAblation);
        VIAAndEligibleResults.add(VIAAndEligibleForLEEP);

        VIANegative = Context.getConceptService().getConceptByUuid("a7b08a37-0380-49dd-8f12-c2c2c76c8b13");
        VIANegativeInList.add(VIANegative);

        VIAScreen = Context.getConceptService().getConceptByUuid("690d8a13-a1ac-4fd7-97a4-f3964b97f049");
        VIAScreenInList.add(VIAScreen);


        typeOfTreatmentPerformed =  Context.getConceptService().getConceptByUuid("f7719cd4-e591-4a39-8bfe-9dd93d3cab89");
        thermalAblation =  Context.getConceptService().getConceptByUuid("ef4d5bd3-2c4e-4ac2-9b91-9d4ba12b44f8");
        cryotherapy = Context.getConceptService().getConceptByUuid("e48bc8db-8f6e-4556-9083-0a000a136e95");
        thermalAblationAndCryotherapyList.add(thermalAblation);
        thermalAblationAndCryotherapyList.add(cryotherapy);

        reasonsForReferral= Context.getConceptService().getConceptByUuid("1aa373f4-4db5-4b01-bce0-c10a636bb931");
        reasonsForReferralIn= Context.getConceptService().getConceptByUuid("cc227602-a240-43fb-926d-b6ad3f42edab");
        loopElectrosurgicalExcisionProcedure =  Context.getConceptService().getConceptByUuid("55040927-bae5-410c-80da-c79f7574167f");
        loopElectrosurgicalExcisionProcedureInList.add(loopElectrosurgicalExcisionProcedure);

        LEEPAndColposcopy.add(loopElectrosurgicalExcisionProcedure); // Colposcopy not available in Reason fo referral


        suspectedCancer= Context.getConceptService().getConceptByUuid("55040927-bae5-410c-80da-c79f7574167f");
        suspectedCancerInList.add(suspectedCancer);

        biopsyperformed = Context.getConceptService().getConceptByUuid("5563e827-9a1e-40eb-b05b-81d55981ce6d");
        yes = Context.getConceptService().getConceptByUuid("3cd6f600-26fe-102b-80cb-0017a47871b2");
        yesInList.add(yes);

        oncologyCervicalCancerScreeningTransferIn=Context.getFormService().getFormByUuid("f939311f-53ac-4587-9a9a-48d41ea1b38b");
        oncologyCervicalCancerScreeningTransferInList.add(oncologyCervicalCancerScreeningTransferIn);

        mUzimaBreastCancerScreening = Context.getFormService().getForm("mUzima Breast cancer screening");

        breastCancerScreeningForms.add(mUzimaBreastCancerScreening);
        breastCancerScreeningForms.add(oncologyBreastScreeningExamination);

        reasonForBreastExam = Context.getConceptService().getConceptByUuid("0483b8fa-b6d2-4551-ab4c-b141399897d7");
        breastSymptoms = Context.getConceptService().getConceptByUuid("b761e2c9-3d07-4a03-b274-c38282cc723c");
        screening = Context.getConceptService().getConceptByUuid("d9e56001-1c4e-479b-95f1-878e18d7ece8");
        breastExamination = Context.getConceptService().getConceptByUuid("f9576ba2-e96f-4b48-a178-42e72e6382ca");
        ABNORMAL = Context.getConceptService().getConceptByUuid("3cd75230-26fe-102b-80cb-0017a47871b2");
        nextStep = Context.getConceptService().getConceptByUuid("69b9671b-d8b1-461b-bb7d-adb151775a57");
        referredTo = Context.getConceptService().getConceptByUuid("25782f2c-074f-4834-b7d2-4668cd645a57");
        furtherManagement = Context.getConceptService().getConceptByUuid("de3a4342-b07e-48f8-ab80-202aab697756");
        medicalImaging = Context.getConceptService().getConceptByUuid("7f779262-de04-425b-97f7-9e5cc834eb55");
        BIOPSY = Context.getConceptService().getConceptByUuid("db64df50-1db1-4f80-abe5-b0307d7d4f9e");
        OTHERNONCODED = Context.getConceptService().getConceptByUuid("3cee7fb4-26fe-102b-80cb-0017a47871b2");
        breastUltrasound = Context.getConceptService().getConceptByUuid("58eacedf-cb05-4a8f-a98d-cc5717348d74");
        YES = Context.getConceptService().getConceptByUuid("3cd6f600-26fe-102b-80cb-0017a47871b2");
        solidMass = Context.getConceptService().getConceptByUuid("9ca1695a-f070-462e-81ce-d9778ee749b6");
        intermediate = Context.getConceptService().getConceptByUuid("636fc0a4-b82e-4a18-a97a-2cd43af8b852");
        highSuspiciousForMalignancy = Context.getConceptService().getConceptByUuid("f133b708-d7cf-47b3-81aa-17e618d47b2d");
        proceduresDone = Context.getConceptService().getConceptByUuid("e44f17a3-28bd-4fe1-a4e1-1bb94779a1fd");
        PAPANICOLAOUSMEAR = Context.getConceptService().getConceptByUuid("3cd4de2e-26fe-102b-80cb-0017a47871b2");
        NORMAL = Context.getConceptService().getConceptByUuid("3cd750a0-26fe-102b-80cb-0017a47871b2");

        muzimaOncologyScreeningDiagnosis = Context.getFormService().getForm("muzima oncology screening Diagnosis");
        oncologyScreeningDiagnosis = Context.getFormService().getForm("Oncology Screening Diagnosis");

        diagnosisScreeningForms.add(muzimaOncologyScreeningDiagnosis);
        diagnosisScreeningForms.add(oncologyScreeningDiagnosis);
        CONFIRMEDDIAGNOSIS = Context.getConceptService().getConceptByUuid("3d762b82-f951-4d13-b147-6aaba63b25d1");
        confirmedCancerDiagnosis = Context.getConceptService().getConceptByUuid("3dc2eb50-6981-43d3-b907-e3dd8b5ed620");
        cervicalCancer = Context.getConceptService().getConceptByUuid("36052b70-ba49-466f-a4eb-bc99581be7a2");
        breastCancer = Context.getConceptService().getConceptByUuid("e1bd83f4-e9fa-4564-b8aa-74a9b199aca8");

        oncologyBreastCancerScreeningTransferIn = Context.getFormService().getForm("Oncology Breast Cancer Screening transfer in");
        oncologyBreastCancerScreeningTransferInList.add(oncologyBreastCancerScreeningTransferIn);

        hivStatus=Context.getConceptService().getConceptByUuid("aec6ad18-f4dd-4cfa-b68d-3d7bb6ea908e");

        positive = Context.getConceptService().getConceptByUuid("3cd3a7a2-26fe-102b-80cb-0017a47871b2");
        furtherManagementInList.add(furtherManagement);

        entryMode = Context.getConceptService().getConceptByUuid("5c1c525c-07f0-4a76-bcff-f64cf1f7108d");
        transferIn = Context.getConceptService().getConceptByUuid("3cda3efa-26fe-102b-80cb-0017a47871b2");
    }
}
