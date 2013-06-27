package org.openmrs.module.rwandareports.reporting;
 
import java.util.ArrayList;
import java.util.Date;
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
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.util.Indicators;
import org.openmrs.module.rwandareports.util.MetadataLookup;
//import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;
 
public class SetupTracNetRwandaReportBySite {
        
        protected final static Log log = LogFactory.getLog(SetupTracNetRwandaReportBySite.class);
        
        Helper h = new Helper();
        
        GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
        //MetadataLookup mlookup=new MetadataLookup();
     // properties
        private Program adulthivProgram;
        private Program pediatrichivProgram;
        private Program pmtctcombinedMother;
        private Program pmtctPregnancyProgram;
        private ProgramWorkflowState adultOnFollowing;
        private ProgramWorkflowState pediOnFollowing;
        private ProgramWorkflowState adultOnART;
        private ProgramWorkflowState pediOnART;
        private ProgramWorkflowState pmtctOnART;
        private ProgramWorkflowState pediPreAndArtDiedState;
        private ProgramWorkflowState adultPreAndArtDiedState;
        private ProgramWorkflowState adulttransferedOutState;
        private ProgramWorkflowState peditransferedOutState;
        private Concept tbScreeningtest;
        private Concept positiveStatus;
        private Concept reasonForExitingCare;
        private Concept patientDied;
        private Concept patientTransferedOut;
        private Concept patientDefaulted;
        private Form adultHivForm;
        private Form pediHivform;
        private Form transferinForm;
        private List<Form> hivVisitsforms = new ArrayList<Form>();
        private Form allergypediForm;
        private Form allergyadultForm;
        private List<Form> medicationForms = new ArrayList<Form>();
        private List<String> onOrAfterOnOrBefore = new ArrayList<String>();
        private List<String> onOrAfterDateOnOrBeforeDate = new ArrayList<String>();
        private List<EncounterType> clinicalEnountersIncLab;
        private List<EncounterType> pediAdnAdultEncounters;
        private EncounterType patientTransferEncounterType;
        private Concept kaletra;
        private Concept cotrimoxazole;
        private Concept fluconazole;
        private Concept dapsone;
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
       
