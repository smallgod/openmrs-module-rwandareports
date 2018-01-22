package org.openmrs.module.rwandareports.reporting;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.definition.DrugsActiveCohortDefinition;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by jberchmas on 11/21/17.
 */
public class SetupHMISMOHReport {


    Program p= Context.getProgramWorkflowService().getProgram(2);

    EncounterType transferInEncType=Context.getEncounterService().getEncounterType("HIV TRANSFER IN");
    EncounterType HIVVisitEncType=Context.getEncounterService().getEncounterType("HIV VISIT");

    Concept tbScreening=Context.getConceptService().getConceptByUuid("3ce14c2c-26fe-102b-80cb-0017a47871b2");

    Concept onAntiretroviral=Context.getConceptService().getConceptByUuid("3cdc0a8c-26fe-102b-80cb-0017a47871b2");

    Concept TBDrugsConcept=Context.getConceptService().getConceptByUuid("3cd79e0c-26fe-102b-80cb-0017a47871b2");

    //UUID to be replaced by uuid from RBC local test server
    Concept unstable=Context.getConceptService().getConceptByUuid("d208371d-4542-410b-9fec-e76879dbd9f3");

    Concept stable=Context.getConceptService().getConceptByUuid("e5cb0eed-d6c5-44bd-9fc1-d88bdd0a8b12");

    Concept firstLineRegiment=Context.getConceptService().getConceptByUuid("588ad048-1f74-4555-9199-78ae3b358d7e");
    Concept secondLineRegiment=Context.getConceptService().getConceptByUuid("8fc0d105-46e4-4f90-a167-7e5e54434739");
    Concept thrirdLineRegiment=Context.getConceptService().getConceptByUuid("3d8cdb75-d07d-47af-9def-0e397c5fb4d9");

    Concept patientDied=Context.getConceptService().getConceptByUuid("3cdd446a-26fe-102b-80cb-0017a47871b2");

    Concept reasonForExitingFromCare=Context.getConceptService().getConceptByUuid("3cde5ef4-26fe-102b-80cb-0017a47871b2");
    Concept patientDefaulted=Context.getConceptService().getConceptByUuid("3cdd5176-26fe-102b-80cb-0017a47871b2");
    Concept transferedOut=Context.getConceptService().getConceptByUuid("3cdd5c02-26fe-102b-80cb-0017a47871b2");





