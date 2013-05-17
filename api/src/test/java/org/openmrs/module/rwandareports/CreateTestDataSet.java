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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.rwandareports.util.MetadataLookup;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.util.Iterator;

/**
 * This class can be run like a junit test, but it is not actually a test.
 * To run it, you should configure your runtime properties file to contain the
 * database whose tables you wish to base the test dataset from, remove the
 * Ignore annotation, and run this as a unit test
 */
//@Ignore
public class CreateTestDataSet extends BaseModuleContextSensitiveTest {

	@Test
	@SkipBaseSetup
	public void createTestMetadata() throws Exception {
		if (getLoadCount() == 1) {
			IDatabaseConnection connection = new DatabaseConnection(getConnection());
			QueryDataSet qds = new QueryDataSet(connection);

			ListMap<String, OpenmrsObject> dataToInclude = new ListMap<String, OpenmrsObject>();

			dataToInclude.putInList("conceptIds", MetadataLookup.getConcept("PIH:WEIGHT (KG)"));
			dataToInclude.putInList("conceptIds", MetadataLookup.getConcept("PIH:HEIGHT (CM)"));
			dataToInclude.putInList("conceptIds", MetadataLookup.getConcept("PIH:CD4 COUNT"));

			Program hivProgram = MetadataLookup.getProgram("Adult HIV PROGRAM");
			dataToInclude.putInList("programIds", hivProgram);
			dataToInclude.putInList("conceptIds", hivProgram.getConcept());

			ProgramWorkflow treatmentGroup = MetadataLookup.getProgramWorkflow("Adult HIV PROGRAM", "TREATMENT GROUP");
			dataToInclude.putInList("workflowIds", treatmentGroup);
			dataToInclude.putInList("conceptIds", treatmentGroup.getConcept());

			ProgramWorkflowState groupFollowing = MetadataLookup.getProgramWorkflowState("Adult HIV PROGRAM", "TREATMENT GROUP", "GROUP FOLLOWING");
			dataToInclude.putInList("workflowStateIds", groupFollowing);
			dataToInclude.putInList("conceptIds", groupFollowing.getConcept());

			ProgramWorkflowState group1 = MetadataLookup.getProgramWorkflowState("Adult HIV PROGRAM", "TREATMENT GROUP", "GROUP 1");
			dataToInclude.putInList("workflowStateIds", group1);
			dataToInclude.putInList("conceptIds", group1.getConcept());

			ProgramWorkflowState group2 = MetadataLookup.getProgramWorkflowState("Adult HIV PROGRAM", "TREATMENT GROUP", "GROUP 2");
			dataToInclude.putInList("workflowStateIds", group2);
			dataToInclude.putInList("conceptIds", group2.getConcept());

			dataToInclude.putInList("locationIds", MetadataLookup.getLocation("Mulindi Health Center"));
			dataToInclude.putInList("locationIds", MetadataLookup.getLocation("Kirehe Health Center"));

			dataToInclude.putInList("orderTypeIds", MetadataLookup.getOrderType("2"));
			dataToInclude.putInList("orderTypeIds", MetadataLookup.getOrderType("Lab Order"));

			dataToInclude.putInList("encounterTypeIds", MetadataLookup.getEncounterType("Adult HIV"));

			dataToInclude.putInList("identifierTypeIds", MetadataLookup.getPatientIdentifierType("IMB ID"));
			dataToInclude.putInList("personAttributeTypeIds", MetadataLookup.getPersonAttributeType("Health Center"));
			dataToInclude.putInList("relationshipTypeIds", MetadataLookup.getRelationshipType("Accompagnateur/Patient"));

			loadData(qds, dataToInclude, "concept_class", "concept_class_id in (select class_id from concept where concept_id in :conceptIds)");
			loadData(qds, dataToInclude, "concept_datatype", "concept_datatype_id in (select datatype_id from concept where concept_id in :conceptIds)");
			loadData(qds, dataToInclude, "concept", "concept_id in :conceptIds");
			loadData(qds, dataToInclude, "concept_name", "concept_id in :conceptIds", "locale = 'en'", "concept_name_type in ('SHORT', 'FULLY_SPECIFIED')");
			loadData(qds, dataToInclude, "concept_description", "concept_id in :conceptIds", "locale = 'en'");
			loadData(qds, dataToInclude, "concept_numeric", "concept_id in :conceptIds");
			loadData(qds, dataToInclude, "concept_set", "concept_id in :conceptIds");
			loadData(qds, dataToInclude, "concept_answer", "concept_id in :conceptIds");
			loadData(qds, dataToInclude, "concept_reference_source", "name = 'PIH'");
			loadData(qds, dataToInclude, "concept_reference_term", "concept_reference_term_id in (select concept_reference_term_id from concept_reference_map where concept_id in :conceptIds)", "concept_source_id in (select concept_source_id from concept_reference_source where name = 'PIH')");
			loadData(qds, dataToInclude, "concept_reference_map", "concept_id in :conceptIds");
			loadData(qds, dataToInclude, "concept_map_type", "concept_map_type_id in (select concept_map_type_id from concept_reference_map where concept_id in :conceptIds)");
			loadData(qds, dataToInclude, "drug", "concept_id in :conceptIds");

			loadData(qds, dataToInclude, "location", "location_id in :locationIds");

			loadData(qds, dataToInclude, "encounter_type", "encounter_type_id in :encounterTypeIds");
			loadData(qds, dataToInclude, "encounter_role");
			loadData(qds, dataToInclude, "form", "encounter_type in :encounterTypeIds");
			loadData(qds, dataToInclude, "visit_type");

			loadData(qds, dataToInclude, "order_type", "order_type_id in :orderTypeIds");

			loadData(qds, dataToInclude, "patient_identifier_type", "patient_identifier_type_id in :identifierTypeIds");
			loadData(qds, dataToInclude, "person_attribute_type", "person_attribute_type_id in :personAttributeTypeIds");
			loadData(qds, dataToInclude, "relationship_type", "relationship_type_id in :relationshipTypeIds");

			loadData(qds, dataToInclude, "program", "program_id in :programIds");
			loadData(qds, dataToInclude, "program_workflow", "program_workflow_id in :workflowIds");
			loadData(qds, dataToInclude, "program_workflow_state", "program_workflow_state_id in :workflowStateIds");

			loadData(qds, dataToInclude, "provider", "person_id = 1");

			//String outFile = System.getProperty("java.io.tmpdir") + File.separator + "TestMetadataDataSet.xml";
			FlatXmlDataSet.write(qds, System.out);
		}
	}

