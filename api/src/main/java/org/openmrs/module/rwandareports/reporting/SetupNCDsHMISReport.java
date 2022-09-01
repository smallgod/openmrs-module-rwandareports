package org.openmrs.module.rwandareports.reporting;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.indicator.EncounterIndicator;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupNCDsHMISReport implements SetupReport{

    protected final Log log = LogFactory.getLog(getClass());

    private List<Form> OPDForms;
    private Concept caseStatus;
    private Concept newCase;
    private Concept oldCase;
    private int ICDConceptClassId;

    Properties properties = new Properties();
    private  List<String> onOrAfterOnOrBefore =new ArrayList<String>();


    public void setup() throws Exception {

        setUpProperties();

        properties.setProperty("hierarchyFields","countryDistrict:District");

        EncounterCohortDefinition patientWithOPDForm=Cohorts.createEncounterBasedOnForms("patientWithOPDForm",onOrAfterOnOrBefore,OPDForms);


        ReportDefinition monthlyRdIV = createReportDefinition("District Hospital Monthly HMIS Report - I. Chronic Diseases",properties);

        monthlyRdIV.setBaseCohortDefinition(patientWithOPDForm,
                ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

        monthlyRdIV.addDataSetDefinition(createCohortMonthlyLocationDataSet(),
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        Helper.saveReportDefinition(monthlyRdIV);

        ReportDesign monthlyDesignIV = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdIV,
                "District_Hospital_Monthly_NCDsHMIS_Report.xls", "District Hospital Monthly NCDs HMIS Report (Excel)", null);
        Properties monthlyPropsIV = new Properties();
        monthlyPropsIV.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Four");
        monthlyPropsIV.put("sortWeight","5000");
        monthlyDesignIV.setProperties(monthlyPropsIV);
        Helper.saveReportDesign(monthlyDesignIV);
    }

    @Override
    public void delete() {

        Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - I. Chronic Diseases");

    }

    private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Monthly Cohort Data Set");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
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

    public ReportDefinition createReportDefinition(String name, Properties properties){

        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        reportDefinition.addParameter(new Parameter("location", "Location", AllLocation.class));
        reportDefinition.setName(name);
        return reportDefinition;
    }

    // Setup Global Properties
    public void setUpProperties(){

        onOrAfterOnOrBefore.add("onOrAfter");

        onOrAfterOnOrBefore.add("onOrBefore");



        caseStatus=Context.getConceptService().getConceptByUuid("14183f94-59b2-4b62-bad7-2c788a21a422");

        newCase=Context.getConceptService().getConceptByUuid("f7b5bf49-cb07-4fca-8c15-93ba92249344");

        oldCase=Context.getConceptService().getConceptByUuid("ae5ba489-9be2-4960-8e44-8d09071ab8ca");

        ICDConceptClassId=Integer.parseInt(Context.getAdministrationService().getGlobalProperty("reports.ICD11ConceptClassId"));
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



        AgeCohortDefinition patientBetweenZeroAndThirtyNineYears = patientWithAgeBetween(0,39);

        GenderCohortDefinition males = new GenderCohortDefinition();
        males.setName("male Patients");
        males.setMaleIncluded(true);

        // AgeCohortDefinition patientBetween20And39Years = patientWithAgeBetween(20,39);

        AgeCohortDefinition patientAbove40Years = patientWithAgeAbove(40);





        // 1.  New case patient with Asthma/Asthme



        SqlCohortDefinition newAsthma=patientWithICDCodesObsByStartDateAndEndDate("CA23",caseStatus,newCase);

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
        maleBetween0And39withAsthma.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39withAsthma.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39AsthmaNewCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39AsthmaNewCasePatientIndicator",
                maleBetween0And39withAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "New Case Asthma Male", new Mapped(maleBetween0And39AsthmaNewCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39withAsthma = new CompositionCohortDefinition();
        femaleBetween0And39withAsthma.setName("femaleBetween0And39withAsthma");
        femaleBetween0And39withAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39withAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39withAsthma.getSearches().put("1", new Mapped<CohortDefinition>(newAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39withAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39withAsthma.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39withAsthma.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39withAsthmaIndicator = Indicators.newCohortIndicator("femaleBetween0And39withAsthmaIndicator",
                femaleBetween0And39withAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "New Case Asthma Female", new Mapped(femaleBetween0And39withAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

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
        malepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithAsthmaIndicator",
                malepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "New case Asthma Male above 40", new Mapped(malepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithAsthma = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithAsthma.setName("femalepatientsAbove40YearsWithAsthma");
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("1", new Mapped<CohortDefinition>(newAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithAsthmaIndicator",
                femalepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "New case Asthma Female above 40", new Mapped(femalepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 1.1 Old case patient with Asthma

        SqlCohortDefinition oldAsthma =patientWithICDCodesObsByStartDateAndEndDate("CA23",caseStatus,oldCase);


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
        maleBetween0And39withAsthmaOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39withAsthmaOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39AsthmaOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39AsthmaOldCasePatientIndicator",
                maleBetween0And39withAsthmaOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "Old case Asthma Male", new Mapped(maleBetween0And39AsthmaOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39withAsthmaOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39withAsthmaOldCasePatient.setName("femaleBetween0And39AsthmaOldCasePatient");
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39withAsthmaOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39withAsthmaOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39withAsthmaOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39withAsthmaOldCasePatientIndicator",
                femaleBetween0And39withAsthmaOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "Old case Asthma Female", new Mapped(femaleBetween0And39withAsthmaOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

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
        maleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleOldCasepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithAsthmaIndicator",
                maleOldCasepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "Old case Asthma Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithAsthma = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithAsthma.setName("femaleOldCasepatientsAbove40YearsWithAsthma");
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("1", new Mapped<CohortDefinition>(oldAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithAsthma.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleOldCasepatientsAbove40YearsWithAsthma.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithAsthmaIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithAsthmaIndicator",
                femaleOldCasepatientsAbove40YearsWithAsthma, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "Old case Asthma Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithAsthmaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");





        // 2.  New case patient with Bronchitis/Bronchite



        SqlCohortDefinition newBronchitis=patientWithICDCodeObsByStartDateAndEndDate("CA20",caseStatus,newCase);

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
        maleBetween0And39Bronchitis.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39Bronchitis.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39BronchitisIndicator = Indicators.newCohortIndicator("maleBetween0And39BronchitisIndicator",
                maleBetween0And39Bronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "New case Bronchitis Male", new Mapped(maleBetween0And39BronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39Bronchitis = new CompositionCohortDefinition();
        femaleBetween0And39Bronchitis.setName("femaleBetween0And39AsthmaIntermittant");
        femaleBetween0And39Bronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39Bronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39Bronchitis.getSearches().put("1", new Mapped<CohortDefinition>(newBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39Bronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39Bronchitis.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39Bronchitis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39BronchitisIndicator = Indicators.newCohortIndicator("femaleBetween0And39BronchitisIndicator",
                femaleBetween0And39Bronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "New case Bronchitis Female", new Mapped(femaleBetween0And39BronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

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
        malepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithBronchitisIndicator",
                malepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "New case Bronchitis Male above 40", new Mapped(malepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithBronchitis = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithBronchitis.setName("femalepatientsAbove40YearsWithBronchitis");
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithBronchitis.getSearches().put("1", new Mapped<CohortDefinition>(newBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithBronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithBronchitisIndicator",
                femalepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "New case Bronchitis Female above 40", new Mapped(femalepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 2.1 Old case patient with Bronchitis/Bronchite

        SqlCohortDefinition oldBronchitis=patientWithICDCodeObsByStartDateAndEndDate("J452",caseStatus,oldCase);


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
        maleBetween0And39BronchitisOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39BronchitisOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39BronchitisOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39BronchitisOldCasePatientIndicator",
                maleBetween0And39BronchitisOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn(".M.039", "Old case Bronchitis Male", new Mapped(maleBetween0And39BronchitisOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39BronchitisOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39BronchitisOldCasePatient.setName("femaleBetween0And39AsthmaIntermittantOldCasePatient");
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39BronchitisOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39BronchitisOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39BronchitisOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39BronchitisOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39BronchitisOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39BronchitisOldCasePatientIndicator",
                femaleBetween0And39BronchitisOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "Old case Bronchitis Female", new Mapped(femaleBetween0And39BronchitisOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

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
        maleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleOldCasepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithBronchitisIndicator",
                maleOldCasepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "Old case Bronchitis Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithBronchitis = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithBronchitis.setName("femaleOldCasepatientsAbove40YearsWithBronchitis");
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("1", new Mapped<CohortDefinition>(oldBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithBronchitis.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleOldCasepatientsAbove40YearsWithBronchitis.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithBronchitisIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithBronchitisIndicator",
                femaleOldCasepatientsAbove40YearsWithBronchitis, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "Old case Bronchitis Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithBronchitisIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // 3. New case patient Other Chronic respiratory diseases



        SqlCohortDefinition newOtherChronicRespiratoryDiseases=patientWithICDCodeObsByStartDateAndEndDate("CA22",caseStatus,newCase);

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
        maleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39OtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39OtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("maleBetween0And39OtherChronicRespiratoryDiseasesIndicator",
                maleBetween0And39OtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "New case Other Chronic respiratory diseases Male", new Mapped(maleBetween0And39OtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39OtherChronicRespiratoryDiseases = new CompositionCohortDefinition();
        femaleBetween0And39OtherChronicRespiratoryDiseases.setName("femaleBetween0And39AsthmaPersintentMild");
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("1", new Mapped<CohortDefinition>(newOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39OtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39OtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("femaleBetween0And39OtherChronicRespiratoryDiseasesIndicator",
                femaleBetween0And39OtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "New case Other Chronic respiratory diseases Female", new Mapped(femaleBetween0And39OtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


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
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases Indicator",
                malepatientsAbove40YearsWithOtherChronicRespiratoryDiseases , ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "New case Other Chronic respiratory diseases Male above 40", new Mapped(malepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases  = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .setName("femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases ");
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("1", new Mapped<CohortDefinition>(newOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases .setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator",
                femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseases , ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "New case Other Chronic respiratory diseases Female above 40", new Mapped(femalepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 2 Old case patient with Other Chronic respiratory diseases

        SqlCohortDefinition oldOtherChronicRespiratoryDiseases=patientWithICDCodeObsByStartDateAndEndDate("CA22",caseStatus,oldCase);


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
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator",
                maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "Old case Other Chronic respiratory diseases Male", new Mapped(maleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setName("femaleBetween0And39AsthmaPersintentMildOldCasePatient");
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator",
                femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "Old case Other Chronic respiratory diseases Female", new Mapped(femaleBetween0And39OtherChronicRespiratoryDiseasesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


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
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator",
                maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "Old case Other Chronic respiratory diseases Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setName("femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases");
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("1", new Mapped<CohortDefinition>(oldOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator",
                femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "Old case Other Chronic respiratory diseases Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithOtherChronicRespiratoryDiseasesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 3 New case patient with Diabetes - Type 1



        SqlCohortDefinition newDiabetesTypeOne=patientWithICDCodeObsByStartDateAndEndDate("5A10",caseStatus,newCase);

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
        maleBetween0And39DiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39DiabetesTypeOne.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeOneIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeOneIndicator",
                maleBetween0And39DiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "New case Diabetes - Type 1 Male", new Mapped(maleBetween0And39DiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeOne = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeOne.setName("femaleBetween0And39AsthmaPersintentModarate");
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39DiabetesTypeOne.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeOneIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeOneIndicator",
                femaleBetween0And39DiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "New case Diabetes - Type 1 Female", new Mapped(femaleBetween0And39DiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


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
        malepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                malepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "New case Diabetes - Type 1 Male above 40", new Mapped(malepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithDiabetesTypeOne = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDiabetesTypeOne.setName("femalepatientsAbove40YearsWithDiabetesTypeOne");
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                femalepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "New case Diabetes - Type 1 Female above 40", new Mapped(femalepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 3 Old case patient with Diabetes - Type 1

        SqlCohortDefinition oldDiabetesTypeOne=patientWithICDCodeObsByStartDateAndEndDate("5A10",caseStatus,oldCase);


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
        maleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39DiabetesTypeOneOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeOneOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeOneOldCasePatientIndicator",
                maleBetween0And39DiabetesTypeOneOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "Old case Diabetes - Type 1 Male", new Mapped(maleBetween0And39DiabetesTypeOneOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeOneOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeOneOldCasePatient.setName("femaleBetween0And39AsthmaPersintentModarateOldCasePatient");
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39DiabetesTypeOneOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeOneOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeOneOldCasePatientIndicator",
                femaleBetween0And39DiabetesTypeOneOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "Old case Diabetes - Type 1 Female", new Mapped(femaleBetween0And39DiabetesTypeOneOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


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
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                maleOldCasepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "Old case Diabetes - Type 1 Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setName("femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne");
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator",
                femaleOldCasepatientsAbove40YearsWithDiabetesTypeOne, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "Old case Diabetes - Type 1 Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDiabetesTypeOneIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        // IV.R. 4 New case patient with Diabetes - Type 2



        SqlCohortDefinition newDiabetesTypeTwo=patientWithICDCodeObsByStartDateAndEndDate("5A11",caseStatus,newCase);

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
        maleBetween0And39DiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39DiabetesTypeTwo.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeTwoIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeTwoIndicator",
                maleBetween0And39DiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "New case Diabetes - Type 2 Male", new Mapped(maleBetween0And39DiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeTwo = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeTwo.setName("femaleBetween0And39DiabetesTypeTwo");
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39DiabetesTypeTwo.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeTwoIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeTwoIndicator",
                femaleBetween0And39DiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "New case Diabetes - Type 2 Female", new Mapped(femaleBetween0And39DiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


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
        malepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                malepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "New case Diabetes - Type 2 Male above 40", new Mapped(malepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithDiabetesTypeTwo = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDiabetesTypeTwo.setName("femalepatientsAbove40YearsWithDiabetesTypeTwo");
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                femalepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "New case Diabetes - Type 2 Female above 40", new Mapped(femalepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // IV.R. 4 Old case patient with Diabetes - Type 2

        SqlCohortDefinition oldDiabetesTypeTwo=patientWithICDCodeObsByStartDateAndEndDate("5A11",caseStatus,oldCase);


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
        maleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39DiabetesTypeTwoOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesTypeTwoOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesTypeTwoOldCasePatientIndicator",
                maleBetween0And39DiabetesTypeTwoOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "Old case Diabetes - Type 2 Male", new Mapped(maleBetween0And39DiabetesTypeTwoOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesTypeTwoOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.setName("femaleBetween0And39DiabetesTypeTwoOldCasePatient");
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39DiabetesTypeTwoOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesTypeTwoOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesTypeTwoOldCasePatientIndicator",
                femaleBetween0And39DiabetesTypeTwoOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "Old case Diabetes - Type 2 Female", new Mapped(femaleBetween0And39DiabetesTypeTwoOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


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
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                maleOldCasepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "Old case Diabetes - Type 2 Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setName("femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo");
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("1", new Mapped<CohortDefinition>(oldDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator",
                femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "Old case Diabetes - Type 2 Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDiabetesTypeTwoIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



        // 6. New case patient with Diabetes gestational



        SqlCohortDefinition newDiabetesGestational=patientWithICDCodeObsByStartDateAndEndDate("JA63.Z",caseStatus,newCase);

// 0-39

        CompositionCohortDefinition maleBetween0And39DiabetesGestational = new CompositionCohortDefinition();
        maleBetween0And39DiabetesGestational.setName("maleBetween0And39DiabetesGestational");
        maleBetween0And39DiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39DiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39DiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39DiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39DiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39DiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39DiabetesGestational.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesGestationalIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesGestationalIndicator",
                maleBetween0And39DiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "New case Diabetes gestational Male", new Mapped(maleBetween0And39DiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesGestational = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesGestational.setName("femaleBetween0And39DiabetesGestational");
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39DiabetesGestational.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesGestationalIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesGestationalIndicator",
                femaleBetween0And39DiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "New case Diabetes gestational Female", new Mapped(femaleBetween0And39DiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 40 +


        CompositionCohortDefinition malepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
        malepatientsAbove40YearsWithDiabetesGestational.setName("malepatientsAbove40YearsWithDiabetesGestational");
        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        malepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and 3");


        CohortIndicator malepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithDiabetesGestationalIndicator",
                malepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "New case Diabetes gestational Male above 40", new Mapped(malepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femalepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
        femalepatientsAbove40YearsWithDiabetesGestational.setName("femalepatientsAbove40YearsWithDiabetesGestational");
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(newDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femalepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femalepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithDiabetesGestationalIndicator",
                femalepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "New case Diabetes gestational Female above 40", new Mapped(femalepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 6. Old case patient with Diabetes gestational

        SqlCohortDefinition OldDiabetesGestational=patientWithICDCodeObsByStartDateAndEndDate("JA63.Z",caseStatus,oldCase);


// 0-39

        CompositionCohortDefinition maleBetween0And39DiabetesGestationalOldCasePatient = new CompositionCohortDefinition();
        maleBetween0And39DiabetesGestationalOldCasePatient.setName("maleBetween0And39DiabetesGestationalOldCasePatient");
        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(OldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleBetween0And39DiabetesGestationalOldCasePatient.setCompositionString("1 and 2 and 3");


        CohortIndicator maleBetween0And39DiabetesGestationalOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And39DiabetesGestationalOldCasePatientIndicator",
                maleBetween0And39DiabetesGestationalOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.039", "Old case Diabetes gestational Male", new Mapped(maleBetween0And39DiabetesGestationalOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleBetween0And39DiabetesGestationalOldCasePatient = new CompositionCohortDefinition();
        femaleBetween0And39DiabetesGestationalOldCasePatient.setName("femaleBetween0And39DiabetesGestationalOldCasePatient");
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(OldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndThirtyNineYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleBetween0And39DiabetesGestationalOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBetween0And39DiabetesGestationalOldCasePatient.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleBetween0And39DiabetesGestationalOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBetween0And39DiabetesGestationalOldCasePatientIndicator",
                femaleBetween0And39DiabetesGestationalOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.039", "Old case Diabetes gestational Female", new Mapped(femaleBetween0And39DiabetesGestationalOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 40 +

        CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.setName("maleOldCasepatientsAbove40YearsWithDiabetesGestational");
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(OldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        maleOldCasepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and 3");


        CohortIndicator maleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator",
                maleOldCasepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("M.40", "Old case Diabetes gestational Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




        CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithDiabetesGestational = new CompositionCohortDefinition();
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.setName("femaleOldCasepatientsAbove40YearsWithDiabetesGestational");
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("startDate", "startDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("1", new Mapped<CohortDefinition>(OldDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleOldCasepatientsAbove40YearsWithDiabetesGestational.setCompositionString("1 and 2 and (not 3)");


        CohortIndicator femaleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator",
                femaleOldCasepatientsAbove40YearsWithDiabetesGestational, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        dsd.addColumn("F.40", "Old case Diabetes gestational Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithDiabetesGestationalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

    }

    private SqlCohortDefinition patientWithICDCodeObsByStartDateAndEndDate(String ICDCode,Concept caseStatusQuestion, Concept caseAnswer){

        SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o " +
                "inner join obs o2 on o.encounter_id=o2.encounter_id" +
                " where o.value_coded in (select distinct concept_id from concept_name where name like '%"+ICDCode+"%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o2.concept_id="+caseStatusQuestion.getConceptId()+" and o2.value_coded="+caseAnswer.getConceptId()+"");
        patientWithIDCObs.setName("patientWithIDCObs");
        patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithIDCObs;

    }

    private SqlCohortDefinition patientWithICDCodesObsByStartDateAndEndDate(String ICDCodes,Concept caseStatusQuestion, Concept caseAnswer){

        String icdTencodes[] =ICDCodes.split(",");

        StringBuilder q=new StringBuilder();
        q.append("select o.person_id from obs o " +
                "inner join obs o2 on o.encounter_id=o2.encounter_id" +
                " where o.value_coded in (select distinct concept_id from concept_name where ");
        int i=0;
        for (String c:icdTencodes){
            if(i==0){
                q.append("name like '%"+c+"%'");
                i++;
            }else {
                q.append(" or name like '%"+c+"%'");
                i++;
            }
        }
        q.append(") and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") " +
                "and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o2.concept_id="+caseStatusQuestion.getConceptId()+" and o2.value_coded="+caseAnswer.getConceptId()+"");

        SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition(q.toString());
        patientWithIDCObs.setName("patientWithIDCObs");
        patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
        patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

        return patientWithIDCObs;

    }
}
