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


	private  List<String> onOrAfterOnOrBefore =new ArrayList<String>();


	public void setup() throws Exception {
		
		setUpProperties();


		//Monthly report set-up

		
		Properties properties = new Properties();
		properties.setProperty("hierarchyFields", "countyDistrict:District");

		
		// Monthly Report Definition: Start
		
		ReportDefinition monthlyRd = new ReportDefinition();
		monthlyRd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		monthlyRd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		monthlyRd.addParameter(new Parameter("location", "Location", AllLocation.class, properties));
		
		monthlyRd.setName("District Hospital Monthly HMIS Report");
		
		monthlyRd.addDataSetDefinition(createMonthlyLocationDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));
		
		// Monthly Report Definition: End



		EncounterCohortDefinition patientWithOPDForm=Cohorts.createEncounterBasedOnForms("patientWithOPDForm",onOrAfterOnOrBefore,OPDForms);



		
		monthlyRd.setBaseCohortDefinition(patientWithOPDForm,
		    ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		

		Helper.saveReportDefinition(monthlyRd);
		

		
		ReportDesign monthlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(monthlyRd,
				"District_Hospital_Monthly_HMIS_Report.xls", "District Hospital Monthly HMIS Report (Excel)", null);
		Properties monthlyProps = new Properties();
		monthlyProps.put("repeatingSections", "sheet:1,dataset:Encounter Monthly Data Set");
		monthlyProps.put("sortWeight","5000");
		monthlyDesign.setProperties(monthlyProps);
		Helper.saveReportDesign(monthlyDesign);
		
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("District Hospital Monthly HMIS Report (Excel)".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("District Hospital Monthly HMIS Report");

	}
	

	
	//Create Monthly Encounter Data set
	
	public LocationHierachyIndicatorDataSetDefinition createMonthlyLocationDataSet() {
		
		LocationHierachyIndicatorDataSetDefinition ldsd = new LocationHierachyIndicatorDataSetDefinition(
		        createEncounterMonthlyBaseDataSet());
		ldsd.addBaseDefinition(createMonthlyBaseDataSet());
		ldsd.setName("Encounter Monthly Data Set");
		ldsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ldsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		ldsd.addParameter(new Parameter("location", "District", LocationHierarchy.class));
		
		return ldsd;
	}
	
	private EncounterIndicatorDataSetDefinition createEncounterMonthlyBaseDataSet() {
		
		EncounterIndicatorDataSetDefinition eidsd = new EncounterIndicatorDataSetDefinition();
		
		eidsd.setName("eidsd");
		eidsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		eidsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		createMonthlyIndicators(eidsd);
		return eidsd;
	}
	
	private void createMonthlyIndicators(EncounterIndicatorDataSetDefinition dsd) {

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
	
	// create quarterly cohort Data set
	private CohortIndicatorDataSetDefinition createMonthlyBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("Monthly Cohort Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createMonthlyIndicators(dsd);
		return dsd;
	}
	
	private void createMonthlyIndicators(CohortIndicatorDataSetDefinition dsd) {


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

	}
}