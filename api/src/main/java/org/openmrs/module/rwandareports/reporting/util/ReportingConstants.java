package org.openmrs.module.rwandareports.reporting.util;

/**
 * Constants for reporting module Centralizes all constants related to report names, parameter
 * names, and SQL resource paths
 * 
 * @author smallGod
 */
public class ReportingConstants {
	
	// ==================== SQL Resource Paths ====================
	
	/**
	 * Path to Lab Result Report SQL query file
	 */
	public static final String SQL_LAB_RESULT_REPORT = "sql/lab_result_report.sql";
	
	/**
	 * Path to Lab Exam Report SQL query file
	 */
	public static final String SQL_LAB_EXAM_REPORT = "sql/lab_exam_report.sql";
	
	// ==================== Report Names ====================
	
	/**
	 * Display name for Lab Results Report
	 */
	public static final String REPORT_LAB_RESULT = "Lab - Results Report";
	
	/**
	 * Display name for Lab Exam Report
	 */
	public static final String REPORT_LAB_EXAM = "Lab - Exam Report";
	
	// ==================== Report Design Names ====================
	
	/**
	 * CSV design name for Lab Results Report
	 */
	public static final String DESIGN_LAB_RESULT_CSV = "Lab - Results Report.csv_";
	
	/**
	 * CSV design name for Lab Exam Report
	 */
	public static final String DESIGN_LAB_EXAM_CSV = "Lab - Exam Report Exam.csv_";
	
	// ==================== Parameter Names ====================
	
	/**
	 * Parameter name for start date
	 */
	public static final String PARAM_START_DATE = "startDate";
	
	/**
	 * Parameter name for end date
	 */
	public static final String PARAM_END_DATE = "endDate";
	
	/**
	 * Parameter name for location filter
	 */
	public static final String PARAM_LOCATION = "location";
	
	/**
	 * Parameter name for concept filter
	 */
	public static final String PARAM_CONCEPT = "concept";
	
	/**
	 * Display label for start date parameter
	 */
	public static final String PARAM_START_DATE_LABEL = "From:";
	
	/**
	 * Display label for end date parameter
	 */
	public static final String PARAM_END_DATE_LABEL = "To:";
	
	/**
	 * Display label for concept parameter (exam name)
	 */
	public static final String PARAM_CONCEPT_LABEL = "Exam Name";
	
	// ==================== SQL Placeholder Names ====================
	
	/**
	 * Placeholder for order type ID in SQL queries Usage in SQL: WHERE order_type_id =
	 * {orderTypeId}
	 */
	public static final String PLACEHOLDER_ORDER_TYPE_ID = "orderTypeId";
	
	/**
	 * Placeholder for health facility attribute type ID in SQL queries Usage in SQL: WHERE
	 * person_attribute_type_id = {healthFacilityTypeId}
	 */
	public static final String PLACEHOLDER_HEALTH_FACILITY_TYPE_ID = "healthFacilityTypeId";
	
	// ==================== Global Property Keys ====================
	
	/**
	 * Global property key for laboratory order type
	 */
	public static final String GP_LAB_ORDER_TYPE = "laboratorymanagement.orderType.labOrderTypeId";
	
	// Private constructor to prevent instantiation
	private ReportingConstants() {
		throw new AssertionError("Cannot instantiate constants class");
	}
}
