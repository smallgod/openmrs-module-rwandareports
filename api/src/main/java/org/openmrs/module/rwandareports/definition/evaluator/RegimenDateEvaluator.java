package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.DateResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.RegimenDateInformation;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

import java.util.List;

@Handler(supports = { RegimenDateInformation.class })
public class RegimenDateEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		DateResult par = new DateResult(patientData, context);
		
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		List<Concept> iv = gp.getConceptList(GlobalPropertiesManagement.IV_CONCEPT);
		
		RegimenDateInformation pd = (RegimenDateInformation) patientData;
		par.setFormat(pd.getDateFormat());
		
//		//DrugRegimen regimen = null;
//
//		if (pd.getRegimen() != null) {
//			Integer regimenId = Integer.parseInt(pd.getRegimen());
//			//regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(regimenId);
//		} else {
////			List<ExtendedDrugOrder> drugOrders = Context.getService(OrderExtensionService.class).getExtendedDrugOrders(
////			    pd.getPatient(), pd.getIndication(), pd.getAsOfDate(), pd.getUntilDate());
////
////			for (ExtendedDrugOrder eo : drugOrders) {
////				if (eo.getGroup() != null) {
////					if (eo.getGroup() instanceof DrugRegimen) {
////						DrugRegimen reg = (DrugRegimen) eo.getGroup();
//
////						if (regimen == null || reg.getFirstDrugOrderStartDate().after(regimen.getFirstDrugOrderStartDate())) {
////							regimen = (DrugRegimen) eo.getGroup();
////						}
//					}
//				}
//			}
//		}
		
//		if (regimen != null) {
//
//			Date startDate = null;
//			for (ExtendedDrugOrder order : regimen.getMembers()) {
//				if (order.getRoute() != null && iv.contains(order.getRoute())) {
//
//					if (pd.getAsOfDate() != null) {
//
//						if (startDate == null && OpenmrsUtil.compare(order.getStartDate(), pd.getAsOfDate()) >= 0)
//						{
//							startDate = order.getStartDate();
//						}
//						else if (startDate != null && order.getStartDate().before(startDate) && OpenmrsUtil.compare(order.getStartDate(),
//						            pd.getAsOfDate()) >= 0) {
//							startDate = order.getStartDate();
//						}
//					} else {
//						if (startDate == null || order.getStartDate().after(startDate)) {
//							startDate = order.getStartDate();
//						}
//					}
//				}
//			}
//			par.setValue(startDate);
//		}
		
		return par;
	}
}
