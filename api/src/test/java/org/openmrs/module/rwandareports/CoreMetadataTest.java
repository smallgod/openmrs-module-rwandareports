package org.openmrs.module.rwandareports;

import ch.vorburger.mariadb4j.Configuration;
import ch.vorburger.mariadb4j.DB;
import junit.framework.Assert;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.test.SkipBaseSetup;

import java.sql.Connection;
import java.util.List;

@SkipBaseSetup
public class CoreMetadataTest {

	@Test
	public void test() throws Exception {

		List<Concept> l = Context.getConceptService().getAllConcepts();
		System.out.println("We have " + l.size() + " concepts");

	}

	@Test
	public void testEmbeddedMariaDB4j() throws Exception {
		Configuration options = new Configuration();
		options.setPort(3307);
		DB db = DB.newEmbeddedDB(options);
		db.start();

		Connection conn = null;
		try {
			conn = db.getConnection();
			QueryRunner qr = new QueryRunner();

			// Should be able to create a new table
			qr.update(conn, "CREATE TABLE hello(world VARCHAR(100))");

			// Should be able to insert into a table
			qr.update(conn, "INSERT INTO hello VALUES ('Hello, world')");

			// Should be able to select from a table
			List<String> results = qr.query(conn, "SELECT * FROM hello", new ColumnListHandler<String>());
			Assert.assertEquals(1, results.size());
			Assert.assertEquals("Hello, world", results.get(0));
		}
		finally {
			DbUtils.closeQuietly(conn);
		}
	}
}
