package org.openmrs.module.rwandareports.reporting;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculationBasedOnMultiplePatientDataDefinitions;
import org.openmrs.module.rowperpatientreports.patientdata.definition.MostRecentObservation;
import org.openmrs.module.rowperpatientreports.patientdata.definition.ObservationInMostRecentEncounter;
import org.openmrs.module.rwandareports.customcalculator.CancerScreenSMSAlert;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;


public class SetupCancerScreeningSMSReport {
	
	//properties retrieved from global variables
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	private Concept hivStatus;

	private Concept HPV;

	private Concept testResult;
	private Concept HPVpositive;
	private Concept HPVNegative;

	private Form oncologyBreastScreeningExamination;

	private Form oncologyCervicalScreeningExamination;

	private  List<EncounterType> breastScreeningEncounterTypes=new ArrayList<EncounterType>();
	private  List<EncounterType> cervicalScreeningEncounterTypes=new ArrayList<EncounterType>();

	private Concept hasPatientBeenReferred_cervical;
	private Concept nextStep;
	private Concept reasonsForReferral;
	private Concept referredTo;


	public void setup() throws Exception {
		setupProperties();

		ReportDefinition smsReportDefinition = createSMSReportDefinition();

		ReportDesign smsReporDesign = Helper.createRowPerPatientXlsOverviewReportDesign(smsReportDefinition, "cancerscreeningSMSReport.xls","cancerscreeningSMSReport.xls_", null);

		Properties consultProps = new Properties();
		consultProps.put("repeatingSections", "sheet:1,row:10,dataset:dataset");
		consultProps.put("sortWeight","5000");

		smsReporDesign.setProperties(consultProps);

		Helper.saveReportDesign(smsReporDesign);
	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("cancerscreeningSMSReport.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("ONC-Cancer Screening SMS Report");

	}

	private ReportDefinition createSMSReportDefinition() {

		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Cancer Screening SMS Report");
		reportDefinition.addParameter(new Parameter("location", "Health Facility", Location.class));
		reportDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

		reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"),
				ParameterizableUtil.createParameterMappings("location=${location}"));

		createsmsDataSetDefinition (reportDefinition);
		Helper.saveReportDefinition(reportDefinition);

		return reportDefinition;
	}



	private void createsmsDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition
		RowPerPatientDataSetDefinition dataSetDef = new RowPerPatientDataSetDefinition();
		dataSetDef.setName("SMS Data set");
		dataSetDef.addParameter(new Parameter("location", "location", Date.class));
		dataSetDef.addParameter(new Parameter("startDate", "startDate", Date.class));
		dataSetDef.addParameter(new Parameter("endDate", "endDate", Date.class));

		//Add Columns

		dataSetDef.addColumn(RowPerPatientColumns.getArchivingId("Id"), new HashMap<String, Object>());

