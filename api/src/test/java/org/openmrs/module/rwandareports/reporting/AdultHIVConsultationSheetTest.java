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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the output of the Adult HIV Consultation Sheet Report
 */
public class AdultHIVConsultationSheetTest extends RwandaReportsTest {

	public String getReportName() {
		return "HIV-Adult Consultation Sheet";
	}

	public List<String> getReportDesignNames() {
		return Arrays.asList("AdultHIVConsultationSheet.xls_");
	}

	public SetupReport getSetupReportClass() {
		return new SetupAdultHIVConsultationSheet();
	}
}
