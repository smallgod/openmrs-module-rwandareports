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


		AgeCohortDefinition patientBelowFiveYear = patientWithAgeBelow(5);

		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("male Patients");
		males.setMaleIncluded(true);



		SqlCohortDefinition foodPoisoningPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%A05%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

	//SqlCohortDefinition foodPoisoningPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition foodPoisoningPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		foodPoisoningPatient.setName("foodPoisoningPatient");
		foodPoisoningPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		foodPoisoningPatient.addParameter(new Parameter("endDate", "endDate", Date.class));


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



		SqlCohortDefinition earInfectionsPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%H65%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

		//SqlCohortDefinition earInfectionsPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition earInfectionsPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		earInfectionsPatient.setName("earInfectionsPatient");
		earInfectionsPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		earInfectionsPatient.addParameter(new Parameter("endDate", "endDate", Date.class));


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




		SqlCohortDefinition schistosomiasisPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%B65%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

		//SqlCohortDefinition schistosomiasisPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition schistosomiasisPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		schistosomiasisPatient.setName("schistosomiasisPatient");
		schistosomiasisPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		schistosomiasisPatient.addParameter(new Parameter("endDate", "endDate", Date.class));


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


		SqlCohortDefinition ascarisLumbricoidesPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (select distinct concept_id from concept_name where name like '%B77%') and o.value_coded in (select distinct concept_id from concept where class_id="+ICDConceptClassId+") and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");

		//SqlCohortDefinition ascarisLumbricoidesPatient=new SqlCohortDefinition("select o.person_id from obs o where o.value_coded in (10201) and o.voided=0 and o.obs_datetime>= :startDate and o.obs_datetime<= :endDate");
		//SqlCohortDefinition ascarisLumbricoidesPatient=new SqlCohortDefinition("select o.person_id from obs o,concept c where c.class_id=19 and o.value_coded=c.concept_id and o.voided=0 and o.obs_datetime>='2019-01-01' and o.obs_datetime<='2019-04-05'");
		ascarisLumbricoidesPatient.setName("ascarisLumbricoidesPatient");
		ascarisLumbricoidesPatient.addParameter(new Parameter("startDate", "startDate", Date.class));
		ascarisLumbricoidesPatient.addParameter(new Parameter("endDate", "endDate", Date.class));


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
}