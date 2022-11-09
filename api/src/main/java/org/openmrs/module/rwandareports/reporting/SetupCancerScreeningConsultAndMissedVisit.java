package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rowperpatientreports.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.rwandareports.filter.DateFormatFilter;
import org.openmrs.module.rwandareports.filter.LocationEncounterFilter;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.module.rwandareports.util.RowPerPatientColumns;
//import org.openmrs.module.rwandareports.widget.AllLocation;
//import org.openmrs.module.rwandareports.widget.ConceptAnswers;
//import org.openmrs.module.rwandareports.util.RowPerPatientColumns;


import java.util.*;

public class SetupCancerScreeningConsultAndMissedVisit {
	protected final static Log log = LogFactory.getLog(SetupCancerScreeningConsultAndMissedVisit.class);

	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	private Form oncologyBreastScreeningExamination;

	private Form oncologyCervicalScreeningExamination;

	private Form mUzimaBreastScreening;

	private Form mUzimaCervicalScreening;
	private Form oncologyCervicalScreeningfollowup;
	private Form mUzimaCervicalcancerscreeningfollowup;


	private  List<EncounterType> breastAndCervicalScreeningEncounterTypes=new ArrayList<EncounterType>();

	private  List<Form> screeningExaminationForms =new ArrayList<Form>();

	private Concept hasPatientBeenReferred_cervical;

	private  List<EncounterType> breastScreeningEncounterTypes=new ArrayList<EncounterType>();
	private  List<EncounterType> cervicalScreeningEncounterTypes=new ArrayList<EncounterType>();
	private  List<Form> breastCancerforms = new ArrayList<Form>();
	private  List<Form> cervalCancerforms = new ArrayList<Form>();
	private Concept reasonsForReferral;
	private Concept referredTo;
	private List<Concept> breastDiagnosisList = new ArrayList<Concept>();
	private Concept breastDiagnosis;
	private Concept breastPain;
	private Concept breastInfection;
	private Concept MASTITIS;
	private Concept ABSCESS;
	private Concept ECZEMA;
	private Concept breastMass;
	private Concept OTHERNONCODED;
	private Concept otherInfection;

	private Concept HPVPositiveType;
	private Concept testResult;

	private Concept HPV16;
	private Concept HPV18;
	private Concept otherHRHPV;
	private List<Concept> cervicalDiagnosisList = new ArrayList<Concept>();
	private Concept	typeOfAttempt;
	private Concept	CHWVisit;
	private Concept resultOfCall;
	private Concept referredToCoded;
	private Map<Concept, Location> locationToLocationConceptMatch = new HashMap<Concept, Location>();






