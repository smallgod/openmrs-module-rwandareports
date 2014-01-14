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
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.SqlIndicator;
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

public class SetupOncologyQuarterlyIndicatorReport {

	public Helper h = new Helper();

	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	// properties
	private Program oncologyProgram;
	private List<Program> oncologyPrograms = new ArrayList<Program>();
	private ProgramWorkflowState suspecteddiagnosisstate;
	private ProgramWorkflowState notCancerdiagnosisstate;
	private ProgramWorkflowState acutelymphoblasticcancer;
	private ProgramWorkflowState breastCancer;
	private ProgramWorkflowState burkittLymphoma;
	private ProgramWorkflowState cancerunkowntype;
	private ProgramWorkflowState cervicalcancer;
	private ProgramWorkflowState chronicmyelogenousleukemia;
	private ProgramWorkflowState colorectalcancer;
	private ProgramWorkflowState headandneckcancer;
	private ProgramWorkflowState hodgkinslymphoma;
	private ProgramWorkflowState karposisarcoma;
	private ProgramWorkflowState largebcelllymphoma;
	private ProgramWorkflowState lungcancerdiagnosis;
	private ProgramWorkflowState metastaticcancer;
	private ProgramWorkflowState multiplemyeloma;
	private ProgramWorkflowState neuphroblastoma;
	private ProgramWorkflowState otherliquidcancer;
	private ProgramWorkflowState othernonhodkinslymphoma;
	private ProgramWorkflowState othersolidcancer;
	private ProgramWorkflowState prostatecancer;
	private ProgramWorkflowState stomachcancer;
	private Form demographicform;
	private Form inpatientOncForm;
	private Form outpatientOncForm;
	private Form outpatientclinicvisitform;
	private Form exitform;
	private List<Form> demographicsAndClinicalforms = new ArrayList<Form>();
	private List<Form> clinicalIntakeForms = new ArrayList<Form>();
	private List<Form> intakeoutpatientclinicvisitflowform = new ArrayList<Form>();
	private List<Form> exitandDemoforms = new ArrayList<Form>();
	private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
	private List<String> startDateEndDate = new ArrayList<String>();
	private Concept socioassistance;
	private Concept foodassistance;
	private Concept schoolassistance;
	private Concept transportassistance;
	private Concept homeassistance;
	private Concept referralType;
	private Concept healthclinic;
	private Concept districhospital;
	private Concept referralhospital;
	private Concept othernonCoded;
	private Concept notReferred;
	private Concept hivStatus;
	private Concept positiveStatus;
	private Concept locreferralType;
	private Concept outsideofRwanda;
	private Concept insideintakedistrict;
	private Concept outsideintakedistrict;
	private Concept visitType;
    private Concept unscheduledVisitType;
	private Concept oncologyprogramendreason;
	private Concept palliationonlycare;
	private Concept cancerrelateddeath;
	private Concept noncancerrelateddeath;
	private Concept deathreasonunknown;