    public void setup() throws Exception {
        ReportDefinition rd=createReportDefinition();
        ReportDesign desinTemplate=Helper.createRowPerPatientXlsOverviewReportDesign(rd, "HMISMOHreport.xls","HMISMOHreport",null);

        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,dataset:HMIS report dataset");
        props.put("sortWeight","5000");
        desinTemplate.setProperties(props);
        Helper.saveReportDesign(desinTemplate);


    }
    public void delete(){

        ReportService rs = Context.getService(ReportService.class);
        for (ReportDesign rd : rs.getAllReportDesigns(false)) {
            if ("HMISMOHreport".equals(rd.getName())) {
                rs.purgeReportDesign(rd);
            }
        }
        Helper.purgeReportDefinition("MOH-HMIS Indicator report");

    }
    private ReportDefinition createReportDefinition() {
        ReportDefinition reportDef=new ReportDefinition();
        reportDef.setName("MOH-HMIS Indicator report");
        reportDef.addParameter(new Parameter("startDate", "Start Date", Date.class));
        reportDef.addParameter(new Parameter("endDate", "End Date", Date.class));
        reportDef.addParameter(new Parameter("location","Health facility",  Location.class));



        SqlCohortDefinition locationAndProgram = new SqlCohortDefinition();
        locationAndProgram.setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat,patient_program pp where p.patient_id = pa.person_id and p.patient_id = pp.patient_id and pp.program_id="+p.getProgramId()+" and pat.name ='Health Center' and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id and pa.value = :location");
        locationAndProgram.setName("Location and program cohort def");
        locationAndProgram.addParameter(new Parameter("location", "location", Location.class));

        reportDef.setBaseCohortDefinition(locationAndProgram, ParameterizableUtil.createParameterMappings("location=${location}"));

        reportDef.addDataSetDefinition(createDataSetDefinition(),ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        //reportDef.addDataSetDefinition(createARTDataSetDefinition(),ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));



        Helper.saveReportDefinition(reportDef);


        return  reportDef;
    }

    private DataSetDefinition createDataSetDefinition() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("HMIS report dataset");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addParameter(new Parameter("location","Health facility",  Location.class));


        EncounterCohortDefinition patientsWithTransferInEncounter=new EncounterCohortDefinition();
        patientsWithTransferInEncounter.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientsWithTransferInEncounter.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        patientsWithTransferInEncounter.addEncounterType(transferInEncType);




        //====================================================
        //  A. ENROLLEMENT IN THE PROGRAM
        //====================================================

        //================================================================================
       //  A01: Total number of male patients newly enrolled in HIV Care and Treatment between 0 and 4 years
        //====================================================================================

//Male patient

        GenderCohortDefinition males=new GenderCohortDefinition();
        males.setName("male Patients");
        males.setMaleIncluded(true);

        // Age between 0 and 4

        AgeCohortDefinition patientsWithAgeBetween0An4Years=new AgeCohortDefinition();
        patientsWithAgeBetween0An4Years.setName("patientsWithAgeBetween0An4Years");
        patientsWithAgeBetween0An4Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAgeBetween0An4Years.setMaxAge(4);
        patientsWithAgeBetween0An4Years.setMaxAgeUnit(DurationUnit.YEARS);


        // Newly Enrolled In HIV program

        ProgramEnrollmentCohortDefinition newlyEnrolledInHIV=new ProgramEnrollmentCohortDefinition();
        newlyEnrolledInHIV.setName("newlyEnrolledInHIV");
        newlyEnrolledInHIV.addProgram(p);
        newlyEnrolledInHIV.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newlyEnrolledInHIV.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));


        CompositionCohortDefinition newMalePatientsUnder4Years=new CompositionCohortDefinition();
        newMalePatientsUnder4Years.setName("newMalePatientsUnder4Years");
        newMalePatientsUnder4Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newMalePatientsUnder4Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newMalePatientsUnder4Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newMalePatientsUnder4Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newMalePatientsUnder4Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newMalePatientsUnder4Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newMalePatientsUnder4Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween0An4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newMalePatientsUnder4Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newMalePatientsUnder4Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newMalePatientsUnder4Years.setCompositionString("(1 AND 2 AND 3) AND (NOT 4)");

        CohortIndicator newMalePatientsUnder4YearsIndicator= Indicators.newCohortIndicator("newMalePatientsUnder4YearsIndicator",
                newMalePatientsUnder4Years, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}"));

        dsd.addColumn("A01","Total number of male patients newly enrolled in HIV Care and Treatment between 0 and 4 years",new Mapped(newMalePatientsUnder4YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //================================================================================
        //  A02: Total number of female patients newly enrolled in HIV Care and Treatment between 0 and 4 years
        //====================================================================================

        CompositionCohortDefinition newFemalePatientsUnder4Years=new CompositionCohortDefinition();
        newFemalePatientsUnder4Years.setName("newFemalePatientsUnder4Years");
        newFemalePatientsUnder4Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newFemalePatientsUnder4Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newFemalePatientsUnder4Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newFemalePatientsUnder4Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newFemalePatientsUnder4Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newFemalePatientsUnder4Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newFemalePatientsUnder4Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween0An4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newFemalePatientsUnder4Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newFemalePatientsUnder4Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newFemalePatientsUnder4Years.setCompositionString("(2 AND 3 AND (NOT 1)) AND (NOT 4)");

        CohortIndicator newFemalePatientsUnder4YearsIndicator= Indicators.newCohortIndicator("newFemalePatientsUnder4YearsIndicator",
                newFemalePatientsUnder4Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A02","Total number of female patients newly enrolled in HIV Care and Treatment between 0 and 4 years",new Mapped(newFemalePatientsUnder4YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//=====================================================================
//  A03: Total number of male patients newly enrolled in HIV Care and Treatment between 5 and 9 years
//======================================================================


        AgeCohortDefinition patientsWithAgeBetween5An9Years=new AgeCohortDefinition();
        patientsWithAgeBetween5An9Years.setName("patientsWithAgeBetween5An9Years");
        patientsWithAgeBetween5An9Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAgeBetween5An9Years.setMinAge(5);
        patientsWithAgeBetween5An9Years.setMaxAge(9);
        patientsWithAgeBetween5An9Years.setMaxAgeUnit(DurationUnit.YEARS);



        CompositionCohortDefinition newMalePatientsBetween5And9Years=new CompositionCohortDefinition();
        newMalePatientsBetween5And9Years.setName("newMalePatientsBetween5And9Years");
        newMalePatientsBetween5And9Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newMalePatientsBetween5And9Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newMalePatientsBetween5And9Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newMalePatientsBetween5And9Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newMalePatientsBetween5And9Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newMalePatientsBetween5And9Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newMalePatientsBetween5And9Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween5An9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newMalePatientsBetween5And9Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newMalePatientsBetween5And9Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newMalePatientsBetween5And9Years.setCompositionString("(1 AND 2 AND 3) AND (NOT 4)");

        CohortIndicator newMalePatientsBetween5And9YearsIndicator= Indicators.newCohortIndicator("newMalePatientsBetween5And9YearsIndicator",
                newMalePatientsBetween5And9Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A03","Total number of male patients newly enrolled in HIV Care and Treatment between 5 and 9 years",new Mapped(newMalePatientsBetween5And9YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //=====================================================================
//  A04: Total number of female patients newly enrolled in HIV Care and Treatment between 5 and 9 years
//======================================================================

        CompositionCohortDefinition newFemalePatientsBetween5And9Years=new CompositionCohortDefinition();
        newFemalePatientsBetween5And9Years.setName("newFemalePatientsBetween5And9Years");
        newFemalePatientsBetween5And9Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newFemalePatientsBetween5And9Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newFemalePatientsBetween5And9Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newFemalePatientsBetween5And9Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newFemalePatientsBetween5And9Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newFemalePatientsBetween5And9Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newFemalePatientsBetween5And9Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween5An9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newFemalePatientsBetween5And9Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newFemalePatientsBetween5And9Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newFemalePatientsBetween5And9Years.setCompositionString("(3 AND 2 AND (NOT 1)) AND (NOT 4)");

        CohortIndicator newFemalePatientsBetween5And9YearsIndicator= Indicators.newCohortIndicator("newFemalePatientsBetween5And9YearsIndicator",
                newMalePatientsBetween5And9Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A04","Total number of female patients newly enrolled in HIV Care and Treatment between 5 and 9 years",new Mapped(newFemalePatientsBetween5And9YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



//=====================================================================
//  A05: Total number of male patients newly enrolled in HIV Care and Treatment between 10 and 14 years
//======================================================================


        AgeCohortDefinition patientsWithAgeBetween10An14Years=new AgeCohortDefinition();
        patientsWithAgeBetween10An14Years.setName("patientsWithAgeBetween10An14Years");
        patientsWithAgeBetween10An14Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAgeBetween10An14Years.setMinAge(10);
        patientsWithAgeBetween10An14Years.setMaxAge(14);
        patientsWithAgeBetween10An14Years.setMaxAgeUnit(DurationUnit.YEARS);



        CompositionCohortDefinition newMalePatientsBetween10And14Years=new CompositionCohortDefinition();
        newMalePatientsBetween10And14Years.setName("newMalePatientsBetween10And14Years");
        newMalePatientsBetween10And14Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newMalePatientsBetween10And14Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newMalePatientsBetween10And14Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newMalePatientsBetween10And14Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newMalePatientsBetween10And14Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newMalePatientsBetween10And14Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newMalePatientsBetween10And14Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween10An14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newMalePatientsBetween10And14Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newMalePatientsBetween10And14Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newMalePatientsBetween10And14Years.setCompositionString("(1 AND 2 AND 3) AND (NOT 4)");

        CohortIndicator newMalePatientsBetween10And14YearsIndicator= Indicators.newCohortIndicator("newMalePatientsBetween10And14YearsIndicator",
                newMalePatientsBetween10And14Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A05","Total number of male patients newly enrolled in HIV Care and Treatment between 10 and 14 years",new Mapped(newMalePatientsBetween10And14YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //=====================================================================
//  A06: Total number of female patients newly enrolled in HIV Care and Treatment between 10 and 14 years
//======================================================================

        CompositionCohortDefinition newFemalePatientsBetween10And14Years=new CompositionCohortDefinition();
        newFemalePatientsBetween10And14Years.setName("newFemalePatientsBetween10And14Years");
        newFemalePatientsBetween10And14Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newFemalePatientsBetween10And14Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newFemalePatientsBetween10And14Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newFemalePatientsBetween10And14Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newFemalePatientsBetween10And14Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newFemalePatientsBetween10And14Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newFemalePatientsBetween10And14Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween10An14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newFemalePatientsBetween10And14Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newFemalePatientsBetween10And14Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newFemalePatientsBetween10And14Years.setCompositionString("(3 AND 2 AND (NOT 1)) AND (NOT 4)");

        CohortIndicator newFemalePatientsBetween10And14YearsIndicator= Indicators.newCohortIndicator("newFemalePatientsBetween10And14YearsIndicator",
                newFemalePatientsBetween10And14Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A06","Total number of female patients newly enrolled in HIV Care and Treatment between 10 and 14 years",new Mapped(newFemalePatientsBetween10And14YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");







//=====================================================================
//  A07: Total number of male patients newly enrolled in HIV Care and Treatment between 15 and 19 years
//======================================================================


        AgeCohortDefinition patientsWithAgeBetween15An19Years=new AgeCohortDefinition();
        patientsWithAgeBetween15An19Years.setName("patientsWithAgeBetween10An14Years");
        patientsWithAgeBetween15An19Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAgeBetween15An19Years.setMinAge(15);
        patientsWithAgeBetween15An19Years.setMaxAge(19);
        patientsWithAgeBetween15An19Years.setMaxAgeUnit(DurationUnit.YEARS);



        CompositionCohortDefinition newMalePatientsBetween15And19Years=new CompositionCohortDefinition();
        newMalePatientsBetween15And19Years.setName("newMalePatientsBetween10And14Years");
        newMalePatientsBetween15And19Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newMalePatientsBetween15And19Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newMalePatientsBetween15And19Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newMalePatientsBetween15And19Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newMalePatientsBetween15And19Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newMalePatientsBetween15And19Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newMalePatientsBetween15And19Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween15An19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newMalePatientsBetween15And19Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newMalePatientsBetween15And19Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newMalePatientsBetween15And19Years.setCompositionString("(1 AND 2 AND 3) AND (NOT 4)");

        CohortIndicator newMalePatientsBetween15And19YearsIndicator= Indicators.newCohortIndicator("newMalePatientsBetween15And19YearsIndicator",
                newMalePatientsBetween15And19Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A07","Total number of male patients newly enrolled in HIV Care and Treatment between 15 and 19 years",new Mapped(newMalePatientsBetween15And19YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //=====================================================================
        //  A08: Total number of female patients newly enrolled in HIV Care and Treatment between 15 and 19 years
        //======================================================================

        CompositionCohortDefinition newFemalePatientsBetween15And19Years=new CompositionCohortDefinition();
        newFemalePatientsBetween15And19Years.setName("newFemalePatientsBetween15And19Years");
        newFemalePatientsBetween15And19Years.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newFemalePatientsBetween15And19Years.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newFemalePatientsBetween15And19Years.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newFemalePatientsBetween15And19Years.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newFemalePatientsBetween15And19Years.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newFemalePatientsBetween15And19Years.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newFemalePatientsBetween15And19Years.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAgeBetween15An19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newFemalePatientsBetween15And19Years.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newFemalePatientsBetween15And19Years.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newFemalePatientsBetween15And19Years.setCompositionString("(3 AND 2 AND (NOT 1)) AND (NOT 4)");

        CohortIndicator newFemalePatientsBetween15And19YearsIndicator= Indicators.newCohortIndicator("newFemalePatientsBetween15And19YearsIndicator",
                newFemalePatientsBetween15And19Years, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A08","Total number of female patients newly enrolled in HIV Care and Treatment between 15 and 19 years",new Mapped(newFemalePatientsBetween15And19YearsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


//=====================================================================
//  A09: Total number of male patients newly enrolled in HIV Care and Treatment with 20 years and above
//======================================================================


        AgeCohortDefinition patientsWithAge20AndAbove=new AgeCohortDefinition();
        patientsWithAge20AndAbove.setName("patientsWithAge20AndAbove");
        patientsWithAge20AndAbove.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAge20AndAbove.setMinAge(20);
        patientsWithAge20AndAbove.setMaxAgeUnit(DurationUnit.YEARS);



        CompositionCohortDefinition newMalePatients20YearsAndAbove=new CompositionCohortDefinition();
        newMalePatients20YearsAndAbove.setName("newMalePatients20YearsAndAbove");
        newMalePatients20YearsAndAbove.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newMalePatients20YearsAndAbove.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newMalePatients20YearsAndAbove.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newMalePatients20YearsAndAbove.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newMalePatients20YearsAndAbove.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newMalePatients20YearsAndAbove.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newMalePatients20YearsAndAbove.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAge20AndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newMalePatients20YearsAndAbove.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newMalePatients20YearsAndAbove.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newMalePatients20YearsAndAbove.setCompositionString("(1 AND 2 AND 3) AND (NOT 4)");

        CohortIndicator newMalePatients20YearsAndAboveIndicator= Indicators.newCohortIndicator("newMalePatients20YearsAndAboveIndicator",
                newMalePatients20YearsAndAbove, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A09","Total number of male patients newly enrolled in HIV Care and Treatment with 20 years and above",new Mapped(newMalePatients20YearsAndAboveIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //=====================================================================
        //  A10: Total number of female patients newly enrolled in HIV Care and Treatment between 15 and 19 years
        //======================================================================

        CompositionCohortDefinition newFemalePatients20YearsAndAbove=new CompositionCohortDefinition();
        newFemalePatients20YearsAndAbove.setName("newFemalePatients20YearsAndAbove");
        newFemalePatients20YearsAndAbove.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newFemalePatients20YearsAndAbove.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newFemalePatients20YearsAndAbove.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        newFemalePatients20YearsAndAbove.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newFemalePatients20YearsAndAbove.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newFemalePatients20YearsAndAbove.getSearches().put("1",new Mapped<CohortDefinition>(males, null));
        newFemalePatients20YearsAndAbove.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithAge20AndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        newFemalePatients20YearsAndAbove.getSearches().put("3",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newFemalePatients20YearsAndAbove.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newFemalePatients20YearsAndAbove.setCompositionString("(3 AND 2 AND (NOT 1)) AND (NOT 4)");

        CohortIndicator newFemalePatients20YearsAndAboveIndicator= Indicators.newCohortIndicator("newFemalePatients20YearsAndAboveIndicator",
                newFemalePatients20YearsAndAbove, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate},effectiveDate=${endDate}"));

        dsd.addColumn("A10","Total number of female patients newly enrolled in HIV Care and Treatment with 20 years and above",new Mapped(newFemalePatients20YearsAndAboveIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//================================================
// A11: New patients screened for active TB at enrollment this month
//============================================

        CodedObsCohortDefinition patientWithTBScreening=new CodedObsCohortDefinition();
        patientWithTBScreening.setQuestion(tbScreening);
        patientWithTBScreening.setTimeModifier(TimeModifier.ANY);
        patientWithTBScreening.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        patientWithTBScreening.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));


        CompositionCohortDefinition newPatientsWithTBScreening=new CompositionCohortDefinition();
        newPatientsWithTBScreening.setName("newPatientsWithTBScreening");
        newPatientsWithTBScreening.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newPatientsWithTBScreening.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newPatientsWithTBScreening.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newPatientsWithTBScreening.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newPatientsWithTBScreening.getSearches().put("1",new Mapped<CohortDefinition>(patientWithTBScreening, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newPatientsWithTBScreening.getSearches().put("2",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newPatientsWithTBScreening.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newPatientsWithTBScreening.setCompositionString("1 and 2 and (not 3)");

        CohortIndicator newPatientsWithTBScreeningIndicator= Indicators.newCohortIndicator("newPatientsWithTBScreeningIndicator",
                newPatientsWithTBScreening, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));

        dsd.addColumn("A11","New patients screened for active TB at enrollment this month",new Mapped(newPatientsWithTBScreeningIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//================================================
// A12: New patients screened for active positive TB at enrollment this month
//============================================
//Concept answer=Context.getConceptService().getConcept("POSITIVE");

        Concept answer=Context.getConceptService().getConceptByUuid("3cd3a7a2-26fe-102b-80cb-0017a47871b2");


        SqlCohortDefinition patientWithPositiveTBScreening=new SqlCohortDefinition();
         patientWithPositiveTBScreening.setQuery("select person_id from obs where concept_id="+tbScreening.getConceptId()+" and value_coded="+answer.getConceptId()+" and voided=0 and obs_datetime>= :onOrAfter and obs_datetime<= :onOrBefore");
        patientWithPositiveTBScreening.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientWithPositiveTBScreening.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));



        CompositionCohortDefinition newPatientsWithPositiveTBScreening=new CompositionCohortDefinition();
        newPatientsWithPositiveTBScreening.setName("newPatientsWithPositiveTBScreening");
        newPatientsWithPositiveTBScreening.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
        newPatientsWithPositiveTBScreening.addParameter(new Parameter("enrolledOnOrBefore","enrolledOnOrBefore",Date.class));
        newPatientsWithPositiveTBScreening.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        newPatientsWithPositiveTBScreening.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        newPatientsWithPositiveTBScreening.getSearches().put("1",new Mapped<CohortDefinition>(patientWithPositiveTBScreening, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newPatientsWithPositiveTBScreening.getSearches().put("2",new Mapped<CohortDefinition>(newlyEnrolledInHIV, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},enrolledOnOrBefore=${enrolledOnOrBefore}")));
        newPatientsWithPositiveTBScreening.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        newPatientsWithPositiveTBScreening.setCompositionString("1 and 2 and (not 3)");

        CohortIndicator newPatientsWithPositiveTBScreeningIndicator= Indicators.newCohortIndicator("newPatientsWithPositiveTBScreeningIndicator",
                newPatientsWithPositiveTBScreening, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}"));


        dsd.addColumn("A12","Patients screened for TB Positive at enrollment this month",new Mapped(newPatientsWithPositiveTBScreeningIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //=======================================================================================================
        // B. B. ART Data Elements
        //=======================================================================================================



        //============================================
        // B01 Total number of male patients with less than 1 year currently on ART
        //==========================================

        AgeCohortDefinition patientBelowOnYear=patientWithAgeBelow(1);


        InStateCohortDefinition onART=new InStateCohortDefinition();
        onART.addState(p.getWorkflowByName("TREATMENT STATUS").getState(onAntiretroviral));
        onART.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        onART.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));


        CompositionCohortDefinition malePatientBelowOneYearOnART=new CompositionCohortDefinition();
        malePatientBelowOneYearOnART.setName("malePatientBelowOneYearOnART");
        malePatientBelowOneYearOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBelowOneYearOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBelowOneYearOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBelowOneYearOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientBelowOnYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBelowOneYearOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBelowOneYearOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBelowOneYearOnART.setCompositionString("1 and 2 and 3");

        CohortIndicator malePatientBelowOneYearOnARTIndicator= Indicators.newCohortIndicator("malePatientBelowOneYearOnARTIndicator",
                malePatientBelowOneYearOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B01","Total number of male patients with less than 1 year currently on ART",new Mapped(malePatientBelowOneYearOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //============================================
        // B02 Total number of female patients with less than 1 year currently on ART
        //==========================================

        CompositionCohortDefinition femalePatientBelowOneYearOnART=new CompositionCohortDefinition();
        femalePatientBelowOneYearOnART.setName("femalePatientBelowOneYearOnART");
        femalePatientBelowOneYearOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBelowOneYearOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBelowOneYearOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBelowOneYearOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientBelowOnYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBelowOneYearOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBelowOneYearOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBelowOneYearOnART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalePatientBelowOneYearOnARTIndicator= Indicators.newCohortIndicator("femalePatientBelowOneYearOnARTIndicator",
                femalePatientBelowOneYearOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B02","Total number of female patients with less than 1 year currently on ART",new Mapped(femalePatientBelowOneYearOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //============================================
        // B03 Total number of male patients between 1 and 4 years currently on ART
        //==========================================


        AgeCohortDefinition patientsBitween1And4Years=patientWithAgeBetween(1,4);

        CompositionCohortDefinition malepatientsBitween1And4YearsOnART=new CompositionCohortDefinition();
        malepatientsBitween1And4YearsOnART.setName("malepatientsBitween1And4YearsOnART");
        malepatientsBitween1And4YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween1And4YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsBitween1And4YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsBitween1And4YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween1And4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween1And4YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween1And4YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malepatientsBitween1And4YearsOnART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween1And4YearsOnARTIndicator= Indicators.newCohortIndicator("malepatientsBitween1And4YearsOnARTIndicator",
                malepatientsBitween1And4YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B03","Total number of male patients between 1 and 4 years currently on ART",new Mapped(malepatientsBitween1And4YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B04 Total number of female patients between 1 and 4 years currently on ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween1And4YearsOnART=new CompositionCohortDefinition();
        femalepatientsBitween1And4YearsOnART.setName("femalepatientsBitween1And4YearsOnART");
        femalepatientsBitween1And4YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween1And4YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsBitween1And4YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsBitween1And4YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween1And4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween1And4YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween1And4YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalepatientsBitween1And4YearsOnART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween1And4YearsOnARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween1And4YearsOnARTIndicator",
                femalepatientsBitween1And4YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B04","Total number of male patients between 1 and 4 years currently on ART",new Mapped(femalepatientsBitween1And4YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //============================================
        // B05 Total number of male patients between 5 and 9 years currently on ART
        //==========================================


        AgeCohortDefinition patientsBitween5And9Years=patientWithAgeBetween(5,9);

        CompositionCohortDefinition malepatientsBitween5And9YearsOnART=new CompositionCohortDefinition();
        malepatientsBitween5And9YearsOnART.setName("malepatientsBitween1And4YearsOnART");
        malepatientsBitween5And9YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween5And9YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsBitween5And9YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsBitween5And9YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween5And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween5And9YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween5And9YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malepatientsBitween5And9YearsOnART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween5And9YearsOnARTIndicator= Indicators.newCohortIndicator("malepatientsBitween5And9YearsOnARTIndicator",
                malepatientsBitween5And9YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B05","Total number of male patients between 5 and 9 years currently on ART",new Mapped(malepatientsBitween5And9YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //==========================================
        // B06 Total number of female patients between 5 and 9 years currently on ART
        //==========================================


        CompositionCohortDefinition femalepatientsBitween5And9YearsOnART=new CompositionCohortDefinition();
        femalepatientsBitween5And9YearsOnART.setName("femalepatientsBitween1And4YearsOnART");
        femalepatientsBitween5And9YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween5And9YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsBitween5And9YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsBitween5And9YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween5And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween5And9YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween5And9YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalepatientsBitween5And9YearsOnART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween5And9YearsOnARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween5And9YearsOnARTIndicator",
                femalepatientsBitween5And9YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B06","Total number of male patients between 5 and 9 years currently on ART",new Mapped(femalepatientsBitween5And9YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //============================================
        // B07 Total number of male patients between 10 and 14 years currently on ART
        //==========================================


        AgeCohortDefinition patientsBitween10And14Years=patientWithAgeBetween(10,14);

        CompositionCohortDefinition malepatientsBitween10And14YearsOnART=new CompositionCohortDefinition();
        malepatientsBitween10And14YearsOnART.setName("malepatientsBitween10And14YearsOnART");
        malepatientsBitween10And14YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween10And14YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsBitween10And14YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsBitween10And14YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween10And14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween10And14YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween10And14YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malepatientsBitween10And14YearsOnART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween10And14YearsOnARTIndicator= Indicators.newCohortIndicator("malepatientsBitween10And14YearsOnARTIndicator",
                malepatientsBitween10And14YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B07","Total number of male patients between 10 and 14 years currently on ART",new Mapped(malepatientsBitween10And14YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //==========================================
        // B07 Total number of female patients between 10 and 14 years currently on ART
        //==========================================


        CompositionCohortDefinition femalepatientsBitween10And14YearsOnART=new CompositionCohortDefinition();
        femalepatientsBitween10And14YearsOnART.setName("femalepatientsBitween10And14YearsOnART");
        femalepatientsBitween10And14YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween10And14YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsBitween10And14YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsBitween10And14YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween10And14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween10And14YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween10And14YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalepatientsBitween10And14YearsOnART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween10And14YearsOnARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween10And14YearsOnARTIndicator",
                femalepatientsBitween10And14YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B08","Total number of female patients between 10 and 14 years currently on ART",new Mapped(femalepatientsBitween10And14YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");




        //============================================
        // B09 Total number of male patients between 15 and 19 years currently on ART
        //==========================================


        AgeCohortDefinition patientsBitween15And19Years=patientWithAgeBetween(15,19);

        CompositionCohortDefinition malepatientsBitween15And19YearsOnART=new CompositionCohortDefinition();
        malepatientsBitween15And19YearsOnART.setName("malepatientsBitween10And14YearsOnART");
        malepatientsBitween15And19YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween15And19YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsBitween15And19YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsBitween15And19YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween15And19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween15And19YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween15And19YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malepatientsBitween15And19YearsOnART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween15And19YearsOnARTIndicator= Indicators.newCohortIndicator("malepatientsBitween15And19YearsOnARTIndicator",
                malepatientsBitween15And19YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B09","Total number of male patients between 15 and 19 years currently on ART",new Mapped(malepatientsBitween15And19YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //==========================================
        // B10 Total number of female patients between 15 and 19 years currently on ART
        //==========================================


        CompositionCohortDefinition femalepatientsBitween15And19YearsOnART=new CompositionCohortDefinition();
        femalepatientsBitween15And19YearsOnART.setName("femalepatientsBitween15And19YearsOnART");
        femalepatientsBitween15And19YearsOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween15And19YearsOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsBitween15And19YearsOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsBitween15And19YearsOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween15And19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween15And19YearsOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween15And19YearsOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalepatientsBitween15And19YearsOnART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween15And19YearsOnARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween15And19YearsOnARTIndicator",
                femalepatientsBitween15And19YearsOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B10","Total number of female patients between 10 and 14 years currently on ART",new Mapped(femalepatientsBitween15And19YearsOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //============================================
        // B11 Total number of male patients with 20 years and above currently on ART
        //==========================================


        AgeCohortDefinition patientsWith20YearsAndAbove=patientWithAgeAbove(20);

        CompositionCohortDefinition malepatientsWith20YearsAndAboveOnART=new CompositionCohortDefinition();
        malepatientsWith20YearsAndAboveOnART.setName("malepatientsWith20YearsAndAboveOnART");
        malepatientsWith20YearsAndAboveOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsWith20YearsAndAboveOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsWith20YearsAndAboveOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malepatientsWith20YearsAndAboveOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith20YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsWith20YearsAndAboveOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsWith20YearsAndAboveOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malepatientsWith20YearsAndAboveOnART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsWith20YearsAndAboveOnARTIndicator= Indicators.newCohortIndicator("malepatientsWith20YearsAndAboveOnARTIndicator",
                malepatientsWith20YearsAndAboveOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B11","Total number of male patients with 20 years and above currently on ART",new Mapped(malepatientsWith20YearsAndAboveOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //==========================================
        // B12 Total number of female patients with 20 years and above currently on ART
        //==========================================


        CompositionCohortDefinition femalepatientsWith20YearsAndAboveOnART=new CompositionCohortDefinition();
        femalepatientsWith20YearsAndAboveOnART.setName("femalepatientsWith20YearsAndAboveOnART");
        femalepatientsWith20YearsAndAboveOnART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsWith20YearsAndAboveOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsWith20YearsAndAboveOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalepatientsWith20YearsAndAboveOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith20YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsWith20YearsAndAboveOnART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsWith20YearsAndAboveOnART.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalepatientsWith20YearsAndAboveOnART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsWith20YearsAndAboveOnARTIndicator= Indicators.newCohortIndicator("femalepatientsWith20YearsAndAboveOnARTIndicator",
                femalepatientsWith20YearsAndAboveOnART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B12","Total number of female patients with 20 years and above currently on ART",new Mapped(femalepatientsWith20YearsAndAboveOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


//======================================================================================
// B13 Total number of male patients with less than 1 year who initiated ART
//======================================================================================

        PatientStateCohortDefinition patientInitiatedART=new PatientStateCohortDefinition();
        patientInitiatedART.setName("patientInitiatedART");
        patientInitiatedART.addState(p.getWorkflowByName("TREATMENT STATUS").getState(onAntiretroviral));
        patientInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        patientInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));



        CompositionCohortDefinition malePatientBelowOneYearInitiatedART=new CompositionCohortDefinition();
        malePatientBelowOneYearInitiatedART.setName("malePatientBelowOneYearInitiatedART");
        malePatientBelowOneYearInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBelowOneYearInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malePatientBelowOneYearInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malePatientBelowOneYearInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientBelowOnYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBelowOneYearInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBelowOneYearInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malePatientBelowOneYearInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malePatientBelowOneYearInitiatedARTIndicator= Indicators.newCohortIndicator("malePatientBelowOneYearInitiatedARTIndicator",
                malePatientBelowOneYearInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B13","Total number of male patients with less than 1 year who initiated ART",new Mapped(malePatientBelowOneYearInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//===========================================================
//  B14 Total number of female patients with less than 1 year who initiated ART
//======================================================

        CompositionCohortDefinition femalePatientBelowOneYearInitiatedART=new CompositionCohortDefinition();
        femalePatientBelowOneYearInitiatedART.setName("femalePatientBelowOneYearInitiatedART");
        femalePatientBelowOneYearInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBelowOneYearInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalePatientBelowOneYearInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalePatientBelowOneYearInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientBelowOnYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBelowOneYearInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBelowOneYearInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalePatientBelowOneYearInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalePatientBelowOneYearInitiatedARTIndicator= Indicators.newCohortIndicator("femalePatientBelowOneYearOnARTIndicator",
                femalePatientBelowOneYearInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B14","Total number of female patients with less than 1 year who initiated ART",new Mapped(femalePatientBelowOneYearInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //============================================
        // B15 Total number of male patients between 1 and 4 years who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsBitween1And4YearsInitiatedART=new CompositionCohortDefinition();
        malepatientsBitween1And4YearsInitiatedART.setName("malepatientsBitween1And4YearsInitiatedART");
        malepatientsBitween1And4YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween1And4YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsBitween1And4YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsBitween1And4YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween1And4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween1And4YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween1And4YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsBitween1And4YearsInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween1And4YearsInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsBitween1And4YearsOnARTIndicator",
                malepatientsBitween1And4YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B15","Total number of male patients between 1 and 4 years who initiated ART",new Mapped(malepatientsBitween1And4YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B16 Total number of female patients between 1 and 4 years who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween1And4YearsInitiatedART=new CompositionCohortDefinition();
        femalepatientsBitween1And4YearsInitiatedART.setName("femalepatientsBitween1And4YearsOnART");
        femalepatientsBitween1And4YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween1And4YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsBitween1And4YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsBitween1And4YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween1And4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween1And4YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween1And4YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsBitween1And4YearsInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween1And4YearsInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween1And4YearsInitiatedARTIndicator",
                femalepatientsBitween1And4YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B16","Total number of female patients between 1 and 4 years who initiated ART",new Mapped(femalepatientsBitween1And4YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");





        //============================================
        // B17 Total number of male patients between 5 and 9 years who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsBitween5And9YearsInitiatedART=new CompositionCohortDefinition();
        malepatientsBitween5And9YearsInitiatedART.setName("malepatientsBitween1And4YearsInitiatedART");
        malepatientsBitween5And9YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween5And9YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsBitween5And9YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsBitween5And9YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween5And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween5And9YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween5And9YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsBitween5And9YearsInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween5And9YearsInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsBitween5And9YearsInitiatedARTIndicator",
                malepatientsBitween5And9YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B17","Total number of male patients between 5 and 9 years who initiated ART",new Mapped(malepatientsBitween5And9YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B18 Total number of female patients between 5 and 9 years who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween5And9YearsInitiatedART=new CompositionCohortDefinition();
        femalepatientsBitween5And9YearsInitiatedART.setName("femalepatientsBitween5And9YearsInitiatedART");
        femalepatientsBitween5And9YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween5And9YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsBitween5And9YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsBitween5And9YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween5And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween5And9YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween5And9YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsBitween5And9YearsInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween5And9YearsInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween1And4YearsInitiatedARTIndicator",
                femalepatientsBitween5And9YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B18","Total number of female patients between 5 and 9 years who initiated ART",new Mapped(femalepatientsBitween5And9YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //============================================
        // B19 Total number of male patients between 10 and 14 years who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsBitween10And14YearsInitiatedART=new CompositionCohortDefinition();
        malepatientsBitween10And14YearsInitiatedART.setName("malepatientsBitween10And14YearsInitiatedART");
        malepatientsBitween10And14YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween10And14YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsBitween10And14YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsBitween10And14YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween10And14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween10And14YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween10And14YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsBitween10And14YearsInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween10And14YearsInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsBitween10And14YearsInitiatedARTIndicator",
                malepatientsBitween10And14YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B19","Total number of male patients between 10 and 14 years who initiated ART",new Mapped(malepatientsBitween10And14YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B20 Total number of female patients between 10 and 14 years who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween10And14YearsInitiatedART=new CompositionCohortDefinition();
        femalepatientsBitween10And14YearsInitiatedART.setName("femalepatientsBitween10And14YearsInitiatedART");
        femalepatientsBitween10And14YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween10And14YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsBitween10And14YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsBitween10And14YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween10And14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween10And14YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween10And14YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsBitween10And14YearsInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween10And14YearsInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween10And14YearsInitiatedARTIndicator",
                femalepatientsBitween10And14YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B20","Total number of female patients between 10 and 14 years who initiated ART",new Mapped(femalepatientsBitween10And14YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");




        //============================================
        // B21 Total number of male patients between 15 and 19 years who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsBitween15And19YearsInitiatedART=new CompositionCohortDefinition();
        malepatientsBitween15And19YearsInitiatedART.setName("malepatientsBitween15And19YearsInitiatedART");
        malepatientsBitween15And19YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween15And19YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsBitween15And19YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsBitween15And19YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween15And19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween15And19YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween15And19YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsBitween15And19YearsInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween15And19YearsInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsBitween15And19YearsInitiatedARTIndicator",
                malepatientsBitween15And19YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B21","Total number of male patients between 10 and 14 years who initiated ART",new Mapped(malepatientsBitween15And19YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B22 Total number of female patients between 15 and 19 years who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween15And19YearsInitiatedART=new CompositionCohortDefinition();
        femalepatientsBitween15And19YearsInitiatedART.setName("femalepatientsBitween15And19YearsInitiatedART");
        femalepatientsBitween15And19YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween15And19YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsBitween15And19YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsBitween15And19YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween15And19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween15And19YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween15And19YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsBitween15And19YearsInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween15And19YearsInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween10And14YearsInitiatedARTIndicator",
                femalepatientsBitween15And19YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B22","Total number of female patients between 15 and 19 years who initiated ART",new Mapped(femalepatientsBitween15And19YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//=======================================
// Other age range
//======================================
        AgeCohortDefinition patientsBitween20And24Years=patientWithAgeBetween(20,24);
        AgeCohortDefinition patientsBitween25And49Years=patientWithAgeBetween(25,49);
        AgeCohortDefinition patientsWith50YearsAndAbove=patientWithAgeAbove(50);




        //============================================
        // B23 Total number of male patients between 20 and 24 years who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsBitween20And24YearsInitiatedART=new CompositionCohortDefinition();
        malepatientsBitween20And24YearsInitiatedART.setName("malepatientsBitween20And24YearsInitiatedART");
        malepatientsBitween20And24YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween20And24YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsBitween20And24YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsBitween20And24YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween20And24Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween20And24YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween20And24YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsBitween20And24YearsInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween20And24YearsInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsBitween20And24YearsInitiatedARTIndicator",
                malepatientsBitween20And24YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B23","Total number of male patients between 20 and 24 years who initiated ART",new Mapped(malepatientsBitween20And24YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B24 Total number of female patients between 15 and 19 years who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween20And24YearsInitiatedART=new CompositionCohortDefinition();
        femalepatientsBitween20And24YearsInitiatedART.setName("femalepatientsBitween20And24YearsInitiatedART");
        femalepatientsBitween20And24YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween20And24YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsBitween20And24YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsBitween20And24YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween20And24Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween20And24YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween20And24YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsBitween20And24YearsInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween20And24YearsInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween20And24YearsInitiatedARTIndicator",
                femalepatientsBitween20And24YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B24","Total number of female patients between 20 and 24 years who initiated ART",new Mapped(femalepatientsBitween20And24YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //============================================
        // B25 Total number of male patients between 25 and 49 years who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsBitween25And49YearsInitiatedART=new CompositionCohortDefinition();
        malepatientsBitween25And49YearsInitiatedART.setName("malepatientsBitween25And49YearsInitiatedART");
        malepatientsBitween25And49YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsBitween25And49YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsBitween25And49YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsBitween25And49YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween25And49Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsBitween25And49YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsBitween25And49YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsBitween25And49YearsInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsBitween25And49YearsInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsBitween25And49YearsInitiatedARTIndicator",
                malepatientsBitween25And49YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B25","Total number of male patients between 25 and 49 years who initiated ART",new Mapped(malepatientsBitween25And49YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B26 Total number of female patients between 25 and 49 years who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsBitween25And49YearsInitiatedART=new CompositionCohortDefinition();
        femalepatientsBitween25And49YearsInitiatedART.setName("femalepatientsBitween25And49YearsInitiatedART");
        femalepatientsBitween25And49YearsInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsBitween25And49YearsInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsBitween25And49YearsInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsBitween25And49YearsInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween25And49Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsBitween25And49YearsInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsBitween25And49YearsInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsBitween25And49YearsInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsBitween25And49YearsInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsBitween25And49YearsInitiatedARTIndicator",
                femalepatientsBitween25And49YearsInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B26","Total number of female patients between 25 and 49 years who initiated ART",new Mapped(femalepatientsBitween25And49YearsInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //============================================
        // B27 Total number of male patients with 50 years and above who initiated ART
        //==========================================


        CompositionCohortDefinition malepatientsWith50YearsAndAboveInitiatedART=new CompositionCohortDefinition();
        malepatientsWith50YearsAndAboveInitiatedART.setName("malepatientsWith50YearsAndAboveInitiatedART");
        malepatientsWith50YearsAndAboveInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malepatientsWith50YearsAndAboveInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        malepatientsWith50YearsAndAboveInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        malepatientsWith50YearsAndAboveInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith50YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malepatientsWith50YearsAndAboveInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malepatientsWith50YearsAndAboveInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        malepatientsWith50YearsAndAboveInitiatedART.setCompositionString("1 and 2 and 3");

        CohortIndicator malepatientsWith50YearsAndAboveInitiatedARTIndicator= Indicators.newCohortIndicator("malepatientsWith50YearsAndAboveInitiatedARTIndicator",
                malepatientsWith50YearsAndAboveInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B27","Total number of male patients with 50 years and above who initiated ART",new Mapped(malepatientsWith50YearsAndAboveInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==========================================
        // B28 Total number of female patients with 50 years and above who initiated ART
        //==========================================

        CompositionCohortDefinition femalepatientsWith50YearsAndAboveInitiatedART=new CompositionCohortDefinition();
        femalepatientsWith50YearsAndAboveInitiatedART.setName("femalepatientsWith50YearsAndAboveInitiatedART");
        femalepatientsWith50YearsAndAboveInitiatedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalepatientsWith50YearsAndAboveInitiatedART.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        femalepatientsWith50YearsAndAboveInitiatedART.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));
        femalepatientsWith50YearsAndAboveInitiatedART.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith50YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalepatientsWith50YearsAndAboveInitiatedART.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalepatientsWith50YearsAndAboveInitiatedART.getSearches().put("3",new Mapped<CohortDefinition>(patientInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startedOnOrAfter},startedOnOrBefore=${startedOnOrBefore}")));
        femalepatientsWith50YearsAndAboveInitiatedART.setCompositionString("1 and 3 and (not 2)");

        CohortIndicator femalepatientsWith50YearsAndAboveInitiatedARTIndicator= Indicators.newCohortIndicator("femalepatientsWith50YearsAndAboveInitiatedARTIndicator",
                femalepatientsWith50YearsAndAboveInitiatedART, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B28","Total number of female patients with 50 years and above who initiated ART",new Mapped(femalepatientsWith50YearsAndAboveInitiatedARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


//=====================================================
// B29 Number of male patients with less than 1 year currently on ARVs who have been initiated on TB treatment
//======================================================

        List<Concept> TBDrugsConceptAnswers=TBDrugsConcept.getSetMembers();

        StringBuilder answers=new StringBuilder();

        int i=0;

        for(Concept concept:TBDrugsConceptAnswers){

            if(i==0){
                answers.append(concept.getConceptId());
            }else {
                answers.append(",");
                answers.append(concept.getConceptId());

            }
            i++;

        }


        SqlCohortDefinition patientsInitiatedTBDrugs=new SqlCohortDefinition();
        patientsInitiatedTBDrugs.setQuery("select patient_id from orders where concept_id in ("+answers.toString()+") and start_date>= :onOrAfter and start_date<= :onOrBefore and voided=0");
        patientsInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        patientsInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));




        CompositionCohortDefinition malePatientBelowOneYearOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBelowOneYearOnARTInitiatedTBDrugs.setName("malePatientBelowOneYearOnARTInitiatedTBDrugs");
        malePatientBelowOneYearOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientBelowOnYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBelowOneYearOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBelowOneYearOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBelowOneYearOnARTInitiatedTBDrugsIndicator",
                malePatientBelowOneYearOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B29","Number of male patients with less than 1 year currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBelowOneYearOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");




        //=====================================================
        // B30 Number of female patients with less than 1 year currently on ARVs who have been initiated on TB treatment
        //======================================================


        CompositionCohortDefinition femalePatientBelowOneYearOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.setName("malePatientBelowOneYearOnARTInitiatedTBDrugs");
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientBelowOnYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBelowOneYearOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBelowOneYearOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBelowOneYearOnARTInitiatedTBDrugsIndicator",
                femalePatientBelowOneYearOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B30","Number of female patients with less than 1 year currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBelowOneYearOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B31: Number of male patients between 1 and 4 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.setName("malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs");
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween1And4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator",
                malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B31","Number of female patients between 1 and 4 years currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B32: Number of female patients between 1 and 4 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.setName("femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs");
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween1And4Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator",
                femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B32","Number of female patients between 1 and 4 years currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");




        //===============================================================================
        // B33: Number of male patients between 5 and 9 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.setName("malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs");
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween5And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator",
                malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B33","Number of female patients between 5 and 9 years currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B34: Number of female patients between 5 and 9 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.setName("femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs");
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween5And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugsIndicator",
                femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B34","Number of female patients between 5 and 9 years currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //===============================================================================
        // B35: Number of male patients between 10 and 14 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientBetween10And14YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.setName("malePatientBetween10And14YearsOnARTInitiatedTBDrugs");
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween10And14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween10And14YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBetween10And14YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBetweenOneAndFourYearsOnARTInitiatedTBDrugsIndicator",
                malePatientBetween10And14YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B35","Number of female patients between 10 and 14 years currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBetween10And14YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B36: Number of female patients between 10 and 14 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientBetween10And14YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.setName("femalePatientBetweenFiveAndNineYearsOnARTInitiatedTBDrugs");
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween10And14Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween10And14YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBetween10And14YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBetween10And14YearsOnARTInitiatedTBDrugsIndicator",
                femalePatientBetween10And14YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B36","Number of female patients between 10 and 14 years currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBetween10And14YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //===============================================================================
        // B37: Number of male patients between 15 and 19 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientBetween15And19YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.setName("malePatientBetween15And19YearsOnARTInitiatedTBDrugs");
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween15And19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween15And19YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBetween15And19YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBetween15And19YearsOnARTInitiatedTBDrugsIndicator",
                malePatientBetween15And19YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B37","Number of female patients between 15 and 19 years currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBetween15And19YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B38: Number of female patients between 15 and 19 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientBetween15And19YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.setName("femalePatientBetween15And19YearsOnARTInitiatedTBDrugs");
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween15And19Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween15And19YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBetween15And19YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBetween14And19YearsOnARTInitiatedTBDrugsIndicator",
                femalePatientBetween15And19YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B38","Number of female patients between 15 and 19 years currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBetween15And19YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B39: Number of male patients between 20 and 24 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientBetween20And24YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.setName("malePatientBetween20And24YearsOnARTInitiatedTBDrugs");
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween20And24Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween20And24YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBetween20And24YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBetween20And24YearsOnARTInitiatedTBDrugsIndicator",
                malePatientBetween20And24YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B39","Number of female patients between 20 and 24 years currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBetween20And24YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B40: Number of female patients between 20 and 24 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientBetween20And24YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.setName("femalePatientBetween20And24YearsOnARTInitiatedTBDrugs");
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween20And24Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween20And24YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBetween20And24YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBetween20And24YearsOnARTInitiatedTBDrugsIndicator",
                femalePatientBetween20And24YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B40","Number of female patients between 20 and 24 years currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBetween20And24YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B41: Number of male patients between 25 and  49 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientBetween25And49YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.setName("malePatientBetween20And24YearsOnARTInitiatedTBDrugs");
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween25And49Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientBetween25And49YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientBetween25And49YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientBetween25And49YearsOnARTInitiatedTBDrugsIndicator",
                malePatientBetween25And49YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B41","Number of female patients between 25 and 49 years currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientBetween25And49YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B42: Number of female patients between 25 and 49 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientBetween25And49YearsOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.setName("femalePatientBetween25And49YearsOnARTInitiatedTBDrugs");
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsBitween25And49Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientBetween25And49YearsOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientBetween25And49YearsOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientBetween25And49YearsOnARTInitiatedTBDrugsIndicator",
                femalePatientBetween25And49YearsOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B42","Number of female patients between 20 and 24 years currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientBetween25And49YearsOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //===============================================================================
        // B43: Number of male patients with 50 years and above currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.setName("malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs");
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith50YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator malePatientWith50YearsAndAboveOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("malePatientWith50YearsAndAboveOnARTInitiatedTBDrugsIndicator",
                malePatientWith50YearsAndAboveOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B43","Number of male patients with 50 years and above currently on ARVs who have been initiated on TB treatment",new Mapped(malePatientWith50YearsAndAboveOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //===============================================================================
        // B44: Number of female patients between 25 and 49 years currently on ARVs who have been initiated on TB treatment
        //==============================================================================

        CompositionCohortDefinition femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs=new CompositionCohortDefinition();
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.setName("femalePatientBetween25And49YearsOnARTInitiatedTBDrugs");
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith50YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("3",new Mapped<CohortDefinition>(onART, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.getSearches().put("4",new Mapped<CohortDefinition>(patientsInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugsIndicator= Indicators.newCohortIndicator("femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugsIndicator",
                femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugs, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B44","Number of female patients with 50 years and above currently on ARVs who have been initiated on TB treatment",new Mapped(femalePatientWith50YearsAndAboveOnARTInitiatedTBDrugsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==================================================================================
        // B45 Number of male pediatric ( < 15 years ) patients unstable on 1st line regimen
        //==================================================================================

        AgeCohortDefinition patientBelow15Years=patientWithAgeBelow(15);
        AgeCohortDefinition patientsWith15YearsAndAbove=patientWithAgeAbove(15);

        InStateCohortDefinition unstablePatients= patientInModel(unstable);

        InStateCohortDefinition stablePatients= patientInModel(stable);

        InStateCohortDefinition patientsOn1stLineRegimen= patientInRegimenStatus(firstLineRegiment);

        InStateCohortDefinition patientsOn2ndLineRegimen= patientInRegimenStatus(secondLineRegiment);

        InStateCohortDefinition patientsOn3rdLineRegimen= patientInRegimenStatus(thrirdLineRegiment);




        CompositionCohortDefinition maleUnstablePatientsBelow15YearsOn1stLineRegimen=new CompositionCohortDefinition();
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.setName("maleUnstablePatientsBelow15YearsOn1stLineRegimen");
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientBelow15Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsBelow15YearsOn1stLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleUnstablePatientsBelow15YearsOn1stLineRegimenIndicator= Indicators.newCohortIndicator("maleUnstablePatientsBelow15YearsOn1stLineRegimenIndicator",
                maleUnstablePatientsBelow15YearsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


         dsd.addColumn("B45","Number of male pediatric ( < 15 years ) patients unstable on 1st line regimen",new Mapped(maleUnstablePatientsBelow15YearsOn1stLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B46 Number of female pediatric ( < 15 years ) patients unstable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition femaleUnstablePatientsBelow15YearsOn1stLineRegimen=new CompositionCohortDefinition();
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.setName("femaleUnstablePatientsBelow15YearsOn1stLineRegimen");
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientBelow15Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsBelow15YearsOn1stLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleUnstablePatientsBelow15YearsOn1stLineRegimenIndicator= Indicators.newCohortIndicator("femaleUnstablePatientsBelow15YearsOn1stLineRegimenIndicator",
                femaleUnstablePatientsBelow15YearsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B46","Number of female pediatric ( < 15 years ) patients unstable on 1st line regimen",new Mapped(femaleUnstablePatientsBelow15YearsOn1stLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B47 Number of male pediatric ( < 15 years ) patients unstable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition maleUnstablePatientsBelow15YearsOn2ndLineRegimen=new CompositionCohortDefinition();
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.setName("maleUnstablePatientsBelow15YearsOn2ndLineRegimen");
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientBelow15Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsBelow15YearsOn2ndLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleUnstablePatientsBelow15YearsOn2ndLineRegimenIndicator= Indicators.newCohortIndicator("maleUnstablePatientsBelow15YearsOn2ndLineRegimenIndicator",
                maleUnstablePatientsBelow15YearsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B47","Number of male pediatric ( < 15 years ) patients unstable on 2nd line regimen",new Mapped(maleUnstablePatientsBelow15YearsOn2ndLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B48 Number of female pediatric ( < 15 years ) patients unstable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition femaleUnstablePatientsBelow15YearsOn2ndLineRegimen=new CompositionCohortDefinition();
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.setName("femaleUnstablePatientsBelow15YearsOn1stLineRegimen");
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientBelow15Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsBelow15YearsOn2ndLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleUnstablePatientsBelow15YearsOn2ndLineRegimenIndicator= Indicators.newCohortIndicator("femaleUnstablePatientsBelow15YearsOn2ndLineRegimenIndicator",
                femaleUnstablePatientsBelow15YearsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B48","Number of female pediatric ( < 15 years ) patients unstable on 2nd line regimen",new Mapped(femaleUnstablePatientsBelow15YearsOn2ndLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B49 Number of male pediatric ( < 15 years ) patients unstable on 3rd line regimen
        //==================================================================================

        CompositionCohortDefinition maleUnstablePatientsBelow15YearsOn3rdLineRegimen=new CompositionCohortDefinition();
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.setName("maleUnstablePatientsBelow15YearsOn3rdLineRegimen");
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientBelow15Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsBelow15YearsOn3rdLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleUnstablePatientsBelow15YearsOn3rdLineRegimenIndicator= Indicators.newCohortIndicator("maleUnstablePatientsBelow15YearsOn3rdLineRegimenIndicator",
                maleUnstablePatientsBelow15YearsOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B49","Number of male pediatric ( < 15 years ) patients unstable on 3rd line regimen",new Mapped(maleUnstablePatientsBelow15YearsOn3rdLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B50 Number of female pediatric ( < 15 years ) patients unstable on 3rd line regimen
        //==================================================================================

        CompositionCohortDefinition femaleUnstablePatientsBelow15YearsOn3rdLineRegimen=new CompositionCohortDefinition();
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.setName("femaleUnstablePatientsBelow15YearsOn3rdLineRegimen");
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientBelow15Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsBelow15YearsOn3rdLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleUnstablePatientsBelow15YearsOn3rdLineRegimenIndicator= Indicators.newCohortIndicator("femaleUnstablePatientsBelow15YearsOn3rdLineRegimenIndicator",
                femaleUnstablePatientsBelow15YearsOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B50","Number of female pediatric ( < 15 years ) patients unstable on 3rd line regimen",new Mapped(femaleUnstablePatientsBelow15YearsOn3rdLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B51 Number of male adult ( >= 15 years ) patients stable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition maleStablePatientsWith15YearsAndAboveOn1stLineRegimen=new CompositionCohortDefinition();
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.setName("maleStablePatientsWith15YearsAndAboveOn1stLineRegimen");
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(stablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleStablePatientsWith15YearsAndAboveOn1stLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator= Indicators.newCohortIndicator("maleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator",
                maleStablePatientsWith15YearsAndAboveOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B51"," Number of male adult ( >= 15 years ) patients stable on 1st line regimen",new Mapped(maleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B52 Number of female adult ( >= 15 years ) patients stable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen=new CompositionCohortDefinition();
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.setName("femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen");
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(stablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator= Indicators.newCohortIndicator("femaleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator",
                femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B52"," Number of male adult ( >= 15 years ) patients stable on 1st line regimen",new Mapped(femaleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B53 Number of male adult ( >= 15 years ) patients unstable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen=new CompositionCohortDefinition();
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.setName("maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen");
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator= Indicators.newCohortIndicator("maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator",
                maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B53","Number of male adult ( >= 15 years ) patients stable on 1st line regimen",new Mapped(maleStablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B54 Number of female adult ( >= 15 years ) patients unstable on 1st line regimen
        //==================================================================================

        CompositionCohortDefinition femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen=new CompositionCohortDefinition();
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.setName("femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen");
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator= Indicators.newCohortIndicator("femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator",
                femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B54"," Number of male adult ( >= 15 years ) patients stable on 1st line regimen",new Mapped(femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==================================================================================
        // B55 Number of male adult ( >= 15 years ) patients stable on 2nd line regimen
        //==================================================================================

        CompositionCohortDefinition maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen=new CompositionCohortDefinition();
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.setName("maleStablePatientsWith15YearsAndAboveOn1stLineRegimen");
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(stablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleStablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator= Indicators.newCohortIndicator("maleStablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator",
                maleStablePatientsWith15YearsAndAboveOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B55"," Number of male adult ( >= 15 years ) patients stable on 2nd line regimen",new Mapped(maleStablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B56 Number of female adult ( >= 15 years ) patients stable on 2nd line regimen
        //==================================================================================

        CompositionCohortDefinition femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen=new CompositionCohortDefinition();
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.setName("femaleStablePatientsWith15YearsAndAboveOn1stLineRegimen");
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(stablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator= Indicators.newCohortIndicator("femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator",
                femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B56"," Number of male adult ( >= 15 years ) patients stable on 2nd line regimen",new Mapped(femaleStablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B57 Number of male adult ( >= 15 years ) patients unstable on 2nd line regimen
        //==================================================================================

        CompositionCohortDefinition maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen=new CompositionCohortDefinition();
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.setName("maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen");
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator= Indicators.newCohortIndicator("maleUnstablePatientsWith15YearsAndAboveOn1stLineRegimenIndicator",
                maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B57","Number of male adult ( >= 15 years ) patients stable on 2nd line regimen",new Mapped(maleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B58 Number of female adult ( >= 15 years ) patients unstable on 2nd line regimen
        //==================================================================================

        CompositionCohortDefinition femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen=new CompositionCohortDefinition();
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.setName("femaleUnstablePatientsWith15YearsAndAboveOn1stLineRegimen");
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator= Indicators.newCohortIndicator("femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator",
                femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B58"," Number of male adult ( >= 15 years ) patients stable on 2nd line regimen",new Mapped(femaleUnstablePatientsWith15YearsAndAboveOn2ndLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        //==================================================================================
        // B59 Number of male adult ( >= 15 years ) patients unstable on 3rd line regimen
        //==================================================================================

        CompositionCohortDefinition maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen=new CompositionCohortDefinition();
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.setName("maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen");
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.setCompositionString("1 and 2 and 3 and 4");

        CohortIndicator maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimenIndicator= Indicators.newCohortIndicator("maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimenIndicator",
                maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B59","Number of male adult ( >= 15 years ) patients stable on 3rd line regimen",new Mapped(maleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //==================================================================================
        // B60 Number of female adult ( >= 15 years ) patients unstable on 3rd line regimen
        //==================================================================================

        CompositionCohortDefinition femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen=new CompositionCohortDefinition();
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.setName("femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen");
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("1",new Mapped<CohortDefinition>(patientsWith15YearsAndAbove, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("2",new Mapped<CohortDefinition>(males, null));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("3",new Mapped<CohortDefinition>(unstablePatients, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.getSearches().put("4",new Mapped<CohortDefinition>(patientsOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen.setCompositionString("1 and 3 and 4 and (not 2)");

        CohortIndicator femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimenIndicator= Indicators.newCohortIndicator("femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimenIndicator",
                femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimen, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));


        dsd.addColumn("B60"," Number of male adult ( >= 15 years ) patients stable on 3rd line regimen",new Mapped(femaleUnstablePatientsWith15YearsAndAboveOn3rdLineRegimenIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//==========================================================
// B61 Total number of patients under ART at the end of last month
//==========================================================
        CohortIndicator patientOnARTIndicator= Indicators.newCohortIndicator("patientOnARTIndicator",
                onART, ParameterizableUtil.createParameterMappings("onOrAfter=${endDate},onOrBefore=${endDate}"));


        dsd.addColumn("B61"," Total number of patients under ART at the end of last month",new Mapped(patientOnARTIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //============================================================
        // B62 Total number of patients under ART who died this month
        //===================================================================


        PatientStateCohortDefinition diedPatientsInThisMonth=new PatientStateCohortDefinition();
        diedPatientsInThisMonth.setName("diedPatientsInThisMonth");
        diedPatientsInThisMonth.addState(p.getWorkflowByName("TREATMENT STATUS").getState(patientDied));
        diedPatientsInThisMonth.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        diedPatientsInThisMonth.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));

        CohortIndicator diedPatientsInThisMonthIndicator= Indicators.newCohortIndicator("diedPatientsInThisMonthIndicator",
                diedPatientsInThisMonth, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate}"));


        dsd.addColumn("B62","Total number of patients under ART who died this month",new Mapped(diedPatientsInThisMonthIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

        //=========================================
        // B63 Total number of patients under ART lost to follow up this month
        //=========================================



        CodedObsCohortDefinition exitedPatientsWithDefaultedReason=new CodedObsCohortDefinition();
        exitedPatientsWithDefaultedReason.setName("exitedPatientsWithDefaultedReason");
        exitedPatientsWithDefaultedReason.setTimeModifier(TimeModifier.LAST);
        exitedPatientsWithDefaultedReason.setOperator(SetComparator.IN);
        exitedPatientsWithDefaultedReason.setQuestion(reasonForExitingFromCare);
        exitedPatientsWithDefaultedReason.addValue(patientDefaulted);
        exitedPatientsWithDefaultedReason.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        exitedPatientsWithDefaultedReason.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));

        CohortIndicator lostpatientsInThisMonthIndicator= Indicators.newCohortIndicator("lostpatientsInThisMonthIndicator",
                exitedPatientsWithDefaultedReason, ParameterizableUtil.createParameterMappings("onOrAfter=${endDate},onOrBefore=${endDate}"));


        dsd.addColumn("B63","Total number of patients under ART at the end of last month",new Mapped(lostpatientsInThisMonthIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//============================================================================
// B64 Total number of patients under ART retraced this month
//============================================================================


        InStateCohortDefinition defaultePatientState=new InStateCohortDefinition();
        defaultePatientState.addState(p.getWorkflowByName("TREATMENT STATUS").getState(patientDefaulted));
        defaultePatientState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        defaultePatientState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));

        EncounterCohortDefinition patientsWithHIVEncounter=new EncounterCohortDefinition();
        patientsWithHIVEncounter.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientsWithHIVEncounter.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
        patientsWithHIVEncounter.addEncounterType(HIVVisitEncType);

        CompositionCohortDefinition retracedPatient=new CompositionCohortDefinition();
        retracedPatient.setName("retracedPatient");
        retracedPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        retracedPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        retracedPatient.getSearches().put("1",new Mapped<CohortDefinition>(defaultePatientState, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        retracedPatient.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithHIVEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
        retracedPatient.setCompositionString("1 and 2");

        CohortIndicator retracedPatientIndicator= Indicators.newCohortIndicator("retracedPatientIndicator",
                retracedPatient, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));


        dsd.addColumn("B64","Total number of patients under ART retraced this month",new Mapped(retracedPatientIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");



        //======================================================
        // B65 Total number of patients under ART transferred out this month
        //======================================================


        PatientStateCohortDefinition transferOutPatientsInThisMonth=new PatientStateCohortDefinition();
        transferOutPatientsInThisMonth.setName("transferOutPatientsInThisMonth");
        transferOutPatientsInThisMonth.addState(p.getWorkflowByName("TREATMENT STATUS").getState(transferedOut));
        transferOutPatientsInThisMonth.addParameter(new Parameter("startedOnOrAfter", "startedOnOrAfter", Date.class));
        transferOutPatientsInThisMonth.addParameter(new Parameter("startedOnOrBefore", "startedOnOrBefore", Date.class));

        CohortIndicator transferOutPatientsInThisMonthIndicator= Indicators.newCohortIndicator("transferOutPatientsInThisMonthIndicator",
                transferOutPatientsInThisMonth, ParameterizableUtil.createParameterMappings("startedOnOrAfter=${startDate},startedOnOrBefore=${endDate}"));


        dsd.addColumn("B65","Total number of patients under ART transferred out this month",new Mapped(transferOutPatientsInThisMonthIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");

//===============================================
// B66 Total number of patients under ART transferred in this month
//===============================================
        CohortIndicator patientsWithTransferInEncounterIndicator= Indicators.newCohortIndicator("patientsWithTransferInEncounterIndicator",
                patientsWithTransferInEncounter, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));


        dsd.addColumn("B66","Total number of patients under ART transferred in this month ",new Mapped(patientsWithTransferInEncounterIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}")),"");


        return  dsd;
    }


    private AgeCohortDefinition patientWithAgeBelow(int age){
        AgeCohortDefinition patientsWithAgebilow=new AgeCohortDefinition();
        patientsWithAgebilow.setName("patientsWithAgebilow");
        patientsWithAgebilow.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAgebilow.setMaxAge(age-1);
        patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
        return patientsWithAgebilow;
    }
    private AgeCohortDefinition patientWithAgeBelowAndIncuded(int age){
        AgeCohortDefinition patientsWithAgebilow=new AgeCohortDefinition();
        patientsWithAgebilow.setName("patientsWithAgebilow");
        patientsWithAgebilow.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAgebilow.setMaxAge(age);
        patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
        return patientsWithAgebilow;
    }
    private AgeCohortDefinition patientWithAgeBetween(int age1,int age2){
        AgeCohortDefinition patientsWithAge=new AgeCohortDefinition();
        patientsWithAge.setName("patientsWithAge");
        patientsWithAge.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAge.setMinAge(age1);
        patientsWithAge.setMaxAge(age2);
        patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
        patientsWithAge.setMaxAgeUnit(DurationUnit.YEARS);
        return patientsWithAge;
    }

    private AgeCohortDefinition patientWithAgeAbove(int age){
        AgeCohortDefinition patientsWithAge=new AgeCohortDefinition();
        patientsWithAge.setName("patientsWithAge");
        patientsWithAge.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
        patientsWithAge.setMinAge(age);
        patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
        return patientsWithAge;
    }
    private InStateCohortDefinition patientInRegimenStatus(Concept state){
        InStateCohortDefinition inState=new InStateCohortDefinition();
        inState.addState(p.getWorkflowByName("REGIMEN STATUS").getState(state));
        inState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        inState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        return inState;
    }

    private InStateCohortDefinition patientInModel(Concept state){
        InStateCohortDefinition inState=new InStateCohortDefinition();
        inState.addState(p.getWorkflowByName("Model").getState(state));
        inState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        inState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        return inState;
    }

}
