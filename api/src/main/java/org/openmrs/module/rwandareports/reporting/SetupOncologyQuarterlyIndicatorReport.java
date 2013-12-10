package org.openmrs.module.rwandareports.reporting;
 
import java.util.Date;
import java.util.Properties;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.EncounterIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.dataset.LocationHierachyIndicatorDataSetDefinition;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;
 
public class SetupOncologyQuarterlyIndicatorReport {
        
        GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
        
        //properties
        private Program oncologyProgram;
        
       
        
        public void setup() throws Exception {
                
                setUpProperties();
                
                
                ReportDefinition rd = new ReportDefinition();
                rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                rd.addParameter(new Parameter("endDate", "End Date", Date.class));
                
                Properties properties = new Properties();
                properties.setProperty("hierarchyFields", "countyDistrict:District");
                rd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
                
                rd.setName("ONC-Indicator Report-Quarterly");
                
                rd.addDataSetDefinition(createQuarterlyLocationDataSet(),
                    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
                        
                
                Helper.saveReportDefinition(rd);
                
                
                ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd,"OncologyQuarterlyIndicatorReport.xls", "OncologyQuarterlyIndicatorReport", null);
                Properties props = new Properties();
                props.put("repeatingSections", "sheet:1,dataset:Encounter Quarterly Data Set");
                props.put("sortWeight","5000");
                design.setProperties(props);
                Helper.saveReportDesign(design);              
                
                
        }
        
        public void delete() {
                ReportService rs = Context.getService(ReportService.class);
                for (ReportDesign rd : rs.getAllReportDesigns(false)) {
                        if ("OncologyQuarterlyIndicatorReport".equals(rd.getName())) {
                                rs.purgeReportDesign(rd);
                        }
                }
                Helper.purgeReportDefinition("ONC-Indicator Report-Quarterly");
                
        }
        
        
        public LocationHierachyIndicatorDataSetDefinition createQuarterlyLocationDataSet() {
                
                LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
                        createQuarterlyEncounterBaseDataSet());
                ldsd.addBaseDefinition(createQuarterlyBaseDataSet());
                ldsd.setName("Encounter Quarterly Data Set");
                ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
                ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
                ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
                
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
        
        private void createQuarterlyIndicators(EncounterIndicatorDataSetDefinition dsd) {
               
                  
                
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
      
                
                AgeCohortDefinition over15Cohort = Cohorts.createOver15AgeCohort("ageQD: Over 15");
                AgeCohortDefinition under15Cohort = Cohorts.createUnder15AgeCohort("ageQD: Under 15");
                
               
       
        }
        
        private void setUpProperties() {
                oncologyProgram = gp.getProgram(GlobalPropertiesManagement.ONCOLOGY_PROGRAM);
                
        }
}