	protected void loadData(QueryDataSet qds, String tableName, String... constraint) throws Exception {
		loadData(qds, null, tableName, constraint);
	}

	protected void loadData(QueryDataSet qds, ListMap<String, ? extends OpenmrsObject> data, String tableName, String... constraint) throws Exception {
		String constraintQuery = "";
		if (constraint != null && constraint.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<constraint.length; i++) {
				sb.append(sb.length() == 0 ? " where " : " and ");
				sb.append(constraint[i]);
			}
			constraintQuery = sb.toString();
		}
		if (data != null) {
			for (String d : data.keySet()) {
				String toCheck = ":"+d;
				if (constraintQuery.contains(toCheck)) {
					StringBuilder sb = new StringBuilder();
					for (Iterator<? extends OpenmrsObject> i = data.get(d).iterator(); i.hasNext();) {
						OpenmrsObject o = i.next();
						sb.append(o.getId()).append(i.hasNext() ? "," : "");
					}
					constraintQuery = constraintQuery.replace(toCheck, "("+sb+")");
				}
			}
		}
		String query = "select * from " + tableName + constraintQuery;
		System.out.println("Adding Query: " + query);
		qds.addTable(tableName, query);
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

	@Before
	public void setup() throws Exception {
		authenticate();
	}

}