	public void setup() throws Exception {
		setupPrograms();

		ReportDefinition consultReportDefinition = createConsultReportDefinition();

		EvaluationContext context = new EvaluationContext();
//		ReportDefinition missedVisitReportDefinition = createMissedVisitReportDefinition(context);

		ReportDefinition missedVisitReportDefinition = createMissedVisitReportDefinition();


		ReportDesign consultReporDesign = Helper.createRowPerPatientXlsOverviewReportDesign(consultReportDefinition, "OncologyCancerScreeningConsultSheet.xls","OncologyCancerScreeningConsultSheet.xls_", null);
		ReportDesign missedVisitReporDesign = Helper.createRowPerPatientXlsOverviewReportDesign(missedVisitReportDefinition, "OncologyCancerScreeningMissedVisitSheet.xls","OncologyCancerScreeningMissedVisitSheet.xls_", null);

		Properties consultProps = new Properties();
		consultProps.put("repeatingSections", "sheet:1,row:10,dataset:dataset1");
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
		//reportDefinition.addParameter(new Parameter("location", "Health Center", Location.class));
		reportDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

		Parameter location = new Parameter("location", "Health Facility", Location.class);
		location.setRequired(false);

		reportDefinition.addParameter(location);

		Parameter encounterTypes = new Parameter("encounterTypes", "Encounter", EncounterType.class);
		encounterTypes.setRequired(false);
		reportDefinition.addParameter(encounterTypes);


		SqlCohortDefinition locationDefinition = new SqlCohortDefinition();
		locationDefinition.setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat where p.patient_id = pa.person_id and pat.format ='org.openmrs.Location' and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id and (:location is null or pa.value = :location)");
		locationDefinition.setName("locationDefinition");
		locationDefinition.addParameter(location);

//		String[] locationMarching = null;
//
//		if (location.getHierarchy() != null) {
//			hierarchyToCheck = location.getHierarchy().split(",");
//		}
//
//		for (String h : hierarchyToCheck) {
//			String[] config = h.split(":");
//			String hVal = config[0];
//			String hDisplay = config[0];
//			if (config.length > 0) {
//				hDisplay = config[1];
//			}

		Concept returnVisitDate = gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE);
		SqlCohortDefinition patientsWithVisitInPeriod=new SqlCohortDefinition();
		patientsWithVisitInPeriod.setQuery("select distinct o.person_id from obs o, encounter e where (:encounterTypes is null or e.encounter_type= :encounterTypes) and e.encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+")  and o.encounter_id=e.encounter_id and e.voided=0 and o.voided=0 and o.concept_id="
				+ returnVisitDate.getConceptId()
				+ " and o.value_datetime>= :startDate and o.value_datetime<= :endDate");
		patientsWithVisitInPeriod.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsWithVisitInPeriod.addParameter(new Parameter("endDate", "endDate", Date.class));
		patientsWithVisitInPeriod.addParameter(encounterTypes);


		CompositionCohortDefinition consultationSheetBaseCohort = new CompositionCohortDefinition();
		consultationSheetBaseCohort.setName("consultationSheetBaseCohort");
		consultationSheetBaseCohort.addParameter(new Parameter("location", "Health Center", Location.class));
		consultationSheetBaseCohort.addParameter(new Parameter("startDate", "startDate", Date.class));
		consultationSheetBaseCohort.addParameter(new Parameter("endDate", "endDate", Date.class));
		consultationSheetBaseCohort.addParameter(encounterTypes);
		consultationSheetBaseCohort.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(locationDefinition,
						ParameterizableUtil.createParameterMappings("location=${location}")));

		consultationSheetBaseCohort.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(patientsWithVisitInPeriod, ParameterizableUtil.createParameterMappings("encounterTypes=${encounterTypes},endDate=${endDate},startDate=${startDate}")));

		consultationSheetBaseCohort.setCompositionString("1 AND 2");

		reportDefinition.setBaseCohortDefinition(consultationSheetBaseCohort,
				ParameterizableUtil.createParameterMappings("location=${location},encounterTypes=${encounterTypes},endDate=${endDate},startDate=${startDate}"));

		createConsultDataSetDefinition (reportDefinition);
		Helper.saveReportDefinition(reportDefinition);

		return reportDefinition;
	}


