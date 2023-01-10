package org.openmrs.module.rwandareports.reporting;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.asm.IProgramElement;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupNCDsHMISReport extends SingleSetupReport implements SetupReport {

    protected final Log log = LogFactory.getLog(getClass());


    private int ICDConceptClassId;

    private Program asthmaProgram;
    private Program diabetesProgram;
    private Program hypertensionProgram;
    private Program heartFailureProgram;
    private List<Program> asthmaPrograms = new ArrayList<Program>();
    private Concept ICD11Concepts;

    private Concept asthma;
    private Concept respiratoryDiseaseAsthma;

    private Concept bronchitis;

    private Concept otherChronicRespiratory;
    private Concept probableAsthma;
    private Concept otherNonCodedPulmonaryDiseases;
    private Concept copd;
    private Concept typeOneDiabetes;
    private Concept typeTwoDiabetes;
    private Concept gestationalDiabetes;
    private Concept typesOfDiabetes;
    private Concept hypertension;
    private Concept cardiomyopathies;
    private Concept coronaryArteryDisease;
    private Concept pericardialDisease;
    private Concept heartFailure;
    private Concept stroke;
    private Concept rheumaticHeartDisease;
    private Concept congenitalHeartDisease ;
    private Concept otherCardiovasculardiseases;
    private Concept deepVeinThrombosis;
    private Concept renalFailure;
    private Concept otherChronicKidney;
    private Concept metabolicUnspecified;
    private Concept patientChronicDiseaseHistory;
    private Concept heartFailureDiagnosis;
    private Concept chronicCareDiagnosis;
    private Concept congenitalHeart;
    private Concept otherCongenitalHeart;
    private Concept otherRheumaticHeartDisease;
    private Concept rheumaticHeart;
    private Concept icd11Asthma;
    private Concept icd11Bronchitis;
    private Concept icd11OtheChronicrRespiratoryDisease;
    private Concept icd11DiabetesType1;
    private Concept icd11DiabetesType2;
    private Concept icd11Hypertension;
    private Concept icd11Cardiomyophaties;
    private Concept icd11CoronaryArteryDisease;
    private Concept icd11PericardialDisease;
    private Concept icd11HeartFailure;
    private Concept icd11Stroke;
    private Concept icd11RheumaticHeart;
    private Concept icd11CongenitalHeart;
    private Concept icd11OtherCardiovasculardiseases;
    private Concept icd11Deepveinusthrombosis;
    private Concept icd11RenalFailure;
    private Concept icd11OtherChronicKidneyDisease;
    private Concept icd11OtherEndocrineAndMetabolicDisease;
    private Concept icd11PericardialDisease2;
    private Concept icd11Cardiomyophaties2;
    private Concept icd11Cardiomyophaties3;
    private Concept icd11Cardiomyophaties4;
    private Concept icd11Cardiomyophaties5;
    private Concept icd11Cardiomyophaties6;
    private Concept icd11Cardiomyophaties7;
    private Concept icd11HeartFailure2;
    private Concept icd11HeartFailure3;
    private Concept icd11HeartFailure4;
    private Concept icd11OtherChronicKidneyDisease2;
    private Concept icd11RenalFailure2;
    private Concept icd11DiabetesCongenital;
    private Concept primaryICDDiagnosis;
    private Concept secondaryICDDiagnosis;

    private List<Concept> combinedAsthma = new ArrayList<Concept>();
    private List<Concept> combinedBronchitis = new ArrayList<Concept>();
    private List<Concept> combinedOtheChronicrRespiratoryDisease = new ArrayList<Concept>();
    private List<Concept> combinedDiabetesType1 = new ArrayList<Concept>();
    private List<Concept> combinedDiabetesType2 = new ArrayList<Concept>();
    private List<Concept> combinedHypertension = new ArrayList<Concept>();
    private List<Concept> combinedCardiomyophaties = new ArrayList<Concept>();
    private List<Concept> combinedCoronaryArteryDisease = new ArrayList<Concept>();
    private List<Concept> combinedPericardialDisease = new ArrayList<Concept>();
    private List<Concept> combinedHeartFailure = new ArrayList<Concept>();
    private List<Concept> combinedStroke = new ArrayList<Concept>();
    private List<Concept> combinedRheumaticHeart = new ArrayList<Concept>();
    private List<Concept> combinedCongenitalHeart = new ArrayList<Concept>();
    private List<Concept> combinedOtherCardiovasculardiseases = new ArrayList<Concept>();
    private List<Concept> combinedDeepveinusthrombosis = new ArrayList<Concept>();
    private List<Concept> combinedRenalFailure = new ArrayList<Concept>();
    private List<Concept> combinedOtherChronicKidneyDisease = new ArrayList<Concept>();
    private List<Concept> combinedOtherEndocrineAndMetabolicDisease = new ArrayList<Concept>();
    private List<Concept> combinedDiabetesCongenital = new ArrayList<Concept>();

    private List<Concept> conceptAsthma = new ArrayList<Concept>();
    private List<Concept> conceptDiabetesType = new ArrayList<Concept>();
    private List<Concept> conceptHypertension = new ArrayList<Concept>();
    private List<Concept> conceptCardiomyophaties = new ArrayList<Concept>();
    private List<Concept> conceptCoronaryArteryDisease = new ArrayList<Concept>();
    private List<Concept> conceptPericardialDisease = new ArrayList<Concept>();
    private List<Concept> conceptHeartFailure = new ArrayList<Concept>();
    private List<Concept> conceptStroke = new ArrayList<Concept>();
    private List<Concept> conceptRheumaticHeart = new ArrayList<Concept>();
    private List<Concept> conceptCongenitalHeart = new ArrayList<Concept>();
    private List<Concept> conceptOtherCardiovasculardiseases = new ArrayList<Concept>();
    private List<Concept> conceptDeepveinusthrombosis = new ArrayList<Concept>();
    private List<Concept> conceptRenalFailure = new ArrayList<Concept>();
    private List<Concept> conceptOtherChronicKidneyDisease = new ArrayList<Concept>();
    private List<Concept> conceptOtherEndocrineAndMetabolicDisease = new ArrayList<Concept>();
    private List<Concept> conceptDiabetesCongenital = new ArrayList<Concept>();


    Properties properties = new Properties();
    private  List<String> onOrAfterOnOrBefore =new ArrayList<String>();


    public String getReportName(){
        return "NCDs HMIS Monthly Indicator Report";
    }
    public void setup() throws Exception {

        log.info("Setting up report:" + getReportName());

        setUpProperties();

        properties.setProperty("hierarchyFields","countyDistrict:District");

        // Monthly Report Definition

       ReportDefinition ncdRd =createReportDefinition("Chronic Diseases HMIS Monthly Indicator Report",properties);

       ncdRd.addDataSetDefinition(createCohortMonthlyBaseDataSet(),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

//        //Set program enrollment
//
//        ProgramEnrollmentCohortDefinition patientEnrolledInAsthmaProgram = new ProgramEnrollmentCohortDefinition();
//        patientEnrolledInAsthmaProgram.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
//        patientEnrolledInAsthmaProgram.setPrograms(asthmaPrograms);
//
//        ncdRd.setBaseCohortDefinition(patientEnrolledInAsthmaProgram,
//                ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));

        Helper.saveReportDefinition(ncdRd);

        // Monthly Report Design

        ReportDesign ncdDesign =  Helper.createRowPerPatientXlsOverviewReportDesign(ncdRd,
                "NCDsHMISreport.xls",
                "Chronic Diseases HMIS Monthly Indicator Report",null);
        Properties ncdProps = new Properties();
        ncdProps.put("repeatingSections","sheet1,dataset: NCDs HMIS Report");
        ncdProps.put("sortWeight","5000");
        ncdDesign.setProperties(ncdProps);
        Helper.saveReportDesign(ncdDesign);



    }
    public ReportDefinition createReportDefinition(String name, Properties properties){

        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.addParameter(new Parameter("startDate","From",Date.class));
        reportDefinition.addParameter(new Parameter("endDate","To",Date.class));
        reportDefinition.addParameter(new Parameter("location", "Health facility", Location.class));

        SqlCohortDefinition location = new SqlCohortDefinition();
        location.setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat,patient_program pp where p.patient_id = pa.person_id and p.patient_id = pp.patient_id and pat.format ='org.openmrs.Location' and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id and pa.value = :location");
        location.setName("Location");
        location.addParameter(new Parameter("location", "location", Location.class));

        reportDefinition.setBaseCohortDefinition(location, ParameterizableUtil.createParameterMappings("location=${location}"));
        reportDefinition.addDataSetDefinition(createCohortMonthlyBaseDataSet(), ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
        reportDefinition.setName(name);
        return reportDefinition;
    }

//    private ReportDefinition createReportDefinition() {
//        ReportDefinition reportDefinition = new ReportDefinition();
//        reportDefinition.setName(getReportName());
//        reportDefinition.addParameter(new Parameter("location", "Location", Location.class));
//        reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
//
//        reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"),
//                ParameterizableUtil.createParameterMappings("location=${location}"));
//
//        createDataSetDefinition(reportDefinition);
//        Helper.saveReportDefinition(reportDefinition);
//
//        return reportDefinition;
//    }

    @Override
    public void delete() {

        Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - I. Chronic Diseases");

    }

    private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Monthly Cohort Data Set");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));

        createCohortMonthlyIndicators(dsd);
        return dsd;
    }

    public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSet() {

        LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
                createCohortMonthlyBaseDataSet());
        ldsd.setName("Monthly Cohort Data Set Four");
        ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
        return ldsd;
    }

//    private EncounterIndicatorDataSetDefinition createEncounterMonthlyBaseDataSet() {
//
//        EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
//        eidsd.setName("eidsd");
//        eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
//        eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
//        createMonthlyIndicators(eidsd);
//        return eidsd;
//    }

//    public ReportDefinition createReportDefinition(String name, Properties properties){
//
//        ReportDefinition reportDefinition = new ReportDefinition();
//        reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
//        reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
//        reportDefinition.addParameter(new Parameter("location", "Location", AllLocation.class));
//        reportDefinition.setName(name);
//        return reportDefinition;
//    }

    // Setup Global Properties
    public void setUpProperties(){

        onOrAfterOnOrBefore.add("onOrAfter");
        onOrAfterOnOrBefore.add("onOrBefore");

        asthmaProgram = gp.getProgram(GlobalPropertiesManagement.CHRONIC_RESPIRATORY_PROGRAM);
        diabetesProgram = gp.getProgram(GlobalPropertiesManagement.DM_PROGRAM);
        hypertensionProgram = gp.getProgram(GlobalPropertiesManagement.HYPERTENSION_PROGRAM);
        heartFailureProgram = gp.getProgram(GlobalPropertiesManagement.HEART_FAILURE_PROGRAM_NAME);
        asthmaPrograms.add(asthmaProgram);
        ICD11Concepts = Context.getConceptService().getConceptByUuid("c30d9cfc-ef2c-46f1-88c9-eabb86f9fe58");
        asthma = Context.getConceptService().getConceptByUuid("3d762b82-f951-4d13-b147-6aaba63b25d1");
        bronchitis = Context.getConceptService().getConceptByUuid("8eee36e3-1aaa-0244-7029-815f6cd3460f");
        otherNonCodedPulmonaryDiseases = Context.getConceptService().getConceptByUuid("0b8560c7-6eb9-4c92-a3b0-351f40fc12c0");
        copd = Context.getConceptService().getConceptByUuid("be7adab0-2ed5-44d7-972e-586911b08c8e");
        probableAsthma = Context.getConceptService().getConceptByUuid("dfb8ba28-2255-4437-b649-fa0bf7bd2fa9");
        otherChronicRespiratory = Context.getConceptService().getConceptByUuid("be7adab0-2ed5-44d7-972e-586911b08c8e");
        gestationalDiabetes = Context.getConceptService().getConceptByUuid("932d395c-d509-4d42-bac4-5a3281efe149");
        typeOneDiabetes = Context.getConceptService().getConceptByUuid("105903f4-7b6d-496a-b613-37ab9d0f5450");
        typeTwoDiabetes = Context.getConceptService().getConceptByUuid("8b26ecd3-8726-4c8e-b042-cbe71e44a863");
        typesOfDiabetes = Context.getConceptService().getConceptByUuid("d66e5df3-bc91-41ea-9592-0f199fbc589e");
        respiratoryDiseaseAsthma = Context.getConceptService().getConceptByUuid("e8f25c6e-9491-4ca3-9d31-7df9cb3d9ed9");
        hypertension = Context.getConceptService().getConceptByUuid("5d31abca-d671-4e93-aaa0-5b383ff7d8ad");
        cardiomyopathies = Context.getConceptService().getConceptByUuid("3ce8c3d0-26fe-102b-80cb-0017a47871b2");
        pericardialDisease = Context.getConceptService().getConceptByUuid("1ccfc018-bcf0-45b1-973a-2fdad7a773be");
        heartFailure = Context.getConceptService().getConceptByUuid("0670f6b9-5456-4bd3-86b1-846abc4fe2ba");
        stroke = Context.getConceptService().getConceptByUuid("111103AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        patientChronicDiseaseHistory = Context.getConceptService().getConceptByUuid("e20ab8c4-bd53-4f9f-b2e1-04fe7cb667ea");
        otherRheumaticHeartDisease = Context.getConceptService().getConceptByUuid("df3532da-64ed-4357-8319-fd1354b86fe8");
        rheumaticHeartDisease = Context.getConceptService().getConceptByUuid("3cceab1c-26fe-102b-80cb-0017a47871b2");
        congenitalHeart = Context.getConceptService().getConceptByUuid("dc1f76e1-0c91-a1de-514e-2e5a2ac13729");
        otherCongenitalHeart = Context.getConceptService().getConceptByUuid("2396ed75-2c3a-4c37-bb4b-c4473e948433");
        congenitalHeartDisease = Context.getConceptService().getConceptByUuid("d4c6945e-9328-4d0c-91c5-20c853f0afa6");
        otherCardiovasculardiseases = Context.getConceptService().getConceptByUuid("3cee7fb4-26fe-102b-80cb-0017a47871b2");
        deepVeinThrombosis = Context.getConceptService().getConceptByUuid("c1563200-58a4-399d-8e49-2a2b8120a482");
        renalFailure = Context.getConceptService().getConceptByUuid("894f306e-a868-44b8-b580-93495e3cca4c");
        otherChronicKidney = Context.getConceptService().getConceptByUuid("8611a721-9f01-484d-9405-5d88efdd9ce7");
        metabolicUnspecified = Context.getConceptService().getConceptByUuid("09be2849-e30b-2171-9caa-c7211c2177b3");
        heartFailureDiagnosis = Context.getConceptService().getConceptByUuid("e6bb1491-43b5-46b8-ba55-bd1ad188123c");
        chronicCareDiagnosis = Context.getConceptService().getConceptByUuid("bb7e04d8-3355-4fe8-9c87-98642eafab93");
        coronaryArteryDisease = Context.getConceptService().getConceptByUuid("92e917d0-baa0-55d5-ee06-bd79bba7c031");
        icd11DiabetesType1 = Context.getConceptService().getConceptByUuid("f2bb570d-78c6-73db-3ad5-707959938924");
        icd11DiabetesType2 = Context.getConceptService().getConceptByUuid("5bd8e2bf-d41f-539b-e3b5-e353d3e8eb4a");
        icd11OtherEndocrineAndMetabolicDisease = Context.getConceptService().getConceptByUuid("d9e647e7-5ebe-c47f-150f-6e7fe60600d6");
        icd11Stroke = Context.getConceptService().getConceptByUuid("265897ac-35e1-c0d8-93b3-42f051496772");
        icd11Hypertension = Context.getConceptService().getConceptByUuid("265897ac-35e1-c0d8-93b3-42f051496772");
        icd11CoronaryArteryDisease = Context.getConceptService().getConceptByUuid("92e917d0-baa0-55d5-ee06-bd79bba7c031");
        icd11PericardialDisease = Context.getConceptService().getConceptByUuid("73631fc8-2ff6-2453-34f4-0116a1d16781");
        icd11PericardialDisease2 = Context.getConceptService().getConceptByUuid("51c6bf27-caaa-b1b5-f835-27647ad11bb2");
        icd11RheumaticHeart = Context.getConceptService().getConceptByUuid("63b72219-fa30-9d9e-5d19-5df3e55b142f");
        icd11Cardiomyophaties = Context.getConceptService().getConceptByUuid("f1ba1876-f7dc-1eff-b416-06a8ae4acdd5");
        icd11Cardiomyophaties2 = Context.getConceptService().getConceptByUuid("fa30a0f6-0ab0-3d45-4a92-ac4280f50ddc");
        icd11Cardiomyophaties3 = Context.getConceptService().getConceptByUuid("a5627e21-c6df-0f53-e23b-7817af15a4fe");
        icd11Cardiomyophaties4 = Context.getConceptService().getConceptByUuid("307af523-2fb9-3213-38cf-ddd5abb7fa0c");
        icd11Cardiomyophaties5 = Context.getConceptService().getConceptByUuid("12d14d18-6bd2-1456-acb0-8772d554d366");
        icd11Cardiomyophaties6 = Context.getConceptService().getConceptByUuid("3835ca9b-8cae-0d5d-dc26-7515467b2249");
        icd11Cardiomyophaties7 = Context.getConceptService().getConceptByUuid("90623b79-cac2-bd96-af6e-36d84c9b5d78");
        icd11HeartFailure = Context.getConceptService().getConceptByUuid("8d090d05-6499-6677-acb2-aa4944d074bf");
        icd11HeartFailure2 = Context.getConceptService().getConceptByUuid("dbc3b319-4e50-0ea2-bea1-8259eb638c08");
        icd11HeartFailure3 = Context.getConceptService().getConceptByUuid("97f41af6-82db-534b-fa2b-3441e0874491");
        icd11HeartFailure4 = Context.getConceptService().getConceptByUuid("5f7cd7a6-1622-66df-d5ba-d83cce63d2b0");
        icd11OtherCardiovasculardiseases = Context.getConceptService().getConceptByUuid("332fb12f-d63b-938f-860d-dfdc7404624e");
        icd11Bronchitis = Context.getConceptService().getConceptByUuid("01d2b50e-75be-4e39-ac5d-61275db95da5");
        icd11OtheChronicrRespiratoryDisease = Context.getConceptService().getConceptByUuid("54660567-ffbe-ef9b-ef02-888bcd1c5c8c");
        icd11Asthma = Context.getConceptService().getConceptByUuid("6402ca00-e77f-9877-e152-a9e947253335");
        icd11Deepveinusthrombosis = Context.getConceptService().getConceptByUuid("0de70901-c5f0-9527-412d-6469d939b8b8");
        icd11OtherChronicKidneyDisease = Context.getConceptService().getConceptByUuid("8cb5f98a-977c-536b-68d0-a6df7eba5de4");
        icd11OtherChronicKidneyDisease2 = Context.getConceptService().getConceptByUuid("4403512f-4266-c7a6-e1df-ba84401f2022");
        icd11CongenitalHeart = Context.getConceptService().getConceptByUuid("ae1a2531-9215-be02-3772-ecb03139540b");
        icd11RenalFailure = Context.getConceptService().getConceptByUuid("3ddf059e-5cfe-6991-2d00-9a014266e03b");
        icd11RenalFailure2 = Context.getConceptService().getConceptByUuid("9088da7f-e207-f993-ecaa-795404ce7ba8");
        icd11DiabetesCongenital = Context.getConceptService().getConceptByUuid("fdaf2ef2-d7d5-4b6b-5337-963c9ef25d6a");
        primaryICDDiagnosis = Context.getConceptService().getConceptByUuid("2dce81f9-3874-4247-b441-6369ca0725c2");
        secondaryICDDiagnosis = Context.getConceptService().getConceptByUuid("afb8006f-e7c4-45bd-82bd-16f6e4b4b51d");


        //value_coded related to each diagnosis

        combinedAsthma.add(asthma);
        combinedAsthma.add(probableAsthma);
        combinedAsthma.add(icd11Asthma);

        combinedDiabetesType1.add(typeOneDiabetes);
        combinedDiabetesType1.add(icd11DiabetesType1);

        combinedDiabetesType2.add(typeTwoDiabetes);
        combinedDiabetesType2.add(icd11DiabetesType2);

        combinedBronchitis.add(bronchitis);
        combinedBronchitis.add(icd11Bronchitis);

        combinedDiabetesCongenital.add(gestationalDiabetes);
        combinedDiabetesCongenital.add(icd11DiabetesCongenital);

        combinedOtheChronicrRespiratoryDisease.add(otherChronicRespiratory);
        combinedOtheChronicrRespiratoryDisease.add(icd11OtheChronicrRespiratoryDisease);
        combinedOtheChronicrRespiratoryDisease.add(copd);


        combinedHypertension.add(hypertension);
        combinedHypertension.add(icd11Hypertension);

        combinedCardiomyophaties.add(cardiomyopathies);
        combinedCardiomyophaties.add(icd11Cardiomyophaties);
        combinedCardiomyophaties.add(icd11Cardiomyophaties2);
        combinedCardiomyophaties.add(icd11Cardiomyophaties3);
        combinedCardiomyophaties.add(icd11Cardiomyophaties4);
        combinedCardiomyophaties.add(icd11Cardiomyophaties5);
        combinedCardiomyophaties.add(icd11Cardiomyophaties6);
        combinedCardiomyophaties.add(icd11Cardiomyophaties7);

        combinedPericardialDisease.add(pericardialDisease);
        combinedPericardialDisease.add(icd11PericardialDisease);
        combinedPericardialDisease.add(icd11PericardialDisease2);

        combinedHeartFailure.add(heartFailure);
        combinedHeartFailure.add(icd11HeartFailure);
        combinedHeartFailure.add(icd11HeartFailure2);
        combinedHeartFailure.add(icd11HeartFailure3);
        combinedHeartFailure.add(icd11HeartFailure4);

        combinedStroke.add(stroke);
        combinedStroke.add(icd11Stroke);

        combinedRheumaticHeart.add(rheumaticHeartDisease);
        combinedRheumaticHeart.add(icd11RheumaticHeart);

        combinedCongenitalHeart.add(congenitalHeartDisease);
        combinedCongenitalHeart.add(icd11CongenitalHeart);

        combinedOtherCardiovasculardiseases.add(otherCardiovasculardiseases);
        combinedOtherCardiovasculardiseases.add(icd11OtherCardiovasculardiseases);

        combinedDeepveinusthrombosis.add(deepVeinThrombosis);
        combinedDeepveinusthrombosis.add(icd11Deepveinusthrombosis);

        combinedRenalFailure.add(renalFailure);
        combinedRenalFailure.add(icd11RenalFailure);
        combinedRenalFailure.add(icd11RenalFailure2);

        combinedOtherChronicKidneyDisease.add(otherChronicKidney);
        combinedOtherChronicKidneyDisease.add(icd11OtherChronicKidneyDisease);
        combinedOtherChronicKidneyDisease.add(icd11OtherChronicKidneyDisease2);

        combinedOtherEndocrineAndMetabolicDisease.add(metabolicUnspecified);
        combinedOtherEndocrineAndMetabolicDisease.add(icd11OtherEndocrineAndMetabolicDisease);
        
        
        //concepts that classify the above value_coded

        conceptAsthma.add(ICD11Concepts);
        conceptAsthma.add(respiratoryDiseaseAsthma);
//        conceptAsthma.add(primaryICDDiagnosis);
//        conceptAsthma.add(secondaryICDDiagnosis);

        conceptDiabetesType.add(typesOfDiabetes);
        conceptDiabetesType.add(ICD11Concepts);
//        conceptDiabetesType.add(primaryICDDiagnosis);
//        conceptDiabetesType.add(secondaryICDDiagnosis);

        conceptCardiomyophaties.add(heartFailureDiagnosis);
        conceptCardiomyophaties.add(ICD11Concepts);
//        conceptCardiomyophaties.add(primaryICDDiagnosis);
//        conceptCardiomyophaties.add(secondaryICDDiagnosis);

        conceptPericardialDisease.add(heartFailureDiagnosis);
        conceptPericardialDisease.add(ICD11Concepts);
//        conceptPericardialDisease.add(primaryICDDiagnosis);
//        conceptPericardialDisease.add(secondaryICDDiagnosis);

        conceptHeartFailure.add(chronicCareDiagnosis);
        conceptHeartFailure.add(ICD11Concepts);
//        conceptHeartFailure.add(primaryICDDiagnosis);
//        conceptHeartFailure.add(secondaryICDDiagnosis);

        conceptStroke.add(patientChronicDiseaseHistory);
        conceptStroke.add(ICD11Concepts);
//        conceptStroke.add(primaryICDDiagnosis);
//        conceptStroke.add(secondaryICDDiagnosis);

        conceptRheumaticHeart.add(heartFailureDiagnosis);
        conceptRheumaticHeart.add(ICD11Concepts);
//        conceptRheumaticHeart.add(primaryICDDiagnosis);
//        conceptRheumaticHeart.add(secondaryICDDiagnosis);

        conceptCongenitalHeart.add(heartFailureDiagnosis);
        conceptCongenitalHeart.add(ICD11Concepts);
//        conceptCongenitalHeart.add(primaryICDDiagnosis);
//        conceptCongenitalHeart.add(secondaryICDDiagnosis);

        conceptOtherCardiovasculardiseases.add(heartFailureDiagnosis);
        conceptOtherCardiovasculardiseases.add(ICD11Concepts);
//        conceptOtherCardiovasculardiseases.add(primaryICDDiagnosis);
//        conceptOtherCardiovasculardiseases.add(secondaryICDDiagnosis);

        conceptRenalFailure.add(chronicCareDiagnosis);
        conceptRenalFailure.add(ICD11Concepts);
//        conceptRenalFailure.add(primaryICDDiagnosis);
//        conceptRenalFailure.add(secondaryICDDiagnosis);

        conceptOtherChronicKidneyDisease.add(patientChronicDiseaseHistory);
        conceptOtherChronicKidneyDisease.add(ICD11Concepts);
//        conceptOtherChronicKidneyDisease.add(primaryICDDiagnosis);
//        conceptOtherChronicKidneyDisease.add(secondaryICDDiagnosis);

        conceptHypertension.add(ICD11Concepts);
        conceptHypertension.add(chronicCareDiagnosis);
//        conceptHypertension.add(primaryICDDiagnosis);
//        conceptHypertension.add(secondaryICDDiagnosis);


    }

    private AgeCohortDefinition patientWithAgeBetween(int age1, int age2) {
        AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
        patientsWithAge.setName("patientsWithAge");
        patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        patientsWithAge.setMinAge(age1);
        patientsWithAge.setMaxAge(age2);
        patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
        patientsWithAge.setMaxAgeUnit(DurationUnit.YEARS);
        return patientsWithAge;
    }

    private AgeCohortDefinition patientWithAgeAbove(int age) {
        AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
        patientsWithAge.setName("patientsWithAge");
        patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        patientsWithAge.setMinAge(age);
        patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
        return patientsWithAge;
    }

    private void createCohortMonthlyIndicators(CohortIndicatorDataSetDefinition dsd) {


        GenderCohortDefinition male = new GenderCohortDefinition();
        male.setName("male Patients");
        male.setMaleIncluded(true);

//        GenderCohortDefinition male=Cohorts.createMaleCohortDefinition("male");
        GenderCohortDefinition female=Cohorts.createFemaleCohortDefinition("female");

        AgeCohortDefinition patientBetweenZeroAndThirtyNineYears = patientWithAgeBetween(0,39);
        AgeCohortDefinition patientAbove40Years = patientWithAgeAbove(40);



        // 1.  New case patient with Asthma/Asthme



        SqlCohortDefinition newAsthma= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptAsthma,combinedAsthma);

        //New Asthma Enrolled
        ProgramEnrollmentCohortDefinition newlyEnrolledInAsthma = new ProgramEnrollmentCohortDefinition();
        newlyEnrolledInAsthma.setName("newlyEnrolledInAsthma");
        newlyEnrolledInAsthma.addProgram(asthmaProgram);
        newlyEnrolledInAsthma.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
        newlyEnrolledInAsthma.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));

        // 0-39

        CompositionCohortDefinition maleBetween0And39withAsthma = new CompositionCohortDefinition();
        maleBetween0And39withAsthma.setName("maleBetween0And39withAsthma");
        maleBetween0And39withAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39withAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39withAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39withAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39withAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39withAsthma.getSearches().put("1", new Mapped<CohortDefinition>(newAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39withAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39withAsthma.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        maleBetween0And39withAsthma.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39withAsthma.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator maleBetween0And39AsthmaNewCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39AsthmaNewCasePatientIndicator",
                maleBetween0And39withAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.N.M.039", "New Case Asthma Male", new Mapped(maleBetween0And39AsthmaNewCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39withAsthma = new CompositionCohortDefinition();
        femaleBetween0And39withAsthma.setName("femaleBetween0And39withAsthma");
        femaleBetween0And39withAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39withAsthma.getSearches().put("1", new Mapped<CohortDefinition>(newAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39withAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39withAsthma.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femaleBetween0And39withAsthma.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39withAsthma.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator femaleBetween0And39withAsthmaIndicator = Indicators.newCohortIndicator("femaleBetween0And39withAsthmaIndicator",
                femaleBetween0And39withAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.N.F.039", "New Case Asthma Female", new Mapped(femaleBetween0And39withAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithAsthma = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithAsthma.setName("malepatientsAbove40YearsWithAsthma");
        malepatientsAbove40YearsWithAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithAsthma.getSearches().put("1", new Mapped<CohortDefinition>(newAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        malepatientsAbove40YearsWithAsthma.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator malepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithAsthmaIndicator",
                malepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.N.M.40", "New case Asthma Male above 40", new Mapped(malepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithAsthma = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithAsthma.setName("femalepatientsAbove40YearsWithAsthma");
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("1", new Mapped<CohortDefinition>(newAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator femalepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithAsthmaIndicator",
                femalepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.N.F.40", "New case Asthma Female above 40", new Mapped(femalepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 1.1 Old case patient with Asthma

        SqlCohortDefinition oldAsthma =oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptAsthma,combinedAsthma);


// 0-39

        CompositionCohortDefinition maleBetween0And39withAsthmaOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39withAsthmaOldCasePatient.setName("maleBetween0And39withAsthmaOldCasePatient");
        maleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39withAsthmaOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39withAsthmaOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39withAsthmaOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        maleBetween0And39withAsthmaOldCasePatient.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39withAsthmaOldCasePatient.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator maleBetween0And39AsthmaOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39AsthmaOldCasePatientIndicator",
                maleBetween0And39withAsthmaOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.O.M.039", "Old case Asthma Male", new Mapped(maleBetween0And39AsthmaOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39withAsthmaOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39withAsthmaOldCasePatient.setName("femaleBetween0And39AsthmaOldCasePatient");
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39withAsthmaOldCasePatient.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator femaleBetween0And39withAsthmaOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39withAsthmaOldCasePatientIndicator",
                femaleBetween0And39withAsthmaOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.O.F.039", "Old case Asthma Female", new Mapped(femaleBetween0And39withAsthmaOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithAsthma = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithAsthma.setName("maleOldCasepatientsAbove40YearsWithAsthma");
        maleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("1", new Mapped<CohortDefinition>(oldAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        maleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator maleOldCasepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithAsthmaIndicator",
                maleOldCasepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.O.M.40", "Old case Asthma Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithAsthma = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithAsthma.setName("femaleOldCasepatientsAbove40YearsWithAsthma");
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("1", new Mapped<CohortDefinition>(oldAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator femaleOldCasepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithAsthmaIndicator",
                femaleOldCasepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("A.O.F.40", "Old case Asthma Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");





//        // 2.  New case patient with Bronchitis/Bronchite



SqlCohortDefinition newBronchitis=newPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,icd11Bronchitis);

// 0-39

        CompositionCohortDefinition maleBetween0And39Bronchitis = new CompositionCohortDefinition();
        maleBetween0And39Bronchitis.setName("maleBetween0And39Bronchitis");
        maleBetween0And39Bronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39Bronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39Bronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39Bronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39Bronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39Bronchitis.getSearches().put("1", new Mapped<CohortDefinition>(newBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39Bronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39Bronchitis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39Bronchitis.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39BronchitisIndicator = Indicators.newCohortIndicator("maleBetween0And39BronchitisIndicator",
                maleBetween0And39Bronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.N.M.039", "New case Bronchitis Male", new Mapped(maleBetween0And39BronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39Bronchitis = new CompositionCohortDefinition();
        femaleBetween0And39Bronchitis.setName("femaleBetween0And39AsthmaIntermittant");
        femaleBetween0And39Bronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39Bronchitis.getSearches().put("1", new Mapped<CohortDefinition>(newBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39Bronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39Bronchitis.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femaleBetween0And39Bronchitis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39BronchitisIndicator = Indicators.newCohortIndicator("femaleBetween0And39BronchitisIndicator",
                femaleBetween0And39Bronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.N.F.039", "New case Bronchitis Female", new Mapped(femaleBetween0And39BronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithBronchitis = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithBronchitis.setName("malepatientsAbove40YearsWithBronchitis");
        malepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithBronchitis.getSearches().put("1", new Mapped<CohortDefinition>(newBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithBronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithBronchitisIndicator",
                malepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.N.M.40", "New case Bronchitis Male above 40", new Mapped(malepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithBronchitis = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithBronchitis.setName("femalepatientsAbove40YearsWithBronchitis");
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithBronchitis.getSearches().put("1", new Mapped<CohortDefinition>(newBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithBronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femalepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithBronchitisIndicator",
                femalepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.N.F.40", "New case Bronchitis Female above 40", new Mapped(femalepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 2.1 Old case patient with Bronchitis/Bronchite

        SqlCohortDefinition oldBronchitis=oldPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,icd11Bronchitis);


// 0-39

        CompositionCohortDefinition maleBetween0And39BronchitisOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39BronchitisOldCasePatient.setName("maleBetween0And39BronchitisOldCasePatient");
        maleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39BronchitisOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39BronchitisOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39BronchitisOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39BronchitisOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39BronchitisOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39BronchitisOldCasePatientIndicator",
                maleBetween0And39BronchitisOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.O.M.039", "Old case Bronchitis Male", new Mapped(maleBetween0And39BronchitisOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39BronchitisOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39BronchitisOldCasePatient.setName("femaleBetween0And39AsthmaIntermittantOldCasePatient");
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39BronchitisOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39BronchitisOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femaleBetween0And39BronchitisOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39BronchitisOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39BronchitisOldCasePatientIndicator",
                femaleBetween0And39BronchitisOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.O.F.039", "Old case Bronchitis Female", new Mapped(femaleBetween0And39BronchitisOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithBronchitis = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithBronchitis.setName("maleOldCasepatientsAbove40YearsWithBronchitis");
        maleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("1", new Mapped<CohortDefinition>(oldBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithBronchitisIndicator",
                maleOldCasepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.O.M.40", "Old case Bronchitis Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithBronchitis = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithBronchitis.setName("femaleOldCasepatientsAbove40YearsWithBronchitis");
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("1", new Mapped<CohortDefinition>(oldBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femaleOldCasepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithBronchitisIndicator",
                femaleOldCasepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("B.O.F.40", "Old case Bronchitis Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 3. New case patient Other Chronic respiratory diseases



        SqlCohortDefinition newOtherChronicRespiratoryDiseases=newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptAsthma,combinedOtheChronicrRespiratoryDisease);

// 0-39

        CompositionCohortDefinition maleBetween0And39OtherChronicRespiratoryDiseases = new CompositionCohortDefinition();
        maleBetween0And39OtherChronicRespiratoryDiseases.setName("maleBetween0And39OtherChronicRespiratoryDiseases");
        maleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("1", new Mapped<CohortDefinition>(newOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        maleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39OtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator maleBetween0And39OtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("maleBetween0And39OtherChronicRespiratoryDiseasesIndicator",
                maleBetween0And39OtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.N.M.039", "New case Other Chronic respiratory diseases Male", new Mapped(maleBetween0And39OtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39OtherChronicRespiratoryDiseases = new CompositionCohortDefinition();
        femaleBetween0And39OtherChronicRespiratoryDiseases.setName("femaleBetween0And39AsthmaPersintentMild");
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("1", new Mapped<CohortDefinition>(newOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39OtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator femaleBetween0And39OtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("femaleBetween0And39OtherChronicRespiratoryDiseasesIndicator",
                femaleBetween0And39OtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.N.F.039", "New case Other Chronic respiratory diseases Female", new Mapped(femaleBetween0And39OtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases  = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .setName("malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases ");
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("1", new Mapped<CohortDefinition>(newOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases Indicator",
                malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases , ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.N.M.40", "New case Other Chronic respiratory diseases Male above 40", new Mapped(malepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases  = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .setName("femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases ");
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("1", new Mapped<CohortDefinition>(newOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator",
                femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases , ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.N.F.40", "New case Other Chronic respiratory diseases Female above 40", new Mapped(femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 2 Old case patient with Other Chronic respiratory diseases

        SqlCohortDefinition oldOtherChronicRespiratoryDiseases=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptAsthma,combinedOtheChronicrRespiratoryDisease);


// 0-39

        CompositionCohortDefinition maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setName("maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient");
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator",
                maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.O.M.039", "Old case Other Chronic respiratory diseases Male", new Mapped(maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setName("femaleBetween0And39AsthmaPersintentMildOldCasePatient");
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator",
                femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.O.F.039", "Old case Other Chronic respiratory diseases Female", new Mapped(femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setName("maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases");
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator",
                maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.O.M.40", "Old case Other Chronic respiratory diseases Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setName("femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases");
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(newlyEnrolledInAsthma, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("4", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3 and 4");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator",
                femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCR.O.F.40", "Old case Other Chronic respiratory diseases Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 3 New case patient with Diabetes - Type 1



        SqlCohortDefinition newDiabetesTypeOne=newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptDiabetesType,combinedDiabetesType1);

// 0-39

        CompositionCohortDefinition maleBetween0And39DiabetesTypeOne = new CompositionCohortDefinition();
        maleBetween0And39DiabetesTypeOne.setName("maleBetween0And39DiabetesTypeOne");
        maleBetween0And39DiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39DiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39DiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39DiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39DiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39DiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39DiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39DiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39DiabetesTypeOne.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeOneIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeOneIndicator",
                maleBetween0And39DiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.N.M.039", "New case Diabetes - Type 1 Male", new Mapped(maleBetween0And39DiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeOne = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeOne.setName("femaleBetween0And39AsthmaPersintentModarate");
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39DiabetesTypeOne.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeOneIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeOneIndicator",
                femaleBetween0And39DiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.N.F.039", "New case Diabetes - Type 1 Female", new Mapped(femaleBetween0And39DiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithDiabetesTypeOne = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithDiabetesTypeOne.setName("malepatientsAbove40YearsWithDiabetesTypeOne");
        malepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                malepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.N.M.40", "New case Diabetes - Type 1 Male above 40", new Mapped(malepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithDiabetesTypeOne = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDiabetesTypeOne.setName("femalepatientsAbove40YearsWithDiabetesTypeOne");
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                femalepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.N.F.40", "New case Diabetes - Type 1 Female above 40", new Mapped(femalepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 3 Old case patient with Diabetes - Type 1

        SqlCohortDefinition oldDiabetesTypeOne=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptDiabetesType,combinedDiabetesType1);


// 0-39

        CompositionCohortDefinition maleBetween0And39DiabetesTypeOneOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39DiabetesTypeOneOldCasePatient.setName("maleBetween0And39DiabetesTypeOneOldCasePatient");
        maleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39DiabetesTypeOneOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeOneOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeOneOldCasePatientIndicator",
                maleBetween0And39DiabetesTypeOneOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.O.M.039", "Old case Diabetes - Type 1 Male", new Mapped(maleBetween0And39DiabetesTypeOneOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeOneOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeOneOldCasePatient.setName("femaleBetween0And39AsthmaPersintentModarateOldCasePatient");
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeOneOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeOneOldCasePatientIndicator",
                femaleBetween0And39DiabetesTypeOneOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.O.F.039", "Old case Diabetes - Type 1 Female", new Mapped(femaleBetween0And39DiabetesTypeOneOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithDiabetesTypeOne = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setName("maleOldCasepatientsAbove40YearsWithDiabetesTypeOne");
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                maleOldCasepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.O.M.40", "Old case Diabetes - Type 1 Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setName("femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne");
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D1.O.F.40", "Old case Diabetes - Type 1 Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        // IV.R. 4 New case patient with Diabetes - Type 2



        SqlCohortDefinition newDiabetesTypeTwo=newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptDiabetesType,combinedDiabetesType2);

// 0-39

        CompositionCohortDefinition maleBetween0And39DiabetesTypeTwo = new CompositionCohortDefinition();
        maleBetween0And39DiabetesTypeTwo.setName("maleBetween0And39DiabetesTypeTwo");
        maleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39DiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39DiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39DiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39DiabetesTypeTwo.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeTwoIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeTwoIndicator",
                maleBetween0And39DiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.N.M.039", "New case Diabetes - Type 2 Male", new Mapped(maleBetween0And39DiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeTwo = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeTwo.setName("femaleBetween0And39DiabetesTypeTwo");
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39DiabetesTypeTwo.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeTwoIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeTwoIndicator",
                femaleBetween0And39DiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.N.F.039", "New case Diabetes - Type 2 Female", new Mapped(femaleBetween0And39DiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithDiabetesTypeTwo = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithDiabetesTypeTwo.setName("malepatientsAbove40YearsWithDiabetesTypeTwo");
        malepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                malepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.N.M.40", "New case Diabetes - Type 2 Male above 40", new Mapped(malepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithDiabetesTypeTwo = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDiabetesTypeTwo.setName("femalepatientsAbove40YearsWithDiabetesTypeTwo");
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                femalepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.N.F.40", "New case Diabetes - Type 2 Female above 40", new Mapped(femalepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with Diabetes - Type 2

        SqlCohortDefinition oldDiabetesTypeTwo=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptDiabetesType,combinedDiabetesType2);


// 0-39

        CompositionCohortDefinition maleBetween0And39DiabetesTypeTwoOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39DiabetesTypeTwoOldCasePatient.setName("maleBetween0And39DiabetesTypeTwoOldCasePatient");
        maleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeTwoOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeTwoOldCasePatientIndicator",
                maleBetween0And39DiabetesTypeTwoOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.O.M.039", "Old case Diabetes - Type 2 Male", new Mapped(maleBetween0And39DiabetesTypeTwoOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeTwoOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.setName("femaleBetween0And39DiabetesTypeTwoOldCasePatient");
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeTwoOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeTwoOldCasePatientIndicator",
                femaleBetween0And39DiabetesTypeTwoOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.O.F.039", "Old case Diabetes - Type 2 Female", new Mapped(femaleBetween0And39DiabetesTypeTwoOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setName("maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo");
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.O.M.40", "Old case Diabetes - Type 2 Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setName("femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo");
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("D2.O.F.40", "Old case Diabetes - Type 2 Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // 6. New case patient with Diabetes gestational



        SqlCohortDefinition newDiabetesGestational=newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptDiabetesType,combinedDiabetesCongenital);

// 0-39

//        CompositionCohortDefinition maleBetween0And39DiabetesGestational = new CompositionCohortDefinition();
//        maleBetween0And39DiabetesGestational.setName("maleBetween0And39DiabetesGestational");
//        maleBetween0And39DiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
//        maleBetween0And39DiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
//        maleBetween0And39DiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
//        maleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
//        maleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//        maleBetween0And39DiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
//        maleBetween0And39DiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
//        maleBetween0And39DiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
//        maleBetween0And39DiabetesGestational.setCompositionString("1 and 2 and 3");
//
//
//        CohortIndicator maleBetween0And39DiabetesGestationalIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesGestationalIndicator",
//                maleBetween0And39DiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
//        dsd.addColumn("DG.N.M.039", "New case Diabetes gestational Male", new Mapped(maleBetween0And39DiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesGestational = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesGestational.setName("femaleBetween0And39DiabetesGestational");
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39DiabetesGestational.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesGestationalIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesGestationalIndicator",
                femaleBetween0And39DiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DG.N.F.039", "New case Diabetes gestational Female", new Mapped(femaleBetween0And39DiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


//        CompositionCohortDefinition malepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
//        malepatientsAbove40YearsWithDiabetesGestational.setName("malepatientsAbove40YearsWithDiabetesGestational");
//        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
//        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
//        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
//        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
//        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//        malepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
//        malepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
//        malepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
//        malepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and 3");
//
//
//        CohortIndicator malepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDiabetesGestationalIndicator",
//                malepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
//        dsd.addColumn("DG.N.M.40", "New case Diabetes gestational Male above 40", new Mapped(malepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
//



        CompositionCohortDefinition femalepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDiabetesGestational.setName("femalepatientsAbove40YearsWithDiabetesGestational");
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDiabetesGestationalIndicator",
                femalepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DG.N.F.40", "New case Diabetes gestational Female above 40", new Mapped(femalepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 6. Old case patient with Diabetes gestational

        SqlCohortDefinition oldDiabetesGestational=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptDiabetesType,combinedDiabetesCongenital);


// 0-39

//        CompositionCohortDefinition maleBetween0And39DiabetesGestationalOldCasePatient = new CompositionCohortDefinition();
//        maleBetween0And39DiabetesGestationalOldCasePatient.setName("maleBetween0And39DiabetesGestationalOldCasePatient");
//        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
//        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
//        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
//        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
//        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//        maleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
//        maleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
//        maleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
//        maleBetween0And39DiabetesGestationalOldCasePatient.setCompositionString("1 and 2 and 3");
//
//
//        CohortIndicator maleBetween0And39DiabetesGestationalOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesGestationalOldCasePatientIndicator",
//                maleBetween0And39DiabetesGestationalOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
//        dsd.addColumn("DG.O.M.039", "Old case Diabetes gestational Male", new Mapped(maleBetween0And39DiabetesGestationalOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
//



        CompositionCohortDefinition femaleBetween0And39DiabetesGestationalOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesGestationalOldCasePatient.setName("femaleBetween0And39DiabetesGestationalOldCasePatient");
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39DiabetesGestationalOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesGestationalOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesGestationalOldCasePatientIndicator",
                femaleBetween0And39DiabetesGestationalOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DG.O.F.039", "Old case Diabetes gestational Female", new Mapped(femaleBetween0And39DiabetesGestationalOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +

//        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.setName("maleOldCasepatientsAbove40YearsWithDiabetesGestational");
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
//        maleOldCasepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and 3");
//
//
//        CohortIndicator maleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator",
//                maleOldCasepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
//        dsd.addColumn("DG.O.M.40", "Old case Diabetes gestational Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.setName("femaleOldCasepatientsAbove40YearsWithDiabetesGestational");
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator",
                femaleOldCasepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DG.O.F.40", "Old case Diabetes gestational Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // IV.R. 4 New case patients with Hypertension



        SqlCohortDefinition newPatientWithHypertension= newPatientWithNCDDiagnosisObsByStartDateAndEndDateHypertension(combinedHypertension);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithHypertension = new CompositionCohortDefinition();
        maleBetween0And39WithHypertension.setName("maleBetween0And39WithHypertension");
        maleBetween0And39WithHypertension.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithHypertension.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithHypertension.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithHypertension.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithHypertension.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithHypertension.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithHypertension.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithHypertension.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithHypertension.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithHypertensionIndicator = Indicators.newCohortIndicator("maleBetween0And39WithHypertensionIndicator",
                maleBetween0And39WithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.N.M.039", "New Case patients with Hypertension Male", new Mapped(maleBetween0And39WithHypertensionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithHypertension = new CompositionCohortDefinition();
        femaleBetween0And39WithHypertension.setName("femaleBetween0And39WithHypertension");
        femaleBetween0And39WithHypertension.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithHypertension.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithHypertension.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithHypertension.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithHypertension.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithHypertension.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithHypertension.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithHypertension.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithHypertension.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithHypertensionIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithHypertensionIndicator",
                femaleBetween0And39WithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.N.F.039", "New case Hypertension Female", new Mapped(femaleBetween0And39WithHypertensionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithHypertension = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithHypertension.setName("malepatientsAbove40YearsWithHypertension");
        malepatientsAbove40YearsWithHypertension.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithHypertension.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithHypertension.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithHypertension.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithHypertension.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithHypertension.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithHypertension.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithHypertensionIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithHypertensionIndicator",
                malepatientsAbove40YearsWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.N.M.40", "New case Hypertension Male above 40", new Mapped(malepatientsAbove40YearsWithHypertensionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithHypertension = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithHypertension.setName("femalepatientsAbove40YearsWithHypertension");
        femalepatientsAbove40YearsWithHypertension.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithHypertension.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithHypertension.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithHypertension.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithHypertension.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithHypertension.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithHypertension.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithHypertensionIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithHypertensionIndicator",
                femalepatientsAbove40YearsWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.N.F.40", "New case Hypertension Female above 40", new Mapped(femalepatientsAbove40YearsWithHypertensionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with Hypertension

        SqlCohortDefinition oldHypertension=oldPatientWithNCDDiagnosisObsByStartDateAndEndDateHypertension(combinedHypertension,hypertensionProgram);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithHypertensionOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithHypertensionOldCasePatient.setName("maleBetween0And39WithHypertensionOldCasePatient");
        maleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithHypertensionOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithHypertensionOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithHypertensionOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithHypertensionOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithHypertensionOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithHypertensionOldCasePatientIndicator",
                maleBetween0And39WithHypertensionOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.O.M.039", "Old case Hypertension Male", new Mapped(maleBetween0And39WithHypertensionOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithHypertensionOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithHypertensionOldCasePatient.setName("femaleBetween0And39WithHypertensionOldCasePatient");
        femaleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithHypertensionOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithHypertensionOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithHypertensionOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithHypertensionOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithHypertensionOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithHypertensionOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithHypertensionOldCasePatientIndicator",
                femaleBetween0And39WithHypertensionOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.O.F.039", "Old case Hypertension Female", new Mapped(femaleBetween0And39WithHypertensionOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithHypertension = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithHypertension.setName("maleOldCasepatientsAbove40YearsWithHypertension");
        maleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithHypertension.getSearches().put("1", new Mapped<CohortDefinition>(oldHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithHypertension.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithHypertension.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithHypertension.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithHypertensionIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithHypertensionIndicator",
                maleOldCasepatientsAbove40YearsWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.O.M.40", "Old case Hypertension Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithHypertensionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithHypertension = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithHypertension.setName("femaleOldCasepatientsAbove40YearsWithHypertension");
        femaleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithHypertension.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithHypertension.getSearches().put("1", new Mapped<CohortDefinition>(oldHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithHypertension.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithHypertension.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithHypertension.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithHypertensionIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithHypertensionIndicator",
                femaleOldCasepatientsAbove40YearsWithHypertension, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("H.O.F.40", "Old case Hypertension Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithHypertensionIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // New patients with cardiomyopathies

        SqlCohortDefinition newPatientWithcardiomyopathies= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptCardiomyophaties,combinedCardiomyophaties);

// 0-39

        CompositionCohortDefinition maleBetween0And39Withcardiomyopathies = new CompositionCohortDefinition();
        maleBetween0And39Withcardiomyopathies.setName("maleBetween0And39Withcardiomyopathies");
        maleBetween0And39Withcardiomyopathies.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39Withcardiomyopathies.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39Withcardiomyopathies.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39Withcardiomyopathies.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39Withcardiomyopathies.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39Withcardiomyopathies.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39Withcardiomyopathies.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39Withcardiomyopathies.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39Withcardiomyopathies.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithcardiomyopathiesIndicator = Indicators.newCohortIndicator("maleBetween0And39WithcardiomyopathiesIndicator",
                maleBetween0And39Withcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.N.M.039", "New Case patients with cardiomyopathies Male", new Mapped(maleBetween0And39WithcardiomyopathiesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39Withcardiomyopathies = new CompositionCohortDefinition();
        femaleBetween0And39Withcardiomyopathies.setName("femaleBetween0And39Withcardiomyopathies");
        femaleBetween0And39Withcardiomyopathies.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39Withcardiomyopathies.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39Withcardiomyopathies.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39Withcardiomyopathies.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39Withcardiomyopathies.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39Withcardiomyopathies.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39Withcardiomyopathies.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39Withcardiomyopathies.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39Withcardiomyopathies.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithcardiomyopathiesIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithcardiomyopathiesIndicator",
                femaleBetween0And39Withcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.N.F.039", "New case cardiomyopathies Female", new Mapped(femaleBetween0And39WithcardiomyopathiesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithcardiomyopathies = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithcardiomyopathies.setName("malepatientsAbove40YearsWithcardiomyopathies");
        malepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithcardiomyopathies.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithcardiomyopathies.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithcardiomyopathies.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithcardiomyopathies.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithcardiomyopathiesIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithcardiomyopathiesIndicator",
                malepatientsAbove40YearsWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.N.M.40", "New case cardiomyopathies Male above 40", new Mapped(malepatientsAbove40YearsWithcardiomyopathiesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithcardiomyopathies = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithcardiomyopathies.setName("femalepatientsAbove40YearsWithcardiomyopathies");
        femalepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithcardiomyopathies.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithcardiomyopathies.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithcardiomyopathies.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithcardiomyopathies.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithcardiomyopathiesIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithcardiomyopathiesIndicator",
                femalepatientsAbove40YearsWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.N.F.40", "New case cardiomyopathies Female above 40", new Mapped(femalepatientsAbove40YearsWithcardiomyopathiesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with cardiomyopathies

        SqlCohortDefinition oldcardiomyopathies=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptCardiomyophaties,combinedCardiomyophaties);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithcardiomyopathiesOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithcardiomyopathiesOldCasePatient.setName("maleBetween0And39WithcardiomyopathiesOldCasePatient");
        maleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithcardiomyopathiesOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithcardiomyopathiesOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithcardiomyopathiesOldCasePatientIndicator",
                maleBetween0And39WithcardiomyopathiesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.O.M.039", "Old case cardiomyopathies Male", new Mapped(maleBetween0And39WithcardiomyopathiesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithcardiomyopathiesOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.setName("femaleBetween0And39WithcardiomyopathiesOldCasePatient");
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithcardiomyopathiesOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithcardiomyopathiesOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithcardiomyopathiesOldCasePatientIndicator",
                femaleBetween0And39WithcardiomyopathiesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.O.F.039", "Old case cardiomyopathies Female", new Mapped(femaleBetween0And39WithcardiomyopathiesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithcardiomyopathies = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.setName("maleOldCasepatientsAbove40YearsWithcardiomyopathies");
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.getSearches().put("1", new Mapped<CohortDefinition>(oldcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithcardiomyopathies.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithcardiomyopathiesIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithcardiomyopathiesIndicator",
                maleOldCasepatientsAbove40YearsWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.O.M.40", "Old case cardiomyopathies Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithcardiomyopathiesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithcardiomyopathies = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.setName("femaleOldCasepatientsAbove40YearsWithcardiomyopathies");
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.getSearches().put("1", new Mapped<CohortDefinition>(oldcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithcardiomyopathies.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithcardiomyopathiesIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithcardiomyopathiesIndicator",
                femaleOldCasepatientsAbove40YearsWithcardiomyopathies, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("C.O.F.40", "Old case cardiomyopathies Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithcardiomyopathiesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New Patients with Pericardial Disease

        SqlCohortDefinition newPatientWithpericardialDisease= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptPericardialDisease,combinedPericardialDisease);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithpericardialDisease = new CompositionCohortDefinition();
        maleBetween0And39WithpericardialDisease.setName("maleBetween0And39WithpericardialDisease");
        maleBetween0And39WithpericardialDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithpericardialDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithpericardialDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithpericardialDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithpericardialDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithpericardialDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithpericardialDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithpericardialDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithpericardialDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithpericardialDiseaseIndicator = Indicators.newCohortIndicator("maleBetween0And39WithpericardialDiseaseIndicator",
                maleBetween0And39WithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.N.M.039", "New Case patients with pericardialDisease Male", new Mapped(maleBetween0And39WithpericardialDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithpericardialDisease = new CompositionCohortDefinition();
        femaleBetween0And39WithpericardialDisease.setName("femaleBetween0And39WithpericardialDisease");
        femaleBetween0And39WithpericardialDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithpericardialDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithpericardialDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithpericardialDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithpericardialDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithpericardialDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithpericardialDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithpericardialDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithpericardialDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithpericardialDiseaseIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithpericardialDiseaseIndicator",
                femaleBetween0And39WithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.N.F.039", "New case pericardialDisease Female", new Mapped(femaleBetween0And39WithpericardialDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithpericardialDisease = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithpericardialDisease.setName("malepatientsAbove40YearsWithpericardialDisease");
        malepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithpericardialDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithpericardialDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithpericardialDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithpericardialDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithpericardialDiseaseIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithpericardialDiseaseIndicator",
                malepatientsAbove40YearsWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.N.M.40", "New case pericardialDisease Male above 40", new Mapped(malepatientsAbove40YearsWithpericardialDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithpericardialDisease = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithpericardialDisease.setName("femalepatientsAbove40YearsWithpericardialDisease");
        femalepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithpericardialDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithpericardialDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithpericardialDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithpericardialDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithpericardialDiseaseIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithpericardialDiseaseIndicator",
                femalepatientsAbove40YearsWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.N.F.40", "New case pericardialDisease Female above 40", new Mapped(femalepatientsAbove40YearsWithpericardialDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with pericardialDisease

        SqlCohortDefinition oldpericardialDisease=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptPericardialDisease,combinedPericardialDisease);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithpericardialDiseaseOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithpericardialDiseaseOldCasePatient.setName("maleBetween0And39WithpericardialDiseaseOldCasePatient");
        maleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithpericardialDiseaseOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithpericardialDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithpericardialDiseaseOldCasePatientIndicator",
                maleBetween0And39WithpericardialDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.O.M.039", "Old case pericardialDisease Male", new Mapped(maleBetween0And39WithpericardialDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithpericardialDiseaseOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.setName("femaleBetween0And39WithpericardialDiseaseOldCasePatient");
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithpericardialDiseaseOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithpericardialDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithpericardialDiseaseOldCasePatientIndicator",
                femaleBetween0And39WithpericardialDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.O.F.039", "Old case pericardialDisease Female", new Mapped(femaleBetween0And39WithpericardialDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithpericardialDisease = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithpericardialDisease.setName("maleOldCasepatientsAbove40YearsWithpericardialDisease");
        maleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithpericardialDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithpericardialDiseaseIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithpericardialDiseaseIndicator",
                maleOldCasepatientsAbove40YearsWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.O.M.40", "Old case pericardialDisease Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithpericardialDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithpericardialDisease = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.setName("femaleOldCasepatientsAbove40YearsWithpericardialDisease");
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithpericardialDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithpericardialDiseaseIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithpericardialDiseaseIndicator",
                femaleOldCasepatientsAbove40YearsWithpericardialDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("PD.O.F.40", "Old case pericardialDisease Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithpericardialDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New patients with Heart failure

        SqlCohortDefinition newPatientWithHeartFailure= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptHeartFailure,combinedHeartFailure);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithHeartFailure = new CompositionCohortDefinition();
        maleBetween0And39WithHeartFailure.setName("maleBetween0And39WithHeartFailure");
        maleBetween0And39WithHeartFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithHeartFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithHeartFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithHeartFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithHeartFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithHeartFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithHeartFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithHeartFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithHeartFailure.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithHeartFailureIndicator = Indicators.newCohortIndicator("maleBetween0And39WithHeartFailureIndicator",
                maleBetween0And39WithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.N.M.039", "New Case patients with heartFailure Male", new Mapped(maleBetween0And39WithHeartFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithHeartFailure = new CompositionCohortDefinition();
        femaleBetween0And39WithHeartFailure.setName("femaleBetween0And39WithHeartFailure");
        femaleBetween0And39WithHeartFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithHeartFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithHeartFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithHeartFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithHeartFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithHeartFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithHeartFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithHeartFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithHeartFailure.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithHeartFailureIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithHeartFailureIndicator",
                femaleBetween0And39WithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.N.F.039", "New case heartFailure Female", new Mapped(femaleBetween0And39WithHeartFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithHeartFailure = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithHeartFailure.setName("malepatientsAbove40YearsWithHeartFailure");
        malepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithHeartFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithHeartFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithHeartFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithHeartFailure.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithHeartFailureIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithHeartFailureIndicator",
                malepatientsAbove40YearsWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.N.M.40", "New case heartFailure Male above 40", new Mapped(malepatientsAbove40YearsWithHeartFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithHeartFailure = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithHeartFailure.setName("femalepatientsAbove40YearsWithHeartFailure");
        femalepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithHeartFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithHeartFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithHeartFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithHeartFailure.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithHeartFailureIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithHeartFailureIndicator",
                femalepatientsAbove40YearsWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.N.F.40", "New case heartFailure Female above 40", new Mapped(femalepatientsAbove40YearsWithHeartFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with heartFailure

        SqlCohortDefinition oldheartFailure=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptHeartFailure,combinedHeartFailure);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithHeartFailureOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithHeartFailureOldCasePatient.setName("maleBetween0And39WithHeartFailureOldCasePatient");
        maleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithHeartFailureOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldheartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithHeartFailureOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithHeartFailureOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithHeartFailureOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithHeartFailureOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithHeartFailureOldCasePatientIndicator",
                maleBetween0And39WithHeartFailureOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.O.M.039", "Old case heartFailure Male", new Mapped(maleBetween0And39WithHeartFailureOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithHeartFailureOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithHeartFailureOldCasePatient.setName("femaleBetween0And39WithHeartFailureOldCasePatient");
        femaleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithHeartFailureOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithHeartFailureOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldheartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithHeartFailureOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithHeartFailureOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithHeartFailureOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithHeartFailureOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithHeartFailureOldCasePatientIndicator",
                femaleBetween0And39WithHeartFailureOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.O.F.039", "Old case heartFailure Female", new Mapped(femaleBetween0And39WithHeartFailureOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithHeartFailure = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithHeartFailure.setName("maleOldCasepatientsAbove40YearsWithHeartFailure");
        maleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithHeartFailure.getSearches().put("1", new Mapped<CohortDefinition>(oldheartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithHeartFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithHeartFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithHeartFailure.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithHeartFailureIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithHeartFailureIndicator",
                maleOldCasepatientsAbove40YearsWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.O.M.40", "Old case heartFailure Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithHeartFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithHeartFailure = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithHeartFailure.setName("femaleOldCasepatientsAbove40YearsWithHeartFailure");
        femaleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.getSearches().put("1", new Mapped<CohortDefinition>(oldheartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithHeartFailure.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithHeartFailureIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithHeartFailureIndicator",
                femaleOldCasepatientsAbove40YearsWithHeartFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("HF.O.F.40", "Old case heartFailure Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithHeartFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // New patients with Stroke

        SqlCohortDefinition newPatientWithStroke= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptStroke,combinedStroke);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithStroke = new CompositionCohortDefinition();
        maleBetween0And39WithStroke.setName("maleBetween0And39WithStroke");
        maleBetween0And39WithStroke.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithStroke.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithStroke.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithStroke.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithStroke.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithStroke.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithStroke.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithStroke.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithStroke.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithStrokeIndicator = Indicators.newCohortIndicator("maleBetween0And39WithStrokeIndicator",
                maleBetween0And39WithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.N.M.039", "New Case patients with Stroke Male", new Mapped(maleBetween0And39WithStrokeIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithStroke = new CompositionCohortDefinition();
        femaleBetween0And39WithStroke.setName("femaleBetween0And39WithStroke");
        femaleBetween0And39WithStroke.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithStroke.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithStroke.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithStroke.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithStroke.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithStroke.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithStroke.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithStroke.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithStroke.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithStrokeIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithStrokeIndicator",
                femaleBetween0And39WithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.N.F.039", "New case Stroke Female", new Mapped(femaleBetween0And39WithStrokeIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithStroke = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithStroke.setName("malepatientsAbove40YearsWithStroke");
        malepatientsAbove40YearsWithStroke.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithStroke.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithStroke.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithStroke.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithStroke.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithStroke.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithStroke.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithStrokeIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithStrokeIndicator",
                malepatientsAbove40YearsWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.N.M.40", "New case Stroke Male above 40", new Mapped(malepatientsAbove40YearsWithStrokeIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithStroke = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithStroke.setName("femalepatientsAbove40YearsWithStroke");
        femalepatientsAbove40YearsWithStroke.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithStroke.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithStroke.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithStroke.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithStroke.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithStroke.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithStroke.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithStrokeIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithStrokeIndicator",
                femalepatientsAbove40YearsWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.N.F.40", "New case Stroke Female above 40", new Mapped(femalepatientsAbove40YearsWithStrokeIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with Stroke

        SqlCohortDefinition oldStroke=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptStroke,combinedStroke);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithStrokeOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithStrokeOldCasePatient.setName("maleBetween0And39WithStrokeOldCasePatient");
        maleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithStrokeOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithStrokeOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithStrokeOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithStrokeOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithStrokeOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithStrokeOldCasePatientIndicator",
                maleBetween0And39WithStrokeOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.O.M.039", "Old case Stroke Male", new Mapped(maleBetween0And39WithStrokeOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithStrokeOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithStrokeOldCasePatient.setName("femaleBetween0And39WithStrokeOldCasePatient");
        femaleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithStrokeOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithStrokeOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithStrokeOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithStrokeOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithStrokeOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithStrokeOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithStrokeOldCasePatientIndicator",
                femaleBetween0And39WithStrokeOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.O.F.039", "Old case Stroke Female", new Mapped(femaleBetween0And39WithStrokeOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithStroke = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithStroke.setName("maleOldCasepatientsAbove40YearsWithStroke");
        maleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithStroke.getSearches().put("1", new Mapped<CohortDefinition>(oldStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithStroke.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithStroke.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithStroke.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithStrokeIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithStrokeIndicator",
                maleOldCasepatientsAbove40YearsWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.O.M.40", "Old case Stroke Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithStrokeIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithStroke = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithStroke.setName("femaleOldCasepatientsAbove40YearsWithStroke");
        femaleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithStroke.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithStroke.getSearches().put("1", new Mapped<CohortDefinition>(oldStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithStroke.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithStroke.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithStroke.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithStrokeIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithStrokeIndicator",
                femaleOldCasepatientsAbove40YearsWithStroke, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("ST.O.F.40", "Old case Stroke Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithStrokeIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New patients with Rheumatic Heart Disease
        SqlCohortDefinition newPatientWithRheumaticHeartDisease= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptRheumaticHeart,combinedRheumaticHeart);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithRheumaticHeartDisease = new CompositionCohortDefinition();
        maleBetween0And39WithRheumaticHeartDisease.setName("maleBetween0And39WithRheumaticHeartDisease");
        maleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithRheumaticHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithRheumaticHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithRheumaticHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithRheumaticHeartDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithRheumaticHeartDiseaseIndicator = Indicators.newCohortIndicator("maleBetween0And39WithRheumaticHeartDiseaseIndicator",
                maleBetween0And39WithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.N.M.039", "New Case patients with RheumaticHeartDisease Male", new Mapped(maleBetween0And39WithRheumaticHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithRheumaticHeartDisease = new CompositionCohortDefinition();
        femaleBetween0And39WithRheumaticHeartDisease.setName("femaleBetween0And39WithRheumaticHeartDisease");
        femaleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithRheumaticHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithRheumaticHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithRheumaticHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithRheumaticHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithRheumaticHeartDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithRheumaticHeartDiseaseIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithRheumaticHeartDiseaseIndicator",
                femaleBetween0And39WithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.N.F.039", "New case RheumaticHeartDisease Female", new Mapped(femaleBetween0And39WithRheumaticHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithRheumaticHeartDisease = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithRheumaticHeartDisease.setName("malepatientsAbove40YearsWithRheumaticHeartDisease");
        malepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithRheumaticHeartDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator",
                malepatientsAbove40YearsWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.N.M.40", "New case RheumaticHeartDisease Male above 40", new Mapped(malepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithRheumaticHeartDisease = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithRheumaticHeartDisease.setName("femalepatientsAbove40YearsWithRheumaticHeartDisease");
        femalepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithRheumaticHeartDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator",
                femalepatientsAbove40YearsWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.N.F.40", "New case RheumaticHeartDisease Female above 40", new Mapped(femalepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with RheumaticHeartDisease

        SqlCohortDefinition oldRheumaticHeartDisease=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptRheumaticHeart,combinedRheumaticHeart);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.setName("maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient");
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithRheumaticHeartDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithRheumaticHeartDiseaseOldCasePatientIndicator",
                maleBetween0And39WithRheumaticHeartDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.O.M.039", "Old case RheumaticHeartDisease Male", new Mapped(maleBetween0And39WithRheumaticHeartDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.setName("femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient");
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatientIndicator",
                femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.O.F.039", "Old case RheumaticHeartDisease Female", new Mapped(femaleBetween0And39WithRheumaticHeartDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.setName("maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease");
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator",
                maleOldCasepatientsAbove40YearsWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.O.M.40", "Old case RheumaticHeartDisease Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.setName("femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease");
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator",
                femaleOldCasepatientsAbove40YearsWithRheumaticHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RH.O.F.40", "Old case RheumaticHeartDisease Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithRheumaticHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New patients with congenital heart diseases

        SqlCohortDefinition newPatientWithCongenitalHeartDisease= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptCongenitalHeart,combinedCongenitalHeart);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithCongenitalHeartDisease = new CompositionCohortDefinition();
        maleBetween0And39WithCongenitalHeartDisease.setName("maleBetween0And39WithCongenitalHeartDisease");
        maleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithCongenitalHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithCongenitalHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithCongenitalHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithCongenitalHeartDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithCongenitalHeartDiseaseIndicator = Indicators.newCohortIndicator("maleBetween0And39WithCongenitalHeartDiseaseIndicator",
                maleBetween0And39WithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.N.M.039", "New Case patients with CongenitalHeartDisease Male", new Mapped(maleBetween0And39WithCongenitalHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithCongenitalHeartDisease = new CompositionCohortDefinition();
        femaleBetween0And39WithCongenitalHeartDisease.setName("femaleBetween0And39WithCongenitalHeartDisease");
        femaleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithCongenitalHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithCongenitalHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithCongenitalHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithCongenitalHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithCongenitalHeartDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithCongenitalHeartDiseaseIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithCongenitalHeartDiseaseIndicator",
                femaleBetween0And39WithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.N.F.039", "New case CongenitalHeartDisease Female", new Mapped(femaleBetween0And39WithCongenitalHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithCongenitalHeartDisease = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithCongenitalHeartDisease.setName("malepatientsAbove40YearsWithCongenitalHeartDisease");
        malepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithCongenitalHeartDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator",
                malepatientsAbove40YearsWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.N.M.40", "New case CongenitalHeartDisease Male above 40", new Mapped(malepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithCongenitalHeartDisease = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithCongenitalHeartDisease.setName("femalepatientsAbove40YearsWithCongenitalHeartDisease");
        femalepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithCongenitalHeartDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator",
                femalepatientsAbove40YearsWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.N.F.40", "New case CongenitalHeartDisease Female above 40", new Mapped(femalepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with CongenitalHeartDisease

        SqlCohortDefinition oldCongenitalHeartDisease=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptCongenitalHeart,combinedCongenitalHeart);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.setName("maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient");
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithCongenitalHeartDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithCongenitalHeartDiseaseOldCasePatientIndicator",
                maleBetween0And39WithCongenitalHeartDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.O.M.039", "Old case CongenitalHeartDisease Male", new Mapped(maleBetween0And39WithCongenitalHeartDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.setName("femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient");
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatientIndicator",
                femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.O.F.039", "Old case CongenitalHeartDisease Female", new Mapped(femaleBetween0And39WithCongenitalHeartDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.setName("maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease");
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator",
                maleOldCasepatientsAbove40YearsWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.O.M.40", "Old case CongenitalHeartDisease Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.setName("femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease");
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator",
                femaleOldCasepatientsAbove40YearsWithCongenitalHeartDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CH.O.F.40", "Old case CongenitalHeartDisease Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithCongenitalHeartDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New patient with Other Cardiovascular diseases

        SqlCohortDefinition newPatientWithOtherCardiovasculardiseases= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptOtherCardiovasculardiseases,combinedOtherCardiovasculardiseases);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithOtherCardiovasculardiseases = new CompositionCohortDefinition();
        maleBetween0And39WithOtherCardiovasculardiseases.setName("maleBetween0And39WithOtherCardiovasculardiseases");
        maleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseases.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithOtherCardiovasculardiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithOtherCardiovasculardiseases.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithOtherCardiovasculardiseases.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithOtherCardiovasculardiseasesIndicator = Indicators.newCohortIndicator("maleBetween0And39WithOtherCardiovasculardiseasesIndicator",
                maleBetween0And39WithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.N.M.039", "New Case patients with OtherCardiovasculardiseases Male", new Mapped(maleBetween0And39WithOtherCardiovasculardiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithOtherCardiovasculardiseases = new CompositionCohortDefinition();
        femaleBetween0And39WithOtherCardiovasculardiseases.setName("femaleBetween0And39WithOtherCardiovasculardiseases");
        femaleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseases.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithOtherCardiovasculardiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithOtherCardiovasculardiseases.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithOtherCardiovasculardiseases.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithOtherCardiovasculardiseasesIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithOtherCardiovasculardiseasesIndicator",
                femaleBetween0And39WithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.N.F.039", "New case OtherCardiovasculardiseases Female", new Mapped(femaleBetween0And39WithOtherCardiovasculardiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithOtherCardiovasculardiseases = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.setName("malepatientsAbove40YearsWithOtherCardiovasculardiseases");
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithOtherCardiovasculardiseases.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator",
                malepatientsAbove40YearsWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.N.M.40", "New case OtherCardiovasculardiseases Male above 40", new Mapped(malepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithOtherCardiovasculardiseases = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.setName("femalepatientsAbove40YearsWithOtherCardiovasculardiseases");
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithOtherCardiovasculardiseases.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator",
                femalepatientsAbove40YearsWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.N.F.40", "New case OtherCardiovasculardiseases Female above 40", new Mapped(femalepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with OtherCardiovasculardiseases

        SqlCohortDefinition oldOtherCardiovasculardiseases=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptOtherCardiovasculardiseases,combinedOtherCardiovasculardiseases);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.setName("maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient");
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatientIndicator",
                maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.O.M.039", "Old case OtherCardiovasculardiseases Male", new Mapped(maleBetween0And39WithOtherCardiovasculardiseasesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.setName("femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient");
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatientIndicator",
                femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.O.F.039", "Old case OtherCardiovasculardiseases Female", new Mapped(femaleBetween0And39WithOtherCardiovasculardiseasesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.setName("maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases");
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator",
                maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.O.M.40", "Old case OtherCardiovasculardiseases Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.setName("femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases");
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator",
                femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OC.O.F.40", "Old case OtherCardiovasculardiseases Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithOtherCardiovasculardiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New patients with Deep Vein Thrombosis
        SqlCohortDefinition newPatientWithDeepVeinThrombosis= newPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,icd11Deepveinusthrombosis);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithDeepVeinThrombosis = new CompositionCohortDefinition();
        maleBetween0And39WithDeepVeinThrombosis.setName("maleBetween0And39WithDeepVeinThrombosis");
        maleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithDeepVeinThrombosis.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithDeepVeinThrombosis.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithDeepVeinThrombosis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithDeepVeinThrombosis.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithDeepVeinThrombosisIndicator = Indicators.newCohortIndicator("maleBetween0And39WithDeepVeinThrombosisIndicator",
                maleBetween0And39WithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.N.M.039", "New Case patients with DeepVeinThrombosis Male", new Mapped(maleBetween0And39WithDeepVeinThrombosisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithDeepVeinThrombosis = new CompositionCohortDefinition();
        femaleBetween0And39WithDeepVeinThrombosis.setName("femaleBetween0And39WithDeepVeinThrombosis");
        femaleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithDeepVeinThrombosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithDeepVeinThrombosis.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithDeepVeinThrombosis.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithDeepVeinThrombosis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithDeepVeinThrombosis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithDeepVeinThrombosisIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithDeepVeinThrombosisIndicator",
                femaleBetween0And39WithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.N.F.039", "New case DeepVeinThrombosis Female", new Mapped(femaleBetween0And39WithDeepVeinThrombosisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithDeepVeinThrombosis = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithDeepVeinThrombosis.setName("malepatientsAbove40YearsWithDeepVeinThrombosis");
        malepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithDeepVeinThrombosis.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithDeepVeinThrombosisIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDeepVeinThrombosisIndicator",
                malepatientsAbove40YearsWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.N.M.40", "New case DeepVeinThrombosis Male above 40", new Mapped(malepatientsAbove40YearsWithDeepVeinThrombosisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithDeepVeinThrombosis = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDeepVeinThrombosis.setName("femalepatientsAbove40YearsWithDeepVeinThrombosis");
        femalepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithDeepVeinThrombosis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDeepVeinThrombosisIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDeepVeinThrombosisIndicator",
                femalepatientsAbove40YearsWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.N.F.40", "New case DeepVeinThrombosis Female above 40", new Mapped(femalepatientsAbove40YearsWithDeepVeinThrombosisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with DeepVeinThrombosis

        SqlCohortDefinition oldDeepVeinThrombosis=oldPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,icd11Deepveinusthrombosis);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithDeepVeinThrombosisOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.setName("maleBetween0And39WithDeepVeinThrombosisOldCasePatient");
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithDeepVeinThrombosisOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithDeepVeinThrombosisOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithDeepVeinThrombosisOldCasePatientIndicator",
                maleBetween0And39WithDeepVeinThrombosisOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.O.M.039", "Old case DeepVeinThrombosis Male", new Mapped(maleBetween0And39WithDeepVeinThrombosisOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithDeepVeinThrombosisOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.setName("femaleBetween0And39WithDeepVeinThrombosisOldCasePatient");
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithDeepVeinThrombosisOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithDeepVeinThrombosisOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithDeepVeinThrombosisOldCasePatientIndicator",
                femaleBetween0And39WithDeepVeinThrombosisOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.O.F.039", "Old case DeepVeinThrombosis Female", new Mapped(femaleBetween0And39WithDeepVeinThrombosisOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.setName("maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis");
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("1", new Mapped<CohortDefinition>(oldDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithDeepVeinThrombosisIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDeepVeinThrombosisIndicator",
                maleOldCasepatientsAbove40YearsWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.O.M.40", "Old case DeepVeinThrombosis Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDeepVeinThrombosisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.setName("femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis");
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("1", new Mapped<CohortDefinition>(oldDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosisIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosisIndicator",
                femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("DVT.O.F.40", "Old case DeepVeinThrombosis Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDeepVeinThrombosisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New patients with Renal failure

        SqlCohortDefinition newPatientWithRenalFailure= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptRenalFailure,combinedRenalFailure);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithRenalFailure = new CompositionCohortDefinition();
        maleBetween0And39WithRenalFailure.setName("maleBetween0And39WithRenalFailure");
        maleBetween0And39WithRenalFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithRenalFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithRenalFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithRenalFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithRenalFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithRenalFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithRenalFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithRenalFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithRenalFailure.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithRenalFailureIndicator = Indicators.newCohortIndicator("maleBetween0And39WithRenalFailureIndicator",
                maleBetween0And39WithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.N.M.039", "New Case patients with RenalFailure Male", new Mapped(maleBetween0And39WithRenalFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithRenalFailure = new CompositionCohortDefinition();
        femaleBetween0And39WithRenalFailure.setName("femaleBetween0And39WithRenalFailure");
        femaleBetween0And39WithRenalFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithRenalFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithRenalFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithRenalFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithRenalFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithRenalFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithRenalFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithRenalFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithRenalFailure.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithRenalFailureIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithRenalFailureIndicator",
                femaleBetween0And39WithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.N.F.039", "New case RenalFailure Female", new Mapped(femaleBetween0And39WithRenalFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithRenalFailure = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithRenalFailure.setName("malepatientsAbove40YearsWithRenalFailure");
        malepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithRenalFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithRenalFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithRenalFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithRenalFailure.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithRenalFailureIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithRenalFailureIndicator",
                malepatientsAbove40YearsWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.N.M.40", "New case RenalFailure Male above 40", new Mapped(malepatientsAbove40YearsWithRenalFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithRenalFailure = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithRenalFailure.setName("femalepatientsAbove40YearsWithRenalFailure");
        femalepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithRenalFailure.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithRenalFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithRenalFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithRenalFailure.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithRenalFailureIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithRenalFailureIndicator",
                femalepatientsAbove40YearsWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.N.F.40", "New case RenalFailure Female above 40", new Mapped(femalepatientsAbove40YearsWithRenalFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with RenalFailure

        SqlCohortDefinition oldRenalFailure=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptRenalFailure,combinedRenalFailure);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithRenalFailureOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithRenalFailureOldCasePatient.setName("maleBetween0And39WithRenalFailureOldCasePatient");
        maleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithRenalFailureOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithRenalFailureOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithRenalFailureOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithRenalFailureOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithRenalFailureOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithRenalFailureOldCasePatientIndicator",
                maleBetween0And39WithRenalFailureOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.O.M.039", "Old case RenalFailure Male", new Mapped(maleBetween0And39WithRenalFailureOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithRenalFailureOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithRenalFailureOldCasePatient.setName("femaleBetween0And39WithRenalFailureOldCasePatient");
        femaleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithRenalFailureOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithRenalFailureOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithRenalFailureOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithRenalFailureOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithRenalFailureOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithRenalFailureOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithRenalFailureOldCasePatientIndicator",
                femaleBetween0And39WithRenalFailureOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.O.F.039", "Old case RenalFailure Female", new Mapped(femaleBetween0And39WithRenalFailureOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithRenalFailure = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithRenalFailure.setName("maleOldCasepatientsAbove40YearsWithRenalFailure");
        maleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithRenalFailure.getSearches().put("1", new Mapped<CohortDefinition>(oldRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithRenalFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithRenalFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithRenalFailure.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithRenalFailureIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithRenalFailureIndicator",
                maleOldCasepatientsAbove40YearsWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.O.M.40", "Old case RenalFailure Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithRenalFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithRenalFailure = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithRenalFailure.setName("femaleOldCasepatientsAbove40YearsWithRenalFailure");
        femaleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.getSearches().put("1", new Mapped<CohortDefinition>(oldRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithRenalFailure.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithRenalFailureIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithRenalFailureIndicator",
                femaleOldCasepatientsAbove40YearsWithRenalFailure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("RF.O.F.40", "Old case RenalFailure Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithRenalFailureIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // New patients with OtherChronicKidney

        SqlCohortDefinition newPatientWithOtherChronicKidney= newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(conceptOtherChronicKidneyDisease,combinedOtherChronicKidneyDisease);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithOtherChronicKidney = new CompositionCohortDefinition();
        maleBetween0And39WithOtherChronicKidney.setName("maleBetween0And39WithOtherChronicKidney");
        maleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithOtherChronicKidney.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithOtherChronicKidney.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithOtherChronicKidney.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithOtherChronicKidney.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithOtherChronicKidneyIndicator = Indicators.newCohortIndicator("maleBetween0And39WithOtherChronicKidneyIndicator",
                maleBetween0And39WithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.N.M.039", "New Case patients with OtherChronicKidney Male", new Mapped(maleBetween0And39WithOtherChronicKidneyIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithOtherChronicKidney = new CompositionCohortDefinition();
        femaleBetween0And39WithOtherChronicKidney.setName("femaleBetween0And39WithOtherChronicKidney");
        femaleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithOtherChronicKidney.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithOtherChronicKidney.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithOtherChronicKidney.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithOtherChronicKidney.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithOtherChronicKidney.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithOtherChronicKidneyIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithOtherChronicKidneyIndicator",
                femaleBetween0And39WithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.N.F.039", "New case OtherChronicKidney Female", new Mapped(femaleBetween0And39WithOtherChronicKidneyIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithOtherChronicKidney = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithOtherChronicKidney.setName("malepatientsAbove40YearsWithOtherChronicKidney");
        malepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithOtherChronicKidney.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithOtherChronicKidneyIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithOtherChronicKidneyIndicator",
                malepatientsAbove40YearsWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.N.M.40", "New case OtherChronicKidney Male above 40", new Mapped(malepatientsAbove40YearsWithOtherChronicKidneyIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithOtherChronicKidney = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithOtherChronicKidney.setName("femalepatientsAbove40YearsWithOtherChronicKidney");
        femalepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithOtherChronicKidney.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithOtherChronicKidneyIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithOtherChronicKidneyIndicator",
                femalepatientsAbove40YearsWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.N.F.40", "New case OtherChronicKidney Female above 40", new Mapped(femalepatientsAbove40YearsWithOtherChronicKidneyIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with OtherChronicKidney

        SqlCohortDefinition oldOtherChronicKidney=oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(conceptOtherChronicKidneyDisease,combinedOtherChronicKidneyDisease);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithOtherChronicKidneyOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.setName("maleBetween0And39WithOtherChronicKidneyOldCasePatient");
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithOtherChronicKidneyOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithOtherChronicKidneyOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithOtherChronicKidneyOldCasePatientIndicator",
                maleBetween0And39WithOtherChronicKidneyOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.O.M.039", "Old case OtherChronicKidney Male", new Mapped(maleBetween0And39WithOtherChronicKidneyOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithOtherChronicKidneyOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.setName("femaleBetween0And39WithOtherChronicKidneyOldCasePatient");
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithOtherChronicKidneyOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithOtherChronicKidneyOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithOtherChronicKidneyOldCasePatientIndicator",
                femaleBetween0And39WithOtherChronicKidneyOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.O.F.039", "Old case OtherChronicKidney Female", new Mapped(femaleBetween0And39WithOtherChronicKidneyOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithOtherChronicKidney = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.setName("maleOldCasepatientsAbove40YearsWithOtherChronicKidney");
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithOtherChronicKidney.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithOtherChronicKidneyIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithOtherChronicKidneyIndicator",
                maleOldCasepatientsAbove40YearsWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.O.M.40", "Old case OtherChronicKidney Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithOtherChronicKidneyIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithOtherChronicKidney = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.setName("femaleOldCasepatientsAbove40YearsWithOtherChronicKidney");
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithOtherChronicKidney.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithOtherChronicKidneyIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithOtherChronicKidneyIndicator",
                femaleOldCasepatientsAbove40YearsWithOtherChronicKidney, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("OCK.O.F.40", "Old case OtherChronicKidney Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithOtherChronicKidneyIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // New patients with MetabolicUnspecified

        SqlCohortDefinition newPatientWithMetabolicUnspecified= newPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,icd11OtherEndocrineAndMetabolicDisease);

// 0-39

        CompositionCohortDefinition maleBetween0And39WithMetabolicUnspecified = new CompositionCohortDefinition();
        maleBetween0And39WithMetabolicUnspecified.setName("maleBetween0And39WithMetabolicUnspecified");
        maleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithMetabolicUnspecified.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithMetabolicUnspecified.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithMetabolicUnspecified.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithMetabolicUnspecified.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithMetabolicUnspecifiedIndicator = Indicators.newCohortIndicator("maleBetween0And39WithMetabolicUnspecifiedIndicator",
                maleBetween0And39WithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.N.M.039", "New Case patients with MetabolicUnspecified Male", new Mapped(maleBetween0And39WithMetabolicUnspecifiedIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithMetabolicUnspecified = new CompositionCohortDefinition();
        femaleBetween0And39WithMetabolicUnspecified.setName("femaleBetween0And39WithMetabolicUnspecified");
        femaleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithMetabolicUnspecified.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithMetabolicUnspecified.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithMetabolicUnspecified.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithMetabolicUnspecified.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithMetabolicUnspecified.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithMetabolicUnspecifiedIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithMetabolicUnspecifiedIndicator",
                femaleBetween0And39WithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.N.F.039", "New case MetabolicUnspecified Female", new Mapped(femaleBetween0And39WithMetabolicUnspecifiedIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithMetabolicUnspecified = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithMetabolicUnspecified.setName("malepatientsAbove40YearsWithMetabolicUnspecified");
        malepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithMetabolicUnspecified.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithMetabolicUnspecifiedIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithMetabolicUnspecifiedIndicator",
                malepatientsAbove40YearsWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.N.M.40", "New case MetabolicUnspecified Male above 40", new Mapped(malepatientsAbove40YearsWithMetabolicUnspecifiedIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithMetabolicUnspecified = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithMetabolicUnspecified.setName("femalepatientsAbove40YearsWithMetabolicUnspecified");
        femalepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("1", new Mapped<CohortDefinition>(newPatientWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femalepatientsAbove40YearsWithMetabolicUnspecified.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithMetabolicUnspecifiedIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithMetabolicUnspecifiedIndicator",
                femalepatientsAbove40YearsWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.N.F.40", "New case MetabolicUnspecified Female above 40", new Mapped(femalepatientsAbove40YearsWithMetabolicUnspecifiedIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with MetabolicUnspecified

        SqlCohortDefinition oldMetabolicUnspecified=oldPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,icd11OtherEndocrineAndMetabolicDisease);


// 0-39

        CompositionCohortDefinition maleBetween0And39WithMetabolicUnspecifiedOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.setName("maleBetween0And39WithMetabolicUnspecifiedOldCasePatient");
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39WithMetabolicUnspecifiedOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39WithMetabolicUnspecifiedOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39WithMetabolicUnspecifiedOldCasePatientIndicator",
                maleBetween0And39WithMetabolicUnspecifiedOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.O.M.039", "Old case MetabolicUnspecified Male", new Mapped(maleBetween0And39WithMetabolicUnspecifiedOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.setName("femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient");
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39WithMetabolicUnspecifiedOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39WithMetabolicUnspecifiedOldCasePatientIndicator",
                femaleBetween0And39WithMetabolicUnspecifiedOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.O.F.039", "Old case MetabolicUnspecified Female", new Mapped(femaleBetween0And39WithMetabolicUnspecifiedOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithMetabolicUnspecified = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.setName("maleOldCasepatientsAbove40YearsWithMetabolicUnspecified");
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("1", new Mapped<CohortDefinition>(oldMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithMetabolicUnspecified.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithMetabolicUnspecifiedIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithMetabolicUnspecifiedIndicator",
                maleOldCasepatientsAbove40YearsWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.O.M.40", "Old case MetabolicUnspecified Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithMetabolicUnspecifiedIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.setName("femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified");
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("1", new Mapped<CohortDefinition>(oldMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithMetabolicUnspecifiedIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithMetabolicUnspecifiedIndicator",
                femaleOldCasepatientsAbove40YearsWithMetabolicUnspecified, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("MU.O.F.40", "Old case MetabolicUnspecified Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithMetabolicUnspecifiedIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // New case patient with CoronaryArteryDisease



        SqlCohortDefinition newCoronaryArteryDisease=newPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,coronaryArteryDisease);

// 0-39

        CompositionCohortDefinition maleBetween0And39CoronaryArteryDisease = new CompositionCohortDefinition();
        maleBetween0And39CoronaryArteryDisease.setName("maleBetween0And39CoronaryArteryDisease");
        maleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39CoronaryArteryDisease.getSearches().put("1", new Mapped<CohortDefinition>(newCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39CoronaryArteryDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39CoronaryArteryDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39CoronaryArteryDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39CoronaryArteryDiseaseIndicator = Indicators.newCohortIndicator("maleBetween0And39CoronaryArteryDiseaseIndicator",
                maleBetween0And39CoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.N.M.039", "New case CoronaryArteryDisease Male", new Mapped(maleBetween0And39CoronaryArteryDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39CoronaryArteryDisease = new CompositionCohortDefinition();
        femaleBetween0And39CoronaryArteryDisease.setName("femaleBetween0And39AsthmaIntermittant");
        femaleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39CoronaryArteryDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39CoronaryArteryDisease.getSearches().put("1", new Mapped<CohortDefinition>(newCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39CoronaryArteryDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39CoronaryArteryDisease.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femaleBetween0And39CoronaryArteryDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39CoronaryArteryDiseaseIndicator = Indicators.newCohortIndicator("femaleBetween0And39CoronaryArteryDiseaseIndicator",
                femaleBetween0And39CoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.N.F.039", "New case CoronaryArteryDisease Female", new Mapped(femaleBetween0And39CoronaryArteryDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithCoronaryArteryDisease = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithCoronaryArteryDisease.setName("malepatientsAbove40YearsWithCoronaryArteryDisease");
        malepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("1", new Mapped<CohortDefinition>(newCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        malepatientsAbove40YearsWithCoronaryArteryDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator",
                malepatientsAbove40YearsWithCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.N.M.40", "New case CoronaryArteryDisease Male above 40", new Mapped(malepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithCoronaryArteryDisease = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithCoronaryArteryDisease.setName("femalepatientsAbove40YearsWithCoronaryArteryDisease");
        femalepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("1", new Mapped<CohortDefinition>(newCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femalepatientsAbove40YearsWithCoronaryArteryDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator",
                femalepatientsAbove40YearsWithCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.N.F.40", "New case CoronaryArteryDisease Female above 40", new Mapped(femalepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 2.1 Old case patient with CoronaryArteryDisease

        SqlCohortDefinition oldCoronaryArteryDisease=oldPatientWithNCDDiagnosisObsByStartDateAndEndDate(ICD11Concepts,coronaryArteryDisease);


// 0-39

        CompositionCohortDefinition maleBetween0And39CoronaryArteryDiseaseOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.setName("maleBetween0And39CoronaryArteryDiseaseOldCasePatient");
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleBetween0And39CoronaryArteryDiseaseOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39CoronaryArteryDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39CoronaryArteryDiseaseOldCasePatientIndicator",
                maleBetween0And39CoronaryArteryDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.O.M.039", "Old case CoronaryArteryDisease Male", new Mapped(maleBetween0And39CoronaryArteryDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39CoronaryArteryDiseaseOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.setName("femaleBetween0And39AsthmaIntermittantOldCasePatient");
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femaleBetween0And39CoronaryArteryDiseaseOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39CoronaryArteryDiseaseOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39CoronaryArteryDiseaseOldCasePatientIndicator",
                femaleBetween0And39CoronaryArteryDiseaseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.O.F.039", "Old case CoronaryArteryDisease Female", new Mapped(femaleBetween0And39CoronaryArteryDiseaseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.setName("maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease");
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("3", new Mapped<CohortDefinition>(male, null));
        maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator",
                maleOldCasepatientsAbove40YearsWithCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.O.M.40", "Old case CoronaryArteryDisease Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.setName("femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease");
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("1", new Mapped<CohortDefinition>(oldCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.getSearches().put("3", new Mapped<CohortDefinition>(female, null));
        femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator",
                femaleOldCasepatientsAbove40YearsWithCoronaryArteryDisease, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("CAD.O.F.40", "Old case CoronaryArteryDisease Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithCoronaryArteryDiseaseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




    }

    private SqlCohortDefinition newPatientWithNCDDiagnosisObsByStartDateAndEndDate(Concept NcdDiagnosis, Concept NcdAnswer){

        SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o " +
                " JOIN patient_program pp on pp.patient_id=o.person_id" +
                " where o.voided= 0 and o.concept_id = "+NcdDiagnosis.getConceptId()+" " +
                " and o.value_coded = "+NcdAnswer.getConceptId()+" " +
                " and pp.voided=0 and pp.date_completed is null" +
                " and pp.date_enrolled>= :startDate " +
                " and pp.date_enrolled<= :endDate");
        patientWithIDCObs.setName("patientWithIDCObs");
        patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithIDCObs;

    }
    private SqlCohortDefinition newPatientWithNCDDiagnosisObsByStartDateAndEndDateHypertension(List<Concept> NcdDiagnosis){

        StringBuilder ncdDia = new StringBuilder();
        int y = 0;
        for (Concept c : NcdDiagnosis) {
            if (y > 0) {
                ncdDia.append(",");
            }
            ncdDia.append(c.getConceptId());

            y++;
        }
        StringBuilder qStr = new StringBuilder();
        SqlCohortDefinition patientWithNCDsObs=new SqlCohortDefinition();
        qStr.append("select o.person_id from obs o inner JOIN patient_program pp on pp.patient_id=o.person_id");
        qStr.append(" where o.voided= 0 and o.concept_id in (");
        qStr.append(ncdDia);
        qStr.append(") and pp.voided=0 and pp.date_completed is null and pp.date_enrolled>=:startDate and pp.date_enrolled<=:endDate");
        System.out.println("Queryyyyyy: "+qStr);
        patientWithNCDsObs.setQuery(qStr.toString());
        patientWithNCDsObs.setName("patientWithNCDsObs");
        patientWithNCDsObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithNCDsObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithNCDsObs;

    }
    private SqlCohortDefinition oldPatientWithNCDDiagnosisObsByStartDateAndEndDateHypertension(List<Concept> NcdDiagnosis, Program program){
        StringBuilder ncdDia = new StringBuilder();
        int y = 0;
        for (Concept c : NcdDiagnosis) {
            if (y > 0) {
                ncdDia.append(",");
            }
            ncdDia.append(c.getConceptId());

            y++;
        }
        StringBuilder qStr = new StringBuilder();
        SqlCohortDefinition patientWithNCDsObs=new SqlCohortDefinition();
        qStr.append("select o.person_id from obs o inner JOIN patient_program pp on pp.patient_id=o.person_id");
        qStr.append(" where o.voided= 0 and o.concept_id in (");
        qStr.append(ncdDia);
        qStr.append(") and pp.program_id="+program.getProgramId()+" ");
        qStr.append("and pp.voided=0 and pp.date_completed is null and pp.date_enrolled<:startDate");
        System.out.println("Queryyyyyy: "+qStr);
        patientWithNCDsObs.setQuery(qStr.toString());
        patientWithNCDsObs.setName("patientWithNCDsObs");
        patientWithNCDsObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithNCDsObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithNCDsObs;

    }
    private SqlCohortDefinition oldPatientWithNCDDiagnosisObsByStartDateAndEndDate(Concept NcdDiagnosis, Concept NcdAnswer){

        SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o " +
                " JOIN patient_program pp on pp.patient_id=o.person_id" +
                " where o.voided=0 and o.concept_id ="+NcdDiagnosis.getConceptId()+" " +
                " and o.value_coded="+NcdAnswer.getConceptId()+" " +
                " and pp.voided=0 and pp.date_completed is null" +
                " and pp.date_enrolled< :startDate");
        patientWithIDCObs.setName("patientWithIDCObs");
        patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithIDCObs;

    }

    private SqlCohortDefinition newPatientWithNCDsDiagnosisObsByStartDateAndEndDate(List<Concept> NcdDiagnosis, List<Concept> NcdAnswers){

        StringBuilder ncdDia = new StringBuilder();
        int y = 0;
        for (Concept c : NcdDiagnosis) {
            if (y > 0) {
                ncdDia.append(",");
            }
            ncdDia.append(c.getConceptId());

            y++;
        }

        StringBuilder ncdAns = new StringBuilder();
        int i = 0;
        for (Concept concept : NcdAnswers) {
            if (i > 0) {
                ncdAns.append(",");
            }
            ncdAns.append(concept.getConceptId());

            i++;
        }

        StringBuilder qStr = new StringBuilder();
        SqlCohortDefinition patientWithNCDsObs=new SqlCohortDefinition();
                qStr.append("select o.person_id from obs o inner JOIN patient_program pp on pp.patient_id=o.person_id");
                qStr.append(" where o.voided= 0 and o.concept_id in (");
                qStr.append(ncdDia);
                qStr.append(") and o.value_coded in (");
                qStr.append(ncdAns);
                qStr.append(") and pp.voided=0 and pp.date_completed is null and pp.date_enrolled>=:startDate and pp.date_enrolled<=:endDate");
                System.out.println("Queryyyyyy: "+qStr);
        patientWithNCDsObs.setQuery(qStr.toString());
        patientWithNCDsObs.setName("patientWithNCDsObs");
        patientWithNCDsObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithNCDsObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithNCDsObs;

    }
    private SqlCohortDefinition oldPatientWithNCDsDiagnosisObsByStartDateAndEndDate(List<Concept> NcdDiagnosis, List<Concept> NcdAnswers){


        StringBuilder ncdDia = new StringBuilder();
        int y = 0;
        for (Concept c : NcdDiagnosis) {
            if (y > 0) {
                ncdDia.append(",");
            }
            ncdDia.append(c.getConceptId());

            y++;
        }

        StringBuilder ncdAns = new StringBuilder();
        int i = 0;
        for (Concept c : NcdAnswers) {
            if (i > 0) {
                ncdAns.append(",");
            }
            ncdAns.append(c.getConceptId());

            i++;
        }

        StringBuilder qStr = new StringBuilder();
        SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition();
        qStr.append("select o.person_id from obs o JOIN patient_program pp on pp.patient_id=o.person_id");
        qStr.append(" where o.voided= 0 and o.concept_id in ( ");
        qStr.append(ncdDia);
        qStr.append(") and o.value_coded in (");
        qStr.append(ncdAns);
//        qStr.append(") and pp.program_id="+program.getProgramId()+" ");
        qStr.append(") and pp.voided=0 and pp.date_completed is null and pp.date_enrolled< :startDate group by person_id");

        patientWithIDCObs.setQuery(qStr.toString());
        patientWithIDCObs.setName("patientWithIDCObs");
        patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithIDCObs;

    }
    private SqlCohortDefinition oldPatientWithNCDsDiagnosisObsByStartDateAndEndDateWithOutProgram(List<Concept> NcdDiagnosis, List<Concept> NcdAnswers){


        StringBuilder ncdDia = new StringBuilder();
        int y = 0;
        for (Concept c : NcdDiagnosis) {
            if (y > 0) {
                ncdDia.append(",");
            }
            ncdDia.append(c.getConceptId());

            y++;
        }

        StringBuilder ncdAns = new StringBuilder();
        int i = 0;
        for (Concept c : NcdAnswers) {
            if (i > 0) {
                ncdAns.append(",");
            }
            ncdAns.append(c.getConceptId());

            i++;
        }

        StringBuilder qStr = new StringBuilder();
        SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition();
        qStr.append("select o.person_id from obs o JOIN patient_program pp on pp.patient_id=o.person_id");
        qStr.append(" where o.voided= 0 and o.concept_id in ( ");
        qStr.append(ncdDia);
        qStr.append(") and o.value_coded in (");
        qStr.append(ncdAns);
        qStr.append(") and pp.voided=0 and pp.date_completed is null and pp.date_enrolled< :startDate group by person_id");

        patientWithIDCObs.setQuery(qStr.toString());
        patientWithIDCObs.setName("patientWithIDCObs");
        patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithIDCObs;

    }


}