	public void setup() throws Exception {

		setUpProperties();

		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));

		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");
		rd.addParameter(new Parameter("location", "Location",
				AllLocation.class, properties));

		rd.setName("ONC-Indicator Report-Quarterly");

		rd.addDataSetDefinition(
				createQuarterlyLocationDataSet(),
				ParameterizableUtil
						.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		// Based on Program cohort Definition
		/*
		 * ProgramEnrollmentCohortDefinition patientEnrolledInOncology = new
		 * ProgramEnrollmentCohortDefinition();
		 * patientEnrolledInOncology.addParameter(new
		 * Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		 * patientEnrolledInOncology.setPrograms(oncologyPrograms);
		 */
		// rd.setBaseCohortDefinition(patientEnrolledInOncology,
		// ParameterizableUtil.createParameterMappings("enrolledOnOrBefore=${endDate}"));

		h.saveReportDefinition(rd);

		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd,
				"OncologyQuarterlyIndicatorReport.xls",
				"OncologyQuarterlyIndicatorReport", null);
		Properties props = new Properties();
		props.put("repeatingSections",
				"sheet:1,dataset:Encounter Quarterly Data Set");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		h.saveReportDesign(design);

	}

	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("OncologyQuarterlyIndicatorReport".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("ONC-Indicator Report-Quarterly");

	}

	public LocationHierachyIndicatorDataSetDefinition createQuarterlyLocationDataSet() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createQuarterlyEncounterBaseDataSet());
		ldsd.addBaseDefinition(createQuarterlyBaseDataSet());
		ldsd.setName("Encounter Quarterly Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District",
				LocationHierarchy.class));

		return ldsd;
	}

	private EncounterIndicatorDataSetDefinition createQuarterlyEncounterBaseDataSet() {

		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();

		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		createQuarterlyIndicators(eidsd);
		return eidsd;
	}

	private void createQuarterlyIndicators(
			EncounterIndicatorDataSetDefinition dsd) {

	}

	// create monthly cohort Data set

	private CohortIndicatorDataSetDefinition createQuarterlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Quarterly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		createQuarterlyIndicators(dsd);
		return dsd;
	}

	private void createQuarterlyIndicators(CohortIndicatorDataSetDefinition dsd) {

		// =============================================================== //
		/* Demographics of NEW cases in the last quarter, pedi and adults */
		// ================================================================ //

		AgeCohortDefinition over15Cohort = Cohorts
				.createOver15AgeCohort("ageQD: Over 15");
		AgeCohortDefinition under15Cohort = Cohorts
				.createUnder15AgeCohort("ageQD: Under 15");
		GenderCohortDefinition femaleCohort = Cohorts
				.createFemaleCohortDefinition("femalesDefinition");
		GenderCohortDefinition maleCohort = Cohorts
				.createMaleCohortDefinition("malesDefinition");

		InProgramCohortDefinition inOncologyProgram = Cohorts
				.createInProgramParameterizableByDate("inOncologyProgram",
						oncologyProgram, "onDate");

		EncounterCohortDefinition patientWithDemoandIntakeForms = Cohorts
				.createEncounterBasedOnForms("patientWithDemoandIntakeForms",
						onOrAfterOnOrBefore, demographicsAndClinicalforms);

		CompositionCohortDefinition newCasesunder15withintakeDemoform = new CompositionCohortDefinition();
		newCasesunder15withintakeDemoform
				.setName("newCasesunder15withintakeDemoform");
		newCasesunder15withintakeDemoform.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newCasesunder15withintakeDemoform.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newCasesunder15withintakeDemoform.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newCasesunder15withintakeDemoform
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								patientWithDemoandIntakeForms,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesunder15withintakeDemoform.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(inOncologyProgram,
						ParameterizableUtil
								.createParameterMappings("onDate=${onDate}")));
		newCasesunder15withintakeDemoform.getSearches().put("3",
				new Mapped<CohortDefinition>(under15Cohort, null));
		newCasesunder15withintakeDemoform.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition newCasesOver15withintakeDemoform = new CompositionCohortDefinition();
		newCasesOver15withintakeDemoform
				.setName("newCasesOver15witnintadeDemoform");
		newCasesOver15withintakeDemoform.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newCasesOver15withintakeDemoform.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newCasesOver15withintakeDemoform.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newCasesOver15withintakeDemoform
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								patientWithDemoandIntakeForms,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newCasesOver15withintakeDemoform.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(inOncologyProgram,
						ParameterizableUtil
								.createParameterMappings("onDate=${onDate}")));
		newCasesOver15withintakeDemoform.getSearches().put("3",
				new Mapped<CohortDefinition>(over15Cohort, null));
		newCasesOver15withintakeDemoform.setCompositionString("1 AND 2 AND 3");

		CohortIndicator under15DemoIntakeFormsIndicator = Indicators
				.newCountIndicator(
						"under15DemoIntakeFormsIndicator",
						newCasesunder15withintakeDemoform,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		CohortIndicator over15DemoIntakeFormsIndicator = Indicators
				.newCountIndicator(
						"patientWithDemoandIntakeFormsIndicator",
						newCasesOver15withintakeDemoform,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// ==========================================================================
		// //
		/*
		 * D1 Demographics of females NEW cases in the last quarter, pedi and
		 * adults
		 */
		// ==========================================================================
		// //

		CompositionCohortDefinition femalesnewCasesunder15 = new CompositionCohortDefinition();
		femalesnewCasesunder15.setName("femalesnewCasesunder15");
		femalesnewCasesunder15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		femalesnewCasesunder15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		femalesnewCasesunder15.addParameter(new Parameter("onDate", "onDate",
				Date.class));
		femalesnewCasesunder15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		femalesnewCasesunder15.getSearches().put("2",
				new Mapped<CohortDefinition>(femaleCohort, null));
		femalesnewCasesunder15.setCompositionString("1 AND 2");

		CompositionCohortDefinition femalesnewCasesOver15 = new CompositionCohortDefinition();
		femalesnewCasesOver15.setName("femalesnewCasesOver15");
		femalesnewCasesOver15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		femalesnewCasesOver15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		femalesnewCasesOver15.addParameter(new Parameter("onDate", "onDate",
				Date.class));
		femalesnewCasesOver15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		femalesnewCasesOver15.getSearches().put("2",
				new Mapped<CohortDefinition>(femaleCohort, null));
		femalesnewCasesOver15.setCompositionString("1 AND 2");

		CohortIndicator newCasesFemalesUnder15Indicator = Indicators
				.newCountIndicator(
						"newCasesFemalesUnder15Indicator",
						femalesnewCasesunder15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		CohortIndicator newCasesFemalesOver15Indicator = Indicators
				.newCountIndicator(
						"newCasesFemalesOver15Indicator",
						femalesnewCasesOver15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// =========================================================================
		// //
		/*
		 * D2 Demographics of males NEW cases in the last quarter, pedi and
		 * adults
		 */
		// =========================================================================
		// //

		CompositionCohortDefinition malesnewCasesunder15 = new CompositionCohortDefinition();
		malesnewCasesunder15.setName("malesnewCasesunder15");
		malesnewCasesunder15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		malesnewCasesunder15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		malesnewCasesunder15.addParameter(new Parameter("onDate", "onDate",
				Date.class));
		malesnewCasesunder15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		malesnewCasesunder15.getSearches().put("2",
				new Mapped<CohortDefinition>(maleCohort, null));
		malesnewCasesunder15.setCompositionString("1 AND 2");

		CompositionCohortDefinition malesnewCasesOver15 = new CompositionCohortDefinition();
		malesnewCasesOver15.setName("malesnewCasesOver15");
		malesnewCasesOver15.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		malesnewCasesOver15.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		malesnewCasesOver15.addParameter(new Parameter("onDate", "onDate",
				Date.class));
		malesnewCasesOver15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		malesnewCasesOver15.getSearches().put("2",
				new Mapped<CohortDefinition>(maleCohort, null));
		malesnewCasesOver15.setCompositionString("1 AND 2");

		CohortIndicator newCasesmalesUnder15Indicator = Indicators
				.newCountIndicator(
						"newCasesmalesUnder15Indicator",
						malesnewCasesunder15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		CohortIndicator newCasesmalesOver15Indicator = Indicators
				.newCountIndicator(
						"newCasesmalesOver15Indicator",
						malesnewCasesOver15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// =======================================================================
		// //
		// D5 % of new cases recommended for socioeconomic assistance at intake
		// */
		// ======================================================================
		// //

		SqlCohortDefinition foodAssistancerec = Cohorts
				.getPatientWithProgramAndConcept(
						"patientWithsocioAssistanceRecommended",
						oncologyProgram, socioassistance, foodassistance);
		SqlCohortDefinition schoolAssistancerec = Cohorts
				.getPatientWithProgramAndConcept(
						"patientWithsocioAssistanceRecommended",
						oncologyProgram, socioassistance, schoolassistance);
		SqlCohortDefinition transportAssistancerec = Cohorts
				.getPatientWithProgramAndConcept(
						"patientWithsocioAssistanceRecommended",
						oncologyProgram, socioassistance, transportassistance);
		SqlCohortDefinition homeAssistancerec = Cohorts
				.getPatientWithProgramAndConcept(
						"patientWithsocioAssistanceRecommended",
						oncologyProgram, socioassistance, homeassistance);
		SqlCohortDefinition otherAssistancerec = Cohorts
				.getPatientWithProgramAndConcept(
						"patientWithsocioAssistanceRecommended",
						oncologyProgram, socioassistance, othernonCoded);

		CompositionCohortDefinition patientsWithsocioeconomicassistancerecmended = new CompositionCohortDefinition();
		patientsWithsocioeconomicassistancerecmended
				.setName("patientsWithsocioeconomicassistancerecmended");
		patientsWithsocioeconomicassistancerecmended.getSearches().put("1",
				new Mapped<CohortDefinition>(foodAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("2",
				new Mapped<CohortDefinition>(schoolAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("3",
				new Mapped<CohortDefinition>(homeAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("4",
				new Mapped<CohortDefinition>(otherAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended.getSearches().put("5",
				new Mapped<CohortDefinition>(transportAssistancerec, null));
		patientsWithsocioeconomicassistancerecmended
				.setCompositionString("1 OR 2 OR 3 OR 4 OR 5");

		CompositionCohortDefinition patsWithSocioassistanceRecUnder15 = new CompositionCohortDefinition();
		patsWithSocioassistanceRecUnder15
				.setName("patsWithSocioassistanceRecUnder15");
		patsWithSocioassistanceRecUnder15.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		patsWithSocioassistanceRecUnder15.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		patsWithSocioassistanceRecUnder15.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		patsWithSocioassistanceRecUnder15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patsWithSocioassistanceRecUnder15.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(
						patientsWithsocioeconomicassistancerecmended, null));
		patsWithSocioassistanceRecUnder15.setCompositionString("1 AND 2");

		CompositionCohortDefinition patsWithSocioassistanceRecOver15 = new CompositionCohortDefinition();
		patsWithSocioassistanceRecOver15
				.setName("patsWithSocioassistanceRecOver15");
		patsWithSocioassistanceRecOver15.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		patsWithSocioassistanceRecOver15.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		patsWithSocioassistanceRecOver15.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		patsWithSocioassistanceRecOver15
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patsWithSocioassistanceRecOver15.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(
						patientsWithsocioeconomicassistancerecmended, null));
		patsWithSocioassistanceRecOver15.setCompositionString("1 AND 2");

		CohortIndicator newCasesocioeconomicrecUnder15Indicator = Indicators
				.newCountIndicator(
						"newCasesocioeconomicrecUnder15Indicator",
						patsWithSocioassistanceRecUnder15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		CohortIndicator newCasessocioeconomicrecOver15Indicator = Indicators
				.newCountIndicator(
						"newCasessocioeconomicrecOver15Indicator",
						patsWithSocioassistanceRecOver15,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// ================================================================== //
		// D6 % breakdown of new cases' referral facility type at intake */
		// ================================================================ //
		// Health center referral facility type
		CodedObsCohortDefinition patientsreferredforHealthClinic = Cohorts.createCodedObsCohortDefinition("patientsreferredforHealthClinic", onOrAfterOnOrBefore,referralType, healthclinic, SetComparator.IN,TimeModifier.LAST);
		CompositionCohortDefinition pedPatientsreferredForHC = new CompositionCohortDefinition();
		pedPatientsreferredForHC.setName("pedPatientsreferredForHC");
		pedPatientsreferredForHC.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedPatientsreferredForHC.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedPatientsreferredForHC.addParameter(new Parameter("onDate","onDate", Date.class));
		pedPatientsreferredForHC.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pedPatientsreferredForHC.getSearches().put("2",new Mapped<CohortDefinition>(patientsreferredforHealthClinic, null));
		pedPatientsreferredForHC.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsreferredforHC = new CompositionCohortDefinition();
		adultsreferredforHC.setName("adultsreferredforHC");
		adultsreferredforHC.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredforHC.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredforHC.addParameter(new Parameter("onDate","onDate", Date.class));
		adultsreferredforHC.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		adultsreferredforHC.getSearches().put("2",new Mapped<CohortDefinition>(patientsreferredforHealthClinic, null));
		adultsreferredforHC.setCompositionString("1 AND 2");

		CohortIndicator pedPatientsreferredForHCIndicator= Indicators.newCountIndicator("pedPatientsreferredForHCIndicator",
				pedPatientsreferredForHC,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator adultsPatientsreferredForHCIndicator = Indicators.newCountIndicator("adultsPatientsreferredForHCIndicator",
				adultsreferredforHC,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

        // district hospital
		CodedObsCohortDefinition referredforDistrictHospital = Cohorts.createCodedObsCohortDefinition("referredforDistrictHospital",onOrAfterOnOrBefore, referralType, districhospital,SetComparator.IN, TimeModifier.LAST);
		CompositionCohortDefinition pedsreferredForDistrictHospital = new CompositionCohortDefinition();
		pedsreferredForDistrictHospital.setName("pedsreferredForDistrictHospital");
		pedsreferredForDistrictHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedsreferredForDistrictHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedsreferredForDistrictHospital.addParameter(new Parameter("onDate","onDate", Date.class));
		pedsreferredForDistrictHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pedsreferredForDistrictHospital.getSearches().put("2",new Mapped<CohortDefinition>(referredforDistrictHospital, null));
		pedsreferredForDistrictHospital.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsreferredForDistrictHospital = new CompositionCohortDefinition();
		adultsreferredForDistrictHospital.setName("adultsreferredForDistrictHospital");
		adultsreferredForDistrictHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredForDistrictHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredForDistrictHospital.addParameter(new Parameter("onDate","onDate", Date.class));
		adultsreferredForDistrictHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		adultsreferredForDistrictHospital.getSearches().put("2",new Mapped<CohortDefinition>(referredforDistrictHospital, null));
		adultsreferredForDistrictHospital.setCompositionString("1 AND 2");
		
		CohortIndicator pedsreferredForDistrictHospitalIndicator= Indicators.newCountIndicator("pedsreferredForDistrictHospitalIndicator",
				pedsreferredForDistrictHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator adultsreferredForDistrictHospitalIndicator = Indicators.newCountIndicator("adultsreferredForDistrictHospitalIndicator",
				adultsreferredForDistrictHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		//referral hospital
		CodedObsCohortDefinition referredforReferralHospital = Cohorts.createCodedObsCohortDefinition("referredforReferralHospital",onOrAfterOnOrBefore, referralType, referralhospital,SetComparator.IN, TimeModifier.LAST);
		CompositionCohortDefinition pedsreferredforReferralHospital = new CompositionCohortDefinition();
		pedsreferredforReferralHospital.setName("pedsreferredforReferralHospital");
		pedsreferredforReferralHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedsreferredforReferralHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedsreferredforReferralHospital.addParameter(new Parameter("onDate","onDate", Date.class));
		pedsreferredforReferralHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pedsreferredforReferralHospital.getSearches().put("2",new Mapped<CohortDefinition>(referredforReferralHospital, null));
		pedsreferredforReferralHospital.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsreferredforReferralHospital = new CompositionCohortDefinition();
		adultsreferredforReferralHospital.setName("adultsreferredforReferralHospital");
		adultsreferredforReferralHospital.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsreferredforReferralHospital.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsreferredforReferralHospital.addParameter(new Parameter("onDate","onDate", Date.class));
		adultsreferredforReferralHospital.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		adultsreferredforReferralHospital.getSearches().put("2",new Mapped<CohortDefinition>(referredforReferralHospital, null));
		adultsreferredforReferralHospital.setCompositionString("1 AND 2");
		
		CohortIndicator pedsreferredforReferralHospitalIndicator= Indicators.newCountIndicator("pedsreferredforReferralHospitalIndicator",
				pedsreferredforReferralHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator adultsreferredforReferralHospitalIndicator = Indicators.newCountIndicator("adultsreferredforReferralHospitalIndicator",
				adultsreferredforReferralHospital,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// others
		CodedObsCohortDefinition withOtheReferral = Cohorts.createCodedObsCohortDefinition("withOtheReferral",onOrAfterOnOrBefore, referralType, othernonCoded,SetComparator.IN, TimeModifier.LAST);
		CompositionCohortDefinition pedsWithOtherReferral = new CompositionCohortDefinition();
		pedsWithOtherReferral.setName("pedsWithOtherReferral");
		pedsWithOtherReferral.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedsWithOtherReferral.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedsWithOtherReferral.addParameter(new Parameter("onDate","onDate", Date.class));
		pedsWithOtherReferral.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pedsWithOtherReferral.getSearches().put("2",new Mapped<CohortDefinition>(withOtheReferral, null));
		pedsWithOtherReferral.setCompositionString("1 AND 2");
        
		CompositionCohortDefinition adultsWithOtherReferral = new CompositionCohortDefinition();
		adultsWithOtherReferral.setName("adultsWithOtherReferral");
		adultsWithOtherReferral.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsWithOtherReferral.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsWithOtherReferral.addParameter(new Parameter("onDate","onDate", Date.class));
		adultsWithOtherReferral.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		adultsWithOtherReferral.getSearches().put("2",new Mapped<CohortDefinition>(withOtheReferral, null));
		adultsWithOtherReferral.setCompositionString("1 AND 2");
		
		CohortIndicator pedsWithOtherReferralIndicator= Indicators.newCountIndicator("pedsWithOtherReferralIndicator",
				pedsWithOtherReferral,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator adultsWithOtherReferralIndicator = Indicators.newCountIndicator("adultsWithOtherReferralIndicator",
				adultsWithOtherReferral,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		//not referred
		CodedObsCohortDefinition patsnotReferred = Cohorts.createCodedObsCohortDefinition("patsnotReferred",onOrAfterOnOrBefore, referralType, notReferred,SetComparator.IN, TimeModifier.LAST);
        CompositionCohortDefinition pedwithNoreferralType = new CompositionCohortDefinition();
		pedwithNoreferralType.setName("pedwithNoreferralType");
		pedwithNoreferralType.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedwithNoreferralType.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedwithNoreferralType.addParameter(new Parameter("onDate","onDate", Date.class));
		pedwithNoreferralType.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pedwithNoreferralType.getSearches().put("2",new Mapped<CohortDefinition>(patsnotReferred, null));
		pedwithNoreferralType.setCompositionString("1 AND 2");

		CompositionCohortDefinition adultsPatientswithreferralType = new CompositionCohortDefinition();
		adultsPatientswithreferralType.setName("adultsPatientswithreferralType");
		adultsPatientswithreferralType.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsPatientswithreferralType.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsPatientswithreferralType.addParameter(new Parameter("onDate","onDate", Date.class));
		adultsPatientswithreferralType.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		adultsPatientswithreferralType.getSearches().put("2",new Mapped<CohortDefinition>(patsnotReferred, null));
		adultsPatientswithreferralType.setCompositionString("1 AND 2");
		
        CohortIndicator pedwithNoreferralTypeIndicator= Indicators.newCountIndicator("pedwithNoreferralTypeIndicator",
				pedwithNoreferralType,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator adultswithreferralTypeIndicator = Indicators.newCountIndicator("adultswithreferralTypeIndicator",
				adultsPatientswithreferralType,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		
		//missing references
		CompositionCohortDefinition missingReferences = new CompositionCohortDefinition();
		missingReferences.setName("missingReferences");
		missingReferences.getSearches().put("1",new Mapped<CohortDefinition>(patientsreferredforHealthClinic, null));
		missingReferences.getSearches().put("2",new Mapped<CohortDefinition>(referredforDistrictHospital, null));
		missingReferences.getSearches().put("3",new Mapped<CohortDefinition>(referredforReferralHospital, null));
		missingReferences.getSearches().put("4",new Mapped<CohortDefinition>(withOtheReferral, null));
		missingReferences.getSearches().put("5",new Mapped<CohortDefinition>(patsnotReferred, null));
		missingReferences.setCompositionString("1 OR 2 OR 3 OR 4 OR 5");
		
		CompositionCohortDefinition pediWithMissingReferences = new CompositionCohortDefinition();
		pediWithMissingReferences.setName("pediWithMissingReferences");
		pediWithMissingReferences.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pediWithMissingReferences.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pediWithMissingReferences.addParameter(new Parameter("onDate","onDate", Date.class));
		pediWithMissingReferences.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pediWithMissingReferences.getSearches().put("2",new Mapped<CohortDefinition>(missingReferences, null));
		pediWithMissingReferences.setCompositionString("1 AND (NOT 2)");
		
		CompositionCohortDefinition adultsWithMissingReferences = new CompositionCohortDefinition();
		adultsWithMissingReferences.setName("adultsWithMissingReferences");
		adultsWithMissingReferences.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		adultsWithMissingReferences.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		adultsWithMissingReferences.addParameter(new Parameter("onDate","onDate", Date.class));
		adultsWithMissingReferences.getSearches().put("1",new Mapped<CohortDefinition>(newCasesOver15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		adultsWithMissingReferences.getSearches().put("2",new Mapped<CohortDefinition>(missingReferences, null));
		adultsWithMissingReferences.setCompositionString("1 AND (NOT 2)");
		
		CohortIndicator pediWithMissingReferencesIndi= Indicators.newCountIndicator("pediWithMissingReferencesIndi",
				pediWithMissingReferences,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator adultsWithMissingReferencesIndi = Indicators.newCountIndicator("adultsWithMissingReferencesInd",
				adultsWithMissingReferences,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
				
				

		 
		// ==============================================================================
		// //
		// D7 % of new referral cases referred from outside district of care at
		// intake */
		// ==============================================================================//
		CodedObsCohortDefinition insideIntakeDistrictinRwanda = Cohorts.createCodedObsCohortDefinition("insideIntakeDistrictinRwanda",onOrAfterOnOrBefore, locreferralType,insideintakedistrict, SetComparator.IN,TimeModifier.LAST);
		CodedObsCohortDefinition outsideIntakeDistrict = Cohorts.createCodedObsCohortDefinition("patWithLocreferredOutsideRwanda", onOrAfterOnOrBefore,locreferralType, outsideintakedistrict,SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition outsideRwanda = Cohorts.createCodedObsCohortDefinition("patWithLocreferredOutsideRwanda", onOrAfterOnOrBefore,locreferralType, outsideofRwanda, SetComparator.IN,TimeModifier.LAST);
		
		// Denominator:Total cases referred:
		
		CompositionCohortDefinition patientsReferredInRwandaAndInsideOusideIntake = new CompositionCohortDefinition();
		patientsReferredInRwandaAndInsideOusideIntake.setName("patientsReferredInRwandaAndInsideOusideIntake");
		patientsReferredInRwandaAndInsideOusideIntake.getSearches().put("1",new Mapped<CohortDefinition>(insideIntakeDistrictinRwanda, null));
		patientsReferredInRwandaAndInsideOusideIntake.getSearches().put("2",new Mapped<CohortDefinition>(outsideIntakeDistrict, null));
		patientsReferredInRwandaAndInsideOusideIntake.getSearches().put("3",new Mapped<CohortDefinition>(outsideRwanda,null));
		patientsReferredInRwandaAndInsideOusideIntake.setCompositionString("1 OR 2 OR 3");

		CompositionCohortDefinition totalnewCasesThatareReferred = new CompositionCohortDefinition();
		totalnewCasesThatareReferred.setName("totalnewCasesThatareReferred");
		totalnewCasesThatareReferred.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		totalnewCasesThatareReferred.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		totalnewCasesThatareReferred.addParameter(new Parameter("onDate","onDate", Date.class));
		totalnewCasesThatareReferred.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		totalnewCasesThatareReferred.getSearches().put("2",new Mapped<CohortDefinition>(patientsReferredInRwandaAndInsideOusideIntake, null));
		totalnewCasesThatareReferred.setCompositionString("1 AND 2");
		
		//refered inside Rwanda District
		CompositionCohortDefinition referredinsideRwandanDistrict=new CompositionCohortDefinition();
		referredinsideRwandanDistrict.setName("referredinsideRwandanDistrict");
		referredinsideRwandanDistrict.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		referredinsideRwandanDistrict.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		referredinsideRwandanDistrict.addParameter(new Parameter("onDate","onDate", Date.class));
		referredinsideRwandanDistrict.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		referredinsideRwandanDistrict.getSearches().put("2",new Mapped<CohortDefinition>(insideIntakeDistrictinRwanda, null));
		referredinsideRwandanDistrict.setCompositionString("1 AND 2");
		
		//outside intake district within Rwanda.
		CompositionCohortDefinition referredoutsideIntakedistrictInRwanda=new CompositionCohortDefinition();
		referredoutsideIntakedistrictInRwanda.setName("referredoutsideIntakedistrictInRwanda");
		referredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		referredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		referredoutsideIntakedistrictInRwanda.addParameter(new Parameter("onDate","onDate", Date.class));
		referredoutsideIntakedistrictInRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		referredoutsideIntakedistrictInRwanda.getSearches().put("2",new Mapped<CohortDefinition>(outsideIntakeDistrict, null));
		referredoutsideIntakedistrictInRwanda.setCompositionString("1 AND 2");
		
		//outside Rwanda.
		CompositionCohortDefinition referredOutsideOfRwanda=new CompositionCohortDefinition();
		referredOutsideOfRwanda.setName("referredOutsideOfRwanda");
		referredOutsideOfRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		referredOutsideOfRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		referredOutsideOfRwanda.addParameter(new Parameter("onDate","onDate", Date.class));
		referredOutsideOfRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		referredOutsideOfRwanda.getSearches().put("2",new Mapped<CohortDefinition>(outsideRwanda, null));
		referredOutsideOfRwanda.setCompositionString("1 AND 2");
		
		// Numerator
		
		CohortIndicator pedinewCasesThatareReferredIndicator = Indicators.newCountIndicator("pedinewCasesThatareReferredIndicator",
				totalnewCasesThatareReferred,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator referredinsideRwandanDistrictIndi = Indicators.newCountIndicator("referredinsideRwandanDistrictIndi",
				referredinsideRwandanDistrict,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
        
		CohortIndicator referredoutsideIntakedistrictInRwandaIndi = Indicators.newCountIndicator("referredoutsideIntakedistrictInRwandaInd",
				referredoutsideIntakedistrictInRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator referredOutsideOfRwandaIndicator = Indicators.newCountIndicator("referredOutsideOfRwandaIndicator",
				referredOutsideOfRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// ======================================================= //
		// D8 % breakdown of new cases' home district at intake */
		// ======================================================= //

		// peds; GIS map; Inside home district:
		// 1) Kayonza,
		SqlCohortDefinition patientsLocatedInKayonzaDistrict = Cohorts
				.getPatientWithStructuredDistrict("patientsLocatedInKireheDistrict", "Kayonza");
		SqlCohortDefinition patientsLocatedInBureraDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKireheDistrict", "Burera");
		SqlCohortDefinition patientsLocatedInKireheDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKireheDistrict", "Kirehe");
		SqlCohortDefinition patientsLocatedInNyarugengeDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKigaliDistrict", "Nyarugenge");
		SqlCohortDefinition patientsLocatedInKicukiroDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKigaliDistrict", "Kicukiro");
		SqlCohortDefinition patientsLocatedInGasaboDistrict = Cohorts.getPatientWithStructuredDistrict("patientsLocatedInKigaliDistrict", "Gasabo");
		SqlCohortDefinition patientwithUnstructuredDistrict = Cohorts.getPatientWithunStructuredDistrict("patientwithUnstructuredDistrict");

		CompositionCohortDefinition patientsLocatedInKayonzaDistrictComposition = new CompositionCohortDefinition();
		patientsLocatedInKayonzaDistrictComposition.setName("patientsLocatedInKayonzaDistrictComposition");
		patientsLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsLocatedInKayonzaDistrictComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
		patientsLocatedInKayonzaDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientsLocatedInKayonzaDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKayonzaDistrict,null));
		patientsLocatedInKayonzaDistrictComposition.setCompositionString("1 AND 2");

		// 2) Burera,
		CompositionCohortDefinition patientsLocatedInBureraDistrictComposition = new CompositionCohortDefinition();
		patientsLocatedInBureraDistrictComposition
				.setName("patientsLocatedInBureraDistrictComposition");
		patientsLocatedInBureraDistrictComposition.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		patientsLocatedInBureraDistrictComposition.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		patientsLocatedInBureraDistrictComposition.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		patientsLocatedInBureraDistrictComposition
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientsLocatedInBureraDistrictComposition.getSearches().put(
				"2",
				new Mapped<CohortDefinition>(patientsLocatedInBureraDistrict,
						null));
		patientsLocatedInBureraDistrictComposition
				.setCompositionString("1 AND 2");

		// 3) Kirehe,
		CompositionCohortDefinition patientsLocatedInKireheDistrictComposition = new CompositionCohortDefinition();
		patientsLocatedInKireheDistrictComposition.setName("patientsLocatedInKireheDistrictComposition");
		patientsLocatedInKireheDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsLocatedInKireheDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsLocatedInKireheDistrictComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
		patientsLocatedInKireheDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientsLocatedInKireheDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInKireheDistrict,null));
		patientsLocatedInKireheDistrictComposition.setCompositionString("1 AND 2");

		// 4) Kigali,
		CompositionCohortDefinition patientsLocatedInKigaliDistrictComposition = new CompositionCohortDefinition();
		patientsLocatedInKigaliDistrictComposition.setName("patientsLocatedInKigaliDistrictComposition");
		patientsLocatedInKigaliDistrictComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsLocatedInKigaliDistrictComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsLocatedInKigaliDistrictComposition.addParameter(new Parameter("onDate", "onDate", Date.class));
		patientsLocatedInKigaliDistrictComposition.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientsLocatedInKigaliDistrictComposition.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInNyarugengeDistrict, null));
		patientsLocatedInKigaliDistrictComposition.getSearches().put("3",new Mapped<CohortDefinition>(patientsLocatedInKicukiroDistrict,null));
		patientsLocatedInKigaliDistrictComposition.getSearches().put("4",new Mapped<CohortDefinition>(patientsLocatedInGasaboDistrict,null));
		patientsLocatedInKigaliDistrictComposition.setCompositionString("1 AND (2 OR 3 OR 4)");

		// 5) Other Rwanda district,
		CompositionCohortDefinition patientsWithOtherRwandanDistrict = new CompositionCohortDefinition();
		patientsWithOtherRwandanDistrict.setName("patientsWithOtherRwandanDistrict");
		patientsWithOtherRwandanDistrict.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWithOtherRwandanDistrict.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsWithOtherRwandanDistrict.addParameter(new Parameter("onDate","onDate", Date.class));
		patientsWithOtherRwandanDistrict.getSearches().put("1",new Mapped<CohortDefinition>(patientsLocatedInKayonzaDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("2",new Mapped<CohortDefinition>(patientsLocatedInBureraDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("3",new Mapped<CohortDefinition>(patientsLocatedInKireheDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("4",new Mapped<CohortDefinition>(patientsLocatedInNyarugengeDistrict, null));
		patientsWithOtherRwandanDistrict.getSearches().put("5",new Mapped<CohortDefinition>(patientsLocatedInKicukiroDistrict,null));
		patientsWithOtherRwandanDistrict.getSearches().put("6",new Mapped<CohortDefinition>(patientsLocatedInGasaboDistrict,null));
		patientsWithOtherRwandanDistrict.setCompositionString("1 OR 2 OR 3 OR 4 OR 5 OR 6");

		SqlCohortDefinition notNullCountryAddress = new SqlCohortDefinition("select DISTINCT(p.patient_id) FROM patient p,person_address pa WHERE p.patient_id=pa.person_id AND pa.preferred=1 AND p.voided=0 AND country is not NULL");
		SqlCohortDefinition notNullCountryDistrictAddress = new SqlCohortDefinition("select DISTINCT(p.patient_id) FROM patient p,person_address pa WHERE p.patient_id=pa.person_id AND pa.preferred=1 AND p.voided=0 AND country is not NULL AND county_district is not NULL");
		   
        //Other Rwandan districts
        CompositionCohortDefinition patientsLocatedInOtherRwandanDistricts = new CompositionCohortDefinition();
		patientsLocatedInOtherRwandanDistricts.setName("patientsLocatedInOtherRwandanDistricts");
		patientsLocatedInOtherRwandanDistricts.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsLocatedInOtherRwandanDistricts.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientsLocatedInOtherRwandanDistricts.addParameter(new Parameter("onDate", "onDate", Date.class));
		patientsLocatedInOtherRwandanDistricts.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientsLocatedInOtherRwandanDistricts.getSearches().put("2",new Mapped<CohortDefinition>(notNullCountryDistrictAddress,null));
		patientsLocatedInOtherRwandanDistricts.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithOtherRwandanDistrict,null));
		patientsLocatedInOtherRwandanDistricts.setCompositionString("1 AND 2 AND (NOT 3)");

		//Patient with missing address
		CompositionCohortDefinition patientswithMissingAddress = new CompositionCohortDefinition();
		patientswithMissingAddress.setName("patientswithMissingAddress");
		patientswithMissingAddress.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientswithMissingAddress.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientswithMissingAddress.addParameter(new Parameter("onDate", "onDate", Date.class));
		patientswithMissingAddress.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
				ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientswithMissingAddress.getSearches().put("2",new Mapped<CohortDefinition>(patientwithUnstructuredDistrict,null));
		patientswithMissingAddress.getSearches().put("3",new Mapped<CohortDefinition>(patientsWithOtherRwandanDistrict,null));
		patientswithMissingAddress.setCompositionString("1 AND 2 AND (NOT 3)");

		// 6) Outside Rwanda
		SqlCohortDefinition inRwanda = new SqlCohortDefinition("select DISTINCT(p.patient_id) FROM patient p,person_address pa WHERE p.patient_id=pa.person_id AND pa.preferred=1 AND p.voided=0 AND country='Rwanda' AND country is not NULL");
		InverseCohortDefinition patientsnotInRwanda = new InverseCohortDefinition(inRwanda);

		CompositionCohortDefinition patientsfromOutsideOfRwanda = new CompositionCohortDefinition();
		patientsfromOutsideOfRwanda.setName("patientsfromOutsideOfRwanda");
		patientsfromOutsideOfRwanda.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		patientsfromOutsideOfRwanda.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		patientsfromOutsideOfRwanda.addParameter(new Parameter("onDate","onDate", Date.class));
		patientsfromOutsideOfRwanda.getSearches().put("1",new Mapped<CohortDefinition>(newCasesunder15withintakeDemoform,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		patientsfromOutsideOfRwanda.getSearches().put("2",new Mapped<CohortDefinition>(notNullCountryAddress, null));
		patientsfromOutsideOfRwanda.getSearches().put("3",new Mapped<CohortDefinition>(patientsnotInRwanda, null));
		patientsfromOutsideOfRwanda.setCompositionString("1 AND 2 AND 3");

		CohortIndicator patientsLocatedInKayonzaDistrictIndi = Indicators
				.newCountIndicator(
						"patientsLocatedInKayonzaDistrictIndi",
						patientsLocatedInKayonzaDistrictComposition,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator patientsLocatedInBureraDistrictInd = Indicators
				.newCountIndicator(
						"patientsLocatedInBureraDistrictInd",
						patientsLocatedInBureraDistrictComposition,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator patientsLocatedInKireheDistrictIndicator = Indicators
				.newCountIndicator(
						"patientsLocatedInKireheDistrictIndicator",
						patientsLocatedInKireheDistrictComposition,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator patientsLocatedInKigaliDistrictIndicator = Indicators
				.newCountIndicator(
						"patientsLocatedInKigaliDistrictIndicator",
						patientsLocatedInKigaliDistrictComposition,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator patientsLocatedInOtherRwandanDistrictsIndicator = Indicators.newCountIndicator("patientsLocatedInOtherRwandanDistrictsIndicator",
						patientsLocatedInOtherRwandanDistricts,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		
		CohortIndicator patientsLocatedWithMissingIndicator = Indicators.newCountIndicator("patientsLocatedWithMissingIndicator",
				patientswithMissingAddress,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator patientsfromOutsideOfRwandaIndicator = Indicators.newCountIndicator("patientsfromOutsideOfRwandaIndicator",
						patientsfromOutsideOfRwanda,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// ===========================================================================================
		// //
		// D9 Of the new cases with HIV status documented at intake, % with a
		// positive HIV status */
		// ==========================================================================================
		// //

		CodedObsCohortDefinition patientswithHivStatus = Cohorts
				.createCodedObsCohortDefinition("patientswithHivStatus",
						hivStatus, null, SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition patientWithPosHivStatus = Cohorts
				.createCodedObsCohortDefinition("patientWithPosHivStatus",
						hivStatus, positiveStatus, SetComparator.IN,
						TimeModifier.LAST);

		// Denominator
		CompositionCohortDefinition newCasespediWithHiVstatus = new CompositionCohortDefinition();
		newCasespediWithHiVstatus.setName("newCasespediWithHiVstatus");
		newCasespediWithHiVstatus.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newCasespediWithHiVstatus.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newCasespediWithHiVstatus.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newCasespediWithHiVstatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newCasespediWithHiVstatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientswithHivStatus, null));
		newCasespediWithHiVstatus.setCompositionString("1 AND 2");

		CompositionCohortDefinition newCasesadultsWithHiVstatus = new CompositionCohortDefinition();
		newCasesadultsWithHiVstatus.setName("newCasesadultsWithHiVstatus");
		newCasesadultsWithHiVstatus.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newCasesadultsWithHiVstatus.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newCasesadultsWithHiVstatus.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newCasesadultsWithHiVstatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newCasesadultsWithHiVstatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientswithHivStatus, null));
		newCasesadultsWithHiVstatus.setCompositionString("1 AND 2");

		// Numerator
		CompositionCohortDefinition newCasespediWithHiVPositivestatus = new CompositionCohortDefinition();
		newCasespediWithHiVPositivestatus
				.setName("newCasespediWithHiVPositivestatus");
		newCasespediWithHiVPositivestatus.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newCasespediWithHiVPositivestatus.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newCasespediWithHiVPositivestatus.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newCasespediWithHiVPositivestatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesunder15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newCasespediWithHiVPositivestatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientWithPosHivStatus, null));
		newCasespediWithHiVPositivestatus.setCompositionString("1 AND 2");

		CompositionCohortDefinition newCasesadultsWithHiVPositivestatus = new CompositionCohortDefinition();
		newCasesadultsWithHiVPositivestatus
				.setName("newCasesadultsWithHiVPositivestatus");
		newCasesadultsWithHiVPositivestatus.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newCasesadultsWithHiVPositivestatus.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newCasesadultsWithHiVPositivestatus.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newCasesadultsWithHiVPositivestatus
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newCasesOver15withintakeDemoform,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newCasesadultsWithHiVPositivestatus.getSearches().put("2",
				new Mapped<CohortDefinition>(patientWithPosHivStatus, null));
		newCasesadultsWithHiVPositivestatus.setCompositionString("1 AND 2");

		CohortIndicator newCasespediWithHiVstatusIndicator = Indicators
				.newCountIndicator(
						"newCasespediWithHiVstatusIndicator",
						newCasespediWithHiVstatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newCasesadultsWithHiVstatusIndicator = Indicators
				.newCountIndicator(
						"newCasesadultsWithHiVstatusIndicator",
						newCasesadultsWithHiVstatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newCasespediWithHiVPositivestatusIndicator = Indicators
				.newCountIndicator(
						"newCasespediWithHiVPositivestatusIndicator",
						newCasespediWithHiVPositivestatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newCasesadultsWithHiVPositivestatusIndicator = Indicators
				.newCountIndicator(
						"newCasesadultsWithHiVPositivestatusIndicator",
						newCasesadultsWithHiVPositivestatus,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// ===========================================================================================
		// //
		// A1 Total # of patients ever enrolled in program at the end of the
		// review quarter */
		// ==========================================================================================
		// //

		EncounterCohortDefinition patientWithClinicalIntakeForm = Cohorts
				.createEncounterBasedOnForms("patientWithClinicalIntakeForms",
						onOrAfterOnOrBefore, clinicalIntakeForms);
		InProgramCohortDefinition everEnrolleInOncologyProgram = Cohorts
				.createInProgramParameterizableByDate(
						"everEnrolleInOncologyProgram", oncologyProgram,
						"onDate");
		SqlCohortDefinition under15YrsAtEnrol = Cohorts
				.createUnder15AtEnrollmentCohort("under15YrsAtEnrol",
						oncologyProgram);

		CompositionCohortDefinition pedsWitClinicalIntakeintheReviewQuarter = new CompositionCohortDefinition();
		pedsWitClinicalIntakeintheReviewQuarter
				.setName("pedsWitClinicalIntakeintheReviewQuarter");
		pedsWitClinicalIntakeintheReviewQuarter.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		pedsWitClinicalIntakeintheReviewQuarter
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								patientWithClinicalIntakeForm,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore}")));
		pedsWitClinicalIntakeintheReviewQuarter.getSearches().put("2",
				new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		pedsWitClinicalIntakeintheReviewQuarter.getSearches()
				.put("3",
						new Mapped<CohortDefinition>(
								everEnrolleInOncologyProgram, null));
		pedsWitClinicalIntakeintheReviewQuarter
				.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition adultsWitClinicalIntakeintheReviewQuarter = new CompositionCohortDefinition();
		adultsWitClinicalIntakeintheReviewQuarter
				.setName("adultsWitClinicalIntakeintheReviewQuarter");
		adultsWitClinicalIntakeintheReviewQuarter.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		adultsWitClinicalIntakeintheReviewQuarter
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								patientWithClinicalIntakeForm,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore}")));
		adultsWitClinicalIntakeintheReviewQuarter.getSearches().put("2",
				new Mapped<CohortDefinition>(over15Cohort, null));
		adultsWitClinicalIntakeintheReviewQuarter.getSearches()
				.put("3",
						new Mapped<CohortDefinition>(
								everEnrolleInOncologyProgram, null));
		adultsWitClinicalIntakeintheReviewQuarter
				.setCompositionString("1 AND 2 AND 3");

		CohortIndicator pedsWitClinicalIntakeintheReviewQuarterIndicator = Indicators
				.newCountIndicator(
						"pedsWitClinicalIntakeintheReviewQuarterIndicator",
						pedsWitClinicalIntakeintheReviewQuarter,
						ParameterizableUtil
								.createParameterMappings("onOrBefore=${endDate}"));

		CohortIndicator adultsWitClinicalIntakeintheReviewQuarterIndicator = Indicators
				.newCountIndicator(
						"adultsWitClinicalIntakeintheReviewQuarterIndicator",
						adultsWitClinicalIntakeintheReviewQuarter,
						ParameterizableUtil
								.createParameterMappings("onOrBefore=${endDate}"));

		// ==================================================================================//
		// A2 Total # of new patients not suspected of cancer at intake in the
		// last quarter */
		// ===================================================================================//
		List<ProgramWorkflowState> notsuspectedOnCancer = new ArrayList<ProgramWorkflowState>();
		notsuspectedOnCancer.add(notCancerdiagnosisstate);
		InStateCohortDefinition notSuspectedstateOnOrBefore = Cohorts
				.createInCurrentState("ONC:inSuspectedstateOnOrBefore",
						notsuspectedOnCancer, onOrAfterOnOrBefore);

		// Ped
		CompositionCohortDefinition pediAtIntakeInTheProgram = new CompositionCohortDefinition();
		pediAtIntakeInTheProgram.setName("pediAtIntakeInTheProgram");
		pediAtIntakeInTheProgram.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		pediAtIntakeInTheProgram.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		pediAtIntakeInTheProgram.addParameter(new Parameter("onDate", "onDate",
				Date.class));
		pediAtIntakeInTheProgram.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(inOncologyProgram,
						ParameterizableUtil
								.createParameterMappings("onDate=${onDate}")));
		pediAtIntakeInTheProgram
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithClinicalIntakeForm,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pediAtIntakeInTheProgram.getSearches().put("3",
				new Mapped<CohortDefinition>(under15YrsAtEnrol, null));
		pediAtIntakeInTheProgram.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition adultsAtIntakeInTheProgram = new CompositionCohortDefinition();
		adultsAtIntakeInTheProgram.setName("adultsAtIntakeInTheProgram");
		adultsAtIntakeInTheProgram.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		adultsAtIntakeInTheProgram.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		adultsAtIntakeInTheProgram.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		adultsAtIntakeInTheProgram.getSearches().put(
				"1",
				new Mapped<CohortDefinition>(inOncologyProgram,
						ParameterizableUtil
								.createParameterMappings("onDate=${onDate}")));
		adultsAtIntakeInTheProgram
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithClinicalIntakeForm,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		adultsAtIntakeInTheProgram.getSearches().put("3",
				new Mapped<CohortDefinition>(over15Cohort, null));
		adultsAtIntakeInTheProgram.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition newPediNotSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newPediNotSuspectedCancerAtIntake
				.setName("newPediNotSuspectedCancerAtIntake");
		newPediNotSuspectedCancerAtIntake.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediNotSuspectedCancerAtIntake.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediNotSuspectedCancerAtIntake.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediNotSuspectedCancerAtIntake
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								pediAtIntakeInTheProgram,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediNotSuspectedCancerAtIntake
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								notSuspectedstateOnOrBefore,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediNotSuspectedCancerAtIntake.setCompositionString("1 AND 2 ");

		// Adults
		CompositionCohortDefinition newAdultsNotSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newAdultsNotSuspectedCancerAtIntake
				.setName("newAdultsNotSuspectedCancerAtIntake");
		newAdultsNotSuspectedCancerAtIntake.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsNotSuspectedCancerAtIntake.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsNotSuspectedCancerAtIntake.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsNotSuspectedCancerAtIntake
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								adultsAtIntakeInTheProgram,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsNotSuspectedCancerAtIntake
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								notSuspectedstateOnOrBefore,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsNotSuspectedCancerAtIntake.setCompositionString("1 AND 2");

		CohortIndicator newPediNotSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newPediNotSuspectedCancerAtIntakeIndicator",
						newPediNotSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsNotSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newPediNotSuspectedCancerAtIntakeIndicator",
						newAdultsNotSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// =========================================================================//
		// A3 Total # of new suspected cancer cases at intake in the last
		// quarter */
		// =========================================================================//
		List<ProgramWorkflowState> suspectedOnCancer = new ArrayList<ProgramWorkflowState>();
		suspectedOnCancer.add(suspecteddiagnosisstate);
		InStateCohortDefinition suspectedstateOnOrBefore = Cohorts
				.createInCurrentState("ONC:inSuspectedstateOnOrBefore",
						suspectedOnCancer, onOrAfterOnOrBefore);

		// adults and Pedi with all suspected cancer types (suspected state +all
		// diagnosis states):

		List<ProgramWorkflowState> suspectedCancerDiagnosisTypes = new ArrayList<ProgramWorkflowState>();
		suspectedCancerDiagnosisTypes.add(acutelymphoblasticcancer);
		suspectedCancerDiagnosisTypes.add(breastCancer);
		suspectedCancerDiagnosisTypes.add(burkittLymphoma);
		suspectedCancerDiagnosisTypes.add(cancerunkowntype);
		suspectedCancerDiagnosisTypes.add(cervicalcancer);
		suspectedCancerDiagnosisTypes.add(chronicmyelogenousleukemia);
		suspectedCancerDiagnosisTypes.add(colorectalcancer);
		suspectedCancerDiagnosisTypes.add(headandneckcancer);
		suspectedCancerDiagnosisTypes.add(hodgkinslymphoma);
		suspectedCancerDiagnosisTypes.add(karposisarcoma);
		suspectedCancerDiagnosisTypes.add(largebcelllymphoma);
		suspectedCancerDiagnosisTypes.add(lungcancerdiagnosis);
		suspectedCancerDiagnosisTypes.add(metastaticcancer);
		suspectedCancerDiagnosisTypes.add(multiplemyeloma);
		suspectedCancerDiagnosisTypes.add(neuphroblastoma);
		suspectedCancerDiagnosisTypes.add(otherliquidcancer);
		suspectedCancerDiagnosisTypes.add(othernonhodkinslymphoma);
		suspectedCancerDiagnosisTypes.add(othersolidcancer);
		suspectedCancerDiagnosisTypes.add(prostatecancer);
		suspectedCancerDiagnosisTypes.add(stomachcancer);
		InStateCohortDefinition patientWithAllSuspectedCancerDiagnosis = Cohorts
				.createInCurrentState(
						"ONC:patientWithAllSuspectedCancerDiagnosis",
						suspectedCancerDiagnosisTypes, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediSuspectedCanceratIntakeCases = new CompositionCohortDefinition();
		newPediSuspectedCanceratIntakeCases
				.setName("newPediSuspectedCanceratIntakeCases");
		newPediSuspectedCanceratIntakeCases.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediSuspectedCanceratIntakeCases.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediSuspectedCanceratIntakeCases.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediSuspectedCanceratIntakeCases
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								pediAtIntakeInTheProgram,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediSuspectedCanceratIntakeCases
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								suspectedstateOnOrBefore,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediSuspectedCanceratIntakeCases.setCompositionString("1 AND 2");

		CompositionCohortDefinition newPediSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newPediSuspectedCancerAtIntake
				.setName("newPediSuspectedCancerAtIntake");
		newPediSuspectedCancerAtIntake.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediSuspectedCancerAtIntake.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediSuspectedCancerAtIntake.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediSuspectedCancerAtIntake
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediSuspectedCancerAtIntake
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAllSuspectedCancerDiagnosis,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediSuspectedCancerAtIntake.setCompositionString("1 AND 2");

		CompositionCohortDefinition newAdultSuspectedCancerAtIntakeCases = new CompositionCohortDefinition();
		newAdultSuspectedCancerAtIntakeCases
				.setName("newAdultSuspectedCancerAtIntakeCases");
		newAdultSuspectedCancerAtIntakeCases.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultSuspectedCancerAtIntakeCases.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultSuspectedCancerAtIntakeCases.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultSuspectedCancerAtIntakeCases
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								adultsAtIntakeInTheProgram,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultSuspectedCancerAtIntakeCases
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								suspectedstateOnOrBefore,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultSuspectedCancerAtIntakeCases.setCompositionString("1 AND 2");

		CompositionCohortDefinition newAdultSuspectedCancerAtIntake = new CompositionCohortDefinition();
		newAdultSuspectedCancerAtIntake
				.setName("newPediSuspectedCancerAtIntake");
		newAdultSuspectedCancerAtIntake.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultSuspectedCancerAtIntake.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultSuspectedCancerAtIntake.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultSuspectedCancerAtIntake
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultSuspectedCancerAtIntake
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAllSuspectedCancerDiagnosis,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultSuspectedCancerAtIntake.setCompositionString("1 AND 2");

		CohortIndicator newPediSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newPediSuspectedCancerAtIntakeIndicator",
						newPediSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		CohortIndicator newAdultSuspectedCancerAtIntakeIndicator = Indicators
				.newCountIndicator(
						"newAdultSuspectedCancerAtIntakeIndicator",
						newAdultSuspectedCancerAtIntake,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// Acute lymphoblastic leukemia
		List<ProgramWorkflowState> acutelymphoblasticleukemiacancer = new ArrayList<ProgramWorkflowState>();
		acutelymphoblasticleukemiacancer.add(acutelymphoblasticcancer);
		InStateCohortDefinition patientWithAcutelypholeukemiaCancer = Cohorts
				.createInCurrentState("acutelymphoblasticleukemiacancer",
						acutelymphoblasticleukemiacancer, onOrAfterOnOrBefore);
		// Ped
		CompositionCohortDefinition newPediWithAcutelymphoblasticleukemia = new CompositionCohortDefinition();
		newPediWithAcutelymphoblasticleukemia
				.setName("newPediWithAcutelymphoblasticleukemia");
		newPediWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediWithAcutelymphoblasticleukemia
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithAcutelymphoblasticleukemia
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAcutelypholeukemiaCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithAcutelymphoblasticleukemia.setCompositionString("1 AND 2 ");

		// Adults
		CompositionCohortDefinition newAdultsWithAcutelymphoblasticleukemia = new CompositionCohortDefinition();
		newAdultsWithAcutelymphoblasticleukemia
				.setName("newAdultsWithAcutelymphoblasticleukemia");
		newAdultsWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithAcutelymphoblasticleukemia.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsWithAcutelymphoblasticleukemia
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithAcutelymphoblasticleukemia
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithAcutelypholeukemiaCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithAcutelymphoblasticleukemia
				.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithAcutelymphoblasticleukemiaIndicator = Indicators
				.newCountIndicator(
						"newPediWithAcutelymphoblasticleukemiaIndicator",
						newPediWithAcutelymphoblasticleukemia,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithAcutelymphoblasticleukemiaIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithAcutelymphoblasticleukemiaIndicator",
						newAdultsWithAcutelymphoblasticleukemia,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Breast Cancer
		List<ProgramWorkflowState> breastcancertype = new ArrayList<ProgramWorkflowState>();
		breastcancertype.add(breastCancer);
		InStateCohortDefinition patientWithBreastcancerCancer = Cohorts
				.createInCurrentState("breastcancertype", breastcancertype,
						onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithbreastcancertype = new CompositionCohortDefinition();
		newPediWithbreastcancertype.setName("newPediWithbreastcancertype");
		newPediWithbreastcancertype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithbreastcancertype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithbreastcancertype.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithbreastcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithbreastcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBreastcancerCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithbreastcancertype.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithbreastcancertype = new CompositionCohortDefinition();
		newAdultsWithbreastcancertype.setName("newAdultsWithbreastcancertype");
		newAdultsWithbreastcancertype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithbreastcancertype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newAdultsWithbreastcancertype.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithbreastcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithbreastcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBreastcancerCancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithbreastcancertype.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithbreastcancertypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithbreastcancertypeIndicator",
						newPediWithbreastcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithbreastcancertypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithbreastcancertypeIndicator",
						newAdultsWithbreastcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Burkitt's lymphoma
		List<ProgramWorkflowState> burkitlymphomacancer = new ArrayList<ProgramWorkflowState>();
		burkitlymphomacancer.add(burkittLymphoma);
		InStateCohortDefinition patientWithBurkitlymphoma = Cohorts
				.createInCurrentState("burkitlymphomacancer",
						burkitlymphomacancer, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithburkitlymphomacancer = new CompositionCohortDefinition();
		newPediWithburkitlymphomacancer
				.setName("newPediWithburkitlymphomacancer");
		newPediWithburkitlymphomacancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithburkitlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithburkitlymphomacancer.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithburkitlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithburkitlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBurkitlymphoma,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithburkitlymphomacancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithburkitlymphomacancer = new CompositionCohortDefinition();
		newAdultsWithburkitlymphomacancer
				.setName("newAdultsWithburkitlymphomacancer");
		newAdultsWithburkitlymphomacancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithburkitlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithburkitlymphomacancer.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithburkitlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithburkitlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithBurkitlymphoma,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithburkitlymphomacancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithburkitlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithburkitlymphomacancerIndicator",
						newPediWithburkitlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithburkitlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithburkitlymphomacancerIndicator",
						newAdultsWithburkitlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Cancer unknown type
		List<ProgramWorkflowState> cancerunknowntypeC = new ArrayList<ProgramWorkflowState>();
		cancerunknowntypeC.add(cancerunkowntype);
		InStateCohortDefinition patientCanceunknowntype = Cohorts
				.createInCurrentState("cancerunknowntypeC", cancerunknowntypeC,
						onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithcancerunknowntypeC = new CompositionCohortDefinition();
		newPediWithcancerunknowntypeC.setName("newPediWithcancerunknowntypeC");
		newPediWithcancerunknowntypeC.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithcancerunknowntypeC.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithcancerunknowntypeC.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithcancerunknowntypeC
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithcancerunknowntypeC
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientCanceunknowntype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcancerunknowntypeC.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithcancerunknowntypeC = new CompositionCohortDefinition();
		newAdultsWithcancerunknowntypeC
				.setName("newAdultsWithcancerunknowntypeC");
		newAdultsWithcancerunknowntypeC.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithcancerunknowntypeC.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithcancerunknowntypeC.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithcancerunknowntypeC
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithcancerunknowntypeC
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientCanceunknowntype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcancerunknowntypeC.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithcancerunknowntypeCIndicator = Indicators
				.newCountIndicator(
						"newPediWithcancerunknowntypeCIndicator",
						newPediWithcancerunknowntypeC,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithcancerunknowntypeCIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithcancerunknowntypeCIndicator",
						newAdultsWithcancerunknowntypeC,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Cervical cancer
		List<ProgramWorkflowState> cervicalCancerType = new ArrayList<ProgramWorkflowState>();
		cervicalCancerType.add(cervicalcancer);
		InStateCohortDefinition patientcervicalCancerType = Cohorts
				.createInCurrentState("patientcervicalCancerType",
						cervicalCancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithcervicalCancerType = new CompositionCohortDefinition();
		newPediWithcervicalCancerType.setName("newPediWithcervicalCancerType");
		newPediWithcervicalCancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithcervicalCancerType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithcervicalCancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithcervicalCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithcervicalCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientcervicalCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcervicalCancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithcervicalCancerType = new CompositionCohortDefinition();
		newAdultsWithcervicalCancerType
				.setName("newAdultsWithcervicalCancerType");
		newAdultsWithcervicalCancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithcervicalCancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithcervicalCancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithcervicalCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithcervicalCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientcervicalCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcervicalCancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithcervicalCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithcervicalCancerTypeIndicator",
						newPediWithcervicalCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithcervicalCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithcervicalCancerTypeIndicator",
						newAdultsWithcervicalCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Chronic Myelogenous leukemia
		List<ProgramWorkflowState> chronicmyelogousleukemiac = new ArrayList<ProgramWorkflowState>();
		chronicmyelogousleukemiac.add(chronicmyelogenousleukemia);
		InStateCohortDefinition patientWithchronicmyelogousleukemiacType = Cohorts
				.createInCurrentState(
						"patientWithchronicmyelogousleukemiacType",
						chronicmyelogousleukemiac, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithchronicmyelogousleukemiac = new CompositionCohortDefinition();
		newPediWithchronicmyelogousleukemiac
				.setName("newPediWithchronicmyelogousleukemiac");
		newPediWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediWithchronicmyelogousleukemiac
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithchronicmyelogousleukemiac
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithchronicmyelogousleukemiacType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithchronicmyelogousleukemiac.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithchronicmyelogousleukemiac = new CompositionCohortDefinition();
		newAdultsWithchronicmyelogousleukemiac
				.setName("newAdultsWithchronicmyelogousleukemiac");
		newAdultsWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithchronicmyelogousleukemiac.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsWithchronicmyelogousleukemiac
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithchronicmyelogousleukemiac
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithchronicmyelogousleukemiacType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithchronicmyelogousleukemiac.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithchronicmyelogousleukemiacIndicator = Indicators
				.newCountIndicator(
						"newPediWithchronicmyelogousleukemiacIndicator",
						newPediWithchronicmyelogousleukemiac,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithchronicmyelogousleukemiacIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithchronicmyelogousleukemiacIndicator",
						newAdultsWithchronicmyelogousleukemiac,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Colo-rectal cancer
		List<ProgramWorkflowState> colorectalcancerType = new ArrayList<ProgramWorkflowState>();
		colorectalcancerType.add(colorectalcancer);
		InStateCohortDefinition patientWithcolorectalcancerType = Cohorts
				.createInCurrentState("patientWithcolorectalcancerType",
						colorectalcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithcolorectalcancerType = new CompositionCohortDefinition();
		newPediWithcolorectalcancerType
				.setName("newPediWithcolorectalcancerType");
		newPediWithcolorectalcancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithcolorectalcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithcolorectalcancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithcolorectalcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithcolorectalcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithcolorectalcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithcolorectalcancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithcolorectalcancerType = new CompositionCohortDefinition();
		newAdultsWithcolorectalcancerType
				.setName("newAdultsWithcolorectalcancerType");
		newAdultsWithcolorectalcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithcolorectalcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithcolorectalcancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithcolorectalcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithcolorectalcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithcolorectalcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithcolorectalcancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithcolorectalcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithcolorectalcancerTypeIndicator",
						newPediWithcolorectalcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithcolorectalcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithcolorectalcancerTypeIndicator",
						newAdultsWithcolorectalcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Head and neck cancer
		List<ProgramWorkflowState> headandneckcancertype = new ArrayList<ProgramWorkflowState>();
		headandneckcancertype.add(headandneckcancer);
		InStateCohortDefinition patientwithheadandneckcancertype = Cohorts
				.createInCurrentState("patientwithheadandneckcancertype",
						headandneckcancertype, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithheadandneckcancertype = new CompositionCohortDefinition();
		newPediWithheadandneckcancertype
				.setName("newPediWithheadandneckcancertype");
		newPediWithheadandneckcancertype.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithheadandneckcancertype.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithheadandneckcancertype.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithheadandneckcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithheadandneckcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientwithheadandneckcancertype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithheadandneckcancertype.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithheadandneckcancertype = new CompositionCohortDefinition();
		newAdultsWithheadandneckcancertype
				.setName("newAdultsWithheadandneckcancertype");
		newAdultsWithheadandneckcancertype.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithheadandneckcancertype.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithheadandneckcancertype.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithheadandneckcancertype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithheadandneckcancertype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientwithheadandneckcancertype,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithheadandneckcancertype.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithheadandneckcancertypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithheadandneckcancertypeIndicator",
						newPediWithheadandneckcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithheadandneckcancertypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithheadandneckcancertypeIndicator",
						newAdultsWithheadandneckcancertype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Hodgkin's lymphoma
		List<ProgramWorkflowState> hodkinlymphomacancer = new ArrayList<ProgramWorkflowState>();
		hodkinlymphomacancer.add(hodgkinslymphoma);
		InStateCohortDefinition patientWithhodkinlymphomacancer = Cohorts
				.createInCurrentState("patientWithhodkinlymphomacancer",
						hodkinlymphomacancer, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithhodkinlymphomacancer = new CompositionCohortDefinition();
		newPediWithhodkinlymphomacancer
				.setName("newPediWithhodkinlymphomacancer");
		newPediWithhodkinlymphomacancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithhodkinlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithhodkinlymphomacancer.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithhodkinlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithhodkinlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithhodkinlymphomacancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithhodkinlymphomacancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithhodkinlymphomacancer = new CompositionCohortDefinition();
		newAdultsWithhodkinlymphomacancer
				.setName("newAdultsWithhodkinlymphomacancer");
		newAdultsWithhodkinlymphomacancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithhodkinlymphomacancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithhodkinlymphomacancer.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithhodkinlymphomacancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithhodkinlymphomacancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithhodkinlymphomacancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithhodkinlymphomacancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithhodkinlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithhodkinlymphomacancerIndicator",
						newPediWithhodkinlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithhodkinlymphomacancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithhodkinlymphomacancerIndicator",
						newAdultsWithhodkinlymphomacancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Karposi's sarcoma
		List<ProgramWorkflowState> karposysarcomaType = new ArrayList<ProgramWorkflowState>();
		karposysarcomaType.add(karposisarcoma);
		InStateCohortDefinition patientWithkarposusarcomaType = Cohorts
				.createInCurrentState("patientWithkarposusarcomaType",
						karposysarcomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithkarposysarcomaType = new CompositionCohortDefinition();
		newPediWithkarposysarcomaType.setName("newPediWithkarposysarcomaType");
		newPediWithkarposysarcomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithkarposysarcomaType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithkarposysarcomaType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithkarposysarcomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithkarposysarcomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithkarposusarcomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithkarposysarcomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithkarposysarcomaType = new CompositionCohortDefinition();
		newAdultsWithkarposysarcomaType
				.setName("newAdultsWithkarposysarcomaType");
		newAdultsWithkarposysarcomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithkarposysarcomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithkarposysarcomaType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithkarposysarcomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithkarposysarcomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithkarposusarcomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithkarposysarcomaType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithkarposysarcomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithkarposysarcomaTypeIndicator",
						newPediWithkarposysarcomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithkarposysarcomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithkarposysarcomaTypeIndicator",
						newAdultsWithkarposysarcomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Large B-cell lymphoma
		List<ProgramWorkflowState> largebcellLymphomaCancerType = new ArrayList<ProgramWorkflowState>();
		largebcellLymphomaCancerType.add(largebcelllymphoma);
		InStateCohortDefinition patientWithlargebcellLymphomaCancerType = Cohorts
				.createInCurrentState(
						"patientWithlargebcellLymphomaCancerType",
						largebcellLymphomaCancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithlargebcellLymphomaCancerType = new CompositionCohortDefinition();
		newPediWithlargebcellLymphomaCancerType
				.setName("newPediWithlargebcellLymphomaCancerType");
		newPediWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediWithlargebcellLymphomaCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithlargebcellLymphomaCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithlargebcellLymphomaCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithlargebcellLymphomaCancerType
				.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithlargebcellLymphomaCancerType = new CompositionCohortDefinition();
		newAdultsWithlargebcellLymphomaCancerType
				.setName("newAdultsWithlargebcellLymphomaCancerType");
		newAdultsWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithlargebcellLymphomaCancerType.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsWithlargebcellLymphomaCancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithlargebcellLymphomaCancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithlargebcellLymphomaCancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithlargebcellLymphomaCancerType
				.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithlargebcellLymphomaCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithlargebcellLymphomaCancerTypeIndicator",
						newPediWithlargebcellLymphomaCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithlargebcellLymphomaCancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithlargebcellLymphomaCancerTypeIndicator",
						newAdultsWithlargebcellLymphomaCancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Lung cancer diagnosis
		List<ProgramWorkflowState> lungdiagnosistype = new ArrayList<ProgramWorkflowState>();
		lungdiagnosistype.add(lungcancerdiagnosis);
		InStateCohortDefinition patientWithLungdiagnosiscancerType = Cohorts
				.createInCurrentState("patientWithLungdiagnosiscancerType",
						lungdiagnosistype, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithlungdiagnosistype = new CompositionCohortDefinition();
		newPediWithlungdiagnosistype.setName("newPediWithlungdiagnosistype");
		newPediWithlungdiagnosistype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithlungdiagnosistype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithlungdiagnosistype.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithlungdiagnosistype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithlungdiagnosistype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithLungdiagnosiscancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithlungdiagnosistype.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithlungdiagnosistype = new CompositionCohortDefinition();
		newAdultsWithlungdiagnosistype
				.setName("newAdultsWithlungdiagnosistype");
		newAdultsWithlungdiagnosistype.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithlungdiagnosistype.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newAdultsWithlungdiagnosistype.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithlungdiagnosistype
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithlungdiagnosistype
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithLungdiagnosiscancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithlungdiagnosistype.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithlungdiagnosistypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithlungdiagnosistypeIndicator",
						newPediWithlungdiagnosistype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithlungdiagnosistypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithlungdiagnosistypeIndicator",
						newAdultsWithlungdiagnosistype,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Metastatic cancer
		List<ProgramWorkflowState> metastaticcancerType = new ArrayList<ProgramWorkflowState>();
		metastaticcancerType.add(metastaticcancer);
		InStateCohortDefinition patientWithmetastaticcancerType = Cohorts
				.createInCurrentState("patientWithmetastaticcancerType",
						metastaticcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithWithmetastaticcancerType = new CompositionCohortDefinition();
		newPediWithWithmetastaticcancerType
				.setName("newPediWithWithmetastaticcancerType");
		newPediWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithWithmetastaticcancerType.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediWithWithmetastaticcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithWithmetastaticcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmetastaticcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithWithmetastaticcancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithWithmetastaticcancerType = new CompositionCohortDefinition();
		newAdultsWithWithmetastaticcancerType
				.setName("newAdultsWithWithmetastaticcancerType");
		newAdultsWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithWithmetastaticcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithWithmetastaticcancerType.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsWithWithmetastaticcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithWithmetastaticcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmetastaticcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithWithmetastaticcancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithWithmetastaticcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithWithmetastaticcancerTypeIndicator",
						newPediWithWithmetastaticcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithWithmetastaticcancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithWithmetastaticcancerTypeIndicator",
						newAdultsWithWithmetastaticcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Multiple myeloma
		List<ProgramWorkflowState> multiplemyelomaType = new ArrayList<ProgramWorkflowState>();
		multiplemyelomaType.add(multiplemyeloma);
		InStateCohortDefinition patientWithmultiplemyelomaType = Cohorts
				.createInCurrentState("patientWithmultiplemyelomaType",
						multiplemyelomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithmultiplemyelomaType = new CompositionCohortDefinition();
		newPediWithmultiplemyelomaType
				.setName("newPediWithmultiplemyelomaType");
		newPediWithmultiplemyelomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithmultiplemyelomaType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithmultiplemyelomaType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithmultiplemyelomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithmultiplemyelomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmultiplemyelomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithmultiplemyelomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithmultiplemyelomaType = new CompositionCohortDefinition();
		newAdultsWithmultiplemyelomaType
				.setName("newAdultsWithmultiplemyelomaType");
		newAdultsWithmultiplemyelomaType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithmultiplemyelomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithmultiplemyelomaType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithmultiplemyelomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithmultiplemyelomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithmultiplemyelomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithmultiplemyelomaType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithmultiplemyelomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithmultiplemyelomaTypeIndicator",
						newPediWithmultiplemyelomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithmultiplemyelomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithmultiplemyelomaTypeIndicator",
						newAdultsWithmultiplemyelomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Nephroblastoma
		List<ProgramWorkflowState> neurobblastomaType = new ArrayList<ProgramWorkflowState>();
		neurobblastomaType.add(neuphroblastoma);
		InStateCohortDefinition patientsWithneurobblastomaType = Cohorts
				.createInCurrentState("patientsWithneurobblastomaType",
						neurobblastomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithneurobblastomaType = new CompositionCohortDefinition();
		newPediWithneurobblastomaType.setName("newPediWithneurobblastomaType");
		newPediWithneurobblastomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithneurobblastomaType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithneurobblastomaType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithneurobblastomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithneurobblastomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithneurobblastomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithneurobblastomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithneurobblastomaType = new CompositionCohortDefinition();
		newAdultsWithneurobblastomaType
				.setName("newAdultsWithneurobblastomaType");
		newAdultsWithneurobblastomaType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithneurobblastomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithneurobblastomaType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithneurobblastomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithneurobblastomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithneurobblastomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithneurobblastomaType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithneurobblastomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithneurobblastomaTypeIndicator",
						newPediWithneurobblastomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithneurobblastomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithneurobblastomaTypeIndicator",
						newAdultsWithneurobblastomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Other liquid cancer
		List<ProgramWorkflowState> otherLiquidcancer = new ArrayList<ProgramWorkflowState>();
		otherLiquidcancer.add(otherliquidcancer);
		InStateCohortDefinition patientWithotherLiquidcancer = Cohorts
				.createInCurrentState("patientWithotherLiquidcancer",
						otherLiquidcancer, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithotherLiquidcancer = new CompositionCohortDefinition();
		newPediWithotherLiquidcancer.setName("newPediWithotherLiquidcancer");
		newPediWithotherLiquidcancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithotherLiquidcancer.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithotherLiquidcancer.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithotherLiquidcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithotherLiquidcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherLiquidcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithotherLiquidcancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithotherLiquidcancer = new CompositionCohortDefinition();
		newAdultsWithotherLiquidcancer
				.setName("newAdultsWithotherLiquidcancer");
		newAdultsWithotherLiquidcancer.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithotherLiquidcancer.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newAdultsWithotherLiquidcancer.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithotherLiquidcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithotherLiquidcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherLiquidcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithotherLiquidcancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithotherLiquidcancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithotherLiquidcancerIndicator",
						newPediWithotherLiquidcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithotherLiquidcancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithotherLiquidcancerIndicator",
						newAdultsWithotherLiquidcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Other non Hodgkin's lymphoma
		List<ProgramWorkflowState> othernonHodkindLymphomaType = new ArrayList<ProgramWorkflowState>();
		othernonHodkindLymphomaType.add(othernonhodkinslymphoma);
		InStateCohortDefinition patientWithothernonHodkindLymphomaType = Cohorts
				.createInCurrentState("patientWithothernonHodkindLymphomaType",
						othernonHodkindLymphomaType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithothernonHodkindLymphomaType = new CompositionCohortDefinition();
		newPediWithothernonHodkindLymphomaType
				.setName("newPediWithothernonHodkindLymphomaType");
		newPediWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediWithothernonHodkindLymphomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithothernonHodkindLymphomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithothernonHodkindLymphomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithothernonHodkindLymphomaType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithothernonHodkindLymphomaType = new CompositionCohortDefinition();
		newAdultsWithothernonHodkindLymphomaType
				.setName("newAdultsWithothernonHodkindLymphomaType");
		newAdultsWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithothernonHodkindLymphomaType.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsWithothernonHodkindLymphomaType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithothernonHodkindLymphomaType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithothernonHodkindLymphomaType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithothernonHodkindLymphomaType
				.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithothernonHodkindLymphomaTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithothernonHodkindLymphomaTypeIndicator",
						newPediWithothernonHodkindLymphomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithothernonHodkindLymphomaTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithothernonHodkindLymphomaTypeIndicator",
						newAdultsWithothernonHodkindLymphomaType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Other solid cancer
		List<ProgramWorkflowState> othersolidcancerType = new ArrayList<ProgramWorkflowState>();
		othersolidcancerType.add(othersolidcancer);
		InStateCohortDefinition patientWithotherSolidcancerType = Cohorts
				.createInCurrentState("patientWithotherSolidcancerType",
						othersolidcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithotherLiquidcancerType = new CompositionCohortDefinition();
		newPediWithotherLiquidcancerType
				.setName("newPediWithotherLiquidcancerType");
		newPediWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithotherLiquidcancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithotherLiquidcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithotherLiquidcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherSolidcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithotherLiquidcancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithotherLiquidcancerType = new CompositionCohortDefinition();
		newAdultsWithotherLiquidcancerType
				.setName("newAdultsWithotherLiquidcancerType");
		newAdultsWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithotherLiquidcancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithotherLiquidcancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithotherLiquidcancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithotherLiquidcancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithotherSolidcancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithotherLiquidcancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithotherLiquidcancerTypeeIndicator = Indicators
				.newCountIndicator(
						"newPediWithotherLiquidcancerTypeeIndicator",
						newPediWithotherLiquidcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithotherLiquidcancerTypeeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithotherLiquidcancerTypeeIndicator",
						newAdultsWithotherLiquidcancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));
		// Prostate cancer
		List<ProgramWorkflowState> prostatecancerType = new ArrayList<ProgramWorkflowState>();
		prostatecancerType.add(prostatecancer);
		InStateCohortDefinition patientsWithprostatecancerType = Cohorts
				.createInCurrentState("patientsWithprostatecancerType",
						prostatecancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithprostatecancerType = new CompositionCohortDefinition();
		newPediWithprostatecancerType.setName("newPediWithprostatecancerType");
		newPediWithprostatecancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newPediWithprostatecancerType.addParameter(new Parameter("onOrBefore",
				"onOrBefore", Date.class));
		newPediWithprostatecancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newPediWithprostatecancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithprostatecancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithprostatecancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithprostatecancerType.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithprostatecancerType = new CompositionCohortDefinition();
		newAdultsWithprostatecancerType
				.setName("newAdultsWithprostatecancerType");
		newAdultsWithprostatecancerType.addParameter(new Parameter("onOrAfter",
				"onOrAfter", Date.class));
		newAdultsWithprostatecancerType.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithprostatecancerType.addParameter(new Parameter("onDate",
				"onDate", Date.class));
		newAdultsWithprostatecancerType
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithprostatecancerType
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithprostatecancerType,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithprostatecancerType.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithprostatecancerTypeIndicator = Indicators
				.newCountIndicator(
						"newPediWithprostatecancerTypeIndicator",
						newPediWithprostatecancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithprostatecancerTypeIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithprostatecancerTypeIndicator",
						newAdultsWithprostatecancerType,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// stomach cancer
		List<ProgramWorkflowState> stomachcancerType = new ArrayList<ProgramWorkflowState>();
		stomachcancerType.add(stomachcancer);
		InStateCohortDefinition patientsWithstomachcancer = Cohorts
				.createInCurrentState("patientsWithstomachcancer",
						stomachcancerType, onOrAfterOnOrBefore);

		CompositionCohortDefinition newPediWithpatientsWithstomachcancer = new CompositionCohortDefinition();
		newPediWithpatientsWithstomachcancer
				.setName("newPediWithpatientsWithstomachcancer");
		newPediWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newPediWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newPediWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newPediWithpatientsWithstomachcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newPediSuspectedCanceratIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newPediWithpatientsWithstomachcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithstomachcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newPediWithpatientsWithstomachcancer.setCompositionString("1 AND 2 ");

		CompositionCohortDefinition newAdultsWithpatientsWithstomachcancer = new CompositionCohortDefinition();
		newAdultsWithpatientsWithstomachcancer
				.setName("newAdultsWithpatientsWithstomachcancere");
		newAdultsWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrAfter", "onOrAfter", Date.class));
		newAdultsWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onOrBefore", "onOrBefore", Date.class));
		newAdultsWithpatientsWithstomachcancer.addParameter(new Parameter(
				"onDate", "onDate", Date.class));
		newAdultsWithpatientsWithstomachcancer
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								newAdultSuspectedCancerAtIntakeCases,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		newAdultsWithpatientsWithstomachcancer
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientsWithstomachcancer,
								ParameterizableUtil
										.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		newAdultsWithpatientsWithstomachcancer.setCompositionString("1 AND 2 ");

		CohortIndicator newPediWithpatientsWithstomachcancerIndicator = Indicators
				.newCountIndicator(
						"newPediWithpatientsWithstomachcancerIndicator",
						newPediWithpatientsWithstomachcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator newAdultsWithpatientsWithstomachcancerIndicator = Indicators
				.newCountIndicator(
						"newAdultsWithpatientsWithstomachcancerIndicator",
						newAdultsWithpatientsWithstomachcancer,
						ParameterizableUtil
								.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// ====================================================//
		// A6 Total # of clinic visits in the last quarter */
		// ===================================================//
		EncounterCohortDefinition patientWithOncologyOutpatientEnc = Cohorts.createEncounterBasedOnForms("patientWithOncologyOutpatientEnc",onOrAfterOnOrBefore,intakeoutpatientclinicvisitflowform);
		CodedObsCohortDefinition patientswithunscheduledvisit =Cohorts.createCodedObsCohortDefinition("patientswithunscheduledvisite",onOrAfterOnOrBefore, visitType, null, SetComparator.IN,TimeModifier.LAST);
		CodedObsCohortDefinition patientswithunscheduledvisittype =Cohorts.createCodedObsCohortDefinition("patientswithunscheduledvisittype",onOrAfterOnOrBefore, visitType, unscheduledVisitType, SetComparator.IN,TimeModifier.LAST);
  
		CompositionCohortDefinition patientsWithclinicVisits = new CompositionCohortDefinition();
		patientsWithclinicVisits.setName("patientsWithclinicVisits");
		patientsWithclinicVisits.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		patientsWithclinicVisits.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		patientsWithclinicVisits.addParameter(new Parameter("onDate","onDate", Date.class));
		patientsWithclinicVisits.getSearches().put("1",new Mapped<CohortDefinition>(inOncologyProgram,
			ParameterizableUtil.createParameterMappings("onDate=${onDate}")));
		patientsWithclinicVisits.getSearches().put("2",new Mapped<CohortDefinition>(patientWithOncologyOutpatientEnc,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsWithclinicVisits.getSearches().put("3",new Mapped<CohortDefinition>(patientswithunscheduledvisit,
		    ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		patientsWithclinicVisits.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort, null));
		patientsWithclinicVisits.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition pedipatientwithUnscheduledVisit= new CompositionCohortDefinition();
		pedipatientwithUnscheduledVisit.setName("pedipatientwithUnscheduledVisit");
		pedipatientwithUnscheduledVisit.addParameter(new Parameter("onOrAfter","onOrAfter", Date.class));
		pedipatientwithUnscheduledVisit.addParameter(new Parameter("onOrBefore","onOrBefore", Date.class));
		pedipatientwithUnscheduledVisit.addParameter(new Parameter("onDate","onDate", Date.class));
		pedipatientwithUnscheduledVisit.getSearches().put("1",new Mapped<CohortDefinition>(patientsWithclinicVisits,
			ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},onDate=${onDate}")));
		pedipatientwithUnscheduledVisit.getSearches().put("2",new Mapped<CohortDefinition>(patientswithunscheduledvisittype,
		   ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
		pedipatientwithUnscheduledVisit.setCompositionString("1 AND 2");

		//CohortIndicator patientsWithclinicVisitsIndicator = Indicators.newCountIndicator("patientsWithclinicVisitsIndicator",
				//patientsWithclinicVisits,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		CohortIndicator pedipatientwithUnscheduledVisitIndicator = Indicators.newCountIndicator("pedipatientwithUnscheduledVisitIndicator",
				pedipatientwithUnscheduledVisit,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate},onDate=${endDate}"));

		// =======================================================================================//
		// A14 Total # of patients exited for palliation-only care within the
		// reporting period */
		// =====================================================================================//
		SqlCohortDefinition patientWithOncologyOutcomepalliationOnly = Cohorts
				.getPatientsWithOutcomeprogramEndReasons(
						"patientWithOncologyOutcomepalliationOnly",
						oncologyprogramendreason, palliationonlycare);
		EncounterCohortDefinition patientWithDemoandOrExitform = Cohorts
				.createEncounterBasedOnForms("patientWithDemoandIntakeForms",
						startDateEndDate, exitandDemoforms);

		CompositionCohortDefinition pediExitedforPalliationOnlyCare = new CompositionCohortDefinition();
		pediExitedforPalliationOnlyCare
				.setName("pediExitedforPalliationOnlyCare");
		pediExitedforPalliationOnlyCare.addParameter(new Parameter("startDate",
				"startDate", Date.class));
		pediExitedforPalliationOnlyCare.addParameter(new Parameter("endDate",
				"endDate", Date.class));
		pediExitedforPalliationOnlyCare.getSearches().put("1",
				new Mapped<CohortDefinition>(inOncologyProgram, null));
		pediExitedforPalliationOnlyCare
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithOncologyOutcomepalliationOnly,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		pediExitedforPalliationOnlyCare
				.getSearches()
				.put("3",
						new Mapped<CohortDefinition>(
								patientWithDemoandOrExitform,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		pediExitedforPalliationOnlyCare.getSearches().put("4",
				new Mapped<CohortDefinition>(under15Cohort, null));
		pediExitedforPalliationOnlyCare
				.setCompositionString("1 AND 2 AND 3 AND 4");

		CompositionCohortDefinition adultsExitedforPalliationOnlyCare = new CompositionCohortDefinition();
		adultsExitedforPalliationOnlyCare
				.setName("adultsExitedforPalliationOnlyCare");
		adultsExitedforPalliationOnlyCare.addParameter(new Parameter(
				"startDate", "startDate", Date.class));
		adultsExitedforPalliationOnlyCare.addParameter(new Parameter("endDate",
				"endDate", Date.class));
		adultsExitedforPalliationOnlyCare.getSearches().put("1",
				new Mapped<CohortDefinition>(inOncologyProgram, null));
		adultsExitedforPalliationOnlyCare
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithOncologyOutcomepalliationOnly,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsExitedforPalliationOnlyCare
				.getSearches()
				.put("3",
						new Mapped<CohortDefinition>(
								patientWithDemoandOrExitform,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsExitedforPalliationOnlyCare.getSearches().put("4",
				new Mapped<CohortDefinition>(over15Cohort, null));
		adultsExitedforPalliationOnlyCare
				.setCompositionString("1 AND 2 AND 3 AND 4");

		CohortIndicator pediExitedforPalliationOnlyCareIndicator = Indicators
				.newCountIndicator(
						"pediExitedforPalliationOnlyCareIndicator",
						pediExitedforPalliationOnlyCare,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		CohortIndicator adultsExitedforPalliationOnlyCareIndicator = Indicators
				.newCountIndicator(
						"adultsExitedforPalliationOnlyCareIndicator",
						adultsExitedforPalliationOnlyCare,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		// ======================================================//
		// A15 Total # of deaths within the reporting period * /
		// =====================================================//

		SqlCohortDefinition patientWithOncologyOutcomeCancerdeath = Cohorts
				.getPatientsWithOutcomeprogramEndReasons(
						"patientWithOncologyOutcomeCancerdeath",
						oncologyprogramendreason, cancerrelateddeath);
		SqlCohortDefinition patientWithOncologyOutcomenoncancerdeath = Cohorts
				.getPatientsWithOutcomeprogramEndReasons(
						"patientWithOncologyOutcomenoncancerdeath",
						oncologyprogramendreason, noncancerrelateddeath);
		SqlCohortDefinition patientWithOncologyOutcomedeathunknownreason = Cohorts
				.getPatientsWithOutcomeprogramEndReasons(
						"patientWithOncologyOutcomedeathunknownreason",
						oncologyprogramendreason, deathreasonunknown);

		CompositionCohortDefinition oncologypatientwithdearreasons = new CompositionCohortDefinition();
		oncologypatientwithdearreasons
				.setName("oncologypatientwithdearreasons");
		oncologypatientwithdearreasons.addParameter(new Parameter("startDate",
				"startDate", Date.class));
		oncologypatientwithdearreasons.addParameter(new Parameter("endDate",
				"endDate", Date.class));
		oncologypatientwithdearreasons
				.getSearches()
				.put("1",
						new Mapped<CohortDefinition>(
								patientWithOncologyOutcomeCancerdeath,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		oncologypatientwithdearreasons
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								patientWithOncologyOutcomenoncancerdeath,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		oncologypatientwithdearreasons
				.getSearches()
				.put("3",
						new Mapped<CohortDefinition>(
								patientWithOncologyOutcomedeathunknownreason,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		oncologypatientwithdearreasons
				.getSearches()
				.put("4",
						new Mapped<CohortDefinition>(
								patientWithDemoandOrExitform,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		oncologypatientwithdearreasons
				.setCompositionString("(1 OR 2 OR 3) AND 4");

		CompositionCohortDefinition pediexitedfromcarewithDeathreasons = new CompositionCohortDefinition();
		pediexitedfromcarewithDeathreasons
				.setName("pediexitedfromcarewithDeathreasons");
		pediexitedfromcarewithDeathreasons.addParameter(new Parameter(
				"startDate", "startDate", Date.class));
		pediexitedfromcarewithDeathreasons.addParameter(new Parameter(
				"endDate", "endDate", Date.class));
		pediexitedfromcarewithDeathreasons.getSearches().put("1",
				new Mapped<CohortDefinition>(inOncologyProgram, null));
		pediexitedfromcarewithDeathreasons
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								oncologypatientwithdearreasons,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		pediexitedfromcarewithDeathreasons.getSearches().put("3",
				new Mapped<CohortDefinition>(under15Cohort, null));
		pediexitedfromcarewithDeathreasons
				.setCompositionString("1 AND 2 AND 3");

		CompositionCohortDefinition adultsexitedfromcarewithDeathreasons = new CompositionCohortDefinition();
		adultsexitedfromcarewithDeathreasons
				.setName("adultsexitedfromcarewithDeathreasons");
		adultsexitedfromcarewithDeathreasons.addParameter(new Parameter(
				"startDate", "startDate", Date.class));
		adultsexitedfromcarewithDeathreasons.addParameter(new Parameter(
				"endDate", "endDate", Date.class));
		adultsexitedfromcarewithDeathreasons.getSearches().put("1",
				new Mapped<CohortDefinition>(inOncologyProgram, null));
		adultsexitedfromcarewithDeathreasons
				.getSearches()
				.put("2",
						new Mapped<CohortDefinition>(
								oncologypatientwithdearreasons,
								ParameterizableUtil
										.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		adultsexitedfromcarewithDeathreasons.getSearches().put("3",
				new Mapped<CohortDefinition>(over15Cohort, null));
		adultsexitedfromcarewithDeathreasons
				.setCompositionString("1 AND 2 AND 3");

		CohortIndicator pediexitedfromcarewithDeathreasonsIndicator = Indicators
				.newCountIndicator(
						"pediexitedfromcarewithDeathreasonsIndicator",
						pediexitedfromcarewithDeathreasons,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		CohortIndicator adultsexitedfromcarewithDeathreasonsIndicator = Indicators
				.newCountIndicator(
						"adultsexitedfromcarewithDeathreasonsIndicator",
						adultsexitedfromcarewithDeathreasons,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		// =================================================
		// Adding columns to data set definition //
		// =================================================

		dsd.addColumn(
				"D1Q1A",
				"% of peds demographics of new cases in the last quarter",
				new Mapped(
						under15DemoIntakeFormsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D1Q1C",
				"% of adults demographics of new cases in the last quarter",
				new Mapped(
						over15DemoIntakeFormsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D1Q2A",
				"% of females peds demographics of new cases in the last quarter",
				new Mapped(
						newCasesFemalesUnder15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D1Q2C",
				"% of females adults demographics of new cases in the last quarter",
				new Mapped(
						newCasesFemalesOver15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D2Q1A",
				"% of males peds demographics of new cases in the last quarter",
				new Mapped(
						newCasesmalesUnder15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D2Q1C",
				"% of males adults demographics of new cases in the last quarter",
				new Mapped(
						newCasesmalesOver15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D5Q1A",
				"% of new cases peds recommended for socioeconomic assistance at intake",
				new Mapped(
						newCasesocioeconomicrecUnder15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D5Q1C",
				"% of new cases adults recommended for socioeconomic assistance at intake",
				new Mapped(
						newCasessocioeconomicrecOver15Indicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn("D6Q1A","% pedi breakdown of new cases with Health Center at intake",new Mapped(pedPatientsreferredForHCIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q1C","% adults breakdown of new cases with Health Center as referral facility type  at intake",new Mapped (adultsPatientsreferredForHCIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
        dsd.addColumn("D6Q2A","% pedi breakdown of new cases with District Hospital at intake",new Mapped(pedsreferredForDistrictHospitalIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q2C","% adults breakdown of new cases with District Hospital at intake",new Mapped(adultsreferredForDistrictHospitalIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q3A","% pedi breakdown of new referral cases at intake",new Mapped(pedsreferredforReferralHospitalIndicator,
								ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q3C","% adults breakdown of new referral cases at intake",new Mapped(adultsreferredforReferralHospitalIndicator,
								ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q4A","% pedi breakdown of new cases with other referral types at intake",new Mapped(pedsWithOtherReferralIndicator,
										ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q4C","% adults breakdown of new cases with other referral types at intake",new Mapped(adultsWithOtherReferralIndicator,
										ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q5A","% pedi breakdown of new cases not referred at intake",new Mapped(pedwithNoreferralTypeIndicator,
												ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q5C","% adults breakdown of new cases not referred at intake",new Mapped(adultswithreferralTypeIndicator,
												ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q6A","% pedi breakdown of new cases with missing references at intake",new Mapped(pediWithMissingReferencesIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D6Q6C","% adults breakdown of new cases with missig references at intake",new Mapped(adultsWithMissingReferencesIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D7Q1A","% of new referral cases inside Intake district",new Mapped(referredinsideRwandanDistrictIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q2A","% of new referral cases outside Intake district",new Mapped(referredoutsideIntakedistrictInRwandaIndi,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		dsd.addColumn("D7Q3A","% of new referral cases referred from outside of Rwanda ",new Mapped(referredOutsideOfRwandaIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D7Q1C","% of total new referral cases referred",new Mapped(pedinewCasesThatareReferredIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		
		dsd.addColumn(
				"D8Q1A",
				"% breakdown of Kayonza new cases home district at intake",
				new Mapped(
						patientsLocatedInKayonzaDistrictIndi,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D8Q2A",
				"% breakdown of Burera new cases home district at intake",
				new Mapped(
						patientsLocatedInBureraDistrictInd,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D8Q3A",
				"% breakdown of Kirehe new cases home district at intake",
				new Mapped(
						patientsLocatedInKireheDistrictIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D8Q4A",
				"% breakdown of Kigali new cases home district at intake",
				new Mapped(
						patientsLocatedInKigaliDistrictIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D8Q5A",
				"% breakdown of other rwandan districts new cases home district at intake",
				new Mapped(
						patientsLocatedInOtherRwandanDistrictsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		
		dsd.addColumn("D8Q6A","% breakdown of new cases' owith missing address at intake",new Mapped(patientsLocatedWithMissingIndicator,
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		dsd.addColumn("D8Q7A","% breakdown of new cases' outside of Rwandan home district at intake",new Mapped(patientsfromOutsideOfRwandaIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		
		
		dsd.addColumn(
				"D9Q1A",
				"Of the new cases with HIV status documented at intake, pediatric ",
				new Mapped(
						newCasespediWithHiVstatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D9Q1C",
				"Of the new cases with HIV status documented at intake, adults ",
				new Mapped(
						newCasesadultsWithHiVstatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D9Q2A",
				"% Of the new cases with HIV status documented at intake, % of pedi with a positive HIV status",
				new Mapped(
						newCasespediWithHiVPositivestatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"D9Q2C",
				"% Of the new cases with HIV status documented at intake, % of adults with a positive HIV status",
				new Mapped(
						newCasesadultsWithHiVPositivestatusIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A1Q1A",
				"Total # of pediatric patients ever enrolled in program at the end of the review quarter",
				new Mapped(pedsWitClinicalIntakeintheReviewQuarterIndicator,
						ParameterizableUtil
								.createParameterMappings("endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A1Q1C",
				"Total # of adults patients ever enrolled in program at the end of the review quarter",
				new Mapped(adultsWitClinicalIntakeintheReviewQuarterIndicator,
						ParameterizableUtil
								.createParameterMappings("endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A2Q1A",
				"Total # of pediatric patients ever enrolled in program at the end of the review quarter",
				new Mapped(
						newPediNotSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A2Q1C",
				"Total # of adults patients ever enrolled in program at the end of the review quarter",
				new Mapped(
						newAdultsNotSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3QA",
				"Total # of new suspected  pedi cancer cases at intake in the last quarter",
				new Mapped(
						newPediSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3QC",
				"Total # of new suspected adults cancer cases at intake in the last quarter",
				new Mapped(
						newAdultSuspectedCancerAtIntakeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q1A",
				"Total # of new pedi suspected of Acute lymphoblastic leukemia cancer at intake in the last quarter",
				new Mapped(
						newPediWithAcutelymphoblasticleukemiaIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q1C",
				"Total # of new adults suspected of Acute lymphoblastic leukemia cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithAcutelymphoblasticleukemiaIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q2A",
				"Total # of new pedi suspected  of Breast Cancer at intake in the last quarter",
				new Mapped(
						newPediWithbreastcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q2C",
				"Total # of new adults suspected  of Breast Cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithbreastcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q3A",
				"Total # of new pedi suspected  Burkitt's lymphoma at intake in the last quarter",
				new Mapped(
						newPediWithburkitlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q3C",
				"Total # of new adults suspected  Burkitt's lymphoma at intake in the last quarter",
				new Mapped(
						newAdultsWithburkitlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q4A",
				"Total # of new suspected pedi cancer unknown type at intake in the last quarter",
				new Mapped(
						newPediWithcancerunknowntypeCIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q4C",
				"Total # of new suspected adults of cancer unknown type at intake in the last quarter",
				new Mapped(
						newAdultsWithcancerunknowntypeCIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q5A",
				"Total # of new pedi suspected Cervical cancer at intake in the last quarter",
				new Mapped(
						newPediWithcervicalCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q5C",
				"Total # of new adults suspected Cervical cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithcervicalCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q6A",
				"Total # of new adults suspected chronic Myelogenous leukemia at intake in the last quarter",
				new Mapped(
						newPediWithchronicmyelogousleukemiacIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q6C",
				"Total # of new adults suspected chronic Myelogenous leukemia at intake in the last quarter",
				new Mapped(
						newAdultsWithchronicmyelogousleukemiacIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q7A",
				"Total # of new suspected  pedi with Colo-rectal cancer at intake in the last quarter",
				new Mapped(
						newPediWithcolorectalcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q7C",
				"Total # of new suspected adults with Colo-rectal cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithcolorectalcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q8A",
				"Total # of new suspected  pedi with Head and neck cancer at intake in the last quarter",
				new Mapped(
						newPediWithheadandneckcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q8C",
				"Total # of new suspected adults with Head and neck cancer at intake in the last quarter",
				new Mapped(
						newAdultsWithheadandneckcancertypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q9A",
				"Total # of new pedi with Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithhodkinlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q9C",
				"Total # of new adults with Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithhodkinlymphomacancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q10A",
				"Total # of new pedi with Karposi's sarcoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithkarposysarcomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q10C",
				"Total # of new adults with Karposi's sarcoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithkarposysarcomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");

		dsd.addColumn(
				"A3Q11A",
				"Total # of new pedi with Large B-cell lymphoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithlargebcellLymphomaCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q11C",
				"Total # of new adults with Large B-cell lymphoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithlargebcellLymphomaCancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q12A",
				"Total # of new pedi with Lung cancer diagnosis suspected at intake in the last quarter",
				new Mapped(
						newPediWithlungdiagnosistypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q12C",
				"Total # of new adults with Lung cancer diagnosis suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithlungdiagnosistypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q13A",
				"Total # of new pedi with Metastatic cancer suspected at intake in the last quarter",
				new Mapped(
						newPediWithWithmetastaticcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q13C",
				"Total # of new adults with Metastatic cancer suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithWithmetastaticcancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q14A",
				"Total # of new pedi with Multiple myeloma suspected at intake in the last quarter",
				new Mapped(
						newPediWithmultiplemyelomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q14C",
				"Total # of new adults with Multiple myeloma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithmultiplemyelomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q15A",
				"Total # of new pedi with Nephroblastoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithneurobblastomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q15C",
				"Total # of new adults with Nephroblastoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithneurobblastomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q16A",
				"Total # of new pedi with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newPediWithotherLiquidcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q16C",
				"Total # of new adults with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithotherLiquidcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q17A",
				"Total # of new pedi with Other non Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newPediWithothernonHodkindLymphomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q17C",
				"Total # of new adults with Other non Hodgkin's lymphoma suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithothernonHodkindLymphomaTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q18A",
				"Total # of new adults with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newPediWithotherLiquidcancerTypeeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q18C",
				"Total # of new adults with Other liquid cancer suspected at intake in the last quarter",
				new Mapped(
						newAdultsWithotherLiquidcancerTypeeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q19A",
				"Total # of new pedi with prostate cancer suspected in last quarter",
				new Mapped(
						newPediWithprostatecancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q19C",
				"Total # of new adults with prostate cancer suspected in the last quarter",
				new Mapped(
						newAdultsWithprostatecancerTypeIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q20A",
				"Total # of new pedi with Stomach cancer suspected in the last quarter",
				new Mapped(
						newPediWithpatientsWithstomachcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A3Q20C",
				"Total # of new adults with Stomach cancer suspected in the last quarter",
				new Mapped(
						newAdultsWithpatientsWithstomachcancerIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn("A6Q1A","Total # of pedi clinic visits in the last quarter",new Mapped(pedipatientwithUnscheduledVisitIndicator,
						ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
		/*dsd.addColumn(
				"A6Q1C",
				"Total # of adults clinic visits in the last quarter",
				new Mapped(
						adultspatientwithclinicalVisitIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");*/
		dsd.addColumn(
				"A14Q1A",
				"Total # of pedi patients exited for palliation-only care within the reporting period",
				new Mapped(
						pediExitedforPalliationOnlyCareIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A14Q1C",
				"Total # of adults patients exited for palliation-only care within the reporting period",
				new Mapped(
						adultsExitedforPalliationOnlyCareIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A15Q1A",
				"Total # of pedi deaths within the reporting period",
				new Mapped(
						pediexitedfromcarewithDeathreasonsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");
		dsd.addColumn(
				"A15Q1C",
				"Total # of adults deaths within the reporting period",
				new Mapped(
						adultsexitedfromcarewithDeathreasonsIndicator,
						ParameterizableUtil
								.createParameterMappings("startDate=${startDate},endDate=${endDate}")),
				"");

	}

	private void setUpProperties() {
		oncologyProgram = gp
				.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		oncologyPrograms.add(oncologyProgram);
		notCancerdiagnosisstate = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.NOT_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		suspecteddiagnosisstate = gp
				.getProgramWorkflowState(
						GlobalPropertiesManagement.SUSPECTED_STATE,
						GlobalPropertiesManagement.ONCOLOGY_DIAGNOSIS_STATUS_PROGRAM_WORKFLOW,
						GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		acutelymphoblasticcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.ACUTE_LYMPHOBLASTIC_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		breastCancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.BREAST_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		burkittLymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.BURKITTLYMPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		cancerunkowntype = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.CANCERUNKNOWNTYPE_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		cervicalcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.CERVICAL_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		chronicmyelogenousleukemia = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.CHRONICMYELOGENOUSLEUKEMIA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		colorectalcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.COLORECTAL_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		headandneckcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.HEADANDNECK_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		hodgkinslymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.HODKINLYPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		karposisarcoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.KARPOSISARCOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		largebcelllymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.LARGEBCELLLYMPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		lungcancerdiagnosis = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.LUNGCANCERDIAGNOSIS_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		metastaticcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.METASTATIC_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		multiplemyeloma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.MULTIPLEMYELOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		neuphroblastoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.NEUPHROBLASTOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		otherliquidcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.OTHERLIQUID_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		othernonhodkinslymphoma = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.OTHERNONHODKINLYMPHOMA_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		othersolidcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.OTHERSOLID_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		prostatecancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.PROSTATE_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
		stomachcancer = gp.getProgramWorkflowState(
				GlobalPropertiesManagement.STOMACH_CANCER_STATE,
				GlobalPropertiesManagement.DIAGNOSIS_WORKFLOW,
				GlobalPropertiesManagement.ONCOLOGY_PROGRAM);

		demographicform = gp
				.getForm(GlobalPropertiesManagement.ONCOLOGY_DEMO_FORM);
		inpatientOncForm = gp
				.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_INPATIENT_FORM);
		outpatientOncForm = gp
				.getForm(GlobalPropertiesManagement.ONCOLOGY_INTAKE_OUTPATIENT_FORM);
		outpatientclinicvisitform = gp
				.getForm(GlobalPropertiesManagement.OUTPATIENT_CLINIC_VISITS_FORM);
		exitform = gp.getForm(GlobalPropertiesManagement.ONCOLOGY_EXIT_FORM);
		demographicsAndClinicalforms.add(demographicform);
		demographicsAndClinicalforms.add(inpatientOncForm);
		demographicsAndClinicalforms.add(outpatientOncForm);
		intakeoutpatientclinicvisitflowform.add(outpatientclinicvisitform);
		clinicalIntakeForms.add(inpatientOncForm);
		clinicalIntakeForms.add(outpatientOncForm);
		exitandDemoforms.add(demographicform);
		exitandDemoforms.add(exitform);
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		startDateEndDate.add("startDate");
		startDateEndDate.add("endDate");
		socioassistance = gp
				.getConcept(GlobalPropertiesManagement.SOCIO_ECONOMIC_ASSISTANCE_RECOMENDED);
		foodassistance = gp
				.getConcept(GlobalPropertiesManagement.NUTRITIONAL_AID);
		schoolassistance = gp
				.getConcept(GlobalPropertiesManagement.SCHOOL_ASSISTANCE);
		transportassistance = gp
				.getConcept(GlobalPropertiesManagement.TRANSPORT_ASSISTANCE);
		homeassistance = gp
				.getConcept(GlobalPropertiesManagement.HOME_ASSISTANCE);
		referralType = gp
				.getConcept(GlobalPropertiesManagement.TYPE_OF_REFERRING_CLINIC_OR_HOSPITAL);
		healthclinic = gp.getConcept(GlobalPropertiesManagement.HEALTH_CLINIC);
		districhospital = gp
				.getConcept(GlobalPropertiesManagement.DISTRICT_HOSPITAL);
		referralhospital = gp
				.getConcept(GlobalPropertiesManagement.REFERRAL_HOSPITAL);
		visitType=gp.getConcept(GlobalPropertiesManagement.VISIT_TYPE);
		unscheduledVisitType=gp.getConcept(GlobalPropertiesManagement.UNSCHEDULED_VISIT_TYPE);
		othernonCoded = gp
				.getConcept(GlobalPropertiesManagement.OTHER_NON_CODED);
		notReferred = gp.getConcept(GlobalPropertiesManagement.NOT_REFERRED);
		hivStatus = gp.getConcept(GlobalPropertiesManagement.HIV_STATUS);
		positiveStatus = gp
				.getConcept(GlobalPropertiesManagement.POSITIVE_HIV_TEST_ANSWER);
		locreferralType = gp
				.getConcept(GlobalPropertiesManagement.LOCATION_REFFERAL_TYPE);
		outsideofRwanda = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_OUTSIDE_OF_RWANDA);
		insideintakedistrict = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_AT_INTAKE_DISTRICT);
		outsideintakedistrict = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_OUTSIDE_INTAKE_DISTRICT);
		oncologyprogramendreason = gp
				.getConcept(GlobalPropertiesManagement.ONCOLOGY_PROGRAM_END_REASON);
		palliationonlycare = gp
				.getConcept(GlobalPropertiesManagement.REFERRED_FOR_PALLIATIONONLY_CARE);
		cancerrelateddeath = gp
				.getConcept(GlobalPropertiesManagement.CANCER_RELATED_DEATH);
		noncancerrelateddeath = gp
				.getConcept(GlobalPropertiesManagement.NON_CANCER_RELATED_DEATH);
		deathreasonunknown = gp
				.getConcept(GlobalPropertiesManagement.DEATH_UNKNOWN_REASON);

	}
}