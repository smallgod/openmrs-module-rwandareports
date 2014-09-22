package org.openmrs.module.rwandareports.reporting;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;
 
public class SetupPBFReport {
        
        protected final static Log log = LogFactory.getLog(SetupPBFReport.class);
        
        GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
     // properties
        private Program pmtctPregnancyProgram;
        private Program CCMotherProgram;
        private Program exposedInfant;
        private ProgramWorkflowState Artpregnant;
        private List<ProgramWorkflowState> ArtpregnantState = new ArrayList<ProgramWorkflowState>();
        private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
        private List<String> startDateEndDate = new ArrayList<String>();
        private Program pediatrichivProgram;
        private Program adulthivProgram;
        private Concept cotrimoxazole;
        private Form tranferToCC;
        private List<Form> transferToCCForms=new ArrayList<Form>();
        List<Program> hivPrograms = new ArrayList<Program>();
        private Concept hivPCR;
        private Concept sti;
        private Concept reasonForExitingCare;
        private Concept patientDied;
        private Concept patientTransferedOut;
        private Concept weight;
        private Concept tbScreeningtest;
        private Concept cd4;
        private Concept viralLoadConcept;
        private ProgramWorkflowState adultOnART;
        private ProgramWorkflowState pediOnART;
        private ProgramWorkflowState adultOnFollowing;
        private ProgramWorkflowState pediOnFollowing;
        private ProgramWorkflowState pmtctArt;
        private List<EncounterType> clinicalEnountersIncLab;
       
       
        public void setup() throws Exception {
                
                setupProperties();
                
                ReportDefinition rd = createReportDefinition();
                ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "MohPBFReport.xls",
                    "XlsPBFreporttemplate", null);
                Properties props = new Properties();
                props.put("repeatingSections", "sheet:1,dataset:PBF Report Location");
                props.put("sortWeight","5000");
                design.setProperties(props);
                Helper.saveReportDesign(design);
        }
        
        public void delete() {
                ReportService rs = Context.getService(ReportService.class);
                for (ReportDesign rd : rs.getAllReportDesigns(false)) {
                        if ("XlsPBFreporttemplate".equals(rd.getName())) {
                                rs.purgeReportDesign(rd);
                        }
                }
                Helper.purgeReportDefinition("PBF Report");
        }
        
        private ReportDefinition createReportDefinition() {
           
        	ReportDefinition rd = new ReportDefinition();
            rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
            rd.addParameter(new Parameter("endDate", "End Date", Date.class));
            Properties properties = new Properties();
            //properties.setProperty("hierarchyFields", "countyDistrict:District");
            rd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
            
            rd.setName("PBF Report");
            
            LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
                    createDataSetDefinition());
            ldsd.setName("PBF Report Location");
            ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
            ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
            ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
            
            rd.addDataSetDefinition(ldsd,
                ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
            
            Helper.saveReportDefinition(rd);
            
            return rd;
                
       }
        
        private DataSetDefinition createDataSetDefinition() {
         CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
         dsd.setName("PBF Report Indicators");
         dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
         dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
         
         
         //Gender Cohort definitions
         GenderCohortDefinition females = Cohorts.createFemaleCohortDefinition("femalesDefinition");
         
         // PMTCT Pregnancy Program
        // 1 Pregnant women from the previous quarter
         ProgramEnrollmentCohortDefinition enrolledInPMTCTPreg = Cohorts.createProgramEnrollment("PMTCT Pregnancy", pmtctPregnancyProgram);
         enrolledInPMTCTPreg.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter",Date.class)); 
         
         CompositionCohortDefinition womenOnARTInPMTCTPreg = new CompositionCohortDefinition();
         womenOnARTInPMTCTPreg.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
         womenOnARTInPMTCTPreg.setName("pedsonARTStateHIVClinicunder5");
         womenOnARTInPMTCTPreg.getSearches().put("1",new Mapped<CohortDefinition>(enrolledInPMTCTPreg,ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter}")));
         womenOnARTInPMTCTPreg.getSearches().put("2",new Mapped<CohortDefinition>(females,null));
         womenOnARTInPMTCTPreg.setCompositionString("1 AND 2");
         
         CohortIndicator onePMTCTPreg = Indicators.newCountIndicator( "PMTCTPregQ:in pmtctPreg ", womenOnARTInPMTCTPreg,
                 ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter}"));
         onePMTCTPreg.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
         
         //1 ART Pregnant women from the previous quarter
         InStateCohortDefinition pregWomenLastQuarter = Cohorts.createInCurrentState("PMTCTPregQ:in: pregWomenLastQuarter", ArtpregnantState,
                 onOrAfterOnOrBefore);
         
         CompositionCohortDefinition artPMTCTPreg = new CompositionCohortDefinition();
         artPMTCTPreg.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
         artPMTCTPreg.setName("pedsonARTStateHIVClinicunder5");
         artPMTCTPreg.getSearches().put("1",new Mapped<CohortDefinition>(pregWomenLastQuarter,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
         artPMTCTPreg.getSearches().put("2",new Mapped<CohortDefinition>(females,null));
         artPMTCTPreg.setCompositionString("1 AND 2");
         
         CohortIndicator oneArtPMTCTPreg = Indicators.newCountIndicator( "PMTCTPregQ:in pmtctPreg ", artPMTCTPreg,
                 ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
         oneArtPMTCTPreg.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
         
       //2 Numerator Number of exposed infant who received ARV prophylaxis
         AgeCohortDefinition at18monthsOfAge = Cohorts.createinfantUnder18months("under18months");
         List<Program> infantProgram = new ArrayList<Program>();
         infantProgram.add(exposedInfant);
         InProgramCohortDefinition inExposedInfant = Cohorts.createInProgramParameterizableByDate("exposedInfant",infantProgram, "onDate");
         SqlCohortDefinition startedCotrimoXazoleDuringP = Cohorts.getPatientsOnRegimenBasedOnStartDateEndDate("startedCotrimoXazoleDuringP", cotrimoxazole);
          
         CompositionCohortDefinition exposedinfantOnARV = new CompositionCohortDefinition();
         exposedinfantOnARV.setName("exposedinfantOnARV");
         exposedinfantOnARV.addParameter(new Parameter("startDate", "startDate", Date.class));
         exposedinfantOnARV.addParameter(new Parameter("endDate", "endDate", Date.class));
         exposedinfantOnARV.getSearches().put("1",new Mapped<CohortDefinition>(at18monthsOfAge, null));
         exposedinfantOnARV.getSearches().put("2",new Mapped<CohortDefinition>(inExposedInfant, ParameterizableUtil.createParameterMappings("onDate=${now}")));
         exposedinfantOnARV.getSearches().put("3",new Mapped<CohortDefinition>(startedCotrimoXazoleDuringP, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
         exposedinfantOnARV.setCompositionString("1 AND 2 AND 3");
         
         CohortIndicator exposedinfantOnARVInd = Indicators.newCountIndicator( "exposedinfantOnARVInd ", exposedinfantOnARV,
                 ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
         exposedinfantOnARVInd.addParameter(new Parameter("startDate","startDate",Date.class));
         exposedinfantOnARVInd.addParameter(new Parameter("endDate","endDate",Date.class));
         
         // 2 Denominator number of women who gave birth during the last quarter
         ProgramEnrollmentCohortDefinition inPMTCTCCMother = Cohorts.createProgramEnrollment("PMTCT CCMother", CCMotherProgram);
         inPMTCTCCMother.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter",Date.class)); 
         EncounterCohortDefinition womenTranferedToCC = Cohorts.createEncounterBasedOnForms("womenTranferedToCC",startDateEndDate, transferToCCForms);
         
         CompositionCohortDefinition womeninCCandGaveBirth = new CompositionCohortDefinition();
         womeninCCandGaveBirth.setName("womeninCCandGaveBirth");
         womeninCCandGaveBirth.addParameter(new Parameter("startDate", "startDate", Date.class));
         womeninCCandGaveBirth.addParameter(new Parameter("endDate", "endDate", Date.class));
         womeninCCandGaveBirth.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
         womeninCCandGaveBirth.getSearches().put("1",new Mapped<CohortDefinition>(inPMTCTCCMother, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter}")));
         womeninCCandGaveBirth.getSearches().put("2",new Mapped<CohortDefinition>(womenTranferedToCC, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
         womeninCCandGaveBirth.setCompositionString("1 OR 2");
         
         CohortIndicator womeninCCandGaveBirthInd = Indicators.newCountIndicator( "womeninCCandGaveBirthInd ", womeninCCandGaveBirth,
                 ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},startDate=${startDate},endDate=${endDate}"));
         womeninCCandGaveBirthInd.addParameter(new Parameter("startDate","startDate",Date.class));
         womeninCCandGaveBirthInd.addParameter(new Parameter("endDate","endDate",Date.class));
         womeninCCandGaveBirthInd.addParameter(new Parameter("enrolledOnOrAfter","enrolledOnOrAfter",Date.class));
         
     //3 exposedInfant on PCT between 6 and 8 weeks
         AgeCohortDefinition b6To8Weeks = new AgeCohortDefinition();
         b6To8Weeks.setName("b6To8Weeks");
         b6To8Weeks.setMinAge(6);
         b6To8Weeks.setMinAgeUnit(DurationUnit.WEEKS);
         b6To8Weeks.setMaxAge(8);
         b6To8Weeks.setMaxAgeUnit(DurationUnit.WEEKS);
         b6To8Weeks.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
         CodedObsCohortDefinition patientsWithPCR = Cohorts.createCodedObsCohortDefinition("patientsWithPCR",onOrAfterOnOrBefore, hivPCR, null, SetComparator.IN, TimeModifier.LAST);
         
         CompositionCohortDefinition infantOnPCR = new CompositionCohortDefinition();
         infantOnPCR.setName("infantOnPCR");
         infantOnPCR.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
         infantOnPCR.getSearches().put("1",new Mapped<CohortDefinition>(b6To8Weeks, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
         infantOnPCR.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithPCR, null));
         infantOnPCR.setCompositionString("1 AND 2");
         
         CohortIndicator infantOnPCRInd = Indicators.newCountIndicator( "exposedinfantOnARVInd ", infantOnPCR,ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}"));
         infantOnPCRInd.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
      
         //3 denominator:
        SqlCohortDefinition motherofExposedbabies=new SqlCohortDefinition();
        motherofExposedbabies.setName("motherofExposedbabies");
        motherofExposedbabies.setQuery("SELECT DISTINCT r.person_a FROM relationship r, patient_program pr " +
        		"where pr.patient_id=r.person_a and r.person_b in " +
        		"(select distinct pp.patient_id from patient_program pp, person p " +
        		"where pp.program_id="+exposedInfant.getProgramId()+" and p.person_id=pp.patient_id and pp.voided=0 " +
        		"and DATEDIFF(:startDate,p.birthdate) between 42 and 56) " +
        		"and relationship='13'and pr.program_id="+CCMotherProgram.getProgramId()+" and pr.voided=0 order by pr.date_enrolled desc");
        motherofExposedbabies.addParameter(new Parameter("startDate", "startDate", Date.class));
        
        CohortIndicator motherofExposedbabiesInd = Indicators.newCountIndicator( "motherofExposedbabiesInd ", motherofExposedbabies,ParameterizableUtil.createParameterMappings("startDate=${startDate}"));
        motherofExposedbabiesInd.addParameter(new Parameter("startDate","startDate",Date.class));
           
       // 4 Numerator on ARV
        CodedObsCohortDefinition exitedCareWithDeadStatus = Cohorts.createCodedObsCohortDefinition("exitedCareWithDeadStatus",onOrAfterOnOrBefore, reasonForExitingCare,patientDied, SetComparator.IN, TimeModifier.LAST);
        CodedObsCohortDefinition exitedCareWithtransferStatus = Cohorts.createCodedObsCohortDefinition("exitedCareWithtransferStatus",onOrAfterOnOrBefore, reasonForExitingCare,patientTransferedOut, SetComparator.IN, TimeModifier.LAST);
        List<ProgramWorkflowState> onARTstates = new ArrayList<ProgramWorkflowState>();
        onARTstates.add(adultOnART);
        onARTstates.add(pediOnART);
        onARTstates.add(pmtctArt);
        InStateCohortDefinition onArtatEndDatePeriod = Cohorts.createInCurrentState("TR: started on Art", onARTstates,onOrAfterOnOrBefore);
        
        //ltfu
        EncounterCohortDefinition clinicalEncWithLab = Cohorts.createEncounterParameterizedByDate("clinicalEncWithoutLab", onOrAfterOnOrBefore,clinicalEnountersIncLab);
        CompositionCohortDefinition notActives = new CompositionCohortDefinition();
        notActives.setName("notActives");
        notActives.getSearches().put("1", new Mapped<CohortDefinition>(exitedCareWithDeadStatus, null));
        notActives.getSearches().put("2",new Mapped<CohortDefinition>(exitedCareWithtransferStatus,null));
        notActives.setCompositionString("1 OR 2");
        
        CompositionCohortDefinition activePatientsOnArt = new CompositionCohortDefinition();
        activePatientsOnArt.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        //activePatientsOnArt.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        activePatientsOnArt.setName("activePatientsOnArt");
        activePatientsOnArt.getSearches().put("1", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        activePatientsOnArt.getSearches().put("2",new Mapped<CohortDefinition>(clinicalEncWithLab,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        activePatientsOnArt.getSearches().put("3",new Mapped<CohortDefinition>(notActives,null));
        activePatientsOnArt.setCompositionString("1 AND 2 AND (NOT 3)");
        
        CohortIndicator activePatientsOnArtInd = Indicators.newCountIndicator( "activePatientsOnArtInd ", activePatientsOnArt,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
        //activePatientsOnArtInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        activePatientsOnArtInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        
        //Denominator
        CompositionCohortDefinition allPatientsOnArt = new CompositionCohortDefinition();
        allPatientsOnArt.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        allPatientsOnArt.setName("allPatientsOnArt");
        allPatientsOnArt.getSearches().put("1", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        allPatientsOnArt.getSearches().put("2",new Mapped<CohortDefinition>(exitedCareWithtransferStatus,null));
        allPatientsOnArt.setCompositionString("1 AND (NOT 2)");
        
        CohortIndicator allPatientsOnArtInd = Indicators.newCountIndicator( "allPatientsOnArtInd ", allPatientsOnArt,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
        allPatientsOnArtInd.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
       
        // 5 numerator + denominator same as above just changed the date param to 24 months
        // 6 numerator + denominator same as above just changed the date param to 36 months
        // 7 Numerator:
        List<ProgramWorkflowState> OnFollowingstates = new ArrayList<ProgramWorkflowState>();
        OnFollowingstates.add(adultOnFollowing);
        OnFollowingstates.add(pediOnFollowing);
        InStateCohortDefinition onFollowingEndDatePeriod = Cohorts.createInCurrentState("TR: started on Art", onARTstates,onOrAfterOnOrBefore);
        
        NumericObsCohortDefinition weightCohort = Cohorts.createNumericObsCohortDefinition("heightCohortDefinition","onOrAfter", weight, new Double(0), null, TimeModifier.LAST);
        CompositionCohortDefinition patwithWeightForLast6m = new CompositionCohortDefinition();
        patwithWeightForLast6m.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patwithWeightForLast6m.setName("patwithWeightForLast6m");
        patwithWeightForLast6m.getSearches().put("1", new Mapped<CohortDefinition>(onFollowingEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patwithWeightForLast6m.getSearches().put("2", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patwithWeightForLast6m.getSearches().put("3",new Mapped<CohortDefinition>(weightCohort,null));
        patwithWeightForLast6m.setCompositionString("(1 OR 2) AND 3");
        CohortIndicator patwithWeightForLast6mInd = Indicators.newCountIndicator( "patwithHeightForLast6mInd ", patwithWeightForLast6m,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
        patwithWeightForLast6mInd.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        
        // 8
        CodedObsCohortDefinition screenedForSTI = Cohorts.createCodedObsCohortDefinition("screenedForSTI",onOrAfterOnOrBefore, sti, null, SetComparator.IN, TimeModifier.LAST);
        CompositionCohortDefinition patientsScreendForIST6m = new CompositionCohortDefinition();
        patientsScreendForIST6m.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientsScreendForIST6m.setName("patientsScreendForIST6m");
        patientsScreendForIST6m.getSearches().put("1", new Mapped<CohortDefinition>(onFollowingEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patientsScreendForIST6m.getSearches().put("2", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patientsScreendForIST6m.getSearches().put("3",new Mapped<CohortDefinition>(screenedForSTI,null));
        patientsScreendForIST6m.setCompositionString("(1 OR 2) AND 3");
        CohortIndicator patientsScreendForIST6mInd = Indicators.newCountIndicator( "patientsScreendForIST6mInd ", patientsScreendForIST6m,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
        patientsScreendForIST6mInd.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        
        //9
        CodedObsCohortDefinition screenedForTB = Cohorts.createCodedObsCohortDefinition("screenedForSTI",onOrAfterOnOrBefore, tbScreeningtest, null, SetComparator.IN, TimeModifier.LAST);
        CompositionCohortDefinition patientsScreendForTB6m = new CompositionCohortDefinition();
        patientsScreendForTB6m.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientsScreendForTB6m.setName("patientsScreendForTB6m");
        patientsScreendForTB6m.getSearches().put("1", new Mapped<CohortDefinition>(onFollowingEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patientsScreendForTB6m.getSearches().put("2", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patientsScreendForTB6m.getSearches().put("3",new Mapped<CohortDefinition>(screenedForTB,null));
        patientsScreendForTB6m.setCompositionString("(1 OR 2) AND 3");
        CohortIndicator patientsScreendForTB6mInd = Indicators.newCountIndicator( "patientsScreendForTB6mInd ", patientsScreendForTB6m,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
        patientsScreendForTB6mInd.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        
        //10
        CompositionCohortDefinition patientForCotrimo6m = new CompositionCohortDefinition();
        patientForCotrimo6m.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientForCotrimo6m.addParameter(new Parameter("startDate","startDate",Date.class));
        patientForCotrimo6m.addParameter(new Parameter("endDate","endDate",Date.class));
        patientForCotrimo6m.setName("patientForCotrimo6m");
        patientForCotrimo6m.getSearches().put("1", new Mapped<CohortDefinition>(onFollowingEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patientForCotrimo6m.getSearches().put("2", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patientForCotrimo6m.getSearches().put("3",new Mapped<CohortDefinition>(startedCotrimoXazoleDuringP, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        patientForCotrimo6m.setCompositionString("(1 OR 2) AND 3");
        CohortIndicator patientForCotrimo6mInd = Indicators.newCountIndicator( "patientForCotrimo6mInd ", patientForCotrimo6m,
        	ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
        patientForCotrimo6mInd.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patientForCotrimo6mInd.addParameter(new Parameter("startDate","startDate",Date.class));
        patientForCotrimo6mInd.addParameter(new Parameter("endDate","endDate",Date.class));
        
        //11
        NumericObsCohortDefinition cd4Cohort = Cohorts.createNumericObsCohortDefinition("heightCohortDefinition","onOrAfter", cd4, new Double(0), null, TimeModifier.LAST);
        CompositionCohortDefinition patwitCd4ForLast6m = new CompositionCohortDefinition();
        patwitCd4ForLast6m.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        patwitCd4ForLast6m.setName("patwitCd4ForLast6m");
        patwitCd4ForLast6m.getSearches().put("1", new Mapped<CohortDefinition>(onFollowingEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patwitCd4ForLast6m.getSearches().put("2", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
        patwitCd4ForLast6m.getSearches().put("3",new Mapped<CohortDefinition>(cd4Cohort,null));
        patwitCd4ForLast6m.setCompositionString("(1 OR 2) AND 3");
        CohortIndicator patwitCd4ForLast6mInd = Indicators.newCountIndicator( "patwitCd4ForLast6mInd ", patwitCd4ForLast6m,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}"));
        patwitCd4ForLast6mInd.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
        
        //12
        AgeCohortDefinition patientWithUnder5=Cohorts.createUnderAgeCohort("patientWithUnder5",5);
        SqlCohortDefinition pediOnArtJustAfterEnrol=Cohorts.getPediOnArtIn2Weeks("pediOnArtIn2Weeks", pediatrichivProgram);
        
        CompositionCohortDefinition pediOnArtIn2Weeks = new CompositionCohortDefinition();
        pediOnArtIn2Weeks.setName("pediOnArtIn2Weeks");
        pediOnArtIn2Weeks.getSearches().put("1", new Mapped<CohortDefinition>(patientWithUnder5, null));
        pediOnArtIn2Weeks.getSearches().put("2", new Mapped<CohortDefinition>(pediOnArtJustAfterEnrol, null));
        pediOnArtIn2Weeks.setCompositionString("(1 AND 2");
        CohortIndicator pediOnArtIn2WeeksInd = Indicators.newCountIndicator( "pediOnArtIn2WeeksInd ", pediOnArtIn2Weeks,null);
       
        //13
        InStateCohortDefinition artCurrently = Cohorts.createInCurrentState("hivQD: currently on ART",onARTstates);
        NumericObsCohortDefinition viralLoad = Cohorts.createNumericObsCohortDefinition("obsQD: Viral Load recorded",
    		    onOrAfterOnOrBefore, viralLoadConcept, 0, null, TimeModifier.LAST);
        
        CompositionCohortDefinition eligibleForViralLoad = new CompositionCohortDefinition();
		eligibleForViralLoad.setName("hivQD: In all programs");
		eligibleForViralLoad.addParameter(new Parameter("onDate", "onDate", Date.class));
		eligibleForViralLoad.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		eligibleForViralLoad.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		eligibleForViralLoad.getSearches().put("1",new Mapped(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter}")));
		eligibleForViralLoad.getSearches().put("2",new Mapped(artCurrently, ParameterizableUtil.createParameterMappings("onDate=${now}")));
		eligibleForViralLoad.getSearches().put("3",new Mapped(viralLoad,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
		eligibleForViralLoad.setCompositionString("1 AND 2 AND 3");
		CohortIndicator eligibleForViralLoadInd = Indicators.newCountIndicator( "eligibleForViralLoadInd ", 
				eligibleForViralLoad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		eligibleForViralLoadInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		eligibleForViralLoadInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		 
       //14
		CohortIndicator patientsOnArtInd = Indicators.newCountIndicator( "patientOnArtInd ", artCurrently, 
				ParameterizableUtil.createParameterMappings("onDate=${now}"));
		//15
		SqlCohortDefinition patientsOnArt3MonthsAtProgEnrol=Cohorts.getPatientsOnArtIn3Months("adultsOnArt3MonthsAtProgEnrol", hivPrograms);
		CohortIndicator patientsOnArtin3MonthsInd = Indicators.newCountIndicator( "patientsOnArtin3MonthsInd ", patientsOnArt3MonthsAtProgEnrol,null);
        
           //Add global filters to the report
           //PRE-ART DATA ELEMENT
           dsd.addColumn("1a","Pregnant Women from the previous quarter",new Mapped(onePMTCTPreg,ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate-3m}")), "");
           dsd.addColumn("1b","ART Pregnant Women from the previous quarter",new Mapped(oneArtPMTCTPreg,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m}")), "");
           dsd.addColumn("2a","Exposed Infant on ARV from the previous quarter",new Mapped(exposedinfantOnARVInd,ParameterizableUtil.createParameterMappings("startDate=${startDate-3m},endDate=${endDate}")), "");
           dsd.addColumn("2b","ART Pregnant Women from the previous quarter",new Mapped(womeninCCandGaveBirthInd,ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate-3m},startDate=${startDate-3m},endDate=${endDate}")), "");
           dsd.addColumn("3a","Exposed Infant tested for PCR from previous quarter",new Mapped(infantOnPCRInd,ParameterizableUtil.createParameterMappings("effectiveDate=${startDate-3m}")), "");
           dsd.addColumn("3b","Pregnant women who gave birth (child between 6-8 weeks) in the previous quarter",new Mapped(motherofExposedbabiesInd,ParameterizableUtil.createParameterMappings("startDate=${startDate-3m}")), "");
           dsd.addColumn("4a","Active Patients who initiated ARV in the last 12 months",new Mapped(activePatientsOnArtInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-12m}")), "");
           dsd.addColumn("4b","Patients who initiated ARV from the last 12 months",new Mapped(allPatientsOnArtInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-12m}")), "");
           dsd.addColumn("5a","Active Patients who initiated ARV in the last 24 months",new Mapped(activePatientsOnArtInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-24m}")), "");
           dsd.addColumn("5b","Patients who initiated ARV from the last 24 months",new Mapped(allPatientsOnArtInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-24m}")), "");
           dsd.addColumn("6a","Active Patients who initiated ARV in the last 36 months",new Mapped(activePatientsOnArtInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-36m}")), "");
           dsd.addColumn("6b","Patients who initiated ARV from the last 36 months",new Mapped(allPatientsOnArtInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-36m}")), "");
           dsd.addColumn("7","Patients in care with weight the last 6 months",new Mapped(patwithWeightForLast6mInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-6m}")), "");
           dsd.addColumn("8","Patients screened for STI in the last 6 months",new Mapped(patientsScreendForIST6mInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-6m}")), "");
           dsd.addColumn("9","Patients screened for TB in the last 6 months",new Mapped(patientsScreendForTB6mInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-6m}")), "");
           dsd.addColumn("10","Patients on cotrimoxazole TB in the last 6 months",new Mapped(patientForCotrimo6mInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-6m},startDate=${startDate-6m},endDate=${endDate}")), "");
           dsd.addColumn("11","Patients in care with CD4 the last 6 months",new Mapped(patwitCd4ForLast6mInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-6m}")), "");
           dsd.addColumn("12","Pediatric patient under 5, who started ART 2 weeks after their HIV confirmation", new Mapped(pediOnArtIn2WeeksInd,null), "");
           dsd.addColumn("13","Patients on ART eligible for VL",new Mapped(eligibleForViralLoadInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-1y},onOrBefore=${startDate}")), "");
           dsd.addColumn("14","Patients on Art", new Mapped(patientsOnArtInd,null), "");
           dsd.addColumn("15","Patient on started ART 3 months after program enrolment", new Mapped(patientsOnArtin3MonthsInd,null), "");
           
           return dsd;     
        }
        
        private void setupProperties() {
            pmtctPregnancyProgram = gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
            CCMotherProgram= gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
            pediatrichivProgram = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            adulthivProgram=gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            exposedInfant=gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_CLINIC_PROGRAM);
            Artpregnant = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,
            		GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
            ArtpregnantState.add(Artpregnant);
            hivPrograms.add(pediatrichivProgram);
            hivPrograms.add(adulthivProgram);
            onOrAfterOnOrBefore.add("onOrAfter");
            onOrAfterOnOrBefore.add("onOrBefore");
            startDateEndDate.add("startDate");
            startDateEndDate.add("endDate");
            cotrimoxazole = gp.getConcept(GlobalPropertiesManagement.COTRIMOXAZOLE_DRUG);
            tranferToCC=gp.getForm(GlobalPropertiesManagement.TRANSFER_TO_CC);
            transferToCCForms.add(tranferToCC);
            hivPCR=gp.getConcept(GlobalPropertiesManagement.DBS_CONCEPT);
            sti = gp.getConcept(GlobalPropertiesManagement.STI);
            reasonForExitingCare= gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
            patientDied = gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
            patientTransferedOut=gp.getConcept(GlobalPropertiesManagement.TRASNFERED_OUT);
            adultOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            pediOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            adultOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            pediOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            clinicalEnountersIncLab = gp.getEncounterTypeList(GlobalPropertiesManagement.CLINICAL_ENCOUNTER_TYPES);
            weight = gp.getConcept(GlobalPropertiesManagement.WEIGHT_CONCEPT);
            cd4=gp.getConcept(GlobalPropertiesManagement.CD4_TEST);
            tbScreeningtest=gp.getConcept(GlobalPropertiesManagement.TB_SCREENING_TEST);
            viralLoadConcept = gp.getConcept(GlobalPropertiesManagement.VIRAL_LOAD_TEST);
            pmtctArt = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,
        		    GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW, GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
           
           
            
           
        }
        
}