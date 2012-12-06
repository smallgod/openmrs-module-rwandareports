package org.openmrs.module.rwandareports.definition.evaluator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.OrderService.ORDER_STATUS;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.AllObservationValues;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllDrugOrdersResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.ObservationResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rwandareports.definition.CurrentPatientDrugOrder;
import org.openmrs.module.rwandareports.definition.result.CurrentDrugOrderResults;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

@Handler(supports={CurrentPatientDrugOrder.class})
public class CurrentPatientDrugOrderEvaluator implements RowPerPatientDataEvaluator{

	protected Log log = LogFactory.getLog(this.getClass());
	StringBuilder result = new StringBuilder();
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
	    
		CurrentDrugOrderResults par = new CurrentDrugOrderResults(patientData, context);
		CurrentPatientDrugOrder pd = (CurrentPatientDrugOrder)patientData;
		par.setDrugFilter(pd.getResultFilter());
		List<DrugOrder> orders = Context.getOrderService().getDrugOrdersByPatient(pd.getPatient(),ORDER_STATUS.CURRENT, false);
		
		for (DrugOrder drugOrder : orders) {
             if((drugOrder != null) && ! drugOrder.isDiscontinuedRightNow()){ 
            	
			         par.setValue(orders);
        	 }
        }
		return par;
	}
}
