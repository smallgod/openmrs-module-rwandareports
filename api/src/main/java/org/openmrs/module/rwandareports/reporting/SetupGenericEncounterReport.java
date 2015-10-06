package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class SetupGenericEncounterReport {
		
	protected final static Log log = LogFactory.getLog(SetupGenericEncounterReport.class);
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

		
	public void setup() throws Exception {		
		ReportDefinition rd =createReportDefinition();		
		  ReportDesign design = Helper.xlsReportDesign(rd, null,null,"Generic Encounter Report.xls_");			
			Helper.saveReportDesign(design);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Generic Encounter Report.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("Generic Encounter Report");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Generic Encounter Report");	
		reportDefinition.addParameter(new Parameter("startDate", "From Date", Date.class));	
		reportDefinition.addParameter(new Parameter("endDate", "To Date", Date.class));
		Parameter encouterType = new Parameter("encounterTypes", "Encounter Type", EncounterType.class);
		Parameter form = new Parameter("forms", "Form", Form.class);
		encouterType.setRequired(false);
		form.setRequired(false);
		
		reportDefinition.addParameter(encouterType);
		reportDefinition.addParameter(form);
		
		createDataSetDefinition(reportDefinition);

		Helper.saveReportDefinition(reportDefinition);
		
		return reportDefinition;
	}
	
	private void createDataSetDefinition(ReportDefinition reportDefinition) {
		EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
		dsd.setName("dsd");
		dsd.setParameters(getParameters());

		BasicEncounterQuery rowFilter = new BasicEncounterQuery();
		rowFilter.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
		rowFilter.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
		Parameter encouterType = new Parameter("encounterTypes", "Encounter Type", EncounterType.class);
		Parameter form = new Parameter("forms", "Form", Form.class);
		encouterType.setRequired(false);
		form.setRequired(false);
		rowFilter.addParameter(encouterType);
		rowFilter.addParameter(form);
		MappedParametersEncounterQuery q = new MappedParametersEncounterQuery(rowFilter, ObjectUtil.toMap("onOrAfter=startDate,onOrBefore=endDate,encounterTypes=encounterTypes,forms=forms"));
		dsd.addRowFilter(Mapped.mapStraightThrough(q));

		
		reportDefinition.addDataSetDefinition("dsd",Mapped.mapStraightThrough(dsd));
		
	}
	
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("startDate", "From Date", Date.class));
		l.add(new Parameter("endDate", "To Date", Date.class));
		Parameter encouterType = new Parameter("encounterTypes", "Encounter Type", EncounterType.class);
		Parameter form = new Parameter("forms", "Form", Form.class);
		encouterType.setRequired(false);
		form.setRequired(false);
		l.add(encouterType);
		l.add(form);
		return l;
	}
	
}
