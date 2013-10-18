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
package org.openmrs.module.rwandareports;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces the coreMetadata.xml test dataset.  This should be run as follows:
 * 1. Install a new, empty mirebalais implementation following the steps on the wiki
 * 2. Point your .OpenMRS/openmrs-runtime.properties file at this database
 * 3. Specify a location where you want the dataset to be written (leaving this blank will skip the execution of this)
 * 4. Run this as a unit test
 * This should produce a new version of coreMetadata.xml at the location you have specified,
 * and you can copy it into the resources folder if and as appropriate
 */
public class CreatePatientData extends BaseModuleContextSensitiveTest {

	public String getOutputDirectory() {
		return "/home/mseaton/code/rwandareports/api/src/test/resources";
	}

	@Test
	public void run() throws Exception {

		// only run this test if it is being run alone and if an output directory has been specified
		if (getLoadCount() != 1 || ObjectUtil.isNull(getOutputDirectory()))
			return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());
		QueryDataSet qds = new QueryDataSet(connection);

		List<Integer> patientIds = new ArrayList<Integer>();
		patientIds.add(1491);
		patientIds.add(12032);

		loadData(qds, patientIds, "person", "person_id in :patientIds");
		loadData(qds, patientIds, "patient", "patient_id in :patientIds");
		loadData(qds, patientIds, "patient_identifier", "patient_id in :patientIds");
		loadData(qds, patientIds, "patient_program", "patient_id in :patientIds");
		loadData(qds, patientIds, "patient_state", "patient_program_id in (select patient_program_id from patient_program where patient_id in :patientIds)");
		loadData(qds, patientIds, "person_address", "person_id in :patientIds");
		loadData(qds, patientIds, "person_attribute", "person_id in :patientIds", "value <> ''");
		loadData(qds, patientIds, "person_name", "person_id in :patientIds");
		loadData(qds, patientIds, "orders", "patient_id in :patientIds");
		loadData(qds, patientIds, "drug_order", "order_id in (select order_id from orders where patient_id in :patientIds)");
		loadData(qds, patientIds, "encounter", "patient_id in :patientIds");
		loadData(qds, patientIds, "encounter_provider", "encounter_id in (select encounter_id from encounter where patient_id in :patientIds)");
		loadData(qds, patientIds, "obs", "person_id in :patientIds", "(encounter_id is null or encounter_id in (select encounter_id from encounter where patient_id in :patientIds))");
		loadData(qds, patientIds, "visit", "visit_id in (select visit_id from encounter where patient_id in :patientIds)");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FlatXmlDataSet.write(qds, baos);
		String contents = baos.toString("UTF-8");

		File outputFile = new File(getOutputDirectory(), "org/openmrs/module/rwandareports/patientData.xml");
		FileWriter writer = new FileWriter(outputFile);
		writer.write(contents);

		writer.flush();
		writer.close();
	}

	protected void loadData(QueryDataSet qds, List<Integer> patientIds, String tableName, String... constraint) throws Exception {
		String constraintQuery = "";
		if (constraint != null && constraint.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<constraint.length; i++) {
				sb.append(sb.length() == 0 ? " where " : " and ");
				sb.append(constraint[i]);
			}
			constraintQuery = sb.toString();
		}
		if (patientIds != null) {
			String toCheck = ":patientIds";
			if (constraintQuery.contains(toCheck)) {
				String replacement = OpenmrsUtil.join(patientIds, ",");
				constraintQuery = constraintQuery.replace(toCheck, "("+replacement+")");
			}
		}
		String query = "select * from " + tableName + constraintQuery;
		System.out.println("Adding Query: " + query);
		qds.addTable(tableName, query);
	}

	protected void overrideValue(DefaultTable table, String columnName, int rowNum, Object value) throws DataSetException {
		String tableName = table.getTableMetaData().getTableName();
		try {
			if (table.getValue(rowNum, columnName) != null) {
				table.setValue(rowNum, columnName, value);
			}
		}
		catch (NoSuchColumnException e) {}
	}

	protected void overrideValue(DefaultTable table, String tableName, String columnName, int rowNum, Object value) throws DataSetException {
		if (table.getTableMetaData().getTableName().equalsIgnoreCase(tableName)) {
			overrideValue(table, columnName, rowNum, value);
		}
	}

	protected void fixValues(DefaultTable table, int rowNum) throws DataSetException {
		for (Column c : table.getTableMetaData().getColumns()) {
			Object currentValue = table.getValue(rowNum, c.getColumnName());
			if ("".equals(currentValue)) {
				table.setValue(rowNum, c.getColumnName(), null);
			}
			if (c.getColumnName().equalsIgnoreCase("short_name")) {
				table.setValue(rowNum, c.getColumnName(), null);
			}
		}
	}
	
	/**
	 * Make sure we use the database defined by the runtime properties and not the hsql in-memory
	 * database
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
