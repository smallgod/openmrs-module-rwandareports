package org.openmrs.module.rwandareports.definition.evaluator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.RowPerPatientData;
import org.openmrs.module.rowperpatientreports.patientdata.evaluator.RowPerPatientDataEvaluator;
import org.openmrs.module.rowperpatientreports.patientdata.result.PatientDataResult;
import org.openmrs.module.rowperpatientreports.patientdata.result.StringResult;
import org.openmrs.module.rwandareports.definition.DrugRegimenInformation;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;
import org.openmrs.util.OpenmrsUtil;

@Handler(supports = { DrugRegimenInformation.class })
public class DrugRegimenInformationEvaluator implements RowPerPatientDataEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public PatientDataResult evaluate(RowPerPatientData patientData, EvaluationContext context) {
		
		StringResult par = new StringResult(patientData, context);
		DrugRegimenInformation pd = (DrugRegimenInformation) patientData;
		
		GlobalPropertiesManagement gp = new GlobalPropertiesManagement();
		List<Concept> iv = gp.getConceptList(GlobalPropertiesManagement.IV_CONCEPT);
		
		DrugRegimen regimen = null;
		
		if (pd.getRegimen() != null) {
			String[] regimenInfo = pd.getRegimen().split(":");
			Integer regimenId = Integer.parseInt(regimenInfo[0]);
			regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(regimenId);
			
			if (regimenInfo.length > 1) {
				Integer regOffset = Integer.parseInt(regimenInfo[1]);
				Calendar offset = Calendar.getInstance();
				offset.setTime(regimen.getFirstDrugOrderStartDate());
				offset.add(Calendar.DAY_OF_YEAR, regOffset);
				pd.setAsOfDate(offset.getTime());
				pd.setShowStartDate(true);
			}
		} else {
			
			
			List<ExtendedDrugOrder> drugOrders = Context.getService(OrderExtensionService.class).getExtendedDrugOrders(
			    pd.getPatient(), pd.getIndication(), pd.getAsOfDate(), pd.getUntilDate());
			
			
			for (ExtendedDrugOrder eo : drugOrders) {
				if (eo.getGroup() != null) {
					if (eo.getGroup() instanceof DrugRegimen) {
						DrugRegimen reg = (DrugRegimen) eo.getGroup();
						
						if(regimen == null || reg.getFirstDrugOrderStartDate().after(regimen.getFirstDrugOrderStartDate()))
						{
							regimen = (DrugRegimen) eo.getGroup();
						}
					}
				}
			}
		}
		
		if (regimen != null) {
			StringBuilder result = new StringBuilder();
			if (regimen.isCyclical()) {
				Integer maxCycleNum = Context.getService(OrderExtensionService.class).getMaxNumberOfCyclesForRegimen(
				    pd.getPatient(), regimen);
				
				result.append("Cycle #: ");
				result.append(regimen.getCycleNumber());
				result.append(" of ");
				result.append(maxCycleNum.toString());
			}
			
			result.append(" Regimen: ");
			result.append(regimen.getName());
			
			Date startDate = null;
			for (ExtendedDrugOrder order : regimen.getMembers()) {
				if (order.getRoute() != null && iv.contains(order.getRoute())) {
					if (pd.getAsOfDate() != null) {
						if ((startDate == null || OpenmrsUtil.compare(order.getStartDate(), pd.getAsOfDate()) >= 0)
						        || (order.getStartDate().before(startDate) && OpenmrsUtil.compare(order.getStartDate(),
						            pd.getAsOfDate()) >= 0)) {
							startDate = order.getStartDate();
						}
					}
					else
					{
						if (startDate == null || order.getStartDate().after(startDate)) {
							startDate = order.getStartDate();
						}
					}
				}
			}
			
			if (startDate != null) {
				long cycleDay = startDate.getTime() - regimen.getFirstDrugOrderStartDate().getTime();
				if (cycleDay > 0) {
					cycleDay = cycleDay / 86400000;
				}
				result.append(" Day: ");
				result.append(String.valueOf(cycleDay + 1));
			}
			
			
			par.setValue(result.toString());
		}
		return par;
	}
}
