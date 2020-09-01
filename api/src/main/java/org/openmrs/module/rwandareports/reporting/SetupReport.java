package org.openmrs.module.rwandareports.reporting;

/**
 * Report Setup / teardown interface
 */
public interface SetupReport {
	void setup() throws Exception;
	void delete();
}
