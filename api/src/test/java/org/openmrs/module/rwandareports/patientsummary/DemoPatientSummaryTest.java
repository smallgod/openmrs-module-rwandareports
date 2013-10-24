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
import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSetRow;

/**
 * Tests the output of the Adult HIV Consultation Sheet Report
 */
public class DemoPatientSummaryTest extends BasePatientSummaryTest {

	@Override
	public void setupTestData() {
		String patientLookup = "1";
		tdm.addPatient(patientLookup, "Troy", "Sanders", "M", "1980-02-15", false, "12345");
		tdm.addObs(patientLookup, "WEIGHT (KG)", "45.0", "2013-04-11");
		tdm.addObs(patientLookup, "WEIGHT (KG)", "48.0", "2013-08-13");
		tdm.addObs(patientLookup, "WEIGHT (KG)", "50.0", "2013-06-12");
	}

	@Override
	public PatientSummaryManager getPatientSummaryManager() {
		return new DemoPatientSummary();
	}

	@Test
	public void test() throws Exception {
		String patientLookup = "1";
		Integer pId = tdm.getPatientId(patientLookup);
		DataSetRow row = evaluateDataForPatient(patientLookup);
		Assert.assertEquals(5, row.getColumnValues().size());
		Assert.assertEquals(pId, row.getColumnValue("patientId"));
		Assert.assertEquals("Troy", row.getColumnValue("givenName"));
		Assert.assertEquals("Sanders", row.getColumnValue("familyName"));
		Assert.assertEquals(48.0, row.getColumnValue("lastWeight"));
		Assert.assertEquals("13/Aug/2013", row.getColumnValue("lastWeightDate"));
	}
}
