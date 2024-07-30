package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class SetupPathologyRequestReport implements SetupReport {

    protected final Log log = LogFactory.getLog(getClass());
    GlobalPropertiesManagement gp = new GlobalPropertiesManagement();


    private EncounterType pathologyEncounterType;
    private PersonAttributeType healthCenterPersonAttributeType;
//    private PersonAttributeType phoneNumberPersonAttributeType;
    private Form pathologyRequestForm;
    private Concept sampleStatusConcept;
    private Concept referralStatusConcept;
    private Concept sampleDropOffConcept;
    private Concept pathologyRequestEncounterUUID;
    private Concept telephoneNumberConcept;
    private Concept PATHOLOGYREQUESTRESULTSAPPROVED;
    private Concept pathologicDiagnoisis;
    private PatientIdentifierType patientIMBPrimaryCareId;


    /**
     * @return
     */
    @Override
    public String getReportName() {
        return null;
    }

    public void setup() throws Exception {

        setupProperties();

        ReportDefinition rd =createReportDefinition();
        ReportDesign designCSV = Helper.createCsvReportDesign(rd,"Pathology Request Report.csv_");
        Helper.saveReportDesign(designCSV);
    }

    public void delete() {
        Helper.purgeReportDefinition("Pathology Request Report");
    }

    private ReportDefinition createReportDefinition() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Pathology Request Report");
        reportDefinition.setUuid("996cf192-ff54-11eb-a63a-080027ce9ca0");
        Parameter location = new Parameter("location", "Location", Location.class);
        // Parameter baseEnc = new Parameter("baseEnc","Base Encounter",String.class);
        // Parameter limitNumber = new Parameter("limitNumber", "Limit Number", String.class);
        location.setRequired(false);
        // baseEnc.setRequired(false);
        //limitNumber.setRequired(false);
        reportDefinition.addParameter(location);
        // reportDefinition.addParameter(baseEnc);
        // reportDefinition.addParameter(limitNumber);

        createDataSetDefinition(reportDefinition);

        Helper.saveReportDefinition(reportDefinition);

        return reportDefinition;

    }


    private void createDataSetDefinition(ReportDefinition reportDefinition) {


        SqlDataSetDefinition sqldsd=new SqlDataSetDefinition();

        sqldsd.setSqlQuery("select \n" +
               " (select count(*) from encounter enc \n" +
                    "\t left join person p on enc.patient_id=p.person_id\n" +
                    "    left join (select person_id,name,location_id,retired from person_attribute pat left join location l on pat.value=l.location_id where person_attribute_type_id= "+ healthCenterPersonAttributeType.getPersonAttributeTypeId() +" and pat.voided=0 group by person_id) \n" +
                    "\t\t\t\thealthcenter on enc.patient_id=healthcenter.person_id\n" +
                    "where enc.voided=0 and p.voided=0 and enc.form_id= " + pathologyRequestForm.getFormId() +
                    " and p.dead=0 and IF( :location IS NULL, true, healthcenter.location_id= :location) ) as totalRows, " +

                "\t p.person_id as personId,\n" +
                "\tp.uuid as patientUuid,\n" +
                "\tp.birthdate as personBirthdate,\n" +
                "\tp.gender as personGender,\n" +
                "\t(select family_name from person_name where preferred=1 and voided=0 and  enc.patient_id=person_id order by person_name_id limit 1 ) as family_name,\n" +
                "    (select family_name2 from person_name where preferred=1 and voided=0 and  enc.patient_id=person_id order by person_name_id limit 1 ) as family_name2,\n" +
                "    (select middle_name from person_name where preferred=1 and voided=0 and  enc.patient_id=person_id order by person_name_id limit 1 ) as middle_name,\n" +
                "    (select given_name from person_name where preferred=1 and voided=0 and  enc.patient_id=person_id order by person_name_id limit 1 ) as given_name,  \n" +
                "     (SELECT identifier FROM patient_identifier where patient_id=enc.patient_id and voided = 0 and identifier_type in (select patient_identifier_type_id from patient_identifier_type where name='IMB Primary Care Registration ID') limit 1) as IMBPrimaryCare, \n" +
                "    (select value_text from obs  where concept_id= " + telephoneNumberConcept.getConceptId() + " and voided=0 and enc.patient_id = person_id order by obs_id desc limit 1) as patientPhoneNumber,\n" +
                "    healthcenter.name as patientHealthCenter,\n" +
                "    enc.encounter_id as encounterId,\n" +
                "    enc.uuid as encounterUuid,\n" +
                "    DATE_FORMAT(enc.encounter_datetime, \"%Y/%m/%d\") as encounterDatetime, \n" +
                "    (select cn.name from obs o left join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + sampleStatusConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\" and o.encounter_id = enc.encounter_id order by obs_id desc limit 1 ) as sampleStatusObs, \n" +
                "    (select o.uuid from obs o left join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\twhere o.concept_id= " + sampleStatusConcept.getConceptId() + "  and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\" and o.encounter_id = enc.encounter_id order by obs_id desc limit 1 ) as sampleStatusObsUuid,\n" +
                "    (select cn.name from obs o left join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + referralStatusConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\" and o.encounter_id = enc.encounter_id order by obs_id desc limit 1 ) as referralStatusObs, \n" +
                "    (select o.uuid from obs o left join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\twhere o.concept_id= " + referralStatusConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\" and o.encounter_id = enc.encounter_id order by obs_id desc limit 1 ) as referralStatusObsUuid, \n" +
                "    (select cn.name from obs o left join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + sampleDropOffConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\" and o.encounter_id = enc.encounter_id order by obs_id desc limit 1 ) as sampleDropoffObs, \n" +
                "    (select o.uuid from obs o left join concept_name cn on o.value_coded=cn.concept_id  \n" +
                "\t\t\t\t\twhere o.concept_id= " + sampleDropOffConcept.getConceptId() + " and cn.concept_name_type=\"FULLY_SPECIFIED\" and o.voided=0 and cn.voided=0 and locale=\"en\" and o.encounter_id = enc.encounter_id order by obs_id desc limit 1 ) as sampleDropoffObsUuid, \n" +
                "    (select encounter_id from obs o where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0 and enc.uuid=value_text order by obs_id desc limit 1) as resultsEncounterId, " +
                "    (select resultsEnc.uuid  from obs o left join encounter resultsEnc on o.encounter_id=resultsEnc.encounter_id \n" +
                "\t        where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0 and o.value_text=enc.uuid order by obs_id desc limit 1) as resultsEncounterUuid, " +
                "    (select Concat( pn.given_name,\"  \",pn.family_name, \" On: \", DATE_FORMAT(approvalObs.date_created, \"%d/%m/%Y\") ) from obs approvalObs left join users user on approvalObs.creator=user.user_id left join person_name pn on user.person_id=pn.person_id where  encounter_id = ( \n" +
                "\t\t       select encounter_id from obs o where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0 and enc.uuid=value_text order by obs_id desc limit 1 \n" +
                "\t     ) and approvalObs.concept_id="+PATHOLOGYREQUESTRESULTSAPPROVED.getConceptId()+" and approvalObs.voided=0 order by obs_id DESC LIMIT 1) as approvedBy,  " +
                "    (select approvalObs.uuid from obs approvalObs where  encounter_id = ( \n" +
                "\t\t       select encounter_id from obs o where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0 and enc.uuid=value_text order by obs_id desc limit 1 \n" +
                "\t     ) and approvalObs.concept_id="+PATHOLOGYREQUESTRESULTSAPPROVED.getConceptId()+" and approvalObs.voided=0 order by obs_id DESC LIMIT 1) as approvalObsUuid,  " +
                "    (select DATE_FORMAT(approvalObs.date_created, \"%Y/%m/%d\")  from obs approvalObs left join users user on approvalObs.creator=user.user_id left join person_name pn on user.person_id=pn.person_id where  encounter_id = ( \n" +
                "\t\t       select encounter_id from obs o where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0 and enc.uuid=value_text order by obs_id desc limit 1 \n" +
                "\t     ) and approvalObs.concept_id="+PATHOLOGYREQUESTRESULTSAPPROVED.getConceptId()+" and approvalObs.voided=0 order by obs_id DESC LIMIT 1) as approvedDate,  " +
                "    (select group_concat(distinct cn.name) from obs pathologicDiagnosis left join concept_name cn on pathologicDiagnosis.value_coded=cn.concept_id where  encounter_id = ( \n" +
                "\t\t       select encounter_id from obs o where o.concept_id= "+pathologyRequestEncounterUUID.getConceptId() + " and o.voided=0 and enc.uuid=value_text order by obs_id desc limit 1 \n" +
                "\t     ) and pathologicDiagnosis.concept_id="+pathologicDiagnoisis.getConceptId()+" and pathologicDiagnosis.voided=0 and cn.concept_name_type=\"FULLY_SPECIFIED\" and cn.voided=0 and locale=\"en\") as pathologicDiagnosisObs  " +


                " from encounter enc \n" +
                "\t left join person p on enc.patient_id=p.person_id\n" +
                "    left join (select person_id,name,location_id,retired from person_attribute pat left join location l on pat.value=l.location_id where person_attribute_type_id= "+ healthCenterPersonAttributeType.getPersonAttributeTypeId() +" and pat.voided=0 group by person_id) \n" +
                "\t\t\t\thealthcenter on enc.patient_id=healthcenter.person_id\n" +

                "where \n" +
                "\t enc.voided=0 " +
                "\tand p.voided=0 " +
                "    and enc.form_id= " + pathologyRequestForm.getFormId() +
                " and p.dead=0" +
                " and IF( :location IS NULL, true, healthcenter.location_id= :location) " +
//                " and IF( :baseEnc IS NULL , true, enc.encounter_id < :baseEnc)  " +
                " and enc.encounter_datetime >= (CURDATE() - INTERVAL 12 MONTH) "+
//                " and TIMESTAMPDIFF(MONTH, enc.encounter_datetime, now()) <= 6" +

                " order by enc.encounter_id desc " );

        sqldsd.addParameter(new Parameter("location", "Location", Location.class));
        sqldsd.addParameter(new Parameter("baseEnc", "baseEnc", String.class));
        sqldsd.addParameter(new Parameter("limitNumber", "limitNumber", String.class));


//        Map<String, Object> mappings = new HashMap<String, Object>();
//        mappings.put("location", "${location}");





        reportDefinition.addDataSetDefinition("dsd", Mapped.mapStraightThrough(sqldsd));


    }

    private void setupProperties() {

        pathologyRequestForm = gp.getForm(GlobalPropertiesManagement.PATHOLOGYREQUESTFORM);
        sampleStatusConcept =gp.getConcept(GlobalPropertiesManagement.SAMPLESTATUSCONCEPT);
        referralStatusConcept = gp.getConcept(GlobalPropertiesManagement.REFERRALSTATUSCONCEPT);
        sampleDropOffConcept = gp.getConcept(GlobalPropertiesManagement.SAMPLEDROPOFFCONCEPT);
        pathologyEncounterType  = gp.getEncounterType(GlobalPropertiesManagement.PATHOLOGYENCOUNTERTYPE);
//        phoneNumberPersonAttributeType =gp.getPersonAttributeType(GlobalPropertiesManagement.PERSONATTRIBUTEPHONENUMBER);
        healthCenterPersonAttributeType = gp.getPersonAttributeType(GlobalPropertiesManagement.FACILITY_PERSON_ATTRIBUTE_TYPE_ID);
        pathologyRequestEncounterUUID =  gp.getConcept(GlobalPropertiesManagement.PATHOLOGYREQUESTENCOUNTERUUID);
        telephoneNumberConcept = gp.getConcept(GlobalPropertiesManagement.TELEPHONE_NUMBER_CONCEPT);
        PATHOLOGYREQUESTRESULTSAPPROVED = gp.getConcept(GlobalPropertiesManagement.PATHOLOGYREQUESTRESULTSAPPROVED);
        pathologicDiagnoisis =  gp.getConcept(GlobalPropertiesManagement.PATHOLOGICDIAGNOSIS);
//        patientIMBPrimaryCareId = gp.getPatientIdentifier(GlobalPropertiesManagement.PC_IDENTIFIER);
    }

}
