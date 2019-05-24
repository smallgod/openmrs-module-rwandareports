/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.rwandareports.reporting;

import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
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

import java.util.*;

public class SetupHMISIndicatorMonthlyReport {
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
	
	// properties

	private List<Form> OPDForms;

	private Form OPDForm;

	private  Concept caseStatus;

	private  Concept oldCase;

	private  Concept newCase;

	private int noneInsuranceID;

	private String hundrepercentInsuredInsuranceIDs;

	private String indigentsInsuranceIDs;

	private  int ICDConceptClassId;


	private  List<String> onOrAfterOnOrBefore =new ArrayList<String>();

	Properties properties = new Properties();

	public void setup() throws Exception {
		
		setUpProperties();


		//Monthly report set-up



		properties.setProperty("hierarchyFields", "countyDistrict:District");


		EncounterCohortDefinition patientWithOPDForm=Cohorts.createEncounterBasedOnForms("patientWithOPDForm",onOrAfterOnOrBefore,OPDForms);

// II. Outpatient Consultations/ Consultations Externes
		
		ReportDefinition monthlyRdII = createReportDefinition("District Hospital Monthly HMIS Report - II. Outpatient Consultations",properties);

		monthlyRdII.setBaseCohortDefinition(patientWithOPDForm,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdII.addDataSetDefinition(createEncounterCohortMonthlyLocationDataSetII(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdII);

		ReportDesign monthlyDesignII = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdII,
				"District_Hospital_Monthly_HMIS_Report_II.xls", "District Hospital Monthly HMIS Report_II (Excel)", null);
		Properties monthlyPropsII = new Properties();
		monthlyPropsII.put("repeatingSections", "sheet:1,dataset:Encounter Monthly Data Set Two");
		monthlyPropsII.put("sortWeight","5000");
		monthlyDesignII.setProperties(monthlyPropsII);
		Helper.saveReportDesign(monthlyDesignII);

// III. Mental Health/ Santé mentale


		ReportDefinition monthlyRdIII = createReportDefinition("District Hospital Monthly HMIS Report - III. Mental Health",properties);

		monthlyRdIII.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdIII.addDataSetDefinition(createCohortMonthlyLocationDataSetIII(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdIII);

		ReportDesign monthlyDesignIII = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdIII,
				"District_Hospital_Monthly_HMIS_Report_III.xls", "District Hospital Monthly HMIS Report_III (Excel)", null);
		Properties monthlyPropsIII = new Properties();
		monthlyPropsIII.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Three");
		monthlyPropsIII.put("sortWeight","5000");
		monthlyDesignIII.setProperties(monthlyPropsIII);
		Helper.saveReportDesign(monthlyDesignIII);


//IV. Chronic Diseases
		ReportDefinition monthlyRdIV = createReportDefinition("District Hospital Monthly HMIS Report - IV. Chronic Diseases",properties);

		monthlyRdIV.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdIV.addDataSetDefinition(createCohortMonthlyLocationDataSetIV(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdIV);

		ReportDesign monthlyDesignIV = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdIV,
				"District_Hospital_Monthly_HMIS_Report_IV.xls", "District Hospital Monthly HMIS Report_IV (Excel)", null);
		Properties monthlyPropsIV = new Properties();
		monthlyPropsIV.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Four");
		monthlyPropsIV.put("sortWeight","5000");
		monthlyDesignIV.setProperties(monthlyPropsIV);
		Helper.saveReportDesign(monthlyDesignIV);

// V. Other Cardiovascular and Kidney diseases


		ReportDefinition monthlyRdV = createReportDefinition("District Hospital Monthly HMIS Report - V. Other Cardiovascular and Kidney diseases",properties);

		monthlyRdV.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdV.addDataSetDefinition(createCohortMonthlyLocationDataSetV(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdV);

		ReportDesign monthlyDesignV = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdV,
				"District_Hospital_Monthly_HMIS_Report_V.xls", "District Hospital Monthly HMIS Report_V (Excel)", null);
		Properties monthlyPropsV = new Properties();
		monthlyPropsV.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Five");
		monthlyPropsV.put("sortWeight","5000");
		monthlyDesignV.setProperties(monthlyPropsV);
		Helper.saveReportDesign(monthlyDesignV);


// VI. Injuries

		ReportDefinition monthlyRdVI = createReportDefinition("District Hospital Monthly HMIS Report - VI. Injuries",properties);

		monthlyRdVI.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdVI.addDataSetDefinition(createCohortMonthlyLocationDataSetVI(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdVI);

		ReportDesign monthlyDesignVI = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdVI,
				"District_Hospital_Monthly_HMIS_Report_VI.xls", "District Hospital Monthly HMIS Report_VI (Excel)", null);
		Properties monthlyPropsVI = new Properties();
		monthlyPropsVI.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Six");
		monthlyPropsVI.put("sortWeight","5000");
		monthlyDesignVI.setProperties(monthlyPropsVI);
		Helper.saveReportDesign(monthlyDesignVI);

// VII. Palliative care

		ReportDefinition monthlyRdVII = createReportDefinition("District Hospital Monthly HMIS Report - VII. Palliative care",properties);

		monthlyRdVII.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdVII.addDataSetDefinition(createCohortMonthlyLocationDataSetVII(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdVII);

		ReportDesign monthlyDesignVII = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdVII,
				"District_Hospital_Monthly_HMIS_Report_VII.xls", "District Hospital Monthly HMIS Report_VII (Excel)", null);
		Properties monthlyPropsVII = new Properties();
		monthlyPropsVII.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Seven");
		monthlyPropsVII.put("sortWeight","5000");
		monthlyDesignVII.setProperties(monthlyPropsVII);
		Helper.saveReportDesign(monthlyDesignVII);

// VIII. Community Checkup

		ReportDefinition monthlyRdVIII = createReportDefinition("District Hospital Monthly HMIS Report - VIII. Community Checkup",properties);

		monthlyRdVIII.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdVIII.addDataSetDefinition(createCohortMonthlyLocationDataSetVIII(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdVIII);

		ReportDesign monthlyDesignVIII = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdVIII,
				"District_Hospital_Monthly_HMIS_Report_VIII.xls", "District Hospital Monthly HMIS Report_VIII (Excel)", null);
		Properties monthlyPropsVIII = new Properties();
		monthlyPropsVIII.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Eight");
		monthlyPropsVIII.put("sortWeight","5000");
		monthlyDesignVIII.setProperties(monthlyPropsVIII);
		Helper.saveReportDesign(monthlyDesignVIII);

// IX.  Cancer screening

		ReportDefinition monthlyRdIX = createReportDefinition("District Hospital Monthly HMIS Report - IX. Cancer screening",properties);

		monthlyRdIX.setBaseCohortDefinition(patientWithOPDForm,
				ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));

		monthlyRdIX.addDataSetDefinition(createCohortMonthlyLocationDataSetIX(),
				ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

		Helper.saveReportDefinition(monthlyRdIX);

		ReportDesign monthlyDesignIX = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRdIX,
				"District_Hospital_Monthly_HMIS_Report_IX.xls", "District Hospital Monthly HMIS Report_IX (Excel)", null);
		Properties monthlyPropsIX = new Properties();
		monthlyPropsIX.put("repeatingSections", "sheet:1,dataset:Monthly Cohort Data Set Nine");
		monthlyPropsIX.put("sortWeight","5000");
		monthlyDesignIX.setProperties(monthlyPropsIX);
		Helper.saveReportDesign(monthlyDesignIX);


	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("District Hospital Monthly HMIS Report_II (Excel)".equals(rd.getName()) || "District Hospital Monthly HMIS Report_III (Excel)".equals(rd.getName())
					|| "District Hospital Monthly HMIS Report_IV (Excel)".equals(rd.getName())
					|| "District Hospital Monthly HMIS Report_V (Excel)".equals(rd.getName())
					|| "District Hospital Monthly HMIS Report_VI (Excel)".equals(rd.getName())
					|| "District Hospital Monthly HMIS Report_VII (Excel)".equals(rd.getName())
					|| "District Hospital Monthly HMIS Report_VIII (Excel)".equals(rd.getName())
					|| "District Hospital Monthly HMIS Report_IX (Excel)".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - II. Outpatient Consultations");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - III. Mental Health");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - IV. Chronic Diseases");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - V. Other Cardiovascular and Kidney diseases");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - VI. Injuries");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - VII. Palliative care");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - VIII. Community Checkup");
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report - IX. Cancer screening");
	}


	public ReportDefinition createReportDefinition(String name, Properties properties){

		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		reportDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		reportDefinition.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		reportDefinition.setName(name);
		return reportDefinition;
	}


	

	
	//Create Monthly Encounter and Cohort Data set
	
	public LocationHierachyIndicatorDataSetDefinition createEncounterCohortMonthlyLocationDataSetII() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createEncounterMonthlyBaseDataSetII());
		ldsd.addBaseDefinition(createCohortMonthlyBaseDataSetII());
		ldsd.setName("Encounter Monthly Data Set Two");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}

	// create Monthly cohort Data set
	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetII() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsII(dsd);
		return dsd;
	}

	private EncounterIndicatorDataSetDefinition createEncounterMonthlyBaseDataSetII() {
		
		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createEncounterMonthlyIndicatorsII(eidsd);
		return eidsd;
	}



// III. Mental Health/ Santé mentale

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetIII() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetIII());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}



	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetIII() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsIII(dsd);
		return dsd;
	}

// III. Mental Health/ Santé mentale

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetIV() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetIV());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}



	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetIV() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsIV(dsd);
		return dsd;
	}

// V. Other Cardiovascular and Kidney diseases

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetV() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetV());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}



	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetV() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsV(dsd);
		return dsd;
	}


