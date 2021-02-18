package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.filter.LocationEncounterFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;

import java.util.*;

public class SetupCancerScreeningConsultAndMissedVisit {
	protected final static Log log = LogFactory.getLog(SetupCancerScreeningConsultAndMissedVisit.class);
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	private Form oncologyBreastScreeningExamination;

	private Form oncologyCervicalScreeningExamination;

	private Form mUzimaBreastScreening;

	private Form mUzimaCervicalScreening;


	private  List<EncounterType> breastAndCervicalScreeningEncounterTypes=new ArrayList<EncounterType>();

	private  List<Form> screeningExaminationForms =new ArrayList<Form>();

	private Concept hasPatientBeenReferred_cervical;

	private  List<EncounterType> breastScreeningEncounterTypes=new ArrayList<EncounterType>();
	private  List<EncounterType> cervicalScreeningEncounterTypes=new ArrayList<EncounterType>();
	private Concept reasonsForReferral;
	private Concept referredTo;





	public void setup() throws Exception {
		setupPrograms();
		
		ReportDefinition consultReportDefinition = createConsultReportDefinition();	
		ReportDefinition missedVisitReportDefinition = createMissedVisitReportDefinition();
		
		ReportDesign consultReporDesign = Helper.createRowPerPatientXlsOverviewReportDesign(consultReportDefinition, "OncologyCancerScreeningConsultSheet.xls","OncologyCancerScreeningConsultSheet.xls_", null);
		ReportDesign missedVisitReporDesign = Helper.createRowPerPatientXlsOverviewReportDesign(missedVisitReportDefinition, "OncologyCancerScreeningMissedVisitSheet.xls","OncologyCancerScreeningMissedVisitSheet.xls_", null);
		
		Properties consultProps = new Properties();
		consultProps.put("repeatingSections", "sheet:1,row:9,dataset:dataset1");
		consultProps.put("sortWeight","5000");
		
		Properties missedVisitProps = new Properties();
		missedVisitProps.put("repeatingSections", "sheet:1,row:8,dataset:dataset2");
		missedVisitProps.put("sortWeight","5000");
		
		consultReporDesign.setProperties(consultProps);
		missedVisitReporDesign.setProperties(missedVisitProps);
		
		Helper.saveReportDesign(consultReporDesign);
		Helper.saveReportDesign(missedVisitReporDesign);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("OncologyCancerScreeningConsultSheet.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
			if ("OncologyCancerScreeningMissedVisitSheet.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("ONC-Cancer Screening Consultation Sheet");
		Helper.purgeReportDefinition("ONC-Cancer Screening Missed Visit");
		
	}
	
	private ReportDefinition createConsultReportDefinition() {

		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Cancer Screening Consultation Sheet");
		reportDefinition.addParameter(new Parameter("location", "Health Center", Location.class));
		reportDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

		reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"),
			    ParameterizableUtil.createParameterMappings("location=${location}"));

		createConsultDataSetDefinition (reportDefinition);
		Helper.saveReportDefinition(reportDefinition);

		return reportDefinition;
	}
	
	private ReportDefinition createMissedVisitReportDefinition() {

		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Cancer Screening Missed Visit");
		reportDefinition.addParameter(new Parameter("location", "Health Center", Location.class));

		reportDefinition.addParameter(new Parameter("endDate", "Date", Date.class));

		reportDefinition.setBaseCohortDefinition(Cohorts.createParameterizedLocationCohort("At Location"),
				ParameterizableUtil.createParameterMappings("location=${location}"));

		createMissedVisitDataSetDefinition(reportDefinition);

		Helper.saveReportDefinition(reportDefinition);

		return reportDefinition;
	}
	
	private void createConsultDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDef = new RowPerPatientDataSetDefinition();
		dataSetDef.setName("Consult Data set");
		dataSetDef.addParameter(new Parameter("location", "location", Date.class));
		dataSetDef.addParameter(new Parameter("startDate", "startDate", Date.class));
		dataSetDef.addParameter(new Parameter("endDate", "endDate", Date.class));

		//Add filters

		//dataSetDef.addFilter(Cohorts.getPatientReturnVisitByStartDateAndEndDate("patientsWithVisitInPeriod",breastAndCervicalScreeningEncounterTypes), ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


		//Add Columns
         
        addCommonColumns(dataSetDef);
		dataSetDef.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextVisit", "dd-MMM-yyyy", null), new HashMap<String, Object>());



		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");


		reportDefinition.addDataSetDefinition("dataset1", dataSetDef, mappings);
		
		
	}
	
