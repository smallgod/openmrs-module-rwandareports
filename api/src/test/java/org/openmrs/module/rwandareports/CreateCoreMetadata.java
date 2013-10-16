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
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

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
public class CreateCoreMetadata extends BaseModuleContextSensitiveTest {

	public String getOutputDirectory() {
		return "/home/mseaton/Desktop";
	}

	public String[] getMetadataTables() {
		return new String[] {
			"concept", "concept_answer", "concept_class", "concept_complex",
			"concept_datatype", "concept_description", "concept_map_type", "concept_name",
			"concept_name_tag", "concept_name_tag_map", "concept_numeric", "concept_reference_map",
			"concept_reference_source", "concept_reference_term", "concept_reference_term_map",
			"concept_set", "concept_set_derived", "concept_stop_word",
			"drug", "drug_ingredient", "encounter_role", "encounter_type", "form",
			"location", "location_attribute", "location_attribute_type", "location_tag", "location_tag_map",
			"order_type", "orderextension_order_set", "orderextension_order_set_member",
			"patient_identifier_type", "person_attribute_type", "privilege",
			"program", "program_workflow", "program_workflow_state", "provider_attribute_type",
			"relationship_type", "role", "visit_attribute_type", "visit_type"
		};
	}

	@Test
	public void run() throws Exception {

		// only run this test if it is being run alone and if an output directory has been specified
		if (getLoadCount() != 1 || ObjectUtil.isNull(getOutputDirectory()))
			return;
		
		// database connection for dbunit
		IDatabaseConnection connection = new DatabaseConnection(getConnection());
		
		// partial database export
		QueryDataSet initialDataSet = new QueryDataSet(connection);

		initialDataSet.addTable("users", "SELECT * FROM users where user_id = 1");
		initialDataSet.addTable("user_role", "SELECT * FROM user_role where user_id = 1");
		initialDataSet.addTable("persons", "SELECT * FROM person where person_id in (select person_id from users where user_id = 1)");
		initialDataSet.addTable("role", "SELECT * FROM role where role = 'System Developer'");
		initialDataSet.addTable("persons", "SELECT * FROM person where person_id in (select person_id from users where user_id = 1)");

		for (String table : getMetadataTables()) {
			initialDataSet.addTable(table, "SELECT * FROM " + table);
		}

		File outputFile = new File(getOutputDirectory(), "coreMetadata.xml");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FlatXmlDataSet.write(initialDataSet, baos);
		String contents = baos.toString("UTF-8");
		FileWriter writer = new FileWriter(outputFile);

		for (String line : contents.split(System.getProperty("line.separator"))) {
			if (line.contains("<concept ")) {
				line = line.replace("short_name=\"\" ", "");
				line = line.replace("description=\"\" ", "");
			}
			if (line.contains("system_id=\"admin\"")) {
				line = "  <users user_id=\"1\" person_id=\"1\" system_id=\"admin\" username=\"\" password=\"4a1750c8607d0fa237de36c6305715c223415189\" salt=\"c788c6ad82a157b712392ca695dfcf2eed193d7f\" secret_question=\"\" creator=\"1\" date_created=\"2005-01-01 00:00:00.0\" changed_by=\"1\" date_changed=\"2007-09-20 21:54:12.0\" retired=\"false\" retire_reason=\"\" uuid=\"1010d442-e134-11de-babe-001e378eb67e\"/>";
			}
			line = line.replaceAll("creator=\"\\d+\"", "creator=\"1\"");
			line = line.replaceAll("changed_by=\"\\d+\"", "changed_by=\"1\"");
			line = line.replaceAll("retired_by=\"\\d+\"", "retired_by=\"1\"");

			writer.write(line + System.getProperty("line.separator"));
		}

		writer.flush();
		writer.close();
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
