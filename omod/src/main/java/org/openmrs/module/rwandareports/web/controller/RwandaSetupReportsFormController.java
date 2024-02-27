package org.openmrs.module.rwandareports.web.controller;

import org.openmrs.module.rwandareports.reporting.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RwandaSetupReportsFormController {


	@RequestMapping("/module/rwandareports/register_adulthivartregister")
	public ModelAndView registerAdultHivArtRegiser() throws Exception {
		// new SetupHivArtRegisterReport(false).setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_adulthivartregister")
	public ModelAndView removeAdultHivArtRegister() throws Exception {
		// new SetupHivArtRegisterReport(false).delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_pedihivartregister")
	public ModelAndView registerPediHivArtRegiser() throws Exception {
		// new SetupHivArtRegisterReport(true).setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pedihivartregister")
	public ModelAndView removePediHivArtRegister() throws Exception {
		// new SetupHivArtRegisterReport(true).delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_combinedHSCSPConsultation")
	public ModelAndView registerCombinedHSCSPConsultation() throws Exception {
		new SetupCombinedHFCSPConsultationReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_combinedHSCSPConsultation")
	public ModelAndView removeCombinedHSCSPConsultation() throws Exception {
		new SetupCombinedHFCSPConsultationReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_EncounterAndObsReport")
	public ModelAndView registerEncounterAndObsReport() throws Exception {
		new SetupGenericEncounterReport().setup();
		//new SetupGenericEncounterBySiteReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_EncounterAndObsReport")
	public ModelAndView removeEncounterAndObsReport() throws Exception {
		new SetupGenericEncounterReport().delete();
		//new SetupGenericEncounterBySiteReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	/*
	 * @RequestMapping("/module/rwandareports/register_pmtctFoodDistributionSheet"
	 * ) public ModelAndView registerPmtctFoodDistribution() throws Exception {
	 * new SetupPMTCTFoodDistributionReport().setup(); return new
	 * ModelAndView(new RedirectView("rwandareports.form")); }
	 * 
	 * @RequestMapping("/module/rwandareports/remove_pmtctFoodDistributionSheet")
	 * public ModelAndView removePmtctFoodDistribution() throws Exception { new
	 * SetupPMTCTFoodDistributionReport().delete(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 * 
	 * @RequestMapping(
	 * "/module/rwandareports/register_pmtctFormulaDistributionSheet") public
	 * ModelAndView registerPmtctFormulaDistribution() throws Exception { new
	 * SetupPMTCTFormulaDistributionReport().setup(); return new
	 * ModelAndView(new RedirectView("rwandareports.form")); }
	 * 
	 * @RequestMapping("/module/rwandareports/remove_pmtctFormulaDistributionSheet"
	 * ) public ModelAndView removePmtctFormulaDistribution() throws Exception {
	 * new SetupPMTCTFormulaDistributionReport().delete(); return new
	 * ModelAndView(new RedirectView("rwandareports.form")); }
	 */
	@RequestMapping("/module/rwandareports/register_pmtctPregnancyConsultationSheet")
	public ModelAndView registerPmtctPregnancyConsultation() throws Exception {
		new SetupPMTCTPregnancyConsultationReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pmtctPregnancyConsultationSheet")
	public ModelAndView removePmtctPregnanacyConsultation() throws Exception {
		new SetupPMTCTPregnancyConsultationReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	/*
	 * @RequestMapping("/module/rwandareports/register_pmtctFormCompletionSheet")
	 * public ModelAndView registerPmtctFormCompletionSheet() throws Exception {
	 * new SetupPMTCTFormCompletionSheet().setup(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 * 
	 * @RequestMapping("/module/rwandareports/remove_pmtctFormCompletionSheet")
	 * public ModelAndView removePmtctFormCompletionSheet() throws Exception {
	 * new SetupPMTCTFormCompletionSheet().delete(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 */

	// Consult sheets
	@RequestMapping("/module/rwandareports/register_pediHIVConsultationSheet")
	public ModelAndView registerPediHIVConsultationSheet() throws Exception {
		new SetupPediHIVConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pediHIVConsultationSheet")
	public ModelAndView removePediHIVConsultationSheet() throws Exception {
		new SetupPediHIVConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_adultHIVConsultationSheet")
	public ModelAndView registerAdultHIVConsultationSheet() throws Exception {
		new SetupAdultHIVConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_adultHIVConsultationSheet")
	public ModelAndView removeAdultHIVConsultationSheet() throws Exception {
		new SetupAdultHIVConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_tbConsultationSheet")
	public ModelAndView registerTbConsultationSheet() throws Exception {
		new SetupTBConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_tbConsultationSheet")
	public ModelAndView removeTbConsultationSheet() throws Exception {
		new SetupTBConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_asthmaConsultationSheet")
	public ModelAndView registerAsthmaConsultationSheet() throws Exception {
		new SetupAsthmaConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_asthmaConsultationSheet")
	public ModelAndView removeAsthmaConsultationSheet() throws Exception {
		new SetupAsthmaConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Adult Late visit And CD4

	@RequestMapping("/module/rwandareports/register_adultLatevisitAndCD4")
	public ModelAndView registerAdultLatevisitAndCD4() throws Exception {
		new SetupAdultLateVisitAndCD4Report().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_adultLatevisitAndCD4")
	public ModelAndView removeAdultLatevisitAndCD4() throws Exception {
		new SetupAdultLateVisitAndCD4Report().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Pediatric Late visit And CD4
	@RequestMapping("/module/rwandareports/register_pediatricLatevisitAndCD4")
	public ModelAndView registerPediatricLatevisitAndCD4() throws Exception {
		new SetupPediatricLateVisitAndCD4Report().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pediatricLatevisitAndCD4")
	public ModelAndView removePediatricLatevisitAndCD4() throws Exception {
		new SetupPediatricLateVisitAndCD4Report().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}



	// Remove/Register Heart Failure report by site and all
	@RequestMapping("/module/rwandareports/remove_heartFailureReport")
	public ModelAndView removeHeartFailureIndicator() throws Exception {
		new SetupHeartFailurereport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_heartFailureReport")
	public ModelAndView registerHeartFailureIndicatorIndicator()
			throws Exception {
		new SetupHeartFailurereport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// end of Heart failure report

	/*
	 * @RequestMapping("/module/rwandareports/register_missingCD4Report") public
	 * ModelAndView registerMissingCD4Report() throws Exception { new
	 * SetupMissingCD4Report().setup(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 * 
	 * @RequestMapping("/module/rwandareports/remove_missingCD4Report") public
	 * ModelAndView removeMissingCD4Report() throws Exception { new
	 * SetupMissingCD4Report().delete(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 */
	@RequestMapping("/module/rwandareports/register_dataQualityReport")
	public ModelAndView registerDataQualityReport() throws Exception {
		new SetupDataQualityIndicatorReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_dataQualityReport")
	public ModelAndView removeDataQualityReport() throws Exception {
		new SetupDataQualityIndicatorReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Diabetes Consult/LTFU
	@RequestMapping("/module/rwandareports/register_DiabetesConsultAndLTFU")
	public ModelAndView registerDiabetesConsultAndLTFU() throws Exception {
		new SetupDiabetesConsultAndLTFU().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_DiabetesConsultAndLTFU")
	public ModelAndView removeDiabetesConsultAndLTFU() throws Exception {
		new SetupDiabetesConsultAndLTFU().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// NCD late visit and LTFU
	@RequestMapping("/module/rwandareports/register_NCDlatevistAndLTFU")
	public ModelAndView registerNCDlatevistAndLTFUReport() throws Exception {
		new SetupNCDLateVisitandLTFUReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_NCDlatevistAndLTFU")
	public ModelAndView removeNCDlatevistAndLTFUReport() throws Exception {
		new SetupNCDLateVisitandLTFUReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Pediatric Late visit And CD4
	@RequestMapping("/module/rwandareports/register_NCDConsult")
	public ModelAndView registerNCDConsult() throws Exception {
		new SetupNCDConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_NCDConsult")
	public ModelAndView removeNCDConsult() throws Exception {
		new SetupNCDConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	/*
	 * @RequestMapping("/module/rwandareports/register_monthlyCD4Decline")
	 * public ModelAndView registerMonthlyCD4Decline() throws Exception { new
	 * SetupMonthlyCD4DeclineReport().setup(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 * 
	 * @RequestMapping("/module/rwandareports/remove_monthlyCD4Decline") public
	 * ModelAndView removeMonthlyCD4Decline() throws Exception { new
	 * SetupMonthlyCD4DeclineReport().delete(); return new ModelAndView(new
	 * RedirectView("rwandareports.form")); }
	 */


	@RequestMapping("/module/rwandareports/register_DiabetesQuarterlyAndMonthReport")
	public ModelAndView registerDiabetesQuarterlyAndMonthReport()
			throws Exception {
		new SetupDiabetesQuarterlyAndMonthReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_DiabetesQuarterlyAndMonthReport")
	public ModelAndView removeDiabetesQuarterlyAndMonthReport()
			throws Exception {
		new SetupDiabetesQuarterlyAndMonthReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register HMIS report
	@RequestMapping("/module/rwandareports/remove_hmisReport")
	public ModelAndView removeHMISIndicator() throws Exception {
		new SetupHMISRwandaReportBySite().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_hmisReport")
	public ModelAndView registerHMISIndicator() throws Exception {
		new SetupHMISRwandaReportBySite().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register PMTCT Combined Clinic Mother Monthly Report report

	@RequestMapping("/module/rwandareports/register_pmtctCombinedClinicMotherMonthlyReport")
	public ModelAndView registerPMTCTCombinedClinicMotherMonthly()
			throws Exception {
		new SetupPMTCTCombinedClinicMotherMonthlyReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pmtctCombinedClinicMotherMonthlyReport")
	public ModelAndView removePMTCTCombinedClinicMotherMonthly()
			throws Exception {
		new SetupPMTCTCombinedClinicMotherMonthlyReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Asthma Quarterly And Monthly Report

	@RequestMapping("/module/rwandareports/register_asthmaQuarterlyAndMonthReport")
	public ModelAndView registerAsthmaQuarterlyAndMonthReport()
			throws Exception {
		new SetupAsthmaQuarterlyAndMonthReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_asthmaQuarterlyAndMonthReport")
	public ModelAndView removeAsthmaQuarterlyAndMonthReport() throws Exception {
		new SetupAsthmaQuarterlyAndMonthReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Asthma Late Visit Report

	@RequestMapping("/module/rwandareports/register_asthmaLateVisitReport")
	public ModelAndView registerAsthmaLateVisit() throws Exception {
		new SetupAsthmaLateVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_asthmaLateVisitReport")
	public ModelAndView removeAsthmaLateVisit() throws Exception {
		new SetupAsthmaLateVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Hypertension Reports
	@RequestMapping("/module/rwandareports/register_hypertensionConsultationSheet")
	public ModelAndView registerHypertensionConsultationSheet()
			throws Exception {
		new SetupHypertensionConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_hypertensionConsultationSheet")
	public ModelAndView removeHypertensionConsultationSheet() throws Exception {
		new SetupHypertensionConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_hypertensionLateVisit")
	public ModelAndView registerHypertensionLateVisit() throws Exception {
		new SetupHypertensionLateVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_hypertensionLateVisit")
	public ModelAndView removeHypertensionLateVisit() throws Exception {
		new SetupHypertensionLateVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_hypertensionQuarterlyAndMonthlyReport")
	public ModelAndView registerHypertensionQuarterlyAndMonthlyReport()
			throws Exception {
		new SetupHypertensionQuarterlyAndMonthlyReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_hypertensionQuarterlyAndMonthlyReport")
	public ModelAndView removeHypertensionQuarterlyAndMonthlyReport()
			throws Exception {
		new SetupHypertensionQuarterlyAndMonthlyReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register PMTCT Pregnancy Monthly Report

	@RequestMapping("/module/rwandareports/register_pmtctPregMonthlyReport")
	public ModelAndView registerPMTCTPregMonthlyVisit() throws Exception {
		new SetupPMTCTPregnancyMonthlyReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pmtctPregMonthlyReport")
	public ModelAndView removePMTCTPregMonthlyVisit() throws Exception {
		new SetupPMTCTPregnancyMonthlyReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Combined Infant Monthly Report

	@RequestMapping("/module/rwandareports/register_pmtctCombinedClinicInfantReport")
	public ModelAndView registerPMTCTCombinedInfantReport() throws Exception {
		new SetupExposedClinicInfantMonthly().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pmtctCombinedClinicInfantReport")
	public ModelAndView removePMTCTCombinedInfantReport() throws Exception {
		new SetupExposedClinicInfantMonthly().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Oncology Reports
	@RequestMapping("/module/rwandareports/register_treatmentAdministrationPlan")
	public ModelAndView registerTreatmentAdministrationPlan() throws Exception {
		new SetupOncologyTreatmentAdministrationPlan().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_treatmentAdministrationPlan")
	public ModelAndView removeTreatmentAdministrationPlan() throws Exception {
		new SetupOncologyTreatmentAdministrationPlan().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyBiopsyList")
	public ModelAndView registerOncologyBiopsyList() throws Exception {
		new SetupOncologyTestPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyBiopsyList")
	public ModelAndView removeOncologyBiopsyList() throws Exception {
		new SetupOncologyTestPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyExternalBiopsyList")
	public ModelAndView registerOncologyExternalBiopsyList() throws Exception {
		new SetupOncologyExternalBiopsyContactList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyExternalBiopsyList")
	public ModelAndView removeOncologyExternalBiopsyList() throws Exception {
		new SetupOncologyExternalBiopsyContactList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_chemotherapyPatientList")
	public ModelAndView registerChemotherapyPatientList() throws Exception {
		new SetupChemotherapyExpectedPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_chemotherapyPatientList")
	public ModelAndView removeChemotherapyPatientList() throws Exception {
		new SetupChemotherapyExpectedPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_chemotherapyDailyPatientList")
	public ModelAndView registerChemotherapyDailyPatientList() throws Exception {
		new SetupChemotherapyDailyExpectedPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_chemotherapyDailyPatientList")
	public ModelAndView removeChemotherapyDailyPatientList() throws Exception {
		new SetupChemotherapyDailyExpectedPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_missedChemotherapyPatientList")
	public ModelAndView registerMissedChemotherapyPatientList()
			throws Exception {
		new SetupMissedChemotherapyPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_missedChemotherapyPatientList")
	public ModelAndView removeMissedChemotherapyPatientList() throws Exception {
		new SetupMissedChemotherapyPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyOutpatientClinicPatientList")
	public ModelAndView registerOncologyOutpatientClinicPatientList()
			throws Exception {
		new SetupOncologyOutpatientClinicPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyOutpatientClinicPatientList")
	public ModelAndView removeOncologyOutpatientClinicPatientList()
			throws Exception {
		new SetupOncologyOutpatientClinicPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyOutpatientClinicMissedVisit")
	public ModelAndView registerOncologyOutpatientClinicMissedVisit()
			throws Exception {
		new SetupOncologyOutpatientClinicMissedVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyOutpatientClinicMissedVisit")
	public ModelAndView removeOncologyOutpatientClinicMissedVisit()
			throws Exception {
		new SetupOncologyOutpatientClinicMissedVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyOutpatientAppointment")
	public ModelAndView registerOncologyOutpatientAppointment()
			throws Exception {
		new SetupOncologyOutpatientAppointmentList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyOutpatientAppointment")
	public ModelAndView removeOncologyOutpatientAppointment() throws Exception {
		new SetupOncologyOutpatientAppointmentList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Data Delay
	@RequestMapping("/module/rwandareports/register_dataDelay")
	public ModelAndView registerDataDelay() throws Exception {
		new SetupDataEntryDelayReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_dataDelay")
	public ModelAndView removeDataDelay() throws Exception {
		new SetupDataEntryDelayReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// ID Program Quarterly Indicators
	@RequestMapping("/module/rwandareports/register_IDProgramQuarterlyIndicators")
	public ModelAndView registerIDProgramQuarterlyIndicators() throws Exception {
		new SetupIDProgramQuarterlyIndicatorReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_IDProgramQuarterlyIndicators")
	public ModelAndView removeIDProgramQuarterlyIndicators() throws Exception {
		new SetupIDProgramQuarterlyIndicatorReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Heart Failure consult sheet by site and all
	@RequestMapping("/module/rwandareports/remove_heartFailureConsultSheet")
	public ModelAndView removeHeartFailureConsultSheet() throws Exception {
		new SetupHeartFailureConsultSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_heartFailureConsultSheet")
	public ModelAndView registerHeartFailureConsultSheet() throws Exception {
		new SetupHeartFailureConsultSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Heart Failure Late Visit by site and all
	@RequestMapping("/module/rwandareports/remove_heartFailureLateVisit")
	public ModelAndView removeHeartFailureLateVisit() throws Exception {
		new SetupHeartFailureLateVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_heartFailureLateVisit")
	public ModelAndView registerHeartFailureLateVisit() throws Exception {
		new SetupHeartFailureLateVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Heart Failure Quarterly and Monthly Reports
	@RequestMapping("/module/rwandareports/register_heartFailureQuarterlyAndMonthlyReport")
	public ModelAndView registerHeartFailureQuarterlyAndMonthlyReport()
			throws Exception {
		new SetupHeartFailureQuarterlyAndMonthlyReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_heartFailureQuarterlyAndMonthlyReport")
	public ModelAndView removeHeartFailureQuarterlyAndMonthlyReport()
			throws Exception {
		new SetupHeartFailureQuarterlyAndMonthlyReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Oncology data extraction report
	@RequestMapping("/module/rwandareports/register_oncologydataextrationSheet")
	public ModelAndView registerOncologyDataExtrationSheet() throws Exception {
		new SetupOncologyDataExtractionSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologydataextrationSheet")
	public ModelAndView removeOncologyDataExtrationSheet() throws Exception {
		new SetupOncologyDataExtractionSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register Oncology quarterly indicato report
	@RequestMapping("/module/rwandareports/register_oncologyquarterlyindicatorreport")
	public ModelAndView registerOncologyQuarterlyIndicatorReport()
			throws Exception {
		new SetupOncologyQuarterlyIndicatorReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyquarterlyindicatorreport")
	public ModelAndView removeOncologyQuarterlyIndicatorReport()
			throws Exception {
		new SetupOncologyQuarterlyIndicatorReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyOutpatientExpectedPatientList")
	public ModelAndView registerOncologyOutpatientExpectedPatientListReport()
			throws Exception {
		new SetupOncologyOutpatientExpectedPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyOutpatientExpectedPatientList")
	public ModelAndView removeOncologyOutpatientExpectedPatientListReport()
			throws Exception {
		new SetupOncologyOutpatientExpectedPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_oncologyInpatientClinicMissedVisit")
	public ModelAndView registerOncologyInpatientClinicMissedVisitReport()
			throws Exception {
		new SetupOncologyInpatientClinicMissedVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_oncologyInpatientClinicMissedVisit")
	public ModelAndView removeOncologyInpatientClinicMissedVisitReport()
			throws Exception {
		new SetupOncologyInpatientClinicMissedVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_OncologyRegistry")
	public ModelAndView registerOncologyRegistryReport()
			throws Exception {
		new SetupOncologyRegistry().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_OncologyRegistry")
	public ModelAndView removeOncologyRegistryReport()
			throws Exception {
		new SetupOncologyRegistry().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register PBF report
	@RequestMapping("/module/rwandareports/remove_PBFReport")
	public ModelAndView removePBFIndicator() throws Exception {
		new SetupPBFReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_PBFReport")
	public ModelAndView registerPBFIndicator() throws Exception {
		new SetupPBFReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register PDC-consultation sheet
	@RequestMapping("/module/rwandareports/register_pdcWeeklySheet")
	public ModelAndView registerPDCIndicators() throws Exception {
		new SetupPDCWeeklyAlert().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pdcWeeklySheet")
	public ModelAndView removePDCIndicators() throws Exception {
		new SetupPDCWeeklyAlert().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register PDC-Weekly Consultation sheet
	@RequestMapping("/module/rwandareports/register_pdcMonthlySheet")
	public ModelAndView registerPDCMonthlyIndicators() throws Exception {
		new SetupPDCMonthlyAlert().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pdcMonthlySheet")
	public ModelAndView removePDCMonthlyIndicators() throws Exception {
		new SetupPDCMonthlyAlert().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Remove/Register PDC-Indicator Report
	@RequestMapping("/module/rwandareports/register_pdcIndicatorReport")
	public ModelAndView registerPDCIndicatorReport() throws Exception {
		new SetupPDCIndicatorReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pdcIndicatorReport")
	public ModelAndView removePDCIndicatorReport() throws Exception {
		new SetupPDCIndicatorReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	// Register/Remove PDC missed visits report
	@RequestMapping("/module/rwandareports/register_pdcMissedVisitsReport")
	public ModelAndView registerPDCMissedVisits() throws Exception {
		new SetupPDCMissedVisits().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pdcMissedVisitsReport")
	public ModelAndView removePDCMissedVisits() throws Exception {
		new SetupPDCMissedVisits().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	
	// Register/Remove PDC Monthly Lost to Follow-up report
	@RequestMapping("/module/rwandareports/register_pdcMonthlyLTFUReport")
	public ModelAndView registerPDCMonthlyLTFU() throws Exception {
		new SetupPDCMonthlyLTFU().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_pdcMonthlyLTFUReport")
	public ModelAndView removePDCMonthlyLTFU() throws Exception {
		new SetupPDCMonthlyLTFU().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_PatientByProgramReport.form")
	public ModelAndView registerGenericPatientsByProgramReport()
			throws Exception {
		new SetupGenericPatientByProgramReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_PatientByProgramReport.form")
	public ModelAndView removeGenericPatientsByProgramReport() throws Exception {
		new SetupGenericPatientByProgramReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_DrugReport")
	public ModelAndView registerDrugReport() throws Exception {
		new SetupGenericDrugReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_DrugReport")
	public ModelAndView removeDrugReport() throws Exception {
		new SetupGenericDrugReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_CKDMissedVisitReport")
	public ModelAndView registerCKDMissedVisitReport() throws Exception {
		new SetupCKDMissedvisitReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_CKDMissedVisitReport")
	public ModelAndView removeCKDMissedVisitReport() throws Exception {
		new SetupCKDMissedvisitReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_CKDConsultSheetReport")
	public ModelAndView registerCKDConsultSheetReport() throws Exception {
		new SetupCKDConsultationSheetReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_CKDConsultSheetReport")
	public ModelAndView removeCKDConsultSheetReport() throws Exception {
		new SetupCKDConsultationSheetReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_CKDIndicatorReport")
	public ModelAndView registerCKDIndicatorReport() throws Exception {
		new SetupCKDQuarterlyAndMonthlyReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_CKDIndicatorReport")
	public ModelAndView removeCKDIndicatorReport() throws Exception {
		new SetupCKDQuarterlyAndMonthlyReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_MOH-HMISIndicatorreport")
	public ModelAndView registerMOHHMISIndicatorreport() throws Exception {
		new SetupHMISMOHReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_MOH-HMISIndicatorreport")
	public ModelAndView removeMOHHMISIndicatorreport() throws Exception {
		new SetupHMISMOHReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_MentalHealthConsultationreport")
	public ModelAndView registerMentalHealthConsultationreport() throws Exception {
		new SetupMentalHealthConsultationSheet().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_MentalHealthConsultationreport")
	public ModelAndView removeMentalHealthConsultationreport() throws Exception {
		new SetupMentalHealthConsultationSheet().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_MentalHealthLateVisit")
	public ModelAndView registerMentalHealthLateVisit() throws Exception {
		new SetupMentalHealthLateVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_MentalHealthLateVisit")
	public ModelAndView removeMentalHealthLateVisit() throws Exception {
		new SetupMentalHealthLateVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_MentalHealthIndicatorReport")
	public ModelAndView registerMentalHealthIndicatorReport() throws Exception {
		new SetupMentalHealthIndicatorReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_MentalHealthIndicatorReport")
	public ModelAndView removeMentalHealthIndicatorReport() throws Exception {
		new SetupMentalHealthIndicatorReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_MonthlyExecutiveDashboardMetricsReport")
	public ModelAndView registerMonthlyExecutiveDashboardMetricsReport() throws Exception {
		new SetupMonthlyExecutiveDashboardMetricsReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_MonthlyExecutiveDashboardMetricsReport")
	public ModelAndView removeMonthlyExecutiveDashboardMetricsReport() throws Exception {
		new SetupMonthlyExecutiveDashboardMetricsReport().delete();
    return new ModelAndView(new RedirectView("rwandareports.form"));
  	}

	@RequestMapping("/module/rwandareports/register_HMISIndicatorMonthly")
	public ModelAndView registerHMISIndicatorMonthlyIndicatorReport() throws Exception {
		new SetupHMISIndicatorMonthlyReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_HMISIndicatorMonthlyReport")
	public ModelAndView removeHMISIndicatorMonthlyReport() throws Exception {
		new SetupHMISIndicatorMonthlyReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_DataEntryQuantityReport")
	public ModelAndView registerDataEntryQuantityReport() throws Exception {
		new SetupDataEntryQuantityReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_DataEntryQuantityReport")
	public ModelAndView removeDataEntryQuantityReport() throws Exception {
		new SetupDataEntryQuantityReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_LabResultReport")
	public ModelAndView registerGenericLabResultReport() throws Exception {
		new SetupLabResultReports().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_LabResultReport")
	public ModelAndView removeGenericLabResultReport() throws Exception {
		new SetupLabResultReports().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

//		@RequestMapping("/module/rwandareports/register_HMISCancerScreeningIndicatorReport")
//	public ModelAndView registerHMISCancerScreeningIndicatorReport() throws Exception {
//		new SetupHMISCancerScreeningMonthlyIndicatorReport().setup();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
//
//	@RequestMapping("/module/rwandareports/remove_HMISCancerScreeningIndicatorReport")
//	public ModelAndView removeHMISCancerScreeningIndicatorReport() throws Exception {
//		new SetupHMISCancerScreeningMonthlyIndicatorReport().delete();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
		@RequestMapping("/module/rwandareports/register_CancerScreeningConsultationSheetAndMissedVisitReport")
	public ModelAndView registerCancerScreeningConsultationSheetAndMissedVisitReport() throws Exception {
		new SetupCancerScreeningConsultAndMissedVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_CancerScreeningConsultationSheetAndMissedVisitReport")
	public ModelAndView removeCancerScreeningConsultationSheetAndMissedVisitReport() throws Exception {
		new SetupCancerScreeningConsultAndMissedVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
//		@RequestMapping("/module/rwandareports/register_CancerScreeningSMSReport")
//	public ModelAndView registerCancerScreeningSMSReport() throws Exception {
//		new SetupCancerScreeningSMSReport().setup();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
//
//	@RequestMapping("/module/rwandareports/remove_CancerScreeningSMSReport")
//	public ModelAndView removeCancerScreeningSMSReport() throws Exception {
//		new SetupCancerScreeningSMSReport().delete();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
//	@RequestMapping("/module/rwandareports/register_SetupCancerScreeningLabReport")
//	public ModelAndView registerCancerScreeningLabReport() throws Exception {
//		new SetupCancerScreeningLabReport().setup();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
//	@RequestMapping("/module/rwandareports/remove_CancerScreeningLabReport")
//	public ModelAndView removeCancerScreeningLabReport() throws Exception {
//		new SetupCancerScreeningLabReport().delete();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
//	@RequestMapping("/module/rwandareports/register_CancerScreeningProgramIndicatorReport")
//	public ModelAndView registerCancerScreeningProgramIndicatorReport() throws Exception {
//		new SetupCancerScreeningProgramIndicatorReport().setup();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}
//	@RequestMapping("/module/rwandareports/remove_CancerScreeningProgramIndicatorReport")
//	public ModelAndView removeCancerScreeningProgramIndicatorReport() throws Exception {
//		new SetupCancerScreeningProgramIndicatorReport().delete();
//		return new ModelAndView(new RedirectView("rwandareports.form"));
//	}

	@RequestMapping("/module/rwandareports/register_PathologyRequestReport")
	public ModelAndView registerPathologyRequestReport() throws Exception {
		new SetupPathologyRequestReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/remove_PathologyRequestReport")
	public ModelAndView removePathologyRequestReport() throws Exception {
		new SetupPathologyRequestReport().delete();

		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_NCDsHMISReport")
	public ModelAndView registerNCDsHMISReport() throws Exception{
		new SetupNCDsHMISReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_NCDsHMISReport")
	public ModelAndView removeNCDsHMISReport() throws Exception{
		new SetupNCDsHMISReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}

	@RequestMapping("/module/rwandareports/register_OncologyInpatientAppointmentList")
	public ModelAndView registerOncologyInpatientAppointmentList() throws Exception{
		new SetupOncologyInpatientAppointmentList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_OncologyInpatientAppointmentList")
	public ModelAndView removeOncologyInpatientAppointmentList() throws Exception{
		new SetupOncologyInpatientAppointmentList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_OncologyCancerCenterAppointmentList")
	public ModelAndView registerOncologyCancerCenterAppointmentList() throws Exception{
		new SetupOncologyRwandaCancerCenterAppointmentList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_OncologyCancerCenterAppointmentList")
	public ModelAndView removeOncologyCancerCenterAppointmentList() throws Exception{
		new SetupOncologyRwandaCancerCenterAppointmentList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_OncologyPediatricAppointmentList")
	public ModelAndView registerOncologyPediatricAppointmentList() throws Exception{
		new SetupOncologyPediatricAppointmentList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_OncologyPediatricAppointmentList")
	public ModelAndView removeOncologyPediatricAppointmentList() throws Exception{
		new SetupOncologyPediatricAppointmentList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_oncologyPediatricExpectedPatientList")
	public ModelAndView registeroncologyPediatricExpectedPatientList() throws Exception{
		new SetupChemotherapyPediatricExpectedPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_oncologyPediatricExpectedPatientList")
	public ModelAndView removeoncologyPediatricExpectedPatientList() throws Exception{
		new SetupChemotherapyPediatricExpectedPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_oncologyCancerCenterExpectedPatientList")
	public ModelAndView registeroncologyCancerCenterExpectedPatientList() throws Exception{
		new SetupChemotherapyCancerCenterExpectedPatientList().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_oncologyCancerCenterExpectedPatientList")
	public ModelAndView removeoncologyCancerCenterExpectedPatientList() throws Exception{
		new SetupChemotherapyCancerCenterExpectedPatientList().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_oncologyPediatricClinicMissedVisit")
	public ModelAndView registeroncologyPediatricClinicMissedVisit() throws Exception{
		new SetupOncologyPediatricClinicMissedVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_oncologyPediatricClinicMissedVisit")
	public ModelAndView removeoncologyPediatricClinicMissedVisit() throws Exception{
		new SetupOncologyCancerCenterClinicMissedVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_oncologyRwandaCancerCenterClinicMissedVisit")
	public ModelAndView registeroncologyRwandaCancerCenterClinicMissedVisit() throws Exception{
		new SetupOncologyCancerCenterClinicMissedVisit().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_oncologyRwandaCancerCenterClinicMissedVisit")
	public ModelAndView removeoncologyRwandaCancerCenterClinicMissedVisit() throws Exception{
		new SetupOncologyCancerCenterClinicMissedVisit().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_OncologyLostToFollowUpIndicatorReport")
	public ModelAndView registerOncologyLostToFollowUpIndicatorReport() throws Exception{
		new SetupOncologyLostToFollowUpIndicatorReport().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_OncologyLostToFollowUpIndicatorReport")
	public ModelAndView removeOncologyLostToFollowUpIndicatorReport() throws Exception{
		new SetupOncologyLostToFollowUpIndicatorReport().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/register_IncomeGroupedByInsurance")
	public ModelAndView registerIncomeGroupedByInsurance() throws Exception{
		new SetupIncomeGroupedByInsurance().setup();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
	@RequestMapping("/module/rwandareports/remove_IncomeGroupedByInsurance")
	public ModelAndView removeIncomeGroupedByInsurance() throws Exception{
		new SetupIncomeGroupedByInsurance().delete();
		return new ModelAndView(new RedirectView("rwandareports.form"));
	}
}