	private void createMissedVisitDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition 
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Missed visit Data set");
		dataSetDefinition.addParameter(new Parameter("location", "Location", Location.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "enDate", Date.class));
		
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextRDV", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);
		
		//patients with late visits

		// !!!!!!! Follow up form ??????

		SqlCohortDefinition missedVisit=new SqlCohortDefinition("select o.person_id from obs o,encounter e where o.encounter_id=e.encounter_id and e.encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+") and o.voided=0 and o.concept_id="+gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE).getConceptId()+" and datediff(:endDate,o.value_datetime)<400 and datediff(:endDate,o.value_datetime)>7 and o.value_datetime< :endDate and o.person_id not in (select patient_id from encounter where encounter_datetime>o.value_datetime and encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+")) and o.person_id not in (select person_id from person where dead=1)");
		missedVisit.addParameter(new Parameter("endDate", "enDate", Date.class));

		//dataSetDefinition.addFilter(missedVisit,ParameterizableUtil.createParameterMappings("endDate=${endDate}"));




		//Add Columns
		addCommonColumns(dataSetDefinition);
		
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextRDV", "yyyy/MM/dd", null), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("Address", true, true, false, false), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getDateOfBirth("Date of Birth", null, null), new HashMap<String, Object>());



		
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		
		reportDefinition.addDataSetDefinition("dataset2", dataSetDefinition, mappings);
		
	}


	private void setupPrograms() {
		oncologyBreastScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_BREAST_SCREENING_EXAMINATION);
		oncologyCervicalScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_CERVICAL_SCREENING_EXAMINATION);

		mUzimaBreastScreening=Context.getFormService().getForm("mUzima Breast cancer screening");
		mUzimaCervicalScreening=Context.getFormService().getForm("mUzima Cervical cancer screening");

		screeningExaminationForms.add(oncologyBreastScreeningExamination);
		screeningExaminationForms.add(oncologyCervicalScreeningExamination);
		screeningExaminationForms.add(mUzimaBreastScreening);
		screeningExaminationForms.add(mUzimaCervicalScreening);

		breastAndCervicalScreeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		breastAndCervicalScreeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());

		hasPatientBeenReferred_cervical=Context.getConceptService().getConceptByUuid("805f40f2-4720-4474-b761-5880c9d3870e");

		breastScreeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		cervicalScreeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());

		reasonsForReferral = Context.getConceptService().getConceptByUuid("1aa373f4-4db5-4b01-bce0-c10a636bb931");

		referredTo= Context.getConceptService().getConceptByUuid("3a84ab37-f75c-48ad-8bcf-322c927f36bb");

	}
	
	//Add common columns for the two datasets
	private void addCommonColumns(RowPerPatientDataSetDefinition dataSetDefinition){
		
        dataSetDefinition.addColumn(RowPerPatientColumns.getArchivingId("Id"), new HashMap<String, Object>());

        dataSetDefinition.addColumn(RowPerPatientColumns.getAge("age"), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getFamilyNameColumn("familyName"), new HashMap<String, Object>());
     	
		dataSetDefinition.addColumn(RowPerPatientColumns.getFirstNameColumn("givenName"), new HashMap<String, Object>());

	/*	dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounterOfTheTypes("lastEncounterLocation",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),breastAndCervicalScreeningEncounterTypes,new LocationObsFilter()), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounterOfTheTypes("lastEncounter",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());
*/

		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("recentencounterdate", null,breastAndCervicalScreeningEncounterTypes,"dd/MM/yyyy", null), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getRecentEncounter("recentencounterLocation", null,breastAndCervicalScreeningEncounterTypes,"dd/MM/yyyy", new LocationEncounterFilter()), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("nextScheduledDate",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),null,breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());

		// hasPatientBeenReferred_cervical. Breast use different concept
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referred",hasPatientBeenReferred_cervical,null,breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referredBreast",reasonsForReferral,null,breastScreeningEncounterTypes,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referredCervical",reasonsForReferral,null,cervicalScreeningEncounterTypes,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referredTo",referredTo,null,breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());


	}
	
}