// VI. Injuries

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetVI() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetVI());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}



	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetVI() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsVI(dsd);
		return dsd;
	}

// VII. Palliative care

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetVII() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetVII());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}



	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetVII() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsVII(dsd);
		return dsd;
	}

// VIII. Community Checkup

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetVIII() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetVIII());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}

	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetVIII() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsVIII(dsd);
		return dsd;
	}

// IX.  Cancer screening

	public LocationHierachyIndicatorDataSetDefinition createCohortMonthlyLocationDataSetIX() {

		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
				createCohortMonthlyBaseDataSetIX());
		ldsd.setName("Monthly Cohort Data Set Three");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		return ldsd;
	}



	private CohortIndicatorDataSetDefinition createCohortMonthlyBaseDataSetIX() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createCohortMonthlyIndicatorsIX(dsd);
		return dsd;
	}


// II. Outpatient Consultations/ Consultations Externes: EncounterIndicatorDataSetDefinition

	private void createEncounterMonthlyIndicatorsII(EncounterIndicatorDataSetDefinition dsd) {

		// A) Outpatient Morbidity summary table/ Tableau synthétique Consultations externes

         //Outpatient visits/Consultations Externes


		//New Case

		// Male Under 5 years

		SqlEncounterQuery outpatientVisitsNewCaseMaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseMaleUnder5Years.setName("outpatientVisitsNewCaseMaleUnder5Years");
		outpatientVisitsNewCaseMaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" group by e.encounter_datetime");
		// adultHivVisits.setQuery("select e.encounter_id from encounter e, person p where e.encounter_type in ("+pediHIVEncounterType.getEncounterTypeId()+","+adultHIVEncounterType.getEncounterTypeId()+") and e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate and p.person_id = e.patient_id and DATEDIFF(:endDate , p.birthdate) >=5475 and e.voided=0 and p.voided=0");
		outpatientVisitsNewCaseMaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseMaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseMaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseMaleUnder5YearsIndicator.setName("outpatientVisitsNewCaseMaleUnder5YearsIndicator");
		outpatientVisitsNewCaseMaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseMaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseMaleUnder5YearsIndicator);


		// Female Under 5 years

		SqlEncounterQuery outpatientVisitsNewCaseFemaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseFemaleUnder5Years.setName("outpatientVisitsNewCaseFemaleUnder5Years");
		outpatientVisitsNewCaseFemaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" group by e.encounter_datetime");
		outpatientVisitsNewCaseFemaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseFemaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseFemaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseFemaleUnder5YearsIndicator.setName("outpatientVisitsNewCaseFemaleUnder5YearsIndicator");
		outpatientVisitsNewCaseFemaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseFemaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseFemaleUnder5YearsIndicator);


		// Male >=5 - 19 y

		SqlEncounterQuery outpatientVisitsNewCaseMaleBetween5And19Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseMaleBetween5And19Years.setName("outpatientVisitsNewCaseMaleBetween5And19Years");
		outpatientVisitsNewCaseMaleBetween5And19Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) >= 1825 and DATEDIFF(:endDate , p.birthdate) < 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" group by e.encounter_datetime");
		// adultHivVisits.setQuery("select e.encounter_id from encounter e, person p where e.encounter_type in ("+pediHIVEncounterType.getEncounterTypeId()+","+adultHIVEncounterType.getEncounterTypeId()+") and e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate and p.person_id = e.patient_id and DATEDIFF(:endDate , p.birthdate) >=5475 and e.voided=0 and p.voided=0");
		outpatientVisitsNewCaseMaleBetween5And19Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseMaleBetween5And19Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseMaleBetween5And19YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseMaleBetween5And19YearsIndicator.setName("outpatientVisitsNewCaseMaleBetween5And19YearsIndicator");
		outpatientVisitsNewCaseMaleBetween5And19YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseMaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseMaleBetween5And19YearsIndicator);


		// Female >=5 - 19 y

		SqlEncounterQuery outpatientVisitsNewCaseFemaleBetween5And19Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseFemaleBetween5And19Years.setName("outpatientVisitsNewCaseFemaleBetween5And19Years");
		outpatientVisitsNewCaseFemaleBetween5And19Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) >= 1825 and DATEDIFF(:endDate , p.birthdate) < 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" group by e.encounter_datetime");
		outpatientVisitsNewCaseFemaleBetween5And19Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseFemaleBetween5And19Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseFemaleBetween5And19YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseFemaleBetween5And19YearsIndicator.setName("outpatientVisitsNewCaseFemaleBetween5And19YearsIndicator");
		outpatientVisitsNewCaseFemaleBetween5And19YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseFemaleBetween5And19Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseFemaleBetween5And19YearsIndicator);




		// Male >=20 y

		SqlEncounterQuery outpatientVisitsNewCaseMaleEqualAndAbove20Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseMaleEqualAndAbove20Years.setName("outpatientVisitsNewCaseMaleEqualAndAbove20Years");
		outpatientVisitsNewCaseMaleEqualAndAbove20Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) >= 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" group by e.encounter_datetime");
		outpatientVisitsNewCaseMaleEqualAndAbove20Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseMaleEqualAndAbove20Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseMaleEqualAndAbove20YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseMaleEqualAndAbove20YearsIndicator.setName("outpatientVisitsNewCaseMaleEqualAndAbove20YearsIndicator");
		outpatientVisitsNewCaseMaleEqualAndAbove20YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseMaleEqualAndAbove20Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseMaleEqualAndAbove20YearsIndicator);


		// Female >=20 y

		SqlEncounterQuery outpatientVisitsNewCaseFemaleEqualAndAbove20Years=new SqlEncounterQuery();
		outpatientVisitsNewCaseFemaleEqualAndAbove20Years.setName("outpatientVisitsNewCaseFemaleEqualAndAbove20Years");
		outpatientVisitsNewCaseFemaleEqualAndAbove20Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) >= 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" group by e.encounter_datetime");
		outpatientVisitsNewCaseFemaleEqualAndAbove20Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsNewCaseFemaleEqualAndAbove20Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsNewCaseFemaleEqualAndAbove20YearsIndicator = new EncounterIndicator();
		outpatientVisitsNewCaseFemaleEqualAndAbove20YearsIndicator.setName("outpatientVisitsNewCaseFemaleEqualAndAbove20YearsIndicator");
		outpatientVisitsNewCaseFemaleEqualAndAbove20YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsNewCaseFemaleEqualAndAbove20Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsNewCaseFemaleEqualAndAbove20YearsIndicator);





		//===========================================
		//Old Case									//
		//===========================================

		// Male Under 5 years

		SqlEncounterQuery outpatientVisitsoldCaseMaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseMaleUnder5Years.setName("outpatientVisitsoldCaseMaleUnder5Years");
		outpatientVisitsoldCaseMaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase+" group by e.encounter_datetime");
		// adultHivVisits.setQuery("select e.encounter_id from encounter e, person p where e.encounter_type in ("+pediHIVEncounterType.getEncounterTypeId()+","+adultHIVEncounterType.getEncounterTypeId()+") and e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate and p.person_id = e.patient_id and DATEDIFF(:endDate , p.birthdate) >=5475 and e.voided=0 and p.voided=0");
		outpatientVisitsoldCaseMaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseMaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseMaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseMaleUnder5YearsIndicator.setName("outpatientVisitsoldCaseMaleUnder5YearsIndicator");
		outpatientVisitsoldCaseMaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseMaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseMaleUnder5YearsIndicator);


		// Female Under 5 years

		SqlEncounterQuery outpatientVisitsoldCaseFemaleUnder5Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseFemaleUnder5Years.setName("outpatientVisitsoldCaseFemaleUnder5Years");
		outpatientVisitsoldCaseFemaleUnder5Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) < 1825 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase+" group by e.encounter_datetime");
		outpatientVisitsoldCaseFemaleUnder5Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseFemaleUnder5Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseFemaleUnder5YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseFemaleUnder5YearsIndicator.setName("outpatientVisitsoldCaseFemaleUnder5YearsIndicator");
		outpatientVisitsoldCaseFemaleUnder5YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseFemaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseFemaleUnder5YearsIndicator);


		// Male >=5 - 19 y

		SqlEncounterQuery outpatientVisitsoldCaseMaleBetween5And19Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseMaleBetween5And19Years.setName("outpatientVisitsoldCaseMaleBetween5And19Years");
		outpatientVisitsoldCaseMaleBetween5And19Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) >= 1825 and DATEDIFF(:endDate , p.birthdate) < 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase+" group by e.encounter_datetime");
		// adultHivVisits.setQuery("select e.encounter_id from encounter e, person p where e.encounter_type in ("+pediHIVEncounterType.getEncounterTypeId()+","+adultHIVEncounterType.getEncounterTypeId()+") and e.encounter_datetime >= :startDate and e.encounter_datetime <= :endDate and p.person_id = e.patient_id and DATEDIFF(:endDate , p.birthdate) >=5475 and e.voided=0 and p.voided=0");
		outpatientVisitsoldCaseMaleBetween5And19Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseMaleBetween5And19Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseMaleBetween5And19YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseMaleBetween5And19YearsIndicator.setName("outpatientVisitsoldCaseMaleBetween5And19YearsIndicator");
		outpatientVisitsoldCaseMaleBetween5And19YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseMaleUnder5Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseMaleBetween5And19YearsIndicator);


		// Female >=5 - 19 y

		SqlEncounterQuery outpatientVisitsoldCaseFemaleBetween5And19Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseFemaleBetween5And19Years.setName("outpatientVisitsoldCaseFemaleBetween5And19Years");
		outpatientVisitsoldCaseFemaleBetween5And19Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) >= 1825 and DATEDIFF(:endDate , p.birthdate) < 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase+" group by e.encounter_datetime");
		outpatientVisitsoldCaseFemaleBetween5And19Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseFemaleBetween5And19Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseFemaleBetween5And19YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseFemaleBetween5And19YearsIndicator.setName("outpatientVisitsoldCaseFemaleBetween5And19YearsIndicator");
		outpatientVisitsoldCaseFemaleBetween5And19YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseFemaleBetween5And19Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseFemaleBetween5And19YearsIndicator);




		// Male >=20 y

		SqlEncounterQuery outpatientVisitsoldCaseMaleEqualAndAbove20Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseMaleEqualAndAbove20Years.setName("outpatientVisitsoldCaseMaleEqualAndAbove20Years");
		outpatientVisitsoldCaseMaleEqualAndAbove20Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='M' and DATEDIFF(:endDate , p.birthdate) >= 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase+" group by e.encounter_datetime");
		outpatientVisitsoldCaseMaleEqualAndAbove20Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseMaleEqualAndAbove20Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseMaleEqualAndAbove20YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseMaleEqualAndAbove20YearsIndicator.setName("outpatientVisitsoldCaseMaleEqualAndAbove20YearsIndicator");
		outpatientVisitsoldCaseMaleEqualAndAbove20YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseMaleEqualAndAbove20Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseMaleEqualAndAbove20YearsIndicator);


		// Female >=20 y

		SqlEncounterQuery outpatientVisitsoldCaseFemaleEqualAndAbove20Years=new SqlEncounterQuery();
		outpatientVisitsoldCaseFemaleEqualAndAbove20Years.setName("outpatientVisitsoldCaseFemaleEqualAndAbove20Years");
		outpatientVisitsoldCaseFemaleEqualAndAbove20Years.setQuery("select e.encounter_id from encounter e, obs o, person p where e.encounter_id=o.encounter_id and o.person_id=p.person_id and p.gender='F' and DATEDIFF(:endDate , p.birthdate) >= 7300 and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+oldCase+" group by e.encounter_datetime");
		outpatientVisitsoldCaseFemaleEqualAndAbove20Years.addParameter(new Parameter("startDate", "startDate", Date.class));
		outpatientVisitsoldCaseFemaleEqualAndAbove20Years.addParameter(new Parameter("endDate", "endDate", Date.class));


		EncounterIndicator outpatientVisitsoldCaseFemaleEqualAndAbove20YearsIndicator = new EncounterIndicator();
		outpatientVisitsoldCaseFemaleEqualAndAbove20YearsIndicator.setName("outpatientVisitsoldCaseFemaleEqualAndAbove20YearsIndicator");
		outpatientVisitsoldCaseFemaleEqualAndAbove20YearsIndicator.setEncounterQuery(new Mapped<EncounterQuery>(outpatientVisitsoldCaseFemaleEqualAndAbove20Years,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")));

		dsd.addColumn(outpatientVisitsoldCaseFemaleEqualAndAbove20YearsIndicator);



	}


	// II. Outpatient Consultations/ Consultations Externes : CohortIndicatorDataSetDefinition
private void createCohortMonthlyIndicatorsII(CohortIndicatorDataSetDefinition dsd) {


		//B) Health insurance status of new cases/ Assurance maladies pour nouveaux cas

SqlCohortDefinition newcasePatientsWithInsurance=new SqlCohortDefinition("select o.person_id from obs o, moh_bill_insurance_policy ip where o.person_id=ip.owner and DATEDIFF(ip.expiration_date , o.obs_datetime) >= 0 and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o.voided=0  and ip.insurance_id!="+ noneInsuranceID +" group by o.person_id");
newcasePatientsWithInsurance.setName("newcasePatientsWithInsurance");
newcasePatientsWithInsurance.addParameter(new Parameter("startDate", "startDate", Date.class));
newcasePatientsWithInsurance.addParameter(new Parameter("endDate", "endDate", Date.class));


CohortIndicator newcasePatientsWithInsuranceIndicator = Indicators.newCountIndicator("newcasePatientsWithInsuranceInicator",newcasePatientsWithInsurance,
				ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


dsd.addColumn("II.B.1","Insured  (Mutuelle or other insurance members)/Assurés  (Mutuelle ou autres assurances)",new Mapped(newcasePatientsWithInsuranceIndicator,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");




String insurance_card_no="CONCAT('%', ip.insurance_card_no ,'%')";

		//SqlCohortDefinition nonPayingNewcasesPatient=new SqlCohortDefinition("select o.person_id from obs o, moh_bill_insurance_policy ip where o.person_id=ip.owner and DATEDIFF(ip.expiration_date , o.obs_datetime) >= 0 and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o.voided=0 and gb.bill_identifier like "+insurance_card_no+" and gb.global_amount=0 and gb.closed=1 and gb.created_date>= :startDate and gb.closing_date<= :endDate and gb.voided=0 group by o.person_id");
		SqlCohortDefinition nonPayingNewcasesPatient=new SqlCohortDefinition("select o.person_id from obs o, moh_bill_insurance_policy ip,moh_bill_global_bill gb where o.person_id=ip.owner and DATEDIFF(ip.expiration_date , o.obs_datetime) >= 0 and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" and o.obs_datetime>=:startDate and o.obs_datetime<=:endDate and o.voided=0 and gb.bill_identifier like "+insurance_card_no+" and gb.global_amount=0 and gb.closed=1 and gb.created_date>=:startDate and gb.closing_date<=:endDate and gb.voided=0 group by o.person_id");
		nonPayingNewcasesPatient.setName("nonPayingNewcasesPatient");
		nonPayingNewcasesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		nonPayingNewcasesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));



		CohortIndicator nonPayingNewcasesPatientIndicator = Indicators.newCountIndicator("nonPayingNewcasesPatientIndicator",nonPayingNewcasesPatient,
				ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


		dsd.addColumn("II.B.2","Non-Paying New cases/ Nouveaux cas non-payant",new Mapped(nonPayingNewcasesPatientIndicator,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");





		SqlCohortDefinition indigentPatientsnonPayingNewcasesPatient=new SqlCohortDefinition("select o.person_id from obs o, moh_bill_insurance_policy ip,moh_bill_global_bill gb where o.person_id=ip.owner and DATEDIFF(ip.expiration_date , o.obs_datetime) >= 0 and o.concept_id="+caseStatus.getConceptId()+" and o.value_coded="+newCase+" and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o.voided=0  and ip.insurance_id in ("+indigentsInsuranceIDs+") and gb.bill_identifier like "+insurance_card_no+" and gb.global_amount=0 and gb.closed=1 and gb.created_date>= :startDate and gb.closing_date<= :endDate and gb.voided=0 group by o.person_id");
		indigentPatientsnonPayingNewcasesPatient.setName("indigentPatientsnonPayingNewcasesPatient");
		indigentPatientsnonPayingNewcasesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		indigentPatientsnonPayingNewcasesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));


		CohortIndicator indigentPatientsnonPayingNewcasesPatientIndicator = Indicators.newCountIndicator("indigentPatientsnonPayingNewcasesPatientIndicator",indigentPatientsnonPayingNewcasesPatient,
				ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));


		dsd.addColumn("II.B.3","Number of Indigent new cases/ Nombre d’indigents parmi les non payants",new Mapped(indigentPatientsnonPayingNewcasesPatientIndicator,ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}")), "");




		// E) New cases of priority health problems in General OPD/Nouveaux cas de maladies (Causes majeures de Consultation)


		AgeCohortDefinition patientBelowFiveYear = patientWithAgeBelow(5);

		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("male Patients");
		males.setMaleIncluded(true);



		SqlCohortDefinition foodPoisoningPatient= patientWithICDCodeObsByStartDateAndEndDate("A05");

				//new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%A05%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

	//SqlCohortDefinition foodPoisoningPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition foodPoisoningPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		/*foodPoisoningPatient.setName("foodPoisoningPatient");
		foodPoisoningPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		foodPoisoningPatient.addParameter(new Parameter("endDate", "endDate", Date.class));*/


		CompositionCohortDefinition maleBelow5FoodPoisoningPatient = new CompositionCohortDefinition();
		maleBelow5FoodPoisoningPatient.setName("maleBelow5FoodPoisoningPatient");
		maleBelow5FoodPoisoningPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBelow5FoodPoisoningPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBelow5FoodPoisoningPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBelow5FoodPoisoningPatient.getSearches().put("1", new Mapped<CohortDefinition>(foodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBelow5FoodPoisoningPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBelow5FoodPoisoningPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBelow5FoodPoisoningPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBelow5FoodPoisoningPatientIndicator = Indicators.newCohortIndicator("maleBelow5FoodPoisoningPatientIndicator",
				maleBelow5FoodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.4.M.5", "Food poisoning/ Intoxication alimentaire Male Under 5 years", new Mapped(maleBelow5FoodPoisoningPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition femaleBelow5FoodPoisoningPatient = new CompositionCohortDefinition();
		femaleBelow5FoodPoisoningPatient.setName("FemaleBelow5FoodPoisoningPatient");
		femaleBelow5FoodPoisoningPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBelow5FoodPoisoningPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBelow5FoodPoisoningPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBelow5FoodPoisoningPatient.getSearches().put("1", new Mapped<CohortDefinition>(foodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBelow5FoodPoisoningPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBelow5FoodPoisoningPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBelow5FoodPoisoningPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBelow5FoodPoisoningPatientIndicator = Indicators.newCohortIndicator("femaleBelow5FoodPoisoningPatientIndicator",
				femaleBelow5FoodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.4.F.5", "Food poisoning/ Intoxication alimentaire Female Under 5 years", new Mapped(femaleBelow5FoodPoisoningPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		AgeCohortDefinition patientBetweenFiveAndNineteenYears = patientWithAgeBetween(5,19);


		CompositionCohortDefinition betweenFiveAndNineteenYearsFoodPoisoningMalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.setName("betweenFiveAndNineteenYearsFoodPoisoningMalePatients");
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(foodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsFoodPoisoningMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator betweenFiveAndNineteenYearsFoodPoisoningMalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsFoodPoisoningMalePatientsIndicator",
				betweenFiveAndNineteenYearsFoodPoisoningMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.4.M.5_19", "Food poisoning/ Intoxication alimentaire Male Between 5 and 19 years", new Mapped(betweenFiveAndNineteenYearsFoodPoisoningMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsFoodPoisoningFemalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.setName("betweenFiveAndNineteenYearsFoodPoisoningFemalePatients");
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(foodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsFoodPoisoningFemalePatients.setCompositionString("1 and 2 and (not 3)");



		CohortIndicator betweenFiveAndNineteenYearsFoodPoisoningFemalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsFoodPoisoningFemalePatientsIndicator",
				betweenFiveAndNineteenYearsFoodPoisoningFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.4.F.5_19", "Food poisoning/ Intoxication alimentaire Female Between  5 and 19 years", new Mapped(betweenFiveAndNineteenYearsFoodPoisoningFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		AgeCohortDefinition patientAbove20Years = patientWithAgeAbove(20);

		CompositionCohortDefinition above20YearsFoodPoisoningMalePatients = new CompositionCohortDefinition();
		above20YearsFoodPoisoningMalePatients.setName("above20YearsFoodPoisoningMalePatients");
		above20YearsFoodPoisoningMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsFoodPoisoningMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsFoodPoisoningMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsFoodPoisoningMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(foodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsFoodPoisoningMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsFoodPoisoningMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsFoodPoisoningMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator above20YearsFoodPoisoningMalePatientsIndicator = Indicators.newCohortIndicator("above20YearsFoodPoisoningMalePatientsIndicator",
				above20YearsFoodPoisoningMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.4.M.20", "Food poisoning/ Intoxication alimentaire Male above 20 years", new Mapped(above20YearsFoodPoisoningMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		CompositionCohortDefinition above20YearsFoodPoisoningFemalePatients = new CompositionCohortDefinition();
		above20YearsFoodPoisoningFemalePatients.setName("above20YearsFoodPoisoningFemalePatients");
		above20YearsFoodPoisoningFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsFoodPoisoningFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsFoodPoisoningFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsFoodPoisoningFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(foodPoisoningPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsFoodPoisoningFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsFoodPoisoningFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsFoodPoisoningFemalePatients.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator above20YearsFoodPoisoningFemalePatientsIndicator = Indicators.newCohortIndicator("above20YearsFoodPoisoningFemalePatientsIndicator",
				above20YearsFoodPoisoningFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.4.F.20", "Food poisoning/ Intoxication alimentaire Female above 20 years", new Mapped(above20YearsFoodPoisoningFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//===================================================================================



		SqlCohortDefinition earInfectionsPatient= patientWithICDCodeObsByStartDateAndEndDate("H65");

		/*		new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%H65%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

		//SqlCohortDefinition earInfectionsPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition earInfectionsPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		earInfectionsPatient.setName("earInfectionsPatient");
		earInfectionsPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		earInfectionsPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
*/

		CompositionCohortDefinition maleBelow5earInfectionsPatient = new CompositionCohortDefinition();
		maleBelow5earInfectionsPatient.setName("maleBelow5earInfectionsPatient");
		maleBelow5earInfectionsPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBelow5earInfectionsPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBelow5earInfectionsPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBelow5earInfectionsPatient.getSearches().put("1", new Mapped<CohortDefinition>(earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBelow5earInfectionsPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBelow5earInfectionsPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBelow5earInfectionsPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBelow5earInfectionsPatientIndicator = Indicators.newCohortIndicator("maleBelow5earInfectionsPatientIndicator",
				maleBelow5earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.5.M.5", "Ear infections/ Infections de l’oreille Male Under 5 years", new Mapped(maleBelow5earInfectionsPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition femaleBelow5earInfectionsPatient = new CompositionCohortDefinition();
		femaleBelow5earInfectionsPatient.setName("FemaleBelow5earInfectionsPatient");
		femaleBelow5earInfectionsPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBelow5earInfectionsPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBelow5earInfectionsPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBelow5earInfectionsPatient.getSearches().put("1", new Mapped<CohortDefinition>(earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBelow5earInfectionsPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBelow5earInfectionsPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBelow5earInfectionsPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBelow5earInfectionsPatientIndicator = Indicators.newCohortIndicator("femaleBelow5earInfectionsPatientIndicator",
				femaleBelow5earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.5.F.5", "Ear infections/ Infections de l’oreille Female Under 5 years", new Mapped(femaleBelow5earInfectionsPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsearInfectionsMalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsearInfectionsMalePatients.setName("betweenFiveAndNineteenYearsearInfectionsMalePatients");
		betweenFiveAndNineteenYearsearInfectionsMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsearInfectionsMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsearInfectionsMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsearInfectionsMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsearInfectionsMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsearInfectionsMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsearInfectionsMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator betweenFiveAndNineteenYearsearInfectionsMalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsearInfectionsMalePatientsIndicator",
				betweenFiveAndNineteenYearsearInfectionsMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.5.M.5_19", "Ear infections/ Infections de l’oreille Male Between 5 and 19 years", new Mapped(betweenFiveAndNineteenYearsearInfectionsMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsearInfectionsFemalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.setName("betweenFiveAndNineteenYearsearInfectionsFemalePatients");
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsearInfectionsFemalePatients.setCompositionString("1 and 2 and (not 3)");



		CohortIndicator betweenFiveAndNineteenYearsearInfectionsFemalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsearInfectionsFemalePatientsIndicator",
				betweenFiveAndNineteenYearsearInfectionsFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.5.F.5_19", "Ear infections/ Infections de l’oreille Female Between  5 and 19 years", new Mapped(betweenFiveAndNineteenYearsearInfectionsFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition above20YearsearInfectionsMalePatients = new CompositionCohortDefinition();
		above20YearsearInfectionsMalePatients.setName("above20YearsearInfectionsMalePatients");
		above20YearsearInfectionsMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsearInfectionsMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsearInfectionsMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsearInfectionsMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsearInfectionsMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsearInfectionsMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsearInfectionsMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator above20YearsearInfectionsMalePatientsIndicator = Indicators.newCohortIndicator("above20YearsearInfectionsMalePatientsIndicator",
				above20YearsearInfectionsMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.5.M.20", "Ear infections/ Infections de l’oreille Male above 20 years", new Mapped(above20YearsearInfectionsMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		CompositionCohortDefinition above20YearsearInfectionsFemalePatients = new CompositionCohortDefinition();
		above20YearsearInfectionsFemalePatients.setName("above20YearsearInfectionsFemalePatients");
		above20YearsearInfectionsFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsearInfectionsFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsearInfectionsFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsearInfectionsFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(earInfectionsPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsearInfectionsFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsearInfectionsFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsearInfectionsFemalePatients.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator above20YearsearInfectionsFemalePatientsIndicator = Indicators.newCohortIndicator("above20YearsearInfectionsFemalePatientsIndicator",
				above20YearsearInfectionsFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.5.F.20", "Ear infections/ Infections de l’oreille Female above 20 years", new Mapped(above20YearsearInfectionsFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

//=========================================================================================================




		SqlCohortDefinition schistosomiasisPatient= patientWithICDCodeObsByStartDateAndEndDate("B65");

		/*		new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%B65%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

		//SqlCohortDefinition schistosomiasisPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition schistosomiasisPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		schistosomiasisPatient.setName("schistosomiasisPatient");
		schistosomiasisPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		schistosomiasisPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
*/

		CompositionCohortDefinition maleBelow5schistosomiasisPatient = new CompositionCohortDefinition();
		maleBelow5schistosomiasisPatient.setName("maleBelow5schistosomiasisPatient");
		maleBelow5schistosomiasisPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBelow5schistosomiasisPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBelow5schistosomiasisPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBelow5schistosomiasisPatient.getSearches().put("1", new Mapped<CohortDefinition>(schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBelow5schistosomiasisPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBelow5schistosomiasisPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBelow5schistosomiasisPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBelow5schistosomiasisPatientIndicator = Indicators.newCohortIndicator("maleBelow5schistosomiasisPatientIndicator",
				maleBelow5schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.6.M.5", "Schistosomiasis/ Schistosomiasis Male Under 5 years", new Mapped(maleBelow5schistosomiasisPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition femaleBelow5schistosomiasisPatient = new CompositionCohortDefinition();
		femaleBelow5schistosomiasisPatient.setName("FemaleBelow5schistosomiasisPatient");
		femaleBelow5schistosomiasisPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBelow5schistosomiasisPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBelow5schistosomiasisPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBelow5schistosomiasisPatient.getSearches().put("1", new Mapped<CohortDefinition>(schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBelow5schistosomiasisPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBelow5schistosomiasisPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBelow5schistosomiasisPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBelow5schistosomiasisPatientIndicator = Indicators.newCohortIndicator("femaleBelow5schistosomiasisPatientIndicator",
				femaleBelow5schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.6.F.5", "Schistosomiasis/ Schistosomiasis Female Under 5 years", new Mapped(femaleBelow5schistosomiasisPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsschistosomiasisMalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.setName("betweenFiveAndNineteenYearsschistosomiasisMalePatients");
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsschistosomiasisMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator betweenFiveAndNineteenYearsschistosomiasisMalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsschistosomiasisMalePatientsIndicator",
				betweenFiveAndNineteenYearsschistosomiasisMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.6.M.5_19", "Schistosomiasis/ Schistosomiasis Male Between 5 and 19 years", new Mapped(betweenFiveAndNineteenYearsschistosomiasisMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsschistosomiasisFemalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.setName("betweenFiveAndNineteenYearsschistosomiasisFemalePatients");
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsschistosomiasisFemalePatients.setCompositionString("1 and 2 and (not 3)");



		CohortIndicator betweenFiveAndNineteenYearsschistosomiasisFemalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsschistosomiasisFemalePatientsIndicator",
				betweenFiveAndNineteenYearsschistosomiasisFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.6.F.5_19", "Schistosomiasis/ Schistosomiasis Female Between  5 and 19 years", new Mapped(betweenFiveAndNineteenYearsschistosomiasisFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition above20YearsschistosomiasisMalePatients = new CompositionCohortDefinition();
		above20YearsschistosomiasisMalePatients.setName("above20YearsschistosomiasisMalePatients");
		above20YearsschistosomiasisMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsschistosomiasisMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsschistosomiasisMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsschistosomiasisMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsschistosomiasisMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsschistosomiasisMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsschistosomiasisMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator above20YearsschistosomiasisMalePatientsIndicator = Indicators.newCohortIndicator("above20YearsschistosomiasisMalePatientsIndicator",
				above20YearsschistosomiasisMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.6.M.20", "Schistosomiasis/ Schistosomiasis Male above 20 years", new Mapped(above20YearsschistosomiasisMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		CompositionCohortDefinition above20YearsschistosomiasisFemalePatients = new CompositionCohortDefinition();
		above20YearsschistosomiasisFemalePatients.setName("above20YearsschistosomiasisFemalePatients");
		above20YearsschistosomiasisFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsschistosomiasisFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsschistosomiasisFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsschistosomiasisFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(schistosomiasisPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsschistosomiasisFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsschistosomiasisFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsschistosomiasisFemalePatients.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator above20YearsschistosomiasisFemalePatientsIndicator = Indicators.newCohortIndicator("above20YearsschistosomiasisFemalePatientsIndicator",
				above20YearsschistosomiasisFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.6.F.20", "Schistosomiasis/ Schistosomiasis Female above 20 years", new Mapped(above20YearsschistosomiasisFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		//==============================================================================


		SqlCohortDefinition ascarisLumbricoidesPatient= patientWithICDCodeObsByStartDateAndEndDate("B77");


		/*		new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%B77%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

		//SqlCohortDefinition ascarisLumbricoidesPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition ascarisLumbricoidesPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		ascarisLumbricoidesPatient.setName("ascarisLumbricoidesPatient");
		ascarisLumbricoidesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		ascarisLumbricoidesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
*/

		CompositionCohortDefinition maleBelow5ascarisLumbricoidesPatient = new CompositionCohortDefinition();
		maleBelow5ascarisLumbricoidesPatient.setName("maleBelow5ascarisLumbricoidesPatient");
		maleBelow5ascarisLumbricoidesPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBelow5ascarisLumbricoidesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBelow5ascarisLumbricoidesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBelow5ascarisLumbricoidesPatient.getSearches().put("1", new Mapped<CohortDefinition>(ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBelow5ascarisLumbricoidesPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBelow5ascarisLumbricoidesPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBelow5ascarisLumbricoidesPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBelow5ascarisLumbricoidesPatientIndicator = Indicators.newCohortIndicator("maleBelow5ascarisLumbricoidesPatientIndicator",
				maleBelow5ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.7.M.5", "Ascaris lumbricoides/ Ascaris lumbricoides Male Under 5 years", new Mapped(maleBelow5ascarisLumbricoidesPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition femaleBelow5ascarisLumbricoidesPatient = new CompositionCohortDefinition();
		femaleBelow5ascarisLumbricoidesPatient.setName("FemaleBelow5ascarisLumbricoidesPatient");
		femaleBelow5ascarisLumbricoidesPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBelow5ascarisLumbricoidesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBelow5ascarisLumbricoidesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBelow5ascarisLumbricoidesPatient.getSearches().put("1", new Mapped<CohortDefinition>(ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBelow5ascarisLumbricoidesPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBelowFiveYear, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBelow5ascarisLumbricoidesPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBelow5ascarisLumbricoidesPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBelow5ascarisLumbricoidesPatientIndicator = Indicators.newCohortIndicator("femaleBelow5ascarisLumbricoidesPatientIndicator",
				femaleBelow5ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.7.F.5", "Ascaris lumbricoides/ Ascaris lumbricoides Female Under 5 years", new Mapped(femaleBelow5ascarisLumbricoidesPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.setName("betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients");
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator betweenFiveAndNineteenYearsascarisLumbricoidesMalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsascarisLumbricoidesMalePatientsIndicator",
				betweenFiveAndNineteenYearsascarisLumbricoidesMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.7.M.5_19", "Ascaris lumbricoides/ Ascaris lumbricoides Male Between 5 and 19 years", new Mapped(betweenFiveAndNineteenYearsascarisLumbricoidesMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");



		CompositionCohortDefinition betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients = new CompositionCohortDefinition();
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.setName("betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients");
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenFiveAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients.setCompositionString("1 and 2 and (not 3)");



		CohortIndicator betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatientsIndicator = Indicators.newCohortIndicator("betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatientsIndicator",
				betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.7.F.5_19", "Ascaris lumbricoides/ Ascaris lumbricoides Female Between  5 and 19 years", new Mapped(betweenFiveAndNineteenYearsascarisLumbricoidesFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		CompositionCohortDefinition above20YearsascarisLumbricoidesMalePatients = new CompositionCohortDefinition();
		above20YearsascarisLumbricoidesMalePatients.setName("above20YearsascarisLumbricoidesMalePatients");
		above20YearsascarisLumbricoidesMalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsascarisLumbricoidesMalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsascarisLumbricoidesMalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsascarisLumbricoidesMalePatients.getSearches().put("1", new Mapped<CohortDefinition>(ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsascarisLumbricoidesMalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsascarisLumbricoidesMalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsascarisLumbricoidesMalePatients.setCompositionString("1 and 2 and 3");

		CohortIndicator above20YearsascarisLumbricoidesMalePatientsIndicator = Indicators.newCohortIndicator("above20YearsascarisLumbricoidesMalePatientsIndicator",
				above20YearsascarisLumbricoidesMalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.7.M.20", "Ascaris lumbricoides/ Ascaris lumbricoides Male above 20 years", new Mapped(above20YearsascarisLumbricoidesMalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		CompositionCohortDefinition above20YearsascarisLumbricoidesFemalePatients = new CompositionCohortDefinition();
		above20YearsascarisLumbricoidesFemalePatients.setName("above20YearsascarisLumbricoidesFemalePatients");
		above20YearsascarisLumbricoidesFemalePatients.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		above20YearsascarisLumbricoidesFemalePatients.addParameter(new Parameter("startDate", "startDate", Date.class));
		above20YearsascarisLumbricoidesFemalePatients.addParameter(new Parameter("endDate", "endDate", Date.class));
		above20YearsascarisLumbricoidesFemalePatients.getSearches().put("1", new Mapped<CohortDefinition>(ascarisLumbricoidesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		above20YearsascarisLumbricoidesFemalePatients.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove20Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		above20YearsascarisLumbricoidesFemalePatients.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		above20YearsascarisLumbricoidesFemalePatients.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator above20YearsascarisLumbricoidesFemalePatientsIndicator = Indicators.newCohortIndicator("above20YearsascarisLumbricoidesFemalePatientsIndicator",
				above20YearsascarisLumbricoidesFemalePatients, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("II.E.7.F.20", "Ascaris lumbricoides/ Ascaris lumbricoides Female above 20 years", new Mapped(above20YearsascarisLumbricoidesFemalePatientsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


	}


// III. Mental Health/ Santé mentale

	private void createCohortMonthlyIndicatorsIII(CohortIndicatorDataSetDefinition dsd) {


		AgeCohortDefinition patientBetweenZeroAndNineteenYears = patientWithAgeBetween(0,19);

		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("male Patients");
		males.setMaleIncluded(true);




		// III.B. 2 New case patient with Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique


		SqlCohortDefinition newCasePatiens=Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("newCasePatiens",caseStatus,newCase);

		SqlCohortDefinition newpostTraumaticStressDisorderPatient= patientWithICDCodeObsByStartDateAndEndDate("F431",caseStatus,newCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19PostTraumaticStressDisorderPatient = new CompositionCohortDefinition();
		maleBeteen0And19PostTraumaticStressDisorderPatient.setName("maleBeteen0And19TraumaticStressDisorderPatient");
		maleBeteen0And19PostTraumaticStressDisorderPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderPatient.getSearches().put("1", new Mapped<CohortDefinition>(newpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19PostTraumaticStressDisorderPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19PostTraumaticStressDisorderPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19PostTraumaticStressDisorderPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBetween0And19PosttTraumaticStressDisorderPatientIndicator = Indicators.newCohortIndicator("maleBetween0And19PosttTraumaticStressDisorderPatientIndicator",
				maleBeteen0And19PostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.N.M.019", "New case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Male", new Mapped(maleBetween0And19PosttTraumaticStressDisorderPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19postTraumaticStressDisorderPatient = new CompositionCohortDefinition();
		femaleBeteen0And19postTraumaticStressDisorderPatient.setName("femaleBelow5postTraumaticStressDisorderPatient");
		femaleBeteen0And19postTraumaticStressDisorderPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
        femaleBeteen0And19postTraumaticStressDisorderPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femaleBeteen0And19postTraumaticStressDisorderPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderPatient.getSearches().put("1", new Mapped<CohortDefinition>(newpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19postTraumaticStressDisorderPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19postTraumaticStressDisorderPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femaleBeteen0And19postTraumaticStressDisorderPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBeteen0And19PostTraumaticStressDisorderPatientIndicator = Indicators.newCohortIndicator("femaleBelow5postTraumaticStressDisorderPatientIndicator",
				femaleBeteen0And19postTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.N.F.019", "New case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Female", new Mapped(femaleBeteen0And19PostTraumaticStressDisorderPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		AgeCohortDefinition patientBetween20And39Years = patientWithAgeBetween(20,39);


		CompositionCohortDefinition malepatientsBetween20And39YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setName("malepatientsBetween20And39YearsWithPostTraumaticStressDisorder");
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(newpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and 3");

		CohortIndicator malepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("malepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator",
				malepatientsBetween20And39YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.N.M.2039", "New case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Male between 20 and 39 years", new Mapped(malepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setName("femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder");
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(newpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("femalepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator",
				femalepatientsBetween20And39YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.N.F.2039", "New case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Female between 20 and 39 years", new Mapped(femalepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		AgeCohortDefinition patientAbove40Years = patientWithAgeAbove(40);

		CompositionCohortDefinition malepatientsAbove40YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.setName("malepatientsAbove40YearsWithPostTraumaticStressDisorder");
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
        malepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        malepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(newpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        malepatientsAbove40YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and 3");

		CohortIndicator malepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator",
				malepatientsAbove40YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.N.M.40", "New case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Male above 40", new Mapped(malepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsAbove40YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.setName("femalepatientsAbove40YearsWithPostTraumaticStressDisorder");
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
        femalepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
        femalepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(newpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
        femalepatientsAbove40YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator",
				femalepatientsAbove40YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.N.F.40", "New case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Female above 40", new Mapped(femalepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// III.B. 2 Old case patient with Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique

		SqlCohortDefinition oldCasePatiens=Cohorts.getPatientsWithCodedObservationsBetweenStartDateAndEndDate("oldCasePatiens",caseStatus,oldCase);

		SqlCohortDefinition odlpostTraumaticStressDisorderPatient= patientWithICDCodeObsByStartDateAndEndDate("F431",caseStatus,oldCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19PostTraumaticStressDisorderOldCasePatient = new CompositionCohortDefinition();
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.setName("maleBeteen0And19TraumaticStressDisorderOldCasePatient");
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(odlpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19PostTraumaticStressDisorderOldCasePatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBetween0And19PosttTraumaticStressDisorderOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19PosttTraumaticStressDisorderOldCasePatientIndicator",
				maleBeteen0And19PostTraumaticStressDisorderOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.O.M.019", "Old case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Male", new Mapped(maleBetween0And19PosttTraumaticStressDisorderOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19postTraumaticStressDisorderOldCasePatient = new CompositionCohortDefinition();
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.setName("femaleBelow5postTraumaticStressDisorderOldCasePatient");
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(odlpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19postTraumaticStressDisorderOldCasePatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBeteen0And19PostTraumaticStressDisorderOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBelow5postTraumaticStressDisorderOldCasePatientIndicator",
				femaleBeteen0And19postTraumaticStressDisorderOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.O.F.019", "Old case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Female", new Mapped(femaleBeteen0And19PostTraumaticStressDisorderOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		CompositionCohortDefinition maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setName("maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder");
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(odlpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator",
				malepatientsBetween20And39YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.O.M.2039", "Old case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Male between 20 and 39 years", new Mapped(maleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setName("femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder");
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(odlpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator",
				femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.O.F.2039", "Old case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Female between 20 and 39 years", new Mapped(femaleOldCasepatientsBetween20And39YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.setName("maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder");
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(odlpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator",
				maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.O.M.40", "Old case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder = new CompositionCohortDefinition();
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.setName("femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder");
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("1", new Mapped<CohortDefinition>(odlpostTraumaticStressDisorderPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator",
				femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorder, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.2.O.F.40", "Old case Post-traumatic stress disorder/ Syndrome de Stress Post-Traumatique Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithPostTraumaticStressDisorderIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");







		// III.B. 3 New case patient with Schizophrenia and other psychoses / Schizophrénie et autres Psychoses



		SqlCohortDefinition  newschizophreniaAndOtherPsychosesPatient= patientWithICDCodeObsByStartDateAndEndDate("F209",caseStatus,newCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19schizophreniaAndOtherPsychosesPatient = new CompositionCohortDefinition();
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.setName("maleBeteen0And19schizophreniaAndOtherPsychosesPatient");
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.getSearches().put("1", new Mapped<CohortDefinition>(newschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19schizophreniaAndOtherPsychosesPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBetween0And19schizophreniaAndOtherPsychosesPatientIndicator = Indicators.newCohortIndicator("maleBetween0And19schizophreniaAndOtherPsychosesPatientIndicator",
				maleBeteen0And19schizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.N.M.019", "New case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Male", new Mapped(maleBetween0And19schizophreniaAndOtherPsychosesPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19schizophreniaAndOtherPsychosesPatient = new CompositionCohortDefinition();
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.setName("femaleBelow5schizophreniaAndOtherPsychosesPatient");
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.getSearches().put("1", new Mapped<CohortDefinition>(newschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19schizophreniaAndOtherPsychosesPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBeteen0And19schizophreniaAndOtherPsychosesPatientIndicator = Indicators.newCohortIndicator("femaleBelow5schizophreniaAndOtherPsychosesPatientIndicator",
				femaleBeteen0And19schizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.N.F.019", "New case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Female", new Mapped(femaleBeteen0And19schizophreniaAndOtherPsychosesPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39



		CompositionCohortDefinition malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setName("malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses");
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(newschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and 3");

		CohortIndicator malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator",
				malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.N.M.2039", "New case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Male between 20 and 39 years", new Mapped(malepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setName("femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses");
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(newschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("4", new Mapped<CohortDefinition>(newCasePatiens, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}")));
		femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator",
				femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.N.F.2039", "New case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Female between 20 and 39 years", new Mapped(femalepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +


		CompositionCohortDefinition malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setName("malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses");
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(newschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and 3");

		CohortIndicator malepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator",
				malepatientsAbove40YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.N.M.40", "New case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Male above 40", new Mapped(malepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setName("femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses");
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(newschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator",
				femalepatientsAbove40YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.N.F.40", "New case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Female above 40", new Mapped(femalepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// III.B. 3 Old case patient with Schizophrenia and other psychoses / Schizophrénie et autres Psychoses
		SqlCohortDefinition  oldschizophreniaAndOtherPsychosesPatient= patientWithICDCodeObsByStartDateAndEndDate("F209",caseStatus,oldCase);


// 0-19

		CompositionCohortDefinition maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient = new CompositionCohortDefinition();
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.setName("maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient");
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBetween0And19schizophreniaAndOtherPsychosesOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19schizophreniaAndOtherPsychosesOldCasePatientIndicator",
				maleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.O.M.019", "Old case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Male", new Mapped(maleBetween0And19schizophreniaAndOtherPsychosesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient = new CompositionCohortDefinition();
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.setName("femaleBelow5schizophreniaAndOtherPsychosesOldCasePatient");
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBelow5schizophreniaAndOtherPsychosesOldCasePatientIndicator",
				femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.O.F.019", "Old case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Female", new Mapped(femaleBeteen0And19schizophreniaAndOtherPsychosesOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		CompositionCohortDefinition maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setName("maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses");
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(oldschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator",
				maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.O.M.2039", "Old case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Male between 20 and 39 years", new Mapped(maleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setName("femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses");
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(oldschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator",
				femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.O.F.2039", "Old case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Female between 20 and 39 years", new Mapped(femaleOldCasepatientsBetween20And39YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setName("maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses");
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(oldschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator",
				maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.O.M.40", "Old case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses = new CompositionCohortDefinition();
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setName("femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses");
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("1", new Mapped<CohortDefinition>(oldschizophreniaAndOtherPsychosesPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator",
				femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.3.O.F.40", "Old case Schizophrenia and other psychoses / Schizophrénie et autres Psychoses Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithschizophreniaAndOtherPsychosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");







		// III.B. 4 New case patient with Somatoform disorders/Troubles somatiques



		SqlCohortDefinition newsomatoformDisordersPatient= patientWithICDCodeObsByStartDateAndEndDate("F459",caseStatus,newCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19somatoformDisordersPatient = new CompositionCohortDefinition();
		maleBeteen0And19somatoformDisordersPatient.setName("maleBeteen0And19somatoformDisordersPatient");
		maleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19somatoformDisordersPatient.getSearches().put("1", new Mapped<CohortDefinition>(newsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19somatoformDisordersPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19somatoformDisordersPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19somatoformDisordersPatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBetween0And19somatoformDisordersPatientIndicator = Indicators.newCohortIndicator("maleBetween0And19somatoformDisordersPatientIndicator",
				maleBeteen0And19somatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.N.M.019", "New case Somatoform disorders/Troubles somatiques Male", new Mapped(maleBetween0And19somatoformDisordersPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19somatoformDisordersPatient = new CompositionCohortDefinition();
		femaleBeteen0And19somatoformDisordersPatient.setName("femaleBelow5somatoformDisordersPatient");
		femaleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19somatoformDisordersPatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19somatoformDisordersPatient.getSearches().put("1", new Mapped<CohortDefinition>(newsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19somatoformDisordersPatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19somatoformDisordersPatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19somatoformDisordersPatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBeteen0And19somatoformDisordersPatientIndicator = Indicators.newCohortIndicator("femaleBelow5somatoformDisordersPatientIndicator",
				femaleBeteen0And19somatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.N.F.019", "New case Somatoform disorders/Troubles somatiques Female", new Mapped(femaleBeteen0And19somatoformDisordersPatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39



		CompositionCohortDefinition malepatientsBetween20And39YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		malepatientsBetween20And39YearsWithsomatoformDisorders.setName("malepatientsBetween20And39YearsWithsomatoformDisorders");
		malepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(newsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsBetween20And39YearsWithsomatoformDisorders.setCompositionString("1 and 2 and 3");

		CohortIndicator malepatientsBetween20And39YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("malepatientsBetween20And39YearsWithsomatoformDisordersIndicator",
				malepatientsBetween20And39YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.N.M.2039", "New case Somatoform disorders/Troubles somatiques Male between 20 and 39 years", new Mapped(malepatientsBetween20And39YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsBetween20And39YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		femalepatientsBetween20And39YearsWithsomatoformDisorders.setName("femalepatientsBetween20And39YearsWithsomatoformDisorders");
		femalepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(newsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsBetween20And39YearsWithsomatoformDisorders.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsBetween20And39YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("femalepatientsBetween20And39YearsWithsomatoformDisordersIndicator",
				femalepatientsBetween20And39YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.N.F.2039", "New case Somatoform disorders/Troubles somatiques Female between 20 and 39 years", new Mapped(femalepatientsBetween20And39YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +


		CompositionCohortDefinition malepatientsAbove40YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		malepatientsAbove40YearsWithsomatoformDisorders.setName("malepatientsAbove40YearsWithsomatoformDisorders");
		malepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(newsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsAbove40YearsWithsomatoformDisorders.setCompositionString("1 and 2 and 3");

		CohortIndicator malepatientsAbove40YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithsomatoformDisordersIndicator",
				malepatientsAbove40YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.N.M.40", "New case Somatoform disorders/Troubles somatiques Male above 40", new Mapped(malepatientsAbove40YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsAbove40YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		femalepatientsAbove40YearsWithsomatoformDisorders.setName("femalepatientsAbove40YearsWithsomatoformDisorders");
		femalepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(newsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsAbove40YearsWithsomatoformDisorders.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsAbove40YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithsomatoformDisordersIndicator",
				femalepatientsAbove40YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.N.F.40", "New case Somatoform disorders/Troubles somatiques Female above 40", new Mapped(femalepatientsAbove40YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// III.B. 4 Old case patient with Somatoform disorders/Troubles somatiques

		SqlCohortDefinition oldsomatoformDisordersPatient= patientWithICDCodeObsByStartDateAndEndDate("F459",caseStatus,newCase);


// 0-19

		CompositionCohortDefinition maleBeteen0And19somatoformDisordersOldCasePatient = new CompositionCohortDefinition();
		maleBeteen0And19somatoformDisordersOldCasePatient.setName("maleBeteen0And19somatoformDisordersOldCasePatient");
		maleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19somatoformDisordersOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19somatoformDisordersOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19somatoformDisordersOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19somatoformDisordersOldCasePatient.setCompositionString("1 and 2 and 3");

		CohortIndicator maleBetween0And19somatoformDisordersOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19somatoformDisordersOldCasePatientIndicator",
				maleBeteen0And19somatoformDisordersOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.O.M.019", "Old case Somatoform disorders/Troubles somatiques Male", new Mapped(maleBetween0And19somatoformDisordersOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19somatoformDisordersOldCasePatient = new CompositionCohortDefinition();
		femaleBeteen0And19somatoformDisordersOldCasePatient.setName("femaleBelow5somatoformDisordersOldCasePatient");
		femaleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19somatoformDisordersOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19somatoformDisordersOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19somatoformDisordersOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19somatoformDisordersOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19somatoformDisordersOldCasePatient.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleBeteen0And19somatoformDisordersOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBelow5somatoformDisordersOldCasePatientIndicator",
				femaleBeteen0And19somatoformDisordersOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.O.F.019", "Old case Somatoform disorders/Troubles somatiques Female", new Mapped(femaleBeteen0And19somatoformDisordersOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		CompositionCohortDefinition maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.setName("maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders");
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(oldsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsBetween20And39YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("maleOldCasepatientsBetween20And39YearsWithsomatoformDisordersIndicator",
				maleOldCasepatientsBetween20And39YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.O.M.2039", "Old case Somatoform disorders/Troubles somatiques Male between 20 and 39 years", new Mapped(maleOldCasepatientsBetween20And39YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.setName("femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders");
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(oldsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleOldCasepatientsBetween20And39YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsBetween20And39YearsWithsomatoformDisordersIndicator",
				femaleOldCasepatientsBetween20And39YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.O.F.2039", "Old case Somatoform disorders/Troubles somatiques Female between 20 and 39 years", new Mapped(femaleOldCasepatientsBetween20And39YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.setName("maleOldCasepatientsAbove40YearsWithsomatoformDisorders");
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(oldsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsAbove40YearsWithsomatoformDisorders.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsAbove40YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithsomatoformDisordersIndicator",
				maleOldCasepatientsAbove40YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.O.M.40", "Old case Somatoform disorders/Troubles somatiques Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithsomatoformDisorders = new CompositionCohortDefinition();
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.setName("femaleOldCasepatientsAbove40YearsWithsomatoformDisorders");
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("1", new Mapped<CohortDefinition>(oldsomatoformDisordersPatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsAbove40YearsWithsomatoformDisorders.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femaleOldCasepatientsAbove40YearsWithsomatoformDisordersIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithsomatoformDisordersIndicator",
				femaleOldCasepatientsAbove40YearsWithsomatoformDisorders, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.4.O.F.40", "Old case Somatoform disorders/Troubles somatiques Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithsomatoformDisordersIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");






		// III.B. 5 New case patient with Behavioral and emotional disorders with on set usually occurring in childhood and adolescence



		SqlCohortDefinition newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient=patientWithICDCodesObsByStartDateAndEndDate("F10,F11,F12,F13,F14,F15,F16,F17,F18,F19",caseStatus,newCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient = new CompositionCohortDefinition();
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.setName("maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient");
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.getSearches().put("1", new Mapped<CohortDefinition>(newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.setCompositionString("1 and 2 and 3");


		CohortIndicator maleBetween0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatientIndicator",
				maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.N.M.019", "New case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Male", new Mapped(maleBetween0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient = new CompositionCohortDefinition();
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.setName("femaleBelow5BehavioralDisordersOccurringInChildhoodAndAdolescencePatient");
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.getSearches().put("1", new Mapped<CohortDefinition>(newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatientIndicator = Indicators.newCohortIndicator("femaleBelow5BehavioralDisordersOccurringInChildhoodAndAdolescencePatientIndicator",
				femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.N.F.019", "New case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Female", new Mapped(femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescencePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39



		CompositionCohortDefinition malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and 3");


		CohortIndicator malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.N.M.2039", "New case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Male between 20 and 39 years", new Mapped(malepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and (not 3)");

		CohortIndicator femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.N.F.2039", "New case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Female between 20 and 39 years", new Mapped(femalepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +


		CompositionCohortDefinition malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and 3");


		CohortIndicator malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.N.M.40", "New case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Male above 40", new Mapped(malepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(newBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.N.F.40", "New case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Female above 40", new Mapped(femalepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// III.B. 5 Old case patient with Behavioral and emotional disorders with on set usually occurring in childhood and adolescence


		SqlCohortDefinition oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient=patientWithICDCodesObsByStartDateAndEndDate("F10,F11,F12,F13,F14,F15,F16,F17,F18,F19",caseStatus,oldCase);


// 0-19

		CompositionCohortDefinition maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient = new CompositionCohortDefinition();
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.setName("maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient");
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.setCompositionString("1 and 2 and 3");


		CohortIndicator maleBetween0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatientIndicator",
				maleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.O.M.019", "Old case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Male", new Mapped(maleBetween0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient = new CompositionCohortDefinition();
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.setName("femaleBelow5BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient");
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBelow5BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatientIndicator",
				femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.O.F.019", "Old case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Female", new Mapped(femaleBeteen0And19BehavioralDisordersOccurringInChildhoodAndAdolescenceOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		CompositionCohortDefinition maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and 3");


		CohortIndicator maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.O.M.2039", "Old case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Male between 20 and 39 years", new Mapped(maleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.O.F.2039", "Old case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Female between 20 and 39 years", new Mapped(femaleOldCasepatientsBetween20And39YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and 3");


		CohortIndicator maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.O.M.40", "Old case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence = new CompositionCohortDefinition();
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setName("femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence");
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("1", new Mapped<CohortDefinition>(oldBehavioralDisordersOccurringInChildhoodAndAdolescencePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator",
				femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescence, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.5.O.F.40", "Old case Behavioral and emotional disorders with on set usually occurring in childhood and adolescence Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithBehavioralDisordersOccurringInChildhoodAndAdolescenceIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		// III.B. 6 New case patient with Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool



		SqlCohortDefinition newmentalAndBehavioralDisorderDueToUseOfAlcohol=patientWithICDCodesObsByStartDateAndEndDate("F80,F81,F82,F83,F84,F85,F86,F87,F88,F89",caseStatus,newCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.setName("maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol");
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and 3");


		CohortIndicator maleBetween0And19mentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("maleBetween0And19mentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.N.M.019", "New case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Male", new Mapped(maleBetween0And19mentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.setName("femaleBelow5mentalAndBehavioralDisorderDueToUseOfAlcohol");
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("femaleBelow5mentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.N.F.019", "New case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Female", new Mapped(femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39



		CompositionCohortDefinition malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and 3");


		CohortIndicator malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.N.M.2039", "New case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Male between 20 and 39 years", new Mapped(malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.N.F.2039", "New case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Female between 20 and 39 years", new Mapped(femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +


		CompositionCohortDefinition malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and 3");


		CohortIndicator malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.N.M.40", "New case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Male above 40", new Mapped(malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.N.F.40", "New case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Female above 40", new Mapped(femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// III.B. 6 Old case patient with Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool

		SqlCohortDefinition OldmentalAndBehavioralDisorderDueToUseOfAlcohol=patientWithICDCodesObsByStartDateAndEndDate("F80,F81,F82,F83,F84,F85,F86,F87,F88,F89",caseStatus,oldCase);


// 0-19

		CompositionCohortDefinition maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient = new CompositionCohortDefinition();
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.setName("maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient");
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.setCompositionString("1 and 2 and 3");


		CohortIndicator maleBetween0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatientIndicator",
				maleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.O.M.019", "Old case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Male", new Mapped(maleBetween0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient = new CompositionCohortDefinition();
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.setName("femaleBelow5mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient");
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBelow5mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatientIndicator",
				femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.O.F.019", "Old case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Female", new Mapped(femaleBeteen0And19mentalAndBehavioralDisorderDueToUseOfAlcoholOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		CompositionCohortDefinition maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.O.M.2039", "Old case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Male between 20 and 39 years", new Mapped(maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.O.F.2039", "Old case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Female between 20 and 39 years", new Mapped(femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and 3");


		CohortIndicator maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.O.M.40", "Old case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol = new CompositionCohortDefinition();
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setName("femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol");
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator",
				femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcohol, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.6.O.F.40", "Old case Mental and behavioral disorder due to use of alcohol/Troubles mentaux et du comportement due à l’usage de l’alcool Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToUseOfAlcoholIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");







		// III.B. 7 New case patient with Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues



		SqlCohortDefinition newmentalAndBehavioralDisorderDueToSubstanceAbuse=patientWithICDCodesObsByStartDateAndEndDate("F11,F12,F13,F14,F15,F16,F17,F18,F19",caseStatus,newCase);

// 0-19

		CompositionCohortDefinition maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.setName("maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse");
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and 3");


		CohortIndicator maleBetween0And19mentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("maleBetween0And19mentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.N.M.019", "New case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Male", new Mapped(maleBetween0And19mentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.setName("femaleBelow5mentalAndBehavioralDisorderDueToSubstanceAbuse");
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("femaleBelow5mentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.N.F.019", "New case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Female", new Mapped(femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39



		CompositionCohortDefinition malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and 3");


		CohortIndicator malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.N.M.2039", "New case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Male between 20 and 39 years", new Mapped(malepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.N.F.2039", "New case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Female between 20 and 39 years", new Mapped(femalepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +


		CompositionCohortDefinition malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and 3");


		CohortIndicator malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.N.M.40", "New case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Male above 40", new Mapped(malepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(newmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.N.F.40", "New case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Female above 40", new Mapped(femalepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// III.B. 7 Old case patient with Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues

		SqlCohortDefinition OldmentalAndBehavioralDisorderDueToSubstanceAbuse=patientWithICDCodesObsByStartDateAndEndDate("F11,F12,F13,F14,F15,F16,F17,F18,F19",caseStatus,oldCase);


// 0-19

		CompositionCohortDefinition maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient = new CompositionCohortDefinition();
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.setName("maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient");
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.setCompositionString("1 and 2 and 3");


		CohortIndicator maleBetween0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatientIndicator = Indicators.newCohortIndicator("maleBetween0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatientIndicator",
				maleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.O.M.019", "Old case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Male", new Mapped(maleBetween0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient = new CompositionCohortDefinition();
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.setName("femaleBelow5mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient");
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.getSearches().put("2", new Mapped<CohortDefinition>(patientBetweenZeroAndNineteenYears, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatientIndicator = Indicators.newCohortIndicator("femaleBelow5mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatientIndicator",
				femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatient, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.O.F.019", "Old case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Female", new Mapped(femaleBeteen0And19mentalAndBehavioralDisorderDueToSubstanceAbuseOldCasePatientIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//20-39

		CompositionCohortDefinition maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and 3");

		CohortIndicator maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.O.M.2039", "Old case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Male between 20 and 39 years", new Mapped(maleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientBetween20And39Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.O.F.2039", "Old case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Female between 20 and 39 years", new Mapped(femaleOldCasepatientsBetween20And39YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


		// 40 +

		CompositionCohortDefinition maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and 3");


		CohortIndicator maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.O.M.40", "Old case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Male above 40", new Mapped(maleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");




		CompositionCohortDefinition femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse = new CompositionCohortDefinition();
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setName("femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse");
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("startDate", "startDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("endDate", "endDate", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("1", new Mapped<CohortDefinition>(OldmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("2", new Mapped<CohortDefinition>(patientAbove40Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.getSearches().put("3", new Mapped<CohortDefinition>(males, null));
		femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse.setCompositionString("1 and 2 and (not 3)");


		CohortIndicator femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator = Indicators.newCohortIndicator("femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator",
				femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuse, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		dsd.addColumn("III.B.7.O.F.40", "Old case Mental and behavioral disorder due to substance abuse/Troubles mentaux et du comportement due à l’usage de drogues Female above 40", new Mapped(femaleOldCasepatientsAbove40YearsWithmentalAndBehavioralDisorderDueToSubstanceAbuseIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");






	}

// IV. Chronic Diseases

	private void createCohortMonthlyIndicatorsIV(CohortIndicatorDataSetDefinition dsd) {


	}

// V. Other Cardiovascular and Kidney diseases

	private void createCohortMonthlyIndicatorsV(CohortIndicatorDataSetDefinition dsd) {


	}

// VI. Injuries

	private void createCohortMonthlyIndicatorsVI(CohortIndicatorDataSetDefinition dsd) {


	}

// VII. Palliative care

	private void createCohortMonthlyIndicatorsVII(CohortIndicatorDataSetDefinition dsd) {


	}

// VIII. Community Checkup

	private void createCohortMonthlyIndicatorsVIII(CohortIndicatorDataSetDefinition dsd) {


	}

// IX.  Cancer screening

	private void createCohortMonthlyIndicatorsIX(CohortIndicatorDataSetDefinition dsd) {


	}


	private void setUpProperties() {
		
		onOrAfterOnOrBefore.add("onOrAfter");
		
		onOrAfterOnOrBefore.add("onOrBefore");


		OPDForm=Context.getFormService().getFormByUuid("a4d59540-9c55-4cda-8e0d-0337a743540e");

		caseStatus=Context.getConceptService().getConceptByUuid("14183f94-59b2-4b62-bad7-2c788a21a422");

		newCase=Context.getConceptService().getConceptByUuid("f7b5bf49-cb07-4fca-8c15-93ba92249344");

		oldCase=Context.getConceptService().getConceptByUuid("ae5ba489-9be2-4960-8e44-8d09071ab8ca");

		noneInsuranceID =Integer.parseInt(Context.getAdministrationService().getGlobalProperty("reports.NoneInsuranceID"));

		hundrepercentInsuredInsuranceIDs=Context.getAdministrationService().getGlobalProperty("reports.hundrepercentInsuredInsuranceIDs");

		indigentsInsuranceIDs=Context.getAdministrationService().getGlobalProperty("reports.indigentsInsuranceIDs");

		ICDConceptClassId=Integer.parseInt(Context.getAdministrationService().getGlobalProperty("reports.ICDConceptClassId"));

	}

	private AgeCohortDefinition patientWithAgeBelow(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age - 1);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}

	private AgeCohortDefinition patientWithAgeBelowAndIncuded(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}

	private AgeCohortDefinition patientWithAgeBetween(int age1, int age2) {
		AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
		patientsWithAge.setName("patientsWithAge");
		patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAge.setMinAge(age1);
		patientsWithAge.setMaxAge(age2);
		patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
		patientsWithAge.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAge;
	}

	private AgeCohortDefinition patientWithAgeAbove(int age) {
		AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
		patientsWithAge.setName("patientsWithAge");
		patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAge.setMinAge(age);
		patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
		return patientsWithAge;
	}


	private SqlCohortDefinition patientWithICDCodeObsByStartDateAndEndDate(String ICDCode){

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%"+ICDCode+"%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}

	private SqlCohortDefinition patientWithICDCodeObsByStartDateAndEndDate(String ICDCode,Concept caseStatusQuestion, Concept caseAnswer){

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition("select o.person_id from obs o " +
				"inner join obs o2 on o.encounter_id=o2.encounter_id" +
				" where o.value_coded in (select distinct concept_id from concept_name where name like '%"+ICDCode+"%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o2.concept_id="+caseStatusQuestion.getConceptId()+" and o2.value_coded="+caseAnswer.getConceptId()+"");
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}
	private SqlCohortDefinition patientWithICDCodesObsByStartDateAndEndDate(String ICDCodes,Concept caseStatusQuestion, Concept caseAnswer){

		String icdTencodes[] =ICDCodes.split(",");

		StringBuilder q=new StringBuilder();
		q.append("select o.person_id from obs o " +
				"inner join obs o2 on o.encounter_id=o2.encounter_id" +
				" where o.value_coded in (select distinct concept_id from concept_name where ");
		int i=0;
		for (String c:icdTencodes){
			if(i==0){
				q.append("name like '%"+c+"%'");
				i++;
			}else {
				q.append(" or name like '%"+c+"%'");
				i++;
			}
		}
		q.append(") and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate and o2.concept_id="+caseStatusQuestion.getConceptId()+" and o2.value_coded="+caseAnswer.getConceptId()+"");

		SqlCohortDefinition patientWithIDCObs=new SqlCohortDefinition(q.toString());
		patientWithIDCObs.setName("patientWithIDCObs");
		patientWithIDCObs.addParameter(new Parameter("startDate", "startDate", Date.class));
		patientWithIDCObs.addParameter(new Parameter("endDate", "endDate", Date.class));

		return patientWithIDCObs;

	}





}