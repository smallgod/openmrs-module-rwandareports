package org.openmrs.module.rwandareports.web.controller;

import org.openmrs.module.rwandareports.reporting.SetupAdultHIVConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupAdultLateVisitAndCD4Report;
import org.openmrs.module.rwandareports.reporting.SetupAsthmaConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupAsthmaLateVisit;
import org.openmrs.module.rwandareports.reporting.SetupAsthmaQuarterlyAndMonthReport;
import org.openmrs.module.rwandareports.reporting.SetupChemotherapyDailyExpectedPatientList;
import org.openmrs.module.rwandareports.reporting.SetupChemotherapyExpectedPatientList;
import org.openmrs.module.rwandareports.reporting.SetupCombinedHFCSPConsultationReport;
import org.openmrs.module.rwandareports.reporting.SetupDataEntryDelayReport;
import org.openmrs.module.rwandareports.reporting.SetupDataQualityIndicatorReport;
import org.openmrs.module.rwandareports.reporting.SetupDiabetesConsultAndLTFU;
import org.openmrs.module.rwandareports.reporting.SetupDiabetesQuarterlyAndMonthReport;
import org.openmrs.module.rwandareports.reporting.SetupEligibleForViralLoadReport;
import org.openmrs.module.rwandareports.reporting.SetupEpilepsyConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupEpilepsyLateVisit;
import org.openmrs.module.rwandareports.reporting.SetupExposedClinicInfantMonthly;
import org.openmrs.module.rwandareports.reporting.SetupHIVResearchDataQualitySheet;
import org.openmrs.module.rwandareports.reporting.SetupHIVResearchExtractionSheet;
import org.openmrs.module.rwandareports.reporting.SetupHeartFailureConsultSheet;
import org.openmrs.module.rwandareports.reporting.SetupHeartFailureLateVisit;
import org.openmrs.module.rwandareports.reporting.SetupHeartFailureQuarterlyAndMonthlyReport;
import org.openmrs.module.rwandareports.reporting.SetupHeartFailurereport;
import org.openmrs.module.rwandareports.reporting.SetupHypertensionConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupHypertensionLateVisit;
import org.openmrs.module.rwandareports.reporting.SetupHypertensionQuarterlyAndMonthlyReport;
import org.openmrs.module.rwandareports.reporting.SetupIDProgramQuarterlyIndicatorReport;
import org.openmrs.module.rwandareports.reporting.SetupMissedChemotherapyPatientList;
import org.openmrs.module.rwandareports.reporting.SetupMissingCD4Report;
import org.openmrs.module.rwandareports.reporting.SetupMonthlyCD4DeclineReport;
import org.openmrs.module.rwandareports.reporting.SetupNCDConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupNCDLateVisitandLTFUReport;
import org.openmrs.module.rwandareports.reporting.SetupOncologyDailyDrugList;
import org.openmrs.module.rwandareports.reporting.SetupOncologyDataExtractionSheet;
import org.openmrs.module.rwandareports.reporting.SetupOncologyExternalBiopsyContactList;
import org.openmrs.module.rwandareports.reporting.SetupOncologyOutpatientAppointmentList;
import org.openmrs.module.rwandareports.reporting.SetupOncologyOutpatientClinicMissedVisit;
import org.openmrs.module.rwandareports.reporting.SetupOncologyOutpatientClinicPatientList;
import org.openmrs.module.rwandareports.reporting.SetupOncologyQuarterlyIndicatorReport;
import org.openmrs.module.rwandareports.reporting.SetupOncologyTestPatientList;
import org.openmrs.module.rwandareports.reporting.SetupOncologyTreatmentAdministrationPlan;
import org.openmrs.module.rwandareports.reporting.SetupPMTCTCombinedClinicMotherMonthlyReport;
import org.openmrs.module.rwandareports.reporting.SetupPMTCTFoodDistributionReport;
import org.openmrs.module.rwandareports.reporting.SetupPMTCTFormCompletionSheet;
import org.openmrs.module.rwandareports.reporting.SetupPMTCTFormulaDistributionReport;
import org.openmrs.module.rwandareports.reporting.SetupPMTCTPregnancyConsultationReport;
import org.openmrs.module.rwandareports.reporting.SetupPMTCTPregnancyMonthlyReport;
import org.openmrs.module.rwandareports.reporting.SetupPediHIVConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupPediatricLateVisitAndCD4Report;
import org.openmrs.module.rwandareports.reporting.SetupPrimaryCareRegistrationReport;
import org.openmrs.module.rwandareports.reporting.SetupQuarterlyCrossSiteIndicatorByDistrictReport;
import org.openmrs.module.rwandareports.reporting.SetupQuarterlyViralLoadReport;
import org.openmrs.module.rwandareports.reporting.SetupRwandaPrimaryCareReport;
import org.openmrs.module.rwandareports.reporting.SetupTBConsultationSheet;
import org.openmrs.module.rwandareports.reporting.SetupHMISRwandaReportBySite;
import org.openmrs.module.rwandareports.util.CleanReportingTablesAndRegisterAllReports;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RwandaRegisterAllReportsFormController {
	@RequestMapping("/module/rwandareports/register_allReports")
	public ModelAndView registerAllReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerHIVReports();	
		CleanReportingTablesAndRegisterAllReports.registerNCDReports();
		CleanReportingTablesAndRegisterAllReports.registerCentralReports();
		CleanReportingTablesAndRegisterAllReports.registerSiteReports();
		CleanReportingTablesAndRegisterAllReports.registerPCReports();
		CleanReportingTablesAndRegisterAllReports.registerCHWReports();
		CleanReportingTablesAndRegisterAllReports.registerOncologyReports();	
		CleanReportingTablesAndRegisterAllReports.registerPDCReports();
		CleanReportingTablesAndRegisterAllReports.registerMHReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_allHIVReports")
	public ModelAndView registerAllHIVReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerHIVReports();	
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allNCDReports")
	public ModelAndView registerAllNCDReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerNCDReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allCentralReports")
	public ModelAndView registerAllCentralReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerCentralReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allSiteReports")
	public ModelAndView registerAllSiteReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerSiteReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allOncologyReport")
	public ModelAndView registerAllOncologyReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerOncologyReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allPCReport")
	public ModelAndView registerAllPCReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerPCReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allCHWReport")
	public ModelAndView registerAllCHWReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerCHWReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	@RequestMapping("/module/rwandareports/register_allPDCReport")
	public ModelAndView registerAllregisterPDCReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerPDCReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_allMHReport")
	public ModelAndView registerAllregisterMHReports() throws Exception{
		CleanReportingTablesAndRegisterAllReports.registerMHReports();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	
}
