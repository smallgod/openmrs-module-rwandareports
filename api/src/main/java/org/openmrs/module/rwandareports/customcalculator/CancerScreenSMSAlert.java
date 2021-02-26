package org.openmrs.module.rwandareports.customcalculator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.*;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CancerScreenSMSAlert implements CustomCalculation{

	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	String hivStatus="";


	public PatientDataResult calculateResult(List<PatientDataResult> results, EvaluationContext context) {
		
		PatientAttributeResult alert = new PatientAttributeResult(null, null);
		

		StringBuffer alerts = new StringBuffer();

		for(PatientDataResult result: results)
		{
			if(result.getName().equals("hivResultTest"))
			{
				ObservationResult hivResultTest = (ObservationResult)result;
				if(hivResultTest != null) {
					hivStatus = hivResultTest.getValue();
					System.out.println("HIV STATUS:  "+hivStatus);
					break;
				}
			}
		}

		
		for(PatientDataResult result: results)
		{
			String patientFullName=result.getPatientData().getPatient().getFamilyName()+" "+result.getPatientData().getPatient().getGivenName();
			String healthFacility=Context .getLocationService().getLocation(Integer.parseInt(result.getPatientData().getPatient().getAttribute("Health Facility").getValue())).getName();
			if(result.getName().equals("hpvResultTest"))
			{
				ObservationResult hpvResultTest = (ObservationResult)result;

				Date startDate=(Date) context.getParameterValue("startDate");
				Date endDate=(Date) context.getParameterValue("endDate");


				if(hpvResultTest != null && hpvResultTest.getValue()!=null)
				{
					Date obsDate= hpvResultTest.getObs().getObsDatetime();

					if (hpvResultTest.getValue().equals("HPV negative") && hivStatus!=null && !hivStatus.equals("POSITIVE") && obsDate.after(startDate) && obsDate.before(endDate))
					alerts.append("Message from:"+healthFacility+". "+patientFullName+", would like to inform  you have been tested negative for HPV, you are requested to come back for retesting after 5 years."+"\n");

					if (hpvResultTest.getValue().equals("HPV negative") && hivStatus!=null && hivStatus.equals("POSITIVE") && obsDate.after(startDate) && obsDate.before(endDate))
						alerts.append("Message from:"+healthFacility+". "+patientFullName+", You have been tested negative for HPV, you are requested to come back for retesting after 3 years."+"\n");

					if (hpvResultTest.getValue().equals("HPV positive Type") && obsDate.after(startDate) && obsDate.before(endDate))
						alerts.append(""+patientFullName+", Your  HPV test result is available, you are requested to come  to "+healthFacility+" for medical follow up."+"\n");


					//alerts.append("HPV Result is:"+ hpvResultTest.getValue()+"\n");
				}

			}


		}
		

		
		alert.setValue(alerts.toString().trim());
		return alert;
	}
	

}
