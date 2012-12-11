package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.datasource.PersonDataSource;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.token.TokenService;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.aggregation.MedianAggregator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.definition.DrugsActiveCohortDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

public class SetupTracNetRwandaReportBySite {
	
	protected final static Log log = LogFactory.getLog(SetupTracNetRwandaReportBySite.class);
	
	Helper h = new Helper();
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	// properties
		private Program adulthivProgram;
	    private Program pediatrichivProgram;
		private Program pmtctcombinedMother;
		private ProgramWorkflowState adultOnFollowing;
		private ProgramWorkflowState pediOnFollowing;
		private ProgramWorkflowState adultOnART;
		private ProgramWorkflowState pediOnART;
		private ProgramWorkflowState adulttransferedOutState;
		private ProgramWorkflowState peditransferedOutState;
	    private Concept tbScreeningtest;
		private Concept positiveStatus;
		private Concept reasonForExitingCare;
		private Concept patientDied;
		private Concept patientTransferedOut;
		private Form adultHivForm;
		private Form pediHivform;
		private Form transferinForm;
		private List<Form> hivVisitsforms = new ArrayList<Form>();
		private Form allergypediForm;
		private Form allergyadultForm;
		private List<Form> medicationForms = new ArrayList<Form>();
		private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
		private List<EncounterType> clinicalEnountersIncLab;
		private EncounterType patientTransferEncounterType;
		private Concept rifampicin;
		private Concept ethambutol;
		private Concept isoniazid;
		private Concept adverse_med;
		private Concept kaletra;
		private Concept cotrimoxazole;
		public Concept whostage;
		public Concept whostage4p;
		public Concept whostage3p;
		public Concept whostage2p;
		public Concept whostage1p;
		public Concept whostageinconue;
		public Concept whostage4adlt;
		public Concept whostage3adlt;
		public Concept whostage2adlt;
		public Concept whostage1adlt;
		private List<String> onOrAfterOnOrBeforeParamterNames = new ArrayList<String>();
	
