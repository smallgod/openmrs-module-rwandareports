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
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.TestUtil;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can be run like a junit test, but it is not actually a test.
 * To run it, you should configure your runtime properties file to contain the
 * database whose tables you wish to base the test dataset from, remove the
 * Ignore annotation, and run this as a unit test
 */
@Ignore
public class CreateTestDataSet extends BaseModuleContextSensitiveTest {

	int[] conceptsToInclude = { 5089, 5497 };

	@Test
	@SkipBaseSetup
	public void createTestMetadata() throws Exception {
		if (getLoadCount() == 1) {
			IDatabaseConnection connection = new DatabaseConnection(getConnection());
			QueryDataSet qds = new QueryDataSet(connection);

			// First, let's pull in Concepts





			qds.addTable("concept", getQuery("select * from concept where concept_id in :ids", conceptsToInclude));

			String outFile = System.getProperty("java.io.tmpdir") + File.separator + "TestMetadataDataSet.xml";
			FlatXmlDataSet.write(qds, new FileOutputStream(outFile));
		}
	}

	private String getQuery(String query, int[] ids) {
		StringBuilder ret = new StringBuilder("(");
		for (int id : ids) {
			ret.append(ret.length() == 1 ? "" : ",").append(id);
		}
		ret.append(")");
		return query.replace(":ids", ret.toString());
	}


	/**
	 * This test creates an xml dbunit file from the current database connection information found
	 * in the runtime properties. This method has to "skip over the base setup" because it tries to
	 * do things (like initialize the database) that shouldn't be done to a standard mysql database.
	 * 
	 * @throws Exception
	 */
	//@Test
	@SkipBaseSetup
	public void shouldExportData() throws Exception {





		// only run this test if it is being run alone.
		// this allows the junit-report ant target and the "right-
		// click-on-/test/api-->run as-->junit test" methods to skip
		// over this whole "test"
		if (getLoadCount() != 1)
			return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());

		int[] patientsToExport = { 13612, 13246 };
		
		// partial database export
		QueryDataSet[] dataSets = new QueryDataSet[patientsToExport.length];
		for (int i=0; i<patientsToExport.length; i++) {
			int pId = patientsToExport[i];
			QueryDataSet qds = new QueryDataSet(connection);

			qds.addTable("obs", "select * from obs where person_id = " + pId);

			dataSets[i] = qds;
		}
		CompositeDataSet cds = new CompositeDataSet(dataSets);


		/*
		global_property

		initialDataSet.addTable("user_role", "SELECT * FROM user_role");
		initialDataSet.addTable("users", "SELECT * FROM users");

		initialDataSet.addTable("encounter", "SELECT * FROM encounter");
		initialDataSet.addTable("drug_order", "SELECT * FROM drug_order");
		initialDataSet.addTable("obs", "SELECT * FROM obs");
		initialDataSet.addTable("orders", "SELECT * FROM orders");
		initialDataSet.addTable("patient", "SELECT * FROM patient");
		initialDataSet.addTable("patient_identifier", "SELECT * FROM patient_identifier");
		initialDataSet.addTable("patient_program", "SELECT * FROM patient_program");
		initialDataSet.addTable("patient_state", "SELECT * FROM patient_state");
		initialDataSet.addTable("person", "SELECT * FROM person");
		initialDataSet.addTable("person_address", "SELECT * FROM person_address");
		initialDataSet.addTable("person_attribute", "SELECT * FROM person_attribute");
		initialDataSet.addTable("person_name", "SELECT * FROM person_name");
		initialDataSet.addTable("relationship", "SELECT * FROM relationship");
		*/

		/*
		initialDataSet.addTable("field", "SELECT * FROM field");
		initialDataSet.addTable("field_answer", "SELECT * FROM field_answer");
		initialDataSet.addTable("field_type", "SELECT * FROM field_type");
		initialDataSet.addTable("form", "SELECT * FROM form");
		initialDataSet.addTable("form_field", "SELECT * FROM form_field");
		initialDataSet.addTable("hl7_source", "SELECT * FROM hl7_source");
		*/

		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "TestMetadataDataSet.xml";
		FlatXmlDataSet.write(cds, new FileOutputStream(outFile));


		
		// full database export
		//IDataSet fullDataSet = connection.createDataSet();
		//FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
		
		// dependent tables database export: export table X and all tables that
		// have a PK which is a FK on X, in the right order for insertion
		//String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
		//IDataSet depDataset = connection.createDataSet( depTableNames );
		//FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml")); 
		
		//TestUtil.printOutTableContents(getConnection(), "encounter_type", "encounter");
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
