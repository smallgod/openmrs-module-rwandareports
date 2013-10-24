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
package org.openmrs.module.rwandareports.patientsummary;

import junit.framework.Assert;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.rwandareports.BaseRwandaReportsTest;

/**
 * Base test for patient summaries that provides a consistent way of running and testing this data
 */
public abstract class BasePatientSummaryTest extends BaseRwandaReportsTest {

	public abstract PatientSummaryManager getPatientSummaryManager();

	protected DataSetRow evaluateDataForPatient(String patientLookup) throws Exception {
		EvaluationContext context = new EvaluationContext();
		Cohort cohort = new Cohort();
		cohort.addMember(tdm.getPatientId(patientLookup));
		context.setBaseCohort(cohort);

		PatientSummaryManager manager = getPatientSummaryManager();
		ReportDefinition rd = manager.constructReportDefinition();
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		Assert.assertEquals(1, data.getDataSets().size());
		SimpleDataSet dataSet = (SimpleDataSet)data.getDataSets().get("patient");
		Assert.assertNotNull(dataSet);
		Assert.assertEquals(1, dataSet.getRows().size());

		return dataSet.getRows().get(0);
	}
}