//	private ReportDefinition createMissedVisitReportDefinition(EvaluationContext context) {

	private ReportDefinition createMissedVisitReportDefinition() {


		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("ONC-Cancer Screening Missed Visit");
		//reportDefinition.addParameter(new Parameter("location", "Health Center", Location.class));

		reportDefinition.addParameter(new Parameter("endDate", "Date", Date.class));


		Parameter location = new Parameter("location", "Health Facility", Location.class);
		location.setRequired(false);

		reportDefinition.addParameter(location);

//		Properties properties = new Properties();
//		properties.setProperty("conceptQuestion", referredToCoded.getName().toString());
//		Parameter referredTo =new Parameter("referredTo", "referredTo", ConceptAnswers.class, properties);
//		referredTo.setRequired(false);
//		reportDefinition.addParameter(referredTo);

		reportDefinition.addParameter(location);

		Parameter encounterTypes = new Parameter("encounterTypes", "Encounter", EncounterType.class);
		encounterTypes.setRequired(false);
		reportDefinition.addParameter(encounterTypes);

//		int locationId = (Integer) context.getParameterValue("location");



		StringBuilder sql = new StringBuilder();
//		sql.append("DROP TABLE IF EXISTS location_to_concept_match;");
//		sql.append("CREATE TEMPORARY TABLE location_to_concept_match(concept_id INT,creditLimit INT);");
//		sql.append("INSERT INTO location_to_concept_match VALUES");


//		int size = locationToLocationConceptMatch.entrySet().size();

//		sql.append("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat " +
//				"where p.patient_id = pa.person_id " +
//				"and pat.format ='org.openmrs.Location' " +
//				"and pa.voided = 0 " +
//				"and pat.person_attribute_type_id = pa.person_attribute_type_id " +
//				"and (:location is null or (pa.value = :location " +
//				"      	or p.patient_id in (select person_id from obs " +
//				"		where concept_id= " + referredToCoded.getConceptId()  +
//				"		and obs_datetime < :endDate " +
//				"		and (:referredTo is null or value_coded = :referredTo ))))");
//		boolean first = true;
//		for (Map.Entry<Concept, Location> match : locationToLocationConceptMatch.entrySet() ) {
//			if(size > 1) {
//				sql.append("(  " + match.getKey().getConceptId() + "," + match.getValue().getLocationId() + " ), ");
//			}else{
//				sql.append("(  " + match.getKey().getConceptId() + "," + match.getValue().getLocationId() + " ); ");
//			}
//			size--;
//			if (!first) {
//				sql.append(",");
//			}
//			if(match.getValue().getLocationId()== locationId){
//
//				sql.append(match.getKey().getConceptId());
//			}
//			sql.append(match);
//			first = false;
//		}
//		sql.append(" ) ");

		SqlCohortDefinition locationDefinition = new SqlCohortDefinition();
		locationDefinition.setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat " +
										" where p.patient_id = pa.person_id and pat.format ='org.openmrs.Location' " +
										" and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id " +
										" and (:location is null or pa.value = :location)");

//		locationDefinition.setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat " +
//				" where p.patient_id = pa.person_id and pat.format ='org.openmrs.Location' " +
//				" and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id " +
//				" and (:location is null or pa.value = :location and (:endDate is null or p.patient_id in " +
//				"(select person_id from obs where concept_id= " + referredToCoded.getConceptId()  + " " +
//				"and obs_datetime < :endDate and value_coded = :referredTo ))))");
//		locationDefinition.setQuery("select person_id from obs where concept_id= " + referredToCoded.getConceptId()  +
//				" and obs_datetime < :endDate and value_coded = :referredTo )");
//		locationDefinition.setQuery(sql.toString());
		locationDefinition.setName("locationDefinition");
		locationDefinition.addParameter(location);
//		locationDefinition.addParameter(referredTo);


//		SqlCohortDefinition locationDefinition = new SqlCohortDefinition();
//		locationDefinition.setQuery("select p.patient_id from patient p, person_attribute pa, person_attribute_type pat where p.patient_id = pa.person_id and pat.format ='org.openmrs.Location' and pa.voided = 0 and pat.person_attribute_type_id = pa.person_attribute_type_id and (:location is null or pa.value = :location)");
//		locationDefinition.setName("locationDefinition");
//		locationDefinition.addParameter(location);

		//patients with late visits

		// !!!!!!! Follow up form ??????
// and (:encounterType is null or e.encounter_type=:encounterType)
		//SqlCohortDefinition missedVisit=new SqlCohortDefinition("select o.person_id from obs o,encounter e where o.encounter_id=e.encounter_id and e.encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+") and o.voided=0 and o.concept_id="+gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE).getConceptId()+" and datediff(:endDate,o.value_datetime)<400 and datediff(:endDate,o.value_datetime)>7 and o.value_datetime< :endDate and o.person_id not in (select patient_id from encounter where encounter_datetime>o.value_datetime and encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+")) and o.person_id not in (select person_id from person where dead=1)");
		SqlCohortDefinition missedVisit=new SqlCohortDefinition("select o.person_id from obs o,encounter e " +
				"where o.encounter_id=e.encounter_id and (:encounterTypes is null or e.encounter_type= :encounterTypes) " +
				"and  o.voided=0 and o.person_id not in (select person_id from person where dead=1) " +
				"and ((o.concept_id= "+ testResult.getConceptId() + " and o.value_coded= " +HPVPositiveType.getConceptId()+ " and o.obs_datetime<=:endDate " +
				" and o.person_id not in (select patient_id from encounter " +
				"		where encounter_datetime>=o.obs_datetime " +
				"		and encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId() +","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+")) " +
				") or ( " +
				" o.concept_id="+gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE).getConceptId()+" " +
				"and datediff(:endDate,o.value_datetime)<400 and o.value_datetime< :endDate and o.value_datetime IS NOT NULL " +
				"and o.value_datetime != \"\" and o.person_id not in " +
				"		(select patient_id from encounter where encounter_datetime>=o.value_datetime " +
				"		and encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+")) " +
				" and o.person_id not in (select person_id from obs CHWO where CHWO.concept_id="+ typeOfAttempt.getConceptId() +" and CHWO.value_coded="+ CHWVisit.getConceptId() +" and CHWO.obs_datetime >= o.value_datetime and CHWO.voided=0) " +
				" and o.person_id not in (select person_id from obs where concept_id="+ resultOfCall.getConceptId() +" and obs_datetime >= o.value_datetime and voided=0) " +
				" and datediff(o.value_datetime,e.encounter_datetime)<=400 )) ");

