package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.dataset.DataEntryQuantityReport;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.module.rwandareports.widget.AllLocation;
import org.openmrs.module.rwandareports.widget.LocationHierarchy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SetupDataEntryQuantityReport {
    protected final static Log log = LogFactory.getLog(SetupDataEntryDelayReport.class);

    private GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

    public void setup() throws Exception {

        ReportDefinition rd = createReportDefinition();
        ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "DataEntryQuantityReport.xls", "DataEntryQuantityReport",
                null);

        createDataSetDefinition(rd);

        Helper.saveReportDefinition(rd);

        Properties props = new Properties();
        props.put("repeatingSections", "sheet:1,row:10,dataset:dataSet");

        props.put("sortWeight","5000");
        design.setProperties(props);
        Helper.saveReportDesign(design);

    }

    public void delete() {
        ReportService rs = Context.getService(ReportService.class);
        for (ReportDesign rd : rs.getAllReportDesigns(false)) {
            if ("DataEntryQuantityReport".equals(rd.getName())) {
                rs.purgeReportDesign(rd);
            }
        }
        Helper.purgeReportDefinition("Data Entry Quantity Report");
    }

    private ReportDefinition createReportDefinition() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Data Entry Quantity Report");
        reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

        Parameter prog=new Parameter("program", "Program", Program.class);
        prog.setRequired(false);
        reportDefinition.addParameter(prog);
        Parameter enc=new Parameter("encounterType","Encounter Type", EncounterType.class);
        enc.setRequired(false);
        reportDefinition.addParameter(enc);

        return reportDefinition;
    }

    private void createDataSetDefinition(ReportDefinition rd) {

        DataEntryQuantityReport dataEntryDelay = new DataEntryQuantityReport();
        dataEntryDelay.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dataEntryDelay.addParameter(new Parameter("endDate", "End Date", Date.class));
        dataEntryDelay.addParameter(new Parameter("program", "Program", Program.class));
        dataEntryDelay.addParameter(new Parameter("encounterType","Encounter Type", EncounterType.class));




        Map<String, Object> mappings = new HashMap<String, Object>();
        mappings.put("endDate", "${endDate+1d}");
        mappings.put("startDate", "${startDate}");
        mappings.put("program", "${program}");
        mappings.put("encounterType", "${encounterType}");

        rd.addDataSetDefinition("dataSet", dataEntryDelay, mappings);
    }
}
