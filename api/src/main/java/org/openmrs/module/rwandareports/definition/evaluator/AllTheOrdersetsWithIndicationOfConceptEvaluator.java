package org.openmrs.module.rwandareports.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.AllDrugOrdersResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;
import org.openmrs.module.rwandareports.definition.AllTheOrdersetsWithIndicationOfConcept;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;
import java.util.List;

@Handler(supports = {AllTheOrdersetsWithIndicationOfConcept.class})
public class AllTheOrdersetsWithIndicationOfConceptEvaluator implements RowPerPatientDataEvaluator {

    protected Log log = LogFactory.getLog(this.getClass());

    public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {

        StringResult par = new StringResult(patientData, context);

        AllTheOrdersetsWithIndicationOfConcept pd = (AllTheOrdersetsWithIndicationOfConcept)patientData;

        Concept IndicationConcept = pd.getIndicationConcept();
        Date beforeDate = pd.getBeforeDate();
        Date afterDate = pd.getAfterDate();
//        List<ExtendedDrugOrder> orders = Context.getService(OrderExtensionService.class).getExtendedDrugOrders(pd.getPatient(),IndicationConcept,null,null);
//
//        if(orders !=null){
//            String allOrderSetsName = "";
//            int i = 0;
//            for(ExtendedDrugOrder order: orders)
//            {
//                if(order.getStartDate() != null &&(afterDate==null || OpenmrsUtil.compare(order.getStartDate(),afterDate) >=0) && (beforeDate==null || OpenmrsUtil.compare(order.getStartDate(),beforeDate)<=0))
//                {
//
//                    if (i > 0) {
//                        allOrderSetsName = allOrderSetsName + ",";
//                    }
//                    String orderSetName = null;
//                    try{
//                        orderSetName = order.getGroup().getOrderSet().getName();
//                    }
//                    catch(Exception e)
//                    {
//                        log.error("Unable to retrieve a name from the order set: " + e.getMessage());
//                    }
//                    if(orderSetName !=null) {
//                        allOrderSetsName = allOrderSetsName + orderSetName;
//                    }
//                }
//                i++;
//            }
//            if(allOrderSetsName !=null){
//                par.setValue(allOrderSetsName);
//            }
//        }

        return par;
    }


}
