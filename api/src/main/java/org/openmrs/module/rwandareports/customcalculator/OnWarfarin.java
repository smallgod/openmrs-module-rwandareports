package org.openmrs.module.rwandareports.customcalculator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.OrderService.ORDER_STATUS;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

public class OnWarfarin implements CustomCalculation {

	protected Log log = LogFactory.getLog(OnWarfarin.class);
	
	public OnWarfarin() {
		
	}

	public PatientDataResult calculateResult(List<PatientDataResult> results,EvaluationContext context) {
			
		StringResult sr = new StringResult(null, null);
		
		for (PatientDataResult result : results) {
			if (result.getName().equals("age")) {

				Patient p = result.getPatientData().getPatient();
				List<Encounter> patientEncounters = Context.getEncounterService().getEncounters(p, null, null, null, null, null, null, false);
				List<DrugOrder> patientDrugOrders = Context.getOrderService().getDrugOrdersByPatient(p, ORDER_STATUS.NOTVOIDED, false);
                
				if (patientEncounters.size() > 0 && patientDrugOrders.size() > 0) {
					//Encounter recentEncounter = patientEncounters.get(patientEncounters.size() - 1);  //the last encounter in the List should be the most recent one.
					 for (DrugOrder drugOrder : patientDrugOrders) {
						if (isWarfarinOrder(drugOrder) &&  !drugOrder.isDiscontinuedRightNow()) {
							
							sr.setValue("Y");
							return sr;
						} 
					}
					 sr.setValue("N");
					 return sr;

				} else {
					sr.setValue("N");
				}

			}
		}

		return sr;
	}

	//should return true if the order-concept's conceptId is equal to one of the warfarin-concept conceptId
	public boolean isWarfarinOrder(DrugOrder order) {
		String insulineConcepts = Context.getAdministrationService().getGlobalProperty(GlobalPropertiesManagement.WARFARIN);
		String[] items = insulineConcepts.split(",");
		
		for (String string : items) {
			try {
				int i = Integer.parseInt(string);
				if (i == order.getDrug().getConcept().getConceptId())
					return true;
			} catch (NumberFormatException e) {
				log.error("Invalid Global property: "+GlobalPropertiesManagement.WARFARIN+". Value should be a comma separated list of Integers");
			}
			
		}
		return false;

	}
}