//		SqlCohortDefinition missedVisit=new SqlCohortDefinition("select o.person_id from obs o,encounter e where o.encounter_id=e.encounter_id and (:encounterTypes is null or e.encounter_type= :encounterTypes) and o.voided=0 and o.concept_id="+gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE).getConceptId()+" and datediff(:endDate,o.value_datetime)<400 and datediff(:endDate,o.value_datetime)>7 and o.value_datetime< :endDate and o.person_id not in (select patient_id from encounter where encounter_datetime>o.value_datetime and encounter_type in ("+oncologyBreastScreeningExamination.getEncounterType().getEncounterTypeId()+","+oncologyCervicalScreeningExamination.getEncounterType().getEncounterTypeId()+")) and o.person_id not in (select person_id from person where dead=1)");
		//SqlCohortDefinition missedVisit=new SqlCohortDefinition("select o.person_id from obs o,encounter e where o.encounter_id=e.encounter_id and (:encounterTypes is null or e.encounter_type= :encounterTypes) and o.voided=0 and o.concept_id="+gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE).getConceptId()+" and datediff(:endDate,o.value_datetime)<400 and datediff(:endDate,o.value_datetime)>7 and o.value_datetime< :endDate and o.person_id not in (select patient_id from encounter where encounter_datetime>o.value_datetime) and o.person_id not in (select person_id from person where dead=1)");
		//SqlCohortDefinition missedVisit=new SqlCohortDefinition("select e.patient_id from encounter e where (:encounterTypes is null or e.encounter_type= :encounterTypes) and e.voided=0");
     	missedVisit.addParameter(new Parameter("endDate", "enDate", Date.class));
		missedVisit.addParameter(encounterTypes);


		CompositionCohortDefinition missedVisitBaseCohort = new CompositionCohortDefinition();
		missedVisitBaseCohort.setName("missedVisitBaseCohort");
		missedVisitBaseCohort.addParameter(new Parameter("location", "Health Center", Location.class));
		missedVisitBaseCohort.addParameter(new Parameter("endDate", "endDate", Date.class));
		missedVisitBaseCohort.addParameter(encounterTypes);

		missedVisitBaseCohort.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(locationDefinition,
						ParameterizableUtil.createParameterMappings("location=${location}")));

		missedVisitBaseCohort.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(missedVisit, ParameterizableUtil.createParameterMappings("encounterTypes=${encounterTypes},endDate=${endDate}")));

		missedVisitBaseCohort.setCompositionString("1 AND 2");

		reportDefinition.setBaseCohortDefinition(missedVisitBaseCohort,
				ParameterizableUtil.createParameterMappings("location=${location},endDate=${endDate},encounterTypes=${encounterTypes}"));

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

		//Add Columns

		addCommonColumns(dataSetDef);
		dataSetDef.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextVisit", "dd-MMM-yyyy", null), new HashMap<String, Object>());



		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");
		mappings.put("startDate", "${startDate}");