		dataSetDef.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());

		dataSetDef.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());

		dataSetDef.addColumn(RowPerPatientColumns.getAge("age"), new HashMap<String, Object>());
		dataSetDef.addColumn(RowPerPatientColumns.getPhoneNumber("phoneNumber"), new HashMap<String, Object>());

		//Calculation definitions

		MostRecentObservation mostRecentHPVResultTest = RowPerPatientColumns.getMostRecent("hpvResultTest",testResult, null);
		mostRecentHPVResultTest.addParameter(new Parameter("startDate", "startDate",Date.class));
		mostRecentHPVResultTest.addParameter(new Parameter("endDate", "endDate",Date.class));

		MostRecentObservation mostRecenthivStatus = RowPerPatientColumns.getMostRecent("hivResultTest",hivStatus, null);

		ObservationInMostRecentEncounter cervicalNextScheduledDate = RowPerPatientColumns.getObservationInMostRecentEncounter("cervicalNextScheduledDate",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),null,cervicalScreeningEncounterTypes,null);
		ObservationInMostRecentEncounter breastNextScheduledDate = RowPerPatientColumns.getObservationInMostRecentEncounter("breastNextScheduledDate",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),null,breastScreeningEncounterTypes,null);


		ObservationInMostRecentEncounter referred_Cervical = RowPerPatientColumns.getObservationInMostRecentEncounter("referred_Cervical",hasPatientBeenReferred_cervical,null,cervicalScreeningEncounterTypes,null);
		ObservationInMostRecentEncounter referred_Breast = RowPerPatientColumns.getObservationInMostRecentEncounter("referred_Breast",nextStep,null,breastScreeningEncounterTypes,null);



		ObservationInMostRecentEncounter referredReasonBreast = RowPerPatientColumns.getObservationInMostRecentEncounter("referredBreast",reasonsForReferral,null,breastScreeningEncounterTypes,null);
		ObservationInMostRecentEncounter referredReasonCervical = RowPerPatientColumns.getObservationInMostRecentEncounter("referredCervical",reasonsForReferral,null,cervicalScreeningEncounterTypes,null);
		ObservationInMostRecentEncounter cervicalReferredTo = RowPerPatientColumns.getObservationInMostRecentEncounter("cervicalReferredTo",referredTo,null,cervicalScreeningEncounterTypes,null);
		ObservationInMostRecentEncounter beastReferredTo = RowPerPatientColumns.getObservationInMostRecentEncounter("breastReferredTo",referredTo,null,breastScreeningEncounterTypes,null);


		CustomCalculationBasedOnMultiplePatientDataDefinitions sms = new CustomCalculationBasedOnMultiplePatientDataDefinitions();
		sms.setName("sms");
		sms.addPatientDataToBeEvaluated(mostRecentHPVResultTest, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		sms.addPatientDataToBeEvaluated(mostRecenthivStatus, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(cervicalNextScheduledDate, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(cervicalReferredTo, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(breastNextScheduledDate, new HashMap<String, Object>());
		sms.addPatientDataToBeEvaluated(beastReferredTo, new HashMap<String, Object>());




		sms.setCalculator(new CancerScreenSMSAlert());
		sms.addParameter(new Parameter("startDate", "startDate",Date.class));
		sms.addParameter(new Parameter("endDate", "endDate",Date.class));

		dataSetDef.addColumn(sms,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");


		reportDefinition.addDataSetDefinition("dataset", dataSetDef, mappings);

	}
	
	private void setupProperties() {
		hivStatus=Context.getConceptService().getConceptByUuid("aec6ad18-f4dd-4cfa-b68d-3d7bb6ea908e");

		HPV=Context.getConceptService().getConceptByUuid("f7c2d59d-2043-42ce-b04d-08564d54b0c7");
		testResult=Context.getConceptService().getConceptByUuid("bfb3eb1e-db98-4846-9915-0168511c6298");
		HPVpositive=Context.getConceptService().getConceptByUuid("1b4a5f67-6106-4a4d-a389-2f430be543e4");
		HPVNegative =Context.getConceptService().getConceptByUuid("64c23192-54e4-4750-9155-2ed0b736a0db");

		oncologyBreastScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_BREAST_SCREENING_EXAMINATION);
		oncologyCervicalScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_CERVICAL_SCREENING_EXAMINATION);

		breastScreeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		cervicalScreeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());
		hasPatientBeenReferred_cervical=Context.getConceptService().getConceptByUuid("805f40f2-4720-4474-b761-5880c9d3870e");
        nextStep=Context.getConceptService().getConceptByUuid("69b9671b-d8b1-461b-bb7d-adb151775a57");
		reasonsForReferral = Context.getConceptService().getConceptByUuid("1aa373f4-4db5-4b01-bce0-c10a636bb931");
		referredTo= Context.getConceptService().getConceptByUuid("3a84ab37-f75c-48ad-8bcf-322c927f36bb");

	}
}
