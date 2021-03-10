package org.openmrs.module.rwandareports.customcalculator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.rowperpatientreports.patientdata.definition.CustomCalculation;
import org.openmrs.module.rowperpatientreports.patientdata.result.*;
import org.openmrs.module.rwandareports.util.GlobalPropertiesManagement;

import java.util.Date;
import java.util.List;

public class CancerScreenSMSAlert implements CustomCalculation{

	protected Log log = LogFactory.getLog(this.getClass());
	
	GlobalPropertiesManagement gp = new GlobalPropertiesManagement();

	String hivStatus="";
	ObservationResult cervicalReferredToObsResult =null;
	ObservationResult breastReferredToObsResult =null;




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
				}
			}
			if(result.getName().equals("cervicalReferredTo"))
			{
				ObservationResult obs = (ObservationResult)result;
				if(obs != null) {
					cervicalReferredToObsResult =obs;
				}
			}
			if(result.getName().equals("breastReferredTo"))
			{
				ObservationResult obs = (ObservationResult)result;
				if(obs != null) {
					breastReferredToObsResult =obs;
				}
			}
		}



		Date startDate=(Date) context.getParameterValue("startDate");
		Date endDate=(Date) context.getParameterValue("endDate");

		for(PatientDataResult result: results)
		{
			String patientFullName=result.getPatientData().getPatient().getFamilyName()+" "+result.getPatientData().getPatient().getGivenName();
			String healthFacility=Context .getLocationService().getLocation(Integer.parseInt(result.getPatientData().getPatient().getAttribute("Health Facility").getValue())).getName();
			if(result.getName().equals("hpvResultTest"))
			{
				ObservationResult hpvResultTest = (ObservationResult)result;

				if(hpvResultTest != null && hpvResultTest.getValue()!=null)
				{
					Date obsDate= hpvResultTest.getObs().getObsDatetime();

					if (hpvResultTest.getValue().equals("HPV negative") && hivStatus!=null && !hivStatus.equals("POSITIVE") && obsDate.after(startDate) && obsDate.before(endDate))
					alerts.append("Message from:"+healthFacility+". "+patientFullName+", would like to inform  you have been tested negative for HPV, you are requested to come back for retesting after 5 years."+"\n");

					if (hpvResultTest.getValue().equals("HPV negative") && hivStatus!=null && hivStatus.equals("POSITIVE") && obsDate.after(startDate) && obsDate.before(endDate))
						alerts.append("Message from:"+healthFacility+". "+patientFullName+", You have been tested negative for HPV, you are requested to come back for retesting after 3 years."+"\n");

					if (hpvResultTest.getValue().equals("HPV positive Type") && obsDate.after(startDate) && obsDate.before(endDate))
						alerts.append(""+patientFullName+", Your  HPV test result is available, you are requested to come  to "+healthFacility+" for medical follow up."+"\n");

				}

			}
			if(result.getName().equals("cervicalNextScheduledDate"))
			{
				ObservationResult cervicalNextScheduledDateObs = (ObservationResult)result;
				if(cervicalNextScheduledDateObs != null && cervicalReferredToObsResult!=null)
					{
						Date valueDate=null;
						if (cervicalNextScheduledDateObs.getObs()!=null)
						valueDate=cervicalNextScheduledDateObs.getObs().getValueDate();
						if (valueDate!=null && valueDate.after(endDate) && cervicalReferredToObsResult.getValue()!=null && !cervicalReferredToObsResult.getValue().equals("") && valueDate.after(endDate) && cervicalNextScheduledDateObs.getObs().getEncounter().getEncounterId()==cervicalReferredToObsResult.getObs().getEncounter().getEncounterId())
						alerts.append(""+patientFullName+", you have an appointment for cervical cancer screening follow up on "+cervicalNextScheduledDateObs.getValue()+" . Please go to "+cervicalReferredToObsResult.getValue()+" to continue this care."+"\n");
				}
				if(cervicalNextScheduledDateObs != null)
				{
					Date valueDate=null;
					if (cervicalNextScheduledDateObs.getObs()!=null)
						valueDate=cervicalNextScheduledDateObs.getObs().getValueDate();
					if (valueDate!=null && valueDate.after(endDate) && cervicalReferredToObsResult.getValue()==null)
						alerts.append(""+patientFullName+", you have an appointment for cervical cancer screening follow up on "+cervicalNextScheduledDateObs.getValue()+" . Please go to "+cervicalNextScheduledDateObs.getObs().getLocation().getName()+" to continue this care."+"\n");
				}
			}


			if(result.getName().equals("breastNextScheduledDate"))
			{
				ObservationResult breastlNextScheduledDateObs = (ObservationResult)result;
				if(breastlNextScheduledDateObs != null && breastReferredToObsResult!=null)
				{
					Date valueDate=null;
					if (breastlNextScheduledDateObs.getObs()!=null)
						valueDate=breastlNextScheduledDateObs.getObs().getValueDate();
					if (valueDate!=null && valueDate.after(endDate) && breastReferredToObsResult.getValue()!=null && !breastReferredToObsResult.getValue().equals("") && valueDate.after(endDate) && breastlNextScheduledDateObs.getObs().getEncounter().getEncounterId()==breastReferredToObsResult.getObs().getEncounter().getEncounterId())
						alerts.append(""+patientFullName+", you have an appointment for breast cancer screening follow up on "+breastlNextScheduledDateObs.getValue()+" . Please go to "+breastReferredToObsResult.getValue()+" to continue this care."+"\n");


				}
				if(breastlNextScheduledDateObs != null)
				{
					Date valueDate=null;
					if (breastlNextScheduledDateObs.getObs()!=null) {
						valueDate = breastlNextScheduledDateObs.getObs().getValueDate();
					}
					if (valueDate!=null && valueDate.after(endDate) && breastReferredToObsResult.getValue() == null) {
						alerts.append("" + patientFullName + ", you have an appointment for breast cancer screening follow up on " + breastlNextScheduledDateObs.getValue() + " . Please go to " + breastlNextScheduledDateObs.getObs().getLocation().getName() + " to continue this care." + "\n");
					}
				}
			}

		}
		
		alert.setValue(alerts.toString().trim());
		return alert;
	}
	

}