//		mappings.put("referredTo", "${referredTo}");

		reportDefinition.addDataSetDefinition("dataset1", dataSetDef, mappings);


	}

	private void createMissedVisitDataSetDefinition(ReportDefinition reportDefinition) {
		// Create new dataset definition
		RowPerPatientDataSetDefinition dataSetDefinition = new RowPerPatientDataSetDefinition();
		dataSetDefinition.setName("Missed visit Data set");
		dataSetDefinition.addParameter(new Parameter("location", "Location", Location.class));
		dataSetDefinition.addParameter(new Parameter("endDate", "enDate", Date.class));

		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addSortElement("nextScheduledDate", SortDirection.DESC);

//		sortCriteria.addSortElement("nextRDV", SortDirection.ASC);
		dataSetDefinition.setSortCriteria(sortCriteria);

		//Add Columns
		addCommonColumns(dataSetDefinition);

		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextRDV", "yyyy/MM/dd", null), new HashMap<String, Object>());

//		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("Address", true, true, true, true), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getDateOfBirth("Date of Birth", null, null), new HashMap<String, Object>());




		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("location", "${location}");
		mappings.put("endDate", "${endDate}");

		reportDefinition.addDataSetDefinition("dataset2", dataSetDefinition, mappings);

	}


	private void setupPrograms() {
		oncologyBreastScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_BREAST_SCREENING_EXAMINATION);
		oncologyCervicalScreeningExamination=gp.getForm(GlobalPropertiesManagement.ONCOLOGY_CERVICAL_SCREENING_EXAMINATION);
		oncologyCervicalScreeningfollowup = Context.getFormService().getForm("Oncology Cervical Screening follow up");

		mUzimaBreastScreening=Context.getFormService().getForm("mUzima Breast cancer screening");
		mUzimaCervicalScreening=Context.getFormService().getForm("mUzima Cervical cancer screening");
		mUzimaCervicalcancerscreeningfollowup = Context.getFormService().getForm("mUzima Cervical cancer screening follow up");

		screeningExaminationForms.add(oncologyBreastScreeningExamination);
		screeningExaminationForms.add(oncologyCervicalScreeningExamination);
		screeningExaminationForms.add(mUzimaBreastScreening);
		screeningExaminationForms.add(mUzimaCervicalScreening);

		breastCancerforms.add(mUzimaBreastScreening);
		breastCancerforms.add(oncologyBreastScreeningExamination);

		cervalCancerforms.add(oncologyCervicalScreeningExamination);
		cervalCancerforms.add(oncologyCervicalScreeningfollowup);
		cervalCancerforms.add(mUzimaCervicalcancerscreeningfollowup);


		breastAndCervicalScreeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		breastAndCervicalScreeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());

		hasPatientBeenReferred_cervical=Context.getConceptService().getConceptByUuid("805f40f2-4720-4474-b761-5880c9d3870e");

		breastScreeningEncounterTypes.add(oncologyBreastScreeningExamination.getEncounterType());
		cervicalScreeningEncounterTypes.add(oncologyCervicalScreeningExamination.getEncounterType());

		reasonsForReferral = Context.getConceptService().getConceptByUuid("1aa373f4-4db5-4b01-bce0-c10a636bb931");

		referredTo= Context.getConceptService().getConceptByUuid("3a84ab37-f75c-48ad-8bcf-322c927f36bb");

		breastDiagnosis = Context.getConceptService().getConceptByUuid("1ed543c7-36ff-4444-bc99-0f01eede9937");
		breastPain = Context.getConceptService().getConceptByUuid("89f78558-aa31-48f1-956f-74427640ec26");
		breastInfection = Context.getConceptService().getConceptByUuid("56ac82d2-547d-4a28-9148-11659b15a459");
		MASTITIS = Context.getConceptService().getConceptByUuid("3ccd029e-26fe-102b-80cb-0017a47871b2");
		ABSCESS = Context.getConceptService().getConceptByUuid("3ccd2666-26fe-102b-80cb-0017a47871b2");
		ECZEMA = Context.getConceptService().getConceptByUuid("3cd47e3e-26fe-102b-80cb-0017a47871b2");
		breastMass = Context.getConceptService().getConceptByUuid("09e3246a-5968-4ab4-960a-6b324517dc64");
		OTHERNONCODED = Context.getConceptService().getConceptByUuid("3cee7fb4-26fe-102b-80cb-0017a47871b2");
		otherInfection = Context.getConceptService().getConceptByUuid("e5155801-8d61-43c9-ac7d-dcb886a36f46");

		breastDiagnosisList.add(breastPain);
		breastDiagnosisList.add(breastInfection);
		breastDiagnosisList.add(MASTITIS);
		breastDiagnosisList.add(ABSCESS);
		breastDiagnosisList.add(ECZEMA);
		breastDiagnosisList.add(breastMass);
		breastDiagnosisList.add(OTHERNONCODED);
		breastDiagnosisList.add(otherInfection);

		HPVPositiveType = Context.getConceptService().getConceptByUuid("1b4a5f67-6106-4a4d-a389-2f430be543e4");
		HPV16 = Context.getConceptService().getConceptByUuid("059fddd3-711f-47ab-818f-087984aeecc3");
		HPV18 = Context.getConceptService().getConceptByUuid("b672c3ff-96c9-41cd-9ae6-aa0811ce347f");
		otherHRHPV = Context.getConceptService().getConceptByUuid("6c3428c6-f406-4ef9-b2dd-4fe5a79f3432");

		cervicalDiagnosisList.add(HPVPositiveType);
		cervicalDiagnosisList.add(HPV16);
		cervicalDiagnosisList.add(HPV18);
		cervicalDiagnosisList.add(otherHRHPV);

		typeOfAttempt = gp.getConcept(GlobalPropertiesManagement.TYPE_OF_ATTEMPT);
		CHWVisit = gp.getConcept(GlobalPropertiesManagement.CHW_VISIT);
		resultOfCall = gp.getConcept(GlobalPropertiesManagement.RESULT_OF_CALL);
