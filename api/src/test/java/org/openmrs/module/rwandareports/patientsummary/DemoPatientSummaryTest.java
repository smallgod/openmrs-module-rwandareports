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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.rwandareports.RwandaReportsTestUtil;
import org.openmrs.module.rwandareports.reporting.RwandaReportsTest;
import org.openmrs.module.rwandareports.reporting.SetupAdultHIVConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupReport;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the output of the Adult HIV Consultation Sheet Report
 */
public class DemoPatientSummaryTest extends BaseModuleContextSensitiveTest {

	@Test
	public void test() throws Exception {
		DemoPatientSummary manager = new DemoPatientSummary();
		ReportDefinition rd = manager.constructReportDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("12088"));
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		Assert.assertEquals(1, data.getDataSets().size());

		SimpleDataSet dataSet = (SimpleDataSet)data.getDataSets().get("patient");
		Assert.assertNotNull(dataSet);

		Assert.assertEquals(1, dataSet.getRows().size());
		Assert.assertEquals(5, dataSet.getMetaData().getColumnCount());
		Assert.assertEquals(12088, dataSet.getColumnValue(12088, "patientId"));
		Assert.assertEquals("Troy", dataSet.getColumnValue(12088, "givenName"));
		Assert.assertEquals("Sanders", dataSet.getColumnValue(12088, "familyName"));
		Assert.assertEquals(48.0, dataSet.getColumnValue(12088, "lastWeight"));
		Assert.assertEquals("13/Aug/2013", dataSet.getColumnValue(12088, "lastWeightDate"));
	}

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void setup() throws Exception {
		authenticate();
	}

	// 12088, Troy, Sanders, 48
}