        public void setup() throws Exception {
                
                setupProperties();
                
                ReportDefinition rd = createReportDefinition();
                ReportDesign design = h.createRowPerPatientXlsOverviewReportDesign(rd, "Mohrwandatracnetreporttemplate.xls",
                    "Xlstracnetreporttemplate", null);
                Properties props = new Properties();
                props.put("repeatingSections", "sheet:1,dataset:TracNet Report Location");
                props.put("sortWeight","5000");
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
         SqlCohortDefinition under15inDays=new SqlCohortDefinition();
         under15inDays.setName("under15inDays");
         under15inDays.setQuery("SELECT distinct pe.person_id from person pe WHERE DATEDIFF(:dateborn, pe.birthdate) < 5479 and pe.dead=0 and pe.voided=0 ");
         under15inDays.addParameter(new Parameter("dateborn", "dateborn", Date.class));
         
         SqlCohortDefinition over15inDays=new SqlCohortDefinition();
         over15inDays.setName("over15inDays");
         over15inDays.setQuery("SELECT distinct pe.person_id from person pe WHERE DATEDIFF(:dateborn, pe.birthdate) >= 5479 and pe.dead=0 and pe.voided=0 ");
         over15inDays.addParameter(new Parameter("dateborn", "dateborn", Date.class));
 		 
         AgeCohortDefinition at18monthsOfAge = Cohorts.createUnder18monthsCohort("TR:at18monthsOfAge");
         AgeCohortDefinition underFive = new AgeCohortDefinition(null, 4, null);
         
         //Program Cohorts
         //PMTCT Combined Mother
         List<Program> PmtctCombinrMotherProgram = new ArrayList<Program>();
         PmtctCombinrMotherProgram.add(pmtctcombinedMother);
         InProgramCohortDefinition inPmtctMotherprogram = Cohorts.createInProgramParameterizableByDate("TR:inPmtctMotherprogram",PmtctCombinrMotherProgram, "onDate");
         //HIV programs
         List<Program> hivPrograms = new ArrayList<Program>();
         hivPrograms.add(adulthivProgram);
         hivPrograms.add(pediatrichivProgram);
         InProgramCohortDefinition inPediAndAdultprogram = Cohorts.createInProgramParameterizableByDate("TR:inPediAndAdult",hivPrograms, "onDate");
         SqlCohortDefinition onARTDrugs = Cohorts.getArtDrugs("TR:On Art Drugs ever");
         // PMTCT Pregnancy Program
         List<Program> PmtctPregnancyProgram = new ArrayList<Program>();
         PmtctPregnancyProgram.add(pmtctPregnancyProgram);
         InProgramCohortDefinition inPmtctPregnancyprogram = Cohorts.createInProgramParameterizableByDate("TR:inPmtctPregnancyprogram",PmtctPregnancyProgram, "onDate");
         
         ProgramEnrollmentCohortDefinition patientEnrolledInPediAndAdultProgram = new ProgramEnrollmentCohortDefinition();
         patientEnrolledInPediAndAdultProgram.addParameter(new Parameter("enrolledOnOrBefore", "enrolledOnOrBefore", Date.class));
         patientEnrolledInPediAndAdultProgram.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
         patientEnrolledInPediAndAdultProgram.setPrograms(hivPrograms);
         
          ProgramEnrollmentCohortDefinition patientEnrolledInPMTCTProgram = new ProgramEnrollmentCohortDefinition();
          patientEnrolledInPMTCTProgram.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
          patientEnrolledInPMTCTProgram.setPrograms(PmtctCombinrMotherProgram);
         
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
          CodedObsCohortDefinition exitedCareWithtransferStatus = Cohorts.createCodedObsCohortDefinition("exitedCareWithtransferStatus",onOrAfterOnOrBefore, reasonForExitingCare,patientTransferedOut, SetComparator.IN, TimeModifier.LAST);
          CodedObsCohortDefinition exitedCareWithDefaultedStatus = Cohorts.createCodedObsCohortDefinition("exitedCareWithDefaultedStatus",onOrAfterOnOrBefore, reasonForExitingCare,patientDefaulted, SetComparator.IN, TimeModifier.LAST);
          
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
          EncounterCohortDefinition patientTransferEncounter = Cohorts.createEncounterParameterizedByDate("patientTransferEncounter", onOrAfterOnOrBefore,patientTransferEncounterType);
                       
          //------------------------------
          //     PRE-ART START
          //------------------------------ 
                 
          //1 Total number of new pediatric patients (age <=18 months at enrolment) enrolled in HIV care this month  
          SqlCohortDefinition under18monthsAtEnrol = Cohorts.createUnder18monthsAtEnrollmentCohort("under18monthsAtEnrol", pediatrichivProgram);
          SqlCohortDefinition under5YrsAtEnrol = Cohorts.createUnder5AtEnrollmentCohort("under5YrsAtEnrol", pediatrichivProgram);
          SqlCohortDefinition under15YrsAtEnrol = Cohorts.createUnder15AtEnrollmentCohort("under15YrsAtEnrol", pediatrichivProgram);
          SqlCohortDefinition over15YrsAtEnrol = Cohorts.create15orOverAtEnrollmentCohort("over15YrsAtEnrol", adulthivProgram);
          SqlCohortDefinition enrolledInAdultProgram = Cohorts.createPatientInProgramDuringTime("Enrolled In adult HIV Program");
          
          
          CompositionCohortDefinition preArtduringPeriod = new CompositionCohortDefinition();
          preArtduringPeriod.addParameter(new Parameter("startDate","startDate",Date.class));
          preArtduringPeriod.addParameter(new Parameter("endDate","endDate",Date.class));
          preArtduringPeriod.setName("TR:preArtduringPeriod");
          preArtduringPeriod.getSearches().put("1",new Mapped<CohortDefinition>(onFollowingStateCohort,ParameterizableUtil.createParameterMappings("onDate=${now}")));
          preArtduringPeriod.getSearches().put("2",new Mapped<CohortDefinition>(enrolledInAdultProgram, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          preArtduringPeriod.setCompositionString("1 AND 2");
                  
          CompositionCohortDefinition newlyEnrolledInHIVCareunder18months = new CompositionCohortDefinition();
          newlyEnrolledInHIVCareunder18months.addParameter(new Parameter("startDate","startDate",Date.class));
          newlyEnrolledInHIVCareunder18months.addParameter(new Parameter("endDate","endDate",Date.class));
          newlyEnrolledInHIVCareunder18months.setName("TR:newlyEnrolledInHIVCareunder18months");
          newlyEnrolledInHIVCareunder18months.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          newlyEnrolledInHIVCareunder18months.getSearches().put("2",new Mapped<CohortDefinition>(under18monthsAtEnrol,null));
          newlyEnrolledInHIVCareunder18months.setCompositionString("1 AND 2 ");
          CohortIndicator newlyEnrolledInHIVCareunder18monthsInd=Indicators.newCohortIndicator("TR:enrolledInAdultProgramInd",
        		  newlyEnrolledInHIVCareunder18months,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
         
          
         //2 Total number of new pediatric patients (age <5 years) enrolled in HIV care this month   
          CompositionCohortDefinition newlyEnrolledInHIVCareunder5yrs = new CompositionCohortDefinition();
          newlyEnrolledInHIVCareunder5yrs.addParameter(new Parameter("startDate","startDate",Date.class));
          newlyEnrolledInHIVCareunder5yrs.addParameter(new Parameter("endDate","endDate",Date.class));
          newlyEnrolledInHIVCareunder5yrs.setName("TR:newlyEnrolledInHIVCareunder5yrs");
          newlyEnrolledInHIVCareunder5yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          newlyEnrolledInHIVCareunder5yrs.getSearches().put("2",new Mapped<CohortDefinition>(under5YrsAtEnrol,null));
          newlyEnrolledInHIVCareunder5yrs.setCompositionString("1 AND 2 ");
          CohortIndicator newlyEnrolledInHIVCareunder5yrsInd=Indicators.newCohortIndicator("TR:newlyEnrolledInHIVCareunder5yrsInd", newlyEnrolledInHIVCareunder5yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //3 Total number of new female pediatric patients (age < 15 years) enrolled in HIV care
          CompositionCohortDefinition nonActivePatients=new CompositionCohortDefinition();
          nonActivePatients.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
          nonActivePatients.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          nonActivePatients.addParameter(new Parameter("endDate","endDate",Date.class));
          nonActivePatients.setName("TR:nonActivePatients");
          nonActivePatients.getSearches().put("1", new Mapped<CohortDefinition>(exitedCareWithDeadStatus,null));
          nonActivePatients.getSearches().put("2", new Mapped<CohortDefinition>(exitedCareWithtransferStatus,null));
          nonActivePatients.getSearches().put("3", new Mapped<CohortDefinition>(exitedCareWithDefaultedStatus,null));
          nonActivePatients.setCompositionString("1 OR 2 OR 3");
          
          CompositionCohortDefinition femalenewlyEnrolledInHIVCareunder15yrs = new CompositionCohortDefinition();
          femalenewlyEnrolledInHIVCareunder15yrs.setName("femalenewlyEnrolledInHIVCareunder15yrs");
          femalenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("startDate","startDate",Date.class));
          femalenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("endDate","endDate",Date.class));
          //femalenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
          //femalenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
          femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
          femalenewlyEnrolledInHIVCareunder15yrs.getSearches().put("4",new Mapped<CohortDefinition>(nonActivePatients,null));
          femalenewlyEnrolledInHIVCareunder15yrs.setCompositionString("1 AND 2 AND 3 AND (NOT 4) ");
          CohortIndicator femalesnewlyEnrolledInHIVCareunder15yrsInd=Indicators.newCohortIndicator("TR:femalesnewlyEnrolledInHIVCareunder15yrsInd", femalenewlyEnrolledInHIVCareunder15yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //4 Total number of new male pediatric patients (age < 15 years at enrollment) enrolled in HIV care   
          CompositionCohortDefinition malenewlyEnrolledInHIVCareunder15yrs = new CompositionCohortDefinition();
          malenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("startDate","startDate",Date.class));
          malenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("endDate","endDate",Date.class));
          malenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("onOrAfter","onOrAfter",Date.class));
          malenewlyEnrolledInHIVCareunder15yrs.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          malenewlyEnrolledInHIVCareunder15yrs.setName("MalenewlyEnrolledInHIVCareunder15yrs");
          malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
          malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
          malenewlyEnrolledInHIVCareunder15yrs.getSearches().put("4",new Mapped<CohortDefinition>(nonActivePatients,null));
          malenewlyEnrolledInHIVCareunder15yrs.setCompositionString("1 AND 2 AND 3 AND (NOT 4) ");
          CohortIndicator malesnewlyEnrolledInHIVCareunder15yrsInd=Indicators.newCohortIndicator("TR:malesnewlyEnrolledInHIVCareunder15yrsInd", malenewlyEnrolledInHIVCareunder15yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
           
          //5 Total number of new female adult patients (age 15 or more at enrollment) enrolled in HIV care                
          CompositionCohortDefinition femalenewlyEnrolledInHIVCareOver15yrs = new CompositionCohortDefinition();
          femalenewlyEnrolledInHIVCareOver15yrs.addParameter(new Parameter("startDate","startDate",Date.class));
          femalenewlyEnrolledInHIVCareOver15yrs.addParameter(new Parameter("endDate","endDate",Date.class));
          femalenewlyEnrolledInHIVCareOver15yrs.setName("femalenewlyEnrolledInHIVCareOver15yrs");
          femalenewlyEnrolledInHIVCareOver15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          femalenewlyEnrolledInHIVCareOver15yrs.getSearches().put("2",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
          femalenewlyEnrolledInHIVCareOver15yrs.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
          femalenewlyEnrolledInHIVCareOver15yrs.setCompositionString("1 AND 2 AND 3 ");
          CohortIndicator femalesnewlyEnrolledInHIVCareOver15yrsInd=Indicators.newCohortIndicator("TR:femalesnewlyEnrolledInHIVCareOver15yrsInd", femalenewlyEnrolledInHIVCareOver15yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //6 Total number of new male adult patients (age 15 or more) enrolled in HIV care 
          CompositionCohortDefinition malenewlyEnrolledInHIVCareOver15yrs = new CompositionCohortDefinition();
          malenewlyEnrolledInHIVCareOver15yrs.addParameter(new Parameter("startDate","startDate",Date.class));
          malenewlyEnrolledInHIVCareOver15yrs.addParameter(new Parameter("endDate","endDate",Date.class));
          malenewlyEnrolledInHIVCareOver15yrs.setName("malenewlyEnrolledInHIVCareOver15yrs");
          malenewlyEnrolledInHIVCareOver15yrs.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          malenewlyEnrolledInHIVCareOver15yrs.getSearches().put("2",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
          malenewlyEnrolledInHIVCareOver15yrs.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
          malenewlyEnrolledInHIVCareOver15yrs.setCompositionString("1 AND 2 AND 3 ");
          CohortIndicator malesnewlyEnrolledInHIVCareOver15yrsInd=Indicators.newCohortIndicator("TR:malesnewlyEnrolledInHIVCareOver15yrsInd", malenewlyEnrolledInHIVCareOver15yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //7 Total Number of pediatric patient (age< 15 years) currently in Pre-ART
          PatientStateCohortDefinition onFollowingEver = Cohorts.createPatientStateEverCohortDefinition("onFollowingEver", OnFollowingstates);      
          CompositionCohortDefinition everOnPreArtduringPeriod = new CompositionCohortDefinition();
          everOnPreArtduringPeriod.addParameter(new Parameter("endDate","endDate",Date.class));
          everOnPreArtduringPeriod.setName("TR:everOnPreArtduringPeriod");
          everOnPreArtduringPeriod.getSearches().put("1",new Mapped<CohortDefinition>(enrolledInAdultProgram, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          everOnPreArtduringPeriod.getSearches().put("2",new Mapped<CohortDefinition>(onFollowingEver, null));
          everOnPreArtduringPeriod.setCompositionString("1 AND 2");
           
          CompositionCohortDefinition pedienrolledInHIVCareUnder15 = new CompositionCohortDefinition();
          pedienrolledInHIVCareUnder15.addParameter(new Parameter("endDate","endDate",Date.class));
          pedienrolledInHIVCareUnder15.setName("TR:pedienrolledInHIVCareUnder15");
          pedienrolledInHIVCareUnder15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          pedienrolledInHIVCareUnder15.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
          pedienrolledInHIVCareUnder15.setCompositionString("1 AND 2");
          CohortIndicator under15InHIVcareInAllProgram=Indicators.newCohortIndicator("TR:under15InHIVcareInAllProgram", pedienrolledInHIVCareUnder15, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
         
          //8 Total Number of adult patient (age 15+ ) currently in Pre-ART		
          CompositionCohortDefinition pedienrolledInHIVCareOver15 = new CompositionCohortDefinition();
          pedienrolledInHIVCareOver15.addParameter(new Parameter("endDate","endDate",Date.class));
          pedienrolledInHIVCareOver15.setName("TR:pedienrolledInHIVCareOver15");
          pedienrolledInHIVCareOver15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          pedienrolledInHIVCareOver15.getSearches().put("2",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
          pedienrolledInHIVCareOver15.setCompositionString("1 AND 2");
          CohortIndicator pedienrolledInHIVCareOver15Indi=Indicators.newCohortIndicator("TR:pedienrolledInHIVCareOver15Indi", pedienrolledInHIVCareOver15, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
         
          
          //9 Total number of female adult patient (age 15+) curently in Pre-ART   
          CompositionCohortDefinition enrolledInHIVCareFemaleOver15 = new CompositionCohortDefinition();
          enrolledInHIVCareFemaleOver15.addParameter(new Parameter("endDate","endDate",Date.class));
          enrolledInHIVCareFemaleOver15.setName("TR:enrolledInHIVCareFemaleOver15");
          enrolledInHIVCareFemaleOver15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod, ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          enrolledInHIVCareFemaleOver15.getSearches().put("2",new Mapped<CohortDefinition>(femaleCohort, null));
          enrolledInHIVCareFemaleOver15.getSearches().put("3",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
          enrolledInHIVCareFemaleOver15.setCompositionString("1 AND 2 AND 3");
          CohortIndicator Over15FemalenHIVcareInd=Indicators.newCohortIndicator("TR:Over15FemalenHIVcareInd", enrolledInHIVCareFemaleOver15,ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
          
          //10 Total number of male adult patient (age 15+) curently in Pre-ART 
          CompositionCohortDefinition enrolledInHIVCareFemaleUnde15 = new CompositionCohortDefinition();
          enrolledInHIVCareFemaleUnde15.addParameter(new Parameter("endDate","endDate",Date.class));
          enrolledInHIVCareFemaleUnde15.setName("TR:enrolledInHIVCareFemaleUnde15");
          enrolledInHIVCareFemaleUnde15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          enrolledInHIVCareFemaleUnde15.getSearches().put("2",new Mapped<CohortDefinition>(femaleCohort, null));
          enrolledInHIVCareFemaleUnde15.getSearches().put("3",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
          enrolledInHIVCareFemaleUnde15.setCompositionString("1 AND 2 AND 3");
          CohortIndicator under15FemaleInHIVcareInAllProgram=Indicators.newCohortIndicator("TR:under15FemaleInHIVcareInAllProgram", enrolledInHIVCareFemaleUnde15, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));;
          
          //11 Total number of male adult patient (age 15+) curently in Pre-ART   
          CompositionCohortDefinition enrolledInHIVCareMaleOver15 = new CompositionCohortDefinition();
          enrolledInHIVCareMaleOver15.addParameter(new Parameter("endDate","endDate",Date.class));
          enrolledInHIVCareMaleOver15.setName("TR:enrolledInHIVCareMaleOver15");
          enrolledInHIVCareMaleOver15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          enrolledInHIVCareMaleOver15.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort, null));
          enrolledInHIVCareMaleOver15.getSearches().put("3",new Mapped<CohortDefinition>(over15YrsAtEnrol,null));
          enrolledInHIVCareMaleOver15.setCompositionString("1 AND 2 AND 3");
          CohortIndicator Over15MalenHIVcareInd=Indicators.newCohortIndicator("TR:Over15MalenHIVcareInd", enrolledInHIVCareMaleOver15,ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
          
          // 12 Total number of female pediatric patients (age <15 years) ever enrolled in HIV care 
          CompositionCohortDefinition enrolledInHIVCareMaleUnde15 = new CompositionCohortDefinition();
          enrolledInHIVCareMaleUnde15.addParameter(new Parameter("endDate","endDate",Date.class));
          enrolledInHIVCareMaleUnde15.setName("TR:enrolledInHIVCareMaleUnde15");
          enrolledInHIVCareMaleUnde15.getSearches().put("1",new Mapped<CohortDefinition>(everOnPreArtduringPeriod,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          enrolledInHIVCareMaleUnde15.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort, null));
          enrolledInHIVCareMaleUnde15.getSearches().put("3",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
          enrolledInHIVCareMaleUnde15.setCompositionString("1 AND 2 AND 3");
          CohortIndicator under15MalenHIVcare=Indicators.newCohortIndicator("TR:under15MalenHIVcare", enrolledInHIVCareMaleUnde15,ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
                      
          //13 Number of patients on Cotrimoxazole Prophylaxis this month
           SqlCohortDefinition startedCotrimoXazoleDuringP = Cohorts.getPatientsCotrimoRegimenBasedOnStartDateEndDate("startedCotrimoXazoleDuringP", cotrimoxazole);
           SqlCohortDefinition testedFluconazole = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("testedFluconazole", fluconazole);
           SqlCohortDefinition testedDapsone = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("testedDapsone", dapsone);
           CompositionCohortDefinition testedFluconazoleAndDapsone = new CompositionCohortDefinition();
           testedFluconazoleAndDapsone.setName("testedFluconazoleAndDapsone");
           testedFluconazoleAndDapsone.addParameter(new Parameter("endDate", "endDate", Date.class));
           testedFluconazoleAndDapsone.getSearches().put("1",new Mapped<CohortDefinition>(testedFluconazole,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
           testedFluconazoleAndDapsone.getSearches().put("2", new Mapped<CohortDefinition>(testedDapsone,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
           testedFluconazoleAndDapsone.setCompositionString("1 OR 2");
           
           CompositionCohortDefinition preArtAndARTOnCotrimoComposition = new CompositionCohortDefinition();
           preArtAndARTOnCotrimoComposition.setName("preArtAndARTOnCotrimoComposition");
           preArtAndARTOnCotrimoComposition.addParameter(new Parameter("endDate", "endDate", Date.class));
           preArtAndARTOnCotrimoComposition.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
           preArtAndARTOnCotrimoComposition.getSearches().put("2", new Mapped<CohortDefinition>(testedFluconazoleAndDapsone,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
           preArtAndARTOnCotrimoComposition.setCompositionString("1 AND 2");
           
           CompositionCohortDefinition allARTpatientsOnPreArt = new CompositionCohortDefinition();
           allARTpatientsOnPreArt.setName("allARTpatientsOnPreArt");
           allARTpatientsOnPreArt.addParameter(new Parameter("endDate", "endDate", Date.class));
           allARTpatientsOnPreArt.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
           allARTpatientsOnPreArt.getSearches().put("2", new Mapped<CohortDefinition>(preArtAndARTOnCotrimoComposition,null));
           allARTpatientsOnPreArt.setCompositionString("1 AND (NOT 2)");
         
           CompositionCohortDefinition patientsInHIVonCotrimoOrBactrim = new CompositionCohortDefinition();
           patientsInHIVonCotrimoOrBactrim.setName("patientsInHIVonCotrimoOrBactrim");
           patientsInHIVonCotrimoOrBactrim.addParameter(new Parameter("startDate", "startDate", Date.class));
           patientsInHIVonCotrimoOrBactrim.addParameter(new Parameter("endDate", "endDate", Date.class));
           patientsInHIVonCotrimoOrBactrim.getSearches().put("1",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
           patientsInHIVonCotrimoOrBactrim.getSearches().put("2",new Mapped<CohortDefinition>(startedCotrimoXazoleDuringP, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsInHIVonCotrimoOrBactrim.setCompositionString("1 AND 2");
            
           CompositionCohortDefinition allARTpatientsOnPreArtonCotrimo = new CompositionCohortDefinition();
           allARTpatientsOnPreArtonCotrimo.setName("allARTpatientsOnPreArtonCotrimo");
           allARTpatientsOnPreArtonCotrimo.addParameter(new Parameter("endDate", "endDate", Date.class));
           allARTpatientsOnPreArtonCotrimo.getSearches().put("1",new Mapped<CohortDefinition>(allARTpatientsOnPreArt,null));
           allARTpatientsOnPreArtonCotrimo.getSearches().put("2", new Mapped<CohortDefinition>(patientsInHIVonCotrimoOrBactrim,null));
           allARTpatientsOnPreArtonCotrimo.setCompositionString("1 OR 2");
           CohortIndicator patientsInHIVonCotrimoOrBactrimInd=Indicators.newCohortIndicator("TR:patientsInHIVonCotrimoOrBactrimInd", allARTpatientsOnPreArtonCotrimo,null);

           //14 Number of new patients screened for active TB at enrollment this month
           CodedObsCohortDefinition patientsWithTBinHIVForms = Cohorts.createCodedObsCohortDefinition("patientsWithTBinHIVForms",onOrAfterOnOrBefore, tbScreeningtest,null, SetComparator.IN, TimeModifier.LAST);
           CodedObsCohortDefinition tbScreenngPosTest = Cohorts.createCodedObsCohortDefinition("patientsWithTBinHIVForms",onOrAfterOnOrBefore, tbScreeningtest,positiveStatus, SetComparator.IN, TimeModifier.LAST);
           
           CompositionCohortDefinition screenedForTbInHIVProgramsComp = new CompositionCohortDefinition();
           screenedForTbInHIVProgramsComp.addParameter(new Parameter("startDate", "startDate", Date.class));
           screenedForTbInHIVProgramsComp.addParameter(new Parameter("endDate", "endDate", Date.class));
           screenedForTbInHIVProgramsComp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           screenedForTbInHIVProgramsComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           screenedForTbInHIVProgramsComp.setName("screenedForTbInHIVProgramsComp");
           screenedForTbInHIVProgramsComp.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           screenedForTbInHIVProgramsComp.getSearches().put("2",new Mapped<CohortDefinition>(patientsWithTBinHIVForms,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           screenedForTbInHIVProgramsComp.setCompositionString("1 AND 2");
           CohortIndicator screenedForTbInHIVProgramsIndi=Indicators.newCohortIndicator("screenedForTbInHIVProgramsIndi", screenedForTbInHIVProgramsComp,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}"));
           screenedForTbInHIVProgramsIndi.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           screenedForTbInHIVProgramsIndi.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
            
           //15 Number of new patients screened for active TB Positive at enrollment this month
           CompositionCohortDefinition screenedForTbPosInHIVProgramsComp = new CompositionCohortDefinition();
           screenedForTbPosInHIVProgramsComp.addParameter(new Parameter("startDate", "startDate", Date.class));
           screenedForTbPosInHIVProgramsComp.addParameter(new Parameter("endDate", "endDate", Date.class));
           screenedForTbPosInHIVProgramsComp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           screenedForTbPosInHIVProgramsComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           screenedForTbPosInHIVProgramsComp.setName("TR:screenedForTbPosInHIVProgramsComp");
           screenedForTbPosInHIVProgramsComp.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           screenedForTbPosInHIVProgramsComp.getSearches().put("2",new Mapped<CohortDefinition>(tbScreenngPosTest,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           screenedForTbPosInHIVProgramsComp.setCompositionString("1 AND 2 ");
           CohortIndicator screenedForTbPosInHIVProgramsInd=Indicators.newCohortIndicator("screenedForTbPosInHIVProgramsInd", screenedForTbPosInHIVProgramsComp, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
           screenedForTbPosInHIVProgramsInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           screenedForTbPosInHIVProgramsInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
            
           //16 Number of newly enrolled patients (age <15 years) who started TB treatment this month
           SqlCohortDefinition onTbDrugduringPeriod = Cohorts.geOnTBDrugsByStartEndDate("on TB drug");
           CompositionCohortDefinition patientsOnTBdrugInHIvProgramsUnder15 = new CompositionCohortDefinition();
           patientsOnTBdrugInHIvProgramsUnder15.addParameter(new Parameter("startDate", "startDate", Date.class));
           patientsOnTBdrugInHIvProgramsUnder15.addParameter(new Parameter("endDate", "endDate", Date.class));
           patientsOnTBdrugInHIvProgramsUnder15.setName("patientsOnTBdrugInHIvProgramsUnder15");
           patientsOnTBdrugInHIvProgramsUnder15.getSearches().put("1",new Mapped<CohortDefinition>(preArtduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsOnTBdrugInHIvProgramsUnder15.getSearches().put("2",new Mapped<CohortDefinition>(under15YrsAtEnrol,null));
           patientsOnTBdrugInHIvProgramsUnder15.getSearches().put("3",new Mapped<CohortDefinition>(onTbDrugduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsOnTBdrugInHIvProgramsUnder15.setCompositionString("1 AND 2 AND 3");
           CohortIndicator patientsOnTBdrugInHIvProgramsUnder15Ind=Indicators.newCohortIndicator("patientsOnTBdrugInHIvProgramsUnder15Ind", patientsOnTBdrugInHIvProgramsUnder15,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
            
           //17 Number of newly enrolled patients (age 15 or more years) who started TB treatment this month
           CompositionCohortDefinition patientsOnTBdrugInHIvProgramsOver15 = new CompositionCohortDefinition();
           patientsOnTBdrugInHIvProgramsOver15.addParameter(new Parameter("startDate", "startDate", Date.class));
           patientsOnTBdrugInHIvProgramsOver15.addParameter(new Parameter("endDate", "endDate", Date.class));
           patientsOnTBdrugInHIvProgramsOver15.setName("patientsOnTBdrugInHIvProgramsOver15");
           patientsOnTBdrugInHIvProgramsOver15.getSearches().put("1",new Mapped<CohortDefinition>(femalenewlyEnrolledInHIVCareOver15yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsOnTBdrugInHIvProgramsOver15.getSearches().put("2",new Mapped<CohortDefinition>(malenewlyEnrolledInHIVCareOver15yrs,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsOnTBdrugInHIvProgramsOver15.getSearches().put("3",new Mapped<CohortDefinition>(onTbDrugduringPeriod,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsOnTBdrugInHIvProgramsOver15.setCompositionString("(1 OR 2) AND 3");
           CohortIndicator patientsOnTBdrugInHIvProgramsOver15Ind=Indicators.newCohortIndicator("patientsOnTBdrugInHIvProgramsOver15Ind", patientsOnTBdrugInHIvProgramsOver15, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
           
           //18 Number of PRE-ARV patients who have died this month
           SqlCohortDefinition pediOnpreArtBeforeExitedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:pediOnpreArtBeforeExitedFromCare", pediatrichivProgram,pediOnFollowing,pediPreAndArtDiedState);
           SqlCohortDefinition adultOnpreArtBeforeExitedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:adultOnpreArtBeforeExitedFromCare", adulthivProgram,adultOnFollowing,pediPreAndArtDiedState);
 
           CompositionCohortDefinition patientsDiedandNotOnART = new CompositionCohortDefinition();
           patientsDiedandNotOnART.addParameter(new Parameter("startDate", "startDate", Date.class));
           patientsDiedandNotOnART.addParameter(new Parameter("endDate", "endDate", Date.class));
           patientsDiedandNotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsDiedandNotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           patientsDiedandNotOnART.setName("patientsDiedandNotOnART");
           patientsDiedandNotOnART.getSearches().put("1",new Mapped<CohortDefinition>(pediOnpreArtBeforeExitedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsDiedandNotOnART.getSearches().put("2",new Mapped<CohortDefinition>(adultOnpreArtBeforeExitedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsDiedandNotOnART.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithDeadStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           patientsDiedandNotOnART.setCompositionString("(1 OR 2) AND 3 ");
           CohortIndicator patientsDiedandNotOnARTInd=Indicators.newCohortIndicator("patientsDiedandNotOnARTInd", patientsDiedandNotOnART, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
           patientsDiedandNotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsDiedandNotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           //19 Number of PRE-ARV patients who have been transferred in this month
           CompositionCohortDefinition patientsTransferedIntAndnotOnART = new CompositionCohortDefinition();
           patientsTransferedIntAndnotOnART.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
           patientsTransferedIntAndnotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsTransferedIntAndnotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           patientsTransferedIntAndnotOnART.setName("patientsTransferedIntAndnotOnART");
           patientsTransferedIntAndnotOnART.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediAndAdultProgram, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter}")));
           patientsTransferedIntAndnotOnART.getSearches().put("2", new Mapped<CohortDefinition>(patientTransferEncounter,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
           patientsTransferedIntAndnotOnART.getSearches().put("3", new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
           patientsTransferedIntAndnotOnART.setCompositionString("1 AND 2 AND (NOT 3)");
          
           CohortIndicator patientsTransferedIntAndnotOnARTInd=Indicators.newCohortIndicator("patientsTransferedIntAndnotOnARTInd", patientsTransferedIntAndnotOnART, ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${enrolledOnOrAfter},onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}"));
           patientsTransferedIntAndnotOnARTInd.addParameter(new Parameter("enrolledOnOrAfter", "enrolledOnOrAfter", Date.class));
           patientsTransferedIntAndnotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsTransferedIntAndnotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
          //20 Number of PRE-ARV patients who have been transferred out this month
           SqlCohortDefinition pediOnpreArtBeforeTranferedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:pediOnpreArtBeforeTranferedFromCare", pediatrichivProgram,pediOnFollowing,peditransferedOutState);
           SqlCohortDefinition adultOnpreArtBeforeTransferedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:adultOnpreArtBeforeTransferedFromCare", adulthivProgram,adultOnFollowing,adulttransferedOutState);        
           
           CompositionCohortDefinition patientsTransferedoutAndnotOnART = new CompositionCohortDefinition();
           patientsTransferedoutAndnotOnART.addParameter(new Parameter("startDate", "startDate", Date.class));
           patientsTransferedoutAndnotOnART.addParameter(new Parameter("endDate", "endDate", Date.class));
           patientsTransferedoutAndnotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsTransferedoutAndnotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           patientsTransferedoutAndnotOnART.setName("patientsTransferedoutAndnotOnART");
           patientsTransferedoutAndnotOnART.getSearches().put("1",new Mapped<CohortDefinition>(pediOnpreArtBeforeTranferedFromCare, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsTransferedoutAndnotOnART.getSearches().put("2", new Mapped<CohortDefinition>(adultOnpreArtBeforeTransferedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsTransferedoutAndnotOnART.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithtransferStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           patientsTransferedoutAndnotOnART.setCompositionString("(1 OR 2) AND 3");
           CohortIndicator patientsTransferedoutAndnotOnARTInd=Indicators.newCohortIndicator("patientsTransferedoutAndnotOnARTInd", patientsTransferedoutAndnotOnART, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
           patientsTransferedoutAndnotOnARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsTransferedoutAndnotOnARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           EncounterCohortDefinition clinicalEncWithoutLab = Cohorts.createEncounterParameterizedByDate("clinicalEncWithoutLab", onOrAfterOnOrBefore,clinicalEnountersIncLab);
           CompositionCohortDefinition ltfDuringPeriod = new CompositionCohortDefinition();
           ltfDuringPeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           ltfDuringPeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           ltfDuringPeriod.setName("ltfDuringPeriod");
           ltfDuringPeriod.getSearches().put("1",new Mapped<CohortDefinition>(clinicalEncWithoutLab,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           ltfDuringPeriod.setCompositionString("NOT 1");
          
           //21 Number of PRE-ARV patients lost to follow up (> 3months)
           CompositionCohortDefinition patientsinHIVcareLostTofolowUp = new CompositionCohortDefinition();
           patientsinHIVcareLostTofolowUp.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsinHIVcareLostTofolowUp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           patientsinHIVcareLostTofolowUp.setName("patientsinHIVcareLostTofolowUp");
           patientsinHIVcareLostTofolowUp.getSearches().put("1",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
           patientsinHIVcareLostTofolowUp.getSearches().put("2",new Mapped<CohortDefinition>(ltfDuringPeriod,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           patientsinHIVcareLostTofolowUp.getSearches().put("3",new Mapped<CohortDefinition>(nonActivePatients,null));
           patientsinHIVcareLostTofolowUp.setCompositionString("1 AND 2 AND (NOT 3)");
           CohortIndicator patientsinHIVcareLostTofolowUpInd=Indicators.newCohortIndicator("patientsinHIVcareLostTofolowUpInd", patientsinHIVcareLostTofolowUp, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
           patientsinHIVcareLostTofolowUpInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsinHIVcareLostTofolowUpInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           //22 Number of PRe-ART patients lost to follow up (> 3months) back to program
           SqlCohortDefinition lostbacktoProgramThismonth=new SqlCohortDefinition();
           lostbacktoProgramThismonth.setName("TR:lostbacktoProgramThismonth");
           lostbacktoProgramThismonth.setQuery("select DISTINCT patient_id FROM encounter WHERE patient_id " +
           	"IN (select DISTINCT e.patient_id FROM encounter e, patient p WHERE e.patient_id=p.patient_id " +
           	"AND DATEDIFF(:startDate, e.encounter_datetime) >= 90 AND e.encounter_type IN (1,2,3,4,24,25) " +
           	"AND p.voided=0 AND e.voided=0) AND encounter_datetime >= :startDate AND encounter_datetime <= :endDate AND voided=0  ");
           lostbacktoProgramThismonth.addParameter(new Parameter("startDate", "startDate", Date.class));
           lostbacktoProgramThismonth.addParameter(new Parameter("endDate", "endDate", Date.class));
           
           CompositionCohortDefinition patientsinLostAndBackToPRogramThismonth = new CompositionCohortDefinition();
           patientsinLostAndBackToPRogramThismonth.setName("patientsinLostAndBackToPRogramThismonth");
           patientsinLostAndBackToPRogramThismonth.addParameter(new Parameter("startDate","startDate",Date.class));
           patientsinLostAndBackToPRogramThismonth.addParameter(new Parameter("endDate","endDate",Date.class));
           patientsinLostAndBackToPRogramThismonth.getSearches().put("1",new Mapped<CohortDefinition>(lostbacktoProgramThismonth,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsinLostAndBackToPRogramThismonth.getSearches().put("2",new Mapped<CohortDefinition>(onFollowingStateHIVClinic,null));
           patientsinLostAndBackToPRogramThismonth.getSearches().put("3",new Mapped<CohortDefinition>(nonActivePatients,null));
           patientsinLostAndBackToPRogramThismonth.setCompositionString("1 AND 2 AND (NOT 3)");
           CohortIndicator patientsinLostAndBackToPRogramThismonthInd=Indicators.newCohortIndicator("patientsinLostAndBackToPRogramThismonth", patientsinLostAndBackToPRogramThismonth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
           
          // -------------------------------------------
          //       ART CATEGORY
          //-------------------------------------------
                 
          InStateCohortDefinition onArtatEndDatePeriod = Cohorts.createInCurrentState("TR: started on Art", onARTstates,onOrAfterOnOrBefore);
          CompositionCohortDefinition onARTStateatTheEnd = new CompositionCohortDefinition();
          onARTStateatTheEnd.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          onARTStateatTheEnd.setName("onARTStateatTheEnd");
          onARTStateatTheEnd.getSearches().put("1", new Mapped<CohortDefinition>(onArtatEndDatePeriod, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          onARTStateatTheEnd.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
          onARTStateatTheEnd.setCompositionString("1 AND 2");
          // ntahantu na hamwe imappinze
         /* CompositionCohortDefinition onARTStateatDuringP = new CompositionCohortDefinition();
          onARTStateatDuringP.addParameter(new Parameter("startDate","startDate",Date.class));
          onARTStateatDuringP.setName("onARTStateatDuringP");
          onARTStateatDuringP.getSearches().put("1", new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          onARTStateatDuringP.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
          onARTStateatDuringP.setCompositionString("1 AND 2");*/
          //1
          CompositionCohortDefinition pedsonARTStateHIVClinic = new CompositionCohortDefinition();
          pedsonARTStateHIVClinic.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedsonARTStateHIVClinic.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
          pedsonARTStateHIVClinic.setName("pedsonARTStateHIVClinic");
          pedsonARTStateHIVClinic.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          pedsonARTStateHIVClinic.getSearches().put("2",new Mapped<CohortDefinition>(at18monthsOfAge,ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
          pedsonARTStateHIVClinic.setCompositionString("1 AND 2");
          CohortIndicator pedsonARTStateHIVClinicInd=Indicators.newCohortIndicator("pedsonARTStateHIVClinicInd", pedsonARTStateHIVClinic, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},effectiveDate=${effectiveDate}"));
          pedsonARTStateHIVClinicInd.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedsonARTStateHIVClinicInd.addParameter(new Parameter("effectiveDate","effectiveDate",Date.class));
          //2
          CompositionCohortDefinition pedsonARTStateHIVClinicunder5 = new CompositionCohortDefinition();
          pedsonARTStateHIVClinicunder5.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedsonARTStateHIVClinicunder5.setName("pedsonARTStateHIVClinicunder5");
          pedsonARTStateHIVClinicunder5.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          pedsonARTStateHIVClinicunder5.getSearches().put("2",new Mapped<CohortDefinition>(underFive,null));
          pedsonARTStateHIVClinicunder5.setCompositionString("1 AND 2");
          CohortIndicator pedsonARTStateHIVClinicunder5Ind=Indicators.newCohortIndicator("pedsonARTStateHIVClinicunder5Ind", pedsonARTStateHIVClinicunder5, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}"));
          pedsonARTStateHIVClinicunder5Ind.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          //3
          CompositionCohortDefinition patientEnrolledInPediDuringP= Cohorts.createEnrolledInProgramDuringPeriod("patientEnrolledInPediPrograms",pediatrichivProgram);
          CompositionCohortDefinition enrolledInPMTCTProgramsDuringP= Cohorts.createEnrolledInProgramDuringPeriod("enrolledInPMTCTProgramsDuringP",adulthivProgram);
          CompositionCohortDefinition enrolledInAdultProgramsDuringP= Cohorts.createEnrolledInProgramDuringPeriod("enrolledInAdultProgramsDuringP",pmtctcombinedMother);
          
          CompositionCohortDefinition pedFemalesonARTStateHIVClinicunder15 = new CompositionCohortDefinition();
          pedFemalesonARTStateHIVClinicunder15.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedFemalesonARTStateHIVClinicunder15.addParameter(new Parameter("dateborn","dateborn",Date.class));
          pedFemalesonARTStateHIVClinicunder15.setName("pedFemalesonARTStateHIVClinicunder15");
          pedFemalesonARTStateHIVClinicunder15.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          pedFemalesonARTStateHIVClinicunder15.getSearches().put("2",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          pedFemalesonARTStateHIVClinicunder15.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
          pedFemalesonARTStateHIVClinicunder15.setCompositionString("1 AND 2 AND 3");
          CohortIndicator pedFemalesonARTStateHIVClinicunder15Ind=Indicators.newCohortIndicator("pedFemalesonARTStateHIVClinicunder15Ind", pedFemalesonARTStateHIVClinicunder15,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},dateborn=${dateborn}"));
          pedFemalesonARTStateHIVClinicunder15Ind.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedFemalesonARTStateHIVClinicunder15Ind.addParameter(new Parameter("dateborn","dateborn",Date.class));
          //4     
          CompositionCohortDefinition pedMalesonARTStateHIVClinicunder15 = new CompositionCohortDefinition();
          pedMalesonARTStateHIVClinicunder15.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedMalesonARTStateHIVClinicunder15.addParameter(new Parameter("dateborn","dateborn",Date.class));
          pedMalesonARTStateHIVClinicunder15.setName("pedMalesonARTStateHIVClinicunder15");
          pedMalesonARTStateHIVClinicunder15.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          pedMalesonARTStateHIVClinicunder15.getSearches().put("2",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          pedMalesonARTStateHIVClinicunder15.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
          pedMalesonARTStateHIVClinicunder15.setCompositionString("1 AND 2 AND 3");
          CohortIndicator pedMalesonARTStateHIVClinicunder15Ind=Indicators.newCohortIndicator("pedMalesonARTStateHIVClinicunder15Ind", pedMalesonARTStateHIVClinicunder15, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},dateborn=${dateborn}"));
          pedMalesonARTStateHIVClinicunder15Ind.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          pedMalesonARTStateHIVClinicunder15Ind.addParameter(new Parameter("dateborn","dateborn",Date.class));
          
          //5 Total number of pediatric patients who are on First Line Regimen
          CompositionCohortDefinition patientOnArtOrPMTCTPrograms = new CompositionCohortDefinition();
          patientOnArtOrPMTCTPrograms.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          patientOnArtOrPMTCTPrograms.setName("patientOnArtOrPMTCTPrograms");
          patientOnArtOrPMTCTPrograms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          patientOnArtOrPMTCTPrograms.getSearches().put("2",new Mapped<CohortDefinition>(inPmtctPregnancyprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
          patientOnArtOrPMTCTPrograms.getSearches().put("3",new Mapped<CohortDefinition>(inPmtctMotherprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
          patientOnArtOrPMTCTPrograms.setCompositionString("1 OR 2 OR 3");
          
          SqlCohortDefinition onCurrentKaletraDrugOrder = Cohorts.getPatientsOnCurrentRegimenBasedOnEndDate("onCurrentKaletraDrugOrder", kaletra);
          CompositionCohortDefinition notOnCurrentKaletraDrugOrder = new CompositionCohortDefinition();
          notOnCurrentKaletraDrugOrder.setName("notOnCurrentKaletraDrugOrder");
          notOnCurrentKaletraDrugOrder.addParameter(new Parameter("endDate", "endDate", Date.class));
          notOnCurrentKaletraDrugOrder.addParameter(new Parameter("dateborn","dateborn",Date.class));
          notOnCurrentKaletraDrugOrder.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          notOnCurrentKaletraDrugOrder.getSearches().put("1",new Mapped<CohortDefinition>(patientOnArtOrPMTCTPrograms,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          notOnCurrentKaletraDrugOrder.getSearches().put("2",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          notOnCurrentKaletraDrugOrder.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          notOnCurrentKaletraDrugOrder.setCompositionString("1 AND 2 AND (NOT 3)");
          CohortIndicator notOnCurrentKaletraDrugOrderInd=Indicators.newCohortIndicator("notOnCurrentKaletraDrugOrderInd", notOnCurrentKaletraDrugOrder, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},endDate=${endDate},dateborn=${dateborn}"));
          notOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          notOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("dateborn","dateborn",Date.class));
          
          //6 Total number of pediatric patients who are on Second Line Regimen 
          CompositionCohortDefinition onCurrentKaletraDrugOrderComp = new CompositionCohortDefinition();
          onCurrentKaletraDrugOrderComp.setName("activeOnCurrentKaletraDrugOrderComp");
          onCurrentKaletraDrugOrderComp.addParameter(new Parameter("endDate", "endDate", Date.class));
          onCurrentKaletraDrugOrderComp.addParameter(new Parameter("dateborn","dateborn",Date.class));
          onCurrentKaletraDrugOrderComp.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          onCurrentKaletraDrugOrderComp.getSearches().put("1",new Mapped<CohortDefinition>(patientOnArtOrPMTCTPrograms,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          onCurrentKaletraDrugOrderComp.getSearches().put("2",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          onCurrentKaletraDrugOrderComp.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          onCurrentKaletraDrugOrderComp.setCompositionString("1 AND 2 AND 3");
          CohortIndicator activeOnCurrentKaletraDrugOrderInd=Indicators.newCohortIndicator("activeOnCurrentKaletraDrugOrderInd", onCurrentKaletraDrugOrderComp,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},dateborn=${dateborn},endDate=${endDate}"));
          activeOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("dateborn","dateborn",Date.class));
          activeOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("onOrBefore","onOrBefore",Date.class));
          
          //7 Total number of female adult patients (age 15 or older) who are currently on ARV treatment
          CompositionCohortDefinition onARTStateInPMTCTprogrs = new CompositionCohortDefinition();
          onARTStateInPMTCTprogrs.setName("TR:onARTStateInPMTCTprogrs");
          onARTStateInPMTCTprogrs.getSearches().put("1",new Mapped<CohortDefinition>(onARTDrugs, null));
          onARTStateInPMTCTprogrs.getSearches().put("2",new Mapped<CohortDefinition>(inPmtctMotherprogram, ParameterizableUtil.createParameterMappings("onDate=${now}")));
          onARTStateInPMTCTprogrs.getSearches().put("3",new Mapped<CohortDefinition>(inPmtctPregnancyprogram, ParameterizableUtil.createParameterMappings("onDate=${now}")));
          onARTStateInPMTCTprogrs.setCompositionString("1 AND (2 OR 3)");
          
          CompositionCohortDefinition femaleOnArtStateinAllHIVPrograms = new CompositionCohortDefinition();
          femaleOnArtStateinAllHIVPrograms.setName("femaleOnArtStateinAllHIVPrograms");
          femaleOnArtStateinAllHIVPrograms.addParameter(new Parameter("dateborn","dateborn",Date.class));
          femaleOnArtStateinAllHIVPrograms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
          femaleOnArtStateinAllHIVPrograms.getSearches().put("2", new Mapped<CohortDefinition>(onARTStateInPMTCTprogrs,null));
          femaleOnArtStateinAllHIVPrograms.getSearches().put("3", new Mapped<CohortDefinition>(femaleCohort,null));
          femaleOnArtStateinAllHIVPrograms.getSearches().put("4", new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          femaleOnArtStateinAllHIVPrograms.setCompositionString("(1 OR 2) AND 3 AND 4");
          CohortIndicator femalesOnArtStateinAllHIVProgramsInd=Indicators.newCohortIndicator("femalesOnArtStateinAllHIVProgramsInd", femaleOnArtStateinAllHIVPrograms, ParameterizableUtil.createParameterMappings("dateborn=${dateborn}"));
          femalesOnArtStateinAllHIVProgramsInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          
          //8 Total number of male adult patients (age 15 or older) who are currently on ARV treatment
          CompositionCohortDefinition malesOnArtStateinAllHIVPrograms = new CompositionCohortDefinition();
          malesOnArtStateinAllHIVPrograms.setName("malesOnArtStateinAllHIVPrograms");
          malesOnArtStateinAllHIVPrograms.addParameter(new Parameter("dateborn","dateborn",Date.class));
          malesOnArtStateinAllHIVPrograms.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
          malesOnArtStateinAllHIVPrograms.getSearches().put("2", new Mapped<CohortDefinition>(onARTStateInPMTCTprogrs,null));
          malesOnArtStateinAllHIVPrograms.getSearches().put("3", new Mapped<CohortDefinition>(maleCohort,null));
          malesOnArtStateinAllHIVPrograms.getSearches().put("4", new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          malesOnArtStateinAllHIVPrograms.setCompositionString("(1 OR 2) AND 3 AND 4");
          CohortIndicator malesOnArtStateinAllHIVProgramsInd=Indicators.newCohortIndicator("malesOnArtStateinAllHIVProgramsInd", malesOnArtStateinAllHIVPrograms, ParameterizableUtil.createParameterMappings("dateborn=${dateborn}"));
          malesOnArtStateinAllHIVProgramsInd.addParameter(new Parameter("dateborn","dateborn",Date.class)); 
          
          //9 Total number of adult patients who are on First Line Regimen
          CompositionCohortDefinition adultsnotOnCurrentKaletraDrugOrder = new CompositionCohortDefinition();
          adultsnotOnCurrentKaletraDrugOrder.setName("adultsnotOnCurrentKaletraDrugOrder");
          adultsnotOnCurrentKaletraDrugOrder.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsnotOnCurrentKaletraDrugOrder.addParameter(new Parameter("dateborn","dateborn",Date.class));
          adultsnotOnCurrentKaletraDrugOrder.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
          adultsnotOnCurrentKaletraDrugOrder.getSearches().put("2",new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          adultsnotOnCurrentKaletraDrugOrder.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          adultsnotOnCurrentKaletraDrugOrder.setCompositionString("1 AND 2 AND (NOT 3)");
          CohortIndicator notoadultsOnCurrentKaletraDrugOrderInd=Indicators.newCohortIndicator("notoadultsOnCurrentKaletraDrugOrderInd", adultsnotOnCurrentKaletraDrugOrder, ParameterizableUtil.createParameterMappings("dateborn=${dateborn},endDate=${endDate}"));
          notoadultsOnCurrentKaletraDrugOrderInd.addParameter(new Parameter("dateborn","dateborn",Date.class));  
          
          //10 Total number of adult patients who are on Second Line Regimen
          CompositionCohortDefinition adultonCurrentKaletraDrugOrderCompo = new CompositionCohortDefinition();
          adultonCurrentKaletraDrugOrderCompo.setName("adultonCurrentKaletraDrugOrderCompo");
          adultonCurrentKaletraDrugOrderCompo.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultonCurrentKaletraDrugOrderCompo.addParameter(new Parameter("dateborn","dateborn",Date.class));
          adultonCurrentKaletraDrugOrderCompo.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateHIVClinic,null));
          adultonCurrentKaletraDrugOrderCompo.getSearches().put("2",new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          adultonCurrentKaletraDrugOrderCompo.getSearches().put("3",new Mapped<CohortDefinition>(onCurrentKaletraDrugOrder,ParameterizableUtil.createParameterMappings("endDate=${endDate}")));
          adultonCurrentKaletraDrugOrderCompo.setCompositionString("1 AND 2 AND 3");
          CohortIndicator adultonCurrentKaletraDrugOrderCompoInd=Indicators.newCohortIndicator("adultonCurrentKaletraDrugOrderCompoInd", adultonCurrentKaletraDrugOrderCompo, ParameterizableUtil.createParameterMappings("dateborn=${dateborn},endDate=${endDate}"));
          adultonCurrentKaletraDrugOrderCompoInd.addParameter(new Parameter("dateborn","dateborn",Date.class));
          
          //11 Number of new pediatric patients (<18 months) starting ARV treatment this month
          SqlCohortDefinition under18monthsAtstartOfArt = Cohorts.createUnder18monthsAtStartOfArtbyStartEndDate("TR:under18monthsAtstartOfArt", pediatrichivProgram,pediOnART);
          CohortIndicator pedsPatientsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("pedsPatientsNotOnArtStateNotOnFolowingInd", under18monthsAtstartOfArt, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //12 Number of new  pediatric patients (age <5 years) starting ARV treatment this month
          SqlCohortDefinition under5yearsAtstartOfArt = Cohorts.createUnder5yrsAtStartOfArtbyStartEndDate("TR:under5yearsAtstartOfArt", pediatrichivProgram,pediOnART);
          CohortIndicator under5PatientsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("under5PatientsNotOnArtStateNotOnFolowingInd", under5yearsAtstartOfArt, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //13 Number of new female pediatric patients (age <15 years) starting ARV treatment this month
          SqlCohortDefinition under15yearsAtstartOfArt = Cohorts.createUnder15yrsAtStartOfArtbyStartEndDate("TR:under15yearsAtstartOfArt", pediatrichivProgram,pediOnART);
          CompositionCohortDefinition femalesPedsNotOnArtStateNotOnFolowing = new CompositionCohortDefinition();
          femalesPedsNotOnArtStateNotOnFolowing.setName("femalesPedsNotOnArtStateNotOnFolowing");
          femalesPedsNotOnArtStateNotOnFolowing.addParameter(new Parameter("startDate", "startDate", Date.class));
          femalesPedsNotOnArtStateNotOnFolowing.addParameter(new Parameter("endDate", "endDate", Date.class));
          femalesPedsNotOnArtStateNotOnFolowing.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          femalesPedsNotOnArtStateNotOnFolowing.getSearches().put("2",new Mapped<CohortDefinition>(femaleCohort,null));
          femalesPedsNotOnArtStateNotOnFolowing.setCompositionString("1 AND 2 ");
          CohortIndicator femalesPedsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("femalesPedsNotOnArtStateNotOnFolowingInd", femalesPedsNotOnArtStateNotOnFolowing, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //14 Number of new male pediatric patients (age <15 years) starting ARV treatment this month
          CompositionCohortDefinition malesPedsNotOnArtStateNotOnFolowing = new CompositionCohortDefinition();
          malesPedsNotOnArtStateNotOnFolowing.setName("malesPedsNotOnArtStateNotOnFolowing");
          malesPedsNotOnArtStateNotOnFolowing.addParameter(new Parameter("startDate", "startDate", Date.class));
          malesPedsNotOnArtStateNotOnFolowing.addParameter(new Parameter("endDate", "endDate", Date.class));
          malesPedsNotOnArtStateNotOnFolowing.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          malesPedsNotOnArtStateNotOnFolowing.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort,null));
          malesPedsNotOnArtStateNotOnFolowing.setCompositionString("1 AND 2");
          CohortIndicator malesPedsNotOnArtStateNotOnFolowingInd=Indicators.newCohortIndicator("malesPedsNotOnArtStateNotOnFolowingInd", malesPedsNotOnArtStateNotOnFolowing,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
        
          //nta handi hantu na hamwe ikoreshejwe
        /*  CompositionCohortDefinition allHivProgOnORnotArt = new CompositionCohortDefinition();
          allHivProgOnORnotArt.setName("allHivProgOnORnotArt");
          allHivProgOnORnotArt.getSearches().put("1",new Mapped<CohortDefinition>(patientEnrolledInPediDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          allHivProgOnORnotArt.getSearches().put("2",new Mapped<CohortDefinition>(enrolledInAdultProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          allHivProgOnORnotArt.getSearches().put("3",new Mapped<CohortDefinition>(enrolledInPMTCTProgramsDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          allHivProgOnORnotArt.setCompositionString("(1 AND 2 ) OR 3");
       */
          //15 Number of new pediatric patients who are WHO stage 4 this month
          SqlCohortDefinition whoStage4p = Cohorts.getPatientsWithlastObservation("whoStage4p", whostage,whostage4p);
          CompositionCohortDefinition pediOnArtStateinWhostage4 = new CompositionCohortDefinition();
          pediOnArtStateinWhostage4.setName("pediOnArtStateinWhostage4");
          pediOnArtStateinWhostage4.addParameter(new Parameter("startDate", "startDate", Date.class));
          pediOnArtStateinWhostage4.addParameter(new Parameter("endDate", "endDate", Date.class));
          pediOnArtStateinWhostage4.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage4.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pediOnArtStateinWhostage4.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          pediOnArtStateinWhostage4.getSearches().put("2",new Mapped<CohortDefinition>(whoStage4p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          pediOnArtStateinWhostage4.setCompositionString("1 AND 2");
          CohortIndicator pediOnArtStateinWhostage4Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage4Ind", pediOnArtStateinWhostage4, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}"));
          pediOnArtStateinWhostage4Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage4Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //16 Number of new pediatric patients who are WHO stage 3 this month
          SqlCohortDefinition whoStage3p = Cohorts.getPatientsWithlastObservation("whoStage3p", whostage,whostage3p);
          CompositionCohortDefinition pediOnArtStateinWhostage3 = new CompositionCohortDefinition();
          pediOnArtStateinWhostage3.setName("pediOnArtStateinWhostage3");
          pediOnArtStateinWhostage3.addParameter(new Parameter("startDate", "startDate", Date.class));
          pediOnArtStateinWhostage3.addParameter(new Parameter("endDate", "endDate", Date.class));
          pediOnArtStateinWhostage3.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage3.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pediOnArtStateinWhostage3.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          pediOnArtStateinWhostage3.getSearches().put("2",new Mapped<CohortDefinition>(whoStage3p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          pediOnArtStateinWhostage3.setCompositionString("1 AND 2 ");
          CohortIndicator pediOnArtStateinWhostage3Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage3Ind", pediOnArtStateinWhostage3, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}"));
          pediOnArtStateinWhostage3Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage3Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //17 Number of new pediatric patients who are WHO stage 2 this month
          SqlCohortDefinition whoStage2p = Cohorts.getPatientsWithlastObservation("whoStage2p", whostage,whostage2p);
          CompositionCohortDefinition pediOnArtStateinWhostage2 = new CompositionCohortDefinition();
          pediOnArtStateinWhostage2.setName("pediOnArtStateinWhostage2");
          pediOnArtStateinWhostage2.addParameter(new Parameter("startDate", "startDate", Date.class));
          pediOnArtStateinWhostage2.addParameter(new Parameter("endDate", "endDate", Date.class));
          pediOnArtStateinWhostage2.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage2.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pediOnArtStateinWhostage2.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          pediOnArtStateinWhostage2.getSearches().put("2",new Mapped<CohortDefinition>(whoStage2p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          pediOnArtStateinWhostage2.setCompositionString("1 AND 2");
          CohortIndicator pediOnArtStateinWhostage2Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage2Ind", pediOnArtStateinWhostage2, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}"));
          pediOnArtStateinWhostage2Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage2Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //18 Number of new pediatric patients who are WHO stage 1 this month
          SqlCohortDefinition whoStage1p = Cohorts.getPatientsWithlastObservation("whoStage1p", whostage,whostage1p);
          CompositionCohortDefinition pediOnArtStateinWhostage1 = new CompositionCohortDefinition();
          pediOnArtStateinWhostage1.setName("pediOnArtStateinWhostage1");
          pediOnArtStateinWhostage1.addParameter(new Parameter("startDate", "startDate", Date.class));
          pediOnArtStateinWhostage1.addParameter(new Parameter("endDate", "endDate", Date.class));
          pediOnArtStateinWhostage1.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage1.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pediOnArtStateinWhostage1.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          pediOnArtStateinWhostage1.getSearches().put("2",new Mapped<CohortDefinition>(whoStage1p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrBefore},onOrBefore=${onOrBefore}")));
          pediOnArtStateinWhostage1.setCompositionString("1 AND 2");
          CohortIndicator pediOnArtStateinWhostage1Ind=Indicators.newCohortIndicator("pediOnArtStateinWhostage1Ind", pediOnArtStateinWhostage1, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}"));
          pediOnArtStateinWhostage1Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateinWhostage1Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //19 Number of new pediatric patients whose WHO Stage is undefined this month
          CompositionCohortDefinition allWhoStage = new CompositionCohortDefinition();
          allWhoStage.setName("allWhoStage");
          allWhoStage.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          allWhoStage.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          allWhoStage.getSearches().put("1",new Mapped<CohortDefinition>(whoStage4p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStage.getSearches().put("2",new Mapped<CohortDefinition>(whoStage3p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStage.getSearches().put("3",new Mapped<CohortDefinition>(whoStage2p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStage.getSearches().put("4",new Mapped<CohortDefinition>(whoStage1p,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStage.setCompositionString("1 OR 2 OR 3 OR 4");
          
          CompositionCohortDefinition pediOnArtStateInWhoStageX = new CompositionCohortDefinition();
          pediOnArtStateInWhoStageX.addParameter(new Parameter("startDate", "startDate", Date.class));
          pediOnArtStateInWhoStageX.addParameter(new Parameter("endDate", "endDate", Date.class));
          pediOnArtStateInWhoStageX.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateInWhoStageX.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pediOnArtStateInWhoStageX.getSearches().put("1",new Mapped<CohortDefinition>(under15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          pediOnArtStateInWhoStageX.getSearches().put("2",new Mapped<CohortDefinition>(allWhoStage,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          pediOnArtStateInWhoStageX.setCompositionString("1 AND (NOT 2)");
          CohortIndicator pediOnArtStateInWhoStageXInd=Indicators.newCohortIndicator("pediOnArtStateInWhoStageXInd", pediOnArtStateInWhoStageX, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},startDate=${startDate},endDate=${endDate}"));
          pediOnArtStateInWhoStageXInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pediOnArtStateInWhoStageXInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //20 Number of new female adult patients (age 15 or more) starting ARV treatment this month
          SqlCohortDefinition over15yearsAtstartOfArtAd = Cohorts.createOver15yrsAtStartOfArtbyStartEndDate("TR:over15yearsAtstartOfArtAd", adulthivProgram, adultOnART);
          SqlCohortDefinition over15yearsAtstartOfArtP = Cohorts.createOver15yrsAtStartOfArtbyStartEndDate("TR:over15yearsAtstartOfArtP", pediatrichivProgram, pediOnART);
          SqlCohortDefinition over15yearsAtstartOfArtPMTCT = Cohorts.createOver15yrsAtStartOfArtbyStartEndDate("TR:over15yearsAtstartOfArtPMTCT", pmtctPregnancyProgram, pmtctOnART);
          SqlCohortDefinition patientstakingARTfortheFirstTimes = Cohorts.getPatientsWithFirstDrugOrdersOnlyDurindStartEndDate("patientstakingARTfortheFirstTimes");
     		
          CompositionCohortDefinition over15yearsAtstartOfArt= new CompositionCohortDefinition();
          over15yearsAtstartOfArt.setName("over15yearsAtstartOfArt");
          over15yearsAtstartOfArt.addParameter(new Parameter("startDate", "startDate", Date.class));
          over15yearsAtstartOfArt.addParameter(new Parameter("endDate", "endDate", Date.class));
          over15yearsAtstartOfArt.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArtAd,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          over15yearsAtstartOfArt.getSearches().put("2",new Mapped<CohortDefinition>(over15yearsAtstartOfArtP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          over15yearsAtstartOfArt.getSearches().put("3",new Mapped<CohortDefinition>(over15yearsAtstartOfArtPMTCT,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          over15yearsAtstartOfArt.setCompositionString("1 OR 2 OR 3 ");
          
          CompositionCohortDefinition patientneverTakenorStoppedOnARTBeforeComp= new CompositionCohortDefinition();
          patientneverTakenorStoppedOnARTBeforeComp.setName("TR:patientneverTakenorStoppedOnARTBeforeComp");
          patientneverTakenorStoppedOnARTBeforeComp.addParameter(new Parameter("onOrAfter", "onOrAfter",Date.class));
          patientneverTakenorStoppedOnARTBeforeComp.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          patientneverTakenorStoppedOnARTBeforeComp.getSearches().put("1",new Mapped(inPmtctMotherprogram, ParameterizableUtil.createParameterMappings("onDate=${now}")));
          patientneverTakenorStoppedOnARTBeforeComp.getSearches().put("2",new Mapped(patientstakingARTfortheFirstTimes, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}")));
          patientneverTakenorStoppedOnARTBeforeComp.setCompositionString("1 AND 2 ");
          
          CompositionCohortDefinition adultsEnrolledInPMTCNewToArtorMotherProg= new CompositionCohortDefinition();
          adultsEnrolledInPMTCNewToArtorMotherProg.setName("TR:adultsEnrolledInPMTCTHIVforTheFirstTime");
          adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("onOrAfter", "onOrAfter",Date.class));
          adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("startDate", "startDate",Date.class));
          adultsEnrolledInPMTCNewToArtorMotherProg.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsEnrolledInPMTCNewToArtorMotherProg.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultsEnrolledInPMTCNewToArtorMotherProg.getSearches().put("2",new Mapped<CohortDefinition>(patientneverTakenorStoppedOnARTBeforeComp,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter}")));
          adultsEnrolledInPMTCNewToArtorMotherProg.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
          adultsEnrolledInPMTCNewToArtorMotherProg.setCompositionString("(1 OR 2) AND 3");
          CohortIndicator femaleAdultsadultsOnArtStateInd=Indicators.newCohortIndicator("femaleAdultsadultsOnArtStateInd", adultsEnrolledInPMTCNewToArtorMotherProg, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
          femaleAdultsadultsOnArtStateInd.addParameter(new Parameter("onOrAfter", "onOrAfter",Date.class));
          femaleAdultsadultsOnArtStateInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
        
          //21 Number of new male adult patients (age 15 or more) starting ARV treatment this month
         
          CompositionCohortDefinition maleAdultsadultsOnArtState= new CompositionCohortDefinition();
          maleAdultsadultsOnArtState.addParameter(new Parameter("startDate", "startDate",Date.class));
          maleAdultsadultsOnArtState.addParameter(new Parameter("endDate", "endDate", Date.class));
          maleAdultsadultsOnArtState.setName("maleAdultsadultsOnArtState");
          maleAdultsadultsOnArtState.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          maleAdultsadultsOnArtState.getSearches().put("2",new Mapped<CohortDefinition>(maleCohort,null));
          maleAdultsadultsOnArtState.setCompositionString("1 AND 2 ");
          CohortIndicator maleAdultsadultsOnArtStateInd=Indicators.newCohortIndicator("maleAdultsadultsOnArtStateInd", maleAdultsadultsOnArtState,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //22 Number of new adult patients who are WHO stage 4 this month
          SqlCohortDefinition whoStage4ad = Cohorts.getPatientsWithlastObservation("whoStage4ad", whostage,whostage4adlt);
          
          CompositionCohortDefinition adultsOnArtStateinWhostage4 = new CompositionCohortDefinition();
          adultsOnArtStateinWhostage4.addParameter(new Parameter("startDate", "startDate",Date.class));
          adultsOnArtStateinWhostage4.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsOnArtStateinWhostage4.setName("adultsOnArtStateinWhostage4");
          adultsOnArtStateinWhostage4.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage4.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultsOnArtStateinWhostage4.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultsOnArtStateinWhostage4.getSearches().put("2",new Mapped<CohortDefinition>(whoStage4ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          adultsOnArtStateinWhostage4.setCompositionString("1 AND 2");
          CohortIndicator adultsOnArtStateinWhostage4Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage4Ind", adultsOnArtStateinWhostage4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
          adultsOnArtStateinWhostage4Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage4Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class)); 
          
          //23 Number of new adult patients who are WHO stage 3 this month
          SqlCohortDefinition whoStage3ad = Cohorts.getPatientsWithlastObservation("whoStage3ad", whostage,whostage3adlt);
          
          CompositionCohortDefinition adultsOnArtStateinWhostage3 = new CompositionCohortDefinition();
          adultsOnArtStateinWhostage3.setName("adultsOnArtStateinWhostage3");
          adultsOnArtStateinWhostage3.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage3.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultsOnArtStateinWhostage3.addParameter(new Parameter("startDate", "startDate", Date.class));
          adultsOnArtStateinWhostage3.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsOnArtStateinWhostage3.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultsOnArtStateinWhostage3.getSearches().put("2",new Mapped<CohortDefinition>(whoStage3ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          adultsOnArtStateinWhostage3.setCompositionString("1 AND 2");
          CohortIndicator adultsOnArtStateinWhostage3Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage3Ind", adultsOnArtStateinWhostage3, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
          adultsOnArtStateinWhostage3Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage3Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //24 Number of new adult patients who are WHO stage 2 this month
          SqlCohortDefinition whoStage2ad = Cohorts.getPatientsWithlastObservation("whoStage2ad", whostage,whostage2adlt);
          CompositionCohortDefinition adultsOnArtStateinWhostage2 = new CompositionCohortDefinition();
          adultsOnArtStateinWhostage2.setName("adultsOnArtStateinWhostage2");
          adultsOnArtStateinWhostage2.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage2.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultsOnArtStateinWhostage2.addParameter(new Parameter("startDate", "startDate", Date.class));
          adultsOnArtStateinWhostage2.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsOnArtStateinWhostage2.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultsOnArtStateinWhostage2.getSearches().put("2",new Mapped<CohortDefinition>(whoStage2ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          adultsOnArtStateinWhostage2.setCompositionString("1 AND 2 ");
          CohortIndicator adultsOnArtStateinWhostage2Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage2Ind", adultsOnArtStateinWhostage2, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
          adultsOnArtStateinWhostage2Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage2Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //25 Number of new adult patients who are WHO stage 1 this month
          SqlCohortDefinition whoStage1ad = Cohorts.getPatientsWithlastObservation("whoStage1ad", whostage,whostage1adlt);
          CompositionCohortDefinition adultsOnArtStateinWhostage1 = new CompositionCohortDefinition();
          adultsOnArtStateinWhostage1.setName("adultsOnArtStateinWhostage1");
          adultsOnArtStateinWhostage1.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage1.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultsOnArtStateinWhostage1.addParameter(new Parameter("startDate", "startDate", Date.class));
          adultsOnArtStateinWhostage1.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsOnArtStateinWhostage1.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultsOnArtStateinWhostage1.getSearches().put("2",new Mapped<CohortDefinition>(whoStage1ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          adultsOnArtStateinWhostage1.setCompositionString("1 AND 2");
          CohortIndicator adultsOnArtStateinWhostage1Ind=Indicators.newCohortIndicator("adultsOnArtStateinWhostage1Ind", adultsOnArtStateinWhostage1, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
          adultsOnArtStateinWhostage1Ind.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostage1Ind.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //26 Number of new adult patients who are WHO stage undefined this month
          CompositionCohortDefinition allWhoStageAdult = new CompositionCohortDefinition();
          allWhoStageAdult.setName("allWhoStageAdult");
          allWhoStageAdult.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          allWhoStageAdult.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          allWhoStageAdult.getSearches().put("1",new Mapped<CohortDefinition>(whoStage4ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStageAdult.getSearches().put("2",new Mapped<CohortDefinition>(whoStage3ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStageAdult.getSearches().put("3",new Mapped<CohortDefinition>(whoStage2ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStageAdult.getSearches().put("4",new Mapped<CohortDefinition>(whoStage1ad,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          allWhoStageAdult.setCompositionString("1 OR 2 OR 3 OR 4");
          
          CompositionCohortDefinition adultsOnArtStateinWhostageX = new CompositionCohortDefinition();
          adultsOnArtStateinWhostageX.addParameter(new Parameter("startDate", "startDate", Date.class));
          adultsOnArtStateinWhostageX.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultsOnArtStateinWhostageX.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostageX.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultsOnArtStateinWhostageX.setName("adultsOnArtStateinWhostageX");
          adultsOnArtStateinWhostageX.getSearches().put("1",new Mapped<CohortDefinition>(over15yearsAtstartOfArt,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultsOnArtStateinWhostageX.getSearches().put("2",new Mapped<CohortDefinition>(allWhoStageAdult,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          adultsOnArtStateinWhostageX.setCompositionString("1 AND (NOT 2)");
          CohortIndicator adultsOnArtStateinWhostageXInd=Indicators.newCohortIndicator("adultsOnArtStateinWhostageXInd", adultsOnArtStateinWhostageX, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
          adultsOnArtStateinWhostageXInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultsOnArtStateinWhostageXInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          
          //27 Number of ARV patients (age < 15) who have had their treatment interrupted this month
          SqlCohortDefinition pediArtDrugsInteruptedThisMonth = Cohorts.createUnder15yrsAtStartOfArtbyCompletedDuringP("TR:pediArtDrugsInteruptedThisMonth", pediatrichivProgram, pediOnART);
          SqlCohortDefinition adultArtDrugsInteruptedThisMonth = Cohorts.createOver15yrsAtStartOfArtbyCompletedDuringP("TR:adultArtDrugsInteruptedThisMonth", adulthivProgram, adultOnART);
          CohortIndicator pediArtDrugsInteruptedThisMonthInd=Indicators.newCohortIndicator("adultsOnArtStateinWhostageXInd", pediArtDrugsInteruptedThisMonth,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //28 Number of ARV patients (age 15+) who have had their treatment interrupted this month   
          CohortIndicator adultArtDrugsInteruptedThisMonthInd=Indicators.newCohortIndicator("adultsOnArtStateinWhostageXInd", adultArtDrugsInteruptedThisMonth,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
          
          //29 Number of ARV patients (age <15) who have died this month
          SqlCohortDefinition pediOnArtBeforeExitedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:pediOnArtBeforeExitedFromCare", pediatrichivProgram,pediOnART,pediPreAndArtDiedState);
          SqlCohortDefinition adultOnArtBeforeExitedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("TR:adultOnArtBeforeExitedFromCare", adulthivProgram,adultOnART,adultPreAndArtDiedState);
          
          SqlCohortDefinition under15inDaysDead=new SqlCohortDefinition();
          under15inDaysDead.setName("under15inDaysDead");
          under15inDaysDead.setQuery("SELECT distinct pe.person_id from person pe WHERE DATEDIFF(:dateborn, pe.birthdate) < 5479 and pe.voided=0 ");
          under15inDaysDead.addParameter(new Parameter("dateborn", "dateborn", Date.class));
  		
          SqlCohortDefinition over15inDaysDead=new SqlCohortDefinition();
          over15inDaysDead.setName("over15inDaysDead");
          over15inDaysDead.setQuery("SELECT distinct pe.person_id from person pe WHERE DATEDIFF(:dateborn, pe.birthdate) >= 5479 and pe.voided=0 ");
          over15inDaysDead.addParameter(new Parameter("dateborn", "dateborn", Date.class));
  	
          CompositionCohortDefinition adultspatientsDiedandNotOnART = new CompositionCohortDefinition();
          adultspatientsDiedandNotOnART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultspatientsDiedandNotOnART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultspatientsDiedandNotOnART.addParameter(new Parameter("startDate", "startDate", Date.class));
          adultspatientsDiedandNotOnART.addParameter(new Parameter("endDate", "endDate", Date.class));
          adultspatientsDiedandNotOnART.setName("adultspatientsDiedandNotOnART");
          adultspatientsDiedandNotOnART.getSearches().put("1",new Mapped<CohortDefinition>(pediOnArtBeforeExitedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultspatientsDiedandNotOnART.getSearches().put("2",new Mapped<CohortDefinition>(adultOnArtBeforeExitedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
          adultspatientsDiedandNotOnART.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithDeadStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          adultspatientsDiedandNotOnART.setCompositionString("(1 OR 2) AND 3  ");
         
          CompositionCohortDefinition preArtPediAndDiedThisMonth = new CompositionCohortDefinition();
          preArtPediAndDiedThisMonth.setName("preArtPediAndDiedThisMonth");
          preArtPediAndDiedThisMonth.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          preArtPediAndDiedThisMonth.addParameter(new Parameter("endDate", "endDate", Date.class));
          preArtPediAndDiedThisMonth.addParameter(new Parameter("startDate", "startDate", Date.class));
          preArtPediAndDiedThisMonth.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          preArtPediAndDiedThisMonth.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          preArtPediAndDiedThisMonth.getSearches().put("1",new Mapped<CohortDefinition>(under15inDaysDead,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          preArtPediAndDiedThisMonth.getSearches().put("2",new Mapped<CohortDefinition>(adultspatientsDiedandNotOnART,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          preArtPediAndDiedThisMonth.setCompositionString("1 AND 2");
          CohortIndicator preArtPediAndDiedThisMonthInd=Indicators.newCohortIndicator("pedionARTDiedDuringPInd", preArtPediAndDiedThisMonth, ParameterizableUtil.createParameterMappings("dateborn=${dateborn},startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
          preArtPediAndDiedThisMonthInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          preArtPediAndDiedThisMonthInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          preArtPediAndDiedThisMonthInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          
          //30 Number of ARV patients (age 15 or more) who have died this month
          CompositionCohortDefinition preArtAdultAndDiedThisMonth = new CompositionCohortDefinition();
          preArtAdultAndDiedThisMonth.setName("preArtAdultAndDiedThisMonth");
          preArtAdultAndDiedThisMonth.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          preArtAdultAndDiedThisMonth.addParameter(new Parameter("endDate", "endDate", Date.class));
          preArtAdultAndDiedThisMonth.addParameter(new Parameter("startDate", "startDate", Date.class));
          preArtAdultAndDiedThisMonth.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          preArtAdultAndDiedThisMonth.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          preArtAdultAndDiedThisMonth.getSearches().put("1",new Mapped<CohortDefinition>(over15inDaysDead,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          preArtAdultAndDiedThisMonth.getSearches().put("2",new Mapped<CohortDefinition>(adultspatientsDiedandNotOnART,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          preArtAdultAndDiedThisMonth.setCompositionString("1 AND 2");
          CohortIndicator preArtAdultAndDiedThisMonthInd=Indicators.newCohortIndicator("adultsonARTDiedDuringPInd", preArtAdultAndDiedThisMonth, ParameterizableUtil.createParameterMappings("dateborn=${dateborn},startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
          preArtAdultAndDiedThisMonthInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          preArtAdultAndDiedThisMonthInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          preArtAdultAndDiedThisMonthInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          
          //31 Number of ARV patients (age <15) lost to follow up (>3 months)
         // EncounterCohortDefinition patientWithHIVEncounters = Cohorts.createEncounterParameterizedByDate("patientWithHIVEncounters", onOrAfterOnOrBefore,pediAdnAdultEncounters);
          EncounterCohortDefinition patientWithHIVEncountersDates = Cohorts.createEncounterParameterizedByDate("patientWithHIVEncounters", onOrAfterDateOnOrBeforeDate,pediAdnAdultEncounters);
          CompositionCohortDefinition artLostAdActive = new CompositionCohortDefinition();
          artLostAdActive.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          artLostAdActive.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          artLostAdActive.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
          artLostAdActive.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
          artLostAdActive.setName("artLostAdActive");
          artLostAdActive.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
          artLostAdActive.getSearches().put("2", new Mapped<CohortDefinition>(patientWithHIVEncountersDates,ParameterizableUtil.createParameterMappings("onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate}")));
          artLostAdActive.getSearches().put("3",new Mapped<CohortDefinition>(nonActivePatients,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
          artLostAdActive.setCompositionString("1 AND 2 AND (NOT 3) ");
          
          CompositionCohortDefinition pedsOnArtLostAndwithHIVForms = new CompositionCohortDefinition();
          pedsOnArtLostAndwithHIVForms.setName("pedsOnArtLostAndwithHIVForms");
          pedsOnArtLostAndwithHIVForms.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          pedsOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pedsOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pedsOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
          pedsOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
          pedsOnArtLostAndwithHIVForms.getSearches().put("1",new Mapped<CohortDefinition>(artLostAdActive,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate}")));
          pedsOnArtLostAndwithHIVForms.getSearches().put("3",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          pedsOnArtLostAndwithHIVForms.setCompositionString("1 AND 3 ");
          CohortIndicator pedsOnArtLostAndwithHIVFormsInd=Indicators.newCohortIndicator("pedsOnArtLostAndwithHIVFormsInd", pedsOnArtLostAndwithHIVForms, ParameterizableUtil.createParameterMappings("onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},dateborn=${dateborn"));
          pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
          pedsOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
          
          //32 Number of ARV patients (age 15 or more) lost to follow up (>3 months)
          CompositionCohortDefinition adultOnArtLostAndwithHIVForms = new CompositionCohortDefinition();
          adultOnArtLostAndwithHIVForms.setName("adultOnArtLostAndwithHIVForms");
          adultOnArtLostAndwithHIVForms.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          adultOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
          adultOnArtLostAndwithHIVForms.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
          adultOnArtLostAndwithHIVForms.getSearches().put("1",new Mapped<CohortDefinition>(artLostAdActive,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate}")));
          adultOnArtLostAndwithHIVForms.getSearches().put("2",new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
          adultOnArtLostAndwithHIVForms.setCompositionString("1 AND 2");
          CohortIndicator adultOnArtLostAndwithHIVFormsInd=Indicators.newCohortIndicator("adultOnArtLostAndwithHIVFormsInd", adultOnArtLostAndwithHIVForms,ParameterizableUtil.createParameterMappings("onOrAfterDate=${onOrAfterDate-3m},onOrBeforeDate=${onOrBeforeDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},dateborn=${dateborn"));
          adultOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
          adultOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
          adultOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrAfterDate", "onOrAfterDate", Date.class));
          adultOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("onOrBeforeDate", "onOrBeforeDate", Date.class));
          adultOnArtLostAndwithHIVFormsInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
          
           //33 Number of male patients on treatment 12 months after initiation of ARVs this month
           CompositionCohortDefinition malesOnArtLostforMoreThan12months = new CompositionCohortDefinition();
           malesOnArtLostforMoreThan12months.setName("malesOnArtLostforMoreThan12months");
           malesOnArtLostforMoreThan12months.addParameter(new Parameter("onDate", "onDate", Date.class));
           malesOnArtLostforMoreThan12months.getSearches().put("1",new Mapped<CohortDefinition>(onARTstatesStateCohort,ParameterizableUtil.createParameterMappings("onDate=${onDate-12m}")));
           malesOnArtLostforMoreThan12months.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
           malesOnArtLostforMoreThan12months.getSearches().put("3",new Mapped<CohortDefinition>(maleCohort,null));
           malesOnArtLostforMoreThan12months.setCompositionString("1 AND 2 AND 3 ");
           CohortIndicator malesOnArtLostforMoreThan12monthsInd=Indicators.newCohortIndicator("malesOnArtLostforMoreThan12monthsInd", malesOnArtLostforMoreThan12months,ParameterizableUtil.createParameterMappings("onDate=${onDate-12m}"));
           malesOnArtLostforMoreThan12monthsInd.addParameter(new Parameter("onDate", "onDate", Date.class)); 
           
           //34 Number of female patients on treatment 12 months after initiation of ARVs this month
           CompositionCohortDefinition femalesOnArtLostforMoreThan12months = new CompositionCohortDefinition();
           femalesOnArtLostforMoreThan12months.setName("femalesOnArtLostforMoreThan12months");
           femalesOnArtLostforMoreThan12months.addParameter(new Parameter("onDate", "onDate",Date.class));
           femalesOnArtLostforMoreThan12months.getSearches().put("1",new Mapped<CohortDefinition>(onARTstatesStateCohort,ParameterizableUtil.createParameterMappings("onDate=${onDate-12m}")));
           femalesOnArtLostforMoreThan12months.getSearches().put("2",new Mapped<CohortDefinition>(inPediAndAdultprogram,ParameterizableUtil.createParameterMappings("onDate=${now}")));
           femalesOnArtLostforMoreThan12months.getSearches().put("3",new Mapped<CohortDefinition>(femaleCohort,null));
           femalesOnArtLostforMoreThan12months.setCompositionString("1 AND 2 AND 3 ");
           CohortIndicator femalesOnArtLostforMoreThan12monthsInd=Indicators.newCohortIndicator("femalesOnArtLostforMoreThan12monthsInd", femalesOnArtLostforMoreThan12months, ParameterizableUtil.createParameterMappings("onDate=${onDate-12m}"));
           femalesOnArtLostforMoreThan12monthsInd.addParameter(new Parameter("onDate", "onDate", Date.class)); 
           
           //35 Number of  ARV patients (age <15) who have been transferred out this month
           SqlCohortDefinition pediOnArtBeforeTranferedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("pediOnArtBeforeTranferedFromCare", pediatrichivProgram,pediOnART,peditransferedOutState);
           SqlCohortDefinition adultOnArtBeforeTransferedFromCare = Cohorts.createArtOrPreArtAndActiveonPatientDiedORTransferedStateDuringPeriod("adultOnArtBeforeTransferedFromCare", adulthivProgram,adultOnART,adulttransferedOutState);        
           
           CompositionCohortDefinition patientArtTrasferedOut = new CompositionCohortDefinition();
           patientArtTrasferedOut.setName("patientArtTrasferedOut");
           patientArtTrasferedOut.addParameter(new Parameter("startDate", "startDate", Date.class));
           patientArtTrasferedOut.addParameter(new Parameter("endDate", "endDate", Date.class));
           patientArtTrasferedOut.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientArtTrasferedOut.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           patientArtTrasferedOut.getSearches().put("1",new Mapped<CohortDefinition>(pediOnArtBeforeTranferedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientArtTrasferedOut.getSearches().put("2",new Mapped<CohortDefinition>(adultOnArtBeforeTransferedFromCare,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientArtTrasferedOut.getSearches().put("3",new Mapped<CohortDefinition>(exitedCareWithtransferStatus,ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           patientArtTrasferedOut.setCompositionString("(1 OR 2) AND 3");
               
           CompositionCohortDefinition pedionARTTransferedOutDuringP = new CompositionCohortDefinition();
           pedionARTTransferedOutDuringP.setName("pedionARTTransferedOutDuringP");
           pedionARTTransferedOutDuringP.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           pedionARTTransferedOutDuringP.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           pedionARTTransferedOutDuringP.addParameter(new Parameter("startDate", "startDate", Date.class));
           pedionARTTransferedOutDuringP.addParameter(new Parameter("endDate", "endDate", Date.class));
           pedionARTTransferedOutDuringP.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           pedionARTTransferedOutDuringP.getSearches().put("1",new Mapped<CohortDefinition>(patientArtTrasferedOut,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           pedionARTTransferedOutDuringP.getSearches().put("2", new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
           pedionARTTransferedOutDuringP.setCompositionString("1 AND 2");
           CohortIndicator pedionARTTransferedOutDuringPInd=Indicators.newCohortIndicator("pedionARTTransferedOutDuringPInd", pedionARTTransferedOutDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},dateborn=${dateborn}"));
           pedionARTTransferedOutDuringPInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           pedionARTTransferedOutDuringPInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class)); 
           pedionARTTransferedOutDuringPInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
                 
           //36 Number of  ARV patients (age 15 or more) who have been transferred out this month
           
           CompositionCohortDefinition adultsonARTTransferedOutDuringP = new CompositionCohortDefinition();
           adultsonARTTransferedOutDuringP.setName("adultsonARTTransferedOutDuringP");
           adultsonARTTransferedOutDuringP.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           adultsonARTTransferedOutDuringP.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           adultsonARTTransferedOutDuringP.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class)); 
           adultsonARTTransferedOutDuringP.addParameter(new Parameter("startDate", "startDate", Date.class));
           adultsonARTTransferedOutDuringP.addParameter(new Parameter("endDate", "endDate", Date.class)); 
           adultsonARTTransferedOutDuringP.getSearches().put("1",new Mapped<CohortDefinition>(patientArtTrasferedOut,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
           adultsonARTTransferedOutDuringP.getSearches().put("2",new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
           adultsonARTTransferedOutDuringP.setCompositionString("1 AND 2");
           CohortIndicator adultsonARTTransferedOutDuringPInd=Indicators.newCohortIndicator("adultsonARTTransferedOutDuringPInd", adultsonARTTransferedOutDuringP, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},dateborn=${dateborn}"));
           adultsonARTTransferedOutDuringPInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           adultsonARTTransferedOutDuringPInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           adultsonARTTransferedOutDuringPInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           //37 Number of  ARV patients (age <15) who have been transferred in this month
           CompositionCohortDefinition pedionWithTransferEncounter = new CompositionCohortDefinition();
           pedionWithTransferEncounter.setName("pedionWithTransferEncounter");
           pedionWithTransferEncounter.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           pedionWithTransferEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           pedionWithTransferEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           pedionWithTransferEncounter.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
           pedionWithTransferEncounter.getSearches().put("2",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
           pedionWithTransferEncounter.getSearches().put("3", new Mapped<CohortDefinition>(patientTransferEncounter,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
           pedionWithTransferEncounter.setCompositionString("1 AND 2 AND 3");
           CohortIndicator pedionWithTransferInFormInd=Indicators.newCohortIndicator("pedionWithTransferInFormInd", pedionWithTransferEncounter, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},dateborn=${dateborn}"));
           pedionWithTransferInFormInd.setName("pedionWithTransferEncounter");
           pedionWithTransferInFormInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           pedionWithTransferInFormInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           pedionWithTransferInFormInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           //38 Number of ARV patients (age 15 or more) who have been transferred in this month
           CompositionCohortDefinition adultsOnWithTransferEncounter = new CompositionCohortDefinition();
           adultsOnWithTransferEncounter.setName("adultsOnWithTransferEncounter");
           adultsOnWithTransferEncounter.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           adultsOnWithTransferEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           adultsOnWithTransferEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           adultsOnWithTransferEncounter.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
           adultsOnWithTransferEncounter.getSearches().put("2",new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
           adultsOnWithTransferEncounter.getSearches().put("3", new Mapped<CohortDefinition>(patientTransferEncounter,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter}")));
           adultsOnWithTransferEncounter.setCompositionString("1 AND 2 AND 3");
           CohortIndicator adultsOnWithTransferEncounterInd=Indicators.newCohortIndicator("adultsOnWithTransferEncounterInd", adultsOnWithTransferEncounter, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},dateborn=${dateborn}"));
           adultsOnWithTransferEncounterInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           adultsOnWithTransferEncounterInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class)); 
           adultsOnWithTransferEncounterInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           
           //39 Number of PRe-ART patients lost to follow up (> 3months) back to program
           SqlCohortDefinition lostbacktoProgramThismonthinART=Cohorts.createPatientBackToProgramThisYear("TR:lostbacktoProgramThismonthinART");
           
           CompositionCohortDefinition patientsinLostAndBackToProgramThismonthART = new CompositionCohortDefinition();
           patientsinLostAndBackToProgramThismonthART.setName("patientsinLostAndBackToProgramThismonthART");
           patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("startDate","startDate",Date.class));
           patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("endDate","endDate",Date.class));
           patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsinLostAndBackToProgramThismonthART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           patientsinLostAndBackToProgramThismonthART.getSearches().put("1",new Mapped<CohortDefinition>(lostbacktoProgramThismonthinART,
        		   ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           patientsinLostAndBackToProgramThismonthART.getSearches().put("2",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
           patientsinLostAndBackToProgramThismonthART.setCompositionString("1 AND 2");
           CohortIndicator patientsinLostAndBackToProgramThismonthARTInd=Indicators.newCohortIndicator("patientsinLostAndBackToProgramThismonthARTInd", patientsinLostAndBackToProgramThismonthART, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},onOrAfter=${onOrAfter},startDate=${startDate},endDate=${endDate}"));
           patientsinLostAndBackToProgramThismonthARTInd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
           patientsinLostAndBackToProgramThismonthARTInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           //40 Number of new pediatric patient (age<15 years) starting ART second line regimen this month   
           SqlCohortDefinition onSecondLineDuringP=Cohorts.getPatientsOnCurrentRegimenBasedOnStartDateEndDate("patientOnSecondLine",kaletra);
   		
           CompositionCohortDefinition pediStartedSecondLineThisMonth = new CompositionCohortDefinition();
           pediStartedSecondLineThisMonth.setName("pediStartedSecondLineThisMonth");
           pediStartedSecondLineThisMonth.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           pediStartedSecondLineThisMonth.addParameter(new Parameter("startDate", "startDate", Date.class));
           pediStartedSecondLineThisMonth.addParameter(new Parameter("endDate", "endDate", Date.class));
           pediStartedSecondLineThisMonth.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           pediStartedSecondLineThisMonth.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd, ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
           pediStartedSecondLineThisMonth.getSearches().put("2",new Mapped<CohortDefinition>(under15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
           pediStartedSecondLineThisMonth.getSearches().put("3", new Mapped<CohortDefinition>(onSecondLineDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           pediStartedSecondLineThisMonth.setCompositionString("1 AND 2 AND 3");
           CohortIndicator pediOnArtStartedSecondLineThisMonthInd=Indicators.newCohortIndicator("pediOnArtStartedSecondLineThisMonthInd", pediStartedSecondLineThisMonth, ParameterizableUtil.createParameterMappings("dateborn=${dateborn},startDate=${startDate},endDate=${endDate},onOrBefore=${onOrBefore}"));
           pediOnArtStartedSecondLineThisMonthInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           pediOnArtStartedSecondLineThisMonthInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
            //41 Number of new pediatric patient (age 15+) starting ART second line regimen this month   
           CompositionCohortDefinition adultStartedSecondLineThisMonth1 = new CompositionCohortDefinition();
           adultStartedSecondLineThisMonth1.setName("adultStartedSecondLineThisMonth1");
           adultStartedSecondLineThisMonth1.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           adultStartedSecondLineThisMonth1.addParameter(new Parameter("startDate", "startDate", Date.class));
           adultStartedSecondLineThisMonth1.addParameter(new Parameter("endDate", "endDate", Date.class));
           adultStartedSecondLineThisMonth1.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           adultStartedSecondLineThisMonth1.getSearches().put("1",new Mapped<CohortDefinition>(onARTStateatTheEnd,ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore}")));
           adultStartedSecondLineThisMonth1.getSearches().put("2",new Mapped<CohortDefinition>(over15inDays,ParameterizableUtil.createParameterMappings("dateborn=${dateborn}")));
           adultStartedSecondLineThisMonth1.getSearches().put("3", new Mapped<CohortDefinition>(onSecondLineDuringP,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
           adultStartedSecondLineThisMonth1.setCompositionString("1 AND 2 AND 3");
           CohortIndicator adultOnArtStartedSecondLineThisMonthInd=Indicators.newCohortIndicator("adultOnArtStartedSecondLineThisMonthInd", adultStartedSecondLineThisMonth1, ParameterizableUtil.createParameterMappings("dateborn=${dateborn},startDate=${startDate},endDate=${endDate},onOrBefore=${onOrBefore}"));
           adultOnArtStartedSecondLineThisMonthInd.addParameter(new Parameter("dateborn", "dateborn", Date.class));
           adultOnArtStartedSecondLineThisMonthInd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
           
           //Add global filters to the report
           //PRE-ART DATA ELEMENT
           dsd.addColumn("1a","rwandareports.tracnetreport.indicator.preart.newPedsUnderEighteenMonthsInHivCare",new Mapped(newlyEnrolledInHIVCareunder18monthsInd, null), "");
           dsd.addColumn("2a","rwandareports.tracnetreport.indicator.preart.newPedsUnderFiveInHivCare",new Mapped(newlyEnrolledInHIVCareunder5yrsInd,null),"");
           dsd.addColumn("3a","rwandareports.tracnetreport.indicator.preart.newFemaleUnderFifteenInHivCare",new Mapped(femalesnewlyEnrolledInHIVCareunder15yrsInd,null),"");
           dsd.addColumn("4a","rwandareports.tracnetreport.indicator.preart.newMaleUnderFifteenInHivCare",new Mapped(malesnewlyEnrolledInHIVCareunder15yrsInd,null),"");
           dsd.addColumn("5a","rwandareports.tracnetreport.indicator.preart.newFemaleMoreThanFifteenInHivCare",new Mapped(femalesnewlyEnrolledInHIVCareOver15yrsInd,null),"");
           dsd.addColumn("6a","rwandareports.tracnetreport.indicator.preart.newMaleMoreThanFifteenInHivCare",new Mapped(malesnewlyEnrolledInHIVCareOver15yrsInd,null),"");
           dsd.addColumn("7a","rwandareports.tracnetreport.indicator.preart.pedUnderFifteenCurentlyInHiv",new Mapped(under15InHIVcareInAllProgram,null),"");
           dsd.addColumn("8a","rwandareports.tracnetreport.indicator.preart.pedsOverFifteenCurentlyInHiv",new Mapped(pedienrolledInHIVCareOver15Indi,null),"");
           dsd.addColumn("9a","rwandareports.tracnetreport.indicator.preart.femaleMoreThanFifteenEverInHiv",new Mapped(Over15FemalenHIVcareInd,null),"");
           dsd.addColumn("10a","rwandareports.tracnetreport.indicator.preart.femalePedsUnderFifteenEverInHiv",new Mapped(under15FemaleInHIVcareInAllProgram,null),"");
           dsd.addColumn("11a","rwandareports.tracnetreport.indicator.preart.maleMoreThanFifteenEverInHiv",new Mapped(Over15MalenHIVcareInd,null),"");
           dsd.addColumn("12a","rwandareports.tracnetreport.indicator.preart.malePedsUnderFifteenEverInHiv",new Mapped(under15MalenHIVcare,null),"");
           dsd.addColumn("13a","rwandareports.tracnetreport.indicator.preart.patientsOnCotrimoProphylaxis",new Mapped(patientsInHIVonCotrimoOrBactrimInd,null),"");
           dsd.addColumn("14a","rwandareports.tracnetreport.indicator.preart.patientsActiveTbAtEnrolThisMonth",new Mapped(screenedForTbInHIVProgramsIndi,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("15a","rwandareports.tracnetreport.indicator.preart.patientsTbPositiveAtEnrolThisMonth",new Mapped(screenedForTbPosInHIVProgramsInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("16a","rwandareports.tracnetreport.indicator.preart.newEnrolledPedsStartTbTreatThisMonth",new Mapped(patientsOnTBdrugInHIvProgramsUnder15Ind,null),"");
           dsd.addColumn("17a","rwandareports.tracnetreport.indicator.preart.newEnrolledAdultsStartTbTreatThisMonth",new Mapped(patientsOnTBdrugInHIvProgramsOver15Ind,null),"");
           dsd.addColumn("18a","rwandareports.tracnetreport.indicator.preart.patientsInPreARVDiedThisMonth",new Mapped(patientsDiedandNotOnARTInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("19a","rwandareports.tracnetreport.indicator.preart.patientsInPreARVTransferredInThisMonth",new Mapped(patientsTransferedIntAndnotOnARTInd,ParameterizableUtil.createParameterMappings("enrolledOnOrAfter=${startDate},onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("20a","rwandareports.tracnetreport.indicator.preart.patientsInPreARVTransferredOutThisMonth",new Mapped(patientsTransferedoutAndnotOnARTInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");  
           dsd.addColumn("21a","rwandareports.tracnetreport.indicator.preart.patientsInPreARVTLostToFollowUpThisMonth",new Mapped(patientsinHIVcareLostTofolowUpInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate-3m},onOrBefore=${endDate}")),"");
           dsd.addColumn("22a","rwandareports.tracnetreport.indicator.preart.patientsInPreARVTLostToFollowUpNotLostThisMonth",new Mapped(patientsinLostAndBackToPRogramThismonthInd,null),"");
                               
           // ART DATA ELEMENTS
           dsd.addColumn("1b","rwandareports.tracnetreport.indicator.art.pedsUnderEighteenMonthsCurrentOnArv",new Mapped(pedsonARTStateHIVClinicInd,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},effectiveDate=${endDate}")),"");
           dsd.addColumn("2b","rwandareports.tracnetreport.indicator.art.pedsUnderFiveCurrentOnArv",new Mapped(pedsonARTStateHIVClinicunder5Ind,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate}")),"");  
           dsd.addColumn("3b","rwandareports.tracnetreport.indicator.art.femalePedsUnderFifteenCurrentOnArv",new Mapped(pedFemalesonARTStateHIVClinicunder15Ind,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},dateborn=${endDate}")),"");  
           dsd.addColumn("4b","rwandareports.tracnetreport.indicator.art.malePedsUnderFifteenCurrentOnArv",new Mapped(pedMalesonARTStateHIVClinicunder15Ind,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},dateborn=${endDate}")),"");  
           dsd.addColumn("5b","rwandareports.tracnetreport.indicator.art.pedsOnFirstLineReg",new Mapped(notOnCurrentKaletraDrugOrderInd,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},dateborn=${endDate}")),"");
           dsd.addColumn("6b","rwandareports.tracnetreport.indicator.art.pedsOnSecondLineReg",new Mapped(activeOnCurrentKaletraDrugOrderInd,ParameterizableUtil.createParameterMappings("onOrBefore=${endDate},dateborn=${endDate}")),"");
           dsd.addColumn("7b","rwandareports.tracnetreport.indicator.art.femaleMoreThanFifteenCurrentOnArv",new Mapped(femalesOnArtStateinAllHIVProgramsInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate}")),"");
           dsd.addColumn("8b","rwandareports.tracnetreport.indicator.art.maleMoreThanFifteenCurrentOnArv",new Mapped(malesOnArtStateinAllHIVProgramsInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate}")),"");
           dsd.addColumn("9b","rwandareports.tracnetreport.indicator.art.adultOnFirstLineReg",new Mapped(notoadultsOnCurrentKaletraDrugOrderInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate}")),"");
           dsd.addColumn("10b","rwandareports.tracnetreport.indicator.art.adultOnSecondLineReg",new Mapped(adultonCurrentKaletraDrugOrderCompoInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate}")),""); 
           dsd.addColumn("11b","rwandareports.tracnetreport.indicator.art.newPedsUnderEighteenMonthStartArvThisMonth",new Mapped(pedsPatientsNotOnArtStateNotOnFolowingInd,null),"");  
           dsd.addColumn("12b","rwandareports.tracnetreport.indicator.art.newPedsUnderFiveStartArvThisMonth",new Mapped(under5PatientsNotOnArtStateNotOnFolowingInd,null),"");  
           dsd.addColumn("13b","rwandareports.tracnetreport.indicator.art.newFemalePedsUnderFifteenStartArvThisMonth",new Mapped(femalesPedsNotOnArtStateNotOnFolowingInd,null),"");  
           dsd.addColumn("14b","rwandareports.tracnetreport.indicator.art.newMalePedsUnderFifteenStartArvThisMonth",new Mapped(malesPedsNotOnArtStateNotOnFolowingInd,null),"");
           dsd.addColumn("15b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageFourThisMonth",new Mapped(pediOnArtStateinWhostage4Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("16b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageThreeThisMonth",new Mapped(pediOnArtStateinWhostage3Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("17b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageTwoThisMonth",new Mapped(pediOnArtStateinWhostage2Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");  
           dsd.addColumn("18b","rwandareports.tracnetreport.indicator.art.newPedsWhoStageOneThisMonth",new Mapped(pediOnArtStateinWhostage1Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),""); 
           dsd.addColumn("19b","rwandareports.tracnetreport.indicator.art.newPedsUndefinedWhoStageThisMonth",new Mapped(pediOnArtStateInWhoStageXInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("20b","rwandareports.tracnetreport.indicator.art.newFemaleAdultStartiArvThisMonth",new Mapped(femaleAdultsadultsOnArtStateInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("21b","rwandareports.tracnetreport.indicator.art.newMaleAdultStartiArvThisMonth",new Mapped(maleAdultsadultsOnArtStateInd,null),"");  
           dsd.addColumn("22b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageFourThisMonth",new Mapped(adultsOnArtStateinWhostage4Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");  
           dsd.addColumn("23b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageThreeThisMonth",new Mapped(adultsOnArtStateinWhostage3Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("24b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageTwoThisMonth",new Mapped(adultsOnArtStateinWhostage2Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("25b","rwandareports.tracnetreport.indicator.art.newAdultWhoStageOneThisMonth",new Mapped(adultsOnArtStateinWhostage1Ind,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),""); 
           dsd.addColumn("26b","rwandareports.tracnetreport.indicator.art.newAdultUndefinedWhoStageThisMonth",new Mapped(adultsOnArtStateinWhostageXInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),""); 
           dsd.addColumn("27b","rwandareports.tracnetreport.indicator.art.arvPedsFifteenInterruptTreatThisMonth",new Mapped(pediArtDrugsInteruptedThisMonthInd,null),"");  
           dsd.addColumn("28b","rwandareports.tracnetreport.indicator.art.arvAdultFifteenInterruptTreatThisMonth",new Mapped(adultArtDrugsInteruptedThisMonthInd,null),"");  
           dsd.addColumn("29b","rwandareports.tracnetreport.indicator.art.arvPedsDiedThisMonth",new Mapped(preArtPediAndDiedThisMonthInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("30b","rwandareports.tracnetreport.indicator.art.arvAdultDiedThisMonth",new Mapped(preArtAdultAndDiedThisMonthInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("31b","rwandareports.tracnetreport.indicator.art.arvPedsLostFollowupMoreThreeMonths",new Mapped(pedsOnArtLostAndwithHIVFormsInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},dateborn=${endDate},onOrAfterDate=${startDate-3m},onOrBeforeDate=${startDate}")),"");  
           dsd.addColumn("32b","rwandareports.tracnetreport.indicator.art.arvAdultLostFollowupMoreThreeMonths",new Mapped(adultOnArtLostAndwithHIVFormsInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},dateborn=${endDate},onOrAfterDate=${startDate-3m},onOrBeforeDate=${startDate}")),"");  
           dsd.addColumn("33b","rwandareports.tracnetreport.indicator.art.maleOnTreatTwelveAfterInitArv",new Mapped(malesOnArtLostforMoreThan12monthsInd,ParameterizableUtil.createParameterMappings("onDate=${startDate-12m}")),"");  
           dsd.addColumn("34b","rwandareports.tracnetreport.indicator.art.femaleOnTreatTwelveAfterInitArv",new Mapped(femalesOnArtLostforMoreThan12monthsInd,ParameterizableUtil.createParameterMappings("onDate=${startDate-12m}")),"");
           dsd.addColumn("35b","rwandareports.tracnetreport.indicator.art.arvPedsTransferredOutThisMonth",new Mapped(pedionARTTransferedOutDuringPInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("36b","rwandareports.tracnetreport.indicator.art.arvAdultTransferredOutThisMonth",new Mapped(adultsonARTTransferedOutDuringPInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}")),""); 
           dsd.addColumn("37b","rwandareports.tracnetreport.indicator.art.arvPedsTransferredInThisMonth",new Mapped(pedionWithTransferInFormInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}")),"");
           dsd.addColumn("38b","rwandareports.tracnetreport.indicator.art.arvAdultTransferreInThisMonth",new Mapped(adultsOnWithTransferEncounterInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrAfter=${startDate},onOrBefore=${endDate}")),"");  
           dsd.addColumn("39b","rwandareports.tracnetreport.indicator.art.patientsInARVTLostToFollowUpNotLostThisMonth",new Mapped(patientsinLostAndBackToProgramThismonthARTInd,ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")),"");   
           dsd.addColumn("40b","rwandareports.tracnetreport.indicator.art.pediInARVTstartingARVSecondLineThisMonth",new Mapped(pediOnArtStartedSecondLineThisMonthInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrBefore=${endDate}")),"");   
           dsd.addColumn("41b","rwandareports.tracnetreport.indicator.art.adultInARVTstartingARVSecondLineThisMonth",new Mapped(adultOnArtStartedSecondLineThisMonthInd,ParameterizableUtil.createParameterMappings("dateborn=${endDate},onOrBefore=${endDate}")),"");   
           
           return dsd;     
        }
        
        private void setupProperties() {
            adulthivProgram = gp.getProgram(GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            pediatrichivProgram = gp.getProgram(GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            pmtctcombinedMother = gp.getProgram(GlobalPropertiesManagement.PMTCT_COMBINED_MOTHER_PROGRAM);
            pmtctPregnancyProgram=gp.getProgram(GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
            adultOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            pediOnFollowing = gp.getProgramWorkflowState(GlobalPropertiesManagement.FOLLOWING_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            adultOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            pediOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            pmtctOnART = gp.getProgramWorkflowState(GlobalPropertiesManagement.ON_ANTIRETROVIRALS_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PMTCT_PREGNANCY_PROGRAM);
            adulttransferedOutState = gp.getProgramWorkflowState(GlobalPropertiesManagement.TRANSFERRED_OUT_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            peditransferedOutState = gp.getProgramWorkflowState(GlobalPropertiesManagement.TRANSFERRED_OUT_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            pediPreAndArtDiedState = gp.getProgramWorkflowState(GlobalPropertiesManagement.PATIENT_DIED_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.PEDI_HIV_PROGRAM);
            adultPreAndArtDiedState = gp.getProgramWorkflowState(GlobalPropertiesManagement.PATIENT_DIED_STATE,GlobalPropertiesManagement.TREATMENT_STATUS_WORKFLOW,GlobalPropertiesManagement.ADULT_HIV_PROGRAM);
            onOrAfterOnOrBefore.add("onOrAfter");
            onOrAfterOnOrBefore.add("onOrBefore");
            onOrAfterDateOnOrBeforeDate.add("onOrAfterDate");
            onOrAfterDateOnOrBeforeDate.add("onOrBeforeDate");
            tbScreeningtest = gp.getConcept(GlobalPropertiesManagement.TB_SCREENING_TEST);
            positiveStatus = gp.getConcept(GlobalPropertiesManagement.POSITIVE_HIV_TEST_ANSWER);
            reasonForExitingCare= gp.getConcept(GlobalPropertiesManagement.REASON_FOR_EXITING_CARE);
            patientDied = gp.getConcept(GlobalPropertiesManagement.PATIENT_DIED);
            patientTransferedOut=gp.getConcept(GlobalPropertiesManagement.TRASNFERED_OUT);
            patientDefaulted=MetadataLookup.getConcept("PIH:1743");
            allergypediForm = gp.getForm(GlobalPropertiesManagement.PEDI_ALLERGY);
            allergyadultForm = gp.getForm(GlobalPropertiesManagement.ADULT_ALLERGY);
            medicationForms.add(allergypediForm);
            medicationForms.add(allergyadultForm);
            clinicalEnountersIncLab = gp.getEncounterTypeList(GlobalPropertiesManagement.CLINICAL_ENCOUNTER_TYPES);
            pediAdnAdultEncounters=gp.getEncounterTypeList(GlobalPropertiesManagement.PEDIANDADULTHIV_ENCOUNTER_TYPES);
            adultHivForm = gp.getForm(GlobalPropertiesManagement.ADULT_FLOW_VISIT);
            pediHivform = gp.getForm(GlobalPropertiesManagement.PEDI_FLOW_VISIT);
            hivVisitsforms.add(adultHivForm);
            hivVisitsforms.add(pediHivform);
            transferinForm=gp.getForm(GlobalPropertiesManagement.HIV_TRANSFER_FORM);
            patientTransferEncounterType = gp.getEncounterType(GlobalPropertiesManagement.PATIENT_TRANSFER_ENCOUNTER);
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
            fluconazole=gp.getConcept(GlobalPropertiesManagement.FLUCONAZOLE_DRUG);
            dapsone=gp.getConcept(GlobalPropertiesManagement.DAPSONE_DRUG);
           
        }
        
}