//		Context.getAdministrationService().getGlobalProperty("reports.locationToLocationConceptMatch");
		referredToCoded = gp.getConcept(GlobalPropertiesManagement.REFERRED_TO_CODED);
		testResult =gp.getConcept(GlobalPropertiesManagement.TEST_RESULT);

		String matchesGP =  Context.getAdministrationService().getGlobalProperty("reports.locationToLocationConceptMatch");
		String[] matches = matchesGP.split(",");
		for(String match: matches) {
			String[] v = match.split(":");
			try {
				Concept locationConcept = MetadataLookup.getConcept(v[0]);
				Location location = MetadataLookup.getLocation(v[1]);
				locationToLocationConceptMatch.put(locationConcept, location);
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Unable to convert " + match + " into a usable Concept and location", e);
			}
		}






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

		dataSetDefinition.addColumn(RowPerPatientColumns.getPhoneNumber("phoneNumber"), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getContactPersonPhoneNumber("contactPersonPhonenumber"), new HashMap<String, Object>());


//		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("nextScheduledDate",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),null,breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentReturnVisitDate("nextScheduledDate", "dd/MM/yyyy", null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("nextScheduledDate",gp.getConcept(GlobalPropertiesManagement.RETURN_VISIT_DATE),null,breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());

		// hasPatientBeenReferred_cervical. Breast use different concept
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referred",hasPatientBeenReferred_cervical,null,breastAndCervicalScreeningEncounterTypes,new DateFormatFilter()), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getPatientAddress("Address", true, true, true, true), new HashMap<String, Object>());

		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referredBreast",reasonsForReferral,breastCancerforms,breastScreeningEncounterTypes,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referredCervical",reasonsForReferral,cervalCancerforms,cervicalScreeningEncounterTypes,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getObservationInMostRecentEncounter("referredTo",referredTo,null,breastAndCervicalScreeningEncounterTypes,null), new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers("breastDiagnosis",breastDiagnosis,breastDiagnosisList,null,null,null),new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers("cervicalDiagnosis",HPVPositiveType,cervicalDiagnosisList,null,null,"dd/MM/yyyy"),new HashMap<String, Object>());
		dataSetDefinition.addColumn(RowPerPatientColumns.getMostRecentInperiodHavingCodedAnswers("cervicalDiagnosis",HPVPositiveType,cervicalDiagnosisList,null,null,null),new HashMap<String, Object>());

	}

}