	public void setup() throws Exception {
		
		setupProperties();
		
		ReportDefinition rd = createReportDefinition();
		
		ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "Mohrwandatracnetreporttemplate.xls",
		    "Xlstracnetreporttemplate", null);
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,dataset:TracNet Report Location");
		design.setProperties(props);
		h.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Xlstracnetreporttemplate".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		h.purgeReportDefinition("TracNet Report");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		Properties properties = new Properties();
		//properties.setProperty("hierarchyFields", "countyDistrict:District");
		rd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		rd.setName("TracNet Report");
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createDataSetDefinition());
		ldsd.setName("TracNet Report Location");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		rd.addDataSetDefinition(ldsd,
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		h.saveReportDefinition(rd);
		
		return rd;
		
	}
	
	private DataSetDefinition createDataSetDefinition() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("TracNet Report Indicators");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		//Gender Cohort definitions
		GenderCohortDefinition femaleCohort = Cohorts.createFemaleCohortDefinition("femalesDefinition");
		GenderCohortDefinition maleCohort = Cohorts.createMaleCohortDefinition("malesDefinition");	
		// Ages Cohort Definitions
		AgeCohortDefinition under15Cohort = Cohorts.createUnder15AgeCohort("TR:under15Cohort");
		AgeCohortDefinition over15Cohort = Cohorts.createOver15AgeCohort("TR:over15Cohort");
		AgeCohortDefinition at18monthsOfAge = Cohorts.createUnder18monthsCohort("TR:at18monthsOfAge");
		AgeCohortDefinition under5Cohort = Cohorts.createUnder5AgeCohort("TR:under5Cohort");
		
		//Program Cohorts
		List<Program> PmtctCombinrMotherProgram = new ArrayList<Program>();
		PmtctCombinrMotherProgram.add(pmtctcombinedMother);
		InProgramCohortDefinition inPmtctMotherprogram = Cohorts.createInProgramParameterizableByDate("TR:PMTCTmotherClinic",PmtctCombinrMotherProgram, "onDate");
		
		List<Program> hivPrograms = new ArrayList<Program>();
		hivPrograms.add(adulthivProgram);
		hivPrograms.add(pediatrichivProgram);
		InProgramCohortDefinition inPediAndAdultprogram = Cohorts.createInProgramParameterizableByDate("TR:inPediAndAdult",hivPrograms, "onDate");
		SqlCohortDefinition onARTDrugs = Cohorts.getArtDrugs("TR:On Art Drugs ever");
		
		ProgramEnrollmentCohortDefinition patientEnrolledInPediAndAdultProgram = new ProgramEnrollmentCohortDefinition();
		patientEnrolledInPediAndAdultProgram.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
		patientEnrolledInPediAndAdultProgram.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInPediAndAdultProgram.setPrograms(hivPrograms);
		
		List<Program> pmtctProgram = new ArrayList<Program>();
		pmtctProgram.add(pmtctcombinedMother);
		ProgramEnrollmentCohortDefinition patientEnrolledInPMTCTProgram = new ProgramEnrollmentCohortDefinition();
		patientEnrolledInPMTCTProgram.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
		patientEnrolledInPMTCTProgram.setPrograms(pmtctProgram);
		
		// Total number of female pediatric patients (age <15 years) ever enrolled in HIV care  
		CompositionCohortDefinition onARTStateInPMTCTClinic = new CompositionCohortDefinition();
		onARTStateInPMTCTClinic.setName("TR:onARTStateInPMTCTClinic");
		onARTStateInPMTCTClinic.getSearches().put("1",new Mapped<CohortDefinition>(onARTDrugs, null));
		onARTStateInPMTCTClinic.getSearches().put("2",new Mapped<CohortDefinition>(inPmtctMotherprogram, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		onARTStateInPMTCTClinic.setCompositionString("1 AND 2");
	
		List<ProgramWorkflowState> OnFollowingstates = new ArrayList<ProgramWorkflowState>();
		OnFollowingstates.add(adultOnFollowing);
		OnFollowingstates.add(pediOnFollowing);
		InStateCohortDefinition onFollowingStateCohort = Cohorts.createInCurrentState("TR:onFollowingStateCohort", OnFollowingstates,"onDate");
		
		CompositionCohortDefinition onFollowingStateHIVClinic = new CompositionCohortDefinition();
		onFollowingStateHIVClinic.setName("TR:onFollowingStateHIVClinic");
		onFollowingStateHIVClinic.getSearches().put("1",new Mapped<CohortDefinition>(onFollowingStateCohort,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		onFollowingStateHIVClinic.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		onFollowingStateHIVClinic.setCompositionString("1 AND 2");
		
		CodedObsCohortDefinition exitedCareWithDeadStatus = Cohorts.createCodedObsCohortDefinition("exitedCareWithDeadStatus",onOrAfterOnOrBefore, reasonForExitingCare,patientDied, SetComparator.IN, TimeModifier.LAST);
		CodedObsCohortDefinition exitedCareWithtransferStatus = Cohorts.createCodedObsCohortDefinition("exitedCareWithDeadStatus",onOrAfterOnOrBefore, reasonForExitingCare,patientTransferedOut, SetComparator.IN, TimeModifier.LAST);
		 
		SqlCohortDefinition patientsWithhivTransferVisit = new SqlCohortDefinition();
		patientsWithhivTransferVisit.setQuery("select distinct patient_id from encounter where encounter_type="+patientTransferEncounterType.getId()+ " and (form_id="+transferinForm.getFormId()+ ") and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
		patientsWithhivTransferVisit.setName("patientsWithhivTransferVisit");
		patientsWithhivTransferVisit.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientsWithhivTransferVisit.addParameter(new Parameter("endDate", "endDate", Date.class));
		
		CompositionCohortDefinition everEnrolledInHIVCare = new CompositionCohortDefinition();
		everEnrolledInHIVCare.setName("TR: everEnrolledInHIVCare");
		everEnrolledInHIVCare.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateInPMTCTClinic, null));
		everEnrolledInHIVCare.getSearches().put("2",new Mapped<CohortDefinition>(onFollowingStateHIVClinic, null));
		everEnrolledInHIVCare.setCompositionString("1 OR 2");
		
		//Art States
		 List<ProgramWorkflowState> onARTstates = new ArrayList<ProgramWorkflowState>();
		 onARTstates.add(adultOnART);
		 onARTstates.add(pediOnART);
		 InStateCohortDefinition onARTstatesStateCohort = Cohorts.createInCurrentState("onARTstatesStateCohort", onARTstates,"onDate");
		 CompositionCohortDefinition onARTStateHIVClinic = new CompositionCohortDefinition();
		 onARTStateHIVClinic.setName("onARTStateHIVClinic");
		 onARTStateHIVClinic.getSearches().put("1",new Mapped<CohortDefinition>(onARTstatesStateCohort,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		 onARTStateHIVClinic.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		 onARTStateHIVClinic.setCompositionString("1 AND 2");
		 
		 //------------------------------
		 //     PRE- ART START
		 //------------------------------
			
		//Total number of new pediatric patients (age <=18 months at enrolment) enrolled in HIV care this month  
		SqlCohortDefinition under18monthsAtEnrol = Cohorts.createUnder18monthsAtEnrollmentCohort("under18monthsAtEnrol", pediatrichivProgram);
		SqlCohortDefinition under5YrsAtEnrol = Cohorts.createUnder5AtEnrollmentCohort("under5YrsAtEnrol", pediatrichivProgram);
		SqlCohortDefinition under15YrsAtEnrol = Cohorts.createUnder15AtEnrollmentCohort("under15YrsAtEnrol", pediatrichivProgram);
		SqlCohortDefinition over15YrsAtEnrol = Cohorts.createOver15AtEnrollmentCohort("over15YrsAtEnrol", adulthivProgram);
		
		CompositionCohortDefinition preArtduringPeriod = new CompositionCohortDefinition();
		preArtduringPeriod.setName("TR:preArtduringPeriod");
		preArtduringPeriod.getSearches().put("1",new Mapped<CohortDefinition>(onFollowingStateCohort,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		preArtduringPeriod.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInPediAndAdultProgram, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}")));
		preArtduringPeriod.setCompositionString("1 AND 2");
			
		CompositionCohortDefinition newlyEnrolledInHIVCareunder18months = new CompositionCohortDefinition();
		newlyEnrolledInHIVCareunder18months.setName("TR:newlyEnrolledInHIVCareunder18months");
		newlyEnrolledInHIVCareunder18months.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod,null));
		newlyEnrolledInHIVCareunder18months.getSearches().put("2",new Mapped<CohortDefinition>(under18monthsAtEnrol,null));
		newlyEnrolledInHIVCareunder18months.setCompositionString("1 AND 2 ");
		CohortIndicator newlyEnrolledInHIVCareunder18monthsInd=Indicators.newCohortIndicator("TR:newlyEnrolledInHIVCareunder18monthsInd", newlyEnrolledInHIVCareunder18months,null);
	
		//Total number of new pediatric patients (age <5 years) enrolled in HIV care this month   
		CompositionCohortDefinition newlyEnrolledInHIVCareunder5yrs = new CompositionCohortDefinition();
		newlyEnrolledInHIVCareunder5yrs.setName("TR:newlyEnrolledInHIVCareunder5yrs");
		newlyEnrolledInHIVCareunder5yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, null));
		newlyEnrolledInHIVCareunder5yrs.getSearches().put("2",new Mapped<CohortDefinition>(under5YrsAtEnrol,null));
		newlyEnrolledInHIVCareunder5yrs.setCompositionString("1 AND 2 ");
		CohortIndicator newlyEnrolledInHIVCareunder5yrsInd=Indicators.newCohortIndicator("TR:newlyEnrolledInHIVCareunder5yrsInd", newlyEnrolledInHIVCareunder5yrs,null);
		
		//Total number of new female pediatric patients (age < 15 years) enrolled in HIV care   
		CompositionCohortDefinition femalenewlyEnrolledInHIVCareunder15yrs = new CompositionCohortDefinition();
		femalenewlyEnrolledInHIVCareunder15yrs.setName("femalenewlyEnrolledInHIVCareunder15yrs");
		femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, null));
		femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
		femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
		femalenewlyEnrolledInHIVCareunder15yrs.setCompositionString("1 AND 2 AND 3 ");
		CohortIndicator femalesnewlyEnrolledInHIVCareunder15yrsInd=Indicators.newCohortIndicator("TR:femalesnewlyEnrolledInHIVCareunder15yrsInd", femalenewlyEnrolledInHIVCareunder15yrs,null);
		
		//Total number of new male pediatric patients (age < 15 years at enrollment) enrolled in HIV care   
		CompositionCohortDefinition malenewlyEnrolledInHIVCareunder15yrs = new CompositionCohortDefinition();
		malenewlyEnrolledInHIVCareunder15yrs.setName("MalenewlyEnrolledInHIVCareunder15yrs");
		malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, null));
		malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
		malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
		malenewlyEnrolledInHIVCareunder15yrs.setCompositionString("1 AND 2 AND 3 ");
		CohortIndicator malesnewlyEnrolledInHIVCareunder15yrsInd=Indicators.newCohortIndicator("TR:malesnewlyEnrolledInHIVCareunder15yrsInd", malenewlyEnrolledInHIVCareunder15yrs,null);
		
		// Total number of new female adult patients (age 15 or more at enrollment) enrolled in HIV care  		
        CompositionCohortDefinition femalenewlyEnrolledInHIVCareOver15yrs = new CompositionCohortDefinition();
		femalenewlyEnrolledInHIVCareOver15yrs.setName("femalenewlyEnrolledInHIVCareOver15yrs");
		femalenewlyEnrolledInHIVCareOver15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, null));
		femalenewlyEnrolledInHIVCareOver15yrs.getSearches().put("2",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
		femalenewlyEnrolledInHIVCareOver15yrs.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
		femalenewlyEnrolledInHIVCareOver15yrs.setCompositionString("1 AND 2 AND 3 ");
		CohortIndicator femalesnewlyEnrolledInHIVCareOver15yrsInd=Indicators.newCohortIndicator("TR:femalesnewlyEnrolledInHIVCareOver15yrsInd", femalenewlyEnrolledInHIVCareOver15yrs,null);
		
		// Total number of new male adult patients (age 15 or more) enrolled in HIV care 
		CompositionCohortDefinition malenewlyEnrolledInHIVCareOver15yrs = new CompositionCohortDefinition();
		malenewlyEnrolledInHIVCareOver15yrs.setName("malenewlyEnrolledInHIVCareOver15yrs");
		malenewlyEnrolledInHIVCareOver15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, null));
		malenewlyEnrolledInHIVCareOver15yrs.getSearches().put("2",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
		malenewlyEnrolledInHIVCareOver15yrs.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
		malenewlyEnrolledInHIVCareOver15yrs.setCompositionString("1 AND 2 AND 3 ");
		CohortIndicator malesnewlyEnrolledInHIVCareOver15yrsInd=Indicators.newCohortIndicator("TR:malesnewlyEnrolledInHIVCareOver15yrsInd", malenewlyEnrolledInHIVCareOver15yrs,null);
		
		//Total number of female adult patient (age 15+) curently in Pre-ART   
		
		PatientStateCohortDefinition onFollowingEver = Cohorts.createPatientStateEverCohortDefinition("onFollowingEver", OnFollowingstates);
		
	    CompositionCohortDefinition everOnPreArtduringPeriod = new CompositionCohortDefinition();
	    everOnPreArtduringPeriod.setName("TR:everOnPreArtduringPeriod");
	    everOnPreArtduringPeriod.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediAndAdultProgram, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},enrolledOnOrBefore=${endDate}")));
	    everOnPreArtduringPeriod.getSearches().put("2",new Mapped<CohortDefinition>(onFollowingEver, null));
	    everOnPreArtduringPeriod.setCompositionString("1 AND 2");
	    
	    CompositionCohortDefinition enrolledInHIVCareFemaleOver15 = new CompositionCohortDefinition();
	    enrolledInHIVCareFemaleOver15.setName("TR:enrolledInHIVCareFemaleOver15");
	    enrolledInHIVCareFemaleOver15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, null));
	    enrolledInHIVCareFemaleOver15.getSearches().put("2",new Mapped<CohortDefinition>(femaleCohort, null));
	    enrolledInHIVCareFemaleOver15.getSearches().put("3",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
	    enrolledInHIVCareFemaleOver15.setCompositionString("1 AND 2 AND 3");
		CohortIndicator Over15FemalenHIVcareInd=Indicators.newCohortIndicator("TR:Over15FemalenHIVcareInd", enrolledInHIVCareFemaleOver15,null);
	    
		CompositionCohortDefinition enrolledInHIVCareFemaleUnde15 = new CompositionCohortDefinition();
		enrolledInHIVCareFemaleUnde15.setName("TR:enrolledInHIVCareFemaleUnde15");
		enrolledInHIVCareFemaleUnde15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, null));
		enrolledInHIVCareFemaleUnde15.getSearches().put("2",new Mapped<CohortDefinition>(femaleCohort, null));
		enrolledInHIVCareFemaleUnde15.getSearches().put("3",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
		enrolledInHIVCareFemaleUnde15.setCompositionString("1 AND 2 AND 3");
		CohortIndicator under15FemaleInHIVcareInAllProgram=Indicators.newCohortIndicator("TR:under15FemaleInHIVcareInAllProgram", enrolledInHIVCareFemaleUnde15, null);
		
		//Total number of male adult patient (age 15+) curently in Pre-ART   
		CompositionCohortDefinition enrolledInHIVCareMaleOver15 = new CompositionCohortDefinition();
		enrolledInHIVCareMaleOver15.setName("TR:enrolledInHIVCareMaleOver15");
		enrolledInHIVCareMaleOver15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, null));
		enrolledInHIVCareMaleOver15.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort, null));
		enrolledInHIVCareMaleOver15.getSearches().put("3",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
		enrolledInHIVCareMaleOver15.setCompositionString("1 AND 2 AND 3");
		CohortIndicator Over15MalenHIVcareInd=Indicators.newCohortIndicator("TR:Over15MalenHIVcareInd", enrolledInHIVCareMaleOver15,null);
		
		// Total number of female pediatric patients (age <15 years) ever enrolled in HIV care 
		CompositionCohortDefinition enrolledInHIVCareMaleUnde15 = new CompositionCohortDefinition();
		enrolledInHIVCareMaleUnde15.setName("TR:enrolledInHIVCareMaleUnde15");
		enrolledInHIVCareMaleUnde15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, null));
		enrolledInHIVCareMaleUnde15.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort, null));
		enrolledInHIVCareMaleUnde15.getSearches().put("3",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
		enrolledInHIVCareMaleUnde15.setCompositionString("1 AND 2 AND 3");
		CohortIndicator under15MalenHIVcare=Indicators.newCohortIndicator("TR:under15MalenHIVcare", enrolledInHIVCareMaleUnde15,null);
				
		// Number of patients on Cotrimoxazole Prophylaxis this month
		 SqlCohortDefinition startedCotrimoXazoleDuringP = Cohorts.getPatientsOnCurrentRegimenBasedOnStartDateEndDate("startedCotrimoXazoleDuringP", cotrimoxazole);
		 InStateCohortDefinition preArtStartedInPeriod = Cohorts.createInCurrentState("TR: started on pre-Art", OnFollowingstates,onOrAfterOnOrBefore);
		 
		 CompositionCohortDefinition patientsInHIVonCotrimoOrBactrim = new CompositionCohortDefinition();
		 patientsInHIVonCotrimoOrBactrim.setName("patientsInHIVonCotrimoOrBactrim");
		 patientsInHIVonCotrimoOrBactrim.addParameter(new Parameter("startDate", "startDate", Date.class));
		 patientsInHIVonCotrimoOrBactrim.addParameter(new Parameter("endDate", "endDate", Date.class));
		 patientsInHIVonCotrimoOrBactrim.getSearches().put("1",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		 patientsInHIVonCotrimoOrBactrim.getSearches().put("2", new Mapped<CohortDefinition>(preArtStartedInPeriod, ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		 patientsInHIVonCotrimoOrBactrim.getSearches().put("3",new Mapped<CohortDefinition>(startedCotrimoXazoleDuringP, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 patientsInHIVonCotrimoOrBactrim.setCompositionString("1 AND 2 AND 3 ");
		 CohortIndicator patientsInHIVonCotrimoOrBactrimInd=Indicators.newCohortIndicator("TR:patientsInHIVonCotrimoOrBactrimInd", patientsInHIVonCotrimoOrBactrim,null);
		 
		 //Number of new patients screened for active TB at enrollment this month
		 CodedObsCohortDefinition patientsWithTBinHIVForms = Cohorts.createCodedObsCohortDefinition("patientsWithTBinHIVForms",onOrAfterOnOrBefore, tbScreeningtest,null, SetComparator.IN, TimeModifier.LAST);
		 CodedObsCohortDefinition tbScreenngPosTest = Cohorts.createCodedObsCohortDefinition("patientsWithTBinHIVForms",onOrAfterOnOrBefore, tbScreeningtest,positiveStatus, SetComparator.IN, TimeModifier.LAST);
		 
		 CompositionCohortDefinition screenedForTbInHIVProgramsComp = new CompositionCohortDefinition();
		 screenedForTbInHIVProgramsComp.setName("screenedForTbInHIVProgramsComp");
		 screenedForTbInHIVProgramsComp.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod,null));
		 screenedForTbInHIVProgramsComp.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithTBinHIVForms,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		 screenedForTbInHIVProgramsComp.setCompositionString("1 AND 2");
		 CohortIndicator screenedForTbInHIVProgramsIndi=Indicators.newCohortIndicator("screenedForTbInHIVProgramsIndi", screenedForTbInHIVProgramsComp, null);
		 
		//Number of new patients screened for active TB Positive at enrollment this month
		 CompositionCohortDefinition screenedForTbPosInHIVProgramsComp = new CompositionCohortDefinition();
		 screenedForTbPosInHIVProgramsComp.setName("TR:screenedForTbPosInHIVProgramsComp");
		 screenedForTbPosInHIVProgramsComp.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, null));
		 screenedForTbPosInHIVProgramsComp.getSearches().put("2",new Mapped<CohortDefinition>(tbScreenngPosTest,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		 screenedForTbPosInHIVProgramsComp.setCompositionString("1 AND 2 ");
		 CohortIndicator screenedForTbPosInHIVProgramsInd=Indicators.newCohortIndicator("screenedForTbPosInHIVProgramsInd", screenedForTbPosInHIVProgramsComp, null);
		
		 //Number of newly enrolled patients (age <15 years) who started TB treatment this month
		 SqlCohortDefinition onTbDrugduringPeriod = Cohorts.geOnTBDrugsByStartEndDate("on TB drug");
		 CompositionCohortDefinition patientsOnTBdrugInHIvProgramsUnder15 = new CompositionCohortDefinition();
		 patientsOnTBdrugInHIvProgramsUnder15.setName("patientsOnTBdrugInHIvProgramsUnder15");
		 patientsOnTBdrugInHIvProgramsUnder15.getSearches().put("1",new Mapped<CohortDefinition>(newlyEnrolledInHIVCareunder5yrs,null));
		 patientsOnTBdrugInHIvProgramsUnder15.getSearches().put("2",new Mapped<CohortDefinition>(femalenewlyEnrolledInHIVCareunder15yrs,null));
		 patientsOnTBdrugInHIvProgramsUnder15.getSearches().put("3",new Mapped<CohortDefinition>(onTbDrugduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 patientsOnTBdrugInHIvProgramsUnder15.setCompositionString("(1 OR 2) AND 3");
		 CohortIndicator patientsOnTBdrugInHIvProgramsUnder15Ind=Indicators.newCohortIndicator("patientsOnTBdrugInHIvProgramsUnder15Ind", patientsOnTBdrugInHIvProgramsUnder15, null);
	     
		 //Number of newly enrolled patients (age 15 or more years) who started TB treatment this month
		 CompositionCohortDefinition patientsOnTBdrugInHIvProgramsOver15 = new CompositionCohortDefinition();
		 patientsOnTBdrugInHIvProgramsOver15.setName("patientsOnTBdrugInHIvProgramsOver15");
		 patientsOnTBdrugInHIvProgramsOver15.getSearches().put("1",new Mapped<CohortDefinition>(femalenewlyEnrolledInHIVCareOver15yrs,null));
		 patientsOnTBdrugInHIvProgramsOver15.getSearches().put("2",new Mapped<CohortDefinition>(malenewlyEnrolledInHIVCareOver15yrs,null));
		 patientsOnTBdrugInHIvProgramsOver15.getSearches().put("3",new Mapped<CohortDefinition>(onTbDrugduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 patientsOnTBdrugInHIvProgramsOver15.setCompositionString("(1 OR 2) AND 3");
		 CohortIndicator patientsOnTBdrugInHIvProgramsOver15Ind=Indicators.newCohortIndicator("patientsOnTBdrugInHIvProgramsOver15Ind", patientsOnTBdrugInHIvProgramsOver15, null);
		 
		 //Number of PRE-ARV patients who have died this month
		 CompositionCohortDefinition patientsDiedandNotOnART = new CompositionCohortDefinition();
		 patientsDiedandNotOnART.setName("patientsDiedandNotOnART");
		 patientsDiedandNotOnART.getSearches().put("1",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		 patientsDiedandNotOnART.getSearches().put("2",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 patientsDiedandNotOnART.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithDeadStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		 patientsDiedandNotOnART.setCompositionString("1 AND NOT (2) AND 3");
		 CohortIndicator patientsDiedandNotOnARTInd=Indicators.newCohortIndicator("patientsDiedandNotOnARTInd", patientsDiedandNotOnART, null);
		 
		 //Number of PRE-ARV patients who have been transferred in this month
		 EncounterCohortDefinition patientTransferEncounter = Cohorts.createEncounterParameterizedByDate("clinicalEncWithoutLab", onOrAfterOnOrBefore,patientTransferEncounterType);
         CompositionCohortDefinition patientsTransferedIntAndnotOnART = new CompositionCohortDefinition();
		 patientsTransferedIntAndnotOnART.setName("patientsTransferedIntAndnotOnART");
		 patientsTransferedIntAndnotOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediAndAdultProgram, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate}")));
		 patientsTransferedIntAndnotOnART.getSearches().put("2", new Mapped<CohortDefinition>(patientTransferEncounter,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		 patientsTransferedIntAndnotOnART.getSearches().put("3", new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 patientsTransferedIntAndnotOnART.setCompositionString("1 AND 2 AND (NOT 3)");
		 CohortIndicator patientsTransferedIntAndnotOnARTInd=Indicators.newCohortIndicator("patientsTransferedIntAndnotOnARTInd", patientsTransferedIntAndnotOnART, null);
		 
		//Number of PRE-ARV patients who have been transferred out this month
		 List<ProgramWorkflowState> transferedOutstates = new ArrayList<ProgramWorkflowState>();
		 transferedOutstates.add(adulttransferedOutState);
		 transferedOutstates.add(peditransferedOutState);
		 InStateCohortDefinition transferedOutStateStartedInPeriod = Cohorts.createInCurrentState("TR:transferedOutStateStartedInPeriod", transferedOutstates,onOrAfterOnOrBefore);
		 CompositionCohortDefinition patientsTransferedoutAndnotOnART = new CompositionCohortDefinition();
		 patientsTransferedoutAndnotOnART.setName("patientsTransferedoutAndnotOnART");
		 patientsTransferedoutAndnotOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediAndAdultProgram, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate}")));
		 patientsTransferedoutAndnotOnART.getSearches().put("2", new Mapped<CohortDefinition>(transferedOutStateStartedInPeriod,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},onOrAfter=${startDate}")));
		 patientsTransferedoutAndnotOnART.getSearches().put("3",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 patientsTransferedoutAndnotOnART.setCompositionString("1 AND 2 AND (NOT 3)");
		 CohortIndicator patientsTransferedoutAndnotOnARTInd=Indicators.newCohortIndicator("patientsTransferedoutAndnotOnARTInd", patientsTransferedoutAndnotOnART, null);
		 
		 EncounterCohortDefinition clinicalEncWithoutLab = Cohorts.createEncounterParameterizedByDate("clinicalEncWithoutLab", onOrAfterOnOrBefore,clinicalEnountersIncLab);
         CompositionCohortDefinition ltfDuringPeriod = new CompositionCohortDefinition();
         ltfDuringPeriod.setName("ltfDuringPeriod");
         ltfDuringPeriod.getSearches().put("1",new Mapped<CohortDefinition>(clinicalEncWithoutLab,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${endDate}")));
         ltfDuringPeriod.setCompositionString("NOT 1");

		 CompositionCohortDefinition patientsinHIVcareLostTofolowUp = new CompositionCohortDefinition();
		 patientsinHIVcareLostTofolowUp.setName("patientsinHIVcareLostTofolowUp");
		 patientsinHIVcareLostTofolowUp.getSearches().put("1",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 patientsinHIVcareLostTofolowUp.getSearches().put("2",new Mapped<CohortDefinition>(ltfDuringPeriod,null));
		 patientsinHIVcareLostTofolowUp.setCompositionString("1 AND 2");
		 CohortIndicator patientsinHIVcareLostTofolowUpInd=Indicators.newCohortIndicator("patientsinHIVcareLostTofolowUpInd", patientsinHIVcareLostTofolowUp, null);
		 
	     
		 
         // -------------------------------------------
        //       ART CATEGORY
        //-------------------------------------------
	
		 CompositionCohortDefinition pedsonARTStateHIVClinic = new CompositionCohortDefinition();
		 pedsonARTStateHIVClinic.setName("pedsonARTStateHIVClinic");
		 pedsonARTStateHIVClinic.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 pedsonARTStateHIVClinic.getSearches().put("2",new Mapped<CohortDefinition>(at18monthsOfAge,null));
		 pedsonARTStateHIVClinic.setCompositionString("1 AND 2");
		 CohortIndicator pedsonARTStateHIVClinicInd=Indicators.newCohortIndicator("pedsonARTStateHIVClinicInd", pedsonARTStateHIVClinic, null);
		
		 CompositionCohortDefinition pedsonARTStateHIVClinicunder5 = new CompositionCohortDefinition();
		 pedsonARTStateHIVClinicunder5.setName("pedsonARTStateHIVClinicunder5");
		 pedsonARTStateHIVClinicunder5.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 pedsonARTStateHIVClinicunder5.getSearches().put("2",new Mapped<CohortDefinition>(under5Cohort,null));
		 pedsonARTStateHIVClinicunder5.setCompositionString("1 AND 2");
		 CohortIndicator pedsonARTStateHIVClinicunder5Ind=Indicators.newCohortIndicator("pedsonARTStateHIVClinicunder5Ind", pedsonARTStateHIVClinicunder5, null);
		 
		 CompositionCohortDefinition patientEnrolledInPediDuringP= Cohorts.createEnrolledInProgramDuringPeriod("patientEnrolledInPediPrograms",pediatrichivProgram);
		 CompositionCohortDefinition enrolledInPMTCTProgramsDuringP= Cohorts.createEnrolledInProgramDuringPeriod("enrolledInPMTCTProgramsDuringP",adulthivProgram);
		 CompositionCohortDefinition enrolledInAdultProgramsDuringP= Cohorts.createEnrolledInProgramDuringPeriod("enrolledInAdultProgramsDuringP",pmtctcombinedMother);
		 
		 CompositionCohortDefinition allHivProgramsDuringP = new CompositionCohortDefinition();
		 allHivProgramsDuringP.setName("allHivProgramsDuringP");
		 allHivProgramsDuringP.getSearches().put("1",new Mapped<CohortDefinition>(onARTstatesStateCohort,null));
		 allHivProgramsDuringP.getSearches().put("2",new Mapped<CohortDefinition>(patientEnrolledInPediDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 allHivProgramsDuringP.getSearches().put("3",new Mapped<CohortDefinition>(enrolledInAdultProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 allHivProgramsDuringP.getSearches().put("4",new Mapped<CohortDefinition>(enrolledInPMTCTProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 allHivProgramsDuringP.getSearches().put("5",new Mapped<CohortDefinition>(onARTDrugs,null));
		 allHivProgramsDuringP.setCompositionString("(1 AND 2 AND 3) OR (4 AND 5) ");
		 
		 CompositionCohortDefinition pedFemalesonARTStateHIVClinicunder15 = new CompositionCohortDefinition();
		 pedFemalesonARTStateHIVClinicunder15.setName("pedFemalesonARTStateHIVClinicunder15");
		 pedFemalesonARTStateHIVClinicunder15.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 pedFemalesonARTStateHIVClinicunder15.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 pedFemalesonARTStateHIVClinicunder15.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
		 pedFemalesonARTStateHIVClinicunder15.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pedFemalesonARTStateHIVClinicunder15Ind=Indicators.newCohortIndicator("pedFemalesonARTStateHIVClinicunder15Ind", pedFemalesonARTStateHIVClinicunder15, null);
			
		 CompositionCohortDefinition pedMalesonARTStateHIVClinicunder15 = new CompositionCohortDefinition();
		 pedMalesonARTStateHIVClinicunder15.setName("pedMalesonARTStateHIVClinicunder15");
		 pedMalesonARTStateHIVClinicunder15.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 pedMalesonARTStateHIVClinicunder15.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 pedMalesonARTStateHIVClinicunder15.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
		 pedMalesonARTStateHIVClinicunder15.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pedMalesonARTStateHIVClinicunder15Ind=Indicators.newCohortIndicator("pedMalesonARTStateHIVClinicunder15Ind", pedMalesonARTStateHIVClinicunder15, null);
		
		 SqlCohortDefinition onCurrentKaletraDrugOrder = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("onCurrentKaletraDrugOrder", kaletra);
		 CompositionCohortDefinition notOnCurrentKaletraDrugOrder = new CompositionCohortDefinition();
		 notOnCurrentKaletraDrugOrder.setName("notOnCurrentKaletraDrugOrder");
		 notOnCurrentKaletraDrugOrder.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 notOnCurrentKaletraDrugOrder.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 notOnCurrentKaletraDrugOrder.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,null));
		 notOnCurrentKaletraDrugOrder.setCompositionString("1 AND 2 AND (NOT 3)");
		 CohortIndicator notOnCurrentKaletraDrugOrderInd=Indicators.newCohortIndicator("notOnCurrentKaletraDrugOrderInd", notOnCurrentKaletraDrugOrder, null);
					
		 CompositionCohortDefinition onCurrentKaletraDrugOrderCompo = new CompositionCohortDefinition();
		 onCurrentKaletraDrugOrderCompo.setName("onCurrentKaletraDrugOrderCompo");
		 onCurrentKaletraDrugOrderCompo.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 onCurrentKaletraDrugOrderCompo.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 onCurrentKaletraDrugOrderCompo.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,null));
		 onCurrentKaletraDrugOrderCompo.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator onCurrentKaletraDrugOrderInd=Indicators.newCohortIndicator("onCurrentKaletraDrugOrderInd", onCurrentKaletraDrugOrderCompo, null);
		 
		 CompositionCohortDefinition femaleOnArtStateinAllHIVPrograms = new CompositionCohortDefinition();
		 femaleOnArtStateinAllHIVPrograms.setName("femaleOnArtStateinAllHIVPrograms");
		 femaleOnArtStateinAllHIVPrograms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 femaleOnArtStateinAllHIVPrograms.getSearches().put("2", new Mapped<CohortDefinition>(onARTStateInPMTCTClinic,null));
		 femaleOnArtStateinAllHIVPrograms.getSearches().put("3", new Mapped<CohortDefinition>(femaleCohort,null));
		 femaleOnArtStateinAllHIVPrograms.getSearches().put("4", new Mapped<CohortDefinition>(over15Cohort,null));
		 femaleOnArtStateinAllHIVPrograms.setCompositionString("(1 OR 2) AND 3 AND 4");
		 CohortIndicator femalesOnArtStateinAllHIVProgramsInd=Indicators.newCohortIndicator("femalesOnArtStateinAllHIVProgramsInd", femaleOnArtStateinAllHIVPrograms, null);
		 
	     CompositionCohortDefinition malesOnArtStateinAllHIVPrograms = new CompositionCohortDefinition();
		 malesOnArtStateinAllHIVPrograms.setName("malesOnArtStateinAllHIVPrograms");
		 malesOnArtStateinAllHIVPrograms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 malesOnArtStateinAllHIVPrograms.getSearches().put("2", new Mapped<CohortDefinition>(onARTStateInPMTCTClinic,null));
		 malesOnArtStateinAllHIVPrograms.getSearches().put("3", new Mapped<CohortDefinition>(maleCohort,null));
		 malesOnArtStateinAllHIVPrograms.getSearches().put("4", new Mapped<CohortDefinition>(over15Cohort,null));
		 malesOnArtStateinAllHIVPrograms.setCompositionString("(1 OR 2) AND 3 AND 4");
		 CohortIndicator malesOnArtStateinAllHIVProgramsInd=Indicators.newCohortIndicator("malesOnArtStateinAllHIVProgramsInd", malesOnArtStateinAllHIVPrograms, null);
			
		 CompositionCohortDefinition adultsnotOnCurrentKaletraDrugOrder = new CompositionCohortDefinition();
	     adultsnotOnCurrentKaletraDrugOrder.setName("adultsnotOnCurrentKaletraDrugOrder");
	     adultsnotOnCurrentKaletraDrugOrder.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
	     adultsnotOnCurrentKaletraDrugOrder.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
	     adultsnotOnCurrentKaletraDrugOrder.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,null));
	     adultsnotOnCurrentKaletraDrugOrder.setCompositionString("1 AND 2 AND (NOT 3)");
		 CohortIndicator notoadultsOnCurrentKaletraDrugOrderInd=Indicators.newCohortIndicator("notoadultsOnCurrentKaletraDrugOrderInd", adultsnotOnCurrentKaletraDrugOrder, null);
			
		 CompositionCohortDefinition adultonCurrentKaletraDrugOrderCompo = new CompositionCohortDefinition();
		 adultonCurrentKaletraDrugOrderCompo.setName("adultonCurrentKaletraDrugOrderCompo");
		 adultonCurrentKaletraDrugOrderCompo.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 adultonCurrentKaletraDrugOrderCompo.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		 adultonCurrentKaletraDrugOrderCompo.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,null));
		 adultonCurrentKaletraDrugOrderCompo.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultonCurrentKaletraDrugOrderCompoInd=Indicators.newCohortIndicator("adultonCurrentKaletraDrugOrderCompoInd", adultonCurrentKaletraDrugOrderCompo, null);
		 
		 CompositionCohortDefinition pedsPatientsNotOnArtStateNotOnFolowing = new CompositionCohortDefinition();
		 pedsPatientsNotOnArtStateNotOnFolowing.setName("pedsPatientsNotOnArtStateNotOnFolowing");
		 pedsPatientsNotOnArtStateNotOnFolowing.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 pedsPatientsNotOnArtStateNotOnFolowing.getSearches().put("2",new Mapped<CohortDefinition>(at18monthsOfAge,null));
		 pedsPatientsNotOnArtStateNotOnFolowing.getSearches().put("3",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 pedsPatientsNotOnArtStateNotOnFolowing.setCompositionString("1 AND 2 AND (NOT 3)");
		 CohortIndicator pedsPatientsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("pedsPatientsNotOnArtStateNotOnFolowingInd", pedsPatientsNotOnArtStateNotOnFolowing, null);
		 
		 CompositionCohortDefinition under5PatientsNotOnArtStateNotOnFolowing = new CompositionCohortDefinition();
		 under5PatientsNotOnArtStateNotOnFolowing.setName("under5PatientsNotOnArtStateNotOnFolowing");
		 under5PatientsNotOnArtStateNotOnFolowing.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 under5PatientsNotOnArtStateNotOnFolowing.getSearches().put("2",new Mapped<CohortDefinition>(under5Cohort,null));
		 under5PatientsNotOnArtStateNotOnFolowing.getSearches().put("3",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 under5PatientsNotOnArtStateNotOnFolowing.setCompositionString("1 AND 2 AND (NOT 3)");
		 CohortIndicator under5PatientsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("under5PatientsNotOnArtStateNotOnFolowingInd", under5PatientsNotOnArtStateNotOnFolowing, null);
		
		 CompositionCohortDefinition femalesPedsNotOnArtStateNotOnFolowing = new CompositionCohortDefinition();
		 femalesPedsNotOnArtStateNotOnFolowing.setName("femalesPedsNotOnArtStateNotOnFolowing");
		 femalesPedsNotOnArtStateNotOnFolowing.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 femalesPedsNotOnArtStateNotOnFolowing.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 femalesPedsNotOnArtStateNotOnFolowing.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
		 femalesPedsNotOnArtStateNotOnFolowing.getSearches().put("4",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 femalesPedsNotOnArtStateNotOnFolowing.setCompositionString("1 AND 2 AND 3 AND (NOT 4)");
		 CohortIndicator femalesPedsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("femalesPedsNotOnArtStateNotOnFolowingInd", femalesPedsNotOnArtStateNotOnFolowing, null);
		 
		 CompositionCohortDefinition malesPedsNotOnArtStateNotOnFolowing = new CompositionCohortDefinition();
		 malesPedsNotOnArtStateNotOnFolowing.setName("malesPedsNotOnArtStateNotOnFolowing");
		 malesPedsNotOnArtStateNotOnFolowing.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 malesPedsNotOnArtStateNotOnFolowing.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 malesPedsNotOnArtStateNotOnFolowing.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
		 malesPedsNotOnArtStateNotOnFolowing.getSearches().put("4",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 malesPedsNotOnArtStateNotOnFolowing.setCompositionString("1 AND 2 AND 3 AND (NOT 4)");
		 CohortIndicator malesPedsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("malesPedsNotOnArtStateNotOnFolowingInd", malesPedsNotOnArtStateNotOnFolowing, null);
		  
		 CompositionCohortDefinition allHivProgOnORnotArt = new CompositionCohortDefinition();
		 allHivProgOnORnotArt.setName("allHivProgOnORnotArt");
		 allHivProgOnORnotArt.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 allHivProgOnORnotArt.getSearches().put("2",new Mapped<CohortDefinition>(enrolledInAdultProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 allHivProgOnORnotArt.getSearches().put("3",new Mapped<CohortDefinition>(enrolledInPMTCTProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		 allHivProgOnORnotArt.setCompositionString("(1 AND 2 ) OR 3");
		 
		 CodedObsCohortDefinition whoStage4p = Cohorts.createCodedObsCohortDefinition("whoStage4p", whostage, whostage4p, SetComparator.IN, TimeModifier.LAST);
	     CompositionCohortDefinition pediOnArtStateinWhostage4 = new CompositionCohortDefinition();
		 pediOnArtStateinWhostage4.setName("pediOnArtStateinWhostage4");
		 pediOnArtStateinWhostage4.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgOnORnotArt,null));
		 pediOnArtStateinWhostage4.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		 pediOnArtStateinWhostage4.getSearches().put("3",new Mapped<CohortDefinition>(whoStage4p,null));
		 pediOnArtStateinWhostage4.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pediOnArtStateinWhostage4Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage4Ind", pediOnArtStateinWhostage4, null);
			
		 CodedObsCohortDefinition whoStage3p = Cohorts.createCodedObsCohortDefinition("whoStage3p", whostage, whostage3p, SetComparator.IN, TimeModifier.LAST);
	     CompositionCohortDefinition pediOnArtStateinWhostage3 = new CompositionCohortDefinition();
	     pediOnArtStateinWhostage3.setName("pediOnArtStateinWhostage3");
	     pediOnArtStateinWhostage3.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgOnORnotArt,null));
	     pediOnArtStateinWhostage3.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
	     pediOnArtStateinWhostage3.getSearches().put("3",new Mapped<CohortDefinition>(whoStage3p,null));
	     pediOnArtStateinWhostage3.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pediOnArtStateinWhostage3Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage3Ind", pediOnArtStateinWhostage3, null);
			
		 CodedObsCohortDefinition whoStage2p = Cohorts.createCodedObsCohortDefinition("whoStage2p", whostage, whostage2p, SetComparator.IN, TimeModifier.LAST);
	     CompositionCohortDefinition pediOnArtStateinWhostage2 = new CompositionCohortDefinition();
	     pediOnArtStateinWhostage2.setName("pediOnArtStateinWhostage2");
	     pediOnArtStateinWhostage2.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgOnORnotArt,null));
	     pediOnArtStateinWhostage2.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
	     pediOnArtStateinWhostage2.getSearches().put("3",new Mapped<CohortDefinition>(whoStage2p,null));
	     pediOnArtStateinWhostage2.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pediOnArtStateinWhostage2Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage2Ind", pediOnArtStateinWhostage2, null);
			
		 CodedObsCohortDefinition whoStage1p = Cohorts.createCodedObsCohortDefinition("whoStage1p", whostage, whostage1p, SetComparator.IN, TimeModifier.LAST);
	     CompositionCohortDefinition pediOnArtStateinWhostage1 = new CompositionCohortDefinition();
	     pediOnArtStateinWhostage1.setName("pediOnArtStateinWhostage1");
	     pediOnArtStateinWhostage1.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgOnORnotArt,null));
	     pediOnArtStateinWhostage1.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
	     pediOnArtStateinWhostage1.getSearches().put("3",new Mapped<CohortDefinition>(whoStage1p,null));
	     pediOnArtStateinWhostage1.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pediOnArtStateinWhostage1Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage1Ind", pediOnArtStateinWhostage1, null);
			
		 CodedObsCohortDefinition whoStageX = new CodedObsCohortDefinition();
		 whoStageX.setName("whoStageX");
		 whoStageX.setTimeModifier(TimeModifier.LAST);
		 whoStageX.setQuestion(Context.getConceptService().getConceptByName("WHO STAGE"));
		 CompositionCohortDefinition pediOnArtStateInWhoStageX = new CompositionCohortDefinition();
	     pediOnArtStateInWhoStageX.setName("pediOnArtStateInWhoStageX");
	     pediOnArtStateInWhoStageX.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgOnORnotArt,null));
	     pediOnArtStateInWhoStageX.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
	     pediOnArtStateInWhoStageX.getSearches().put("3",new Mapped<CohortDefinition>(whoStageX,null));
	     pediOnArtStateInWhoStageX.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pediOnArtStateInWhoStageXInd=Indicators.newCohortIndicator("pediOnArtStateInWhoStageXInd", pediOnArtStateInWhoStageX, null);
		 
		 CompositionCohortDefinition femaleAdultsadultsOnArtState= new CompositionCohortDefinition();
		 femaleAdultsadultsOnArtState.setName("femaleAdultsadultsOnArtState");
		 femaleAdultsadultsOnArtState.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 femaleAdultsadultsOnArtState.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		 femaleAdultsadultsOnArtState.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
		 femaleAdultsadultsOnArtState.getSearches().put("4",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 femaleAdultsadultsOnArtState.setCompositionString("1 AND 2 AND 3 AND (NOT 4)");
		 CohortIndicator femaleAdultsadultsOnArtStateInd=Indicators.newCohortIndicator("femaleAdultsadultsOnArtStateInd", femaleAdultsadultsOnArtState, null);
		 
		 CompositionCohortDefinition maleAdultsadultsOnArtState= new CompositionCohortDefinition();
		 maleAdultsadultsOnArtState.setName("maleAdultsadultsOnArtState");
		 maleAdultsadultsOnArtState.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 maleAdultsadultsOnArtState.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		 maleAdultsadultsOnArtState.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
		 maleAdultsadultsOnArtState.getSearches().put("4",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
		 maleAdultsadultsOnArtState.setCompositionString("1 AND 2 AND 3 AND (NOT 4)");
		 CohortIndicator maleAdultsadultsOnArtStateInd=Indicators.newCohortIndicator("maleAdultsadultsOnArtStateInd", maleAdultsadultsOnArtState, null);
		 
		 CodedObsCohortDefinition whoStage4ad = Cohorts.createCodedObsCohortDefinition("whoStage4ad", onOrAfterOnOrBefore, whostage, whostage4adlt, SetComparator.IN, TimeModifier.LAST);
	     CompositionCohortDefinition adultsOnArtStateinWhostage4 = new CompositionCohortDefinition();
	     adultsOnArtStateinWhostage4.setName("adultsOnArtStateinWhostage4");
	     adultsOnArtStateinWhostage4.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
	     adultsOnArtStateinWhostage4.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
	     adultsOnArtStateinWhostage4.getSearches().put("3",new Mapped<CohortDefinition>(whoStage4ad,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
	     adultsOnArtStateinWhostage4.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultsOnArtStateinWhostage4Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage4Ind", adultsOnArtStateinWhostage4, null);
			
	     CodedObsCohortDefinition whoStage3ad = Cohorts.createCodedObsCohortDefinition("whoStage3ad", onOrAfterOnOrBefore, whostage, whostage3adlt, SetComparator.IN, TimeModifier.LAST);
		 CompositionCohortDefinition adultsOnArtStateinWhostage3 = new CompositionCohortDefinition();
		 adultsOnArtStateinWhostage3.setName("adultsOnArtStateinWhostage3");
		 adultsOnArtStateinWhostage3.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 adultsOnArtStateinWhostage3.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		 adultsOnArtStateinWhostage3.getSearches().put("3",new Mapped<CohortDefinition>(whoStage3ad,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		 adultsOnArtStateinWhostage3.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultsOnArtStateinWhostage3Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage3Ind", adultsOnArtStateinWhostage3, null);
			
		 CodedObsCohortDefinition whoStage2ad = Cohorts.createCodedObsCohortDefinition("whoStage2ad", onOrAfterOnOrBefore, whostage, whostage2adlt, SetComparator.IN, TimeModifier.LAST);
		 CompositionCohortDefinition adultsOnArtStateinWhostage2 = new CompositionCohortDefinition();
		 adultsOnArtStateinWhostage2.setName("adultsOnArtStateinWhostage2");
		 adultsOnArtStateinWhostage2.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 adultsOnArtStateinWhostage2.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		 adultsOnArtStateinWhostage2.getSearches().put("3",new Mapped<CohortDefinition>(whoStage2ad,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		 adultsOnArtStateinWhostage2.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultsOnArtStateinWhostage2Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage2Ind", adultsOnArtStateinWhostage2, null);
			
		 CodedObsCohortDefinition whoStage1ad = Cohorts.createCodedObsCohortDefinition("whoStage1ad", onOrAfterOnOrBefore, whostage, whostage1adlt, SetComparator.IN, TimeModifier.LAST);
		 CompositionCohortDefinition adultsOnArtStateinWhostage1 = new CompositionCohortDefinition();
		 adultsOnArtStateinWhostage1.setName("adultsOnArtStateinWhostage1");
		 adultsOnArtStateinWhostage1.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		 adultsOnArtStateinWhostage1.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		 adultsOnArtStateinWhostage1.getSearches().put("3",new Mapped<CohortDefinition>(whoStage1ad,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		 adultsOnArtStateinWhostage1.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultsOnArtStateinWhostage1Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage1Ind", adultsOnArtStateinWhostage1, null);
		 
		 CompositionCohortDefinition adultsOnArtStateinWhostageX = new CompositionCohortDefinition();
	     adultsOnArtStateinWhostageX.setName("adultsOnArtStateinWhostageX");
	     adultsOnArtStateinWhostageX.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgOnORnotArt,null));
	     adultsOnArtStateinWhostageX.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
	     adultsOnArtStateinWhostageX.getSearches().put("3",new Mapped<CohortDefinition>(whoStageX,null));
	     adultsOnArtStateinWhostageX.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultsOnArtStateinWhostageXInd=Indicators.newCohortIndicator("adultsOnArtStateinWhostageXInd", adultsOnArtStateinWhostageX, null);
		 
		 CompositionCohortDefinition pedionARTDiedDuringP = new CompositionCohortDefinition();
	     pedionARTDiedDuringP.setName("pedionARTDiedDuringP");
	     pedionARTDiedDuringP.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
	     pedionARTDiedDuringP.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
	     pedionARTDiedDuringP.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithDeadStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
	     pedionARTDiedDuringP.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator pedionARTDiedDuringPInd=Indicators.newCohortIndicator("pedionARTDiedDuringPInd", pedionARTDiedDuringP, null);
		 
		 CompositionCohortDefinition adultsonARTDiedDuringP = new CompositionCohortDefinition();
	     adultsonARTDiedDuringP.setName("adultsonARTDiedDuringP");
	     adultsonARTDiedDuringP.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
	     adultsonARTDiedDuringP.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
	     adultsonARTDiedDuringP.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithDeadStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
	     adultsonARTDiedDuringP.setCompositionString("1 AND 2 AND 3");
		 CohortIndicator adultsonARTDiedDuringPInd=Indicators.newCohortIndicator("adultsonARTDiedDuringPInd", adultsonARTDiedDuringP, null);
		 
		 EncounterCohortDefinition patientWithHIVForms = Cohorts.createEncounterBasedOnForms("patientWithHIVForms",onOrAfterOnOrBefore, hivVisitsforms);
		 CompositionCohortDefinition pedsOnArtLostAndwithHIVForms = new CompositionCohortDefinition();
		 pedsOnArtLostAndwithHIVForms.setName("pedsOnArtLostAndwithHIVForms");
		 pedsOnArtLostAndwithHIVForms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 pedsOnArtLostAndwithHIVForms.getSearches().put("2",new Mapped<CohortDefinition>(onARTStateInPMTCTClinic,null));
		 pedsOnArtLostAndwithHIVForms.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort,null));
		 pedsOnArtLostAndwithHIVForms.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithhivTransferVisit,ParameterizableUtil.createParameterMappings("startDate=${startDate-3m},endDate=${startDate}")));
		 pedsOnArtLostAndwithHIVForms.getSearches().put("5",new Mapped<CohortDefinition>(patientWithHIVForms,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate}")));
		 pedsOnArtLostAndwithHIVForms.setCompositionString("(1 OR 2) AND 3 AND (NOT (4 OR 5)) ");
		 CohortIndicator pedsOnArtLostAndwithHIVFormsInd=Indicators.newCohortIndicator("pedsOnArtLostAndwithHIVFormsInd", pedsOnArtLostAndwithHIVForms, null);
				 
	     CompositionCohortDefinition adultOnArtLostAndwithHIVForms = new CompositionCohortDefinition();
		 adultOnArtLostAndwithHIVForms.setName("adultOnArtLostAndwithHIVForms");
		 adultOnArtLostAndwithHIVForms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
		 adultOnArtLostAndwithHIVForms.getSearches().put("2",new Mapped<CohortDefinition>(onARTStateInPMTCTClinic,null));
		 adultOnArtLostAndwithHIVForms.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort,null));
		 adultOnArtLostAndwithHIVForms.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithhivTransferVisit,ParameterizableUtil.createParameterMappings("startDate=${startDate-3m},endDate=${startDate}")));
		 adultOnArtLostAndwithHIVForms.getSearches().put("5",new Mapped<CohortDefinition>(patientWithHIVForms,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${startDate}")));
		 adultOnArtLostAndwithHIVForms.setCompositionString("(1 OR 2) AND 3 AND (NOT (4 OR 5)) ");
		 CohortIndicator adultOnArtLostAndwithHIVFormsInd=Indicators.newCohortIndicator("adultOnArtLostAndwithHIVFormsInd", adultOnArtLostAndwithHIVForms, null);
		 
		  CompositionCohortDefinition malesOnArtLostforMoreThan12months = new CompositionCohortDefinition();
		  malesOnArtLostforMoreThan12months.setName("malesOnArtLostforMoreThan12months");
		  malesOnArtLostforMoreThan12months.getSearches().put("1",new Mapped<CohortDefinition>(onARTstatesStateCohort,ParameterizableUtil.createParameterMappings("onDate=${startDate-12m}")));
		  malesOnArtLostforMoreThan12months.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		  malesOnArtLostforMoreThan12months.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
		  malesOnArtLostforMoreThan12months.setCompositionString("1 AND 2 AND 3 ");
		  CohortIndicator malesOnArtLostforMoreThan12monthsInd=Indicators.newCohortIndicator("malesOnArtLostforMoreThan12monthsInd", malesOnArtLostforMoreThan12months, null);
			
		  CompositionCohortDefinition femalesOnArtLostforMoreThan12months = new CompositionCohortDefinition();
		  femalesOnArtLostforMoreThan12months.setName("femalesOnArtLostforMoreThan12months");
		  femalesOnArtLostforMoreThan12months.getSearches().put("1",new Mapped<CohortDefinition>(onARTstatesStateCohort,ParameterizableUtil.createParameterMappings("onDate=${startDate-12m}")));
		  femalesOnArtLostforMoreThan12months.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
		  femalesOnArtLostforMoreThan12months.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
		  femalesOnArtLostforMoreThan12months.setCompositionString("1 AND 2 AND 3 ");
		  CohortIndicator femalesOnArtLostforMoreThan12monthsInd=Indicators.newCohortIndicator("femalesOnArtLostforMoreThan12monthsInd", femalesOnArtLostforMoreThan12months, null);
		 
		  CompositionCohortDefinition pedionARTTransferedOutDuringP = new CompositionCohortDefinition();
		  pedionARTTransferedOutDuringP.setName("pedionARTTransferedOutDuringP");
		  pedionARTTransferedOutDuringP.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		  pedionARTTransferedOutDuringP.getSearches().put("2",new Mapped<CohortDefinition>(under15Cohort,null));
		  pedionARTTransferedOutDuringP.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithtransferStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${endDate},onOrBefore=${endDate}")));
		  pedionARTTransferedOutDuringP.setCompositionString("1 AND 2 AND 3");
		  CohortIndicator pedionARTTransferedOutDuringPInd=Indicators.newCohortIndicator("pedionARTTransferedOutDuringPInd", pedionARTTransferedOutDuringP, null);
				 		
		  CompositionCohortDefinition adultsonARTTransferedOutDuringP = new CompositionCohortDefinition();
		  adultsonARTTransferedOutDuringP.setName("adultsonARTTransferedOutDuringP");
		  adultsonARTTransferedOutDuringP.getSearches().put("1",new Mapped<CohortDefinition>(allHivProgramsDuringP,null));
		  adultsonARTTransferedOutDuringP.getSearches().put("2",new Mapped<CohortDefinition>(over15Cohort,null));
		  adultsonARTTransferedOutDuringP.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithtransferStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${endDate},onOrBefore=${endDate}")));
		  adultsonARTTransferedOutDuringP.setCompositionString("1 AND 2 AND 3");
		  CohortIndicator adultsonARTTransferedOutDuringPInd=Indicators.newCohortIndicator("adultsonARTTransferedOutDuringPInd", adultsonARTTransferedOutDuringP, null);
		  
		  CompositionCohortDefinition pedionWithTransferInForm = new CompositionCohortDefinition();
		  pedionWithTransferInForm.setName("pedionWithTransferInForm");
		  pedionWithTransferInForm.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		  pedionWithTransferInForm.getSearches().put("2",new Mapped<CohortDefinition>(enrolledInAdultProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		  pedionWithTransferInForm.getSearches().put("3",new Mapped<CohortDefinition>(under15Cohort,null));
		  pedionWithTransferInForm.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithhivTransferVisit,ParameterizableUtil.createParameterMappings("startDate=${endDate},endDate=${endDate}")));
		  pedionWithTransferInForm.setCompositionString("(1 OR 2) AND 3 AND 4");
		  CohortIndicator pedionWithTransferInFormInd=Indicators.newCohortIndicator("pedionWithTransferInFormInd", pedionWithTransferInForm, null);
				
		  CompositionCohortDefinition adultsOnWithTransferInForm = new CompositionCohortDefinition();
		  adultsOnWithTransferInForm.setName("adultsOnWithTransferInForm");
		  adultsOnWithTransferInForm.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		  adultsOnWithTransferInForm.getSearches().put("2",new Mapped<CohortDefinition>(enrolledInAdultProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		  adultsOnWithTransferInForm.getSearches().put("3",new Mapped<CohortDefinition>(over15Cohort,null));
		  adultsOnWithTransferInForm.getSearches().put("4",new Mapped<CohortDefinition>(patientsWithhivTransferVisit,ParameterizableUtil.createParameterMappings("startDate=${endDate},endDate=${endDate}")));
		  adultsOnWithTransferInForm.setCompositionString("(1 OR 2) AND 3 AND 4");
		  CohortIndicator adultsOnWithTransferInFormInd=Indicators.newCohortIndicator("adultsOnWithTransferInFormInd", adultsOnWithTransferInForm, null);
				
	
		//Add global filters to the report
		//PRE-ART DATA ELEMENT
		dsd.addColumn("1a","rwandareports.tracnetreport.indicator.preart.newPedsUnderEighteenMonthsInHivCare",new Mapped(newlyEnrolledInHIVCareunder18monthsInd, null), "");
     	dsd.addColumn("2a","rwandareports.tracnetreport.indicator.preart.newPedsUnderFiveInHivCare",new Mapped(newlyEnrolledInHIVCareunder5yrsInd,null),"");
	    dsd.addColumn("3a","rwandareports.tracnetreport.indicator.preart.newFemaleUnderFifteenInHivCare",new Mapped(femalesnewlyEnrolledInHIVCareunder15yrsInd,null),"");
		dsd.addColumn("4a","rwandareports.tracnetreport.indicator.preart.newMaleUnderFifteenInHivCare",new Mapped(malesnewlyEnrolledInHIVCareunder15yrsInd,null),"");
		dsd.addColumn("5a","rwandareports.tracnetreport.indicator.preart.newFemaleMoreThanFifteenInHivCare",new Mapped(femalesnewlyEnrolledInHIVCareOver15yrsInd,null),"");
		dsd.addColumn("6a","rwandareports.tracnetreport.indicator.preart.newMaleMoreThanFifteenInHivCare",new Mapped(malesnewlyEnrolledInHIVCareOver15yrsInd,null),"");
		dsd.addColumn("9a","rwandareports.tracnetreport.indicator.preart.femaleMoreThanFifteenEverInHiv",new Mapped(Over15FemalenHIVcareInd,null),"");
		dsd.addColumn("10a","rwandareports.tracnetreport.indicator.preart.femalePedsUnderFifteenEverInHiv",new Mapped(under15FemaleInHIVcareInAllProgram,null),"");
		dsd.addColumn("11a","rwandareports.tracnetreport.indicator.preart.maleMoreThanFifteenEverInHiv",new Mapped(Over15MalenHIVcareInd,null),"");
		dsd.addColumn("12a","rwandareports.tracnetreport.indicator.preart.malePedsUnderFifteenEverInHiv",new Mapped(under15MalenHIVcare,null),"");
		dsd.addColumn("13a","rwandareports.tracnetreport.indicator.preart.patientsOnCotrimoProphylaxis",new Mapped(patientsInHIVonCotrimoOrBactrimInd,null),"");
		dsd.addColumn("14a","rwandareports.tracnetreport.indicator.preart.patientsActiveTbAtEnrolThisMonth",new Mapped(screenedForTbInHIVProgramsIndi,null),"");
		dsd.addColumn("15a","rwandareports.tracnetreport.indicator.preart.patientsTbPositiveAtEnrolThisMonth",new Mapped(screenedForTbPosInHIVProgramsInd,null),"");
		dsd.addColumn("16a","rwandareports.tracnetreport.indicator.preart.newEnrolledPedsStartTbTreatThisMonth",new Mapped(patientsOnTBdrugInHIvProgramsUnder15Ind,null),""); 
	    dsd.addColumn("17a","rwandareports.tracnetreport.indicator.preart.newEnrolledAdultsStartTbTreatThisMonth",new Mapped(patientsOnTBdrugInHIvProgramsOver15Ind,null),""); 
		dsd.addColumn("18a","rwandareports.tracnetreport.indicator.preart.PatientsInPreARVDiedThisMonth",new Mapped(patientsDiedandNotOnARTInd,null),""); 
		dsd.addColumn("19a","rwandareports.tracnetreport.indicator.preart.PatientsInPreARVTransferredInThisMonth",new Mapped(patientsTransferedIntAndnotOnARTInd,null),""); 
		dsd.addColumn("20a","rwandareports.tracnetreport.indicator.preart.PatientsInPreARVTransferredOutThisMonth",new Mapped(patientsTransferedoutAndnotOnARTInd,null),"");  
		dsd.addColumn("21a","rwandareports.tracnetreport.indicator.preart.PatientsInPreARVTLostToFollowUpThisMonth",new Mapped(patientsinHIVcareLostTofolowUpInd,null),""); 
		
    	// ART DATA ELEMENTS
		dsd.addColumn("1b","rwandareports.tracnetreport.indicator.art.pedsUnderEighteenMonthsCurrentOnArv",new Mapped(pedsonARTStateHIVClinicInd,null),""); 
		dsd.addColumn("2b","rwandareports.tracnetreport.indicator.art.pedsUnderFiveCurrentOnArv",new Mapped(pedsonARTStateHIVClinicunder5Ind,null),"");  
		dsd.addColumn("3b","rwandareports.tracnetreport.indicator.art.femalePedsUnderFifteenCurrentOnArv",new Mapped(pedFemalesonARTStateHIVClinicunder15Ind,null),"");  
		dsd.addColumn("4b","rwandareports.tracnetreport.indicator.art.malePedsUnderFifteenCurrentOnArv",new Mapped(pedMalesonARTStateHIVClinicunder15Ind,null),""); 
		dsd.addColumn("5b","rwandareports.tracnetreport.indicator.art.pedsOnFirstLineReg",new Mapped(notOnCurrentKaletraDrugOrderInd,null),""); 
		dsd.addColumn("6b","rwandareports.tracnetreport.indicator.art.pedsOnSecondLineReg",new Mapped(onCurrentKaletraDrugOrderInd,null),""); 
		dsd.addColumn("7b","rwandareports.tracnetreport.indicator.art.femaleMoreThanFifteenCurrentOnArv",new Mapped(femalesOnArtStateinAllHIVProgramsInd,null),""); 
		dsd.addColumn("8b","rwandareports.tracnetreport.indicator.art.maleMoreThanFifteenCurrentOnArv",new Mapped(malesOnArtStateinAllHIVProgramsInd,null),""); 
		dsd.addColumn("9b","rwandareports.tracnetreport.indicator.art.adultOnFirstLineReg",new Mapped(notoadultsOnCurrentKaletraDrugOrderInd,null),"");  
		dsd.addColumn("10b","rwandareports.tracnetreport.indicator.art.adultOnSecondLineReg",new Mapped(adultonCurrentKaletraDrugOrderCompoInd,null),"");  
		dsd.addColumn("11b","rwandareports.tracnetreport.indicator.art.newPedsUnderEighteenMonthStartArvThisMonth",new Mapped(pedsPatientsNotOnArtStateNotOnFolowingInd,null),"");  
		dsd.addColumn("12b","rwandareports.tracnetreport.indicator.art.newPedsUnderFiveStartArvThisMonth",new Mapped(under5PatientsNotOnArtStateNotOnFolowingInd,null),"");  
		dsd.addColumn("13b","rwandareports.tracnetreport.indicator.art.newFemalePedsUnderFifteenStartArvThisMonth",new Mapped(femalesPedsNotOnArtStateNotOnFolowingInd,null),"");  
		dsd.addColumn("14b","rwandareports.tracnetreport.indicator.art.newMalePedsUnderFifteenStartArvThisMonth",new Mapped(malesPedsNotOnArtStateNotOnFolowingInd,null),""); 
		dsd.addColumn("15b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageFourThisMonth",new Mapped(pediOnArtStateinWhostage4Ind,null),""); 
		dsd.addColumn("16b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageThreeThisMonth",new Mapped(pediOnArtStateinWhostage3Ind,null),"");  
		dsd.addColumn("17b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageTwoThisMonth",new Mapped(pediOnArtStateinWhostage2Ind,null),"");  
		dsd.addColumn("18b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageOneThisMonth",new Mapped(pediOnArtStateinWhostage1Ind,null),"");  
		dsd.addColumn("19b","rwandareports.tracnetreport.indicator.art.newPedsUndefinedWhoStageThisMonth",new Mapped(pediOnArtStateInWhoStageXInd,null),"");  
		dsd.addColumn("20b","rwandareports.tracnetreport.indicator.art.newFemaleAdultStartiArvThisMonth",new Mapped(femaleAdultsadultsOnArtStateInd,null),"");  
		dsd.addColumn("21b","rwandareports.tracnetreport.indicator.art.newMaleAdultStartiArvThisMonth",new Mapped(maleAdultsadultsOnArtStateInd,null),"");  
		dsd.addColumn("22b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageFourThisMonth",new Mapped(adultsOnArtStateinWhostage4Ind,null),"");  
		dsd.addColumn("23b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageThreeThisMonth",new Mapped(adultsOnArtStateinWhostage3Ind,null),"");  
		dsd.addColumn("24b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageTwoThisMonth",new Mapped(adultsOnArtStateinWhostage2Ind,null),"");  
		dsd.addColumn("25b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageOneThisMonth",new Mapped(adultsOnArtStateinWhostage1Ind,null),"");  
		dsd.addColumn("26b","rwandareports.tracnetreport.indicator.art.newAdultUndefinedWhoStageThisMonth",new Mapped(adultsOnArtStateinWhostageXInd,null),"");  
		dsd.addColumn("29b","rwandareports.tracnetreport.indicator.art.arvPedsDiedThisMonth",new Mapped(pedionARTDiedDuringPInd,null),"");  
		dsd.addColumn("30b","rwandareports.tracnetreport.indicator.art.arvAdultDiedThisMonth",new Mapped(adultsonARTDiedDuringPInd,null),"");  
		dsd.addColumn("31b","rwandareports.tracnetreport.indicator.art.arvPedsLostFollowupMoreThreeMonths",new Mapped(pedsOnArtLostAndwithHIVFormsInd,null),"");  
		dsd.addColumn("32b","rwandareports.tracnetreport.indicator.art.arvAdultLostFollowupMoreThreeMonths",new Mapped(adultOnArtLostAndwithHIVFormsInd,null),""); 
		dsd.addColumn("33b","rwandareports.tracnetreport.indicator.art.maleOnTreatTwelveAfterInitArv",new Mapped(malesOnArtLostforMoreThan12monthsInd,null),"");  
		dsd.addColumn("34b","rwandareports.tracnetreport.indicator.art.femaleOnTreatTwelveAfterInitArv",new Mapped(femalesOnArtLostforMoreThan12monthsInd,null),"");  
		dsd.addColumn("35b","rwandareports.tracnetreport.indicator.art.arvPedsTransferredOutThisMonth",new Mapped(pedionARTTransferedOutDuringPInd,null),""); 
		dsd.addColumn("36b","rwandareports.tracnetreport.indicator.art.arvAdultTransferredOutThisMonth",new Mapped(adultsonARTTransferedOutDuringPInd,null),"");  
	    dsd.addColumn("37b","rwandareports.tracnetreport.indicator.art.arvPedsTransferredInThisMonth",new Mapped(pedionWithTransferInFormInd,null),""); 
		dsd.addColumn("38b","rwandareports.tracnetreport.indicator.art.arvAdultTransferreInThisMonth",new Mapped(adultsOnWithTransferInFormInd,null),"");  
		
		return dsd;
	}
	
	private void setupProperties() {
		//onOrAfterOnOrBeforeParamterNames.add("onOrAfter");
		//onOrAfterOnOrBeforeParamterNames.add("onOrBefore");
		adulthivProgram = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediatrichivProgram = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		pmtctcombinedMother = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
		adultOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		adultOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		pediOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		adulttransferedOutState = gp.getProgramWorkflowState(GlobalPropertiesManagement.TRANSFERRED_OUT_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
		peditransferedOutState = gp.getProgramWorkflowState(GlobalPropertiesManagement.TRANSFERRED_OUT_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
		onOrAfterOnOrBefore.add("onOrAfter");
		onOrAfterOnOrBefore.add("onOrBefore");
		tbScreeningtest = gp.getConcept(GlobalPropertiesManagement.TB_SCREENING_TEST);
		positiveStatus = gp.getConcept(GlobalPropertiesManagement.POSITIVE_HIV_TEST_ANSWER);
		reasonForExitingCare= gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
		patientDied = gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
		patientTransferedOut=gp.getConcept(GlobalPropertiesManagement.TRASNFERED_OUT);
		allergypediForm = gp.getForm(GlobalPropertiesManagement.PEDI_ALLERGY);
		allergyadultForm = gp.getForm(GlobalPropertiesManagement.ADULT_ALLERGY);
		medicationForms.add(allergypediForm);
		medicationForms.add(allergyadultForm);
		clinicalEnountersIncLab = gp.getEncounterTypeList(GlobalPropertiesManagement.CLINICAL_ENCOUNTER_TYPES);
		adultHivForm = gp.getForm(GlobalPropertiesManagement.ADULT_FLOW_VISIT);
		pediHivform = gp.getForm(GlobalPropertiesManagement.PEDI_FLOW_VISIT);
		hivVisitsforms.add(adultHivForm);
		hivVisitsforms.add(pediHivform);
		transferinForm=gp.getForm(GlobalPropertiesManagement.HIV_TRANSFER_FORM);
		patientTransferEncounterType = gp.getEncounterType(GlobalPropertiesManagement.PATIENT_TRANSFER_ENCOUNTER);
		adverse_med=gp.getConcept(GlobalPropertiesManagement.ADVERSE_MED_EFFECT);
		rifampicin=gp.getConcept(GlobalPropertiesManagement.RIFAMPICIN_DRUG);
		ethambutol=gp.getConcept(GlobalPropertiesManagement.ETHAMBUTOL_DRUG);
		isoniazid=gp.getConcept(GlobalPropertiesManagement.ISONIAZID_DRUG);
		kaletra = gp.getConcept(GlobalPropertiesManagement.KALETRA_DRUG);
		whostage = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE);
		whostage4p = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE4PED);
		whostage3p = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE3PED);
		whostage2p = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE2PED);
		whostage1p = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE1PED);
		whostageinconue = gp.getConcept(GlobalPropertiesManagement.WHOSTAGEUNKOWN);
		whostage4adlt = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE4AD);
		whostage3adlt = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE3AD);
		whostage2adlt = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE2AD);
		whostage1adlt = gp.getConcept(GlobalPropertiesManagement.WHOSTAGE1AD);
		cotrimoxazole = gp.getConcept(GlobalPropertiesManagement.COTRIMOXAZOLE_DRUG);
	}
	
}
