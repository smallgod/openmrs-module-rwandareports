/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.rwandareports.StandaloneContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Iterator;

/**
 * Tests elements of the SetupAdultLateVisitAndCD4Report class
 */
@Ignore
public class SetupCancerScreeningConsultAndMissedVisitReportTest extends StandaloneContextSensitiveTest {

    @Autowired
    @Qualifier(value = "reportingReportDefinitionService")
    ReportDefinitionService reportDefinitionService;

    @Test
    public void test() throws Exception {

        //LogManager.getLogger(EvaluationProfiler.class).setLevel(Level.TRACE);

        StopWatch sw = new StopWatch();
        sw.start();
//        GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", DateUtil.getDateTime(2023,1,2));
        context.addParameterValue("endDate", DateUtil.getDateTime(2023,1,9));


        System.out.println("Setting up report");
        SetupBreastCancerScreeningConsultAndMissedVisit report = new SetupBreastCancerScreeningConsultAndMissedVisit();
        report.setup();

        System.out.println("Running report");
        ReportDefinition rd = reportDefinitionService.getDefinitions("ONC-Cancer Screening Consultation Sheet", true).get(0);
        System.out.println("retrieved report : " + rd.getName());
        ReportData data = reportDefinitionService.evaluate(rd, context);

        for (String dsName : data.getDataSets().keySet()) {
            System.out.println("Got Data Set: " + dsName);
            DataSet ds = data.getDataSets().get(dsName);
            int numRows = 0;
            for (Iterator<DataSetRow> i = ds.iterator(); i.hasNext();) {
                DataSetRow row = i.next();
                numRows++;
            }
            System.out.println("Iterated over " + numRows + " rows");
        }

        sw.stop();
        System.out.println("Lasted: " + sw.toString());
    }

}
