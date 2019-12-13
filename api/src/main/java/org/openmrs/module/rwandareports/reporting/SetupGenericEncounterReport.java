package org.openmrs.module.rwandareports.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
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
		  ReportDesign designExcel = Helper.createExcelDesign(rd,"Generic Encounter Report.xls_",true);
		  ReportDesign designCSV = Helper.createCsvReportDesign(rd,"Generic Encounter Report.csv_");
		  
			Helper.saveReportDesign(designExcel);
			Helper.saveReportDesign(designCSV);
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("Generic Encounter Report.xls_".equals(rd.getName()) || "Generic Encounter Report.csv_".equals(rd.getName())) {
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
		reportDefinition.addParameter(new Parameter("location", "Health Facility", Location.class));
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
		rowFilter.addParameter(new Parameter("location", "Health Facility", Location.class));
		Parameter encouterType = new Parameter("encounterTypes", "Encounter Type", EncounterType.class);
		Parameter form = new Parameter("forms", "Form", Form.class);
		encouterType.setRequired(false);
		form.setRequired(false);
		rowFilter.addParameter(encouterType);
		rowFilter.addParameter(form);

		BuiltInPatientDataLibrary patientData=new BuiltInPatientDataLibrary();

		dsd.addColumn("SYSTEM_PATIENT_ID", patientData.getPatientId(), "");
		dsd.addColumn("AGE", patientData.getAgeOnDateYears(), "");
		dsd.addColumn("GENDER", patientData.getGender(), "");
		dsd.addColumn("FAMILY NAME", patientData.getPreferredFamilyName(), "");
		dsd.addColumn("GIVEN NAME", patientData.getPreferredGivenName(), "");

		BuiltInEncounterDataLibrary encounterData=new BuiltInEncounterDataLibrary();

		dsd.addColumn("ENCOUNTER_ID", encounterData.getEncounterId(), "");
		dsd.addColumn("ENCOUNTER_TYPE", encounterData.getEncounterTypeName(), "");
		dsd.addColumn("ENCOUNTER_DATETIME", encounterData.getEncounterDatetime(), "");
		dsd.addColumn("LOCATION", encounterData.getLocationName(), "");

		ConvertedEncounterDataDefinition encounterCreatorGivenName=new ConvertedEncounterDataDefinition(new AuditInfoEncounterDataDefinition(),new PropertyConverter(AuditInfo.class, "creator.givenName"));
		dsd.addColumn("CREATOR_GIVEN_NAME", encounterCreatorGivenName, "");
		ConvertedEncounterDataDefinition encounterCreatorFamillynName=new ConvertedEncounterDataDefinition(new AuditInfoEncounterDataDefinition(),new PropertyConverter(AuditInfo.class, "creator.familyName"));
		dsd.addColumn("CREATOR_FAMILLY_NAME", encounterCreatorFamillynName, "");

		MappedParametersEncounterQuery q = new MappedParametersEncounterQuery(rowFilter, ObjectUtil.toMap("onOrAfter=${startDate},onOrBefore=${endDate},location=${location},encounterTypes=${encounterTypes},forms=${forms}"));
		dsd.addRowFilter(Mapped.mapStraightThrough(q));


		reportDefinition.addDataSetDefinition("dsd",Mapped.mapStraightThrough(dsd));
		
	}
	
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(new Parameter("startDate", "From Date", Date.class));
		l.add(new Parameter("endDate", "To Date", Date.class));
		l.add(new Parameter("location", "Health Facility", Location.class));
		Parameter encouterType = new Parameter("encounterTypes", "Encounter Type", EncounterType.class);
		Parameter form = new Parameter("forms", "Form", Form.class);
		encouterType.setRequired(false);
		form.setRequired(false);
		l.add(encouterType);
		l.add(form);
		return l;
	}
	
}
